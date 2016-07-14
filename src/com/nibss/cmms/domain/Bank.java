package com.nibss.cmms.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "BANKS")
public class Bank implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8928809572705999915L;
	
	private Long id;
	
	private String bankCode;
	
	private String bankName;
	
	private String nipBankCode;
	@Column(nullable = true)
	private String notificationUrl;
	public String getNotificationUrl() {
		return notificationUrl;
	}

	public void setNotificationUrl(String notificationUrl) {
		this.notificationUrl = notificationUrl;
	}

	private Set<Mandate> mandates= new HashSet<>();
	
	

	

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(unique = true, nullable = false, length = 4)
	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	
	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@OneToMany(mappedBy="bank", cascade=CascadeType.ALL)  
	public Set<Mandate> getMandates() {
		return mandates;
	}

	public void setMandates(Set<Mandate> mandates) {
		this.mandates = mandates;
	}

	public String getNipBankCode() {
		return nipBankCode;
	}

	public void setNipBankCode(String nipBankCode) {
		this.nipBankCode = nipBankCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bankCode == null) ? 0 : bankCode.hashCode());
		result = prime * result
				+ ((bankName == null) ? 0 : bankName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((nipBankCode == null) ? 0 : nipBankCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bank other = (Bank) obj;
		if (bankCode == null) {
			if (other.bankCode != null)
				return false;
		} else if (!bankCode.equals(other.bankCode))
			return false;
		if (bankName == null) {
			if (other.bankName != null)
				return false;
		} else if (!bankName.equals(other.bankName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nipBankCode == null) {
			if (other.nipBankCode != null)
				return false;
		} else if (!nipBankCode.equals(other.nipBankCode))
			return false;
		return true;
	}
	
	

	
}
