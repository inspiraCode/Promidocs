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
package com.inspiracode.promidocs.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logger implementation using log4j
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
public class PromidocsLog4jLoggerImpl implements PromidocsLogger {

	private transient final Logger logger;

	public PromidocsLog4jLoggerImpl(String className) {
		System.setProperty("java.util.logging.manager",
				"org.apache.logging.log4j.jul.LogManager");
		logger = LogManager.getLogger(className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inspiracode.promidocs.logger.PromidocsLogger#log(int,
	 * java.lang.String)
	 */
	public void log(int level, String message) {
		message = logger.getName() + ": " + message;
		switch (level) {
		case 1:
			logger.trace(message);
			break;
		case 2:
			logger.info(message);
			break;
		case 3:
			logger.debug(message);
			break;
		case 4:
			logger.warn(message);
			break;
		case 5:
			logger.error(message);
			break;
		default:
			logger.warn("No Logger Level Found");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inspiracode.promidocs.logger.PromidocsLogger#log(int,
	 * java.lang.String, java.lang.Throwable)
	 */
	public void log(int level, String message, Throwable thrower) {
		message = logger.getName() + ": " + message;
		switch (level) {
		case 1:
			logger.trace(message, thrower);
			break;
		case 2:
			logger.info(message, thrower);
			break;
		case 3:
			logger.debug(message, thrower);
			break;
		case 4:
			logger.warn(message, thrower);
			break;
		case 5:
			logger.error(message, thrower);
			break;
		default:
			logger.warn("No Logger Level Found");
		}
	}

}
