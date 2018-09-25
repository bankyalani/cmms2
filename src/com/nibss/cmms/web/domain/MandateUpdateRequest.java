package com.nibss.cmms.web.domain;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;


public class MandateUpdateRequest{
	
	@Valid
	@JsonProperty("auth")
	@NotNull(message="Authentication is required")
	private APIAuthentication apiAuthentication;
	
	@Valid
	@JsonProperty("mandateUpdateRequests")
	@NotEmpty(message="At least one record is required")
	private List<MandateUpdateRequestDTO> mandateRequestBean;

	
	public APIAuthentication getApiAuthentication() {
		return apiAuthentication;
	}

	public void setApiAuthentication(APIAuthentication apiAuthentication) {
		this.apiAuthentication = apiAuthentication;
	}

	public List<MandateUpdateRequestDTO> getMandateRequestBean() {
		return mandateRequestBean;
	}

	public void setMandateRequestBean(List<MandateUpdateRequestDTO> mandateRequestBean) {
		this.mandateRequestBean = mandateRequestBean;
	}
	
}

