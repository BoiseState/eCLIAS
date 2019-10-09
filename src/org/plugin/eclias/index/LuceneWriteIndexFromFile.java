package org.plugin.eclias.index;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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

public class LuceneWriteIndexFromFile
{
	static HashMap<String, IMethod> methodMap = new HashMap<String, IMethod>();
	static HashMap<String, IMethod> methodIDMap = new HashMap<String, IMethod>();
	static IProgressMonitor monitor;
	static Analyzer analyzer;
	private static IndexReader reader;
	private static Query query;

	public static void main(String[] args) throws Exception
	{
		IWorkspace root = ResourcesPlugin.getWorkspace();
		IProject[] allProjects = root.getRoot().getProjects();

		for (IProject project : allProjects) {
			if(project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
				IPackageFragment[] frags = JavaCore.create(project).getPackageFragments();
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
				String projectsname = project.toString();
				System.out.println(projectsname);
				File indexPath1 = new File("/Users/Vasanth/git/eCLIAS/indexedFiles/Example/" + projectsname);
				indexPath1.mkdirs();
				String indexPath = "/Users/Vasanth/git/eCLIAS/indexedFiles/Example/" + projectsname;

				// org.apache.lucene.store.Directory instance
				Directory dir = FSDirectory.open(Paths.get(indexPath));

				// analyzer with the default stop words
//				Analyzer analyzer = new StandardAnalyzer();

				// IndexWriter Configuration
				IndexWriterConfig iwc = new IndexWriterConfig(getAnalyzer());
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

				// IndexWriter writes new index files to the directory
				//IndexWriter writer = new IndexWriter(dir, iwc);
				// Its recursive method to iterate all files and directories
				//indexDocs(writer, docDir);
				Job job = new Job("Lucene Indexing") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {

						IndexWriter writer;
						try {
							writer = new IndexWriter(dir, iwc);
							writer.deleteAll();
							monitor.beginTask("Building index",
									methodIDMap.size());
							int count = 0;
							for (String key : methodIDMap.keySet()) {
								indexDoc(writer, methodIDMap.get(key));
								//System.out.println("document is:" +methodIDMap.get(key));
								monitor.worked(1);
								int check = writer.numDocs();
								
								while(count<=check) {
									count ++;
										 }	
							}
							monitor.done();
							writer.close();
							System.out.println("Count is:" +count);
							// print all info about writer
							System.out.println("writer is:" +writer.toString());
							System.out.println("Monitor is:" +monitor.toString());

							
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
				// job.setRule(new CodeModelRule());
				job.setUser(true);
				job.schedule();

			}
			else {
				System.out.println("error");
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
				writer.addDocument(doc);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static CharArraySet getStopWords() {
		String[] JAVA_STOP_WORDS = { "public", "private", "protected",
				"interface", "abstract", "implements", "extends", "null",
				"new", "switch", "case", "default", "synchronized", "do", "if",
				"else", "break", "continue", "this", "assert", "for",
				"instanceof", "transient", "final", "static", "void", "catch",
				"try", "throws", "throw", "class", "finally", "return",
				"const", "native", "super", "while", "import", "package",
				"true", "false", "but", "does", "shouldn't", "aren't", "are",
				"should", "such", "they", "how" };
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
			String id = LuceneWriteIndexFromFile.getIndexReader().document(docNum)
					.get("nodeHandlerID");
			if (id.equals(iMethod.getHandleIdentifier())) {
				return docNum;
			}
		}
		return null;
	}
	
	private static final String INDEX_DIR = "/Users/Vasanth/git/eCLIAS/indexedFiles/Example/P/jEdit4.3pre9";
	
	 
	    public static String search(String queryString) throws Exception
	    {
	        //Create lucene searcher. It search over a single IndexReader.
	        IndexSearcher searcher = createSearcher();
	         
	        //Search indexed contents using search term
	        TopDocs foundDocs = searchInContent(queryString, searcher);
	         
	        //Total found documents
	        System.out.println("Total Results :: " + foundDocs.totalHits);
	        String answer = "Total Results :: " + foundDocs.totalHits;
	        List<String> score = new ArrayList<String>();
	        //Let's print out the path of files which have searched term
	        for (ScoreDoc sd : foundDocs.scoreDocs)
	        {
	            Document d = searcher.doc(sd.doc);
	            System.out.println("Path : "+ d.get("path") + "\n Score : " + sd.score + "\nmethod: " + d.get("filename"));
	            String finalscore = "Path : "+ d.get("path") + "\n Score : " + sd.score + "\ntitle: " + d.get("title"); 
	            score.add(finalscore);
	        }
	        String finalAnswer = answer + "\n" + score + "\n"; 
	        
			return finalAnswer;
	    }
	     
	    private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception
	    {
	        //Create search query
	        QueryParser qp = new QueryParser("contents", new StandardAnalyzer());
	        Query query = qp.parse(textToFind);
	         
	        //search the index
	        TopDocs hits = searcher.search(query, 50);
	        return hits;
	    }
	 
	    private static IndexSearcher createSearcher() throws IOException
	    {
	    	
	        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
	         
	        //It is an interface for accessing a point-in-time view of a lucene index
	        IndexReader reader = DirectoryReader.open(dir);
	         
	        //Index searcher
	        IndexSearcher searcher = new IndexSearcher(reader);
	        return searcher;
	    }
	    
	    public static Query getQuery() {
			return query;
		}

}