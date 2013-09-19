<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for creating a Delivery

Additional Model:

deliveryTemplate

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Edit Delivery properties">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Assessment Manager</a> &#xbb;
      <a href="${utils:escapeLink(assessmentRouting['show'])}">
        ${fn:escapeXml(assessmentPackage.fileName)}
        [${fn:escapeXml(assessmentPackage.title)}]
      </a> &#xbb;
    </nav>
    <h2>Create New Delivery</h2>
    <div class="hints">
      <p>
        This form lets you create a new Delivery of the Assessment
        ${fn:escapeXml(assessmentPackage.fileName)}
        [${fn:escapeXml(assessmentPackage.title)}].
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
        <div class="grid_3">
          <label for="title">Enter Title:</label>
        </div>
        <div class="grid_8">
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
                  ${ds.title}
                </label>
                <c:if test="${ds['class'].simpleName=='ItemDeliverySettings' && !empty ds.prompt}">
                  <div class="dsPrompt">${fn:escapeXml(utils:trimSentence(ds.prompt, 200))}</div>
                </c:if>
              </li>
            </c:forEach>
          </ul>
          <p>
            <a href="${utils:escapeLink(primaryRouting[assessment.assessmentType=='ASSESSMENT_ITEM' ? 'listItemDeliverySettings' : 'listTestDeliverySettings'])}">(You can create additional settings to choose from.)</a>
          </p>
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

</page:page>
