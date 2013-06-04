<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Validator service submission form

Model attributes:

standaloneRunCommand
itemDeliverySettingsList

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="QTI Quick Run">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/public/')}">Demos</a> &#xbb;
  </nav>
  <h2>QTI Quick Run</h2>

  <div class="hints">
    <p>
      This lets you quickly upload, validate then try out a QTI Assessment
      Item or Test, using some pre-defined "delivery settings".
      If you log in, you'll be able to do much more.
    </p>
  </div>

  <form:form id="uploadForm" method="post" acceptCharset="UTF-8" enctype="multipart/form-data" commandName="standaloneRunCommand">

    <%-- Show any validation errors discovered --%>
    <form:errors element="div" cssClass="formErrors" path="*"/>

    <fieldset>
      <div class="grid_1">
        <div class="workflowStep">1</div>
      </div>
      <div class="grid_5">
        <label for="file">Select a Content Package ZIP file or Assessment Item XML file to upload and validate:</label>
        <br/>
        <form:input path="file" type="file"/>
      </div>
      <div class="grid_6">
        <aside>
          <p>
            You may upload any of the following to the validator:
          </p>
          <ul>
            <li>An IMS Content Package containing a QTI 2.1 (or 2.0) Assessment Item, plus any related resources, such as images, response processing templates...</li>
            <li>An IMS Content Package containing a QTI 2.1 Assessment Test, its Assessment Items, plus any related resources.</li>
            <li>A self-contained QTI 2.1 (or 2.0) Assessment Item XML file.</li>
          </ul>
        </aside>
      </div>
    </fieldset>
    <div class="clear"></div>
    <fieldset>
      <div class="grid_1">
        <div class="workflowStep">2</div>
      </div>
      <div class="grid_11">
        <label for="submit">Hit "Upload and Run"</label>
        <br/>
        <input id="submit" name="submit" type="submit" value="Upload and Run"/>
      </div>
    </fieldset>
  </form:form>

</page:page>
