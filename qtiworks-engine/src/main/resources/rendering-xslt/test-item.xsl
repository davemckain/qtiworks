<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders an AssessmentItem within an AssessmentTest, as seen by candidates.

NB: This is used both while being presented, and during review.

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:qw="http://www.ph.ed.ac.uk/qtiworks"
  xmlns:m="http://www.w3.org/1998/Math/MathML"
  xmlns="http://www.w3.org/1999/xhtml"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xs qti qw m">

  <!-- ************************************************************ -->

  <xsl:import href="qti-fallback.xsl"/>
  <xsl:import href="test-common.xsl"/>
  <xsl:import href="item-common.xsl"/>
  <xsl:import href="serialize.xsl"/>
  <xsl:import href="utils.xsl"/>

  <!--
  Key for item being rendered is passed here.
  NB: Can't simply extract $testSessionState/@currentItemKey as this will be null
  when *reviewing* an item.
  -->
  <xsl:param name="itemKey" as="xs:string"/>

  <!-- Action permissions -->
  <xsl:param name="testPartNavigationAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="finishItemAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="reviewTestPartAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="testItemSolutionAllowed" as="xs:boolean" required="yes"/>

  <!-- Effective value of itemSessionControl/@showFeedback for this item -->
  <xsl:param name="showFeedback" as="xs:boolean"/>

  <!--
  Keep reference to assesssmentItem element as the processing chain goes off on a tangent
  at one point.
  -->
  <xsl:variable name="assessmentItem" select="/*[1]" as="element(qti:assessmentItem)"/>

  <xsl:variable name="itemFeedbackAllowed" as="xs:boolean"
    select="if ($renderingMode='REVIEW')
      then (/qti:assessentItem/@adaptive='true' or $showFeedback)
      else ($renderingMode!='SOLUTION')"/>

  <!-- ************************************************************ -->

  <xsl:template match="/">
    <xsl:variable name="unserialized-output" as="element()">
      <xsl:apply-templates select="*"/>
    </xsl:variable>
    <xsl:apply-templates select="$unserialized-output" mode="serialize"/>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="qti:assessmentItem" as="element(html)">
    <xsl:variable name="contains-mathml" select="exists(qti:itemBody//m:*)" as="xs:boolean"/>
    <xsl:variable name="containsMathEntryInteraction"
      select="exists(qti:itemBody//qti:customInteraction[@class='org.qtitools.mathassess.MathEntryInteraction'])"
      as="xs:boolean"/>
    <html>
      <xsl:if test="@lang">
        <xsl:copy-of select="@lang"/>
        <xsl:attribute name="xml:lang" select="@lang"/>
      </xsl:if>
      <head>
        <title><xsl:value-of select="@title"/></title>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"/>
        <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.1/jquery-ui.min.js"/>
        <script src="{$webappContextPath}/rendering/javascript/QtiWorksRendering.js?{$qtiWorksVersion}"/>
        <xsl:if test="$authorMode">
          <script src="{$webappContextPath}/rendering/javascript/AuthorMode.js?{$qtiWorksVersion}"/>
        </xsl:if>

        <!--
        Import ASCIIMathML stuff if there are any MathEntryInteractions in the question.
        (It would be quite nice if we could allow each interaction to hook into this
        part of the result generation directly.)
        -->
        <xsl:if test="$containsMathEntryInteraction">
          <script src="{$webappContextPath}/rendering/javascript/UpConversionAjaxController.js?{$qtiWorksVersion}"/>
          <script src="{$webappContextPath}/rendering/javascript/AsciiMathInputController.js?{$qtiWorksVersion}"/>
          <script>
            UpConversionAjaxController.setUpConversionServiceUrl('<xsl:value-of select="$webappContextPath"/>/candidate/verifyAsciiMath');
            UpConversionAjaxController.setDelay(300);
          </script>
        </xsl:if>

        <!-- Styling for JQuery -->
        <link rel="stylesheet" type="text/css" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.1/themes/redmond/jquery-ui.css"/>

        <!-- QTIWorks Item styling -->
        <link rel="stylesheet" href="{$webappContextPath}/rendering/css/assessment.css?{$qtiWorksVersion}" type="text/css" media="screen"/>
      </head>
      <body class="qtiworks assessmentItem assessmentTest">

        <!-- Drill down into current item via current testPart structure -->
        <xsl:apply-templates select="$currentTestPartNode" mode="testPart-drilldown"/>

        <!--
        Show 'during' tetFeedback for the current testPart and/or the test itself.
        The info model says this should be shown directly after outcome processing.
        This is equivalent in this case to the item's sessionStatus='final'
        -->
        <xsl:if test="$sessionStatus='final'">
          <!-- Show any 'during' testFeedback for the current testPart -->
          <xsl:apply-templates select="$currentTestPart/qti:testFeedback[@access='during']"/>

          <!-- Show any 'during' testFeedback for the test -->
          <xsl:apply-templates select="$assessmentTest/qti:testFeedback[@access='during']"/>
        </xsl:if>

        <!-- Test Session control -->
        <xsl:call-template name="qw:test-controls"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="qw:test-controls">
    <div class="sessionControl">
      <ul class="controls test">
        <xsl:if test="$testPartNavigationAllowed">
          <li>
            <form action="{$webappContextPath}{$testPartNavigationUrl}" method="post">
              <input type="submit" value="Test Question Menu"/>
            </form>
          </li>
        </xsl:if>
        <xsl:if test="$finishItemAllowed">
          <li>
            <form action="{$webappContextPath}{$finishTestItemUrl}" method="post">
              <input type="submit" value="Finish Question"/>
            </form>
          </li>
        </xsl:if>
        <!-- Review state -->
        <xsl:if test="$reviewTestPartAllowed">
          <li>
            <form action="{$webappContextPath}{$reviewTestPartUrl}" method="post">
              <input type="submit" value="Back to Test Feedback"/>
            </form>
          </li>
        </xsl:if>
        <xsl:if test="$testItemSolutionAllowed">
          <li>
            <form action="{$webappContextPath}{$showTestItemSolutionUrl}/{$itemKey}" method="post">
              <input type="submit" value="Show Solution"/>
            </form>
          </li>
        </xsl:if>
        <xsl:if test="$renderingMode='SOLUTION'">
          <!-- Allow return to item review state -->
          <li>
            <form action="{$webappContextPath}{$reviewTestItemUrl}/{$itemKey}" method="post">
              <input type="submit" value="Hide Solution"/>
            </form>
          </li>
        </xsl:if>
      </ul>
    </div>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="qw:node[@type='TEST_PART']" mode="testPart-drilldown">
    <ul class="testPartDrilldown">
      <xsl:apply-templates mode="testPart-drilldown"/>
    </ul>
  </xsl:template>

  <xsl:template match="qw:node[@type='ASSESSMENT_SECTION']" mode="testPart-drilldown">
    <xsl:if test=".//qw:node[@key=$itemKey]">
      <!-- Only show sections that ancestors of current item -->
      <li class="assessmentSection">
        <header>
          <!-- Section title -->
          <h2><xsl:value-of select="@sectionPartTitle"/></h2>
          <!-- Handle rubrics -->
          <xsl:variable name="sectionIdentifier" select="qw:extract-identifier(.)" as="xs:string"/>
          <xsl:variable name="assessmentSection" select="$assessmentTest//qti:assessmentSection[@identifier=$sectionIdentifier]" as="element(qti:assessmentSection)*"/>
          <xsl:apply-templates select="$assessmentSection/qti:rubricBlock"/>
        </header>
        <!-- Descend -->
        <ul class="testPartDrilldownInner">
          <xsl:apply-templates mode="testPart-drilldown"/>
        </ul>
      </li>
    </xsl:if>
  </xsl:template>

  <xsl:template match="qw:node[@type='ASSESSMENT_ITEM_REF']" mode="testPart-drilldown">
    <xsl:if test="@key=$itemKey">
      <!-- We've reached the current item -->
      <li class="currentItem">
        <xsl:apply-templates select="$assessmentItem" mode="render-item"/>
      </li>
    </xsl:if>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="qti:assessmentItem" mode="render-item">
    <!-- Item title -->
    <h1 class="itemTitle">
      <!-- FIXME: This isn't very nice! -->
      <xsl:choose>
        <xsl:when test="$renderingMode='REVIEW'">
          <div class="itemStatus review">Review</div>
        </xsl:when>
        <xsl:when test="$renderingMode='SOLUTION'">
          <div class="itemStatus review">Model Solution</div>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="$itemSessionState" mode="item-status"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="@title"/>
    </h1>

    <!-- Render item body -->
    <xsl:apply-templates select="qti:itemBody"/>

    <!-- Display active modal feedback (only after responseProcessing) -->
    <xsl:if test="$itemFeedbackAllowed and $sessionStatus='final'">
      <xsl:variable name="modalFeedback" as="element()*">
        <xsl:for-each select="qti:modalFeedback">
          <xsl:variable name="feedback" as="node()*">
            <xsl:call-template name="feedback"/>
          </xsl:variable>
          <xsl:if test="$feedback">
            <div class="modalFeedback">
              <xsl:if test="@title"><h3><xsl:value-of select="@title"/></h3></xsl:if>
              <xsl:sequence select="$feedback"/>
            </div>
          </xsl:if>
        </xsl:for-each>
      </xsl:variable>
      <xsl:if test="exists($modalFeedback)">
        <div class="modalFeedback">
          <h2>Feedback</h2>
          <xsl:sequence select="$modalFeedback"/>
        </div>
      </xsl:if>
    </xsl:if>

    <!-- Item Session control -->
    <!-- (We are not using any of the controls present for standalone items)
    <xsl:call-template name="qw:item-controls"/>
    -->
  </xsl:template>

  <xsl:template match="qti:itemBody">
    <div id="itemBody">
      <form method="post" action="{$webappContextPath}{$attemptUrl}"
        onsubmit="return QtiWorksRendering.submit()" enctype="multipart/form-data"
        onreset="QtiWorksRendering.reset()" autocomplete="off">

        <xsl:apply-templates/>

        <xsl:if test="$isSessionInteracting">
          <xsl:variable name="submitText" as="xs:string"
            select="if ($currentTestPart/@submissionMode='individual') then 'SUBMIT ANSWER' else 'SAVE ANSWER'"/>
          <div class="controls">
            <input id="submit_button" name="submit" type="submit" value="{$submitText}"/>
          </div>
        </xsl:if>
      </form>
    </div>
  </xsl:template>

  <!-- Override using $showFeedback -->
  <xsl:template match="qti:feedbackInline | qti:feedbackBlock">
    <xsl:if test="$itemFeedbackAllowed">
      <xsl:apply-imports/>
    </xsl:if>
  </xsl:template>

  <!-- Disable any buttons in the question (from endAttemptInteraction) if not in interacting state -->
  <xsl:template match="qti:endAttemptInteraction[not($isSessionInteracting)]">
    <input type="submit" name="{@responseIdentifier}" value="{@title}" disabled="disabled"/>
  </xsl:template>

</xsl:stylesheet>
