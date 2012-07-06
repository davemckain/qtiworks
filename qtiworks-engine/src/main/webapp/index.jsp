<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page cssClass="homepage" title="x">

  <div class="grid_12">
    <div class="intro">
      QTIWorks is a new open-source tool for managing and delivering Question &amp; Test
      Interoperability (QTI) v2.1 assessment items and tests.
      <a href="${utils:internalLink(pageContext, '/about/')}">Find out more about QTIWorks</a>.
    </div>
  </div>
  <div class="clear"></div>

  <div class="boxes">
    <div class="grid_3">
      <div class="box">
        <a href="${utils:internalLink(pageContext, '/about/')}" class="boxButton about" title="About QTIWorks">
          <h3>About QTIWorks</h3>
          <div>Find out more about QTIWorks</div>
        </a>
      </div>
    </div>
    <div class="grid_3">
      <div class="box">
        <a href="${utils:internalLink(pageContext, '/public/')}" class="boxButton demos" title="Demos">
          <h3>Demos</h3>
          <div>Try some demos of QTIWorks' functionality</div>
        </a>
      </div>
    </div>
    <div class="grid_3">
      <div class="box">
        <a href="${utils:internalLink(pageContext, '/web/public/validator')}" class="boxButton signup" title="Sign Up">
          <h3>Sign Up</h3>
          <div>Sign up for a free QTIWorks account</div>
        </a>
      </div>
    </div>
    <div class="grid_3">
      <div class="box">
        <a href="${utils:internalLink(pageContext, '/instructor/')}" class="boxButton login" title="Log In">
          <h3>Log In</h3>
          <div>Sign into your QTIWorks account</div>
        </a>
      </div>
    </div>
  </div>

</page:page>
