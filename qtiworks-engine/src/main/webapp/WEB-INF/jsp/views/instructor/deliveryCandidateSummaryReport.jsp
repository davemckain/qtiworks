<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Shows summary report of candidate sessions on a delivery

Model:

delivery
assessment
assessmentRouting (action -> URL)
instructorAssessmentRouting (action -> URL)
deliveryRouting (action -> URL)
deliveryCandidateSummaryReport

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Candidate Session Report">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:escapeLink(instructorAssessmentRouting['listAssessments'])}">Your assessments</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment '${fn:escapeXml(assessment.name)}'</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['deliveries'])}">Assessment Deliveries</a> &#xbb;
    <a href="${utils:escapeLink(deliveryRouting['show'])}">Delivery '${fn:escapeXml(delivery.title)}'</a>
  </nav>
  <h2>Candidate Session Report</h2>

  <div class="hints">
    <p>
      This shows you a summary report of all candidate attempts made on this delivery,
      showing you the state of the session and the final value of all (numeric) outcome variables once
      the candidate has finished the assessment. You can download an expanded CSV version of this report
      containing ALL outcomes variables using the link below. You can also download result XML and perform
      basic proctoring of candidate sessions.
    </p>
  </div>

  <c:choose>
    <c:when test="${!empty deliveryCandidateSummaryReport}">
      <c:set var="numericOutcomeCount" value="${fn:length(deliveryCandidateSummaryReport.numericOutcomeNames)}"/>
      <table class="assessmentList">
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
            <th>Email Address</th>
            <th>First Name</th>
            <th>Last Name</th>
            <c:choose>
              <c:when test="${numericOutcomeCount > 0}">
                <c:forEach var="outcomeName" items="${deliveryCandidateSummaryReport.numericOutcomeNames}">
                  <th>${fn:escapeXml(outcomeName)}</th>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <th>(Will appear when first candidate session is started)</th>
              </c:otherwise>
            </c:choose>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="row" items="${deliveryCandidateSummaryReport.rows}">
            <tr>
              <td align="center">${row.sessionId}</td>
              <td align="center"><c:out value="${utils:formatDayDateAndTime(row.launchTime)}"/></td>
              <td align="center">${row.sessionStatus}</td>
              <td><c:out value="${row.emailAddress}"/></td>
              <td><c:out value="${row.firstName}"/></td>
              <td><c:out value="${row.lastName}"/></td>
              <c:forEach var="outcomeValue" items="${row.numericOutcomeValues}">
                <td align="center"><c:out value="${outcomeValue}"/></td>
              </c:forEach>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>No candidate sessions have been launched on this delivery yet.</p>
    </c:otherwise>
  </c:choose>

  <h4>Actions</h4>
  <ul class="menu">
    <li><a href="${utils:escapeLink(deliveryRouting['candidateSummaryReportCsv'])}">Download full candidate outcome summary (CSV)</a></li>
    <li><a href="${utils:escapeLink(deliveryRouting['candidateResultsZip'])}">Download all candiate &lt;assessmentResult&gt; XML files (ZIP)</a></li>
    <li><page:postLink path="${deliveryRouting['terminateAllSessions']}" confirm="Are you sure?" title="Terminate all remaining candidate sessions on this delivery"/></li>
  </ul>

</page:page>

