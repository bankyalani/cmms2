package com.nibss.cmms.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public abstract class AbstractTimeStampEntity {
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date dateCreated;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date dateModified;
	
	
	@PrePersist
    protected void onCreate() {
		dateModified = dateCreated = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
    	dateModified = new Date();
    }

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}


}
