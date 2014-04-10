<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page cssClass="homepage" title="QTIWorks">

  <div class="grid_12">
    <div class="intro">
      QTIWorks is a new open-source system for managing, verifying and delivering Question &amp; Test
      Interoperability (QTI) v2.1 assessment items and tests. It can be launched from any system supporting the
      Learning Tools Interoperability (LTI) v1.1 specification.
      <a href="${utils:internalLink(pageContext, '/about/')}" title="About QTIWorks">Find out more about QTIWorks</a>.
    </div>
  </div>
  <div class="clear"></div>

  <c:set var="publicDemosEnabled" value="${qtiWorksDeploymentSettings['publicDemosEnabled']}"/>
  <c:set var="gridSize" value="${publicDemosEnabled ? 3 : 6}"/>
  <div class="boxes">
    <c:if test="${publicDemosEnabled}">
      <div class="grid_${gridSize}">
        <div class="box">
          <a href="${utils:internalLink(pageContext, '/about/')}" class="boxButton about" title="About QTIWorks">
            <h3>About QTIWorks</h3>
            <div>Find out more about QTIWorks</div>
          </a>
        </div>
      </div>
      <div class="grid_${gridSize}">
        <div class="box">
          <a href="${utils:internalLink(pageContext, '/public/')}" class="boxButton demos" title="Demos">
            <h3>Demos</h3>
            <div>Try some demos of QTIWorks' functionality</div>
          </a>
        </div>
      </div>
    </c:if>
    <div class="grid_${gridSize}">
      <div class="box">
        <a href="${utils:internalLink(pageContext, '/signup/')}" class="boxButton signup" title="Request Access">
          <h3>Request Access</h3>
          <div>Find out how to request full access to QTIWorks</div>
        </a>
      </div>
    </div>
    <div class="grid_${gridSize}">
      <div class="box">
        <a href="${utils:internalLink(pageContext, '/instructor/')}" class="boxButton login" title="Log In">
          <h3>Log In</h3>
          <div>Sign into your QTIWorks account</div>
        </a>
      </div>
    </div>
  </div>

</page:page>
