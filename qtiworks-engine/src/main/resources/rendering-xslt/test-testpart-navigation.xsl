<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders the navigation for the current testPart

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
  <xsl:param name="selectItemUrl" as="xs:string" required="yes"/>

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
      <body class="qtiworks assessmentTest testPartNavigation">
        <xsl:choose>
          <xsl:when test="exists($currentTestPart)">
            <h2>Test Questions</h2>
            <p>
              Yes, it looks like crap at the moment. Bear with me!
            </p>
            <xsl:apply-templates select="$currentTestPart" mode="testPart-navigation"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message terminate="yes">
              No current testPart
            </xsl:message>
          </xsl:otherwise>
        </xsl:choose>

        <!-- Test session control -->
        <xsl:call-template name="qw:test-controls"/>
       </body>
    </html>
  </xsl:template>

  <xsl:template match="qw:node" mode="testPart-navigation">
    <ul class="testPartNavigation">
      <xsl:apply-templates select=".//qw:node[@type='ASSESSMENT_ITEM_REF']" mode="testPart-item"/>
    </ul>
  </xsl:template>

  <xsl:template match="qw:node" mode="testPart-item">
    <xsl:variable name="itemSessionState" select="$testSessionState/qw:item[@key=current()/@key]/qw:itemSessionState" as="element(qw:itemSessionState)"/>
    <li>
      <form action="{$webappContextPath}{$selectItemUrl}/{@key}" method="post">
        <input type="submit" value="{@itemTitle}"/>
        <xsl:apply-templates select="$itemSessionState" mode="item-status"/>
      </form>
    </li>
  </xsl:template>

</xsl:stylesheet>

