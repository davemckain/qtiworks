<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Shows a Delivery

Model:

delivery
assessment
assessmentRouting (action -> URL)
deliveryRouting (action -> URL)
instructorAssessmentRouting (action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Assessment details">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:escapeLink(instructorAssessmentRouting['listAssessments'])}">Your assessments</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['show'])}">Assessment '${fn:escapeXml(assessment.name)}'</a> &#xbb;
    <a href="${utils:escapeLink(assessmentRouting['deliveries'])}">Assessment Deliveries</a>
  </nav>
  <h2>Delivery '${fn:escapeXml(delivery.title)}'</h2>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Title</div>
      <div class="value">${fn:escapeXml(delivery.title)}</div>
    </div>
  </div>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Delivery Settings used</div>
      <div class="value">${fn:escapeXml(delivery.deliverySettings.title)}</div>
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
      Details will appear here once you enable LTI for this Delivery.
    </c:otherwise>
  </c:choose>

  <h4>Actions</h4>

  <ul>
    <li><a href="${utils:escapeLink(deliveryRouting['edit'])}">Edit Delivery Properties</a></li>
    <li><page:postLink path="${utils:escapeLink(deliveryRouting['try'])}" title="Try Out"/></li>
    <li>Delete Delivery</li>
    <li><a href="${utils:escapeLink(deliveryRouting['candidateSummaryReport'])}">View Candidate Reports</a></li>
  </ul>

</page:page>
