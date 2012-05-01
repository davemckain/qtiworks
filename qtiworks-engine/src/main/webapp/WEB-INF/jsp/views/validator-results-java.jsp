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

assessmentUpload

--%>
<c:set var="assessmentPackage" value="${assessmentUpload.assessmentPackage}"/>
<c:set var="assessmentType" value="${assessmentPackage.assessmentType}"/>
<c:set var="validationResult" value="${assessmentUpload.validationResult}"/>

<%-- Generate header --%>
<c:set var="title" value="QTI Validator" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<h2>Validation result (Java Object dump)</h2>

<p>
  This is the Java Object graph generated from the upload and validation process,
  which will be useful for developers and geeky types.
</p>
<pre>
<c:out value="${utils:dumpObject(assessmentUpload)}"/>
</pre>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
