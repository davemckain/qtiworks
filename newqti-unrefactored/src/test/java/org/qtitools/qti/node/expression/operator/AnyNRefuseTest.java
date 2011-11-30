/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package org.qtitools.qti.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.exception.QTIAttributeException;
import uk.ac.ed.ph.jqtiplus.exception.QTIBaseTypeException;
import uk.ac.ed.ph.jqtiplus.exception.QTICardinalityException;
import uk.ac.ed.ph.jqtiplus.exception.QTIRuntimeException;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionRefuseTest;

/**
 * Test of <code>AnyN</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.AnyN
 */
@RunWith(Parameterized.class)
public class AnyNRefuseTest extends ExpressionRefuseTest
{
    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][]
        {
            // attributes
            {"<anyN min='1' max='A'>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
            "</anyN>", QTIAttributeException.class},
            {"<anyN min='1.0' max='1'>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
            "</anyN>", QTIAttributeException.class},
            // condition: 0 <= min <= max
            /*
            {"<anyN min='-1' max='1'>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
            "</anyN>", QTIAttributeException.class},
            {"<anyN min='2' max='1'>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
            "</anyN>", QTIAttributeException.class},
            */
            // multiple
            {"<anyN min='1' max='1'>" +
                "<multiple>" +
                    "<baseValue baseType='boolean'>true</baseValue>" +
                "</multiple>" +
            "</anyN>", QTICardinalityException.class},
            // ordered
            {"<anyN min='1' max='1'>" +
                "<ordered>" +
                    "<baseValue baseType='boolean'>true</baseValue>" +
                "</ordered>" +
            "</anyN>", QTICardinalityException.class},
            // record
            {"<anyN min='1' max='1'>" +
                "<recordEx identifiers='key_1'>" +
                    "<baseValue baseType='boolean'>true</baseValue>" +
                "</recordEx>" +
            "</anyN>", QTICardinalityException.class},
            // identifier
            {"<anyN min='1' max='1'>" +
                "<baseValue baseType='identifier'>true</baseValue>" +
            "</anyN>", QTIBaseTypeException.class},
            // boolean + integer
            {"<anyN min='1' max='1'>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
            "</anyN>", QTIBaseTypeException.class},
            // integer
            {"<anyN min='1' max='1'>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</anyN>", QTIBaseTypeException.class},
            // float
            {"<anyN min='1' max='1'>" +
                "<baseValue baseType='float'>1</baseValue>" +
            "</anyN>", QTIBaseTypeException.class},
            // string
            {"<anyN min='1' max='1'>" +
                "<baseValue baseType='string'>true</baseValue>" +
            "</anyN>", QTIBaseTypeException.class},
            // point
            {"<anyN min='1' max='1'>" +
                "<baseValue baseType='point'>1 1</baseValue>" +
            "</anyN>", QTIBaseTypeException.class},
            // pair
            {"<anyN min='1' max='1'>" +
                "<baseValue baseType='pair'>idntifier_1 identifier_2</baseValue>" +
            "</anyN>", QTIBaseTypeException.class},
            // directedPair
            {"<anyN min='1' max='1'>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
            "</anyN>", QTIBaseTypeException.class},
            // duration
            {"<anyN min='1' max='1'>" +
                "<baseValue baseType='duration'>1</baseValue>" +
            "</anyN>", QTIBaseTypeException.class},
            // file
            {"<anyN min='1' max='1'>" +
                "<baseValue baseType='file'>file</baseValue>" +
            "</anyN>", QTIBaseTypeException.class},
            // uri
            {"<anyN min='1' max='1'>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
            "</anyN>", QTIBaseTypeException.class},
        });
    }

    /**
     * Constructs <code>AnyN</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedException expected exception during evaluation of tested expression
     */
    public AnyNRefuseTest(String xml, Class<? extends QTIRuntimeException> expectedException)
    {
        super(xml, expectedException);
    }
}
