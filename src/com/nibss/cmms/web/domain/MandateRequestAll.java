package com.nibss.cmms.web.domain;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;


public class MandateRequestAll{
	
	@Valid
	@JsonProperty("auth")
	@NotNull(message="Authentication is required")
	private APIAuthentication apiAuthentication;
	

	
	public APIAuthentication getApiAuthentication() {
		return apiAuthentication;
	}

	public void setApiAuthentication(APIAuthentication apiAuthentication) {
		this.apiAuthentication = apiAuthentication;
	}
	@Valid
	@JsonProperty("subscriberCodes")
@NotEmpty(message="At least one subscriber code is required")
	private List<String> subscriberCodes;



	public List<String> getSubscriberCodes() {
		return subscriberCodes;
	}

	public void setSubscriberCodes(List<String> subscriberCodes) {
		this.subscriberCodes = subscriberCodes;
	}
	
}

