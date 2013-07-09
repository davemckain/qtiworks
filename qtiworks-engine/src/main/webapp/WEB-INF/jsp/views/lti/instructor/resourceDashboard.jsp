<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

LTI resource dashboard (after domain-level launch)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Assessment Launcher - Getting Started">

  <header class="actionHeader">
    <h2>Assessment Launch Dashboard</h2>
    <p class="hints">
      This dashboard lets you control the Assessment that is going to be delivered to candidates
      when they launch this resource.
    <p>
  </header>

  <div class="dashboardRow">
    <div class="grid_1">
      <div class="trafficLight green">&#xa0;</div>
    </div>
    <div class="grid_8">
      <div class="name">Selected Assessment:</div>
      <div class="value">
        <a href="${utils:escapeLink(thisAssessmentRouting['show'])}">
          <c:out value="${thisAssessment.title}"/>
          (<c:out value="${thisAssessment.name}"/>)
        </a>
      </div>
    </div>
    <div class="grid_2">
      <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Select from Assessment Library</a>
    </div>
    <div class="clear"></div>
  </div>
  <div class="dashboardRow">
    <div class="grid_1">
      <c:choose>
        <c:when test="${empty thisAssessment || empty thisAssessmentPackage || !thisAssessmentPackage.launchable}">
          <div class="trafficLight red">&#xa0;</div>
        </c:when>
        <c:when test="${!thisAssessmentPackage.valid}">
          <div class="trafficLight amber">&#xa0;</div>
        </c:when>
        <c:otherwise>
          <div class="trafficLight green">&#xa0;</div>
        </c:otherwise>
      </c:choose>
    </div>
    <div class="grid_8">
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
    </div>
    <div class="grid_2">
      <page:postLink path="${primaryRouting['try']}" title="Try out"/>
    </div>
    <div class="clear"></div>
  </div>
  <div class="dashboardRow">
    <div class="grid_1">
      <c:choose>
        <c:when test="${empty theseDeliverySettings}">
          <div class="trafficLight amber">&#xa0;</div>
        </c:when>
        <c:otherwise>
          <div class="trafficLight green">&#xa0;</div>
        </c:otherwise>
      </c:choose>
    </div>
    <div class="grid_8">
      <div class="name">Delivery Settings:</div>
      <div class="value">
        <c:choose>
          <c:when test="${empty theseDeliverySettings}">
            None chosen - defaults will be used.
          </c:when>
          <c:otherwise>
            <a href="${utils:escapeLink(theseDeliverySettingsRouting['showOrEdit'])}">
              <c:out value="${theseDeliverySettings.title}"/>
            </a>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
    <div class="grid_2">
      <c:choose>
        <c:when test="${!empty thisAssessment && thisAssessment.assessmentType=='ASSESSMENT_ITEM'}">
          <a href="${utils:escapeLink(primaryRouting['listItemDeliverySettings'])}">Manage / Select</a>
        </c:when>
        <c:when test="${!empty thisAssessment && thisAssessment.assessmentType=='ASSESSMENT_TEST'}">
          <a href="${utils:escapeLink(primaryRouting['listTestDeliverySettings'])}">Manage / Select</a>
        </c:when>
        <c:otherwise>
          <a href="${utils:escapeLink(primaryRouting['deliverySettingsManager'])}">Manage / Select</a>
        </c:otherwise>
      </c:choose>
    </div>
    <div class="clear"></div>
  </div>
  <div class="dashboardRow">
    <div class="grid_1">
      <c:choose>
        <c:when test="${thisDelivery.open}">
          <div class="trafficLight green">&#xa0;</div>
        </c:when>
        <c:otherwise>
          <div class="trafficLight red">&#xa0;</div>
        </c:otherwise>
      </c:choose>
    </div>
    <div class="grid_8">
      <div class="name">Currently available to candidates:</div>
      <div class="value">
        <c:choose>
          <c:when test="${thisDelivery.open}">
            Yes - candidates may currently launch this assessment
          </c:when>
          <c:otherwise>
            No - candidates cannot currently launch this assessment
          </c:otherwise>
        </c:choose>
      </div>
    </div>
    <div class="grid_2">
      <page:postLink path="${primaryRouting['toggleAvailability']}" title="Toggle"/>
    </div>
    <div class="clear"></div>
  </div>
  <div class="dashboardRow">
    <div class="grid_1">
      <c:choose>
        <c:when test="${!empty thisAssessment.ltiResultOutcomeIdentifier}">
          <div class="trafficLight green">&#xa0;</div>
        </c:when>
        <c:otherwise>
          <div class="trafficLight red">&#xa0;</div>
        </c:otherwise>
      </c:choose>
    </div>
    <div class="grid_8">
      <div class="name">LTI Outcomes Reporting Setup:</div>
      <div class="value">
        <c:choose>
          <c:when test="${!empty thisAssessment.ltiResultOutcomeIdentifier}">
            Reporting outcome <code>${thisAssessment.ltiResultOutcomeIdentifier}</code>
            with range [${thisAssessment.ltiResultMinimum}..${thisAssessment.ltiResultMaximum}]
          </c:when>
          <c:otherwise>
            Not set up. LTI outcomes will not be sent back until this is set up.
          </c:otherwise>
        </c:choose>
      </div>
    </div>
    <div class="grid_2">
      <a href="${thisAssessmentRouting['outcomesSettings']}">Set up</a>
    </div>
    <div class="clear"></div>
  </div>
  <div class="dashboardRow">
    <div class="grid_1">
      <div class="trafficLight green">&#xa0;</div>
    </div>
    <div class="grid_8">
      <div class="name">Candidate Session Reporting &amp; Proctoring:</div>
      <div class="value">
        ${thisDeliveryStatusReport.nonTerminatedSessionCount}
        session${thisDeliveryStatusReport.nonTerminatedSessionCount==1?'':'s'}
        currently running out of ${thisDeliveryStatusReport.sessionCount} total.
      </div>
    </div>
    <div class="grid_2">
      <a href="${primaryRouting['listCandidateSessions']}">Show candidate sessions</a>
    </div>
    <div class="clear"></div>
  </div>
  <div class="clear"></div>

  <a href="${utils:escapeLink(primaryRouting['debug'])}">Show developer diagnostic information</a>

</page:ltipage>

