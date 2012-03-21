<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders a standalone assessmentItem

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:m="http://www.w3.org/1998/Math/MathML"
  xmlns:jqti="http://jqti.qtitools.org"
  xmlns="http://www.w3.org/1999/xhtml"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xs qti jqti m">

  <xsl:import href="qti-common.xsl"/>
  <xsl:import href="serialize.xsl"/>
  <xsl:import href="utils.xsl"/>

  <!-- Import stylesheet to fix-up QTI 2.0 questions before processing -->
  <xsl:import href="qti20-fixer.xsl"/>

  <xsl:param name="articleId" as="xs:string?"/>
  <xsl:param name="queryString" as="xs:string?"/>
  <xsl:param name="validation" select="()" as="xs:string?"/>

  <xsl:param name="displayTitle" select="true()" as="xs:boolean"/>
  <xsl:param name="displayControls" select="true()" as="xs:boolean"/>
  <xsl:param name="embedded" select="false()" as="xs:boolean"/>

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

        <script src="{$engineBasePath}/rendering/javascript/JQTIItemRendering.js" type="text/javascript"/>
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

        <!-- Item styling -->
        <link rel="stylesheet" href="{$engineBasePath}/css/item.css" type="text/css" media="screen"/>
        <xsl:apply-templates select="qti:stylesheet"/>

        <!-- MathAssessEngine template styling -->
        <xsl:if test="not($embedded)">
          <link rel="stylesheet" href="{$engineBasePath}/css/item-layout.css" type="text/css" media="screen"/>
        </xsl:if>
      </head>
      <body class="mathassessengine qtiengine">
        <xsl:if test="not($embedded)">
          <div class="logo">
            <img src="{$engineBasePath}/images/mathassessengine.png" alt="MathAssessEngine"/>
          </div>
          <div class="nav">
            <span class="menuButton"><a class="home" href="{$engineBasePath}">Home</a></span>
            <span class="menuButton"><a class="list" href="{$engineBasePath}/article/list">Item &amp; Test list</a></span>
            <span class="menuButton"><a class="reset" href="{$engineBasePath}/article/reset/{$articleId}{if ($queryString) then concat('?', $queryString) else ''}">Reset and replay</a></span>
            <span class="menuButton"><a class="source" href="{$engineBasePath}/article/source/{$articleId}">View source XML</a></span>
          </div>
        </xsl:if>
        <div class="renderingBody">
          <div class="qtiengine mathassessengine item">
            <xsl:if test="$validation">
              <pre class="validation">
                <xsl:value-of select="$validation"/>
              </pre>
            </xsl:if>
            <div class="item">
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
          </div>
        </div>
        <!-- Optional debugging information -->
        <xsl:if test="not($embedded) and $showInternalState">
          <div id="debug_panel">
            <xsl:call-template name="internalState"/>
          </div>
        </xsl:if>
       </body>
    </html>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="qti:itemBody">
    <div id="itemBody">
      <form method="post"
        onsubmit="return JQTIItemRendering.submit()" enctype="multipart/form-data"
        onreset="JQTIItemRendering.reset()" autocomplete="off">
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
