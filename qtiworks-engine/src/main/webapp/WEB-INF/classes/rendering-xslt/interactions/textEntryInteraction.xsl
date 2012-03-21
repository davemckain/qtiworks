<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:jqti="http://jqti.qtitools.org"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xs qti jqti">

  <xsl:template match="qti:textEntryInteraction">
    <input name="jqtipresented_{@responseIdentifier}" type="hidden" value="1"/>
    <span class="{local-name()}">
      <xsl:variable name="responseDeclaration" select="jqti:get-response-declaration(/, @responseIdentifier)" as="element(qti:responseDeclaration)?"/>
      <xsl:variable name="responseInput" select="jqti:get-response-input(@responseIdentifier)" as="element(jqti:responseInput)?"/>
      <xsl:variable name="responseInputString" select="jqti:extract-single-cardinality-response-input($responseInput)" as="xs:string?"/>
      <xsl:variable name="checks" as="xs:string*">
        <xsl:choose>
          <xsl:when test="$responseDeclaration/@baseType='float'"><xsl:sequence select="'float'"/></xsl:when>
          <xsl:when test="$responseDeclaration/@baseType='integer'"><xsl:sequence select="'integer'"/></xsl:when>
        </xsl:choose>
        <xsl:if test="@patternMask">
          <xsl:sequence select="('regex', @patternMask)"/>
        </xsl:if>
      </xsl:variable>
      <xsl:variable name="checkJavaScript" select="concat('JQTIItemRendering.validateInput(this, ',
        jqti:to-javascript-arguments($checks),
        ')')" as="xs:string"/>

      <input type="text" name="jqtiresponse_{@responseIdentifier}">
        <xsl:if test="jqti:is-bad-response(@responseIdentifier) or jqti:is-invalid-response(@responseIdentifier)">
          <xsl:attribute name="class" select="'badResponse'"/>
        </xsl:if>
        <xsl:if test="@expectedLength">
          <xsl:attribute name="size" select="@expectedLength"/>
        </xsl:if>
        <xsl:if test="exists($responseInputString)">
          <xsl:attribute name="value" select="$responseInputString"/>
        </xsl:if>
        <xsl:if test="exists($checks)">
          <xsl:attribute name="onchange" select="$checkJavaScript"/>
        </xsl:if>
      </input>
      <xsl:if test="jqti:is-bad-response(@responseIdentifier)">
        <span class="badResponse">
          You must enter a valid <xsl:value-of select="$responseDeclaration/@baseType"/>!
        </span>
      </xsl:if>
      <xsl:if test="jqti:is-invalid-response(@responseIdentifier)">
        <!-- (This must be a regex issue) -->
        <span class="badResponse">
          Your input is not of the required format!
        </span>
      </xsl:if>
    </span>
  </xsl:template>

</xsl:stylesheet>
