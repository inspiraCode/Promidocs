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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;

import com.inspiracode.launcher.progress.SCPProgressMonitor;
import com.inspiracode.launcher.scp.SCPException;
import com.inspiracode.launcher.ssh.SSHServerResponse;
import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;

/**
 * Base abstract class for uploaders and downloaders
 * 
 * <B>Revision History:</B>
 * 
 * <PRE>
 * ====================================================================================
 * Date-------- By---------------- Description
 * ------------ --------------------------- -------------------------------------------
 * 07/03/2015 - torredie - Initial Version.
 * ====================================================================================
 * </PRE>
 * 
 * 
 * @author torredie
 * 
 */
public abstract class BaseSSHMessage {
	private static final PromidocsLogger logger = PromidocsLogFactory
			.getLoggerInstance(BaseSSHMessage.class.getName());

	private static final double ONE_SECOND = 1000.0;

	protected String chmodFile;
	protected String chmodDirectory;

	private Session session;
	private boolean verbose;

	/**
	 * Constructor for BaseSSH
	 * 
	 * @param session
	 *            the ssh session to use
	 */
	public BaseSSHMessage(Session session) {
		this(false, session);
	}

	/**
	 * Constructor for BaseSSH
	 * 
	 * @param verbose
	 *            if true do verbose logging
	 * @param session
	 *            the ssh session to use
	 */
	public BaseSSHMessage(boolean verbose, Session session) {
		this.verbose = verbose;
		this.session = session;
	}

	/**
	 * Open an ssh channel.
	 * 
	 * @param command
	 *            the command to use
	 * @return the execution channel
	 * @throws JSchException
	 *             on error
	 */
	protected Channel openExecChannel(String command) throws JSchException {
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setCommand(command);

		return channel;
	}

	/**
	 * Open sftp ssh connection channel.
	 * 
	 * @return
	 * @throws JSchException
	 */
	protected ChannelSftp openSftpChannel() throws JSchException {
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");

		return channel;
	}

	/**
	 * write acknowledge to output stream
	 * 
	 * @param out
	 * @throws IOException
	 */
	protected void sendAck(OutputStream out) throws IOException {
		byte[] buffer = new byte[1];
		buffer[0] = 0;
		out.write(buffer);
		out.flush();
	}

	protected void waitForAck(InputStream is) throws IOException, SCPException {
		int read = is.read();
		SSHServerResponse ssr = SSHServerResponse.fromValue(read);
		switch (ssr) {
		case SUCCESS:
			return;
		case NO_RESPONSE:
			throw new SCPException("No response from server");
		case ERROR:
		case FATAL:
			throw new SCPException("Server indicated an error: "
					+ serverErrorDescription(is));
		default:
			throw new SCPException("unknown response, code " + read
					+ " message: " + serverErrorDescription(is));
		}
	}

	private String serverErrorDescription(InputStream is) throws IOException {
		StringBuffer sb = new StringBuffer();

		int c = is.read();
		while (c > 0 && c != '\n') {
			sb.append((char) c);
			c = is.read();
		}
		return sb.toString();
	}

	/**
	 * Implement the transfer method.
	 * 
	 * @throws IOException
	 * @throws JSchException
	 * @throws SCPException
	 */
	public abstract void execute() throws IOException, JSchException,
			SCPException;

	/**
	 * 
	 * Log transfer statistics to the log listener
	 * 
	 * @param timeStarted
	 * @param timeCompleted
	 * @param totalLength
	 */
	protected void logStats(long timeStarted, long timeCompleted,
			long totalLength) {
		double elapsed = (timeCompleted - timeStarted) / ONE_SECOND;
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(1);
		String logMessage = String.format(
				"File transfer duration:%s Average Rate: %s B/s",
				format.format(elapsed), format.format(totalLength / elapsed));
		logger.log(PromidocsLogger.INFO, logMessage);
	}

	/**
	 * Is the verbose attribute set?
	 * 
	 * @return true if the verbose attribute is set.
	 */
	protected final boolean getVerbose() {
		return verbose;
	}

	public String getChmodDirectory() {
		return chmodDirectory;
	}

	public void setChmodDirectory(String chmodDirectory) {
		this.chmodDirectory = chmodDirectory;
	}

	public String getChmodFile() {
		return chmodFile;
	}

	public void setChmodFile(String chmodFile) {
		this.chmodFile = chmodFile;
	}
	
	private SftpProgressMonitor monitor = null;

    /**
     * Get the progress monitor.
     * @return the progress monitor.
     */
    protected SftpProgressMonitor getProgressMonitor() {
        if (monitor == null) {
            monitor = new SCPProgressMonitor();
        }
        return monitor;
    }

}
