<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page>

  <h2>Instructor functions to be tidied up</h2>

  <ul>
    <li><a href="${utils:internalLink(pageContext, '/web/instructor/assessments')}">Manage your Assessments</a></li>
    <li><a href="${utils:internalLink(pageContext, '/web/instructor/assessments/upload')}">Upload new Assessment</a></li>
    <li><a href="${utils:internalLink(pageContext, '/web/instructor/deliverysettings')}">Manage your Item Delivery Settings</a></li>
  </ul>

</page:page>

