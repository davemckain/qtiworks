<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Instructor upload assessment package form

Model:

- assessment
- assessmentPackage (most recent)

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="title" value="Upload Assessment" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<h2>Assessment details</h2>

<dl>
  <dt>Name:</dt>
  <dd><c:out value="${assessment.name}"/></dd>

  <dt>Title</dt>
  <dd><c:out value="${assessment.title}"/></dd>

  <dt>Package Import Version:</dt>
  <dd>${assessment.packageImportVersion}</dd>

  <dt>Type:</dt>
  <dd>${assessment.assessmentType}</dd>
</dl>

<h3>Current package content</h3>

<dl>
  <dt>Version:</dt>
  <dd>${assessmentPackage.importVersion}</dd>

  <dt>Uploaded:</dt>
  <dd>${assessmentPackage.creationTime}</dd>

  <dt>Import type:</dt>
  <dd>${assessmentPackage.importType}</dd>

  <dt>Validated?</dt>
  <dd>${assessmentPackage.validated}</dd>

  <dt>Valid?</dt>
  <dd>${assessmentPackage.valid}</dd>
</dl>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>

