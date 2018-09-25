<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="status" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- Main content -->
<layout:layout pageTitle="View Mandate" >
<jsp:attribute name="pageFragment">
<script>

userTable = $("#userTable").DataTable({
	"procesing" : true,
	"serverSide" : true,
	"dom" : '<"H"<"pull-left tInfo"l><"pull-right"T>>t<"F"<"pull-left"i>p>',
	"jQueryUI" : true,
	"paginate" : true,
	"sort" : false,
	"info" : true,
	"paginationType" : "bootstrap",
	 "columns":[
				 { "data":null,"title": "S/N",defaultContent:"" },
	             { "data": "firstName", "title": "First Name" },
	             { "data": "lastName", "title": "Last Name" },
	             { "data": "email", "title": "Email", "_":"email","filter":"email_string",dispaly:"email_display"},
	             { "data": "role\\.name", "title": "Role" },
	             { "data": "status\\.id", "title": "Status", 
	            	 "render": function (val, type, row) {
	                    return val == 1 ? "Active" : "In-active";
	                }
	             },
	             { "data": "dateCreated", "title": "Date Created"},
	             { "data": null, "title": "Action", defaultContent : '<div class="btn-group"><a href="" class="btn btn-warning btn-xs editor_edit"> <i	class="fa fa-fw  fa-edit"></i></a> <a href="" class="btn btn-danger editor_remove btn-xs"><i	class="fa fa-fw fa-trash-o"></i> </a> <a href="" title="Reset" class="btn btn-info editor_reset btn-xs"><i	class="glyphicon glyphicon-refresh"></i> </a></div>'},
	            
		],
	ajax:{
		"type": "POST",
       	 url : "<c:url value='${billerUserDataTableUrl}'/>",
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
	} , tableTools: {
        sRowSelect: "os",
       
        aButtons: [
                   
            
        ]
    } 
});// dataTable
var userTableEditor = new $.fn.dataTable.Editor({
	table : "#userTable",
	fields : [ {
		label : "Email Address:",
		name : "email",
		"className" :"form-group",
		attr:  {
	        "class": "form-control"		        
	    }
		//"type": "readonly"
	},{
		label : "First Name:",
		name : "firstName",
		"className" :"form-group",
		attr:  {
	        "class": "form-control"		        
	    }
	}, {
		label : "Last Name:",
		name : "lastName",
		"className" :"form-group",
		attr:  {
	        "class": "form-control"		        
	    }
	}
	, {
		label : "Status:",
		name : "status\\.id",
		"type" :"radio",
		"className" :"form-group",
		attr:  {
	        "class": "form-control"		        
	    },
		"ipOpts": [
                  { label: "Active", value: 1 },
                  { label: "In-active",  value: 0 }
              ],
              "default": 1
	},{
		label : "DT_RowId",
		name : "DT_RowId",
		type:"hidden"
	}],
	ajax : function(method, url, data, successCallback, errorCallback) {
		var id = null;
		if (data.action === 'edit') {
			id = data.data.DT_RowId;
			$.post('<c:url value="${billerUpdateUserUrl}"/>', {
				userId : id,
				firstName : data.data.firstName,
				lastName : data.data.lastName,
				"roleId": data.data["role.id"],
				status:data.data["status.id"]
			}, function(response) {
				if (response && response == 'SUCCESS') {

					successCallback({"id" : id});

				} 
			}).fail(function(xhr, error, thrown) {
				error = "{Error occurred}";
				errorCallback(xhr, error, thrown);
			});
		}else if (data.action === 'create') {
			$.post('<c:url value="${billerCreateUserUrl}"/>', {
				firstName : data.data.firstName,
				lastName : data.data.lastName,
				"roleId": data.data["role.id"],
				status:data.data["status.id"],
				"email":data.data.email
			}, function(response) {
				if (response && response == 'SUCCESS') {
					successCallback({"id" : id});
				}
			}).fail(function(xhr, error, thrown ){
				 
				 errorCallback( xhr, error, thrown );
			});
		}else if (data.action === 'remove') {
			$.post('<c:url value="${billerDeleteUserUrl}"/>', {
				"userId" : data.id[0]
			}, function(response) {
				if (response && response == 'SUCCESS') {
					successCallback({"id" : id});
				} else {
					errorCallback( xhr, error, thrown );
				}
			}).fail(function(xhr, error, thrown ){
				 errorCallback( xhr, error, thrown );
			});
		}
	}
});

userTableEditor.on( 'modify', function ( e, json, data ) {
	userTable.draw();
} );

userTableEditor.on( 'onInitCreate', function () {
	userTableEditor.enable('email');
});

$('#userTable').on(
		'click',
		'a.editor_edit',
		function(e) {		
			e.preventDefault();
			userTableEditor.disable('email');
			userTableEditor
			.title('Modify User')
			.buttons('Update')
			.edit($(this).closest('tr'));
		});
		
		

//Delete a record
$('#userTable').on(
		'click',
		'a.editor_remove',
		function(e) {
			e.preventDefault();

			userTableEditor.message('Are you sure you wish to delete '+$(this).closest('tr').find('td:nth-child(4)').html()+" ?")
			.title('Delete User')
			.buttons('Delete').remove($(this).closest('tr'));
		});
		
$('#editor_create_billerAdmin').on('click', function(e) {
	e.preventDefault();
	//userTableEditor.field( 'dateCreated' ).hide();
	//userTableEditor.field( 'status\\.description' ).hide();
	userTableEditor.field( 'DT_RowId' ).hide();
	
	
	userTableEditor.buttons('Save').create('Create new Biller Administrator');
	
});

	productTable = $("#productTable").dataTable(
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
					<li class="active"><a href="#tab_1" data-toggle="tab">Products</a></li>
					<li><a href="#tab_2" data-toggle="tab">Biller
							Administrator</a></li>
								</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="tab_1">
						<!-- /.box-header -->
						<div class="box-body no-padding table-responsive">
							<div class="panel panel-primary">
								<div class="panel-heading">


									<p></p>
								</div>

								<table class="table table-striped" id="productTable">
									<thead>
										<tr>
											<th>S/N</th>
											<th>Product Name</th>
											<th>Product Description</th>
											<th>Amount</th>
											<th>Active</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${products }" var="product">
											<tr>
												<td></td>
												<td>${product.name }</td>
												<td>${product.description }</td>
												<td><fmt:formatNumber pattern="#,##0.00"
														value="${product.amount }" type="number" /></td>
												<td><status:status status="${product.status}" icon="false" /></td>

											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
						<!-- /.box -->
					</div>
					<!-- /.tab-pane -->
					<div class="tab-pane" id="tab_2">
						<div class="box-body no-padding table-responsive">
							
							<div class="panel panel-primary">
					<div class="panel-heading">

						<div class="col-md-6"></div>

						
						<p></p>
					</div>
					<div class="panel-body">
						<form id="searchMandateForm">
							<div class="row-fluid">
								<div class="col-md-3 form-group">
									<label for="email">Email</label> <input name="email"
										class="form-control" id="email" type="text" />
								</div>
															
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
									<sec:authorize access="hasAnyRole('ROLE_BANK_ADMINISTRATOR,ROLE_NIBSS_ADMINISTRATOR ')">
									<a id="editor_create_billerAdmin" class="btn btn-info btn-sm"> <i
												class="fa fa-fw fa-plus-square text-default "></i> Create User
									</a>
									</sec:authorize>
								</div>
							</div>
						</form>
					</div>
					<table class="table table-striped table-hover" id="userTable" style="width: 100%">
					
					</table>
				</div>
							<!-- /.box-body -->
						</div>
					</div>
	
				</div>
				<!-- /.tab-pane -->
			</div>
			<!-- /.tab-content -->
		</div>
		<!-- nav-tabs-custom -->
	</div>
	<!-- /.tab-pane -->
</section>
</jsp:body>
</layout:layout>

