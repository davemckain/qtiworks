<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Validation results (only shown if invalid)

Model attributes:

validationResult (AssessmentObjectValidationResult)

--%>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="QTI Validator Result">

  <p>
    The assessment you uploaded failed our validation checks so was not run.
    Validation errors are shown below.
  </p>

  <c:set var="assessmentType" value="${validationResult.resolvedAssessmentObject.type}"/>
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
