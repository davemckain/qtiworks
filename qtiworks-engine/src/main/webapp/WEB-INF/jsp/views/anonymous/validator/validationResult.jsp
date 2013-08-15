<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Validation results

Model attributes:

validationResult (AssessmentObjectValidationResult)

--%>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="QTI Validator Result">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/public/')}">Demos</a> &#xbb;
    <a href="${utils:internalLink(pageContext, '/anonymous/validator')}">QTI Validator</a> &#xbb;
  </nav>
  <h2>QTI Validation Result</h2>

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
