<?xml version="1.0" encoding="UTF-8"?>
<!-- 
Copyright 2008 - 2008 JEuclid, http://jeuclid.sf.net

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
<!-- $Id: mathmlc2p.xsl 2205 2010-01-13 12:09:39Z davemckain $ -->
<!--
Copyright (c) 2000-2004 Xerox Corporation
Copyright (c) 2005-2007 INRIA

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
-->
<!--
Author: E. Pietriga {emmanuel.pietriga@inria.fr}
Created: 02/10/2000
Last updated: 05/22/2007

This work was initially conducted by Xerox Research Centre Europe for INRIA
project Opera during a thesis work under a CIFRE contract.
Improvements made in 2005 have been contributed by Brent Hendricks from Connexions.
Improvements made in 2007 have been contributed by Josh Bode.
-->
<!-- general rules: 
*based on the 13 November 2000 WD   http://www.w3.org/TR/2000/CR-MathML2-20001113
*comments about char refs which do not work are related to Amaya 4.1, since this stylesheet was tested using Amaya as the presentation renderer; perhaps some of the char refs said not to be working in Amaya will work with another renderer.
*the subtrees returned by a template decide for themselves if they have to be surrounded by an mrow element (sometimes it is an mfenced element)
*they never add brackets to themselves (or this will be an exception); it is the parent (template from which this one has been called) which decides this since the need for brackets depends on the context
-->
<!-- TO DO LIST
*handling of compose and inverse is probably not good enough
*as for divide, we could use the dotted notation for differentiation provided apply has the appropriate 'other' attribute (which is not defined yet ans will perhaps never be: it does not seem to be something that will be specified, rather application dependant)
*have to find a way to detect when a vector should be represented verticaly (we do that only in one case: when preceding sibling is a matrix and operation is a multiplication; there are other cases where a vertical vector is the correct representation, but they are not yet supported)
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:m="http://www.w3.org/1998/Math/MathML" xmlns="http://www.w3.org/1998/Math/MathML" version="1.0" exclude-result-prefixes="m">
  <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
  <xsl:param name="And">&#8743;</xsl:param>
  <xsl:param name="ApplyFunction">&#8289;</xsl:param>
  <xsl:param name="Backslash">&#8726;</xsl:param>
  <xsl:param name="DoubleRightArrow">&#8658;</xsl:param>
  <xsl:param name="DownArrow">&#8595;</xsl:param>
  <xsl:param name="ee">&#8519;</xsl:param>
  <xsl:param name="empty">&#8709;</xsl:param>
  <xsl:param name="equiv">&#8801;</xsl:param>
  <xsl:param name="Exists">&#8707;</xsl:param>
  <xsl:param name="ExponentialE">&#8519;</xsl:param>
  <xsl:param name="ForAll">&#8704;</xsl:param>
  <xsl:param name="gamma">&#947;</xsl:param>
  <xsl:param name="GreaterEqual">&#8805;</xsl:param>
  <xsl:param name="gt">&gt;</xsl:param>
  <xsl:param name="ImaginaryI">&#8520;</xsl:param>
  <xsl:param name="infin">&#8734;</xsl:param>
  <xsl:param name="Integral">&#8747;</xsl:param>
  <xsl:param name="Intersection">&#8898;</xsl:param>
  <xsl:param name="InvisibleComma">&#8291;</xsl:param>
  <xsl:param name="InvisibleTimes">&#8290;</xsl:param>
  <xsl:param name="isin">&#8712;</xsl:param>
  <xsl:param name="lambda">&#955;</xsl:param>
  <xsl:param name="lang">&#9001;</xsl:param>
  <xsl:param name="LeftCeiling">&#8968;</xsl:param>
  <xsl:param name="LeftFloor">&#8970;</xsl:param>
  <xsl:param name="LessEqual">&#8806;</xsl:param>
  <xsl:param name="lt">&lt;</xsl:param>
  <xsl:param name="Not">&#172;</xsl:param>
  <xsl:param name="NotEqual">&#8800;</xsl:param>
  <xsl:param name="notin">&#8713;</xsl:param>
  <xsl:param name="NotSubset">&#8834;&#8402;</xsl:param>
  <xsl:param name="NotSubsetEqual">&#8840;</xsl:param>
  <xsl:param name="Or">&#8744;</xsl:param>
  <xsl:param name="ovbar">&#9021;</xsl:param>
  <xsl:param name="PartialD">&#8706;</xsl:param>
  <xsl:param name="pi">&#960;</xsl:param>
  <xsl:param name="Product">&#8719;</xsl:param>
  <xsl:param name="rang">&#9002;</xsl:param>
  <xsl:param name="RightArrow">&#8594;</xsl:param>
  <xsl:param name="RightFloor">&#8971;</xsl:param>
  <xsl:param name="RightCeiling">&#8969;</xsl:param>
  <xsl:param name="sigma">&#963;</xsl:param>
  <xsl:param name="SmallCircle">&#8728;</xsl:param>
  <xsl:param name="Subset">&#8912;</xsl:param>
  <xsl:param name="SubsetEqual">&#8838;</xsl:param>
  <xsl:param name="Sum">&#8721;</xsl:param>
  <xsl:param name="times">&#215;</xsl:param>
  <xsl:param name="Union">&#8899;</xsl:param>
  <xsl:param name="UpArrow">&#8593;</xsl:param>
<!-- #################### main #################### -->
  <!-- <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="text()|@*">
    <xsl:value-of disable-output-escaping="no" select="."/>
  </xsl:template> -->
<!-- #################### 4.4.1 #################### -->
<!-- number-->
<!-- support for bases and types-->
  <xsl:template match="m:cn">
    <xsl:choose>
      <xsl:when test="@base and @base!=10">
<!-- base specified and different from 10 ; if base = 10 we do not display it -->
        <msub>
          <mrow>
<!-- mrow to be sure that the base is actually the element put as sub in case the number is a composed one-->
            <xsl:choose>
              <xsl:when test="./@type='complex-cartesian' or ./@type='complex'">
                <mn>
                  <xsl:value-of select="text()[1]"/>
                </mn>
                <xsl:choose>
                  <xsl:when test="contains(text()[2],'-')">
                    <mo>-</mo>
                    <mn>
                      <xsl:value-of select="substring-after(text()[2],'-')"/>
                    </mn>
<!--substring-after does not seem to work well in XT : if imaginary part is expressed with at least one space char before the minus sign, then it does not work (we end up with two minus sign since the one in the text is kept)-->
                  </xsl:when>
                  <xsl:otherwise>
                    <mo>+</mo>
                    <mn>
                      <xsl:value-of select="text()[2]"/>
                    </mn>
                  </xsl:otherwise>
                </xsl:choose>
                <mo>
                  <xsl:value-of select="$InvisibleTimes"/>
                </mo>
                <mi>
                  <xsl:value-of select="$ImaginaryI"/>
                </mi>
<!-- perhaps ii-->
              </xsl:when>
              <xsl:when test="./@type='complex-polar'">
        Polar<mfenced><mn><xsl:value-of select="text()[1]"/></mn><mn><xsl:value-of select="text()[2]"/></mn></mfenced>
      </xsl:when>
              <xsl:when test="./@type='e-notation'">
                <mrow>
                  <mn>
                    <xsl:value-of select="text()[1]"/>
                  </mn>
                  <mo>e</mo>
                  <mn>
                    <xsl:value-of select="text()[2]"/>
                  </mn>
                </mrow>
              </xsl:when>
              <xsl:when test="./@type='rational'">
                <mn>
                  <xsl:value-of select="text()[1]"/>
                </mn>
                <mo>/</mo>
                <mn>
                  <xsl:value-of select="text()[2]"/>
                </mn>
              </xsl:when>
              <xsl:otherwise>
                <mn>
                  <xsl:value-of select="."/>
                </mn>
              </xsl:otherwise>
            </xsl:choose>
          </mrow>
          <mn>
            <xsl:value-of select="@base"/>
          </mn>
        </msub>
      </xsl:when>
      <xsl:otherwise>
<!-- no base specified -->
        <xsl:choose>
          <xsl:when test="./@type='complex-cartesian' or ./@type='complex'">
            <mrow>
              <mn>
                <xsl:value-of select="text()[1]"/>
              </mn>
              <xsl:choose>
                <xsl:when test="contains(text()[2],'-')">
                  <mo>-</mo>
                  <mn>
                    <xsl:value-of select="substring(text()[2],2)"/>
                  </mn>
                  <mo>
                    <xsl:value-of select="$InvisibleTimes"/>
                  </mo>
                  <mi>
                    <xsl:value-of select="$ImaginaryI"/>
                  </mi>
<!-- perhaps ii-->
                </xsl:when>
                <xsl:otherwise>
                  <mo>+</mo>
                  <mn>
                    <xsl:value-of select="text()[2]"/>
                  </mn>
                  <mo>
                    <xsl:value-of select="$InvisibleTimes"/>
                  </mo>
                  <mi>
                    <xsl:value-of select="$ImaginaryI"/>
                  </mi>
<!-- perhaps ii-->
                </xsl:otherwise>
              </xsl:choose>
            </mrow>
          </xsl:when>
          <xsl:when test="./@type='complex-polar'">
            <mrow>
              <mi>Polar</mi>
              <mfenced>
                <mn>
                  <xsl:value-of select="text()[1]"/>
                </mn>
                <mn>
                  <xsl:value-of select="text()[2]"/>
                </mn>
              </mfenced>
            </mrow>
          </xsl:when>
          <xsl:when test="./@type='e-notation'">
            <mrow>
              <mn>
                <xsl:value-of select="text()[1]"/>
              </mn>
              <mo>e</mo>
              <mn>
                <xsl:value-of select="text()[2]"/>
              </mn>
            </mrow>
          </xsl:when>
          <xsl:when test="./@type='rational'">
            <mrow>
              <mn>
                <xsl:value-of select="text()[1]"/>
              </mn>
              <mo>/</mo>
              <mn>
                <xsl:value-of select="text()[2]"/>
              </mn>
            </mrow>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
<!-- If child elements exist assume presentation markup -->
              <xsl:when test="*">
                <mrow>
                  <xsl:copy-of select="*"/>
                </mrow>
              </xsl:when>
              <xsl:otherwise>
                <mn>
                  <xsl:value-of select="."/>
                </mn>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
<!-- identifier -->
<!--support for presentation markup-->
  <xsl:template match="m:ci">
    <xsl:choose>
      <xsl:when test="./@type='complex-cartesian' or ./@type='complex'">
        <xsl:choose>
          <xsl:when test="count(*)&gt;0">
<!--if identifier is composed of real+imag parts-->
            <mrow>
              <mi>
                <xsl:value-of select="text()[1]"/>
              </mi>
              <xsl:choose>
<!-- im part is negative-->
                <xsl:when test="contains(text()[preceding-sibling::*[1][self::m:sep]],'-')">
                  <mo>-</mo>
                  <mi>
                    <xsl:value-of select="substring-after(text()[preceding-sibling::*[1][self::m:sep]],'-')"/>
                  </mi>
                  <mo>
                    <xsl:value-of select="$InvisibleTimes"/>
                  </mo>
                  <mi>
                    <xsl:value-of select="$ImaginaryI"/>
                  </mi>
<!-- perhaps ii-->
                </xsl:when>
                <xsl:otherwise>
<!-- im part is not negative-->
                  <mo>+</mo>
                  <mi>
                    <xsl:value-of select="text()[preceding-sibling::*[1][self::m:sep]]"/>
                  </mi>
                  <mo>
                    <xsl:value-of select="$InvisibleTimes"/>
                  </mo>
                  <mi>
                    <xsl:value-of select="$ImaginaryI"/>
                  </mi>
<!-- perhaps ii-->
                </xsl:otherwise>
              </xsl:choose>
            </mrow>
          </xsl:when>
          <xsl:otherwise>
<!-- if identifier is composed only of one text child-->
            <mi>
              <xsl:value-of select="."/>
            </mi>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="./@type='complex-polar'">
        <xsl:choose>
          <xsl:when test="count(*)&gt;0">
<!--if identifier is composed of real+imag parts-->
            <mrow>
              <mi>Polar</mi>
              <mfenced>
                <mi>
                  <xsl:value-of select="text()[following-sibling::*[self::m:sep]]"/>
                </mi>
                <mi>
                  <xsl:value-of select="text()[preceding-sibling::*[self::m:sep]]"/>
                </mi>
              </mfenced>
            </mrow>
          </xsl:when>
          <xsl:otherwise>
<!-- if identifier is composed only of one text child-->
            <mi>
              <xsl:value-of select="."/>
            </mi>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="./@type='rational'">
        <xsl:choose>
          <xsl:when test="count(*)&gt;0">
<!--if identifier is composed of two parts-->
            <mrow>
              <mi>
                <xsl:value-of select="text()[following-sibling::*[self::m:sep]]"/>
              </mi>
              <mo>/</mo>
              <mi>
                <xsl:value-of select="text()[preceding-sibling::*[self::m:sep]]"/>
              </mi>
            </mrow>
          </xsl:when>
          <xsl:otherwise>
<!-- if identifier is composed only of one text child-->
            <mi>
              <xsl:value-of select="."/>
            </mi>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="./@type='vector'">
        <xsl:choose>
<!-- If child nodes exist assume presentation markup -->
          <xsl:when test="*">
            <xsl:choose>
<!-- if first child is msub do not bold subscript -->
              <xsl:when test="*[1][self::m:msub]">
                <msub>
                  <mrow>
                    <mstyle fontweight="bold">
                      <mrow>
                        <xsl:apply-templates select="m:msub/*[1]"/>
                      </mrow>
                    </mstyle>
                  </mrow>
                  <mrow>
                    <xsl:apply-templates select="m:msub/*[2]"/>
                  </mrow>
                </msub>
              </xsl:when>
<!-- if first child is msup do not bold superscript -->
              <xsl:when test="*[1][self::m:msup]">
                <msup>
                  <mrow>
                    <mstyle fontweight="bold">
                      <mrow>
                        <xsl:apply-templates select="m:msup/*[1]"/>
                      </mrow>
                    </mstyle>
                  </mrow>
                  <mrow>
                    <xsl:apply-templates select="msup/*[2]"/>
                  </mrow>
                </msup>
              </xsl:when>
<!-- if first child is msubsup do not bold scripts -->
              <xsl:when test="*[1][self::m:msubsup]">
                <msubsup>
                  <mrow>
                    <mstyle fontweight="bold">
                      <mrow>
                        <xsl:apply-templates select="m:msubsup/*[1]"/>
                      </mrow>
                    </mstyle>
                  </mrow>
                  <mrow>
                    <xsl:apply-templates select="m:msubsup/*[2]"/>
                  </mrow>
                  <mrow>
                    <xsl:apply-templates select="m:msubsup/*[3]"/>
                  </mrow>
                </msubsup>
              </xsl:when>
              <xsl:otherwise>
                <mrow>
                  <xsl:copy-of select="*"/>
                </mrow>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <mi fontweight="bold">
              <xsl:value-of select="text()"/>
            </mi>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
<!-- type 'set' seems to be deprecated (use 4.4.12 instead); besides, there is no easy way to translate set identifiers to chars in ISOMOPF -->
      <xsl:otherwise>
<!-- no type attribute provided -->
        <xsl:choose>
<!-- If children elements exist assume presentation markup-->
          <xsl:when test="*">
            <mrow>
              <xsl:copy-of select="*"/>
            </mrow>
          </xsl:when>
          <xsl:otherwise>
<!-- common case -->
            <mi>
              <xsl:value-of select="."/>
            </mi>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
<!-- externally defined symbols-->
  <xsl:template match="m:apply[*[1][self::m:csymbol]]">
    <mrow>
      <xsl:apply-templates select="m:csymbol[1]"/>
      <mfenced>
        <xsl:for-each select="*[position()!=1]">
          <xsl:apply-templates select="."/>
        </xsl:for-each>
      </mfenced>
    </mrow>
  </xsl:template>
  <xsl:template match="m:csymbol">
    <xsl:choose>
<!--test if children are not all text nodes, meaning there is markup assumed to be presentation markup-->
<!--perhaps it would be sufficient to test if there is more than one node or text node-->
      <xsl:when test="count(node()) != count(text())">
        <mrow>
          <xsl:copy-of select="*"/>
        </mrow>
      </xsl:when>
      <xsl:otherwise>
        <mo>
          <xsl:value-of select="."/>
        </mo>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="m:mtext">
    <xsl:copy-of select="."/>
  </xsl:template>
<!-- #################### 4.4.2 #################### -->
<!-- apply/apply -->
  <xsl:template match="m:apply[*[1][self::m:apply]]">
<!-- when the function itself is defined by other functions: (F+G)(x) -->
    <xsl:choose>
      <xsl:when test="count(*)&gt;=2">
        <mrow>
          <mfenced separators="">
            <xsl:apply-templates select="*[1]"/>
          </mfenced>
          <mfenced>
            <xsl:apply-templates select="*[position()!=1]"/>
          </mfenced>
        </mrow>
      </xsl:when>
      <xsl:otherwise>
<!-- apply only contains apply, no operand-->
        <mfenced separators="">
          <xsl:apply-templates select="*"/>
        </mfenced>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
<!-- force function or operator MathML 1.0 deprecated-->
  <xsl:template match="m:apply[*[1][self::m:fn]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:fn/*[1][self::m:apply]">
<!-- fn definition is complex, surround with brackets, but only one child-->
          <mfenced separators="">
            <mrow>
              <xsl:apply-templates select="m:fn/*"/>
            </mrow>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <mi>
            <xsl:apply-templates select="m:fn/*"/>
          </mi>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="count(*)&gt;1">
<!-- if no operands, don't put empty parentheses-->
        <mo>
          <xsl:value-of select="$ApplyFunction"/>
        </mo>
        <mfenced>
          <xsl:apply-templates select="*[position()!=1]"/>
        </mfenced>
      </xsl:if>
    </mrow>
  </xsl:template>
<!--first ci is supposed to be a function-->
  <xsl:template match="m:apply[*[1][self::m:ci]]">
    <mrow>
      <xsl:apply-templates select="m:ci[1]"/>
      <xsl:if test="count(*)&gt;1">
<!-- if no operands, don't put empty parentheses-->
        <mo>
          <xsl:value-of select="$ApplyFunction"/>
        </mo>
        <mfenced>
          <xsl:apply-templates select="*[position()!=1]"/>
        </mfenced>
      </xsl:if>
    </mrow>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:mo]]">
<!--operator assumed to be infix-->
    <xsl:choose>
      <xsl:when test="count(*)&gt;=3">
        <mrow>
          <xsl:for-each select="*[position()!=last() and  position()!=1]">
            <xsl:apply-templates select="."/>
            <xsl:copy-of select="preceding-sibling::m:mo"/>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()!=1 and position()=last()]"/>
        </mrow>
      </xsl:when>
      <xsl:when test="count(*)=2">
        <mrow>
          <xsl:copy-of select="m:mo[1]/*"/>
          <xsl:apply-templates select="*[2]"/>
        </mrow>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
<!-- intervals -->
  <xsl:template match="m:interval">
    <xsl:choose>
      <xsl:when test="count(*)=2">
<!--we have an interval defined by two real numbers-->
        <xsl:choose>
          <xsl:when test="@closure and @closure='open-closed'">
            <mfenced open="(" close="]">
              <xsl:apply-templates select="*[1]"/>
              <xsl:apply-templates select="*[2]"/>
            </mfenced>
          </xsl:when>
          <xsl:when test="@closure and @closure='closed-open'">
            <mfenced open="[" close=")">
              <xsl:apply-templates select="*[1]"/>
              <xsl:apply-templates select="*[2]"/>
            </mfenced>
          </xsl:when>
          <xsl:when test="@closure and @closure='closed'">
            <mfenced open="[" close="]">
              <xsl:apply-templates select="*[1]"/>
              <xsl:apply-templates select="*[2]"/>
            </mfenced>
          </xsl:when>
          <xsl:when test="@closure and @closure='open'">
            <mfenced open="(" close=")">
              <xsl:apply-templates select="*[1]"/>
              <xsl:apply-templates select="*[2]"/>
            </mfenced>
          </xsl:when>
          <xsl:otherwise>
<!--default is close-->
            <mfenced open="[" close="]">
              <xsl:apply-templates select="*[1]"/>
              <xsl:apply-templates select="*[2]"/>
            </mfenced>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
<!--we have an interval defined by a condition-->
        <mrow>
          <xsl:apply-templates select="m:condition"/>
        </mrow>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
<!-- inverse -->
  <xsl:template match="m:apply[*[1][self::m:apply]/m:inverse]">
    <mrow>
      <msup>
<!-- elementary classical functions have two templates: apply[func] for standard case, func[position()!=1] for inverse and compose case-->
        <mrow>
          <xsl:apply-templates select="m:apply[1]/*[2]"/>
        </mrow>
<!-- function to be inversed-->
        <mfenced>
          <mn>-1</mn>
        </mfenced>
      </msup>
      <xsl:if test="count(*)&gt;=2">
<!-- deal with operands, if any-->
        <mo>
          <xsl:value-of select="$ApplyFunction"/>
        </mo>
        <mfenced>
          <xsl:apply-templates select="*[position()!=1]"/>
        </mfenced>
      </xsl:if>
    </mrow>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:inverse]]">
    <msup>
<!-- elementary classical functions have two templates: apply[func] for standard case, func[position()!=1] for inverse and compose case-->
      <mrow>
        <xsl:apply-templates select="*[2]"/>
      </mrow>
<!-- function to be inversed-->
      <mfenced>
        <mn>-1</mn>
      </mfenced>
    </msup>
  </xsl:template>
<!-- conditions -->
<!-- no support for deprecated reln-->
  <xsl:template match="m:condition">
    <mrow>
      <xsl:apply-templates select="*"/>
    </mrow>
  </xsl:template>
<!-- domain of application -->
  <xsl:template match="m:domainofapplication">
    <mrow>
      <xsl:apply-templates select="*"/>
    </mrow>
  </xsl:template>
<!-- declare -->
  <xsl:template match="m:declare">
<!-- no rendering for declarations-->
  </xsl:template>
<!-- lambda -->
  <xsl:template match="m:lambda">
    <mrow>
      <mo>
        <xsl:value-of select="$lambda"/>
      </mo>
      <mrow>
        <mo>(</mo>
        <xsl:for-each select="m:bvar">
          <xsl:apply-templates select="."/>
          <mo>,</mo>
        </xsl:for-each>
        <xsl:apply-templates select="*[position()=last()]"/>
        <mo>)</mo>
      </mrow>
    </mrow>
  </xsl:template>
<!-- composition -->
  <xsl:template match="m:apply[*[1][self::m:apply]/m:compose]">
    <mrow>
<!-- elementary classical functions have two templates: apply[func] for standard case, func[position()!=1] for inverse and compose case-->
      <xsl:choose>
        <xsl:when test="count(*)&gt;=2">
<!-- if there are operands, mfence functions-->
          <mfenced>
            <mrow>
              <xsl:for-each select="m:apply[1]/*[position()!=1 and position()!=last()]">
                <xsl:apply-templates select="."/>
                <mo>
                  <xsl:value-of select="$SmallCircle"/>
                </mo>
<!-- compose functions -->
<!-- does not work, perhaps compfn, UNICODE 02218-->
              </xsl:for-each>
              <xsl:apply-templates select="m:apply[1]/*[position()=last()]"/>
            </mrow>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="m:apply[1]/*[position()!=1 and position()!=last()]">
            <xsl:apply-templates select="."/>
            <mo>
              <xsl:value-of select="$SmallCircle"/>
            </mo>
<!-- compose functions -->
<!-- does not work, perhaps compfn, UNICODE 02218-->
          </xsl:for-each>
          <xsl:apply-templates select="m:apply[1]/*[position()=last()]"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="count(*)&gt;=2">
<!-- deal with operands, if any-->
        <mo>
          <xsl:value-of select="$ApplyFunction"/>
        </mo>
        <mrow>
          <mo>(</mo>
          <xsl:for-each select="*[position()!=1 and position()!=last()]">
            <xsl:apply-templates select="."/>
            <mo>,</mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
          <mo>)</mo>
        </mrow>
      </xsl:if>
    </mrow>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:compose]]">
<!-- elementary classical functions have two templates: apply[func] for standard case, func[position()!=1] for inverse and compose case-->
    <xsl:for-each select="*[position()!=1 and position()!=last()]">
      <xsl:apply-templates select="."/>
      <mo>
        <xsl:value-of select="$SmallCircle"/>
      </mo>
<!-- compose functions -->
<!-- does not work, perhaps compfn, UNICODE 02218-->
    </xsl:for-each>
    <xsl:apply-templates select="*[position()=last()]"/>
  </xsl:template>
<!-- identity -->
  <xsl:template match="m:ident">
    <mi>id</mi>
  </xsl:template>
<!-- domain -->
  <xsl:template match="m:apply[*[1][self::m:domain]]">
    <mrow>
      <mi>domain</mi>
      <mfenced open="(" close=")">
        <xsl:apply-templates select="*[position()!=1]"/>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- codomain -->
  <xsl:template match="m:apply[*[1][self::m:codomain]]">
    <mrow>
      <mi>codomain</mi>
      <mfenced open="(" close=")">
        <xsl:apply-templates select="*[position()!=1]"/>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- image -->
  <xsl:template match="m:apply[*[1][self::m:image]]">
    <mrow>
      <mi>image</mi>
      <mfenced open="(" close=")">
        <xsl:apply-templates select="*[position()!=1]"/>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- piecewise -->
  <xsl:template match="m:piecewise">
    <mrow>
      <mfenced open="{{" close="">
        <mtable>
          <xsl:for-each select="m:piece">
            <mtr>
              <mtd>
                <xsl:apply-templates select="*[1]"/>
                <mspace width="0.3em"/>
                <m:mtext>if</m:mtext>
                <mspace width="0.3em"/>
                <xsl:apply-templates select="*[2]"/>
              </mtd>
            </mtr>
          </xsl:for-each>
          <xsl:if test="m:otherwise">
            <mtr>
              <mtd>
                <xsl:apply-templates select="m:otherwise/*"/>
                <mspace width="0.3em"/>
                <m:mtext>otherwise</m:mtext>
              </mtd>
            </mtr>
          </xsl:if>
        </mtable>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- #################### 4.4.3 #################### -->
<!-- quotient -->
  <xsl:template match="m:apply[*[1][self::m:quotient]]">
    <mrow>
<!-- the third notation uses UNICODE chars x0230A and x0230B -->
      <mo>integer part of</mo>
      <mrow>
        <xsl:choose>
<!-- surround with brackets if operands are composed-->
          <xsl:when test="*[2] and self::m:apply">
            <mfenced separators="">
              <xsl:apply-templates select="*[2]"/>
            </mfenced>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="*[2]"/>
          </xsl:otherwise>
        </xsl:choose>
        <mo>/</mo>
        <xsl:choose>
          <xsl:when test="*[3] and self::m:apply">
            <mfenced separators="">
              <xsl:apply-templates select="*[3]"/>
            </mfenced>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="*[3]"/>
          </xsl:otherwise>
        </xsl:choose>
      </mrow>
    </mrow>
  </xsl:template>
<!-- factorial -->
  <xsl:template match="m:apply[*[1][self::m:factorial]]">
    <mrow>
      <xsl:choose>
<!-- surround with brackets if operand is composed-->
        <xsl:when test="*[2][self::m:apply]">
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[2]"/>
        </xsl:otherwise>
      </xsl:choose>
      <mo>!</mo>
    </mrow>
  </xsl:template>
<!-- divide -->
  <xsl:template match="m:apply[*[1][self::m:divide]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="contains(@other,'scriptstyle')">
          <mfrac bevelled="true">
            <mrow>
              <xsl:apply-templates select="*[2]"/>
            </mrow>
            <mrow>
              <xsl:apply-templates select="*[3]"/>
            </mrow>
          </mfrac>
        </xsl:when>
        <xsl:otherwise>
          <mfrac>
            <mrow>
              <xsl:apply-templates select="*[2]"/>
            </mrow>
            <mrow>
              <xsl:apply-templates select="*[3]"/>
            </mrow>
          </mfrac>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- min -->
  <xsl:template match="m:apply[*[1][self::m:min]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:bvar">
<!-- if there are bvars-->
          <msub>
            <mi>min</mi>
            <mrow>
              <xsl:for-each select="m:bvar[position()!=last()]">
<!--select every bvar except the last one (position() only counts bvars, not the other siblings)-->
                <xsl:apply-templates select="."/>
                <mo>,</mo>
              </xsl:for-each>
              <xsl:apply-templates select="m:bvar[position()=last()]"/>
            </mrow>
          </msub>
          <mrow>
            <mo>{</mo>
            <xsl:apply-templates select="*[not(self::m:condition) and not(self::m:bvar)]"/>
            <xsl:if test="m:condition">
              <mo>|</mo>
              <xsl:apply-templates select="m:condition"/>
            </xsl:if>
            <mo>}</mo>
          </mrow>
        </xsl:when>
        <xsl:otherwise>
<!-- if there are no bvars-->
          <mo>min</mo>
          <mrow>
            <mo>{</mo>
            <mfenced open="" close="">
              <xsl:apply-templates select="*[not(self::m:condition) and not(self::m:min)]"/>
            </mfenced>
            <xsl:if test="m:condition">
              <mo>|</mo>
              <xsl:apply-templates select="m:condition"/>
            </xsl:if>
            <mo>}</mo>
          </mrow>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- min with domainofapplication -->
  <xsl:template match="m:apply[*[1][self::m:min] and m:domainofapplication]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:bvar">
<!-- bvar and domain of application -->
          <msub>
            <munder>
              <mi>min</mi>
              <xsl:apply-templates select="m:domainofapplication"/>
            </munder>
            <mrow>
              <xsl:for-each select="m:bvar[position()!=last()]">
<!--select every bvar except the last one (position() only counts bvars, not the other siblings)-->
                <xsl:apply-templates select="."/>
                <mo>,</mo>
              </xsl:for-each>
              <xsl:apply-templates select="m:bvar[position()=last()]"/>
            </mrow>
          </msub>
        </xsl:when>
        <xsl:otherwise>
          <munder>
            <mi>min</mi>
            <xsl:apply-templates select="m:domainofapplication"/>
          </munder>
        </xsl:otherwise>
      </xsl:choose>
      <mo>{</mo>
      <xsl:apply-templates select="*[not(self::m:condition or self::m:domainofapplication or self::m:bvar)]"/>
      <xsl:if test="m:condition">
        <mo>|</mo>
        <xsl:apply-templates select="m:condition"/>
      </xsl:if>
      <mo>}</mo>
    </mrow>
  </xsl:template>
<!-- max -->
  <xsl:template match="m:apply[*[1][self::m:max]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:bvar">
<!-- if there are bvars-->
          <msub>
            <mi>max</mi>
            <mrow>
              <xsl:for-each select="m:bvar[position()!=last()]">
<!--select every bvar except the last one (position() only counts bvars, not the other siblings)-->
                <xsl:apply-templates select="."/>
                <mo>,</mo>
              </xsl:for-each>
              <xsl:apply-templates select="m:bvar[position()=last()]"/>
            </mrow>
          </msub>
          <mrow>
            <mo>{</mo>
            <xsl:apply-templates select="*[not(self::m:condition) and not(self::m:bvar)]"/>
            <xsl:if test="m:condition">
              <mo>|</mo>
              <xsl:apply-templates select="m:condition"/>
            </xsl:if>
            <mo>}</mo>
          </mrow>
        </xsl:when>
        <xsl:otherwise>
<!-- if there are no bvars-->
          <mo>max</mo>
          <mrow>
            <mo>{</mo>
            <mfenced open="" close="">
              <xsl:apply-templates select="*[not(self::m:condition) and not(self::m:max)]"/>
            </mfenced>
            <xsl:if test="m:condition">
              <mo>|</mo>
              <xsl:apply-templates select="m:condition"/>
            </xsl:if>
            <mo>}</mo>
          </mrow>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- max with domainofapplication -->
  <xsl:template match="m:apply[*[1][self::m:max] and m:domainofapplication]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:bvar">
<!-- bvar and domain of application -->
          <msub>
            <munder>
              <mi>max</mi>
              <xsl:apply-templates select="m:domainofapplication"/>
            </munder>
            <mrow>
              <xsl:for-each select="m:bvar[position()!=last()]">
<!--select every bvar except the last one (position() only counts bvars, not the other siblings)-->
                <xsl:apply-templates select="."/>
                <mo>,</mo>
              </xsl:for-each>
              <xsl:apply-templates select="m:bvar[position()=last()]"/>
            </mrow>
          </msub>
        </xsl:when>
        <xsl:otherwise>
          <munder>
            <mi>max</mi>
            <xsl:apply-templates select="m:domainofapplication"/>
          </munder>
        </xsl:otherwise>
      </xsl:choose>
      <mo>{</mo>
      <xsl:apply-templates select="*[not(self::m:condition or self::m:domainofapplication or self::m:bvar)]"/>
      <xsl:if test="m:condition">
        <mo>|</mo>
        <xsl:apply-templates select="m:condition"/>
      </xsl:if>
      <mo>}</mo>
    </mrow>
  </xsl:template>
<!-- substraction(minus); unary or binary operator-->
  <xsl:template match="m:apply[*[1][self::m:minus]]">
    <mrow>
      <xsl:choose>
<!-- binary -->
        <xsl:when test="count(*)=3">
          <xsl:apply-templates select="*[2]"/>
          <mo>-</mo>
          <xsl:choose>
            <xsl:when test="(*[3][self::m:ci or self::m:cn] and contains(*[3]/text(),'-')) or *[3][self::m:apply]">
              <mfenced separators="">
                <xsl:apply-templates select="*[3]"/>
              </mfenced>
<!-- surround negative or complex things with brackets -->
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="*[3]"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
<!-- unary -->
          <mo>-</mo>
          <xsl:choose>
            <xsl:when test="(*[2][self::m:ci or self::m:cn] and contains(*[2]/text(),'-')) or *[2][self::m:apply]">
              <mfenced separators="">
<!-- surround negative or complex things with brackets -->
                <xsl:apply-templates select="*[position()=last()]"/>
              </mfenced>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="*[position()=last()]"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- addition -->
  <xsl:template match="m:apply[*[1][self::m:plus]]">
    <xsl:choose>
      <xsl:when test="count(*)&gt;=3">
        <mrow>
          <xsl:choose>
            <xsl:when test="(*[2][self::m:ci or self::m:cn] and contains(*[2]/text(),'-')) or (*[2][self::m:apply and child::m:minus])">
              <mfenced separators="">
                <xsl:apply-templates select="*[2]"/>
              </mfenced>
<!-- surround negative things with brackets -->
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="*[2]"/>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:for-each select="*[position()!=1 and position()!=2]">
            <xsl:choose>
              <xsl:when test="((self::m:ci or self::m:cn) and contains(./text(),'-')) or (self::m:apply and child::m:minus)">
<!-- surround negative things with brackets -->
                <mo>+</mo>
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
              </xsl:when>
              <xsl:otherwise>
                <mo>+</mo>
                <xsl:apply-templates select="."/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </mrow>
      </xsl:when>
      <xsl:when test="count(*)=2">
        <mrow>
          <mo>+</mo>
          <xsl:apply-templates select="*[2]"/>
        </mrow>
      </xsl:when>
      <xsl:otherwise>
        <mo>+</mo>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
<!-- power -->
  <xsl:template match="m:apply[*[1][self::m:power]]">
    <msup>
      <xsl:choose>
        <xsl:when test="*[2][self::m:apply]">
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[2]"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="*[3]"/>
    </msup>
  </xsl:template>
<!-- remainder -->
  <xsl:template match="m:apply[*[1][self::m:rem]]">
    <mrow>
      <xsl:choose>
<!-- surround with brackets if operands are composed-->
        <xsl:when test="*[2][self::m:apply]">
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[2]"/>
        </xsl:otherwise>
      </xsl:choose>
      <mo>mod</mo>
      <xsl:choose>
        <xsl:when test="*[3][self::m:apply]">
          <mfenced separators="">
            <xsl:apply-templates select="*[3]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[3]"/>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- multiplication -->
  <xsl:template match="m:apply[*[1][self::m:times]]">
    <xsl:choose>
      <xsl:when test="count(*)&gt;=3">
        <mrow>
          <xsl:for-each select="*[position()!=last() and  position()!=1]">
            <xsl:choose>
              <xsl:when test="m:plus">
<!--add brackets around + children for priority purpose-->
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
                <mo>
                  <xsl:value-of select="$InvisibleTimes"/>
                </mo>
              </xsl:when>
              <xsl:when test="m:minus">
<!--add brackets around - children for priority purpose-->
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
                <mo>
                  <xsl:value-of select="$InvisibleTimes"/>
                </mo>
              </xsl:when>
              <xsl:when test="(self::m:ci or self::m:cn) and contains(text(),'-')">
<!-- have to do it using contains because starts-with doesn't seem to work well in XT-->
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
                <mo>
                  <xsl:value-of select="$InvisibleTimes"/>
                </mo>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="."/>
                <mo>
                  <xsl:value-of select="$InvisibleTimes"/>
                </mo>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
          <xsl:for-each select="*[position()=last()]">
            <xsl:choose>
              <xsl:when test="m:plus">
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
              </xsl:when>
              <xsl:when test="m:minus">
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
              </xsl:when>
              <xsl:when test="(self::m:ci or self::m:cn) and contains(text(),'-')">
<!-- have to do it using contains because starts-with doesn't seem to work well in  XT-->
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="."/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </mrow>
      </xsl:when>
      <xsl:when test="count(*)=2">
<!-- unary -->
        <mrow>
          <mo>
            <xsl:value-of select="$InvisibleTimes"/>
          </mo>
          <xsl:choose>
            <xsl:when test="m:plus">
              <mfenced separators="">
                <xsl:apply-templates select="*[2]"/>
              </mfenced>
            </xsl:when>
            <xsl:when test="m:minus">
              <mfenced separators="">
                <xsl:apply-templates select="*[2]"/>
              </mfenced>
            </xsl:when>
            <xsl:when test="*[2][self::m:ci or self::m:cn] and contains(*[2]/text(),'-')">
              <mfenced separators="">
                <xsl:apply-templates select="*[2]"/>
              </mfenced>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="*[2]"/>
            </xsl:otherwise>
          </xsl:choose>
        </mrow>
      </xsl:when>
      <xsl:otherwise>
<!-- no operand -->
        <mo>
          <xsl:value-of select="$InvisibleTimes"/>
        </mo>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
<!-- root -->
  <xsl:template match="m:apply[*[1][self::m:root]]">
    <xsl:choose>
      <xsl:when test="m:degree">
        <xsl:choose>
          <xsl:when test="m:degree/m:cn/text()='2'">
<!--if degree=2 display a standard square root-->
            <msqrt>
              <xsl:apply-templates select="*[3]"/>
            </msqrt>
          </xsl:when>
          <xsl:otherwise>
            <mroot>
              <xsl:apply-templates select="*[3]"/>
              <mrow>
                <xsl:apply-templates select="m:degree/*"/>
              </mrow>
            </mroot>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
<!-- no degree specified-->
        <msqrt>
          <xsl:apply-templates select="*[2]"/>
        </msqrt>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
<!-- greatest common divisor -->
  <xsl:template match="m:apply[*[1][self::m:gcd]]">
    <mrow>
      <mi>gcd</mi>
      <mo>
        <xsl:value-of select="$ApplyFunction"/>
      </mo>
      <mfenced>
        <xsl:apply-templates select="*[position()!=1]"/>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- AND -->
  <xsl:template match="m:apply[*[1][self::m:and]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="count(*)&gt;=3">
<!-- at least two operands (common case)-->
          <xsl:for-each select="*[position()!=last() and  position()!=1]">
            <xsl:choose>
              <xsl:when test="m:or">
<!--add brackets around OR children for priority purpose-->
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
                <mo>
                  <xsl:value-of select="$And"/>
                </mo>
              </xsl:when>
              <xsl:when test="m:xor">
<!--add brackets around XOR children for priority purpose-->
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
                <mo>
                  <xsl:value-of select="$And"/>
                </mo>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="."/>
                <mo>
                  <xsl:value-of select="$And"/>
                </mo>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
          <xsl:for-each select="*[position()=last()]">
            <xsl:choose>
              <xsl:when test="m:or">
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
              </xsl:when>
              <xsl:when test="m:xor">
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="."/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </xsl:when>
        <xsl:when test="count(*)=2">
          <mo>
            <xsl:value-of select="$And"/>
          </mo>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:otherwise>
          <mo>
            <xsl:value-of select="$And"/>
          </mo>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- OR -->
  <xsl:template match="m:apply[*[1][self::m:or]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="count(*)&gt;=3">
          <xsl:for-each select="*[position()!=last() and  position()!=1]">
            <xsl:apply-templates select="."/>
            <mo>
              <xsl:value-of select="$Or"/>
            </mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:when test="count(*)=2">
          <mo>
            <xsl:value-of select="$Or"/>
          </mo>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:otherwise>
          <mo>
            <xsl:value-of select="$Or"/>
          </mo>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- XOR -->
  <xsl:template match="m:apply[*[1][self::m:xor]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="count(*)&gt;=3">
          <xsl:for-each select="*[position()!=last() and  position()!=1]">
            <xsl:apply-templates select="."/>
            <mo>xor</mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:when test="count(*)=2">
          <mo>xor</mo>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:otherwise>
          <mo>xor</mo>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- NOT -->
  <xsl:template match="m:apply[*[1][self::m:not]]">
    <mrow>
      <mo>
        <xsl:value-of select="$Not"/>
      </mo>
      <xsl:choose>
        <xsl:when test="m:apply">
<!--add brackets around OR,AND,XOR children for priority purpose-->
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[2]"/>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- implies -->
  <xsl:template match="m:apply[*[1][self::m:implies]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$DoubleRightArrow"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:implies]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$DoubleRightArrow"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- for all-->
  <xsl:template match="m:apply[*[1][self::m:forall]]">
    <mrow>
      <mo>
        <xsl:value-of select="$ForAll"/>
      </mo>
      <mrow>
        <xsl:for-each select="m:bvar[position()!=last()]">
          <xsl:apply-templates select="."/>
          <mo>,</mo>
        </xsl:for-each>
        <xsl:apply-templates select="m:bvar[position()=last()]"/>
      </mrow>
      <xsl:if test="m:condition">
        <mrow>
          <mo>,</mo>
          <xsl:apply-templates select="m:condition"/>
        </mrow>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="m:apply">
          <mo>:</mo>
          <xsl:apply-templates select="m:apply"/>
        </xsl:when>
        <xsl:when test="m:reln">
          <mo>:</mo>
          <xsl:apply-templates select="m:reln"/>
        </xsl:when>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- exist-->
  <xsl:template match="m:apply[*[1][self::m:exists]]">
    <mrow>
      <mo>
        <xsl:value-of select="$Exists"/>
      </mo>
      <mrow>
        <xsl:for-each select="m:bvar[position()!=last()]">
          <xsl:apply-templates select="."/>
          <mo>,</mo>
        </xsl:for-each>
        <xsl:apply-templates select="m:bvar[position()=last()]"/>
      </mrow>
      <xsl:if test="m:condition">
        <mrow>
          <mo>,</mo>
          <xsl:apply-templates select="m:condition"/>
        </mrow>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="m:apply">
          <mo>:</mo>
          <xsl:apply-templates select="m:apply"/>
        </xsl:when>
        <xsl:when test="m:reln">
          <mo>:</mo>
          <xsl:apply-templates select="m:reln"/>
        </xsl:when>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- absolute value -->
  <xsl:template match="m:apply[*[1][self::m:abs]]">
    <mrow>
      <mo>|</mo>
      <xsl:apply-templates select="*[position()=last()]"/>
      <mo>|</mo>
    </mrow>
  </xsl:template>
<!-- conjugate -->
  <xsl:template match="m:apply[*[1][self::m:conjugate]]">
    <mover>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$ovbar"/>
      </mo>
<!-- does not work, UNICODE x0233D  or perhaps OverBar-->
    </mover>
  </xsl:template>
<!-- argument of complex number -->
  <xsl:template match="m:apply[*[1][self::m:arg]]">
    <mrow>
      <mi>arg</mi>
      <mo>
        <xsl:value-of select="$ApplyFunction"/>
      </mo>
      <mfenced separators="">
        <xsl:apply-templates select="*[2]"/>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- real part of complex number -->
  <xsl:template match="m:apply[*[1][self::m:real]]">
    <mrow>
      <mi>
        <xsl:text disable-output-escaping="yes">&#8476;</xsl:text>
<!-- &Re; or &realpart; should work-->
      </mi>
      <mo>
        <xsl:value-of select="$ApplyFunction"/>
      </mo>
      <mfenced separators="">
        <xsl:apply-templates select="*[2]"/>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- imaginary part of complex number -->
  <xsl:template match="m:apply[*[1][self::m:imaginary]]">
    <mrow>
      <mi>
        <xsl:text disable-output-escaping="yes">&#8465;</xsl:text>
<!-- &Im; or &impartl should work-->
      </mi>
      <mo>
        <xsl:value-of select="$ApplyFunction"/>
      </mo>
      <mfenced separators="">
        <xsl:apply-templates select="*[2]"/>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- lowest common multiple -->
  <xsl:template match="m:apply[*[1][self::m:lcm]]">
    <mrow>
      <mi>lcm</mi>
      <mo>
        <xsl:value-of select="$ApplyFunction"/>
      </mo>
      <mfenced>
        <xsl:apply-templates select="*[position()!=1]"/>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- floor -->
  <xsl:template match="m:apply[*[1][self::m:floor]]">
    <mrow>
      <mo>
        <xsl:value-of select="$LeftFloor"/>
      </mo>
      <xsl:apply-templates select="*[position()=last()]"/>
      <mo>
        <xsl:value-of select="$RightFloor"/>
      </mo>
    </mrow>
  </xsl:template>
<!-- ceiling -->
  <xsl:template match="m:apply[*[1][self::m:ceiling]]">
    <mrow>
      <mo>
        <xsl:value-of select="$LeftCeiling"/>
      </mo>
      <xsl:apply-templates select="*[position()=last()]"/>
      <mo>
        <xsl:value-of select="$RightCeiling"/>
      </mo>
    </mrow>
  </xsl:template>
<!-- #################### 4.4.4 #################### -->
<!-- equal to -->
  <xsl:template name="eqRel">
    <xsl:choose>
      <xsl:when test="count(*)&gt;=3">
        <mrow>
          <xsl:for-each select="*[position()!=1 and position()!=last()]">
            <xsl:apply-templates select="."/>
            <mo>=</mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </mrow>
      </xsl:when>
      <xsl:when test="count(*)=2">
        <mrow>
          <mo>=</mo>
          <xsl:apply-templates select="*[2]"/>
        </mrow>
      </xsl:when>
      <xsl:otherwise>
        <mo>=</mo>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:eq]]">
    <xsl:call-template name="eqRel"/>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:eq]]">
    <xsl:call-template name="eqRel"/>
  </xsl:template>
<!-- not equal to -->
  <xsl:template match="m:apply[*[1][self::m:neq]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$NotEqual"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:neq]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$NotEqual"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- greater than -->
  <xsl:template name="gtRel">
    <xsl:choose>
      <xsl:when test="count(*)&gt;=3">
        <mrow>
          <xsl:for-each select="*[position()!=1 and position()!=last()]">
            <xsl:apply-templates select="."/>
            <mo>
              <xsl:value-of select="$gt"/>
            </mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </mrow>
      </xsl:when>
      <xsl:when test="count(*)=2">
        <mrow>
          <mo>
            <xsl:value-of select="$gt"/>
          </mo>
          <xsl:apply-templates select="*[2]"/>
        </mrow>
      </xsl:when>
      <xsl:otherwise>
        <mo>
          <xsl:value-of select="$gt"/>
        </mo>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:gt]]">
    <xsl:call-template name="gtRel"/>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:gt]]">
    <xsl:call-template name="gtRel"/>
  </xsl:template>
<!-- less than -->
  <xsl:template name="ltRel">
    <xsl:choose>
      <xsl:when test="count(*)&gt;=3">
        <mrow>
          <xsl:for-each select="*[position()!=1 and position()!=last()]">
            <xsl:apply-templates select="."/>
            <mo>
              <xsl:value-of select="$lt"/>
            </mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </mrow>
      </xsl:when>
      <xsl:when test="count(*)=2">
        <mrow>
          <mo>
            <xsl:value-of select="$lt"/>
          </mo>
          <xsl:apply-templates select="*[2]"/>
        </mrow>
      </xsl:when>
      <xsl:otherwise>
        <mo>
          <xsl:value-of select="$lt"/>
        </mo>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:lt]]">
    <xsl:call-template name="ltRel"/>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:lt]]">
    <xsl:call-template name="ltRel"/>
  </xsl:template>
<!-- greater than or equal to -->
  <xsl:template name="geqRel">
    <xsl:choose>
      <xsl:when test="count(*)&gt;=3">
        <mrow>
          <xsl:for-each select="*[position()!=1 and position()!=last()]">
            <xsl:apply-templates select="."/>
            <mo>
              <xsl:value-of select="$GreaterEqual"/>
            </mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </mrow>
      </xsl:when>
      <xsl:when test="count(*)=2">
        <mrow>
          <mo>
            <xsl:value-of select="$GreaterEqual"/>
          </mo>
          <xsl:apply-templates select="*[2]"/>
        </mrow>
      </xsl:when>
      <xsl:otherwise>
        <mo>
          <xsl:value-of select="$GreaterEqual"/>
        </mo>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:geq]]">
    <xsl:call-template name="geqRel"/>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:geq]]">
    <xsl:call-template name="geqRel"/>
  </xsl:template>
<!-- less than or equal to -->
  <xsl:template name="leqRel">
    <xsl:choose>
      <xsl:when test="count(*)&gt;=3">
        <mrow>
          <xsl:for-each select="*[position()!=1 and position()!=last()]">
            <xsl:apply-templates select="."/>
            <mo>
              <xsl:value-of select="$LessEqual"/>
            </mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </mrow>
      </xsl:when>
      <xsl:when test="count(*)=2">
        <mrow>
          <mo>
            <xsl:value-of select="$LessEqual"/>
          </mo>
          <xsl:apply-templates select="*[2]"/>
        </mrow>
      </xsl:when>
      <xsl:otherwise>
        <mo>
          <xsl:value-of select="$LessEqual"/>
        </mo>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:leq]]">
    <xsl:call-template name="leqRel"/>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:leq]]">
    <xsl:call-template name="leqRel"/>
  </xsl:template>
<!-- equivalent -->
  <xsl:template name="equivRel">
    <xsl:choose>
      <xsl:when test="count(*)&gt;=3">
        <mrow>
          <xsl:for-each select="*[position()!=1 and position()!=last()]">
            <xsl:apply-templates select="."/>
            <mo>
              <xsl:value-of select="$equiv"/>
            </mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </mrow>
      </xsl:when>
      <xsl:when test="count(*)=2">
        <mrow>
          <mo>
            <xsl:value-of select="$equiv"/>
          </mo>
          <xsl:apply-templates select="*[2]"/>
        </mrow>
      </xsl:when>
      <xsl:otherwise>
        <mo>
          <xsl:value-of select="$equiv"/>
        </mo>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:equivalent]]">
    <xsl:call-template name="equivRel"/>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:equivalent]]">
    <xsl:call-template name="equivRel"/>
  </xsl:template>
<!-- approximately equal -->
  <xsl:template match="m:apply[*[1][self::m:approx]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:text disable-output-escaping="yes">&#8776;</xsl:text>
<!-- &TildeTilde; or &approx; should work-->
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:approx]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:text disable-output-escaping="yes">&#8776;</xsl:text>
<!-- &TildeTilde; or &approx; should work-->
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- factor of -->
  <xsl:template match="m:apply[*[1][self::m:factorof]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>|</mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- #################### 4.4.5 #################### -->
<!-- integral -->
  <xsl:template match="m:apply[*[1][self::m:int]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:condition">
<!-- integration domain expressed by a condition-->
          <msub>
            <mo>
              <xsl:value-of select="$Integral"/>
            </mo>
            <xsl:apply-templates select="m:condition"/>
          </msub>
          <mrow>
            <xsl:apply-templates select="*[position()=last()]"/>
          </mrow>
          <mrow>
            <mo>d<!--DifferentialD does not work--></mo>
            <xsl:apply-templates select="m:bvar"/>
          </mrow>
        </xsl:when>
        <xsl:when test="m:domainofapplication">
<!-- integration domain expressed by a domain of application-->
          <msub>
            <mo>
              <xsl:value-of select="$Integral"/>
            </mo>
            <xsl:apply-templates select="m:domainofapplication"/>
          </msub>
          <mrow>
            <xsl:apply-templates select="*[position()=last()]"/>
          </mrow>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="m:interval">
<!-- integration domain expressed by an interval-->
              <msubsup>
                <mo>
                  <xsl:value-of select="$Integral"/>
                </mo>
                <xsl:apply-templates select="m:interval/*[1]"/>
                <xsl:apply-templates select="m:interval/*[2]"/>
              </msubsup>
              <xsl:apply-templates select="*[position()=last()]"/>
              <mo>d<!--DifferentialD does not work--></mo>
              <xsl:apply-templates select="m:bvar"/>
            </xsl:when>
            <xsl:when test="m:lowlimit">
<!-- integration domain expressed by lower and upper limits-->
              <msubsup>
                <mo>
                  <xsl:value-of select="$Integral"/>
                </mo>
                <mrow>
                  <xsl:apply-templates select="m:lowlimit"/>
                </mrow>
                <mrow>
                  <xsl:apply-templates select="m:uplimit"/>
                </mrow>
              </msubsup>
              <xsl:apply-templates select="*[position()=last()]"/>
              <mo>d<!--DifferentialD does not work--></mo>
              <xsl:apply-templates select="m:bvar"/>
            </xsl:when>
            <xsl:otherwise>
              <mo>
                <xsl:value-of select="$Integral"/>
              </mo>
              <xsl:apply-templates select="*[position()=last()]"/>
              <mo>d<!--DifferentialD does not work--></mo>
              <xsl:apply-templates select="m:bvar"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- differentiation -->
  <xsl:template match="m:apply[*[1][self::m:diff]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:bvar/m:degree">
<!-- if the order of the derivative is specified-->
          <xsl:choose>
            <xsl:when test="contains(m:bvar/m:degree/m:cn/text(),'1') and string-length(normalize-space(m:bvar/m:degree/m:cn/text()))=1">
              <mfrac>
                <mo>d<!--DifferentialD does not work--></mo>
                <mrow>
                  <mo>d<!--DifferentialD does not work--></mo>
                  <xsl:apply-templates select="m:bvar/*[not(self::m:degree)]"/>
                </mrow>
              </mfrac>
              <mrow>
                <xsl:choose>
                  <xsl:when test="m:apply[position()=last()]/m:fn[1]">
                    <xsl:apply-templates select="*[position()=last()]"/>
                  </xsl:when>
<!--add brackets around expression if not a function-->
                  <xsl:otherwise>
                    <mfenced separators="">
                      <xsl:apply-templates select="*[position()=last()]"/>
                    </mfenced>
                  </xsl:otherwise>
                </xsl:choose>
              </mrow>
            </xsl:when>
            <xsl:otherwise>
<!-- if the order of the derivative is not 1-->
              <mfrac>
                <mrow>
                  <msup>
                    <mo>d<!--DifferentialD does not work--></mo>
                    <mrow>
                      <xsl:apply-templates select="m:bvar/m:degree"/>
                    </mrow>
                  </msup>
                </mrow>
                <mrow>
                  <mo>d<!--DifferentialD does not work--></mo>
                  <msup>
                    <mrow>
                      <xsl:apply-templates select="m:bvar/*[not(self::m:degree)]"/>
                    </mrow>
                    <mrow>
                      <xsl:apply-templates select="m:bvar/m:degree"/>
                    </mrow>
                  </msup>
                </mrow>
              </mfrac>
              <mrow>
                <xsl:choose>
                  <xsl:when test="m:apply[position()=last()]/m:fn[1]">
                    <xsl:apply-templates select="*[position()=last()]"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <mfenced separators="">
                      <xsl:apply-templates select="*[position()=last()]"/>
                    </mfenced>
                  </xsl:otherwise>
                </xsl:choose>
              </mrow>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
<!-- if no order is specified, default to 1-->
          <xsl:choose>
            <xsl:when test="count(*)&lt;=2"><!--if just a function identifier, use f' instead of d(f)/dx--><xsl:apply-templates select="*[2]"/>'
    </xsl:when>
            <xsl:otherwise>
              <mfrac>
                <mo>d<!--DifferentialD does not work--></mo>
                <mrow>
                  <mo>d<!--DifferentialD does not work--></mo>
                  <xsl:apply-templates select="m:bvar"/>
                </mrow>
              </mfrac>
              <mrow>
                <xsl:choose>
                  <xsl:when test="m:apply[position()=last()]/m:fn[1]">
                    <xsl:apply-templates select="*[position()=last()]"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <mfenced separators="">
                      <xsl:apply-templates select="*[position()=last()]"/>
                    </mfenced>
                  </xsl:otherwise>
                </xsl:choose>
              </mrow>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- partial differentiation -->
<!-- the latest working draft sets the default rendering of the numerator
to only one mfrac with one PartialD for the numerator, exponent being the sum
of every partial diff's orders; not supported yet (I am not sure it is even possible with XSLT)-->
  <xsl:template match="m:apply[*[1][self::m:partialdiff]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:list">
          <msub>
            <mo>D</mo>
            <mfenced separators="," open="" close="">
              <xsl:apply-templates select="m:list/*"/>
            </mfenced>
          </msub>
          <mfenced open="(" close=")">
            <xsl:apply-templates select="*[not(self::m:list)]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:for-each select="m:bvar">
            <xsl:choose>
              <xsl:when test="m:degree">
<!-- if the order of the derivative is specified-->
                <xsl:choose>
                  <xsl:when test="contains(m:degree/m:cn/text(),'1') and string-length(normalize-space(m:degree/m:cn/text()))=1">
                    <mfrac>
                      <mrow>
                        <mo>
                          <xsl:value-of select="$PartialD"/>
                        </mo>
                      </mrow>
                      <mrow>
                        <mo>
                          <xsl:value-of select="$PartialD"/>
                        </mo>
                        <xsl:apply-templates select="*[not(self::m:degree)]"/>
                      </mrow>
                    </mfrac>
                  </xsl:when>
                  <xsl:otherwise>
<!-- if the order of the derivative is not 1-->
                    <mfrac>
                      <mrow>
                        <msup>
                          <mrow>
                            <mo>
                              <xsl:value-of select="$PartialD"/>
                            </mo>
                          </mrow>
                          <mrow>
                            <xsl:apply-templates select="m:degree"/>
                          </mrow>
                        </msup>
                      </mrow>
                      <mrow>
                        <mrow>
                          <mo>
                            <xsl:value-of select="$PartialD"/>
                          </mo>
                        </mrow>
                        <msup>
                          <mrow>
                            <xsl:apply-templates select="*[not(self::m:degree)]"/>
                          </mrow>
                          <mrow>
                            <xsl:apply-templates select="m:degree"/>
                          </mrow>
                        </msup>
                      </mrow>
                    </mfrac>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:when>
              <xsl:otherwise>
<!-- if no order is specified, default to 1-->
                <mfrac>
                  <mrow>
                    <mo>
                      <xsl:value-of select="$PartialD"/>
                    </mo>
                  </mrow>
                  <mrow>
                    <mo>
                      <xsl:value-of select="$PartialD"/>
                    </mo>
                    <xsl:apply-templates select="."/>
                  </mrow>
                </mfrac>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
          <mrow>
            <xsl:choose>
              <xsl:when test="m:apply[position()=last()]/m:fn[1]">
                <xsl:apply-templates select="*[position()=last()]"/>
              </xsl:when>
<!--add brackets around expression if not a function-->
              <xsl:otherwise>
                <mfenced separators="">
                  <xsl:apply-templates select="*[position()=last()]"/>
                </mfenced>
              </xsl:otherwise>
            </xsl:choose>
          </mrow>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- lowlimit -->
  <xsl:template match="m:lowlimit">
    <xsl:apply-templates select="*"/>
  </xsl:template>
<!-- uplimit -->
  <xsl:template match="m:uplimit">
    <xsl:apply-templates select="*"/>
  </xsl:template>
<!-- bvar -->
  <xsl:template match="m:bvar">
    <xsl:apply-templates select="*"/>
  </xsl:template>
<!-- degree -->
  <xsl:template match="m:degree">
    <xsl:apply-templates select="*"/>
  </xsl:template>
<!-- divergence -->
  <xsl:template match="m:apply[*[1][self::m:divergence]]">
    <mrow>
      <mi>div</mi>
      <xsl:choose>
        <xsl:when test="*[2][self::m:apply] or (*[2][self::m:ci or self::m:cn] and contains(*[2]/text(),'-'))">
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[2]"/>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- gradient -->
  <xsl:template match="m:apply[*[1][self::m:grad]]">
    <mrow>
      <mi>grad</mi>
      <xsl:choose>
        <xsl:when test="*[2][self::m:apply] or (*[2][self::m:ci or self::m:cn] and contains(*[2]/text(),'-'))">
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[2]"/>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- vector calculus curl -->
  <xsl:template match="m:apply[*[1][self::m:curl]]">
    <mrow>
      <mi>curl</mi>
      <xsl:choose>
        <xsl:when test="*[2][self::m:apply] or (*[2][self::m:ci or self::m:cn] and contains(*[2]/text(),'-'))">
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[2]"/>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- laplacian -->
  <xsl:template match="m:apply[*[1][self::m:laplacian]]">
    <mrow>
      <msup>
        <mo>
          <xsl:text disable-output-escaping="yes">&#8711;</xsl:text>
        </mo>
<!-- Del or nabla should work-->
        <mn>2</mn>
      </msup>
      <xsl:choose>
        <xsl:when test="*[2][self::m:apply] or (*[2][self::m:ci or self::m:cn] and contains(*[2]/text(),'-'))">
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[2]"/>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- #################### 4.4.6 #################### -->
<!-- set -->
  <xsl:template match="m:set">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:condition">
<!-- set defined by a condition-->
          <mo>{</mo>
          <mrow>
            <mfenced open="" close="">
              <xsl:apply-templates select="m:bvar"/>
            </mfenced>
            <mo>|</mo>
            <xsl:apply-templates select="m:condition"/>
          </mrow>
          <mo>}</mo>
        </xsl:when>
        <xsl:otherwise>
<!-- set defined by an enumeration -->
          <mfenced open="{{" close="}}" separators=",">
            <xsl:apply-templates select="*"/>
          </mfenced>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- list -->
<!-- sorting is not supported yet; not sure we should do it; anyway, can be  done using xsl:sort-->
  <xsl:template match="m:list">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:condition">
<!-- set defined by a condition-->
          <mo>[</mo>
          <mrow>
            <mfenced open="" close="">
              <xsl:apply-templates select="m:bvar"/>
            </mfenced>
            <mo>|</mo>
            <xsl:apply-templates select="m:condition"/>
          </mrow>
          <mo>]</mo>
        </xsl:when>
        <xsl:otherwise>
<!-- set defined by an enumeration -->
          <mfenced open="[" close="]">
            <xsl:apply-templates select="*"/>
          </mfenced>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- union -->
  <xsl:template match="m:apply[*[1][self::m:union]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="count(*)&gt;=3">
          <xsl:for-each select="*[position()!=last() and  position()!=1]">
            <xsl:apply-templates select="."/>
            <mo>
              <xsl:value-of select="$Union"/>
            </mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:when test="count(*)=2">
          <mo>
            <xsl:value-of select="$Union"/>
          </mo>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:otherwise>
          <mo>
            <xsl:value-of select="$Union"/>
          </mo>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- intersection -->
  <xsl:template match="m:apply[*[1][self::m:intersect]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="count(*)&gt;=3">
          <xsl:for-each select="*[position()!=last() and  position()!=1]">
            <xsl:choose>
              <xsl:when test="m:union">
<!-- add brackets around UNION children for priority purpose: intersection has higher precedence than union -->
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
                <mo>
                  <xsl:value-of select="$Intersection"/>
                </mo>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="."/>
                <mo>
                  <xsl:value-of select="$Intersection"/>
                </mo>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:when test="count(*)=2">
          <mo>
            <xsl:value-of select="$Intersection"/>
          </mo>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:otherwise>
          <mo>
            <xsl:value-of select="$Intersection"/>
          </mo>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- inclusion -->
  <xsl:template match="m:apply[*[1][self::m:in]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$isin"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:in]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$isin"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- exclusion -->
  <xsl:template match="m:apply[*[1][self::m:notin]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$notin"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:notin]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$notin"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- containment (subset of)-->
  <xsl:template name="subsetRel">
    <mrow>
      <xsl:choose>
        <xsl:when test="count(*)&gt;=3">
          <xsl:for-each select="*[position()!=last() and  position()!=1]">
            <xsl:apply-templates select="."/>
            <mo>
              <xsl:value-of select="$SubsetEqual"/>
            </mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:when test="count(*)=2">
          <mo>
            <xsl:value-of select="$SubsetEqual"/>
          </mo>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:otherwise>
          <mo>
            <xsl:value-of select="$SubsetEqual"/>
          </mo>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:subset]]">
    <xsl:call-template name="subsetRel"/>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:subset]]">
    <xsl:call-template name="subsetRel"/>
  </xsl:template>
<!-- containment (proper subset of) -->
  <xsl:template name="prsubsetRel">
    <mrow>
      <xsl:choose>
        <xsl:when test="count(*)&gt;=3">
          <xsl:for-each select="*[position()!=last() and  position()!=1]">
            <xsl:apply-templates select="."/>
            <mo>
              <xsl:value-of select="$Subset"/>
            </mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:when test="count(*)=2">
          <mo>
            <xsl:value-of select="$Subset"/>
          </mo>
          <xsl:apply-templates select="*[position()=last()]"/>
        </xsl:when>
        <xsl:otherwise>
          <mo>
            <xsl:value-of select="$Subset"/>
          </mo>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:prsubset]]">
    <xsl:call-template name="prsubsetRel"/>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:prsubset]]">
    <xsl:call-template name="prsubsetRel"/>
  </xsl:template>
<!-- perhaps Subset and SubsetEqual signs are used in place of one another ; not according to the spec -->
<!-- containment (not subset of)-->
  <xsl:template match="m:apply[*[1][self::m:notsubset]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$NotSubset"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:notsubset]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$NotSubset"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- containment (not proper subset of) -->
  <xsl:template match="m:apply[*[1][self::m:notprsubset]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$NotSubsetEqual"/>
      </mo>
<!-- does not work, perhaps nsube, or nsubE, or nsubseteqq or nsubseteq, UNICODE x02288-->
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:notprsubset]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$NotSubsetEqual"/>
      </mo>
<!-- does not work, perhaps nsube, or nsubE, or nsubseteqq or nsubseteq, UNICODE x02288-->
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- difference of two sets -->
  <xsl:template match="m:apply[*[1][self::m:setdiff]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$Backslash"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- cardinality -->
  <xsl:template match="m:apply[*[1][self::m:card]]">
    <mrow>
      <mo>|</mo>
      <xsl:apply-templates select="*[position()=last()]"/>
      <mo>|</mo>
    </mrow>
  </xsl:template>
<!-- cartesian product -->
  <xsl:template match="m:apply[*[1][self::m:cartesianproduct]]">
    <xsl:choose>
      <xsl:when test="count(*)&gt;=3">
        <mrow>
          <xsl:for-each select="*[position()!=last() and  position()!=1]">
            <xsl:choose>
              <xsl:when test="m:plus">
<!--add brackets around + children for priority purpose-->
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
                <mo>
                  <xsl:value-of select="$times"/>
                </mo>
              </xsl:when>
              <xsl:when test="m:minus">
<!--add brackets around - children for priority purpose-->
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
                <mo>
                  <xsl:value-of select="$times"/>
                </mo>
              </xsl:when>
              <xsl:when test="(self::m:ci or self::m:cn) and contains(text(),'-')">
<!-- have to do it using contains because starts-with doesn't seem to work well in XT-->
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
                <mo>
                  <xsl:value-of select="$times"/>
                </mo>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="."/>
                <mo>
                  <xsl:value-of select="$times"/>
                </mo>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
          <xsl:for-each select="*[position()=last()]">
            <xsl:choose>
              <xsl:when test="m:plus">
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
              </xsl:when>
              <xsl:when test="m:minus">
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
              </xsl:when>
              <xsl:when test="(self::m:ci or self::m:cn) and contains(text(),'-')">
<!-- have to do it using contains because starts-with doesn't seem to work well in  XT-->
                <mfenced separators="">
                  <xsl:apply-templates select="."/>
                </mfenced>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="."/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </mrow>
      </xsl:when>
      <xsl:when test="count(*)=2">
<!-- unary -->
        <mrow>
          <mo>
            <xsl:value-of select="$times"/>
          </mo>
          <xsl:choose>
            <xsl:when test="m:plus">
              <mfenced separators="">
                <xsl:apply-templates select="*[2]"/>
              </mfenced>
            </xsl:when>
            <xsl:when test="m:minus">
              <mfenced separators="">
                <xsl:apply-templates select="*[2]"/>
              </mfenced>
            </xsl:when>
            <xsl:when test="*[2][self::m:ci or self::m:cn] and contains(*[2]/text(),'-')">
              <mfenced separators="">
                <xsl:apply-templates select="*[2]"/>
              </mfenced>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="*[2]"/>
            </xsl:otherwise>
          </xsl:choose>
        </mrow>
      </xsl:when>
      <xsl:otherwise>
<!-- no operand -->
        <mo>
          <xsl:value-of select="$InvisibleTimes"/>
        </mo>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
<!-- #################### 4.4.7 #################### -->
<!-- sum -->
  <xsl:template match="m:apply[*[1][self::m:sum]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:condition and m:domainofapplication">
<!-- domainofapplication as well as condition -->
          <munder>
            <mo>
              <xsl:value-of select="$Sum"/>
            </mo>
            <mrow>
              <munder>
                <mrow>
                  <xsl:apply-templates select="m:domainofapplication"/>
                </mrow>
                <mrow>
                  <xsl:apply-templates select="m:condition"/>
                </mrow>
              </munder>
            </mrow>
          </munder>
        </xsl:when>
        <xsl:when test="m:condition and m:lowlimit and m:uplimit">
<!-- uplimit and lowlimit as well as condition -->
          <munderover>
            <mo>
              <xsl:value-of select="$Sum"/>
            </mo>
            <mrow>
              <munder>
                <mrow>
                  <xsl:apply-templates select="m:bvar"/>
                  <mo>=</mo>
                  <xsl:apply-templates select="m:lowlimit"/>
                </mrow>
                <mrow>
                  <xsl:apply-templates select="m:condition"/>
                </mrow>
              </munder>
            </mrow>
            <mrow>
              <xsl:apply-templates select="m:uplimit"/>
            </mrow>
          </munderover>
        </xsl:when>
        <xsl:when test="m:condition">
<!-- domain specified by a condition -->
          <munder>
            <mo>
              <xsl:value-of select="$Sum"/>
            </mo>
            <xsl:apply-templates select="m:condition"/>
          </munder>
        </xsl:when>
        <xsl:when test="m:domainofapplication">
<!-- domain specified by domain of application -->
          <munder>
            <mo>
              <xsl:value-of select="$Sum"/>
            </mo>
            <xsl:apply-templates select="m:domainofapplication"/>
          </munder>
        </xsl:when>
        <xsl:when test="m:lowlimit and m:uplimit">
<!-- domain specified by low and up limits -->
          <munderover>
            <mo>
              <xsl:value-of select="$Sum"/>
            </mo>
            <mrow>
              <xsl:apply-templates select="m:bvar"/>
              <mo>=</mo>
              <xsl:apply-templates select="m:lowlimit"/>
            </mrow>
            <mrow>
              <xsl:apply-templates select="m:uplimit"/>
            </mrow>
          </munderover>
        </xsl:when>
        <xsl:otherwise>
          <mo>
            <xsl:value-of select="$Sum"/>
          </mo>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
        <xsl:when test="*[position()=last() and self::m:apply]">
<!-- if expression is complex, wrap it in brackets -->
          <mfenced separators="">
            <xsl:apply-templates select="*[position()=last()]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
<!-- if not put it in an mrow -->
          <mrow>
            <xsl:apply-templates select="*[position()=last()]"/>
          </mrow>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- product -->
  <xsl:template match="m:apply[*[1][self::m:product]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:condition and m:domainofapplication">
<!-- domainofapplication as well as condition -->
          <munder>
            <mo>
              <xsl:value-of select="$Product"/>
            </mo>
            <mrow>
              <munder>
                <mrow>
                  <xsl:apply-templates select="m:domainofapplication"/>
                </mrow>
                <mrow>
                  <xsl:apply-templates select="m:condition"/>
                </mrow>
              </munder>
            </mrow>
          </munder>
        </xsl:when>
        <xsl:when test="m:condition and m:lowlimit and m:uplimit">
<!-- uplimit and lowlimit as well as condition -->
          <munderover>
            <mo>
              <xsl:value-of select="$Product"/>
            </mo>
            <mrow>
              <munder>
                <mrow>
                  <xsl:apply-templates select="m:bvar"/>
                  <mo>=</mo>
                  <xsl:apply-templates select="m:lowlimit"/>
                </mrow>
                <mrow>
                  <xsl:apply-templates select="m:condition"/>
                </mrow>
              </munder>
            </mrow>
            <mrow>
              <xsl:apply-templates select="m:uplimit"/>
            </mrow>
          </munderover>
        </xsl:when>
        <xsl:when test="m:condition">
<!-- domain specified by a condition -->
          <munder>
            <mo>
              <xsl:value-of select="$Product"/>
            </mo>
            <xsl:apply-templates select="m:condition"/>
          </munder>
        </xsl:when>
        <xsl:when test="m:domainofapplication">
<!--domain specified by a domain -->
          <munder>
            <mo>
              <xsl:value-of select="$Product"/>
            </mo>
            <xsl:apply-templates select="m:domainofapplication"/>
          </munder>
        </xsl:when>
        <xsl:otherwise>
<!-- domain specified by low and up limits -->
          <munderover>
            <mo>
              <xsl:value-of select="$Product"/>
            </mo>
            <mrow>
              <xsl:apply-templates select="m:bvar"/>
              <mo>=</mo>
              <xsl:apply-templates select="m:lowlimit"/>
            </mrow>
            <mrow>
              <xsl:apply-templates select="m:uplimit"/>
            </mrow>
          </munderover>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
        <xsl:when test="*[position()=last() and self::m:apply]">
<!-- if expression is complex, wrap it in brackets -->
          <mfenced separators="">
            <xsl:apply-templates select="*[position()=last()]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
<!-- if not put it in an mrow -->
          <mrow>
            <xsl:apply-templates select="*[position()=last()]"/>
          </mrow>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- limit -->
  <xsl:template match="m:apply[*[1][self::m:limit]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:condition">
          <munder>
            <mo>lim</mo>
            <xsl:apply-templates select="m:condition"/>
          </munder>
        </xsl:when>
        <xsl:otherwise>
          <munder>
            <mo>lim</mo>
            <mrow>
              <xsl:apply-templates select="m:bvar"/>
              <mo>
                <xsl:value-of select="$RightArrow"/>
              </mo>
              <xsl:apply-templates select="m:lowlimit"/>
            </mrow>
          </munder>
        </xsl:otherwise>
      </xsl:choose>
      <mrow>
        <xsl:apply-templates select="*[position()=last()]"/>
      </mrow>
    </mrow>
  </xsl:template>
<!-- tends to -->
  <xsl:template name="tendstoRel">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:tendsto/@type">
          <xsl:choose>
            <xsl:when test="m:tendsto/@type='above'">
<!-- from above -->
              <xsl:apply-templates select="*[2]"/>
              <mo>
                <xsl:value-of select="$DownArrow"/>
              </mo>
              <xsl:apply-templates select="*[3]"/>
            </xsl:when>
            <xsl:when test="m:tendsto/@type='below'">
<!-- from below -->
              <xsl:apply-templates select="*[2]"/>
              <mo>
                <xsl:value-of select="$UpArrow"/>
              </mo>
              <xsl:apply-templates select="*[3]"/>
            </xsl:when>
            <xsl:when test="m:tendsto/@type='two-sided'">
<!-- from above or below -->
              <xsl:apply-templates select="*[2]"/>
              <mo>
                <xsl:value-of select="$RightArrow"/>
              </mo>
              <xsl:apply-templates select="*[3]"/>
            </xsl:when>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
<!-- no type attribute -->
          <xsl:apply-templates select="*[2]"/>
          <mo>
            <xsl:value-of select="$RightArrow"/>
          </mo>
          <xsl:apply-templates select="*[3]"/>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
  <xsl:template match="m:apply[*[1][self::m:tendsto]]">
    <xsl:call-template name="tendstoRel"/>
  </xsl:template>
  <xsl:template match="m:reln[*[1][self::m:tendsto]]">
    <xsl:call-template name="tendstoRel"/>
  </xsl:template>
<!-- #################### 4.4.8 #################### -->
<!-- main template for all trigonometric functions -->
  <xsl:template name="trigo">
    <xsl:param name="func">sin</xsl:param>
<!-- provide sin as default function in case none is provided (this should never occur)-->
    <mrow>
      <mi>
        <xsl:value-of select="$func"/>
      </mi>
      <mo>
        <xsl:value-of select="$ApplyFunction"/>
      </mo>
      <xsl:choose>
        <xsl:when test="*[2][self::m:apply] or (*[2][self::m:ci or self::m:cn] and contains(*[2]/text(),'-'))">
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <mrow>
            <xsl:apply-templates select="*[2]"/>
          </mrow>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- trigonometric function: sine -->
  <xsl:template match="m:apply[*[1][self::m:sin]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">sin</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:sin[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>sin</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: cosine -->
  <xsl:template match="m:apply[*[1][self::m:cos]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">cos</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:cos[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>cos</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: tan -->
  <xsl:template match="m:apply[*[1][self::m:tan]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">tan</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:tan[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>tan</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: sec -->
  <xsl:template match="m:apply[*[1][self::m:sec]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">sec</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:sec[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>sec</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: csc -->
  <xsl:template match="m:apply[*[1][self::m:csc]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">csc</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:csc[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>csc</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: cotan -->
  <xsl:template match="m:apply[*[1][self::m:cot]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">cot</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:cot[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>cot</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: hyperbolic sin -->
  <xsl:template match="m:apply[*[1][self::m:sinh]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">sinh</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:sinh[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>sinh</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: hyperbolic cos -->
  <xsl:template match="m:apply[*[1][self::m:cosh]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">cosh</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:cosh[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>cosh</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: hyperbolic tan -->
  <xsl:template match="m:apply[*[1][self::m:tanh]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">tanh</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:tanh[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>tanh</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: hyperbolic sec -->
  <xsl:template match="m:apply[*[1][self::m:sech]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">sech</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:sech[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>sech</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: hyperbolic csc -->
  <xsl:template match="m:apply[*[1][self::m:csch]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">csch</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:csch[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>csch</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: hyperbolic cotan -->
  <xsl:template match="m:apply[*[1][self::m:coth]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">coth</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:coth[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>coth</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc sine -->
  <xsl:template match="m:apply[*[1][self::m:arcsin]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arcsin</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arcsin[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arcsin</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc cosine -->
  <xsl:template match="m:apply[*[1][self::m:arccos]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arccos</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arccos[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arccos</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc tan -->
  <xsl:template match="m:apply[*[1][self::m:arctan]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arctan</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arctan[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arctan</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc sec -->
  <xsl:template match="m:apply[*[1][self::m:arcsec]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arcsec</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arcsec[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arcsec</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc csc -->
  <xsl:template match="m:apply[*[1][self::m:arccsc]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arccsc</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arccsc[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arccsc</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc cotan -->
  <xsl:template match="m:apply[*[1][self::m:arccot]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arccot</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arccot[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arccot</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc sinh -->
  <xsl:template match="m:apply[*[1][self::m:arcsinh]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arcsinh</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arcsinh[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arcsinh</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc cosh -->
  <xsl:template match="m:apply[*[1][self::m:arccosh]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arccosh</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arccosh[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arccosh</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc tanh -->
  <xsl:template match="m:apply[*[1][self::m:arctanh]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arctanh</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arctanh[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arctanh</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc sech -->
  <xsl:template match="m:apply[*[1][self::m:arcsech]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arcsech</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arcsech[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arcsech</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc csch -->
  <xsl:template match="m:apply[*[1][self::m:arccsch]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arccsch</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arccsch[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arccsch</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- trigonometric function: arc coth -->
  <xsl:template match="m:apply[*[1][self::m:arccoth]]">
    <xsl:call-template name="trigo">
      <xsl:with-param name="func">arccoth</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="m:arccoth[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>arccoth</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- exponential -->
  <xsl:template match="m:apply[*[1][self::m:exp]]">
    <msup>
      <mi>
        <xsl:value-of select="$ee"/>
      </mi>
<!-- ExponentialE does not work yet -->
      <xsl:apply-templates select="*[2]"/>
    </msup>
  </xsl:template>
  <xsl:template match="m:exp[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>
      <xsl:value-of select="$ExponentialE"/>
    </mi>
<!-- used with inverse or composition; not sure it is appropriate for exponential-->
  </xsl:template>
<!-- natural logarithm -->
  <xsl:template match="m:apply[*[1][self::m:ln]]">
    <mrow>
      <mi>ln</mi>
      <mo>
        <xsl:value-of select="$ApplyFunction"/>
      </mo>
      <xsl:choose>
        <xsl:when test="*[2][self::m:apply] or (*[2][self::m:ci or self::m:cn] and contains(*[2]/text(),'-'))">
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[2]"/>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
  <xsl:template match="m:ln[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mi>ln</mi>
<!-- used with inverse or composition-->
  </xsl:template>
<!-- logarithm to a given base (default 10)-->
  <xsl:template match="m:apply[*[1][self::m:log]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:logbase">
          <msub>
            <mi>log</mi>
            <xsl:apply-templates select="m:logbase"/>
          </msub>
          <mo>
            <xsl:value-of select="$ApplyFunction"/>
          </mo>
          <xsl:choose>
            <xsl:when test="*[3][self::m:apply] or (*[3][self::m:ci or self::m:cn] and contains(*[3]/text(),'-'))">
              <mfenced separators="">
                <xsl:apply-templates select="*[3]"/>
              </mfenced>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="*[3]"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
<!--if no base is provided, default to 10-->
          <msub>
            <mi>log</mi>
            <mn>10</mn>
          </msub>
          <mo>
            <xsl:value-of select="$ApplyFunction"/>
          </mo>
          <xsl:choose>
            <xsl:when test="*[2][self::m:apply] or (*[2][self::m:ci or self::m:cn] and contains(*[2]/text(),'-'))">
              <mfenced separators="">
                <xsl:apply-templates select="*[2]"/>
              </mfenced>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="*[2]"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
  <xsl:template match="m:log[preceding-sibling::*[position()=last() and (self::m:compose or self::m:inverse)]]">
    <mrow>
<!-- used with inverse or composition-->
      <xsl:choose>
        <xsl:when test="m:logbase">
          <msub>
            <mi>log</mi>
            <xsl:apply-templates select="m:logbase"/>
          </msub>
        </xsl:when>
        <xsl:otherwise>
<!--if no base is provided, default to 10-->
          <msub>
            <mi>log</mi>
            <mn>10</mn>
          </msub>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- logbase -->
  <xsl:template match="m:logbase">
    <xsl:apply-templates select="*"/>
  </xsl:template>
<!-- #################### 4.4.9 #################### -->
<!-- mean -->
<!-- not sure we handle the n-ary thing correctly as far as display is concerned-->
  <xsl:template match="m:apply[*[1][self::m:mean]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="count(*)&gt;2">
<!-- if more than one element use angle bracket notation-->
          <mo>
            <xsl:value-of select="$lang"/>
          </mo>
          <xsl:for-each select="*[position()!=1 and position()!=last()]">
            <xsl:apply-templates select="."/>
            <mo>,</mo>
          </xsl:for-each>
          <xsl:apply-templates select="*[position()=last()]"/>
          <mo>
            <xsl:value-of select="$rang"/>
          </mo>
<!-- does not work, UNICODE x03009 or perhaps rangle or RightAngleBracket -->
        </xsl:when>
        <xsl:otherwise>
<!-- if only one element use overbar notation-->
          <mover>
            <mrow>
              <xsl:apply-templates select="*[position()=last()]"/>
            </mrow>
            <mo>
              <xsl:value-of select="$ovbar"/>
            </mo>
<!-- does not work, UNICODE x0233D  or perhaps OverBar-->
          </mover>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- standard deviation -->
<!-- not sure we handle the n-ary thing correctly as far as display is concerned-->
  <xsl:template match="m:apply[*[1][self::m:sdev]]">
    <mrow>
      <mi>
        <xsl:value-of select="$sigma"/>
      </mi>
      <mfenced>
        <xsl:apply-templates select="*[position()!=1]"/>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- statistical variance -->
<!-- not sure we handle the n-ary thing correctly as far as display is concerned-->
  <xsl:template match="m:apply[*[1][self::m:variance]]">
    <mrow>
      <mi>
        <xsl:value-of select="$sigma"/>
      </mi>
      <msup>
        <mfenced>
          <xsl:apply-templates select="*[position()!=1]"/>
        </mfenced>
        <mn>2</mn>
      </msup>
    </mrow>
  </xsl:template>
<!-- median -->
<!-- not sure we handle the n-ary thing correctly as far as display is concerned-->
  <xsl:template match="m:apply[*[1][self::m:median]]">
    <mrow>
      <mi>median</mi>
      <mfenced>
        <xsl:apply-templates select="*[position()!=1]"/>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- statistical mode -->
<!-- not sure we handle the n-ary thing correctly as far as display is concerned-->
  <xsl:template match="m:apply[*[1][self::m:mode]]">
    <mrow>
      <mi>mode</mi>
      <mfenced>
        <xsl:apply-templates select="*[position()!=1]"/>
      </mfenced>
    </mrow>
  </xsl:template>
<!-- statistical moment -->
<!-- not sure we handle the n-ary thing correctly as far as display is concerned-->
  <xsl:template match="m:apply[*[1][self::m:moment]]">
    <mrow>
      <mo>
        <xsl:value-of select="$lang"/>
      </mo>
      <xsl:for-each select="*[position()!=1 and position()!=2 and position()!=last() and not(self::m:momentabout)]">
        <msup>
          <xsl:apply-templates select="."/>
          <xsl:apply-templates select="../m:degree"/>
        </msup>
        <mo>,</mo>
      </xsl:for-each>
      <msup>
        <xsl:apply-templates select="*[position()=last()]"/>
        <xsl:apply-templates select="m:degree"/>
      </msup>
      <mo>
        <xsl:value-of select="$rang"/>
      </mo>
    </mrow>
  </xsl:template>
<!-- point of moment (according to the spec it is not rendered)-->
  <xsl:template match="m:momentabout">
</xsl:template>
<!-- #################### 4.4.10 #################### -->
<!-- vector -->
  <xsl:template match="m:vector">
<!--default representation for vectors is horizontal, since they can be used for instance as the set of params of a function-->
    <xsl:choose>
<!-- support for some cases where vectors should be displayed verticaly-->
      <xsl:when test="(preceding-sibling::*[1][self::m:matrix] and preceding-sibling::*[position()=last() and self::m:times])">
        <mfenced>
<!-- vectors that are after a matrix, the operation being a multiplication -->
          <mtable>
            <xsl:for-each select="*">
              <mtr>
                <mtd>
                  <xsl:apply-templates select="."/>
                </mtd>
              </mtr>
            </xsl:for-each>
          </mtable>
        </mfenced>
      </xsl:when>
      <xsl:otherwise>
        <mfenced>
          <xsl:apply-templates select="*"/>
        </mfenced>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
<!-- matrix -->
  <xsl:template match="m:matrix">
    <mrow>
      <mfenced>
        <mtable>
          <xsl:apply-templates select="*"/>
        </mtable>
      </mfenced>
    </mrow>
  </xsl:template>
  <xsl:template match="m:matrixrow">
    <mtr>
      <xsl:for-each select="*">
        <mtd>
          <mpadded width="+0.3em" lspace="+0.3em">
            <xsl:apply-templates select="."/>
          </mpadded>
        </mtd>
      </xsl:for-each>
    </mtr>
  </xsl:template>
<!-- determinant -->
  <xsl:template match="m:apply[*[1][self::m:determinant]]">
    <mrow>
      <mo>det</mo>
      <xsl:choose>
        <xsl:when test="m:apply">
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[2]"/>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- transpose -->
  <xsl:template match="m:apply[*[1][self::m:transpose]]">
    <msup>
      <xsl:choose>
        <xsl:when test="m:apply">
          <mfenced separators="">
            <xsl:apply-templates select="*[2]"/>
          </mfenced>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[2]"/>
        </xsl:otherwise>
      </xsl:choose>
      <mo>T</mo>
    </msup>
  </xsl:template>
<!-- selector-->
  <xsl:template match="m:apply[*[1][self::m:selector]]">
    <mrow>
      <xsl:choose>
        <xsl:when test="*[2][self::m:matrix]">
<!-- select in a matrix defined inside the selector -->
          <xsl:choose>
            <xsl:when test="count(*)=4">
<!-- matrix element-->
              <xsl:variable name="i">
                <xsl:value-of select="*[3]"/>
              </xsl:variable>
<!--extract row-->
              <xsl:variable name="j">
                <xsl:value-of select="*[4]"/>
              </xsl:variable>
<!--extract column-->
              <xsl:apply-templates select="*[2]/*[position()=number($i)]/*[position()=number($j)]"/>
            </xsl:when>
            <xsl:when test="count(*)=3">
<!-- matrix row -->
              <xsl:variable name="i">
                <xsl:value-of select="*[3]"/>
              </xsl:variable>
<!--extract row, put it in a matrix container of its own-->
              <mtable>
                <xsl:apply-templates select="*[2]/*[position()=number($i)]"/>
              </mtable>
            </xsl:when>
            <xsl:otherwise>
<!-- no index select the entire thing-->
              <xsl:apply-templates select="*[2]"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:when test="*[2][(self::m:vector or self::m:list)]">
<!-- select in a vector or list defined inside the selector -->
          <xsl:choose>
            <xsl:when test="count(*)=3">
<!-- list/vector element -->
              <xsl:variable name="i">
                <xsl:value-of select="*[3]"/>
              </xsl:variable>
<!--extract index-->
              <xsl:apply-templates select="*[2]/*[position()=number($i)]"/>
            </xsl:when>
            <xsl:otherwise>
<!-- no index select the entire thing-->
              <xsl:apply-templates select="*[2]"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
<!-- select in something defined elsewhere : an identifier is provided-->
          <xsl:choose>
            <xsl:when test="count(*)=4">
<!-- two indices (matrix element)-->
              <msub>
                <xsl:apply-templates select="*[2]"/>
                <mrow>
                  <xsl:apply-templates select="*[3]"/>
                  <mo>
                    <xsl:value-of select="$InvisibleComma"/>
                  </mo>
<!-- InvisibleComma does not work -->
                  <xsl:apply-templates select="*[4]"/>
                </mrow>
              </msub>
            </xsl:when>
            <xsl:when test="count(*)=3">
<!-- one index probably list or vector element, or matrix row -->
              <msub>
                <xsl:apply-templates select="*[2]"/>
                <xsl:apply-templates select="*[3]"/>
              </msub>
            </xsl:when>
            <xsl:otherwise>
<!-- no index select the entire thing-->
              <xsl:apply-templates select="*[2]"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- vector product = A x B x sin(teta) -->
  <xsl:template match="m:apply[*[1][self::m:vectorproduct]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>
        <xsl:value-of select="$times"/>
      </mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- scalar product = A x B x cos(teta) -->
  <xsl:template match="m:apply[*[1][self::m:scalarproduct]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>.</mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- outer product = A x B x cos(teta) -->
  <xsl:template match="m:apply[*[1][self::m:outerproduct]]">
    <mrow>
      <xsl:apply-templates select="*[2]"/>
      <mo>.</mo>
      <xsl:apply-templates select="*[3]"/>
    </mrow>
  </xsl:template>
<!-- #################### 4.4.11 #################### -->
<!-- annotation-->
  <xsl:template match="m:annotation">
<!-- no rendering for annotations-->
  </xsl:template>
<!-- semantics-->
  <xsl:template match="m:semantics">
    <mrow>
      <xsl:choose>
        <xsl:when test="contains(m:annotation-xml/@encoding,'MathML-Presentation')">
<!-- if specific representation is provided use it-->
          <xsl:apply-templates select="annotation-xml[contains(@encoding,'MathML-Presentation')]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="*[1]"/>
<!--if no specific representation is provided use the default one-->
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>
<!-- MathML presentation in annotation-xml-->
  <xsl:template match="m:annotation-xml[contains(@encoding,'MathML-Presentation')]">
    <mrow>
      <xsl:copy-of select="*"/>
    </mrow>
  </xsl:template>
<!-- #################### 4.4.12 #################### -->
<!-- integer numbers -->
  <xsl:template match="m:integers">
    <mi>
      <xsl:text disable-output-escaping="yes">&#8484;</xsl:text>
    </mi>
<!-- open face Z -->
<!-- UNICODE char works -->
  </xsl:template>
<!-- real numbers -->
  <xsl:template match="m:reals">
    <mi>
      <xsl:text disable-output-escaping="yes">&#8477;</xsl:text>
    </mi>
<!-- open face R -->
<!-- UNICODE char works -->
  </xsl:template>
<!-- rational numbers -->
  <xsl:template match="m:rationals">
    <mi>
      <xsl:text disable-output-escaping="yes">&#8474;</xsl:text>
    </mi>
<!-- open face Q -->
<!-- UNICODE char works -->
  </xsl:template>
<!-- natural numbers -->
  <xsl:template match="m:naturalnumbers">
    <mi>
      <xsl:text disable-output-escaping="yes">&#8469;</xsl:text>
    </mi>
<!-- open face N -->
<!-- UNICODE char works -->
  </xsl:template>
<!-- complex numbers -->
  <xsl:template match="m:complexes">
    <mi>
      <xsl:text disable-output-escaping="yes">&#8450;</xsl:text>
    </mi>
<!-- open face C -->
<!-- UNICODE char works -->
  </xsl:template>
<!-- prime numbers -->
  <xsl:template match="m:primes">
    <mi>
      <xsl:text disable-output-escaping="yes">&#8473;</xsl:text>
    </mi>
<!-- open face P -->
<!-- UNICODE char works -->
  </xsl:template>
<!-- exponential base -->
  <xsl:template match="m:exponentiale">
    <mi>
      <xsl:value-of select="$ee"/>
    </mi>
<!-- ExponentialE does not work yet -->
  </xsl:template>
<!-- square root of -1 -->
  <xsl:template match="m:imaginaryi">
    <mi>
      <xsl:value-of select="$ImaginaryI"/>
    </mi>
<!-- or perhaps ii -->
  </xsl:template>
<!-- result of an ill-defined floating point operation -->
  <xsl:template match="m:notanumber">
    <mi>NaN</mi>
  </xsl:template>
<!-- logical constant for truth -->
  <xsl:template match="m:true">
    <mi>true</mi>
  </xsl:template>
<!-- logical constant for falsehood -->
  <xsl:template match="m:false">
    <mi>false</mi>
  </xsl:template>
<!-- empty set -->
  <xsl:template match="m:emptyset">
    <mi>
      <xsl:value-of select="$empty"/>
    </mi>
  </xsl:template>
<!-- ratio of a circle's circumference to its diameter -->
  <xsl:template match="m:pi">
    <mi>
      <xsl:value-of select="$pi"/>
    </mi>
  </xsl:template>
<!-- Euler's constant -->
  <xsl:template match="m:eulergamma">
    <mi>
      <xsl:value-of select="$gamma"/>
    </mi>
  </xsl:template>
<!-- Infinity -->
  <xsl:template match="m:infinity">
    <mi>
      <xsl:value-of select="$infin"/>
    </mi>
  </xsl:template>
  <xsl:template match="*">
    <xsl:copy>
      <xsl:for-each select="@*">
        <xsl:copy/>
      </xsl:for-each>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
