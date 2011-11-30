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

import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionAcceptTest;

/**
 * Test of <code>Member</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Member
 */
@RunWith(Parameterized.class)
public class MemberAcceptTest extends ExpressionAcceptTest
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
            // identifier
            {"<member>" +
                "<baseValue baseType='identifier'>identifier</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='identifier'>identifier</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='identifier'>identifier</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='identifier'>identifier</baseValue>" +
                "</ordered>" +
            "</member>", true},
            // boolean
            {"<member>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='boolean'>true</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='boolean'>false</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='boolean'>false</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='boolean'>true</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='boolean'>true</baseValue>" +
                "</ordered>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='boolean'>false</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='boolean'>false</baseValue>" +
                "</ordered>" +
            "</member>", true},
            // integer
            {"<member>" +
                "<baseValue baseType='integer'>0</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</multiple>" +
            "</member>", false},
            {"<member>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='integer'>3</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='integer'>4</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</multiple>" +
            "</member>", false},
            {"<member>" +
                "<baseValue baseType='integer'>0</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</ordered>" +
            "</member>", false},
            {"<member>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</ordered>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</ordered>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='integer'>3</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</ordered>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='integer'>4</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='integer'>1</baseValue>" +
                    "<baseValue baseType='integer'>2</baseValue>" +
                    "<baseValue baseType='integer'>3</baseValue>" +
                "</ordered>" +
            "</member>", false},
            // float
            {"<member>" +
                "<baseValue baseType='float'>1.2</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='float'>1.2</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='float'>1.2</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='float'>1.2</baseValue>" +
                "</ordered>" +
            "</member>", true},
            // string
            {"<member>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='string'>string</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='string'>string</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='string'>string</baseValue>" +
                "</ordered>" +
            "</member>", true},
            // point
            {"<member>" +
                "<baseValue baseType='point'>1 1</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='point'>1 1</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='point'>1 1</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='point'>1 1</baseValue>" +
                "</ordered>" +
            "</member>", true},
            // pair
            {"<member>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                "</ordered>" +
            "</member>", true},
            // directedPair
            {"<member>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                "</ordered>" +
            "</member>", true},
            // file
            {"<member>" +
                "<baseValue baseType='file'>file</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='file'>file</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='file'>file</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='file'>file</baseValue>" +
                "</ordered>" +
            "</member>", true},
            // uri
            {"<member>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='uri'>uri</baseValue>" +
                "</multiple>" +
            "</member>", true},
            {"<member>" +
                "<baseValue baseType='uri'>uri</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='uri'>uri</baseValue>" +
                "</ordered>" +
            "</member>", true},
        });
    }

    /**
     * Constructs <code>Member</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public MemberAcceptTest(String xml, Boolean expectedValue)
    {
        super(xml, (expectedValue != null) ? BooleanValue.valueOf(expectedValue) : NullValue.INSTANCE);
    }
}
