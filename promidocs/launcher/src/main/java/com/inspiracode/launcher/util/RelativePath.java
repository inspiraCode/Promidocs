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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides functions used to generate a relative path from two absolute paths.
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
public class RelativePath {

	public static String getRelativePath(File home, File relative) {
		List<String> homeList;
		List<String> relativeList;
		String match;

		homeList = getPathList(home);
		relativeList = getPathList(relative);
		match = matchPathLists(homeList, relativeList);

		return match;
	}

	/**
	 * calculate a string representing the relative path.
	 * 
	 * @param homeList
	 * @param relativeList
	 * @return
	 */
	private static String matchPathLists(List<String> homeList,
			List<String> relativeList) {

		String result = "";
		int i = homeList.size() - 1, j = relativeList.size() - 1;

		// start at the beginning of the lists
		// iterate while both lists are equal
		// first eliminate common root
		while (i >= 0 && j >= 0 && homeList.get(i).equals(relativeList.get(j))) {
			i--;
			j--;
		}
		
		// for each remaining level in the home path, add a ".."
		for(;i>=0;i--){
			result += ".." + File.separator;
		}
		
		// for each level in the file path, add the path
		for(;j>=1;j--){
			result += relativeList.get(j) + File.separator;
		}
		
		// file name
		result += relativeList.get(j);
		
		return result;
	}

	/**
	 * Break a path down into individual elements and add to a list. If a path
	 * is /a/b/c/d.txt, the breakdown will be [d.txt, c, b, a]
	 * 
	 * @param file
	 *            input file
	 * @return a List collection with the individual elements of the path in
	 *         reverse order.
	 */
	private static List<String> getPathList(File file) {
		List<String> result = new ArrayList<String>();
		File individualFile;
		try {
			individualFile = file.getCanonicalFile();
			while (individualFile != null) {
				result.add(individualFile.getName());
				individualFile = individualFile.getParentFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

}
