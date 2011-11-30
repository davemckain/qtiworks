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

package org.qtitools.qti.node.expression.general;

import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.DirectedPairValue;
import uk.ac.ed.ph.jqtiplus.value.DurationValue;
import uk.ac.ed.ph.jqtiplus.value.FileValue;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.PairValue;
import uk.ac.ed.ph.jqtiplus.value.PointValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.UriValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionAcceptTest;

/**
 * Test of <code>BaseValue</code> expression.
 *
 * @see uk.ac.ed.ph.jqtiplus.node.expression.general.BaseValue
 */
@RunWith(Parameterized.class)
public class BaseValueAcceptTest extends ExpressionAcceptTest
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
            {"<baseValue baseType='identifier'>identifier</baseValue>", new IdentifierValue("identifier")},
            {"<baseValue baseType='identifier'>Identifier</baseValue>", new IdentifierValue("Identifier")},
            {"<baseValue baseType='identifier'>IDENTIFIER</baseValue>", new IdentifierValue("IDENTIFIER")},
            {"<baseValue baseType='identifier'>identifier_1</baseValue>", new IdentifierValue("identifier_1")},
            // boolean
            {"<baseValue baseType='boolean'>true</baseValue>", BooleanValue.TRUE},
            {"<baseValue baseType='boolean'>false</baseValue>", BooleanValue.FALSE},
            {"<baseValue baseType='boolean'>1</baseValue>", BooleanValue.TRUE},
            {"<baseValue baseType='boolean'>0</baseValue>", BooleanValue.FALSE},
            // integer
            {"<baseValue baseType='integer'>1234</baseValue>", new IntegerValue(1234)},
            {"<baseValue baseType='integer'>3</baseValue>", new IntegerValue(3)},
            {"<baseValue baseType='integer'>1</baseValue>", new IntegerValue(1)},
            {"<baseValue baseType='integer'>0</baseValue>", new IntegerValue(0)},
            {"<baseValue baseType='integer'>+0</baseValue>", new IntegerValue(0)},
            {"<baseValue baseType='integer'>-0</baseValue>", new IntegerValue(0)},
            {"<baseValue baseType='integer'>-1</baseValue>", new IntegerValue(-1)},
            {"<baseValue baseType='integer'>-3</baseValue>", new IntegerValue(-3)},
            {"<baseValue baseType='integer'>-1234</baseValue>", new IntegerValue(-1234)},
            // float
            {"<baseValue baseType='float'>123.45</baseValue>", new FloatValue(123.45)},
            {"<baseValue baseType='float'>59</baseValue>", new FloatValue(59)},
            {"<baseValue baseType='float'>3.9999</baseValue>", new FloatValue(3.9999)},
            {"<baseValue baseType='float'>1.0</baseValue>", new FloatValue(1)},
            {"<baseValue baseType='float'>1</baseValue>", new FloatValue(1)},
            {"<baseValue baseType='float'>0.0</baseValue>", new FloatValue(0)},
            {"<baseValue baseType='float'>+0.0</baseValue>", new FloatValue(0)},
            {"<baseValue baseType='float'>-0.0</baseValue>", new FloatValue(0)},
            {"<baseValue baseType='float'>0</baseValue>", new FloatValue(0)},
            {"<baseValue baseType='float'>+0</baseValue>", new FloatValue(0)},
            {"<baseValue baseType='float'>-0</baseValue>", new FloatValue(0)},
            {"<baseValue baseType='float'>-1.0</baseValue>", new FloatValue(-1)},
            {"<baseValue baseType='float'>-1</baseValue>", new FloatValue(-1)},
            {"<baseValue baseType='float'>-3.9999</baseValue>", new FloatValue(-3.9999)},
            {"<baseValue baseType='float'>-59</baseValue>", new FloatValue(-59)},
            {"<baseValue baseType='float'>-123.45</baseValue>", new FloatValue(-123.45)},
            // string
            {"<baseValue baseType='string'>string</baseValue>", new StringValue("string")},
            {"<baseValue baseType='string'>String</baseValue>", new StringValue("String")},
            {"<baseValue baseType='string'>STRING</baseValue>", new StringValue("STRING")},
            {"<baseValue baseType='string'>sTrInG</baseValue>", new StringValue("sTrInG")},
            {"<baseValue baseType='string'>1</baseValue>", new StringValue("1")},
            {"<baseValue baseType='string'>true</baseValue>", new StringValue("true")},
            // point
            {"<baseValue baseType='point'>1 1</baseValue>", new PointValue(1, 1)},
            // pair
            {"<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>", new PairValue("identifier_1", "identifier_2")},
            // directedPair
            {"<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>", new DirectedPairValue("identifier_1", "identifier_2")},
            // duration
            {"<baseValue baseType='duration'>1</baseValue>", new DurationValue(1)},
            // file
            {"<baseValue baseType='file'>file</baseValue>", new FileValue("file")},
            // uri
            {"<baseValue baseType='uri'>uri</baseValue>", new UriValue("uri")},
        });
    }

    /**
     * Constructs <code>BaseValue</code> expression test.
     *
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public BaseValueAcceptTest(String xml, Value expectedValue)
    {
        super(xml, expectedValue);
    }
}
