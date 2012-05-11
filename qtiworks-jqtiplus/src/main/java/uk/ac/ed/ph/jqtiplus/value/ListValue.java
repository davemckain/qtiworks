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

import uk.ac.ed.ph.jqtiplus.exception.QtiBaseTypeException;
import uk.ac.ed.ph.jqtiplus.exception.QtiEvaluationException;
import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Superclass for multiple and ordered containers.
 * <p>
 * This container can contain 0..N non NULL single values of the same <code>BaseType</code>.
 * <p>
 * This container can be multiple, ordered or NULL (if empty).
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public abstract class ListValue extends AbstractValue implements Cloneable, MultiValue, Iterable<SingleValue> {

    private static final long serialVersionUID = 4655949258467611295L;

    /** Container for single values. */
    protected List<SingleValue> container;

    /**
     * Constructs empty (NULL) <code>ListValue</code> container.
     */
    public ListValue() {
        container = new ArrayList<SingleValue>();
    }

    /**
     * Constructs empty (NULL) <code>ListValue</code> container and adds given <code>SingleValue</code> into it.
     *
     * @param value added <code>SingleValue</code>
     */
    public ListValue(final SingleValue value) {
        container = new ArrayList<SingleValue>();

        add(value);
    }

    /**
     * Constructs empty (NULL) <code>ListValue</code> container and copies all given <code>SingleValue</code>s into it.
     *
     * @param values added <code>SingleValue</code>s
     */
    public ListValue(final Iterable<? extends SingleValue> values) {
        container = new ArrayList<SingleValue>();

        for (final SingleValue value : values) {
            add(value);
        }
    }

    @Override
    public Iterator<SingleValue> iterator() {
        return container.iterator();
    }

    @Override
    public boolean isNull() {
        return container.size() == 0;
    }

    @Override
    public BaseType getBaseType() {
        if (isNull()) {
            return null;
        }

        return container.get(0).getBaseType();
    }

    @Override
    public int size() {
        return container.size();
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
     * @param value given <code>SingleValue</code>
     * @return true if this container contains given <code>SingleValue</code>; false otherwise
     */
    public boolean contains(final SingleValue value) {
        return container.contains(value);
    }

    /**
     * Returns number of occurrences of given <code>SingleValue</code>.
     *
     * @param value given <code>SingleValue</code>
     * @return number of occurrences of given <code>SingleValue</code>
     */
    public int count(final SingleValue value) {
        int count = 0;

        for (final SingleValue singleValue : container) {
            if (singleValue.equals(value)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns <code>SingleValue</code> on given index.
     *
     * @param index given index
     * @return <code>SingleValue</code> on given index
     */
    public SingleValue get(final int index) {
        return container.get(index);
    }

    /**
     * Returns a list of <code>SingleValue</code>s.
     *
     * @return list of <code>SingleValue</code>s.
     */
    public List<SingleValue> getAll() {
        return container;
    }

    /**
     * Adds <code>SingleValue</code> into this container.
     * <p>
     * This container can contain only values of the same <code>BaseType</code>.
     * <p>
     * NULL <code>SingleValue</code> is ignored.
     *
     * @param value added <code>SingleValue</code>
     * @return true if value was added; false otherwise
     * @throws QtiBaseTypeException if <code>BaseType</code> is not same
     */
    public boolean add(final SingleValue value) throws QtiBaseTypeException {
        if (value == null || value.isNull()) {
            return false;
        }

        if (!isNull() && getBaseType() != value.getBaseType()) {
            throw new QtiBaseTypeException("Invalid baseType: " + value.getBaseType());
        }

        return container.add(value);
    }

    public boolean merge(final ListValue value) throws QtiBaseTypeException {
        if (value.isNull()) {
            return false;
        }

        if (!isNull() && getBaseType() != value.getBaseType()) {
            throw new QtiBaseTypeException("Invalid baseType: " + value.getBaseType());
        }

        return container.addAll(value.container);
    }

    /**
     * Removes all occurrences of given <code>SingleValue</code> from this container.
     *
     * @param value given <code>SingleValue</code>
     * @return true if value was removed (container contained this value); false otherwise
     */
    public boolean removeAll(final SingleValue value) {
        boolean result = false;

        while (container.remove(value)) {
            result = true;
        }

        return result;
    }

    @Override
    public Object clone() throws QtiEvaluationException {
        try {
            final ListValue value = (ListValue) super.clone();
            if (container != null) {
                value.container = new ArrayList<SingleValue>(container);
            }
            return value;
        }
        catch (final CloneNotSupportedException e) {
            throw new QtiLogicException("Cannot clone container", e);
        }
    }

    @Override
    public final int hashCode() {
        return container.hashCode();
    }

    /**
     * This outputs this value in the format used when describing ordered and multiple
     * cardinalities, i.e.
     *
     * <pre>[value1,value2,value3]</pre>
     */
    @Override
    public final String toQtiString() {
        final StringBuilder stringBuilder = new StringBuilder('[');
        final Iterator<SingleValue> iterator = container.iterator();
        while (iterator.hasNext()) {
            final SingleValue value = iterator.next();
            stringBuilder.append(value.toQtiString());
            if (iterator.hasNext()) {
                stringBuilder.append(',');
            }
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }
}
