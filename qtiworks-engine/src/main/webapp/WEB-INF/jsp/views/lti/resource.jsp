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

  <p>Content goes here</p>

  <ul>
    <li><a href="${utils:escapeLink(primaryRouting['listAssessments'])}">Assessment library</a></li>
    <li><a href="${utils:escapeLink(primaryRouting['listDeliverySettings'])}">Delivery Settings manager</a></li>
  </ul>

  <h3>Congratulations!</h3>


  <p>
    If you are seeing this then you have managed to launch the QTIWorks
    instructor interface for managing and delivering an assessment here.
  </p>
  <p>
    The interface is still being built and will appear shortly. In the
    mean time, please enjoy the diagnostic information below.
  </p>

  <h3>LTI Diagnostic information</h3>

  <h4>This LTI user</h4>
  <pre>${utils:dumpObject(ltiUser)}</pre>

  <h4>This LTI resource</h4>
  <pre>${utils:dumpObject(ltiResource)}</pre>

  <h4>This LTI context</h4>
  <pre>${utils:dumpObject(ltiResource.ltiContext)}</pre>

  <h4>This LTI domain</h4>
  <pre>${utils:dumpObject(ltiResource.ltiContext.ltiDomain)}</pre>

</page:ltipage>

