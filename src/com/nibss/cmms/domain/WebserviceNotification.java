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

@Table(name = "Webservice_Notification")
@Entity
public class WebserviceNotification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1714262226337033210L;

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int bankId;

	private int billerId;

	private int notificationType;
	private Long mandateId;
	private int bankNotified;
	private int billerNotified;
	private int billerNotificationCounter;
	private int bankNotificationCounter;

	public int getBillerNotificationCounter() {
		return billerNotificationCounter;
	}

	public void setBillerNotificationCounter(int billerNotificationCounter) {
		this.billerNotificationCounter = billerNotificationCounter;
	}

	public int getBankNotificationCounter() {
		return bankNotificationCounter;
	}

	public void setBankNotificationCounter(int bankNotificationCounter) {
		this.bankNotificationCounter = bankNotificationCounter;
	}

	private Date dateCreated;

	public int getBankId() {
		return bankId;
	}

	public void setBankId(int bankId) {
		this.bankId = bankId;
	}

	public int getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}

	public int getBankNotified() {
		return bankNotified;
	}

	public void setBankNotified(int bankNotified) {
		this.bankNotified = bankNotified;
	}

	public int getBillerNotified() {
		return billerNotified;
	}

	public void setBillerNotified(int billerNotified) {
		this.billerNotified = billerNotified;
	}

	public void setBillerId(int billerId) {
		this.billerId = billerId;
	}

	public void setMandateId(Long mandateId) {
		this.mandateId = mandateId;
	}

	public long getMandateId() {
		return mandateId;
	}

	public void setMandateId(long mandateId) {
		this.mandateId = mandateId;
	}

	public long getBillerId() {
		return billerId;
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



}
