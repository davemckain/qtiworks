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
  xmlns:qw="http://www.ph.ed.ac.uk/qtiworks"
  xmlns:saxon="http://saxon.sf.net/"
  xmlns="http://www.w3.org/1999/xhtml"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="qti xs qw saxon m">

  <!-- ************************************************************ -->

  <!-- Web Application contextPath. Starts with a '/' -->
  <xsl:param name="webappContextPath" as="xs:string" required="yes"/>

  <!-- URI of the Item being rendered -->
  <xsl:param name="itemSystemId" as="xs:string" required="yes"/>

  <!-- Action URLs -->
  <xsl:param name="attemptUrl" as="xs:string" required="yes"/>
  <xsl:param name="resetUrl" as="xs:string" required="yes"/>
  <xsl:param name="reinitUrl" as="xs:string" required="yes"/>
  <xsl:param name="endUrl" as="xs:string" required="yes"/>
  <xsl:param name="closeUrl" as="xs:string" required="yes"/>
  <xsl:param name="sourceUrl" as="xs:string" required="yes"/>
  <xsl:param name="resultUrl" as="xs:string" required="yes"/>
  <xsl:param name="serveFileUrl" as="xs:string" required="yes"/>

  <!-- Action permissions -->
  <xsl:param name="attemptAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="endAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="resetAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="reinitAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="sourceAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="resultAllowed" as="xs:boolean" required="yes"/>
  <xsl:param name="closeAllowed" as="xs:boolean" required="yes"/>

  <!-- Raw response information -->
  <xsl:param name="responseInputs" select="()" as="element(qw:responseInput)*"/>
  <xsl:param name="badResponseIdentifiers" select="()" as="xs:string*"/>
  <xsl:param name="invalidResponseIdentifiers" select="()" as="xs:string*"/>

  <!-- FIXME: This is not used at the moment? -->
  <xsl:param name="view" select="false()" as="xs:boolean"/>

  <!-- Current state -->
  <xsl:param name="itemSessionState" as="element(qw:itemSessionState)"/>

  <!-- AssessmentTest variables (only passed when rendering tests) -->
  <!-- FIXME: These need to be refactored -->
  <xsl:param name="testOutcomeValues" select="()" as="element(qw:outcome)*"/>
  <xsl:param name="testOutcomeDeclarations" select="()" as="element(qti:outcomeDeclaration)*"/>

  <!-- Debugging Params -->
  <!-- FIXME: These are not currently used! -->
  <xsl:param name="overrideFeedback" select="false()" as="xs:boolean"/> <!-- enable all feedback  -->
  <xsl:param name="overrideTemplate" select="false()" as="xs:boolean"/> <!-- enable all templates -->

  <!-- Extract stuff from the state -->
  <xsl:variable name="shuffledChoiceOrders" select="$itemSessionState/qw:shuffledInteractionChoiceOrder"
    as="element(qw:shuffledInteractionChoiceOrder)*"/>
  <xsl:variable name="templateValues" select="$itemSessionState/qw:templateVariable" as="element(qw:templateVariable)*"/>
  <xsl:variable name="responseValues" select="$itemSessionState/qw:responseVariable" as="element(qw:responseVariable)*"/>
  <xsl:variable name="outcomeValues" select="$itemSessionState/qw:outcomeVariable" as="element(qw:outcomeVariable)*"/>

  <!-- Has the candidate made a response? -->
  <xsl:variable name="isResponded" as="xs:boolean" select="exists($responseInputs)"/>

  <!-- Codebase URL for engine-provided applets -->
  <xsl:variable name="appletCodebase" select="concat($webappContextPath, '/rendering/applets')" as="xs:string"/>

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
  <xsl:function name="qw:convert-link" as="xs:string">
    <xsl:param name="uri" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="starts-with($uri, 'http:') or starts-with($uri, 'https:') or starts-with($uri, 'mailto:')">
        <xsl:sequence select="$uri"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="resolved" as="xs:string" select="string(resolve-uri($uri, $itemSystemId))"/>
        <xsl:sequence select="concat($webappContextPath, $serveFileUrl, '?href=', encode-for-uri($resolved))"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <!-- ************************************************************ -->

  <xsl:function name="qw:get-response-input" as="element(qw:responseInput)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$responseInputs[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="qw:is-bad-response" as="xs:boolean">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$badResponseIdentifiers=$identifier"/>
  </xsl:function>

  <xsl:function name="qw:is-invalid-response" as="xs:boolean">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$invalidResponseIdentifiers=$identifier"/>
  </xsl:function>

  <xsl:function name="qw:extract-single-cardinality-response-input" as="xs:string">
    <xsl:param name="responseInput" as="element(qw:responseInput)?"/>
    <xsl:variable name="values" select="$responseInput/qw:value" as="element(qw:value)*"/>
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

  <xsl:function name="qw:format-number" as="xs:string">
    <xsl:param name="format" as="xs:string"/>
    <xsl:param name="number" as="xs:double"/>
    <xsl:sequence select="fmt:format($format, $number)" xmlns:fmt="java:uk.ac.ed.ph.qtiworks.rendering.XsltExtensionFunctions"/>
  </xsl:function>

  <xsl:function name="qw:get-template-value" as="element(qw:templateVariable)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$templateValues[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="qw:get-outcome-value" as="element(qw:outcomeVariable)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$outcomeValues[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="qw:get-response-value" as="element(qw:responseVariable)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$responseValues[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="qw:get-template-declaration" as="element(qti:templateDeclaration)?">
    <xsl:param name="document" as="document-node()"/>
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$document/qti:assessmentItem/qti:templateDeclaration[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="qw:get-outcome-declaration" as="element(qti:outcomeDeclaration)?">
    <xsl:param name="document" as="document-node()"/>
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$document/qti:assessmentItem/qti:outcomeDeclaration[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="qw:get-test-outcome-value" as="element(qw:outcome)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$testOutcomeValues[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="qw:get-test-outcome-declaration" as="element(qti:outcomeDeclaration)?">
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$testOutcomeDeclarations[@identifier=$identifier]"/>
  </xsl:function>

  <xsl:function name="qw:get-response-declaration" as="element(qti:responseDeclaration)?">
    <xsl:param name="document" as="document-node()"/>
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="$document/qti:assessmentItem/qti:responseDeclaration[@identifier=$identifier]"/>
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
          or multiple cardinality
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="qw:is-record-cardinality-value" as="xs:boolean">
    <xsl:param name="valueHolder" as="element()"/>
    <xsl:sequence select="boolean($valueHolder[@cardinality='record'])"/>
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

  <!-- Tests the @showHide and @templateIdentifier attributes of the given (choice) element to determine whether it
  should be shown or not -->
  <xsl:function name="qw:is-visible" as="xs:boolean">
    <xsl:param name="element" as="element()"/>
    <xsl:sequence select="boolean($element[$overrideTemplate
        or not(@templateIdentifier)
        or (qw:value-contains(qw:get-template-value(@templateIdentifier), @identifier) and not(@showHide='hide'))])"/>
  </xsl:function>

  <!-- Filters out the elements in the given sequence having @showHide and @templateIdentifier attributes to return
  the ones that will actually be visible -->
  <xsl:function name="qw:filter-visible" as="element()*">
    <xsl:param name="elements" as="element()*"/>
    <xsl:sequence select="$elements[qw:is-visible(.)]"/>
  </xsl:function>

  <xsl:function name="qw:get-shuffled-choice-order" as="xs:string*">
    <xsl:param name="interaction" as="element()"/>
    <xsl:variable name="choiceSequence" as="xs:string?"
      select="$shuffledChoiceOrders[@responseIdentifier=$interaction/@responseIdentifier]/@choiceSequence"/>
    <xsl:sequence select="tokenize($choiceSequence, ' ')"/>
  </xsl:function>

  <xsl:function name="qw:get-visible-ordered-choices" as="element()*">
    <xsl:param name="interaction" as="element()"/>
    <xsl:param name="choices" as="element()*"/>
    <xsl:variable name="orderedChoices" as="element()*">
      <xsl:choose>
        <xsl:when test="$interaction/@shuffle='true'">
          <xsl:for-each select="qw:get-shuffled-choice-order($interaction)">
            <xsl:sequence select="$choices[@identifier=current()]"/>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:sequence select="$choices"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:sequence select="qw:filter-visible($orderedChoices)"/>
  </xsl:function>

  <!-- ************************************************************ -->

  <xsl:template name="qw:generic-bad-response-message">
    <div class="badResponse">
      Please complete this interaction as directed.
    </div>
  </xsl:template>
  <!-- ************************************************************ -->

  <xsl:template match="qti:infoControl" as="element(div)">
    <div class="infoControl">
      <input type="submit" onclick="return QtiWorks.showInfoControlContent(this)" value="{@title}"/>
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
        <xsl:attribute name="href" select="qw:convert-link(@href)"/>
      </xsl:if>
    </link>
  </xsl:template>

  <!-- prompt -->
  <xsl:template match="qti:prompt">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- param -->
  <xsl:template match="qti:param">
    <xsl:variable name="templateValue" select="qw:get-template-value(@value)" as="element(qw:templateVariable)?"/>
    <!-- Note: spec is not explicit in that we really only allow single cardinality param substitution -->
    <param name="{@name}" value="{if (exists($templateValue)
        and qw:is-single-cardinality-value($templateValue)
        and qw:get-template-declaration(/, @value)[@paramVariable='true'])
      then qw:extract-single-cardinality-value($templateValue) else @value}"/>
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
    <xsl:variable name="templateValue" select="qw:get-template-value(@identifier)" as="element(qw:templateVariable)?"/>
    <xsl:variable name="outcomeValue" select="qw:get-outcome-value(@identifier)" as="element(qw:outcomeVariable)?"/>
    <xsl:variable name="testOutcomeValue" select="if (ancestor::qti:testFeedback) then qw:get-test-outcome-value(@identifier) else ()" as="element(qw:outcome)?"/>
    <span class="printedVariable">
      <xsl:choose>
        <xsl:when test="exists($testOutcomeValue)">
          <xsl:call-template name="printedVariable">
            <xsl:with-param name="valueHolder" select="$testOutcomeValue"/>
            <xsl:with-param name="valueDeclaration" select="qw:get-test-outcome-declaration(@identifier)"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="exists($outcomeValue)">
          <xsl:call-template name="printedVariable">
            <xsl:with-param name="valueHolder" select="$outcomeValue"/>
            <xsl:with-param name="valueDeclaration" select="qw:get-outcome-declaration(/, @identifier)"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="exists($templateValue)">
          <xsl:call-template name="printedVariable">
            <xsl:with-param name="valueHolder" select="$templateValue"/>
            <xsl:with-param name="valueDeclaration" select="qw:get-template-declaration(/, @identifier)"/>
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
      <xsl:when test="qw:is-null-value($valueHolder)">
        <!-- We'll output NULL as an empty string -->
        <xsl:text/>
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
        <xsl:copy-of select="qw:extract-maths-content-pmathml($valueHolder)"/>
      </xsl:when>
      <xsl:when test="qw:is-multiple-cardinality-value($valueHolder)">
        <xsl:text>{</xsl:text>
        <xsl:value-of select="qw:extract-iterable-elements($valueHolder)" separator=", "/>
        <xsl:text>}</xsl:text>
      </xsl:when>
      <xsl:when test="qw:is-ordered-cardinality-value($valueHolder)">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="qw:extract-iterable-elements($valueHolder)" separator=", "/>
        <xsl:text>]</xsl:text>
      </xsl:when>
      <xsl:when test="qw:is-record-cardinality-value($valueHolder)">
        <xsl:text>{</xsl:text>
        <xsl:variable name="to-print" as="xs:string*"
          select="for $v in $valueHolder/qw:value return concat($v/@identifier, ': ', $v/qw:value)"/>
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
    <xsl:variable name="templateValue" select="qw:get-template-value($content)" as="element(qw:templateVariable)?"/>
    <xsl:variable name="responseValue" select="qw:get-response-value($content)" as="element(qw:responseVariable)?"/>
    <xsl:variable name="outcomeValue" select="qw:get-outcome-value($content)" as="element(qw:outcomeVariable)?"/>
    <xsl:variable name="testOutcomeValue" select="if (ancestor::qti:testFeedback) then qw:get-test-outcome-value(@identifier) else ()" as="element(qw:outcome)?"/>
    <xsl:choose>
      <xsl:when test="exists($templateValue) and qw:get-template-declaration(/, $content)[@mathVariable='true']">
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
    <xsl:variable name="templateValue" select="qw:get-template-value($value)" as="element(qw:templateVariable)?"/>
    <xsl:variable name="templateDeclaration" select="qw:get-template-declaration(/, $value)" as="element(qti:templateDeclaration)?"/>
    <xsl:choose>
      <xsl:when test="exists($templateValue) and $templateDeclaration[@mathVariable='true']">
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
        <xsl:variable name="identifierMatch" select="boolean(qw:value-contains(qw:get-outcome-value(@outcomeIdentifier), @identifier))" as="xs:boolean"/>
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
        <xsl:variable name="identifierMatch" select="boolean(qw:value-contains(qw:get-template-value(@templateIdentifier),@identifier))" as="xs:boolean"/>
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

