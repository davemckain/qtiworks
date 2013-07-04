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

  <nav class="breadcrumbs">
    <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">This Assessment launch</a></li> &#xbb;
    <a href="${utils:escapeLink(primaryRouting['listDeliverySettings'])}">Delivery Settings manager</a> &#xbb;
  </nav>
  <h2>Item Delivery Settings '${fn:escapeXml(deliverySettings.title)}'</h2>

  <div class="hints">
    <ul>
      <li>
        The current values for these settings are shown below. You can make changes to these if required.
        below.
      </li>
      <li>
        <page:postLink path="${deliverySettingsRouting['delete']}" confirm="Are you sure?" title="Delete these Delivery Settings"/>
      </li>
    </ul>
  </div>

  <%@ include file="/WEB-INF/jsp/includes/instructor/itemDeliverySettingsForm.jspf" %>

</page:ltipage>
