<%@ tag import="org.springframework.util.StringUtils"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ attribute name="status" required="true" type="java.lang.String"
	description="Text to use in the second cell."%>
<%@ attribute name="icon" required="false" type="java.lang.Boolean"
	description="Use icon"%>



<c:choose>
	<c:when test="${status eq 1 }">
		<c:choose>
			<c:when test="${icon eq true }">
				<i class="glyphicon glyphicon-ok text-success"></i>
			</c:when>
			<c:otherwise>
			Active
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${icon eq true }">
				<i class="glyphicon glyphicon-remove-2 text-danger"></i>
			</c:when>
			<c:otherwise>
				Inactive
				</c:otherwise>
		</c:choose>
	</c:otherwise>

</c:choose>