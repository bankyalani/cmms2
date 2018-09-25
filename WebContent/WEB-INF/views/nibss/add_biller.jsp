<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<!-- Main content -->
<layout:layout pageTitle="CMMS :: Add Biller" >
<jsp:attribute name="pageFragment">
<script>
$(function(){
	$('.autocomplete').select2({
	    minimumInputLength: 3,
	    placeholder: "Search for a Biller",
	    ajax: {
	      url: '<c:url value="/nibss/biller/getUnregisteredBillers"/>',
	      dataType: 'json',
	      data: function (request) {
	        return {
	        	 billerName: request.term
	        };
	      },
	      processResults: function (data, page) {
		   var stuff = new Object;
		   stuff.items = [];
		   for (var i=0;i<data.length;i++) {
			   stuff.items.push( { id: data[i].id, text: data[i].value});
		   }
	          return {
	            results: stuff.items
	          };
	        },
	      cache: true
	    }
	  });
});

</script>
</jsp:attribute>
	<jsp:body>
<section class="content">
	<div class="row">
		<!-- left column -->
		<div class="col-md-10">
			<!-- general form elements -->
			<div class="box box-default">
				<div class="box-header">
					<h3 class="box-title">${box_header }</h3>
				</div>
				<!-- /.box-header -->
				<!-- form start -->
				<spring:hasBindErrors name="biller">
							<div class="alert alert-danger">
								<a href="#" class="close" data-dismiss="alert">&times;</a>
								<div>
									<form:errors path="biller.*" element="div"></form:errors>
								</div>
							</div>
						</spring:hasBindErrors>
				<form:form method="POST"  modelAttribute="biller" enctype="multipart/form-data">
					<div class="box-body">
					<div class="form-group col-md-12" >

						<form:label path="company">Search Biller</form:label>
						
						<%-- <input type="text" id="companyLabel" class="form-control autocomplete" /> 
						<form:input path="company" id="companyId" type="hidden" class="form-control" value="0"/>  --%>
						<form:select path="company" class="form-control autocomplete">

						</form:select>
						
						
					</div>
					
						<div class="form-group col-md-12 required">
							<label for="bank">Customer Bank</label>
							<form:select path="bank" cssClass="form-control" required="true">
								<form:option value="">--Select--</form:option>
								<form:options items="${banks}" itemLabel="bankName"
											itemValue="bankCode" />
							</form:select>

						</div>
							
					<div class="form-group col-md-12" id="accountNameControlGroup">

						<form:label path="accountName">Account Name</form:label>
						
						<form:input path="accountName" type="text"  class="form-control" /> 
						
						
					</div>
					
					<div class="form-group col-md-12" id="accountNumberControlGroup">

						<form:label path="accountNumber">Account Number</form:label>
						
						<form:input path="accountNumber" type="text" class="form-control" /> 
						
					</div>
					
					<div class="form-group col-md-12" id="slaDocument">

						<label for="slaDocument">SLA & Indemnity</label>
						
						<form:input path="slaAttachment" type="file" class="form-control" /> 
						
					</div>
					</div>
					<div class="box-footer text-right">
						<button type="submit" class="btn btn-primary">Save</button>

					</div>
				</form:form>
			</div>

		</div>
	</div> 
</section>
</jsp:body>
</layout:layout>