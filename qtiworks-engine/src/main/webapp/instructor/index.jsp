<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page>

  <h2>QTIWorks Dashboard</h2>

  <div class="hints">
    This is where most of the "real" functionality of QTIWorks will build up. Things will be rather disorganised
    at first, so please be patient for a while!
  </div>

  <div class="boxes">
    <div class="grid_6">
      <div class="box">
        <a href="${utils:internalLink(pageContext, '/web/instructor/assessments')}" class="boxButton assessments" title="Run">
          <h3>Manage Assessments</h3>
          <div>Upload, manage and deliver your Assessments</div>
        </a>
      </div>
    </div>

    <div class="grid_6">
      <div class="box">
        <a href="${utils:internalLink(pageContext, '/web/instructor/deliverysettings')}" class="boxButton deliverysettings" title="Samples">
          <h3>Manage Delivery Settings</h3>
          <div>Manage your delivery settings</div>
        </a>
      </div>
    </div>
  </div>

</page:page>

