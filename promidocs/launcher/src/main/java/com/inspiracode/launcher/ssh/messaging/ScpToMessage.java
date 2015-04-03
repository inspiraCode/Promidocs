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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import com.inspiracode.launcher.scp.SCPException;
import com.inspiracode.launcher.ssh.Directory;
import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Handle to upload scp.
 * 
 * <B>Revision History:</B>
 * 
 * <PRE>
 * ====================================================================================
 * Date-------- By---------------- Description
 * ------------ --------------------------- -------------------------------------------
 * 09/03/2015 - torredie - Initial Version.
 * ====================================================================================
 * </PRE>
 * 
 * 
 * @author torredie
 * 
 */
public class ScpToMessage extends BaseSSHMessage {
	private static PromidocsLogger logger = PromidocsLogFactory
			.getLoggerInstance(ScpToMessage.class.getName());
	private String remotePath;
	private File localFile;
	private List<Directory> directoryList;

	private static final int BUFFER_SIZE = 1024;

	/**
	 * @param verbose
	 * @param session
	 */
	public ScpToMessage(boolean verbose, Session session) {
		super(verbose, session);
	}

	public ScpToMessage(Session session) {
		super(session);
	}

	public ScpToMessage(boolean verbose, Session session, File aLocalFile,
			String aRemotePath) {
		this(verbose, session, aRemotePath);
		this.localFile = aLocalFile;
	}

	/**
	 * @param verbose
	 * @param session
	 * @param aRemotePath
	 */
	public ScpToMessage(boolean verbose, Session session, String aRemotePath) {
		super(verbose, session);
		this.remotePath = aRemotePath;
	}

	public ScpToMessage(boolean verbose, Session session,
			List<Directory> aDirectoryList, String aRemotePath) {
		this(verbose, session, aRemotePath);
		this.directoryList = aDirectoryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inspiracode.launcher.ssh.messaging.BaseSSHMessage#execute()
	 */
	@Override
	public void execute() throws IOException, JSchException, SCPException {
		if (directoryList != null) {
			doMultipleTransfer();
		}
		if (localFile != null) {
			doSingleTransfer();
		}
		logger.log(PromidocsLogger.INFO, "DONE");
	}

	/**
	 * @throws JSchException
	 * @throws IOException
	 * @throws SCPException
	 * 
	 */
	private void doSingleTransfer() throws JSchException, IOException,
			SCPException {
		String cmd = "scp -t " + remotePath;
		Channel channel = openExecChannel(cmd);
		try {
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();
			waitForAck(in);
			sendFileToRemote(localFile, in, out);
		} finally {
			if (channel != null)
				channel.disconnect();
		}
	}

	/**
	 * @param localFile2
	 * @param in
	 * @param out
	 * @throws IOException
	 * @throws SCPException
	 */
	private void sendFileToRemote(File localFile, InputStream in,
			OutputStream out) throws IOException, SCPException {
		// send "C0644 filesize filename", where filename should not include '/'
		long fileSize = localFile.length();
		String command = "C0644 " + fileSize + " ";
		command += localFile.getName();
		command += "\n";

		if (this.getVerbose()) {
			logger.log(PromidocsLogger.INFO, "scp command is " + command);
		}

		out.write(command.getBytes());
		out.flush();

		waitForAck(in);

		// send content of local file
		FileInputStream fis = new FileInputStream(localFile);
		byte[] buf = new byte[BUFFER_SIZE];
		long startTime = System.currentTimeMillis();
		long totalLength = 0;

		// only track progress for files larger than 100kb in verbose mode.
		try {
			if (this.getVerbose())
				logger.log(PromidocsLogger.INFO,
						"Sending file " + localFile.getName() + " ("
								+ localFile.length() + ")");

			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0) {
					break;
				}
				out.write(buf, 0, len);
				totalLength += len;
			}
			out.flush();
			sendAck(out);
			waitForAck(in);
		} finally {
			if (getVerbose()) {
				long endTime = System.currentTimeMillis();
				logStats(startTime, endTime, totalLength);
			}
			fis.close();
		}
	}

	/**
	 * @throws JSchException
	 * @throws IOException
	 * @throws SCPException
	 * 
	 */
	private void doMultipleTransfer() throws JSchException, IOException,
			SCPException {
		Channel channel = openExecChannel("scp -r -d -t " + remotePath);
		try {
			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			waitForAck(in);

			for (Directory current : directoryList) {
				sendDirectory(current, in, out);
			}

		} finally {
			if (channel != null)
				channel.disconnect();
		}
	}

	/**
	 * @param current
	 * @param in
	 * @param out
	 * @throws SCPException
	 * @throws IOException
	 */
	private void sendDirectory(Directory current, InputStream in,
			OutputStream out) throws IOException, SCPException {

		for (Iterator<File> fileIt = current.filesIterator(); fileIt.hasNext();) {
			sendFileToRemote(fileIt.next(), in, out);
		}
		for (Iterator<Directory> dirIt = current.directoryIterator(); dirIt
				.hasNext();) {
			Directory dir = dirIt.next();
			sendDirectoryToRemote(dir, in, out);
		}
	}

	/**
	 * @param dir
	 * @param in
	 * @param out
	 * @throws IOException 
	 * @throws SCPException 
	 */
	private void sendDirectoryToRemote(Directory dir, InputStream in,
			OutputStream out) throws IOException, SCPException {
		String command = "D0755 0";
		command += dir.getDirectory().getName();
		command += "\n";

		if (getVerbose())
			logger.log(PromidocsLogger.INFO, "scp command is " + command);
		
		out.write(command.getBytes());
		out.flush();
		
		waitForAck(in);
		sendDirectory(dir, in, out);
		out.write("E\n".getBytes());
		out.flush();
		waitForAck(in);
	}

}
