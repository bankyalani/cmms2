/**
 * 
 */
package com.nibss.cmms.web;


public interface WebAppConstants {
	
	
	public final String CURRENT_USER_LAST_NAME="lastName";
	public final String CURRENT_USER_FIRST_NAME="firstName";
	public final String CURRENT_USER_EMAIL="email";
	public final String CURRENT_USER_ID="id";
	public final String CURRENT_USER_ROLE="currentUserRole";
	public final String CURRENT_USER_LAST_LOGIN="lastLogin";
	public final String CURRENT_USER_USER="currentUser";
	public final String CURRENT_BILLER_USER="billerUser";
	public final String CURRENT_BANK_USER="bankUser";
	
	public final String ROLE_BILLER_INITIATOR="ROLE_BILLER_INITIATOR";
	public final String ROLE_BILLER_ADMINISTRATOR="ROLE_BILLER_ADMINISTRATOR";
	public final String ROLE_BILLER_AUDITOR="ROLE_BILLER_AUDITOR";
	public final String ROLE_BILLER_AUTHORIZER="ROLE_BILLER_AUTHORIZER";	
	public final String ROLE_BANK_INITIATOR="ROLE_BANK_INITIATOR";
	public final String ROLE_BANK_AUTHORIZER="ROLE_BANK_AUTHORIZER";
	public final String ROLE_BANK_ADMINISTRATOR="ROLE_BANK_ADMINISTRATOR";
	public final String ROLE_BANK_AUDITOR="ROLE_BANK_AUDITOR";	
	public final String ROLE_NIBSS_ADMINISTRATOR="ROLE_NIBSS_ADMINISTRATOR";
	
	public final String BILLER_INITIATOR="BILLER INITIATOR";
	public final String BILLER_ADMINISTRATOR="BILLER ADMINISTRATOR";
	public final String BILLER_AUDITOR="BILLER AUDITOR";
	public final String BILLER_AUTHORIZER="BILLER AUTHORIZER";	
	public final String BANK_INITIATOR="BANK INITIATOR";
	public final String BANK_AUTHORIZER="BANK AUTHORIZER";
	public final String BANK_ADMINISTRATOR="BANK ADMINISTRATOR";
	public final String BANK_AUDITOR="BANK AUDITOR";	
	public final String NIBSS_ADMINISTRATOR="NIBSS ADMINISTRATOR";
	
	
	
	
	/*	
	public static final Long BILLER_INITIATE_MANDATE=1L;
	public static final Long BILLER_APPROVE_MANDATE=2L;
	public static final Long BILLER_REJECT_MANDATE=3L;
	public static final Long BANK_ACCEPT_MANDATE=4L;
	public static final Long BANK_AUTHORIZE_MANDATE=5L;
	public static final Long BANK_REJECT_MANDATE=6L;
	*/
	public static final Long BILLER_INITIATE_MANDATE=1L; // biller initiator to biller authorizer
	public static final Long BILLER_AUTHORIZE_MANDATE=2L; // biller authorizer to bank initiator
	public static final Long BILLER_REJECT_MANDATE=3L; //biller authorizer to biller initiator
	public static final Long BILLER_APPROVE_MANDATE=4L; // biller authorizer to live
	public static final Long BILLER_DISAPPROVE_MANDATE=5L; //biller authorizer declined
	
	
	public static final Long BANK_AUTHORIZE_MANDATE=6L; //biller authorizer to bank initiator to bank authorizer
	public static final Long BANK_REJECT_MANDATE=7L; // bank authorizer to bank initiator
	public static final Long BANK_APPROVE_MANDATE=8L; // bank authorizer to live
	public static final Long BANK_DISAPPROVE_MANDATE=9L; //bank authorizer declined
	public static final Long BANK_INITIATE_MANDATE=10L; //bank initiator to bank authorizer
	

	
	public static final int STATUS_ACTIVE=1;
	public static final int STATUS_MANDATE_SUSPENDED=2;
	public static final int STATUS_MANDATE_DELETED=3;
	
	public static final int SERVICE_TYPE_POSTPAID=1;
	public static final int SERVICE_TYPE_PREPAID=2;
	
	
	
	
	public static final String VIEW_MAIN_HEADER="main_header";
	public static final String VIEW_MODAL_HEADER="modal_header";
	public static final String VIEW_BOX_HEADER="box_header";
	public static final String VIEW_BREADCRUMB="breadCrumb";
	
	
	public static final String DT_FILTER="dataTableFilter";
	public static final String DT_FILTER_1="dataTableFilter1";
	public static final String DT_FILTER_2="dataTableFilter2";
	
	/*Report*/
	public static final String REPORT_TYPE_MANDATE="mandateReport";
	public static final String REPORT_TYPE="reportType";
	public static final String REPORT_TYPE_DETAILED="detailedReportType";
	public static final String REPORT_OBJECT_TYPE="reportObjectType";
	public static final String REPORT_DATA="reportData";
	
	
	/*Transaction Status*/
	public static final String TRANSACTION_STATUS="transactionStatus";
	
	/*Channel*/
	public static final int CHANNEL_PORTAL=1;
	public static final int CHANNEL_API=2;
	
	
	/*Email Delimiter*/
	public static final String EMAIL_DELIMITER=";";
	
	
	/*Ajax response*/
	public static final String AJAX_FAILED="FAILED";
	public static final String AJAX_SUCCESS="SUCCESS";
	
	/*User Types*/
	public static final String USER_TYPE_BILLER="1";
	public static final String USER_TYPE_BANK="2";
	public static final String USER_TYPE_NIBSS="0";
	
	/*Notification Types*/
	public static final String BILLER_NOTIFICATION="1";
	public static final String BANK_NOTIFICATION="2";
	
	
	/*User Status*/
	public static final Byte USER_STATUS_ACTIVE=1;
	public static final Byte USER_STATUS_INACTIVE=0;
	
	/*Biller Status*/
	public static final int BILLER_STATUS_ACTIVE=1;
	public static final int BILLER_STATUS_INACTIVE=0;
	
	/*Report Formats*/
	public static final String EXCEL_REPORT_FORMAT="xls";
	public static final String PDF_REPORT_FORMAT="pdf";
	public static final String CSV_REPORT_FORMAT="csv";
	
	
	/*Transaction Source*/
	public static final String TRANSACTION_API="0";
	public static final String TRANSACTION_APP="1";
	
	public final int PAYMENT_SUCCESSFUL = 2;
	public final int PAYMENT_IN_PROGRESS = 1;
	public final int PAYMENT_ENTERED = 0;
	public final int PAYMENT_REVERSED = 4;
	public final int PAYMENT_FAILED = 3;
	
	public final String TRANSACTION_TYPE_DEBIT = "D";
	public final String TRANSACTION_TYPE_CREDIT = "C";
	
}
