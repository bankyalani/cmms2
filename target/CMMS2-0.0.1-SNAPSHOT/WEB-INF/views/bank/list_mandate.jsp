<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="status" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>

<!-- Main content -->
<layout:layout pageTitle="List Mandates" >
<jsp:attribute name="pageFragment">
<script>
var oTBExample2 = $("#tDataTable").DataTable({
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
	             { "data": "mandateCode", "title": "Mandate Code"},
	             { "data": "status\\.id", "title": "Workflow Status" },
	             { "data": "lastActionBy", "title": "Last Action By"},
	             { "data": "product\\.biller\\.id", "title": "Biller"},
	             { "data": "subscriberCode", "title": "Biller Subscriber Reference"},
	             { "data": "product\\.id", "title": "Product"},
	             { "data": "bank\\.bankCode","title":"Bank","visible":false},
	             { "data": "amount", "title": "Amount" },
	             { "data": "debitFrequency", "title": "Debit Frequency"},
	             { "data": "debitStartDate", "title": "Debit Start Date"},
	             { "data": "debitEndDate", "title": "Debit End Date"},
	             { "data": "nextDebitDate", "title": "Next Debit Date"},
	             { "data": "dateCreated","title":"Date Added"},
	             { "data": "product\\.biller\\.id","visible":false},
	             { "data": "dateApproved","visible":false},
	             { "data":"fixedAmountMandate","visible": false},
	             { "data": "requestStatus", "title": "Status" },
	             { "data": "payerName", "title": "Payer Name" }
		],
	ajax:{
		"type": "POST",
       	url : "<c:url value='/bank/datatable/mandate/list/1'/>",
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
          var btnRow = "<a href=\"<c:url value='/bank/mandate/view/"+aData.id+"'/>\" title='View'>"+aData.mandateCode+"</a>";
          $('td:eq(1)', nRow).html(btnRow);

          return nRow;
	} , "oTableTools" : {
		"aButtons" : [ {
			"sButtonClass" : "",
			"sExtends" : "download",
			"sButtonText" : " Download Excel",
			"sUrl" : "<c:url value='/bank/download/mandate/xls/1'/>"
		} ]
	},"fnDrawCallback": function( oSettings ) {
		 $(".mydt_processing").remove();
	 },
	 "fnPreDrawCallback": function( oSettings ) {
		 $('.tInfo label').append("<span class='mydt_processing'> <img src='<c:url value='/resources/img/ajax-loading.gif'/>'/></span>");
	 }
});
	
	$(".dataTables_wrapper").addClass("table table-striped table-hover table-responsive");
	$('#btn_search_mandate').on('click', function(e) {
		e.preventDefault();
		//not tying it to dt cos i cant figure out multiple filtering
		var searchT = $("#tDataTable").DataTable();
		var doSearch = false;
		if ($('#mandateCode').val()) {
			searchT.column(1).search($('#mandateCode').val());
			doSearch=true;
		}
		if ($('#subscriberCode').val()) {
			searchT.column(4).search($('#subscriberCode').val());
			doSearch=true;
		}
			
		if ($('#mandateStatus').val()) {
			searchT.column(2).search($('#mandateStatus').val());
			doSearch=true;
		}
		if ($('#mandateBank').val()) {
			searchT.column(7).search($('#mandateBank').val());
			doSearch=true;
		}
		if ($('#biller').val()) {
			searchT.column(4).search($('#biller').val());
			doSearch = true;
		}
		if ($('#payerName').val()!="") {
			searchT.column(17).search($('#payerName').val());
			doSearch=true;
		}
		
		
		if ($('#product').val()) {
			searchT.column(6).search($('#product').val());
			doSearch=true;
		}
		if(doSearch){
		searchT.draw();
		$('#resetDT').removeClass("hidden");
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
	$(function() {
		$("#biller").select2({
			allowClear : true
		})//
		.on(
				"change",
				function(e) {
					// mostly used event, fired to the original element when the value changes
					//console.log("change val=" + this.value);
					if($(this).val()=="") return false;
					$.getJSON('<c:url value="/product/getActiveProducts/"/>'
							+ $(this).val(), null, function(j) {
						var options = '';
						$('#product')[0].options.length = 0;
						options += '<option value="">--Select--</option>';
						for (var i = 0; i < j.length; i++) {

							options += '<option value="' + j[i].id + '">'
									+ j[i].value + '</option>';
						}

						$("#product").html(options);

					});
				}).trigger('change');

	});

	

	$(function() {
		$("#biller_other")
		.on(
				"change",
				function(e) {
					// mostly used event, fired to the original element when the value changes
					console.log("change val=" + this.value);
					$.getJSON('<c:url value="/product/getActiveProducts/"/>'
							+ $(this).val(), null, function(j) {
						var options = '';
						$('#product_other')[0].options.length = 0;
						options += '<option value="">--Select--</option>';
						for (var i = 0; i < j.length; i++) {

							options += '<option value="' + j[i].id + '">'
									+ j[i].value + '</option>';
						}

						$("#product_other").html(options);

					});
				}).trigger('change');

	});

	
	otherBanksTable = $("#otherBanksTable").DataTable({
		"procesing" : true,
		"serverSide" : true,
		"dom" : '<"H"<"pull-left tInfo2"l><"pull-right"T>>t<"F"<"pull-left"i>p>',
		"jQueryUI" : true,
		"paginate" : true,
		"sort" : false,
		"info" : true,
		"paginationType" : "bootstrap",
		 "columns":[
					 { "data":null,"title": "S/N",defaultContent:"" },
		             { "data": "mandateCode", "title": "Mandate Code"},
		             { "data": "status\\.id", "title": "Status" },
		             { "data": "product\\.biller\\.id", "title": "Biller"},
		             { "data": "subscriberCode", "title": "Subscriber Code"},
		             { "data": "product\\.id", "title": "Product"},
		             { "data": "bank\\.bankCode","title":"Bank"},
		             { "data": "amount", "title": "Amount" },
		             { "data": "debitFrequency", "title": "Debit Frequency"},
		             { "data": "debitStartDate", "title": "Debit Start Date"},
		             { "data": "debitEndDate", "title": "Debit End Date"},
		             { "data": "nextDebitDate", "title": "Next Debit Date"},
		             { "data": "dateCreated","title":"Date Added"},
		             { "data": "product\\.biller\\.id","visible":false},
		             { "data": "dateApproved","visible":false},
		             { "data":"fixedAmountMandate","visible": false}
			],
		ajax:{
			"type": "POST",
	       	url : "<c:url value='/bank/datatable/mandate/list/2'/>",
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
		} , "oTableTools" : {
			"aButtons" : [ {
				"sButtonClass" : "",
				"sExtends" : "download",
				"sButtonText" : " Download Excel",
				"sUrl" : "<c:url value='/bank/download/mandate/xls/2'/>"
			} ]
		},"fnDrawCallback": function( oSettings ) {
			 $(".mydt_processing2").remove();
		 },
		 "fnPreDrawCallback": function( oSettings ) {
			 $('.tInfo2 label').append("<span class='mydt_processing2'> <img src='<c:url value='/resources/img/ajax-loading.gif'/>'/></span>");
		 }
	});
		
		$(".dataTables_wrapper").addClass("table table-striped table-hover table-responsive");
		$('#btn_search_mandate_other').on('click', function(e) {
			e.preventDefault();
			//not tying it to dt cos i cant figure out multiple filtering
			var searchT = $("#otherBanksTable").DataTable();
			var doSearch = false;
			if ($('#mandateCode_other').val()) {
				searchT.column(1).search($('#mandateCode_other').val());
				doSearch=true;
			}
			if ($('#subscriberCode_other').val()) {
				searchT.column(4).search($('#subscriberCode_other').val());
				doSearch=true;
			}
				
			if ($('#mandateStatus_other').val()) {
				searchT.column(2).search($('#mandateStatus_other').val());
				doSearch=true;
			}
			if ($('#mandateBank_other').val()) {
				searchT.column(10).search($('#mandateBank_other').val());
				doSearch=true;
			}
			if ($('#biller_other').val()) {
				searchT.column(3).search($('#biller_other').val());
				doSearch = true;
			}
			
			if ($('#product_other').val()) {
				searchT.column(5).search($('#product_other').val());
				doSearch=true;
			}
			if(doSearch){
			searchT.draw();
			$('#resetOtherTable').removeClass("hidden");
			}

		});
		$('#tDataTable').on('search.dt', function() {
			$('#resetDT').removeClass("hidden");
		});

		$('#otherBanksTable').on('search.dt', function() {
			$('#resetOtherTable').removeClass("hidden");
		});
	
	$('#resetOtherTable').on('click', function() {
		$("#searchMandateForm_other select").each(function(elem, indx) {
			this.selectedIndex = 0;
		});
		//$("#biller").select2('val', '');
		$("#otherBanksTable").DataTable().column(3).search("");
		$('#product_other').html("<option value=''>--Select--</option>");
		//$('#select2-biller-container').html('--Select--');
		otherBanksTable.draw();
		$(this).addClass("hidden");
		//console.log(oSettings);
	});
	
	$('#resetDT').on('click', function() {
		$("#searchMandateForm select").each(function(elem, indx) {
			this.selectedIndex = 0;
		});
		$("#tDataTable").DataTable().column(3).search("");
		$('#product').html("<option value=''>--Select--</option>");
		oTBExample2.draw();
		$(this).addClass("hidden");
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
					<li class="active"><a href="#tab_1" data-toggle="tab">Mandates
							By Bank's Customer</a></li>
					<li><a href="#tab_2" data-toggle="tab">Mandates By Biller
							Recruited</a></li>

					<!--  <li class="pull-right"><a href="#" class="text-muted"><i class="fa fa-gear"></i></a></li> -->
				</ul>
				<div class="tab-content">
					<div class="tab-pane active" id="tab_1">
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
												<label for="status">Status</label> <select
													id="mandateStatus" name="mandateStatus"
													class="form-control">
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
													<c:forEach items="${products}" var="product">
														<option value="${product.id }">${product.name}</option>
													</c:forEach>

												</select>
											</div>
											<div class="col-md-4 form-group">
												<label for="status">Bank</label> <select id="mandateBank"
													name="mandateBank" class="form-control">

													<c:forEach items="${banks}" var="bank">
														<option value="${bank.bankCode}">${bank.bankName}</option>
													</c:forEach>

												</select>
											</div>
												<div class="col-md-4 form-group">
									<label for="payerName">Payer Name</label> <input
										name="payerName" class="form-control" id="payerName"
										type="text" />
								</div>
											<div class="pull-right form-group">
												<button type="button" id="resetDT"
													class="btn btn-danger btn-md hidden">
													<i class="glyphicon glyphicon-refresh"></i> Reset
												</button>
												<button name="submit" type="button" id="btn_search_mandate"
													class="btn btn-warning btn-md ">
													<i class="glyphicon glyphicon-search"></i> Search
												</button>
											</div>
										</div>
									</form>
								</div>
								<table class="table table-striped table-hover" style="width: 100%" id="tDataTable">
									
								</table>
							</div>
							<!-- /.box-body -->
						</div>
						<!-- /.box -->
					</div>
					<!-- /.tab-pane -->
					<div class="tab-pane" id="tab_2">

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
									<form id="searchMandateForm_other">
										<div class="row-fluid">
											<div class="col-md-4 form-group">
												<label for="mandateCode">Mandate Code</label> <input
													name="mandateCode" class="form-control"
													id="mandateCode_other" type="text" />
											</div>
											<div class="col-md-4 form-group">
												<label for="subscriberCode">Subscriber ID</label> <input
													name="subscriberCode" class="form-control"
													id="subscriberCode_other" type="text" />
											</div>
											<div class="col-md-4 form-group">
												<label for="status">Status</label> <select
													id="mandateStatus_other" name="mandateStatus"
													class="form-control">
													<option value="">--Select--</option>
													<c:forEach items="${mandateStatuses}" var="status">
														<option value="${status.id }">${status.name }</option>
													</c:forEach>

												</select>
											</div>
										</div>
										<div class="row-fluid">
											<div class="col-md-4 form-group">
												<label for="biller">Biller</label> <select id="biller_other"
													name="biller" class="form-control">
													<option value="">--Select--</option>
													<c:forEach items="${bankBillers}" var="biller">
														<option value="${biller.id }">
															${biller.company.name}</option>
													</c:forEach>

												</select>
											</div>
											<div class="col-md-4 form-group">
												<label for="product">Product</label> <select
													id="product_other" name="product" class="form-control">
													<option value="">--Select--</option>
													<c:forEach items="${products}" var="product">
														<option value="${product.id }">${product.name}</option>
													</c:forEach>

												</select>
											</div>
											<div class="col-md-4 form-group">
												<label for="bank">Bank</label> <select
													id="mandateBank_other" name="mandateBank"
													class="form-control">
													<option value="">--Select--</option>
													<c:forEach items="${customerBanks}" var="bank">
														<option value="${bank.bankCode}">${bank.bankName}</option>
													</c:forEach>

												</select>
											</div>
											<div class="pull-right form-group">
												<button type="button" id="resetOtherTable"
													class="btn btn-danger btn-md hidden">
													<i class="glyphicon glyphicon-refresh"></i> Reset
												</button>
												<button name="submit" type="button"
													id="btn_search_mandate_other"
													class="btn btn-warning btn-md ">
													<i class="glyphicon glyphicon-search"></i> Search
												</button>
											</div>
										</div>
									</form>
								</div>
								<table class="table table-striped table-hover" style="width: 100%"
									id="otherBanksTable">
									
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
</section>
</jsp:body>
</layout:layout>