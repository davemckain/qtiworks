<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<utils:exposeStaticFields className="uk.ac.ed.ph.qtiengine.web.domain.AssessmentPackage$AssessmentType" targetName="AssessmentType" />
<%--

Validator submission form

Model attributes:

assessmentUpload

--%>
<c:set var="title" value="QTI Validator" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<h2>Upload &amp; validation result</h2>

<p>You uploaded a ${assessmentUpload.assessmentPackage.assessmentType}, uploaded as
${assessmentUpload.uploadType}.</p>

<%-- Show main result --%>
<c:set var="validationResult" value="${assessmentUpload.validationResult}"/>
<c:set var="showLookupSearch" value="${false}"/>
<div class="validationResult">
  <%@ include file="validator-single-result.jspf" %>
</div>

<%-- If test, show child results --%>
<c:if test="${assessmentUpload.assessmentPackage.assessmentType==AssessmentType.TEST}">
  <c:forEach var="validationResult" items="${assessmentUpload.validationResult.itemValidationResults}">
    <c:set var="showLookupSearch" value="${true}"/>
    <%@ include file="validator-single-result.jspf" %>
  </c:forEach>
</c:if>

<h2>Java Object dump (geeks only!)</h2>
<p>
  This is a temporary dump of the Java Object graph generated from the upload and validation process,
  which will be useful for developers but is likely to scare everyone else away!
</p>
<pre>
${utils:dumpObject(assessmentUpload)}
</pre>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
