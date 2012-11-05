<?xml version="1.0" encoding="UTF-8"?>
<!--

Base templates used in test rendering

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:m="http://www.w3.org/1998/Math/MathML"
  xmlns:qw="http://www.ph.ed.ac.uk/qtiworks"
  xmlns:saxon="http://saxon.sf.net/"
  xmlns="http://www.w3.org/1999/xhtml"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="qti xs qw saxon m">

  <!-- ************************************************************ -->

  <xsl:import href="qti-common.xsl"/>

  <!-- URI of the Test being rendered -->
  <xsl:param name="testSystemId" as="xs:string" required="yes"/>

  <!-- State of test being rendered -->
  <xsl:param name="testSessionState" as="element(qw:itemSessionState)" required="yes"/>

  <!-- Outcome declarations in test -->
  <xsl:param name="testOutcomeDeclarations" select="()" as="element(qti:outcomeDeclaration)*"/>

  <!-- assesssmentTest element -->
  <xsl:variable name="assessmentTest" as="element(qti:assessmentTest)"
    select="document($testSystemId)/*[1]"/>

  <!-- Test outcome values -->
  <xsl:param name="testOutcomeValues" select="()" as="element(qw:outcomeVariable)*"/>

  <!-- ************************************************************ -->

  <xsl:function name="qw:get-test-outcome-value" as="element(qw:outcomeVariable)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$testOutcomeValues[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="qw:get-test-outcome-declaration" as="element(qti:outcomeDeclaration)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$assessmentTest/qti:outcomeDeclaration[@identifier=$identifier]"/>
  </xsl:function>

  <!-- ************************************************************ -->

  <xsl:template match="qti:rubricBlock" as="element(div)">
    <div class="rubric {@view}">
      <xsl:if test="not($view) or ($view = @view)">
        <xsl:apply-templates/>
      </xsl:if>
    </div>
  </xsl:template>

  <!-- printedVariable. Numeric output currently only supports Java String.format formatting. -->
  <xsl:template match="qti:assessmentTest//qti:printedVariable" as="element(span)">
    <xsl:variable name="identifier" select="@identifier" as="xs:string"/>
    <xsl:variable name="testOutcomeValue" select="qw:get-test-outcome-value(@identifier)" as="element(qw:outcomeVariable)?"/>
    <span class="printedVariable">
      <xsl:choose>
        <xsl:when test="exists($testOutcomeValue)">
          <xsl:call-template name="printedVariable">
            <xsl:with-param name="valueHolder" select="$testOutcomeValue"/>
            <xsl:with-param name="valueDeclaration" select="qw:get-test-outcome-declaration(@identifier)"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          (variable <xsl:value-of select="$identifier"/> was not found)
        </xsl:otherwise>
      </xsl:choose>
    </span>
  </xsl:template>

  <!-- Keep MathML by default -->
  <xsl:template match="m:*" as="element()">
    <xsl:element name="{local-name()}" namespace="http://www.w3.org/1998/Math/MathML">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <!-- MathML parallel markup containers: we'll remove any non-XML annotations, which may
  result in the container also being removed as it's no longer required in that case. -->
  <xsl:template match="m:semantics" as="element()*">
    <xsl:choose>
      <xsl:when test="not(*[position()!=1 and self::m:annotation-xml])">
        <!-- All annotations are non-XML so remove this wrapper completely (and unwrap a container mrow if required) -->
        <xsl:apply-templates select="if (*[1][self::m:mrow]) then *[1]/* else *[1]"/>
      </xsl:when>
      <xsl:otherwise>
        <!-- Keep non-XML annotations -->
        <xsl:element name="semantics" namespace="http://www.w3.org/1998/Math/MathML">
          <xsl:apply-templates select="* except m:annotation"/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="m:*/text()" as="text()">
    <!-- NOTE: The XML input is produced using JQTI's toXmlString() method, which has
    the unfortunate effect of indenting MathML, so we'll renormalise -->
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>

  <!-- mathml (mi) -->
  <!--
  We are extending the spec here in 2 ways:
  1. Allowing MathsContent variables to be substituted
  2. Allowing arbitrary response and outcome variables to be substituted.
  -->
  <xsl:template match="qti:assessmentTest//m:mi" as="element()">
    <xsl:variable name="content" select="normalize-space(text())" as="xs:string"/>
    <xsl:variable name="testOutcomeValue" select="qw:get-test-outcome-value(@identifier)" as="element(qw:outcomeVariable)?"/>
    <xsl:choose>
      <xsl:when test="exists($testOutcomeValue)">
        <xsl:call-template name="substitute-mi">
          <xsl:with-param name="identifier" select="$content"/>
          <xsl:with-param name="value" select="$testOutcomeValue"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="mi" namespace="http://www.w3.org/1998/Math/MathML">
          <xsl:copy-of select="@*"/>
          <xsl:apply-templates/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- ************************************************************ -->

  <!-- feedback (block and inline) -->
  <xsl:template name="feedback" as="node()*">
    <xsl:choose>
      <xsl:when test="$overrideFeedback">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="identifierMatch" select="boolean(qw:value-contains(qw:get-test-outcome-value(@outcomeIdentifier), @identifier))" as="xs:boolean"/>
        <xsl:if test="($identifierMatch and @showHide='show') or (not($identifierMatch) and @showHide='hide')">
          <xsl:apply-templates/>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Convert XHTML elements that have been "imported" into QTI -->
  <xsl:template match="qti:abbr|qti:acronym|qti:address|qti:blockquote|qti:br|qti:cite|qti:code|
                       qti:dfn|qti:div|qti:em|qti:h1|qti:h2|qti:h3|qti:h4|qti:h5|qti:h6|qti:kbd|
                       qti:p|qti:pre|qti:q|qti:samp|qti:span|qti:strong|qti:var|
                       qti:dl|qti:dt|qti:dd|qti:ol|qti:ul|qti:li|
                       qti:object|qti:b|qti:big|qti:hr|qti:i|qti:small|qti:sub|qti:sup|qti:tt|
                       qti:caption|qti:col|qti:colgroup|qti:table|qti:tbody|qti:td|qti:tfoot|qti:tr|qti:thead|
                       qti:img|qti:a">
    <xsl:element name="{local-name()}">
      <xsl:apply-templates select="@*" mode="qti-to-xhtml"/>
      <xsl:apply-templates select="node()"/>
    </xsl:element>
  </xsl:template>

  <!-- Handle path attributes carefully so that relative paths get fixed up -->
  <xsl:template match="qti:img/@src|@href|qti:object/@data" mode="qti-to-xhtml">
    <xsl:attribute name="{local-name()}" select="qw:convert-link(string(.))"/>
  </xsl:template>

  <!-- Copy other attributes as-is -->
  <xsl:template match="@*" mode="qti-to-xhtml">
    <xsl:copy-of select="."/>
  </xsl:template>

  <!-- Catch-all for QTI elements not handled elsewhere. -->
  <xsl:template match="qti:*" priority="-10">
    <xsl:message terminate="yes">
      QTI element <xsl:value-of select="local-name()"/> was not handled by a template
    </xsl:message>
  </xsl:template>

</xsl:stylesheet>

