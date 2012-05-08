<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Validation results

Model attributes:

result (AssessmentUploadAndValidationResultV1)

--%>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="QTI Validator Result">

  <c:set var="assessmentPackage" value="${result.assessmentPackage}"/>
  <c:set var="validationResult" value="${result.validationResult}"/>
  <c:set var="assessmentType" value="${validationResult.resolvedAssessmentObject.type}"/>

  <p>You uploaded an ${assessmentType}, uploaded as ${assessmentPackage.importType}.</p>

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
