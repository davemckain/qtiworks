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
 * Tests <code>MultipleValue</code> implementation of <code>equals</code> and <code>hashCode</code> methods.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.MultipleValue
 */
@RunWith(Parameterized.class)
public class MultipleValueTest extends ValueTest {

    private static final Value MULTIPLE_1__1_2_3;

    private static final Value MULTIPLE_2__1_2_3;

    private static final Value MULTIPLE_3__3_2_1;

    private static final Value MULTIPLE_4__1_2_3_4;

    static {
        // MULTIPLE_1__1_2_3
        MULTIPLE_1__1_2_3 = MultipleValue.createMultipleValue(
                new IntegerValue(1),
                new IntegerValue(2),
                new IntegerValue(3));

        // MULTIPLE_2__1_2_3
        MULTIPLE_2__1_2_3 = MultipleValue.createMultipleValue(
                new IntegerValue(1),
                new IntegerValue(2),
                new IntegerValue(3));

        // MULTIPLE_3__3_2_1
        MULTIPLE_3__3_2_1 = MultipleValue.createMultipleValue(
                new IntegerValue(3),
                new IntegerValue(1),
                new IntegerValue(2));

        // MULTIPLE_4__1_2_3_4
        MULTIPLE_4__1_2_3_4 = MultipleValue.createMultipleValue(
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
                {true, MultipleValue.emptyValue(), NullValue.INSTANCE},
                {false, MultipleValue.createMultipleValue(new IntegerValue(1)), NullValue.INSTANCE},
                // IdentifierValue
                {false, MultipleValue.emptyValue(), new IdentifierValue("identifier")},
                {false, MultipleValue.createMultipleValue(new IdentifierValue("identifier")), new IdentifierValue("identifier")},
                // BooleanValue
                {false, MultipleValue.emptyValue(), BooleanValue.TRUE},
                {false, MultipleValue.createMultipleValue(BooleanValue.TRUE), BooleanValue.TRUE},
                {false, MultipleValue.emptyValue(), BooleanValue.FALSE},
                {false, MultipleValue.createMultipleValue(BooleanValue.FALSE), BooleanValue.FALSE},
                // IntegerValue
                {false, MultipleValue.emptyValue(), new IntegerValue(1)},
                {false, MultipleValue.createMultipleValue(new IntegerValue(1)), new IntegerValue(1)},
                // FloatValue
                {false, MultipleValue.emptyValue(), new FloatValue(1)},
                {false, MultipleValue.createMultipleValue(new FloatValue(1)), new FloatValue(1)},
                // StringValue
                {false, MultipleValue.emptyValue(), new StringValue("string")},
                {false, MultipleValue.createMultipleValue(new StringValue("string")), new StringValue("string")},
                // PointValue
                {false, MultipleValue.emptyValue(), new PointValue(1, 1)},
                {false, MultipleValue.createMultipleValue(new PointValue(1, 1)), new PointValue(1, 1)},
                // PairValue
                {false, MultipleValue.emptyValue(), new PairValue("ident1", "ident2")},
                {false, MultipleValue.createMultipleValue(new PairValue("ident1", "ident2")), new PairValue("ident1", "ident2")},
                // DirectedPairValue
                {false, MultipleValue.emptyValue(), new DirectedPairValue("ident1", "ident2")},
                {false, MultipleValue.createMultipleValue(new DirectedPairValue("ident1", "ident2")), new DirectedPairValue("ident1", "ident2")},
                // DurationValue
                {false, MultipleValue.emptyValue(), new DurationValue(1)},
                {false, MultipleValue.createMultipleValue(new DurationValue(1)), new DurationValue(1)},
                // FileValue
                {false, MultipleValue.emptyValue(), ValueTestUtils.createTestFileValue("file")},
                {false, MultipleValue.createMultipleValue(ValueTestUtils.createTestFileValue("file")), ValueTestUtils.createTestFileValue("file")},
                // UriValue
                {false, MultipleValue.emptyValue(), new UriValue("uri")},
                {false, MultipleValue.createMultipleValue(new UriValue("uri")), new UriValue("uri")},
                // MultipleValue
                {true, MultipleValue.emptyValue(), MultipleValue.emptyValue()},
                {false, MultipleValue.emptyValue(), MultipleValue.createMultipleValue(new IntegerValue(1))},
                {false, MultipleValue.createMultipleValue(new IntegerValue(1)), MultipleValue.emptyValue()},
                {true, MultipleValue.createMultipleValue(new IntegerValue(1)), MultipleValue.createMultipleValue(new IntegerValue(1))},
                {true, MULTIPLE_1__1_2_3, MULTIPLE_2__1_2_3},
                {true, MULTIPLE_1__1_2_3, MULTIPLE_3__3_2_1},
                {false, MULTIPLE_1__1_2_3, MULTIPLE_4__1_2_3_4},
                // OrderedValue
                {true, MultipleValue.emptyValue(), OrderedValue.emptyValue()},
                {false, MultipleValue.emptyValue(), OrderedValue.createOrderedValue(new IntegerValue(1))},
                {false, MultipleValue.createMultipleValue(new IntegerValue(1)), OrderedValue.emptyValue()},
                {false, MultipleValue.createMultipleValue(new IntegerValue(1)), OrderedValue.createOrderedValue(new IntegerValue(1))},
                // RecordValue
                {true, MultipleValue.emptyValue(), RecordValue.emptyRecord()},
                {false, MultipleValue.emptyValue(), RecordValue.createRecordValue("identifier", new IntegerValue(1))},
                {false, MultipleValue.createMultipleValue(new IntegerValue(1)), RecordValue.emptyRecord()},
                {false, MultipleValue.createMultipleValue(new IntegerValue(1)), RecordValue.createRecordValue("identifier", new IntegerValue(1))},
        });
    }

    /**
     * Constructs this test.
     *
     * @param equals true if given values are equal; false otherwise
     * @param value1 first value
     * @param value2 second value
     */
    public MultipleValueTest(final boolean equals, final Value value1, final Value value2) {
        super(equals, value1, value2);
    }
}
