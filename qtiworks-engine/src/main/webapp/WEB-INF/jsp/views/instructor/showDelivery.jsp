<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Shows a Delivery

Model:

delivery
assessment
assessmentPackage (most recent)
assessmentRouting (action -> URL)
deliveryRouting (action -> URL)
primaryRouting (action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Assessment details">

  <nav class="breadcrumbs">
    <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Your assessments</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment '${fn:escapeXml(assessment.name)}'</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['deliveries'])}">Assessment Deliveries</a>
  </nav>
  <h2>Delivery '${fn:escapeXml(delivery.title)}'</h2>

  <c:choose>
    <c:when test="${!assessmentPackage.launchable}">
      <p class="errorMessage">The assessment corresponding to this Delivery is not launchable! You must fix this before you let candidates run it!</p>
    </c:when>
    <c:when test="${!assessmentPackage.valid}">
      <p class="warningMessage">The assessment corresponding to this Delivery is not valid! You should fix this before you let candidates run it!</p>
    </c:when>
  </c:choose>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Title</div>
      <div class="value">${fn:escapeXml(delivery.title)}</div>
    </div>
  </div>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Delivery Settings used</div>
      <div class="value">
        <c:set var="deliverySettings" value="${delivery.deliverySettings}"/>
        <c:choose>
          <c:when test="${!empty deliverySettings}">
            ${fn:escapeXml(deliverySettings.title)}
          </c:when>
          <c:otherwise>
            (Default Delivery Settings)
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Created</div>
      <div class="value">${utils:formatDayDateAndTime(delivery.creationTime)}</div>
    </div>
  </div>

  <div class="clear"></div>

  <div class="grid_2">
    <div class="infoBox">
      <div class="cat">Open to candidates?</div>
      <div class="value">${delivery.open ? 'Yes' : 'No'}</div>
    </div>
  </div>

  <div class="grid_10">
    <div class="infoBox">
      <div class="cat">LTI enabled?</div>
      <div class="value">${delivery.ltiEnabled ? 'Yes' : 'No'}</div>
    </div>
  </div>

  <div class="clear"></div>

  <h4>LTI launch details</h4>
  <c:choose>
    <c:when test="${delivery.ltiEnabled}">
      <ul>
        <li><b>Launch URL</b>: ${fn:escapeXml(deliveryRouting['ltiLaunch'])}</li>
        <li><b>Consumer Key</b>: <code>${delivery.id}X${delivery.ltiConsumerKeyToken}</code></li>
        <li><b>Consumer Secret</b>: <code>${delivery.ltiConsumerSecret}</code></li>
      </ul>
    </c:when>
    <c:otherwise>
      <p>Details will appear here once you enable LTI for this Delivery.</p>
    </c:otherwise>
  </c:choose>

  <h4>Actions</h4>

  <ul>
    <li><a href="${utils:escapeLink(deliveryRouting['edit'])}">Edit Delivery Properties</a></li>
    <c:if test="${true or assessmentPackage.launchable}">
      <li><page:postLink path="${utils:escapeLink(deliveryRouting['try'])}" title="Try Out"/></li>
    </c:if>
    <li><page:postLink path="${deliveryRouting['delete']}"
      confirm="Are you sure? This will delete the Delivery and all candidate data collected for it."
      title="Delete Delivery"/></li>
    <li><a href="${utils:escapeLink(deliveryRouting['candidateSessions'])}">Candidate Session Reports &amp; Proctoring</a></li>
  </ul>

</page:page>
