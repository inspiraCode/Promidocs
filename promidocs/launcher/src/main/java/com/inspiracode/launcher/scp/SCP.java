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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.inspiracode.launcher.file.FileException;
import com.inspiracode.launcher.file.Fileable;
import com.inspiracode.launcher.file.Filer;
import com.inspiracode.launcher.ssh.BaseSSH;
import com.inspiracode.launcher.ssh.Directory;
import com.inspiracode.launcher.ssh.messaging.ScpFromMessage;
import com.inspiracode.launcher.ssh.messaging.ScpFromMessageBySftp;
import com.inspiracode.launcher.ssh.messaging.ScpToMessage;
import com.inspiracode.launcher.util.CMDUserInput;
import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * SCP Transfer client
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
public class SCP extends BaseSSH implements Fileable {

	private static PromidocsLogger logger = PromidocsLogFactory
			.getLoggerInstance(SCP.class.getName());

	private List<Directory> dirList = new ArrayList<Directory>();

	private Directory currentDir;
	private File from = null;
	private String chmodFile = null;
	private String chmodDirectory = null;
	private String fromUri = null;
	private String toUri = null;
	private boolean isSftp = false;
	private boolean isRecursive = false;
	private boolean askPassword = false;

	/**
	 * Execute SCP task.
	 * 
	 * @throws IOException
	 * @throws JSchException
	 * @throws SCPException
	 */
	public void execute() throws IOException, JSchException, SCPException {
		logger.log(PromidocsLogger.INFO,
				String.format("fromUri is %s", this.fromUri));
		logger.log(PromidocsLogger.INFO,
				String.format("toUri is %s", this.toUri));

		if (toUri == null) {
			throw new SCPException("Parameter 'toUri' is null!");
		}
		if (fromUri == null) {
			throw new SCPException("Parameter 'fromUri' is null!");
		}

		boolean isFromRemote = this.isRemoteUri(this.fromUri);
		boolean isToRemote = this.isRemoteUri(this.toUri);
		logger.log(PromidocsLogger.INFO, String.format("isFromRemote is %s",
				isFromRemote ? "true" : "false"));
		logger.log(PromidocsLogger.INFO, String.format("isToRemote is %s",
				isToRemote ? "true" : "false"));

		if (isFromRemote && !isToRemote) {
			logger.log(PromidocsLogger.INFO, "DOWNLOAD MODE");
			download();
		} else if (!isFromRemote && isToRemote) {
			logger.log(PromidocsLogger.INFO, "UPLOAD MODE");
			upload();
		} else if (isFromRemote && isToRemote) {
			throw new SCPException(
					"Copying from a remote server to a remote server is not supported.");
		} else {
			throw new SCPException(
					"Follow SCP sintax as user:password@host:/path");

		}

	}

	private void download() throws SCPException {
		String remote = this.parseUri(fromUri);
		logger.log(PromidocsLogger.INFO, String.format("remote is %s", remote));

		File local = new File(toUri);
		logger.log(PromidocsLogger.INFO,
				String.format("The local path is %s", local.getAbsolutePath()));

		try {
			download(remote, local);
		} catch (Exception e) {
			logger.log(PromidocsLogger.ERROR, e.getMessage(), e);
		}
	}

	private void download(String fromSshUri, File toPath) throws SCPException,
			JSchException, IOException {
		String file = parseUri(fromSshUri);

		Session session = null;
		try {
			session = openSession();
			ScpFromMessage message = null;
			if (!isSftp)
				message = new ScpFromMessage(isVerbose(), session, file,
						toPath, this.isRecursive);
			else
				message = new ScpFromMessageBySftp(isVerbose(), session, file,
						toPath, this.isRecursive);

			logger.log(PromidocsLogger.INFO, "Receiving file " + file);
			message.execute();
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
	}

	private void upload() throws SCPException, JSchException, IOException {
		try {
			from = new File(fromUri).getCanonicalFile();
		} catch (IOException e) {
			throw new SCPException(e);
		}

		logger.log(PromidocsLogger.INFO, "from is " + from);

		String wildcards = null;
		if (from.isFile()) {
			// TODO: Check DB for tag.
			this.upload(from, toUri);
		} else if (from.isDirectory()) {
			logger.log(PromidocsLogger.INFO, "Directory mode");
			wildcards = "*.*";
			logger.log(PromidocsLogger.INFO, "wildcard is " + wildcards);

			FileFilter fileFilter = new WildcardFileFilter(wildcards);
			File[] listOfFilesAndDirs = from.listFiles(fileFilter);
			logger.log(PromidocsLogger.INFO, "Found "
					+ listOfFilesAndDirs.length + " files and directories.");
			this.currentDir = new Directory(from, null);

			Filer filer = new Filer(this);
			try {
				filer.searchDirectoryTree(from, null);
				dirList.add(currentDir);
			} catch (FileException e) {
				throw new SCPException(e);
			}
			String file = parseUri(toUri);
			Session session = null;
			try {
				logger.log(PromidocsLogger.INFO,
						"dirList.size()=" + dirList.size());
				if (!this.dirList.isEmpty()) {
					session = openSession();
					ScpToMessage message = null;
					message = new ScpToMessage(isVerbose(), session,
							this.dirList, file);
					/*
					 * if (!isSftp) { message = new ScpToMessage(isVerbose(),
					 * session, this.dirList, file); } else { message = new
					 * ScpToMessageBySftp(isVerbose(), session, this.dirList,
					 * file); }
					 */
					message.execute();
				}
			} catch (JSchException e) {
				throw new SCPException(e);
			} catch (IOException e) {
				throw new SCPException(e);
			} finally {
				if (session != null)
					session.disconnect();
			}
		} else if (new File(fromUri).getName().indexOf("*") != -1
				|| new File(fromUri).getName().indexOf("?") != -1) {
			
			logger.log(PromidocsLogger.INFO, "Wildcards mode");
			try{
				from = new File(fromUri).getParentFile().getCanonicalFile();
			}catch(IOException e){
				throw new SCPException(e);
			}
			
			logger.log(PromidocsLogger.INFO, "from is " + from);
			
			wildcards = new File(fromUri).getName();
			logger.log(PromidocsLogger.INFO, "wildcards is " + wildcards);
			
			this.currentDir = new Directory(from, null);
			logger.log(PromidocsLogger.INFO, "current is " + currentDir.getDirectory());
			
			FileFilter fileFilter = new WildcardFileFilter(wildcards);
			File[] listOfFilesAndDirs = from.listFiles(fileFilter);
			logger.log(PromidocsLogger.INFO, "Found " + listOfFilesAndDirs.length + " files and directories.");
			
			Vector<File> listOfFiles = new Vector<File>();
			for(int j=0; j < listOfFilesAndDirs.length; j++){
				if(listOfFilesAndDirs[j].isFile()){
					logger.log(PromidocsLogger.INFO, "listOfFilesAndDirs[" + j + "]=" + listOfFilesAndDirs[j]);
					listOfFiles.add(listOfFilesAndDirs[j]);
				}
			}
			
			int totalOfFilesFound = listOfFiles.size();
			logger.log(PromidocsLogger.INFO, "Found " + totalOfFilesFound + " files.");
			for(int j=0; j<totalOfFilesFound; j++){
				File localFile = listOfFiles.get(j);
				upload(localFile, toUri);
			}
			
		} else {
			throw new SCPException(fromUri + " not valid!");
		}

	}

	/**
	 * @param fromUri
	 * @param toUri
	 * @throws SCPException 
	 * @throws JSchException 
	 * @throws IOException 
	 */
	private void upload(File fromUri, String toUri) throws SCPException, JSchException, IOException {
		String file = this.parseUri(toUri);
		Session session = null;
		try{
			session = openSession();
			ScpToMessage message = null;
			message = new ScpToMessage(isVerbose(), session, fromUri, file);
			/*if(!isSftp)
				message = new ScpToMessage(isVerbose(), session, fromUri, file);
			else
				message = new ScpToMessageBySftp(isVerbose(), session, fromUri, file);*/
			message.setChmodFile(this.chmodFile);
			message.setChmodDirectory(this.chmodDirectory);
			message.execute();
		}finally{
			if(session!=null)
				session.disconnect();
		}
	}

	/**
	 * @param uri
	 * @return
	 */
	private String parseUri(String uri) throws SCPException {
		logger.log(PromidocsLogger.INFO, String.format("uri is %s", uri));
		int indexOfAt = uri.indexOf('@');
		int indexOfColon = uri.indexOf(':');

		if (indexOfColon > -1 && indexOfColon < indexOfAt) {
			// user:password@host:/path notation
			// everything up to the last @ before the last :is considered
			// password.
			// If the path contains @ or : it will not work.
			int indexOfCurrentAt = indexOfAt;
			int indexOfLastColon = uri.lastIndexOf(':');
			while (indexOfCurrentAt > -1 && indexOfCurrentAt < indexOfLastColon) {
				indexOfAt = indexOfCurrentAt;
				indexOfCurrentAt = uri.indexOf('@', indexOfCurrentAt + 1);
			}
			setUserName(uri.substring(0, indexOfColon));
			setPassword(uri.substring(indexOfColon + 1, indexOfAt));
		} else if (indexOfAt > -1) {
			// no password, will require keyfile
			setUserName(uri.substring(0, indexOfAt));
		} else {
			throw new SCPException(
					"No user name was given. Can not authenticate.");
		}

		int indexOfPath = uri.indexOf(':', indexOfAt + 1);
		if (indexOfPath == -1) {
			throw new SCPException(String.format("No remote path in ", uri));
		}

		setHost(uri.substring(indexOfAt + 1, indexOfPath));
		String remotePath = uri.substring(indexOfPath + 1);

		if (getUserInfo().getPassword() == null
				&& getUserInfo().getKeyfile() == null
				&& getUserInfo().getPassphrase() == null
				&& this.askPassword == true) {

			// ASK FOR THE PASSWORD
			String promptMessage = String.format("%s@%s's password:",
					getUserInfo().getName(), getHost());
			String pwd = CMDUserInput.inputString(promptMessage, null);
			logger.log(PromidocsLogger.INFO,
					String.format("Just read password from user"));

			if (pwd == null) {
				String errMessage = String
						.format("Neither password nor keyfile for user %s has been given. Can not authenticate.",
								getUserInfo().getName());
				throw new SCPException(errMessage);
			} else {
				setPassword(pwd);
			}

			if ("".equals(remotePath)) {
				remotePath = ".";
			}
			return remotePath;

		}

		return null;
	}

	/**
	 * @param uri
	 * @return
	 */
	private boolean isRemoteUri(String uri) {
		logger.log(PromidocsLogger.INFO, String.format("Check the uri %s", uri));
		boolean isRemote = true;
		File f = new File(uri);
		if (f.getParentFile().exists()) {
			isRemote = false;
		}
		return isRemote;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inspiracode.launcher.file.Fileable#onFileFound(java.io.File)
	 */
	public void onFileFound(File aFile) {
		logger.log(PromidocsLogger.INFO, "Added file " + aFile.getName());
		currentDir.addFile(aFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inspiracode.launcher.file.Fileable#onDirFound(java.io.File)
	 */
	public void onDirFound(File aDirectory) {
		currentDir = new Directory(aDirectory, currentDir.getParent());
		dirList.add(currentDir);
	}

	/**
	 * @param source
	 */
	public void setFromUri(String source) {
		this.fromUri = source;
	}

	/**
	 * @param target
	 */
	public void setToUri(String target) {
		this.toUri = target;
	}

	/**
	 * @param recursive
	 */
	public void setRecursive(boolean recursive) {
		isRecursive = recursive;
	}

	/**
	 * @param ask
	 */
	public void setAskPassword(boolean ask) {
		this.askPassword = ask;
	}

	/**
	 * @param fchmod
	 */
	public void setChmodFile(String fchmod) {
		this.chmodFile = fchmod;
	}

	/**
	 * @param dchmod
	 */
	public void setChmodDirectory(String dchmod) {
		this.chmodDirectory = dchmod;
		
	}
}
