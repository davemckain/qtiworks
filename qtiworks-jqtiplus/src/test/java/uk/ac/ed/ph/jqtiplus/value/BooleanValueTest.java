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
package uk.ac.ed.ph.jqtiplus.value;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests <code>BooleanValue</code> implementation of <code>equals</code> and <code>hashCode</code> methods.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.BooleanValue
 */
@RunWith(Parameterized.class)
public class BooleanValueTest extends ValueTest {

    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // null
                {false, BooleanValue.TRUE, null}, {false, BooleanValue.FALSE, null},
                // NullValue
                {false, BooleanValue.TRUE, NullValue.INSTANCE}, {false, BooleanValue.FALSE, NullValue.INSTANCE},
                // IdentifierValue
                {false, BooleanValue.TRUE, new IdentifierValue("identifier")}, {false, BooleanValue.FALSE, new IdentifierValue("identifier")},
                // BooleanValue
                {true, BooleanValue.TRUE, BooleanValue.TRUE}, {true, BooleanValue.FALSE, BooleanValue.FALSE}, {false, BooleanValue.TRUE, BooleanValue.FALSE}, {false, BooleanValue.FALSE, BooleanValue.TRUE},
                // IntegerValue
                {false, BooleanValue.TRUE, new IntegerValue(1)}, {false, BooleanValue.FALSE, new IntegerValue(1)},
                // FloatValue
                {false, BooleanValue.TRUE, new FloatValue(1)}, {false, BooleanValue.FALSE, new FloatValue(1)},
                // StringValue
                {false, BooleanValue.TRUE, new StringValue("string")}, {false, BooleanValue.FALSE, new StringValue("string")},
                // PointValue
                {false, BooleanValue.TRUE, new PointValue(1, 1)}, {false, BooleanValue.FALSE, new PointValue(1, 1)},
                // PairValue
                {false, BooleanValue.TRUE, new PairValue("ident1", "ident2")}, {false, BooleanValue.FALSE, new PairValue("ident1", "ident2")},
                // DirectedPairValue
                {false, BooleanValue.TRUE, new DirectedPairValue("ident1", "ident2")}, {false, BooleanValue.FALSE, new DirectedPairValue("ident1", "ident2")},
                // DurationValue
                {false, BooleanValue.TRUE, new DurationValue(1)}, {false, BooleanValue.FALSE, new DurationValue(1)},
                // FileValue
                {false, BooleanValue.TRUE, ValueTestUtils.createTestFileValue("file")}, {false, BooleanValue.FALSE, ValueTestUtils.createTestFileValue("file")},
                // UriValue
                {false, BooleanValue.TRUE, new UriValue("uri")}, {false, BooleanValue.FALSE, new UriValue("uri")},
                // MultipleValue
                {false, BooleanValue.TRUE, MultipleValue.emptyValue()}, {false, BooleanValue.TRUE, MultipleValue.createMultipleValue(BooleanValue.TRUE)}, {false, BooleanValue.FALSE, MultipleValue.emptyValue()}, {false, BooleanValue.FALSE, MultipleValue.createMultipleValue(BooleanValue.FALSE)},
                // OrderedValue
                {false, BooleanValue.TRUE, OrderedValue.emptyValue()}, {false, BooleanValue.TRUE, OrderedValue.createOrderedValue(BooleanValue.TRUE)}, {false, BooleanValue.FALSE, OrderedValue.emptyValue()}, {false, BooleanValue.FALSE, OrderedValue.createOrderedValue(BooleanValue.FALSE)},
                // RecordValue
                {false, BooleanValue.TRUE, RecordValue.emptyRecord()}, {false, BooleanValue.TRUE, RecordValue.createRecordValue("identifier", BooleanValue.TRUE)}, {false, BooleanValue.FALSE, RecordValue.emptyRecord()}, {false, BooleanValue.FALSE, RecordValue.createRecordValue("identifier", BooleanValue.FALSE)},
                });
    }

    /**
     * Constructs this test.
     *
     * @param equals true if given values are equal; false otherwise
     * @param value1 first value
     * @param value2 second value
     */
    public BooleanValueTest(final boolean equals, final Value value1, final Value value2) {
        super(equals, value1, value2);
    }
}
