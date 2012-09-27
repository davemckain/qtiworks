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
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.PairValue;
import uk.ac.ed.ph.jqtiplus.value.PointValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
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
 * Test of <code>Record</code> expression.
 * 
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.RecordEx
 */
@RunWith(Parameterized.class)
public class RecordAcceptTest extends ExpressionAcceptTest {

    private static final RecordValue RECORD_1__1_2_3;

    static {
        // RECORD_1__1_2_3
        RECORD_1__1_2_3 = RecordValue.emptyRecord();
        RECORD_1__1_2_3.add("key_1", new IntegerValue(1));
        RECORD_1__1_2_3.add("key_2", new IntegerValue(2));
        RECORD_1__1_2_3.add("key_3", new IntegerValue(3));
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
                { "<recordEx>" +
                        "</recordEx>", NullValue.INSTANCE }, { "<recordEx identifiers='key_1'>" +
                        "<null/>" +
                        "</recordEx>", NullValue.INSTANCE },
                // identifier
                { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='identifier'>identifier</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", new IdentifierValue("identifier")) },
                // boolean
                { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='boolean'>true</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", BooleanValue.TRUE) }, { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='boolean'>false</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", BooleanValue.FALSE) },
                // integer
                { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", new IntegerValue(1)) }, { "<recordEx identifiers='key_1 key_2 key_3'>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</recordEx>", RECORD_1__1_2_3 }, { "<recordEx identifiers='key_1 key_a key_2 key_b key_3'>" +
                        "<baseValue baseType='integer'>1</baseValue>" +
                        "<null/>" +
                        "<baseValue baseType='integer'>2</baseValue>" +
                        "<null/>" +
                        "<baseValue baseType='integer'>3</baseValue>" +
                        "</recordEx>", RECORD_1__1_2_3 },
                // float
                { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='float'>1</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", new FloatValue(1)) },
                // string
                { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='string'>string</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", new StringValue("string")) },
                // point
                { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='point'>1 1</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", new PointValue(1, 1)) },
                // pair
                { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='pair'>identifier_1 identifier_2</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", new PairValue("identifier_1", "identifier_2")) },
                // directedPair
                { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='directedPair'>identifier_1 identifier_2</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", new DirectedPairValue("identifier_1", "identifier_2")) },
                // duration
                { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='duration'>1</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", new DurationValue(1)) },
                // file
                { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='file'>file</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", ValueTestUtils.createTestFileValue("file")) },
                // uri
                { "<recordEx identifiers='key_1'>" +
                        "<baseValue baseType='uri'>uri</baseValue>" +
                        "</recordEx>", RecordValue.createRecordValue("key_1", new UriValue("uri")) },
        });
    }

    /**
     * Constructs <code>Record</code> expression test.
     * 
     * @param xml xml data used for creation tested expression
     * @param expectedValue expected evaluated value
     */
    public RecordAcceptTest(String xml, Value expectedValue) {
        super(xml, expectedValue);
    }
}
