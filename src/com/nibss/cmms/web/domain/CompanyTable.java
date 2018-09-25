package com.nibss.cmms.web.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyTable implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 596632303921838035L;

	@JsonProperty("name")
	private String companyName;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("rcNumber")
	private String rcNumber;
	
	@JsonProperty("status.id")
	private String status;
	
	@JsonProperty("createdBy.id")
	private String createdBy;
	
	@JsonProperty("dateCreated")
	private String dateCreated;
	
	@JsonProperty("industry.id")
	private Long industry;
	
	@JsonProperty("industry.name")
	private String industryName;
	
	@JsonProperty(value = "DT_RowId")
	private Long id;

	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRcNumber() {
		return rcNumber;
	}

	public void setRcNumber(String rcNumber) {
		this.rcNumber = rcNumber;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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
	
}
