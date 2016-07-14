package com.nibss.cmms.web.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MandateTable implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4449858013596891110L;

	
	private String mandateCode;
	
	@JsonProperty("status.id") 
	private String status;
	
	@JsonProperty("product.biller.id")
	private String billerName;
	
	private String subscriberCode;
	
	@JsonProperty("product.id")
	private String productName;
	
	private String amount;
	
	private String debitFrequency;
	
	private String nextDebitDate;
	
	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	private String lastActionBy;
	
	@JsonProperty("dateCreated")
	private String dateAdded;
	
	private String debitStartDate;
	
	private String debitEndDate;
	
	@JsonProperty("bank.bankCode")
	private String bank;
	
	private String dateApproved;

	private Long id;
	
	private boolean fixedAmountMandate;
	
	@JsonProperty("requestStatus")
	private String requestStatus;
	private String payerName;
	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	public String getMandateCode() {
		return mandateCode;
	}

	public void setMandateCode(String mandateCode) {
		this.mandateCode = mandateCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBillerName() {
		return billerName;
	}

	public void setBillerName(String billerName) {
		this.billerName = billerName;
	}

	public String getSubscriberCode() {
		return subscriberCode;
	}

	public void setSubscriberCode(String subscriberCode) {
		this.subscriberCode = subscriberCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDebitFrequency() {
		return debitFrequency;
	}

	public void setDebitFrequency(String debitFrequency) {
		this.debitFrequency = debitFrequency;
	}

	public String getNextDebitDate() {
		return nextDebitDate;
	}

	public void setNextDebitDate(String nextDebitDate) {
		this.nextDebitDate = nextDebitDate;
	}

	public String getLastActionBy() {
		return lastActionBy;
	}

	public void setLastActionBy(String lastActionBy) {
		this.lastActionBy = lastActionBy;
	}

	public String getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDebitStartDate() {
		return debitStartDate;
	}

	public void setDebitStartDate(String debitStartDate) {
		this.debitStartDate = debitStartDate;
	}

	public String getDebitEndDate() {
		return debitEndDate;
	}

	public void setDebitEndDate(String debitEndDate) {
		this.debitEndDate = debitEndDate;
	}

	public String getDateApproved() {
		return dateApproved;
	}

	public void setDateApproved(String dateApproved) {
		this.dateApproved = dateApproved;
	}

	public boolean isFixedAmountMandate() {
		return fixedAmountMandate;
	}

	public void setFixedAmountMandate(boolean fixedAmountMandate) {
		this.fixedAmountMandate = fixedAmountMandate;
	}
	
	
}
