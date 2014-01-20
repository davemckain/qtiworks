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

  <header class="actionHeader">
    <nav class="breadcrumbs">
      <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">Assessment Launch Dashboard</a> &#xbb;
    </nav>
    <h2>LTI Resource Diagnostic Information</h2>
  </header>

  <h3>This Delivery</h3>
  <pre>${utils:dumpObject(thisDelivery)}</pre>
  <pre>${utils:dumpObject(thisDeliveryStatusReport)}</pre>

  <h3>This Assessment</h3>
  <pre>${utils:dumpObject(thisAssessmentStatusReport)}</pre>

  <h3>This LTI Resource</h3>
  <pre>${utils:dumpObject(thisLtiResource)}</pre>

  <h3>This LTI Identity Context</h3>
  <pre>${utils:dumpObject(thisLtiIdentityContext)}</pre>

  <h3>This LTI User</h3>
  <pre>${utils:dumpObject(thisLtiUser)}</pre>

  <h3>This LTI Context</h3>
  <pre>${utils:dumpObject(ltiContext)}</pre>

  <h3>This LTI Domain</h3>
  <pre>${utils:dumpObject(ltiDomain)}</pre>

  <h3>This LTI Domain</h3>
  <pre>${utils:dumpObject(ltiDomain)}</pre>

</page:ltipage>

