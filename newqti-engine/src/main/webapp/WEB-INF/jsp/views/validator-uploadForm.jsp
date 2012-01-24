<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--

Validator submission form

--%>
<c:set var="title" value="QTI Validator" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<h2>QTI validation service</h2>

<p>
  This service checks your QTI AssessmentTests and AssessmentItems to make sure they will work
  correctly and point out some pitfalls you might want to avoid.
</p>
<p>
  You can upload any of the following to the validator:
</p>
<ul>
  <li>A standalone QTI 2.1 AssessmentItem XML file.</li>
  <li>An IMS Content Package containing a QTI 2.1 AssessmentItem (plus any related resources, such as images, response processing templates...)</li>
  <li>An IMS Content Package containing a QTI 2.1 AssessmentTest and corresponding AssessmentItems and related resources.</li>
</ul>

<h2>Upload</h2>

<form:form id="validatorForm" method="post" acceptCharset="UTF-8" enctype="multipart/form-data">
  <dl>
    <dt><label for="submissionFile">Select XML File or Content Package to upload:</label></dt>
    <dd>
      &#x200a;<input type="file" id="upload" name="upload">
    </dd>
  </dl>
  <div class="controls">
    <input id="submit" name="submit" type="submit" value="Validate">
  </div>
</form:form>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
