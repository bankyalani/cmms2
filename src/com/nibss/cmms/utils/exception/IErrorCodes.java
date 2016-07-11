package com.nibss.cmms.utils.exception;

/**
 * It contains all the errorCodes in the system.
 * 
 * User should define the separate block the different segments of codes for 
 * their respective modules. Like 0 to 299 are blocked for system and generic 
 * errors.
 * 
 * Error codes are mandatory for exceptions extending the ApplicationException 
 * and SystemException 
 * 
 * ApplicationErrorCodes further will be used to get the localized message from resources.
 * To get an localized message, constant "ER_" will be preappended to error code.
 * 
 * Error codes should not be used generically to distinugish one exception from 
 * other untill unless both the exceptions are not related to same 
 * class/category
 * 
 * Using error code to distinguish between IO Error and SQL Error is wrong
 * 
 * Using Error Code to check whether SQL Error was due to connection breakup or
 * due to statement failure is fair
 * 
 * @author Mohit Gupta
 */
public interface IErrorCodes {

	String LOCALIZED_MESSAGE_QUALIFIER	= "ER_";

	// 0  - 299 ARE BLOCKED FOR SYSTEM AND GENERIC EXCEPTIONS
	int	UNDEFINED					  = 0;

	int	EXTERNAL_SERVICE_1_DOWN		= 1;
	int	EXTERNAL_SERVICE_2_DOWN		= 2;

	int	ILLEGAL_ARGUMENT_ERROR		 = 3;
	int	SQL_STATEMENT_ERROR			= 4;
	int	DATABASE_CONNECTION_ERROR	  = 5;

	// RESOURSE can be anything like file, network connection etc
	int	RESOURCE_NOT_FOUND			 = 6;

	int	JAVA_NAMING_ERROR			  = 7;

	int	IO_ERROR					   = 8;
	int	COMPONENT_INITIALIZATION_ERROR = 9;
	int	SESSION_OPEN_ERROR			 = 10;
	int	SESSION_CLOSE_ERROR			= 11;

	int	ILLEGAL_STATE_ERROR			= 12;
	int	SERVER_SYSTEM_ERROR			= 13;
	int	AUTHENTICATION_FAILURE		 = 14;
	int	ILLEGAL_ACCESS_ERROR		   = 15;

	int	XML_PARSING_ERROR			  = 16;

	int	DAO_ERROR					  = 17;

	int	INSTANTIATION_ERROR			= 17;
	int	CLASS_NOT_FOUND_ERROR		  = 18;

	int	CHILD_RECORD_FOUND			 = 19;
	int	UNIQUE_CONSTRAINT_FAILED			 = 20;
	int	MAIL_CONFIGURATION_FAILED			 = 21;
	
	int	OBJECT_NOT_FOUND			 = 22;
	
	int REQUESTOR_FOUND_NULL = 23;
	int SUPERVISOR_FOUND_NULL = 24;
	int GRANTOR_FOUND_NULL = 25;
	
	int CODE_GENERATOR_ERROR = 26;

	int COMPONENT_ERROR = 27;
	
	int USER_NOT_FOUND = 28;
	int MAIL_ADDRESS_NOT_FOUND = 29;

    int INVALID_EMAIL_MESSAGE = 30;
}
