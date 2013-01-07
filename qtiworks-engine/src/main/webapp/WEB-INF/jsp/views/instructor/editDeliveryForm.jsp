<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for editing Delivery properties

Model:

deliveryTemplate
delivery
deliverySettingsList
assessment
assessmentRouting (action -> URL)
deliveryRouting (action -> URL)
instructorAssessmentRouting (action -> URL)
primaryRouting

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Edit Delivery properties">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:escapeLink(instructorAssessmentRouting['listAssessments'])}">Your assessments</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment '${fn:escapeXml(assessment.name)}'</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['deliveries'])}">Assessment Deliveries</a> &#xbb;
    <a href="${utils:escapeLink(deliveryRouting['show'])}">Delivery '${fn:escapeXml(delivery.title)}'</a>
  </nav>
  <h2>Edit Delivery Properties</h2>

  <div class="hints">
    <p>
      This page lets you change certain properties about this delivery.
    </p>
  </div>

  <form:form method="post" acceptCharset="UTF-8" commandName="deliveryTemplate">

    <%-- Show any form validation errors discovered --%>
    <form:errors element="div" cssClass="formErrors" path="*"/>

    <fieldset>
      <div class="stdFormRow">
        <div class="grid_1">
          <div class="workflowStep">1<span class="required">*</span></div>
        </div>
        <div class="grid_2">
          <label for="title">Title:</label>
        </div>
        <div class="grid_9">
          <form:input path="title" size="30" type="input" cssClass="expandy" />
        </div>
      </div>
      <div class="clear"></div>
      <div class="stdFormRow">
        <div class="grid_1">
          <div class="workflowStep">2<span class="required">*</span></div>
        </div>
        <div class="grid_2">
          <label for="open">Open to candidates?</label>
        </div>
        <div class="grid_5">
          <form:radiobutton path="open" value="true" /> Yes
          <form:radiobutton path="open" value="false" /> No
        </div>
      </div>
      <div class="clear"></div>
      <div class="stdFormRow">
        <div class="grid_1">
          <div class="workflowStep">3&#xa0;</div>
        </div>
        <div class="grid_2">
          <label for="ltiEnabled">LTI enabled?</label>
        </div>
        <div class="grid_5">
          <form:radiobutton path="ltiEnabled" value="true" /> Yes
          <form:radiobutton path="ltiEnabled" value="false" /> No
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>
    <fieldset>
      <div class="stdFormRow">
        <div class="grid_1">
          <div class="workflowStep">4<span class="required">*</span></div>
        </div>
        <div class="grid_11">
          Select delivery settings:
          <ul class="dsSelector">
            <c:forEach var="ds" items="${deliverySettingsList}">
              <c:set var="checked" value="${deliveryTemplate.dsid==ds.id}"/>
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
          <p>
          <a href="${utils:escapeLink(instructorAssessmentRouting['listItemDeliverySettings'])}">(You can create additional settings to choose from.)</a></p>
        </div>
      </div>
      <div class="clear"></div>
    </fieldset>
    <fieldset>
      <div class="stdFormRow">
        <div class="grid_1">
          <div class="workflowStep">5</div>
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
