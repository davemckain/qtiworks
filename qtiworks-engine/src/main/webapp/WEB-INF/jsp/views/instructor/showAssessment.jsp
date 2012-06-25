<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Instructor upload assessment package form

Model:

assessment
assessmentPackage (most recent)
assessmentRouting (action -> URL)
instructorAssessmentRouting (action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Assessment details">

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

  <h3>Actions</h3>

  <ul>
    <li><a href="${utils:escapeLink(assessmentRouting['upload'])}">Upload and replace package files</a></li>
    <li><a href="${utils:escapeLink(assessmentRouting['validate'])}">Show validation status</a></li>
    <li><a href="${utils:escapeLink(instructorAssessmentRouting['listAssessments'])}">Your assessments</a></li>
  </ul>
</page:page>
