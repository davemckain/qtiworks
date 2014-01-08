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

import uk.ac.ed.ph.jqtiplus.exception.QtiContainerException;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Superclass for multiple and ordered containers.
 * <p>
 * This container can contain 1..N non NULL single values of the same <code>BaseType</code>.
 * <p>
 * In JQTI+, these values are now immutable. Subclasses now use factory methods instead of
 * constructors, and empty containers (which are treated as NULL) now generate {@link NullValue}
 * instances rather than instances of this class.
 *
 * @author David McKain
 * @author Jiri Kajaba (original version)
 */
public abstract class ListValue extends ContainerValue implements Iterable<SingleValue> {

    private static final long serialVersionUID = 4655949258467611295L;

    /** Container for single values. */
    protected final SingleValue[] container;

    protected ListValue(final SingleValue value) {
        container = new SingleValue[1];
        container[0] = value;
    }

    protected ListValue(final Collection<? extends SingleValue> values) {
        if (values.isEmpty()) {
            throw new QtiLogicException("Did not expect subclass to call this method with an empty list");
        }
        container = new SingleValue[values.size()];
        BaseType contentBaseType = null;
        int i = 0;
        for (final SingleValue value : values) {
            if (contentBaseType==null) {
                contentBaseType = value.getBaseType();
            }
            else {
                if (value.getBaseType()!=contentBaseType) {
                    throw new QtiContainerException("Values in a list container must all have the same baseType");
                }
            }
            container[i++] = value;
        }
    }

    protected ListValue(final SingleValue... values) {
        Assert.notNull(values, "values");
        if (values.length==0) {
            throw new QtiLogicException("Did not expect subclass to call this method with an empty list");
        }
        container = new SingleValue[values.length];
        container[0] = values[0];
        final BaseType contentBaseType = container[0].getBaseType();
        for (int i=1; i<values.length; i++) {
            if (values[i].getBaseType()!=contentBaseType) {
                throw new QtiContainerException("Values in a list container must all have the same baseType");
            }
            container[i] = values[i];
        }
    }

    @Override
    public final BaseType getBaseType() {
        return container[0].getBaseType();
    }

    @Override
    public final Iterator<SingleValue> iterator() {
        return ObjectUtilities.createView(container).iterator();
    }

    public final List<SingleValue> getAll() {
        return ObjectUtilities.createView(container);
    }

    @SuppressWarnings("unchecked")
    public final <F extends SingleValue> Collection<F> values(final Class<F> expectedValueClass) {
        if (expectedValueClass.isInstance(container[0])) {
            return (Collection<F>) ObjectUtilities.createView(container);
        }
        throw new QtiContainerException("Expected list container to contain " + expectedValueClass);

    }

    @Override
    public int size() {
        return container.length;
    }

    public SingleValue get(final int index) {
        if (index<0 || index>=container.length) {
            throw new QtiContainerException("Index " + index + " out of bounds of container of size " + container.length);
        }
        return container[index];
    }

    /**
     * Returns true if this container is ordered; false otherwise.
     *
     * @return true if this container is ordered; false otherwise
     */
    public abstract boolean isOrdered();

    /**
     * Returns true if this container contains given <code>SingleValue</code>; false otherwise.
     *
     * @param testValue given <code>SingleValue</code>
     * @return true if this container contains given <code>SingleValue</code>; false otherwise
     */
    public final boolean contains(final SingleValue testValue) {
        for (final SingleValue singleValue : container) {
            if (singleValue.equals(testValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns number of occurrences of given <code>SingleValue</code>.
     *
     * @param testValue given <code>SingleValue</code>
     * @return number of occurrences of given <code>SingleValue</code>
     */
    public final int count(final SingleValue testValue) {
        int count = 0;
        for (final SingleValue singleValue : container) {
            if (singleValue.equals(testValue)) {
                count++;
            }
        }
        return count;
    }

    /**
     * This outputs this value in the format used when describing ordered and multiple
     * cardinalities, i.e.
     *
     * <pre>[value1, value2, value3]</pre>
     */
    @Override
    public final String toQtiString() {
        final StringBuilder resultBuilder = new StringBuilder("[");
        for (int i=0; i<container.length; i++) {
            resultBuilder.append(container[i].toQtiString());
            if (i<container.length-1) {
                resultBuilder.append(',');
            }
        }
        resultBuilder.append(']');
        return resultBuilder.toString();
    }
}
