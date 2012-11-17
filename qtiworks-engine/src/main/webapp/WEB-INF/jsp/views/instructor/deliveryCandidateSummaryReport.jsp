<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Shows summary report of candidate sessions on a delivery

Model:

delivery
assessment
assessmentRouting (action -> URL)
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
      showing you the state of the session and the final value of all outcome variables once
      the candidate has finished the assessment. You can downlaod a CSV version of this report
      using the link below.
    </p>
  </div>

  <c:choose>
    <c:when test="${!empty deliveryCandidateSummaryReport}">
      <c:set var="outcomeCount" value="${fn:length(deliveryCandidateSummaryReport.outcomeNames)}"/>
      <table class="assessmentList">
        <thead>
          <tr>
            <th colspan="3">Session</th>
            <th colspan="3">Candidate</th>
            <th colspan="${outcomeCount > 0 ? outcomeCount : 1}">Outcomes</th>
          </tr>
          <tr>
            <th>Session ID</th>
            <th>Session Launch Time</th>
            <th>Session Status</th>
            <th>Email Address</th>
            <th>First Name</th>
            <th>Last Name</th>
            <c:choose>
              <c:when test="${outcomeCount > 0}">
                <c:forEach var="outcomeName" items="${deliveryCandidateSummaryReport.outcomeNames}">
                  <th>${fn:escapeXml(outcomeName)}</th>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <th>(Will appear when first session is closed)</th>
              </c:otherwise>
            </c:choose>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="row" items="${deliveryCandidateSummaryReport.rows}">
            <tr>
              <td align="center">${row.sessionId}</td>
              <td align="center"><c:out value="${utils:formatDayDateAndTime(row.launchTime)}"/></td>
              <td align="center">${row.sessionClosed ? 'Finished' : 'In Progress'}</td>
              <td><c:out value="${row.emailAddress}"/></td>
              <td><c:out value="${row.firstName}"/></td>
              <td><c:out value="${row.lastName}"/></td>
              <c:choose>
                <c:when test="${!empty row.outcomeValues}">
                  <c:forEach var="outcomeValue" items="${row.outcomeValues}">
                    <td align="center"><c:out value="${outcomeValue}"/></td>
                  </c:forEach>
                </c:when>
                <c:otherwise>
                  <td align="center" colspan="${outcomeCount > 0 ? outcomeCount : 1}}">Available when session closes</td>
                </c:otherwise>
              </c:choose>
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
    <li><a href="${utils:escapeLink(deliveryRouting['candidateSummaryReportCsv'])}">Download candidate outcome summary (CSV)</a></li>
    <li><a href="${utils:escapeLink(deliveryRouting['candidateResultsZip'])}">Download all candiate &lt;assessmentResult&gt; XML files (ZIP)</a></li>
  </ul>

</page:page>

