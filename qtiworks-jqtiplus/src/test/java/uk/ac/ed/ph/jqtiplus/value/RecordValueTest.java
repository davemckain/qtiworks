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

import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests <code>RecordValue</code> implementation of <code>equals</code> and <code>hashCode</code> methods.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.RecordValue
 */
@RunWith(Parameterized.class)
public class RecordValueTest extends ValueTest {

    private static final RecordValue RECORD_1__1_2_3;

    private static final RecordValue RECORD_2__1_2_3;

    private static final RecordValue RECORD_3__3_2_1;

    private static final RecordValue RECORD_4__1_2_3_4;

    static {
        // RECORD_1__1_2_3
        final Map<Identifier, SingleValue> recordBuilder = new HashMap<Identifier, SingleValue>();
        recordBuilder.put(Identifier.parseString("key_1"), new IntegerValue(1));
        recordBuilder.put(Identifier.parseString("key_2"), new IntegerValue(2));
        recordBuilder.put(Identifier.parseString("key_3"), new IntegerValue(3));
        RECORD_1__1_2_3 = (RecordValue) RecordValue.createRecordValue(recordBuilder);

        // RECORD_2__1_2_3
        RECORD_2__1_2_3 = (RecordValue) RecordValue.createRecordValue(recordBuilder);

        // RECORD_3__3_2_1
        recordBuilder.clear();
        recordBuilder.put(Identifier.parseString("key_3"), new IntegerValue(3));
        recordBuilder.put(Identifier.parseString("key_2"), new IntegerValue(2));
        recordBuilder.put(Identifier.parseString("key_1"), new IntegerValue(1));
        RECORD_3__3_2_1 = (RecordValue) RecordValue.createRecordValue(recordBuilder);

        // RECORD_4__1_2_3_4
        recordBuilder.clear();
        recordBuilder.put(Identifier.parseString("key_1"), new IntegerValue(1));
        recordBuilder.put(Identifier.parseString("key_2"), new IntegerValue(2));
        recordBuilder.put(Identifier.parseString("key_3"), new IntegerValue(3));
        recordBuilder.put(Identifier.parseString("key_4"), new IntegerValue(4));
        RECORD_4__1_2_3_4 = (RecordValue) RecordValue.createRecordValue(recordBuilder);
    }

    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // NullValue
                {true, RecordValue.emptyRecord(), NullValue.INSTANCE}, {false, RecordValue.createRecordValue("identifier", new IntegerValue(1)), NullValue.INSTANCE},
                // IdentifierValue
                {false, RecordValue.emptyRecord(), new IdentifierValue("identifier")}, {false, RecordValue.createRecordValue("identifier", new IdentifierValue("identifier")), new IdentifierValue("identifier")},
                // BooleanValue
                {false, RecordValue.emptyRecord(), BooleanValue.TRUE}, {false, RecordValue.createRecordValue("identifier", BooleanValue.TRUE), BooleanValue.TRUE}, {false, RecordValue.emptyRecord(), BooleanValue.FALSE}, {false, RecordValue.createRecordValue("identifier", BooleanValue.FALSE), BooleanValue.FALSE},
                // IntegerValue
                {false, RecordValue.emptyRecord(), new IntegerValue(1)}, {false, RecordValue.createRecordValue("identifier", new IntegerValue(1)), new IntegerValue(1)},
                // FloatValue
                {false, RecordValue.emptyRecord(), new FloatValue(1)}, {false, RecordValue.createRecordValue("identifier", new FloatValue(1)), new FloatValue(1)},
                // StringValue
                {false, RecordValue.emptyRecord(), new StringValue("string")}, {false, RecordValue.createRecordValue("identifier", new StringValue("string")), new StringValue("string")},
                // PointValue
                {false, RecordValue.emptyRecord(), new PointValue(1, 1)}, {false, RecordValue.createRecordValue("identifier", new PointValue(1, 1)), new PointValue(1, 1)},
                // PairValue
                {false, RecordValue.emptyRecord(), new PairValue("ident1", "ident2")}, {false, RecordValue.createRecordValue("identifier", new PairValue("ident1", "ident2")), new PairValue("ident1", "ident2")},
                // DirectedPairValue
                {false, RecordValue.emptyRecord(), new DirectedPairValue("ident1", "ident2")}, {false, RecordValue.createRecordValue("identifier", new DirectedPairValue("ident1", "ident2")), new DirectedPairValue("ident1", "ident2")},
                // DurationValue
                {false, RecordValue.emptyRecord(), new DurationValue(1)}, {false, RecordValue.createRecordValue("identifier", new DurationValue(1)), new DurationValue(1)},
                // FileValue
                {false, RecordValue.emptyRecord(), ValueTestUtils.createTestFileValue("file")}, {false, RecordValue.createRecordValue("identifier", ValueTestUtils.createTestFileValue("file")), ValueTestUtils.createTestFileValue("file")},
                // UriValue
                {false, RecordValue.emptyRecord(), new UriValue("uri")}, {false, RecordValue.createRecordValue("identifier", new UriValue("uri")), new UriValue("uri")},
                // MultipleValue
                {true, RecordValue.emptyRecord(), MultipleValue.emptyValue()}, {false, RecordValue.emptyRecord(), MultipleValue.createMultipleValue(new IntegerValue(1))}, {false, RecordValue.createRecordValue("identifier", new IntegerValue(1)), MultipleValue.emptyValue()}, {false, RecordValue.createRecordValue("identifier", new IntegerValue(1)), MultipleValue.createMultipleValue(new IntegerValue(1))},
                // OrderedValue
                {true, RecordValue.emptyRecord(), OrderedValue.emptyValue()}, {false, RecordValue.emptyRecord(), OrderedValue.createOrderedValue(new IntegerValue(1))}, {false, RecordValue.createRecordValue("identifier", new IntegerValue(1)), OrderedValue.emptyValue()}, {false, RecordValue.createRecordValue("identifier", new IntegerValue(1)), OrderedValue.createOrderedValue(new IntegerValue(1))},
                // RecordValue
                {true, RecordValue.emptyRecord(), RecordValue.emptyRecord()}, {false, RecordValue.emptyRecord(), RecordValue.createRecordValue("identifier", new IntegerValue(1))}, {false, RecordValue.createRecordValue("identifier", new IntegerValue(1)), RecordValue.emptyRecord()}, {true, RecordValue.createRecordValue("identifier", new IntegerValue(1)), RecordValue.createRecordValue("identifier", new IntegerValue(1))}, {true, RECORD_1__1_2_3, RECORD_2__1_2_3}, {true, RECORD_1__1_2_3, RECORD_3__3_2_1}, {false, RECORD_1__1_2_3, RECORD_4__1_2_3_4},
                });
    }

    /**
     * Constructs this test.
     *
     * @param equals true if given values are equal; false otherwise
     * @param value1 first value
     * @param value2 second value
     */
    public RecordValueTest(final boolean equals, final Value value1, final Value value2) {
        super(equals, value1, value2);
    }
}
