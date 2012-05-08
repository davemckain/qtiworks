<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Instructor upload assessment package form

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Upload assessment">

  <h2>Upload assessment</h2>

  <form:form method="post" acceptCharset="UTF-8" enctype="multipart/form-data" commandName="uploadAssessmentPackageCommand">
    <form:errors element="div" cssClass="error" path="*"/>

    <label for="file">Select a Content Package or Assessment Item XML file to upload:</label>
    <input name="file" type="file" />

    <input type="submit" value="Upload" />

  </form:form>

</page:page>
