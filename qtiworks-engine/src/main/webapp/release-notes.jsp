<%--

Copyright (c) 2012-2013, The University of Edinburgh.
All Rights Reserved

--%>
<%@ include file="/WEB-INF/jsp/includes/pageheader.jspf" %>
<page:page title="Release notes">

  <nav class="breadcrumbs">
    <a href="${utils:internalLink(pageContext, '/about/')}">About QTIWorks</a> &#xbb;
  </nav>
  <h2>QTIWorks Release Notes</h2>

  <h3>Release 1.0-beta9 (05/01/2016)</h3>
  <p>
    This release rolls up some minor improvements and fixes made during 2015.
    There are no new features or functionality included here.
  </p>
  <ul>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/57">#57</a>:
      Added guard to prevent blow-ups when the <code>@index</code> attribute of
      a <code>printedVariable</code> is a variable reference.
      (This missing functionality is still be to be implemented.)
    </li>
    <li>
      Simplified some of the internal candidate session access management.
    </li>
    <li>
      Minor change to validation result objects to make it easier to extract
      information from them, and some improvements to the validation examples.
      (Note that this introduces a minor API change to the validation classes.
      Please refer to the examples if you are affected by this.)
    </li>
    <li>
      The QTIWorks engine exception logger now no longer logs certain exceptions
      caused by clients, such as bad HTTP methods. Logging these is usually not helpful.
    </li>
    <li>
      QTIWorks engine library dependencies have been refreshed to current versions.
    </li>
  </ul>

  <h3>Release 1.0-beta8 (21/01/2015)</h3>
  <p>
    This release incorporates minor bug fixes and some minor tidying.
  </p>
  <h4>Issues resolved</h4>
  <ul>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/46">#46</a>:
      Fixed handling
      of <code>&lt;param/&gt;</code> in rendering.
    </li>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/47">#47</a>:
      Fixed regression in simple RESTish API for launching assessments in demo mode.
    </li>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/52">#52</a>:
      Updated MathJax HTTPS URL
    </li>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/55">#55</a>:
      Fixed <code>label</code> attribute in <code>BodyElement</code>.
    </li>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/55">#58</a>:
      Fixes and improvements to <code>stringInteractions</code> when bound
      to record cardinality variables.
    </li>
  </ul>
  <h4>Other notable changes</h4>
  <ul>
    <li>
      If QTIWorks encounters an internal error while rendering an assessment, the
      candidate session will be marked as 'exploded' and a less scary error page will
      be shown.
    </li>
    <li>
      Validation for <code>responseDeclaration</code> and <code>outcomeDeclaration</code>
      now checks baseType and cardinality. (There was a missing superclass class here.)
    </li>
  </ul>

  <h3>Release 1.0-beta7 (12/05/2014)</h3>
  <p>
    Security release. This fixes potential cross-site scripting (XSS) vulnerabilities
    caused by a failure to escape user input in some instructor-facing JSP
    pages, including the system user login page.
  </p>
  <p>
    The <a href="https://webapps.ph.ed.ac.uk/qtiworks">public demo of QTIWorks</a> has been
    upgraded in conjunction with this release.  All people running their own
    QTIWorks installations are strongly encouraged to upgrade as soon as
    possible.
  </p>
  <h4>Issues resolved</h4>
  <ul>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/45">#45</a>: XSS vulnerabilities
      in some instructor interface pages.
    </li>
  </ul>

  <h3>Release 1.0-beta6 (15/04/2014)</h3>
  <p>
    This is hoped to be the final beta before a RC or final 1.0.0 release.
  </p>
  <h4>Migration notes</h4>
  <p>
    If you have an existing QTIWorks installation then note that a database
    schema change will be required. Please run
    <code>qtiworks-engine/support/schema-migrations/beta5-to-beta6.sql</code>
    to upgrade your database if you have been running your own QTIWorks
    1.0-beta5 installation. Please follow instructions in earlier release notes
    to upgrade incrementally from earlier beta releases.
  </p>
  <p>
    There are some additions to <code>default.qtiworks-deployment.properties</code>.
    Please merge into your <code>qtiworks-deployment.properties</code> as required.
  </p>
  <h4>Issues resolved</h4>
  <ul>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/44">#44</a>:
      Changes have been made to DB column naming to avoid the superset of
      reserved words used by most command databases. (Thanks for Kevin Curley for
      noting this problem while trying out QTIWorks on Microsoft SQL Server.)
    </li>
    <li>
      Fix to (incomplete) implementation if <code>timeLimit</code> class in JQTI+. This
      now has the correct name, and recognises the <code>allowLateSubmission</code> attribute.
    </li>
    <li>
      The animated progress bar shown when sessions take a few moments to launch now animate
      correctly on Chrome.
    </li>
    <li>
      A missing exit page has been added for the (legacy) Uniqurate controller.
    </li>
    <li>
      Added the HTML5 shim to the instructor interfaces for older versions of Internet Explorer.
    </li>
  </ul>

  <h4>New features and changes</h4>
  <ul>
    <li>
      The public demo and (sketchy) REST functions provided by the QTIWorks
      Engine are now optional and disabled by default. They can be easily
      enabled via your <code>qtiworks-deployment.properties</code> if you want
      to use these features.
    </li>
    <li>
      There have been several internal change to how candidate sessions are
      launched and authenticated. Access to a candidate session is now linked
      to the underlying HTTP session, making it more difficult for sessions to
      be hijacked. (Cookies are therefore required now.) The random token added
      to candidate session URLs is now specific to an individual launch of a
      session.
    </li>
    <li>
      Test candidate sessions are now marked as finished once the final item in
      the final testPart has been ended. This means results should be available
      (and returned to the LTI TC) as soon as they have been finalised.
    </li>
    <li>
      Maven plugins and dependencies have been updated to the latest versions.
    </li>
    <li>
      The concept of 'public' Assessments and Delivery Settings have been
      dropped, at least for now. This idea was never really fleshed out.
    </li>
    <li>
      The QTIWorks Engine Manager actions have been tidied up and rationalised,
      with better inline documentation about what they do.
    </li>
    <li>
      There are some minor improvements to the proctoring and reporting
      interfaces, separating the reporting functions from the more destructing
      proctoring functions.
    </li>
  </ul>

  <h3>Release 1.0-beta5 (20/01/2014)</h3>
  <p>
    Further bug fixes and minor feature enhancements, some done to support the pilot use of QTIWorks
    for delivering a diagnostic test at the University of Edinburgh.
  </p>
  <h4>Issues resolved</h4>
  <ul>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/42">#42</a>: Duration timers are now
      updated before test outcome processing runs.
    </li>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/39">#39</a>: LTI candidate users created
      for domain-level launches are now deleted when such users are no longer associated with any
      candidate sessions.
    </li>
    <li>
      Fix to ResolvedAssessmentTest to make it possible to perform lookups on the built-in duration
      variable. (NB: Lookups of duration restricted to testPart or
      assessmentSection level are still not implemented.)
    </li>
    <li>
      Some fixes and changes to rendering: XSLT now processes rubricBlocks in testPart navigation correctly,
      and some fixes to validity of resulting HTML. CSS now renders things a bit smaller.
    </li>
    <li>
      Refactoring of HTTP cache control in rendering MVC.
    </li>
    <li>
      Tidy-up of client exceptions in service layer.
    </li>
    <li>
      Improvements to LTI domain authentication.
    </li>
  </ul>
  <h4>New features</h4>
  <ul>
    <li>
      Minor enhancements to proctoring functionality: candidate activity log, terminate individual session, delete session.
    </li>
    <li>
      New facade API (<code>SimpleJqtiFacade</code>) for JQTI+, making it a bit easier to perform basic functions.
    </li>
    <li>
      Implemented exit button for the LTI instructor web interface.
    </li>
  </ul>

  <h3>Release 1.0-beta4 (09/01/2014)</h3>
  <p>
    Bug-fix release addressing problems reported since beta3, as well as including a few
    minor features and tweaks.
  </p>
  <h4>Migration notes</h4>
  <p>
    A small database schema change is required. Please run <code>qtiworks-engine/support/schema-migrations/beta3-to-beta4.sql</code>
    to upgrade your database if you have been running your own QTIWorks 1.0-beta3 installation.
  </p>
  <h4>Issues resolved</h4>
  <ul>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/37">#37</a>: Fixed encoding of candidate
      responses, which previously resulted in non-ASCII characters being mangled.
    </li>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/38">#38</a>: Further investigation
      of LTI result return on Moodle has been performed with assistance from Glasgow University.
      Result return now works for them for <em>domain</em> launches, but not <em>link</em> launches.
      It is currently believe that the issues with the latter are problems with Moodle, rather than
      QTIWorks. Some refactoring of the LTI code within QTIWorks was done to assist with the investigation,
      including a few improvements to the result return's HTTP headers.
    </li>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/41">#41</a>: Empty
      <code>StringValue</code>s now correctly compare with NULL. Fixing this bug
      identified some issues with the handling of whitespace when building the JQTI
      object model (typically from XML), which have been dealt with in an acceptable
      fashion.
    </li>
    <li>
      Improved handling of responseProcessing templates that fail to load at
      runtime. These are now logged as runtime warnings.
    </li>
    <li>
      Fixes to the logic assembling the outcome variable information that gets
      displayed in the candidate session proctoring page.
    </li>
    <li>
      Some minor fixes to routing/linking in the instructor web interface.
    </li>
  </ul>
  <h4>New features</h4>
  <ul>
    <li>
      Improvement to the visual workflow when launching a candidate session. A
      new entry page includes a splash message if the session takes a few
      moments to start up, and this page also
      attempts to push the candidate back into the session if they use their browser's back button.
      (Some further testing is required for this - it works everywhere I've tried so far except for
      Apple iDevices.)
    </li>
    <li>
      Added stricter checking of OAuth timestamps and nonces when invoking LTI
      links. (This requires a new database table, hence a schema update. See
      below for details.)
    </li>
    <li>
      Deletion of deliveries now sensibly deletes all information about any candidates running the delivery via an LTI link.
    </li>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/40">#40</a>: Added code to seek out and
      clean up any application-level ThreadLocals when the webapp is shut down. This doesn't yet fix issue #40, but is a good start.
    </li>
    <li>
      Page layout for (the non-candidate part of) the QTIWorks engine webapp
      now uses a fluid grid instead of a fixed grid. This should make it
      integrate nicer visually with LTI Tool Consumers. (For example,
      Blackboard Learn's iFrame option for invoking LTI launches.)
    </li>
  </ul>
  <h3>Release 1.0-beta3 (10/10/2013)</h3>
  <p>
    Bug-fix release addressing problems reported since beta2. Issues fixed:
  </p>
  <ul>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/30">#30</a>: Due
      to a brain-related issue in my original implementation of LTI result
      returning, errors were being generated when running single item
      assessments with LTI result returning enabled if the item session was
      able to close, reopen and then close again. This issue is now fixed.
    </li>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/31">#31</a>: There has
      been a change to the validation logic to associate mismatches in the
      cardinality/baseType of the values produced by child expressions against what
      is required by the parent. Mismatches are now reported as problems of the parent,
      rather than the child. This makes issue #31 disappear.
    </li>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/33">#33</a>: The
      <code>identifier</code> of an <code>itemResult</code> was not being computed
      in the manner defined by the specification for items within tests. This is fixed here.
    </li>
    <li>
      <a href="https://github.com/davemckain/qtiworks/issues/35">#35</a>: The initialisation
      of outcome variables now correctly sets numeric OVs to 0 instead of NULL if there are no defaults provided.
    </li>
  </ul>
  <p>
    A small database schema change is required. Please run <code>qtiworks-engine/support/schema-migrations/beta2-to-beta3.sql</code>
    to upgrade your database if you have been running your own QTIWorks 1.0-beta2 installation.
  </p>

  <h3>Release 1.0-beta2 (22/08/2013)</h3>
  <p>
    This fixes a few bugs noted by people testing the production instance of QTIWorks:
  </p>
  <ul>
    <li>
      Fixed logic bug in the Test Plan's internal storage of nodes, arising in testParts
      using selection and ordering, which could trigger a logic exception during the delivery
      of the test.
    </li>
    <li>
      Fixed silly bug in the new author view triggered if an assessmentItemRef has been selected multiple
      times in the testPart.
    </li>
    <li>
      Fixed regression in the rendering stylesheets when determining whether feedback blocks should be
      shown when rendering the current state of a test item.
    </li>
    <li>
      MathAssess extensions should now intercept all possible cases where the assessment Maxima
      code could cause something bad to happen in Maxima, converting all of these to runtime errors
      rather than letting the candidate session explode.
    </li>
    <li>
      Validation process now flags up warnings if adaptive items are used in testParts having
      simultaneous submissionMode.
    </li>
  </ul>

  <h3>Release 1.0-DEV34 (20/08/2013)</h3>
  <p>
    Fixes a couple of bugs discovered after beta1 went live:
  </p>
  <ul>
    <li>
      There was a logic bug in the handling of global indices in the TestPlanner which
      occurred in tests using selection/ordering. The unit tests didn't cover this so it
      was missed. This is fixed now, with a slightly nicer implementation.
    </li>
    <li>
      There had been a regression in the logic determining whether to display item feedback
      in tests while the item session was still open. Nobody had noticed this before. Fixed here.
    </li>
  </ul>

  <h3>Release 1.0-beta1 (19/08/2013)</h3>
  <p>
    This first beta release brings the production branch back in line with
    the master (development) branch. It is essentially the same as 1.0-DEV33, but
    contains a few final bug fixes made during final testing on the production
    data hosted at Edinburgh.
  </p>
  <p>
    If you have been hosting your own instance of QTIWorks 1.0-M4, then you need to
    schedule and execute the upgrade to 1.0-beta1 quite carefully as there were
    many changes to the data model, requiring all candidate data to be deleted when
    applying this upgrade. You should upgrade as follows:
  </p>
  <ol>
    <li>Take QTIWorks 1.0-M4 offline (and make a full backup of the filesystem and database).</li>
    <li>Run the <code>purgeAnonymousData</code> engine manager action (on M4 binaries).</li>
    <li>Run the SQL script <code>qtiworks-engine/support/schema-migrations/m4-to-beta1.sql</code> on your QTIWorks database to upgrade its schema
      and delete the candidate data stored in the DB.</li>
    <li>Perform a <code>git fetch</code> and <code>git merge origin/production</code> to bring your code up to beta1, then do a clean rebuild.</li>
    <li>Run the <code>update</code> engine action to complete deleting candidate data from the QTIWorks database and file store.</li>
    <li>Update your <code>qtiworks-deployment.properties</code> against the newly-updated default version in the git tree.</li>
    <li>
      Deploy the QTIWorks 1.0-beta1 webapp. It should deploy and run without issues and all existing instructor users
      should be able to access the system and all of their existing assessments.
    </li>
  </ol>

  <h3>Release 1.0-DEV33 (12/08/2013)</h3>
  <p>
    Minor bug-fix release ahead of next release, which is currently scheduled to be BETA1.
    No changes are needed to the database schema.
  </p>

  <h3>Release 1.0-DEV32 (17/07/2013)</h3>
  <p>
    This final planned development snapshot brings the project to feature freeze in the context
    of its current funding.
  </p>
  <p>
    This development snapshot mainly includes some reorganisation and tidying of the MVC
    layer, in particular the new domain-level LTI instructor interface and the
    existing instructor interface. The latter has been simplified a bit and
    tidied up visually.
  </p>
  <p>
    Additionally, there are a number of fixes to the recent LTI functionality,
    including a fix to the LTI outcomes reporting service to handle issues with
    the computation of body hashes. Oddly, this was working fine in the free
    Blackboard coursesites.com site, but not on any Learn VLEs that I had tried.
    There is also a minor fix to the rendering system to tell it what to do with
    HTML <code>th</code> elements.
  </p>
  <p>
    <strong>Note:</strong>  If you are following these development releases,
    then you will need to run the schema migration script
    <code>qtiworks-engine/support/schema-migrations/dev31-to-dev32.sql</code>
    after compiling this version of the webapp. There is no need to delete
    candidate data if upgrading from DEV31.
  </p>

  <h3>Release 1.0-M4b (11/07/2013)</h3>
  <p>
    Patch release that cherry-picks the new MathJax SSL CDN URL from the master branch.
    (The old SSN CDN appears to have gone offline recently!)
  </p>

  <h3>Release 1.0-DEV31 (09/07/2013)</h3>
  <p>
    This development snapshot includes a working implementation of LTI instructor role
    functionality (via domain-level launches). It also completes and makes
    available the LTI result returning functionality sketched out earlier.
  </p>
  <p>
    A further snapshot will tidy the way this looks a bit, before we move to
    a beta/RC release if there are no major issues reported.
  </p>
  <p>
    <strong>Note:</strong>  If you are following these development releases,
    then you will need to run the schema migration script
    <code>qtiworks-engine/support/schema-migrations/dev30-to-dev31.sql</code>
    after compiling this version of the webapp. There is no need to delete
    candidate data if upgrading from DEV30.
  </p>

  <h3>Release 1.0-M4a (01/07/2013)</h3>
  <p>
    Patch release that cherry-picks the new Content Package handling code from the development branch. QTIWorks will
    no longer complain about the odd MIME types sent by some browsers when uploading ZIP files.
  </p>

  <h3>Release 1.0-DEV30 (01/07/2013)</h3>
  <p>
    This work-in-progress release adds in enough functionality for testing out the
    new domain-level LTI launches. I've released it now so that partners can start
    setting up their VLEs to use the new LTI instructor role functionality, which
    should appear in the next developer snapshot.
  </p>
  <p>
    <strong>Note:</strong>  If you are following these development releases,
    then you will need to run the schema migration script
    <code>qtiworks-engine/support/schema-migrations/dev29-to-dev30.sql</code>
    after compiling this version of the webapp. Then you must run the
    <code>update</code> action in the QTIWorks Engine Manager. (Note that all
    candidate session data needs to be deleted here.)
  </p>

  <h3>Release 1.0-DEV29 (03/06/2013)</h3>
  <p>
    This continues the work of the last 2 development snapshots, adding in a new author
    view which finally covers tests as well as items. It is now also possible
    to launch assessment containing validation errors or warnings.
  </p>
  <p>
    The rendering process now also records whether an assessment "explodes"
    while being delivered to candidates, and this information can be seen by
    instructors. Candidates experiencing an exploding assessment will be
    provided with a non-scary error page. (Explosions are generally unlikely,
    but the relaxation on when assessments can be run may cause some unplanned
    explosions to happen now.)
  </p>
  <p>
    Test handling now allows EXIT_TEST to be used anywhere. This will be
    treated the same way as EXIT_TESTPART in tests containing a single
    testPart, so that the candidate can still access feedback for individual
    items. In tests with multiple testParts, this will end the test and show
    only the test feedback. Support for branchRule has improved in that any
    sectionParts jumped by a branchRule are now recorded as such. The rendering
    process now excludes any jumped sectionParts.
  </p>
  <p>
    The handling of ZIP Content Packages has been relaxed so that silly MIME types sent by browsers no longer
    cause the import process to refuse to proceed. (See bug #28.)
  </p>
  <p>
    <strong>Note:</strong>  If you are following these development releases, then you will need to run the schema migration script
    <code>qtiworks-engine/support/schema-migrations/dev28-to-dev29.sql</code>
    after compiling this version of the webapp. Then you must run the
    <code>update</code> action in the QTIWorks Engine Manager. (Note that all
    candidate session data needs to be deleted here.)
  </p>

  <h3>Release 1.0-DEV28 (05/05/2013)</h3>
  <p>
    This release consolidates on DEV27, fixing some bugs and refining features added in DEV27. This includes some
    changes to the rendering of items and tests, including some improvements to the rendering of <code>mathEntryInteraction</code>s.
  </p>
  <p>
    The bundled set of MathAssess examples have been tidied up slightly, with the addition of some basic CSS to make
    their feedback stand out a bit more clearly. One of the items (MAB01) has been deprecated.
  </p>
  <p>
    There is a small database schema fix required here. See <code>qtiworks-engine/support/schema-migrations/dev27-to-dev28.sql</code>.
  </p>

  <h3>Release 1.0-DEV27 (30/04/2013)</h3>
  <p>
    This development snapshot fills in the remaining parts of the test specification that we plan to implement, namely
    <code>preCondition</code>, <code>branchRule</code> and the recording of duration at all required levels within
    the test. The low-level test and item running logic has been significantly refactored, and split into new classes
    called <code>TestSessionController</code> and <code>ItemSessionController</code> within JQTI+, which should be easier
    to reuse for other purposes. A large number of units tests have been created to test these new classes. This API can now
    be considered stable.
  </p>
  <p>
    This snapshot also includes significant refactorings to the higher-level code for running assessments, including the
    rendering packages. Assessment result XMLs are now generated and stored after each interaction a candidate makes with an
    assessment, so instructors can see and download partial results much
    earlier if they need to. A few further changes will be needed, but the API
    should now be considered stable.
  </p>
  <p>
    Finally, the snapshot includes a couple of bits of very basic "proctoring" functionality, allowing instructors to forcibly
    terminate candidate sessions if required.
  </p>
  <p>
    This snapshot requires a fairly large set of fixes to the database schema. See <code>qtiworks-engine/support/schema-migrations/dev26-to-dev27.sql</code>.
    You must also wipe all candidate session data as the internal XML state files have changed significantly.
  </p>

  <h3>Release 1.0-DEV26 (07/03/2013)</h3>
  <p>
    This development snapshot adds support for delivering tests containing multiple <code>testPart</code>s, and now
    evaluations any <code>preCondition</code>s declared at <code>testPart</code> level. It also completes
    the implementation of <code>testFeedback</code> to support both <code>during</code> and <code>atEnd</code>
    feedback, at both test and test part level.
  </p>
  <p>
    Note: This snapshot fixes a bug in the setting of default values for outcome variables in tests. As a result,
    the <code>showHide</code> attribute would not have been working correctly when referencing outcome variables
    defined to have a fixed default value. You may therefore need to check existing assessments to ensure they now
    behave correctly.
  </p>

  <h3>Release 1.0-M4 (07/03/2013)</h3>
  <p>
    This is basically 1.0-DEV25 with some further behind-the-scenes changes added since then. There is no noticeable
    change in functionality between M3 and M4, though we have dropped some obscure features that were never really used
    that much (such as the "playback" feature when running single items).
  </p>

  <h3>Release 1.0-DEV25 (28/02/2013)</h3>
  <p>
    This development snapshot includes a large number of behind-the-scenes changes to make QTIWorks easier to install and manage.
    It also includes a number of minor fixes, but no real functional changes.
  </p>

  <h3>Release 1.0-M3 (14/02/2013)</h3>
  <p>
    Third production milestone, equivalent to the 1.0-DEV24 development snapshot.
  </p>

  <h3>Release 1.0-DEV24 (11/02/2013)</h3>
  <p>
    Minor update including mainly low-level code refactoring and documentation improvements.
    Visible changes are:
  </p>
  <ul>
    <li>Temporary removal of 32 character constraint check on identifiers (for Uniqurate questions)</li>
    <li>Fixed corner case in response processing with endAttemptInteractions if no responses had previously been bound</li>
    <li>Minor improvement to rendering of uploadInteraction</li>
  </ul>

  <h3>Release 1.0-DEV23 (29/01/2013)</h3>
  <p>
    Minor update that finally includes front-end functionality for deleting Assessments
    and Deliverables. (There is more back-end deletion functionality included too.)
  </p>

  <h3>Release 1.0-DEV22 (11/01/2013)</h3>
  <p>
    This development snapshot continues with the implementation of the test specification, refining and slightly extending
    what was added in DEV21, as well as adding some improved test samples.
  </p>
  <p>
    We now support <code>testPart</code> feedback (albeit still only with single <code>testPart</code>s), the
    showing of item solutions and the display of section/rubric information within test item rendering.
  </p>

  <h3>Release 1.0-DEV21 (07/01/2013)</h3>
  <p>
    This development snapshot continues with the implementation of the test specification. It now supports all 4 combinations
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

  <h3>Release 1.0-M2 (07/01/2013)</h3>
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

  <h3>Release 1.0-DEV20 (26/11/2012)</h3>
  <p>
    Minor update before Sue Milne's test pilot. This adds support for
    <code>printedVariable/@index</code>, as well as a change to
    <code>CandidateSessionStarter</code>'s logic. We now attempt to
    reconnect to an existing non-terminated session if available, rather
    than always starting a new one.
  </p>

  <h3>Release 1.0-DEV19 (17/11/2012)</h3>
  <p>
    Filled in initial sketch of support for <code>allowReview</code> and
    <code>showFeedback</code> in the test delivery. Fixed issue with mixed
    namespaces when serializing <code>assessmentResult</code> XML. Added
    basic functionality for getting at candidate data (summary table, CSV summary,
    ZIP bundle containing all <code>assessmentResult</code> files).
  </p>

  <h3>Release 1.0-DEV18 (15/11/2012)</h3>
  <p>
    This development snapshot tidies up implementation of tests added in DEV17, and adds in
    initial functionality within the webapp for viewing and downloading result
    data for candidate sessions on a given delivery.
  </p>

  <h3>Release 1.0-DEV17 (09/11/2012)</h3>
  <p>
    This development snapshot continues with the implementation of tests.
    A first sketch of the full delivery of NONLINEAR/INDIVIDUAL tests is now in place,
    ready for discussion with project partners.
  </p>

  <h3>Release 1.0-DEV16 (02/11/2012)</h3>
  <p>
    This development snapshot continues with the implementation of tests. The basic logic for handling
    tests with one NONLINEAR/INDIVIDUAL part are now in place, and much of the
    supporting data model is now ready. You can now upload and start one of
    these tests, but you'll just end up seeing a dump of the resulting test
    state (after template processing has run on each item).
  </p>

  <h3>Release 1.0-DEV15 (25/10/2012)</h3>
  <p>
    This development snapshot includes a lot of the groundwork required for the test implementation,
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

  <h3>Release 1.0-DEV14 (28/09/2012)</h3>
  <p>
    This is the first development snapshot following the temporarily split into
    production and development instances.  This snapshot does not contain any
    visible new features but includes a lot of changes and code refactoring to
    consolidate the work of the last few iterations and help prepare for the
    work on tests. Key changes are:
  </p>
  <ul>
    <li>
      This snapshot now includes the final (final?!) schema.
    </li>
    <li>
      The validation API has been significantly refactored, merging with a newer more general
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

  <h3>Release 1.0-M1 (27/09/2012)</h3>
  <p>
    This "Milestone 1" snapshot is the first of a set of stable, less frequent releases
    so that people using QTIWorks for "real" stuff don't have to worry too much about things
    suddenly changing. Functionally, this is the same as 1.0-DEV13 but includes a few improvements
    you won't notice.
  </p>
  <p>
    The next milestone snapshot will be released once we have some of the test functionality implemented.
  </p>

  <h3>Release 1.0-DEV13 (04/09/2012)</h3>
  <p>
    This development snapshot finally adds in support for the <code>integerOrVariableRef</code>,
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

  <h3>Release 1.0-DEV12 (15/08/2012)</h3>
  <p>
    This development snapshot fills in more of the Instructor functionality, such as the
    management of "deliveries" of an assessment. It also includes a first cut of the LTI
    launch for assessments, as well as a number of less noticeable improvements.
  </p>

  <h3>Release 1.0-DEV11 (09/07/2012)</h3>
  <p>
    This development snapshot fixes a number of minor bugs found after the release of 1.0-DEV10, including
    a couple of regressions in the display of validation results. The item rendering now handles
    overridden correct response values correctly, and the "show solution" button is only shown if
    there is something to show, which should cut down the number of delivery settings that people
    need to manage.
  </p>

  <h3>Release 1.0-DEV10 (07/07/2012)</h3>
  <p>
    This consolidates the work in the last snapshot by making it look a bit nicer and easier to use.
    More details can be found in the accompanying
    <a href="http://qtisupport.blogspot.co.uk/2012/07/qtiworks-snapshot-10-has-been-released.html">blog post about this release</a>.
  </p>

  <h3>Release 1.0-DEV9 (03/07/2012)</h3>
  <p>
    This development snapshot adds a standalone "upload and run" feature that can be run without requiring a login,
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

  <h3>Release 1.0-DEV8 (31/05/2012)</h3>
  <p>
    This adds further enhancements to the rendering process for single items.
    I have written a
    <a href="http://qtisupport.blogspot.co.uk/2012/05/enhanced-item-rendering-in-qtiworks.html">blog post for this release</a>,
    so it's probably more useful to
    link to it than try to paraphrase it badly here.
    </p>

  <h3>Release 1.0-DEV7 (25/05/2012)</h3>
  <p>
    This snapshot finally includes all of the internal logic for successfully
    delivering - and recording the delivery of - a single assessment item to a
    candidate, as well as much of the logic for managing assessments within the
    system. However, not much of this is yet visible to end users, apart from a
    revised version of the "play sample items" functionality that uses the new
    implementation. There's a little bit of the assessment management functionality
    visible, as well as the login form that people will use to access this, but
    not enough to be truly usable yet.
  </p>

  <h3>Release 1.0-DEV6 (02/05/2012)</h3>
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

  <h3>Release 1.0-DEV5 (25/04/2012)</h3>
  <p>
    Further tweaks to logic determining whether submit button should
    appear in item rendering, in light of discussion with Sue.
    Removed automated feedback styling added in DEV4 as it can't
    determine between real feedback and selective content.
    I've also hidden the RESET button to see if anyone misses it...
  </p>

  <h3>Release 1.0-DEV4 (24/04/2012)</h3>
  <p>
    Fixed bug introduced when refactoring endAttemptInteraction,
    which prevented it from working correctly. Also added some experimental
    styling on feedback elements, which will need further work.
    Further significant refactoring work has been done on JQTI+, in particular
    the API for extensions (customOperator/customInteraction). I have also
    started laying the ORM pipework for the webapp domain model.
  </p>

  <h3>Release 1.0-DEV3 (11/04/2012)</h3>
  <p>
    This development snapshot adds in the MathAssess examples, as well as the
    "get source" and "get item result" functions in the item delivery.
    Significant further refactorings and improvements have also 
    been made within JQTI+.
  </p>

  <h3>Release 1.0-DEV2 (23/03/2012)</h3>
  <p>
    Demonstrates the newly-refactored assessmentItem state &amp; logic
    code in JQTI+, joining it back in with the rendering components
    from MathAssessEngine-dev. This demo only lets you play around with
    some pre-loaded sample items as I haven't started work on the CRUD
    API for getting your items into the system.
  </p>

  <h3>Release 1.0-DEV1 (26/01/2012)</h3>
  <p>
    Demonstrates the newly-refactored validation functionality in JQTI+,
    with more general JQTI -&gt; JQTI+ refactoring work continuing apace.
  </p>

</page:page>
