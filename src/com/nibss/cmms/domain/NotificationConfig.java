package com.nibss.cmms.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="notification_config")
public class NotificationConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9065477443113828070L;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	

	@OneToOne(optional=false)
	@JoinColumn(unique=true) 
	private MandateStatus mandateStatus;
	
	private int billerAllowedCount;
	
	private int bankAllowedCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MandateStatus getMandateStatus() {
		return mandateStatus;
	}

	public void setMandateStatus(MandateStatus mandateStatus) {
		this.mandateStatus = mandateStatus;
	}

	public int getBillerAllowedCount() {
		return billerAllowedCount;
	}

	public void setBillerAllowedCount(int billerAllowedCount) {
		this.billerAllowedCount = billerAllowedCount;
	}

	public int getBankAllowedCount() {
		return bankAllowedCount;
	}

	public void setBankAllowedCount(int bankAllowedCount) {
		this.bankAllowedCount = bankAllowedCount;
	}

	
	
	
	

}
