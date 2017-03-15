import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import org.json.simple.JSONArray;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        if (created_at == null || created_at.toString().isEmpty()) return true;

        Object text = obj.get("text");
        if (text == null || text.toString().isEmpty()) return true;

        Object lang = obj.get("lang");
        if (lang == null || lang.toString().isEmpty()) return true;
        
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

        //if (lang == null || lang.toString().isEmpty()) return true; //前面已经filter过了
        String langValue = lang.toString();
        final String regex = "^(ar|en|fr|in|pt|es|tr)$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(langValue);
        return (!matcher.find());
    }


    public static String isShortenedURLs(String line) {

        final String regex = "(https?|ftp):\\/\\/[\\.[a-zA-Z0-9]\\/\\-_]+";
        line = line.replaceAll(regex,""); //直接replaceall，不需要再match几次

        return line;

    }

    private static void map() {
        BufferedReader br;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(FILENAME), StandardCharsets.UTF_8));

            PrintWriter printWriter = new PrintWriter(new File("mapper"),"UTF-8");

            String line;
            while((line = br.readLine()) != null) {
                try {
                    JSONParser parser = new JSONParser();
                    line = isShortenedURLs(line.replaceAll("\\\\/", "/"));
                    JSONObject obj = (JSONObject) parser.parse(line);//http://stackoverflow.com/questions/13939925/remove-all-occurrences-of-from-string

                    if (isMalformed(obj)) continue;
                    
                    if (isInValidLanguage(obj)) continue;
                    
                    if (isDuplicate(obj)) continue;

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

                    for (Object hashtag : hashtags) {
                        JSONObject tag = (JSONObject) hashtag;
                        JSONObject lineResult = new JSONObject();
                        lineResult.put("hashtag_text", tag.get("text"));
                        lineResult.put("text", obj.get("text"));
                        if (id == null || id.toString().isEmpty()) {
                            lineResult.put("userid", idStr);
                        } else {
                            lineResult.put("userid", id);
                        }
                        printWriter.write(lineResult.toString() + "\n");
                    }

                    
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
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Stopwatch timer1 = new Stopwatch();
        map();
        System.out.println(timer1.elapsedTime()/1000 + "sec");
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
