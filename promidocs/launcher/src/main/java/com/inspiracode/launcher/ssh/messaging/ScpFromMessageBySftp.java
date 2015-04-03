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

import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

/**
 * represent an scp download.
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
public class ScpFromMessageBySftp extends ScpFromMessage {
	private static PromidocsLogger logger = PromidocsLogFactory
			.getLoggerInstance(ScpFromMessageBySftp.class.getName());

	private static final int HUNDRED_KILOBYTES = 102400;

	private String remoteFile;
	private File localFile;

	/**
	 * @param verbose
	 * @param session
	 */
	public ScpFromMessageBySftp(boolean verbose, Session session) {
		super(verbose, session);
	}

	/**
	 * @param verbose
	 * @param session
	 * @param aRemoteFile
	 * @param aLocalFile
	 * @param recursive
	 */
	public ScpFromMessageBySftp(boolean verbose, Session session,
			String aRemoteFile, File aLocalFile, boolean recursive) {
		super(verbose, session, aRemoteFile, aLocalFile, recursive);
		this.remoteFile = aRemoteFile;
		this.localFile = aLocalFile;
	}

	/**
	 * @param session
	 * @param aRemoteFile
	 * @param aLocalFile
	 * @param recursive
	 */
	public ScpFromMessageBySftp(Session session, String aRemoteFile,
			File aLocalFile, boolean recursive) {
		this(false, session, aRemoteFile, aLocalFile, recursive);
	}

	/**
	 * @param session
	 */
	public ScpFromMessageBySftp(Session session) {
		super(session);
	}

	@Override
	public void execute() throws JSchException {
		ChannelSftp channel = openSftpChannel();
		try {
			channel.connect();
			try {
				SftpATTRS attrs = channel.stat(remoteFile);
				if (attrs.isDir() && !remoteFile.endsWith("/")) {
					remoteFile = remoteFile + "/";
				}
			} catch (SftpException ee) {
				logger.log(PromidocsLogger.ERROR, ee.getMessage(), ee);
			}
			getDir(channel, remoteFile, localFile);
		} catch (SftpException e) {
			JSchException schException = new JSchException("Could not get '"
					+ remoteFile + "' to '" + localFile + "' - " + e.toString());
			schException.initCause(e);
			throw schException;
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
		}
		logger.log(PromidocsLogger.INFO, "done.");
	}

	/**
	 * @param channel
	 * @param remoteFile2
	 * @param localFile2
	 * @throws SftpException
	 */
	@SuppressWarnings("rawtypes")
	private void getDir(ChannelSftp channel, String remoteFile2, File localFile2)
			throws SftpException {
		String pwd = remoteFile;
		if (remoteFile.lastIndexOf('/') != -1) {
			if (remoteFile.length() > 1) {
				pwd = remoteFile.substring(0, remoteFile.lastIndexOf('/'));
			}
		}
		channel.cd(pwd);
		if (!localFile.exists()) {
			localFile.mkdirs();
		}
		java.util.Vector files = channel.ls(remoteFile);
		for (int i = 0; i < files.size(); i++) {
			ChannelSftp.LsEntry le = (ChannelSftp.LsEntry) files.elementAt(i);
			String name = le.getFilename();
			if (le.getAttrs().isDir()) {
				if (name.equals(".") || name.equals("..")) {
					continue;
				}
				getDir(channel, channel.pwd() + "/" + name + "/", new File(
						localFile, le.getFilename()));
			} else {
				getFile(channel, le, localFile);
			}
		}
		channel.cd("..");
	}

	/**
	 * @param channel
	 * @param le
	 * @param localFile2
	 * @throws SftpException 
	 */
	private void getFile(ChannelSftp channel, LsEntry le, File localFile2) throws SftpException {
		String remoteFile = le.getFilename();
		if (!localFile.exists()) {
			String path = localFile.getAbsolutePath();
			int i = path.lastIndexOf(File.pathSeparator);
			if (i != -1) {
				if (path.length() > File.pathSeparator.length()) {
					new File(path.substring(0, i)).mkdirs();
				}
			}
		}

		if (localFile.isDirectory()) {
			localFile = new File(localFile, remoteFile);
		}

		long startTime = System.currentTimeMillis();
		long totalLength = le.getAttrs().getSize();

		SftpProgressMonitor monitor = null;
		boolean trackProgress = getVerbose() && totalLength > HUNDRED_KILOBYTES;
		if (trackProgress) {
			monitor = getProgressMonitor();
		}
		try {
			logger.log(PromidocsLogger.INFO, "Receiving: " + remoteFile + " : "
					+ le.getAttrs().getSize());
			channel.get(remoteFile, localFile.getAbsolutePath(), monitor);
		} finally {
			long endTime = System.currentTimeMillis();
			logStats(startTime, endTime, (int) totalLength);
		}
	}

}
