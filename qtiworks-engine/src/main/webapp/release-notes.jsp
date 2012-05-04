<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>
<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<h2>1.0-DEV6 (02/05/2012)</h2>

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

<h2>1.0-DEV5 (25/04/2012)</h2>

<p>
  Further tweaks to logic determining whether submit button should
  appear in item rendering, in light of discussion with Sue.
  Removed automated feedback styling added in DEV4 as it can't
  determine between real feedback and selective content.
  I've also hidden the RESET button to see if anyone misses it...
</p>

<h2>1.0-DEV4 (24/04/2012)</h2>

<p>
  Fixed bug introduced when refactoring endAttemptInteraction,
  which prevented it from working correctly. Also added some experimental
  stlying on feedback elements, which will need further work.
  Further significant refactoring work has been done on JQTI+, in particular
  the API for extensions (customOperator/customInteraction). I have also
  started laying the ORM pipework for the webapp domain model.
</p>


<h2>1.0-DEV3 (11/04/2012)</h2>

<p>
  This snapshot adds in the MathAssess examples, as well as the
  "get source" and "get item result" functions in the item delivery.
  Significant further refactorings and improvements have also 
  been made within JQTI+.
</p>

<h2>1.0-DEV2 (23/03/2012)</h2>

<p>
  Demonstrates the newly-refactored assessmentItem state &amp; logic
  code in JQTI+, joining it back in with the rendering components
  from MathAssessEngine-dev. This demo only lets you play around with
  some pre-loaded sample items as I haven't started work on the CRUD
  API for getting your items into the system.
</p>

<h2>1.0-DEV1 (26/01/2012)</h2>
<p>
  Demonstrates the newly-refactored validation functionality in JQTI+,
  with more general JQTI -&gt; JQTI+ refactoring work continuing apace.
</p>

<%@ include file="/WEB-INF/jsp/includes/footer.jspf" %>

