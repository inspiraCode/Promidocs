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

import com.inspiracode.promidocs.logger.PromidocsLogFactory;
import com.inspiracode.promidocs.logger.PromidocsLogger;

/**
 * SCP Process exception
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
public class SCPException extends Exception {
	private static final long serialVersionUID = 3737323639332617615L;
	private static final PromidocsLogger logger = PromidocsLogFactory
			.getLoggerInstance(SCPException.class.getName());

	/**
	 * @param message
	 */
	public SCPException(String message) {
		super(message);
		logger.log(PromidocsLogger.ERROR, message);
	}

	/**
	 * @param cause
	 */
	public SCPException(Throwable cause) {
		super(cause);
		logger.log(PromidocsLogger.ERROR, cause.getMessage(), cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SCPException(String message, Throwable cause) {
		super(message, cause);
		logger.log(PromidocsLogger.ERROR, message, cause);
	}
}
