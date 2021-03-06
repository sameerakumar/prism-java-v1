package com.serverless.data;

import java.util.Map;

public class NotificationModel {
	
	private String message;
	private int response;
	private Map<String, Object> data;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getResponse() {
		return response;
	}
	
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public void setResponse(int response) {
		this.response = response;
	}
	
}
