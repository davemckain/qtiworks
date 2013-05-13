<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

LTI diagnostic

Model:

ltiLaunchData
isBasicLtiLaunch (boolean)

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="LTI Diagnostics">

  <h2>LTI Diangostic information</h2>

  <pre>${utils:dumpObject(ltiLaunchData)}</pre>

</page:page>

