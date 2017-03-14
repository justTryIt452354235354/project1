import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class Mapper {
    private final static String FILENAME = "part-r-00000";
    private static HashSet<Object> idSet = new HashSet<>();
    private static HashSet<Object> idStrSet = new HashSet<>();

    // first step
    public static boolean isMalformed(JSONObject obj) {
        Object id = obj.get("id");
        Object idStr = obj.get("id_str");
        if ((id == null || id.toString().isEmpty()) || (idStr == null || idStr.toString().isEmpty())) {
            System.out.println("==================tid is missing");
            return true;
        }
        JSONObject user = (JSONObject) obj.get("user");
        Object id2 = user.get("id");
        Object idStr2 = user.get("id_str");
        if ((id2 == null || id2.toString().isEmpty()) || (idStr2 == null || idStr2.toString().isEmpty())) {
            System.out.println("==================uid is missing" + obj.toJSONString());
            return true;
        }
        Object created_at = obj.get("created_at");
        if (created_at == null || created_at.toString().isEmpty()) {
            System.out.println("==================created at is missing");
            return true;
        }
        Object text = obj.get("text");
        if (text == null || text.toString().isEmpty()) {
            System.out.println("==================text is missing");
            return true;
        }
        Object lang = obj.get("lang");
        if (lang == null || lang.toString().isEmpty()) {
            System.out.println("==================lang is missing");
            return true;
        }
        JSONObject entities = (JSONObject) obj.get("entities");
        if (entities != null) {
            JSONArray hashtags = (JSONArray) entities.get("hashtags");
            if (hashtags != null) {
                for (int j = 0; j < hashtags.size(); j++) {
                    JSONObject o = (JSONObject) hashtags.get(j);
                    if (o.get("text") == null || o.get("text").toString().isEmpty())
                        return true;
                }
            }
        }
        return false;
    }

    // second step
    public static boolean isDuplicate(JSONObject obj) {
        Object id = obj.get("id");
        Object id_str = obj.get("id_str");
        if (id != null) {
            if (idSet.contains(id))
                return true;
            else
                idSet.add(id);
        }
        if (id_str != null) {
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
        if (lang == null) return true;
        String langValue = lang.toString();
        final String regex = "^(ar|en|fr|in|pt|es|tr)$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(langValue);
        if (!matcher.find())
            return true;
        else
            return false;
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


    private static void map() {
        BufferedReader br;
        PrintWriter out = null;

        try {
//            br = new BufferedReader(
//                    new InputStreamReader(new FileInputStream("data25"),
//                            StandardCharsets.UTF_8));

            br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            //br = new BufferedReader(new FileReader(FILENAME)); //the input is json format

            out = new PrintWriter(
                    new OutputStreamWriter(System.out, "UTF-8"), true);

            String line;
            while((line = br.readLine()) != null) {
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject init = (JSONObject) parser.parse(line);
                    //System.out.println(obj.toJSONString().replaceAll("\\\\", "")); //http://stackoverflow.com/questions/13939925/remove-all-occurrences-of-from-string
                    line = isShortenedURLs(init.toJSONString().replaceAll("\\\\", ""));

                    JSONObject obj = (JSONObject) parser.parse(line);
                    if (isDuplicate(obj)) {
                        System.out.println("===========duplicate tid==========");
                        continue;
                    }

                    if (isInValidLanguage(obj)) {
                        System.out.println("===========language invalid==========");
                        continue;
                    }

                    if (isMalformed(obj)) {
                        System.out.println("===========malformed json==========");
                        continue;
                    }

                    out.println(obj.toJSONString());

                } catch (ParseException e) {
                    continue; // cannot be parsed as a JSON object
                }
            }

        } catch (IOException e) {
            out.flush();
            out.close();
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        map();
    }
}
