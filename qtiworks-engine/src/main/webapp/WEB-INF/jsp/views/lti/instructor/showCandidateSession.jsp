<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Shows a single CandidateSession, with some summary data

Additional model data:

candidateSession
candidateSessionSummaryReport

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<c:set var="candidateSessionSummaryMetadata" value="${candidateSessionSummaryReport.candidateSessionSummaryMetadata}"/>
<c:set var="candidateSessionSummaryData" value="${candidateSessionSummaryReport.candidateSessionSummaryData}"/>
<c:set var="assessmentResultXml" value="${candidateSessionSummaryReport.assessmentResultXml}"/>
<page:ltipage title="Candidate Session details">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listCandidateSessions'])}">Candidate Session Reports &amp; Proctoring</a> &#xbb;
    </nav>
    <h2>Candidate Session #${candidateSession.id}</h2>
  </header>

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

  <h4>Actions</h4>
  <ul class="menu">
    <li>
      <c:choose>
        <c:when test="${!candidateSessionSummaryData.sessionTerminated}">
          <page:postLink path="${utils:escapeLink(candidateSessionRouting['terminate'])}" title="Terminate this Candidate Session"/>
        </c:when>
        <c:otherwise>
          Terminate Candidate Session (already terminated)
        </c:otherwise>
      </c:choose>
    </li>
  </ul>

  <h3>Outcome Variables</h3>

  <c:set var="numericOutcomeCount" value="${fn:length(candidateSessionSummaryMetadata.numericOutcomeIdentifiers)}"/>
  <c:set var="otherOutcomeCount" value="${fn:length(candidateSessionSummaryMetadata.otherOutcomeIdentifiers)}"/>
  <table class="cellTable">
    <thead>
      <tr>
        <th>Outcome Identifier</th>
        <th>Value</th>
      </tr>
    </thead>
    <tbody>
      <c:if test="${numericOutcomeCount > 0}">
        <c:forEach var="index" begin="0" end="${numericOutcomeCount-1}">
          <tr>
            <td>${candidateSessionSummaryMetadata.numericOutcomeIdentifiers[index]}</td>
            <td>${candidateSessionSummaryData.numericOutcomeValues[index]}</td>
          </tr>
        </c:forEach>
      </c:if>
      <c:if test="${otherOutcomeCount > 0}">
        <c:forEach var="index" begin="0" end="${otherOutcomeCount-1}">
          <tr>
            <td>${candidateSessionSummaryMetadata.otherOutcomeIdentifiers[index]}</td>
            <td>${candidateSessionSummaryData.otherOutcomeValues[index]}</td>
          </tr>
        </c:forEach>
      </c:if>
      <c:if test="${numericOutcomeCount==0 && otherOutcomeCount==0}">
        <tr>
          <td align="center" colspan="2">(No outcome values currently recorded)</td>
        </tr>
      </c:if>
    </body>
  </table>

  <c:if test="${!empty assessmentResultXml}">
    <h3>QTI assessmentResult XML</h3>

    <script src="//google-code-prettify.googlecode.com/svn/loader/run_prettify.js"></script>
    <pre class="xmlSource prettyprint">${fn:escapeXml(assessmentResultXml)}</pre>
  </c:if>

</page:ltipage>
