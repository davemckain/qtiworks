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

  <!-- ************************************************************ -->

  <xsl:import href="qti-common.xsl"/>
  <xsl:import href="serialize.xsl"/>
  <xsl:import href="utils.xsl"/>

  <!-- State -->
  <xsl:param name="candidateSessionState" as="xs:string" required="yes"/>
  <xsl:param name="renderingMode" as="xs:string" required="yes"/>
  <xsl:variable name="isSessionInteracting" as="xs:boolean" select="$candidateSessionState='INTERACTING'"/>
  <xsl:variable name="isSessionClosed" as="xs:boolean" select="$candidateSessionState='CLOSED'"/>

  <xsl:param name="prompt" select="()" as="xs:string?"/>
  <xsl:param name="authorMode" as="xs:boolean" required="yes"/>

  <!-- Item Action URLs -->
  <xsl:param name="attemptUrl" as="xs:string" required="yes"/>
  <xsl:param name="resetUrl" as="xs:string" required="yes"/>
  <xsl:param name="reinitUrl" as="xs:string" required="yes"/>
  <xsl:param name="closeUrl" as="xs:string" required="yes"/>
  <xsl:param name="solutionUrl" as="xs:string" required="yes"/>
  <xsl:param name="terminateUrl" as="xs:string" required="yes"/>
  <xsl:param name="sourceUrl" as="xs:string" required="yes"/>
  <xsl:param name="resultUrl" as="xs:string" required="yes"/>

  <!-- Action permissions -->
  <xsl:param name="closeAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="solutionAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="resetAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="reinitAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="sourceAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="resultAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="playbackAllowed" as="xs:boolean" required="yes"/>

  <!-- Playback -->
  <xsl:param name="playbackUrlBase" as="xs:string" required="yes"/>
  <xsl:param name="currentPlaybackEventId" select="()" as="xs:integer?"/>
  <xsl:param name="currentPlaybackEventType" select="()" as="xs:string?"/>
  <xsl:param name="playbackEventIds" select="()" as="xs:integer*"/>
  <xsl:param name="playbackEventTypes" select="()" as="xs:string*"/>

  <xsl:function name="qw:describe-candidate-event" as="xs:string">
    <xsl:param name="candidate-event-type" as="xs:string"/>
    <xsl:variable name="descriptions" as="element(qw:description)+">
      <qw:description type="INIT">Initial presentation of your assessment</qw:description>
      <qw:description type="ATTEMPT_VALID">Submission of an answer</qw:description>
      <qw:description type="ATTEMPT_INVALID">Submission of an answer that did not fit what was asked for</qw:description>
      <qw:description type="ATTEMPT_BAD">Submission of the wrong type of answer</qw:description>
      <qw:description type="REINIT">Re-initialisation of your assessment</qw:description>
      <qw:description type="RESET">Reset of your assessment</qw:description>
      <qw:description type="SOLUTION">Display of a model solution for this assessment</qw:description>
      <qw:description type="PLAYBACK">Playback of your assessment</qw:description>
      <qw:description type="CLOSE">Completion of your assessment</qw:description>
      <qw:description type="TERMINATE">Termination of your assessment</qw:description>
    </xsl:variable>
    <xsl:sequence select="$descriptions[@type=$candidate-event-type]/text()"/>
  </xsl:function>

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
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"/>
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js"/>
        <script src="{$webappContextPath}/rendering/javascript/QtiWorksRendering.js" type="text/javascript"/>
        <xsl:if test="$authorMode">
          <script src="{$webappContextPath}/rendering/javascript/AuthorMode.js" type="text/javascript"/>
        </xsl:if>
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
        <link rel="stylesheet" href="{$webappContextPath}/rendering/css/item.css" type="text/css" media="screen"/>

        <!-- Include stylesheet declared within item -->
        <xsl:apply-templates select="qti:stylesheet"/>
      </head>
      <body class="qtiworks assessmentItem">
        <xsl:choose>
          <xsl:when test="$candidateSessionState='TERMINATED'">
            <p>
              This assessment session is no longer available to you.
            </p>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="." mode="rendering-allowed"/>
          </xsl:otherwise>
        </xsl:choose>
       </body>
    </html>
  </xsl:template>

  <!-- Renders the body content of the item -->
  <xsl:template match="qti:assessmentItem" mode="rendering-allowed">
    <!-- Author mode note (maybe) -->
    <xsl:if test="$authorMode">
      <div class="authorModeNote authorMode">
        <p>
          You are currently running this item in "author" mode, which shows extra information
          that would not normally be shown to candidates.
        </p>
      </div>
      <script type="text/javascript"><![CDATA[
        AuthorMode.setupAuthorModeToggler();
      ]]></script>
    </xsl:if>

    <h1 class="itemTitle"><xsl:value-of select="@title"/></h1>

    <!-- Delivery prompt -->
    <xsl:if test="$prompt">
      <div class="itemPrompt">
        <xsl:value-of select="$prompt"/>
      </div>
    </xsl:if>

    <!-- Candidate status -->
    <xsl:if test="$renderingMode=('SOLUTION', 'CLOSED', 'PLAYBACK')">
      <div class="candidateStatus">
        <xsl:choose>
          <xsl:when test="$renderingMode='SOLUTION'">
            A model solution to this assessment is shown below.
          </xsl:when>
          <xsl:when test="$renderingMode='CLOSED'">
            This assessment is now complete.
          </xsl:when>
          <xsl:when test="$renderingMode='PLAYBACK'">
            <p>
              You are currently playing back your interaction with this assessment.
            </p>
            <p>
              Currently showing: <xsl:value-of select="qw:describe-candidate-event($currentPlaybackEventType)"/>
              (Event #<xsl:value-of select="for $i in 1 to count($playbackEventIds), $id in $playbackEventIds[$i]
                return if ($id = $currentPlaybackEventId) then $i else ()"/>
              of <xsl:value-of select="count($playbackEventIds)"/>)
            </p>
            <ul class="playbackControls">
              <xsl:for-each select="$playbackEventIds">
                <xsl:variable name="eventIndex" select="position()" as="xs:integer"/>
                <li>
                  <form action="{$webappContextPath}{$playbackUrlBase}/{.}" method="post">
                    <input type="submit" value="Play back event #{$eventIndex} ({qw:describe-candidate-event($playbackEventTypes[$eventIndex])})"/>
                  </form>
                </li>
              </xsl:for-each>
            </ul>
          </xsl:when>
        </xsl:choose>
      </div>
    </xsl:if>

    <!-- Item body -->
    <xsl:apply-templates select="qti:itemBody"/>

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

    <!-- Session control -->
    <div class="sessionControl">
      <xsl:if test="$authorMode">
        <div class="authorMode sessionControl">
          The candidate currently has the following "session control" options. You can choose
          exactly which options are available via the "item delivery". (You will be able
          to do this soon!)
        </div>
      </xsl:if>
      <ul class="controls">
        <xsl:if test="$resetAllowed">
          <li>
            <form action="{$webappContextPath}{$resetUrl}" method="post">
              <input type="submit" value="Reset{if ($isSessionClosed) then ' and play again' else ''}"/>
            </form>
          </li>
        </xsl:if>
        <xsl:if test="$reinitAllowed">
          <li>
            <form action="{$webappContextPath}{$reinitUrl}" method="post">
              <input type="submit" value="Reinitialise{if ($isSessionClosed) then ' and play again' else ''}"/>
            </form>
          </li>
        </xsl:if>
        <xsl:if test="$closeAllowed">
          <li>
            <form action="{$webappContextPath}{$closeUrl}" method="post">
              <input type="submit" value="Finish and review"/>
            </form>
          </li>
        </xsl:if>
        <xsl:if test="$playbackAllowed and exists($playbackEventIds) and $renderingMode!='PLAYBACK'">
          <li>
            <form action="{$webappContextPath}{$playbackUrlBase}/{$playbackEventIds[1]}" method="post">
              <input type="submit" value="Enter playback mode"/>
            </form>
          </li>
        </xsl:if>
        <xsl:if test="$solutionAllowed">
          <li>
            <form action="{$webappContextPath}{$solutionUrl}" method="post">
              <input type="submit" value="Show model solution"/>
            </form>
          </li>
        </xsl:if>
      </ul>
      <ul class="controls">
        <xsl:if test="$resultAllowed">
          <li>
            <form action="{$webappContextPath}{$resultUrl}" method="get" class="showXmlInDialog" title="Item Result XML">
              <input type="submit" value="View ItemResult"/>
            </form>
          </li>
        </xsl:if>
        <xsl:if test="$sourceAllowed">
          <li>
            <form action="{$webappContextPath}{$sourceUrl}" method="get" class="showXmlInDialog" title="Item Source XML">
              <input type="submit" value="View Item source"/>
            </form>
          </li>
        </xsl:if>
        <li>
          <form action="{$webappContextPath}{$terminateUrl}" method="post">
            <input type="submit" value="Terminate session"/>
          </form>
        </li>
      </ul>
    </div>

    <xsl:if test="$authorMode">
      <div class="authorInfo authorMode">
        <h2>QTI state information</h2>
        <h3>Candidate Session State</h3>
        <p>Current candidate session state is:</p>
        <ul>
          <li>Primary: <xsl:value-of select="$candidateSessionState"/></li>
          <li>Secondary: <xsl:value-of select="$renderingMode"/></li>
        </ul>

        <xsl:if test="exists($badResponseIdentifiers) or exists($invalidResponseIdentifiers)">
          <h3>Response errors</h3>
          <xsl:if test="exists($badResponseIdentifiers)">
            <h4>Bad responses</h4>
            <p>
              The responses listed below were not successfully bound to their corresponding variables.
              This might happen, for example, if you bind a <code>&lt;textEntryInteraction&gt;</code> to
              a numeric variable and the candidate enters something that is not a number.
            </p>
            <ul>
              <xsl:for-each select="$badResponseIdentifiers">
                <li>
                  <span class="variableName">
                    <xsl:value-of select="."/>
                  </span>
                </li>
              </xsl:for-each>
            </ul>
          </xsl:if>
          <xsl:if test="exists($invalidResponseIdentifiers)">
            <h4>Invalid responses</h4>
            <p>
              The responses were successfully bound to their corresponding variables,
              but failed to satisfy the constraints specified by their corresponding interactions:
            </p>
            <ul>
              <xsl:for-each select="$invalidResponseIdentifiers">
                <li>
                  <span class="variableName">
                    <xsl:value-of select="."/>
                  </span>
                </li>
              </xsl:for-each>
            </ul>
          </xsl:if>
        </xsl:if>
        <h3>Item Session State</h3>
        <xsl:call-template name="sessionState"/>

        <h2>Delivery settings</h2>
        <p>
          Candidate is currently allowed to:
        </p>
        <dl>
          <dt>Reset session?</dt>
          <dd><xsl:value-of select="$resetAllowed"/></dd>

          <dt>Reinitialize session?</dt>
          <dd><xsl:value-of select="$reinitAllowed"/></dd>

          <dt>Close session?</dt>
          <dd><xsl:value-of select="$closeAllowed"/></dd>

          <dt>View solution?</dt>
          <dd><xsl:value-of select="$solutionAllowed"/></dd>

          <dt>View item source XML?</dt>
          <dd><xsl:value-of select="$sourceAllowed"/></dd>

          <dt>View <code>&lt;itemResult&gt;</code> XML?</dt>
          <dd><xsl:value-of select="$resultAllowed"/></dd>

          <dt>Play back actions?</dt>
          <dd><xsl:value-of select="$playbackAllowed"/></dd>
        </dl>
      </div>
    </xsl:if>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="qti:itemBody">
    <div id="itemBody">
      <form method="post" action="{$webappContextPath}{$attemptUrl}"
        onsubmit="return QtiWorksRendering.submit()" enctype="multipart/form-data"
        onreset="QtiWorksRendering.reset()" autocomplete="off">

        <xsl:apply-templates/>

        <xsl:if test="$isSessionInteracting">
          <div class="controls">
            <input id="submit_button" name="submit" type="submit" value="SUBMIT ANSWER"/>
          </div>
        </xsl:if>
      </form>
    </div>
  </xsl:template>

</xsl:stylesheet>
