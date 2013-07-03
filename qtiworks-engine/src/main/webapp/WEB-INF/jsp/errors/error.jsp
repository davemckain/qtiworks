<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Generic error page

Model:
status
reason
exception

--%>
<%@ page isErrorPage="true" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>

<%-- The following data should always be present --%>
<c:set var="statusCode" value="${pageContext.errorData.statusCode}"/>
<c:set var="servletName" value="${requestScope['javax.servlet.error.servlet_name']}"/>
<c:set var="requestUri" value="${requestScope['javax.servlet.error.request_uri']}"/>
<%-- The following are only set in certain cases --%>
<c:set var="message" value="${requestScope['javax.servlet.error.message']}"/>
<c:set var="exception" value="${pageContext.errorData.throwable}"/>

<%-- Load readable error messages --%>
<fmt:setLocale value="en"/>
<fmt:setBundle basename="errors" var="errors"/>

<%-- Extract error title and description --%>
<fmt:message var="errorTitle" key="title${statusCode}" bundle="${errors}"/>
<fmt:message var="errorDescription" key="description${statusCode}" bundle="${errors}"/>

<page:page title="${errorTitle}">

  <h2><c:out value="${errorTitle}"/></h2>

  <%-- NB: The following resources are already HTML so we don't escape these --%>
  <c:out escapeXml="false" value="${errorDescription}"/>

  <h3>Further Details</h3>
  <c:if test="${!empty message}">
    <strong>Message:</strong> <c:out value="${message}"/><br>
  </c:if>
  <strong>Status Code:</strong> <c:out value="${statusCode}"/><br>
  <strong>Request URI:</strong> <c:out value="${requestUri}"/><br>
  <c:if test="${!empty exception}">
    <c:set var="ex" value="${exception}"/>
    <%-- Unwind Exceptions trapped by Spring --%>
    <c:if test="${ex['class'].name == 'org.springframework.web.util.NestedServletException'}">
      <c:set var="ex" value="${ex.cause}"/>
    </c:if>
    <%-- Show Exception --%>
    <br><strong>Exception:</strong> <c:out value="${ex}"/><br>
    <c:forEach var="stackTraceItem" items="${ex.stackTrace}">
      <c:out value="${stackTraceItem}"/><br>
    </c:forEach>

    <%-- Unwind cause (up to 5 times since no while loop, but this will do!) --%>
    <c:set var="cause" value="${ex.cause}"/>
    <c:forEach begin="1" end="5" varStatus="loop">
      <c:if test="${!empty cause}">
        <br><strong>Cause ${loop.index}:</strong> <c:out value="${cause}"/><br>
        <c:forEach var="stackTraceItem" items="${cause.stackTrace}">
          <c:out value="${stackTraceItem}"/><br>
        </c:forEach>
        <c:set var="cause" value="${cause.cause}"/>
      </c:if>
    </c:forEach>
  </c:if>

</page:page>
