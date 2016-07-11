package com.nibss.cmms.utils.exception;

public final class DAOException extends ApplicationException {

	private static final long serialVersionUID = 2008020603L;

	public DAOException( int errorCode, String message ) {
		super( errorCode, message );
	}

	public DAOException( int errorCode, String message, Throwable th ) {
		super( errorCode, message, th );
	}

}
