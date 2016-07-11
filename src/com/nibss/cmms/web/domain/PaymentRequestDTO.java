package com.nibss.cmms.web.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3013818701877111974L;
	

	@Pattern(regexp = "\\w*/[0-9]{3}/[0-9]{10}",message="Invalid Mandate Code")
	@NotBlank(message="Mandate Code is mandatory")
	private String mandateCode;
	
	@NotBlank(message="Subscriber Code is mandatory")
	private String subscriberCode;
	
	//used for the response
	private String responseCode;

	private BigDecimal amount;
	
	@JsonProperty(required=false)
	private String narration;

	public String getMandateCode() {
		return mandateCode;
	}

	public void setMandateCode(String mandateCode) {
		this.mandateCode = mandateCode;
	}

	public String getSubscriberCode() {
		return subscriberCode;
	}

	public void setSubscriberCode(String subscriberCode) {
		this.subscriberCode = subscriberCode;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}	
}
