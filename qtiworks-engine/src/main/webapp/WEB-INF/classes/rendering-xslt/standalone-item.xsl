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

  <xsl:param name="articleId" as="xs:string?"/>
  <xsl:param name="queryString" as="xs:string?"/>

  <xsl:param name="displayTitle" select="true()" as="xs:boolean"/>
  <xsl:param name="displayControls" select="true()" as="xs:boolean"/>

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

        <script src="{$engineBasePath}/rendering/javascript/QtiWorks.js" type="text/javascript"/>
        <!-- The following are used for certain interactions, as well as the debugging panel. -->
        <script src="{$engineBasePath}/rendering/javascript/jquery.min.js" type="text/javascript"/>
        <script src="{$engineBasePath}/rendering/javascript/jquery-ui.custom.min.js" type="text/javascript"/>

        <!--
        Import ASCIIMathML stuff if there are any MathEntryInteractions in the question.
        (It would be quite nice if we could allow each interaction to hook into this
        part of the result generation directly.)
        -->
        <xsl:if test="$containsMathEntryInteraction">
          <script src="{$engineBasePath}/rendering/javascript/UpConversionAJAXController.js" type="text/javascript"/>
          <script src="{$engineBasePath}/rendering/javascript/ASCIIMathInputController.js" type="text/javascript"/>
          <script type="text/javascript">
            UpConversionAJAXController.setUpConversionServiceUrl('<xsl:value-of select="$engineBasePath"/>/input/verifyASCIIMath');
            UpConversionAJAXController.setDelay(300);
          </script>
        </xsl:if>

        <!-- Styling for JQuery -->
        <link rel="stylesheet" href="{$engineBasePath}/rendering/css/redmond/jquery-ui.custom.css" type="text/css"/>

        <!-- QTIWorks Item styling -->
        <link rel="stylesheet" href="{$engineBasePath}/rendering/css/item-rendering.css" type="text/css" media="screen"/>

        <!-- Include stylesheet declared within item -->
        <xsl:apply-templates select="qti:stylesheet"/>
      </head>
      <body>
        <div class="qtiworksRendering">
          <div class="assessmentItem">
            <xsl:if test="$displayTitle">
              <h1 class="itemTitle"><xsl:value-of select="@title"/></h1>
            </xsl:if>
            <div class="itemBody">
              <!-- Descend into itemBody only -->
              <xsl:apply-templates select="qti:itemBody"/>

              <!-- Display active modal feedback (only after responseProcessing) -->
              <xsl:if test="$isResponded">
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
                  <hr/>
                  <h2>Feedback</h2>
                  <xsl:sequence select="$modalFeedback"/>
                </xsl:if>
              </xsl:if>
            </div>
          </div>
          <!-- Optional debugging information -->
          <xsl:if test="$showInternalState">
            <div id="debug_panel">
              <xsl:call-template name="internalState"/>
            </div>
          </xsl:if>
        </div>
       </body>
    </html>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="qti:itemBody">
    <div id="itemBody">
      <form method="post"
        onsubmit="return QtiWorks.submit()" enctype="multipart/form-data"
        onreset="QtiWorks.reset()" autocomplete="off">
        <xsl:apply-templates/>
        <xsl:if test="$displayControls">
          <div class="controls">
            <input type="reset" value="RESET"/>
            <input id="submit_button" name="submit" type="submit" value="SUBMIT ANSWER"/>
          </div>
        </xsl:if>
      </form>
    </div>
  </xsl:template>

</xsl:stylesheet>
