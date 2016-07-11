package com.nibss.cmms.utils.exception.domain;

import com.nibss.cmms.utils.exception.ApplicationException;


/**
 * It is used to indicate any Object not found while trying to fetch from the
 * database exception from server.
 * 
 * this exception will be thrown by the HibernateUtil class and will be catched
 * by the others who are accessing the method who throws this.
 * 
 * @author Zanibal LLC
 * 
 */

public class ObjectNotFoundException extends ApplicationException {

	private static final long serialVersionUID = 200802102L;

	public ObjectNotFoundException( int errorCode, String message ) {
		super( errorCode, message );
	}

	public ObjectNotFoundException( int errorCode, String message, Throwable th ) {
		super( errorCode, message, th );
	}

}
