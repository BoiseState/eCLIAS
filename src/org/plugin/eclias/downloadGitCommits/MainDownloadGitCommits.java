package org.plugin.eclias.downloadGitCommits;

import java.io.File;

public class MainDownloadGitCommits {

	public static void testDownloadGitCommits(String repoAddress, String startRevision, String endRevision)
			throws Exception {

		String outputFolder = System.getProperty("user.dir") + "/GIT_metadata/";

		File newDirectory = new File(outputFolder);
		newDirectory.mkdirs();

		DownloadGitCommits downloadGitCommits = new DownloadGitCommits(repoAddress, startRevision, endRevision,
				outputFolder, "guest", "");

		downloadGitCommits.initializeRepository();
		downloadGitCommits.downloadSVNCommits();

		System.out.println("The data has been saved in the folder: " + outputFolder);
	}

}