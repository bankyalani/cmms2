<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<!-- Main content -->
<layout:layout pageTitle="CMMS - Organizations" >
<jsp:attribute name="pageFragment">
<script>
$('#btnYes').click(function() {
  
    // handle form processing here
    $.post('<c:url value="/nibss/company/add"/>', $('#new-companies-modal-form').serialize(), function(response) {
    	//alert($("#new-companies-modal-response").val);
    	$("#new-companies-modal-response").html("");
    	if (response && response.status=='SUCCESS'){
    		$("#new-companies-modal-response").removeClass("alert-danger");
    		$("#new-companies-modal-response").addClass("alert alert-success");
    		$("#new-companies-modal-response").html("<i class='fa fa-check'></i>Company created successfully!");
    		oTBExample2.draw();
    		setTimeout(function() {$('#modal-dialog').modal('hide');}, 1000);
    	}else{
    		$("#new-companies-modal-response").addClass("alert alert-danger");
    		$("#new-companies-modal-response").removeClass("alert-success");
    		for (var i = 0; i < response.errorMessageList.length; i++) {
				var item = response.errorMessageList[i];
				$("#new-companies-modal-response").html($("#new-companies-modal-response").html()+item+"<br/>");
				
				}
    		
	
    		//$("#responseMessage").html("<div class='alert alert-danger'>Erro has occured</div>");
    	}
    	
    });
 
  
});
	
	var editor = new $.fn.dataTable.Editor({
		table : "#tDataTable",
		fields : [ {
			label : "First Name:",
			name : "firstName"
		}, {
			label : "Last Name:",
			name : "lastName"
		}, {
			label : "Email Address:",
			name : "email"
		} ],
		ajax : function(method, url, data, successCallback, errorCallback) {
			var id = null;

			if (data.action === 'edit') {
				id = data.data.id;
				$.post('<c:url value="/nibss/notification/config/update"/>', {
					id : data.data.id,
					value : data.data.max_email
				}, function(response) {
					if (response && response == 'SUCCESS') {

						successCallback({
							"id" : id
						});

					} else {
						errorCallback(xhr, error, thrown);
					}
				}).fail(function(xhr, error, thrown) {
					error = "{Error occurred}";
					errorCallback(xhr, error, thrown);
				});
			}
		}
	});
	
	editor.on( 'modify', function ( e, json, data ) {
		oTBExample2.draw();
	} );

	$('#tDataTable').on(
			'click',
			'a.editor_edit',
			function(e) {
				e.preventDefault();

				editor
				.title('Modify Email')
				.buttons('Update')
				.edit($(this).closest('tr'));
			});

	oTBExample2 = $("#tDataTable").DataTable({
		"procesing" : true,
		"serverSide" : true,
		"dom" : '<"H"<"pull-left tInfo"l>>t<"F"<"pull-left"i>p>',
		"jQueryUI" : true,
		"paginate" : true,
		"sort" : false,
		"info" : true,
		"paginationType" : "bootstrap",
		 "columns":[
					 { "data":null,"title": "S/N",defaultContent:"" },
		             { "data": "name", "title": "Company Name" },
		             { "data": "industry\\.name", "title": "Category" },
		             { "data": "rcNumber", "title": "RC Number"},
		             { "data": "createdBy\\.id", "title": "Created By"},
		             { "data": "status\\.id", "title": "Status"},
		             { "data": "dateCreated", "title": "Date Created"},
		             { "data": "industry\\.id","visible": false },
		             { "data": null, "title": "Action", defaultContent : '<div class="btn-group"><a href="" class="btn btn-warning btn-xs editor_edit"> <i	class="fa fa-fw  fa-edit"></i></a> <a href="" class="btn btn-danger editor_remove btn-xs"><i	class="fa fa-fw fa-trash-o"></i> </a></div>'}
			            
			],
		ajax:{
			"type": "POST",
	       	url : "<c:url value='/nibss/datatable/company/list'/>",
	        "data": function(data){
	                // Send data as json for POST.
	                return JSON.stringify(data); 
	            },
	        "contentType": "application/json; charset=UTF-8",
	        "processData": true,
	        "async": true,
	        "accepts": {
	   
	            text: "text/plain",
	            html: "text/html",
	            xml: "application/xml, text/xml",
	            json: "application/json, text/javascript"
	        }
		},
		 "fnRowCallback" : function(nRow, aData, iDisplayIndex){      
             var oSettings = this.fnSettings();
              $("td:first", nRow).html(oSettings._iDisplayStart+iDisplayIndex +1);
              return nRow;
   }
	});// dataTable
	$(".dataTables_wrapper").addClass(
			"table table-striped table-hover table-responsive");
	$('#btn_search_mandate').on('click', function(e) {
		e.preventDefault();
		//not tying it to dt cos i cant figure out multiple filtering
		var searchT = $("#tDataTable").DataTable();
		
		var doSearch = false;
		if ($('#category').val()) {
			searchT.column(7).search($('#category').val());
			doSearch = true;
		}
		if (doSearch) {
			searchT.draw();
			//$('#resetDT').removeClass("hidden");
		}

	});

	$('#tDataTable').on('search.dt', function() {
		$('#resetDT').removeClass("hidden");
	});

	$('#resetDT').on('click', function() {
		/* var oSettings = oTBExample2.fnSettings();
		for(iCol = 0; iCol < oSettings.aoPreSearchCols.length; iCol++) {
		    oSettings.aoPreSearchCols[ iCol ].sSearch = '';
		} */
		// $("#searchMandateForm select option:first").attr('selected','selected');
		$("#searchMandateForm select").each(function(elem, indx) {
			this.selectedIndex = 0;
		});
		
		$("#searchMandateForm input").val('');
		oTBExample2.columns().each(function (item) {
			oTBExample2.column(item).search("");
		});
		oTBExample2.draw();
		$(this).addClass("hidden");
		//console.log(oSettings);
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
				<sec:authorize access="hasAnyRole('ROLE_NIBSS_ADMINISTRATOR')">
							<a href="#modal-dialog" id="modal-dialog-btn" data-toggle="modal"
								class="modal-toggle btn btn-default pull-right btn-sm"> <i
								class="fa fa-fw fa-plus-square-o text-danger"></i> Add Organization
							</a>
						</sec:authorize>
				<div class="panel panel-primary">
					<div class="panel-heading">
						
						<div class="col-md-6"></div>

						
						<p></p>
					</div>
					<div class="panel-body">
						<form id="searchMandateForm">
							<div class="row-fluid">
								<div class="col-md-3 form-group">
									<label for="email">RC Number</label> <input name="email"
										class="form-control" id="email" type="text" />
								</div>
								<sec:authorize access="hasAnyRole('ROLE_NIBSS_ADMINISTRATOR')">
									<div class="col-md-3 form-group">
										<label for="status">Category</label> <select id="category" name="category"
											class="form-control">
											<option value="">--Select--</option>
											<c:forEach items="${categories}" var="category">
												<option value="${category.id}">${category.name}</option>
											</c:forEach>
										</select>
									</div>
								</sec:authorize>
							</div>
							<div class="col-md-12">
								<div class="pull-right form-group">
									<button type="button" id="resetDT"
										class="btn btn-danger btn-sm hidden">
										<i class="glyphicon glyphicon-refresh"></i> Reset
									</button>
									<button name="submit" type="button" id="btn_search_mandate"
										class="btn btn-warning btn-sm ">
										<i class="glyphicon glyphicon-search"></i> Search
									</button>
								</div>
							</div>
						</form>
					</div>
					<table class="table table-striped table-hover" id="tDataTable"
						style="width: 100%">

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
				<h3 id="modal-header-name">Create New Company</h3>
			</div>

			<div class="modal-body">
				<div id="new-companies-modal-response" class="responseMessage"></div>
				<form:form method="POST" id="new-companies-modal-form"
					modelAttribute="company" commandName="company">

					<div class="form-group col-md-12">

						<form:label path="name">Organization Name</form:label>

						<form:input path="name" type="text" id="name" class="form-control" />
						<span class="help-inline"><form:errors path="name" /></span>

					</div>

					<div class="form-group col-md-12" id="accountNameControlGroup">

						<form:label path="description">Description</form:label>

						<form:input path="description" type="text" class="form-control" />
						<span class="help-inline"><form:errors path="rcNumber" /></span>

					</div>

					<div class="form-group col-md-12" id="accountNumberControlGroup">

						<form:label path="rcNumber">RC Number</form:label>

						<form:input path="rcNumber" type="text" class="form-control" />
						<span class="help-inline"><form:errors path="rcNumber" /></span>
					</div>
					
					<div class="form-group col-md-12">
					<form:label path="industry">Category</form:label>
					<form:select path="industry" id="product" cssClass="form-control" 	required="true">
								<form:options items="${categories}" itemLabel="name"
											itemValue="id" />
						</form:select>
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