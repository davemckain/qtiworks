<%--

Generates a POST button

Copyright (c) 2013, The University of Edinburgh.
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
<%@ attribute name="cssClass" required="false" type="java.lang.String" %>

<c:set var="actionUrl" value="${utils:escapeLink(path)}"/>
<form action="${actionUrl}" method="post" class="buttonLink">
  <button type="submit" class="${cssClass ? cssClass : 'playButton'}">${fn:replace(title, ' ', '&#xa0;')}</button>
</form>
<c:if test="${!empty confirm && confirmCondition==true}">
  <script>
    $("form[action='${actionUrl}']").submit(function() {
      return confirm('${confirm}');
    });
  </script>
</c:if>
