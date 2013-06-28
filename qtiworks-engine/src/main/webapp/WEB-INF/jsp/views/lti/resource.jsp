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
<page:page title="LTI Diagnostics">

  <h2><c:out value="${utils:formatLtiResourceTitle(ltiResource)}"/></a></h2>

  <h3>LTI Diagnostic information</h3>

  <h4>Current resource</h4>
  <pre>${utils:dumpObject(ltiResource)}</pre>

  <h4>Current user</h4>
  <pre>${utils:dumpObject(ltiUser)}</pre>

</page:page>

