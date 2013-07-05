<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Lists Item DeliverySettings associated with LTI context

Additional Model attrs:

deliverySettingsList
deliverySettingsListRouting: dsid -> action -> URL

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Item Delivery Settings">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['deliverySettingsManager'])}">Delivery Settings Manager</a> &#xbb;
    </nav>
    <h2>Item Delivery Settings</h2>
    <div class="hints">
      <p>
        Your Delivery Settings for running single Assessment Items are shown below.
      </p>
    </div>
  </header>
  <ul class="menu">
    <li><a href="${utils:escapeLink(primaryRouting['createItemDeliverySettings'])}">Create new Delivery Settings for running a single Assessment Item</a></li>
  </ul>
  <%@ include file="/WEB-INF/jsp/includes/instructor/deliverySettingsList.jsp" %>
</page:ltipage>
