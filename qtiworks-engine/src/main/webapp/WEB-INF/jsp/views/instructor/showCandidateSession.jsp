
<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Shows a single CandidateSession, with some summary data

Model:

candidateSessionSummaryReport
candidateSession
delivery
assessment
candidateSessionRouting (action -> URL)
deliveryRouting (action -> URL)
assessmentRouting (action -> URL)
primaryRouting (action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Candidate Session details">

  <nav class="breadcrumbs">
    <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Your assessments</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment '${fn:escapeXml(assessment.name)}'</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['deliveries'])}">Assessment Deliveries</a> &#xbb;
    <a href="${utils:escapeLink(deliveryRouting['show'])}">Delivery '${fn:escapeXml(delivery.title)}'</a> &#xbb;
    <a href="${utils:escapeLink(deliveryRouting['candidateSessions'])}">Candidate Reports &amp; Proctoring</a> &#xbb;
  </nav>
  <h2>Candidate Session #${candidateSession.id}</h2>

  <c:set var="candidateSessionSummaryMetadata" value="${candidateSessionSummaryReport.candidateSessionSummaryMetadata}"/>
  <c:set var="candidateSessionSummaryData" value="${candidateSessionSummaryReport.candidateSessionSummaryData}"/>
  <c:set var="assessmentResultXml" value="${candidateSessionSummaryReport.assessmentResultXml}"/>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Candidate Name</div>
      <div class="value">${fn:escapeXml(candidateSessionSummaryData.firstName)}&#xa0;${fn:escapeXml(candidateSessionSummaryData.lastName)}</div>
    </div>
  </div>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Session Created</div>
      <div class="value">${utils:formatDayDateAndTime(candidateSessionSummaryData.launchTime)}</div>
    </div>
  </div>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Session Status</div>
      <div class="value">${fn:escapeXml(candidateSessionSummaryData.sessionStatus)}</div>
    </div>
  </div>

  <div class="clear"></div>

  <h4>Outcome Variables</h4>

  <c:set var="numericOutcomeCount" value="${fn:length(candidateSessionSummaryMetadata.numericOutcomeIdentifiers)}"/>
  <c:set var="otherOutcomeCount" value="${fn:length(candidateSessionSummaryMetadata.otherOutcomeIdentifiers)}"/>
  <table class="listTable">
    <thead>
      <tr>
        <th>Outcome Identifier</th>
        <th>Value</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="index" begin="0" end="${numericOutcomeCount-1}">
        <tr>
          <td>${candidateSessionSummaryMetadata.numericOutcomeIdentifiers[index]}</td>
          <td>${candidateSessionSummaryData.numericOutcomeValues[index]}</td>
        </tr>
      </c:forEach>
      <c:forEach var="index" begin="0" end="${otherOutcomeCount-1}">
        <tr>
          <td>${candidateSessionSummaryMetadata.otherOutcomeIdentifiers[index]}</td>
          <td>${candidateSessionSummaryData.otherOutcomeValues[index]}</td>
        </tr>
      </c:forEach>
    </body>
  </table>

  <h4>Actions</h4>
  <ul>
    <li>
      <c:choose>
        <c:when test="${!candidateSessionSummaryData.sessionTerminated}">
          <a href="${utils:escapeLink(candidateSessionRouting['terminate'])}">Terminate Candidate Session</a>
        </c:when>
        <c:otherwise>
          Terminate Candidate Session (already terminated)
        </c:otherwise>
      </c:choose>
    </li>
  </ul>

  <h4>QTI assessmentResult XML</h4>

  <script src="//google-code-prettify.googlecode.com/svn/loader/run_prettify.js"></script>
  <pre class="xmlSource prettyprint">${fn:escapeXml(assessmentResultXml)}</pre>

</page:page>
