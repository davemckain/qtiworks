<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Validator service submission form

Model attributes:

standaloneDeliveryCommand:

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="QTI Validator">

  <h2>Run Assessment Item</h2>

  <div class="uploadForm">
    <form:form id="uploadForm" method="post" acceptCharset="UTF-8" enctype="multipart/form-data" commandName="standaloneDeliveryCommand">

      <%-- Show any validation errors discovered --%>
      <form:errors element="div" cssClass="error" path="*"/>

      <dl>
        <dt><label for="submissionFile">Select a Content Package or Assessment Item XML file to upload and validate:</label></dt>
        <dd>
          <form:input path="file" type="file"/>
        </dd>
        <dt><label for="deliverySettings">Select delivery configuration:</label></dt>
        <dd>
          <form:input path="dsid" type="text"/>
        </dd>
        <dt><label for="submit">Hit "Upload and Validate!"</label></dt>
        <dd>
          <input id="submit" name="submit" type="submit" value="Upload and Validate!">
        </dd>
      </dl>
    </form:form>
  </div>

</page:page>
