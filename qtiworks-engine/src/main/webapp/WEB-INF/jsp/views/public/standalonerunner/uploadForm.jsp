<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Validator service submission form

Model attributes:

standaloneDeliveryCommand
itemDeliverySettingsList

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
        <dt><label for="deliverySettings">Select delivery settings:</label></dt>
        <dd>
          (NB: You can create your own delivery settings if you log in.)
          <ul>
            <c:forEach var="ds" items="${itemDeliverySettingsList}">
              <c:set var="checked" value="${standaloneDeliveryCommand.dsid==ds.id}"/>
              <li>
              <input type="radio" name="dsid" value="${ds.id}"${checked ? ' checked="checked"' : ''} />
                ${ds.title}
                <br />
                ${ds.prompt}
              </li>
            </c:forEach>
          </ul>
        </dd>
        <dt><label for="submit">Hit "Upload and Run!"</label></dt>
        <dd>
          <input id="submit" name="submit" type="submit" value="Upload and Run!">
        </dd>
      </dl>
    </form:form>
  </div>

</page:page>
