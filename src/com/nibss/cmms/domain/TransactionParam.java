package com.nibss.cmms.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="TRANSACTION_PARAMS")
@Entity
public class TransactionParam implements Serializable{

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

	@ManyToOne
	private Transaction transaction;


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

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
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
