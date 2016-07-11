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
	userTable = $("#userTable")
	.dataTable({
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
	
	
	
	
	var emailConfigTable = $("#emailConfigTable")
			.DataTable(
					{
						"jQueryUI" : true,
						"dom" : '<"H"<"pull-left tInfo"l>f>t<"F"<"pull-left"i>p>',
						"ordering" : false,
						"pagingType" : "bootstrap",
						"fnRowCallback" : function(nRow, aData, iDisplayIndex) {

							$("td:first", nRow).html(
									this.fnSettings()._iDisplayStart
											+ iDisplayIndex + 1);
							return nRow;
						},
						
						columns : [
						        {data : null},
								{data : "status_name"},
								{data : "description"},
								{data : "email"},
								{data : "dateCreated"},
								{data : "createdBy"}
								]
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
					<li><a href="#tab_3" data-toggle="tab">Email
							Configurations</a></li>

					<!--  <li class="pull-right"><a href="#" class="text-muted"><i class="fa fa-gear"></i></a></li> -->
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
											<th>Status</th>
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
												<td><status:status status="${product.status}" icon="true" /></td>

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
							<sec:authorize access="hasAnyRole('ROLE_BANK_ADMINISTRATOR')">
								<a href="#modal-dialog" id="modal-dialog-btn"
									data-toggle="modal"
									class="modal-toggle btn btn-default pull-right btn-sm"> <i
									class="fa fa-fw fa-plus-square-o text-primary"></i> Add User
								</a>
							</sec:authorize>
							<div class="panel panel-primary">
								<div class="panel-heading">
									<p></p>
								</div>
								<table id="userTable"
									class="table table-responsive table-striped">
									<thead>
										<tr>
											<th>S/N</th>
											<th>Email</th>
											<th>First Name</th>
											<th>Last Name</th>
											<th>Role</th>
											<th>Status</th>
											<th>Date Created</th>
											<sec:authorize access="hasAnyRole('ROLE_BANK_ADMINISTRATOR')">
												<th>Action</th>
											</sec:authorize>

										</tr>
									</thead>
									<tbody>
										<c:forEach items="${users}" var="user" varStatus="counter">
											<tr>
												<td>${counter.count}</td>
												<td><a title="View details"
													href='<c:url value="${view_link}${user.id}"/>'>
														${user.email }</a></td>
												<td>${user.firstName}</td>
												<td>${user.lastName}</td>
												<td>${user.role.name }</td>
												<td><status:status status="${user.status}" icon="true" /></td>
												<td>${user.dateCreated}</td>
												<sec:authorize
													access="hasAnyRole('ROLE_BANK_ADMINISTRATOR')">
													<td><a
														href='<c:url value="/product/edit/${product.id}"/>'
														title="Edit ${product.name }" class="btn btn-info btn-xs"><i
															class="fa fa-fw  fa-edit"></i></a></td>
												</sec:authorize>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
							<!-- /.box-body -->
						</div>
					</div>


					<div class="tab-pane" id="tab_3">

						<div class="box-body no-padding table-responsive">
							<sec:authorize access="hasAnyRole('ROLE_BANK_ADMINISTRATOR')">
								<a href=""
									class="editor_create btn btn-default pull-right btn-sm"> <i
									class="fa fa-fw fa-plus-square-o text-primary"></i> Add Email
								</a>
							</sec:authorize>
							<div class="panel panel-primary">
								<div class="panel-heading">

									<div class="col-md-6"></div>

									<p></p>
								</div>

								<table id="emailConfigTable"
									class="table table-responsive table-striped">
									<thead>
										<tr>
											<th>S/N</th>
											<th>Status</th>
											<th>Status Description</th>
											<th>Email Address</th>
											<th>Date Created</th>
											<th>Created By</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${notifications}" var="notification"
											varStatus="counter">
											<tr>
												<td></td>
												<td>${notification.mandateStatus.name}</td>
												<td>${notification.mandateStatus.description}</td>
												<td>${notification.emailAddress}</td>
												<td>${notification.dateCreated}</td>
												<td>${notification.createdBy.email}</td>
												
											</tr>
										</c:forEach>
									</tbody>
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