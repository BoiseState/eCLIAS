package org.plugin.eclias.downloadGitCommits;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class MainDownloadGitCommits {

	public static String outputFolder = "/Users/Vasanth/git/eCLIAS/GIT_metadata/";
	
	public static void testDownloadGitCommits(String repoAddress, String startRevision, String endRevision)
			throws Exception {

	
		File newDirectory = new File(outputFolder);
		FileUtils.deleteDirectory(newDirectory);
		newDirectory.mkdirs();

		DownloadGitCommits downloadGitCommits = new DownloadGitCommits(repoAddress, startRevision, endRevision,
				outputFolder, "guest", "");

		downloadGitCommits.initializeRepository(repoAddress);
		downloadGitCommits.downloadGitCommits();

		System.out.println("The data has been saved in the folder: " + outputFolder);
	}

}