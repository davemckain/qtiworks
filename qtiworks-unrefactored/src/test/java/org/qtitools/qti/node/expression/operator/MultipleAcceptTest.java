/* Copyright (c) 2012, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package org.qtitools.qti.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.DirectedPairValue;
import uk.ac.ed.ph.jqtiplus.value.DurationValue;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.PairValue;
import uk.ac.ed.ph.jqtiplus.value.PointValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.UriValue;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.value.ValueTestUtils;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.qtitools.qti.node.expression.ExpressionAcceptTest;

/**
 * Test of <code>Multiple</code> expression.
 * 
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Multiple
 */
@RunWith(Parameterized.class)
public class MultipleAcceptTest extends ExpressionAcceptTest {

    private static final MultipleValue MULTIPLE_1__1_2_3;

    private static final MultipleValue MULTIPLE_2__1_2_3_4_5_6_7;

    static {
        // MULTIPLE_1__1_2_3
        MULTIPLE_1__1_2_3 = MultipleValue.emptyValue();
        MULTIPLE_1__1_2_3.add(new IntegerValue(1));
        MULTIPLE_1__1_2_3.add(new IntegerValue(2));
        MULTIPLE_1__1_2_3.add(new IntegerValue(3));
        // MULTIPLE_2__1_2_3_4_5_6_7
        MULTIPLE_2__1_2_3_4_5_6_7 = MultipleValue.emptyValue();
        MULTIPLE_2__1_2_3_4_5_6_7.add(new IntegerValue(1));
        MULTIPLE_2__1_2_3_4_5_6_7.add(new IntegerValue(2));
        MULTIPLE_2__1_2_3_4_5_6_7.add(new IntegerValue(3));
        MULTIPLE_2__1_2_3_4_5_6_7.add(new IntegerValue(4));
        MULTIPLE_2__1_2_3_4_5_6_7.add(new IntegerValue(5));
        MULTIPLE_2__1_2_3_4_5_6_7.add(new IntegerValue(6));
        MULTIPLE_2__1_2_3_4_5_6_7.add(new IntegerValue(7));
    }

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // null
                { "<multiple>" +
                        "</multiple>", NullValue.INSTANCE }, { "<multiple>" +
                        "<null/>" +
                        "</multiple>", NullValue.INSTANCE }, { "<multiple>" +
                        "</multiple>", MultipleValue.emptyValue() }, { "<multiple>" +
                        "<null/>" +
                        "</multiple>", MultipleValue.emptyValue() },
                // identifier
                { "<multiple>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(new IdentifierValue("identifier")) },
                // boolean
                { "<multiple>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(BooleanValue.TRUE) }, { "<multiple>" +
                        "<baseValue baseType='boolean'>false</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(BooleanValue.FALSE) },
                // integer
                { "<multiple>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(new IntegerValue(1)) }, { "<multiple>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</multiple>", MULTIPLE_1__1_2_3 }, { "<multiple>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<null/>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<null/>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</multiple>", MULTIPLE_1__1_2_3 }, { "<multiple>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<multiple>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "<baseValue baseType='integer'>4</baseValue>" +
                        "<baseValue baseType='integer'>5</baseValue>" +
                        "</multiple>" +
                        "<baseValue baseType='integer'>6</baseValue>" +
                        "<baseValue baseType='integer'>7</baseValue>" +
                        "</multiple>", MULTIPLE_2__1_2_3_4_5_6_7 },
                // float
                { "<multiple>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(new FloatValue(1)) },
                // string
                { "<multiple>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(new StringValue("string")) },
                // point
                { "<multiple>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(new PointValue(1, 1)) },
                // pair
                { "<multiple>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(new PairValue("identifier_1", "identifier_2")) },
                // directedPair
                { "<multiple>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(new DirectedPairValue("identifier_1", "identifier_2")) },
                // duration
                { "<multiple>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(new DurationValue(1)) },
                // file
                { "<multiple>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(ValueTestUtils.createTestFileValue("file")) },
                // uri
                { "<multiple>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</multiple>", MultipleValue.createMultipleValue(new UriValue("uri")) },
                // multiple
                { "<multiple>" +
                        "<multiple>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</multiple>" +
                        "</multiple>", MultipleValue.createMultipleValue(new IntegerValue(1)) },
        });
    }

    /**
     * Constructs <code>Multiple</code> expression test.
     * 
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public MultipleAcceptTest(String xml, Value expectedValue) {
        super(xml, expectedValue);
    }
}
