<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for creating a new Item Delivery settings

Model:

itemDeliverySettingsTemplate - form backing template

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Create Item Delivery Settings">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['deliverySettingsManager'])}">Delivery Settings Manager</a> &#xbb;
    </nav>
    <h2>
      <a href="${utils:escapeLink(primaryRouting['listItemDeliverySettings'])}">Item Delivery Settings</a> &#xbb;
      Create New
    </h2>
    <div class="hints">
      <p>
        This form lets you create a new set of Delivery Settings for use with deliveries of single Assessment Items.
      </p>
    </div>
  </header>

  <%@ include file="/WEB-INF/jsp/includes/instructor/itemDeliverySettingsForm.jspf" %>

  <p class="floatRight">
    <a href="${utils:escapeLink(primaryRouting['listItemDeliverySettings'])}">Cancel and return to Item Delivery Settings list</a>
  </p>

</page:page>
