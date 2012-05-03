<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Instructor upload assessment package form

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="title" value="Upload Assessment" />
<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>

<h2>Upload Assessment</h2>

<form:form method="post" acceptCharset="UTF-8" enctype="multipart/form-data" commandName="uploadAssessmentPackageCommand">
  <form:errors element="div" cssClass="error" path="*"/>

  <label for="file">Select a Content Package or Assessment Item XML file to upload:</label>
  <input name="file" type="file" />

  <input type="submit" value="Upload" />

</form:form>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>
