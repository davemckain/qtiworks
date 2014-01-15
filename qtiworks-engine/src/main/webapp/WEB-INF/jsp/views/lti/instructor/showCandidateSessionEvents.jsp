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
<page:ltipage title="Candidate Session Activity Log">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listCandidateSessions'])}">Candidate Session Reports &amp; Proctoring</a> &#xbb;
      <a href="${utils:escapeLink(candidateSessionRouting['show'])}">Candidate Session #${candidateSession.id}</a> &#xbb;
    </nav>
    <h2>
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

  <c:if test="${!candidateSessionSummaryData.sessionTerminated}">
    <p class="floatRight">
      <a href="${utils:escapeLink(candidateSessionRouting['events'])}">Refresh this information</a>
      <br style="clear:both">
    </p>
  </c:if>

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

  <ul class="footActions">
    <c:if test="${!candidateSessionSummaryData.sessionTerminated}">
      <li><a href="${utils:escapeLink(candidateSessionRouting['events'])}">Refresh this information</a></li>
    </c:if>
    <li><a href="${utils:escapeLink(candidateSessionRouting['show'])}">Return to Candidate Session information</a></li>
  </ul>

</page:ltipage>
