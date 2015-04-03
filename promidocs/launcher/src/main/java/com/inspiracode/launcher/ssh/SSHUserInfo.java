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

import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

/**
 * USAGE HERE
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
public class SSHUserInfo implements UserInfo, UIKeyboardInteractive {
	private PromidocsLogger logger = PromidocsLogFactory
			.getLoggerInstance(SSHUserInfo.class.getName());

	private String name;
	private String password = null;
	private String keyfile;
	private String passphrase = null;
	private boolean trustAllCertificates;

	/**
	 * Constructor for SSHUserInfo
	 */
	public SSHUserInfo() {
		super();
		this.trustAllCertificates = false;
	}

	/**
	 * Constructor for SSHUserInfo
	 * 
	 * @param password
	 * @param trustAllCertificates
	 */
	public SSHUserInfo(String password, boolean trustAllCertificates) {
		super();
		this.setPassword(password);
		this.trustAllCertificates = trustAllCertificates;
	}

	/**
	 * prompts a string.
	 * 
	 * @param str
	 * @return
	 */
	public boolean prompt(String str) {
		return false;
	}

	/**
	 * Indicates whether a retry was done.
	 * 
	 * @return
	 */
	public boolean retry() {
		return false;
	}

	public void setTrust(boolean trust) {
		this.trustAllCertificates = trust;
	}

	public boolean getTrust() {
		return trustAllCertificates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jcraft.jsch.UIKeyboardInteractive#promptKeyboardInteractive(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String[],
	 * boolean[])
	 */
	public String[] promptKeyboardInteractive(String destination, String name,
			String instruction, String[] prompt, boolean[] echo) {
		if (prompt.length != 1 || echo[0] || this.password == null) {
			return null;
		}
		String[] response = new String[1];
		response[0] = this.password;
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#getPassphrase()
	 */
	public String getPassphrase() {
		return passphrase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#getPassword()
	 */
	public String getPassword() {
		return password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#promptPassphrase(java.lang.String)
	 */
	public boolean promptPassphrase(String message) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
	 */
	public boolean promptPassword(String passwordPrompt) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
	 */
	public boolean promptYesNo(String message) {
		return trustAllCertificates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
	 */
	public void showMessage(String message) {
		logger.log(PromidocsLogger.INFO, message);
	}

	/**
	 * Gets the user name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the user name
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param passphrase
	 *            the passphrase to set
	 */
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the keyfile
	 */
	public String getKeyfile() {
		return keyfile;
	}

	/**
	 * @param keyfile
	 *            the keyfile to set
	 */
	public void setKeyfile(String keyfile) {
		this.keyfile = keyfile;
	}

}
