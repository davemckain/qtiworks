<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="qti">

  <xsl:template match="qti:endAttemptInteraction">
    <input name="jqtipresented_{@responseIdentifier}" type="hidden" value="1"/>
    <span class="{local-name()}">
      <input type="submit" name="jqtiresponse_{@responseIdentifier}" value="{@title}"/>
    </span>
  </xsl:template>

</xsl:stylesheet>
