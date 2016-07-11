package com.nibss.cmms.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.Email;

@Entity
@Table(
	    name="Notification", 
	    uniqueConstraints=
	        @UniqueConstraint(columnNames={"mandate_status_id","emailAddress"})
	)
@DiscriminatorValue("0")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "notification_type", discriminatorType = DiscriminatorType.INTEGER)	
public class Notification extends AbstractTimeStampEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2972797933716386867L;
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="mandate_status_id")
	private MandateStatus mandateStatus;
	
	
	@Column(name="notification_type")
	private int notificationType;
	
	@Email
	@Column(nullable=false, name="emailAddress")
	private String emailAddress;
	
	
	@ManyToOne
	private User createdBy;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public MandateStatus getMandateStatus() {
		return mandateStatus;
	}

	public void setMandateStatus(MandateStatus mandateStatus) {
		this.mandateStatus = mandateStatus;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	

}
