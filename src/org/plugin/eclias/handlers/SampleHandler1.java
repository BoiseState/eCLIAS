package org.plugin.eclias.handlers;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
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
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */

	public Object execute(ExecutionEvent event) throws ExecutionException {		
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		
		try {
			parseMainProject();
			System.out.println("Check:1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String projectsname = Arrays.toString(projects);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Eclias",
				"Starting to extract the corpus:" +projectsname);

		return null;


	}
	private static void parseMainProject() throws Exception{ 
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		// Get all projects in the workspace

		IProject[] projects = root.getProjects();
		System.out.println(projects);
		Date date = Calendar.getInstance().getTime();  
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd_hh-mm-ss");  
		String strDate = dateFormat.format(date); 
		for (IProject project1 : projects) {
			String projectsname = project1.toString();
			System.out.println(projectsname);
		File newDirectory = new File("/Users/Vasanth/git/eCLIAS/inputFiles/"+ projectsname);
		if(newDirectory.mkdirs()){
		File file = new File("/Users/Vasanth/git/eCLIAS/inputFiles/"+ strDate +".txt");
		FileWriter fileWriter = null;

		try {
			fileWriter = new FileWriter(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Loop over all projects

		for (IProject project : projects) {
			try {
				if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {

					IPackageFragment[] packages = JavaCore.create(project)
							.getPackageFragments();
					// parse(JavaCore.create(project));
					for (IPackageFragment mypackage : packages) {
						if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
							for (ICompilationUnit unit : mypackage
									.getCompilationUnits()) {
								// Now create the AST for the ICompilationUnits
								CompilationUnit parse = parse(unit);
								String res = parse.toString();
								
								fileWriter.write(res);
							} 
						}
					}
				}


			} catch (CoreException e) {
				e.printStackTrace();
			} 

			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

		}
		try {
			fileWriter.flush();
			
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileUtils.copyFileToDirectory(file, newDirectory);
		}
		}
	}
	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}


}



