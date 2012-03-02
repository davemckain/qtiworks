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
package uk.ac.ed.ph.jqtiplus.value;

import java.util.Arrays;
import java.util.Collection;

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
        RECORD_1__1_2_3 = new RecordValue();
        RECORD_1__1_2_3.add("key_1", new IntegerValue(1));
        RECORD_1__1_2_3.add("key_2", new IntegerValue(2));
        RECORD_1__1_2_3.add("key_3", new IntegerValue(3));
        // RECORD_2__1_2_3
        RECORD_2__1_2_3 = new RecordValue();
        RECORD_2__1_2_3.add("key_1", new IntegerValue(1));
        RECORD_2__1_2_3.add("key_2", new IntegerValue(2));
        RECORD_2__1_2_3.add("key_3", new IntegerValue(3));
        // RECORD_3__3_2_1
        RECORD_3__3_2_1 = new RecordValue();
        RECORD_3__3_2_1.add("key_3", new IntegerValue(3));
        RECORD_3__3_2_1.add("key_2", new IntegerValue(2));
        RECORD_3__3_2_1.add("key_1", new IntegerValue(1));
        // RECORD_4__1_2_3_4
        RECORD_4__1_2_3_4 = new RecordValue();
        RECORD_4__1_2_3_4.add("key_1", new IntegerValue(1));
        RECORD_4__1_2_3_4.add("key_2", new IntegerValue(2));
        RECORD_4__1_2_3_4.add("key_3", new IntegerValue(3));
        RECORD_4__1_2_3_4.add("key_4", new IntegerValue(4));
    }

    /**
     * Creates test data for this test.
     * 
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // null {false, new RecordValue(), null}, {false, new RecordValue("identifier", new IntegerValue(1)), null},
                // NullValue {true, new RecordValue(), NullValue.INSTANCE}, {false, new RecordValue("identifier", new IntegerValue(1)), NullValue.INSTANCE},
                // IdentifierValue {false, new RecordValue(), new IdentifierValue("identifier")}, {false, new RecordValue("identifier", new IdentifierValue("identifier")), new IdentifierValue("identifier")},
                // BooleanValue {false, new RecordValue(), BooleanValue.TRUE}, {false, new RecordValue("identifier", BooleanValue.TRUE), BooleanValue.TRUE}, {false, new RecordValue(), BooleanValue.FALSE}, {false, new RecordValue("identifier", BooleanValue.FALSE), BooleanValue.FALSE},
                // IntegerValue {false, new RecordValue(), new IntegerValue(1)}, {false, new RecordValue("identifier", new IntegerValue(1)), new IntegerValue(1)},
                // FloatValue {false, new RecordValue(), new FloatValue(1)}, {false, new RecordValue("identifier", new FloatValue(1)), new FloatValue(1)},
                // StringValue {false, new RecordValue(), new StringValue("string")}, {false, new RecordValue("identifier", new StringValue("string")), new StringValue("string")},
                // PointValue {false, new RecordValue(), new PointValue(1, 1)}, {false, new RecordValue("identifier", new PointValue(1, 1)), new PointValue(1, 1)},
                // PairValue {false, new RecordValue(), new PairValue("ident1", "ident2")}, {false, new RecordValue("identifier", new PairValue("ident1", "ident2")), new PairValue("ident1", "ident2")},
                // DirectedPairValue {false, new RecordValue(), new DirectedPairValue("ident1", "ident2")}, {false, new RecordValue("identifier", new DirectedPairValue("ident1", "ident2")), new DirectedPairValue("ident1", "ident2")},
                // DurationValue {false, new RecordValue(), new DurationValue(1)}, {false, new RecordValue("identifier", new DurationValue(1)), new DurationValue(1)},
                // FileValue {false, new RecordValue(), new FileValue("file")}, {false, new RecordValue("identifier", new FileValue("file")), new FileValue("file")},
                // UriValue {false, new RecordValue(), new UriValue("uri")}, {false, new RecordValue("identifier", new UriValue("uri")), new UriValue("uri")},
                // MultipleValue {true, new RecordValue(), new MultipleValue()}, {false, new RecordValue(), new MultipleValue(new IntegerValue(1))}, {false, new RecordValue("identifier", new IntegerValue(1)), new MultipleValue()}, {false, new RecordValue("identifier", new IntegerValue(1)), new MultipleValue(new IntegerValue(1))},
                // OrderedValue {true, new RecordValue(), new OrderedValue()}, {false, new RecordValue(), new OrderedValue(new IntegerValue(1))}, {false, new RecordValue("identifier", new IntegerValue(1)), new OrderedValue()}, {false, new RecordValue("identifier", new IntegerValue(1)), new OrderedValue(new IntegerValue(1))},
                // RecordValue {true, new RecordValue(), new RecordValue()}, {false, new RecordValue(), new RecordValue("identifier", new IntegerValue(1))}, {false, new RecordValue("identifier", new IntegerValue(1)), new RecordValue()}, {true, new RecordValue("identifier", new IntegerValue(1)), new RecordValue("identifier", new IntegerValue(1))}, {true, RECORD_1__1_2_3, RECORD_2__1_2_3}, {true, RECORD_1__1_2_3, RECORD_3__3_2_1}, {false, RECORD_1__1_2_3, RECORD_4__1_2_3_4},
                });
    }

    /**
     * Constructs this test.
     * 
     * @param equals true if given values are equal; false otherwise
     * @param value1 first value
     * @param value2 second value
     */
    public RecordValueTest(boolean equals, Value value1, Value value2) {
        super(equals, value1, value2);
    }
}
