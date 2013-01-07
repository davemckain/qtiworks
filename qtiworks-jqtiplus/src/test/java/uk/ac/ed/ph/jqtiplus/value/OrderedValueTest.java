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
 * Tests <code>OrderedValue</code> implementation of <code>equals</code> and <code>hashCode</code> methods.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.OrderedValue
 */
@RunWith(Parameterized.class)
public class OrderedValueTest extends ValueTest {

    private static final Value ORDERED_1__1_2_3;

    private static final Value ORDERED_2__1_2_3;

    private static final Value ORDERED_3__3_2_1;

    private static final Value ORDERED_4__1_2_3_4;

    static {
        // ORDERED_1__1_2_3
        ORDERED_1__1_2_3 = OrderedValue.createOrderedValue(
                new IntegerValue(1),
                new IntegerValue(2),
                new IntegerValue(3));

        // ORDERED_2__1_2_3
        ORDERED_2__1_2_3 = OrderedValue.createOrderedValue(
                new IntegerValue(1),
                new IntegerValue(2),
                new IntegerValue(3));

        // ORDERED_3__3_2_1
        ORDERED_3__3_2_1 = OrderedValue.createOrderedValue(
                new IntegerValue(3),
                new IntegerValue(2),
                new IntegerValue(1));

        // ORDERED_4__1_2_3_4
        ORDERED_4__1_2_3_4 = OrderedValue.createOrderedValue(
                new IntegerValue(1),
                new IntegerValue(2),
                new IntegerValue(3),
                new IntegerValue(4));
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
                {true, OrderedValue.emptyValue(), NullValue.INSTANCE},
                {false, OrderedValue.createOrderedValue(new IntegerValue(1)), NullValue.INSTANCE},
                // IdentifierValue
                {false, OrderedValue.emptyValue(), new IdentifierValue("identifier")},
                {false, OrderedValue.createOrderedValue(new IdentifierValue("identifier")), new IdentifierValue("identifier")},
                // BooleanValue
                {false, OrderedValue.emptyValue(), BooleanValue.TRUE},
                {false, OrderedValue.createOrderedValue(BooleanValue.TRUE), BooleanValue.TRUE},
                {false, OrderedValue.emptyValue(), BooleanValue.FALSE},
                {false, OrderedValue.createOrderedValue(BooleanValue.FALSE), BooleanValue.FALSE},
                // IntegerValue
                {false, OrderedValue.emptyValue(), new IntegerValue(1)},
                {false, OrderedValue.createOrderedValue(new IntegerValue(1)), new IntegerValue(1)},
                // FloatValue
                {false, OrderedValue.emptyValue(), new FloatValue(1)},
                {false, OrderedValue.createOrderedValue(new FloatValue(1)), new FloatValue(1)},
                // StringValue
                {false, OrderedValue.emptyValue(), new StringValue("string")},
                {false, OrderedValue.createOrderedValue(new StringValue("string")), new StringValue("string")},
                // PointValue
                {false, OrderedValue.emptyValue(), new PointValue(1, 1)},
                {false, OrderedValue.createOrderedValue(new PointValue(1, 1)), new PointValue(1, 1)},
                // PairValue
                {false, OrderedValue.emptyValue(), new PairValue("ident1", "ident2")},
                {false, OrderedValue.createOrderedValue(new PairValue("ident1", "ident2")), new PairValue("ident1", "ident2")},
                // DirectedPairValue
                {false, OrderedValue.emptyValue(), new DirectedPairValue("ident1", "ident2")},
                {false, OrderedValue.createOrderedValue(new DirectedPairValue("ident1", "ident2")), new DirectedPairValue("ident1", "ident2")},
                // DurationValue
                {false, OrderedValue.emptyValue(), new DurationValue(1)},
                {false, OrderedValue.createOrderedValue(new DurationValue(1)), new DurationValue(1)},
                // FileValue
                {false, OrderedValue.emptyValue(), ValueTestUtils.createTestFileValue("file")},
                {false, OrderedValue.createOrderedValue(ValueTestUtils.createTestFileValue("file")), ValueTestUtils.createTestFileValue("file")},
                // UriValue
                {false, OrderedValue.emptyValue(), new UriValue("uri")},
                {false, OrderedValue.createOrderedValue(new UriValue("uri")), new UriValue("uri")},
                // MultipleValue
                {true, OrderedValue.emptyValue(), MultipleValue.emptyValue()},
                {false, OrderedValue.emptyValue(), MultipleValue.createMultipleValue(new IntegerValue(1))},
                {false, OrderedValue.createOrderedValue(new IntegerValue(1)), MultipleValue.emptyValue()},
                {false, OrderedValue.createOrderedValue(new IntegerValue(1)), MultipleValue.createMultipleValue(new IntegerValue(1))},
                // OrderedValue
                {true, OrderedValue.emptyValue(), OrderedValue.emptyValue()},
                {false, OrderedValue.emptyValue(), OrderedValue.createOrderedValue(new IntegerValue(1))},
                {false, OrderedValue.createOrderedValue(new IntegerValue(1)), OrderedValue.emptyValue()},
                {true, OrderedValue.createOrderedValue(new IntegerValue(1)), OrderedValue.createOrderedValue(new IntegerValue(1))},
                {true, ORDERED_1__1_2_3, ORDERED_2__1_2_3}, {false, ORDERED_1__1_2_3, ORDERED_3__3_2_1},
                {false, ORDERED_1__1_2_3, ORDERED_4__1_2_3_4},
                // RecordValue
                {true, OrderedValue.emptyValue(), RecordValue.emptyRecord()},
                {false, OrderedValue.emptyValue(), RecordValue.createRecordValue("identifier", new IntegerValue(1))},
                {false, OrderedValue.createOrderedValue(new IntegerValue(1)), RecordValue.emptyRecord()},
                {false, OrderedValue.createOrderedValue(new IntegerValue(1)), RecordValue.createRecordValue("identifier", new IntegerValue(1))},
                });
    }

    /**
     * Constructs this test.
     *
     * @param equals true if given values are equal; false otherwise
     * @param value1 first value
     * @param value2 second value
     */
    public OrderedValueTest(final boolean equals, final Value value1, final Value value2) {
        super(equals, value1, value2);
    }
}
