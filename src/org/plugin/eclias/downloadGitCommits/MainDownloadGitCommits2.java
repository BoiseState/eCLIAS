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
		try (Git git = Git.open(new File("/Users/Vasanth/git/eCLIAS/"))) {
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
                    System.out.println(MessageFormat.format("({0} {1} {2}", diff.getChangeType(), diff.getNewMode().getBits(), diff.getOldPath()));
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



	//private static File getFile(Repository repository, String sRelativePath2File, RevTree tree) throws Exception{
//  // use the blob id to read the file's data
//  File f = Files.createTempFile(UUID.randomUUID().toString(), ".java").toFile();
//
//  ObjectReader reader = null;
//
//  try (FileOutputStream fop = new FileOutputStream(f)){
//      reader = repository.newObjectReader();
//      // determine treewalk by relative path
//      TreeWalk treewalk = TreeWalk.forPath(reader, sRelativePath2File, tree);
//      // open object
//      reader.open(treewalk.getObjectId(0)).copyTo(fop);
//
//  } finally{
//  	reader.release();
//  }
//
//  return f;
//}}


//	public static void main(String args[]) {
//		try (Git git = Git.open(new File(System.getProperty("user.dir") + "/clonedrepo/"))) {
//			Repository repository = git.getRepository();
//			System.out.println("repo is:" + repository);
//			RevWalk walk = new RevWalk(repository);
//
//		// find the HEAD
//		ObjectId lastCommitId = repository.resolve(Constants.HEAD);
//		// now we have to get the commit
//		RevWalk revWalk = new RevWalk(repository);
//		RevCommit commit = revWalk.parseCommit(lastCommitId);
//		// and using commit's tree find the path
//		RevTree tree = commit.getTree();
//		TreeWalk treeWalk = new TreeWalk(repository);
//		treeWalk.addTree(tree);
//		treeWalk.setRecursive(true);
//		treeWalk.setFilter(PathFilter.create("README.md"));
////		if (!treeWalk.next()) {
////		  return;
////		}
//		ObjectId objectId = treeWalk.getObjectId(0);
//		ObjectLoader loader = repository.open(objectId);
//
//		loader.copyTo(System.out);
//	}
//		catch(Exception e){
//			
//		}

//} }
