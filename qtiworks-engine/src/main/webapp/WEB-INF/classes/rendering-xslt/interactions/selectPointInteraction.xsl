<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:jqti="http://jqti.qtitools.org"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="qti jqti xs">

  <xsl:template match="qti:selectPointInteraction">
    <input name="jqtipresented_{@responseIdentifier}" type="hidden" value="1"/>
    <div class="{local-name()}">
      <xsl:if test="qti:prompt">
        <div class="prompt">
          <xsl:apply-templates select="qti:prompt"/>
        </div>
      </xsl:if>
      <xsl:if test="jqti:is-invalid-response(@responseIdentifier)">
        <xsl:call-template name="jqti:generic-bad-response-message"/>
      </xsl:if>

      <xsl:variable name="object" select="qti:object" as="element(qti:object)"/>
      <xsl:variable name="appletContainerId" select="concat('qtiid_appletContainer_', @responseIdentifier)" as="xs:string"/>
      <div id="{$appletContainerId}" class="appletContainer">
        <object type="application/x-java-applet" height="{$object/@height + 40}" width="{$object/@width}">
          <param name="code" value="rhotspotV2"/>
          <param name="codebase" value="{$appletCodebase}"/>
          <param name="identifier" value="{@responseIdentifier}"/>
          <param name="NoOfMainImages" value="1"/>
          <param name="Mainimageno1" value="{jqti:convert-link($object/@data)}::0::0::{$object/@width}::{$object/@height}"/>
          <param name="markerName" value="images/marker.gif"/>
          <param name="baseType" value="point"/>
          <param name="noOfTargets" value="0"/>
          <param name="identifiedTargets" value="FALSE"/>
          <param name="noOfMarkers" value="{@maxChoices}"/>
          <param name="markerType" value="STANDARD"/>

          <xsl:variable name="responseValue" select="jqti:get-response-value(@responseIdentifier)" as="element(jqti:response)?"/>
          <param name="feedbackState">
            <xsl:attribute name="value">
              <xsl:choose>
                <xsl:when test="jqti:is-not-null-value($responseValue)">
                  <xsl:text>Yes:</xsl:text>
                  <xsl:value-of select="$responseValue/jqti:value" separator=":"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>No</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>
          </param>
        </object>
        <script type="text/javascript">
          $(document).ready(function() {
            JQTIItemRendering.registerAppletBasedInteractionContainer('<xsl:value-of
              select="$appletContainerId"/>', ['<xsl:value-of select="@responseIdentifier"/>']);
          });
        </script>
      </div>
    </div>
  </xsl:template>

</xsl:stylesheet>
