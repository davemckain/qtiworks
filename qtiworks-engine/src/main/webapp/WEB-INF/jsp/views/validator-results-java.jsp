<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
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

<h2>Validation result (Java Object dump)</h2>

<p>
  This is the Java Object graph generated from the upload and validation process,
  which will be useful for developers and geeky types.
</p>
<pre>
<c:out value="${utils:dumpObject(assessmentUploadAndValidationResultV1)}"/>
</pre>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
