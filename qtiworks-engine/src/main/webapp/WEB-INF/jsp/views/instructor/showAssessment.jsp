<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Instructor upload assessment package form

Model:

assessment
assessmentPackage (most recent)
itemDeliverySettingsList (List<ItemDeliverySettings> - possibly empty)
assessmentRouting (action -> URL)
instructorAssessmentRouting (action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Assessment details">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:escapeLink(instructorAssessmentRouting['listAssessments'])}">Your assessments</a> &#xbb;
  </nav>
  <h2>Assessment ${fn:escapeXml(assessment.name)}</h2>

  <dl>
    <dt>Name:</dt>
    <dd><c:out value="${assessment.name}"/></dd>

    <dt>Title</dt>
    <dd><c:out value="${assessment.title}"/></dd>

    <dt>Package Import Version:</dt>
    <dd>${assessment.packageImportVersion}</dd>

    <dt>Type:</dt>
    <dd>${assessment.assessmentType}</dd>

    <dt>Public?</dt>
    <dd>${assessment.public}</dd>
  </dl>

  <h3>Current package content</h3>

  <dl>
    <dt>Version:</dt>
    <dd>${assessmentPackage.importVersion}</dd>

    <dt>Uploaded:</dt>
    <dd>${assessmentPackage.creationTime}</dd>

    <dt>Import type:</dt>
    <dd>${assessmentPackage.importType}</dd>

    <dt>Validated?</dt>
    <dd>${assessmentPackage.validated}</dd>

    <dt>Valid?</dt>
    <dd>${assessmentPackage.valid}</dd>
  </dl>

  <h3>Actions</h3>

  <ul>
    <li><a href="${utils:escapeLink(assessmentRouting['edit'])}">Edit Assessment properties</a></li>
    <li><a href="${utils:escapeLink(assessmentRouting['upload'])}">Replace Assessment Package Content</a></li>
    <li><a href="${utils:escapeLink(assessmentRouting['validate'])}">Show validation status</a></li>
    <li><a href="${utils:escapeLink(instructorAssessmentRouting['listAssessments'])}">Your assessments</a></li>
    <li>
      <c:choose>
        <c:when test="${assessmentPackage.valid}">
        <c:choose>
          <c:when test="${!empty itemDeliverySettingsList}">
            Try out using:
            <ul>
              <c:forEach var="itemDeliverySettings" items="${itemDeliverySettingsList}">
                <li>
                  <form action="${utils:escapeLink(assessmentRouting['try'])}/${itemDeliverySettings.id}" method="post">
                    <input type="submit" value="${fn:escapeXml(itemDeliverySettings.title)}" />
                  </form>
                </li>
              </c:forEach>
            </ul>
          </c:when>
          <c:otherwise>
            <%-- No options exist yet, so allow try out with a default set --%>
            <form action="${utils:escapeLink(assessmentRouting['try'])}" method="post">
              <input type="submit" value="Try out">
            </form>
          </c:otherwise>
        </c:choose>
        </c:when>
        <c:otherwise>
          (A button allowing you to try this assessment out will appear here once you fix its validation issues)
        </c:otherwise>
      </c:choose>
    </li>
    <li>Delete Assignment (coming soon)</li>
    <li>Create a Delivery (coming soon)</li>
    <li>Manage Deliveries of this Assignment</li>
  </ul>
</page:page>
