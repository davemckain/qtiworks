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
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represents a non-NULL record value.
 *
 * @author Jiri Kajaba
 * @author David McKain (revised)
 */
public final class RecordValue extends ContainerValue {

    private static final long serialVersionUID = -8055629924489632630L;

    private final TreeMap<Identifier, SingleValue> container;

    public static NullValue emptyRecord() {
        return NullValue.INSTANCE;
    }

    public static RecordValue createRecordValue(final Identifier identifier, final SingleValue value) {
        Assert.notNull(identifier, "identifier");
        Assert.notNull(value, "value");
        return new RecordValue(identifier, value);
    }

    public static RecordValue createRecordValue(final String identifier, final SingleValue value) {
        Assert.notNull(identifier, "identifier");
        Assert.notNull(value, "value");
        return new RecordValue(Identifier.parseString(identifier), value);
    }

    public static Value createRecordValue(final Map<Identifier, SingleValue> values) {
        Assert.notNull(values);
        for (final Entry<Identifier, SingleValue> entry : values.entrySet()) {
            Assert.notNull(entry.getValue(), "Value for record entry " + entry.getKey());
        }
        return values.isEmpty() ? NullValue.INSTANCE : new RecordValue(values);
    }

    private RecordValue(final Identifier identifier, final SingleValue value) {
        container = new TreeMap<Identifier, SingleValue>();
        container.put(identifier, value);
    }

    private RecordValue(final Map<Identifier, SingleValue> values) {
        container = new TreeMap<Identifier, SingleValue>();
        container.putAll(values);
    }

    @Override
    public Cardinality getCardinality() {
        return Cardinality.RECORD;
    }

    @Override
    public BaseType getBaseType() {
        return null;
    }

    @Override
    public int size() {
        return container.size();
    }

    /**
     * Returns whether this record contains a value corresponding to the given {@link Identifier}.
     */
    public boolean contains(final Identifier identifier) {
        return container.containsKey(identifier);
    }

    /**
     * Returns the value corresponding to the given {@link Identifier}, or null if there is no
     * such value in this container.
     *
     * @see #contains(Identifier)
     */
    public SingleValue get(final Identifier identifier) {
        return container.get(identifier);
    }

    /**
     * Returns true if this container contains any <code>SingleValue</code> with given <code>BaseType</code> or false otherwise.
     */
    public boolean containsBaseType(final BaseType baseType) {
        return containsBaseType(new BaseType[] { baseType });
    }

    /**
     * Returns true if this container contains any <code>SingleValue</code> with any given <code>BaseType</code>s or false otherwise.
     *
     * @param baseTypes given <code>BaseType</code>s
     * @return true if this container contains any <code>SingleValue</code> with any given <code>BaseType</code>s or false otherwise
     */
    public boolean containsBaseType(final BaseType[] baseTypes) {
        for (final SingleValue value : container.values()) {
            if (Arrays.asList(baseTypes).contains(value.getBaseType())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns a view of the keys contained in this container.
     */
    public Set<Identifier> keySet() {
        return Collections.unmodifiableSet(container.keySet());
    }

    /**
     * Returns a view of the values contained in this container.
     */
    public Collection<SingleValue> values() {
        return Collections.unmodifiableCollection(container.values());
    }

    public Set<Entry<Identifier, SingleValue>> entrySet() {
        return Collections.unmodifiableSet(container.entrySet());
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof RecordValue)) {
            return false;
        }

        final RecordValue other = (RecordValue) object;
        return container.equals(other.container);
    }

    @Override
    public int hashCode() {
        return container.hashCode();
    }

    /**
     * NOTE: The QTI specification doesn't list a String format for record variables. Here
     * we will use the format
     * <pre>
     * {
     *   key1: value,
     *   key2: value2
     * }
     * </pre>
     */
    @Override
    public final String toQtiString() {
        final StringBuilder stringBuilder = new StringBuilder("{\n");
        final Iterator<Entry<Identifier, SingleValue>> iterator = container.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<Identifier, SingleValue> entry = iterator.next();
            final Identifier key = entry.getKey();
            final SingleValue value = entry.getValue();
            stringBuilder.append("  ")
                .append(key.toString())
                .append(": ")
                .append(value.toQtiString());
            if (iterator.hasNext()) {
                stringBuilder.append(',');
            }
            stringBuilder.append('\n');
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
