<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Shows a Delivery

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Assessment details">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Your Assessments</a> &#xbb;
      <a href="${utils:escapeLink(assessmentRouting['show'])}">
        ${fn:escapeXml(utils:formatAssessmentFileName(assessmentPackage))}
        [${fn:escapeXml(assessmentPackage.title)}]
      </a> &#xbb;
      <a href="${utils:escapeLink(assessmentRouting['deliveries'])}">Assessment Deliveries</a>
    </nav>
    <h2>Delivery '${fn:escapeXml(delivery.title)}'</h2>
  </header>

  <c:choose>
    <c:when test="${!assessmentPackage.launchable}">
      <p class="errorMessage">The assessment corresponding to this Delivery is not launchable! You must fix this before you let candidates run it!</p>
    </c:when>
    <c:when test="${!assessmentPackage.valid}">
      <p class="warningMessage">The assessment corresponding to this Delivery is not valid! You should fix this before you let candidates run it!</p>
    </c:when>
  </c:choose>

  <table class="dashboard">
    <tbody>
      <%-- Launchability status --%>
      <c:set var="status" value="${assessmentPackage.launchable ? (assessmentPackage.valid ? 'statusGood' : 'statusWarning' ) : 'statusError'}"/>
      <tr class="${status}">
        <td class="indicator"></td>
        <td class="category">
          <div class="name">Launchability:</div>
          <div class="value">
            <a href="${utils:escapeLink(assessmentRouting['show'])}">The Assessment corresponding to this Delivery</a>
            <c:choose>
              <c:when test="${!assessmentPackage.launchable}">
                is not launchable! You must fix this before you let candidates run it!
              </c:when>
              <c:when test="${!assessmentPackage.valid}">
                is not valid! You should fix this before you let candidates run it!
              </c:when>
              <c:otherwise>
                is launchable and valid
              </c:otherwise>
            </c:choose>
          </div>
        </td>
        <td class="actions">
          <c:if test="${assessmentPackage.launchable}">
            <page:postLink path="${utils:escapeLink(deliveryRouting['try'])}" title="Try Out"/>
          </c:if>
        </td>
      </tr>
      <%-- Single-link LTI status --%>
      <c:set var="status" value="${delivery.open && delivery.ltiEnabled ? 'statusGood' : 'statusError'}"/>
      <tr class="${status}">
        <td class="indicator"></td>
        <td class="category">
          <div class="name">Availability to candidates (via LTI link):</div>
          <div class="value">
            <c:choose>
              <c:when test="${delivery.open && delivery.ltiEnabled}">
                This Delivery is available to candidates via a LTI launch (details below)
              </c:when>
              <c:otherwise>
                This Delivery is not currently available to candidates
              </c:otherwise>
            </c:choose>
          </div>
        </td>
        <td class="actions">
          <page:postLink path="${utils:escapeLink(deliveryRouting['toggleAvailability'])}" title="${delivery.open ? 'Make Unavailable' : 'Make Available'}"/>
        </td>
      </tr>
      <%-- Delivery Settings --%>
      <c:set var="status" value="${empty deliverySettings ? 'statusWarning' : 'statusGood'}"/>
      <tr class="${status}">
        <td class="indicator"></td>
        <td class="category">
          <div class="name">Delivery Settings used:</div>
          <div class="value">
            <c:choose>
              <c:when test="${empty deliverySettings}">
                Not specified - will use QTIWorks default Delivery Settings
              </c:when>
              <c:otherwise>
                Using '${fn:escapeXml(deliverySettings.title)}'
              </c:otherwise>
            </c:choose>
          </div>
        </td>
        <td class="actions">
          <a href="${utils:escapeLink(deliveryRouting['edit'])}">Edit&#xa0;Delivery&#xa0;Properties</a>
        </td>
      </tr>
      <%-- Candidate Sessions --%>
      <tr class="statusOk">
        <td class="indicator"></td>
        <td class="category">
          <div class="name">Candidate Session Reporting &amp; Proctoring:</div>
          <div class="value">
            ${deliveryStatusReport.nonTerminatedSessionCount}
            candidate session${deliveryStatusReport.nonTerminatedSessionCount==1?'':'s'}
            currently running out of ${deliveryStatusReport.sessionCount} total
          </div>
        </td>
        <td class="actions">
          <a href="${deliveryRouting['candidateSessions']}">Show&#xa0;/&#xa0;Proctor&#xa0;candidate&#xa0;sessions</a>
        </td>
      </tr>
    </tbody>
  </table>
  <div class="floatRight">
    <page:postLink path="${deliveryRouting['delete']}"
      confirm="Are you sure? This will delete the Delivery and all candidate data collected for it."
      title="Delete Delivery"/>
  </div>

  <c:if test="${delivery.ltiEnabled}">
    <h3>LTI launch details</h3>
    <p class="hints">
      The details below can be used in a LTI Tool Consumer to enable candidates to run this Assessment Delivery.
      This should work in any VLE or similar tool that supports these kinds of LTI web links.
      (Note that it is preferable and simpler to use the more integrated "domain" launch for QTIWorks, if your
      LTI Tool Consumer (e.g. VLE) administrator has had this set up and made available for you.)
    </p>
    <ul>
      <li><b>Launch URL</b>: ${fn:escapeXml(deliveryRouting['ltiLaunch'])}</li>
      <li><b>Key</b>: <code>${delivery.id}X${delivery.ltiConsumerKeyToken}</code></li>
      <li><b>Secret</b>: <code>${delivery.ltiConsumerSecret}</code></li>
    </ul>
  </c:if>

</page:page>
