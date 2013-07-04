<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Initial set-up page for an LTI resource

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Assessment Launcher - Getting Started">

  <%-- No Assessment selected yet, so do cheapo "Wizard" thingy --%>
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
          <h3>Upload New Assessment</h3>
          <div>Upload a new Assessment to use for this launch.</div>
        </a>
      </div>
    </div>

    <div class="grid_6">
      <div class="box">
        <a href="${utils:escapeLink(primaryRouting['listAssessments'])}" class="boxButton assessments" title="Samples">
          <h3>Choose From Existing Assessments</h3>
          <div>
            Browse the Assessments you've uploaded into QTIWorks for
            <c:out value="${utils:formatLtiContextTitle(ltiContext)}"/>.
          </div>
        </a>
      </div>
    </div>
  </div>

</page:ltipage>

