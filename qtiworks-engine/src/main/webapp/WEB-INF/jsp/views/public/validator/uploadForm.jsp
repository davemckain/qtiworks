<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Validator service submission form

Model attributes:

uploadAssessmentPackageCommand:

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="QTI Validator">

  <div class="container_12">

    <h2>QTI validator</h2>

    <div class="hints">
      <p>
        Our QTI validator checks your QTI Assessment Tests and Assessment Items
        to determine whether they adhere to the QTI 2.1 specification, giving you
        (hopefully) helpful information to help you fix any issues that are
        discovered.
      </p>
    </div>

    <form:form id="uploadForm" method="post" acceptCharset="UTF-8" enctype="multipart/form-data" commandName="uploadAssessmentPackageCommand">

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
          <label for="submit">Hit "Upload and Validate!"</label>
          <br/>
          <input id="submit" name="submit" type="submit" value="Upload and Validate!"/>
        </div>
      </fieldset>
    </form:form>

    <div class="clear"></div>

    <h3 style="padding-top:10em">Current limitations</h3>
    <ul>
      <li>
        This currently does <strong>not</strong> validate the structure and manifest of IMS content packages themselves.
        I'm not sure I'll ever add support for that, as that is going off on a bit of a tangent...
      </li>
      <li>
        I haven't added support for QTI 2.0 yet. I will do if there's demand.
      </li>
    </ul>

  </div>

</page:page>
