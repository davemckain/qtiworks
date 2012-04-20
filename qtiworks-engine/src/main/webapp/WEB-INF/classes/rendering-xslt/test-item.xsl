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

  <xsl:import href="qti-common.xsl"/>
  <xsl:import href="serialize.xsl"/>
  <xsl:import href="utils.xsl"/>

  <!-- Import stylesheet to fix-up QTI 2.0 questions before processing -->
  <xsl:import href="qti20-fixer.xsl"/>

  <xsl:param name="articleId" as="xs:string?"/>
  <xsl:param name="queryString" as="xs:string?"/>
  <xsl:param name="isResponded" select="false()" as="xs:boolean"/>
  <xsl:param name="title" select="''" as="xs:string"/> <!-- item title -->
  <xsl:param name="timeRemaining" select="-1" as="xs:integer"/> <!-- time remaining -->
  <xsl:param name="numberRemaining" select="-1" as="xs:integer"/> <!-- number of remaining items -->

  <!-- (These are set in qti.controller.TestCoordinator) -->
  <xsl:param name="questionId" select="'1'" as="xs:string"/>
  <xsl:param name="sectionTitles" as="xs:string*"/> <!-- sequence of section titles -->
  <xsl:param name="rubric" as="element(qw:section)*"/> <!-- sequence of grouped rubric blocks -->
  <xsl:param name="assessmentFeedback" as="element(qti:testFeedback)*"/> <!-- feedback as qti dom trees -->
  <xsl:param name="testPartFeedback" as="element(qti:testFeedback)*"/> <!-- feedback as qti dom trees -->

  <xsl:param name="previousEnabled" select="false()" as="xs:boolean"/>
  <xsl:param name="backwardEnabled" select="false()" as="xs:boolean"/>
  <xsl:param name="nextEnabled" select="false()" as="xs:boolean"/>
  <xsl:param name="forwardEnabled" select="false()" as="xs:boolean"/>
  <xsl:param name="submitEnabled" select="true()" as="xs:boolean"/>
  <xsl:param name="skipEnabled" select="false()" as="xs:boolean"/>
  <xsl:param name="allowCandidateComment" select="false()" as="xs:boolean"/>
  <xsl:param name="view" select="false()" as="xs:boolean"/> <!-- view mode (unset shows everthing) -->
  <xsl:param name="flash" as="xs:string?"/>

  <xsl:param name="displayTitle" select="true()" as="xs:boolean"/>
  <xsl:param name="displayControls" select="true()" as="xs:boolean"/>
  <xsl:param name="exitButton" select="true()" as="xs:boolean"/>
  <xsl:param name="reportButton" select="true()" as="xs:boolean"/>

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
        <title><xsl:value-of select="concat($title, ' :: ', @title)"/></title>

        <script src="{$webappContextPath}/rendering/javascript/QtiWorks.js" type="text/javascript"/>
        <!-- The following are used for certain interactions and time limits -->
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"/>
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"/>

        <!-- Timer setup (requires controls to be displayed) -->
        <xsl:if test="$displayControls and $timeRemaining >= 0">
          <script src="{$webappContextPath}/Jscript/TimeLimit.js" type="text/javascript"/>
          <script type="text/javascript">
            $(document).ready(function() {
              initTimer('<xsl:value-of select="$timeRemaining"/>');
            });
          </script>
        </xsl:if>

        <!--
        Import ASCIIMathML stuff if there are any MathEntryInteractions in the question.
        (It would be quite nice if we could allow each interaction to hook into this
        part of the result generation directly.)
        -->
        <xsl:if test="$containsMathEntryInteraction">
          <script src="{$webappContextPath}/rendering/javascript/UpConversionAJAXController.js" type="text/javascript"/>
          <script src="{$webappContextPath}/rendering/javascript/ASCIIMathInputController.js" type="text/javascript"/>
          <script type="text/javascript">
            UpConversionAJAXController.setUpConversionServiceUrl('<xsl:value-of select="$webappContextPath"/>/input/verifyASCIIMath');
            UpConversionAJAXController.setDelay(300);
          </script>
        </xsl:if>

        <!-- Styling for JQuery dialog -->
        <link rel="stylesheet" type="text/css" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/redmond/jquery-ui.css"/>

        <!-- Stylesheet(s) for this item -->
        <link rel="stylesheet" href="{$webappContextPath}/css/item-rendering.css" type="text/css" media="screen"/>
        <xsl:apply-templates select="qti:stylesheet"/>

        <!-- Test styling -->
        <link rel="stylesheet" href="{$webappContextPath}/css/test-rendering.css" type="text/css" media="screen"/>
      </head>
      <body>
        <div class="qtiworksRendering">
          <div class="assessmentTest">
            <!-- TODO: Not sure this is the optimal place for this now? -->
            <xsl:if test="$displayTitle">
              <h1 title="{$itemHref}">
                <xsl:value-of select="@title"/>
              </h1>
            </xsl:if>

            <xsl:if test="exists($flash)">
              <div class="flash">
                <xsl:value-of select="$flash"/>
              </div>
            </xsl:if>

            <div id="navbar">
              <xsl:if test="$displayControls and $timeRemaining >= 0">
                <div id="controls">
                  Time to complete <b><xsl:value-of select="$numberRemaining"/></b> remaining items is <b id="timer">...</b>.
                </div>
              </xsl:if>
            </div>

            <div id="body">
              <div id="body-container">
                <!-- (test only) titles -->
                <xsl:if test="exists($title)">
                  <h1><xsl:value-of select="$title"/></h1>
                </xsl:if>
                <xsl:if test="exists($sectionTitles)">
                  <h2>
                    <xsl:for-each select="$sectionTitles">
                      <xsl:value-of select="."/>
                      <br />
                    </xsl:for-each>
                  </h2>
                </xsl:if>

                <!-- (test only) rubric -->
                <xsl:if test="exists($rubric)">
                  <xsl:call-template name="rubric"/>
                </xsl:if>

                <xsl:choose>
                  <xsl:when test="qti:itemBody">
                    <!-- Render itemBody -->
                    <xsl:apply-templates select="qti:itemBody"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <!-- DM-TODO: Maybe have a separate stylesheet if we're not actually rendering anything? -->
                    <!-- End of test. Just render any feedback -->
                    <xsl:if test="exists($testPartFeedback) or exists($assessmentFeedback)">
                      <xsl:call-template name="test-feedback"/>
                    </xsl:if>

                    <p>This assessment is now complete.</p>
                    <xsl:if test="$reportButton">
                      <p><a href="#" onclick="window.open('?report=yes', 'report');">Click here to view the test report</a></p>
                    </xsl:if>
                    <xsl:if test="$exitButton">
                      <p><a href="?exit=yes">Click here to exit this test</a></p>
                    </xsl:if>
                  </xsl:otherwise>
                </xsl:choose>

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
        <xsl:if test="$showInternalState">
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
        onsubmit="return QtiWorks.submit()" enctype="multipart/form-data"
        onreset="QtiWorks.reset()" autocomplete="off">
        <div class="outer-box">
          <input type="hidden" name="questionId" value="{$questionId}"/>
          <span class="box-title">Question</span>
          <div class="inner-box">

            <!-- Do QTI body as usual -->
            <xsl:apply-templates/>
          </div>
        </div>
        <div class="spacer">&#160;</div>

        <!-- feedback goes here -->
        <xsl:if test="exists($testPartFeedback) or exists($assessmentFeedback)">
          <xsl:call-template name="test-feedback"/>
        </xsl:if>

        <!-- add controls. (NB: logic here is slightly different from QTIEngine) -->
        <xsl:if test="$displayControls">
          <xsl:call-template name="controls"/>
        </xsl:if>
      </form>
    </div>
  </xsl:template>

  <xsl:template name="test-feedback">
    <div class="outer-box">
      <span class="box-title">Feedback</span>
      <div class="inner-box">
        <xsl:if test="exists($assessmentFeedback)">
          <xsl:for-each select="$assessmentFeedback">
            <div class="feedback_title"><xsl:value-of select="@title"/></div>
            <xsl:apply-templates select="*"/>
          </xsl:for-each>
        </xsl:if>

        <xsl:if test="exists($testPartFeedback)">
          <xsl:for-each select="$testPartFeedback">
            <div class="feedback_title"><xsl:value-of select="@title"/></div>
            <xsl:apply-templates select="*"/>
          </xsl:for-each>
        </xsl:if>
      </div>
    </div>
    <div class="spacer">&#160;</div>
  </xsl:template>

  <xsl:template name="rubric">
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

  <xsl:template name="controls">
    <div class="outer-box">
      <span class="box-title">Controls</span>
      <div class="inner-box">
        <table width="100%">
          <xsl:if test="$allowCandidateComment">
            <tr>
              <td colspan="3">
                Candidate Comments: <br />
                <textarea name="candidateComment" rows="4" style="width: 100%;"><xsl:text> </xsl:text></textarea>
              </td>
            </tr>
          </xsl:if>
          <tr>
            <td align="left" width="25%">
              <xsl:if test="$previousEnabled">
                <input type="submit" name="previous" id="previous" value="Previous"/>
              </xsl:if>
            </td>
            <td align="center" width="50%">
              <input type="reset" value="Clear">
                <xsl:if test="not($submitEnabled)">
                  <xsl:attribute name="disabled" select="'disabled'"/>
                </xsl:if>
              </input>

              <xsl:if test="$skipEnabled">
                <input type="submit" name="skip" id="Skip" value="Skip"/>
              </xsl:if>

              <input type="submit" name="submit" id="submit_button" value="Submit answer">
                <xsl:if test="not($submitEnabled)">
                  <xsl:attribute name="disabled" select="'disabled'"/>
                </xsl:if>
              </input>
            </td>
            <td align="right">
              <xsl:if test="$nextEnabled">
                <input type="submit" name="next" id="next" value="Next"/>
              </xsl:if>
            </td>
          </tr>
          <tr>
            <td align="left">
              <xsl:if test="$backwardEnabled">
                <input type="submit" name="backward" id="backward" value="Backward"/>
              </xsl:if>
            </td>
            <td align="center">
              <xsl:if test="$exitButton">
                <input type="submit" name="exit" id="exit" value="Exit test" onclick="return confirm('Are you sure you want to end this test? All progress will be lost.')"/>
              </xsl:if>
              <xsl:if test="$reportButton">
                <input type="button" value="View report" onclick="window.open('?report=yes', 'report');"/>
              </xsl:if>
            </td>
            <td align="right">
              <xsl:if test="$forwardEnabled">
                <input type="submit" name="forward" id="forward" value="Forward"/>
              </xsl:if>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </xsl:template>

  <!-- disable any buttons in the question (from endAttemptInteraction) if the submit button is disabled -->
  <xsl:template match="qti:endAttemptInteraction[not($submitEnabled)]">
    <input type="submit" name="{@responseIdentifier}" value="{@title}" disabled="disabled"/>
  </xsl:template>

</xsl:stylesheet>
