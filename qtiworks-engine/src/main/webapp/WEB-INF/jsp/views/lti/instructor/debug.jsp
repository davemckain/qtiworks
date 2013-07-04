<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Top page for managing LTI resources (after domain-level launch)

Model:

thisLtiUser
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
  <pre>${utils:dumpObject(thisLtiUser)}</pre>

  <h3>This LTI resource</h3>
  <pre>${utils:dumpObject(thisLtiResource)}</pre>

  <h3>This LTI context</h3>
  <pre>${utils:dumpObject(ltiContext)}</pre>

  <h3>This LTI domain</h3>
  <pre>${utils:dumpObject(ltiDomain)}</pre>

  <h3>This Delivery</h3>
  <pre>${utils:dumpObject(thisDelivery)}</pre>

</page:ltipage>

