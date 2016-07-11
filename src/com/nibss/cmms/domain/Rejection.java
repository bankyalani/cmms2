/**
 *
 */
package com.nibss.cmms.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



@Entity
@Table(name = "REJECTIONS")
public class Rejection implements Serializable {

  
    
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -7636940814958092419L;

	private Long id;
    
    private String comment;
    
    private User user;
    
    private RejectionReason rejectionReason;
        
    private Date dateRejected;
    
    
    @Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
	public Date getDateRejected() {
		return dateRejected;
	}

	public void setDateRejected(Date dateRejected) {
		this.dateRejected = dateRejected;
	}

	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	public RejectionReason getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(RejectionReason rejectionReason) {
		this.rejectionReason = rejectionReason;
	}
    
    
}
