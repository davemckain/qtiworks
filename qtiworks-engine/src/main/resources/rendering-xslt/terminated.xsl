<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders a terminated assessment

Input document: doesn't matter

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qw="http://www.ph.ed.ac.uk/qtiworks"
  xmlns="http://www.w3.org/1999/xhtml"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xs qw">

  <!-- ************************************************************ -->

  <!-- Web Application contextPath. Starts with a '/' -->
  <xsl:param name="webappContextPath" as="xs:string" required="yes"/>

  <!-- ************************************************************ -->

  <xsl:template match="/">
    <html>
      <head>
        <title>Assessment Completed</title>
        <link rel="stylesheet" href="{$webappContextPath}/rendering/css/assessment.css" type="text/css" media="screen"/>
      </head>
      <body class="qtiworks">
        <p>
          This assessment is now closed and you can no longer interact with it.
        </p>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
