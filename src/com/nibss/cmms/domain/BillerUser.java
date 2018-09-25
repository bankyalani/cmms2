/**
 *
 */
package com.nibss.cmms.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.nibss.cmms.web.WebAppConstants;

@Entity
@Table(name = "BILLER_USERS")
@DiscriminatorValue(WebAppConstants.USER_TYPE_BILLER)
public class BillerUser extends User implements Serializable {

	private static final long serialVersionUID = 200900302301L;

	
	
	private Biller biller;

	/*private User user;

	private Long id;*/

	

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
	public Biller getBiller() {
		return biller;
	}

	
	public void setBiller(Biller biller) {
		this.biller = biller;
	}
	
	/*@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}*/
	
	

	
}
