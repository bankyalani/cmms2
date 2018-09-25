<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="status" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>


<!-- Modal -->
<div>
<div id="title" class="hidden">${modal_header}</div>
	<div>
		<div >
			<div >
				
				<c:url var="action" value="/biller/product/edit" />
				<form:form method="POST" commandName="product"
					modelAttribute="product" action="${action}">

					<%-- 	<form:errors path="*" cssClass="alert alert-danger" element="div" /> --%>

					<div class="form-group col-md-12">
						<form:label path="name">Product Name</form:label>
						<form:input path="name" type="text" cssClass="form-control"
							id="name" placeholder="Enter Product Name" required="required" />
							<form:hidden path="id"/>
							

					</div>
					<div class="form-group col-md-12">
						<form:label path="description">Product Description</form:label>
						<form:input path="description" type="text" cssClass="form-control"
							placeholder="Enter Product description" required="true" />

					</div>

					<div class="form-group col-md-12">
						<form:label for="amount" path="amount">Amount</form:label>
						<form:input type="text" cssClass="form-control" id="amount"
							path="amount"></form:input>
					</div>
					
					<div class="form-group col-md-12">
						<form:label for="status" path="status" >Status</form:label>
						<form:select path="status" cssClass="form-control">
							<form:option value="1">Active</form:option>
							<form:option value="0">Inactive</form:option>
						</form:select>
					</div>
					
					<div>
				<button type="submit" class="btn btn-success">Save</button>
			</div>
				</form:form>
			</div>
			

		</div>
	</div>
</div>
