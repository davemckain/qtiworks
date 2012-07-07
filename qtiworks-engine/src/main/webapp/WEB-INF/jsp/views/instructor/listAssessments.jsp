<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Lists Assessments owned by caller

Model:

assessmentList
assessmentRouting: aid -> action -> URL
instructorAssessmentRouting

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Your Assessments">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
  </nav>
  <h2>Your Assessments</h2>

  <c:choose>
    <c:when test="${!empty assessmentList}">
      <table class="assessmentList">
        <thead>
          <th></th>
          <th>Details</th>
          <th>Assessment Type</th>
          <th>Version</th>
          <th>Created</th>
        </thead>
        <tbody>
          <c:forEach var="assessment" items="${assessmentList}" varStatus="loopStatus">
            <tr>
              <td align="center">
                <div class="workflowStep">${loopStatus.index + 1}</div>
              </td>
              <td>
                <h4><a href="${utils:escapeLink(assessmentRouting[assessment.id]['show'])}"><c:out value="${assessment.name}"/></a></h4>
                <span class="title"><c:out value="${assessment.title}"/></span>
              </td>
              <td class="center">
                <c:choose>
                  <c:when test="${assessment.assessmentType=='ASSESSMENT_ITEM'}">Item</c:when>
                  <c:otherwise>Test</c:otherwise>
                </c:choose>
              </td>
              <td class="center">
                <c:out value="${assessment.packageImportVersion}"/>
              </td>
              <td class="center">
                <c:out value="${utils:formatDayDateAndTime(assessment.creationTime)}"/>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>You have not uploaded any assessments yet.</p>
    </c:otherwise>
  </c:choose>

  <h4>Actions</h4>
  <ul class="menu">
    <li><a href="${utils:escapeLink(instructorAssessmentRouting['uploadAssessment'])}">Upload a new assessment</a></li>
  </ul>

</page:page>

