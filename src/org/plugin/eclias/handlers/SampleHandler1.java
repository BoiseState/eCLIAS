package org.plugin.eclias.handlers;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;

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
//		IWorkspace workspace = ResourcesPlugin.getWorkspace();
//		IWorkspaceRoot root = workspace.getRoot();
//		IProject[] projects = root.getProjects();
//		String projectsname = Arrays.toString(projects);
//		System.out.println(projectsname);
//		
//		for (IProject project : projects) {
//			project.getName();
//			System.out.println(project);
//		}
//		
		
		HashMap<String, IMethod> methodIDMap;
		IWorkspace root = ResourcesPlugin.getWorkspace();
		IProject[] allProjects = root.getRoot().getProjects();
		methodIDMap = new HashMap<String, IMethod>();
		for (int i = 0; i < allProjects.length; i++) {
			try {
				IJavaProject javaProj = (IJavaProject) allProjects[i]
						.getNature(JavaCore.NATURE_ID);
				if (javaProj != null) {
					IPackageFragment[] frags = javaProj.getPackageFragments();
					for (IPackageFragment frag : frags) {
						for (ICompilationUnit unit : frag.getCompilationUnits()) {
							for (IType type : unit.getAllTypes()) {
								for (IMethod method : type.getMethods()) {
									methodIDMap.put(
											method.getHandleIdentifier(),
											method);
								}
							}
						}
					}
				}
				System.out.println(methodIDMap);
			} catch (CoreException e) {
				 e.printStackTrace();
			}
		}
		String projectsname = Arrays.toString(allProjects);
		System.out.println(projectsname);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Eclias",
				"Starting to extract the corpus:" +projectsname);
		
		return null;
		
		    
	}
	
}
 