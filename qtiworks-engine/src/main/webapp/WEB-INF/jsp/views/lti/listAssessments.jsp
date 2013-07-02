<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Lists Assessments in current LTI context

Additional Model attrs:

assessmentAndPackageList
assessmentListRouting (aid -> action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Assessment library">

  <nav class="breadcrumbs">
    <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">This Assessment launch</a></li> &#xbb;
  </nav>
  <h2>Assessment library</h2>

  <p class="hints">
    The assessment library lets you browse and manage the Assessments you have
    uploaded into <c:out value="${utils:formatLtiContextTitle(ltiContext)}"/>
  </p>

  <ul class="menu">
    <li><a href="${utils:escapeLink(primaryRouting['uploadAssessment'])}">Upload a new assessment</a></li>
  </ul>

  <c:choose>
    <c:when test="${!empty assessmentAndPackageList}">
      <table class="assessmentList">
        <thead>
          <tr>
            <th colspan="3"></th>
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
            <c:set var="assessmentRouting" value="${assessmentListRouting[assessment.id]}"/>
            <tr>
              <td align="center">
                <div class="workflowStep">${loopStatus.index + 1}</div>
              </td>
              <td align="center" class="launch">
                <c:if test="${assessmentPackage.launchable}">
                  <form action="${assessmentRouting['try']}" method="post">
                    <button type="submit" class="playButton">Quick&#xa0;Try</button>
                  </form>
                </c:if>
              </td>
              <td align="center" class="launch">
                <form action="${assessmentRouting['select']}" method="post">
                  <button type="submit" class="playButton">Use this Assessment</button>
                </form>
              </td>
              <td>
                <h4><a href="${utils:escapeLink(assessmentRouting['show'])}"><c:out value="${assessment.name}"/></a></h4>
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
</page:ltipage>
