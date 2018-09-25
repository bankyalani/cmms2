package com.nibss.cmms.web.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BillerTable implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 596632303921838035L;

	@JsonProperty("bank.bankName")
	private String bankName;
	
	@JsonProperty("accountNumber")
	private String accountNumber;
	
	@JsonProperty("status.id")
	private String status;
	
	@JsonProperty("createdBy.id")
	private String createdBy;
	
	@JsonProperty("dateCreated")
	private String dateCreated;
	
	@JsonProperty("biller.id")
	private Long billerId;
	
	@JsonProperty("bank.bankCode")
	private String bankCode;
	
	@JsonProperty("company.industry.id")
	private Long industry;
	
	@JsonProperty("company.industry.name")
	private String industryName;
	
	@JsonProperty("company.name")
	private String companyName;
	
	@JsonProperty("company.rcNumber")
	private String rcNumber;
	
	@JsonProperty(value = "DT_RowId")
	private Long id;


	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Long getBillerId() {
		return billerId;
	}

	public void setBillerId(Long billerId) {
		this.billerId = billerId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIndustry() {
		return industry;
	}

	public void setIndustry(Long industry) {
		this.industry = industry;
	}

	public String getIndustryName() {
		return industryName;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRcNumber() {
		return rcNumber;
	}

	public void setRcNumber(String rcNumber) {
		this.rcNumber = rcNumber;
	}
	
}
