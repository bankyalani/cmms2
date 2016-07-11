package com.nibss.cmms.web.domain;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentStatusRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 820175678489421944L;
	

	@Valid
	@JsonProperty("auth")
	@NotNull(message="Authentication is required")
	private APIAuthentication apiAuthentication;
	
	@Valid
	@JsonProperty("mandateCodes")
	@NotEmpty(message="At least one record is required")
	private List<String> mandateCodes;

	
	public APIAuthentication getApiAuthentication() {
		return apiAuthentication;
	}

	public void setApiAuthentication(APIAuthentication apiAuthentication) {
		this.apiAuthentication = apiAuthentication;
	}

	public List<String> getMandateCodes() {
		return mandateCodes;
	}

	public void setMandateCodes(List<String> mandateCodes) {
		this.mandateCodes = mandateCodes;
	}


}
