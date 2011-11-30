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

import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionAcceptTest;

/**
 * Test of <code>Sum</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Sum
 */
@RunWith(Parameterized.class)
public class SumAcceptTest extends ExpressionAcceptTest
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
            // null
            {"<sum>" +
                "<null/>" +
            "</sum>", NullValue.INSTANCE},
            {"<sum>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<null/>" +
                "<baseValue baseType='integer'>2</baseValue>" +
            "</sum>", NullValue.INSTANCE},
            // integer
            {"<sum>" +
                "<baseValue baseType='integer'>1</baseValue>" +
            "</sum>", new IntegerValue(1)},
            {"<sum>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<baseValue baseType='integer'>3</baseValue>" +
            "</sum>", new IntegerValue(6)},
            // float
            {"<sum>" +
                "<baseValue baseType='float'>1.2</baseValue>" +
            "</sum>", new FloatValue(1.2)},
            {"<sum>" +
                "<baseValue baseType='float'>1</baseValue>" +
                "<baseValue baseType='float'>2.3</baseValue>" +
                "<baseValue baseType='float'>4</baseValue>" +
            "</sum>", new FloatValue(7.3)},
            {"<sum>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<baseValue baseType='integer'>2</baseValue>" +
                "<baseValue baseType='float'>3.4</baseValue>" +
                "<baseValue baseType='integer'>5</baseValue>" +
            "</sum>", new FloatValue(11.4)},
            // single + multiple
            {"<sum>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='float'>2.3</baseValue>" +
                    "<baseValue baseType='float'>4.5</baseValue>" +
                "</multiple>" +
                "<baseValue baseType='integer'>6</baseValue>" +
            "</sum>", new FloatValue(13.8)},
            // single + ordered
            {"<sum>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<ordered>" +
                    "<baseValue baseType='float'>2.3</baseValue>" +
                    "<baseValue baseType='float'>4.5</baseValue>" +
                "</ordered>" +
                "<baseValue baseType='integer'>6</baseValue>" +
            "</sum>", new FloatValue(13.8)},
            // single + multiple + ordered
            {"<sum>" +
                "<baseValue baseType='integer'>1</baseValue>" +
                "<multiple>" +
                    "<baseValue baseType='float'>2.3</baseValue>" +
                    "<baseValue baseType='float'>4.5</baseValue>" +
                "</multiple>" +
                "<ordered>" +
                    "<baseValue baseType='float'>6.7</baseValue>" +
                    "<baseValue baseType='float'>8.9</baseValue>" +
                "</ordered>" +
                "<baseValue baseType='integer'>0</baseValue>" +
            "</sum>", new FloatValue(23.4)},
        });
    }

    /**
     * Constructs <code>Sum</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public SumAcceptTest(String xml, Value expectedValue)
    {
        super(xml, expectedValue);
    }
}
