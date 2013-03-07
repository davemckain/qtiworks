<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders the final test feedback.

(This is currently only shown in multi-part tests. Single part tests
combine the feedback for the test and the testPart.)

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

  <xsl:import href="qti-fallback.xsl"/>
  <xsl:import href="test-common.xsl"/>
  <xsl:import href="serialize.xsl"/>
  <xsl:import href="utils.xsl"/>

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
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"/>
        <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.1/jquery-ui.min.js"/>
        <script src="{$webappContextPath}/rendering/javascript/QtiWorksRendering.js?{$qtiWorksVersion}"/>
        <xsl:if test="$authorMode">
          <script src="{$webappContextPath}/rendering/javascript/AuthorMode.js?{$qtiWorksVersion}"/>
        </xsl:if>

        <!-- Styling for JQuery -->
        <link rel="stylesheet" type="text/css" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.1/themes/redmond/jquery-ui.css"/>

        <!-- QTIWorks styling -->
        <link rel="stylesheet" href="{$webappContextPath}/rendering/css/assessment.css?{$qtiWorksVersion}" type="text/css" media="screen"/>

      </head>
      <body class="qtiworks assessmentTest testFeedback">
        <h1>Test Complete</h1>

        <!-- Show 'atEnd' feedback for the test -->
        <xsl:apply-templates select="qti:testFeedback[@access='atEnd']"/>

        <!-- Test session control -->
        <xsl:call-template name="qw:test-controls"/>
       </body>
    </html>
  </xsl:template>

  <xsl:template name="qw:test-controls">
    <div class="sessionControl">
      <ul class="controls test">
        <li>
          <form action="{$webappContextPath}{$exitTestUrl}" method="post"
            onsubmit="return confirm('Are you sure? This will exit the test and you can\'t go back in.')">
            <input type="submit" value="Exit Test"/>
          </form>
        </li>
      </ul>
    </div>
  </xsl:template>

</xsl:stylesheet>
