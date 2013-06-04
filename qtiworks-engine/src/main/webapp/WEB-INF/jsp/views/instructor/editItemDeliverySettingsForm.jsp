<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for creating a new Item Delivery settings

Model:

itemDeliverySettings - current settings
itemDeliverySettingsTemplate - form backing template

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="View/Edit Item Delivery Settings">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:internalLink(pageContext, '/web/instructor/deliverysettings')}">Your Delivery Settings</a> &#xbb;
  </nav>
  <h2>Item Delivery Settings '${fn:escapeXml(itemDeliverySettings.title)}'</h2>

  <div class="hints">
    <p>
      The current values for these settings are shown below. You can make changes to these if required.
    </p>
    <p>
      (Functionality for deleting these settings will appear shortly!)
    </p>
  </div>

  <%@ include file="/WEB-INF/jsp/includes/instructor/itemDeliverySettingsForm.jspf" %>

</page:page>
