package cc.cmu.edu.Q1;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



/**
 * 
 * @author zack
 * Query 2 for MySQL
 */
public class Q2MySQLServlet extends HttpServlet {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_NAME = "q2db"; //TODO
    private static final String URL = "jdbc:mysql://ccdb.c6nxguhfkaz7.us-east-1.rds.amazonaws.com/" + DB_NAME + "?useSSL=false";
    private static final String DB_USER = System.getenv("db_user"); //TODO
    private static final String DB_PWD = System.getenv("db_pwd"); //TODO
    private static final String TEAM_ID = "";
    private static final String TEAM_AWS_ACCOUNT_ID = "";
    
    
    private static Connection connection;
    
    private void initializeConnection() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        connection = DriverManager.getConnection(URL, DB_USER, DB_PWD);
    }
    
    public Q2MySQLServlet() {
    	try {
    		initializeConnection();
    	} catch (ClassNotFoundException e1) {
    		e1.printStackTrace();
    	} catch (SQLException e2) {
    		e2.printStackTrace();
    	}
    }
    
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) 
            throws ServletException, IOException {
    	String hashtag = request.getParameter("hashtag");
    	String N = request.getParameter("N");
    	String list_of_key_words = request.getParameter("list_of_key_words");
    	keywordSet = getKeyWordList(list_of_key_words);
    	
    	String result = "";
    	if (hashtag == null) {
       		result = TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n";
       	} else {
       		String column = queryFromMySQL(hashtag, N, list_of_key_words);
        	try {
    			String output = parse(column, Integer.parseInt(N));
    			result = TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n" + output + "\n";
    		} catch (ParseException e) {
    			e.printStackTrace();
    		}
       	}
    	
    	PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
    	writer.write(result.toString());
        writer.close();
    }
    
    private String parse(String input, Integer N) throws ParseException {
    	HashMap<String, Integer> map = new HashMap<>(); // userId, sum
    	JSONParser jsonParser = new JSONParser();
    	Object obj = jsonParser.parse(input);
    	JSONArray array = (JSONArray) obj;
    	for (int i = 0; i < array.size(); i++) {
    		JSONObject jsonObj = (JSONObject) array.get(i);
    		JSONObject textObj = (JSONObject) jsonObj.get("text");
    		int value = getSum(textObj);
    		String useridObj = (String) jsonObj.get("userid");
    		map.put(useridObj, value);
    	}
    	
    	// sort
    	Set<Entry<String, Integer>> set = map.entrySet();
    	ArrayList<Entry<String, Integer>> list = new ArrayList<>(set);
    	list.sort(new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				int tmp = o2.getValue().compareTo(o1.getValue());
				if (tmp == 0) {
					tmp = o1.getKey().compareTo(o2.getKey());
				}
				return tmp;
			}		
    	});
    	
    	StringBuilder sb = new StringBuilder();
    	int number = list.size() < N ? list.size() : N;
    	for (int i = 0; i < number - 1; i++) {
			String userId = list.get(i).getKey();
			Integer sum = list.get(i).getValue();
			sb.append(userId + ":" + sum + ",");
		}
		sb.append(list.get(list.size() - 1).getKey() + ":" + list.get(list.size() - 1).getValue());
		
    	return sb.toString();
    }
    
    private static int getSum(JSONObject jObject) {
    	int sum = 0;
    	Iterator<?> keys = (Iterator<?>) jObject.keySet();
  
    	while (keys.hasNext()) {
    		String key = (String) keys.next();
    		if (keywordSet.contains(key)) {
    			Integer value = (Integer) jObject.get(key);
    			sum += value;  			
    		}
    	}
    	return sum;
    }
    
    // TODO
    private String queryFromMySQL(String hashtag, String N, String list_of_key_words) throws ServletException, IOException {
       	String result = ""; //TODO
    	String sql = ""; //TODO
    	PreparedStatement statement = null;
    	ResultSet resultSet = null;    	
    	try {
    		statement = connection.prepareStatement(sql);
    		//TODO
    	} catch (SQLException e) {
    		e.printStackTrace();
    	} finally {
        	try {
				resultSet.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
    	return result;
    }

    
    private static HashSet<String> keywordSet = null;
    private static HashSet<String> getKeyWordList(String list) {
    	HashSet<String> result = new HashSet<>();
    	for (String str : list.split(",")) {
    		result.add(str);
    	}
    	return result;
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}





