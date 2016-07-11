package com.nibss.cmms.utils.exception;

public class ComponentException extends ApplicationException {

	private static final long serialVersionUID = 2009004020L;

	public ComponentException( int errorCode, String message ) {
		super( errorCode, message );
	}

	public ComponentException( int errorCode, String message, Throwable th ) {
		super( errorCode, message );
	}

}
