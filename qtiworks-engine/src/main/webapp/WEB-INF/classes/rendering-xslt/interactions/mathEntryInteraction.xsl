<?xml version="1.0"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:ma="http://mathassess.qtitools.org/xsd/mathassess"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:jqti="http://jqti.qtitools.org"
  xmlns:m="http://www.w3.org/1998/Math/MathML"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xs qti ma jqti m">

  <xsl:template match="qti:customInteraction[@class='org.qtitools.mathassess.MathEntryInteraction']">
    <input name="jqtipresented_{@responseIdentifier}" type="hidden" value="1"/>
    <xsl:variable name="responseInput" select="jqti:get-response-input(@responseIdentifier)" as="element(jqti:responseInput)?"/>
    <xsl:variable name="responseValue" select="jqti:get-response-value(@responseIdentifier)" as="element(jqti:response)?"/>
    <xsl:variable name="asciiMathInput" select="jqti:extract-single-cardinality-response-input($responseInput)" as="xs:string?"/>
    <div class="mathEntryInteraction">
      <div class="inputPanel">
        <a href="{$engineBasePath}/rendering/mathEntryInteractionHelp.html" target="_blank" id="jqtiid_mathEntryHelp_{@responseIdentifier}"></a>
        <input id="jqtiid_mathEntryInput_{@responseIdentifier}" name="jqtiresponse_{@responseIdentifier}" type="text"
            size="{if (exists(@ma:expectedLength)) then @ma:expectedLength else '10'}">
          <xsl:if test="exists($asciiMathInput)">
            <xsl:attribute name="value">
              <xsl:value-of select="$asciiMathInput"/>
            </xsl:attribute>
          </xsl:if>
        </input>
      </div>
      <div class="previewPanel">
        <div id="jqtiid_mathEntryMessages_{@responseIdentifier}"></div>
        <div id="jqtiid_mathEntryPreview_{@responseIdentifier}">
          <!-- Keep this in -->
          <math xmlns="http://www.w3.org/1998/Math/MathML"></math>
        </div>
      </div>
      <script type="text/javascript">
        JQTIItemRendering.registerReadyCallback(function() {
          var inputControlId = 'jqtiid_mathEntryInput_<xsl:value-of select="@responseIdentifier"/>';
          var messageContainerId = 'jqtiid_mathEntryMessages_<xsl:value-of select="@responseIdentifier"/>';
          var previewContainerId = 'jqtiid_mathEntryPreview_<xsl:value-of select="@responseIdentifier"/>';
          var helpContainerId = 'jqtiid_mathEntryHelp_<xsl:value-of select="@responseIdentifier"/>';

          var upConversionAJAXControl = UpConversionAJAXController.createUpConversionAJAXControl(messageContainerId, previewContainerId);
          var widget = ASCIIMathInputController.bindInputWidget(inputControlId, upConversionAJAXControl);
          widget.setHelpButtonId(helpContainerId);
          widget.init();
          <xsl:choose>
            <xsl:when test="jqti:is-bad-response(@responseIdentifier)">
              widget.show('<xsl:value-of select="$asciiMathInput"/>', {
                cmathFailures: {}
              });
            </xsl:when>
            <xsl:when test="jqti:is-null-value($responseValue)">
              widget.syncWithInput();
            </xsl:when>
            <xsl:otherwise>
              widget.show('<xsl:value-of select="$asciiMathInput"/>', {
                cmath: '<xsl:value-of select="jqti:escape-for-javascript-string($responseValue/jqti:value[@identifier='CMathML'])"/>',
                pmathBracketed: '<xsl:value-of select="jqti:escape-for-javascript-string($responseValue/jqti:value[@identifier='PMathMLBracketed'])"/>',
              });
            </xsl:otherwise>
          </xsl:choose>
        });
      </script>
    </div>
  </xsl:template>

</xsl:stylesheet>
