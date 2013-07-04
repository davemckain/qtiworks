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
    <li><a href="${utils:escapeLink(primaryRouting['debug'])}">Diagnostics</a></li>
  </ul>

</page:ltipage>

