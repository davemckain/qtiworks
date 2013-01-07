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
 * Test of <code>FieldValue</code> expression.
 * 
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.FieldValue
 */
@RunWith(Parameterized.class)
public class FieldValueAcceptTest extends ExpressionAcceptTest {

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // null
                { "<fieldValue fieldIdentifier='identifier'>" +
                        "<null/>" +
                        "</fieldValue>", NullValue.INSTANCE }, { "<fieldValue fieldIdentifier='identifier'>" +
                        "<recordEx/>" +
                        "</fieldValue>", NullValue.INSTANCE }, { "<fieldValue fieldIdentifier='key_0'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", NullValue.INSTANCE }, { "<fieldValue fieldIdentifier='key_12'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", NullValue.INSTANCE },
                // identifier
                { "<fieldValue fieldIdentifier='key_1'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", new IdentifierValue("identifier") },
                // boolean
                { "<fieldValue fieldIdentifier='key_2'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", BooleanValue.TRUE },
                // integer
                { "<fieldValue fieldIdentifier='key_3'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", new IntegerValue(1) },
                // float
                { "<fieldValue fieldIdentifier='key_4'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", new FloatValue(1) },
                // string
                { "<fieldValue fieldIdentifier='key_5'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", new StringValue("string") },
                // point
                { "<fieldValue fieldIdentifier='key_6'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", new PointValue(1, 1) },
                // pair
                { "<fieldValue fieldIdentifier='key_7'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", new PairValue("identifier_1", "identifier_2") },
                // directedPair
                { "<fieldValue fieldIdentifier='key_8'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", new DirectedPairValue("identifier_1", "identifier_2") },
                // duration
                { "<fieldValue fieldIdentifier='key_9'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", new DurationValue(1) },
                // file
                { "<fieldValue fieldIdentifier='key_10'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", ValueTestUtils.createTestFileValue("file") },
                // uri
                { "<fieldValue fieldIdentifier='key_11'>" +
                        "<recordEx identifiers='key_1 key_2 key_3 key_4 key_5 key_6 key_7 key_8 key_9 key_10 key_11'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>" +
                        "</fieldValue>", new UriValue("uri") },
        });
    }

    /**
     * Constructs <code>FieldValue</code> expression test.
     * 
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public FieldValueAcceptTest(String xml, Value expectedValue) {
        super(xml, expectedValue);
    }
}
