<!--

$Id: maxima-output-fixer.xsl 2833 2012-02-08 21:16:29Z davemckain $

This stylesheets attempts to fix up the raw MathML output using Maxima's
standard mathml.lisp module.

FIXME: Decide what to do with Maxima's string outputs... should we even allow this?
FIXME: Do we want to mark up obvious function outputs as functions?

Copyright (c) 2010, The University of Edinburgh
All Rights Reserved

-->
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:m="http://www.w3.org/1998/Math/MathML"
  xmlns="http://www.w3.org/1998/Math/MathML"
  xmlns:ma="http://www.qtitools.org/mathassess"
  exclude-result-prefixes="xs m ma"
  xpath-default-namespace="http://www.w3.org/1998/Math/MathML">

  <!-- ************************************************************ -->

  <xsl:strip-space elements="m:*"/>

  <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>

  <xsl:variable name="ma:maxima-operators-to-unicode" as="element()+">
    <operator input="+" output="+"/>
    <operator input="-" output="-"/>
    <operator input="*" output="*"/>
    <operator input="/" output="/"/>
    <operator input="!" output="!"/>
    <operator input="=" output="="/>
    <operator input="#" output="&#x2260;"/><!-- (Not equals) -->
    <operator input="&lt;" output="&lt;"/>
    <operator input="&gt;" output="&gt;"/>
    <operator input="&lt;=" output="&#x2264;"/><!-- (Less than or equal) -->
    <operator input="&gt;=" output="&#x2265;"/><!-- (Greater than or equal) -->
  </xsl:variable>

  <xsl:function name="ma:get-supported-operator" as="element()?">
    <xsl:param name="maxima-form" as="xs:string"/>
    <xsl:sequence select="$ma:maxima-operators-to-unicode[@input=normalize-space($maxima-form)]"/>
  </xsl:function>

  <xsl:function name="ma:is-supported-operator" as="xs:boolean">
    <xsl:param name="maxima-form" as="xs:string"/>
    <xsl:sequence select="exists(ma:get-supported-operator($maxima-form))"/>
  </xsl:function>

  <!-- ************************************************************ -->

  <!-- Make sure we can handle any operators correctly output -->
  <xsl:template match="mo">
    <xsl:copy-of select="ma:make-operator(string(.))"/>
  </xsl:template>

  <!--
  Maxima sometimes loses the <mo/> wrapper for unary minus and some
  other operators. In this case, the text node will have a sibling node
  -->
  <xsl:template match="text()[ma:is-supported-operator(.) and count(../node())&gt;1]">
    <xsl:copy-of select="ma:make-operator(string(.))"/>
  </xsl:template>

  <xsl:function name="ma:make-operator" as="element(mo)">
    <xsl:param name="maxima-form" as="xs:string"/>
    <xsl:variable name="maxima-normalized" as="xs:string" select="normalize-space($maxima-form)"/>
    <xsl:variable name="operator" as="element()?" select="ma:get-supported-operator($maxima-normalized)"/>
    <xsl:choose>
      <xsl:when test="exists($operator)">
        <mo><xsl:value-of select="$operator/@output"/></mo>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          Up-conversion process currently cannot handle Maxima operator <xsl:value-of select="$maxima-normalized"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <!--
  Container elements will be handled as normal, being careful about
  free text nodes that are output without being wrapped up.
  -->
  <xsl:template match="*">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

  <!-- Maxima adds <mspace/> elements to enhance viewability. We don't want these -->
  <xsl:template match="mspace"/>

  <!-- Fix <mfenced/>. Maxima gets the content model for this completely wrong and also
       adds in redundant separators. -->
  <xsl:template match="mfenced">
    <xsl:call-template name="ma:handle-mfenced">
      <xsl:with-param name="open" select="if (@open) then @open else '('"/>
      <xsl:with-param name="close" select="if (@close) then @close else ')'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="ma:handle-mfenced">
    <xsl:param name="open" as="xs:string"/>
    <xsl:param name="close" as="xs:string"/>
    <mfenced open="{$open}" close="{$close}">
      <xsl:for-each-group select="node()" group-adjacent="self::mo and .=','">
        <xsl:choose>
          <xsl:when test="current-grouping-key()">
            <!-- At the separator, which we'll ignore -->
          </xsl:when>
          <xsl:otherwise>
            <!-- At fence items -->
            <xsl:call-template name="ma:maybe-wrap-in-mrow">
              <xsl:with-param name="elements" as="element()*">
                <xsl:apply-templates select="current-group()"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each-group>
    </mfenced>
  </xsl:template>

  <!-- Single child text node will be treated as a Maxima string -->
  <xsl:template match="text()[count(../node())=1]">
    <mtext>
      <xsl:value-of select="normalize-space(.)"/>
    </mtext>
  </xsl:template>

  <!--
  Maxima's raw output for a set is done as a function:

  <mi>set</mi>
  <mfenced>...</mfenced>

  We want to convert this to a set.

  Funnily enough, it gets intervals right (or "better").

  -->
  <xsl:template match="mi[.='set' and following-sibling::*[1][self::mfenced]]" priority="10">
    <!-- We'll pull in in the next template -->
  </xsl:template>

  <xsl:template match="mfenced[preceding-sibling::*[1][self::mi and .='set']]" priority="10">
    <xsl:call-template name="ma:handle-mfenced">
      <xsl:with-param name="open" select="'{'"/>
      <xsl:with-param name="close" select="'}'"/>
    </xsl:call-template>
  </xsl:template>

  <!--
  Other Maxima functions are output in a similar way to sets, i.e.

  <mi>function</mi>
  <mfenced>...</mfenced>

  We will convert these to the slightly more semantic:

  <mrow>
    <mi>function</mi>
    <mo>&ApplyFunction;</mo>
    <mfenced>
        ... args tidied up ...
    </mfenced>
  </mrow>
  -->
  <xsl:template match="mi[following-sibling::*[1][self::mfenced]]" priority="5">
    <!-- Pulled in in the next template -->
  </xsl:template>

  <xsl:template match="mfenced[preceding-sibling::*[1][self::mi]]" priority="5">
    <mrow>
      <xsl:copy-of select="preceding-sibling::*[1]"/>
      <mo>&#x2061;</mo>
      <xsl:call-template name="ma:handle-mfenced">
        <xsl:with-param name="open" select="'('"/>
        <xsl:with-param name="close" select="')'"/>
      </xsl:call-template>
    </mrow>
  </xsl:template>

  <!-- Maxima's log function is the natural log function -->
  <xsl:template match="mi[.='log']">
    <mi>ln</mi>
  </xsl:template>

  <!-- Keep other identifiers and numbers intact -->
  <xsl:template match="mi|mn">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:value-of select="normalize-space(.)"/>
    </xsl:copy>
  </xsl:template>

  <!--
  Maxima's output for x_1 is:

  <msub>
    <mi>x_</mi>
    <mn>1</mn>
  </msub>

  which we'll convert to a slightly nicer form that will up-convert in the same way
  -->
  <xsl:template match="msub[*[1][self::mi and ends-with(.,'_')]]">
    <msub>
      <mi><xsl:value-of select="substring-before(*[1],'_')"/></mi>
      <xsl:copy-of select="*[2]"/>
    </msub>
  </xsl:template>

  <!-- Strip any <mrow/>s containing only 1 child -->
  <xsl:template match="mrow[count(node())=1]">
    <xsl:apply-templates/>
  </xsl:template>

  <!--
  Intercept special Maxima functions for operators and units. These
  come out of Maxima in the form:

  <mi>maOperator</mi>
  <mfenced>+</mfenced>
  -->

  <xsl:template match="mi[.='maOperator' and following-sibling::*[1][self::mfenced]]" priority="10">
    <xsl:variable name="maxima-form" as="xs:string" select="following-sibling::*[1]"/>
    <xsl:copy-of select="ma:make-operator($maxima-form)"/>
  </xsl:template>

  <xsl:template match="mi[.='maUnits' and following-sibling::*[1][self::mfenced]]" priority="10">
    <!-- We generate the same output as SnuggleTeX \units{...} here -->
    <mi mathvariant="normal" class="MathML-Unit">
      <xsl:value-of select="normalize-space(following-sibling::*[1])"/>
    </mi>
  </xsl:template>

  <xsl:template match="mfenced[preceding-sibling::*[1][self::mi and .='maOperator' or .='maUnits']]" priority="10">
    <!-- Handled above -->
  </xsl:template>

  <!-- ************************************************************ -->

  <xsl:template name="ma:maybe-wrap-in-mrow">
    <xsl:param name="elements" as="element()*" required="yes"/>
    <xsl:choose>
      <xsl:when test="count($elements)=1">
        <xsl:copy-of select="$elements"/>
      </xsl:when>
      <xsl:otherwise>
        <mrow>
          <xsl:copy-of select="$elements"/>
        </mrow>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>

