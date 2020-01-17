package org.plugin.eclias.downloadSVNCommits;

import java.io.File;

public class MainDownloadSVNCommits
{
	public static void testDownloadSVNCommitsArgoUML(String repoAddress, String startRevision, String endRevision) throws Exception
	{
		
		String outputFolder= System.getProperty("user.dir") + "/SVN_metadata/"; 

		File newDirectory = new File(outputFolder);
		newDirectory.mkdirs();
		
		DownloadSVNCommits downloadSVNCommits=new DownloadSVNCommits(
				repoAddress,
				startRevision,
				endRevision,
				outputFolder,
				"guest",
				"");
		
		downloadSVNCommits.initializeRepository();
		downloadSVNCommits.downloadSVNCommits();
		
		System.out.println("The data has been saved in the folder: "+outputFolder);
	}
	
}
