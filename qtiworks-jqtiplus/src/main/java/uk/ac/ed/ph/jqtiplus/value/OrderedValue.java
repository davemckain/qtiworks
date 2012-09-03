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

/**
 * Implementation of "sequence-type" container.
 * <p>
 * This container can be ordered or NULL (if empty).
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @author Jiri Kajaba
 */
public final class OrderedValue extends ListValue {

    private static final long serialVersionUID = 5819887477828683688L;

    /**
     * Constructs empty (NULL) <code>OrderedValue</code> container.
     */
    public OrderedValue() {
        super();
    }

    /**
     * Constructs <code>OrderedValue</code> containing the given value
     *
     * @param value added <code>SingleValue</code>
     */
    public OrderedValue(final SingleValue value) {
        super(value);
    }

    /**
     * Constructs <code>OrderedValue</code> containing the given values
     *
     * @param values added <code>SingleValue</code>s
     */
    public OrderedValue(final Iterable<? extends SingleValue> values) {
        super(values);
    }

    /**
     * Constructs empty (NULL) <code>OrderedValue</code> container and adds given <code>OrderedValue</code> into it.
     *
     * @param value added <code>OrderedValue</code>
     */
    public OrderedValue(final OrderedValue value) {
        super();
        merge(value);
    }

    @Override
    public Cardinality getCardinality() {
        if (isNull()) {
            return null;
        }

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
        if (orderedValue.isNull()) {
            return false;
        }

        final SingleValue firstValue = orderedValue.container.get(0);

        // Try to find first value in this container.
        for (int i = 0; i < container.size(); i++) {
            if (container.get(i).equals(firstValue)) {
                // First value was found in this container.
                boolean found = true;

                // Compares rest of values (from 2nd to the last).
                for (int j = 1; j < orderedValue.container.size(); j++) {
                    if (i + j >= container.size()) {
                        return false; // End of this container. Stop searching.
                    }

                    if (!orderedValue.container.get(j).equals(container.get(i + j))) {
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
        return container.equals(other.container);
    }

    @Override
    public final int hashCode() {
        return container.hashCode();
    }
}
