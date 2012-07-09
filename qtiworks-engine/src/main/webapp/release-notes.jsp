<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Release notes">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/about/')}">About QTIWorks</a> &#xbb;
  </nav>
  <h2>QTIWorks Release Notes</h2>

  <h3>1.0-DEV11 (09/07/2012)</h3>

  <p>
    This snapshot fixes a number of minor bugs found after the release of 1.0-DEV10, including
    a couple of regressions in the display of validation results. The item rendering now handles
    overridden correct response values correctly, and the "show solution" button is only shown if
    there is something to show, which should cut down the number of delivery settings that people
    need to manage.
  </p>

  <h3>1.0-DEV10 (07/07/2012)</h3>

  <p>
    This consolidates the work in the last snapshot by making it look a bit nicer and easier to use.
    More details can be found in the accompanying
    <a href="http://qtisupport.blogspot.co.uk/2012/07/qtiworks-snapshot-10-has-been-released.html">blog post about this release</a>.
  </p>

  <h3>1.0-DEV9 (03/07/2012)</h3>

  <p>
    This snapshot adds a standalone "upload and run" feature that can be run without requiring a login,
    allowing candidates to choose from a number of pre-defined "delivery settings". (This is similar but
    more flexible than existing functionality in MathAssessEngine.)
  </p>
  <p>
    This snapshot also adds a "run" feature for logged in users, as well as filling in more functionality
    for logged-in users. It's just about usable for storing, debugging and trying out your own assessment
    items now.
  </p>
  <p>
    The next snapshot will consolidate on this work and improve the user experience somewhat...
  </p>

  <h3>1.0-DEV8 (31/05/2012)</h3>

  <p>
    This adds further enhancements to the rendering process for single items.
    I have written a
    <a href="http://qtisupport.blogspot.co.uk/2012/05/enhanced-item-rendering-in-qtiworks.html">blog post for this release</a>,
    so it's probably more useful to
    link to it than try to paraphrase it badly here.
    </p>

  <h3>1.0-DEV7 (25/05/2012)</h3>

  <p>
    This snapshot finally includes all of the internal logic for successfully
    delivering - and recording the delivery of - a single asseesment item to a
    candidate, as well as much of the logic for managing assessments within the
    system. However, not much of this is yet visible to end users, apart from a
    revised version of the "play sample items" functionality that uses the new
    implementation. There's a little bit of the assessment management functionality
    visible, as well as the login form that people will use to access this, but
    not enough to be truly usable yet.
  </p>

  <h3>1.0-DEV6 (02/05/2012)</h3>

  <p>
    The only real visible change in this snapshot is the addition of
    Graham Smith's sample questions on languages. However, this release
    includes some of the new foundations and pipework that will support
    the webapp as development continues, including a first cut of the DB
    schema, some of the service layer, and some of the authentication modules.
    Note also that from now on, the public demo of QTIWorks will be accessed via
    HTTPS instead of HTTP. (Users will be automatically switched to the correct
    protocol if they come in via HTTP.)
  </p>

  <h3>1.0-DEV5 (25/04/2012)</h3>

  <p>
    Further tweaks to logic determining whether submit button should
    appear in item rendering, in light of discussion with Sue.
    Removed automated feedback styling added in DEV4 as it can't
    determine between real feedback and selective content.
    I've also hidden the RESET button to see if anyone misses it...
  </p>

  <h3>1.0-DEV4 (24/04/2012)</h3>

  <p>
    Fixed bug introduced when refactoring endAttemptInteraction,
    which prevented it from working correctly. Also added some experimental
    stlying on feedback elements, which will need further work.
    Further significant refactoring work has been done on JQTI+, in particular
    the API for extensions (customOperator/customInteraction). I have also
    started laying the ORM pipework for the webapp domain model.
  </p>


  <h3>1.0-DEV3 (11/04/2012)</h3>

  <p>
    This snapshot adds in the MathAssess examples, as well as the
    "get source" and "get item result" functions in the item delivery.
    Significant further refactorings and improvements have also 
    been made within JQTI+.
  </p>

  <h3>1.0-DEV2 (23/03/2012)</h3>

  <p>
    Demonstrates the newly-refactored assessmentItem state &amp; logic
    code in JQTI+, joining it back in with the rendering components
    from MathAssessEngine-dev. This demo only lets you play around with
    some pre-loaded sample items as I haven't started work on the CRUD
    API for getting your items into the system.
  </p>

  <h3>1.0-DEV1 (26/01/2012)</h3>
  <p>
    Demonstrates the newly-refactored validation functionality in JQTI+,
    with more general JQTI -&gt; JQTI+ refactoring work continuing apace.
  </p>

</page:page>
