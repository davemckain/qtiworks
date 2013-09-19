<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Lists Assessments owned by caller

Model:

assessmentAndPackageList
assessmentListRouting (aid -> action -> URL)
primaryRouting (action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Assessment Manager">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
    </nav>
    <h2>Assessment Manager</h2>
    <div class="hints">
      <p>
        Assessment Manager are shown below. You can add new Assessments, view and try each Assessment,
        and manage the Deliveries of each Assessment to your assessment candidates.
      </p>
    </div>
  </header>

  <table class="listTable">
    <thead>
      <tr>
        <th colspan="2"></th>
        <th>Title &amp; Package File Name</th>
        <th>Assessment Type</th>
        <th>Created</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="assessmentAndPackage" items="${assessmentAndPackageList}" varStatus="loopStatus">
        <c:set var="assessment" value="${assessmentAndPackage.assessment}"/>
        <c:set var="assessmentPackage" value="${assessmentAndPackage.assessmentPackage}"/>
        <c:set var="assessmentRouting" value="${assessmentListRouting[assessment.id]}"/>
        <tr>
          <td class="bigStatus">${loopStatus.index + 1}</td>
          <td align="center" class="actions">
            <c:if test="${assessmentPackage.launchable}">
              <page:postLink path="${assessmentRouting['try']}" title="Quick&#xa0;Try"/>
            </c:if>
          </td>
          <td>
            <h4><a href="${utils:escapeLink(assessmentRouting['show'])}"><c:out value="${assessmentPackage.fileName}"/></a></h4>
            <span class="title"><c:out value="${assessmentPackage.title}"/></span>
          </td>
          <td class="center">
            <c:choose>
              <c:when test="${assessment.assessmentType=='ASSESSMENT_ITEM'}">Item</c:when>
              <c:otherwise>Test</c:otherwise>
            </c:choose>
          </td>
          <td class="center">
            <c:out value="${utils:formatDayDateAndTime(assessment.creationTime)}"/>
          </td>
        </tr>
      </c:forEach>
      <tr>
        <td class="plus"></td>
        <td colspan="2" class="actions">
          <a href="${utils:escapeLink(primaryRouting['uploadAssessment'])}">Upload a new assessment</a>
        </td>
        <td colspan="2"></td>
      </tr>
    </tbody>
  </table>
</page:page>

