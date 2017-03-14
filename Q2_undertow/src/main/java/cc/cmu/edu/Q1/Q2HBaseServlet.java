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

public class Q2HBaseServlet extends HttpServlet {
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
		return null;
	}
	
	@Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
	
}

















