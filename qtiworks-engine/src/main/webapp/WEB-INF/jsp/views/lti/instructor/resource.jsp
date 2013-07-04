<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Top page for managing LTI resources (after domain-level launch)

Model:

ltiUser
primaryRouting (action -> URL)
assessmentRouting (aid -> action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Assessment Launcher - Getting Started">

  <header class="grid_12 actionHeader">
    <h2>This Assessment Launch: Getting started</h2>
    <p class="hints">
      You need to choose which assessment should be delivered when candidates launch this resource.
      Either upload a new assessment, or choose one you've
      already uploaded here for <c:out value="${utils:formatLtiContextTitle(ltiContext)}"/>.
    </p>
  </header>

  <div class="boxes">
    <div class="grid_6">
      <div class="box">
        <a href="${utils:escapeLink(primaryRouting['uploadAndUseAssessment'])}" class="boxButton assessments" title="Run">
          <h3>Upload an Assessment</h3>
          <div>Upload a new Assessment to use for this launch.</div>
        </a>
      </div>
    </div>

    <div class="grid_6">
      <div class="box">
        <a href="${utils:escapeLink(primaryRouting['listAssessments'])}" class="boxButton assessments" title="Samples">
          <h3>Browse Assessment Library</h3>
          <div>
            Browse the Assessments you've uploaded into QTIWorks for this course
            already uploaded here for <c:out value="${utils:formatLtiContextTitle(ltiContext)}"/>.
          </div>
        </a>
      </div>
    </div>
  </div>

  <ul style="display:none">
    <li><a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Assessment library</a></li>
    <li><a href="${utils:escapeLink(primaryRouting['listDeliverySettings'])}">Delivery Settings manager</a></li>
    <li><a href="${utils:escapeLink(primaryRouting['debug'])}">Diagnostics</a></li>
  </ul>

</page:ltipage>

