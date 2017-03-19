import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class Reducer {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        reduce();
    }

    public static void reduce() {
        BufferedReader br;
        HashMap<String, JSONArray> result = new HashMap<>();

        try {
            br = new BufferedReader(new InputStreamReader(System.in));

            PrintStream printWriter = new PrintStream(System.out, true, "UTF-8");
            JSONArray array;
            String line;
            while((line = br.readLine()) != null) {


                try {
                    JSONParser parser = new JSONParser();
                    JSONObject lineResult = (JSONObject) parser.parse(line);
                    String hashTag_text = lineResult.get("hashTag_text").toString();
                    lineResult.remove("hashTag_text");
                    if (result.get(hashTag_text) == null) {
                        array = new JSONArray();
                        array.add(lineResult);
                        result.put(hashTag_text, array);
                    } else {
                        array = result.get(hashTag_text);
                        array.add(lineResult);
                        result.put(hashTag_text, array);
                    }

                } catch (ParseException e) {
                    System.out.println("can't parse the string");
                }

            }
            for (Map.Entry<String, JSONArray>  entry : result.entrySet()) {
                printWriter.print(entry.getKey() + "\t" + entry.getValue().toString() + "\n");
            }

        } catch (IOException e) {
            //out.close();
            e.printStackTrace();
        }
    }

}
