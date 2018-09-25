/**
 *
 */
package com.nibss.cmms.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.nibss.cmms.web.WebAppConstants;

@Entity
@Table(name = "BANK_USERS")
@DiscriminatorValue(WebAppConstants.USER_TYPE_BANK)
public class BankUser extends User implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4976102374752802991L;

	//private User user;

	private Bank bank;

	//private Long id;
	
	public BankUser(){}

	public BankUser(User user) {
		this.id = user.id;
		this.email = user.email;
		this.firstName = user.firstName;
		this.lastName=user.lastName;
		this.lastLoginTime = user.lastLoginTime;
		this.password = user.password;
		this.role = user.role;
		this.status = user.status;
		this.tokenId = user.tokenId;
		this.userType=user.userType;
	}
	

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
	@JoinColumn(referencedColumnName ="bankCode")
	public Bank getBank() {
		return bank;
	}

	
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
	/*@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}*/

	/*@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	*/
	

	
}
