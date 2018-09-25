package com.nibss.cmms.utils.exception;

public class SystemException extends RuntimeException implements IException {

	private static final long serialVersionUID = 2008020601L;

	private int			   errorCode;

	private boolean		   logged;

	public SystemException( int errorCode, String message ) {
		super( message );
		this.errorCode = errorCode;
	}

	public SystemException( int errorCode, String message, Throwable th ) {
		super( message, th );
		this.errorCode = errorCode;
	}

	public final int getErrorCode() {
		return errorCode;
	}

	public final boolean isLogged() {
		return logged;
	}

	public final void setLogged( boolean logged ) {
		this.logged = logged;
	}

	public String getMessage() {
		return "errorCode[" + getErrorCode() + "]:: " + super.getMessage();
	}

}
