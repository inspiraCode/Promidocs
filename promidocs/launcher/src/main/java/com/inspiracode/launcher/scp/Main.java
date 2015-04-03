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
package com.inspiracode.launcher.scp;

import java.io.IOException;
import java.io.InputStream;

import com.inspiracode.launcher.util.CastingUtility;
import com.inspiracode.launcher.util.SleepUtility;
import com.inspiracode.launcher.util.UtilityException;
import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;
import com.jcraft.jsch.JSchException;

/**
 * MAIN PROGRAM CLASS. <b>Parameters:</b><br/>
 * <ul>
 * <li><b>source</b>: (-source) This is an alternative to the file attribute.
 * But this must always point to a local file.</li>
 * <li><b>target</b>: (-target) This is an alternative to the file attribute.
 * But this must always point to a remote file.</li>
 * <li><b>port</b>: (-port) The port to connect to on the remote host (default
 * to 22).</li>
 * <li><b>trust</b>: (-trust) This trusts all unknown hosts if set to yes/true
 * (default to false). When false, the host you connect to must be listed in
 * your knownhosts file, this also implies that the file exists.</li>
 * <li><b>knownhosts</b>: (-knownhosts) This sets the known hosts file to use to
 * validate the identity of the remote host. This must be a SSH2 format file.
 * SSH1 format is not supported (default to ${user.home}/.ssh/known_hosts).</li>
 * <li><b>password</b>: (-password) The password. Not if you are using key based
 * authentication or the password has been given in the file or todir attribute.
 * </li>
 * <li><b>keyfile</b>: (-keyfile) Location of the file holding the private key.</li>
 * <li><b>passphrase</b>: (-passphrase) Passphrase for your private key (default
 * to an empty string).</li>
 * <li><b>ask</b>: (-ask) Ask to the use to digit the password.</li>
 * <li><b>verbose</b>: (-v) Determines whether SCP outputs verbosely to the
 * user. Currently this means outputting dots/stars showing the progress of a
 * file transfer (default to false).</li>
 * <li><b>sftp</b>: (-sftp) Determines whether SCP outputs verbosely to the
 * user. Currently this means outputting dots/stars showing the progress of a
 * file transfer (default to false).</li>
 * <li><b>remove</b>: (-remove) Determines whether the source file should be
 * removed.</li>
 * <li><b>tlogger</b>: (-tlogger) class fully qualified name that will handle
 * the transfer log.</li>
 * <li><b>check</b>: (-check) log check is allowed for tlogger. If a file is
 * found in tlogger to be transferred before, it will be avoided to be
 * transferred again.</li>
 * </ul>
 * 
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
public class Main {
	private final static String VERSION = "scp launcher v 1.0";

	private static final PromidocsLogger logger = PromidocsLogFactory
			.getLoggerInstance(Main.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			try {
				System.err.println(man());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Main main = new Main();
			try {
				main.transfer(args);
			} catch (SCPException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private int retry;
	private String source;
	private int port;
	private String target;
	private boolean trust;
	private String keyFile;
	private String passphrase;
	private String knownHosts;
	private boolean verbose;
	private String password;
	private boolean recursive;
	private boolean ask;
	private String fchmod;
	private String dchmod;

	private int delay = 1;

	/**
	 * @param args
	 * @throws IOException
	 */
	private void transfer(String[] args) throws SCPException, IOException {
		boolean follow = configureTransfer(args);
		if(follow){
			int countOfRetries = 0;
			while (countOfRetries <= this.retry) {
				try {
					SCP sftpClient = new SCP();
					sftpClient.setFromUri(this.source);
					sftpClient.setPort(this.port);
					sftpClient.setToUri(this.target);
					sftpClient.setTrust(this.trust);
					sftpClient.setKeyFile(this.keyFile);
					sftpClient.setPassphrase(this.passphrase);
					sftpClient.setKnownHosts(this.knownHosts);
					sftpClient.setVerbose(this.verbose);
					sftpClient.setPassword(this.password);
					// sftpClient.setSftp(this.sftp);
					sftpClient.setFailOnError(true);
					sftpClient.setRecursive(this.recursive);
					sftpClient.setAskPassword(this.ask);
					sftpClient.setChmodFile(this.fchmod);
					sftpClient.setChmodDirectory(this.dchmod);

					sftpClient.execute();
					logger.log(PromidocsLogger.INFO, "counterOfRetries="
							+ countOfRetries);
				} catch (JSchException e) {
					logger.log(PromidocsLogger.ERROR, e.getMessage(), e);
					if (countOfRetries >= this.retry)
						throw new SCPException(e);
				} finally {
					countOfRetries++;
				}

				if (countOfRetries < this.retry) {
					SleepUtility.sleep(this.delay);
				}
			}
		}
		
	}

	private boolean configureTransfer(String[] args) throws SCPException {
		for (int i = 0; i < args.length; i++) {
			logger.log(PromidocsLogger.DEBUG,
					String.format("args[%d]=%s", i, args[i]));
			if ("-version".equalsIgnoreCase(args[i])
					|| "--version".equalsIgnoreCase(args[i])) {
				System.out.println(VERSION);
				return false;
			} else if ("-h".equals(args[i])) {
				try {
					System.err.println(man());
				} catch (IOException e) {
					throw new SCPException(e);
				}
				return false;
			} else if (args[i].equalsIgnoreCase("-retry") == true) {
				if (++i < args.length) {
					try {
						this.retry = CastingUtility.stringToInt(args[i]);
					} catch (UtilityException e) {
						throw new SCPException(e);
					}
				} else {
					throw new SCPException("Too many args!");
				}
				this.delay = 60;
			} else if (args[i].equalsIgnoreCase("-dhcmod") == true) {
				if (++i < args.length) {
					this.dchmod = args[i];
				} else {
					throw new SCPException("Too many args!");
				}
			} else if (args[i].equalsIgnoreCase("-fchmod") == true) {
				if (++i < args.length) {
					this.fchmod = args[i];
				} else {
					throw new SCPException("Too many args!");
				}
			} else if (args[i].equalsIgnoreCase("-delay") == true) {
				if (++i < args.length) {
					try {
						this.delay = CastingUtility.stringToInt(args[i]);
					} catch (UtilityException e) {
						throw new SCPException(e);
					}
				} else {
					throw new SCPException("Too many args!");
				}
			} else if (args[i].equalsIgnoreCase("-port") == true) {
				if (++i < args.length) {
					try {
						this.port = CastingUtility.stringToInt(args[i]);
					} catch (UtilityException e) {
						throw new SCPException(e);
					}
				} else {
					throw new SCPException("Too many args!");
				}
			} else if (args[i].equalsIgnoreCase("-target") == true) {
				if (++i < args.length) {
					this.target = args[i];
				} else {
					throw new SCPException("Too many args!");
				}
			} else if (args[i].equalsIgnoreCase("-ask") == true) {
				this.ask = true;
			} else if (args[i].equalsIgnoreCase("-trust") == true) {
				this.trust = true;
			} else if (args[i].equalsIgnoreCase("-keyFile") == true) {
				if (++i < args.length) {
					this.keyFile = args[i];
					this.passphrase = "";
				} else {
					throw new SCPException("Too many args!");
				}
			} else if (args[i].equalsIgnoreCase("-passphrase") == true) {
				if (++i < args.length) {
					this.passphrase = args[i];
				} else {
					throw new SCPException("Too many args!");
				}
			} else if (args[i].equalsIgnoreCase("-password") == true) {
				if (++i < args.length) {
					this.password = args[i];
				} else {
					throw new SCPException("Too many args!");
				}
			} else if (args[i].equalsIgnoreCase("-knownHosts") == true) {
				if (++i < args.length) {
					this.knownHosts = args[i];
				} else {
					throw new SCPException("Too many args!");
				}
			} else if (args[i].equalsIgnoreCase("-source") == true) {
				if (++i < args.length) {
					this.source = args[i];
				} else {
					throw new SCPException("Too many args!");
				}
			} else if (args[i].equalsIgnoreCase("-v") == true) {
				this.verbose = true;
			} else if (args[i].equalsIgnoreCase("-r") == true) {
				this.recursive = true;
			} /*else if (args[i].equalsIgnoreCase("-sftp") == true) {
				this.sftp = true;
			}*/ else {
				logger.log(PromidocsLogger.WARN, "Parameter " + args[i] + " unknown! Skipped.");
			}
		}
		return true;
	}

	private static String man() throws IOException {
		InputStream is = null;
		StringBuffer sb = new StringBuffer();
		int i;
		char c;
		try {
			is = ClassLoader.getSystemResourceAsStream("man.txt");
			if (is != null) {
				while ((i = is.read()) != -1) {
					c = (char) i;
					sb.append(c);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				is.close();
		}
		return sb.toString();
	}
}
