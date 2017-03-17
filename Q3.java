import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Q3 {
    final static HashMap<String, Double> idf = new HashMap<String, Double>();
    final static ArrayList<HashMap<String, Double>> tf_logy_list = new ArrayList<HashMap<String, Double>>();
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
    }
    
    
    public static String censoring(String str) {
        HashSet<String> banWords = transfer();
        HashMap<String, Integer> topicWords = getWords(str);
        for (Map.Entry<String, Integer> entry : topicWords.entrySet()) {
            if (banWords.contains(entry.getKey().toLowerCase())) {
                System.out.println("I haven't finished");
            }
        }
    }
    
    public static String star(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            if (i == 0 || i == str.length() - 1) {
                result += str.charAt(i);
            } else {
                result += "*";
            }
        }
        return result;
    }
    
    public static HashSet<String> transfer() {
        String FILENAME = "go_flux_yourself.txt";
        HashSet<String> banWords = new HashSet<String>();
        
        String line;
        try {
            FileReader fileReader = new FileReader(FILENAME);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(rot13(line));
                banWords.add(rot13(line));
            }
            bufferedReader.close();  
        } catch (IOException e) {
            e.printStackTrace();
        }
        return banWords;
    }
    public static String rot13(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch >= 97 && ch <= 122) {
                result += (char)((str.charAt(i) - 97 + 13)%26 + 'a');
            } else {
                result += ch;
            }
        }
        return result;
    }
    
    
    
    public static HashMap<String, Double> extraction(List<String> range) {
        int n = range.size();
        HashMap<String, Double> topicScore = new HashMap<String, Double>();
        for (int i = 0; i < n; i++) {
            String str = range.get(i);
            HashMap<String, Integer> topicWords = getWords(str);
            int y = getImpactScore(topicWords, favorite, retweet, followers);
            tf_logy_list.add(tf_logy(topicWords, y));
        }
        for (int i = 0; i < n; i++) {
            HashMap<String, Double> tf_logy = tf_logy_list.get(i);
            for (Map.Entry<String, Double>  entry : tf_logy.entrySet()) {
                String word = entry.getKey();
                topicScore.put(word, entry.getValue()*n/idf.get(word));
            }
        }
        return topicScore;
        
    }
    
    public static HashMap<String, Double> tf_logy(HashMap<String, Integer> topicWords, int y) {
        double log_y = Math.log(y+1);
        HashMap<String, Double> tf_logy = new HashMap<String, Double>();
        int total_length = 0;
        for (Map.Entry<String, Integer>  entry : topicWords.entrySet()) {
            String key = entry.getKey();
            idf.put(key, idf.getOrDefault(key, 0) + 1);
            total_length += entry.getValue();
        }
        for (Map.Entry<String, Integer>  entry : topicWords.entrySet()) {
            tf_logy.put(entry.getKey(), (double) entry.getValue()/total_length*log_y);
        }
        return tf_logy;
    }
    
    
    public static int getImpactScore(ArrayList<String> words, int s1, int s2, int s3) {
        return 0;
    }
    
    public static HashMap<String, Integer> getWords(String text) {
        return new HashMap<String, Integer>();
    }

}
