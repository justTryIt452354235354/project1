import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.json.simple.JSONArray;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class Reducer {

    public static void main(String[] args) {
        reduce();
    }

    public static void reduce() {
        BufferedReader br;
        Map<String, JSONArray> result = new TreeMap<>();

        try {
            br = new BufferedReader(new InputStreamReader(System.in));

            PrintStream printWriter = new PrintStream(System.out, true, "UTF-8");

            String curtline = null;
            String line;
            String currentTag = null;
            String tag;
            JSONArray array;
            while((line = br.readLine()) != null) {
                try {

                    if (line.equals(curtline)) {
                        continue;
                    }
                    curtline = line; //get rid of the duplicate json

                    JSONParser parser = new JSONParser();
                    JSONObject lineResult = (JSONObject) parser.parse(line);
                    tag = lineResult.get("hashTag_text").toString();
                    lineResult.remove("hashTag_text");

//                    if (result.get(hashTag_text) == null) {
//                        array = new JSONArray();
//                        array.add(lineResult);
//                        result.put(hashTag_text, array);
//                    } else {
//                        array = result.get(hashTag_text);
//                        array.add(lineResult);
//                        result.put(hashTag_text, array);
//                    }

                    if (!tag.equals(currentTag)) {
                        if (currentTag != null) {
                            printWriter.print(currentTag + "\t" + result.get(currentTag).toString() + "\n");

                        } //print previous one

                        currentTag = tag;
                        result.clear(); //clear the memory
                        array = new JSONArray();
                        array.add(lineResult);
                        result.put(currentTag, array);
                    } else {
                        currentTag = tag;
                        array = result.get(tag);
                        array.add(lineResult);
                        result.put(currentTag, array);
                    }

                } catch (ParseException e) {
                    e.getMessage();
                }

            }
            printWriter.print(currentTag + "\t" + result.get(currentTag).toString() + "\n"); //the last line

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
