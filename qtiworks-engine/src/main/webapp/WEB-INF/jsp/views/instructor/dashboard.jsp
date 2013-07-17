<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page>

  <header class="actionHeader">
    <h2>QTIWorks Dashboard</h2>
  </header>

  <div class="boxes">
    <div class="grid_6">
      <div class="box">
        <a href="${utils:escapeLink(primaryRouting['listAssessments'])}" class="boxButton assessments" title="Manage Assessments">
          <h3>Assessment Manager</h3>
          <div>Upload, manage and deliver your Assessments</div>
        </a>
      </div>
    </div>

    <div class="grid_6">
      <div class="box">
        <a href="${utils:escapeLink(primaryRouting['deliverySettingsManager'])}" class="boxButton deliverysettings" title="Manage Delivery Settings">
          <h3>Delivery Settings Manager</h3>
          <div>Manage the delivery settings to control how you Deliver your Assessments</div>
        </a>
      </div>
    </div>
  </div>

</page:page>

