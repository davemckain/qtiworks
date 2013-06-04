<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Lists Assessments owned by caller

Model:

assessmentAndPackageList
assessmentRouting (aid -> action -> URL)
instructorAssessmentRouting (action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Your Assessments">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
  </nav>
  <h2>Your Assessments</h2>

  <c:choose>
    <c:when test="${!empty assessmentAndPackageList}">
      <table class="assessmentList">
        <thead>
          <tr>
            <th colspan="2"></th>
            <th>Details</th>
            <th>Assessment Type</th>
            <th>Version</th>
            <th>Created</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="assessmentAndPackage" items="${assessmentAndPackageList}" varStatus="loopStatus">
            <c:set var="assessment" value="${assessmentAndPackage.assessment}"/>
            <c:set var="assessmentPackage" value="${assessmentAndPackage.assessmentPackage}"/>
            <tr>
              <td align="center">
                <div class="workflowStep">${loopStatus.index + 1}</div>
              </td>
              <td align="center" class="launch">
                <c:if test="${assessmentPackage.launchable}">
                  <form action="${assessmentRouting[assessment.id]['try']}" method="post">
                    <button type="submit" class="playButton">Quick Try</button>
                  </form>
                </c:if>
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

