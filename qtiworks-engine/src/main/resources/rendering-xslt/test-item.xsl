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

  <xsl:param name="queryString" as="xs:string?"/>
  <xsl:param name="isResponded" select="false()" as="xs:boolean"/>

  <!-- (These are set in qti.controller.TestCoordinator) -->
  <xsl:param name="rubric" as="element(qw:section)*"/> <!-- sequence of grouped rubric blocks -->

  <!-- Below are all deprecated -->
  <xsl:param name="previousEnabled" select="false()" as="xs:boolean"/>
  <xsl:param name="backwardEnabled" select="false()" as="xs:boolean"/>
  <xsl:param name="nextEnabled" select="false()" as="xs:boolean"/>
  <xsl:param name="forwardEnabled" select="false()" as="xs:boolean"/>
  <xsl:param name="submitEnabled" select="true()" as="xs:boolean"/>
  <xsl:param name="skipEnabled" select="false()" as="xs:boolean"/>
  <xsl:param name="allowCandidateComment" select="false()" as="xs:boolean"/>
  <xsl:param name="flash" as="xs:string?"/>
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
        <h1 class="itemTitle"><xsl:value-of select="@title"/></h1>

        <!-- (test only) rubric -->
        <xsl:if test="exists($rubric)">
          <xsl:call-template name="qw:rubric"/>
        </xsl:if>

        <!-- Item body -->
        <xsl:apply-templates select="qti:itemBody"/>

        <!-- Display active modal feedback (only after responseProcessing) -->
        <xsl:if test="$sessionStatus='final'">
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

  <!-- ************************************************************ -->

  <xsl:template name="sessionState" as="element()+">
    <div class="sessionState">
      <xsl:if test="exists($shuffledChoiceOrders)">
        <h4>Shuffled choice orders</h4>
        <ul>
          <xsl:for-each select="$shuffledChoiceOrders">
            <li>
              <span class="variableName">
                <xsl:value-of select="@responseIdentifier"/>
              </span>
              <xsl:text> = [</xsl:text>
              <xsl:value-of select="tokenize(@choiceSequence, ' ')" separator=", "/>
              <xsl:text>]</xsl:text>
            </li>
          </xsl:for-each>
        </ul>
      </xsl:if>
      <xsl:if test="exists($templateValues)">
        <h4>Template variables</h4>
        <xsl:call-template name="dump-values">
          <xsl:with-param name="valueHolders" select="$templateValues"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="exists($badResponseIdentifiers)">
        <h4>Unbound Response inputs</h4>
        <xsl:call-template name="dump-unbound-response-inputs"/>
      </xsl:if>
      <xsl:if test="exists($responseValues)">
        <h4>Response variables</h4>
        <xsl:call-template name="dump-values">
          <xsl:with-param name="valueHolders" select="$responseValues"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="exists($outcomeValues)">
        <h4>Outcome variables</h4>
        <xsl:call-template name="dump-values">
          <xsl:with-param name="valueHolders" select="$outcomeValues"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="exists($testOutcomeValues)">
        <h4>Test Outcome variables</h4>
        <xsl:call-template name="dump-values">
          <xsl:with-param name="valueHolders" select="$testOutcomeValues"/>
        </xsl:call-template>
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template name="dump-values" as="element(ul)">
    <xsl:param name="valueHolders" as="element()*"/>
    <ul>
      <xsl:for-each select="$valueHolders">
        <xsl:call-template name="dump-value">
          <xsl:with-param name="valueHolder" select="."/>
        </xsl:call-template>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template name="dump-value" as="element(li)">
    <xsl:param name="valueHolder" as="element()"/>
    <li>
      <span class="variableName">
        <xsl:value-of select="@identifier"/>
      </span>
      <xsl:text> = </xsl:text>
      <xsl:choose>
        <xsl:when test="not(*)">
          <xsl:text>NULL</xsl:text>
        </xsl:when>
        <xsl:when test="qw:is-maths-content-value($valueHolder)">
          <!-- We'll handle MathsContent variables specially to help question authors -->
          <span class="type">MathsContent :: </span>
          <xsl:copy-of select="qw:extract-maths-content-pmathml($valueHolder)"/>

          <!-- Make the raw record fields available via a toggle -->
          <xsl:text> </xsl:text>
          <a id="qtiworks_id_toggle_debugMathsContent_{@identifier}" class="debugButton"
            href="javascript:void(0)">Toggle Details</a>
          <div id="qtiworks_id_debugMathsContent_{@identifier}" class="debugMathsContent">
            <xsl:call-template name="dump-record-entries">
              <xsl:with-param name="valueHolders" select="$valueHolder/qw:value"/>
            </xsl:call-template>
          </div>
          <script type="text/javascript">
            $(document).ready(function() {
              $('a#qtiworks_id_toggle_debugMathsContent_<xsl:value-of select="@identifier"/>').click(function() {
                $('#qtiworks_id_debugMathsContent_<xsl:value-of select="@identifier"/>').toggle();
              })
            });
          </script>
        </xsl:when>
        <xsl:otherwise>
          <!-- Other variables will be output in a fairly generic way -->
          <span class="type">
            <xsl:value-of select="(@cardinality, @baseType, ':: ')" separator=" "/>
          </span>
          <xsl:choose>
            <xsl:when test="@cardinality='single'">
              <xsl:variable name="text" select="$valueHolder/qw:value" as="xs:string"/>
              <xsl:choose>
                <xsl:when test="contains($text, '&#x0a;')">
                  <pre><xsl:value-of select="$text"/></pre>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$text"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:when>
            <xsl:when test="@cardinality='multiple'">
              <xsl:text>{</xsl:text>
              <xsl:value-of select="$valueHolder/qw:value" separator=", "/>
              <xsl:text>}</xsl:text>
            </xsl:when>
            <xsl:when test="@cardinality='ordered'">
              <xsl:text>[</xsl:text>
              <xsl:value-of select="$valueHolder/qw:value" separator=", "/>
              <xsl:text>]</xsl:text>
            </xsl:when>
            <xsl:when test="@cardinality='record'">
              <xsl:text>(</xsl:text>
              <xsl:call-template name="dump-record-entries">
                <xsl:with-param name="valueHolders" select="$valueHolder/qw:value"/>
              </xsl:call-template>
              <xsl:text>)</xsl:text>
            </xsl:when>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </li>
  </xsl:template>

  <xsl:template name="dump-record-entries" as="element(ul)">
    <xsl:param name="valueHolders" as="element()*"/>
    <ul>
      <xsl:for-each select="$valueHolders">
        <li>
          <span class="variableName">
            <xsl:value-of select="@fieldIdentifier"/>
          </span>
          <xsl:text> = </xsl:text>
          <xsl:choose>
            <xsl:when test="not(*)">
              <xsl:text>NULL</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <!-- Other variables will be output in a fairly generic way -->
              <span class="type">
                <xsl:value-of select="(@baseType, ':: ')" separator=" "/>
              </span>
              <xsl:variable name="text" select="qw:value" as="xs:string"/>
              <xsl:choose>
                <xsl:when test="contains($text, '&#x0a;')">
                  <pre><xsl:value-of select="$text"/></pre>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$text"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </li>
      </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template name="dump-unbound-response-inputs" as="element(ul)">
    <ul>
      <xsl:for-each select="$responseInputs[@identifier = $badResponseIdentifiers]">
        <li>
          <span class="variableName">
            <xsl:value-of select="@identifier"/>
          </span>
          <xsl:text> = </xsl:text>
          <xsl:choose>
            <xsl:when test="@type='string'">
              <xsl:text>[</xsl:text>
              <xsl:value-of select="qw:value" separator=", "/>
              <xsl:text>]</xsl:text>
            </xsl:when>
            <xsl:when test="@type='file'">
              (Uploaded file)
            </xsl:when>
          </xsl:choose>
        </li>
      </xsl:for-each>
    </ul>
  </xsl:template>



</xsl:stylesheet>
