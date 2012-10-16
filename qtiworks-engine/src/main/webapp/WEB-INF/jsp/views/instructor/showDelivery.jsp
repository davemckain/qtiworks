<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Shows a Delivery

Model:

itemDelivery
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
  <h2>Delivery '${fn:escapeXml(itemDelivery.title)}'</h2>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Title</div>
      <div class="value">${fn:escapeXml(itemDelivery.title)}</div>
    </div>
  </div>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Delivery Settings used</div>
      <div class="value">${fn:escapeXml(itemDelivery.deliverySettings.title)}</div>
    </div>
  </div>

  <div class="grid_4">
    <div class="infoBox">
      <div class="cat">Created</div>
      <div class="value">${utils:formatDayDateAndTime(itemDelivery.creationTime)}</div>
    </div>
  </div>

  <div class="clear"></div>

  <div class="grid_2">
    <div class="infoBox">
      <div class="cat">Open to candidates?</div>
      <div class="value">${itemDelivery.open ? 'Yes' : 'No'}</div>
    </div>
  </div>

  <div class="grid_10">
    <div class="infoBox">
      <div class="cat">LTI enabled?</div>
      <div class="value">${itemDelivery.ltiEnabled ? 'Yes' : 'No'}</div>
    </div>
  </div>

  <div class="clear"></div>

  <h4>LTI launch details</h4>
  <c:choose>
    <c:when test="${itemDelivery.ltiEnabled}">
      <ul>
        <li><b>Launch URL</b>: ${fn:escapeXml(deliveryRouting['ltiLaunch'])}</li>
        <li><b>Consumer Key</b>: <code>${itemDelivery.id}X${itemDelivery.ltiConsumerKeyToken}</code></li>
        <li><b>Consumer Secret</b>: <code>${itemDelivery.ltiConsumerSecret}</code></li>
      </ul>
    </c:when>
    <c:otherwise>
      Details will appear here once you enable LTI for this Delivery.
    </c:otherwise>
  </c:choose>

  <h4>Actions</h4>

  <ul>
    <li><a href="${utils:escapeLink(deliveryRouting['edit'])}">Edit Delivery Properties</a></li>
    <li>
      <form action="${utils:escapeLink(deliveryRouting['try'])}" method="post">
        <input type="submit" value="Try out">
      </form>
    </li>
    <li>Delete Delivery</li>
    <li>View Candidate attempt data</li>

</page:page>
