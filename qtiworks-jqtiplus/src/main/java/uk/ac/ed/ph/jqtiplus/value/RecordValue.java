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

import uk.ac.ed.ph.jqtiplus.exception.QtiEvaluationException;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

/**
 * Implementation of "record-type" container.
 * <p>
 * This container can contain 0..N non NULL single values of any <code>BaseType</code>.
 * <p>
 * This container can be record or NULL (if empty).
 * 
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class RecordValue extends AbstractValue implements MultiValue {

    private static final long serialVersionUID = -8055629924489632630L;

    private TreeMap<Identifier, SingleValue> container;

    /**
     * Constructs empty (NULL) <code>RecordValue</code> container.
     */
    public RecordValue() {
        container = new TreeMap<Identifier, SingleValue>();
    }

    /**
     * Constructs empty (NULL) <code>RecordValue</code> container and adds given <code>SingleValue</code> into it.
     * 
     * @param identifier identifier of added <code>SingleValue</code>
     * @param value added <code>SingleValue</code>
     */
    public RecordValue(Identifier identifier, SingleValue value) {
        container = new TreeMap<Identifier, SingleValue>();
        add(identifier, value);
    }

    public RecordValue(String identifier, SingleValue value) {
        this(new Identifier(identifier), value);
    }

    /**
     * Constructs empty (NULL) <code>RecordValue</code> container and adds given <code>RecordValue</code> into it.
     * 
     * @param value added <code>RecordValue</code>
     */
    public RecordValue(RecordValue value) {
        container = new TreeMap<Identifier, SingleValue>();
        add(value);
    }

    @Override
    public boolean isNull() {
        return container.size() == 0;
    }

    @Override
    public Cardinality getCardinality() {
        if (isNull()) {
            return null;
        }

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
     * Returns <code>SingleValue</code> for given identifier or null.
     * <p>
     * Returns null if there is no such identifier.
     * 
     * @param identifier given identifier
     * @return <code>SingleValue</code> for given identifier or null
     */
    public SingleValue get(Identifier identifier) {
        return container.get(identifier);
    }

    /**
     * Returns true if this container contains any <code>SingleValue</code> with given <code>BaseType</code> or false otherwise.
     * 
     * @param baseType given <code>BaseType</code>
     * @return true if this container contains any <code>SingleValue</code> with given <code>BaseType</code> or false otherwise
     */
    public boolean containsBaseType(BaseType baseType) {
        return containsBaseType(new BaseType[] { baseType });
    }

    /**
     * Returns true if this container contains any <code>SingleValue</code> with any given <code>BaseType</code>s or false otherwise.
     * 
     * @param baseTypes given <code>BaseType</code>s
     * @return true if this container contains any <code>SingleValue</code> with any given <code>BaseType</code>s or false otherwise
     */
    public boolean containsBaseType(BaseType[] baseTypes) {
        for (final SingleValue value : container.values()) {
            if (Arrays.asList(baseTypes).contains(value.getBaseType())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds <code>SingleValue</code> into this container.
     * <p>
     * NULL <code>SingleValue</code> is ignored.
     * 
     * @param identifier identifier of added <code>SingleValue</code>
     * @param value added <code>SingleValue</code>
     * @return true if value was added; false otherwise
     */
    public boolean add(Identifier identifier, SingleValue value) {
        if (value == null || value.isNull()) {
            return false;
        }

        container.put(identifier, value);

        return true;
    }

    public boolean add(String identifier, SingleValue value) {
        return add(new Identifier(identifier), value);
    }

    /**
     * Adds <code>RecordValue</code> into this container.
     * <p>
     * Takes all values from <code>RecordValue</code> container and adds them into this container.
     * <p>
     * NULL <code>RecordValue</code> container is ignored.
     * 
     * @param value added <code>RecordValue</code>
     * @return true if value was added; false otherwise
     */
    public boolean add(RecordValue value) {
        if (value.isNull()) {
            return false;
        }

        container.putAll(value.container);

        return true;
    }

    /**
     * Returns A set view of the keys contained in this container.
     * 
     * @return A set view of the keys contained in this container
     */
    public Set<Identifier> keySet() {
        return container.keySet();
    }

    /**
     * Returns A collection view of the values contained in this container.
     * 
     * @return A collection view of the values contained in this container
     */
    public Collection<SingleValue> values() {
        return container.values();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object clone() throws QtiEvaluationException {
        try {
            final RecordValue value = (RecordValue) super.clone();

            if (container != null) {
                value.container = (TreeMap<Identifier, SingleValue>) container.clone();
            }

            return value;
        }
        catch (final CloneNotSupportedException ex) {
            throw new QtiEvaluationException("Cannot clone container.", ex);
        }
    }

    @Override
    public boolean equals(Object object) {
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

    @Override
    public String stringValue() {
        return container.toString();
    }
}
