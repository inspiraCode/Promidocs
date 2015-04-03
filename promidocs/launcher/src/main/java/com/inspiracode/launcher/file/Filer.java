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
package com.inspiracode.launcher.file;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.inspiracode.launcher.ssh.Directory;
import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;

/**
 * File search and manipulation tool
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
public class Filer {
	private static PromidocsLogger logger = PromidocsLogFactory
			.getLoggerInstance(Filer.class.getName());

	private Fileable target;

	public Filer(Fileable fileable) {
		this.target = fileable;
	}

	/**
	 * 
	 * @param f
	 * @throws FileException
	 */
	public void searchDirectoryFile(File f) throws FileException {
		this.searchFile(f, "*.*");
	}

	/**
	 * @param f
	 * @param string
	 * @throws FileException
	 */
	public void searchFile(File f, String pattern) throws FileException {
		try {
			if (f.isDirectory()) {
				String[] list = f.list();
				for (String child : list) {
					this.searchFile(new File(f, child), pattern);
				}
			} else {
				this.target.onFileFound(f);
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
	}

	/**
	 * 
	 * @param f
	 * @throws FileException
	 */
	public void searchFile(File f) throws FileException {
		this.searchFile(f, "*.*");
	}

	public void searchDirectoryTree(File f, Directory parentDir)
			throws FileException {
		try {
			if (f.isDirectory()) {
				final Directory directory = new Directory(f);
				logger.log(PromidocsLogger.INFO, String.format("Added directory %s", f));
				if (parentDir != null) {
					parentDir.addDirectory(directory);
				}
				String[] list = f.list();
				for (String fileName : list) {
					this.searchDirectoryTree(new File(f, fileName), directory);
				}
			} else {
				assert parentDir != null;
				parentDir.addFile(f);
				logger.log(PromidocsLogger.INFO, String.format("Added file %s", f));
			}
		} catch (Throwable t) {
			throw new FileException(t);
		}
	}

	public void searchDirectoryFile(File fromDirectory, String pattern,
			boolean recursive) throws FileException {
		try {
			if (fromDirectory.isDirectory()) {
				this.target.onDirFound(fromDirectory);
				FileFilter fileFilter = new WildcardFileFilter(pattern);
				File[] files = fromDirectory.listFiles(fileFilter);
				for (int i = 0; i < files.length; i++) {
					this.target.onFileFound(files[i]);
				}
				if (recursive) {
					String[] list = fromDirectory.list();
					for (int i = 0; i < list.length; i++) {
						this.searchDirectoryFile(new File(fromDirectory,
								list[i]), pattern, recursive);
					}
				}
			}
		} catch (Exception e) {
			throw new FileException(e);
		}
	}

	public void searchDirectory(File f) throws FileException {
		try {
			if (f.isDirectory()) {
				this.target.onDirFound(f);
				String[] list = f.list();
				for (int i = 0; i < list.length; i++) {
					this.searchDirectory(new File(f, list[i]));
				}
			}
		} catch (Throwable t) {
			throw new FileException(t);
		}
	}

}
