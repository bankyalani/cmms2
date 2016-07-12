package com.nibss.cmms.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "Billing_Transactions")
@Entity
public class BillingTransaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1714262226337033210L;

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String sessionId;

	private String creditResponseCode;

	private String debitResponseCode;

	private Date dateCreated;

	public long getMandateId() {
		return mandateId;
	}

	public void setMandateId(long mandateId) {
		this.mandateId = mandateId;
	}

	public long getBillerId() {
		return billerId;
	}

	public void setBillerId(long billerId) {
		this.billerId = billerId;
	}

	public BigDecimal getBilledAmount() {
		return billedAmount;
	}

	public void setBilledAmount(BigDecimal billedAmount) {
		this.billedAmount = billedAmount;
	}

	public BigDecimal getUnitFee() {
		return unitFee;
	}

	public void setUnitFee(BigDecimal unitFee) {
		this.unitFee = unitFee;
	}

	public int getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(int transactionCount) {
		this.transactionCount = transactionCount;
	}

	private long mandateId;
	private long billerId;
	private BigDecimal billedAmount;
	private BigDecimal unitFee;
	private int transactionCount;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreditResponseCode() {
		return creditResponseCode;
	}

	public void setCreditResponseCode(String creditResponseCode) {
		this.creditResponseCode = creditResponseCode;
	}

	public String getDebitResponseCode() {
		return debitResponseCode;
	}

	public void setDebitResponseCode(String debitResponseCode) {
		this.debitResponseCode = debitResponseCode;
	}
}
