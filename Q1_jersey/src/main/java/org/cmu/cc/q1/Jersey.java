package org.cmu.cc.q1;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/q1")
public class Jersey {
	private final String TEAM_AWS_ACCOUNT_ID = "368196891489";
    private final String TEAM_ID  = "let's go husky";
    private final String X = "12389084059184098308123098579283204880956800909293831223134798257496372124879237412193918239183928140";
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String print(@QueryParam("key") String key, @QueryParam("message") String message) {
    	String y = key;
    	String c = message;
    	
    	String decryptedMessage = decryption(y, c);
    	StringBuilder result = new StringBuilder();
        result.append(TEAM_ID).append(",").append(TEAM_AWS_ACCOUNT_ID).append("\n");
        result.append(dateTime()).append("\n");
        result.append(decryptedMessage).append("\n");
        
        //System.out.println("=====Result: \n" + result.toString());
        return result.toString();
 	
    }
    
    protected String dateTime() {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleFormat.format(new Date());
    }
    
    public String key_gen(String x, String y) {
        String result = "";
        int length = x.length() - y.length() + 1;
        int total = 0;
        for (int i = 0; i < length; i++) {
            total += x.charAt(i) - '0';
        }
        for (int i = 0; i < y.length(); i++) {
            if (i == 0) {
                result += (char) (y.charAt(i) + total - '0') % 10;
            } else {
                total = total - (x.charAt(i-1) -'0') + (x.charAt(i + length - 1) - '0');
                result += (char) (y.charAt(i) + total - '0') % 10;  
            }
        }
        return result;
    }
    
    public String reverseTriangle(String c, String z) {
        BigInteger num = new BigInteger(z);
        int k = num.remainder(new BigInteger("25")).intValue() + 1;
        int length = c.length();
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
                        arr[sideLength-round-1][j+round] = (char)((c.charAt(index++) - 65 - k + 26)%26 + 'A');
                    }
                } else if (side == 1) {
                    for (int j = sideLength - round - 2; j >= sideLength - round - 1 - n; j--) {
                        arr[j][j-round] = (char)((c.charAt(index++) - 65 - k + 26)%26 + 'A');
                    }
                } else {
                    for (int j = sideLength - round - 2 - n; j <= sideLength - round - 3; j++) {
                        arr[j+1][round] = (char)((c.charAt(index++) - 65 - k + 26)%26 + 'A');
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
    
    public String decryption(String y, String c) {
        String z = key_gen(X, y);
        String m = reverseTriangle(c, z);
        return m;
    }
}
