<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

LTI diagnostic

Model:

object - Object to be dumped

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="LTI Diagnostics">

  <h2>LTI Diagnostic information</h2>

  <pre>${utils:dumpObject(object)}</pre>

</page:page>

