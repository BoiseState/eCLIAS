package org.plugin.eclias.corpus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;


public class MainCorpusGenerator
{
	private static void testMainMethodLevelGranularity() throws Exception
	{

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
			File newDirectory = new File(System.getProperty("user.dir")+"/inputFiles/" + projectsname);
			newDirectory.mkdirs();
			if (newDirectory != null) {
				File file = new File("test" +strDate + ".txt");
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
			File outputdirectory = new File("./inputFiles/" + projectsname + "/Output/MethodLevelGranuality/");
			outputdirectory.mkdirs();
			File outputdirectory1 = new File("./inputFiles/" + projectsname + "/Output/ClassLevelGranuality/");
			outputdirectory1.mkdirs();
			File outputdirectory2 = new File("./inputFiles/" + projectsname + "/Output/FileLevelGranuality/");
			outputdirectory2.mkdirs();
			if (outputdirectory != null && outputdirectory1 != null && outputdirectory2 != null) {
			String project = projectsname.split("/")[1];
			String input = newDirectory.toString() + "/test" +strDate + ".txt";
			String output = "./inputFiles/" + projectsname + "/Output/MethodLevelGranuality/";
			String output1 = "./inputFiles/" + projectsname + "/Output/ClassLevelGranuality/";
			String output2 = "./inputFiles/" + projectsname + "/Output/FileLevelGranuality/";
			InputOutputCorpusMethodLevelGranularity inputOutput=new InputOutputCorpusMethodLevelGranularity(input,output,strDate+" "+"Corpus-"+project);
			parseAndSaveMultipleFiles(inputOutput);
			InputOutputCorpusClassLevelGranularity inputOutput1=new InputOutputCorpusClassLevelGranularity(input,output1,strDate+" "+"Corpus-"+project);
			parseAndSaveMultipleFilesClassLevelGranularity(inputOutput1);
			InputOutputCorpusFileLevelGranularity inputOutput2=new InputOutputCorpusFileLevelGranularity(input,output2,strDate+" "+"Corpus-"+project);
			parseAndSaveMultipleFilesFileLevelGranularity(inputOutput2);
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
	
	
	public static void main(String[] args) throws Exception
	{
		testMainMethodLevelGranularity();
		if (1==1)
			return;
	
		
		if (args.length!=4)
		{
			System.err.println("Generates a corpus from the source code at different levels of granularity.");
			System.err.println("Usage:");
			System.err.println("  java -jar CorpusGenerator.jar -methodLevelGranularity inputFileNameWithListOfInputFileNames outputFolder outputFileNameWithoutExtension");
			System.err.println("  java -jar CorpusGenerator.jar -classLevelGranularity inputFileNameWithListOfInputFileNames outputFolder outputFileNameWithoutExtension");
			System.err.println("  java -jar CorpusGenerator.jar -fileLevelGranularity inputFileNameWithListOfInputFileNames outputFolder outputFileNameWithoutExtension");
			System.err.println();
			System.err.println("Where:");
			System.err.println("  inputFileNameWithListOfInputFileNames");
			System.err.println("    is a file name containing n lines. Each line is a full path of a java file to be analyzed.");
			System.err.println("  outputFolder");
			System.err.println("    is the folder name where the corpus will be saved");
			System.err.println("  outputFileNameWithoutExtension");
			System.err.println("    the prefix of the output files (e.g., the name of the software system)");
			System.err.println();
			System.err.println("The output produced by this tool using the -methodLevelGranularity option will contain 4 files:");
			System.err.println("  outputFolder/outputFileNameWithoutExtension"+InputOutputCorpusMethodLevelGranularity.EXTENSION_CORPUS_RAW);
			System.err.println("    contains the corpus where each method extracted from the java files is on its own line");
			System.err.println("  outputFolder/outputFileNameWithoutExtension"+InputOutputCorpusMethodLevelGranularity.EXTENSION_CORPUS_MAPPING);
			System.err.println("    contains the id of the method from the corpus on its own line (e.g., packageName.className.methodName)");
			System.err.println("  outputFolder/outputFileNameWithoutExtension"+InputOutputCorpusMethodLevelGranularity.EXTENSION_CORPUS_MAPPING_WITH_PACKAGE_SEPARATOR);
			System.err.println("    contains the id of the method from the corpus on its own line, with a separator character ('$') between package and class name (e.g., packageName$className.methodName)");
			System.err.println("  outputFolder/outputFileNameWithoutExtension"+InputOutputCorpusMethodLevelGranularity.EXTENSION_CORPUS_DEBUG);
			System.err.println("    contains some verbose information about the corpus extraction (for verification purposes only)");
			System.err.println();
			System.err.println("The output produced by this tool using the -classLevelGranularity option will contain 3 files:");
			System.err.println("  outputFolder/outputFileNameWithoutExtension"+InputOutputCorpusClassLevelGranularity.EXTENSION_CORPUS_RAW);
			System.err.println("    contains the corpus where each class extracted from the java files is on its own line (i.e., multiple classes in the same file are considered as separate documents)");
			System.err.println("  outputFolder/outputFileNameWithoutExtension"+InputOutputCorpusClassLevelGranularity.EXTENSION_CORPUS_MAPPING);
			System.err.println("    contains the id of the class from the corpus on its own line");
			System.err.println("  outputFolder/outputFileNameWithoutExtension"+InputOutputCorpusClassLevelGranularity.EXTENSION_CORPUS_DEBUG);
			System.err.println("    contains some verbose information about the corpus extraction (for verification purposes only)");
			System.err.println();
			System.err.println("The output produced by this tool using the -fileLevelGranularity option will contain 3 files:");
			System.err.println("  outputFolder/outputFileNameWithoutExtension"+InputOutputCorpusFileLevelGranularity.EXTENSION_CORPUS_RAW);
			System.err.println("    contains the corpus where all the classes extracted from the java files is on its own line (i.e., multiple classes in the same file are considered as one document)");
			System.err.println("  outputFolder/outputFileNameWithoutExtension"+InputOutputCorpusFileLevelGranularity.EXTENSION_CORPUS_MAPPING);
			System.err.println("    contains the id of the file from the corpus on its own line");
			System.err.println("  outputFolder/outputFileNameWithoutExtension"+InputOutputCorpusFileLevelGranularity.EXTENSION_CORPUS_DEBUG);
			System.err.println("    contains some verbose information about the corpus extraction (for verification purposes only)");
			System.err.println();
			System.err.println("Example:");
			System.err.println("  java -jar CorpusGenerator.jar -methodLevelGranularity TestCases/Input/inputFileNamesjEdit4.3.txt TestCases/Output/MethodLevelGranularity/ Corpus-jEdit4.3");
			System.err.println("  java -jar CorpusGenerator.jar -classLevelGranularity TestCases/Input/inputFileNamesjEdit4.3.txt TestCases/Output/ClassLevelGranularity/ Corpus-jEdit4.3");
			System.err.println("  java -jar CorpusGenerator.jar -fileLevelGranularity TestCases/Input/inputFileNamesjEdit4.3.txt TestCases/Output/FileLevelGranularity/ Corpus-jEdit4.3");
			System.exit(1);
		}
		
		if (args[0].equals("-methodLevelGranularity"))
		{
			InputOutputCorpusMethodLevelGranularity inputOutput=new InputOutputCorpusMethodLevelGranularity(args[1],args[2],args[3]);
			parseAndSaveMultipleFiles(inputOutput);
			System.exit(0);
		}

		if (args[0].equals("-classLevelGranularity"))
		{
			InputOutputCorpusClassLevelGranularity inputOutputClassLevelGranularity=new InputOutputCorpusClassLevelGranularity(args[1],args[2],args[3]);
			parseAndSaveMultipleFilesClassLevelGranularity(inputOutputClassLevelGranularity);
			System.exit(0);
		}
		
		if (args[0].equals("-fileLevelGranularity"))
		{
			InputOutputCorpusFileLevelGranularity inputOutputFileLevelGranularity=new InputOutputCorpusFileLevelGranularity(args[1],args[2],args[3]);
			parseAndSaveMultipleFilesFileLevelGranularity(inputOutputFileLevelGranularity);
			System.exit(0);
		}

		//-interfaces: list all the interfaces
		//-classlevelgranularity: list all class level granularities
		//maybe use the INputOutputClassLevel, interface, clss level granularity, method level granularity
	}

	private static void parseAndSaveMultipleFiles(InputOutputCorpusMethodLevelGranularity inputOutput) throws Exception
	{
		//delete output files if they already exist
		inputOutput.initializeOutputStream();
		
		String[] inputFileNames=InputOutput.readFile(inputOutput.getInputFileNameWithListOfInputFileNames()).split(InputOutput.LINE_ENDING);

		for (String inputFileName : inputFileNames)
		{
			inputOutput.appendToCorpusDebug("Preprocessing file:\t"+inputFileName);
			parseAndSaveOneFile(inputOutput,inputFileName);
		}
		
		inputOutput.closeOutputStreams();
	}

	private static void parseAndSaveOneFile(InputOutputCorpusMethodLevelGranularity inputOutput,String inputFileName)
	{
		String fileContent="";
		fileContent=InputOutput.readFile(inputFileName);

		ParserCorpusMethodLevelGranularity parser=new ParserCorpusMethodLevelGranularity(inputOutput,fileContent);
		CompilationUnit compilationUnitSourceCode=parser.parseSourceCode();
		parser.exploreSourceCode(compilationUnitSourceCode);
	}
	
	private static void parseAndSaveMultipleFilesClassLevelGranularity(InputOutputCorpusClassLevelGranularity inputOutput) throws Exception
	{
		//delete output files if they already exist
		inputOutput.initializeOutputStream();
		
		String[] inputFileNames=InputOutput.readFile(inputOutput.getInputFileNameWithListOfInputFileNames()).split(InputOutput.LINE_ENDING);

		for (String inputFileName : inputFileNames)
		{
			inputOutput.appendToCorpusDebug("Preprocessing file:\t"+inputFileName);
			parseAndSaveOneFileClassLevelGranularity(inputOutput,inputFileName);
		}
		
		inputOutput.closeOutputStreams();
	}

	private static void parseAndSaveOneFileClassLevelGranularity(InputOutputCorpusClassLevelGranularity inputOutput,String inputFileName)
	{
		String fileContent="";
		fileContent=InputOutput.readFile(inputFileName);

		ParserCorpusClassLevelGranularity parser=new ParserCorpusClassLevelGranularity(inputOutput,fileContent);
		CompilationUnit compilationUnitSourceCode=parser.parseSourceCode();
		parser.exploreSourceCodeClassLevelGranularity(compilationUnitSourceCode);
	}
	
	private static void parseAndSaveMultipleFilesFileLevelGranularity(InputOutputCorpusFileLevelGranularity inputOutput) throws Exception
	{
		//delete output files if they already exist
		inputOutput.initializeOutputStream();
		
		String[] inputFileNames=InputOutput.readFile(inputOutput.getInputFileNameWithListOfInputFileNames()).split(InputOutput.LINE_ENDING);

		for (String inputFileName : inputFileNames)
		{
			inputOutput.appendToCorpusDebug("Preprocessing file:\t"+inputFileName);
			parseAndSaveOneFileFileLevelGranularity(inputOutput,inputFileName);
		}
		
		inputOutput.closeOutputStreams();
	}

	private static void parseAndSaveOneFileFileLevelGranularity(InputOutputCorpusFileLevelGranularity inputOutput,String inputFileName)
	{
		String fileContent="";
		fileContent=InputOutput.readFile(inputFileName);

		ParserCorpusFileLevelGranularity parser=new ParserCorpusFileLevelGranularity(inputOutput,fileContent,inputFileName);
		CompilationUnit compilationUnitSourceCode=parser.parseSourceCode();
		parser.exploreSourceCodeFileLevelGranularity(compilationUnitSourceCode);
	}
	
}