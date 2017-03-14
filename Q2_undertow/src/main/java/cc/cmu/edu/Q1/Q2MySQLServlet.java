package cc.cmu.edu.Q1;

import java.sql.DriverManager;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



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
    	String result = "";
    	
    	String hashtag = request.getParameter("hashtag");
    	String N = request.getParameter("N");
    	String list_of_key_words = request.getParameter("list_of_key_words");
    	 	
    	result = queryFromMySQL(hashtag, N, list_of_key_words);
    	
    	PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);
    	writer.write(result.toString());
        writer.close();
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
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}






