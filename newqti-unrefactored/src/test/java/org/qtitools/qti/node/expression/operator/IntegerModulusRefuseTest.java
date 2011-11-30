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
 * Test of <code>IntegerModulus</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.IntegerModulus
 */
@RunWith(Parameterized.class)
public class IntegerModulusRefuseTest extends ExpressionRefuseTest
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
            // multiple
            {"<integerModulus>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</multiple>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTICardinalityException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</multiple>" +
            "</integerModulus>", QTICardinalityException.class},
            // ordered
            {"<integerModulus>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</ordered>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTICardinalityException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</ordered>" +
            "</integerModulus>", QTICardinalityException.class},
            // record
            {"<integerModulus>" +
                "<recordEx identifiers='key_1'>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</recordEx>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTICardinalityException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<recordEx identifiers='key_1'>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</recordEx>" +
            "</integerModulus>", QTICardinalityException.class},
            // identifier
            {"<integerModulus>" +
                "<baseValue baseType='identifier'>identifier</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='identifier'>identifier</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            // boolean
            {"<integerModulus>" +
                "<baseValue baseType='boolean'>1</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='boolean'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            // float
            {"<integerModulus>" +
                "<baseValue baseType='float'>1</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='float'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            // string
            {"<integerModulus>" +
                "<baseValue baseType='string'>1</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='string'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            // point
            {"<integerModulus>" +
                "<baseValue baseType='point'>1 1</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='point'>1 1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            // pair
            {"<integerModulus>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            // directedPair
            {"<integerModulus>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            // duration
            {"<integerModulus>" +
                "<baseValue baseType='duration'>1</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='duration'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            // file
            {"<integerModulus>" +
                "<baseValue baseType='file'>file</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='file'>file</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            // uri
            {"<integerModulus>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
            {"<integerModulus>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
            "</integerModulus>", QTIBaseTypeException.class},
        });
    }

    /**
     * Constructs <code>IntegerModulus</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedException expected exception during evaluation of tested expression
     */
    public IntegerModulusRefuseTest(String xml, Class<? extends QTIRuntimeException> expectedException)
    {
        super(xml, expectedException);
    }
}
