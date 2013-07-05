<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

DeliverySettings manager

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="Delivery Settings manager">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
    </nav>
    <h2>Delivery Settings manager</h2>
    <div class="hints">
      <p>
        "Delivery Settings" are reusable sets of options for specifying how your assessmnts should
        be delivered. You can use this to control things that fall outside the scope of QTI, such as
        whether candidates can request a solution,
        how many attempts should be allowed, etc.
      </p>
    </div>
  </header>
  <ul class="menu">
    <li><a href="${utils:escapeLink(primaryRouting['listItemDeliverySettings'])}">Show existing Delivery Settings for running a single Assessment Item</a></li>
    <li><a href="${utils:escapeLink(primaryRouting['listTestDeliverySettings'])}">Show existing Delivery Settings for running a single Assessment Test</a></li>
    <li><a href="${utils:escapeLink(primaryRouting['createTestDeliverySettings'])}">Create new Delivery Settings for running an Assessment Test</a></li>
    <li><a href="${utils:escapeLink(primaryRouting['createItemDeliverySettings'])}">Create new Delivery Settings for running a single Assessment Item</a></li>
  </ul>

</page:ltipage>
