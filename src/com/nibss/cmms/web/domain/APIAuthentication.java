package com.nibss.cmms.web.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotBlank;

public class APIAuthentication implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6415111222879851832L;

	@NotBlank(message="Authentication Username is required")
	private String username;
	
	@NotBlank(message="Authentication password is required")
	private String password;
	
	@NotBlank(message="Authentication API Key is required")
	private String apiKey;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
}
