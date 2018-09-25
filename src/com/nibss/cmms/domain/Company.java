/**
 *
 */
package com.nibss.cmms.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "COMPANIES")
public class Company extends AbstractTimeStampEntity implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 3928461606588736160L;

	/**
	 * 
	 */

	private Long id;

    private String name;
    
    private String description;

    private int status;
      
    private String rcNumber;
    
    private User createdBy;
    
    private Industry industry;
   
    
    @Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	
	@Column(unique=true)
	public String getRcNumber() {
		return rcNumber;
	}

	public void setRcNumber(String rcNumber) {
		this.rcNumber = rcNumber;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Industry getIndustry() {
		return industry;
	}

	public void setIndustry(Industry industry) {
		this.industry = industry;
	}

    
    
    
}
