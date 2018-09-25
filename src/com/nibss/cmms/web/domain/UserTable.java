package com.nibss.cmms.web.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6155576885578434009L;

	@JsonProperty(value = "firstName")
	private String firstName;
	
	@JsonProperty(value = "lastName")
	private String lastName;
	
	@JsonProperty(value = "status.id")
	private String status;
	
	@JsonProperty(value = "email")
	private String email;
	
	@JsonProperty(value = "organization")
	private String Organization;
	
	@JsonProperty(value = "role.name")
	private String roleName;
	
	@JsonProperty(value = "role.id")
	private Long roleId;
	
	@JsonProperty(value = "dateCreated")
	private String dateCreated;
	
	@JsonProperty(value = "biller.id")
	private Long billerId;
	
	@JsonProperty(value = "bank.bankCode")
	private String bankCode;
	
	@JsonProperty(value = "DT_RowId")
	private Long id;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

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

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	
	

}
