<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ attribute name="pageTitle" required="false" type="java.lang.String"
	description="Page title"%>
<%@ attribute name="pageFragment" required="false" fragment="true"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${pageTitle}</title>
<meta
	content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no'
	name='viewport'>
<!-- bootstrap 3.0.2 -->
<link href="<c:url value="/resources/css/bootstrap.min.css"/>"
	rel="stylesheet">

<link href="<c:url value="/resources/css/bootstrap-switch.css"/>"
	rel="stylesheet">
<!-- font Awesome -->
<link href="<c:url value="/resources/css/font-awesome.min.css"/>"
	rel="stylesheet" type="text/css" />
<!-- Theme style -->
<link href="<c:url value="/resources/css/AdminLTE.css"/>"
	rel="stylesheet" type="text/css" />

<%-- <link rel="stylesheet"
	href="<c:url value="/resources/css/custom-theme/jquery-ui-1.10.3.custom.css"/>" />
<link rel="stylesheet"
	href="<c:url value="/resources/css/custom-theme/jquery-ui-1.10.3.theme.css"/>" />
<link rel="stylesheet"
	href="<c:url value="/resources/css/custom-theme/jquery.ui.1.10.3.ie.css"/>" />
 --%>

<link rel="stylesheet"
	href="<c:url value="/resources/css/jQueryUI/custom-theme/jquery-ui-1.10.0.custom.css"/>" />
<link rel="stylesheet"
	href="<c:url value="/resources/css/jQueryUI/custom-theme/jquery-ui-1.9.2.custom.css"/>" />
<link rel="stylesheet"
	href="<c:url value="/resources/css/jQueryUI/custom-theme/jquery.ui.1.10.0.ie.css"/>" />
<link rel="stylesheet"
	href="<c:url value="/resources/css/jQueryUI/custom-theme/jquery.ui.1.9.2.ie.css"/>" />



<link
	href="<c:url value="/resources/css/fancybox/jquery.fancybox.css?v=2.1.5"/>"
	rel="stylesheet" type="text/css" />

<link href="<c:url value="/resources/css/select2.min.css"/>"
	rel="stylesheet" type="text/css" />

<link
	href="<c:url value="/resources/css/datatables/dataTables.bootstrap.css"/>"
	rel="stylesheet" type="text/css" />

<link
	href="<c:url value="/resources/css/datatables/dataTables.tableTools.css"/>"
	rel="stylesheet" type="text/css" />

<%-- <link
	href="<c:url value="/resources/js/plugins/datatables/extensions/Editor-1.4.0/css/dataTables.editor.css"/>"
	rel="stylesheet" type="text/css" />
 --%>
<link
	href="<c:url value="/resources/js/plugins/datatables/extensions/Editor-1.4.0/css/dataTables.editor.1.3.1.css"/>"
	rel="stylesheet" type="text/css" />



<style>
.ui-autocomplete {
	z-index: 5000;
}

.validation-failed {
	border: 1px dashed #eb340a !important;
	background: #faebe7 !important;
}

.ui-toolbar {
	margin: 0px !important
}

.error {
	color: red;
}

.form-group.required:before {
	content: "*" !important;
	color: red !important;
}

.embed-container {
	position: relative;
	padding-bottom: 100%;
	height: 0;
	overflow: hidden;
	max-width: 100%;
	min-height: 100%;
}

.embed-container iframe, .embed-container object, .embed-container embed
	{
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 60%;
	border: 1px solid #ddd;
	overflow: hidden;
	-moz-border-radius: 8px;
	-webkit-border-radius: 8px;
	border-radius: 8px;
	-moz-box-shadow: 4px 4px 14px #ddd;
	-webkit-box-shadow: 4px 4px 14px #ddd;
	box-shadow: 4px 4px 14px #ddd;
}
</style>

<link
	href="<c:url value="/resources/css/daterangepicker/daterangepicker-bs3.css"/>"
	rel="stylesheet" type="text/css" />
<!-- iCheck for checkboxes and radio inputs -->
<link href="<c:url value="/resources/css/iCheck/all.css"/>"
	rel="stylesheet" type="text/css" />
<!-- Bootstrap Color Picker -->
<link
	href="<c:url value="/resources/css/colorpicker/bootstrap-colorpicker.min.css"/>"
	rel="stylesheet" />
<!-- Bootstrap time Picker -->
<link
	href="<c:url value="/resources/css/timepicker/bootstrap-timepicker.min.css"/>"
	rel="stylesheet" />



<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
          <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
        <![endif]-->

<script>
	function formatTableToolsButton(node, icon) {
		$(node).removeClass('DTTT_button');
		$(node).button({
			icons : {
				primary : icon
			}
		});
		$('.DTTT_container').buttonset();
		/* Add this part if you're using a DataTable inside an hidden JUI tab. */
		$(".ui-tabs").bind("tabsshow", function(event, ui) {
			$('.DTTT_container').buttonset();
		});
	}
</script>
</head>
<body class="skin-blue fixed">
		<!-- header logo: style can be found in header.less -->
		<header class="header" style="z-index: 5">
			<a href="/" class="logo"> <!-- Add the class icon to your logo image or logo icon to add the margining -->
				<img src='<c:url value="/resources/img/sponsor4.gif"/>' class=""
				alt="User Image" />
			</a>
			<!-- Header Navbar: style can be found in header.less -->
			<nav class="navbar navbar-static-top" role="navigation">
				<!-- Sidebar toggle button-->
				<!-- <a href="#" class="navbar-btn sidebar-toggle" data-toggle="offcanvas" role="button"> -->
				<a href="#" class="navbar-btn sidebar-toggle"
					data-toggle="offcanvas" role="button"> <span class="sr-only">Toggle
						navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</a>
				<div class="navbar-right">
					<ul class="nav navbar-nav">
						<!-- User Account: style can be found in dropdown.less -->
						<li class="dropdown user user-menu"><a href="#"
						class="dropdown-toggle" data-toggle="dropdown"> <i
							class="glyphicon glyphicon-user"></i> <span>${sessionScope.currentUser.firstName }
								${sessionScope.currentUser.lastName }<i class="caret"></i>
						</span>
					</a>
						<ul class="dropdown-menu">
								<!-- User image -->
								<li class="user-header bg-light-blue">
									<div>

										<div>
											<small>${sessionScope.currentUserRole.name}</small>
										</div>


									</div> <small>Last Login <fmt:formatDate type="both"
											dateStyle="short" timeStyle="short"
											value="${sessionScope.lastLogin}" /></small>
								</li>

								<!-- Menu Footer-->
								<li class="user-footer">
									<div class="pull-left">
										<a href="<c:url value="/user/profile/view"/>"
											class="btn btn-info btn-sm"><i
											class="glyphicon glyphicon-user"></i> Profile</a>
									</div>
									<div class="pull-right">
										<a href="<c:url value="/logout"/>"
											class="btn btn-danger btn-sm"><i class="fa fa-power-off"></i>
											Sign out</a>
									</div>
								</li>
								<li class="user-footer">
									<div style="text-align: center">
										<a href="#change-password-modal-dialog"
											id="change-password-modal-dialog-btn" data-toggle="modal"
											class="modal-toggle btn btn-info btn-sm" style="width: 100%"><i
											class="glyphicon glyphicon-lock"> </i> Change Password</a>
									</div>

								</li>
							</ul></li>
					</ul>
				</div>
			</nav>
		</header>

	<div class="wrapper row-offcanvas row-offcanvas-left">
			<!-- Left side column. contains the logo and sidebar -->
			<aside class="left-side sidebar-offcanvas">
				<!-- sidebar: style can be found in sidebar.less -->
				<section class="sidebar">
					<!-- Sidebar user panel -->
					<%-- <div class="user-panel">
                       
                        <div class="pull-left info">
                        <p> Last Login: 2014-06-06 18:22 AM</p>
                        <c:forEach items="${sessionScope.roles}" var="role">
                        	<p>${role.name}</p>
                        </c:forEach>
                            

                           
                        </div>
                    </div> --%>
					<!-- search form -->
					<br />
					<c:url var="action" value="/search/mandate" />
					<form action="${action }" method="post" class="sidebar-form">
						<div class="input-group">
							<input type="text" name="s_mandateCode" value="${s_mandateCode}"
								class="form-control" placeholder="Search Mandate Code" /> <span
								class="input-group-btn">
								<button type='submit' name='seach' id='search-btn'
									class="btn btn-flat">
									<i class="fa fa-search"></i>
								</button>
							</span>
						</div>
					</form>
					<!-- /.search form -->
					<!-- sidebar menu: : style can be found in sidebar.less -->
					<ul class="sidebar-menu">
						<li class="active"><a href="<c:url value="/"/>"> <i
								class="fa fa-dashboard"></i> <span>Home</span>
						</a></li>



						<!-- Biller initiator Menu Items -->
						<sec:authorize ifAllGranted="ROLE_BILLER_INITIATOR">
							<li class="treeview"><a href="#"> <i
									class="fa fa-bar-chart-o"></i> <span>Create Mandate</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>

								<ul class="treeview-menu">


									<li><a href="<c:url value="/biller/mandate/add"/>"><i
											class="fa fa-angle-double-right"></i> Single</a></li>
									<li><a href="<c:url value="/biller/mandate/bulk/add"/>"><i
											class="fa fa-angle-double-right"></i> Bulk Upload</a></li>
								</ul>
							<li class="treeview"><a href="#"> <i
									class="fa fa-laptop"></i><span>View Mandate</span> <i
									class="fa fa-angle-left pull-right"></i></a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/biller/mandate/list"/>"><i
											class="fa fa-angle-double-right"></i> All Mandates</a></li>

									<%-- <li><a
									href="<c:url value="/biller/mandate/list/approved"/>"><i
										class="fa fa-angle-double-right"></i> Awaiting Bank</a></li>
								<li><a
									href="<c:url value="/biller/mandate/list/rejected"/>"><i
										class="fa fa-angle-double-right"></i> Rejected By Bank</a></li> --%>

								</ul></li>
							<li class="treeview"><a href="#"> <i
									class="fa fa-laptop"></i> <span>Products</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">

									<li><a href="<c:url value="/biller/product/list"/>"><i
											class="fa fa-angle-double-right"></i> View Products</a></li>

								</ul></li>
								<li class="treeview"><a href="#"> <i class="fa fa-edit"></i>
									<span>Report</span> <i class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/biller/report"/>"><i
											class="fa fa-angle-double-right"></i> Download</a></li>

								</ul></li>
						</sec:authorize>
						<!-- End of Biller Initiator Menu -->




						<!-- Biller Authroizer Menu Items -->
						<sec:authorize ifAllGranted="ROLE_BILLER_AUTHORIZER">

							<li class="treeview"><a href="#"> <i
									class="fa fa-laptop"></i> <span>View Mandate</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/biller/mandate/list"/>"><i
											class="fa fa-angle-double-right"></i> All Mandates</a></li>
								</ul></li>
							<li class="treeview"><a href="#"> <i class="fa fa-edit"></i>
									<span>Report</span> <i class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/biller/report"/>"><i
											class="fa fa-angle-double-right"></i> Download</a></li>

								</ul></li>


						</sec:authorize>
						<!-- End of Biller Authorizer Menu -->



						<!-- Biller Auditor Menu Items -->
						<sec:authorize ifAllGranted="ROLE_BILLER_AUDITOR">
							<li class="treeview"><a href="#"> <i class="fa fa-edit"></i>
									<span>Report</span> <i class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/biller/report"/>"><i
											class="fa fa-angle-double-right"></i> Download</a></li>

								</ul></li>

						</sec:authorize>

						<!-- End Biller Auditor Menu Items -->



						<!-- Biller Administrator Menu Items -->
						<sec:authorize ifAllGranted="ROLE_BILLER_ADMINISTRATOR">
							<li class="treeview"><a href="#"> <i
									class="fa fa-laptop"></i> <span>Products</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">


									<li><a href="<c:url value="/biller/product/list"/>"><i
											class="fa fa-angle-double-right"></i> Manage Products</a></li>



								</ul></li>
							<li class="treeview"><a href=""> <i class="fa fa-table"></i>
									<span>User Administration</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/biller/user/list"/>"><i
											class="fa fa-angle-double-right"></i> Manage Users</a></li>

								</ul></li>
							<li class="treeview"><a href=""> <i class="fa fa-table"></i>
									<span>Email Configuration</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/biller/notification/config"/>"><i
											class="fa fa-angle-double-right"></i> Email Notifications</a></li>
								</ul></li>

						</sec:authorize>
						<!--  End Biller Administrator menu Item -->



						<!-- Bank Initator Menu Items -->
						<sec:authorize ifAllGranted="ROLE_BANK_INITIATOR">
							<li class="treeview"><a href="#"> <i
									class="fa fa-bar-chart-o"></i> <span>Create Mandate</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>

								<ul class="treeview-menu">
									<li><a href="<c:url value="/bank/mandate/add"/>"><i
											class="fa fa-angle-double-right"></i> Single</a></li>
									 <li><a href="<c:url value="/bank/mandate/bulk/add"/>"><i
										class="fa fa-angle-double-right"></i> Bulk Upload</a></li>

									<%-- 	<li><a href="<c:url value="/bank/mandate/list/approved"/>"><i
										class="fa fa-angle-double-right"></i> Approve</a></li>--%>

								</ul></li>
							<li class="treeview"><a href="#"> <i
									class="fa fa-laptop"></i> <span>View Mandate</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/bank/mandate/list"/>"><i
											class="fa fa-angle-double-right"></i> All Mandates</a></li>
								</ul></li>


						</sec:authorize>
						<!-- End of Bank Initiator Menu -->


						<!-- Bank Authorizer Menu Items -->
						<sec:authorize ifAllGranted="ROLE_BANK_AUTHORIZER">
							<li class="treeview"><a href="#"> <i
									class="fa fa-bar-chart-o"></i> <span>Mandate</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>

								<ul class="treeview-menu">

									<li><a href="<c:url value="/bank/mandate/list"/>"><i
											class="fa fa-angle-double-right"></i> All Mandates</a></li>

								</ul></li>



						</sec:authorize>
						<!-- End of Bank Authorizer Menu -->


						<!-- Bank Auditor Menu Items -->
						<sec:authorize ifAllGranted="ROLE_BANK_AUDITOR">
							<li class="treeview"><a href="#"> <i class="fa fa-edit"></i>
									<span>Report</span> <i class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/bank/report"/>"><i
											class="fa fa-angle-double-right"></i> Download</a></li>

								</ul></li>

						</sec:authorize>

						<!-- End Bank Auditor Menu Items -->



						<!-- Bank Administrator Menu Items -->
						<sec:authorize ifAllGranted="ROLE_BANK_ADMINISTRATOR">

							<li class="treeview"><a href="#"> <i
									class="fa fa-laptop"></i> <span>Billers</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">

									<li><a href="<c:url value="/bank/biller/add"/>"><i
											class="fa fa-angle-double-right"></i> Add Billers</a></li>

									<li><a href="<c:url value="/bank/biller/list"/>"><i
											class="fa fa-angle-double-right"></i> Manage Billers</a></li>
								</ul></li>


							<li class="treeview"><a href=""> <i class="fa fa-table"></i>
									<span>User Administration</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/bank/user/list"/>"><i
											class="fa fa-angle-double-right"></i> Manage Users</a></li>
								</ul></li>
							<li class="treeview"><a href=""> <i class="fa fa-table"></i>
									<span>Notification</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/bank/notification/config"/>"><i
											class="fa fa-angle-double-right"></i>Email Configuration</a></li>
								</ul></li>

						</sec:authorize>
						<!--  End Bank Administrator menu Item -->


						<!-- NIBSS Administrator Menu Items -->
						<sec:authorize ifAllGranted="ROLE_NIBSS_ADMINISTRATOR">
							<li class="treeview"><a href="#"> <i
									class="fa fa-laptop"></i> <span>View Mandate</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/nibss/mandate/list"/>"><i
											class="fa fa-angle-double-right"></i> All Mandates</a></li>


								</ul></li>
							<li class="treeview"><a href="#"> <i class="fa fa-table"></i>
									<span>Institutions</span> <i
									class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/nibss/biller/list"/>"><i
											class="fa fa-angle-double-right"></i> Billers</a></li>
									<li><a href="<c:url value="/nibss/company/list"/>"><i
											class="fa fa-angle-double-right"></i> All Organizations</a></li>
									<li><a
										href="<c:url value="/nibss/notification/config/list"/>"><i
											class="fa fa-angle-double-right"></i> Email Configuration</a></li>
								</ul></li>

							<li class="treeview"><a href="#"> <i class="fa fa-table"></i>
									<span>Users</span> <i class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/nibss/user/list"/>"><i
											class="fa fa-angle-double-right"></i> All Users</a></li>
								</ul></li>

							<li class="treeview"><a href="#"> <i class="fa fa-edit"></i>
									<span>Report</span> <i class="fa fa-angle-left pull-right"></i>
							</a>
								<ul class="treeview-menu">
									<li><a href="<c:url value="/nibss/report"/>"><i
											class="fa fa-angle-double-right"></i> Mandate</a></li>

								</ul></li>
						<li><a href="<c:url value="/nibss/transaction/billing"/>"> <i class="fa fa-money"></i>
									<span>Billing</span> 
									</a>
							</li>

								

						</sec:authorize>
					</ul>
					<!-- End of NIBSS Administrator Menu -->
				</section>
				<!-- /.sidebar -->
			</aside>
			<!-- Right side column. Contains the navbar and content of the page -->
			
			<aside class="right-side">
				<!-- Content Header (Page header) -->
				
				<section class="content-header">
					<h1>${main_header}</h1>
					<ol class="breadcrumb">
						<li><a href="<c:url value="/"/>"><i
								class="fa fa-dashboard"></i> Home</a></li>
						<c:forEach items="${breadCrumb}" var="breadCrumbItem">
							<li><a href="<c:url  value="${breadCrumbItem.id }"/>">${breadCrumbItem.value}</a></li>
						</c:forEach>

					</ol>
				</section>


				<!-- Modal -->
				<div id="change-password-modal-dialog" class="modal">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<a href="#" data-dismiss="modal" aria-hidden="true"
									class="close">×</a>
								<h3 id="modal-header-name">Change Password</h3>
							</div>
							<div class="modal-body">
								<div id="login-responseMessage"></div>
								<form method="POST" id="change-password-modal-form"
									action="<c:url value='/user/passwordChange'/>">

									<div class="form-group col-md-12">

										<label for="oldPassword">Old Password</label> <input
											type="password" id="oldPassword" name="oldPassword"
											class="form-control" required="required" />

									</div>

									<div class="form-group col-md-12">

										<label for="newPassword">New Password</label> <input
											type="password" id="newPassword" name="newPassword"
											class="form-control" required="required" />

									</div>

									<div class="form-group col-md-12">

										<label for="cNewPassword">Confirm New Password</label> <input
											type="password" id="cNewPassword" name="cNewPassword"
											class="form-control" required="required" />

									</div>
									<div class="modal-footer">
										<button type="submit" id="submit-change-password"
											class="btn btn-success confirm">Submit</button>
										<a href="#" data-dismiss="modal" aria-hidden="true"
											class="btn btn-danger secondary">Cancel</a>
									</div>
								</form>
							</div>
						</div>
					</div>

				</div>
				<!-- /Modal -->
				<jsp:doBody />



			</aside>
			<!-- /.right-side -->
		
		<!-- jQuery 2.0.2 -->
		<c:if test="${isModal ne true}">
		<script
			src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
		<script src="<c:url value="/resources/js/jquery.min.js"/>"></script>
		<script type="text/javascript"
			src="<c:url value="/resources/js/jquery-ui-1.9.2.custom.min.js"/>"></script>



		<!-- Bootstrap -->
		<script src="<c:url value="/resources/js/bootstrap.min.js"/>"></script>
		<script src="<c:url value="/resources/js/bootstrap-switch.js"/>"></script>
		<script src="<c:url value="/resources/js/select2.full.min.js"/>"></script>
		<script
			src="<c:url value="/resources/js/plugins/customd-jquery-number-2.1.3/jquery.number.min.js"/>"></script>


		<!-- AdminLTE App -->
		<script src="<c:url value="/resources/js/AdminLTE/app.js"/>"></script>


		<!-- Morris.js charts -->
		<script
			src="//cdnjs.cloudflare.com/ajax/libs/raphael/2.1.0/raphael-min.js"></script>
		<script
			src="<c:url value="/resources/js/plugins/morris/morris.min.js"/>"></script>
		<!-- Sparkline -->
		<script
			src="<c:url value="/resources/js/plugins/sparkline/jquery.sparkline.min.js"/>"></script>
		<!-- jvectormap -->
		<script
			src="<c:url value="/resources/js/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js"/>"></script>
		<script
			src="<c:url value="/resources/js/plugins/jvectormap/jquery-jvectormap-world-mill-en.js"/>"></script>
		<!-- jQuery Knob Chart -->
		<script
			src="<c:url value="/resources/js/plugins/jqueryKnob/jquery.knob.js"/>"></script>
		<!-- daterangepicker -->
		<script
			src="<c:url value="/resources/js/plugins/daterangepicker/daterangepicker.js"/>"></script>
		<!-- datepicker -->
		<script
			src="<c:url value="/resources/js/plugins/datepicker/bootstrap-datepicker.js"/>"></script>
		<!-- Bootstrap WYSIHTML5 -->
		<script
			src="<c:url value="/resources/js/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js"/>"></script>
		<!-- iCheck -->
		<script
			src="<c:url value="/resources/js/plugins/iCheck/icheck.min.js"/>"></script>

		<!-- iCheck -->
		<script
			src="<c:url value="/resources/js/plugins/jquery.easydrag.js"/>"></script>


		<!-- JQuery Validate -->
		<script
			src="<c:url value="/resources/js/plugins/validate/jquery.validate.js"/>"></script>

		<!-- datatables -->
		<script
			src="<c:url value="/resources/js/plugins/datatables/jquery.dataTables.js"/>"></script>

		<script
			src="<c:url value="/resources/js/plugins/datatables/extensions/TableTools/js/dataTables.tableTools.min.js"/>"></script>


		<script
			src="<c:url value="/resources/js/plugins/datatables/dataTables.bootstrap.js"/>"></script>

		<script
			src="<c:url value="/resources/js/plugins/datatables/dataTables.server.export.js"/>"></script>



		<script
			src="<c:url value="/resources/js/plugins/datatables/extensions/Editor-1.4.0/js/dataTables.editor.1.3.1.js"/>"></script>


		<script type="text/javascript"
			src="<c:url value="/resources/js/fancybox/jquery.fancybox.pack.js?v=2.1.5"/>"></script>

		<script src="<c:url value="/resources/js/blockUI/jquery.blockUI.js"/>"></script>

		<script
			src="<c:url value="/resources/js/plugins/jquery.rotate-1.0.1.js"/>"></script>

		<script src="<c:url value="/resources/js/cmms.js"/>"></script>

		<script type="text/javascript">
			Links.LoadingImageUrl = "<c:url value="/resources/img/ajax-loader.gif" />";
			
		</script>
	</c:if>
		<!-- (Ajax Modal)-->
		<div class="modal fade" id="modal_ajax">
			<div class="modal-dialog">
				<div class="modal-content">

					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h4 class="modal-title">
							
						</h4>
					</div>

					<div class="modal-body" style="min-height:200px; overflow: auto;"></div>
					<div class="modal-footer">
						<button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>

		<!-- (Delete Modal)-->
		<div class="modal fade" id="modal-4">
			<div class="modal-dialog">
				<div class="modal-content" style="margin-top:100px;">

					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">&times;</button>
						<h4 class="modal-title" style="text-align: center;">Are you
							sure to delete this record ?</h4>
					</div>

					<div class="modal-footer"
						style="margin: 0px; border-top: 0px; text-align: center;">
						<a href="#" class="btn btn-danger" id="delete_link">
							Delete
						</a>
						<button type="button" class="btn btn-info" data-dismiss="modal">
							Cancel
						</button>
					</div>
				</div>
			</div>
		</div>
		
		<div class="modal fade" id="notification-modal">
			<div class="modal-dialog">
				<div class="modal-content" style="margin-top:100px;">
					<div class="modal-body text-center" style=" overflow: auto;"></div>
					<div class="modal-footer">
						<button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>

		<c:if test="${not empty pageFragment}">
			<jsp:invoke fragment="pageFragment" />
		</c:if>

	</div>
	<!-- ./wrapper -->

</body>
</html>