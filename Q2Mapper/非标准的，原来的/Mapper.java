import java.io.*;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONArray;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class Mapper {
    //private final static String FILENAME = "part-r-00000";
    private static HashSet<Object> idSet = new HashSet<>();
    private static HashSet<Object> idStrSet = new HashSet<>();
    
    // first step
    public static boolean isMalformed(JSONObject obj) {
        
        // hashtag text (stated above) is missing or empty
        JSONObject entities = (JSONObject) obj.get("entities");
        if (entities == null || entities.isEmpty()) return true;
        
        JSONArray hashtags = (JSONArray) entities.get("hashtags");
        if (hashtags == null || hashtags.size() == 0) return true;
        

//        for (int j = 0; j < hashtags.size(); j++) {
//            JSONObject o = (JSONObject) hashtags.get(j);
//            if (o.get("text") == null || o.get("text").toString().isEmpty()) return true;
//        }

        // Both id and id_str of the tweet object are missing or empty
        Object id = obj.get("id");
        Object idStr = obj.get("id_str");
        if ((id == null || id.toString().isEmpty()) && (idStr == null || idStr.toString().isEmpty())) {
            return true;
        }
        if (id != null && !id.toString().isEmpty()) {
            if (idSet.contains(id))
                return true;
            else
                idSet.add(id);
        }
        if (idStr != null && !idStr.toString().isEmpty()) {
            if (idStrSet.contains(idStr))
                return true;
            else
                idStrSet.add(idStr);
        }
        
        
        // text field is missing or empty
        Object text = obj.get("text");
        if (text == null || text.toString().isEmpty()) {
            return true;
        }
        
        // created_at field is missing or empty
        Object created_at = obj.get("created_at");
        if (created_at == null || created_at.toString().isEmpty()) {
            return true;
        }
        
        
        
        // Both id and id_str in user object are missing or empty
        JSONObject user = (JSONObject) obj.get("user");
        if (user == null || user.isEmpty()) return true;
      
        Object id2 = user.get("id");
        Object idStr2 = user.get("id_str");
        if ((id2 == null || id2.toString().isEmpty()) && (idStr2 == null || idStr2.toString().isEmpty())) {
            return true;
        }
        
        
        
        // lang field is missing or empty or malformed
        Object lang = obj.get("lang");
        if (lang == null || lang.toString().isEmpty()) {
            return true;
        }
        String langValue = lang.toString();
        final String regex = "^(ar|en|fr|in|pt|es|tr)$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(langValue);
        if (!matcher.find()) {
            return true;
        }

        return false;
    }


    public static String isShortenedURLs(String line) {
        final String regex = "(https?|ftp):\\/\\/[\\.[a-zA-Z0-9]\\/\\-_]+";
        line = line.replaceAll(regex,"");

        return line;
    }

    private static void map() {
        HashSet<String> stopwords = readStopWords();
        BufferedReader br;
        //HashMap<String, LinkedList<String>> result = new HashMap<String, LinkedList<String>>();

        try {
            //br = new BufferedReader(new InputStreamReader(new FileInputStream(FILENAME), StandardCharsets.UTF_8));
            br = new BufferedReader(new InputStreamReader(System.in,StandardCharsets.UTF_8));
            //PrintWriter printWriter = new PrintWriter(new File("reducer_Result_final"),"UTF-8");
            PrintStream printWriter = new PrintStream(System.out, true, "UTF-8");
            String line;
            while((line = br.readLine()) != null) {
                try {
                    JSONObject lineResult = new JSONObject();

                    JSONParser parser = new JSONParser();
                    line = isShortenedURLs(line.replaceAll("\\\\/", "/"));
                    JSONObject obj = (JSONObject) parser.parse(line);//http://stackoverflow.com/questions/13939925/remove-all-occurrences-of-from-string


                    if (isMalformed(obj)) {
                        continue;
                    }

                    String text = obj.get("text").toString();//先转成string然后parse成json，然后转成string


                    HashMap<String, Integer> wordFreq = effectiveWord(text, stopwords);
                    lineResult.put("text", wordFreq);

                    JSONObject user = (JSONObject) obj.get("user");
                    Object id = user.get("id");
                    Object idStr = user.get("id_str");
                    if (id == null || id.toString().isEmpty()) {
                        String userid = idStr.toString();
                        lineResult.put("userid", userid);
                    } else {
                        String userid = id.toString();
                        lineResult.put("userid", userid);
                    }

                    JSONObject entities = (JSONObject) obj.get("entities");
                    JSONArray hashtags = (JSONArray) entities.get("hashtags");

                    for (Object hashtag : hashtags) {
                        JSONObject tag = (JSONObject) hashtag;
                        
                        if (tag.get("text") == null || tag.get("text").toString().isEmpty()) continue;
                        
                        String hashTag_text = tag.get("text").toString();
                        lineResult.put("hashTag_text", hashTag_text);
                        //printWriter.append(hashTag_text + "\t");
                        printWriter.append(lineResult.toString() + "\n");
                    }
                } catch (ParseException e) {
                    continue; // cannot be parsed as a JSON object
                }
                //printWriter.flush();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static HashSet<String> readStopWords() {
        String FILENAME = "stopwords.txt";
        HashSet<String> stopwords = new HashSet<String>();

        String line;
        try {
            FileReader fileReader = new FileReader(FILENAME);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                stopwords.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopwords;
    }

    public static HashMap<String, Integer> effectiveWord(String str, HashSet<String> stopwords) {
        HashMap<String, Integer> frequencyMap = new HashMap<String, Integer>();
        //System.out.println(str);
        str = str.replaceAll("[\\p{P} \\p{N} \\p{M} \\p{Z} \\p{S} \\p{C}]+", " ");
       // System.err.println(str);
        String[] arr = str.split("\\s+");
        for (String s : arr) {
            if (!stopwords.contains(s.toLowerCase())) {
                if (s.matches("\\p{L}+")) {
                    frequencyMap.put(s, frequencyMap.getOrDefault(s,0) + 1);
                }
            }
        }
        return frequencyMap;
    }



    public static void main(String[] args) {
        //StopWatch timer1 = new StopWatch();
        map();
        //System.out.println(timer1.elapsedTime()/1000 + "sec");
    }
    
}