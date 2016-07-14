<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<!-- Main content -->
<layout:layout pageTitle="CMMS - Mandates" >
<jsp:attribute name="pageFragment">
<script>
$(function() {
	//Date range picker
	$('#dateCreated, #dateApproved,#nextDebitDate').daterangepicker({
		format : 'DD/MM/YYYY',
		opens:'left',
		showDropdowns : true,
		 cancelClass : 'alert-danger',
		 ranges: {
	           'Today': [moment(), moment()],
	           'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
	           'Last 7 Days': [moment().subtract(6, 'days'), moment()],
	           'Last 30 Days': [moment().subtract(29, 'days'), moment()],
	           'This Month': [moment().startOf('month'), moment().endOf('month')],
	           'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
	        },
        buttonClasses: ['btn', 'btn-sm'],
        applyClass: 'btn-success',
        cancelClass: 'btn-danger',
        dateLimit: { months: 2 }
	});
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
	             { "data": "mandateCode", "title": "Mandate Code"},
	             { "data": "status\\.id", "title": "Workflow Status" },
	             { "data": "lastActionBy", "title": "Last Action By"},
	             { "data": "bank\\.bankCode", "title": "Bank"},
	             { "data": "subscriberCode", "title": "Biller Subscriber Reference"},
	             { "data": "product\\.id", "title": "Product"},
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
       	url : "<c:url value='/biller/datatable/mandate/list'/>",
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
          var btnRow = "<a href=\"<c:url value='/biller/mandate/view/"+aData.id+"'/>\" title='View'>"+aData.mandateCode+"</a>";
          $('td:eq(1)', nRow).html(btnRow);

          return nRow;
	} , "oTableTools" : {
		"aButtons" : [ {
			"sButtonClass" : "",
			"sExtends" : "download",
			"sButtonText" : " Download Excel",
			"sUrl" : "<c:url value='/biller/download/mandate/xls'/>"
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
		if ($('#mandateCode').val()!="") {
			searchT.column(1).search($('#mandateCode').val());
			doSearch=true;
		}
		if ($('#subscriberCode').val()!="") {
			searchT.column(4).search($('#subscriberCode').val());
			doSearch=true;
		}
			
		if ($('#mandateStatus').val()!="") {
			searchT.column(2).search($('#mandateStatus').val());
			doSearch=true;
		}
		if ($('#mandateBank').val()!="") {
			searchT.column(4).search($('#mandateBank').val());
			doSearch=true;
		}
		
		if ($('#dateCreated').val()!="") {
			searchT.column(12).search($('#dateCreated').val());
			doSearch=true;
		}	
		if ($('#dateApproved').val()!="") {
			searchT.column(14).search($('#dateApproved').val());
			doSearch=true;
		}
		if ($('#nextDebitDate').val()!="") {
			searchT.column(11).search($('#nextDebitDate').val());
			doSearch=true;
		}
		
		if ($('#fixedAmountMandate').val()!="") {
			searchT.column(15).search($('#fixedAmountMandate').val());
			doSearch=true;
		}
		
		
		
		if ($('#product').val()!="") {
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
								<div class="col-md-3 form-group">
									<label for="mandateCode">Mandate Code</label> <input
										name="mandateCode" class="form-control" id="mandateCode"
										type="text" />
								</div>
								<div class="col-md-3 form-group">
									<label for="subscriberCode">Subscriber ID</label> <input
										name="subscriberCode" class="form-control" id="subscriberCode"
										type="text" />
								</div>
								<div class="col-md-3 form-group">
									<label for="status">Status</label> <select id="mandateStatus"
										name="mandateStatus" class="form-control">
										<option value="">--Select--</option>
										<c:forEach items="${mandateStatuses}" var="status">
											<option value="${status.id }">${status.name }</option>
										</c:forEach>

									</select>
								</div>
								<div class="col-md-3 form-group">
									<label for="subscriberCode">Date Initiated</label> <input
										name="dateCreated" class="form-control" id="dateCreated"
										type="text" readonly/>
								</div>
							</div>
							<div class="row-fluid">
								<div class="col-md-3 form-group">
									<label for="biller">Biller</label> <select id="biller"
										name="biller" class="form-control">
										<c:forEach items="${billers}" var="biller">
											<option value="${biller.id }">
												${biller.company.name}</option>
										</c:forEach>

									</select>
								</div>
								<div class="col-md-3 form-group">
									<label for="product">Product</label> <select id="product"
										name="product" class="form-control">
										<option value="">--Select--</option>
										<c:forEach items="${products}" var="product">
											<option value="${product.id }">${product.name}</option>
										</c:forEach>

									</select>
								</div>
								<div class="col-md-3 form-group">
									<label for="status">Bank</label> <select id="mandateBank"
										name="mandateBank" class="form-control">
										<option value="">--Select--</option>
										<c:forEach items="${banks}" var="bank">
											<option value="${bank.bankCode}">${bank.bankName}</option>
										</c:forEach>

									</select>
								</div>
								<div class="col-md-3 form-group">
									<label for="dateApproved">Date Approved</label> <input
										name="dateApproved" class="form-control" id="dateApproved"
										type="text" readonly/>
								</div>
								<div class="col-md-3 form-group">
									<label for="fixedAmountMandate">Fixed / Variable Amount</label> <select id="fixedAmountMandate"
										name="fixedAmountMandate" class="form-control">
										<option value="">--Select--</option>
										<option value="true">Fixed</option>
										<option value="false">Variable</option>
									</select>
								</div>
								<div class="col-md-3 form-group">
									<label for="nextDebitDate">Next Debit Date</label> <input
										name="nextDebitDate" class="form-control" id="nextDebitDate"
										type="text" readonly/>
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
					<table class="table table-striped table-hover" id="tDataTable" style="width:100%">
						
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