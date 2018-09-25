package com.nibss.cmms.web.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PaymentResponse  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6422574807415136491L;
	
	private List<PaymentRequestDTO> paymentRequestBean;
	
	private String batchId;

	@JsonProperty("paymentResponse")
	public List<PaymentRequestDTO> getPaymentRequestBean() {
		return paymentRequestBean;
	}


	public void setPaymentRequestBean(List<PaymentRequestDTO> paymentRequestBean) {
		this.paymentRequestBean = paymentRequestBean;
	}


	public String getBatchId() {
		return batchId;
	}


	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	
}

