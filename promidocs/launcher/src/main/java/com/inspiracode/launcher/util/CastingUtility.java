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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

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
public class CastingUtility {
	private static PromidocsLogger logger = PromidocsLogFactory.getLoggerInstance(CastingUtility.class.getName());
	public final static String NEWLINE = System.getProperty("line.separator");

	public static int stringToInt(String s) throws UtilityException {
		int out = -1;
		try {
			out = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new UtilityException(e);
		}
		return out;
	}

	public static boolean stringToBoolean(String s) throws UtilityException {
		if (s == null) {
			return false;
		} else {
			return s.trim().equalsIgnoreCase("true");
		}
	}

	public static void stringToFile(File file, String encoding, String buffer)
			throws UtilityException {
		BufferedWriter bw = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new StringReader(buffer));
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), encoding));
			String line = null;
			String space = "";
			while ((line = br.readLine()) != null) {
				bw.write(space);
				bw.write(line);
				space = NEWLINE;
			}
			logger.log(PromidocsLogger.INFO, "Write " + buffer.length() + " bytes to file.");
			logger.log(PromidocsLogger.DEBUG, "The buffer is:" + NEWLINE + buffer);
		} catch (IOException ioe) {
			throw new UtilityException(ioe);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}

}
