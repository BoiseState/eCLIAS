package org.plugin.eclias.downloadGitCommits;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
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

					if (foundInThisBranch) {
						inputOutput.appendToListOfGitCommits(commit);
						inputOutput.saveGitComments(commit);
					}

				}
			}
		}

		catch (Exception e) {
			System.out.println("error");
		}

	}

}
