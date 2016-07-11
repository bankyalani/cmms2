<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="status" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<spring:eval var="USER_TYPE_BANK"
	expression="T(com.nibss.cmms.web.WebAppConstants).USER_TYPE_BANK" />
<spring:eval var="USER_TYPE_NIBSS"
	expression="T(com.nibss.cmms.web.WebAppConstants).USER_TYPE_NIBSS" />
<spring:eval var="USER_TYPE_BILLER"
	expression="T(com.nibss.cmms.web.WebAppConstants).USER_TYPE_BILLER" />

<!-- Modal -->
<div>
<div id="title" class="hidden">${modal_header}</div>
	<div>
		<div >
			<div >
				
				<c:url var="action" value="/nibss/user/create" />
				<form:form method="POST" commandName="user" action="${action}"
					modelAttribute="user" class="validate">

					<%-- 	<form:errors path="*" cssClass="alert alert-danger" element="div" /> --%>

					<div class="form-group col-md-6 required">
						<form:label path="email">Email</form:label>
						<form:input path="email" type="email" cssClass="form-control"
							id="email" placeholder="Enter User email" required="true" />

					</div>
					<div class="form-group col-md-6 required">
						<form:label path="firstName">First Name</form:label>
						<form:input path="firstName" type="text" cssClass="form-control"
							placeholder="Enter First Name" required="true" />

					</div>

					<div class="form-group col-md-6 required">
						<form:label for="lastName" path="lastName">Last Name</form:label>
						<form:input type="text" cssClass="form-control" id="lastName"
							path="lastName" required="true"></form:input>
					</div>
					
					
					<c:if test ="${param.userType ne USER_TYPE_BILLER}">
					<div class="form-group col-md-6 required">
						<form:label for="userType" path="userType">User Type</form:label>
						<form:select cssClass="form-control" id="userType" required="true"
							path="userType">
							<form:option value="${USER_TYPE_NIBSS}">NIBSS Administrator</form:option>
							<form:option value="${USER_TYPE_BANK}">Bank Administrator</form:option>
						</form:select>

					</div>
					</c:if>
				
					<div class="form-group col-md-12 required" id="bankDivArea">
						<label for="role">Banks</label>
						<select class="form-control" name="bank">
							<c:forEach items="${banks}" var="bank">
								<option value="${bank.bankCode}">${bank.bankName}</option>
							</c:forEach>
						</select>
					</div>
					<%-- <div class="form-group col-md-6 required">
						<label for="role">User Role</label>
						<form:select path="role" cssClass="form-control">

							<form:options items="${roles}" itemLabel="name" itemValue="id" />
						</form:select>
					</div> --%>
					<div class="text-right">
				<button type="submit" class="btn btn-success confirm">Save</button>
				</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
<script>
$(function(){
	$("#userType").on('change',function(){
		var e=this;
		userType=$(e).val();
		bank= $("#bankDivArea");
		if(userType=="${USER_TYPE_BANK}"){
			bank.show();
		}else if(userType=="${USER_TYPE_NIBSS}"){
			bank.hide();
		}
	}).change();
})

</script>
