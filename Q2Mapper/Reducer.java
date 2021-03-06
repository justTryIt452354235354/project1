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
    private static HashSet<Object> idSet = new HashSet<>();

    public static void main(String[] args) {
        reduce();
    }

    public static void reduce() {
        BufferedReader br;
        Map<String, JSONArray> result = new TreeMap<>();

        try {
            br = new BufferedReader(new InputStreamReader(System.in));

            PrintStream printWriter = new PrintStream(System.out, true, "UTF-8");

            String line;
            String currentTag = null;
            String tag;
            JSONArray array;
            while((line = br.readLine()) != null) {
                try {

                    JSONParser parser = new JSONParser();
                    JSONObject lineResult = (JSONObject) parser.parse(line.split("\t")[1]);
                    tag = line.split("\t")[0];
                    Object tid = lineResult.get("tid");


                    //duplicate
                    if (tag.equals(currentTag) && !tid.toString().isEmpty()) {
                        if (idSet.contains(tid.toString()))
                            continue;
                        else
                            idSet.add(tid.toString());
                    }

                    lineResult.remove("tid");

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
