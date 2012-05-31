<%--

This fragment formats the results of validating an AssessmentObject

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
<c:set var="qtiWorksVersion" value="1.0-DEV8"/>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <meta name="author" content="David McKain">
    <meta name="publisher" content="The University of Edinburgh">
    <title>QTIWorks - <c:out value="${title}" default="QTIWorks"/></title>
    <link rel="stylesheet" type="text/css" href="${utils:internalLink(pageContext, '/includes/webapp-base.css')}">
    <link rel="stylesheet" type="text/css" href="${utils:internalLink(pageContext, '/includes/content-styles.css')}">
    <link rel="stylesheet" type="text/css" href="${utils:internalLink(pageContext, '/includes/qtiworks.css')}">
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"></script>
    <script type="text/javascript" src="${utils:internalLink(pageContext, '/includes/validation-toggler.js')}"></script>
    <link rel="stylesheet" type="text/css" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/smoothness/jquery-ui.css"/>
  </head>
  <body>
    <div id="container">
      <div id="header">
        <div id="identities">
          <div id="edlogo"><a href="http://www.ed.ac.uk"><img src="${utils:internalLink(pageContext, '/includes/images/ed_logo.gif')}" alt="University of Edinburgh Logo"/></a></div>
          <div id="edname"><a href="http://www.ed.ac.uk"><img src="${utils:internalLink(pageContext, '/includes/images/uofe2.gif')}" alt="University of Edinburgh"></a></div>
          <div id="schoolname"><a href="/"><img src="${utils:internalLink(pageContext, '/includes/images/panda.gif')}" alt="School of Physics &amp; Astronomy"></a></div>
        </div>
        <div id="GlobalNav">
          <ul>
            <li class="active"><a href="http://www2.ph.ed.ac.uk/elearning/" class="active"><span>e-Learning</span></a></li>
          </ul>
        </div>
        <div id="utility">
          <a class="utilContact" href="http://www2.ph.ed.ac.uk/elearning/contacts/#dmckain">Contact us</a>
        </div>
      </div>
      <div id="sectionHeader">
        <div id="sectionHeaderTitle">
          <a href="${utils:internalLink(pageContext, '/')}">QTIWorks ${qtiWorksVersion}</a>
        </div>
      </div>
      <div id="contentArea">
        <%-- Main content goes in this container --%>
        <div id="content" class="content">

          <jsp:doBody/>

        </div>
      </div>
    </div>
    <div id="footer">
      <div id="copyright">
        <div id="footer-text">
          <%-- Usual Copyright stuff --%>
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
        <div class="clearFloat"></div>
      </div>
    </div>
  </body>
</html>
