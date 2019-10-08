package org.plugin.eclias.index;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.plugin.eclias.corpus.InputOutput;
import org.plugin.eclias.corpus.InputOutputCorpusMethodLevelGranularity;
import org.plugin.eclias.corpus.MainCorpusGenerator;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler2 extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler2() {
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
//			parseMainProject();
			System.out.println("Check:1");
			MainCorpusGenerator mcg = new MainCorpusGenerator();
			mcg.main(null);
			System.out.println("Check:2");
//			LuceneWriteIndexFromFile();
//			System.out.println("Check:3");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String projectsname = Arrays.toString(projects);
		
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "Eclias", "Starting to extract the corpus:" + projectsname);

		return null;

	}


	
	private static void parseMainProject() throws Exception {
		IWorkspace workspace1 = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root1 = workspace1.getRoot();
		// Get all projects in the workspace
		IProject[] projects1 = root1.getProjects();
		// System.out.println(projects1);
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
		String strDate = dateFormat.format(date);

		for (IProject project1 : projects1) {
			if (project1.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
			String projectsname = project1.toString();
			System.out.println(projectsname);
			System.out.println("Project details1");
			
			File newDirectory = new File("/Users/Vasanth/git/eCLIAS/inputFiles/" + projectsname);
			newDirectory.mkdirs();
			if (newDirectory != null) {
				File file = new File("check" +strDate + ".txt");
				FileWriter fileWriter = null;
				String res;
				try {
					fileWriter = new FileWriter(file);
					// Loop over all projects

					
						IPackageFragment[] packages = JavaCore.create(project1).getPackageFragments();
	
						
						// parse(JavaCore.create(project));
						for (IPackageFragment mypackage : packages) {
							if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
								for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
									// Now create the AST for the ICompilationUnits
									IResource underlyingResource = unit.getUnderlyingResource();
									if (underlyingResource.getType() == IResource.FILE) {

									    IFile ifile = (IFile) underlyingResource;

									    String path = ifile.getRawLocation().toString();
									    //System.out.println(path+"\n");

									
									CompilationUnit parse = parse(unit);
//									unit.getPackageDeclarations();
									res = parse.toString();
									fileWriter.write(path+"\n");
									}
								}
							}
						}
						
			

				} catch (CoreException | IOException e) {
					e.printStackTrace();
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
			File outputdirectory = new File("/Users/Vasanth/git/eCLIAS/inputFiles/" + projectsname + "/Output/MethodLevelGranuality/");
			outputdirectory.mkdirs();
			if (outputdirectory != null) {
				File file = new File("check" +strDate + ".txt");
				FileWriter fileWriter = null;
				String res;
				try {
					fileWriter = new FileWriter(file);
					// Loop over all projects

					
						IPackageFragment[] packages = JavaCore.create(project1).getPackageFragments();
	
						
						// parse(JavaCore.create(project));
						for (IPackageFragment mypackage : packages) {
							if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
								for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
									// Now create the AST for the ICompilationUnits
									IResource underlyingResource = unit.getUnderlyingResource();
									if (underlyingResource.getType() == IResource.FILE) {

									    IFile ifile = (IFile) underlyingResource;

									    String path = ifile.getRawLocation().toString();
									    //System.out.println(path+"\n");

									
									CompilationUnit parse = parse(unit);
//									unit.getPackageDeclarations();
									res = parse.toString();
									fileWriter.write(res);
									}
								}
							}
						}
						
			

				} catch (CoreException | IOException e) {
					e.printStackTrace();
				}

				try {
					fileWriter.flush();
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				FileUtils.copyFileToDirectory(file, outputdirectory);
				
			}
			
			
			}
			
			else {
			System.out.println("error");
			}
			
		}
		
	}

	private static CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
	
	
//	private static void LuceneWriteIndexFromFile() {
//		// Input folder
	
//	IWorkspace workspace = ResourcesPlugin.getWorkspace();
//	IWorkspaceRoot root = workspace.getRoot();
//	// Get all projects in the workspace
//	IProject[] projects = root.getProjects();
//
//	for (IProject project : projects) {
//		String projectsname = project.toString();
//		System.out.println(projectsname);
//
//		String docsPath = "/Users/Vasanth/git/eCLIAS/inputFiles/" + projectsname;
//
//		// Output folder
//		String indexPath = "/Users/Vasanth/git/eCLIAS/indexedFiles/" + projectsname;
//
//		// Input Path Variable
//		final Path docDir = Paths.get(docsPath);
//
//		try {
//			// org.apache.lucene.store.Directory instance
//			Directory dir = FSDirectory.open(Paths.get(indexPath));
//
//			// analyzer with the default stop words
//			Analyzer analyzer = new StandardAnalyzer();
//
//			// IndexWriter Configuration
//			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
//			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
//
//			// IndexWriter writes new index files to the directory
//			IndexWriter writer = new IndexWriter(dir, iwc);
//
//			// Its recursive method to iterate all files and directories
//			indexDocs(writer, docDir);
//
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//}
//
//	static void indexDocs(final IndexWriter writer, Path path) throws IOException {
//		// Directory?
//		if (Files.isDirectory(path)) {
//			// Iterate directory
//			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
//				@Override
//				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//					try {
//						// Index this file
//						indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
//					} catch (IOException ioe) {
//						ioe.printStackTrace();
//					}
//					return FileVisitResult.CONTINUE;
//				}
//			});
//		} else {
//			// Index this file
//			indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
//		}
//	}
//
//	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
//		try (InputStream stream = Files.newInputStream(file)) {
//			// Create lucene Document
//			Document doc = new Document();
//
//			doc.add(new StringField("path", file.toString(), Field.Store.YES));
//			doc.add(new LongPoint("modified", lastModified));
//			doc.add(new TextField("contents", new String(Files.readAllBytes(file)), Store.YES));
//
//			// Updates a document by first deleting the document(s)
//			// containing <code>term</code> and then adding the new
//			// document. The delete and then add are atomic as seen
//			// by a reader on the same index
//			writer.updateDocument(new Term("path", file.toString()), doc);
//		}
//	}

}
