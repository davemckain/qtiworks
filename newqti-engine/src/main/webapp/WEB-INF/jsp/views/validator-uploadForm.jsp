<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--

Validator service submission form

Model attributes:

validateCommand:

--%>
<c:set var="title" value="QTI Validator" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<h2>QTI validation service (demo!)</h2>

<p>
  This demo validates your QTI Assessment Tests and Assessment Items to check whether
  they will work correctly, giving you (hopefully) helpful information to help you fix
  any issues that are discovered.
</p>

<div class="validatorUpload">
  <form:form id="validatorForm" method="post" acceptCharset="UTF-8" enctype="multipart/form-data" commandName="validateCommand">

    <%-- Show any validation errors discovered --%>
    <form:errors element="div" cssClass="error" path="*"/>

    <dl>
      <dt><label for="submissionFile">1. Select a Content Package or Assessment Item XML file to upload and validate:</label></dt>
      <dd>
        <form:input path="uploadFile" type="file"/>
      </dd>
      <dt><label for="reportType">2. Select report type:</label></dt>
      <dd>
        <form:select path="reportType">
          <form:option value="HTML" label="Nice(ish) web report" />
          <form:option value="JAVA" label="Java Object dump" />
        </form:select>
      </dd>
      <dt><label for="submit">3. Hit "Upload and Validate!"</label></dt>
      <dd>
        <input id="submit" name="submit" type="submit" value="Upload and Validate!">
      </dd>
    </dl>
  </form:form>
</div>

<h3>What can I upload?</h3>
<p>
  You may upload any of the following to the validator:
</p>
<ul>
  <li>An IMS Content Package containing a QTI 2.1 AssessmentItem (plus any related resources, such as images, response processing templates...)</li>
  <li>An IMS Content Package containing a QTI 2.1 AssessmentTest and corresponding AssessmentItems and related resources.</li>
  <li>A self-contained QTI 2.1 AssessmentItem XML file.</li>
</ul>

<h3>Current limitations</h3>
<ul>
  <li>
    This currently does <strong>not</strong> validate the structure and manifest of IMS content packages themselves.
    I'm not sure I'll ever add support for that, as that is going off on a bit of a tangent...
  </li>
  <li>
    The validator currently doesn't load items or response processing templates from arbitrary HTTP locations.
    (It does support the standard QTI 2.1 RP templates, however.)
  </li>
  <li>
    I haven't added support for QTI 2.0 yet. I will do if there's demand.
  </li>
</ul>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
