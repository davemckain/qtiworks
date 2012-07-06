<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Instructor upload assessment package form

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Upload assessment">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/public/')}">Demos</a> &#xbb;
  </nav>
  <h2>Upload assessment</h2>

  <p class="hints">
    You can upload a new Assessment into the system here. The Assessment will be validated
    after it is uploaded. You can upload new versions of your Assessment as you see fit, and
    you will be allowed to try out and deliver your assessments to candidate (if valid), then
    later access the results.
  </p>

  <form:form method="post" acceptCharset="UTF-8" enctype="multipart/form-data" commandName="uploadAssessmentPackageCommand">

    <%-- Show any form validation errors discovered --%>
    <form:errors element="div" cssClass="formError" path="*"/>

    <fieldset>
      <div class="grid_1">
        <div class="workflowStep">1</div>
      </div>
      <div class="grid_5">
        <label for="file">Select a Content Package ZIP file or Assessment Item XML file to upload and store:</label>
        <br/>
        <form:input path="file" type="file"/>
      </div>
      <div class="grid_6">
        <aside>
          <p>
            You may upload any of the following:
          </p>
          <ul>
            <li>An IMS Content Package containing a QTI 2.1 Assessment Item plus any related resources, such as images, response processing templates...</li>
            <li>An IMS Content Package containing a QTI 2.1 Assessment Test, its Assessment Items, plus any related resources.</li>
            <li>A self-contained QTI 2.1 Assessment Item XML file.</li>
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
        <label for="submit">Hit "Upload New Assessment"</label>
        <br/>
        <input id="submit" name="submit" type="submit" value="Upload New Assessment"/>
      </div>
    </fieldset>

  </form:form>

</page:page>
