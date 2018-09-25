/**
 *
 */
package com.nibss.cmms.domain;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.nibss.cmms.web.WebAppConstants;

@Entity
@Table(name = "BANK_NOTIFICATION")
@DiscriminatorValue(WebAppConstants.BANK_NOTIFICATION)
public class BankNotification extends Notification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1449567099744083667L;


	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JoinColumn(referencedColumnName ="bankCode")
	private Bank bank;

	public Bank getBank() {
		return bank;
	}


	public void setBank(Bank bank) {
		this.bank = bank;
	}


}