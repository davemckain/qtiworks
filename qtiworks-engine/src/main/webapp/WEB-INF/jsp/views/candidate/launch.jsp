<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Custom JSP for assessment entry/re-entry

Model attributes:

sessionEntryPath

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <title>QTIWorks Assessment Launcher</title>
    <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/smoothness/jquery-ui.css">
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
    <script>
$(window).ready(function() {
  // Set up scrolling animation
  $("#progress").progressbar({
    value: false
  });
  var launchBox = $('#launchBox');
  var launcher = $('#launcher');

  // Make launch information fade in so that it's not visually jarring for re-entries
  setTimeout(function() { launchBox.fadeIn({ duration: 500 }); }, 500);

  // Automatically trigger assessment entry via form submission
  setTimeout(function() { launcher.trigger('submit') }, 50);
});

// The next line should ensure ready event gets called again if we browse back here
$(window).bind('unload', function() {} );
    </script>
    <style type="text/css">
body {
  font-size: smaller;
}

h1 {
  font-size: 1.5em;
}

#launchBox {
  width: 50em;
  margin: 5em auto;
  text-align: center;
  display: none; /* Will be made visible by JS */
}

#progress {
  border-radius: 1em;
  margin: 1.5em 0;
}

#launchButton {
  background: none;
  border: none;
}

#launchButton:hover {
  text-decoration: underline;
}
    </style>
  </head>
  <body>
    <div id="launchBox">
      <h1>Launching your assessment...</h1>
      <div id="progress"></div>
      <form id="launcher" method="post" action="${utils:internalLink(pageContext, sessionEntryPath)}">
        <input id="launchButton" type="submit" value="Click here and wait if the assessment does not start in a few seconds">
      </form>
    </div>
  </body>
</html>
