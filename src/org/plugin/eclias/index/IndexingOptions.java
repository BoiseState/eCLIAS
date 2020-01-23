package org.plugin.eclias.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class IndexingOptions {

	public static String FILE_NAME_LIST_OF_STOP_WORDS = System.getProperty("user.dir") + "/stopWords/StopWords.txt";

//	static QueryParser parser = new QueryParser("contents", LuceneWriteIndexFromFile.getAnalyzer());
	
	static HashMap<String, String> stemCache = new HashMap<>();

//	public static boolean doTokensMatch(String s1, String s2) throws ParseException {
//		if (s1.length() == 0 || s2.length() == 0
//				|| Character.toLowerCase(s1.charAt(0)) != Character.toLowerCase(s2.charAt(0))) {
//			return false;
//		}
//		String s1Stemmed = stem(s1);
//		String s2Stemmed = stem(s2);
//		if (s1Stemmed.length() == 0 || s2Stemmed.length() == 0) {
//			return false;
//		}
//		return s1Stemmed.equals(s2Stemmed);
//	}

//	public static String stem(String s) throws ParseException {
//		if (s.length() == 0) {
//			return "";
//		}
//		if (stemCache.containsKey(s)) {
//			return stemCache.get(s);
//		}
//		String sStemmed = parser.parse(QueryParserUtil.escape(s.toLowerCase())).toString();
//		stemCache.put(s, sStemmed);
//		return sStemmed;
//	}

	public static List<String> splitCamelCase(String source) {
		List<String> list = new ArrayList<>();
		for (String s : source.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|[^a-zA-Z]+")) {
			if (s.length() > 1) {
				list.add(s);
			}
		}
		return list;
	}

	public static Hashtable<String, Integer> loadListOfStopWords() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(FILE_NAME_LIST_OF_STOP_WORDS));
		String buf;
		Hashtable<String, Integer> listOfStopWords = new Hashtable<String, Integer>();
		while ((buf = br.readLine()) != null) {
			listOfStopWords.put(buf, new Integer(1));
		}
		br.close();

		return listOfStopWords;
	}

	public static String splitAndMergeCamelCase(String source) {
		StringBuffer sBuffer = new StringBuffer();
		for (String s : splitCamelCase(source)) {
			sBuffer.append(s);
			sBuffer.append(" ");
		}
		return sBuffer.toString().trim();
	}

	public static String splitIdentifiers(String originalBuffer, boolean keepCompoundIdentifier) {
		String words[] = originalBuffer.split(" ");

		StringBuilder newBuffer = new StringBuilder();
		boolean isCompoundIdentifier;

		for (String word : words) {
			String originalWord = word;
			if (word.length() == 0)
				continue;

			isCompoundIdentifier = false;
			if (word.indexOf('_') >= 0) {
				isCompoundIdentifier = true;
				word = word.replaceAll("_", " ");
			}

			StringBuilder newWord = new StringBuilder(word);

			for (int i = newWord.length() - 1; i >= 0; i--) {
				if (Character.isUpperCase(newWord.charAt(i))) {
					if (i > 0)
						if (Character.isLowerCase(newWord.charAt(i - 1))) {
							newWord.insert(i, ' ');
							isCompoundIdentifier = true;
						}
				} else if (Character.isLowerCase(newWord.charAt(i))) {
					if (i > 0)
						if (Character.isUpperCase(newWord.charAt(i - 1))) {
							newWord.insert(i - 1, ' ');
							isCompoundIdentifier = true;
						}
				}

			}

			newBuffer.append(newWord.toString().toLowerCase());
			newBuffer.append(' ');
			if (keepCompoundIdentifier) {
				if (isCompoundIdentifier) {
					newBuffer.append(originalWord.toLowerCase());
					newBuffer.append(' ');
				}
			}
		}
//		System.out.println("=====");
//		System.out.println(newBuffer.toString());
		return newBuffer.toString();
	}

	public static String eliminateNonLiterals(String originalBuffer, boolean keepDigits) {
		String newBuffer;

		if (keepDigits) {
			newBuffer = originalBuffer.replaceAll("[^a-zA-Z0-9_]", " ");
		} else {
			newBuffer = originalBuffer.replaceAll("[^a-zA-Z_]", " ");
		}

//		System.out.println(newBuffer);

		return newBuffer;
	}

	public static boolean isAllDigits(String word) {
		char[] charactersWord = word.toCharArray();
		for (char c : charactersWord) {
			if (Character.isDigit(c) == false)
				return false;
		}
		return true;
	}

	public static String elimiateStopWords(String originalBuffer, int numberOfCharactersForWordToRemove) {
		Hashtable<String, Integer> listOfStopWords = null;

		try {
			listOfStopWords = loadListOfStopWords();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String words[] = originalBuffer.split(" ");
		StringBuilder newBufferAfterEliminatingStopWords = new StringBuilder();

		for (String word : words) {
			if (word.length() == 0)
				continue;

			if (listOfStopWords.get(word) != null)
				continue;

			if (isAllDigits(word))
				continue;

			if (word.length() <= numberOfCharactersForWordToRemove)
				continue;

			newBufferAfterEliminatingStopWords.append(word);
			newBufferAfterEliminatingStopWords.append(' ');
		}
//		System.out.println("-----");
//		System.out.println(newBufferAfterEliminatingStopWords.toString());
		return newBufferAfterEliminatingStopWords.toString();
	}

	public static String stemBuffer(String originalBuffer) {
		String words[] = originalBuffer.split(" ");
		StringBuilder newBufferStemmed = new StringBuilder();

		for (String word : words) {
			if (word.length() == 0)
				continue;

			Stemmer stemmer = new Stemmer();
			for (int i = 0; i < word.length(); i++)
				stemmer.add(word.charAt(i));

			stemmer.stem();
			newBufferStemmed.append(stemmer.toString());
			newBufferStemmed.append(' ');
		}
//		System.out.println("-----");
//		System.out.println(newBufferStemmed.toString());
		return newBufferStemmed.toString();
	}
}
