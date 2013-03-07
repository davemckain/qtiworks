<?xml version="1.0" encoding="UTF-8"?>
<!--

Contains QTI-related templates common to both item and test
rendering.

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

  <!-- Web Application contextPath. Starts with a '/' -->
  <xsl:param name="webappContextPath" as="xs:string" required="yes"/>

  <!-- QTIWorks version number -->
  <xsl:param name="qtiWorksVersion" as="xs:string" required="yes"/>

  <!-- URI of the Item or Test being rendered -->
  <xsl:param name="systemId" as="xs:string" required="yes"/>

  <!-- Set to true to include author debug information -->
  <xsl:param name="authorMode" as="xs:boolean" required="yes"/>

  <!-- Notificates produced during the event being rendered -->
  <xsl:param name="notifications" as="element(qw:notification)*"/>

  <!-- Action URLs -->
  <xsl:param name="attemptUrl" as="xs:string" required="yes"/>
  <xsl:param name="resetUrl" as="xs:string" required="yes"/>
  <xsl:param name="reinitUrl" as="xs:string" required="yes"/>
  <xsl:param name="closeUrl" as="xs:string" required="yes"/>
  <xsl:param name="solutionUrl" as="xs:string" required="yes"/>
  <xsl:param name="terminateUrl" as="xs:string" required="yes"/>
  <xsl:param name="sourceUrl" as="xs:string" required="yes"/>
  <xsl:param name="resultUrl" as="xs:string" required="yes"/>
  <xsl:param name="serveFileUrl" as="xs:string" required="yes"/>
  <xsl:param name="testPartNavigationUrl" as="xs:string" required="yes"/>
  <xsl:param name="selectTestItemUrl" as="xs:string" required="yes"/>
  <xsl:param name="finishTestItemUrl" as="xs:string" required="yes"/>
  <xsl:param name="endTestPartUrl" as="xs:string" required="yes"/>
  <xsl:param name="reviewTestPartUrl" as="xs:string" required="yes"/>
  <xsl:param name="reviewTestItemUrl" as="xs:string" required="yes"/>
  <xsl:param name="showTestItemSolutionUrl" as="xs:string" required="yes"/>
  <xsl:param name="advanceTestPartUrl" as="xs:string" required="yes"/>
  <xsl:param name="exitTestUrl" as="xs:string" required="yes"/>

  <!-- FIXME: This is not used at the moment -->
  <xsl:param name="view" select="false()" as="xs:boolean"/>

  <!-- Debugging Params -->
  <!-- FIXME: These are not currently used! -->
  <xsl:param name="overrideFeedback" select="false()" as="xs:boolean"/> <!-- enable all feedback  -->
  <xsl:param name="overrideTemplate" select="false()" as="xs:boolean"/> <!-- enable all templates -->

  <!-- Codebase URL for engine-provided applets -->
  <xsl:variable name="appletCodebase" select="concat($webappContextPath, '/rendering/applets')" as="xs:string"/>

  <!-- ************************************************************ -->

  <!-- Resolves a link to a resource -->
  <xsl:function name="qw:convert-link" as="xs:string">
    <xsl:param name="uri" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="starts-with($uri, 'http:') or starts-with($uri, 'https:') or starts-with($uri, 'mailto:')">
        <xsl:sequence select="$uri"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="resolved" as="xs:string" select="string(resolve-uri($uri, $systemId))"/>
        <xsl:sequence select="concat($webappContextPath, $serveFileUrl, '?href=', encode-for-uri($resolved))"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <!-- ************************************************************ -->

  <xsl:function name="qw:format-number" as="xs:string">
    <xsl:param name="format" as="xs:string"/>
    <xsl:param name="number" as="xs:double"/>
    <xsl:sequence select="fmt:format($format, $number)" xmlns:fmt="java:uk.ac.ed.ph.qtiworks.rendering.XsltExtensionFunctions"/>
  </xsl:function>

  <xsl:function name="qw:value-contains" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()?"/>
    <xsl:param name="test" as="xs:string"/>
    <xsl:sequence select="boolean($valueHolder/qw:value[string(.)=$test])"/>
  </xsl:function>

  <xsl:function name="qw:is-not-null-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="exists($valueHolder/*)"/>
  </xsl:function>

  <xsl:function name="qw:is-null-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="not(exists($valueHolder/*))"/>
  </xsl:function>

  <xsl:function name="qw:is-single-cardinality-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="boolean($valueHolder[@cardinality='single'])"/>
  </xsl:function>

  <xsl:function name="qw:extract-single-cardinality-value" as="xs:string">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:choose>
      <xsl:when test="qw:is-null-value($valueHolder)">
        <xsl:sequence select="''"/>
      </xsl:when>
      <xsl:when test="qw:is-single-cardinality-value($valueHolder)">
        <xsl:sequence select="string($valueHolder/qw:value)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          Expected value <xsl:copy-of select="$valueHolder"/> to have single cardinality
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="qw:is-multiple-cardinality-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="boolean($valueHolder[@cardinality='multiple'])"/>
  </xsl:function>

  <xsl:function name="qw:is-ordered-cardinality-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="boolean($valueHolder[@cardinality='ordered'])"/>
  </xsl:function>

  <xsl:function name="qw:get-cardinality-size" as="xs:integer">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="count($valueHolder/qw:value)"/>
  </xsl:function>

  <!-- NB: This works for both ordered and multiple cardinalities so as to allow iteration -->
  <!-- (NB: The term 'iterable' is not defined in the spec.) -->
  <xsl:function name="qw:extract-iterable-element" as="xs:string">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:param name="index" as="xs:integer"/>
    <xsl:choose>
      <xsl:when test="qw:is-null-value($valueHolder)">
        <xsl:sequence select="''"/>
      </xsl:when>
      <xsl:when test="qw:is-ordered-cardinality-value($valueHolder) or qw:is-multiple-cardinality-value($valueHolder)">
        <xsl:sequence select="string($valueHolder/qw:value[position()=$index])"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          Expected value <xsl:copy-of select="$valueHolder"/> to have ordered
          or multiple cardinality
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="qw:extract-iterable-elements" as="xs:string*">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:choose>
      <xsl:when test="qw:is-null-value($valueHolder)">
        <xsl:sequence select="()"/>
      </xsl:when>
      <xsl:when test="qw:is-ordered-cardinality-value($valueHolder) or qw:is-multiple-cardinality-value($valueHolder)">
        <xsl:sequence select="for $v in $valueHolder/qw:value return string($v)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          Expected value <xsl:copy-of select="$valueHolder"/> to have ordered
          or multiple cardinality.
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="qw:is-record-cardinality-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="boolean($valueHolder[@cardinality='record'])"/>
  </xsl:function>

  <xsl:function name="qw:extract-record-field-value" as="xs:string?">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:param name="fieldName" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="qw:is-record-cardinality-value($valueHolder)">
        <xsl:value-of select="$valueHolder/qw:value[@fieldIdentifier=$fieldName]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          Expected value <xsl:copy-of select="$valueHolder"/> to have record
          cardinalty.
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="qw:is-maths-content-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="boolean($valueHolder[@cardinality='record'
      and qw:value[@baseType='string' and @fieldIdentifier='MathsContentClass'
        and string(qw:value)='org.qtitools.mathassess']])"/>
  </xsl:function>

  <xsl:function name="qw:extract-maths-content-pmathml" as="element(m:math)">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:choose>
      <xsl:when test="qw:is-maths-content-value($valueHolder)">
        <xsl:variable name="pmathmlString" select="$valueHolder/qw:value[@fieldIdentifier='PMathML']" as="xs:string"/>
        <xsl:variable name="pmathmlDocNode" select="saxon:parse($pmathmlString)" as="document-node()"/>
        <xsl:copy-of select="$pmathmlDocNode/*"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          Expected value <xsl:copy-of select="$valueHolder"/> to be a MathsContent value
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <!-- ************************************************************ -->
  <!-- Variable substitution -->
  <!-- ************************************************************ -->

  <xsl:template name="printedVariable" as="node()?">
    <xsl:param name="source" as="element(qti:printedVariable)"/>
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:param name="valueDeclaration" as="element()"/>
    <!--

    The QTI spec says that this variable must have single cardinality.

    For convenience, we also accept multiple, ordered and record cardinality variables here,
    printing them out in a hard-coded form that probably won't make sense to test
    candidates but might be useful for debugging.

    Our implementation additionally adds support for "printing" MathsContent variables
    used in MathAssess, outputting an inline Presentation MathML element, as documented
    in the MathAssses spec.

    -->
    <xsl:choose>
      <xsl:when test="qw:is-null-value($valueHolder)">
        <!-- (Spec says to output nothing in this case) -->
      </xsl:when>
      <xsl:when test="qw:is-single-cardinality-value($valueHolder)">
        <xsl:variable name="singleValue" select="qw:extract-single-cardinality-value($valueHolder)" as="xs:string"/>
        <xsl:choose>
          <xsl:when test="@format and $valueDeclaration[@baseType='float' or @baseType='integer']">
            <xsl:value-of select="qw:format-number(@format, number($singleValue))"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$singleValue"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="qw:is-maths-content-value($valueHolder)">
        <!-- MathAssess math variable -->
        <xsl:copy-of select="qw:extract-maths-content-pmathml($valueHolder)"/>
      </xsl:when>
      <xsl:when test="qw:is-multiple-cardinality-value($valueHolder)">
        <!--  Multiple cardinality -->
        <xsl:variable name="delimiter" select="if (exists($source/@delimiter)) then $source/@delimiter else ';'"/>
        <xsl:value-of select="qw:extract-iterable-elements($valueHolder)" separator="{$delimiter}"/>
      </xsl:when>
      <xsl:when test="qw:is-ordered-cardinality-value($valueHolder)">
        <!--  Ordered cardinality -->
        <xsl:choose>
          <xsl:when test="exists($source/@index)">
            <xsl:value-of select="qw:extract-iterable-element($valueHolder, $source/@index)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="delimiter" select="if (exists($source/@delimiter)) then $source/@delimiter else ';'"/>
            <xsl:value-of select="qw:extract-iterable-elements($valueHolder)" separator="{$delimiter}"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="qw:is-record-cardinality-value($valueHolder)">
        <xsl:choose>
          <xsl:when test="exists($source/@field)">
            <!-- Display single field -->
            <xsl:value-of select="qw:extract-record-field-value($valueHolder, $source/@field)"/>
          </xsl:when>
          <xsl:otherwise>
            <!-- Dump whole record -->
            <xsl:variable name="delimiter" select="if (exists($source/@delimiter)) then $source/@delimiter else ';'"/>
            <xsl:variable name="mappingIndicator" select="if ($source/@mappingIndicator) then $source/@mappingIndicator else '='"/>
            <xsl:variable name="to-print" as="xs:string*"
              select="for $v in $valueHolder/qw:value return concat($v/@identifier, $mappingIndicator, $v/qw:value)"/>
            <xsl:value-of select="$to-print" separator="{$delimiter}"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          &lt;printedVariable&gt; may not be applied to value
          <xsl:copy-of select="$valueHolder"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- MathML substitution (mi) -->
  <xsl:template name="substitute-mi" as="element()">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:param name="value" as="element()"/>
    <xsl:choose>
      <xsl:when test="qw:is-null-value($value)">
        <!-- We shall represent null as an empty mrow -->
        <xsl:element name="mrow" namespace="http://www.w3.org/1998/Math/MathML"/>
      </xsl:when>
      <xsl:when test="qw:is-single-cardinality-value($value)">
        <!-- Single cardinality template variables are substituted according to Section 6.3.1 of the
        spec. Note that it does not define what should be done with multiple and ordered
        cardinality variables. -->
        <xsl:element name="mn" namespace="http://www.w3.org/1998/Math/MathML">
          <xsl:copy-of select="@*"/>
          <xsl:value-of select="qw:extract-single-cardinality-value($value)"/>
        </xsl:element>
      </xsl:when>
      <xsl:when test="qw:is-maths-content-value($value)">
        <!-- This is a MathAssess MathsContent variable. What we do here is
        replace the matched MathML element with the child(ren) of the <math/> PMathML field
        in this record, wrapping in an <mrow/> if required so as to ensure that we have a
        single replacement element -->
        <xsl:variable name="pmathml" select="qw:extract-maths-content-pmathml($value)" as="element(m:math)"/>
        <xsl:choose>
          <xsl:when test="count($pmathml/*)=1">
            <xsl:copy-of select="$pmathml/*"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:element name="mrow" namespace="http://www.w3.org/1998/Math/MathML">
              <xsl:copy-of select="$pmathml/*"/>
            </xsl:element>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <!-- Unsupported substitution -->
        <xsl:message>
          Substituting the variable <xsl:value-of select="$identifier"/> with value
          <xsl:copy-of select="$value"/>
          within MathML is not currently supported.
        </xsl:message>
        <xsl:element name="mtext" namespace="http://www.w3.org/1998/Math/MathML">(Unsupported variable substitution)</xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- ************************************************************ -->
  <!-- QTI flow -->
  <!-- ************************************************************ -->

  <!-- feedbackInline -->
  <xsl:template match="qti:feedbackInline" as="element(span)?">
    <xsl:variable name="feedback" as="node()*">
      <xsl:call-template name="feedback"/>
    </xsl:variable>
    <xsl:if test="exists($feedback)">
      <span class="feedbackInline">
        <xsl:sequence select="$feedback"/>
      </span>
    </xsl:if>
  </xsl:template>

  <!-- feedbackBlock -->
  <xsl:template match="qti:feedbackBlock" as="element(div)?">
    <xsl:variable name="feedback" as="node()*">
      <xsl:call-template name="feedback"/>
    </xsl:variable>
    <xsl:if test="exists($feedback)">
      <div class="feedbackBlock">
        <xsl:sequence select="$feedback"/>
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template name="feedback" as="node()*">
    <xsl:message terminate="yes">
      This must be overridden by including stylesheet
    </xsl:message>
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

  <!-- ************************************************************ -->

  <xsl:template match="qw:itemSessionState" mode="item-status">
    <xsl:choose>
      <xsl:when test="@closed='true'">
        <div class="itemStatus closed">Finished</div>
      </xsl:when>
      <xsl:when test="not(empty(@unboundResponseIdentifiers) and empty(@invalidResponseIdentifiers))">
        <div class="itemStatus invalid">Needs Attention</div>
      </xsl:when>
      <xsl:when test="@responded='true'">
        <div class="itemStatus answered">Answered</div>
      </xsl:when>
      <xsl:when test="@presented='true'">
          <div class="itemStatus notAnswered">Not Answered</div>
      </xsl:when>
      <xsl:otherwise>
        <div class="itemStatus notPresented">Not Seen</div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
