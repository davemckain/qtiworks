<?xml version="1.0" encoding="UTF-8"?>
<!--

Common templates for QTI flow elements, used in both item and test
rendering.

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:m="http://www.w3.org/1998/Math/MathML"
  xmlns:jqti="http://jqti.qtitools.org"
  xmlns:saxon="http://saxon.sf.net/"
  xmlns="http://www.w3.org/1999/xhtml"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="qti xs jqti saxon m">

  <!-- ************************************************************ -->

  <!-- Engine base path, i.e. Web Application contextPath. Starts with a '/' -->
  <xsl:param name="engineBasePath" as="xs:string" required="yes"/>

  <!-- Base path where resources for this item/test live. This should include
       the web application contextPath as well.  -->
  <xsl:param name="resourceBasePath" select="'/MathAssessEngine/content/someitemfolder'" as="xs:string"/>

  <!-- Href of the Item XML being rendered, relative to the package/folder it lives in -->
  <xsl:param name="itemHref" select="'someitem.xml'" as="xs:string"/>

  <xsl:param name="isResponded" select="false()" as="xs:boolean"/>

  <!-- Raw response information -->
  <xsl:param name="responseInputs" select="()" as="element(jqti:responseInput)*"/>
  <xsl:param name="badResponseIdentifiers" select="()" as="xs:string*"/>
  <xsl:param name="invalidResponseIdentifiers" select="()" as="xs:string*"/>

  <xsl:param name="view" select="false()" as="xs:boolean"/>

  <!-- AssessmentItem variables -->
  <xsl:param name="templateValues" select="()" as="element(jqti:template)*"/>
  <xsl:param name="responseValues" select="()" as="element(jqti:response)*"/>
  <xsl:param name="outcomeValues" select="()" as="element(jqti:outcome)*"/>

  <!-- Choice orders for shuffled shuffleable Interactions -->
  <xsl:param name="shuffledChoiceOrders" select="()" as="element(jqti:shuffledChoiceOrder)*"/>

  <!-- AssessmentTest variables (only passed when rendering tests) -->
  <xsl:param name="testOutcomeValues" select="()" as="element(jqti:outcome)*"/>
  <xsl:param name="testOutcomeDeclarations" select="()" as="element(qti:outcomeDeclaration)*"/>

  <!-- Debugging Params -->
  <xsl:param name="overrideFeedback" select="false()" as="xs:boolean"/> <!-- enable all feedback  -->
  <xsl:param name="overrideTemplate" select="false()" as="xs:boolean"/> <!-- enable all templates -->

  <!-- Codebase URL for engine-provided applets -->
  <xsl:variable name="appletCodebase" select="concat($engineBasePath, '/applets')" as="xs:string"/>

  <!-- Include stylesheets handling each type of interaction -->
  <xsl:include href="interactions/associateInteraction.xsl"/>
  <xsl:include href="interactions/choiceInteraction.xsl"/>
  <xsl:include href="interactions/drawingInteraction.xsl"/>
  <xsl:include href="interactions/endAttemptInteraction.xsl"/>
  <xsl:include href="interactions/extendedTextInteraction.xsl"/>
  <xsl:include href="interactions/gapMatchInteraction.xsl"/>
  <xsl:include href="interactions/graphicAssociateInteraction.xsl"/>
  <xsl:include href="interactions/graphicGapMatchInteraction.xsl"/>
  <xsl:include href="interactions/graphicOrderInteraction.xsl"/>
  <xsl:include href="interactions/hotspotInteraction.xsl"/>
  <xsl:include href="interactions/hottextInteraction.xsl"/>
  <xsl:include href="interactions/inlineChoiceInteraction.xsl"/>
  <xsl:include href="interactions/matchInteraction.xsl"/>
  <xsl:include href="interactions/mediaInteraction.xsl"/>
  <xsl:include href="interactions/orderInteraction.xsl"/>
  <xsl:include href="interactions/positionObjectInteraction.xsl"/>
  <xsl:include href="interactions/selectPointInteraction.xsl"/>
  <xsl:include href="interactions/sliderInteraction.xsl"/>
  <xsl:include href="interactions/textEntryInteraction.xsl"/>
  <xsl:include href="interactions/uploadInteraction.xsl"/>
  <xsl:include href="interactions/mathEntryInteraction.xsl"/>

  <!-- ************************************************************ -->

  <!-- Resolves a link to a resource -->
  <xsl:function name="jqti:convert-link" as="xs:string">
    <xsl:param name="uri" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="starts-with($uri, 'http:') or starts-with($uri, 'https:') or starts-with($uri, 'mailto:')">
        <xsl:sequence select="$uri"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="resolved" as="xs:string" select="string(resolve-uri($uri, $itemHref))"/>
        <xsl:sequence select="concat($resourceBasePath, '?path=', encode-for-uri($resolved))"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <!-- ************************************************************ -->

  <xsl:function name="jqti:get-response-input" as="element(jqti:responseInput)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$responseInputs[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="jqti:is-bad-response" as="xs:boolean">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$badResponseIdentifiers=$identifier"/>
  </xsl:function>

  <xsl:function name="jqti:is-invalid-response" as="xs:boolean">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$invalidResponseIdentifiers=$identifier"/>
  </xsl:function>

  <xsl:function name="jqti:extract-single-cardinality-response-input" as="xs:string">
    <xsl:param name="responseInput" as="element(jqti:responseInput)?"/>
    <xsl:variable name="values" select="$responseInput/jqti:value" as="element(jqti:value)*"/>
    <xsl:choose>
      <xsl:when test="not(exists($values))">
        <xsl:sequence select="''"/>
      </xsl:when>
      <xsl:when test="count($values)=1">
        <xsl:sequence select="$values[1]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          Expected response input <xsl:copy-of select="$responseInput"/> to contain one value only
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="jqti:format-number" as="xs:string">
    <xsl:param name="format" as="xs:string"/>
    <xsl:param name="number" as="xs:double"/>
    <xsl:sequence select="fmt:format($format, $number)" xmlns:fmt="java:uk.ac.ed.ph.qtiworks.rendering.XsltExtensionFunctions"/>
  </xsl:function>

  <xsl:function name="jqti:get-template-value" as="element(jqti:template)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$templateValues[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="jqti:get-outcome-value" as="element(jqti:outcome)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$outcomeValues[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="jqti:get-test-outcome-value" as="element(jqti:outcome)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$testOutcomeValues[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="jqti:get-response-value" as="element(jqti:response)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$responseValues[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="jqti:get-template-declaration" as="element(qti:templateDeclaration)?">
    <xsl:param name="document" as="document-node()"/>
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$document/qti:assessmentItem/qti:templateDeclaration[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="jqti:get-outcome-declaration" as="element(qti:outcomeDeclaration)?">
    <xsl:param name="document" as="document-node()"/>
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$document/qti:assessmentItem/qti:outcomeDeclaration[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="jqti:get-test-outcome-declaration" as="element(qti:outcomeDeclaration)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$testOutcomeDeclarations[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="jqti:get-response-declaration" as="element(qti:responseDeclaration)?">
    <xsl:param name="document" as="document-node()"/>
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$document/qti:assessmentItem/qti:responseDeclaration[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="jqti:value-contains" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()?"/>
    <xsl:param name="test" as="xs:string"/>
    <xsl:sequence select="boolean($valueHolder/jqti:value[string(.)=$test])"/>
  </xsl:function>

  <xsl:function name="jqti:is-not-null-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="exists($valueHolder/*)"/>
  </xsl:function>

  <xsl:function name="jqti:is-null-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="not(exists($valueHolder/*))"/>
  </xsl:function>

  <xsl:function name="jqti:is-single-cardinality-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="boolean($valueHolder[@cardinality='single'])"/>
  </xsl:function>

  <xsl:function name="jqti:extract-single-cardinality-value" as="xs:string">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:choose>
      <xsl:when test="jqti:is-null-value($valueHolder)">
        <xsl:sequence select="''"/>
      </xsl:when>
      <xsl:when test="jqti:is-single-cardinality-value($valueHolder)">
        <xsl:sequence select="string($valueHolder/jqti:value)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          Expected value <xsl:copy-of select="$valueHolder"/> to have single cardinality
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="jqti:is-multiple-cardinality-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="boolean($valueHolder[@cardinality='multiple'])"/>
  </xsl:function>

  <xsl:function name="jqti:is-ordered-cardinality-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="boolean($valueHolder[@cardinality='ordered'])"/>
  </xsl:function>

  <xsl:function name="jqti:get-cardinality-size" as="xs:integer">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="count($valueHolder/jqti:value)"/>
  </xsl:function>

  <!-- NB: This works for both ordered and multiple cardinalities so as to allow iteration -->
  <!-- (NB: The term 'iterable' is not defined in the spec.) -->
  <xsl:function name="jqti:extract-iterable-element" as="xs:string">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:param name="index" as="xs:integer"/>
    <xsl:choose>
      <xsl:when test="jqti:is-null-value($valueHolder)">
        <xsl:sequence select="''"/>
      </xsl:when>
      <xsl:when test="jqti:is-ordered-cardinality-value($valueHolder) or jqti:is-multiple-cardinality-value($valueHolder)">
        <xsl:sequence select="string($valueHolder/jqti:value[position()=$index])"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          Expected value <xsl:copy-of select="$valueHolder"/> to have ordered
          or multiple cardinality
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="jqti:extract-iterable-elements" as="xs:string*">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:choose>
      <xsl:when test="jqti:is-null-value($valueHolder)">
        <xsl:sequence select="()"/>
      </xsl:when>
      <xsl:when test="jqti:is-ordered-cardinality-value($valueHolder) or jqti:is-multiple-cardinality-value($valueHolder)">
        <xsl:sequence select="for $v in $valueHolder/jqti:value return string($v)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          Expected value <xsl:copy-of select="$valueHolder"/> to have ordered
          or multiple cardinality
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="jqti:is-record-cardinality-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="boolean($valueHolder[@cardinality='record'])"/>
  </xsl:function>

  <xsl:function name="jqti:is-maths-content-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="boolean($valueHolder[@cardinality='record'
      and jqti:value[@baseType='string' and @cardinality='single' and @identifier='MathsContentClass'
        and string(jqti:value)='org.qtitools.mathassess']])"/>
  </xsl:function>

  <xsl:function name="jqti:extract-maths-content-pmathml" as="element(m:math)">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:choose>
      <xsl:when test="jqti:is-maths-content-value($valueHolder)">
        <xsl:variable name="pmathmlString" select="$valueHolder/jqti:value[@identifier='PMathML']" as="xs:string"/>
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

  <!-- Tests the @showHide and @templateIdentifier attributes of the given (choice) element to determine whether it
  should be shown or not -->
  <xsl:function name="jqti:is-visible" as="xs:boolean">
    <xsl:param name="element" as="element()"/>
    <xsl:sequence select="boolean($element[$overrideTemplate
        or not(@templateIdentifier)
        or (jqti:value-contains(jqti:get-template-value(@templateIdentifier), @identifier) and not(@showHide='hide'))])"/>
  </xsl:function>

  <!-- Filters out the elements in the given sequence having @showHide and @templateIdentifier attributes to return
  the ones that will actually be visible -->
  <xsl:function name="jqti:filter-visible" as="element()*">
    <xsl:param name="elements" as="element()*"/>
    <xsl:sequence select="$elements[jqti:is-visible(.)]"/>
  </xsl:function>

  <xsl:function name="jqti:get-shuffled-choice-order" as="xs:string*">
    <xsl:param name="interaction" as="element()"/>
    <xsl:variable name="shuffledChoiceOrder" select="$shuffledChoiceOrders[@responseIdentifier=$interaction/@responseIdentifier]"/>
    <xsl:sequence select="for $c in ($shuffledChoiceOrder/jqti:choice) return $c/@identifier"/>
  </xsl:function>

  <xsl:function name="jqti:get-visible-ordered-choices" as="element()*">
    <xsl:param name="interaction" as="element()"/>
    <xsl:param name="choices" as="element()*"/>
    <xsl:variable name="orderedChoices" as="element()*">
      <xsl:choose>
        <xsl:when test="$interaction/@shuffle='true'">
          <xsl:for-each select="jqti:get-shuffled-choice-order($interaction)">
            <xsl:sequence select="$choices[@identifier=current()]"/>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:sequence select="$choices"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:sequence select="jqti:filter-visible($orderedChoices)"/>
  </xsl:function>

  <!-- ************************************************************ -->

  <xsl:template name="jqti:generic-bad-response-message">
    <div class="badResponse">
      Please complete this interaction as directed.
    </div>
  </xsl:template>
  <!-- ************************************************************ -->

  <xsl:template match="qti:infoControl" as="element(div)">
    <div class="infoControl">
      <input type="submit" onclick="return JQTIItemRendering.showInfoControlContent(this)" value="{@title}"/>
      <div class="infoControlContent">
        <xsl:apply-templates/>
      </div>
    </div>
  </xsl:template>

  <!-- Stylesheet link -->
  <xsl:template match="qti:stylesheet" as="element(link)">
    <link rel="stylesheet">
      <xsl:copy-of select="@* except @href"/>
      <xsl:if test="exists(@href)">
        <xsl:attribute name="href" select="jqti:convert-link(@href)"/>
      </xsl:if>
    </link>
  </xsl:template>

  <!-- prompt -->
  <xsl:template match="qti:prompt">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- param -->
  <xsl:template match="qti:param">
    <xsl:variable name="template" select="jqti:get-template-value(@value)" as="element(jqti:template)?"/>
    <!-- Note: spec is not explicit in that we really only allow single cardinality param substitution -->
    <param name="{@name}" value="{if (exists($template)
        and jqti:is-single-cardinality-value($template)
        and jqti:get-template-declaration(/, @value)[@paramVariable='true'])
      then jqti:extract-single-cardinality-value($template) else @value}"/>
  </xsl:template>

  <xsl:template match="qti:rubricBlock" as="element(div)">
    <div class="rubric {@view}">
      <xsl:if test="not($view) or ($view = @view)">
        <xsl:apply-templates/>
      </xsl:if>
    </div>
  </xsl:template>

  <!-- printedVariable. Numeric output currently only supports Java String.format formatting. -->
  <xsl:template match="qti:printedVariable" as="element(span)">
    <xsl:variable name="identifier" select="@identifier" as="xs:string"/>
    <xsl:variable name="testOutcomeValue" select="if (ancestor::qti:testFeedback) then jqti:get-test-outcome-value(@identifier) else ()" as="element(jqti:outcome)?"/>
    <xsl:variable name="outcomeValue" select="jqti:get-outcome-value(@identifier)" as="element(jqti:outcome)?"/>
    <xsl:variable name="templateValue" select="jqti:get-template-value(@identifier)" as="element(jqti:template)?"/>
    <span class="printedVariable">
      <xsl:choose>
        <xsl:when test="exists($testOutcomeValue)">
          <xsl:call-template name="printedVariable">
            <xsl:with-param name="valueHolder" select="$testOutcomeValue"/>
            <xsl:with-param name="valueDeclaration" select="jqti:get-test-outcome-declaration(@identifier)"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="exists($outcomeValue)">
          <xsl:call-template name="printedVariable">
            <xsl:with-param name="valueHolder" select="$outcomeValue"/>
            <xsl:with-param name="valueDeclaration" select="jqti:get-outcome-declaration(/, @identifier)"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="exists($templateValue)">
          <xsl:call-template name="printedVariable">
            <xsl:with-param name="valueHolder" select="$templateValue"/>
            <xsl:with-param name="valueDeclaration" select="jqti:get-template-declaration(/, @identifier)"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          (variable <xsl:value-of select="$identifier"/> was not found)
        </xsl:otherwise>
      </xsl:choose>
    </span>
  </xsl:template>

  <xsl:template name="printedVariable" as="node()?">
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
      <xsl:when test="jqti:is-null-value($valueHolder)">
        <!-- We'll output NULL as an empty string -->
        <xsl:text/>
      </xsl:when>
      <xsl:when test="jqti:is-single-cardinality-value($valueHolder)">
        <xsl:variable name="singleValue" select="jqti:extract-single-cardinality-value($valueHolder)" as="xs:string"/>
        <xsl:choose>
          <xsl:when test="@format and $valueDeclaration[@baseType='float' or @baseType='integer']">
            <xsl:value-of select="jqti:format-number(@format, number($singleValue))"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$singleValue"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="jqti:is-maths-content-value($valueHolder)">
        <xsl:copy-of select="jqti:extract-maths-content-pmathml($valueHolder)"/>
      </xsl:when>
      <xsl:when test="jqti:is-multiple-cardinality-value($valueHolder)">
        <xsl:text>{</xsl:text>
        <xsl:value-of select="jqti:extract-iterable-elements($valueHolder)" separator=", "/>
        <xsl:text>}</xsl:text>
      </xsl:when>
      <xsl:when test="jqti:is-ordered-cardinality-value($valueHolder)">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="jqti:extract-iterable-elements($valueHolder)" separator=", "/>
        <xsl:text>]</xsl:text>
      </xsl:when>
      <xsl:when test="jqti:is-record-cardinality-value($valueHolder)">
        <xsl:text>{</xsl:text>
        <xsl:variable name="to-print" as="xs:string*"
          select="for $v in $valueHolder/jqti:value return concat($v/@identifier, ': ', $v/jqti:value)"/>
        <xsl:value-of select="$to-print" separator=", "/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          &lt;printedVariable&gt; may not be applied to value
          <xsl:copy-of select="$valueHolder"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
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
  <xsl:template match="m:mi" as="element()">
    <xsl:variable name="content" select="normalize-space(text())" as="xs:string"/>
    <xsl:variable name="templateValue" select="jqti:get-template-value($content)" as="element(jqti:template)?"/>
    <xsl:variable name="responseValue" select="jqti:get-response-value($content)" as="element(jqti:response)?"/>
    <xsl:variable name="testOutcomeValue" select="if (ancestor::qti:testFeedback) then jqti:get-test-outcome-value(@identifier) else ()" as="element(jqti:outcome)?"/>
    <xsl:variable name="outcomeValue" select="jqti:get-outcome-value($content)" as="element(jqti:outcome)?"/>
    <xsl:choose>
      <xsl:when test="exists($templateValue) and jqti:get-template-declaration(/, $content)[@mathVariable='true']">
        <xsl:call-template name="substitute-mi">
          <xsl:with-param name="identifier" select="$content"/>
          <xsl:with-param name="value" select="$templateValue"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="exists($responseValue)">
        <xsl:call-template name="substitute-mi">
          <xsl:with-param name="identifier" select="$content"/>
          <xsl:with-param name="value" select="$responseValue"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="exists($testOutcomeValue)">
        <xsl:call-template name="substitute-mi">
          <xsl:with-param name="identifier" select="$content"/>
          <xsl:with-param name="value" select="$outcomeValue"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="exists($outcomeValue)">
        <xsl:call-template name="substitute-mi">
          <xsl:with-param name="identifier" select="$content"/>
          <xsl:with-param name="value" select="$outcomeValue"/>
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

  <xsl:template name="substitute-mi" as="element()">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:param name="value" as="element()"/>
    <xsl:choose>
      <xsl:when test="jqti:is-single-cardinality-value($value)">
        <!-- Single cardinality template variables are substituted according to Section 6.3.1 of the
        spec. Note that it does not define what should be done with multiple and ordered
        cardinality variables. -->
        <xsl:element name="mn" namespace="http://www.w3.org/1998/Math/MathML">
          <xsl:copy-of select="@*"/>
          <xsl:value-of select="jqti:extract-single-cardinality-value($value)"/>
        </xsl:element>
      </xsl:when>
      <xsl:when test="jqti:is-maths-content-value($value)">
        <!-- This is a MathAssess MathsContent variable. What we do here is
        replace the matched MathML element with the child(ren) of the <math/> PMathML field
        in this record, wrapping in an <mrow/> if required so as to ensure that we have a
        single replacement element -->
        <xsl:variable name="pmathml" select="jqti:extract-maths-content-pmathml($value)" as="element(m:math)"/>
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
        <xsl:message terminate="yes">
          Substituting the variable <xsl:value-of select="$identifier"/> with value
          <xsl:copy-of select="$value"/>
          within MathML is not currently supported.
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!-- mathml (ci) -->
  <!--
  (We are currently not allowing MathsContent variables to be substituted in the same way as <mi/>.
  Instead, we're sticking to what the spec says here. This could be expanded later if required.
  -->
  <xsl:template match="m:ci">
    <xsl:variable name="value" select="normalize-space(text())"/>
    <xsl:variable name="template" select="jqti:get-template-value($value)" as="element(jqti:template)?"/>
    <xsl:variable name="templateDeclaration" select="jqti:get-template-declaration(/, $value)" as="element(qti:templateDeclaration)?"/>
    <xsl:choose>
      <xsl:when test="exists($template) and $templateDeclaration[@mathVariable='true']">
        <xsl:element name="cn" namespace="http://www.w3.org/1998/Math/MathML">
          <xsl:copy-of select="@*"/>
          <xsl:apply-templates/>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="ci" namespace="http://www.w3.org/1998/Math/MathML">
          <xsl:copy-of select="@*"/>
          <xsl:apply-templates/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

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

  <!-- feedback (block and inline) -->
  <xsl:template name="feedback" as="node()*">
    <xsl:choose>
      <xsl:when test="$overrideFeedback">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="identifierMatch" select="boolean(jqti:value-contains(jqti:get-outcome-value(@outcomeIdentifier), @identifier))" as="xs:boolean"/>
        <xsl:if test="($identifierMatch and @showHide='show') or (not($identifierMatch) and @showHide='hide')">
          <xsl:apply-templates/>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- templateBlock -->
  <xsl:template match="qti:templateBlock" as="node()*">
    <xsl:call-template name="template"/>
  </xsl:template>

  <!-- templateInline -->
  <xsl:template match="qti:templateInline" as="node()*">
    <xsl:call-template name="template"/>
  </xsl:template>

  <!-- template (block and feedback) -->
  <xsl:template name="template" as="node()*">
    <xsl:choose>
      <xsl:when test="$overrideTemplate">
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="identifierMatch" select="boolean(jqti:value-contains(jqti:get-template-value(@templateIdentifier),@identifier))" as="xs:boolean"/>
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
    <xsl:attribute name="{local-name()}" select="jqti:convert-link(string(.))"/>
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

