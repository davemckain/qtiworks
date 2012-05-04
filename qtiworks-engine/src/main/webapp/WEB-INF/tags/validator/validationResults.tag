<%--

This fragment formats the results of validating an AssessmentObject

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ attribute name="validationResult" required="true" type="uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult" %>

<c:set var="assessmentType" value="${validationResult.resolvedAssessmentObject.type}"/>
<div class="validationResult">
  <c:choose>
    <c:when test="${assessmentType=='ASSESSMENT_ITEM'}">
      <validator:itemValidationResults validationResult="${validationResult}"/>
    </c:when>
    <c:when test="${assessmentType=='ASSESSMENT_TEST'}">
      <c:set var="testValidationResult" value="${validationResult}"/>
      <validator:testValidationResults validationResult="${validationResult}"/>
    </c:when>
    <c:otherwise>
      <%-- Blow up! --%>
    </c:otherwise>
  </c:choose>
</div>
