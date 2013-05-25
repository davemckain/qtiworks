<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Shows information about a particular Assessment

Model:

assessment
assessmentPackage (most recent)
deliverySettingsList (List<DeliverySettings> - possibly empty)
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

  <div class="grid_6">
    <div class="infoBox">
      <div class="cat">Title</div>
      <div class="value">${fn:escapeXml(assessment.title)}</div>
    </div>
  </div>

  <div class="grid_2">
    <div class="infoBox">
      <div class="cat">Assessment Type</div>
      <div class="value">${utils:formatAssessmentType(assessment.assessmentType)}</div>
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

  <div class="grid_2">
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
        <c:when test="${!empty deliverySettingsList}">
          Try out using:
          <ul>
            <c:forEach var="deliverySettings" items="${deliverySettingsList}">
              <li>
                <page:postLink path="${assessmentRouting['try']}/${deliverySettings.id}" title="${fn:escapeXml(deliverySettings.title)}"/>
              </li>
            </c:forEach>
          </ul>
        </c:when>
        <c:otherwise>
          <%-- No options exist yet, so allow try out with a default set, which will be created automatically --%>
          <page:postLink path="${assessmentRouting['try']}" title="Try Out"/>
          (You probably want to create some delivery settings to get more control over this!)
        </c:otherwise>
      </c:choose>
    </li>
    <li><a href="${utils:escapeLink(assessmentRouting['deliveries'])}">Manage deliveries of this Assessment</a></li>
    <li><page:postLink path="${assessmentRouting['delete']}"
      confirm="Are you sure? This will delete the Assessment and all associated Deliveries and Candidate Data"
      title="Delete Assessment"/></li>
  </ul>
</page:page>
