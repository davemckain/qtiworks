<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Update assessment package">

  <h2>Update assessment package</h2>

  <form:form method="post" acceptCharset="UTF-8" enctype="multipart/form-data" commandName="uploadAssessmentPackageCommand">
    <form:errors element="div" cssClass="error" path="*"/>

    <label for="file">Select a Content Package or Assessment Item XML file to upload:</label>
    <input name="file" type="file" />

    <input type="submit" value="Upload" />

  </form:form>

</page:page>

