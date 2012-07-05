<%@ page trimDirectiveWhitespaces="true"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

Top page for public functionality

--%>
<page:page title="Public QTI Samples">

  <div class="grid_12">
    <h2>Demos</h2>

    <div class="hints">
      Try some demos of QTIWorks to see some of the things it can do.
      You'll be able to do all of this (and much more) if you log in!
    </div>
  </div>
  <div class="clear"></div>

  <div class="boxes">
    <div class="grid_4">
      <div class="box">
        <h3>QTI Quick Run</h3>
        <a href="${utils:internalLink(pageContext, '/web/public/standalonerunner')}" class="iconButton" title="Run">&#x25b6;</a>
        <p>
          Upload and run your own QTI 2.1 assessment items.
        </p>
      </div>
    </div>

    <div class="grid_4">
      <div class="box">
        <h3>QTI Examples</h3>
        <a href="${utils:internalLink(pageContext, '/web/public/samples/list')}" class="iconButton" title="Samples">&#x2708;</a>
        <p>
          Browser and run our selection of QTI 2.1 examples.
        </p>
      </div>
    </div>

    <div class="grid_4">
      <div class="box">
        <h3>QTI Validator</h3>
        <a href="${utils:internalLink(pageContext, '/web/public/validator')}" class="iconButton" title="Validator">&#x2714;</a>
        <p>
          Verify your QTI assessment items and tests.
        </p>
      </div>
    </div>
  </div>

</page:page>
