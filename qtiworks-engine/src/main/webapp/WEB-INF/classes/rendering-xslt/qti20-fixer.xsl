<?xml version="1.0" encoding="UTF-8"?>
<!--

This stylesheet handles QTI 2.0 documents by converting them to
QTI 2.1 as a mini-pipeline and then re-processing the resulting
QTI 2.1 document

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:qti20="http://www.imsglobal.org/xsd/imsqti_v2p0"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="qti qti20 xs xsi">

  <xsl:template match="qti20:assessmentItem" as="element(html)">
    <xsl:variable name="qti21-document" as="document-node()">
      <xsl:document>
        <qti:assessmentItem>
          <xsl:copy-of select="@* except @xsi:schemaLocation"/>
          <xsl:if test="@xsi:schemaLocation">
            <xsl:attribute name="xsi:schemaLocation" select="replace(@xsi:schemaLocation, 'imsqti_v2p0', 'imsqti_v2p1')"/>
          </xsl:if>
          <xsl:apply-templates mode="qti20-to-21"/>
        </qti:assessmentItem>
      </xsl:document>
    </xsl:variable>
    <xsl:apply-templates select="$qti21-document/qti:assessmentItem"/>
  </xsl:template>

  <xsl:template match="qti20:*" mode="qti20-to-21">
    <xsl:element name="qti:{local-name()}">
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates mode="qti20-to-21"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="*" mode="qti20-to-21">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates mode="qti20-to-21"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="text()" mode="qti20-to-21">
    <xsl:copy-of select="."/>
  </xsl:template>

</xsl:stylesheet>


