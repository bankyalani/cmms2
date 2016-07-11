package com.nibss.cmms.domain;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.nibss.cmms.web.WebAppConstants;

@Entity
@Table(name="api_transactions")
@DiscriminatorValue(WebAppConstants.TRANSACTION_API)
public class APITransaction extends Transaction{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5144251956488048464L;

	private String batchId;
	
	@ManyToOne
	private User createdBy;
	
	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

}
