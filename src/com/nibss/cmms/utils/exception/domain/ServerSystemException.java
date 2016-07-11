package com.nibss.cmms.utils.exception.domain;

import com.nibss.cmms.utils.exception.SystemException;



/**
 * It is used to indicate any unrecoverable state on server like error from 
 * database connection, error due to wrong parameters, error due to wrong call 
 * etc.
 * 
 * Currently DAOException is inheriting this exception for DAOLayer. A subclass 
 * should be used for every new layer. 
 * 
 * Across the layer, we can communicate the errornous state through these 
 * runtime exceptions, untill unless the error is not related to business logic.
 * 
 * @author Mohit Gupta
 */
public class ServerSystemException extends SystemException {

	private static final long serialVersionUID = 2009004021L;

	public ServerSystemException( int errorCode, String message ) {
		super( errorCode, message );
	}

	public ServerSystemException( int errorCode, String message, Throwable th ) {
		super( errorCode, message, th );
	}

}
