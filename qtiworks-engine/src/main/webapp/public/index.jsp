<%@ page trimDirectiveWhitespaces="true"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Top page for public functionality

--%>
<page:page title="Public QTI Samples">

  <h2>Demos</h2>

  <div class="hints">
    Try some demos of QTIWorks to see some of the things it can do.
    You'll be able to do all of this (and much more) if you log in!
  </div>

  <div class="boxes">
    <div class="grid_4">
      <div class="box">
        <a href="${utils:internalLink(pageContext, '/web/anonymous/standalonerunner')}" class="boxButton run" title="Run">
          <h3>Quick Upload &amp; Run</h3>
          <div>Upload and try your own QTI 2.1 assessment items and tests</div>
        </a>
      </div>
    </div>

    <div class="grid_4">
      <div class="box">
        <a href="${utils:internalLink(pageContext, '/web/anonymous/samples/list')}" class="boxButton samples" title="Samples">
          <h3>QTI Examples</h3>
          <div>Browse and try out our selection of QTI 2.1 examples</div>
        </a>
      </div>
    </div>

    <div class="grid_4">
      <div class="box">
        <a href="${utils:internalLink(pageContext, '/web/anonymous/validator')}" class="boxButton validator" title="Validator">
          <h3>QTI Validator</h3>
          <div>Verify your QTI assessment items and tests</div>
        </a>
      </div>
    </div>
  </div>

</page:page>
