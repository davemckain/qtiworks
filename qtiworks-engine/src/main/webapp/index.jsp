<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:homepage>

  <div class="intro">
    QTIWorks is a new open-source tool for delivering Question &amp; Test
    Interoperability (QTI) v2.1 assessment items and tests. It is being
    developed as part of the JISC QTIDI project as a next-generation
    advancement of the existing QTIEngine and MathAssessEngine tools.  It will
    support almost all of the QTI 2.1 specification, as well as the MathAssess
    QTI extensions. An LTI connector will allow you to use QTIWorks to deliver
    real assessments within popular learning systems such as BlackBoard and
    Moodle.
  </div>

  <div class="boxes">
    <div class="grid_3">
      <div class="box">
        <h3>About QTIWorks</h3>
        <a href="${utils:internalLink(pageContext, '/about/')}" class="iconButton" title="About QTIWorks">&#x2620;</a>
        <p>
          Find out more about QTIWorks
        </p>
      </div>
    </div>
    <div class="grid_3">
      <div class="box">
        <h3>Demos</h3>
        <a href="${utils:internalLink(pageContext, '/public/')}" class="iconButton" title="Demos">&#x261b;</a>
        <p>
          Try some demos of QTIWorks' functionality
        </p>
      </div>
    </div>
    <div class="grid_3">
      <div class="box">
        <h3>Sign Up</h3>
        <a href="${utils:internalLink(pageContext, '/web/public/validator')}" class="iconButton" title="Sign Up">&#x2709;</a>
        <p>
          Sign up for a free QTIWorks account
        </p>
      </div>
    </div>
    <div class="grid_3">
      <div class="box">
        <h3>Log In</h3>
        <a href="${utils:internalLink(pageContext, '/instructor/')}" class="iconButton" title="Log In">&#x2710;</a>
        <p>
          Sign into your QTIWorks account
        </p>
      </div>
    </div>
  </div>

</page:homepage>
