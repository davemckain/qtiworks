<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Release notes">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/about/')}">About QTIWorks</a> &#xbb;
  </nav>
  <h2>QTIWorks Release Notes (Development)</h2>

  <h3>1.0-DEV25 [Development] (28/02/2013)</h3>
  <p>
    This snapshot includes a large number of behind-the-scenes changes to make QTIWorks easier to install and manage.
    It also includes a number of minor fixes, but no real functional changes.
  </p>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-M3 [Production] (14/02/2013)</h3>
  <p>
    Third production milestone, equivalent to the 1.0-DEV24 development snapshot.
  </p>
  <p>
    See production releases at <a href="https://www2.ph.ed.ac.uk/qtiworks">https://www2.ph.ed.ac.uk/qtiworks</a>, and
    development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV24 [Development] (11/02/2013)</h3>
  <p>
    Minor update including mainly low-level code refactoring and documentation improvements.
    Visible changes are:
  </p>
  <ul>
    <li>Temporary removal of 32 character constraint check on identifiers (for Uniqurate questions)</li>
    <li>Fixed corner case in response processing with endAttemptInteractions if no responses had previously been bound</li>
    <li>Minor improvement to rendering of uploadInteraction</li>
  </ul>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV23 [Development] (29/01/2013)</h3>
  <p>
    Minor update that finally includes front-end functionality for deleting Assessments
    and Deliverables. (There is more back-end deletion functionality included too.)
  </p>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV22 [Development] (11/01/2013)</h3>
  <p>
    This snapshot continues with the implementation of the test specification, refining and slightly extending
    what was added in DEV21, as well as adding some improved test samples.
  </p>
  <p>
    We now support <code>testPart</code> feedback (albeit still only with single <code>testPart</code>s), the
    showing of item solutions and the display of section/rubric information within test item rendering.
  </p>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV21 [Development] (07/01/2013)</h3>
  <p>
    This snapshot continues with the implementation of the test specification. It now supports all 4 combinations
    of navigation and submission modes, though linear navigation is currently a bit rough and needs feedback.
    It also shows the <code>assessmentSection</code> structure and <code>rubric</code>s when presenting the
    navigation (in nonlinear mode); something similar will need done for linear navigation mode.
  </p>
  <p>
    This snapshot also changes the default values of <code>maxChoices</code> for <code>choiceInteraction</code>,
    <code>hotspotInteraction</code>, <code>hottextInteraction</code>, <code>positionObjectInteraction</code>
    and <code>selectPointInteraction</code>. The default is now 0 instead of 1, reflecting a poorly-advertised
    changed in the information model.
  </p>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-M2 [Production] (07/01/2013)</h3>
  <p>
    This second Milestone release is based on the 1.0-DEV20 development snapshot (see below),
    which was used to pilot some of the (partial) test implementation included in this snapshot.
  </p>
  <p>
    This milestone includes a partial implementation of the QTI <code>assessmentTest</code>,
    handling test containing one <code>testPart</code> using the NONLINEAR navigation mode and
    INDIVIDUAL submission mode. It does not yet support <code>branchRule</code> or <code>preCondition</code>,
    or similar advanced features. If you are interested in tests, please use the development snapshots for the
    time being. However, bear in mind that these will be subject to change at short notice so should not be used
    for "real" testing with students.
  </p>
  <p>
    See production releases at <a href="https://www2.ph.ed.ac.uk/qtiworks">https://www2.ph.ed.ac.uk/qtiworks</a>, and
    development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV20 [Development] (26/11/2012)</h3>
  <p>
    Minor update before Sue Milne's test pilot. This adds support for
    <code>printedVariable/@index</code>, as well as a change to
    <code>CandidateSessionStarter</code>'s logic. We now attempt to
    reconnect to an existing non-terminated session if available, rather
    than always starting a new one.
  </p>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV19 [Development] (17/11/2012)</h3>
  <p>
    Filled in initial sketch of support for <code>allowReview</code> and
    <code>showFeedback</code> in the test delivery. Fixed issue with mixed
    namepsaces when serializing <code>assessmentResult</code> XML. Added
    basic functionality for getting at candidate data (summary table, CSV summary,
    ZIP bundle containing all <code>assessmentResult</code> files).
  </p>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV18 [Development] (15/11/2012)</h3>
  <p>
    This snapshot tidies up implementation of tests added in DEV17, and adds in
    initial functionality within the webapp for viewing and downloading result
    data for candidate sessions on a given delivery.
  </p>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV17 [Development] (09/11/2012)</h3>
  <p>
    This snapshot continues with the implementation of tests.
    A first sketch of the full delivery of NONLINEAR/INDIVIDUAL tests is now in place,
    ready for discussion with project partners.
  </p>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV16 [Development] (02/11/2012)</h3>
  <p>
    This snapshot continues with the implementation of tests. The basic logic for handling
    tests with one NONLINEAR/INDIVIDUAL part are now in place, and much of the
    supporting data model is now ready. You can now upload and start one of
    these tests, but you'll just end up seeing a dump of the resulting test
    state (after template processing has run on each item).
  </p>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV15 [Development] (25/10/2012)</h3>
  <p>
    This snapshot includes a lot of the groundwork required for the test implementation,
    with significant refactoring to the session state and controller classes. It also includes
    another final QTI 2.1 schema.
  </p>
  <ul>
    <li>
      The <code>ValidationContext</code> callback API has been improved and many validators have been
      updated to use the new convenience methods in this.
    </li>
    <li>
      The <code>ProcessingContext</code> callback API now extends <code>ValidationContext</code> and
      is richer and easier to use.
    </li>
    <li>
      The requirement that items/tests be valid before running has been lifted. Instead, expression
      evaluation will validate each expression (if required) and return NULL and log an error if not valid.
    </li>
    <li>
      Resolution and evaluation of variable references has been completely rewritten. The <code>VariableReferenceIdentifier</code>
      class has been removed and JQTI+ now accepts identifiers with dots in them. The behaviour or how
      variable dereferencing works in the case of ambiguities has been clarified and documented.
    </li>
    <li>
      Added new <code>ItemProcessingMap</code> and <code>TestProcessingMap</code> helper classes to contain
      information about items/tests useful at runtime.
    </li>
    <li>
      Added new <code>TestPlan</code> class to represent the test structure as visible to a candidate once
      selection and ordering have been performed.
    </li>
    <li>
      All test-specific expressions and processing rules have been updated to use the new API.
      (Exceptions are <code>branchRule</code> and <code>preCondition</code>.)
    </li>
    <li>
      Fixed logic issues with <code>EqualRounded</code> and <code>Rounded</code>.
    </li>
    <li>
      Duration is now tracked during item delivery.
    </li>
    <li>
      Some of the front-end web MVC/CRUD has been updated to support tests as well as items. (Further work is
      needed to model test state and events...)
    </li>
  </ul>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV14 [Development] (28/09/2012)</h3>
  <p>
    This is the first development snapshot following the split into two instances.
    This snapshot does not contain any visible new features but includes a lot of changes
    and code refactoring to consolidate the work of the last few iterations and
    help prepare for the work on tests. Key changes are:
  </p>
  <ul>
    <li>
      This snapshot now includes the final (final?!) schema.
    </li>
    <li>
      The validation API has been signficantly refactored, merging with a newer more general
      "notification" API. This notification API can be used to report informational messages,
      warnings and errors at "runtime" (e.g. template, response or outcome processing),
      and will replace the currently inconsistent behaviour or either dying horribly or silently
      recovering.
    </li>
    <li>
      The processing of the MathAssess extensions has been updated to use the new notification API
      and make more sensible decision about which variables should (or not) be set during processing.
      (The MathAssess spec could do with being updated now...)
    </li>
    <li>
      The handling of <code>integerOrVariableRef</code> and friends in the many corner cases not discussed by
      the QTI spec has been refined (using the new notification API) and documented (in the wiki).
    </li>
    <li>
      The <code>Value</code> hierarchy for container values has been refactored and simplified. These values
      are now immutable, and factory constructors now return explicit the <code>NullValue</code> in place of
      empty containers, which should make life easier for using the JQTI+ API.
    </li>
    <li>
      Added a <code>Signature</code> concept, which combines <code>BaseType</code> and <code>Cardinality</code>
      and makes code using this easier to read. Methods have been added to the
      validation API to use this, which should be used in all new code in favour of the old cumbersome checks.
    </li>
  </ul>
  <p>
    See development snapshots at <a href="https://www2.ph.ed.ac.uk/qtiworks-dev">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-M1 [Production] (27/09/2012)</h3>
  <p>
    This "Milestone 1" snapshot is the first of a set of stable, less frequent snapshots
    so that people using QTIWorks for "real" stuff don't have to worry too much about things
    suddenly changing. Functionally, this is the same as 1.0-DEV13 but includes a few improvements
    you won't notice.
  </p>
  <p>
    The next milestone snapshot will be released once we have some of the test functionality implemented.
    You can always see the latest production snapshot at <a href="https://www2.ph.ed.ac.uk/qtiworks">https://www2.ph.ed.ac.uk/qtiworks</a>.
    For bleeding edge snapshots, please see the new DEV instance of QTIWorks at
    <a href="https://www2.ph.ed.ac.uk/qtiworks-dev/">https://www2.ph.ed.ac.uk/qtiworks-dev</a>.
  </p>

  <h3>1.0-DEV13 (04/09/2012)</h3>
  <p>
    This snapshot finally adds in support for the <code>integerOrVariableRef</code>,
    <code>floatOrVariableRef</code> and <code>stringOrVariableRef</code> types.
    Most expressions that use these have been updated, though the behaviour when things
    veer off the "happy path" is still not consistent and will require a bit more refactoring.
  </p>
  <p>
    This snapshot also includes the latest (final?) version of the QTI 2.1 schema.
    However, it does not support the new namespace for <code>assessmentResult</code> (and its
    descendant elements), so results will still be reported in the original (and now wrong)
    namespace. This will require a bit of refactoring to change.
  </p>

  <h3>1.0-DEV12 (15/08/2012)</h3>

  <p>
    This snapshot fills in more of the Instructor functionality, such as the
    management of "deliveries" of an assessment. It also includes a first cut of the LTI
    launch for assessments, as well as a number of less noticeable improvements.
  </p>

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
