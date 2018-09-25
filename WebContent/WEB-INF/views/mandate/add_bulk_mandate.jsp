<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<!-- Main content -->
<layout:layout pageTitle="CMMS - Bulk Mandate Upload" >
<jsp:attribute name="pageFragment">
</jsp:attribute>
<jsp:body>
<section class="content">
	<div class="row">
		<!-- left column -->
		<div class="col-md-10">
			<!-- general form elements -->
			
			<form method="post" enctype='multipart/form-data'>
			<div class="box box-default">
				<!-- /.box-header -->
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
				<!-- form start -->
					<p></p>
					<div class="form-group col-md-4">
						<input type="file" name="uploadFile"  required="required" class="form-control"> 
					</div>
					<div>
						<input type="submit" value="Upload" class="btn btn-md btn-primary"/>
					</div>
					
				
				<div class="box-body">
				<h4>Guidelines for bulk upload</h4>
				<ul>
					<li>File must be a of a .zip format</li>
					<li>The .zip upload must contain only <b>one</b> .xls or .xlxs file with the details of the mandate</li>
					<li>The corresponding image name must be included in the .zip archive</li>
					<li>Click <a href='<c:url value="/common/mandate/bulkTemplate"/>'><b>here</b></a> to download a sample upload file</li>
				</ul>
				</div>
			</div>
			</form>
			<!-- /.box-body -->
		</div>
		<!-- /.box -->

	</div>
</section>
</jsp:body>
</layout:layout>