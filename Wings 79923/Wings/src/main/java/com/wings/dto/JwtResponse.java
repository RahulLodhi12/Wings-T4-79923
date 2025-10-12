package com.wings.dto;

public class JwtResponse {
	private String accessToken;
	private int status;
	
	public JwtResponse() {
		
	}
	
	public JwtResponse(String accessToken, int status) {
		this.accessToken = accessToken;
		this.status = status;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
}
