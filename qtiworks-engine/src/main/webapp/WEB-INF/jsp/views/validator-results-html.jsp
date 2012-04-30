<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<utils:exposeStaticFields className="uk.ac.ed.ph.qtiworks.web.domain.AssessmentPackageV1$AssessmentType" targetName="AssessmentType" />
<%--

Validator submission form

Model attributes:

assessmentUpload

--%>
<c:set var="assessmentPackage" value="${assessmentUpload.assessmentPackage}"/>
<c:set var="assessmentType" value="${assessmentPackage.assessmentType}"/>
<c:set var="validationResult" value="${assessmentUpload.validationResult}"/>

<%-- Generate header --%>
<c:set var="title" value="QTI Validator" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<p>You uploaded a ${assessmentType}, uploaded as ${assessmentUpload.uploadType}.</p>

<div class="validationResult">
  <c:choose>
    <c:when test="${assessmentType==AssessmentType.ITEM}">
      <%@ include file="validator-item-result.jspf" %>
    </c:when>
    <c:when test="${assessmentType==AssessmentType.TEST}">
      <c:set var="testValidationResult" value="${validationResult}"/>
      <%@ include file="validator-test-result.jspf" %>
    </c:when>
    <c:otherwise>
      <%-- Blow up! --%>
    </c:otherwise>
  </c:choose>
</div>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
