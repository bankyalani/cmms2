package com.nibss.cmms.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OrderBy;

import com.nibss.cmms.web.WebAppConstants;

@Entity
@Table(name="TRANSACTIONS")
@DiscriminatorValue(WebAppConstants.TRANSACTION_APP)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "transaction_type", discriminatorType = DiscriminatorType.INTEGER)	
public class Transaction  implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4999444828755215934L;

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private BigDecimal amount;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName ="mandateCode")
	private Mandate mandate;
	
	@Column(name="transaction_type")
	private int transactionType;//used to discriminate between local or API transaction
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "transaction")
	@OrderBy(clause="id DESC")
	private Set<TransactionParam> transactionParam=new HashSet<>();
	
	private int numberOfCreditTrials;
	
	public int getNumberOfCreditTrials() {
		return numberOfCreditTrials;
	}

	public void setNumberOfCreditTrials(int numberOfCreditTrials) {
		this.numberOfCreditTrials = numberOfCreditTrials;
	}

	private int numberOfTrials;
	
	private Date dateCreated;
	
	private Date lastDebitDate;
	
	private Date lastCreditDate;
		
	public Date getLastCreditDate() {
		return lastCreditDate;
	}

	public void setLastCreditDate(Date lastCreditDate) {
		this.lastCreditDate = lastCreditDate;
	}

	private int status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Mandate getMandate() {
		return mandate;
	}

	public void setMandate(Mandate mandate) {
		this.mandate = mandate;
	}

	public int getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Set<TransactionParam> getTransactionParam() {
		return transactionParam;
	}

	public void setTransactionParam(Set<TransactionParam> transactionParam) {
		this.transactionParam = transactionParam;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public int getNumberOfTrials() {
		return numberOfTrials;
	}

	public void setNumberOfTrials(int numberOfTrials) {
		this.numberOfTrials = numberOfTrials;
	}

	public Date getLastDebitDate() {
		return lastDebitDate;
	}

	public void setLastDebitDate(Date lastDebitDate) {
		this.lastDebitDate = lastDebitDate;
	}
	
	
	
	
	
	

}
