<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<!-- Main content -->
<layout:layout pageTitle="CMMS - Users" >
<jsp:attribute name="pageFragment">
<script type="text/javascript">
var options=new Array();
function loader(){
	$("#searchMandateForm #role option").each(function(i,item) {
		obj= { "label" : item.text, "value" : item.value};
		options.push(obj);
	});
	return options;
}
var editor = new $.fn.dataTable.Editor({
	table : "#tDataTable",
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
	}, {
		label : "Role:",
		name : "role\\.id",
		"type" :"select",
		"ipOpts":loader(),
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
			$.post('<c:url value="${updateUserUrl}"/>', {
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
			$.post('<c:url value="${createUserUrl}"/>', {
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
			$.post('<c:url value="${deleteUserUrl}"/>', {
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
		else if (data.action === 'reset') {
			$.post('<c:url value="${resetUserUrl}"/>', {
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

editor.on( 'modify', function ( e, json, data ) {
	oTBExample2.draw();
} );

editor.on( 'onInitCreate', function () {
	  editor.enable('email');
});

$('#tDataTable').on(
		'click',
		'a.editor_edit',
		function(e) {		
			e.preventDefault();
			editor.disable('email');
			editor
			.title('Modify User')
			.buttons('Update')
			.edit($(this).closest('tr'));
		});
		
$('a.editor_create').on(
		'click',
		function(e) {		
			e.preventDefault();
			editor.buttons('Save').create('Create new User');
		});
//Delete a record
$('#tDataTable').on(
		'click',
		'a.editor_remove',
		function(e) {
			e.preventDefault();

			editor.message('Are you sure you wish to delete '+$(this).closest('tr').find('td:nth-child(4)').html()+" ?")
			.title('Delete User')
			.buttons('Delete').remove($(this).closest('tr'));
		});
 
//reset a record password
$('#tDataTable').on(
		'click',
		'a.editor_reset',
		function(e) {
			
			e.preventDefault();

			editor.message('Are you sure you wish to reset '+$(this).closest('tr').find('td:nth-child(4)').html()+" ?")
			.title('Reset User')
			.buttons('Reset').remove($(this).closest('tr'));
		});
 
oTBExample2 = $("#tDataTable").DataTable({
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
	             { "data": "organization", "title": "Organization"},
	             { "data": "role\\.name", "title": "Role"},
	             { "data": "status\\.id", "title": "Status", 
	            	 "render": function (val, type, row) {
	                    return val == 1 ? "Active" : "In-active";
	                }
	             },
	             { "data": "dateCreated", "title": "Date Created"},
	             { "data": null, "title": "Action", defaultContent : '<div class="btn-group"><a href="" class="btn btn-warning btn-xs editor_edit"> <i	class="fa fa-fw  fa-edit"></i></a> <a href="" class="btn btn-danger editor_remove btn-xs"><i	class="fa fa-fw fa-trash-o"></i> </a> <a href="" title="Reset" class="btn btn-info editor_reset btn-xs"><i	class="glyphicon glyphicon-refresh"></i> </a></div>'},
	             { "data": "biller\\.id","visible": false },
	             { "data": "bank\\.bankCode","visible": false },
	             { "data": "role\\.id","visible": false }
		],
	ajax:{
		"type": "POST",
       	url : "<c:url value='${userDataTableUrl}'/>",
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
/* $(document).ready(function() {
    var table = $('#tDataTable').DataTable();
    var tt = new $.fn.dataTable.TableTools( table );
 	tt:{
 		aButtons:[{ sExtends: "editor_create", editor: editor }];
 	}
   // $( tt.fnContainer() ).insertBefore('div.dataTables_wrapper');
} );
 */
$('#btn_search_mandate').on('click', function(e) {
	e.preventDefault();
	//not tying it to dt cos i cant figure out multiple filtering
	var searchT = $("#tDataTable").DataTable();
	var doSearch = false;
	if ($('#email').val()) {
		searchT.column(3).search($('#email').val());
		doSearch = true;
	}

	if ($('#role').val()) {
		searchT.column(11).search($('#role').val());
		doSearch = true;
	}
	if ($('#bank').val()) {
		searchT.column(10).search($('#bank').val());
		doSearch = true;
	}
	if ($('#biller').val()) {
		searchT.column(9).search($('#biller').val());
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
		<!-- left column -->
		<div class="col-md-12">

			<!-- /.box-header -->
			<div class="box-body no-padding table-responsive">
			
				<div class="panel panel-primary">
					<div class="panel-heading">

						<div class="col-md-6"></div>

						<span class="clickable filter pull-right" data-toggle="tooltip"
							title="Toggle table filter" data-container="body"> <i
							class="glyphicon glyphicon-filter"></i> Click to filter Table
						</span>
						<p></p>
					</div>
					<div class="panel-body">
						<form id="searchMandateForm">
							<div class="row-fluid">
								<div class="col-md-3 form-group">
									<label for="email">Email</label> <input name="email"
										class="form-control" id="email" type="text" />
								</div>
								<div class="col-md-3 form-group">
									<label for="role">Role</label> <select id="role" name="role"
										class="form-control">
										<option value="">--Select--</option>
										<c:forEach items="${roles}" var="role">
											<option value="${role.id }">${role.name}</option>
										</c:forEach>
									</select>
								</div>

								<sec:authorize access="hasAnyRole('ROLE_NIBSS_ADMINISTRATOR')">
									<div class="col-md-3 form-group">
										<label for="biller">Biller</label> <select id="biller"
											name="biller" class="form-control">
											<option value="">--Select--</option>
											<c:forEach items="${billers}" var="biller">
												<option value="${biller.id }">
													${biller.company.name}</option>
											</c:forEach>

										</select>
									</div>

									<div class="col-md-3 form-group">
										<label for="status">Bank</label> <select id="bank" name="bank"
											class="form-control">
											<option value="">--Select--</option>
											<c:forEach items="${banks}" var="bank">
												<option value="${bank.bankCode}">${bank.bankName}</option>
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
									<sec:authorize access="hasAnyRole('ROLE_BANK_ADMINISTRATOR,ROLE_NIBSS_ADMINISTRATOR,ROLE_BILLER_ADMINISTRATOR')">
									<a onclick="showAjaxModal('<c:url value="/nibss/user/create"/>')" href='#'
															title="Create User" class="btn btn-info btn-sm"><i
															class="fa fa-fw fa-plus-square text-default "></i>
										Create User
									</a>
									</sec:authorize>
								</div>
							</div>
						</form>
					</div>
					<table class="table table-striped table-hover" id="tDataTable" style="width: 100%">
					
					</table>
				</div>
				<!-- /.box-body -->
			</div>
			<!-- /.box -->
		</div>

	</div>
</section>

</jsp:body>
</layout:layout>