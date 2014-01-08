<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

Session launch

Model attributes:

sessionEntryPath

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Assessment Launch">

<div id="launchBox">
  <h1>Launching assessment...</h1>
  <div id="progress"></div>
  <form id="launcher" method="post" action="${utils:internalLink(pageContext, sessionEntryPath)}">
    <input type="submit" value="Return">
  </form>
</div>
<script>
  $(window).ready(function() {
    $("#progress").progressbar({
      value: false
    });
    var launcher = $('#launcher');
    setTimeout(function() { launcher.trigger('submit') }, 50);
  });
  $(window).bind('unload', function() {} );
</script>

</page:page>
