<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for creating a new Test Delivery settings

Model:

testDeliverySettingsTemplate - form backing template

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Create Test Delivery Settings">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['deliverySettingsManager'])}">Delivery Settings Manager</a> &#xbb;
      <a href="${utils:escapeLink(primaryRouting['listTestDeliverySettings'])}">Test Delivery Settings</a> &#xbb;
    </nav>
    <h2>Create Test Delivery Settings</h2>
    <div class="hints">
      <p>
        This form lets you create a new set of Delivery Settings to use with deliveries of Assessment Tests.
      </p>
    </div>
  </header>

  <%@ include file="/WEB-INF/jsp/includes/instructor/testDeliverySettingsForm.jspf" %>

</page:page>
