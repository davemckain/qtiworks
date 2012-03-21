<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:jqti="http://jqti.qtitools.org"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xs qti jqti">

  <xsl:template match="qti:positionObjectStage">
    <xsl:for-each select="qti:positionObjectInteraction">
      <input name="jqtipresented_{@responseIdentifier}" type="hidden" value="1"/>
    </xsl:for-each>
    <div class="{local-name()}">
      <xsl:if test="qti:prompt">
        <div class="prompt">
          <xsl:apply-templates select="qti:prompt"/>
        </div>
      </xsl:if>
      <!-- TODO: This probably looks awful! -->
      <xsl:for-each select="qti:positionObjectInteraction">
        <xsl:if test="jqti:is-invalid-response(@responseIdentifier)">
          <xsl:call-template name="jqti:generic-bad-response-message"/>
        </xsl:if>
      </xsl:for-each>

      <xsl:variable name="object" select="qti:object"/>
      <xsl:variable name="appletContainerId" select="concat('qtiid_appletContainer_', qti:positionObjectInteraction[1]/@responseIdentifier)" as="xs:string"/>
      <div id="{$appletContainerId}" class="appletContainer">
        <object type="application/x-java-applet" height="{$object/@height + 40}" width="{$object/@width}">
          <param name="code" value="rhotspotV2"/>
          <param name="codebase" value="{$appletCodebase}"/>
          <param name="NoOfMainImages" value="1"/>
          <param name="background_image" value="{jqti:convert-link($object/@data)}"/>
          <param name="Mainimageno1" value="{jqti:convert-link($object/@data)}::0::0::{$object/@width}::{$object/@height}"/>
          <param name="baseType" value="point"/>
          <param name="noOfTargets" value="0"/>
          <param name="identifiedTargets" value="FALSE"/>
          <param name="interactions" value="{string-join(for $i in qti:positionObjectInteraction return $i/@responseIdentifier, '::')}"/>

          <xsl:for-each select="qti:positionObjectInteraction">
            <xsl:variable name="interaction" select="." as="element(qti:positionObjectInteraction)"/>
            <param name="maxChoices:{@responseIdentifier}" value="{@maxChoices}"/>
            <xsl:for-each select="1 to @maxChoices">
              <param name="labelNo{.}:{$interaction/@responseIdentifier}"
                value="::{$interaction/qti:object/@type}::{jqti:convert-link($interaction/qti:object/@data)}::{$interaction/qti:object/@width}::{$interaction/qti:object/@height}::{$interaction/@maxChoices}"/>
            </xsl:for-each>

            <xsl:variable name="responseValue" select="jqti:get-response-value(@responseIdentifier)" as="element(jqti:response)?"/>
            <param name="feedbackState:{@responseIdentifier}">
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
          </xsl:for-each>

          <!--param name="noOfMarkers" value="3"/>
            <xsl:attribute name="value"><xsl:value-of select="@maxChoices"/></xsl:attribute>
          </param -->
          <param name="markerType" value="LABELS"/>
        </object>
        <script type="text/javascript">
          $(document).ready(function() {
            JQTIItemRendering.registerAppletBasedInteractionContainer('<xsl:value-of
              select="$appletContainerId"/>', [<xsl:value-of
              select="jqti:to-javascript-arguments(for $i in qti:positionObjectInteraction return $i/@responseIdentifier)"/>]);
          });
        </script>
      </div>
    </div>
  </xsl:template>

</xsl:stylesheet>
