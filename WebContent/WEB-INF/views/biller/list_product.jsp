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
    $.post('<c:url value="/biller/product/add"/>', $('#new-product-form').serialize(), function(response) {
    	$("#responseMessage").html("");
    	
    	if (response && response.status=='SUCCESS'){
    		$("#responseMessage").removeClass("alert-danger");
    		$("#responseMessage").addClass("alert alert-success");
    		$("#responseMessage").html("<i class='fa fa-check'></i>Product has been added successfully!");
    		
    		amountToDisplay=Number($("#amount").val()).toMoney();
    		//add a new item to the datatable
    		var table = $('#productTable').DataTable();
    		table.row.add( [ 
			        "",$("#name").val(),
			        $("#description").val(),
			        amountToDisplay>0?amountToDisplay:"",
			        "Active",
			        "<a href=\"edit/1\" "+
					"class=\"btn btn-info btn-xs\">"+
					"<i class=\"fa fa-fw fa-edit\"></i></a> "+
					"<a href=\"delete/1\" "+
					"class=\"btn btn-danger btn-xs\"><i class=\"fa fa-fw fa-trash-o\"></i></a>"
			    ] )
			   .draw();
			
    		setTimeout(function() {$('#modal-dialog').modal('hide');}, 1000);
    	}else{
    		$("#responseMessage").addClass("alert alert-danger");
    		$("#responseMessage").removeClass("alert-success");
    		for (var i = 0; i < response.errorMessageList.length; i++) {
				var item = response.errorMessageList[i];
				//alert(item);
				$("#responseMessage").html($("#responseMessage").html()+item+"<br/>");
				//var $controlGroup = $('#' + item.fieldName + 'ControlGroup');
				}
    		
	
    		//$("#responseMessage").html("<div class='alert alert-danger'>Erro has occured</div>");
    	}
    	
    });
 
  
});

$(function(){$('#amount').on("change", function(e) {
	$('#amount').number( true, 2 );
	 var val=$('#amount').val();
	  $('#amount_').prop('value',val!==''?val:0);
	}).trigger('change');
});

productTable=$("#productTable").dataTable({
	"bLenthChange" : true,
	"dom" : '<"H"<"pull-left tInfo"l>f>t<"F"<"pull-left"i>p>',
	"bJQueryUI" : true,
	"bPaginate" : true,
	"bFilter" : true,
	"iDisplayLength":10,
	"bSort" : false,
	"bInfo" : true,
	"bAutoWidth" : false,
	"sPaginationType" : "bootstrap"
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
						<div class="alert alert-danger ">
							<i class="fa fa-times-circle-o"></i> ${message}
						</div>
					</c:otherwise>
				</c:choose>

			</c:if>

			<!-- /.box-header -->
			<div class="box-body no-padding">
				<sec:authorize access="hasAnyRole('ROLE_BILLER_ADMINISTRATOR')">
					<a href="#modal-dialog" id="modal-dialog-btn" data-toggle="modal"
						class="modal-toggle btn btn-warning pull-right btn-sm"> <i
						class="fa fa-fw fa-plus-square-o"></i> Add Product
					</a>
				</sec:authorize>
				<div class="panel panel-primary">
					<div class="panel-heading">


						<p></p>
					</div>

					<table class="table table-striped" id="productTable">
						<thead>
							<tr>
								<th>Product ID</th>
								<th>Product Name</th>
								<th>Product Description</th>
								<th>Amount</th>
								<th>Status</th>
								<sec:authorize ifAllGranted="ROLE_BILLER_ADMINISTRATOR">
									<th>Action</th>
								</sec:authorize>

							</tr>
						</thead>
						<tbody>

								<c:forEach items="${products }" var="product">
										<tr>
											<td>${product.id }</td>
											<td>${product.name }</td>
											<td>${product.description }</td>
											<td><fmt:formatNumber pattern="#,##0.00"
													value="${product.amount }" type="number" /></td>
											<td><status:status status="${product.status}" icon="false" /></td>
											<sec:authorize ifAllGranted="ROLE_BILLER_ADMINISTRATOR">
												<td><div class="btn-group">
														<a onclick="showAjaxModal('<c:url value="/biller/product/edit/${product.id}"/>')" href='#'
															title="Edit ${product.name }" class="btn btn-info btn-xs"><i
															class="fa fa-fw  fa-edit"></i></a> <a onclick="confirm_modal('<c:url value="/biller/product/delete/${product.id}"/>')"
															href='#'
															class="btn btn-danger btn-xs"
															title="Delete ${product.name }"> <i
															class="fa fa-fw fa-trash-o"></i>
														</a>
													</div></td>
											</sec:authorize>
										</tr>
									</c:forEach>
								
						</tbody>
					</table>
				</div>
				<!-- /.box-body -->
			</div>
		</div>
		<!-- /.box -->

	</div>
</section>


<!-- Modal -->
<div id="modal-dialog" class="modal">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<a href="#" data-dismiss="modal" aria-hidden="true" class="close">×</a>
				<h3 id="modal-header-name">Create a new Product</h3>
			</div>


			<div class="modal-body">
				<div id="responseMessage"></div>
				<spring:hasBindErrors name="product">

					<div class="alert alert-danger">
						<div>
							<form:errors path="product.name" element="div"></form:errors>
							<form:errors path="product.description" element="div"></form:errors>
							<form:errors path="product.amount" element="div"></form:errors>
						</div>
					</div>

				</spring:hasBindErrors>


				<c:url var="action" value="/biller/product/add" />
				<form:form method="POST" commandName="product"
					modelAttribute="product" id="new-product-form">

					<%-- 	<form:errors path="*" cssClass="alert alert-danger" element="div" /> --%>

					<div class="form-group col-md-12">
						<form:label path="name">Product Name</form:label>
						<form:input path="name" type="text" cssClass="form-control"
							id="name" placeholder="Enter Product Name" required="required" />

					</div>
					<div class="form-group col-md-12">
						<form:label path="description">Product Description</form:label>
						<form:input path="description" type="text" cssClass="form-control"
							placeholder="Enter Product description" required="true" />

					</div>

					<div class="form-group col-md-12">
						<form:label for="amount" path="amount">Amount</form:label>
						<form:input type="text" cssClass="form-control" id="amount"
							path="amount"></form:input>
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