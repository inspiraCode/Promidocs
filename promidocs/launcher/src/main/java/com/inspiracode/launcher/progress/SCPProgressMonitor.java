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
package com.inspiracode.launcher.progress;

import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;
import com.jcraft.jsch.SftpProgressMonitor;

/**
 * monitor to log transfer progress
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
public class SCPProgressMonitor implements SftpProgressMonitor {

	private static final PromidocsLogger logger = PromidocsLogFactory
			.getLoggerInstance(SCPProgressMonitor.class.getName());

	private long initFileSize = 0;
	private long totalLength = 0;
	private int percentTransmitted = 0;

	private static final long ONE_MB = 1048576;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.SftpProgressMonitor#count(long)
	 */
	public boolean count(long length) {
		totalLength += length;
		percentTransmitted = trackProgress(initFileSize, totalLength,
				percentTransmitted);
		return true;
	}

	/**
	 * Track progress every 10% if 100kb < fileSize < 1mb. For larger files
	 * track progress for every percent transmitted.
	 * 
	 * @param fileSize
	 *            the size of the file being transmitted.
	 * @param length
	 *            the total transmission size.
	 * @param pctTransmitted
	 *            the current percent transmitted
	 * @return the percent that the file is of the total.
	 */
	private final int trackProgress(long fileSize, long length,
			int pctTransmitted) {
		int percent = (int) Math.round(Math
				.floor((length / (double) fileSize) * 100));

		if (percent > pctTransmitted) {
			// track progress every 10% if 100kb < fileSize < 1mb
			if (fileSize < ONE_MB) {
				if (percent % 10 == 0) {
					if (percent == 100) {
						logger.log(PromidocsLogger.INFO, " 100%");
					} else {
						logger.log(PromidocsLogger.INFO, "*");
					}
				}
			} else {
				switch (percent) {
				case 25:
				case 50:
				case 75:
				case 100:
					logger.log(PromidocsLogger.INFO,
							String.format("%d%", percent));
					break;
				default:
					break;
				}

			}
		}
		return percent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.SftpProgressMonitor#end()
	 */
	public void end() {
		logger.log(PromidocsLogger.INFO,
				"********** TRANSMISSION PROCESS COMPLETED *********");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.SftpProgressMonitor#init(int, java.lang.String,
	 * java.lang.String, long)
	 */
	public void init(int op, String src, String dest, long max) {
		initFileSize = max;
		totalLength = 0;
		percentTransmitted = 0;
	}

	/**
	 * Publish transmission file total size
	 * @return
	 */
	public long getTotalLength() {
		return totalLength;
	}

}
