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
package com.inspiracode.launcher.util;

import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;

/**
 * USAGE HERE
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
public class UtilityException extends Exception {
	private static final long serialVersionUID = 5644128544019126618L;
	private static PromidocsLogger logger = PromidocsLogFactory.getLoggerInstance(UtilityException.class.getName());
	/**
	 * 
	 */
	public UtilityException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UtilityException(String message, Throwable cause) {
		super(message, cause);
		logger.log(PromidocsLogger.ERROR, message, cause);
	}
	/**
	 * @param message
	 */
	public UtilityException(String message) {
		super(message);
		logger.log(PromidocsLogger.ERROR, message);
	}
	/**
	 * @param cause
	 */
	public UtilityException(Throwable cause) {
		super(cause);
		logger.log(PromidocsLogger.ERROR, cause.getMessage(), cause);
	}
}
