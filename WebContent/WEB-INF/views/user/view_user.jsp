<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<!-- Main content -->
<layout:layout pageTitle="CMMS - User" >
<jsp:attribute name="pageFragment"/>
<jsp:body>
<section class="content">
	<div class="row">
		<!-- left column -->
		<div class="col-md-10">
			<!-- general form elements -->
			<div class="box box-primary">
				<div class="box-header">

					<h3 class="box-title">${box_header}</h3>

				</div>
				<!-- /.box-header -->
				<!-- form start -->
				<div class="box-body">

					<!-- <div class="col-md-6"> -->
					<table class="table table-condensed">
						<tr>
							<td>First Name</td>
							<td>${user.firstName}</td>
						</tr>
						<tr>
							<td>Last Name</td>
							<td>${user.lastName}</td>
						</tr>
						<tr>
							<td>Email</td>
							<td>${user.email}</td>
						</tr>
						<tr>
							<td>Status</td>
							<td>${user.status}</td>
						</tr>
						<tr>
							<td>Date Created</td>
							<td>${user.dateCreated}</td>
						</tr>
						<tr>
							<td>User Role</td>
							<td>${user.role.name }</td>
						</tr>


					</table>
					<!-- </div> -->
				</div>

				<div class="box-footer"></div>


			</div>



		</div>

	</div>

</section>
</jsp:body>
</layout:layout>