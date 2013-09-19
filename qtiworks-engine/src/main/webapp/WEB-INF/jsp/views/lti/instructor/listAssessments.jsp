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

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
    </nav>
    <h2>Assessment library</h2>
    <div class="hints">
      <p>
        The assessment library contains all of the the Assessments you have
        uploaded into <c:out value="${utils:formatLtiContextTitle(ltiContext)}"/>.
        You can view, edit and upload new Assessments here. You can also choose
        which Assessment should be run for this launch.
    </div>
  </header>

  <table class="listTable">
    <thead>
      <tr>
        <th></th>
        <th colspan="2">Actions</th>
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
        <c:set var="isSelectedAssessment" value="${!empty thisAssessment && thisAssessment.id==assessment.id}"/>
        <tr class="${isSelectedAssessment ? 'selected' : ''}">
          <td class="bigStatus">${loopStatus.index + 1}</td>
          <td align="center" class="actions">
            <c:if test="${assessmentPackage.launchable}">
              <page:postLink path="${assessmentRouting['try']}" title="Quick&#xa0;Try"/>
            </c:if>
          </td>
          <td align="center" class="actions">
            <c:choose>
              <c:when test="${!isSelectedAssessment}">
                <page:postLink path="${assessmentRouting['select']}" title="Select for this launch"
                  confirmCondition="${thisDeliveryStatusReport.sessionCount>0}"
                  confirm="Are you sure? Selecting a different assessment would terminate ${thisDeliveryStatusReport.nonTerminatedSessionCount} candidate session(s) currently running on this launch, and delete the gathered data for all ${thisDeliveryStatusReport.sessionCount} session(s) launched so far"
                />
              </c:when>
              <c:otherwise>
                Selected for this launch
              </c:otherwise>
            </c:choose>
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
        <td colspan="3"></td>
      </tr>
    </tbody>
  </table>

</page:ltipage>
