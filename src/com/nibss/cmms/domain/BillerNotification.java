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
@Table(name = "BILLER_NOTIFICATION")
@DiscriminatorValue(WebAppConstants.BILLER_NOTIFICATION)
public class BillerNotification extends Notification implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8583943412164063265L;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
	private Biller biller;

	

	public Biller getBiller() {
		return biller;
	}

	
	public void setBiller(Biller biller) {
		this.biller = biller;
	}
	
	
}
