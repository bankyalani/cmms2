<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="status" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<spring:eval var="PAYMENT_SUCCESSFUL"
	expression="T(com.nibss.cmms.web.WebAppConstants).PAYMENT_SUCCESSFUL" />
<spring:eval var="PAYMENT_REVERSED"
	expression="T(com.nibss.cmms.web.WebAppConstants).PAYMENT_REVERSED" />
<spring:eval var="PAYMENT_FAILED"
	expression="T(com.nibss.cmms.web.WebAppConstants).PAYMENT_FAILED" />
<spring:eval var="PAYMENT_IN_PROGRESS"
	expression="T(com.nibss.cmms.web.WebAppConstants).PAYMENT_IN_PROGRESS" />

<!-- Main content -->
<layout:layout pageTitle="CMMS | Billing">
	<jsp:attribute name="pageFragment">
	<script src="<c:url value="/resources/js/jquery.cookie.js"/>"></script>
	<script
			src="<c:url value="/resources/js/plugins/jquery.fileDownload.js"/>"></script>
	
	
	
<script>
	$('a.report-btn')
			.on(
					'click',
					function(e) {
						e.preventDefault();
						var form = $(this).closest('form');

						$
								.blockUI({
									'message' : 'Please wait while the report is being generated...'
								});
						$
								.fileDownload(
										$(this).prop('href') + '?'
												+ form.serialize(),
										{
											successCallback : function(url) {
												$.unblockUI();
											},
											failCallback : function(
													responseHtml, url) {
												$.unblockUI();
											},
											failMessageHtml : "There was a problem generating your report, please try again."
										});
					});

	$(function() {
		//Date range picker
		$('.mandateDateRange').daterangepicker({
			format : 'DD/MM/YYYY',
			//minDate : new Date(),
			showDropdowns : true,
			cancelClass : 'alert-danger',
			locale : {
				applyLabel : 'Apply',
				cancelLabel : 'Cancel',
				weekLabel : 'W',
				customRangeLabel : 'Date Created Range'
			}
		});
	});
</script>
</jsp:attribute>
	<jsp:body>

<section class="content">
	<div class="row">
		<!-- left column -->
		<div class="col-md-12">
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



			<!-- /.box-header -->
			<div class="box-body no-padding table-responsive">
				<div class="panel panel-primary">
					<div class="panel-heading">

						<div class="col-md-6"></div>

						<span class="clickable filter pull-right" data-toggle="tooltip"
									title="Toggle table filter" data-container="body"> <i
									class="glyphicon glyphicon-filter"></i>
						</span>
						<p></p>
					</div>
					<div class="panel-body">
						<form id="searchMandateForm">
							<div class="row-fluid">
								<div class="col-md-4 form-group">
									<label for="mandateCode">Mandate Code</label> <input
												name="mandateCode" class="form-control" id="mandateCode"
												type="text" />
								</div>
								<div class="col-md-4 form-group">
									<label for="subscriberCode">Subscriber ID</label> <input
												name="subscriberCode" class="form-control"
												id="subscriberCode" type="text" />
								</div>
								<div class="col-md-4 form-group">
									<label for="status">Status</label> <select id="mandateStatus"
												name="mandateStatus" class="form-control">
										<option value="">--Select--</option>
										<c:forEach items="${mandateStatuses}" var="status">
											<option value="${status.id }">${status.name }</option>
										</c:forEach>

									</select>
								</div>
							</div>
							<div class="row-fluid">
								<div class="col-md-4 form-group">
									<label for="biller">Biller</label> <select id="biller"
												name="biller" class="form-control">
										<option value="">--Select--</option>
										<c:forEach items="${billers}" var="biller">
											<option value="${biller.id }">
												${biller.company.name}</option>
										</c:forEach>

									</select>
								</div>
								<div class="col-md-4 form-group">
									<label for="product">Product</label> <select id="product"
												name="product" class="form-control">
										<option value="">--Select--</option>
									</select>
								</div>
								<div class="col-md-4 form-group">
									<label for="status">Bank</label> <select id="mandateBank"
												name="mandateBank" class="form-control">
										<option value="">--Select--</option>
										<c:forEach items="${banks}" var="bank">
											<option value="${bank.bankCode}">${bank.bankName}</option>
										</c:forEach>

									</select>
								</div>
								<div class="pull-right form-group">
									
									<button name="submit" type="button" id="btn_search_mandate"
												class="btn btn-warning btn-md ">
										Submit
									</button>
								</div>
							</div>
						</form>
					</div>
				</div>
				<!-- /.box-body -->
			</div>
			<!-- /.box -->

		</div>
	</div>
</section>
</jsp:body>
</layout:layout>