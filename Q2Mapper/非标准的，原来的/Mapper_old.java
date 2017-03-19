import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class Filter {
	private HashSet<Object> idSet = new HashSet<Object>();
	private HashSet<Object> idStrSet = new HashSet<Object>();
	
	// first step
	public boolean isMalformed(JSONObject obj) {
		Object id = obj.get("id");
		Object idStr = obj.get("id_str");
		if ((id == null || id.toString().equals("")) && (idStr == null || idStr.toString().equals(""))) {
			return true;
		}
		JSONObject user = (JSONObject) obj.get("user");
		Object id2 = user.get("id");
		Object idStr2 = user.get("id_str");
		if ((id2 == null || id2.toString().equals("")) && (idStr2 == null || idStr2.toString().equals(""))) {
			return true;
		}
		Object created_at = obj.get("created_at");
		if (created_at == null || created_at.toString().equals("")) {
			return true;
		}
		Object text = obj.get("text");
		if (text == null || text.toString().equals("")) {
			return true;
		}
		Object lang = obj.get("lang");
		if (lang == null || lang.toString().equals("")) {
			return true;
		}
		JSONObject entities = (JSONObject) obj.get("entities");
		if (entities != null) {
			JSONArray hashtags = (JSONArray) entities.get("hashtags");
			if (hashtags != null) {
				for (int j = 0; j < hashtags.size(); j++) {
					JSONObject o = (JSONObject) hashtags.get(j);
					if (o.get("text") == null || o.get("text").toString().equals(""))
						return true;
				}
			}
		}
		return false;
	}
	
	// second step
	public boolean isDuplicate(JSONObject obj) {
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
	public boolean isInValidLanguage(JSONObject obj) {
		Object lang = obj.get("lang");
		String langValue = lang.toString();
		final String regex = "^(ar|en|fr|in|pt|es|tr)$";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(langValue);
		if (!matcher.find())
			return true;
		else 
			return false;
	}
	
	public boolean isShortenedURLs(String line) {
		final String regex = "(https?|ftp)://[^\\t\\r\\n /$.?#][^\\t\\r\\n ]*";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(line);
		if (matcher.find()) 
			return true;
		else 
			return false;
	}
	
}

public class Mapper {
	
	private void map() {
		Filter filter = null;
		BufferedReader br = null;
		PrintWriter out = null;
		
		try {
			filter = new Filter();
			br = new BufferedReader(
					new InputStreamReader(new FileInputStream("data25"),
							StandardCharsets.UTF_8));
			out = new PrintWriter(
					new OutputStreamWriter(System.out, "UTF-8"), true);
			
			String line = null;
			for (int i = 0; i < 1; i++) {
				line = br.readLine();
				try {
					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject) parser.parse(line);
					System.out.println(filter.isShortenedURLs(line));
					
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
		Mapper mapper = new Mapper();
		mapper.map();
		
		
	}
}
