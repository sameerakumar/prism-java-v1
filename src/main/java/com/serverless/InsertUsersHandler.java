package com.serverless;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.data.UserModel;
import com.serverless.data.Users;
import com.serverless.db.DynamoDBAdapter;

public class InsertUsersHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
	private static final Logger LOG = LogManager.getLogger(InsertUsersHandler.class);
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
	    LOG.info("received: " + input);
	    String filename = "";
	    try{
	      ObjectMapper mapper = new ObjectMapper();
	      JsonNode body = mapper.readTree((String) input.get("body"));
	      byte[] bI = DynamoDBAdapter.decodeImage(body.get("imageFile").asText());
	  	  InputStream fis = new ByteArrayInputStream(bI);
   		  AmazonS3 s3 = new AmazonS3Client();
	  			Region cnNorth1 = Region.getRegion(Regions.CN_NORTH_1);
	  			s3.setRegion(cnNorth1);
	  			ObjectMetadata metadata = new ObjectMetadata();
	  			metadata.setContentLength(bI.length);
	  			metadata.setContentType("image/png");
	  			metadata.setCacheControl("public, max-age=31536000");
	  			
	  	        String possible1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	  	        for (int i = 0; i < 10; i++) {
	  	        	filename += possible1.charAt((int) Math.floor(Math.random() * possible1.length()));
	  	        }
	  	        System.out.println("pin:" + filename);
	  	        
	  	  s3.putObject("prismusers", filename+".png", fis, metadata);
	  	  s3.setObjectAcl("prismusers", filename+".png", CannedAccessControlList.PublicRead);
	      String userid = body.get("userId").asText();
	      String address = body.get("address").asText();
	      String dob = body.get("dob").asText();
	      String email = body.get("email").asText();
	      String firstname = body.get("firstName").asText();
	      String gender = body.get("gender").asText();
	      String height = body.get("height").asText();
	      String lastname = body.get("lastName").asText();
	      String phone = body.get("phone").asText();
	      String weight = body.get("weight").asText();

	      Users tx = new Users();
	      tx.setAddress(address);
	      tx.setDob(dob);
	      tx.setDpUrl("https://s3.cn-north-1.amazonaws.com.cn/prismusers/"+filename+".png");
	      tx.setEmail(email);
	      tx.setFirstName(firstname);
	      tx.setGender(gender);
	      tx.setHeight(height);
	      tx.setLastName(lastname);
	      tx.setPhone(phone);
	      tx.setWeight(weight);
	      tx.setUserId(userid);
	      DynamoDBAdapter.getInstance().putUsers(tx);
	    } catch(Exception e){
	      LOG.error(e,e);
	      UserModel um = new UserModel();
		    um.setResponse(0);
		    um.setMessage("please pass correct params");
	      return ApiGatewayResponse.builder()
	      .setStatusCode(500)
	      .setObjectBody(um)
	      .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
	      .build();
	    }
	    UserModel um = new UserModel();
	    um.setResponse(3);
	    um.setMessage("user information successfully inserted");
	    um.setProfileUrl("https://s3.cn-north-1.amazonaws.com.cn/prismusers/"+filename+".png");
	    return ApiGatewayResponse.builder()
	    .setStatusCode(200)
	    .setObjectBody(um)
	    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
	    .build();
	}

}
