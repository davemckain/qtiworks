<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for editing Assessment properties

Model:

assessment
assessmentRouting (action -> URL)
primaryRouting (action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Edit Assessment properties">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Assessment library</a> &#xbb;
      <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment ${fn:escapeXml(assessment.name)}</a>
    </nav>
    <h2>Edit Assessment Properties</h2>
    <div class="hints">
      <p>
        This page lets you change certain properties about your assessment.
      </p>
    </div>
  </header>

  <form:form method="post" acceptCharset="UTF-8" commandName="assessmentMetadataTemplate">

    <%-- Show any form validation errors discovered --%>
    <form:errors element="div" cssClass="formErrors" path="*"/>

    <fieldset>
      <div class="stdFormRow">
        <div class="grid_1">
          <div class="workflowStep">1<span class="required">*</span></div>
        </div>
        <div class="grid_2">
          <label for="title">Name:</label>
        </div>
        <div class="grid_9">
          <form:input path="name" size="30" type="input" cssClass="expandy" />
        </div>
      </div>
      <div class="clear"></div>
      <div class="stdFormRow">
        <div class="grid_1">
          <div class="workflowStep">2<span class="required">*</span></div>
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
          <div class="workflowStep">3</div>
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

</page:ltipage>
