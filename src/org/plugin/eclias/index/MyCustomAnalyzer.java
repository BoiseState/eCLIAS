package org.plugin.eclias.index;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.plugin.eclias.views.EcliasView;

public class MyCustomAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		StandardTokenizer src = new StandardTokenizer();
		TokenStream result = new StandardFilter(src);
		if (EcliasView.useStopWords == true) {
			result = new StopFilter(result, getStopWords());
//        	result = new StopFilter(result,  StandardAnalyzer.STOP_WORDS_SET);	
		}
		if (EcliasView.usePorterStemmer == true) {
			result = new PorterStemFilter(result);

		}
		if (EcliasView.useLowerCase == true) {
			result = new LowerCaseFilter(result);
		}
		
		System.out.println("result is:" +result);
		
		return new TokenStreamComponents(src, result);
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
}
