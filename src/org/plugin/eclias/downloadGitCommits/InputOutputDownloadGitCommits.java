package org.plugin.eclias.downloadGitCommits;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.tmatesoft.svn.core.SVNLogEntryPath;

public class InputOutputDownloadGitCommits {
	public static final String LINE_ENDING = "\r\n";
	public static String[] listOfValidExtensions = new String[] { ".java", ".txt", ".xml", ".html", ".htm" };

	public static final String FOLDER_NAME_GIT_FILES_SIDE_BY_SIDE = "GitFilesSideBySide/";
	public static final String FOLDER_NAME_GIT_COMMENTS = "GitComments/";
	public static final String FOLDER_NAME_GIT_LIST_OF_FILES = "GitListOfFiles/";
	public static final String FOLDER_NAME_GIT_DEBUG = "GitDebug/";
	public static final String FILE_NAME_LIST_OF_GIT_COMMITS = "listOfGitCommits.txt";
	public static final String FILE_NAME_LIST_OF_GIT_COMMITS_DEBUG = FOLDER_NAME_GIT_DEBUG
			+ "listOfGITCommitsDebug.txt";

	private static String outputFolder;

	public InputOutputDownloadGitCommits(String outputFolder) {
		InputOutputDownloadGitCommits.outputFolder = outputFolder;
	}

	public static String getFolderNameGitFilesSideBySide() {
		return outputFolder + FOLDER_NAME_GIT_FILES_SIDE_BY_SIDE;
	}

	public static String getFolderNameGitComments() {
		return outputFolder + FOLDER_NAME_GIT_COMMENTS;
	}

	public static String getFolderNameGitListOfFiles() {
		return outputFolder + FOLDER_NAME_GIT_LIST_OF_FILES;
	}

	public static String getFolderNameGitDebug() {
		return outputFolder + FOLDER_NAME_GIT_DEBUG;
	}

	public String getFileNameListOfGitCommits() {
		return outputFolder + FILE_NAME_LIST_OF_GIT_COMMITS;
	}

	public String getFileNameListOfGitCommitsDebug() {
		return outputFolder + FILE_NAME_LIST_OF_GIT_COMMITS_DEBUG;
	}

	public void initializeFolderStructure() throws Exception {
		createFolder(outputFolder);
		createFolder(getFolderNameGitFilesSideBySide());
		createFolder(getFolderNameGitComments());
		createFolder(getFolderNameGitListOfFiles());
		createFolder(getFolderNameGitDebug());
	}

	private static void createFolder(String folderName) throws Exception {
		File folder = new File(folderName);
		if (folder.exists())
			return;

		if (folder.mkdir() == false)
			throw new Exception();
	}

	public void clearListOfGitCommits() throws Exception {
		BufferedWriter outputFile = new BufferedWriter(new FileWriter(getFileNameListOfGitCommits()));
		outputFile.write("");
		outputFile.close();

		outputFile = new BufferedWriter(new FileWriter(getFileNameListOfGitCommitsDebug()));
		outputFile.write("");
		outputFile.close();
	}

	void saveFile(String outputFileName, String content) throws Exception {
		BufferedWriter outputFile = new BufferedWriter(new FileWriter(outputFileName));
		outputFile.write(content + LINE_ENDING);
		outputFile.close();
	}

	void saveFileWithoutLineEnding(String outputFileName, String content) throws Exception {
		BufferedWriter outputFile = new BufferedWriter(new FileWriter(outputFileName));
		outputFile.write(content);
		outputFile.close();
	}

	public void appendToFile(String outputFileName, String content) throws Exception {
		BufferedWriter outputFile = new BufferedWriter(new FileWriter(outputFileName, true));
		outputFile.write(content + LINE_ENDING);
		outputFile.close();
	}

	public void saveGitComments(RevCommit gitLogEntry) throws Exception {
		saveFile(getFolderNameGitComments() + gitLogEntry.getName() + ".GITComment", gitLogEntry.getFullMessage());
	}

	public void saveListOfFiles(RevCommit gitLogEntry, String listOfFiles) throws Exception {
		saveFileWithoutLineEnding(getFolderNameGitListOfFiles() + gitLogEntry.getName() + ".GITListOfFiles",
				listOfFiles);
	}

	public void saveGitDebugInformation(RevCommit gitLogEntry, String debugInformation) throws Exception {
		saveFile(getFolderNameGitDebug() + gitLogEntry.getName() + ".GITDebug", debugInformation);
	}

	public void createRevisionFolderInFolderSideBySideFiles(RevCommit gitLogEntry) throws Exception {
		createFolder(getFolderNameGitFilesSideBySide() + gitLogEntry.getName() + "/");
	}

	public String GITLogEntryPathToString(DiffEntry gitLogEntryPath) {
		return gitLogEntryPath.getChangeType().name() + "\t" + getFileNameOnDisk(gitLogEntryPath.getNewPath());
	}

	public String GITLogEntryToStringDebug(RevCommit gitLogEntry) {
		StringBuilder buf = new StringBuilder();
		buf.append("Revision: " + gitLogEntry.getName() + LINE_ENDING);
		buf.append("Author: " + gitLogEntry.getAuthorIdent().getName() + LINE_ENDING);
		buf.append("Date: " + (new Date(gitLogEntry.getCommitTime() * 1000L)) + LINE_ENDING);
		buf.append("Log:" + LINE_ENDING);
		buf.append(gitLogEntry.getFullMessage().replace("\n", " ").replace("\r", " ") + LINE_ENDING + LINE_ENDING);

		buf.append("List of files:" + LINE_ENDING);

		return buf.toString();
	}

	public String GITLogEntryPathToStringDebug(RevCommit gitLogEntryPath) {
		StringBuilder buf = new StringBuilder();
		buf.append(" " + gitLogEntryPath.getType() + " " + gitLogEntryPath.getTree());
		if (gitLogEntryPath.getTree() != null)
			buf.append(" (from " + gitLogEntryPath.getTree() + ":" + gitLogEntryPath.getName() + ")");

		buf.append(" - (" + gitLogEntryPath.getAuthorIdent().getName() + ")");
		return buf.toString();
	}

	String getFileNameOnDisk(String fileNameOnRepository) {
		return fileNameOnRepository.substring(1).replace("/", "_");
	}

	public boolean hasValidFileExtension(String fileNameOnRepository) {
		for (String extension : listOfValidExtensions) {
			if (fileNameOnRepository.endsWith(extension))
				return true;
		}
		return false;
	}

	public String getFileNameCurrentVersion(String fileNameOnRepository, String string) {
		return getFolderNameGitFilesSideBySide() + string + "/" + getFileNameOnDisk(fileNameOnRepository) + ".v"
				+ string;

	}

	public String getFileNamePreviousVersion(String fileNameOnRepository, String string) {
		return getFolderNameGitFilesSideBySide() + string + "/" + getFileNameOnDisk(fileNameOnRepository)
				+ ".vPrevious";
	}

	public void appendToListOfGitCommits(RevCommit gitLogEntry) throws Exception {
		appendToFile(getFileNameListOfGitCommits(), gitLogEntry.getName() + "");
	}

	public void appendToListOfGitCommitsDebug(RevCommit gitLogEntry) throws Exception {
		appendToFile(getFileNameListOfGitCommitsDebug(), gitLogEntry.getName() + "");
	}
}

//String fileNameOnRepository1 = BlobUtils.getContent(repository, parentCommit, diff.getNewPath());
