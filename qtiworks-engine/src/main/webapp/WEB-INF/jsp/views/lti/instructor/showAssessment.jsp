<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Shows information about a particular Assessment

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<c:set var="nonTerminatedSessionCount" value="${assessmentStatusReport.nonTerminatedSessionCount}" scope="request"/>
<page:ltipage title="Assessment details">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Assessment Library</a> &#xbb;
    </nav>
    <h2>${fn:escapeXml(assessmentPackage.title)} (${fn:escapeXml(utils:formatAssessmentFileName(assessmentPackage))})</h2>
  </header>

  <div class="grid_6">
    <div class="infoBox">
      <div class="cat">Title</div>
      <div class="value">
        ${fn:escapeXml(assessmentPackage.title)}
      </div>
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

  <div class="grid_1">
    <div class="infoBox">
      <div class="cat">Upload</div>
      <div class="value">#${assessment.packageImportVersion}</div>
    </div>
  </div>

  <div class="grid_3">
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
      <div class="cat">Launchable?</div>
      <div class="value">
        ${assessmentPackage.launchable ? 'Yes' : 'No'}
      </div>
    </div>
  </div>

  <div class="grid_2">
    <div class="infoBox">
      <div class="cat">Valid?</div>
      <div class="value">
        <a href="${utils:escapeLink(assessmentRouting['validate'])}">
          <c:choose>
            <c:when test="${assessmentPackage.valid}">
              Yes
            </c:when>
            <c:when test="${assessmentPackage.errorCount > 0}">
              ${assessmentPackage.errorCount}&#xa0;
              ${assessmentPackage.errorCount > 1 ? 'errors' : 'error'}
            </c:when>
            <c:when test="${assessmentPackage.warningCount > 0}">
              ${assessmentPackage.warningCount}&#xa0;
              ${assessmentPackage.warningCount > 1 ? 'warnings' : 'warning'}
            </c:when>
          </c:choose>
        </a>
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

  <div class="grid_12">
    <div class="infoBox">
      <div class="cat">LTI Outcomes Settings</div>
      <div class="value">
        <c:choose>
          <c:when test="${!empty assessment.ltiResultOutcomeIdentifier}">
          Reporting variable <code>${assessment.ltiResultOutcomeIdentifier}</code>
          with range [${assessment.ltiResultMinimum}..${assessment.ltiResultMaximum}]
          </c:when>
          <c:otherwise>
            Not set up
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>

  <h3>Actions</h3>
  <ul class="menu">
    <li><a href="${utils:escapeLink(assessmentRouting['replace'])}">Replace Assessment Package Content</a></li>
    <li><a href="${utils:escapeLink(assessmentRouting['validate'])}">Show validation status</a></li>
    <c:if test="${assessmentPackage.launchable}">
      <li>
        <c:choose>
          <c:when test="${!empty deliverySettingsList}">
            Try out using Delivery Settings:
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
    </c:if>
    <li><a href="${utils:escapeLink(assessmentRouting['outcomesSettings'])}">Specify LTI outcomes reporting settings for this Assessment</a></li>
    <li>
      <page:postLink path="${assessmentRouting['delete']}"
        confirm="Are you sure? This will permanently delete the Assessment and all data gathered about it. There are currently ${nonTerminatedSessionCount} candidate sessions(s) running on this Assessment."
        title="Delete Assessment"/>
    </li>
  </ul>
</page:ltipage>
