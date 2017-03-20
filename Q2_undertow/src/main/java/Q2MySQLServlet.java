import java.math.BigInteger;
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
    private static final String DB_NAME = "q2";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PWD = "husky2017"; 
    private static final String TEAM_ID = "let's go husky";
    private static final String TEAM_AWS_ACCOUNT_ID = "368196891489";
	private static HashSet<String> keywordSet = null;
    
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
    	keywordSet = getKeyWordList(list_of_key_words);//得到全部要求查找的keyword
		//System.out.println("-------" + hashtag);
      	
    	String result = "";
    	if (hashtag == null || hashtag.isEmpty()) {
       		result = TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n";
       	} else {
       		String text = queryFromMySQL(hashtag);//由tag得到jsonarray
            //System.out.println(text);
        	if (text != null && !text.isEmpty()) {
        		try {
        			String output = parse(text, Integer.parseInt(N));
        			result = TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n" + output + "\n";
        		} catch (ParseException e) {
        			System.err.print("can`t be parsed");
        		}
        	} else {
        		result = TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n";
        	}
       		
       	}
    	
    	PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
    	System.out.println(result.toString());
    	writer.write(result.toString());
        writer.close();
    }
    
    private String parse(String input, Integer N) throws ParseException { //从选择出来的text中提取所有的word和freq配对
    	HashMap<String, Integer> map = new HashMap<>(); // userId, sum
    	JSONParser jsonParser = new JSONParser();
    	Object obj = jsonParser.parse(input);
    	JSONArray array = (JSONArray) obj;
    	for (int i = 0; i < array.size(); i++) {
    		JSONObject jsonObj = (JSONObject) array.get(i);
    		JSONObject textObj = (JSONObject) jsonObj.get("text"); //{"text": "word":1 "word2":2, {"userid" : 234234}}
    		int value = getSum(textObj);
    		String useridObj = (String) jsonObj.get("userid");
    		map.put(useridObj, map.getOrDefault(useridObj, 0) + value);
    	}
    	
    	// sort
    	Set<Entry<String, Integer>> set = map.entrySet();
    	ArrayList<Entry<String, Integer>> list = new ArrayList<>(set);
    	list.sort((o1, o2) -> {
            int tmp = o2.getValue().compareTo(o1.getValue());
            if (tmp == 0) {
                tmp = new BigInteger(o1.getKey()).compareTo(new BigInteger(o2.getKey()));
            }
            return tmp;
        });
    	
    	StringBuilder sb = new StringBuilder();
    	int number = list.size() < N ? list.size() : N;
    	for (int i = 0; i < number; i++) {
			String userId = list.get(i).getKey();
			Integer sum = list.get(i).getValue();
			if (i == number - 1) {
			    sb.append(userId + ":" + sum);
            } else {
                sb.append(userId + ":" + sum + ",");
            }
		}
		//sb.append(list.get(list.size() - 1).getKey() + ":" + list.get(list.size() - 1).getValue());
		
    	return sb.toString();
    }
    
    private static int getSum(JSONObject jObject) { //计算和
		int sum = 0;

		for (Object key : jObject.keySet()) {
			if (keywordSet.contains(String.valueOf(key).toLowerCase())) {
				int value = Integer.parseInt(jObject.get(key).toString());
				sum += value;
			}
		}
		return sum;
    }
    
    private String queryFromMySQL(String hashtag) throws ServletException, IOException { // 从数据库中选出tag对应的所有text然后交给下一步处理

    	String sql = "select text from q2table where hashtag = '" + hashtag + "' AND BINARY(hashtag) = BINARY('" + hashtag + "')";
    	Statement statement = null;
    	ResultSet resultSet = null;
    	try {
    		statement = connection.createStatement();
    		resultSet = statement.executeQuery(sql);
    		if (resultSet == null) return null;
    		if (resultSet.next()) {
    			return resultSet.getString(1);
    		}
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
    	return null;
    }

    

    private static HashSet<String> getKeyWordList(String list) { // 从list里面得到所有需要找的word
    	HashSet<String> result = new HashSet<>();
    	for (String str : list.split(",")) {
    		result.add(str.toLowerCase());
    	}
    	return result;
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}





