<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for editing Delivery properties

Additional Model:

deliveryTemplate

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Edit Delivery properties">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Assessment Manager</a> &#xbb;
    </nav>
    <h2>
      <span class="assessmentLabel">Assessment&#xa0;${utils:formatAssessmentType(assessment)}</span>
      <a href="${utils:escapeLink(assessmentRouting['show'])}">${fn:escapeXml(assessmentPackage.fileName)}</a>
      &#xbb;
      <span class="deliveryLabel">Delivery</span>
      <a href="${utils:escapeLink(deliveryRouting['show'])}">${fn:escapeXml(delivery.title)}</a>
      &#xbb; Edit
    </h2>
    <div class="hints">
      <p>
        This page lets you change the key properties for this delivery.
      </p>
    </div>
  </header>

  <form:form method="post" acceptCharset="UTF-8" commandName="deliveryTemplate">

    <%-- Show any form validation errors discovered --%>
    <form:errors element="div" cssClass="formErrors" path="*"/>

    <fieldset>
      <div class="stdFormRow">
        <div class="grid_1">
          <div class="bigStatus">1<span class="required">*</span></div>
        </div>
        <div class="grid_2">
          <label for="title">Title:</label>
        </div>
        <div class="grid_9">
          <form:input path="title" size="30" type="input" cssClass="expandy" />
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>
    <fieldset>
      <div class="stdFormRow">
        <div class="grid_1">
          <div class="bigStatus">2</div>
        </div>
        <div class="grid_3">Select Delivery Settings:</div>
        <div class="grid_8">
          Select Delivery Settings:
          <ul class="dsSelector">
            <li>
              <input type="radio" id="dsdefault" name="dsid" value=""${empty deliveryTemplate.dsid ? ' checked="checked"' : ''} />
              <label for="dsdefault" class="dsTitle">
                Use QTIWorks' default Delivery Settings
              </label>
            </li>
            <c:forEach var="ds" items="${deliverySettingsList}">
              <c:set var="checked" value="${deliveryTemplate.dsid==ds.id}"/>
              <li>
                <input type="radio" id="dsid${ds.id}" name="dsid" value="${ds.id}"${checked ? ' checked="checked"' : ''} />
                <label for="dsid${ds.id}" class="dsTitle">
                  ${fn:escapeXml(ds.title)}
                </label>
                <c:if test="${ds['class'].simpleName=='ItemDeliverySettings' && !empty ds.prompt}">
                  <div class="dsPrompt">${fn:escapeXml(utils:trimSentence(ds.prompt, 200))}</div>
                </c:if>
              </li>
            </c:forEach>
          </ul>
          <p>
          <a href="${utils:escapeLink(primaryRouting['deliverySettingsManager'])}">(You can create additional settings to choose from.)</a></p>
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>
    <fieldset>
      <div class="stdFormRow">
        <div class="grid_1">
          <div class="bigStatus">3</div>
        </div>
        <div class="grid_2">
          <label for="submit">Hit "Save"</label>
        </div>
        <div class="grid_5">
          <input name="submit" type="submit" value="Save"/>
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>
    <div class="hints">
      (<span class="required">*</span> denotes a required field.)
    </div>

  </form:form>
  <p class="floatRight">
    <a href="${utils:escapeLink(deliveryRouting['show'])}">Cancel and return to Delivery</a>
  </p>

</page:page>
