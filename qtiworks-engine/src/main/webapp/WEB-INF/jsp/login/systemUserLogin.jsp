<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Login">

  <form action="${utils:internalLink(pageContext, '/systemUserFormAuthenticator')}" method="post">
    <input type="hidden" id="protectedRequestUri" name="protectedRequestUri"
        value="${fn:escapeXml(requestScope['qtiworks.web.authn.protectedRequestUri'])}" />

    <h2>Instructor Login</h2>

    <c:if test="${!empty errors}">
      <ul class="formErrors">
        <c:forEach var="e" items="${errors}">
          <li>${e}</li>
        </c:forEach>
      </ul>
    </c:if>

    <div class="stdFormRow">
      <div class="grid_1">
        <label for="userId">Login ID:</label>
      </div>
      <div class="grid_2">
        <input size="16" id="loginName" name="loginName" type="text" tabindex="1" value="${loginName}"/>
      </div>
      <div class="grid_9">
        <div class="hints">
          If you don't already have an account you can <a href="${utils:internalLink(pageContext, '/signup')}" tabindex="5">sign up</a>
          for one.
        </div>
      </div>
    </div>
    <div class="clear"></div>
    <div class="stdFormRow">
      <div class="grid_1">
        <label for="password">Password:</label>
      </div>
      <div class="grid_10">
        <input size="16" id="password" name="password" type="password" tabindex="2" value="${password}" />
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

</page:page>
