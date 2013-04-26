<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders the author/debug view of a standalone assessmentItem

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

  <xsl:param name="itemSessionStateXml" as="xs:string" required="yes"/>

  <!-- ************************************************************ -->

  <xsl:template match="/" as="element(html)">
    <html>
      <head>
        <title>Author Debug View</title>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"/>
        <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.1/jquery-ui.min.js"/>
        <script src="{$webappContextPath}/rendering/javascript/QtiWorksRendering.js?{$qtiWorksVersion}"/>
        <script src="{$webappContextPath}/rendering/javascript/AuthorMode.js?{$qtiWorksVersion}"/>
        <script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js"/>

        <!-- Styling for JQuery -->
        <link rel="stylesheet" type="text/css" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.1/themes/redmond/jquery-ui.css"/>
      </head>
      <body class="qtiworks authorInfo">
        <div class="authorInfo authorMode">
          <h1>QTI author's feedback</h1>

          <h2>Candidate Item Session State</h2>
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
            <h2>Response errors</h2>
            <xsl:if test="exists($unboundResponseIdentifiers)">
              <h3>Bad responses</h3>
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
              <h3>Invalid responses</h3>
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
          <h2>Item Variables</h2>
          <p>
            The current values of all item variables are shown below:
          </p>
          <xsl:call-template name="qw:sessionState"/>

          <!-- Show state XML -->
          <h2>Item Session State XML</h2>
          <pre class="prettyprint xmlSource"><xsl:value-of select="$itemSessionStateXml"/></pre>

          <!-- Notifications -->
          <xsl:if test="exists($notifications)">
            <h2>Processing notifications</h2>
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
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="qw:sessionState" as="element()+">
    <div class="sessionState">
      <xsl:if test="exists($shuffledChoiceOrders)">
        <h3>Shuffled choice orders</h3>
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
        <h3>Template variables</h3>
        <xsl:call-template name="dump-values">
          <xsl:with-param name="valueHolders" select="$templateValues"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="exists($unboundResponseIdentifiers)">
        <h3>Unbound Response inputs</h3>
        <xsl:call-template name="dump-unbound-response-inputs"/>
      </xsl:if>
      <xsl:if test="exists($responseValues)">
        <h3>Response variables</h3>
        <xsl:call-template name="dump-values">
          <xsl:with-param name="valueHolders" select="$responseValues"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="exists($outcomeValues)">
        <h3>Outcome variables</h3>
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
