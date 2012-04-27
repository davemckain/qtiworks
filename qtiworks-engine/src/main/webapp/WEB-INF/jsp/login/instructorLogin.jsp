<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<form action="<c:url value='/instructorFormAuthenticator' />" method="post">
  <h2>Instructor Login</h2>

  <c:if test="${!empty errors}">
    <ul class="errors">
      <c:forEach var="e" items="${errors}">
        <li>${e}</li>
      </c:forEach>
    </ul>
  </c:if>

  <div class="row">
    <label for="userId">Login ID:</label>
    <input size="8" id="userId" name="userId" type="text" />
  </div>
  <div class="row">
    <label for="password">Password:</label>
    <input size="8" id="password" name="password" type="password" />
  </div>
  <div class="controls">
    <input type="hidden" id="protectedRequestUrl" name="protectedRequestUrl"
      value="${fn:escapeXml(requestScope['qtiworks.web.authn.protectedRequestUrl'])}" />
    <input type="submit" value="Login" />
    <input type="reset"  value="Clear" />
  </div>
</form>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
