<%--

This fragment formats the results of validating an AssessmentItem

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ attribute name="validationResult" required="true" type="uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult" %>
<%@ attribute name="test" required="false" type="java.lang.Boolean" %>

<%-- Show overall result --%>
<c:set var="resolvedAssessmentItem" value="${validationResult.resolvedAssessmentItem}"/>
<c:set var="itemLookup" value="${resolvedAssessmentItem.rootNodeLookup}"/>
<c:set var="itemSystemId" value="${itemLookup.systemId}"/>
<div class="resultPanel ${validationResult.valid ? 'success' : 'failure'}">
  <h4>
    <c:choose>
      <c:when test="${test}">
        Test Item at ${utils:extractContentPackagePath(itemSystemId)} validation
        ${validationResult.valid ? 'success' : 'failure'}
      </c:when>
      <c:otherwise>
        Item validation
        ${validationResult.valid ? 'success' : 'failure'}
      </c:otherwise>
    </c:choose>
  </h4>
  <div class="details">
    <p>
      <c:choose>
        <c:when test="${test}">
          The item at path ${utils:extractContentPackagePath(itemSystemId)}
          referenced by this test
          ${validationResult.valid ? 'passed' : 'did not pass'}
          all of our validation checks. Further details are provided below.
          (This prevents the test itself from being valid.)
        </c:when>
        <c:otherwise>
          Your item
          ${validationResult.valid ? 'passed' : 'did not pass'}
          all of our validation checks. Further details are provided below.
        </c:otherwise>
      </c:choose>
    </p>
    <c:if test="${test}">
      <validator:xmlFindResult rootNodeLookup="${itemLookup}"/>
    </c:if>
    <validator:xmlParseResults rootNodeLookup="${itemLookup}"/>
    <validator:xmlSchemaValidationResults rootNodeLookup="${itemLookup}"/>
    <c:set var="resolvedResponseProcessingTemplateLookup" value="${resolvedAssessmentItem.resolvedResponseProcessingTemplateLookup}"/>
    <c:if test="${resolvedResponseProcessingTemplateLookup!=null}">
      <c:set var="templateLookup" value="${resolvedAssessmentItem.resolvedResponseProcessingTemplateLookup}"/>
      <c:choose>
        <c:when test="${templateLookup.rootNodeHolder!=null}">
          <div class="resultPanel success">
            <h4>The referenced response processing template was successfully resolved</h4>
            <div class="details">
              <%-- (These are in context of RP) --%>
              <validator:xmlFindResult rootNodeLookup="${templateLookup}"/>
              <validator:xmlParseResults rootNodeLookup="${templateLookup}"/>
              <validator:xmlSchemaValidationResults rootNodeLookup="${templateLookup}"/>
            </div>
          </div>
        </c:when>
        <c:otherwise>
          <div class="resultPanel failure">
            <h4>The referenced response processing template was not successfully resolved</h4>
            <div class="details">
              <p>
                We could not read in and parse the response processing template with href
                <b>${utils:extractContentPackagePath(resolvedResponseProcessingTemplateLookup.systemId)}</b>.
              </p>
              <%-- (These are in context of RP) --%>
              <validator:xmlFindResult rootNodeLookup="${templateLookup}"/>
              <validator:xmlParseResults rootNodeLookup="${templateLookup}"/>
              <validator:xmlSchemaValidationResults rootNodeLookup="${templateLookup}"/>
            </div>
          </div>
        </c:otherwise>
      </c:choose>
    </c:if>
    <validator:modelBuildResults rootNodeLookup="${itemLookup}"/>
    <validator:modelValidationResults validationResult="${validationResult}"/>
  </div>
</div>
