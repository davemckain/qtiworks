<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders the test feedback

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

  <!-- Web Application contextPath. Starts with a '/' -->
  <xsl:param name="webappContextPath" as="xs:string" required="yes"/>

  <!-- Set to true to include author debug information -->
  <xsl:param name="authorMode" as="xs:boolean" required="yes"/>

  <!-- Current state -->
  <xsl:param name="testSessionState" as="element(qw:testSessionState)"/>

  <!-- Relevant action URLs -->
  <xsl:param name="reviewItemUrl" as="xs:string" required="yes"/>

  <!-- Extract current testPart -->
  <xsl:variable name="currentTestPartKey" select="$testSessionState/@currentTestPartKey" as="xs:string"/>
  <xsl:variable name="currentTestPart" select="$testSessionState/qw:testPlan/qw:node[@key=$currentTestPartKey]" as="element(qw:node)?"/>

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
        <!-- Show feedback -->
        <xsl:apply-templates select="qti:testFeedback[@access='atEnd']"/>

        <!-- Review -->
        <xsl:apply-templates select="$currentTestPart" mode="testPart-review"/>

        <!-- Test session control -->
        <xsl:call-template name="qw:test-controls"/>
       </body>
    </html>
  </xsl:template>

  <xsl:template match="qti:testFeedback">
    <div class="testFeedback">
      <h2>Feedback</h2>
      <xsl:call-template name="feedback"/>
    </div>
  </xsl:template>

  <xsl:template match="qw:node" mode="testPart-review">
    <xsl:variable name="reviewable-items" select=".//qw:node[@type='ASSESSMENT_ITEM_REF' and (@allowReview='true' or @showFeedbacl='true')]" as="element(qw:node)*"/>
    <xsl:if test="exists($reviewable-items)">
      <h2>Review your responses</h2>
      <ul class="testPartNavigation">
        <xsl:apply-templates select="$reviewable-items" mode="testPart-review-item"/>
      </ul>
    </xsl:if>
  </xsl:template>

  <xsl:template match="qw:node" mode="testPart-review-item">
    <xsl:variable name="itemSessionState" select="$testSessionState/qw:item[@key=current()/@key]/qw:itemSessionState" as="element(qw:itemSessionState)"/>
    <li>
      <form action="{$webappContextPath}{$reviewItemUrl}/{@key}" method="post">
        <button type="submit">
          <span class="questionTitle"><xsl:value-of select="@sectionPartTitle"/></span>
          <div class="itemStatus review">
            <!-- FIXME: Do this better -->
            <xsl:choose>
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

