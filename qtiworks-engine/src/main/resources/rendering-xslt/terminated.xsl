<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders a terminated assessment

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qw="http://www.ph.ed.ac.uk/qtiworks"
  xmlns="http://www.w3.org/1999/xhtml"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xs qw">

  <!-- ************************************************************ -->

  <xsl:import href="serialize.xsl"/>

  <!-- Web Application contextPath. Starts with a '/' -->
  <xsl:param name="webappContextPath" as="xs:string" required="yes"/>

  <!-- ************************************************************ -->

  <xsl:template match="/">
    <xsl:variable name="unserialized-output" as="element()">
      <xsl:call-template name="terminated"/>
    </xsl:variable>
    <xsl:apply-templates select="$unserialized-output" mode="serialize"/>
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template name="terminated" as="element(html)">
    <html>
      <head>
        <title>Assessment Completed</title>
        <!-- QTIWorks Item styling -->
        <link rel="stylesheet" href="{$webappContextPath}/rendering/css/item.css" type="text/css" media="screen"/>
      </head>
      <body class="qtiworks">
        <p>
          This assessment is now completed.
        </p>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
