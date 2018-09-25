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
<layout:layout pageTitle="CMMS | Report">
	<jsp:attribute name="pageFragment">
	<script	src="<c:url value="/resources/js/jquery.cookie.js"/>"></script>
	<script	src="<c:url value="/resources/js/plugins/jquery.fileDownload.js"/>"></script>
	
	
	
<script>
$('a.report-btn').on('click', function(e) {
	e.preventDefault();
	var form=$(this).closest('form');
	
	$.blockUI({'message':'Please wait while the report is being generated...'});
    $.fileDownload($(this).prop('href')+'?'+form.serialize(), {
    	 successCallback: function (url) {
    		 $.unblockUI();
         },
         failCallback: function (responseHtml, url) {
        	 $.unblockUI();
         },
        failMessageHtml: "There was a problem generating your report, please try again."
    });
});


/* $('a.transaction-report-btn').on('click', function(e) {
	e.preventDefault();
	var form=$(this).closet('form');	
	
	$.blockUI({'message':'Please wait while the report is being generated...'});
    $.fileDownload($(this).prop('href')+'?'+form.serialize(), {
    	 successCallback: function (url) {
    		 $.unblockUI();
         },
        failMessageHtml: "There was a problem generating your report, please try again."
    });
});
	 */	
	$(function() {
		$("#tBiller")//
		.on("change",
				function(e) {
					// mostly used event, fired to the original element when the value changes
					console.log("change val=" + this.value);
					$.getJSON('<c:url value="/product/getAllProducts/"/>'
							+ $(this).val(), null, function(j) {
						
						var options = '<option value="" selected>--Select--</option>';
					      for (var i = 0; i < j.length; i++) {
					        options += '<option value="' + j[i].id + '">' + j[i].value + '</option>';
					      }
					      $("#tProduct").html(options);
					});
				}).trigger('change');
	});
	 
	 $(function() {
			$("#mBiller")//
			.on("change",
					function(e) {
						// mostly used event, fired to the original element when the value changes
						console.log("change val=" + this.value);
						$.getJSON('<c:url value="/product/getAllProducts/"/>'
								+ $(this).val(), null, function(j) {
							
							var options = '<option value="">--Select--</option>';
						      for (var i = 0; i < j.length; i++) {
						        options += '<option value="' + j[i].id + '">' + j[i].value + '</option>';
						      }
						      $("#mProduct").html(options);
						});
					}).trigger('change');
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


			<!-- Custom Tabs -->
			<div class="nav-tabs-custom">
				<ul class="nav nav-tabs">
					<li class="active"><a href="#tab_1" data-toggle="tab">Mandate Report</a></li>
					<li><a href="#tab_2" data-toggle="tab">Transaction Report</a></li>
					
					<!--  <li class="pull-right"><a href="#" class="text-muted"><i class="fa fa-gear"></i></a></li> -->
				</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="tab_1">
				<!-- form start -->
					<div class="row">
						<form id="mandate-report-form">
							<div class="form-group col-md-4">
							<label for="biller">Biller</label>
							<select name="biller" id="mBiller" class="form-control select2">
								<option value="">--Select--</option>
								<c:forEach items="${billers}" var="biller">
									<option value="${biller.id }">${biller.company.name }</option>
								</c:forEach>
							</select>
							</div>
						
						<div class="form-group col-md-4">
							<label for="product">Product</label>
							<select name="product" id="mProduct" class="form-control product">
								<option value="">--Select--</option>
							</select>

						</div>
						
						<div class="form-group col-md-4">
							<label for="exampleInputEmail1">Debit Frequency</label>
							<select name="frequency" class="form-control">
							<option value="">--Select--</option>
							<c:forEach items="${frequencies}" var="frequency">
									<option value="${frequency.id }">${frequency.value }</option>
								</c:forEach>
							</select>
						</div>
						
						<div class="form-group col-md-4">
							<label for="bank">Customer's Bank</label>
							<select name="bank" class="form-control">
							<option value="">--Select--</option>
								<c:forEach items="${banks}" var="bank">
									<option value="${bank.bankCode }">${bank.bankName}</option>
								</c:forEach>
							</select>
						</div>
						
						<div class="col-md-4 form-group">
							<label for="status">Mandate Status</label> <select
										id="mandateStatus" name="mandateStatus" class="form-control">
								<option value="">--Select--</option>
								<c:forEach items="${mandateStatuses}" var="status">
									<option value="${status.id }">${status.name }</option>
								</c:forEach>

							</select>
						</div>

							<div class="form-group col-md-4">
							<label for="email">Date Created:</label>
							<div class="input-group">
								<div class="input-group-addon">
									<i class="fa fa-calendar"></i>
								</div>
								<input type="text" class="form-control pull-right mandateDateRange"
													name="dateCreated" id=""
													readonly="readonly" />

							</div>
						</div>
						<div class="form-group col-md-12 text-right">
						  <a href="<c:url value="${mandateReportUrl}csv"/>" class="btn btn-primary report-btn">CSV</a>
						  <a href="<c:url value="${mandateReportUrl}pdf"/>" class="btn btn-primary report-btn">PDF</a>
						  <a href="<c:url value="${mandateReportUrl}xls"/>" class="btn btn-primary report-btn">Excel</a>
						</div>
						</form>
						</div>
						</div>
					<!-- /.tab-pane -->
					<div class="tab-pane" id="tab_2">
						<div class="row">
						<form id="transaction-report-form">
							
							<div class="form-group col-md-4">
							<label for="biller">Biller</label>
							<select name="biller" id="tBiller" class="form-control">
								<option value="">--Select--</option>
								<c:forEach items="${billers}" var="biller">
									<option value="${biller.id }">${biller.company.name }</option>
								</c:forEach>
							</select>
							</div>
						
						<div class="form-group col-md-4">
							<label for="product">Product</label>
							<select name="product" id="tProduct" class="form-control product">
							<option value="">--Select--</option>
							</select>

						</div>
						
						<div class="form-group col-md-4">
							<label for="subscriberCode">Subscribers Mandate Code</label>
							<input name="subscriberCode" type="text"
												class="form-control" id="subscriberCode" />

						</div>
							
							
						
						
						<div class="form-group col-md-4">
							<label for="bank">Customer's Bank</label>
							<select name="bank" class="form-control">
							<option value="">--Select--</option>
								<c:forEach items="${banks}" var="bank">
									<option value="${bank.bankCode }">${bank.bankName}</option>
								</c:forEach>
							</select>
						</div>
						
						<div class="col-md-4 form-group">
									<label for="status">Debit Status</label> <select
												id="debitStatus" name="debitStatus" class="form-control">
										<option value="">--Select--</option>
										<option value="${PAYMENT_SUCCESSFUL}">Successful</option>
										<option value="${PAYMENT_FAILED}">Failed</option>
									</select>
								</div>
						<div class="form-group col-md-4">
							<label for="email">Transaction Date Range:</label>
							<div class="input-group">
								<div class="input-group-addon">
									<i class="fa fa-calendar"></i>
								</div>
								<input type="text" class="form-control pull-right mandateDateRange"
													name="dateCreated" id=""
													readonly="readonly" />
							</div>
						</div>
						
							
						<div class="form-group col-md-12 text-right">
						  <a href="<c:url value="${transactionReportUrl}csv"/>" class="btn btn-primary report-btn">CSV</a>
						  <a href="<c:url value="${transactionReportUrl}pdf"/>" class="btn btn-primary report-btn">PDF</a>
						  <a href="<c:url value="${transactionReportUrl}xls"/>" class="btn btn-primary report-btn">Excel</a>
						</div>
							</form>
							</div>
						</div>
				</div>
				</div>
				<!-- /.tab-pane -->
			</div>
			<!-- /.tab-content -->
		</div>
		<!-- nav-tabs-custom -->

	<!-- /.tab-pane -->

		
		</section>
</jsp:body>
</layout:layout>