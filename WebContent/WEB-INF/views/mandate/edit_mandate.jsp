<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<!-- Main content -->
<layout:layout pageTitle="Edit Mandate" >
<jsp:attribute name="pageFragment">
<script src="<c:url value="/resources/js/mandateAdd.js"/>"></script>
</jsp:attribute>
<jsp:body>
<section class="content">
	<div class="row">
		<!-- left column -->
		<div class="col-md-10">
			<!-- general form elements -->
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">${box_header }</h3>
				</div>
				<!-- /.box-header -->
				<!-- form start -->
				<div class="box-body">

					<spring:hasBindErrors name="mandate">
						<%-- <c:if test="${mandate.error.errorCount gt 0}"> --%>
						<div class="alert alert-danger">
							<a href="#" class="close" data-dismiss="alert">&times;</a>
							<div>
								<%-- <form:errors path="mandate.subscriberCode" element="div"></form:errors>
									<form:errors path="mandate.bank" element="div"></form:errors>
									<form:errors path="mandate.product" element="div"></form:errors>
									<form:errors path="mandate.email" element="div"></form:errors>
									<form:errors path="mandate.payerName" element="div"></form:errors>
									<form:errors path="mandate.amount" element="div"></form:errors>
									<form:errors path="mandate.payerAddress" element="div"></form:errors>
									<form:errors path="mandate.narration" element="div"></form:errors>
									<form:errors path="mandate.accountName" element="div"></form:errors>
									<form:errors path="mandate.accountNumber" element="div"></form:errors>
									<form:errors path="mandate.phoneNumber" element="div"></form:errors>
									<form:errors path="mandate.validityDateRange" element="div"></form:errors>
									<form:errors path="mandate.uploadFile" element="div"></form:errors> --%>
								<form:errors path="mandate.*" element="div"></form:errors>
							</div>
						</div>
						<%-- </c:if> --%>
					</spring:hasBindErrors>

					<c:if test="${not empty message}">
						<c:choose>
							<c:when test="${messageClass eq 'success' }">
								<div class="alert alert-success">
									<i class="fa fa-check"></i> ${message}
								</div>
							</c:when>
							<c:otherwise>
								<div class="alert alert-danger">
									<i class="fa fa-times-circle-o"></i> ${message}
								</div>
							</c:otherwise>
						</c:choose>

					</c:if>
					<c:url var="action" value="/biller/mandate/edit/${mandate.id}" />
					<form:form enctype="multipart/form-data" method="POST"
						commandName="mandate" action="${action_form}"
						modelAttribute="mandate">
						<div class="form-group col-md-6 required">
							<form:label path="subscriberCode">Biller Subscriber Reference</form:label>
							<form:input path="subscriberCode" type="text"
								cssClass="form-control" id="subscriberCode"
								placeholder="Enter Subscriber Code" />

						</div>
						<div class="form-group col-md-6 required">
							<form:label path="PayerName">Payer</form:label>
							<form:input path="PayerName" type="text" cssClass="form-control"
								placeholder="Enter Payer Name" />

						</div>

						<div class="form-group col-md-4 required">
							<form:label for="biller" path="product.biller">Biller</form:label>
							<select id="" class="form-control" <c:if test="${billers.size()<2}"> disabled="disabled" </c:if>>

								<c:forEach items="${billers}" var="biller">
								
										<option value="${biller.id}">${biller.company.name}</option>
									
								</c:forEach>
								
							</select>

						</div>

						<div class="form-group col-md-5 required">
							<form:label for="product" path="product">Product/Service</form:label>
							<form:select path="product" id="product" cssClass="form-control">
								<form:options items="${products}" itemLabel="name"
									itemValue="id" />
							</form:select>

						</div>
						
						<div class="form-group col-md-3 required">
							<form:label for="amount" path="amount" >Amount (Naira)
							<a data-toggle="tooltip" class="tooltipLink"
											data-original-title="This is the upper limit amount for Variable frequency">
									<span class="glyphicon glyphicon-info-sign text-info"></span>
								</a>
							</form:label>
							<form:input type="hidden" cssClass="form-control" id="amount_"
										path="amount"></form:input>
										<form:input type="hidden" cssClass="form-control" id="mandateCode"
								path="mandateCode"></form:input>
							<form:input type="hidden" cssClass="form-control" id="mandateImage"
								path="mandateImage"></form:input>
							<input type="text" class="form-control" id="amount"	required="required">
						</div>
						
						


						<div class="form-group col-md-4 required">
							<form:label path="email">Mandate Validity Date Range:</form:label>

							<div class="input-group">
								<div class="input-group-addon">
									<i class="fa fa-calendar"></i>
								</div>
								<form:input type="text" class="form-control pull-right"
											path="validityDateRange" id="validityDateRange"
											readonly="true" required="true" />

							</div>
						</div>

						<div class="form-group col-md-4 required">
							<form:label for="phoneNumber" path="phoneNumber">Phone #</form:label>
							<form:input type="text" cssClass="form-control" id="phoneNumber"
										path="phoneNumber" required="true"></form:input>

						</div>
						
						<div class="form-group col-md-4 required">
							<form:label for="fixedAmountMandate" path="fixedAmountMandate">Fixed / Variable Amount
							<a data-toggle="tooltip" class="tooltipLink"
											data-original-title="This is to specify whether or not the amount can be changed before debit date.
											Only applicable for products without fixed amount">
									<span class="glyphicon glyphicon-info-sign text-info"></span>
								</a>
							</form:label>
							<form:select id="fixedAmountMandate" required="required"
										path="fixedAmountMandate" class="form-control">
										<option value="">--Select--</option>
										<option value="true">Fixed</option>
										<option value="false">Variable</option>
									</form:select>

						</div>


						<div class="form-group col-md-6">
							<form:label path="email">Email</form:label>
							<form:input type="text" cssClass="form-control" id="email"
								path="email" placeholder="Email Address"></form:input>
						</div>

						<div class="form-group col-md-6 required">
							<form:label for="payerAddress" path="payerAddress">Payer Address</form:label>
							<form:input type="text" cssClass="form-control" id="payerAddress"
								path="payerAddress"></form:input>
						</div>




						<div class="form-group col-md-4 required">
							<label for="exampleInputEmail1">Frequency</label>
							<form:select path="frequency" cssClass="form-control">
								<form:options items="${frequencies}" itemLabel="value"
									itemValue="id" />
							</form:select>
						</div>

						<div class="form-group col-md-4 required">
							<label for="bank">Customer Bank</label>
							
							<form:select path="bank" cssClass="form-control" required="true">
							    
								<form:options items="${banks}" itemLabel="bankName"
											itemValue="bankCode"  />
							</form:select>

						</div>

						<div class="form-group col-md-4">
							<form:label for="uploadFile" path="uploadFile">Mandate Image 
							<a data-toggle="tooltip" class="tooltipLink"
											data-original-title="Leave blank to retain old image">
									<span class="glyphicon glyphicon-info-sign text-info"></span>
								</a>
							</form:label>
							<form:input type="file" cssClass="form-control" path="uploadFile"></form:input>
						</div>


						<div class="form-group col-md-4 required">
							<form:label for="accountNumber" path="accountNumber">Account Number</form:label>
							 <div class="input-group">
							<form:input type="text" cssClass="form-control" required="true"
											id="accountNumber" path="accountNumber"/>
							<span class="input-group-btn">
        <button class="btn btn-success" type="button" id="verify-account-number">Verify!</button>
      </span>
										</div>
						</div>
						
						<div class="form-group col-md-4 required">
							<form:label for="accountName" path="accountName">Account Name</form:label>
							<form:input type="text" cssClass="form-control" required="true" readonly="true"
											id="accountName" path="accountName"/>
							
									
						</div>
						
						<div class="form-group col-md-4 required">
							<form:label for="narration" path="narration">Narration 
							<a data-toggle="tooltip" class="tooltipLink"
											data-original-title="Only first 25 Characters are captured">
									<span class="glyphicon glyphicon-info-sign"></span>
								</a>
							</form:label>
							<form:input type="text" cssClass="form-control" id="narration"
										path="narration" required="true"></form:input>
						</div>




					<!-- /.box-body -->
					<div class="box-footer text-right">
						<button type="submit" class="btn btn-primary disabled" id="submit-mandate">Save</button>

					</div>

					</form:form>
				</div>
				<!-- /.box-body -->
			</div>
		</div>
	</div>
</section>
</jsp:body>
</layout:layout>