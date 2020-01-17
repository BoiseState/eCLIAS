package org.plugin.eclias.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class MyCustomAnalyzer extends Analyzer {

	@Override
	public TokenStreamComponents createComponents(String fieldName) {
		StandardTokenizer src = new StandardTokenizer();
		TokenStream result = new StandardFilter(src);

		return new TokenStreamComponents(src, result);

	}

}