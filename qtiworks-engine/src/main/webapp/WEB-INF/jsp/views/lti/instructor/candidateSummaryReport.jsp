<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Shows summary report of candidate sessions on this LTI resource

Additional Model attributes:

deliveryCandidateSummaryReport
candidateSessionListRouting (xid -> action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Candidate Session Reports &amp; Proctoring">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
    </nav>
    <h2>Candidate Session Reports &amp; Proctoring</h2>
    <div class="hints">
      <p>
        This shows you a summary report of all candidate attempts made on this delivery,
        showing you the state of the session and the final value of all (numeric) outcome variables once
        the candidate has finished the assessment. You can download an expanded CSV version of this report
        containing ALL outcomes variables using the link below. You can also download result XML and perform
        basic proctoring of candidate sessions.
      </p>
    </div>
  </header>

  <c:choose>
    <c:when test="${!empty deliveryCandidateSummaryReport}">
      <c:set var="candidateSessionSummaryMetadata" value="${deliveryCandidateSummaryReport.candidateSessionSummaryMetadata}"/>
      <c:set var="numericOutcomeCount" value="${fn:length(candidateSessionSummaryMetadata.numericOutcomeIdentifiers)}"/>
      <c:set var="rowCount" value="${fn:length(deliveryCandidateSummaryReport.rows)}"/>
      <table class="listTable">
        <thead>
          <tr>
            <th colspan="3">Session</th>
            <th colspan="3">Candidate</th>
            <th colspan="${numericOutcomeCount > 0 ? numericOutcomeCount : 1}">Numeric Outcomes</th>
          </tr>
          <tr>
            <th>Session ID</th>
            <th>Session Launch Time</th>
            <th>Session Status</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email Address</th>
            <c:choose>
              <c:when test="${rowCount > 0}">
                <c:choose>
                  <c:when test="${numericOutcomeCount > 0}">
                    <c:forEach var="outcomeIdentifier" items="${candidateSessionSummaryMetadata.numericOutcomeIdentifiers}">
                      <th>${fn:escapeXml(outcomeIdentifier)}</th>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <th></th>
                  </c:otherwise>
                </c:choose>
              </c:when>
              <c:otherwise>
                <th>(Will appear when first candidate session is started)</th>
              </c:otherwise>
            </c:choose>
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
                  <td align="center">${row.sessionStatus}</td>
                  <td><c:out value="${row.firstName}"/></td>
                  <td><c:out value="${row.lastName}"/></td>
                  <td><c:out value="${row.emailAddress}"/></td>
                  <c:choose>
                    <c:when test="${numericOutcomeCount>0}">
                      <c:forEach var="outcomeValue" items="${row.numericOutcomeValues}">
                        <td align="center"><c:out value="${outcomeValue}"/></td>
                      </c:forEach>
                    </c:when>
                    <c:otherwise>
                      <td align="center">(Not Available)</td>
                    </c:otherwise>
                  </c:choose>
                </tr>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <tr>
                <td align="center" colspan="${6 + (numericOutcomeCount > 0 ? numericOutcomeCount : 1)}">No Candidate Sessions have been started yet</td>
              </tr>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>No candidate sessions have been launched on this delivery yet.</p>
    </c:otherwise>
  </c:choose>

  <h4>Actions</h4>
  <ul class="menu">
    <li><a href="${utils:escapeLink(primaryRouting['candidateSummaryReportCsv'])}">Download full candidate outcome summary (CSV)</a></li>
    <li><a href="${utils:escapeLink(primaryRouting['candidateResultsZip'])}">Download all candiate &lt;assessmentResult&gt; XML files (ZIP)</a></li>
    <li><page:postLink path="${primaryRouting['terminateAllSessions']}" confirm="Are you sure?" title="Terminate all remaining candidate sessions on this delivery"/></li>
  </ul>

</page:ltipage>
