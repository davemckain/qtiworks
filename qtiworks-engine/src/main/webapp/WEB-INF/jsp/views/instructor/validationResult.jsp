<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Validation results

Model:

validationResult (AssessmentObjectValidationResult)

--%>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Validation result">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Assessment Manager</a> &#xbb;
    </nav>
    <h2>
      <span class="assessmentLabel">Assessment&#xa0;${utils:formatAssessmentType(assessment)}</span>
      <a href="${utils:escapeLink(assessmentRouting['show'])}">${fn:escapeXml(utils:formatAssessmentFileName(assessmentPackage))}</a>
      &#xbb; Validation Status
    </h2>
    <div class="hints">
      <p>
        This page shows the results of passing this Assessment through the QTIWorks QTI validation
        process.
      </p>
    </div>
  </header>
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
  <p>
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Return to Assessment</a>
  </p>

</page:page>
