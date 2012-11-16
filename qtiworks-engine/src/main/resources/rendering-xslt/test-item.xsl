<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders an AssessmentItem within an AssessmentTest, as seen by candidates.

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

  <xsl:import href="test-common.xsl"/>
  <xsl:import href="item-common.xsl"/>
  <xsl:import href="serialize.xsl"/>
  <xsl:import href="utils.xsl"/>

  <!-- State -->
  <xsl:param name="renderingMode" as="xs:string" required="yes"/>
  <xsl:variable name="isSessionClosed" as="xs:boolean" select="$itemSessionState/@closed='true'"/>
  <xsl:variable name="isSessionInteracting" as="xs:boolean" select="not($isSessionClosed)"/>

  <!-- Effective value of itemSessionControl/@showFeedback for this itme -->
  <xsl:param name="showFeedback" as="xs:boolean"/>

  <xsl:variable name="feedbackAllowed" as="xs:boolean"
    select="if ($renderingMode='REVIEW') then (/qti:assessentItem/@adaptive='true' or $showFeedback) else true()"/>

  <!-- FIXME: This is not currently passed -->
  <xsl:param name="rubric" as="element(qw:section)*"/> <!-- sequence of grouped rubric blocks -->

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
      <hello>
        <xsl:copy-of select="$assessmentTest"/>
      </hello>
      <head>
        <title><xsl:value-of select="@title"/></title>
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"/>
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"/>
        <script src="{$webappContextPath}/rendering/javascript/QtiWorksRendering.js" type="text/javascript"/>
        <xsl:if test="$authorMode">
          <script src="{$webappContextPath}/rendering/javascript/AuthorMode.js" type="text/javascript"/>
        </xsl:if>

        <!-- Timer setup (requires controls to be displayed) -->
        <!-- HAS NOT BEEN PORTED OVER YET
        <xsl:if test="$displayControls and $timeRemaining >= 0">
          <script src="{$webappContextPath}/Jscript/TimeLimit.js" type="text/javascript"/>
          <script type="text/javascript">
            $(document).ready(function() {
              initTimer('<xsl:value-of select="$timeRemaining"/>');
            });
          </script>
        </xsl:if>
        -->

        <!--
        Import ASCIIMathML stuff if there are any MathEntryInteractions in the question.
        (It would be quite nice if we could allow each interaction to hook into this
        part of the result generation directly.)
        -->
        <xsl:if test="$containsMathEntryInteraction">
          <script src="{$webappContextPath}/rendering/javascript/UpConversionAjaxController.js" type="text/javascript"/>
          <script src="{$webappContextPath}/rendering/javascript/AsciiMathInputController.js" type="text/javascript"/>
          <script type="text/javascript">
            UpConversionAjaxController.setUpConversionServiceUrl('<xsl:value-of select="$webappContextPath"/>/candidate/verifyAsciiMath');
            UpConversionAjaxController.setDelay(300);
          </script>
        </xsl:if>

        <!-- Styling for JQuery -->
        <link rel="stylesheet" type="text/css" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/redmond/jquery-ui.css"/>

        <!-- QTIWorks Item styling -->
        <link rel="stylesheet" href="{$webappContextPath}/rendering/css/item.css" type="text/css" media="screen"/>
      </head>
      <body class="qtiworks assessmentItem assessmentTest">

        <!-- Item title -->
        <h1 class="itemTitle">
          <!-- FIXME: This isn't very nice! -->
          <xsl:choose>
            <xsl:when test="$renderingMode='REVIEW'">
              <div class="itemStatus review">Review</div>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="$itemSessionState" mode="item-status"/>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:value-of select="@title"/>
        </h1>

        <!-- (test only) rubric -->
        <xsl:if test="exists($rubric)">
          <xsl:call-template name="qw:rubric"/>
        </xsl:if>

        <!-- Item body -->
        <xsl:apply-templates select="qti:itemBody"/>

        <!-- Display active modal feedback (only after responseProcessing) -->
        <xsl:if test="$feedbackAllowed and $sessionStatus='final'">
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
        <xsl:call-template name="qw:item-controls"/>

        <!-- Test Session control -->
        <xsl:call-template name="qw:test-controls"/>
      </body>
    </html>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="qti:itemBody">
    <div id="itemBody">
      <form method="post" action="{$webappContextPath}{$attemptUrl}"
        onsubmit="return QtiWorksRendering.submit()" enctype="multipart/form-data"
        onreset="QtiWorksRendering.reset()" autocomplete="off">

        <xsl:apply-templates/>

        <!-- FIXME: These are copied from item; might not be right here -->
        <xsl:if test="$isSessionInteracting">
          <div class="controls">
            <input id="submit_button" name="submit" type="submit" value="SUBMIT ANSWER"/>
          </div>
        </xsl:if>
      </form>
    </div>
  </xsl:template>

  <!-- Override using $showFeedback -->
  <xsl:template match="qti:feedbackInline | qti:feedbackBlock">
    <xsl:if test="$feedbackAllowed">
      <xsl:apply-imports/>
    </xsl:if>
  </xsl:template>

  <!-- Disable any buttons in the question (from endAttemptInteraction) if not in interacting state -->
  <xsl:template match="qti:endAttemptInteraction[not($isSessionInteracting)]">
    <input type="submit" name="{@responseIdentifier}" value="{@title}" disabled="disabled"/>
  </xsl:template>

  <!-- ************************************************************ -->

  <!-- FIXME: Needs refactored -->
  <xsl:template name="qw:rubric">
    <div class="outer-box">
      <span class="box-title">Rubric</span>
      <div class="inner-box">
        <xsl:for-each select="$rubric">
          <div>
            <xsl:apply-templates select="qti:rubricBlock"/>
          </div>
        </xsl:for-each>
      </div>
    </div>
    <div class="spacer">&#160;</div>
  </xsl:template>

</xsl:stylesheet>
