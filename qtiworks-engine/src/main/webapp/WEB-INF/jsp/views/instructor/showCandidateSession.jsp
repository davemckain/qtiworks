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
<page:page title="Candidate Session Management">

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
      &#xbb; #${candidateSession.id}
    </h2>
    <div class="hints">
      <p>
        This page shows you the state of this candidate session, and allows you to perform some basic proctoring
        actions on it.
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
  <c:if test="${!empty candidateSessionSummaryMetadata.lisResultOutcomeIdentifier}">
    <div class="grid_4">
      <div class="infoBox">
        <div class="cat">LTI Result Outcome Variable (${candidateSessionSummaryMetadata.lisResultOutcomeIdentifier})</div>
        <div class="value">${candidateSessionSummaryData.lisResultOutcomeValue}</div>
      </div>
    </div>
    <div class="grid_4">
      <div class="infoBox">
        <div class="cat">Normalized LTI Score</div>
        <div class="value">
          <c:out value="${candidateSessionSummaryData.lisScore}" default="(Not Available)"/>
        </div>
      </div>
    </div>
    <div class="grid_4">
      <div class="infoBox">
        <div class="cat">LTI Result Return Status</div>
        <div class="value">
          <c:out value="${candidateSessionSummaryData.lisReportingStatusMessage}" default="(Not Available)"/>
        </div>
      </div>
    </div>
    <div class="clear"></div>
  </c:if>

  <h3>Actions</h3>
  <ul class="menu">
    <li>
      <c:choose>
        <c:when test="${!candidateSessionSummaryData.sessionTerminated}">
          <page:postLink path="${utils:escapeLink(candidateSessionRouting['terminate'])}" title="Terminate this Candidate Session"/>
        </c:when>
        <c:otherwise>
          Terminate Candidate Session [already terminated]
        </c:otherwise>
      </c:choose>
    </li>
    <li>
      <form action="${utils:escapeLink(candidateSessionRouting['result'])}" method="get" class="postLink showXmlInDialog" title="assessmentResult XML">
        <input type="submit" value="View assessmentResult XML"/>
      </form>
    </li>
    <li>
      <a href="${utils:escapeLink(candidateSessionRouting['result'])}">Download assessmentResult XML</a>
    </li>
  </ul>

  <h3>All Outcome Variables</h3>
  <div class="hints">
    <p>The current values of all outcome variables are shown below. Numerical variables are shown first, then other variables.</p>
  </div>
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
  <p class="floatRight">
    <a href="${utils:escapeLink(deliveryRouting['candidateSessions'])}">Return to Candidate Sessions list</a>
  </p>

</page:page>
