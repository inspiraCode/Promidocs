/**
 * THIS IS A COMMERCIAL PROGRAM PROVIDED FOR INSPIRACODE AND IT'S ASSOCIATES
 * BUILT BY EXTERNAL SOFTWARE PROVIDERS.
 * THE SOFTWARE COMPRISING THIS SYSTEM IS THE PROPERTY OF INSPIRACODE OR ITS
 * LICENSORS.
 *
 * ALL COPYRIGHT, PATENT, TRADE SECRET, AND OTHER INTELLECTUAL PROPERTY RIGHTS
 * IN THE SOFTWARE COMPRISING THIS SYSTEM ARE, AND SHALL REMAIN, THE VALUABLE
 * PROPERTY OF INSPIRACODE OR ITS LICENSORS.
 *
 * USE, DISCLOSURE, OR REPRODUCTION OF THIS SOFTWARE IS STRICTLY PROHIBITED,
 * EXCEPT UNDER WRITTEN LICENSE FROM INSPIRACODE OR ITS LICENSORS.
 *
 * &copy; COPYRIGHT 2015 INSPIRACODE. ALL RIGHTS RESERVED.
 */
package com.inspiracode.launcher.ssh.messaging;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.inspiracode.launcher.scp.SCPException;
import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Helper object representing an scp download.
 * 
 * <B>Revision History:</B>
 * 
 * <PRE>
 * ====================================================================================
 * Date-------- By---------------- Description
 * ------------ --------------------------- -------------------------------------------
 * 08/03/2015 - torredie - Initial Version.
 * ====================================================================================
 * </PRE>
 * 
 * 
 * @author torredie
 * 
 */
public class ScpFromMessage extends BaseSSHMessage {
	private static PromidocsLogger logger = PromidocsLogFactory
			.getLoggerInstance(ScpFromMessage.class.getName());
	private String remoteFile;
	private File localFile;
	private boolean isRecursive;

	private static final byte LINE_FEED = 0x0a;
	private static final int BUFFER_SIZE = 1024;

	/**
	 * @param session
	 */
	public ScpFromMessage(Session session) {
		super(session);
	}

	/**
	 * @param verbose
	 * @param session
	 */
	public ScpFromMessage(boolean verbose, Session session) {
		super(verbose, session);
	}

	public ScpFromMessage(boolean verbose, Session session, String aRemoteFile,
			File aLocalFile, boolean recursive) {
		super(verbose, session);
		this.remoteFile = aRemoteFile;
		this.localFile = aLocalFile;
		this.isRecursive = recursive;
	}

	public ScpFromMessage(Session session, String aRemoteFile, File aLocalFile,
			boolean recursive) {
		this(false, session, aRemoteFile, aLocalFile, recursive);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inspiracode.launcher.ssh.messaging.BaseSSHMessage#execute()
	 */
	@Override
	public void execute() throws IOException, JSchException, SCPException {
		String command = "scp -f";
		if (isRecursive)
			command += "-r";

		command += remoteFile;

		Channel channel = openExecChannel(command);
		try {
			// get I/O streams for remote scp
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();
			sendAck(out);
			startRemoteCpProtocol(in, out, localFile);
		} finally {
			if (channel != null)
				channel.disconnect();
		}
		logger.log(PromidocsLogger.INFO, "transfer done.");
	}

	/**
	 * @param in
	 * @param out
	 * @param localFile
	 * @throws IOException
	 * @throws SCPException
	 * @throws JSchException
	 */
	private void startRemoteCpProtocol(InputStream in, OutputStream out,
			File localFile) throws IOException, SCPException, JSchException {
		File startFile = localFile;
		logger.log(PromidocsLogger.DEBUG,
				String.format("startFile is %s", startFile));

		while (true) {
			// C0644 filesize filename -header for a regular file
			// T time 0 time 0\n - present if preserve time
			// D directory - this is the header for a directory.
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			while (true) {
				int read = in.read();
				if (read < 0)
					return;
				if ((byte) read == LINE_FEED)
					break;
				stream.write(read);
			}

			String serverResponse = stream.toString("UTF-8");
			logger.log(PromidocsLogger.INFO,
					String.format("Server response is %s", serverResponse));
			if (serverResponse.charAt(0) == 'C') {
				parseAndFetchFile(serverResponse, startFile, out, in);
			} else if (serverResponse.charAt(0) == 'D') {
				startFile = parseAndCreateDirectory(serverResponse, startFile);
				sendAck(out);
			} else if (serverResponse.charAt(0) == 'E') {
				startFile = startFile.getParentFile();
				sendAck(out);
			} else if (serverResponse.charAt(0) == '\01'
					|| serverResponse.charAt(0) == '\02') {
				throw new IOException(serverResponse.substring(1));
			}
		}
	}

	/**
	 * @param serverResponse
	 * @param startFile
	 * @return
	 */
	private File parseAndCreateDirectory(String serverResponse, File startFile) {
		int start = serverResponse.indexOf(" ");
		// appears that the next token is not used and it's zero.
		start = serverResponse.indexOf(" ", start + 1);
		String directoryName = serverResponse.substring(start + 1);
		logger.log(PromidocsLogger.DEBUG, "localFile is " + localFile);
		logger.log(PromidocsLogger.DEBUG, "directoryName is " + directoryName);
		if (localFile.isDirectory()) {
			File dir = new File(localFile, directoryName);
			logger.log(PromidocsLogger.DEBUG,
					"Try to create the local directory " + dir);
			dir.mkdir();
			logger.log(PromidocsLogger.INFO, "Creating: " + dir);
			return dir;
		}
		return null;
	}

	/**
	 * @param serverResponse
	 * @param startFile
	 * @param out
	 * @param in
	 * @throws SCPException
	 * @throws IOException
	 * @throws JSchException
	 */
	private void parseAndFetchFile(String serverResponse, File startFile,
			OutputStream out, InputStream in) throws IOException, SCPException,
			JSchException {
		int start = 0;
		int end = serverResponse.indexOf(" ", start + 1);
		start = end + 1;
		end = serverResponse.indexOf(" ", start + 1);

		long fileSize = Long.parseLong(serverResponse.substring(start, end));
		String fileName = serverResponse.substring(end + 1);
		logger.log(PromidocsLogger.INFO, "Receiving: " + fileName + " : "
				+ fileSize);
		File transferFile = (localFile.isDirectory()) ? new File(localFile,
				fileName) : localFile;
		fetchFile(transferFile, fileSize, out, in);
		waitForAck(in);
		sendAck(out);
	}

	/**
	 * @param transferFile
	 * @param fileSize
	 * @param out
	 * @param in
	 * @throws IOException
	 * @throws JSchException
	 */
	private void fetchFile(File transferFile, long fileSize, OutputStream out,
			InputStream in) throws IOException, JSchException {
		byte[] buf = new byte[BUFFER_SIZE];
		sendAck(out);

		// read the file content
		FileOutputStream fos = new FileOutputStream(localFile);
		int length;
		long totalLength = 0;
		long startTime = System.currentTimeMillis();

		try {
			while (true) {
				length = in.read(buf, 0, (BUFFER_SIZE < fileSize) ? BUFFER_SIZE
						: (int) fileSize);
				if (length < 0) {
					throw new EOFException("Unexpected end of stream.");
				}
				fos.write(buf, 0, length);
				fileSize -= length;
				totalLength += length;
				if (fileSize == 0) {
					break;
				}
			}
		} finally {
			long endTime = System.currentTimeMillis();
			logStats(startTime, endTime, totalLength);
			fos.flush();
			fos.close();
		}
	}
}
