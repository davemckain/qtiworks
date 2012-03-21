<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:jqti="http://jqti.qtitools.org"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="qti jqti xs">

  <xsl:template match="qti:hotspotInteraction">
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
          <param name="code" value="BoundedGraphicalApplet"/>
          <param name="codebase" value="{$appletCodebase}"/>
          <param name="identifier" value="{@responseIdentifier}"/>
          <param name="operation_mode" value="hotspot_interaction"/>
          <!-- (BoundedGraphicalApplet uses -1 to represent 'unlimited') -->
          <param name="number_of_responses" value="{if (@maxChoices &gt; 0) then @maxChoices else -1}"/>
          <param name="background_image" value="{jqti:convert-link($object/@data)}"/>
          <xsl:variable name="hotspotChoices" select="jqti:filter-visible(qti:hotspotChoice)" as="element(qti:hotspotChoice)*"/>
          <param name="hotspot_count" value="{count($hotspotChoices)}"/>
          <xsl:for-each select="qti:hotspotChoice">
            <param name="hotspot{position()-1}"
              value="{@identifier}::::{@shape}::{@coords}{if (@label) then concat('::hotSpotLabel',@label) else ''}{if (@matchGroup) then concat('::', translate(normalize-space(@matchGroup), ' ', '::')) else ''}"/>
          </xsl:for-each>

          <xsl:variable name="responseValue" select="jqti:get-response-value(@responseIdentifier)" as="element(jqti:response)?"/>
          <xsl:if test="jqti:is-not-null-value($responseValue)">
            <param name="feedback">
              <xsl:attribute name="value">
                <xsl:value-of select="$responseValue/jqti:value" separator=","/>
              </xsl:attribute>
            </param>
          </xsl:if>
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
