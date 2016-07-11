/**
 * 
 */
package com.nibss.cmms.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class StringUtils {

	public static void assertQualifiedArgument( String str ) {
		if( !isQualifiedString( str ) ) {
			throw new IllegalArgumentException( "Null or zero length string found. str[" + str + "]" );
		}
	}

	/**
	 * This method will check for the null string
	 * 
	 * @param str string to be check
	 * @return true if string is not null
	 */
	public static boolean isQualifiedString( String str ) {
		return str != null && str.trim().length() > 0;
	}

	/**
	 * To get the String after "." symbol
	 * 
	 * @param str input string with "."
	 * @return string after Dot"."
	 */
	public static String getSimpleClassName( String str ) {
		int index = str.lastIndexOf( "." );
		str = str.substring( index + 1, str.length() );
		return str;
	}

	public static List getTokenizedString(String strToTokenize, String delimiter) {
		StringTokenizer st = new StringTokenizer(strToTokenize, delimiter);
		if (st.countTokens() <= 0) {
			return null;
		}
		List<String> tokens = new ArrayList<String>(st.countTokens());
		while (st.hasMoreTokens()) {
			tokens.add(st.nextToken());
		}
		return tokens;
	}

	public static String lpad(String text, int size, String prefix) {
		if(text.length()>=size)
			return text;
		else{
			size=size-text.length();
			StringBuilder builder = new StringBuilder();
			for(int i=0; i<size;i++){
				builder.append(prefix);
			}
			return builder.append(text).toString();
		}
	}

	public static void main(String[] args) {
		System.out.println(StringUtils.lpad("o", 3,"0"));
	}
}
