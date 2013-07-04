<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Validation results

Additional Model atttributes:

validationResult (AssessmentObjectValidationResult)
assessmentRouting (action -> URL)

--%>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Validation result">

  <nav class="breadcrumbs">
    <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">This Assessment launch</a></li> &#xbb;
    <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Assessment library</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment '${fn:escapeXml(assessment.name)}'</a>
  </nav>
  <h2>Assessment Validation Status</h2>

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
