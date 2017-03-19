package cc.cmu.edu.Q1;

import org.apache.hadoop.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.filter.*;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Q2HBaseServlet extends HttpServlet {
    private final String TEAM_AWS_ACCOUNT_ID = "368196891489";
    private final String TEAM_ID  = "let's go husky";

	private static String zkAddr = "172.31.3.88"; //TODO
	private static TableName tableName = TableName.valueOf("q2db");
	private Table relationsTable;
	private Connection conn;
	private final Logger LOGGER = Logger.getRootLogger();
	private static byte[] bColFamily = Bytes.toBytes("data"); //TODO

	private void initializeConnection() throws IOException {
		LOGGER.setLevel(Level.ERROR);
		if (!zkAddr.matches("\\d+.\\d+.\\d+.\\d+")) {
            System.out.print("Malformed HBase IP address");
            System.exit(-1);
        }
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.master", zkAddr + ":16000");
        conf.set("hbase.zookeeper.quorum", zkAddr);
        conf.set("hbase.zookeeper.property.clientport", "2181");
        conn = ConnectionFactory.createConnection(conf);
        relationsTable = conn.getTable(tableName);
	}

	public Q2HBaseServlet() throws IOException {
		initializeConnection();
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
		try {
			String result = "";
			String hashtag = request.getParameter("hashtag");
	    	String N = request.getParameter("N");
	    	String list_of_key_words = request.getParameter("list_of_key_words");

	    	result = queryFromHBase(hashtag, N, list_of_key_words);

	    	PrintWriter writer = new PrintWriter(
	                new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
	    	writer.write(result.toString());
	        writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	//TODO
	private String queryFromHBase(String hashtag, String N, String list_of_key_words) {
		HashSet<String> key_words_list = key_word_set(list_of_key_words);

		byte[] bColhashtag = Bytes.toBytes("hashtag");
        Get get = new Get(Bytes.toBytes(hashtag));
        get.addColumn(bColFamily, bColhashtag);
        Result r = linkTable.get(get);
        PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);

        if (r.isEmpty()) {
            PrintWriter writer = response.getWriter();
            writer.write(String.format("returnRes(%s)", );
            writer.close();
            StringBuilder result = new StringBuilder();
            result.append(TEAM_ID).append(",").append(TEAM_AWS_ACCOUNT_ID).append("\n");
            result.append(dateTime()).append("\n");
            result.append(decryptedMessage).append("\n");
            return;
        }

		return null;
	}

	@Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    	}

	public static HashSet<String> key_word_set(String list) {
		HashSet<String> result = new HashSet<String>();
		for (String str : list.split(",")) {
		    result.add(str);
		}
		return result;
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
        for (Object key : jObject.keySet()) {
            if (keywordSet.contains(String.valueOf(key).toLowerCase())) {
                int value = Integer.parseInt(jObject.get(key).toString());
                sum += value;
            }
        }
        return sum;
    }

}
