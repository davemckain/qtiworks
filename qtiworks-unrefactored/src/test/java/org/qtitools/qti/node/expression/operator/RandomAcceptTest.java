/* Copyright (c) 2012-2013, University of Edinburgh.
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
 * Test of <code>Random</code> expression.
 * 
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Random
 */
@RunWith(Parameterized.class)
public class RandomAcceptTest extends ExpressionAcceptTest {

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // null
                { "<random>" +
                        "<null/>" +
                        "</random>", NullValue.INSTANCE },
                // identifier
                { "<random>" +
                        "<multiple>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "</multiple>" +
                        "</random>", new IdentifierValue("identifier") }, { "<random>" +
                        "<ordered>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "</ordered>" +
                        "</random>", new IdentifierValue("identifier") },
                // boolean
                { "<random>" +
                        "<multiple>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</multiple>" +
                        "</random>", BooleanValue.TRUE }, { "<random>" +
                        "<ordered>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</ordered>" +
                        "</random>", BooleanValue.TRUE },
                // integer
                { "<random>" +
                        "<multiple>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</multiple>" +
                        "</random>", new IntegerValue(1) }, { "<random>" +
                        "<ordered>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</ordered>" +
                        "</random>", new IntegerValue(1) },
                // integer - multiple
                { "<randomEx seed='0'>" +
                        "<multiple>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</multiple>" +
                        "</randomEx>", new IntegerValue(1) }, { "<randomEx seed='2'>" +
                        "<multiple>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</multiple>" +
                        "</randomEx>", new IntegerValue(2) }, { "<randomEx seed='3'>" +
                        "<multiple>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</multiple>" +
                        "</randomEx>", new IntegerValue(3) },
                // integer - ordered
                { "<randomEx seed='0'>" +
                        "<ordered>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</ordered>" +
                        "</randomEx>", new IntegerValue(1) }, { "<randomEx seed='2'>" +
                        "<ordered>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</ordered>" +
                        "</randomEx>", new IntegerValue(2) }, { "<randomEx seed='3'>" +
                        "<ordered>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</ordered>" +
                        "</randomEx>", new IntegerValue(3) },
                // float
                { "<random>" +
                        "<multiple>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "</multiple>" +
                        "</random>", new FloatValue(1) }, { "<random>" +
                        "<ordered>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "</ordered>" +
                        "</random>", new FloatValue(1) },
                // string
                { "<random>" +
                        "<multiple>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</multiple>" +
                        "</random>", new StringValue("string") }, { "<random>" +
                        "<ordered>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</ordered>" +
                        "</random>", new StringValue("string") },
                // point
                { "<random>" +
                        "<multiple>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "</multiple>" +
                        "</random>", new PointValue(1, 1) }, { "<random>" +
                        "<ordered>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "</ordered>" +
                        "</random>", new PointValue(1, 1) },
                // pair
                { "<random>" +
                        "<multiple>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "</multiple>" +
                        "</random>", new PairValue("identifier_1", "identifier_2") }, { "<random>" +
                        "<ordered>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "</ordered>" +
                        "</random>", new PairValue("identifier_1", "identifier_2") },
                // directedPair
                { "<random>" +
                        "<multiple>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "</multiple>" +
                        "</random>", new DirectedPairValue("identifier_1", "identifier_2") }, { "<random>" +
                        "<ordered>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "</ordered>" +
                        "</random>", new DirectedPairValue("identifier_1", "identifier_2") },
                // duration
                { "<random>" +
                        "<multiple>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "</multiple>" +
                        "</random>", new DurationValue(1) }, { "<random>" +
                        "<ordered>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "</ordered>" +
                        "</random>", new DurationValue(1) },
                // file
                { "<random>" +
                        "<multiple>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "</multiple>" +
                        "</random>", ValueTestUtils.createTestFileValue("file") }, { "<random>" +
                        "<ordered>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "</ordered>" +
                        "</random>", ValueTestUtils.createTestFileValue("file") },
                // uri
                { "<random>" +
                        "<multiple>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</multiple>" +
                        "</random>", new UriValue("uri") }, { "<random>" +
                        "<ordered>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</ordered>" +
                        "</random>", new UriValue("uri") },
        });
    }

    /**
     * Constructs <code>Random</code> expression test.
     * 
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public RandomAcceptTest(String xml, Value expectedValue) {
        super(xml, expectedValue);
    }
}
