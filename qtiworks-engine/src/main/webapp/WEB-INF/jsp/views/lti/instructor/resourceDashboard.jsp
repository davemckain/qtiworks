<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

LTI resource dashboard (after domain-level launch)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Assessment Launch Dashboard">

  <header class="actionHeader">
    <h2>Assessment Launch Dashboard</h2>
    <p class="hints">
      This dashboard lets you control the Assessment that is going to be delivered to candidates
      when they launch this resource.
    <p>
  </header>

  <table class="dashboard">
    <tbody>
      <tr class="statusOk">
        <td class="indicator"></td>
        <td class="category">
          <div class="name">Selected Assessment:</div>
          <div class="value">
            <a href="${utils:escapeLink(thisAssessmentRouting['show'])}">
              <c:out value="${utils:formatAssessmentFileName(thisAssessmentPackage)}"/>
            </a>
            <span class="assessmentTitle"> [${fn:escapeXml(thisAssessmentPackage.title)}]</span>
          </div>
        </td>
        <td class="actions">
          <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Select&#xa0;/&#xa0;Upload&#xa0;from&#xa0;Assessment&#xa0;Library</a>
        </td>
      </tr>
      <%-- Validation status --%>
      <c:set var="status">
        <c:choose>
          <c:when test="${empty thisAssessment || empty thisAssessmentPackage || !thisAssessmentPackage.launchable}">statusError</c:when>
          <c:when test="${!thisAssessmentPackage.valid}">statusWarning</c:when>
          <c:otherwise>statusOk</c:otherwise>
        </c:choose>
      </c:set>
      <tr class="${status}">
        <td class="indicator"></td>
        <td class="category">
          <div class="name">Validation Status:</div>
          <div class="value">
            <a href="${utils:escapeLink(thisAssessmentRouting['validate'])}">
              <c:choose>
                <c:when test="${empty thisAssessment || empty thisAssessmentPackage || !thisAssessmentPackage.launchable}">
                  This Assessment cannot be run and needs fixed
                </c:when>
                <c:when test="${!thisAssessmentPackage.valid}">
                  <c:choose>
                    <c:when test="${thisAssessmentPackage.errorCount > 0}">
                      ${thisAssessmentPackage.errorCount}&#xa0;validation&#xa0;
                      ${thisAssessmentPackage.errorCount > 1 ? 'errors' : 'error'}
                    </c:when>
                    <c:when test="${thisAssessmentPackage.warningCount > 0}">
                      ${thisAssessmentPackage.warningCount}&#xa0;validation&#xa0;
                      ${thisAssessmentPackage.warningCount > 1 ? 'warnings' : 'warning'}
                    </c:when>
                  </c:choose>
                </c:when>
                <c:otherwise>
                  All validation checks passed
                </c:otherwise>
              </c:choose>
            </a>
          </div>
        </td>
        <td class="actions">
          <page:postLink path="${primaryRouting['try']}" title="Try / Debug Assessment"/>
        </td>
      </tr>
      <%-- Availability --%>
      <c:set var="status" value="${thisDelivery.open ? 'statusOk' : 'statusError'}"/>
      <tr class="${status}">
        <td class="indicator"></td>
        <td class="category">
          <div class="name">Availability to candidates:</div>
          <div class="value">
            <c:choose>
              <c:when test="${thisDelivery.open}">
                This Assessment launch is currently available to candidates
              </c:when>
              <c:otherwise>
                You have not yet made this Assessment launch available to candidates
              </c:otherwise>
            </c:choose>
          </div>
        </td>
        <td class="actions">
          <page:postLink path="${primaryRouting['toggleAvailability']}" title="${thisDelivery.open ? 'Make Unavailable' : 'Make Available'}"/>
        </td>
      </tr>
      <%-- LTI status --%>
      <c:set var="status" value="${empty thisAssessment.ltiResultOutcomeIdentifier ? 'statusError' : 'statusOk'}"/>
      <tr class="${status}">
        <td class="indicator"></td>
        <td class="category">
          <div class="name">LTI Outcomes Reporting Setup:</div>
          <div class="value">
            <c:choose>
              <c:when test="${!empty thisAssessment.ltiResultOutcomeIdentifier}">
                Reporting outcome <code>${thisAssessment.ltiResultOutcomeIdentifier}</code>
                with range [${thisAssessment.ltiResultMinimum}..${thisAssessment.ltiResultMaximum}]
              </c:when>
              <c:otherwise>
                Not set up. Outcomes cannot be returned until this is set up
              </c:otherwise>
            </c:choose>
          </div>
        </td>
        <td class="actions">
          <a href="${thisAssessmentRouting['outcomesSettings']}">Set&#xa0;up&#xa0;LTI&#xa0;outcomes</a>
        </td>
      </tr>
      <%-- Delivery Settings --%>
      <tr class="statusOk">
        <td class="indicator"></td>
        <td class="category">
          <div class="name">Delivery Settings:</div>
          <div class="value">
            <c:choose>
              <c:when test="${empty theseDeliverySettings}">
                Using default delivery settings
                <c:choose>
                  <c:when test="${!empty thisAssessment && thisAssessment.assessmentType=='ASSESSMENT_ITEM'}">
                    for single items
                  </c:when>
                  <c:when test="${!empty thisAssessment && thisAssessment.assessmentType=='ASSESSMENT_TEST'}">
                    for tests
                  </c:when>
                </c:choose>
              </c:when>
              <c:otherwise>
                <a href="${utils:escapeLink(theseDeliverySettingsRouting['showOrEdit'])}">
                  <c:out value="${theseDeliverySettings.title}"/>
                </a>
              </c:otherwise>
            </c:choose>
          </div>
        </td>
        <td class="actions">
          <c:choose>
            <c:when test="${!empty thisAssessment && thisAssessment.assessmentType=='ASSESSMENT_ITEM'}">
              <a href="${utils:escapeLink(primaryRouting['listItemDeliverySettings'])}">Manage / Select Delivery Settings</a>
            </c:when>
            <c:when test="${!empty thisAssessment && thisAssessment.assessmentType=='ASSESSMENT_TEST'}">
              <a href="${utils:escapeLink(primaryRouting['listTestDeliverySettings'])}">Manage / Select Delivery Settings</a>
            </c:when>
            <c:otherwise>
              <a href="${utils:escapeLink(primaryRouting['deliverySettingsManager'])}">Manage / Select Delivery Settings</a>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
      <%-- Candidate Sessions --%>
      <tr class="statusOk">
        <td class="indicator"></td>
        <td class="category">
          <div class="name">Candidate Session Reporting &amp; Proctoring:</div>
          <div class="value">
            ${thisDeliveryStatusReport.nonTerminatedSessionCount}
            candidate session${thisDeliveryStatusReport.nonTerminatedSessionCount==1?'':'s'}
            currently running out of ${thisDeliveryStatusReport.sessionCount} total
          </div>
        </td>
        <td class="actions">
          <a href="${primaryRouting['listCandidateSessions']}">Show&#xa0;/&#xa0;Proctor&#xa0;candidate&#xa0;sessions</a>
        </td>
      </tr>
    </tbody>
  </table>

</page:ltipage>

