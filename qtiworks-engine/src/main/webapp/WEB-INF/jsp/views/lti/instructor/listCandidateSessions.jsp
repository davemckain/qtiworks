<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Shows summary report of candidate sessions on this LTI resource

Additional Model attributes:

deliveryCandidateSummaryReport
candidateSessionListRouting (xid -> action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Candidate Session Management">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
    </nav>
    <h2>Candidate Session Reports &amp; Proctoring</h2>
    <div class="hints">
      <p>
        This shows you a summary report of all candidate attempts made on this assessment,
        showing you the state of the session and the value of the LTI result outcome variable (if set up).
      </p>
      <p>
        You may download an expanded CSV version of this report containing all
        outcomes variables using the link below. You can also download the QTI <code>assessmentResult</code>
        reports (in XML format) and perform basic proctoring of candidate sessions.
      </p>
    </div>
  </header>

  <c:choose>
    <c:when test="${!empty deliveryCandidateSummaryReport}">
      <c:set var="candidateSessionSummaryMetadata" value="${deliveryCandidateSummaryReport.candidateSessionSummaryMetadata}"/>
      <c:set var="numericOutcomeCount" value="${fn:length(candidateSessionSummaryMetadata.numericOutcomeIdentifiers)}"/>
      <c:set var="rowCount" value="${fn:length(deliveryCandidateSummaryReport.rows)}"/>
      <table class="cellTable">
        <thead>
          <tr>
            <th colspan="3">Session</th>
            <th colspan="3">Candidate</th>
            <c:if test="${!empty candidateSessionSummaryMetadata.lisResultOutcomeIdentifier}">
              <th colspan="3">LTI Results</th>
            </c:if>
          </tr>
          <tr>
            <th>Session ID</th>
            <th>Session Launch Time</th>
            <th>Session Status</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email Address</th>
            <c:if test="${!empty candidateSessionSummaryMetadata.lisResultOutcomeIdentifier}">
              <c:choose>
                <c:when test="${rowCount > 0}">
                  <th>${fn:escapeXml(candidateSessionSummaryMetadata.lisResultOutcomeIdentifier)} Value</th>
                  <th>Normalized Score</th>
                  <th>Reporting Status</th>
                </c:when>
                <c:otherwise>
                  <th>(Will appear when first candidate session is started)</th>
                </c:otherwise>
              </c:choose>
            </c:if>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${fn:length(deliveryCandidateSummaryReport.rows) > 0}">
              <c:forEach var="row" items="${deliveryCandidateSummaryReport.rows}">
                <tr>
                  <td align="center">
                    <a href="${utils:escapeLink(candidateSessionListRouting[row.sessionId]['show'])}">${row.sessionId}</a>
                  </td>
                  <td align="center"><c:out value="${utils:formatDateAndTime(row.launchTime)}"/></td>
                  <td align="center"><c:out value="${row.sessionStatusMessage}"/></td>
                  <td><c:out value="${row.firstName}"/></td>
                  <td><c:out value="${row.lastName}"/></td>
                  <td><c:out value="${row.emailAddress}"/></td>
                  <c:if test="${!empty candidateSessionSummaryMetadata.lisResultOutcomeIdentifier}">
                    <c:choose>
                      <c:when test="${!empty row.lisResultOutcomeValue}">
                        <td align="center"><c:out value="${row.lisResultOutcomeValue}"/></td>
                        <td align="center"><c:out value="${row.lisScore}"/></td>
                        <td align="center"><c:out value="${row.lisReportingStatusMessage}"/></td>
                      </c:when>
                      <c:otherwise>
                        <td align="center">(Not Available)</td>
                        <td colspan="2" align="center">(Not Applicable)</td>
                      </c:otherwise>
                    </c:choose>
                  </c:if>
                </tr>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <tr>
                <td align="center" colspan="${6 + (!empty candidateSessionSummaryMetadata.lisResultOutcomeIdentifier ? 3 : 1)}">No Candidate Sessions have been started yet</td>
              </tr>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>No candidate sessions have been launched yet.</p>
    </c:otherwise>
  </c:choose>

  <h3>Analysis</h3>
  <ul class="menu">
    <li><a href="${utils:escapeLink(primaryRouting['candidateSummaryReportCsv'])}">Download full candidate outcome summary (CSV)</a></li>
    <li><a href="${utils:escapeLink(primaryRouting['candidateResultsZip'])}">Download all candiate &lt;assessmentResult&gt; XML files (ZIP)</a></li>
    <li><a href="${utils:escapeLink(primaryRouting['listCandidateSessions'])}">Refresh this information</a></li>
  </ul>

  <h3>Proctoring</h3>
  <ul class="menu">
    <li><page:postLink path="${primaryRouting['terminateAllSessions']}" confirm="Are you sure?" title="Terminate all running Candidate Sessions"/></li>
    <li><page:postLink path="${primaryRouting['deleteAllSessions']}" confirm="Are you sure? All candidate data gathered here will be permanently deleted!" title="Delete all Candidate Session data"/></li>
  </ul>

  <ul class="footActions">
    <li><a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Return to Dashboard</a></li>
  </ul>

</page:ltipage>
