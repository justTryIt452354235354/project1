
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.simple.JSONArray;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Mapper_all {
    private final static String FILENAME = "part-r-00000";
    private static HashSet<Object> idSet = new HashSet<>();
    private static HashSet<Object> idStrSet = new HashSet<>();
    
    // first step
    public static boolean isMalformed(JSONObject obj) {

        JSONObject entities = (JSONObject) obj.get("entities");
        if (entities == null || entities.isEmpty()) return true;

        JSONArray hashtags = (JSONArray) entities.get("hashtags");
        if (hashtags == null || hashtags.size() == 0) return true;

//        for (int j = 0; j < hashtags.size(); j++) {
//            JSONObject o = (JSONObject) hashtags.get(j);
//            if (o.get("text") != null && !o.get("text").toString().isEmpty())
//                break;//只要有一个有text，就满足条件, 所以跳出循环，进入下一项
//            if (j == hashtags.size() - 1 && j != 0) return true; //如果已经都到最后了，还是没有发现有text，那么直接return malform
//        }

        for (int j = 0; j < hashtags.size(); j++) {
            JSONObject o = (JSONObject) hashtags.get(j);
            if (o.get("text") == null || o.get("text").toString().isEmpty()) return true;
        }

        Object id = obj.get("id");
        Object idStr = obj.get("id_str");
        if ((id == null || id.toString().isEmpty()) && (idStr == null || idStr.toString().isEmpty())) {//只有都不在的情况才返回,左右两边各2选1
            return true;
        }

        JSONObject user = (JSONObject) obj.get("user");
        if (user == null || user.isEmpty()) return true;

        Object id2 = user.get("id");
        Object idStr2 = user.get("id_str");
        if ((id2 == null || id2.toString().isEmpty()) && (idStr2 == null || idStr2.toString().isEmpty())) {//只有都不在的情况才返回,左右两边各2选1
            return true;
        }
        Object created_at = obj.get("created_at");
        if (created_at == null || created_at.toString().isEmpty()) {
            return true;
        }
        Object text = obj.get("text");
        if (text == null || text.toString().isEmpty()) {
            return true;
        }
        Object lang = obj.get("lang");
        if (lang == null || lang.toString().isEmpty()) {
            return true;
        }
        
        return false;
    }

    // second step
    public static boolean isDuplicate(JSONObject obj) {
        Object id = obj.get("id");
        Object id_str = obj.get("id_str");
        if (id != null && !id.toString().isEmpty()) {
            if (idSet.contains(id))
                return true;
            else
                idSet.add(id);
        }
        if (id_str != null && !id_str.toString().isEmpty()) {
            if (idStrSet.contains(id_str))
                return true;
            else
                idStrSet.add(id_str);
        }
        return false;
    }

    // third step
    public static boolean isInValidLanguage(JSONObject obj) {
        Object lang = obj.get("lang");
        if (lang == null) return true; //前面已经filter过了
        String langValue = lang.toString();
        final String regex = "^(ar|en|fr|in|pt|es|tr)$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(langValue);
        return (!matcher.find());
    }


    public static String isShortenedURLs(String line) {

        final String regex = "(https?|ftp):\\/\\/[\\.[a-zA-Z0-9]\\/\\-_]+";
//        final String regex2 = "([\"'])(?:(?=(\\\\?))\\2.)*?\\1";
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
//            System.out.println("Full match: " + matcher.group(0));
//            for (int i = 1; i <= matcher.groupCount(); i++) {
//                System.out.println("Group " + i + ": " + matcher.group(i));
//            }
            line = line.replaceAll(regex,"");
            //System.out.println(line);
        }

        return line;

    }

//    public static boolean isShortenedURLs(String line) {
//        final String regex = "(https?|ftp)://[^\\t\\r\\n /$.?#][^\\t\\r\\n ]*";
//        final Pattern pattern = Pattern.compile(regex);
//        final Matcher matcher = pattern.matcher(line);
//        if (matcher.find())
//            return true;
//        else
//            return false;
//    }
    


    private static void map() {
        BufferedReader br;
        PrintWriter out = null;

        try {
            //br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            br = new BufferedReader(new InputStreamReader(new FileInputStream(FILENAME), StandardCharsets.UTF_8));
            //br = new BufferedReader(new FileReader(FILENAME)); //the input is json format

            //out = new PrintWriter(
            //        new OutputStreamWriter(System.out, "UTF-8"), true);
            
            PrintWriter printWriter = new PrintWriter(new File("mapper"),"UTF-8");

            String line;
            while((line = br.readLine()) != null) {
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject data = (JSONObject) parser.parse(line);//http://stackoverflow.com/questions/13939925/remove-all-occurrences-of-from-string
                    line = isShortenedURLs(data.toJSONString().replaceAll("\\\\/", "/"));

                    JSONObject obj = (JSONObject) parser.parse(line);

                    if (isMalformed(obj)) {
                        continue;
                    }
                    
                    if (isInValidLanguage(obj)) {
                        continue;
                    }
                    
                    if (isDuplicate(obj)) {
                        continue;
                    }

                    JSONObject user = (JSONObject) obj.get("user");
                    Object id = user.get("id");
                    Object idStr = user.get("id_str");
                    
                    //JSONObject text = (JSONObject) obj.get("text");
                    //HashMap<String, Integer> wordFreq = effectiveWord(text.toString());
                    //for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
                    //    System.out.print("(" + entry.getKey() + ",");
                    //    System.out.print(entry.getValue() + ")");
                    //}
                    JSONObject entities = (JSONObject) obj.get("entities");
                    JSONArray hashtags = (JSONArray) entities.get("hashtags");
                    JSONArray tagArray = new JSONArray();

                    JSONObject lineResult = new JSONObject();
                    for (Object hashtag : hashtags) {
                        JSONObject tag = (JSONObject) hashtag;
                        //if (tag.get("text") == null || tag.get("text").toString().isEmpty()) System.out.println("!!!");
                        //System.out.println(tag.get("text") );
                        tagArray.add(tag.get("text"));
                    }
                    lineResult.put("hashtag_text", tagArray);
                    if (id == null || id.toString().isEmpty()) {
                        lineResult.put("userid", idStr);
                    } else {
                        lineResult.put("userid", id);
                    }
                    lineResult.put("text", obj.get("text"));
                    printWriter.write(lineResult.toString() + "\n");

                    
                    //System.out.println(lineResult.toString());

                    
                    //System.out.print("{");
                    //for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
                    //    System.out.print("(" + entry.getKey() + ",");
                    //    System.out.print(entry.getValue() + ")");
                    //}

                    //out.println(obj.toJSONString());

                } catch (ParseException e) {
                    continue; // cannot be parsed as a JSON object
                }
                printWriter.flush();
            }

        } catch (IOException e) {
            
            //out.close();
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        map();
    }
    
    public static HashSet<String> readStopWords() {
        String FILENAME = "stopwords.txt";
        HashSet<String> stopwords = new HashSet<String>();
        
        String line = null;
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
    
    public static HashMap<String, Integer> effectiveWord(String str) {
        HashMap<String, Integer> frequencyMap = new HashMap<String, Integer>();
        HashSet<String> set = readStopWords();
        String[] arr = str.split(" ");
        for (String s : arr) {
            if (!set.contains(s.toLowerCase())) {
                frequencyMap.put(s, frequencyMap.getOrDefault(s,0) + 1);
            }
        }
        return frequencyMap;
    }
    
    
    
}
