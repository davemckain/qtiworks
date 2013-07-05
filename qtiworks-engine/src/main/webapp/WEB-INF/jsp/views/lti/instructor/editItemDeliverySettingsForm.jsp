<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for editing exsting Item Delivery settings

Model:

deliverySettings - current settings
deliverySettingsRouting
itemDeliverySettingsTemplate - form backing template

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="View/Edit Item Delivery Settings">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['deliverySettingsManager'])}">Delivery Settings Manager</a> &#xbb;
    <a href="${utils:escapeLink(primaryRouting['listItemDeliverySettings'])}">Item Delivery Settings</a> &#xbb;
    </nav>
    <h2>${fn:escapeXml(deliverySettings.title)}</h2>
    <div class="hints">
      <p>
        The current values for these settings are shown below. You can make changes to these if required below, or delete them
        if they are no longer required.
      </p>
    </div>
  </header>

  <ul class="menu">
    <li>
      <page:postLink path="${deliverySettingsRouting['delete']}" confirm="Are you sure?" title="Delete these Delivery Settings"/>
    </li>
  </ul>

  <%@ include file="/WEB-INF/jsp/includes/instructor/itemDeliverySettingsForm.jspf" %>

</page:ltipage>
