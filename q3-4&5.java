package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

// q3
public class Solution {
	
	private static HashSet<String> stopwords = new HashSet<>();
	
	private static void readStopWords() {
		String FILENAME = "stopwords.txt";
		FileReader reader = null;
		BufferedReader bufferedReader = null;
		String line = null;
		try {
			reader = new FileReader(FILENAME);
			bufferedReader = new BufferedReader(reader);
			while ((line = bufferedReader.readLine()) != null) {
				stopwords.add(line.toLowerCase());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static boolean isCharacter(char ch) {
		int ascii = (int) ch;
		if ((ascii >= 65 && ascii <= 90) || (ascii >= 97 && ascii <= 122)) {
			return true;
		}
		return false;
	}
	
	private static boolean isNumber(char ch) {
		int ascii = (int) ch;
		if (ascii >= 48 && ch <= 57) {
			return true;
		}
		return false;
	}
	
	private static boolean isSpecial(char ch) {
		int ascii = (int) ch;
		if (ascii == 39 || ascii == 45) {
			return true;
		} 
		return false;
	}
	
	// step4: word Definition
	public static HashMap<String, Integer> getWords(String text) {
		HashMap<String, Integer> result = new HashMap<>();
		int start = 0;
		int end = 0;
		boolean alpha = false;
		for (int i = 0; i < text.length(); i++) {
			if (isCharacter(text.charAt(i))) {
				end++;
				alpha = true;
				System.out.println(alpha);
			} else if (isNumber(text.charAt(i))) {
				end++;
			} else if (isSpecial(text.charAt(i))) {
				end++;
			} else {
				if (alpha) {
					String word = text.substring(start, end);
					if (result.containsKey(word)) {
						result.put(word, result.get(word) + 1);
					} else {
						result.put(word, 1);
					}
					alpha = false;
					start = i + 1;
					end = i + 1;
				} else {
					start = i + 1;
					end = i + 1;
				}
			}
		}
		if (alpha) {
			String word = text.substring(start, end);
			if (result.containsKey(word)) {
				result.put(word, result.get(word) + 1);
			} else {
				result.put(word, 1);
			}
		}
		return result;
	}
	
	
	// step5: impact score calculation
	public static int getImpactScore(ArrayList<String> words, int favorite_count, int retweet_count, int followers_count) {
		// remove short URLs TODO
		
		// delete stop words and get EWC
		int numberOfStopwords = 0;
		for (String word : words) {
			if (stopwords.contains(word.toLowerCase())) {
				numberOfStopwords++;
			}
		}
		int EWC = words.size() - numberOfStopwords;
		int impact_score = EWC * (favorite_count + retweet_count + followers_count);
		return impact_score < 0 ? 0 : impact_score;
	}
	
	
	public static void main(String[] args) {
		System.out.println(getWords("Query 3 is su-per-b! I'mmmm lovin' it!"));
	}
}
