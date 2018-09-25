/**
 * 
 */
package com.nibss.cmms.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "REJECTION_REASONS")
public class RejectionReason implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6679000931132879296L;

	// role id
	private Long			  id;

	// role name
	private String			name;

	// role description
	private String			description;

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

}
