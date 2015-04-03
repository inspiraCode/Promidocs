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
package com.inspiracode.launcher.ssh;

import com.inspiracode.launcher.scp.SCPException;
import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * base class for tasks using jsch.
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
public abstract class BaseSSH {
	private static final PromidocsLogger logger = PromidocsLogFactory
			.getLoggerInstance(BaseSSH.class.getName());

	// default listen port for SSH daemon
	private static final int SSH_PORT = 22;

	private String host;
	private String knownHosts;
	private int port = SSH_PORT;
	private boolean failOnError = true;
	private boolean verbose;
	private SSHUserInfo userInfo;

	/**
	 * Constructor for BaseSSH
	 */
	public BaseSSH() {
		userInfo = new SSHUserInfo();
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Remote host, either DNS or iP.
	 * 
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the failOnError
	 */
	public boolean isFailOnError() {
		return failOnError;
	}

	/**
	 * Set the failonerror flag. Default is true.
	 * 
	 * @param failOnError
	 *            if true throw a build exception when a failure occurs,
	 *            otherwise just log the failure and continue.
	 */
	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	/**
	 * @return the verbose
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * @param verbose
	 *            the verbose to set
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * User name known to the remote host.
	 * 
	 * @param userName
	 */
	public void setUserName(String userName) {
		userInfo.setName(userName);
	}

	public void setPassword(String password) {
		userInfo.setPassword(password);
	}

	public void setKeyFile(String keyFile) {
		userInfo.setKeyfile(keyFile);
	}

	/**
	 * Sets the pass phrase for the users key.
	 * 
	 * @param passphrase
	 */
	public void setPassphrase(String passphrase) {
		userInfo.setPassphrase(passphrase);
	}

	/**
	 * Sets the path to the file that has the identities of all known hosts.
	 * This is used by SSH protocol to validate the identity of the host. The
	 * default is <i>${user.home}/.ssh/known_hosts</i>
	 * 
	 * @param knownHosts
	 */
	public void setKnownHosts(String knownHosts) {
		this.knownHosts = knownHosts;
	}

	/**
	 * Setting this to true trusts hosts whose identity is unknown.
	 * 
	 * @param trust
	 */
	public void setTrust(boolean trust) {
		userInfo.setTrust(trust);
	}

	/**
	 * Change the port used to connect to the remote host.
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * get the port attribute.
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Initialize the task.
	 * This initializes the known hosts and sets the default port.
	 * @throws SCPException
	 */
	public void init() throws SCPException{
		this.knownHosts = System.getProperty("user.home") + "/.ssh/known_hosts";
		this.port = SSH_PORT;
	}
	
	/**
	 * Open an ssh session.
	 * @return the opened session
	 * @throws JSchException
	 */
	protected Session openSession() throws JSchException{
		JSch jsch = new JSch();
		//final BaseSSH base = this;
		
		if(null!=userInfo.getKeyfile()){
			jsch.addIdentity(userInfo.getKeyfile());
		}
		
		if(!userInfo.getTrust() && knownHosts!=null){
			logger.log(PromidocsLogger.INFO, String.format("Using known hosts file: %s", knownHosts));
			jsch.setKnownHosts(knownHosts);
		}
		
		Session session = jsch.getSession(userInfo.getName(), host, port);
		session.setUserInfo(userInfo);
		logger.log(PromidocsLogger.INFO, String.format("Connecting to %s:%d", host, port));
		session.connect();
		
		return session;
	}
	
	/**
	 * Get the user information.
	 * @return
	 */
	protected SSHUserInfo getUserInfo(){
		return userInfo;
	}

}
