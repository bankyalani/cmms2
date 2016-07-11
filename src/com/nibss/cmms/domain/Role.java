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
@Table(name = "ROLES")
public class Role implements Serializable {

	private static final long serialVersionUID = 200900302302L;

	// role id
	private Long			  id;

	// role name
	private String			name;

	// role description
	private String			description;

	@Id
	@Column(name = "ROLE_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId( Long id ) {
		this.id = id;
	}

	@Column(name = "ROLE_NAME", nullable = false)
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	@Column(name = "ROLE_DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

}
