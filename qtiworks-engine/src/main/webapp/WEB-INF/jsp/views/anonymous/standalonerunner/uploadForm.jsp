<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Validator service submission form

Model attributes:

standaloneRunCommand
itemDeliverySettingsList

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="QTI Validator">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/public/')}">Demos</a> &#xbb;
  </nav>
  <h2>QTI Quick Run</h2>

  <div class="hints">
    <p>
      This lets you quickly upload, validate then try out a QTI Assessment
      Item, using some pre-defined "delivery settings". (Tests will come
      later!)
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
      <div class="grid_5">
        Select delivery settings:
        <ul class="dsSelector">
          <c:forEach var="ds" items="${itemDeliverySettingsList}">
            <c:set var="checked" value="${standaloneRunCommand.dsid==ds.id}"/>
            <li>
              <input type="radio" id="dsid${ds.id}" name="dsid" value="${ds.id}"${checked ? ' checked="checked"' : ''} />
              <label for="dsid${ds.id}" class="dsTitle">
                ${ds.title}
              </label>
              <div class="dsPrompt">
                ${ds.prompt}
              </div>
            </li>
          </c:forEach>
        </ul>
      </div>
      <div class="grid_6">
        <aside>
          <p>
            These "delivery settings" control how your assessment should be delivered.
            We have picked a few basic examples for you to choose from. You can create
            and manage your own delivery settings if you log in.
          </p>
          <p id="deliverySettingsHoverInfo"></p>
          <script type="text/javascript">
            $(document).ready(function() {
                $('.dsTitle').hover(function() {
                  var text = $(this).next().text();
                  $('#deliverySettingsHoverInfo').text(text);
                });
                $('.dsSelector').hover(function() {}, function() {
                  $('#deliverySettingsHoverInfo').text('');
                });
            });
          </script>
        </aside>
      </div>
    </fieldset>

    <div class="clear"></div>
    <fieldset>
      <div class="grid_1">
        <div class="workflowStep">3</div>
      </div>
      <div class="grid_11">
        <label for="submit">Hit "Upload and Run"</label>
        <br/>
        <input id="submit" name="submit" type="submit" value="Upload and Run"/>
      </div>
    </fieldset>
  </form:form>

</page:page>
