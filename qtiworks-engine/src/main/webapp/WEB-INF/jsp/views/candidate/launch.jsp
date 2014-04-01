<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Custom JSP for assessment entry/re-entry

Model attributes:

sessionEntryPath

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>

<%-- Extract config beans stashed in ServletContext during AppContext setup --%>
<c:set var="qtiWorksProperties" value="${applicationScope['qtiWorksProperties']}"/>
<c:set var="qtiWorksDeploymentSettings" value="${applicationScope['qtiWorksDeploymentSettings']}"/>
<c:set var="qtiWorksVersion" value="${qtiWorksProperties.qtiWorksVersion}"/>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <title>QTIWorks Assessment Launcher</title>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <link rel="stylesheet" href="${utils:internalLink(pageContext, '/rendering/css/assessment.css')}?${qtiWorksVersion}">
    <script>
$(window).ready(function() {
  var launchBox = $('#launchBox');
  var launcher = $('#launcher');

  // Automatically trigger assessment entry via form submission
  setTimeout(function() {
    // Make launch information fade in so that it's not visually jarring for re-entries
    setTimeout(function() {
      launchBox.fadeIn({
        duration: 500,
        start: function() {
          //$('#progressBar').css('background-image', 'url(/qtiworks/rendering/images/animated-overlay.gif)');
          //$('#progressBar').css('background-color', 'red');
        }
      });
    }, 200);
    launcher.trigger('submit');
  }, 50);
});

// The next line should ensure ready event gets called again if we browse back here
$(window).bind('unload', function() {} );
    </script>
  </head>
  <body>
    <div id="launchBox">
      <h1>Launching your assessment...</h1>
      <div id="progressBar"></div>
      <form id="launcher" method="post" action="${utils:internalLink(pageContext, sessionEntryPath)}">
        <input id="launchButton" type="submit" value="Click here and wait if the assessment does not start in a few seconds.">
      </form>
    </div>
  </body>
</html>
