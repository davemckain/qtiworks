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
 * Test of <code>Sum</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Sum
 */
@RunWith(Parameterized.class)
public class SumRefuseTest extends ExpressionRefuseTest
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
            // record
            {"<sum>" +
                "<recordEx identifiers='key_1'>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                "</recordEx>" +
            "</sum>", QTICardinalityException.class},
            // identifier
            {"<sum>" +
                "<baseValue baseType='identifier'>identifier</baseValue>" +
            "</sum>", QTIBaseTypeException.class},
            // boolean
            {"<sum>" +
                "<baseValue baseType='boolean'>1</baseValue>" +
            "</sum>", QTIBaseTypeException.class},
            // integer + boolean
            {"<sum>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='boolean'>1</baseValue>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</sum>", QTIBaseTypeException.class},
            // float + boolean
            {"<sum>" +
                "<baseValue baseType='float'>1</baseValue>" +
                "<baseValue baseType='boolean'>1</baseValue>" +
                "<baseValue baseType='float'>1</baseValue>" +
            "</sum>", QTIBaseTypeException.class},
            // string
            {"<sum>" +
                "<baseValue baseType='string'>1</baseValue>" +
            "</sum>", QTIBaseTypeException.class},
            // point
            {"<sum>" +
                "<baseValue baseType='point'>1 1</baseValue>" +
            "</sum>", QTIBaseTypeException.class},
            // pair
            {"<sum>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
            "</sum>", QTIBaseTypeException.class},
            // directedPair
            {"<sum>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
            "</sum>", QTIBaseTypeException.class},
            // duration
            {"<sum>" +
                "<baseValue baseType='duration'>1</baseValue>" +
            "</sum>", QTIBaseTypeException.class},
            // file
            {"<sum>" +
                "<baseValue baseType='file'>file</baseValue>" +
            "</sum>", QTIBaseTypeException.class},
            // uri
            {"<sum>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
            "</sum>", QTIBaseTypeException.class},
        });
    }

    /**
     * Constructs <code>Sum</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedException expected exception during evaluation of tested expression
     */
    public SumRefuseTest(String xml, Class<? extends QTIRuntimeException> expectedException)
    {
        super(xml, expectedException);
    }
}
