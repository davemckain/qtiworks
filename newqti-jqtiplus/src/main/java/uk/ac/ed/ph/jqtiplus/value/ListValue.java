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

package uk.ac.ed.ph.jqtiplus.value;

import uk.ac.ed.ph.jqtiplus.exception.QTIBaseTypeException;
import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;

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
 * 
 * @author Jiri Kajaba
 */
public abstract class ListValue implements Cloneable, MultiValue, Iterable<SingleValue>
{
    private static final long serialVersionUID = 1L;
    
    /** Container for single values. */
    protected List<SingleValue> container;
    
    /**
     * Constructs empty (NULL) <code>ListValue</code> container.
     */
    public ListValue()
    {
        container = new ArrayList<SingleValue>();
    }

    /**
     * Constructs empty (NULL) <code>ListValue</code> container and adds given <code>SingleValue</code> into it.
     *
     * @param value added <code>SingleValue</code>
     */
    public ListValue(SingleValue value)
    {
        container = new ArrayList<SingleValue>();

        add(value);
    }

    /**
     * Constructs empty (NULL) <code>ListValue</code> container and adds all given <code>SingleValue</code>s into it.
     *
     * @param values added <code>SingleValue</code>s
     */
    public ListValue(SingleValue[] values)
    {
        container = new ArrayList<SingleValue>();

        for (SingleValue value : values)
            add(value);
    }

    public Iterator<SingleValue> iterator() {
        return container.iterator();
    }

    public boolean isNull()
    {
        return container.size() == 0;
    }

    public BaseType getBaseType()
    {
        if (isNull())
            return null;

        return container.get(0).getBaseType();
    }

    public int size()
    {
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
    public boolean contains(SingleValue value)
    {
        return container.contains(value);
    }

    /**
     * Returns number of occurrences of given <code>SingleValue</code>.
     *
     * @param value given <code>SingleValue</code>
     * @return number of occurrences of given <code>SingleValue</code>
     */
    public int count(SingleValue value)
    {
        int count = 0;

        for (SingleValue singleValue : container)
            if (singleValue.equals(value))
                count++;

        return count;
    }

    /**
     * Returns <code>SingleValue</code> on given index.
     *
     * @param index given index
     * @return <code>SingleValue</code> on given index
     */
    public SingleValue get(int index)
    {
        return container.get(index);
    }
    
    /**
     * Returns a list of <code>SingleValue</code>s.
     *
     * @return list of <code>SingleValue</code>s.
     */
    public List<SingleValue> getAll()
    {
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
     * @throws QTIBaseTypeException if <code>BaseType</code> is not same
     */
    public boolean add(SingleValue value) throws QTIBaseTypeException
    {
        if (value == null || value.isNull())
            return false;

        if (!isNull() && getBaseType() != value.getBaseType())
            throw new QTIBaseTypeException("Invalid baseType: " + value.getBaseType());

        return container.add(value);
    }

    /**
     * Removes all occurrences of given <code>SingleValue</code> from this container.
     *
     * @param value given <code>SingleValue</code>
     * @return true if value was removed (container contained this value); false otherwise
     */
    public boolean removeAll(SingleValue value)
    {
        boolean result = false;

        while (container.remove(value))
            result = true;

        return result;
    }

    @Override
    @SuppressWarnings ("unchecked")
    public Object clone() throws QTIEvaluationException
    {
        try
        {
            ListValue value = (ListValue) super.clone();

            if (container != null)
                value.container = (List<SingleValue>) ((ArrayList<SingleValue>) container).clone();

            return value;
        }
        catch (CloneNotSupportedException ex)
        {
            throw new QTIEvaluationException("Cannot clone container.", ex);
        }
    }

    @Override
    public int hashCode()
    {
        int hashCode = 0;

        for (Value value : container)
            hashCode += value.hashCode();

        return hashCode;
    }

    @Override
    public String toString()
    {
        String string = container.toString();

        if (isOrdered() && string.length() > 1)
            string = "<" + string.substring(1, string.length() - 1) + ">";

        return string;
    }
}
