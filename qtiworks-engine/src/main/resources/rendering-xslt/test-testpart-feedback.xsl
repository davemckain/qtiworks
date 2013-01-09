<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders the test(Part) feedback

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:m="http://www.w3.org/1998/Math/MathML"
  xmlns:qw="http://www.ph.ed.ac.uk/qtiworks"
  xmlns="http://www.w3.org/1999/xhtml"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xs qti qw m">

  <!-- ************************************************************ -->

  <xsl:import href="test-common.xsl"/>
  <xsl:import href="serialize.xsl"/>
  <xsl:import href="utils.xsl"/>

  <!-- Relevant action URLs -->
  <xsl:param name="reviewItemUrl" as="xs:string" required="yes"/>

  <!-- This test -->
  <xsl:variable name="assessmentTest" select="/*[1]" as="element(qti:assessmentTest)"/>

  <!-- ************************************************************ -->

  <xsl:template match="/">
    <xsl:variable name="unserialized-output" as="element()">
      <xsl:apply-templates select="qw:to-qti21(/)/*"/>
    </xsl:variable>
    <xsl:apply-templates select="$unserialized-output" mode="serialize"/>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="qti:assessmentTest" as="element(html)">
    <html>
      <xsl:if test="@lang">
        <xsl:copy-of select="@lang"/>
        <xsl:attribute name="xml:lang" select="@lang"/>
      </xsl:if>
      <head>
        <title><xsl:value-of select="@title"/></title>
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"/>
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"/>
        <script src="{$webappContextPath}/rendering/javascript/QtiWorksRendering.js" type="text/javascript"/>
        <xsl:if test="$authorMode">
          <script src="{$webappContextPath}/rendering/javascript/AuthorMode.js" type="text/javascript"/>
        </xsl:if>

        <!-- Styling for JQuery -->
        <link rel="stylesheet" type="text/css" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/redmond/jquery-ui.css"/>

        <!-- QTIWorks styling -->
        <link rel="stylesheet" href="{$webappContextPath}/rendering/css/item.css" type="text/css" media="screen"/>

      </head>
      <body class="qtiworks assessmentTest testFeedback">
        <h1><xsl:value-of select="$testOrTestPart"/> Complete</h1>
        <!-- Show testPart feedback -->
        <xsl:apply-templates select="$currentTestPart/qti:testFeedback[@access='atEnd']"/>

        <!-- Show test feedback if there's only 1 testPart -->
        <xsl:if test="not($hasMultipleTestParts)">
          <xsl:apply-templates select="qti:testFeedback[@access='atEnd']"/>
        </xsl:if>

        <!-- Review -->
        <xsl:apply-templates select="$currentTestPartNode" mode="testPart-review"/>

        <!-- Test session control -->
        <xsl:call-template name="qw:test-controls"/>
       </body>
    </html>
  </xsl:template>

  <xsl:template match="qw:node[@type='TEST_PART']" mode="testPart-review">
    <xsl:variable name="reviewable-items" select=".//qw:node[@type='ASSESSMENT_ITEM_REF' and (@allowReview='true' or @showFeedback='true')]" as="element(qw:node)*"/>
    <xsl:if test="exists($reviewable-items)">
      <h2>Review your responses</h2>
      <p>
        You may review your responses to some (or all) questions. These are listed below.
      </p>
      <ul class="testPartNavigation">
        <xsl:apply-templates mode="testPart-review"/>
      </ul>
    </xsl:if>
  </xsl:template>

  <xsl:template match="qw:node[@type='ASSESSMENT_SECTION']" mode="testPart-review">
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
      <ul class="testPartNavigationInner">
        <xsl:apply-templates mode="testPart-review"/>
      </ul>
    </li>
  </xsl:template>

  <xsl:template match="qw:node[@type='ASSESSMENT_ITEM_REF']" mode="testPart-review">
    <xsl:variable name="reviewable" select="@allowReview='true' or @showFeedback='true'" as="xs:boolean"/>
    <xsl:variable name="itemSessionState" select="$testSessionState/qw:item[@key=current()/@key]/qw:itemSessionState" as="element(qw:itemSessionState)"/>
    <li class="assessmentItem">
      <form action="{$webappContextPath}{$reviewItemUrl}/{@key}" method="post">
        <button type="submit">
          <xsl:if test="not($reviewable)">
            <xsl:attribute name="disabled" select="'disabled'"/>
          </xsl:if>
          <span class="questionTitle"><xsl:value-of select="@sectionPartTitle"/></span>
          <div class="itemStatus review">
            <!-- FIXME: Do this better -->
            <xsl:choose>
              <xsl:when test="not($reviewable)">
                Not Reviewable
              </xsl:when>
              <xsl:when test="not(empty($itemSessionState/@unboundResponseIdentifiers) and empty($itemSessionState/@invalidResponseIdentifiers))">
                Review (Invalid Answer)
              </xsl:when>
              <xsl:when test="$itemSessionState/@responded='true'">
                Review
              </xsl:when>
              <xsl:when test="$itemSessionState/@presented='true'">
                Review (Not Answered)
              </xsl:when>
              <xsl:otherwise>
                Review (Not Attempted)
              </xsl:otherwise>
            </xsl:choose>
          </div>
        </button>
      </form>
    </li>
  </xsl:template>

</xsl:stylesheet>
