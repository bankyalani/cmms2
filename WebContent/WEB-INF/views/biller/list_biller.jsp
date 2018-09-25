<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!-- Main content -->
<layout:layout pageTitle="CMMS - Billers">
	<jsp:attribute name="pageFragment">
<script>
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

	editor.on('modify', function(e, json, data) {
		oTBExample2.draw();
	});

	$('#tDataTable').on(
			'click',
			'a.editor_edit',
			function(e) {
				e.preventDefault();

				editor.title('Modify Email').buttons('Update').edit(
						$(this).closest('tr'));
			});

	oTBExample2 = $("#tDataTable")
			.DataTable(
					{
						"procesing" : true,
						"serverSide" : true,
						"dom" : '<"H"<"pull-left tInfo"l>>t<"F"<"pull-left"i>p>',
						"jQueryUI" : true,
						"paginate" : true,
						"sort" : false,
						"info" : true,
						"paginationType" : "bootstrap",
						"columns" : [ {
							"data" : null,
							"title" : "S/N",
							defaultContent : ""
						}, {
							"data" : "company\\.name",
							"title" : "Biller Name"
						}, {
							"data" : "company\\.industry\\.name",
							"title" : "Category"
						}, {
							"data" : "company\\.rcNumber",
							"title" : "RC Number"
						}, {
							"data" : "bank\\.bankName",
							"title" : "Introducing Bank"
						}, {
							"data" : "accountNumber",
							"title" : "Account Number"
						}, {
							"data" : "createdBy\\.id",
							"title" : "Created By"
						}, {
							"data" : "status\\.id",
							"title" : "Status"
						}, {
							"data" : "dateCreated",
							"title" : "Date Created"
						}, {
							"data" : "biller\\.id",
							"visible" : false
						}, {
							"data" : "bank\\.bankCode",
							"visible" : false
						}, {
							"data" : "company\\.industry\\.id",
							"visible" : false
						}],
						ajax : {
							"type" : "POST",
							url : "<c:url value='${billerDataTableUrl}'/>",
							"data" : function(data) {
								// Send data as json for POST.
								return JSON.stringify(data);
							},
							"contentType" : "application/json; charset=UTF-8",
							"processData" : true,
							"async" : true,
							"accepts" : {

								text : "text/plain",
								html : "text/html",
								xml : "application/xml, text/xml",
								json : "application/json, text/javascript"
							}
						},
						"fnRowCallback" : function(nRow, aData, iDisplayIndex) {
							var oSettings = this.fnSettings();
							console.log(aData);
							$("td:first", nRow).html(
									oSettings._iDisplayStart + iDisplayIndex
											+ 1);

							var link = '<a href="{url}">'
									+ aData['company.name'] + '</a>';
							link = link
									.replace(/{url}/g,
											"<c:url value='${viewUrl}"+aData["biller.id"]+"'/>");
							$("td:nth-child(2)", nRow).html(link);
							return nRow;
						},
						"rowCallback" : function(row, data) {
							alert(data);
							$('td:eq(2)', row)
									.html(
											"<a href='"+data[1]+"'>" + data[4]
													+ "</a>");
						}
					});// dataTable
	$(".dataTables_wrapper").addClass(
			"table table-striped table-hover table-responsive");
	$('#btn_search_mandate').on('click', function(e) {
		e.preventDefault();
		//not tying it to dt cos i cant figure out multiple filtering
		var searchT = $("#tDataTable").DataTable();
		/* var oSettings = oTBExample2.settings();
		for(iCol = 0; iCol < oSettings.aoPreSearchCols.length; iCol++) {
		    oSettings.aoPreSearchCols[ iCol ].sSearch = '';
		} */
		var doSearch = false;
		if ($('#email').val()) {
			searchT.column(3).search($('#email').val());
			doSearch = true;
		}

		if ($('#role').val()) {
			searchT.column(5).search($('#role').val());
			doSearch = true;
		}
		if ($('#bank').val()) {
			searchT.column(10).search($('#bank').val());
			doSearch = true;
		}
		if ($('#category').val()) {
			searchT.column(11).search($('#category').val());
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
		oTBExample2.columns().each(function(item) {
			oTBExample2.column(item).search("");
		});
		oTBExample2.draw();
		$(this).addClass("hidden");
		//console.log(oSettings);
	});
	$('#btnYes').click(function() {
		
	    // handle form processing here
	    $.post('<c:url value="/bank/biller/add"/>', $('#modal-form').serialize(), function(response) {
	    	$("#responseMessage").html("");
	    	if (response && response.status=='SUCCESS'){
	    		$("#responseMessage").removeClass("alert-danger");
	    		$("#responseMessage").addClass("alert alert-success");
	    		$("#responseMessage").html("<i class='fa fa-check'></i>Biller created successfully!");
	    		oTBExample2.draw(false);
	    		$('#resetDT').addClass("hidden");
	    		setTimeout(function() {$('#modal-dialog').modal('hide');}, 1000);
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
									title="Toggle table panel" data-container="body"> <i
									class="glyphicon glyphicon-cog"></i> Toggle Panel
						</span>
						<p></p>
					</div>
					<div class="panel-body">
						<form id="searchMandateForm">
							<div class="row-fluid">
								<div class="col-md-3 form-group">
									<label for="email">RC Number</label> <input name="email"
												class="form-control" id="email" type="text" />
								</div>
								<div class="col-md-3 form-group">
										<label for="status">Category</label> 
										<select id="category" name="category" class="form-control">
											<option value="">--Select--</option>
											<c:forEach items="${categories}" var="category">
												<option value="${category.id}">${category.name}</option>
											</c:forEach>
										</select>
								</div>
								<sec:authorize access="hasAnyRole('ROLE_NIBSS_ADMINISTRATOR')">
									<div class="col-md-3 form-group">
										<label for="bank">Introducing Bank</label> 
										<select id="bank" name="bank" class="form-control">
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
								</div>
							</div>
						</form>
					</div>
					<table class="table table-striped table-hover" id="tDataTable"
								style="width: 100%">

					</table>
				</div>
				<!--
				 /.box-body -->
			</div>
			<!-- /.box -->
		</div>

	</div>
</section>
</jsp:body>
</layout:layout>