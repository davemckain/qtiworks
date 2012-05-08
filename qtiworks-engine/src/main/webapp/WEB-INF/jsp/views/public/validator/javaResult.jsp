<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Validation result (Java)

Model attributes:

result (AssessmentUploadAndValidationResultV1)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="QTI Validator Result">

  <h2>Validation result (Java Object dump)</h2>
  <p>
    This is the Java Object graph generated from the upload and validation process,
    which will be useful for developers and geeky types.
  </p>
  <pre>
  <c:out value="${utils:dumpObject(assessmentUploadAndValidationResultV1)}"/>
  </pre>

</page:page>
