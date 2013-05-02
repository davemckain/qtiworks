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

  <xsl:import href="qti-fallback.xsl"/>
  <xsl:import href="item-common.xsl"/>
  <xsl:import href="utils.xsl"/>

  <!-- Item prompt -->
  <xsl:param name="prompt" select="()" as="xs:string?"/>

  <!-- Action permissions -->
  <xsl:param name="endAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="solutionAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="softSoftResetAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="hardResetAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="sourceAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="resultAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="candidateCommentAllowed" as="xs:boolean" required="yes"/>

  <!-- Action URLs -->
  <xsl:param name="softResetUrl" as="xs:string" required="yes"/>
  <xsl:param name="hardResetUrl" as="xs:string" required="yes"/>
  <xsl:param name="endUrl" as="xs:string" required="yes"/>
  <xsl:param name="solutionUrl" as="xs:string" required="yes"/>
  <xsl:param name="exitUrl" as="xs:string" required="yes"/>

  <!-- ************************************************************ -->

  <!-- Item may be QTI 2.0 or 2.1, so we'll put a template in here to fix namespaces to QTI 2.1 -->
  <xsl:template match="/">
    <xsl:apply-templates select="qw:to-qti21(/)/*"/>
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
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"/>
        <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.1/jquery-ui.min.js"/>
        <script src="{$webappContextPath}/rendering/javascript/QtiWorksRendering.js?{$qtiWorksVersion}"/>
        <xsl:if test="$authorMode">
          <script src="{$webappContextPath}/rendering/javascript/AuthorMode.js?{$qtiWorksVersion}"/>
        </xsl:if>
        <!--
        Import ASCIIMathML stuff if there are any MathEntryInteractions in the question.
        (It would be quite nice if we could allow each interaction to hook into this
        part of the result generation directly.)
        -->
        <xsl:if test="$containsMathEntryInteraction">
          <script src="{$webappContextPath}/rendering/javascript/UpConversionAjaxController.js?{$qtiWorksVersion}"/>
          <script src="{$webappContextPath}/rendering/javascript/AsciiMathInputController.js?{$qtiWorksVersion}"/>
          <script>
            UpConversionAjaxController.setUpConversionServiceUrl('<xsl:value-of select="$webappContextPath"/>/candidate/verifyAsciiMath');
            UpConversionAjaxController.setDelay(300);
          </script>
        </xsl:if>

        <!-- Styling for JQuery -->
        <link rel="stylesheet" type="text/css" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.1/themes/redmond/jquery-ui.css"/>

        <!-- QTIWorks assessment styling -->
        <link rel="stylesheet" href="{$webappContextPath}/rendering/css/assessment.css?{$qtiWorksVersion}" type="text/css" media="screen"/>

        <!-- Include stylesheet declared within item -->
        <xsl:apply-templates select="qti:stylesheet"/>
      </head>
      <body class="qtiworks assessmentItem">
        <!-- Author mode note (maybe) -->
        <xsl:if test="$authorMode">
          <div class="authorModeNote authorMode">
            <p>
              You are currently running this item in "author" mode, which shows extra information
              that would not normally be shown to candidates.
            </p>
          </div>
          <script><![CDATA[
            AuthorMode.setupAuthorModeToggler();
          ]]></script>
        </xsl:if>

        <!-- Item title -->
        <h1 class="itemTitle">
          <xsl:apply-templates select="$itemSessionState" mode="item-status"/>
          <xsl:value-of select="@title"/>
        </h1>

        <!-- Delivery prompt -->
        <xsl:if test="$prompt">
          <div class="itemPrompt">
            <xsl:value-of select="$prompt"/>
          </div>
        </xsl:if>

        <!-- Candidate status -->
        <xsl:if test="$isItemSessionEnded">
          <div class="candidateStatus">
            <xsl:choose>
              <xsl:when test="$solutionMode">
                A model solution to this assessment is shown below.
              </xsl:when>
              <xsl:otherwise>
                This assessment is now complete.
              </xsl:otherwise>
            </xsl:choose>
          </div>
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
        <xsl:call-template name="qw:item-controls"/>
        <xsl:call-template name="qw:session-controls"/>

        <!-- Authoring feedback (maybe) -->
        <xsl:if test="$authorMode">
          <xsl:call-template name="qw:item-authoring-feedback"/>
        </xsl:if>
       </body>
    </html>
  </xsl:template>

  <xsl:template name="qw:item-controls">
    <div class="sessionControl">
      <xsl:if test="$authorMode">
        <div class="authorMode">
          The candidate currently has the following options for this item.
          You can choose exactly which options are available via the "item delivery".
        </div>
      </xsl:if>
      <ul class="controls">
        <xsl:if test="$softSoftResetAllowed">
          <li>
            <form action="{$webappContextPath}{$softResetUrl}" method="post">
              <input type="submit" value="Reset{if ($isItemSessionEnded) then ' and play again' else ''}"/>
            </form>
          </li>
        </xsl:if>
        <xsl:if test="$hardResetAllowed">
          <li>
            <form action="{$webappContextPath}{$hardResetUrl}" method="post">
              <input type="submit" value="Reinitialise{if ($isItemSessionEnded) then ' and play again' else ''}"/>
            </form>
          </li>
        </xsl:if>
        <xsl:if test="$endAllowed">
          <li>
            <form action="{$webappContextPath}{$endUrl}" method="post">
              <input type="submit" value="Finish and review"/>
            </form>
          </li>
        </xsl:if>
        <xsl:if test="$solutionAllowed and $hasModelSolution">
          <li>
            <form action="{$webappContextPath}{$solutionUrl}" method="post">
              <input type="submit" value="Show model solution">
                <xsl:if test="$solutionMode">
                  <!-- Already in solution mode -->
                  <xsl:attribute name="disabled" select="'disabled'"/>
                </xsl:if>
              </input>
            </form>
          </li>
        </xsl:if>
      </ul>
    </div>
  </xsl:template>

  <xsl:template name="qw:session-controls">
    <div class="sessionControl">
      <xsl:if test="$authorMode">
        <div class="authorMode">
          The candidate currently has the following options for this session.
          You can choose exactly which options are available via your Item Delivery Settings.
        </div>
      </xsl:if>
      <ul class="controls">
        <xsl:if test="$resultAllowed">
          <li>
            <form action="{$webappContextPath}{$resultUrl}" method="get" class="showXmlInDialog" title="Item Result XML">
              <input type="submit" value="View &lt;assessmentResult&gt;"/>
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
          <form action="{$webappContextPath}{$exitUrl}" method="post">
            <input type="submit" value="Exit"/>
          </form>
        </li>
      </ul>
    </div>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template match="qti:itemBody">
    <div id="itemBody">
      <form method="post" action="{$webappContextPath}{$responseUrl}"
        onsubmit="return QtiWorksRendering.submit()" enctype="multipart/form-data"
        onreset="QtiWorksRendering.reset()" autocomplete="off">

        <xsl:apply-templates/>

        <xsl:if test="$candidateCommentAllowed">
          <fieldset class="candidateComment">
            <legend>Please use the following text box if you need to provide any additional information, comments or feedback during this test:</legend>
            <input name="qtiworks_comment_presented" type="hidden" value="true"/>
            <textarea name="qtiworks_comment"><xsl:value-of select="$itemSessionState/qw:candidateComment"/></textarea>
          </fieldset>
        </xsl:if>

        <xsl:if test="$isItemSessionOpen">
          <div class="controls">
            <input id="submit_button" name="submit" type="submit" value="SUBMIT RESPONSE"/>
          </div>
        </xsl:if>
      </form>
    </div>
  </xsl:template>

  <!-- Overridden to add support for solution state -->
  <xsl:template match="qw:itemSessionState" mode="item-status">
    <xsl:choose>
      <xsl:when test="$solutionMode">
        <div class="itemStatus review">Model Solution</div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-imports/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template name="qw:item-authoring-feedback">
    <div class="authorInfo authorMode">
      <h2>QTI authoring feedback</h2>

      <h3>Candidate Item Session State</h3>
      <ul>
        <li>Entry time: <xsl:value-of select="$itemSessionState/@entryTime"/></li>
        <li>End time: <xsl:value-of select="$itemSessionState/@endTime"/></li>
        <li>Duration accumulated: <xsl:value-of select="$itemSessionState/@durationAccumulated"/> ms</li>
        <li>Initialized: <xsl:value-of select="$itemSessionState/@initialized"/></li>
        <li>Responded: <xsl:value-of select="$itemSessionState/@responded"/></li>
        <li><code>sessionStatus</code>: <xsl:value-of select="$sessionStatus"/></li>
        <li><code>numAttempts</code>: <xsl:value-of select="$itemSessionState/@numAttempts"/></li>
        <li><code>completionStatus</code>: <xsl:value-of select="$itemSessionState/@completionStatus"/></li>
        <li>Solution mode? <xsl:value-of select="$solutionMode"/></li>
      </ul>

      <!-- Show response stuff -->
      <xsl:if test="exists($unboundResponseIdentifiers) or exists($invalidResponseIdentifiers)">
        <h3>Response errors</h3>
        <xsl:if test="exists($unboundResponseIdentifiers)">
          <h4>Bad responses</h4>
          <p>
            The responses listed below were not successfully bound to their corresponding variables.
            This might happen, for example, if you bind a <code>&lt;textEntryInteraction&gt;</code> to
            a numeric variable and the candidate enters something that is not a number.
          </p>
          <ul>
            <xsl:for-each select="$unboundResponseIdentifiers">
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

      <!-- Show current state -->
      <h3>Item Variables</h3>
      <p>
        The current values of all item variables are shown below:
      </p>
      <xsl:call-template name="qw:sessionState"/>

      <!-- Notifications -->
      <xsl:if test="exists($notifications)">
        <h3>Processing notifications</h3>
        <p>
          The following notifications were recorded during the most recent processing run on this item.
          These may indicate issues with your item that need fixed.
        </p>
        <table class="notificationsTable">
          <thead>
            <tr>
              <th>Type</th>
              <th>Severity</th>
              <th>QTI Class</th>
              <th>Attribute</th>
              <th>Line Number</th>
              <th>Column Number</th>
              <th>Message</th>
            </tr>
          </thead>
          <tbody>
            <xsl:for-each select="$notifications">
              <tr>
                <td><xsl:value-of select="@type"/></td>
                <td><xsl:value-of select="@level"/></td>
                <td><xsl:value-of select="if (exists(@nodeQtiClassName)) then @nodeQtiClassName else 'N/A'"/></td>
                <td><xsl:value-of select="if (exists(@attrLocalName)) then @attrLocalName else 'N/A'"/></td>
                <td><xsl:value-of select="if (exists(@lineNumber)) then @lineNumber else 'Unknown'"/></td>
                <td><xsl:value-of select="if (exists(@columnNumber)) then @columnNumber else 'Unknown'"/></td>
                <td><xsl:value-of select="."/></td>
              </tr>
            </xsl:for-each>
          </tbody>
        </table>
      </xsl:if>

      <h3>Delivery settings</h3>
      <p>
        The current delivery settings allow the candidate to:
      </p>
      <dl>
        <dt>Reset session?</dt>
        <dd><xsl:value-of select="$softSoftResetAllowed"/></dd>

        <dt>Reinitialize session?</dt>
        <dd><xsl:value-of select="$hardResetAllowed"/></dd>

        <dt>Close session?</dt>
        <dd><xsl:value-of select="$endAllowed"/></dd>

        <dt>View solution?</dt>
        <dd><xsl:value-of select="$solutionAllowed"/></dd>

        <dt>View item source XML?</dt>
        <dd><xsl:value-of select="$sourceAllowed"/></dd>

        <dt>View <code>&lt;itemResult&gt;</code> XML?</dt>
        <dd><xsl:value-of select="$resultAllowed"/></dd>

        <dt>Submit candidate comment?</dt>
        <dd><xsl:value-of select="$candidateCommentAllowed"/></dd>
      </dl>
    </div>
  </xsl:template>

  <xsl:template name="qw:sessionState" as="element()+">
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
      <xsl:if test="exists($unboundResponseIdentifiers)">
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
          <script>
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
      <xsl:for-each select="$responseInputs[@identifier = $unboundResponseIdentifiers]">
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
