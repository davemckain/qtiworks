<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Shows events within a CandidateSession

Additional model data:

candidateSession
candidateSessionSummaryReport
candidateEventSummaryDataList

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<c:set var="candidateSessionSummaryMetadata" value="${candidateSessionSummaryReport.candidateSessionSummaryMetadata}"/>
<c:set var="candidateSessionSummaryData" value="${candidateSessionSummaryReport.candidateSessionSummaryData}"/>
<c:set var="assessmentResultXml" value="${candidateSessionSummaryReport.assessmentResultXml}"/>
<page:page title="Candidate Session Activity Log">

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
      &#xbb;
      <a href="${utils:escapeLink(deliveryRouting['candidateSessions'])}">Sessions</a>
      &#xbb;
      <a href="${utils:escapeLink(candidateSessionRouting['show'])}">#${candidateSession.id}</a>
      &#xbb;
      Candidate Activity Log
    </h2>
    <div class="hints">
      <p>
        This page shows you full details of the candidate's activity within this session.
      </p>
    </div>
  </header>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Candidate Name</div>
      <div class="value">${fn:escapeXml(candidateSessionSummaryData.firstName)}&#xa0;${fn:escapeXml(candidateSessionSummaryData.lastName)}</div>
    </div>
  </div>
  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Session Launched</div>
      <div class="value">${utils:formatDayDateAndTime(candidateSessionSummaryData.launchTime)}</div>
    </div>
  </div>
  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Session Status</div>
      <div class="value">${fn:escapeXml(candidateSessionSummaryData.sessionStatusMessage)}</div>
    </div>
  </div>
  <div class="clear"></div>

  <table class="cellTable">
    <thead>
      <tr>
        <th>Event</th>
        <th>Timestamp</th>
        <th>Details</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="candidateEventSummaryData" items="${candidateEventSummaryDataList}" varStatus="status">
        <tr>
          <td class="center">#${status.count}</td>
          <td class="center">${utils:formatTimestamp(candidateEventSummaryData.timestamp)}</td>
          <td>${fn:escapeXml(candidateEventSummaryData.formattedDescription)}</td>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  <p class="floatRight">
    <a href="${utils:escapeLink(candidateSessionRouting['show'])}">Return to Candidate Session information</a>
  </p>

</page:page>
