package com.nibss.cmms.utils.exception.domain;

import com.nibss.cmms.utils.exception.ApplicationException;


public class ServerBusinessException extends ApplicationException {
	

	private static final long serialVersionUID = 2008020902L;

	public ServerBusinessException( int errorCode, String message ) {
		super( errorCode, message );
	}

	public ServerBusinessException( int errorCode, String message, Throwable th ) {
		super( errorCode, message, th );
	}

}
