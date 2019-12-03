package org.plugin.eclias.index;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;

public class LuceneWriteIndexFromFile {
	static HashMap<IMethod, Method> methods = new HashMap<IMethod, Method>();
	static HashMap<String, IMethod> methodMap = new HashMap<String, IMethod>();
	static HashMap<String, IMethod> methodIDMap = new HashMap<String, IMethod>();
	static IProgressMonitor monitor;
	static Analyzer analyzer;
	private static IndexReader reader;
	private static Query query;
	private static String projectsname;
	private static String indexPath = "";
	private static String newmethodname ;

	public static class Score {

		float score;
		IMethod method;
		String packageName;
		String methodName;
		static IMethod ideEditor;
		String className;

		Score(float score, IMethod method, String packageName, String methodName, String className, IMethod ideEditor) {
			this.score = score;
			this.method = method;
			this.packageName = packageName;
			this.methodName = methodName;
			this.className = className;
			this.ideEditor = ideEditor;
		}

		public float getScore() {
			return score;
		}

		public IMethod getMethod() {
			return method;
		}

		public String getPackageName() {
			return packageName;
		}

		public String getMethodName() {
			return methodName;
		}

		public String getClassName() {
			return className;
		}

		public static IMethod getEditor() {
			return ideEditor;
		}
	}

	public static void index() throws Exception {
		IWorkspace root = ResourcesPlugin.getWorkspace();
		IProject[] allProjects = root.getRoot().getProjects();

		for (IProject project : allProjects) {
			if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				IPackageFragment[] frags = JavaCore.create(project).getPackageFragments();
				for (IPackageFragment frag : frags) {
					for (ICompilationUnit unit : frag.getCompilationUnits()) {
						for (IType type : unit.getAllTypes()) {
							for (IMethod method : type.getMethods()) {
								methodIDMap.put(method.getHandleIdentifier(), //
										method);
							}
						}
					}
				}

				// List the projects in the explorer tab
				projectsname = project.toString();
				System.out.println(projectsname);

				// Creating a new directory to store indexed files
				File newDirectory = new File("/Users/Vasanth/git/eCLIAS/indexedFiles/Example/" + projectsname);
				newDirectory.mkdirs();

				// Path of indexed files
				indexPath += "/Users/Vasanth/git/eCLIAS/indexedFiles/Example/" + projectsname;

				// analyzer with the default stop words
//				Analyzer analyzer = new StandardAnalyzer();

				// IndexWriter writes new index files to the directory
				// IndexWriter writer = new IndexWriter(dir, iwc);
				// Its recursive method to iterate all files and directories
				// indexDocs(writer, docDir);

				Job job = new Job("Lucene Indexing") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {

						IndexWriter writer;
						try {

							// org.apache.lucene.store.Directory instance
							Directory dir = FSDirectory.open(Paths.get(indexPath));

							// IndexWriter Configuration
							IndexWriterConfig iwc = new IndexWriterConfig(getAnalyzer());
							iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

							// IndexWriter writes new index files to the directory
							writer = new IndexWriter(dir, iwc);

							writer.deleteAll();

							monitor.beginTask("Building index", methodIDMap.size());

							for (String key : methodIDMap.keySet()) {
								indexDoc(writer, methodIDMap.get(key));
								monitor.worked(1);								
							}
							
							monitor.done();
							writer.close();

//							Terms terms = null;
//							int doccount = terms.getDocCount();
//							System.out.println(doccount);

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return Status.OK_STATUS;

					}
				};
				job.setPriority(Job.LONG);
				job.setUser(true);
				job.schedule();

			} else {
				System.out.println("error - It's not a project");
			}
		}
	}

	static void indexDoc(IndexWriter writer, IMethod method) {
		Document doc = new Document();
		String source;
		long lastModified = 0;
		try {
			method.getOpenable().open(new NullProgressMonitor());
			source = method.getSource();
			if (source != null) {
				source = IRUtil.splitAndMergeCamelCase(source);

				doc.add(new StringField("path", source, Field.Store.YES));
				doc.add(new LongPoint("modified", lastModified));
				doc.add(new TextField("contents", source, Store.YES));
				doc.add(new TextField("nodeHandlerID", method.getHandleIdentifier(), Field.Store.YES));
				writer.addDocument(doc);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static CharArraySet getStopWords() {
		String[] JAVA_STOP_WORDS = { "public", "private", "protected", "interface", "abstract", "implements", "extends",
				"null", "new", "switch", "case", "default", "synchronized", "do", "if", "else", "break", "continue",
				"this", "assert", "for", "instanceof", "transient", "final", "static", "void", "catch", "try", "throws",
				"throw", "class", "finally", "return", "const", "native", "super", "while", "import", "package", "true",
				"false", "but", "does", "shouldn't", "aren't", "are", "should", "such", "they", "how" };
		HashSet<String> javaStopWords = new HashSet<String>();
		javaStopWords.addAll(Arrays.asList(JAVA_STOP_WORDS));
		CharArraySet allStopWords = new CharArraySet(javaStopWords, false);
		allStopWords.addAll(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		return allStopWords;
	}

	static Analyzer getAnalyzer() {
		if (analyzer == null) {
			analyzer = new EnglishAnalyzer(getStopWords());
		}
		return analyzer;
	}

	static IndexReader getIndexReader() {
		return reader;
	}

	static Integer getDocNum(IMethod iMethod) throws IOException {
		for (int docNum = 0; docNum < LuceneWriteIndexFromFile.getIndexReader().maxDoc(); docNum++) {
			String id = LuceneWriteIndexFromFile.getIndexReader().document(docNum).get("nodeHandlerID");
			if (id.equals(iMethod.getHandleIdentifier())) {
				return docNum;
			}
		}
		return null;
	}

	public static ArrayList<Score> search(String queryString) throws Exception {

		ArrayList<Score> searchResults = new ArrayList<>();

		// Create lucene searcher. It search over a single IndexReader.
		methodMap = new HashMap<String, IMethod>();

		Directory dir = FSDirectory.open(Paths.get(indexPath));

		// It is an interface for accessing a point-in-time view of a lucene index
		IndexReader reader = DirectoryReader.open(dir);

		for (int i = 0; i < reader.numDocs(); i++) {
			String methodID = reader.document(i).get("nodeHandlerID");
			if (methodIDMap.containsKey(methodID)) {
				IMethod method = methodIDMap.get(methodID);
				methodMap.put(methodID, method);
			}

		}

		// Index searcher
		IndexSearcher searcher = new IndexSearcher(reader);

		// Search indexed contents using search term
		TopDocs foundDocs = searchInContent(queryString, searcher);

		// Total found documents
		System.out.println("Total Results :: " + foundDocs.totalHits);

		// Let's print out the path of files which have searched term

		for (ScoreDoc sd : foundDocs.scoreDocs) {
			Document doc = searcher.doc(sd.doc);
			
			Float similarity = normalize(sd.score);
			DecimalFormat df = new DecimalFormat("0.00");
			
			System.out.println("score in two decimals : " + df.format(similarity));  

			IMethod member = methodMap.get(doc.get("nodeHandlerID"));

			String member1 = member.getKey();
			String[] parts = member1.split(";");
			String part1 = parts[0];
			String packageName = part1.substring(1).replace('/','.');
			

			String[] classnamearray = packageName.split("/");
			String className = classnamearray[classnamearray.length - 1];

			String nameMethod = member.toString();
			String[] methodSplit = nameMethod.split("\\[");
			String methodSplitName = methodSplit[0];

			String methodName = methodSplitName.substring(0);
		
			
			if(methodName.contains(")")) {
				newmethodname = methodName;
			}
			else {
				newmethodname = methodName.concat(")");
			}
			

			Score s = new Score(similarity, member, packageName, newmethodname, className, member);

			searchResults.add(s);

		}

		return searchResults;
	}

	private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception {
		
		// Create search query
		QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
		textToFind = IRUtil.splitAndMergeCamelCase(textToFind);
		Query query = qp.parse(QueryParserUtil.escape(textToFind));

		// search the index
		TopDocs hits = searcher.search(query, Integer.MAX_VALUE);
		return hits;
	}

	public static Query getQuery() {
		return query;
	}

	private static Float normalize(float score) {
		return (float) (1.0 - Math.pow(1.0 / (1.0 + 0.2 * score), 5));
	}

}