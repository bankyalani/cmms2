package com.nibss.cmms.web.domain;

import java.io.Serializable;
import java.util.Map;

public class PaymentStatusResponse implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3497824363829376655L;
	
	private String mandateCode;
	
	private Map<String,String> params;

	public String getMandateCode() {
		return mandateCode;
	}

	public void setMandateCode(String mandateCode) {
		this.mandateCode = mandateCode;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

}
