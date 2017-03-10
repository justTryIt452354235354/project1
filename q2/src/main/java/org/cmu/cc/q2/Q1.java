package org.cmu.cc.q2;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/q2")
public class Q1 {
	
	private static String TEAM_ID = "let's go husky";
	private static String TEAM_AWS_ACCOUNT_ID = "368196891489";
	
	@GET
    @Produces(MediaType.TEXT_PLAIN)
    public String print(@QueryParam("key") String key, @QueryParam("message") String message) {
		String key1 = key;
		String message1 = message;
		System.out.println("key: " + key1);
		System.out.println("message: " + message1);
		
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

	public String getResponse(String plainText) {
		StringBuilder sb = new StringBuilder();
		String time = "";
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = simpleFormat.format(new Date());
		sb.append(TEAM_ID).append(",").append(TEAM_AWS_ACCOUNT_ID)
			.append("\n").append(time)
			.append("\n").append(plainText).append("\n");
		return sb.toString();
	}
}
