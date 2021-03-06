package org.plugin.eclias.handlers;

import java.util.Arrays;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.plugin.eclias.corpus.MainCorpusGenerator;
import org.plugin.eclias.index.LuceneWriteIndexFromFile;
import org.plugin.eclias.preprocessor.MainCorpusPreprocessor;
import org.eclipse.jface.dialogs.MessageDialog;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler1 extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler1() {
	}

	/**
	 * the command has been executed, so extract extract the needed information from
	 * the application context.
	 */

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		try {
			MainCorpusGenerator mcg = new MainCorpusGenerator();
			mcg.main(null);
			System.out.println("Corpus Generated");
			MainCorpusPreprocessor mcp = new MainCorpusPreprocessor();
			mcp.main(null);
			System.out.println("Preprocessing Generated");
//			LuceneWriteIndexFromFile Li = new LuceneWriteIndexFromFile();
//			Li.index();
//			Li.search("Hello world");
//			System.out.println("Working");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String projectsname = Arrays.toString(projects);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "Eclias", "Corpus Extracted and preprocessed for the following projects:" + projectsname);
		return null;

	}

}
