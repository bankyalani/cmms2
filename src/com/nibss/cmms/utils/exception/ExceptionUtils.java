package com.nibss.cmms.utils.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;

import com.nibss.cmms.utils.Utilities;

/**
 * All generic utilities method related to exceptions should be put here. 
 * 
 * @author Mohit Gupta
 * */
public class ExceptionUtils {

	/**
	 * This method takes a exception as an input argument and returns the 
	 * stacktrace as a string.
	 *
	 * @param exception Exception instance to get the stack trace as string
	 */
	public static String getStackTrace( Throwable exception ) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		exception.printStackTrace( pw );
		return sw.toString();
	}

	/**
	 * It logs the exception using logging infrastructure. 
	 * 
	 * If stated exception is a IException, it checks whether the exception has 
	 * already been logged or not and logs only if it is not logged already.
	 * 
	 * @param logger logger to log the error
	 * @param message Message to log
	 * @param exception error to log 
	 * */
	public static void logException( Log logger, String message, Throwable exception ) {
		Utilities.assertNotNullArgument( logger );
		if( !( exception instanceof IException ) || !( (IException) exception ).isLogged() ) {
			logger.error( message, exception );
		}
	}

}
