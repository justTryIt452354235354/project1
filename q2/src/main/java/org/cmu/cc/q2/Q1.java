package org.cmu.cc.q2;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/q1")
public class Q1 {
	@GET
    @Produces(MediaType.TEXT_PLAIN)
    public String print(@QueryParam("key") String key, @QueryParam("message") String message) {
//		String key = request.getParameter("key");
//		String message = request.getParameter("message");
		String key1 = key;
		String message1 = message;
		
		String plainText = "";
		if (key1 == null || message1 == null) {
			plainText = "INVALID";
			return getResponse(plainText);
		}
		
		
		PDC pdc = new PDC();
		boolean isValid = pdc.isValidMessage(message1);
		if (!isValid) {
			plainText = "INVALID";
		} else {
			plainText = pdc.decryption(key1, message1);
		}
		return getResponse(plainText);
    }
	
	private static String TEAM_ID = System.getenv("teamID");
	private static String TEAM_AWS_ACCOUNT_ID = System.getenv("awsID");
	public String getResponse(String plainText) {
		StringBuilder sb = new StringBuilder();
		String time = "";
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = simpleFormat.format(new Date());
		sb.append(TEAM_ID).append(",").append(TEAM_AWS_ACCOUNT_ID)
			.append("\n").append(time)
			.append("\n").append(plainText);
		return sb.toString();
	}
}
