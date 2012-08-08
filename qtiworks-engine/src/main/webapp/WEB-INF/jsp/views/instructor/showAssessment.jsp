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
  <h2>Assessment '${fn:escapeXml(assessment.name)}'</h2>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Title</div>
      <div class="value">${fn:escapeXml(assessment.title)}</div>
    </div>
  </div>

  <div class="grid_2">
    <div class="infoBox">
      <div class="cat">Assessment Type</div>
      <div class="value">
        <c:choose>
          <c:when test="${assessment.assessmentType=='ASSESSMENT_ITEM'}">Item</c:when>
          <c:otherwise>Test</c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>

  <div class="grid_1">
    <div class="infoBox">
      <div class="cat">Shared?</div>
      <div class="value">${assessment.public ? 'Yes' : 'No'}</div>
    </div>
  </div>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Created</div>
      <div class="value">${utils:formatDayDateAndTime(assessment.creationTime)}</div>
    </div>
  </div>

  <div class="clear"></div>

  <div class="grid_2">
    <div class="infoBox">
      <div class="cat">Upload Version</div>
      <div class="value">${assessment.packageImportVersion}</div>
    </div>
  </div>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Uploaded From</div>
      <div class="value">
        <c:choose>
          <c:when test="${assessmentPackage.importType=='CONTENT_PACKAGE'}">IMS Content Package</c:when>
          <c:when test="${assessmentPackage.importType=='STANDALONE_ITEM_XML'}">Standalone Item XML</c:when>
          <c:otherwise>(System sample)</c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>

  <div class="grid_1">
    <div class="infoBox">
      <div class="cat">Valid?</div>
      <div class="value">
        <a href="${utils:escapeLink(assessmentRouting['validate'])}">${assessmentPackage.valid ? 'Yes' : 'No'}</a>
      </div>
    </div>
  </div>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Last QTI Upload</div>
      <div class="value">${utils:formatDayDateAndTime(assessmentPackage.creationTime)}</div>
    </div>
  </div>

  <div class="clear"></div>

  <h4>Actions</h4>

  <ul>
    <li><a href="${utils:escapeLink(assessmentRouting['edit'])}">Edit Assessment properties</a></li>
    <li><a href="${utils:escapeLink(assessmentRouting['upload'])}">Replace Assessment Package Content</a></li>
    <li><a href="${utils:escapeLink(assessmentRouting['validate'])}">Show validation status</a></li>
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
            (You probably want to create some delivery settings to get more control over this!)
          </c:otherwise>
        </c:choose>
        </c:when>
        <c:otherwise>
          (A button allowing you to try this assessment out will appear here once you fix its validation issues)
        </c:otherwise>
      </c:choose>
    </li>
    <li>Delete Assignment (coming soon)</li>
    <li><a href="${utils:escapeLink(assessmentRouting['deliveries'])}">Manage deliveries of this Assessment</a></li>
  </ul>
</page:page>
