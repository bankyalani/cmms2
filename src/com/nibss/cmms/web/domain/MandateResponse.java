package com.nibss.cmms.web.domain;

import java.io.Serializable;

public class MandateResponse implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3401620666210279255L;

	private String responseCode;

	private String mandateCode;
	
	private String responseDescription;

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getMandateCode() {
		return mandateCode;
	}

	public void setMandateCode(String mandateCode) {
		this.mandateCode = mandateCode;
	}

	public String getResponseDescription() {
		return responseDescription;
	}

	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}
	
	

}
