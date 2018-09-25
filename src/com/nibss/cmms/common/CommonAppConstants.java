/**
 * 
 */
package com.nibss.cmms.common;


public interface CommonAppConstants {

    /* ** Domain classes ** */
    String ACCESS_REQUEST_CLASS = "AccessRequest";
    String ACCESS_REQUEST_SORT_COL = "id";
    String ZONE_CLASS = "Zone";
    String COUNTRY_CLASS = "Country";
    String ZONE_SORT_COL = "name";
    String COUNTRY_SORT_COL = "name";
    String APPLICATION_CLASS = "Application";
    String APPLICATION_SORT_COL = "name";
    String BILLER_USER_CLASS = "BillerUser";
    String USER_CLASS = "User";
    String USER_SORT_COL = "domainId";
    String BO_CLASS = "BusinessOffice";
    String CALL_CATEGORY_CLASS = "CallCategory";
    String CATEGORY_CLASS = "Category";
    String BO_SORT_COL = "name";
    String CATEGORY_COL = "description";
    String CATEGORY_CATEGORY_ID_COL = "category";
    String APPLICATION_APPLICATION_ID_COL = "application";
    String ROLE_CLASS = "Role";
    String REQUEST_TYPE = "RequestType";
    String ROLE_SORT_COL = "name";
   

    /* ** All status codes ** */
    Byte NEW = 0;
    Byte SUBMITTED = 1;
    Byte SUP_APPROVED = 2;
    Byte SUP_DENIED = 3;
    Byte IT_GRANTED = 4;
    Byte IT_DENIED = 5;
    Byte OPEN = 6;
    Byte CLOSED = 7;
    
    
    /** Request Types **/
    final String DATA_CENTRE_REQUEST="1";
    final String APPLICATION_REQUEST="2";

    /* ** All status strings ** */
    String TXT_NEW = "NEW";
    String TXT_SUBMITTED = "SUBMITTED";
    String TXT_SUP_APPROVED = "SUP_APPROVED";
    String TXT_SUP_DENIED = "SUP_DENIED";
    String TXT_IT_GRANTED = "SSMG_GRANTED";
    String TXT_IT_DENIED = "SSMG_DENIED";
    String TXT_OPEN = "OPEN";
    String TXT_CLOSED = "CLOSED";
    String ROLE_ADMIN = "Admin";
    String ROLE_AUDIT = "Audit";
    String ROLE_SSMG = "SSMG";
    String APPLY_URL = "apply.do?action=add";
    String APPROVE_URL = "approve.do?action=list";
    String GRANT_URL = "grant.do?action=list";
    String APPLY__DC_URL = "apply_dc.do?action=add";
    String APPROVE_DC_URL = "approve_dc.do?action=list";
    String GRANT_DC_URL = "grant.do?action=list_dc";
    String REPORT_URL = "report.jsp";
    String ADMIN_URL = "administer.jsp";
    String STATUS_REPORT_NAME = "status";
    String TAT_REPORT_NAME = "tat";
    String CALLOVER_REPORT_NAME = "callover";
    String ZONEAUDIT_REPORT_NAME = "zoneaudit";
    String DATACENTRE_REPORT_NAME = "datacentre";
    String ZONEAUDIT_QUERY = "zoneauditQuery";
    String DATACENTRE_QUERY = "dataCentreQuery";
    String START_DATE = "start-date";
    String END_DATE = "end-date";
    String ZONES = "zones";
    String STATUS = "status";
    String APPLICATIONS = "applications";
    String SUPERVISORS = "supervisors";
    String GRANTORS = "grantors";
    String COUNTRIES = "countries";
    String ORDER_BY = "order-by";
    String DIRECTION = "direction";
    
    
    /* ** Report Columns ** */
    String COLUMN_CODE = "request_code";
    String COLUMN_DATE = "request_date";
    String COLUMN_PROPOSED_START_DATE = "proposed_start_date";
    String COLUMN_PROPOSED_END_DATE = "proposed_end_date";
    String COLUMN_ACTUAL_START_DATE = "actual_start_date";
    String COLUMN_ACTUAL_END_DATE = "actual_end_date";
    String COLUMN_STATUS = "request_status";
    String COLUMN_REQUESTOR = "request_requestor";
    String COLUMN_ZONE = "request_zone";
    String COLUMN_APPLICATION = "application_name";
    String COLUMN_APPROVING_SUPERVISOR = "approving_supervisor";
    String COLUMN_GRANTING_OFFICER = "granting_officer";
    String COLUMN_COMMENT= "request_comment";
    String COLUMN_CATEGORY= "category_desc";
    String COLUMN_REQUESTOR_ID = "requestor_id";
    String COLUMN_REQUESTOR_TIME = "requestor_time";
    String COLUMN_APPROVING_SUPERVISOR_ID = "approving_supervisor_id";
    String COLUMN_APPROVING_SUPERVISOR_TIME = "approving_supervisor_time";
    String COLUMN_APPROVING_SUPERVISOR_TT = "approving_supervisor_tt";
    String COLUMN_GRANTING_OFFICER_ID = "granting_officer_id";
    String COLUMN_GRANTING_OFFICER_TIME = "granting_officer_time";
    String COLUMN_OLD_ZONE_MANAGER = "old_zone_manager";
    String COLUMN_NEW_ZONE_MANAGER = "new_zone_manager";
    String COLUMN_GRANTING_OFFICER_TT = "granting_officer_tt";
    String COLUMN_TOTAL_TAT = "total_tat";
    
    
    /** Ajax Actions Validation **/
    String HCM_VER="HCM";
    String LDAP_VER="LDAP";
    String STAFF_LEAVE_STATUS="onLeave";
    String STAFF_RELIEF="reliefID";
    String VERIFY_SUPERVISOR_ACTION="verifySupervisor";
    String LOAD_DATACENTRE_SUBCATEGORIES_ACTION="loadDCSubCategory";
    String LOAD_SUBCATEGORIES_ACTION="loadSubCategory";
    String LOAD_BOS_ACTION="loadBusinessOffices";
    
}
