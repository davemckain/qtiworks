<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Login">

  <form action="${utils:internalLink(pageContext, '/systemUserFormAuthenticator')}" method="post">
    <input type="hidden" id="protectedRequestUri" name="protectedRequestUri"
        value="${fn:escapeXml(requestScope['qtiworks.web.authn.protectedRequestUri'])}" />

    <h2>Individual Account Login</h2>

    <p>
      <div class="hints">
        If you have been granted an individual account for QTIWorks, then please use the form below to log in.
      </div>
      <div class="hints">
        Find out more about <a href="${utils:internalLink(pageContext, '/signup')}" tabindex="5">accessing QTIWorks</a>.
      </div>
    </p>

    <c:if test="${!empty errors}">
      <ul class="formErrors">
        <c:forEach var="e" items="${errors}">
          <li>${fn:escapeXml(e)}</li>
        </c:forEach>
      </ul>
    </c:if>

    <div class="stdFormRow">
      <div class="grid_1">
        <label for="userId">Login ID:</label>
      </div>
      <div class="grid_2">
        <input size="16" id="loginName" name="loginName" type="text" tabindex="1" value="${fn:escapeXml(loginName)}"/>
      </div>
    </div>
    <div class="clear"></div>
    <div class="stdFormRow">
      <div class="grid_1">
        <label for="password">Password:</label>
      </div>
      <div class="grid_10">
        <input size="16" id="password" name="password" type="password" tabindex="2" value="${fn:escapeXml(password)}" />
      </div>
    </div>
    <div class="clear"></div>
    <div class="stdFormRow">
      <div class="grid_1">
        <input type="submit" value="Login" tabindex="3" />
      </div>
      <div class="grid_1">
        <input type="reset"  value="Clear" tabindex="4" />
      </div>
    </div>
    <div class="clear"></div>
  </form>

  <h2 style="margin-top:2em">Deleting your QTIWorks account and/or LTI Connections</h2>

  <p>
    If you have previously been given a QTIWorks individual account and/or LTI Connection,
    then you may request the deletion of all of your data at any time by emailing the
    administrator of this instance of QTIWorks
    (<a href="mailto:${qtiWorksDeploymentSettings.adminEmailAddress}"><c:out value="${qtiWorksDeploymentSettings.adminName}"/></a>).
  </p>

</page:page>
