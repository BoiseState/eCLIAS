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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Method;
import org.plugin.eclias.corpus.InputOutput;
import org.plugin.eclias.corpus.InputOutputCorpusMethodLevelGranularity;
import org.plugin.eclias.corpus.ParserCorpusMethodLevelGranularity;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

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
//                                                     ASTVisitor visitor = new ASTVisitor() {
//													};
//                                                     parse.accept(visitor);
//                                                     System.out.println("*******************");
//                                                     System.out.println(visitor);
//                                                     System.out.println("*******************");
//             
//                                                     MethodDeclaration method = visitor.getMethods();
//                                                             System.out.print("Method name: "
//                                                                             + method.getName()
//                                                                             + " Return type: "
//                                                                             + method.getReturnType2());
//                                                    }  
//                                                     System.out.println("*******************");
                                                     System.out.println(parse);   
                                             }
                                             
                                     }

                             }
                     }
                     
             } catch (CoreException e) {
                     e.printStackTrace();
             }
             
     }
 
       
 		String projectsname = Arrays.toString(projects);
 		System.out.println(projectsname);
		
		
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"Eclias",
				"Starting to extract the corpus:");
		
		return null;
		
		    
	}
	 private static CompilationUnit parse(ICompilationUnit unit) {
         ASTParser parser = ASTParser.newParser(AST.JLS3);
         parser.setKind(ASTParser.K_COMPILATION_UNIT);
         parser.setSource(unit);
         parser.setResolveBindings(true);
         return (CompilationUnit) parser.createAST(null); // parse
 }

	 
	
}
	
	
 