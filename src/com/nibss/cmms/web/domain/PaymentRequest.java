package com.nibss.cmms.web.domain;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PaymentRequest{
	
	@Valid
	@JsonProperty("auth")
	@NotNull(message="Authentication is required")
	private APIAuthentication apiAuthentication;
	
	@Valid
	@JsonProperty("paymentRequests")
	@NotEmpty(message="At least one record is required")
	private List<PaymentRequestDTO> paymentRequestBean;
	@JsonProperty("batchId")
	private String  batchId;
	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public APIAuthentication getApiAuthentication() {
		return apiAuthentication;
	}

	public void setApiAuthentication(APIAuthentication apiAuthentication) {
		this.apiAuthentication = apiAuthentication;
	}

	public List<PaymentRequestDTO> getPaymentRequestBean() {
		return paymentRequestBean;
	}

	public void setPaymentRequestBean(List<PaymentRequestDTO> paymentRequestBean) {
		this.paymentRequestBean = paymentRequestBean;
	}

	
	
}

