import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Q3 {
    final static HashMap<String, Double> idf = new HashMap<String, Double>();
    final static ArrayList<HashMap<String, Double>> tf_logy_list = new ArrayList<HashMap<String, Double>>();
    private static HashSet<String> stopwords = new HashSet<>();
    static String favorite;
    static String retweet;
    static String followers;
    String n1;
    String n2;
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
    }
    
    // step 3: Text Censoring
    public static String censoring(String str) {
        HashSet<String> banWords = transfer();
        String[] arr = split(str);
        for (int i = 0; i < arr.length; i++) {
            if (banWords.contains(str.toLowerCase())) {
                arr[i] = star(arr[i]);
            }
        }
        return arr.toString();
    }
    
    // step 3: helper function: star the ban word
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
    
    // step 3: helper function: transfer ban word from file to set
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
    
    // step 3: helper function: rot13 method
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
    
    // step 4: helper function filter the word
    public static String[] split(String str) {
        String[] arr = str.split("[^(0-9|a-z|A-Z|\\-|\\')]+");
        return arr;
    }
    
    // step4: word Definition
    public static HashMap<String, Integer> getWords(String text) {
        HashMap<String, Integer> result = new HashMap<>();
        String[] filteredArray  = split(text);
        for (String str : filteredArray) {
            result.put(str, result.getOrDefault(str, 0) + 1);
        }
        return result;
    }

    
    // step5: impact score calculation
    public static int getImpactScore(HashMap<String, Integer> words, int favorite_count, int retweet_count, int followers_count) {
        // remove short URLs TODO
        
        // delete stop words and get EWC
        int numberOfStopwords = 0;
        for (Map.Entry<String, Integer> entry : words.entrySet()) {
            if (stopwords.contains(entry.getKey().toLowerCase())) {
                numberOfStopwords++;
            }
        }
        int EWC = words.size() - numberOfStopwords;
        int impact_score = EWC * (favorite_count + retweet_count + followers_count);
        return impact_score < 0 ? 0 : impact_score;
    }
    

    // step 5: helper function
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
    
    // step 6: Topic Words Extraction 
    // TODO: Remember to remove short URLs BEFORE this step! (I haven't done yet)
    public static List<Map.Entry<String, Double>> extraction(List<String> range, String n1) {
        int n = range.size();
        HashMap<String, Double> topicScore = new HashMap<String, Double>();
        for (int i = 0; i < n; i++) {
            String ith_str = range.get(i);
            HashMap<String, Integer> ith_topicWords = getWords(ith_str);
            int y = getImpactScore(ith_topicWords, Integer.parseInt(favorite), Integer.parseInt(retweet), Integer.parseInt(followers));
            tf_logy_list.add(tf_logy(ith_topicWords, y));
            // add tf * ln(y + 1) in each row to the list which will be used later
        }
        for (int i = 0; i < n; i++) {
            HashMap<String, Double> tf_logy = tf_logy_list.get(i);
            for (Map.Entry<String, Double> entry : tf_logy.entrySet()) {
                String word = entry.getKey();
                double score = entry.getValue() * n / idf.get(word);
                topicScore.put(word, topicScore.getOrDefault(word, (double) 0) + score);
                // calculate topicScore sum(x * ln(y + 1))
            }
        }
        
        // sort the topicScore
        List<Map.Entry<String, Double>> list = new ArrayList<>(topicScore.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {   
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {      
                if (o2.getValue() == o1.getValue()) {
                    return o1.getKey().compareTo(o2.getKey());
                }
                return (int)(o2.getValue() - o1.getValue()); 
            }
        });
        
        List<Map.Entry<String, Double>> result = new ArrayList<>();
        int number = Integer.parseInt(n1);
        for (int i = 0; i < number; i++) {
            result.add(list.get(i));
        }
        return result;
    }
    
    // step 6 helper function: calculate tf * ln(y + 1)
    public static HashMap<String, Double> tf_logy(HashMap<String, Integer> topicWords, int y) {
        double log_y = Math.log(y + 1);
        HashMap<String, Double> tf_logy = new HashMap<String, Double>();
        int total_length = 0;
        for (Map.Entry<String, Integer> entry : topicWords.entrySet()) {
            String key = entry.getKey();
            idf.put(key, idf.getOrDefault(key, (double) 0) + entry.getValue());
            total_length += entry.getValue();
        }
        for (Map.Entry<String, Integer>  entry : topicWords.entrySet()) {
            tf_logy.put(entry.getKey(), (double) entry.getValue() / total_length * log_y);
        }
        return tf_logy;
    }
}
