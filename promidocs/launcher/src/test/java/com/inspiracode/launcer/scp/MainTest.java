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
package com.inspiracode.launcer.scp;

import org.junit.Test;

import com.inspiracode.launcher.scp.Main;

/**
 * Main program test cases
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
public class MainTest {

	@Test
	public void noParametersTest() {
		System.out
				.println("----------------  NO PARAMETERS TEST  -------------------------");
		String[] args = new String[0];
		Main.main(args);
	}

	@Test
	public void versionTest() {
		System.out
				.println("------------------  VERSION TEST (-version)  ---------------------------------");
		String[] args = new String[1];
		args[0] = "-version";
		Main.main(args);
		System.out
				.println("------------------  VERSION TEST (--verson)  ----------------------------------");
		args[0] = "--version";
		Main.main(args);
	}

	@Test
	public void helpTest() {
		System.out
				.println("------------------  HELP TEST (-h)  ---------------------------------");
		String[] args = new String[1];
		args[0] = "-h";
		Main.main(args);
	}

	public void uploadTest() {
		System.out
				.println("------------  UPLOAD TEST (-source file -target file)  ---------------------------------");
		String[] args = new String[4];
		args[0] = "-source";
		args[1] = "c:\\temp\\sepro\\t2.pdf";
		args[0] = "-target";
		args[0] = "sepro@inspiracode.net:/home/sepro/upload";
		Main.main(args);
	}
}
