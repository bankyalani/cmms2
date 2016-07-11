<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="status" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- Main content -->
<layout:layout pageTitle="List Products" >
<jsp:attribute name="pageFragment">
<script type="text/javascript">
$('#btnYes').click(function() {
  
    // handle form processing here
    $.post('<c:url value="/bank/biller/add"/>', $('#modal-form').serialize(), function(response) {
    	$("#responseMessage").html("");
    	if (response && response.status=='SUCCESS'){
    		$("#responseMessage").removeClass("alert-danger");
    		$("#responseMessage").addClass("alert alert-success");
    		$("#responseMessage").html("<i class='fa fa-check'></i>Biller created successfully!");
    	}else{
    		$("#responseMessage").addClass("alert alert-danger");
    		$("#responseMessage").removeClass("alert-success");
    		for (var i = 0; i < response.errorMessageList.length; i++) {
				var item = response.errorMessageList[i];
				$("#responseMessage").html($("#responseMessage").html()+item+"<br/>");
				//var $controlGroup = $('#' + item.fieldName + 'ControlGroup');
				}
    		
	
    		//$("#responseMessage").html("<div class='alert alert-danger'>Erro has occured</div>");
    	}
    	
    });
 
  
});
	/* $(function(){
		  $(".autocomplete").autocomplete({
		  source: function( request, response ) {
		    $.ajax({
		      url: '<c:url value="/bank/biller/getUnregisteredBillers"/>',
		      dataType: "json",
		      data: {
		    	  billerName: request.term
		      },
		      

		      success: function(data) {
		        response(
		          $.map(data, function(item) {
		            return {
		              label: item.value,
		              value: item.id
		            }
		          })
		        );
		      }
		    });
		  },
		  minLength: 2,
		  select: function( event, ui ) {
		     if(ui.item) {
		      $(event.target).val(ui.item.value);
		      $('#companyLabel').val(ui.item.label);
				//do something with the value: ui.item.value
				$('#companyId').val(ui.item.value);
		    } 
		    
			return false;
		  }
		  });
		}); */
		

	billerTable = $("#billerTable").dataTable(
			{
				"bLenthChange" : true,
				"dom" : '<"H"<"pull-left tInfo"l>f>t<"F"<"pull-left"i>p>',
				"bJQueryUI" : true,
				"bPaginate" : true,
				"bFilter" : true,
				"iDisplayLength" : 10,
				"bSort" : false,
				"bInfo" : true,
				"bAutoWidth" : false,
				"sPaginationType" : "bootstrap",
				"fnRowCallback" : function(nRow, aData, iDisplayIndex) {

					$("td:first", nRow).html(
							this.fnSettings()._iDisplayStart + iDisplayIndex
									+ 1);
					return nRow;
				}
			});
	
	$(document).ajaxStart($.blockUI);
	$(document).ajaxStop($.unblockUI);	

</script>
</jsp:attribute>
<jsp:body>
	<section class="content">
	<!-- left column -->
			<!-- <div class="pull-left">
					<a href="#modal-dialog" id="modal-dialog-btn" data-toggle="modal"
						class="modal-toggle btn btn-info btn-sm"> <i
						class="fa fa-fw fa-plus-square text-default "></i> Create Biller
					</a>
					</div> -->
		<div class="row">
			
		<div class="col-md-12">
			
			<c:if test="${not empty message}">
				<c:choose>
					<c:when test="${messageClass eq 'success' }">
						<div class="alert alert-success col-md-10">
							<i class="fa fa-check"></i> ${message}
						</div>
					</c:when>
					<c:otherwise>
						<div class="alert alert-danger col-md-10">
							<i class="fa fa-times-circle-o"></i> ${message}
						</div>
					</c:otherwise>
				</c:choose>

			</c:if>

			<!-- /.box-header -->
			
			<div class="box-body no-padding table-responsive">
				
				<div class="panel panel-primary">
					<div class="panel-heading">


						<p></p>
					</div>
					<sec:authorize
					access="hasAnyRole('ROLE_BANK_ADMINISTRATOR')">
					
				</sec:authorize>
						<table  class="table table-responsive" id="billerTable">
							<thead>
								<tr>
									<th>S/N</th>
									<th>Biller Name</th>
									<th>Description</th>
									<th>RC Number</th>
									<th>Account Number</th>
									<th>Account Name</th>
									<th>Status</th>
									<th>Date Created</th>
								</tr>
							</thead>
							<tbody>
								
								<c:forEach items="${billers }" var="biller" varStatus="counter">
											<tr>
												<td>${counter.count}</td>
												<td>
												
												<a title="View details"
													href='<c:url value="/bank/biller/view/${biller.id}"/>'>
													${biller.company.name}</a></td>
												<td>${biller.company.description}</td>
												<td>${biller.company.rcNumber}</td>
												<td>${biller.accountNumber}</td>
												<td>${biller.accountName}</td>
												<td>${biller.status}</td>
												<td>${biller.dateCreated}</td>
												
												</tr>
											</c:forEach>
										
								
							</tbody>
						</table>
					</div>
					<!-- /.box-body -->
				</div>
				<!-- /.box -->

			</div>
		</div>
	</section>



<!-- /.right-side -->

<!-- Modal -->
<div id="modal-dialog" class="modal">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<a href="#" data-dismiss="modal" aria-hidden="true" class="close">×</a>
				<h3 id="modal-header-name">Create New Biller</h3>
			</div>
			<c:url var="approveUrl" value="/bank/biller/add" />


			<div class="modal-body">
				<div  id="responseMessage"></div>
				<form:form method="POST" action="${approveUrl}" id="modal-form"  modelAttribute="Biller">
						<%-- <div class="form-group col-md-12">
								<form:label path="bank">Select Biller</form:label>
								<form:select path="company" id="select2" class="form-control companies" required="true">

									<form:options items="${companies}" itemLabel="name"
										itemValue="id" />
								</form:select>
								<span  class="help-inline"><form:errors path="accountName"/></span>


							</div> --%>
					<div class="form-group col-md-12" >

						<form:label path="company">Search Biller</form:label>
						
						<%-- <input type="text" id="companyLabel" class="form-control autocomplete" /> 
						<form:input path="company" id="companyId" type="hidden" class="form-control" value="0"/>  --%>
						<select class="form-control autocomplete">

						</select>
						<span  class="help-inline"><form:errors path="company"/></span>
						
					</div>
							
					<div class="form-group col-md-12" id="accountNameControlGroup">

						<form:label path="accountName">Account Name</form:label>
						
						<form:input path="accountName" type="text"  class="form-control" /> 
						<span  class="help-inline"><form:errors path="accountName"/></span>
						
					</div>
					
					<div class="form-group col-md-12" id="accountNumberControlGroup">

						<form:label path="accountNumber">Account Number</form:label>
						
						<form:input path="accountNumber" type="text" class="form-control" /> 
						<span  class="help-inline"><form:errors path="accountNumber"/></span>
					</div>
					
					<div class="form-group col-md-12" id="slaDocument">

						<label for="slaDocument">SLA & Indemnity</label>
						
						<input type="file" class="form-control" /> 
						
					</div>
					
					
					
				</form:form>


			</div>
			<div class="modal-footer">
				<a href="#" id="btnYes" class="btn btn-success confirm">Save</a> <a
					href="#" data-dismiss="modal" aria-hidden="true"
					class="btn btn-danger secondary">Cancel</a>
			</div>

		</div>
	</div>

</div>
</jsp:body>
</layout:layout>