<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Form for creating a new Test Delivery settings

Model:

testDeliverySettingsTemplate - form backing template

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Create new Test Delivery Settings">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/instructor/')}">QTIWorks Dashboard</a> &#xbb;
    <a href="${utils:internalLink(pageContext, '/web/instructor/deliverysettings')}">Your Delivery Settings</a> &#xbb;
  </nav>
  <h2>Create Test Delivery Settings</h2>

  <div class="hints">
    This form lets you create a new set of test delivery settings for you to use.
  </div>

  <%@ include file="/WEB-INF/jsp/includes/instructor/testDeliverySettingsForm.jspf" %>

</page:page>
