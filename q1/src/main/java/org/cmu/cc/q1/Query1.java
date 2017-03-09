package org.cmu.cc.q1;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/")
public class Query1 {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String print(@Context HttpServletRequest request) {
		String key = request.getParameter("key");
		String message = request.getParameter("message");
		String plainText = "";
		if (key == null || message == null) {
			plainText = "INVALID";
			return getResponse(plainText);
		}
		
		
		PDC pdc = new PDC();
		boolean isValid = pdc.isValidMessage(message);
		if (!isValid) {
			plainText = "INVALID";
		} else {
			plainText = pdc.decryption(key, message);
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
