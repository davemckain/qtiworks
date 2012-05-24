<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders a standalone assessmentItem

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

  <xsl:import href="qti-common.xsl"/>
  <xsl:import href="serialize.xsl"/>
  <xsl:import href="utils.xsl"/>

  <!-- ************************************************************ -->

  <xsl:template match="/">
    <xsl:variable name="unserialized-output" as="element()">
      <xsl:apply-templates select="*"/>
    </xsl:variable>
    <xsl:apply-templates select="$unserialized-output" mode="serialize"/>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="qti:assessmentItem" as="element(html)">
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

        <script src="{$webappContextPath}/rendering/javascript/QtiWorks.js" type="text/javascript"/>
        <!-- The following are used for certain interactions, as well as the debugging panel. -->
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"/>
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"/>

        <!--
        Import ASCIIMathML stuff if there are any MathEntryInteractions in the question.
        (It would be quite nice if we could allow each interaction to hook into this
        part of the result generation directly.)
        -->
        <xsl:if test="$containsMathEntryInteraction">
          <script src="{$webappContextPath}/rendering/javascript/UpConversionAjaxController.js" type="text/javascript"/>
          <script src="{$webappContextPath}/rendering/javascript/AsciiMathInputController.js" type="text/javascript"/>
          <script type="text/javascript">
            UpConversionAjaxController.setUpConversionServiceUrl('<xsl:value-of select="$webappContextPath"/>/web/public/verifyAsciiMath');
            UpConversionAjaxController.setDelay(300);
          </script>
        </xsl:if>

        <!-- Styling for JQuery -->
        <link rel="stylesheet" type="text/css" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/redmond/jquery-ui.css"/>

        <!-- QTIWorks Item styling -->
        <link rel="stylesheet" href="{$webappContextPath}/rendering/css/common-rendering.css" type="text/css" media="screen"/>
        <link rel="stylesheet" href="{$webappContextPath}/rendering/css/item-rendering.css" type="text/css" media="screen"/>

        <!-- Include stylesheet declared within item -->
        <xsl:apply-templates select="qti:stylesheet"/>
      </head>
      <body>
        <div class="qtiworks">
          <div class="qtiworksRendering">
            <div class="assessmentItem">
              <h1 class="itemTitle"><xsl:value-of select="@title"/></h1>
              <div class="itemBody">
                <!-- Descend into itemBody only -->
                <xsl:apply-templates select="qti:itemBody"/>
              </div>
              <!-- Display active modal feedback (only after responseProcessing) -->
              <xsl:if test="$isResponded">
                <xsl:variable name="modalFeedback" as="element()*">
                  <xsl:for-each select="qti:modalFeedback">
                    <xsl:variable name="feedback" as="node()*">
                      <xsl:call-template name="feedback"/>
                    </xsl:variable>
                    <xsl:if test="$feedback">
                      <div class="modalFeedbackItem">
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
            </div>
          </div>
          <div class="qtiworksAuthorControl">
            <h2>Session control</h2>
            <ul>
              <xsl:if test="$resetAllowed">
                <li>
                  <form action="{$webappContextPath}{$resetUrl}" method="post">
                    <input type="submit" value="Reset"/>
                  </form>
                </li>
              </xsl:if>
              <li>
                <form action="{$webappContextPath}{$exitUrl}" method="post">
                  <input type="submit" value="Exit and return"/>
                </form>
              </li>
              <xsl:if test="$resultAllowed">
                <li>
                  <a href="{$webappContextPath}{$resultUrl}">View ItemResult</a>
                </li>
              </xsl:if>
            </ul>
            <h2>Author tools</h2>
            <ul>
              <xsl:if test="$sourceAllowed">
                <li><a href="{$webappContextPath}{$sourceUrl}">View Item source</a></li>
              </xsl:if>
            </ul>
          </div>
          <!-- Author debugging information -->
          <xsl:call-template name="internalState"/>
        </div>
       </body>
    </html>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="qti:itemBody">
    <div id="itemBody">
      <form method="post" action="{$webappContextPath}{$attemptUrl}"
        onsubmit="return QtiWorks.submit()" enctype="multipart/form-data"
        onreset="QtiWorks.reset()" autocomplete="off">

        <xsl:apply-templates/>

        <!-- Maybe show controls -->
        <xsl:if test="$attemptAllowed">
          <div class="controls">
            <!--<input type="reset" value="RESET INPUT"/>-->
            <input id="submit_button" name="submit" type="submit" value="SUBMIT ANSWER"/>
          </div>
        </xsl:if>
      </form>
    </div>
  </xsl:template>

</xsl:stylesheet>
