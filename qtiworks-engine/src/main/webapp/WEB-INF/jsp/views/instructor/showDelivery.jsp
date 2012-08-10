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
      <div class="value">${fn:escapeXml(itemDelivery.itemDeliverySettings.title)}</div>
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

  <div class="grid_12">
    <div class="infoBox">
      <div class="cat">LTI launch URL</div>
      <%-- FIXME: Need independent URL here! --%>
      <div class="value">http://www2.ph.ed.ac.uk/qtiworks/lti/launch/${itemDelivery.id}</div>
    </div>
  </div>

  <div class="clear"></div>

  <div class="grid_6">
    <div class="infoBox">
      <div class="cat">LTI consumer key</div>
      <div class="value">${itemDelivery.ltiConsumerKey}</div>
    </div>
  </div>
  <div class="grid_6">
    <div class="infoBox">
      <div class="cat">LTI consumer secret</div>
      <div class="value">${itemDelivery.ltiConsumerSecret}</div>
    </div>
  </div>


  <h4>Actions</h4>

  <ul>
    <li><a href="${utils:escapeLink(deliveryRouting['edit'])}">Edit Delivery Properties</a></li>
    <li>Change Delivery Settings</li>
    <li>Get LTI Link</li>
    <li>Delete Delivery</li>
    <li>View Candidate attempt data</li>

</page:page>
