<%@ include file="/WEB-INF/jsp/includes/header.jspf" %>
<%--

Copyright (c) 2012, The University of Edinburgh.
All Rights Reserved

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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

