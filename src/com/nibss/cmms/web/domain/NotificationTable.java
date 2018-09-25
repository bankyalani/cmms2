package com.nibss.cmms.web.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6155576885578434009L;

	@JsonProperty(value = "status.description")
	private String statusDescription;
	
	@JsonProperty(value = "status.id")
	private String statusName;
	
	@JsonProperty(value = "emailAddress")
	private String email;
	
	@JsonProperty(value = "organization")
	private String Organization;
	
	private String mailDescription;
	
	@JsonProperty(value = "dateCreated")
	private String dateCreated;
	
	@JsonProperty(value = "biller.id")
	private Long billerId;
	
	@JsonProperty(value = "bank.bankCode")
	private String bankCode;
	
	@JsonProperty(value = "DT_RowId")
	private Long id;

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOrganization() {
		return Organization;
	}

	public void setOrganization(String organization) {
		Organization = organization;
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

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getMailDescription() {
		return mailDescription;
	}

	public void setMailDescription(String mailDescription) {
		this.mailDescription = mailDescription;
	}
	
	

}
