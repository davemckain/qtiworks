<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

DeliverySettings manager

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Delivery Settings manager">

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['dashboard'])}">QTIWorks Dashboard</a> &#xbb;
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

  <div class="boxes">
    <div class="grid_6">
      <div class="box">
        <a href="${utils:escapeLink(primaryRouting['listItemDeliverySettings'])}" class="boxButton itemdeliverysettings" title="Manage Item Delivery Settings">
          <h3>For single Assessment Items</h3>
          <div>Manage Delivery Settings for single Assessment Items</div>
        </a>
      </div>
    </div>
    <div class="grid_6">
      <div class="box">
        <a href="${utils:escapeLink(primaryRouting['listTestDeliverySettings'])}" class="boxButton testdeliverysettings" title="Manage Test Delivery Settings">
          <h3>For Assessment Tests</h3>
          <div>Manage Delivery Settings for Assessment Tests</div>
        </a>
      </div>
    </div>
    <div class="clear"></div>
  </div>

</page:page>
