<%--

Generates a POST link using a form and some JavaScript to make it look
like a normal link

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="path" required="true" type="java.lang.String" %>
<%@ attribute name="title" required="true" type="java.lang.String" %>
<%@ attribute name="confirm" required="false" type="java.lang.String" %>
<%@ attribute name="confirmCondition" required="false" type="java.lang.Boolean" %>

<c:set var="actionUrl" value="${utils:escapeLink(path)}"/>
<form action="${actionUrl}" method="post" class="postLink">
  <input type="submit" value="${fn:escapeXml(title)}">
</form>
<c:if test="${!empty confirm && (empty confirmCondition || confirmCondition==true)}">
  <script>
    $("form[action='${actionUrl}']").submit(function() {
      return confirm('${confirm}');
    });
  </script>
</c:if>
