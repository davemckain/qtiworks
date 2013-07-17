<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Instructor upload assessment package form

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Upload New Assessment">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Assessment Library</a> &#xbb;
    </nav>
    <h2>Upload Assessment</h2>
    <div class="hints">
      <p>
        You can upload a new QTI Assessment Item or Test here.
      </p>
      <p>
        Your assessment will be validated automatically during the upload process.
        If it is not valid, or if you try it out and discover it doesn't quite work the way you want,
        then you can upload a new version of your assessment to replace it, and repeat that process
        as many times as you like.
      </p>
    </div>
  </header>

  <%@ include file="/WEB-INF/jsp/includes/instructor/uploadAssessmentForm.jspf" %>

</page:ltipage>
