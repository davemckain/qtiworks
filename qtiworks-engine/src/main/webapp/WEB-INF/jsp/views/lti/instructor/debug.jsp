<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Top page for managing LTI resources (after domain-level launch)

Model:

ltiUser
primaryRouting (action -> URL)
assessmentRouting (aid -> action -> URL)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:ltipage title="LTI Diagnostics">

  <nav class="breadcrumbs">
    <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">This Assessment launch</a></li> &#xbb;
  </nav>
  <h2>LTI resource diagnostic information</h2>

  <h3>This LTI user</h3>
  <pre>${utils:dumpObject(ltiUser)}</pre>

  <h3>This LTI resource</h3>
  <pre>${utils:dumpObject(ltiResource)}</pre>

  <h3>This LTI context</h3>
  <pre>${utils:dumpObject(ltiResource.ltiContext)}</pre>

  <h3>This LTI domain</h3>
  <pre>${utils:dumpObject(ltiResource.ltiContext.ltiDomain)}</pre>

  <h3>This Delivery</h3>
  <pre>${utils:dumpObject(ltiResource.delivery)}</pre>

  <h3>Selected Assessment</h3>
  <pre>${utils:dumpObject(ltiResource.delivery.assessment)}</pre>

  <h3>Selected Delivery Settings</h3>
  <pre>${utils:dumpObject(ltiResource.delivery.deliverySettings)}</pre>

</page:ltipage>

