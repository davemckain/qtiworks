<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Model:

assessment
assessmentRouting (action -> URL)
instructorAssessmentRouting (action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Replace Assessment Package Content">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:escapeLink(instructorAssessmentRouting['listAssessments'])}">Your assessments</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment '${fn:escapeXml(assessment.name)}'</a>
  </nav>
  <h2>Replace Assessment Package Content</h2>

  <div class="hints">
    <p>
      This lets you upload new QTI to replace what we have already stored in the system. You'll probably
      want to do this when trying out and/or debugging your own assessments that you are writing or generating
      in another system.
    </p>
    <p>
      All of the existing assessment metadata (e.g. name and title) will be kept around.
    </p>
  </div>

  <form:form method="post" acceptCharset="UTF-8" enctype="multipart/form-data" commandName="uploadAssessmentPackageCommand">

    <%-- Show any form validation errors discovered --%>
    <form:errors element="div" cssClass="formErrors" path="*"/>

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
            As before, you may upload any of the following:
          </p>
          <ul>
            <li>An IMS Content Package containing a QTI 2.1 Assessment Item plus any related resources, such as images, response processing templates...</li>
            <li>An IMS Content Package containing a QTI 2.1 Assessment Test, its Assessment Items, plus any related resources.</li>
            <li>A self-contained QTI 2.1 Assessment Item XML file.</li>
          </ul>
          <p>
            Please additiionally note that you MUST upload the same 'type' of
            assessment. I.e. you must replace an item with an item or a test with
            a test.
          <p>
        </aside>
      </div>
    </fieldset>
    <div class="clear"></div>
    <fieldset>
      <div class="grid_1">
        <div class="workflowStep">2</div>
      </div>
      <div class="grid_11">
        <label for="submit">Hit "Replace Assessment Package Content"</label>
        <br/>
        <input id="submit" name="submit" type="submit" value="Replacement Assessment Package Content"/>
      </div>
    </fieldset>

  </form:form>

</page:page>

