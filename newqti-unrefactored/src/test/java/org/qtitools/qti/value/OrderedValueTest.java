/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package org.qtitools.qti.value;

import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.DirectedPairValue;
import uk.ac.ed.ph.jqtiplus.value.DurationValue;
import uk.ac.ed.ph.jqtiplus.value.FileValue;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.PairValue;
import uk.ac.ed.ph.jqtiplus.value.PointValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.UriValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

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
public class OrderedValueTest extends ValueTest
{
    private static final OrderedValue ORDERED_1__1_2_3;
    private static final OrderedValue ORDERED_2__1_2_3;
    private static final OrderedValue ORDERED_3__3_2_1;
    private static final OrderedValue ORDERED_4__1_2_3_4;

    static
    {
        // ORDERED_1__1_2_3
        ORDERED_1__1_2_3 = new OrderedValue();
        ORDERED_1__1_2_3.add(new IntegerValue(1));
        ORDERED_1__1_2_3.add(new IntegerValue(2));
        ORDERED_1__1_2_3.add(new IntegerValue(3));
        // ORDERED_2__1_2_3
        ORDERED_2__1_2_3 = new OrderedValue();
        ORDERED_2__1_2_3.add(new IntegerValue(1));
        ORDERED_2__1_2_3.add(new IntegerValue(2));
        ORDERED_2__1_2_3.add(new IntegerValue(3));
        // ORDERED_3__3_2_1
        ORDERED_3__3_2_1 = new OrderedValue();
        ORDERED_3__3_2_1.add(new IntegerValue(3));
        ORDERED_3__3_2_1.add(new IntegerValue(2));
        ORDERED_3__3_2_1.add(new IntegerValue(1));
        // ORDERED_4__1_2_3_4
        ORDERED_4__1_2_3_4 = new OrderedValue();
        ORDERED_4__1_2_3_4.add(new IntegerValue(1));
        ORDERED_4__1_2_3_4.add(new IntegerValue(2));
        ORDERED_4__1_2_3_4.add(new IntegerValue(3));
        ORDERED_4__1_2_3_4.add(new IntegerValue(4));
    }

    /**
     * Creates test data for this test.
     *
     * @return test data for this test
     */
    @Parameters
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][]
        {
            // null
            {false, new OrderedValue(), null},
            {false, new OrderedValue(new IntegerValue(1)), null},
            // NullValue
            {true, new OrderedValue(), NullValue.INSTANCE},
            {false, new OrderedValue(new IntegerValue(1)), NullValue.INSTANCE},
            // IdentifierValue
            {false, new OrderedValue(), new IdentifierValue("identifier")},
            {false, new OrderedValue(new IdentifierValue("identifier")), new IdentifierValue("identifier")},
            // BooleanValue
            {false, new OrderedValue(), BooleanValue.TRUE},
            {false, new OrderedValue(BooleanValue.TRUE), BooleanValue.TRUE},
            {false, new OrderedValue(), BooleanValue.FALSE},
            {false, new OrderedValue(BooleanValue.FALSE), BooleanValue.FALSE},
            // IntegerValue
            {false, new OrderedValue(), new IntegerValue(1)},
            {false, new OrderedValue(new IntegerValue(1)), new IntegerValue(1)},
            // FloatValue
            {false, new OrderedValue(), new FloatValue(1)},
            {false, new OrderedValue(new FloatValue(1)), new FloatValue(1)},
            // StringValue
            {false, new OrderedValue(), new StringValue("string")},
            {false, new OrderedValue(new StringValue("string")), new StringValue("string")},
            // PointValue
            {false, new OrderedValue(), new PointValue(1, 1)},
            {false, new OrderedValue(new PointValue(1, 1)), new PointValue(1, 1)},
            // PairValue
            {false, new OrderedValue(), new PairValue("ident1", "ident2")},
            {false, new OrderedValue(new PairValue("ident1", "ident2")), new PairValue("ident1", "ident2")},
            // DirectedPairValue
            {false, new OrderedValue(), new DirectedPairValue("ident1", "ident2")},
            {false, new OrderedValue(new DirectedPairValue("ident1", "ident2")), new DirectedPairValue("ident1", "ident2")},
            // DurationValue
            {false, new OrderedValue(), new DurationValue(1)},
            {false, new OrderedValue(new DurationValue(1)), new DurationValue(1)},
            // FileValue
            {false, new OrderedValue(), new FileValue("file")},
            {false, new OrderedValue(new FileValue("file")), new FileValue("file")},
            // UriValue
            {false, new OrderedValue(), new UriValue("uri")},
            {false, new OrderedValue(new UriValue("uri")), new UriValue("uri")},
            // MultipleValue
            {true, new OrderedValue(), new MultipleValue()},
            {false, new OrderedValue(), new MultipleValue(new IntegerValue(1))},
            {false, new OrderedValue(new IntegerValue(1)), new MultipleValue()},
            {false, new OrderedValue(new IntegerValue(1)), new MultipleValue(new IntegerValue(1))},
            // OrderedValue
            {true, new OrderedValue(), new OrderedValue()},
            {false, new OrderedValue(), new OrderedValue(new IntegerValue(1))},
            {false, new OrderedValue(new IntegerValue(1)), new OrderedValue()},
            {true, new OrderedValue(new IntegerValue(1)), new OrderedValue(new IntegerValue(1))},
            {true, ORDERED_1__1_2_3, ORDERED_2__1_2_3},
            {false, ORDERED_1__1_2_3, ORDERED_3__3_2_1},
            {false, ORDERED_1__1_2_3, ORDERED_4__1_2_3_4},
            // RecordValue
            {true, new OrderedValue(), new RecordValue()},
            {false, new OrderedValue(), new RecordValue("identifier", new IntegerValue(1))},
            {false, new OrderedValue(new IntegerValue(1)), new RecordValue()},
            {false, new OrderedValue(new IntegerValue(1)), new RecordValue("identifier", new IntegerValue(1))},
        });
    }

    /**
     * Constructs this test.
     *
     * @param equals true if given values are equal; false otherwise
     * @param value1 first value
     * @param value2 second value
     */
    public OrderedValueTest(boolean equals, Value value1, Value value2)
    {
        super(equals, value1, value2);
    }
}
