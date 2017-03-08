package cc.cmu.edu.Q1;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Q1Servlet extends HttpServlet {
        private final String TEAM_AWS_ACCOUNT_ID = "368196891489";
        private final String TEAM_ID  = "let's go husky";
        private final String X = "12389084059184098308123098579283204880956800909293831223134798257496372124879237412193918239183928140";

//    public static void main(String[] args) {
//        // TODO Auto-generated method stub
//        Q1Servlet test = new Q1Servlet();
//        //System.out.println(test.reverseTriangle("PQRSTUOJFABDGKLMNIEH"));
//        String Y = "1239793247987948712739187492308012309184023849817397189273981723912221";
//        String C = "QTGXGTHWEQENWQVKPIRFO";
//        System.out.println(test.decryption(Y,C));
//    }

//    public Q1Servlet() {
//
//    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String y = request.getParameter("key");
        String c = request.getParameter("message");

        String decryptedMessage = decryption(y,c);

        StringBuilder result = new StringBuilder();
        result.append(TEAM_ID).append(",").append(TEAM_AWS_ACCOUNT_ID).append("\n");
        result.append(dateTime()).append("\n");
        result.append(decryptedMessage);

        System.out.println(result.toString());

        PrintWriter writer = response.getWriter();
        writer.write(result.toString());
        writer.close();
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }


    protected String dateTime() {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleFormat.format(new Date());
    }

    
    public String digit_add(String a1, String a2) {
        String result = "";
        for (int i = 0; i < a1.length(); i++) {
            int a = Character.getNumericValue(a1.charAt(i));
            int b = Character.getNumericValue(a2.charAt(i));
            result += Integer.toString((a+b) % 10);
        }
        return result;
    }
    
    public String key_gen(String z, String c) {
        int n1 = z.length();
        int n2 = c.length();
        Q1Servlet test = new Q1Servlet();
        for (int i = 0; i < n1 - n2 + 1; i++) {
            c = test.digit_add(c,z.substring(i, i+n2));
        }
        return c;
    }
    
    public String caesarify(String z, String m) {
        BigInteger num = new BigInteger(z);
        int k = num.remainder(new BigInteger("25")).intValue() + 1;
        String result = "";
        for (int i = 0; i < m.length(); i++) {
            result +=  (char)((m.charAt(i) - k + 25)%90 + 'A'); 
        }
        return result;
    }
    
    public String reverseTriangle(String str) {
        int length = str.length();
        if (Math.sqrt(8*length+1) != (int)Math.sqrt(8*length+1)) {
            return "INVALID";
        }
        int sideLength = (int) (-0.5 + 0.5*Math.sqrt(1+8*length));
        char[][] arr = new char[sideLength][];
        for (int level = 0; level < sideLength; level++) {
            char[] temp = new char[level+1];
            arr[level] = temp;
        }
        int index = 0;
        int side = 0;
        int round = 0;
        int n = sideLength;
        while (n != 0) {
            for (int i = 0; i < n; i++) {
                if (side == 0) {
                    for (int j = 0; j < n; j++) {
                        arr[sideLength-round-1][j+round] = str.charAt(index++);
                    }
                } else if (side == 1) {
                    for (int j = sideLength - round - 2; j >= sideLength - round - 1 - n; j--) {
                        arr[j][j-round] = str.charAt(index++);
                    }
                } else {
                    for (int j = sideLength - round - 2 - n; j <= sideLength - round - 3; j++) {
                        arr[j+1][round] = str.charAt(index++);
                    }
                    round++;
                }
                n--;
                side++;
                side %= 3;
            }
        }
        String result = "";
        for (int level = 0; level < sideLength; level++) {
            for (int i = 0; i < level+1; i++) {
                result += arr[level][i];
            }
        }
        return result;
    }
    
    
    public String spiralizeTriangle(String str) {
        int length = str.length();
        if (Math.sqrt(8*length+1) == (int)Math.sqrt(8*length+1)) {
            String result = "";
            int sideLength = (int) (-0.5 + 0.5*Math.sqrt(1+8*length));
            char[][] arr = new char[sideLength][];
            int sum = 0;
            for (int level = 0; level < sideLength; level++) {
                char[] temp = new char[level+1];
                for (int i = 0; i < level+1; i++) {
                    temp[i] = str.charAt(sum++);
                }
                arr[level] = temp;
            }
            int side = 0;
            int round = 0;
            int n = sideLength;
            while (n != 0) {
                for (int i = 0; i < n; i++) {
                    if (side == 0) {
                        for (int j = 0; j < n; j++) {
                            result += arr[sideLength-round-1][j+round];
                        }
                    } else if (side == 1) {
                        for (int j = sideLength - round - 2; j >= sideLength - round - 1 - n; j--) {
                            result += Character.toString(arr[j][j-round]);
                        }
                    } else {
                        for (int j = sideLength - round - 2 - n; j <= sideLength - round - 3; j++) {
                            result += Character.toString(arr[j+1][round]);
                        }
                        round++;
                    }
                    n--;
                    side++;
                    side %= 3;
                }
            }
            return result;
        } else {
            return "INVALID";
        }
    }
    
    public String decryption(String y, String c) {
        //String x = "12389084059184098308123098579283204880956800909293831223134798257496372124879237412193918239183928140";
        Q1Servlet test = new Q1Servlet();
        String z = test.key_gen(X, y);
        String i = test.caesarify(z, c);
        String m = test.reverseTriangle(i);
        return m;
    }
    
    
    
    
    
}
