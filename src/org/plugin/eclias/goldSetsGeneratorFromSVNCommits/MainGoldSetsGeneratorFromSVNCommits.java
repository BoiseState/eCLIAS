package org.plugin.eclias.goldSetsGeneratorFromSVNCommits;

import java.io.File;

public class MainGoldSetsGeneratorFromSVNCommits {
	public static String outputFolder = System.getProperty("user.dir") + "/SVN_metadata/";

	public static void testArgoUML() throws Exception {
		String fileNameListOfSVNCommits = System.getProperty("user.dir") + "/SVN_metadata/listOfSVNCommits.txt";
		String folderNameListOfFiles = System.getProperty("user.dir") + "/SVN_metadata/SVNListOfFiles/";
		String folderNameListOfFilesSideBySide = System.getProperty("user.dir") + "/SVN_metadata/SVNFilesSideBySide/";

		File newDirectory = new File(outputFolder);
		newDirectory.mkdirs();

		GoldSetGeneratorFromSVNCommits goldSetGeneratorFromSVNCommits = new GoldSetGeneratorFromSVNCommits(
				fileNameListOfSVNCommits, folderNameListOfFiles, folderNameListOfFilesSideBySide, outputFolder);

		goldSetGeneratorFromSVNCommits.parseAndSaveMultipleSVNCommits();

		System.out.println("The data has been saved in the folder: " + outputFolder);
	}

}
