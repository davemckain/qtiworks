<%--

This fragment formats the results of validating an AssessmentTest

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ attribute name="validationResult" required="true" type="uk.ac.ed.ph.jqtiplus.validation.TestValidationResult" %>

<c:set var="resolvedAssessmentTest" value="${validationResult.resolvedAssessmentTest}"/>
<c:set var="errorWord">
  <c:choose>
    <c:when test="${validationResult.valid}">success</c:when>
    <c:when test="${validationResult.validationWarningsOnly}">warnings</c:when>
    <c:otherwise>failure</c:otherwise>
  </c:choose>
</c:set>

<div class="resultPanel ${errorWord}">
  <h4>Test validation ${errorWord}</h4>
  <div class="details">
    <p>
      Your test
      ${validationResult.valid ? 'passed' : 'did not pass'}
      all of our validation checks. Further details are provided below:
    </p>
    <c:set var="rootNodeLookup" value="${resolvedAssessmentTest.rootNodeLookup}"/>
    <validator:xmlParseResults rootNodeLookup="${rootNodeLookup}"/>
    <validator:xmlSchemaValidationResults rootNodeLookup="${rootNodeLookup}"/>
    <validator:modelBuildResults rootNodeLookup="${rootNodeLookup}"/>
    <validator:modelValidationResults validationResult="${validationResult}"/>

    <%-- Show item results --%>
    <c:forEach var="itemValidationResult" items="${validationResult.itemValidationResults}">
      <validator:itemValidationResults validationResult="${itemValidationResult}" test="${true}"/>
    </c:forEach>
  </div>
</div>
