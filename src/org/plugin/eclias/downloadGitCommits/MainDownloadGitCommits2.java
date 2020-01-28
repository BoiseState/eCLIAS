package org.plugin.eclias.downloadGitCommits;

import java.io.File;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.gitective.core.BlobUtils;

public class MainDownloadGitCommits2 {

	public static void main(String[] args) throws Exception {

		File localPath = File.createTempFile("JGitTestRepository", "");
		// delete repository before running this
		Files.delete(localPath.toPath());

		// This code would allow to access an existing repository
		try (Git git = Git.open(new File("/Users/Vasanth/git/CS510_Database_Project/"))) {
			Repository repository = git.getRepository();
			System.out.println("repo is:" + repository);
			RevWalk walk = new RevWalk(repository);
//      }

			// List all branches
			List<Ref> call = git.branchList().call();
			for (Ref ref : call) {
//                System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
//            }

				String branchName = ref.getName();

				System.out.println("Commits of branch: " + ref.getName());
				System.out.println("-------------------------------------");

				Iterable<RevCommit> commits = git.log().all().call();

				for (RevCommit commit : commits) {
					boolean foundInThisBranch = false;

					RevCommit targetCommit = walk.parseCommit(repository.resolve(commit.getName()));
					for (Map.Entry<String, Ref> e : repository.getAllRefs().entrySet()) {
						if (e.getKey().startsWith(Constants.R_HEADS)) {
							if (walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId()))) {
								String foundInBranch = e.getValue().getName();
								if (branchName.equals(foundInBranch)) {
									foundInThisBranch = true;
									break;
								}
							}
						}
					}

					RevTree treeId = commit.getTree();

					TreeWalk treeWalk = new TreeWalk(repository);
					treeWalk.reset(treeId);
					while (treeWalk.next()) {
						String path = treeWalk.getPathString();
						System.out.println("Flie modified/changed is:" + path);
						
					}
					


					if (foundInThisBranch) {
						String rThree = commit.getName();
						String r3 = "a4372ba771fe438a41a006bc04093c8dc05f7309";
						String endRevNo = "123";
//						if (rThree.equals(r3)) {
//							System.out.println("coming here");
//							System.out.println(commit.getFullMessage());
////							r3 is true 
////						}
////						
//						if (r3 is true) {
//							
//						}
						System.out.println(commit.getName());
						
						System.out.println(commit.getAuthorIdent().getName());
						
						System.out.println(new Date(commit.getCommitTime() * 1000L));

						System.out.println(commit.getFullMessage());
						
//						getFile(repository, "/Users/Vasanth/git/eCLIAS/GIT_metadata/GitFilesSideBySide", treeId);
					}

                Iterable<RevCommit> log = git.log().call();
                for (Iterator<RevCommit> iterator = log.iterator(); iterator.hasNext();) {
                  RevCommit rev = iterator.next();
//                 System.out.println("log message is:"+ rev.getFullMessage());
                }
                RevWalk rw = new RevWalk(repository);
                ObjectId head = repository.resolve(Constants.HEAD);
                RevCommit commit1 = rw.parseCommit(head)  ;
                RevCommit parent = rw.parseCommit(commit1.getParent(0).getId());
                DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
                df.setRepository(repository);
                df.setDiffComparator(RawTextComparator.DEFAULT);
                df.setDetectRenames(true);
                List<DiffEntry> diffs = df.scan(parent.getTree(), commit1.getTree());
                for (DiffEntry diff : diffs) {
                    System.out.println(MessageFormat.format("({0} {1} {2}", diff.getChangeType(), diff.getOldPath(), diff.getNewPath()));
//                    String fileNameOnRepository = BlobUtils.getHeadContent(repository, diff.getOldPath());
//					System.out.println(fileNameOnRepository);
                }
				}
			}

			Status status = git.status().call();

			Set<String> added = status.getAdded();
			for (String add : added) {
				System.out.println("Added: " + add);
			}
//            Set<String> uncommittedChanges = status.getUncommittedChanges();
//            for (String uncommitted : uncommittedChanges) {
//                System.out.println("Uncommitted: " + uncommitted);
//            }
//
//            Set<String> untracked = status.getUntracked();
//            for (String untrack : untracked) {
//                System.out.println("Untracked: " + untrack);
//            }

			// Find the head for the repository
			ObjectId lastCommitId = repository.resolve(Constants.HEAD);
//			ObjectId lastBeforeCommitId = commit.getParent(0).getId();
			System.out.println("Head points to the following commit :" + lastCommitId.getName());
//			System.out.println("Head points to the following commit :" + );
			

//			DownloadGitCommits.initializeRepository("https://github.com/vasanthgeethanraju/Gray_Hat_Python.git");
//			String outputFolder = "/Users/Vasanth/git/eCLIAS/GIT_metadata/";
//
//			File newDirectory = new File(outputFolder);
//			newDirectory.mkdirs();
//
//			DownloadGitCommits downloadGitCommits = new DownloadGitCommits(
//					"http://argouml.tigris.org/svn/argouml/trunk", "12345", "12348", outputFolder, "guest", "");
//			System.out.println("------------------------------------------------------------------------");
//			DownloadGitCommits.downloadGitCommits();
//
//			System.out.println("downloadrepositoryover");
		}

	}
}




//package org.plugin.eclias.downloadGitCommits;
//
//import java.io.File;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.io.FileUtils;
//import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.diff.DiffEntry;
//import org.eclipse.jgit.diff.DiffFormatter;
//import org.eclipse.jgit.diff.RawTextComparator;
//import org.eclipse.jgit.lib.Constants;
//import org.eclipse.jgit.lib.ObjectId;
//import org.eclipse.jgit.lib.Ref;
//import org.eclipse.jgit.lib.Repository;
//import org.eclipse.jgit.revwalk.RevCommit;
//import org.eclipse.jgit.revwalk.RevTree;
//import org.eclipse.jgit.revwalk.RevWalk;
//import org.eclipse.jgit.treewalk.TreeWalk;
//import org.eclipse.jgit.util.io.DisabledOutputStream;
//import org.tmatesoft.svn.core.SVNLogEntryPath;
//import org.tmatesoft.svn.core.io.SVNRepository;
//
//public class DownloadGitCommits {
//	private String url;
//	private long startRevision;
//	private long endRevision;
//	private String username;
//	private String password;
//	private SVNRepository repository;
//	public InputOutputDownloadGitCommits inputOutput;
//	public static String cloneAddress = System.getProperty("user.dir") + "/clonedrepo/";
//
//	DownloadGitCommits(String url, String startRevision, String endRevision, String outputFolder, String username,
//			String password) {
//		this.url = url;
//		this.startRevision = Long.parseLong(startRevision);
//		this.endRevision = Long.parseLong(endRevision);
//		this.inputOutput = new InputOutputDownloadGitCommits(outputFolder);
//		this.username = username;
//		this.password = password;
//
//		this.repository = null;
//	}
//
//	void initializeRepository(String url) throws Exception {
//		File newDirectory = new File(cloneAddress);
//		FileUtils.deleteDirectory(newDirectory);
//		newDirectory.mkdirs();
//		Git git = Git.cloneRepository().setURI(url).setDirectory(newDirectory).call();
//	}
//
//	void downloadGitCommits() throws Exception {
//		inputOutput.initializeFolderStructure();
//		inputOutput.clearListOfGitCommits();
//		String gitLogEntryPathDebug = "";
//
//		try (Git git = Git.open(new File(cloneAddress))) {
//			Repository repository = git.getRepository();
//			System.out.println("repo is:" + repository);
//			RevWalk walk = new RevWalk(repository);
//
//			List<Ref> call = git.branchList().call();
//
//			for (Ref ref : call) {
////                System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
////            }
//
//				String branchName = ref.getName();
//
//				System.out.println("Commits of branch: " + ref.getName());
//				System.out.println("-------------------------------------");
//
//				Iterable<RevCommit> commits = git.log().all().call();
//
//				for (RevCommit commit : commits) {
//
//					boolean foundInThisBranch = false;
//
//					RevCommit targetCommit = walk.parseCommit(repository.resolve(commit.getName()));
//					for (Map.Entry<String, Ref> e : repository.getAllRefs().entrySet()) {
//						if (e.getKey().startsWith(Constants.R_HEADS)) {
//							if (walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId()))) {
//								String foundInBranch = e.getValue().getName();
//								if (branchName.equals(foundInBranch)) {
//									foundInThisBranch = true;
//									break;
//								}
//							}
//						}
//					}
//					RevTree treeId = commit.getTree();
//					TreeWalk treeWalk = new TreeWalk(repository);
//					treeWalk.reset(treeId);
//					ObjectId head = repository.resolve(Constants.HEAD);
////					ObjectId penultimate = head.
//					RevCommit commit1 = walk.parseCommit(head);
//					RevCommit parentCommit = walk.parseCommit(commit1.getParent(0).getId());
//
//					DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
//					df.setRepository(repository);
//					df.setDiffComparator(RawTextComparator.DEFAULT);
//					df.setDetectRenames(true);
//					List<DiffEntry> diffs = df.scan(parentCommit.getTree(), commit1.getTree());
//					for (DiffEntry diff : diffs) {
//
//						if (foundInThisBranch) {
//							inputOutput.createRevisionFolderInFolderSideBySideFiles(commit);
//							
//							String listOfFiles = "";
//							
//							String append = "";
//							
//							StringBuilder buf = new StringBuilder();
//							while (treeWalk.next()) {
//								String path = treeWalk.getPathString();
//								System.out.println("File modified/changed is:" + path);
//								append = append.concat(diff.getChangeType().name().substring(0, 1)).concat("\t")
//										.concat(path).concat("\n");
//								
////								String fileNameOnRepository = BlobUtils.getHeadContent(repository, diff.getOldPath());
//////								String fileNameOnRepository1 = BlobUtils.getContent(repository, parentCommit, diff.getNewPath());
////								buf.append(fileNameOnRepository);
////							fileappend = fileappend.concat(fileNameOnRepository);
////								System.out.println(fileNameOnRepository);
//							}
//							System.out.println(buf);
//
//							inputOutput.saveListOfFiles(commit, append);
//							String debugInformation = inputOutput.GITLogEntryToStringDebug(commit);
////						System.out.println(debugInformation);
//							inputOutput.saveGitComments(commit);
//
//							gitLogEntryPathDebug = inputOutput.GITLogEntryPathToStringDebug(commit);
//							debugInformation += gitLogEntryPathDebug + inputOutput.LINE_ENDING;
////						System.out.println(gitLogEntryPathDebug);
//
//							inputOutput.saveGitDebugInformation(commit, debugInformation);
//							inputOutput.appendToListOfGitCommitsDebug(commit);
//
//							inputOutput.appendToListOfGitCommits(commit);
//						}
//
//					}
//				}
//			}
//		}
//
//		catch (Exception e) {
//			System.out.println("error");
//			e.printStackTrace();
//		}
//
//	}
//
//
//}
