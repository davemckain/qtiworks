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

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.util.Arrays;
import java.util.Collection;

/**
 * Implementation of a non-NULL ordered value.
 *
 * @author Jiri Kajaba
 * @author David McKain (revised version)
 */
public final class OrderedValue extends ListValue {

    private static final long serialVersionUID = 5819887477828683688L;

    public static NullValue emptyValue() {
        return NullValue.INSTANCE;
    }

    public static OrderedValue createOrderedValue(final SingleValue value) {
        Assert.notNull(value, "value");
        return new OrderedValue(value);
    }

    public static Value createOrderedValue(final SingleValue... values) {
        Assert.notNull(values, "values");
        return values.length==0 ? NullValue.INSTANCE : new OrderedValue(values);
    }

    public static Value createOrderedValue(final Collection<? extends SingleValue> values) {
        Assert.notNull(values, "values");
        return values.isEmpty() ? NullValue.INSTANCE : new OrderedValue(values);
    }

    private OrderedValue(final SingleValue value) {
        super(value);
    }

    private OrderedValue(final SingleValue... values) {
        super(values);
    }

    private OrderedValue(final Collection<? extends SingleValue> values) {
        super(values);
    }

    @Override
    public Cardinality getCardinality() {
        return Cardinality.ORDERED;
    }

    @Override
    public boolean isOrdered() {
        return true;
    }

    /**
     * Returns true if this container contains given <code>OrderedValue</code>; false otherwise.
     *
     * @param orderedValue given <code>OrderedValue</code>
     * @return true if this container contains given <code>OrderedValue</code>; false otherwise
     */
    public boolean contains(final OrderedValue orderedValue) {
        final SingleValue firstValue = orderedValue.container[0];

        // Try to find first value in this container.
        for (int i=0; i<container.length; i++) {
            if (container[i].equals(firstValue)) {
                // First value was found in this container.
                boolean found = true;

                // Compares rest of values (from 2nd to the last).
                for (int j = 1; j < orderedValue.container.length; j++) {
                    if (i + j >= container.length) {
                        return false; // End of this container. Stop searching.
                    }

                    if (!orderedValue.container[j].equals(container[i + j])) {
                        // Values are different. Break searching.
                        found = false;
                        break;
                    }
                }

                if (found) {
                    return true; // Searching was not broken. All values were found in this container.
                }
            }
        }

        return false; // End of this container. Stop searching.
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof OrderedValue)) {
            return false;
        }

        final OrderedValue other = (OrderedValue) object;
        return Arrays.equals(container, other.container);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(container);
    }
}
