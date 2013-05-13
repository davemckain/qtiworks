<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Login">

  <form action="${utils:internalLink(pageContext, '/instructorFormAuthenticator')}" method="post">
    <input type="hidden" id="protectedRequestUri" name="protectedRequestUri"
        value="${fn:escapeXml(requestScope['qtiworks.web.authn.protectedRequestUri'])}" />

    <h2>Instructor Login</h2>

    <c:if test="${!empty errors}">
      <ul class="errors">
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
        <input size="8" id="userId" name="userId" type="text" />
      </div>
      <div class="grid_9">
        <div class="hints">
          If you don't already have an account you can <a href="${utils:internalLink(pageContext, '/signup')}">sign up</a>
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
        <input size="8" id="password" name="password" type="password" />
      </div>
    </div>
    <div class="clear"></div>
    <div class="stdFormRow">
      <div class="grid_1">
        <input type="submit" value="Login" />
      </div>
      <div class="grid_1">
        <input type="reset"  value="Clear" />
      </div>
    </div>
    <div class="clear"></div>
  </form>

</page:page>
