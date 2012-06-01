<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Validation results

Model:

* validationResult (AssessmentObjectValidationResult)
* assessmentId (Long)

--%>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Validation result">

  <c:set var="assessmentType" value="${validationResult.resolvedAssessmentObject.type}"/>

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

</page:page>
