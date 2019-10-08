package org.plugin.eclias.index;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
 
public class LuceneIndex
{
    public static void main(String[] args) throws FileNotFoundException
    {
        //Input folder
        String docsPath = "/Users/Vasanth/git/eCLIAS/preprocessedFiles/P/jEdit4.3pre9/2019-08-19_12-12-26 Corpus-jEdit4.3pre9-AfterSplit.corpusRawMethodLevelGranularity";
         
        //Output folder
        String indexPath = "/Users/Vasanth/git/eCLIAS/indexedFiles/Test/Example";
 
        //Input Path Variable
        final Path docDir = Paths.get(docsPath);
        
        Scanner s = new Scanner(new File(docsPath));
        ArrayList<String> list = new ArrayList<String>();
        while (s.hasNextLine()){
            list.add(s.nextLine());
        }
        s.close();
        String methodList = list.toString();
        try
        {
            //org.apache.lucene.store.Directory instance
            Directory dir = FSDirectory.open( Paths.get(indexPath) );
             
            //analyzer with the default stop words
            Analyzer analyzer = new StandardAnalyzer();
             
            //IndexWriter Configuration
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
             
            //IndexWriter writes new index files to the directory
            IndexWriter writer = new IndexWriter(dir, iwc);
             
            //Its recursive method to iterate all files and directories
            indexDocs(writer, methodList);
 
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
     
    static void indexDocs(final IndexWriter writer, String methodList) throws IOException
    {
        
    	String indexPath = "/Users/Vasanth/git/eCLIAS/indexedFiles/Test/Example";
    	Path docDir = Paths.get(indexPath);
        if (Files.isDirectory(docDir))
        {
            //Iterate directory
            
                        BasicFileAttributes attrs = null; 
						//Index this file
                        indexDoc(writer, methodList, attrs.lastModifiedTime().toMillis());
                   
                  
                }
            
        else
        {
            //Index this file
            indexDoc(writer, methodList, Files.getLastModifiedTime(docDir).toMillis());
        }
    }
 
    static void indexDoc(IndexWriter writer, String methodList, long lastModified) throws IOException
    {
     
            //Create lucene Document
            Document doc = new Document();
            doc.add(new LongPoint("modified", lastModified));
            doc.add(new TextField("contents", methodList, Store.YES));
             
            //Updates a document by first deleting the document(s)
            //containing <code>term</code> and then adding the new
            //document.  The delete and then add are atomic as seen
            //by a reader on the same index
            writer.updateDocument(new Term("path", methodList), doc);
    }
}