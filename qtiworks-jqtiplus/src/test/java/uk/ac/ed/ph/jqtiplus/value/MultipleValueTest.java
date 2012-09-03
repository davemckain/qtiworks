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
 * Tests <code>MultipleValue</code> implementation of <code>equals</code> and <code>hashCode</code> methods.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.MultipleValue
 */
@RunWith(Parameterized.class)
public class MultipleValueTest extends ValueTest {

    private static final MultipleValue MULTIPLE_1__1_2_3;

    private static final MultipleValue MULTIPLE_2__1_2_3;

    private static final MultipleValue MULTIPLE_3__3_2_1;

    private static final MultipleValue MULTIPLE_4__1_2_3_4;

    static {
        // MULTIPLE_1__1_2_3
        MULTIPLE_1__1_2_3 = new MultipleValue();
        MULTIPLE_1__1_2_3.add(new IntegerValue(1));
        MULTIPLE_1__1_2_3.add(new IntegerValue(2));
        MULTIPLE_1__1_2_3.add(new IntegerValue(3));
        // MULTIPLE_2__1_2_3
        MULTIPLE_2__1_2_3 = new MultipleValue();
        MULTIPLE_2__1_2_3.add(new IntegerValue(1));
        MULTIPLE_2__1_2_3.add(new IntegerValue(2));
        MULTIPLE_2__1_2_3.add(new IntegerValue(3));
        // MULTIPLE_3__3_2_1
        MULTIPLE_3__3_2_1 = new MultipleValue();
        MULTIPLE_3__3_2_1.add(new IntegerValue(3));
        MULTIPLE_3__3_2_1.add(new IntegerValue(2));
        MULTIPLE_3__3_2_1.add(new IntegerValue(1));
        // MULTIPLE_4__1_2_3_4
        MULTIPLE_4__1_2_3_4 = new MultipleValue();
        MULTIPLE_4__1_2_3_4.add(new IntegerValue(1));
        MULTIPLE_4__1_2_3_4.add(new IntegerValue(2));
        MULTIPLE_4__1_2_3_4.add(new IntegerValue(3));
        MULTIPLE_4__1_2_3_4.add(new IntegerValue(4));
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
                {true, new MultipleValue(), NullValue.INSTANCE},
                {false, new MultipleValue(new IntegerValue(1)), NullValue.INSTANCE},
                // IdentifierValue
                {false, new MultipleValue(), new IdentifierValue("identifier")},
                {false, new MultipleValue(new IdentifierValue("identifier")), new IdentifierValue("identifier")},
                // BooleanValue
                {false, new MultipleValue(), BooleanValue.TRUE},
                {false, new MultipleValue(BooleanValue.TRUE), BooleanValue.TRUE},
                {false, new MultipleValue(), BooleanValue.FALSE},
                {false, new MultipleValue(BooleanValue.FALSE), BooleanValue.FALSE},
                // IntegerValue
                {false, new MultipleValue(), new IntegerValue(1)},
                {false, new MultipleValue(new IntegerValue(1)), new IntegerValue(1)},
                // FloatValue
                {false, new MultipleValue(), new FloatValue(1)},
                {false, new MultipleValue(new FloatValue(1)), new FloatValue(1)},
                // StringValue
                {false, new MultipleValue(), new StringValue("string")},
                {false, new MultipleValue(new StringValue("string")), new StringValue("string")},
                // PointValue
                {false, new MultipleValue(), new PointValue(1, 1)},
                {false, new MultipleValue(new PointValue(1, 1)), new PointValue(1, 1)},
                // PairValue
                {false, new MultipleValue(), new PairValue("ident1", "ident2")},
                {false, new MultipleValue(new PairValue("ident1", "ident2")), new PairValue("ident1", "ident2")},
                // DirectedPairValue
                {false, new MultipleValue(), new DirectedPairValue("ident1", "ident2")},
                {false, new MultipleValue(new DirectedPairValue("ident1", "ident2")), new DirectedPairValue("ident1", "ident2")},
                // DurationValue
                {false, new MultipleValue(), new DurationValue(1)},
                {false, new MultipleValue(new DurationValue(1)), new DurationValue(1)},
                // FileValue
                {false, new MultipleValue(), ValueTestUtils.createTestFileValue("file")},
                {false, new MultipleValue(ValueTestUtils.createTestFileValue("file")), ValueTestUtils.createTestFileValue("file")},
                // UriValue
                {false, new MultipleValue(), new UriValue("uri")},
                {false, new MultipleValue(new UriValue("uri")), new UriValue("uri")},
                // MultipleValue
                {true, new MultipleValue(), new MultipleValue()},
                {false, new MultipleValue(), new MultipleValue(new IntegerValue(1))},
                {false, new MultipleValue(new IntegerValue(1)), new MultipleValue()},
                {true, new MultipleValue(new IntegerValue(1)), new MultipleValue(new IntegerValue(1))},
                {true, MULTIPLE_1__1_2_3, MULTIPLE_2__1_2_3},
                {true, MULTIPLE_1__1_2_3, MULTIPLE_3__3_2_1},
                {false, MULTIPLE_1__1_2_3, MULTIPLE_4__1_2_3_4},
                // OrderedValue
                {true, new MultipleValue(), new OrderedValue()},
                {false, new MultipleValue(), new OrderedValue(new IntegerValue(1))},
                {false, new MultipleValue(new IntegerValue(1)), new OrderedValue()},
                {false, new MultipleValue(new IntegerValue(1)), new OrderedValue(new IntegerValue(1))},
                // RecordValue
                {true, new MultipleValue(), new RecordValue()},
                {false, new MultipleValue(), new RecordValue("identifier", new IntegerValue(1))},
                {false, new MultipleValue(new IntegerValue(1)), new RecordValue()},
                {false, new MultipleValue(new IntegerValue(1)), new RecordValue("identifier", new IntegerValue(1))},
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
