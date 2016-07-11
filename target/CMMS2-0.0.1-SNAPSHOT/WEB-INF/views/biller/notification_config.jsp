<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="status" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!-- Main content -->
<layout:layout pageTitle="Notification Config">
	<jsp:attribute name="pageFragment">
<script>
	var editor = new $.fn.dataTable.Editor({
		table : "#emailConfigTable",
		fields : [ {
			label : "Status name:",
			name : "status\\.id",
			type : "select",
			"className" : "form-group",
			attr : {
				"class" : "form-control"
			},
			"default" : 1

		}, {
			label : "Description",
			name : "status\\.description"
		}, {
			label : "Email:",
			name : "emailAddress",
			className : "form-group",
			attr : {
				maxlength : 50,
				placeholder : 'Email Address',
				required : "required",
				"class" : "form-control"

			}
		}, {
			label : "Date Created",
			name : "dateCreated"
		}, {
			label : "DT_RowId",
			name : "DT_RowId",
			type : "hidden"
		} ],
		ajax : function(method, url, data, successCallback, errorCallback) {
			var id = data.data.DT_RowId;
			if (data.action === 'edit') {
				$.post('<c:url value="/biller/notification/email/config/update"/>', {
					"id" : id,
					value : data.data.emailAddress
				}, function(response) {
					if (response && response == 'SUCCESS') {
						successCallback({
							"DT_RowId" : id
						});

					} else {
						errorCallback(xhr, error, thrown);
					}
				}).fail(function(xhr, error, thrown) {
					error = "{Error occurred}";
					errorCallback(xhr, error, thrown);
				});

				//alert(data.data.status_name);//val("ttt");
			} else if (data.action === 'remove') {
				$.post('<c:url value="/biller/notification/email/config/delete"/>', {
					"id" : data.id[0]
				}, function(response) {
					if (response && response == 'SUCCESS') {
						successCallback({
							"id" : id
						});
					} else {
						errorCallback(xhr, error, thrown);
					}
				}).fail(function(xhr, error, thrown) {
					errorCallback(xhr, error, thrown);
				});
			} else if (data.action === 'create') {
				$.post('<c:url value="/biller/notification/email/config/add"/>', {
					"status.id" : data.data["status.id"],
					"emailAddress" : data.data.emailAddress
				}, function(response) {
					if (response && response == 'SUCCESS') {

						successCallback({
							"id" : id
						});
					} else {
						errorCallback(xhr, error, thrown);
					}
				}).fail(function(xhr, error, thrown) {

					errorCallback(xhr, error, thrown);
				});
			}
			//successCallback({"id" : id});
		}
	});

	/* $('#emailConfigTable').on( 'click', 'tbody td:nth-child(4)', function (e) {
	 editor.inline( this );
	 } ); */

	var emailConfigTable = $("#emailConfigTable")
			.DataTable(
					{
						"fnRowCallback" : function(nRow, aData, iDisplayIndex) {

							$("td:first", nRow).html(
									this.fnSettings()._iDisplayStart
											+ iDisplayIndex + 1);
							return nRow;
						},
						"procesing" : true,
						"serverSide" : true,
						"dom" : '<"H"<"pull-left tInfo"l><"pull-right"T>>t<"F"<"pull-left"i>p>',
						"jQueryUI" : true,
						"paginate" : true,
						"sort" : false,
						"info" : true,
						"paginationType" : "bootstrap",
						"columns" : [
								{
									"data" : null,
									"title" : "S/N",
									defaultContent : ""
								},
								{
									"data" : "status\\.id",
									"title" : "Status"
								},
								{
									"data" : "mailDescription",
									"title" : "Description"
								},
								{
									"data" : "emailAddress",
									"title" : "Email"
								},
								{
									"data" : "dateCreated",
									"title" : "Date Created"
								},
								{
									"data" : null,
									"title" : "Action",
									className : "center",
									defaultContent : '<div class="btn-group"><a href="" class="btn btn-warning btn-xs editor_edit"> <i	class="fa fa-fw  fa-edit"></i></a> <a href="" class="btn btn-danger editor_remove btn-xs"><i	class="fa fa-fw fa-trash-o"></i> </a></div>'
								} ],
						ajax : {
							"type" : "POST",
							url : "<c:url value='/biller/datatable/notification/email'/>",
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
						tableTools : {
							sRowSelect : "os",
							aButtons : [ {
								sExtends : "editor_create",
								editor : editor,
								sButtonText : "Add new email",
								"fnInit" : function(node) {
									formatTableToolsButton(node,
											'btn btn-primary');
								}
							} ]
						}

					});

	//Delete a record
	$('#emailConfigTable').on(
			'click',
			'a.editor_remove',
			function(e) {
				e.preventDefault();

				editor.message('Are you sure you wish to remove this record?')
						.title('Delete Email Configuration').buttons('Delete')
						.remove($(this).closest('tr'));
			});
	$('#emailConfigTable').on(
			'click',
			'a.editor_edit',
			function(e) {
				e.preventDefault();

				editor.title('Modify Email').buttons('Update').bubble(
						$(this).closest('tr').find('td:nth-child(4)'));
				// .edit( $(this).closest('tr') );
			});

	// New record
	editor.on('onInitCreate', function() {
		editor.enable('emailAddress');
		test = new Array();
		editor.field('status\\.id').update(test);
		editor.field('dateCreated').hide();
		editor.field('status\\.description').hide();
		editor.field('DT_RowId').hide();
		editor.classes.form.buttons = 'btn-group pull-right';

		editor.title('Create new email');
		var test = new Array();
		$.get('<c:url value="/common/mandateStatuses/get"/>').done(
				function(data) {
					data = $.parseJSON(data);
					$.each(data, function(i, item) {
						obj = {
							"label" : item.name,
							"value" : item.id
						};
						test.push(obj);
					});
					editor.field('status\\.id').update(test);
				});

		editor
		.buttons('Save')
		/* .buttons([

		{
			"label" : "Save",
			"className" : "btn btn-md btn-success",
			"fn" : function() {
				editor.submit()
			}
		} ]); */
	});

	$('#emailConfigTable a.editor_create').on(
			'click',
			function(e) {
				e.preventDefault();
				editor.field('dateCreated').hide();
				editor.field('status\\.description').hide();
				editor.field('DT_RowId').hide();
				var test = new Array();
				$.get('<c:url value="/common/mandateStatuses/get"/>').done(
						function(data) {
							data = $.parseJSON(data);
							$.each(data, function(i, item) {
								obj = {
									"label" : item.name,
									"value" : item.id
								};
								test.push(obj);
							});
							editor.field('status\\.id').update(test);
						});

				editor.create('Create new email');
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


						<div class="box-body no-padding table-responsive">
							<%-- <sec:authorize access="hasAnyRole('ROLE_BANK_ADMINISTRATOR')">
								<a href=""
									class="editor_create btn btn-default pull-right btn-sm"> <i
									class="fa fa-fw fa-plus-square-o text-primary"></i> Add Email
								</a>
							</sec:authorize> --%>
							<div class="panel panel-primary">
								<div class="panel-heading">

									<div class="col-md-6"></div>

									<p></p>
								</div>

								<table id="emailConfigTable"
								class="table table-responsive table-striped">
									
								</table>
							</div>
							<!-- /.box-body -->
						</div>
					</div>
				</div>
</section>
</jsp:body>
</layout:layout>

