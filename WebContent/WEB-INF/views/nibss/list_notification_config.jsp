<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!-- Main content -->
<layout:layout pageTitle="CMMS - Notification Configurations">
	<jsp:attribute name="pageFragment">
<script type="text/javascript">
	var editor = new $.fn.dataTable.Editor({
		table : "#nibss-notification-table",
		fields : [ {
			name : "status_name",
			type:"hidden"
		}, {
			name : "description",
			type:"hidden"
			
		}, {
			label : "Maximum Emails For Biller:",
			name : "billerAllowedCount",
			attr : {
				maxlength : 2,
				size : 2
			}
		}, {
			label : "Maximum Emails For Bank:",
			name : "bankAllowedCount",
			attr : {
				maxlength : 2,
				size : 2
			}
		}, {

			name : "id",
			type : "hidden"
		} ],
		ajax : function(method, url, data, successCallback, errorCallback) {
			var id = null;

			if (data.action === 'edit') {
				id = data.data.id;
				$.post('<c:url value="/nibss/notification/config/update"/>', {
					id : data.data.id,
					billerAllowedCount : data.data.billerAllowedCount,
					bankAllowedCount : data.data.bankAllowedCount,
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

	$('#nibss-notification-table').on(
			'click',
			'a.editor_edit',
			function(e) {
				e.preventDefault();
				editor.title('Modify Notification Configuration').buttons(
						'Update').edit($(this).closest('tr'));
				/* editor
					.title('Modify Email')
					.buttons('Update')
					.bubble($('tbody td:nth-child(3)'))
					.bubble($('tbody td:nth-child(4)')); */
			});

	var nibssNotificationTable = $("#nibss-notification-table")
			.DataTable(
					{
						"jQueryUI" : true,
						"dom" : '<"H"<"pull-left tInfo"l>f>t<"F"<"pull-left"i>p>',
						"ordering" : false,
						"pagingType" : "bootstrap",
						columns : [
								{
									data : "status_name"
								},
								{
									data : "description"
								},
								{
									data : "billerAllowedCount"
								},
								{
									data : "bankAllowedCount"
								},
								{
									data : "id"
								},
								{
									data : null,
									className : "center",
									defaultContent : '<a href="" class="btn btn-warning btn-xs editor_edit"> <i	class="fa fa-fw  fa-edit"></i></a>'
								}
						// { data: "salary", render: $.fn.dataTable.render.number( ',', '.', 0, '$' ) }
						]
					});
</script>
</jsp:attribute>
	<jsp:body>
<section class="content">
	<div class="row">
		<!-- left column -->
		<div class="col-md-12">
			<!-- /.box-header -->
			<div class="box-body no-padding">
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
				<div class="panel panel-primary">
					<div class="panel-heading">


						<p></p>
					</div>
					<table class="table table-responsive" id="nibss-notification-table">
						<thead>
							<tr>
								<th>Status Name</th>
								<th>Description</th>
								<th>Billers' Maximum Allowed E-mails</th>
								<th>Banks' Maximum Allowed E-mails</th>
								<th style="display: none">id</th>
								<th>Action</th>
								
							</tr>
						</thead>
						<tbody>

							<c:if test="${configs.size() gt 0 }">
								<c:forEach items="${configs }" var="config" varStatus="counter">
									<tr>
										<td>${config.mandateStatus.name}</td>
										<td>${config.mandateStatus.description}</td>
										<td>${config.billerAllowedCount}</td>
										<td>${config.bankAllowedCount}</td>
										<td style="display: none">${config.id}</td>
										<td></td>
										
									</tr>
								</c:forEach>
							</c:if>


						</tbody>
					</table>
				</div>
			</div>
			<!-- /.box-body -->
		</div>
		<!-- /.box -->

	</div>
</section>
</jsp:body>
</layout:layout>