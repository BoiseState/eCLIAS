package org.plugin.eclias.index;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;


public class IRUtil {

	static QueryParser parser = new QueryParser("contents",
			LuceneWriteIndexFromFile.getAnalyzer());
	static HashMap<String, String> stemCache = new HashMap<>();

	public static boolean doTokensMatch(String s1, String s2)
			throws ParseException {
		if (s1.length() == 0
				|| s2.length() == 0
				|| Character.toLowerCase(s1.charAt(0)) != Character
						.toLowerCase(s2.charAt(0))) {
			return false; 
		}
		String s1Stemmed = stem(s1);
		String s2Stemmed = stem(s2);
		if (s1Stemmed.length() == 0 || s2Stemmed.length() == 0) {
			return false;
		}
		return s1Stemmed.equals(s2Stemmed);
	}

	public static String stem(String s) throws ParseException {
		if (s.length() == 0) {
			return "";
		}
		if (stemCache.containsKey(s)) {
			return stemCache.get(s);
		}
		String sStemmed = parser.parse(QueryParserUtil.escape(s.toLowerCase()))
				.toString();
		stemCache.put(s, sStemmed);
		return sStemmed;
	}

	public static List<String> splitCamelCase(String source) {
		List<String> list = new ArrayList<>();
		for (String s : source
				.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|[^a-zA-Z]+")) {
			if (s.length() > 1) {
				list.add(s);
			}
		}
		return list;
	}

	public static String splitAndMergeCamelCase(String source) {
		StringBuffer sBuffer = new StringBuffer();
		for (String s : splitCamelCase(source)) {
			sBuffer.append(s);
			sBuffer.append(" ");
		}
		return sBuffer.toString().trim();
	}

}
