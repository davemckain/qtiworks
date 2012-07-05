<%--

Home page skeleton

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ tag body-content="scriptless" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ attribute name="title" required="false" %>

<jsp:useBean id="now" class="java.util.Date"/>
<c:set var="qtiWorksVersion" value="1.0-DEV9"/>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <title>QTIWorks</title>
    <%-- FIXME: Need HTTPS versions of the following 3 libs, or pull them in --%>
    <link rel="stylesheet" href="http://cachedcommons.org/cache/960/0.0.0/stylesheets/reset.css">
    <link rel="stylesheet" href="http://cachedcommons.org/cache/960/0.0.0/stylesheets/text.css">
    <link rel="stylesheet" href="http://cachedcommons.org/cache/960/0.0.0/stylesheets/960.css">
    <link rel="stylesheet" href="//fonts.googleapis.com/css?family=Open+Sans:400,400italic,700|Ubuntu:500">
    <link rel="stylesheet" href="${utils:internalLink(pageContext, '/includes/qtiworks.css')}?${qtiWorksVersion}">
    <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/smoothness/jquery-ui.css">
    <%-- Next one is not used yet
    <script src="//cdnjs.cloudflare.com/ajax/libs/modernizr/2.5.3/modernizr.min.js"></script>
    --%>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"></script>
    <%-- TODO: Move the next script into a single library --%>
    <script src="${utils:internalLink(pageContext, '/includes/validation-toggler.js')}?${qtiWorksVersion}"></script>
  </head>
  <body class="homepage">
    <div id="contentArea" class="container_12">
      <header>
        <h1>QTIWorks</h1>
      </header>
      <jsp:doBody/>
      <div class="clear"></div>
      <footer>
        <div class="grid_4">
          <ul>
            <li><a href="http://www.jisc.ac.uk/whatwedo/programmes/elearning/assessmentandfeedback/qtidi.aspx">QTI Delivery Integration (QTIDI)</a></li>
            <li><a href="https://github.com/davemckain/qtiworks">Source code on github</a></li>
            <li><a href="http://qtisupport.blogspot.co.uk">QTI Support Blog</a></li>
            <li><a href="http://www.imsglobal.org/question/">IMS Question &amp; Test Interoperability Specification</a></li>
          </ul>
        </div>
        <div class="grid_4">
          <div class="logos">
            <a href="http://www.jisc.ac.uk"><img src="${utils:internalLink(pageContext, '/includes/images/jisc75.png')}" width="75" height="50" alt="JISC Logo" /></a>
            <a href="http://www.ed.ac.uk"><img src="${utils:internalLink(pageContext, '/includes/images/uoe.png')}" width="60" height="60" alt="University of Edinburgh Logo" /></a>
          </div>
          <div class="copyright">
            <p>
              QTIWorks ${qtiWorksVersion} &#x2012; <a href="${utils:internalLink(pageContext, '/release-notes.jsp')}">Release notes</a>
              <br />
              Copyright &#xa9; <fmt:formatDate value="${now}" type="date" pattern="yyyy"/>
              <a href="http://www.ph.ed.ac.uk">The School of Physics and Astronomy</a>,
              <a href="http://www.ed.ac.uk">The University of Edinburgh</a>.
              <br />
              For more information, contact
              <a href="http://www.ph.ed.ac.uk/elearning/contacts/#dmckain">David McKain</a>.
            </p>
            <p>
              The University of Edinburgh is a charitable body, registered in Scotland,
              with registration number SC005336.
            </p>
          </div>
        </div>
      </footer>
    </div>
  </body>
</html>

