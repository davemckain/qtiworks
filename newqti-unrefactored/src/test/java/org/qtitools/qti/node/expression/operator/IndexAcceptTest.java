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
import uk.ac.ed.ph.jqtiplus.value.FileValue;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
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
 * Test of <code>Index</code> expression.
 * 
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.Index
 */
@RunWith(Parameterized.class)
public class IndexAcceptTest extends ExpressionAcceptTest {

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // null
                { "<index n='1'>" +
                        "<null/>" +
                        "</index>", NullValue.INSTANCE }, { "<index n='1'>" +
                        "<ordered/>" +
                        "</index>", NullValue.INSTANCE }, { "<index n='1'>" +
                        "<ordered>" +
                        "<null/>" +
                        "</ordered>" +
                        "</index>", NullValue.INSTANCE },
                // identifier
                { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "</ordered>" +
                        "</index>", new IdentifierValue("identifier") },
                // boolean
                { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</ordered>" +
                        "</index>", BooleanValue.TRUE }, { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='boolean'>false</baseValue>" +
                        "</ordered>" +
                        "</index>", BooleanValue.FALSE },
                // integer
                { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</ordered>" +
                        "</index>", new IntegerValue(1) }, { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</ordered>" +
                        "</index>", new IntegerValue(1) }, { "<index n='2'>" +
                        "<ordered>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</ordered>" +
                        "</index>", new IntegerValue(2) }, { "<index n='3'>" +
                        "<ordered>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</ordered>" +
                        "</index>", new IntegerValue(3) }, { "<index n='4'>" +
                        "<ordered>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</ordered>" +
                        "</index>", NullValue.INSTANCE }, { "<index n='5'>" +
                        "<ordered>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</ordered>" +
                        "</index>", NullValue.INSTANCE },
                // float
                { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "</ordered>" +
                        "</index>", new FloatValue(1) },
                // string
                { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</ordered>" +
                        "</index>", new StringValue("string") },
                // point
                { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "</ordered>" +
                        "</index>", new PointValue(1, 1) },
                // pair
                { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "</ordered>" +
                        "</index>", new PairValue("identifier_1", "identifier_2") },
                // directedPair
                { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "</ordered>" +
                        "</index>", new DirectedPairValue("identifier_1", "identifier_2") },
                // duration
                { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "</ordered>" +
                        "</index>", new DurationValue(1) },
                // file
                { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "</ordered>" +
                        "</index>", new FileValue("file") },
                // uri
                { "<index n='1'>" +
                        "<ordered>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</ordered>" +
                        "</index>", new UriValue("uri") },
        });
    }

    /**
     * Constructs <code>Index</code> expression test.
     * 
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public IndexAcceptTest(String xml, Value expectedValue) {
        super(xml, expectedValue);
    }
}
