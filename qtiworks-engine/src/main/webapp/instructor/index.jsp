<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page>

  <h2>QTIWorks Dashboard</h2>

  <div class="hints">
    This is where most of the "real" functionality of QTIWorks will build up. Things will be rather disorganised
    at first, so please be patient for a while!
  </div>

  <ul>
    <li><a href="${utils:internalLink(pageContext, '/web/instructor/assessments')}">Manage your Assessments</a></li>
    <li><a href="${utils:internalLink(pageContext, '/web/instructor/assessments/upload')}">Upload new Assessment</a></li>
    <li><a href="${utils:internalLink(pageContext, '/web/instructor/deliverysettings')}">Manage your Item Delivery Settings</a></li>
  </ul>

</page:page>

