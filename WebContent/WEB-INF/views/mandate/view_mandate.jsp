<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<spring:eval var="STATUS_MANDATE_SUSPENDED"
	expression="T(com.nibss.cmms.web.WebAppConstants).STATUS_MANDATE_SUSPENDED" />
<spring:eval var="STATUS_ACTIVE"
	expression="T(com.nibss.cmms.web.WebAppConstants).STATUS_ACTIVE" />

<spring:eval var="BILLER_INITIATE_MANDATE"
	expression="T(com.nibss.cmms.web.WebAppConstants).BILLER_INITIATE_MANDATE" />
<spring:eval var="BILLER_AUTHORIZE_MANDATE"
	expression="T(com.nibss.cmms.web.WebAppConstants).BILLER_AUTHORIZE_MANDATE" />
<spring:eval var="BILLER_REJECT_MANDATE"
	expression="T(com.nibss.cmms.web.WebAppConstants).BILLER_REJECT_MANDATE" />
<spring:eval var="BILLER_APPROVE_MANDATE"
	expression="T(com.nibss.cmms.web.WebAppConstants).BILLER_APPROVE_MANDATE" />
<spring:eval var="BILLER_DISAPPROVE_MANDATE"
	expression="T(com.nibss.cmms.web.WebAppConstants).BILLER_DISAPPROVE_MANDATE" />
<spring:eval var="BANK_AUTHORIZE_MANDATE"
	expression="T(com.nibss.cmms.web.WebAppConstants).BANK_AUTHORIZE_MANDATE" />
<spring:eval var="BANK_REJECT_MANDATE"
	expression="T(com.nibss.cmms.web.WebAppConstants).BANK_REJECT_MANDATE" />
<spring:eval var="BANK_APPROVE_MANDATE"
	expression="T(com.nibss.cmms.web.WebAppConstants).BANK_APPROVE_MANDATE" />
<spring:eval var="BANK_DISAPPROVE_MANDATE"
	expression="T(com.nibss.cmms.web.WebAppConstants).BANK_DISAPPROVE_MANDATE" />
<spring:eval var="BANK_INITIATE_MANDATE"
	expression="T(com.nibss.cmms.web.WebAppConstants).BANK_INITIATE_MANDATE" />

<!-- Main content -->
<layout:layout pageTitle="View Mandate - ${mandate.mandateCode}">
	<jsp:attribute name="pageFragment">
<script>
	$(document)
			.ready(
					function() {
						$("#delMandate")
								.click(
										function(e) {
											e.preventDefault();
											if (!confirm("Are you sure you want to delete this manadate?")) {
												return false;
											} else {
												window.location = '<c:url value="/scommon/mandate/delete/${mandate.id }" />';
											}
										});
					});
	$(document)
			.ready(
					function() {
						$("#actMandate")
								.click(
										function(e) {
											e.preventDefault();
											if (!confirm("Are you sure you want to activate this manadate?")) {
												return false;
											} else {
												window.location = '<c:url value="/scommon/mandate/activate/${mandate.id }" />';
											}
										});
					});

	$(document)
			.ready(
					function() {
						$("#susMandate")
								.click(
										function(e) {
											e.preventDefault();
											if (!confirm("Are you sure you want to suspend this manadate?")) {
												return false;
											} else {
												window.location = '<c:url value="/scommon/mandate/suspend/${mandate.id }" />';
											}
										});
					});

	$("#changeAmount")
			.click(
					function() {
						var amountToDebitSpan = $("#amountToDebit");
						amountToDebitSpan
								.after("<input id='tempInputAmountToDebit' type='number' value='"
										+ amountToDebitSpan.text().trim()
										+ "' />");
						amountToDebitSpan.hide();
						$("#changeAmount")
								.after(
										'<span id="newButtonsWrapper"><button class="btn btn-xs btn-success" id="updateAmountToDebit" onclick="fnupdateAmountToDebit();">Update</button>'
												+ '<button class="btn btn-xs btn-danger" id="cancelAmountToDebit" onclick="fncancelAmountToDebit();">Cancel</button><span>');
						$("#changeAmount").hide();

					});

	function fncancelAmountToDebit() {
		$("#amountToDebit").show();
		$("#tempInputAmountToDebit").remove();
		$("#changeAmount").show();
		$("#newButtonsWrapper").remove();
	}

	function fnupdateAmountToDebit() {
		var urlToSend = "<c:url value= '/scommon/updateVariableAmountMandate/${mandate.id}/?new_amount=' />";
		var newAmount = $("#tempInputAmountToDebit").val();
		$.ajax({
			url : urlToSend + newAmount,
			beforeSend : function(xhr) {
				$.blockUI();
			}
		}).always(function(data) {
			$.unblockUI();

		}).done(function(data) {
			if (data && data == "SUCCESS") {
				fncancelAmountToDebit();
				//$("#tempInputAmountToDebit").number(true, 2);
				$("#amountToDebit").html(newAmount);
			} else {
				alert(data);
			}
		})
	}

	$('#bank-btnYes').click(function() {
		$('#bank-modal-form').submit();
	});
	$('#biller-btnYes').click(function() {
		$('#biller-modal-form').submit();
	});

	$(document).ready(function() {
		$(".fancybox").fancybox({
			'transitionIn' : 'elastic',
			'transitionOut' : 'elastic',
			'speedIn' : 600,
			//openEffect: 'elastic',
			//closeEffect: 'elastic',
			'type' : 'iframe',
			'speedOut' : 200,
			'overlayShow' : true,
			'hideOnContentClick' : false,
			autoSize : true,
			iframe : {
				preload : false
			// fixes issue with iframe and IE
			},
			afterShow : function() {
				// add drag and drop functionality to #box1
				$(".fancybox-skin").easydrag();
			}

		});

		$(document).ready(function() {
			var degree = 0;

			$("#rleft").click(function() {
				if (degree < 360) {
					degree = degree + 90;
				} else {
					degree = 90;
				}
				$('.rotation').jqrotate(degree);
			});

			$("#rright").click(function() {
				if (degree == 0) {
					degree = -90;
				} else {
					degree = degree - 90;
				}
				$('.rotation').jqrotate(degree);
			});
		});
	});
</script>
</jsp:attribute>
	<jsp:body>
<section class="content">
	<div class="row">
		<!-- left column -->
		<div class="col-md-6">
			<!-- general form elements -->
			<div class="box box-primary">
				<div class="box-header">

					<h3 class="box-title">Mandate Code - ${mandate.mandateCode}</h3>
					<%-- <div class="pull-right">
						<a class="fancybox btn btn-warning btn-sm" rel="group"
							title="${mandate.mandateCode}"
							href='<c:url value="/common/mandate/getImage?billerRCNumber=${mandate.product.biller.company.rcNumber}&mandateImage=${mandate.mandateImage}"/>'>
							<i class="fa fa-camera-retro"></i> View Mandate Image
						</a>
					</div> --%>
				</div>
				<!-- /.box-header -->
				<!-- form start -->
				<div class="box-body">

					<!-- <div class="col-md-6"> -->
					<table class="table table-condensed">
						<tr>
							<td><b>Biller</b></td>
							<td>${mandate.product.biller.company.name}</td>
						</tr>
						<tr>
							<td><b>Product/Service</b></td>
							<td>${mandate.product.name}</td>
						</tr>
						
						<tr>
							<td><b>Biller Subscriber Reference</b></td>
							<td>${mandate.subscriberCode}</td>
						</tr>
						<c:choose>
							<c:when test="${mandate.fixedAmountMandate}">
								<tr>
							<td><b>Amount</b></td>
							<td><fmt:formatNumber type="number" pattern="###,##0.00"
													value="${mandate.amount }" /> </td>
						</tr>
							</c:when>
							<c:otherwise>
							 <tr>
							<td><b>Amount</b></td>
							<td><fmt:formatNumber type="number" pattern="###,##0.00"
													value="${mandate.amount }" /> <b>(Variable)</b>
											</td>
						</tr>
								<tr>
							<td><b>Amount To Debit</b></td>
							<td>
							<span id="amountToDebit">
							<fmt:formatNumber type="number" pattern="###,##0.00"
														value="${mandate.variableAmount }" />
							</span>
						<c:if
													test="${(mandate.status.id eq BANK_APPROVE_MANDATE) or (mandate.status.id eq BILLER_APPROVE_MANDATE) }">
						 	<button class="btn btn-xs btn-warning" id="changeAmount">Change Amount</button>
						 </c:if>
						 </td>
						</tr>
							</c:otherwise>
						</c:choose>
						<%-- <tr>
							<td><b>Amount</b></td>
							<td><fmt:formatNumber type="number" pattern="###,##0.00"
											value="${mandate.amount }" /> 
											<c:if test="${mandate.fixedAmountMandate ne false}"> <b>(Variable)</b>
										</c:if>
											
											</td>
						</tr>
						<c:if test="${mandate.fixedAmountMandate ne false}">
						<tr>
							<td><b>Amount To Debit</b></td>
							<td>
							<span id="amountToDebit">
							<fmt:formatNumber type="number" pattern="###,##0.00"
													value="${mandate.variableAmount }" />
							</span>
						<c:if
												test="${(mandate.status.id eq BANK_APPROVE_MANDATE) or (mandate.status.id eq BILLER_APPROVE_MANDATE) }">
						 	<button class="btn btn-xs btn-warning" id="changeAmount">Change Amount</button>
						 </c:if>
						 </td>
						</tr>
						</c:if> --%>
						
						<tr>
							<td><b>Payer</b></td>
							<td>${mandate.payerName}</td>
						</tr>
						<tr>
							<td><b>Email</b></td>
							<td>${mandate.email}</td>
						</tr>
						<tr>
							<td><b>Mobile Phone</b></td>
							<td>${mandate.phoneNumber}</td>
						</tr>
						<tr>
							<td><b>Payer's Address</b></td>
							<td>${mandate.payerAddress}</td>
						</tr>
						<tr>
							<td><b>Mandate Start Date</b></td>
							<td><fmt:formatDate type="date" value="${mandate.startDate}" /></td>
						</tr>
						<tr>
							<td><b>Mandate Expiry Date</b></td>
							<td><fmt:formatDate type="date" value="${mandate.endDate}" /></td>
						</tr>
						<tr>
							<td><b>Status</b></td>
							<td>${mandate.status.name}</td>
						</tr>

						<tr>
							<td><b>Bank</b></td>
							<td>${mandate.bank.bankName}</td>
						</tr>
						<tr>
							<td><b>Account Name</b></td>
							<td>${mandate.accountName}</td>
						</tr>
						<tr>
							<td><b>Account Number</b></td>
							<td>${mandate.accountNumber}</td>
						</tr>
						
						<tr>
							<td><b>Intiated By</b></td>
							<td>${mandate.createdBy.email} <span class="badge"> <fmt:formatDate
												type="both" dateStyle="long" timeStyle="short"
												value="${mandate.dateCreated}" /></span> </td>
						</tr>

						<tr>
							<td><b>Last Action By</b></td>
							<td>${mandate.lastActionBy.email}</td>
						</tr>
						
						<c:if test="${!empty mandate.approvedBy.email}">
							<tr>
								<td><b>Approved By</b></td>
								<td>${mandate.approvedBy.email} <span class="badge"> <fmt:formatDate
													type="both" dateStyle="long" timeStyle="short"
													value="${mandate.dateApproved}" /></span> </td>
							</tr>
						</c:if>
						<c:if test="${!empty mandate.acceptedBy.email}">
							<tr>
								<td><b>Verified By</b></td>
								<td>${mandate.acceptedBy.email} <span class="badge"> <fmt:formatDate
													type="both" dateStyle="long" timeStyle="short"
													value="${mandate.dateAccepted}" /> </span></td>
							</tr>
						</c:if>
						<c:if test="${!empty mandate.authorizedBy.email}">
							<tr>
								<td><b>Authorized By</b></td>
								<td>${mandate.authorizedBy.email} <span class="badge"> <fmt:formatDate
													type="both" dateStyle="long" timeStyle="short"
													value="${mandate.dateAuthorized}" /></span></td>
							</tr>
						</c:if>

						<c:if test="${mandate.status.id eq 6 or mandate.status.id eq 3}">
							<tr>
								<td><b>Rejected By</b></td>
								<td>${mandate.rejection.user.email}  <span class="badge"> <fmt:formatDate
													type="both" dateStyle="long" timeStyle="short"
													value="${mandate.rejection.dateRejected}" /></span></td>
							</tr>
							<tr>
								<td><b>Rejected Reason</b></td>
								<td>${mandate.rejection.rejectionReason.name}</td>
							</tr>
							<tr>
								<td><b>Rejection Comment</b></td>
								<td>${mandate.rejection.comment}</td>
							</tr>
						</c:if>
						<tr>
							<td><b>Narration</b></td>
							<td>${mandate.narration}</td>
						</tr>



					</table>
					<!-- </div> -->
				</div>
				<div class="box-footer text-right">
		
					<sec:authorize
								access="hasAnyRole('ROLE_BILLER_INITIATOR','ROLE_BANK_INITIATOR')">
						<div class="btn-group" style="width: 60%">
						<sec:authorize access="hasAnyRole('ROLE_BILLER_INITIATOR')">
							<c:if
											test="${(mandate.status.id eq BILLER_INITIATE_MANDATE or mandate.status.id eq BILLER_REJECT_MANDATE) and (mandate.createdBy.role.id eq 1) }">
							<a href="<c:url value="/biller/mandate/edit/${mandate.id }" />"
												class="btn btn-primary btn-sm">Edit</a>
											
							<a id="delMandate" href="#" class="btn btn-danger btn-sm">Delete</a>
						
						</c:if>
						</sec:authorize>
						<sec:authorize access="hasAnyRole('ROLE_BANK_INITIATOR')">
							<c:if
											test="${(mandate.status.id eq BANK_INITIATE_MANDATE) and (mandate.createdBy.role.id eq 3) }">
							<a href="<c:url value="/biller/mandate/edit/${mandate.id }" />"
												class="btn btn-primary btn-sm">Edit</a>
											
							<a id="delMandate" href="#" class="btn btn-danger btn-sm">Delete</a>
						
						</c:if>
						</sec:authorize>
						<%-- <form action="" method="post"> --%>
								<input name="id" type="hidden" value="${mandate.id}" />
				
							<!-- 	<div class=""> -->
								<c:if
										test="${mandate.requestStatus eq STATUS_MANDATE_SUSPENDED }">
									<a id="actMandate" href="#"
											class="modal-toggle btn btn-sm btn-success"><i
											class="icon-mark"></i> Activate</a>
								</c:if>
								<c:if test="${mandate.requestStatus eq STATUS_ACTIVE }">
									<a id="susMandate" href="#"
											class="modal-toggle btn btn-sm btn-warning"
											data-toggle="modal" data-modal-title=""><i
											class="icon-trash"></i> Suspend</a>
								</c:if>
							<!-- 	</div> -->
							<%-- </form> --%>
						</div>
						
						<!-- Mandate that is live -->
						
							
							<!--<c:if test="${(mandate.status.id eq BILLER_APPROVE_MANDATE) or (mandate.status.id eq BANK_APPROVE_MANDATE)}">-->
						<!--</c:if>-->
					</sec:authorize>
					<sec:authorize ifAllGranted="ROLE_BILLER_AUTHORIZER">
						<c:if test="${(mandate.status.id eq BILLER_INITIATE_MANDATE)}">
							<c:url var="approveUrl" value="/biller/mandate/approve" />
							<form action="${approveUrl}" method="post">
								<input name="id" type="hidden" value="${mandate.id }" />
								<div class="btn-group">
									<button type="submit" class="btn btn-primary btn-sm"
												name="approve">Approve</button>
									<a href="#biller-modal-dialog" id="modal-dialog-btn"
												class="modal-toggle btn btn-sm btn-warning"
												data-toggle="modal" data-modal-title=""><i
												class="icon-trash"></i> Reject</a>
								</div>
							</form>
						</c:if>
						<c:if
									test="${(mandate.status.id eq BANK_AUTHORIZE_MANDATE) and (mandate.acceptedBy.role.id eq 4) }">
							<c:url var="approveUrl" value="/biller/mandate/approve" />
							<form action="${approveUrl}" method="post">
								<input name="id" type="hidden" value="${mandate.id}" />
								<div class="btn-group">
									<button type="submit" class="btn btn-primary btn-sm"
												name="approve">Approve</button>
									<a href="#biller-modal-dialog" id="modal-dialog-btn"
												class="modal-toggle btn btn-sm btn-warning"
												data-toggle="modal" data-modal-title=""><i
												class="icon-trash"></i> Reject</a>

								</div>

							</form>
						</c:if>
						
						<!-- Mandate that is live -->
						<c:if
									test="${(mandate.status.id eq BANK_APPROVE_MANDATE) or (mandate.status.id eq BILLER_APPROVE_MANDATE)}">
							<form action="" method="post">
								<input name="id" type="hidden" value="${mandate.id}" />
				
								<div class="">
								<c:if
												test="${ mandate.requestStatus eq STATUS_MANDATE_SUSPENDED }">
									<a href="#" class="modal-toggle btn btn-sm btn-success"><i
													class="icon-mark"></i> Activate</a>
								</c:if>
						<%-- 		<c:if test="${ mandate.requestStatus eq STATUS_ACTIVE }">
									<a href="#" class="modal-toggle btn btn-sm btn-warning"
												data-toggle="modal" data-modal-title=""><i
												class="icon-trash"></i>Suspend</a>
								</c:if> --%>
								</div>
							</form>
						</c:if>
					</sec:authorize>
					
					<sec:authorize ifAllGranted="ROLE_BANK_INITIATOR">
						<c:if test="${mandate.status.id eq BILLER_AUTHORIZE_MANDATE}">
							<c:url var="approveUrl" value="/bank/mandate/approve" />
							<form action="${approveUrl}" method="post">
								<input name="id" type="hidden" value="${mandate.id }" />
								<div class="btn-group">
									<button type="submit" class="btn btn-primary btn-sm"
												name="accept">Approve</button>
									<a href="#bank-modal-dialog" id="modal-dialog-btn"
												class="modal-toggle btn btn-sm btn-warning"
												data-toggle="modal" data-modal-title=""><i
												class="icon-trash"></i> Reject</a>

									<!-- <button class="btn btn-warning btn-sm" name="reject" id="#modal-dialog" >Reject</button> -->

								</div>
							</form>
						</c:if>
					</sec:authorize>
					<sec:authorize ifAllGranted="ROLE_BANK_AUTHORIZER">
						<c:if
									test="${(mandate.status.id eq BANK_AUTHORIZE_MANDATE)  and (mandate.acceptedBy.role.id eq 3) }">
							<c:url var="approveUrl" value="/bank/mandate/approve" />
							<form action="${approveUrl}" method="post">
								<input name="id" type="hidden" value="${mandate.id }" />
								<div class="btn-group">
									<button type="submit" class="btn btn-primary btn-sm"
												name="approve">Approve</button>
									<a href="#bank-modal-dialog" id="modal-dialog-btn"
												class="modal-toggle btn btn-sm btn-warning"
												data-toggle="modal" data-modal-title=""><i
												class="icon-trash"></i> Reject</a>
								</div>
							</form>
						</c:if>
						
						<c:if
									test="${(mandate.status.id eq  BANK_INITIATE_MANDATE) and (mandate.createdBy.role.id eq 3)  }">
							<c:url var="approveUrl" value="/bank/mandate/approve" />
							<form action="${approveUrl}" method="post">
								<input name="id" type="hidden" value="${mandate.id }" />
								<div class="btn-group">
									<button type="submit" class="btn btn-primary btn-sm"
												name="accept">Approve</button>
									<a href="#bank-modal-dialog" id="modal-dialog-btn"
												class="modal-toggle btn btn-sm btn-warning"
												data-toggle="modal" data-modal-title=""><i
												class="icon-trash"></i> Reject</a>
								</div>
							</form>
						</c:if>
					</sec:authorize>
				</div>

			</div>
		</div>
		<div>
<button id="rleft" class="btn btn-md btn-primary">
Rotate Right
</button>
<button id="rright" class="btn btn-md btn-primary">
Rotate Left
</button>
</div>
		<div class="col-md-6 embed-container">
		<iframe class="rotation"
						src="<c:url value="/common/mandate/getImage?billerRCNumber=${mandate.product.biller.company.rcNumber}&mandateImage=${mandate.mandateImage}"/>"
						style="scroll: auto">
			
		</iframe>
		</div>
	</div>
	<!-- /.box-body -->
</section>
<!-- /.right-side -->

<!--Biller Reject Modal -->
<div id="biller-modal-dialog" class="modal">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<a href="#" data-dismiss="modal" aria-hidden="true" class="close">×</a>
				<h3>Reject Mandate - ${mandate.mandateCode }</h3>
			</div>
			
			<div class="modal-body">
				<form method="POST"
							action="<c:url value='/biller/mandate/approve' />"
							id="biller-modal-form">
					<div class="form-group col-md-12">
						<label for="comment">Rejection Reason</label> <select
									name="rejectionReason" class="form-control">
							<option value="">--Select--</option>
							<c:forEach var="rejectionReason" items="${rejectionReasons}">
								<option value="${rejectionReason.id }">${rejectionReason.name }</option>
							</c:forEach>
						</select>
					</div>
					<div class="form-group col-md-12">

						<label for="comment">Rejection Comment</label>
						<textarea name="comment" class="form-control"></textarea>
						<input name="id" type="hidden" value="${mandate.id }" /> <input
									name="reject" type="hidden" value="reject" />
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<a href="#" id="biller-btnYes" class="btn btn-success confirm">Submit</a> <a
							href="#" data-dismiss="modal" aria-hidden="true"
							class="btn btn-danger secondary">Cancel</a>
			</div>
		</div>
	</div>
</div>


<!-- Bank Reject Modal -->
<div id="bank-modal-dialog" class="modal">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<a href="#" data-dismiss="modal" aria-hidden="true" class="close">×</a>
				<h3>Reject Mandate - ${mandate.mandateCode}</h3>
			</div>
			
			<div class="modal-body">
				<form method="POST" action='<c:url value="/bank/mandate/approve" />'
							id="bank-modal-form">
					<div class="form-group col-md-12">
						<label for="comment">Rejection Reason</label> <select
									name="rejectionReason" class="form-control">
							<option value="">--Select--</option>
							<c:forEach var="rejectionReason" items="${rejectionReasons}">
								<option value="${rejectionReason.id }">${rejectionReason.name }</option>
							</c:forEach>
						</select>
					</div>
					<div class="form-group col-md-12">

						<label for="comment">Rejection Comment</label>
						<textarea name="comment" class="form-control"></textarea>
						<input name="id" type="hidden" value="${mandate.id }" /> <input
									name="reject" type="hidden" value="reject" />
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<a href="#" id="bank-btnYes" class="btn btn-success confirm">Submit</a> <a
							href="#" data-dismiss="modal" aria-hidden="true"
							class="btn btn-danger secondary">Cancel</a>
			</div>
		</div>
	</div>
</div>
</jsp:body>
</layout:layout>