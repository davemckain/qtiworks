<?xml version="1.0" encoding="UTF-8"?>
<!--

Rendering utility templates

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:qw="http://www.ph.ed.ac.uk/qtiworks"
  xmlns="http://www.w3.org/1999/xhtml"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xs qti qw">

  <!-- Extra Debugging Params -->
  <xsl:param name="showInternalState" select="false()" as="xs:boolean"/>

  <xsl:variable name="apos" as="xs:string" select='"&apos;"'/>

  <!-- ************************************************************ -->

  <xsl:function name="qw:escape-for-javascript-string" as="xs:string">
    <xsl:param name="input" as="xs:string"/>
    <xsl:sequence select="replace(replace($input, '[&#x0d;&#x0a;]', ''), '($apos)', '\\$1')"/>
  </xsl:function>

  <xsl:function name="qw:to-javascript-string" as="xs:string">
    <xsl:param name="input" as="xs:string"/>
    <xsl:sequence select="concat($apos,
      replace(replace($input, '[&#x0d;&#x0a;]', ''), '($apos)', '\\$1'),
      $apos)"/>
  </xsl:function>

  <xsl:function name="qw:to-javascript-arguments" as="xs:string">
    <xsl:param name="inputs" as="xs:string*"/>
    <xsl:sequence select="string-join(for $string in $inputs return qw:to-javascript-string($string), ', ')"/>
  </xsl:function>

  <!-- ************************************************************ -->

  <xsl:template name="internalState" as="element()+">
    <div id="debug_internal_state">
      <h2>Session State</h2>
      <xsl:if test="exists($shuffledChoiceOrders)">
        <h3>Shuffled choice orders</h3>
        <ul>
          <xsl:for-each select="$shuffledChoiceOrders">
            <li>
              <span class="variable_name">
                <xsl:value-of select="@responseIdentifier"/>
              </span>
              <xsl:text> = [</xsl:text>
              <xsl:value-of select="qw:choice/@identifier" separator=", "/>
              <xsl:text>]</xsl:text>
            </li>
          </xsl:for-each>
        </ul>
      </xsl:if>
      <xsl:if test="exists($templateValues)">
        <h3>Item Template vars</h3>
        <xsl:call-template name="dump-values">
          <xsl:with-param name="valueHolders" select="$templateValues"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="exists($responseValues)">
        <h3>Item Response vars</h3>
        <xsl:call-template name="dump-values">
          <xsl:with-param name="valueHolders" select="$responseValues"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="exists($outcomeValues)">
        <h3>Item Outcome vars</h3>
        <xsl:call-template name="dump-values">
          <xsl:with-param name="valueHolders" select="$outcomeValues"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="exists($testOutcomeValues)">
        <h3>Test Outcome vars</h3>
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
      <span class="variable_name">
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
          <a id="qtiworks_id_toggle_debug_maths_content_{@identifier}" class="debug_button"
            href="javascript:void(0)">Toggle Details</a>
          <div id="qtiworks_id_debug_maths_content_{@identifier}" class="debug_maths_content">
            <xsl:call-template name="dump-values">
              <xsl:with-param name="valueHolders" select="$valueHolder/qw:value"/>
            </xsl:call-template>
          </div>
          <script type="text/javascript">
            $(document).ready(function() {
              $('a#qtiworks_id_toggle_debug_maths_content_<xsl:value-of select="@identifier"/>').click(function() {
                $('#qtiworks_id_debug_maths_content_<xsl:value-of select="@identifier"/>').toggle();
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
              <xsl:call-template name="dump-values">
                <xsl:with-param name="valueHolders" select="$valueHolder/qw:value"/>
              </xsl:call-template>
              <xsl:text>)</xsl:text>
            </xsl:when>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </li>
  </xsl:template>

</xsl:stylesheet>
