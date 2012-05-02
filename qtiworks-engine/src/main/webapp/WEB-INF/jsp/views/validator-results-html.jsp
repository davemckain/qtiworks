<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<utils:exposeStaticFields className="uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType" targetName="AssessmentObjectType" />
<%--

Validator submission form

Model attributes:

assessmentUploadAndValidationResultV1

--%>
<c:set var="assessmentPackage" value="${assessmentUploadAndValidationResultV1.assessmentPackage}"/>
<c:set var="assessmentType" value="${assessmentPackage.assessmentType}"/>
<c:set var="validationResult" value="${assessmentUploadAndValidationResultV1.validationResult}"/>

<%-- Generate header --%>
<c:set var="title" value="QTI Validator" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<p>You uploaded a ${assessmentType}, uploaded as ${assessmentPackage.importType}.</p>

<div class="validationResult">
  <c:choose>
    <c:when test="${assessmentType==AssessmentObjectType.ASSESSMENT_ITEM}">
      <%@ include file="validator-item-result.jspf" %>
    </c:when>
    <c:when test="${assessmentType==AssessmentObjectType.ASSESSMENT_TEST}">
      <c:set var="testValidationResult" value="${validationResult}"/>
      <%@ include file="validator-test-result.jspf" %>
    </c:when>
    <c:otherwise>
      <%-- Blow up! --%>
    </c:otherwise>
  </c:choose>
</div>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
