<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Skeleton for an LTI instructor-role page

Core Model:

thisLtiIdentityContext
thisLtiUser
thisLtiResource
thisDelivery
thisDeliveryStatusReport
thisAssessment
thisAssessmentStatusReport
thisAssessmentPackage
theseDeliverySettings
primaryRouting (action -> URL)

--%>
<%@ tag body-content="scriptless" %>
<%@ taglib prefix="utils" uri="http://www.ph.ed.ac.uk/utils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="validator" tagdir="/WEB-INF/tags/validator" %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="cssClass" required="false" %>

<jsp:useBean id="now" class="java.util.Date"/>

<%-- Add additional data to model --%>
<c:set var="ltiContext" value="${thisLtiResource.ltiContext}" scope="request"/>
<c:set var="ltiDomain" value="${ltiContext.ltiDomain}" scope="request"/>
<c:set var="mayExit" value="${!empty(thisLtiIdentityContext.returnUrl)}" scope="request"/>

<%-- Extract config beans stashed in ServletContext during AppContext setup --%>
<c:set var="qtiWorksProperties" value="${applicationScope['qtiWorksProperties']}" scope="request"/>
<c:set var="qtiWorksDeploymentSettings" value="${applicationScope['qtiWorksDeploymentSettings']}" scope="request"/>
<c:set var="qtiWorksVersion" value="${qtiWorksProperties.qtiWorksVersion}" scope="request"/>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <title>QTIWorks Assessment Launch - <c:out value="${title}" default="QTIWorks"/></title>
    <link rel="stylesheet" href="//fonts.googleapis.com/css?family=Open+Sans:400,400italic,700,700italic|Ubuntu:500">
    <link rel="stylesheet" href="${utils:internalLink(pageContext, '/lib/960/reset.css')}">
    <link rel="stylesheet" href="${utils:internalLink(pageContext, '/lib/960/text.css')}">
    <link rel="stylesheet" href="${utils:internalLink(pageContext, '/lib/fluid960gs/grid.css')}">
    <link rel="stylesheet" href="${utils:internalLink(pageContext, '/includes/qtiworks.css')}?v=${qtiWorksVersion}">
    <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/themes/smoothness/jquery-ui.min.css">
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <%-- TODO: Move the next script into a single library --%>
    <script src="${utils:internalLink(pageContext, '/includes/qtiworks.js')}?v=${qtiWorksVersion}"></script>
    <!--[if lt IE 9]><script src="${utils:internalLink(pageContext, '/lib/html5shiv.min.js')}"></script><![endif]-->
  </head>
  <body class="<c:out value='${cssClass}' default='ltipage'/>">
    <c:if test="${mayExit}">
      <div class="ltiExit">
        <form action="${utils:escapeLink(primaryRouting['exit'])}" method="post" class="postLink">
          <input type="submit" value="Exit QTIWorks">
        </form>
      </div>
    </c:if>
    <div class="container_12">
      <header class="pageHeader">
        <h1>QTIWorks Assessment Launch</h1>
        <h2>
          <a href="${utils:escapeLink(primaryRouting['resourceDashboard'])}">
            <c:out value="${utils:formatLtiContextTitle(ltiContext)}"/>:
            <c:out value="${utils:formatLtiResourceTitle(thisLtiResource)}"/>
          </a>
        </h2>
      </header>
      <%-- Show warning if context isn't very good --%>
      <c:if test="${empty ltiContext.contextId}">
        <p class="ltiContextWarning">
          Your Tool Provider (e.g. Virtual Learning Environment) hasn't sent me
          very much information about the "context" (e.g. course) you are
          working in. Consequently, I'm unable to share any Assessments or
          Delivery Settings you create here with other launches you create
          within the same context.
        </p>
      </c:if>
      <%-- Maybe show flash message --%>
      <c:if test="${!empty flashMessage}">
        <div class="flashMessage">
          <c:out value="${flashMessage}"/>
        </div>
      </c:if>
      <jsp:doBody/>
      <div class="clear"></div>
      <footer>
        <div class="logos">
          <a href="http://www.jisc.ac.uk"><img src="${utils:internalLink(pageContext, '/includes/images/jisc75.png')}" width="75" height="50" alt="JISC Logo" /></a>
          <a href="http://www.ed.ac.uk"><img src="${utils:internalLink(pageContext, '/includes/images/uoe.png')}" width="60" height="60" alt="University of Edinburgh Logo" /></a>
        </div>
        <div class="copyright">
          <p>
            QTIWorks ${qtiWorksVersion} &#x2012; <a href="${utils:internalLink(pageContext, '/release-notes.jsp')}">Release notes</a>
          </p>
          <p>
            Copyright &#xa9; <fmt:formatDate value="${now}" type="date" pattern="yyyy"/>
            <a href="http://www.ph.ed.ac.uk">The School of Physics and Astronomy</a>,
            <a href="http://www.ed.ac.uk">The University of Edinburgh</a>.
          </p>
          <p>
            Contact: <a href="mailto:${qtiWorksDeploymentSettings.adminEmailAddress}"><c:out value="${qtiWorksDeploymentSettings.adminName}"/></a>
          </p>
          <p>
            The University of Edinburgh is a charitable body, registered in Scotland,
            with registration number SC005336.
          </p>
        </div>
      </footer>
    </div>
  </body>
</html>
