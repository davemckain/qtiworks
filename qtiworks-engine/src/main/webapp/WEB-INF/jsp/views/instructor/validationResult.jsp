<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%--

Validation results

Model:

validationResult (AssessmentObjectValidationResult)
assessmentId (Long)


--%>
<c:set var="assessmentType" value="${validationResult.resolvedAssessmentObject.type}"/>

<%-- Generate header --%>
<c:set var="title" value="QTI Validator" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<h3>Actions</h3>
<ul>
  <li><a href="<c:url value='/web/instructor/assessment/${assessmentId}'/>">Back to assessment</a></li>
</ul>

<h3>Results</h3>

<div class="validationResult">
  <c:choose>
    <c:when test="${assessmentType=='ASSESSMENT_ITEM'}">
      <validator:itemValidationResults validationResult="${validationResult}"/>
    </c:when>
    <c:when test="${assessmentType=='ASSESSMENT_TEST'}">
      <validator:testValidationResults validationResult="${validationResult}"/>
    </c:when>
    <c:otherwise>
      <%-- Blow up! --%>
    </c:otherwise>
  </c:choose>
</div>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
