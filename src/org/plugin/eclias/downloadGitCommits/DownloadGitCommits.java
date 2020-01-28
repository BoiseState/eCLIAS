package org.plugin.eclias.downloadGitCommits;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
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
import org.tmatesoft.svn.core.io.SVNRepository;

public class DownloadGitCommits {
	private String url;
	private long startRevision;
	private long endRevision;
	private String username;
	private String password;
	private SVNRepository repository;
	public InputOutputDownloadGitCommits inputOutput;
	public static String cloneAddress = System.getProperty("user.dir") + "/clonedrepo/";

	DownloadGitCommits(String url, String startRevision, String endRevision, String outputFolder, String username,
			String password) {
		this.url = url;
		this.startRevision = Long.parseLong(startRevision);
		this.endRevision = Long.parseLong(endRevision);
		this.inputOutput = new InputOutputDownloadGitCommits(outputFolder);
		this.username = username;
		this.password = password;

		this.repository = null;
	}

	void initializeRepository(String url) throws Exception {
		File newDirectory = new File(cloneAddress);
		FileUtils.deleteDirectory(newDirectory);
		newDirectory.mkdirs();
		Git git = Git.cloneRepository().setURI(url).setDirectory(newDirectory).call();
	}

	void downloadGitCommits() throws Exception {
		inputOutput.initializeFolderStructure();
		inputOutput.clearListOfGitCommits();
		String gitLogEntryPathDebug = "";

		try (Git git = Git.open(new File(cloneAddress))) {
			Repository repository = git.getRepository();
			System.out.println("repo is:" + repository);
			RevWalk walk = new RevWalk(repository);

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
					ObjectId head = repository.resolve(Constants.HEAD);
//					ObjectId penultimate = head.
					RevCommit commit1 = walk.parseCommit(head);
					RevCommit parentCommit = walk.parseCommit(commit1.getParent(0).getId());

					DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
					df.setRepository(repository);
					df.setDiffComparator(RawTextComparator.DEFAULT);
					df.setDetectRenames(true);
					List<DiffEntry> diffs = df.scan(parentCommit.getTree(), commit1.getTree());
					for (DiffEntry diff : diffs) {

						if (foundInThisBranch) {
							inputOutput.createRevisionFolderInFolderSideBySideFiles(commit);

							String append = "";
//							String fileappend = "";
							StringBuilder buf = new StringBuilder();
							while (treeWalk.next()) {
								String path = treeWalk.getPathString();
								System.out.println("File modified/changed is:" + path);
								append = append.concat(diff.getChangeType().name().substring(0, 1)).concat("\t")
										.concat(path).concat("\n");
//								String fileNameOnRepository = BlobUtils.getHeadContent(repository, diff.getOldPath());
////								String fileNameOnRepository1 = BlobUtils.getContent(repository, parentCommit, diff.getNewPath());
//								buf.append(fileNameOnRepository);
//							fileappend = fileappend.concat(fileNameOnRepository);
//								System.out.println(fileNameOnRepository);
							}
							System.out.println(buf);

							inputOutput.saveListOfFiles(commit, append);
							String debugInformation = inputOutput.GITLogEntryToStringDebug(commit);
//						System.out.println(debugInformation);
							inputOutput.saveGitComments(commit);

							gitLogEntryPathDebug = inputOutput.GITLogEntryPathToStringDebug(commit);
							debugInformation += gitLogEntryPathDebug + inputOutput.LINE_ENDING;
//						System.out.println(gitLogEntryPathDebug);

							inputOutput.saveGitDebugInformation(commit, debugInformation);
							inputOutput.appendToListOfGitCommitsDebug(commit);

							inputOutput.appendToListOfGitCommits(commit);
						}

					}
				}
			}
		}

		catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
		}

	}

}
