<?xml version="1.0" encoding="UTF-8"?>
<!--

Renders an "exploded" session.

Input document: doesn't matter

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:qti="http://www.imsglobal.org/xsd/imsqti_v2p1"
  xmlns:m="http://www.w3.org/1998/Math/MathML"
  xmlns:qw="http://www.ph.ed.ac.uk/qtiworks"
  xmlns="http://www.w3.org/1999/xhtml"
  xpath-default-namespace="http://www.w3.org/1999/xhtml"
  exclude-result-prefixes="xs qti qw m">

  <!-- ************************************************************ -->

  <xsl:import href="qti-common.xsl"/>

  <!-- ************************************************************ -->

  <xsl:template match="/" as="element(html)">
    <html>
      <head>
        <title>Assessment failure</title>
        <link rel="stylesheet" href="//fonts.googleapis.com/css?family=Open+Sans:400,400italic,700,700italic|Ubuntu:500"/>
        <link rel="stylesheet" href="{$webappContextPath}/lib/960/reset.css"/>
        <link rel="stylesheet" href="{$webappContextPath}/lib/960/text.css"/>
        <link rel="stylesheet" href="{$webappContextPath}/lib/fluid960gs/grid.css"/>
        <link rel="stylesheet" href="{$webappContextPath}/includes/qtiworks.css?{$qtiWorksVersion}"/>
        <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/smoothness/jquery-ui.css"/>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"/>
        <script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"/>
        <script src="{$webappContextPath}/includes/qtiworks.js?{$qtiWorksVersion}"/>

        <!-- QTIWorks assessment styling -->
        <link rel="stylesheet" href="{$webappContextPath}/rendering/css/assessment.css?{$qtiWorksVersion}" type="text/css" media="screen"/>
      </head>
      <body class="qtiworks exploded">
        <div class="container_12">
          <header>
            <h1>QTIWorks</h1>
          </header>
          <xsl:choose>
            <xsl:when test="$authorMode">
              <p>
                This assessment has failed to run correctly:
              </p>
              <ul>
                <li>
                  The most likely reasons for failure is bad or invalid assessment XML.
                  Please <a href="{$webappContextPath}{$validationUrl}">validate this assessment</a> to find and diagnose problems.
                </li>
                <li>
                  If your assessment is valid but is not working correctly, then there
                  may be a logic problem within QTIWorks. Please contact your QTIWorks
                  support contact for guidance, sending them this assessment for diagnosis.
                </li>
              </ul>
            </xsl:when>
            <xsl:otherwise>
              <p>
                Sorry, but this assessment is not working correctly. This problem has been logged.
              </p>
              <p>
                Please contact your instructor for further help.
              </p>
            </xsl:otherwise>
          </xsl:choose>
        </div>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
