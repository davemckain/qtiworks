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
package uk.ac.ed.ph.jqtiplus.internal.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Some random "macros" for doing common Object-based tasks.
 *
 * @author David McKain
 */
public final class ObjectUtilities {

    public static <T> List<T> createView(final T[] array) {
        if (array==null) {
            return Collections.emptyList();
        }
        return unmodifiableList(Arrays.asList(array));
    }

    public static <T> Set<T> unmodifiableSet(final T... items) {
        final Set<T> result = new HashSet<T>();
        for (final T item : items) {
            result.add(item);
        }
        return Collections.unmodifiableSet(result);
    }

    public static <T> List<T> unmodifiableList(final List<T> input) {
        List<T> result;
        if (input!=null && !input.isEmpty()) {
            result = Collections.unmodifiableList(input);
        }
        else {
            result= Collections.emptyList();
        }
        return result;
    }

    public static <T> Set<T> unmodifiableSet(final Set<T> input) {
        Set<T> result;
        if (input!=null && !input.isEmpty()) {
            result = Collections.unmodifiableSet(input);
        }
        else {
            result = Collections.emptySet();
        }
        return result;
    }

    public static <K,V> Map<K,V> unmodifiableMap(final Map<K,V> input) {
        Map<K,V> result;
        if (input!=null && !input.isEmpty()) {
            result = Collections.unmodifiableMap(input);
        }
        else {
            result = Collections.emptyMap();
        }
        return result;
    }

    public static Date addToTime(final Date date, final long milliseconds) {
        return new Date(date.getTime() + milliseconds);
    }

    /**
     * Safely clones a {@link Date} Object, returning null for a null input.
     */
    public static Date safeClone(final Date date) {
        return date!=null ? (Date) date.clone() : null;
    }

    /**
     * Convenience toString() method that can be applied safely to a null
     * Object, yielding null.
     */
    public static String safeToString(final Object object) {
        return object != null ? object.toString() : null;
    }

    /**
     * Checks equality of two Objects, allowing the case where o1==o2==null
     * to return true. The equals() method on o2 should be compatible with
     * equality.
     *
     * @return true if either o1==o2 or (o1!=null and o1.equals(o2))
     */
    public static boolean nullSafeEquals(final Object o1, final Object o2) {
        return o1 == o2 || o1 != null && o1.equals(o2);
    }

    /**
     * Tests whether the given array is null or empty, which is sometimes useful.
     *
     * @return true array is either null or empty
     */
    public static boolean isNullOrEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Concatenates the given arrays into a single array, treating any null arrays as if they
     * were empty. (Note that if exactly one of the input arrays is null, then the result is
     * the other array.)
     *
     * @param <E> generic type of resulting array
     * @param array1 first array to concatenate, which may be null
     * @param array2 second array to concatenate, which may be null
     * @param itemClass generic type of resulting array
     * @return array formed by concatenated array1 and array2, reusing one of the inputs if the
     *         other was null.
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] concat(final E[] array1, final E[] array2, final Class<E> itemClass) {
        final boolean array1Empty = isNullOrEmpty(array1);
        final boolean array2Empty = isNullOrEmpty(array2);
        if (array1Empty && array2Empty) {
            return (E[]) Array.newInstance(itemClass, 0);
        }
        else if (array1Empty) {
            return array2;
        }
        else if (array2Empty) {
            return array1;
        }
        else {
            /* (This is not nice but gets over the problem of creating generic arrays) */
            final E[] result = (E[]) Array.newInstance(itemClass, array1.length + array2.length);
            System.arraycopy(array1, 0, result, 0, array1.length);
            System.arraycopy(array2, 0, result, array1.length, array2.length);
            return result;
        }
    }


    /**
     * Utility method to create a simple String representation of a JavaBean Object by
     * showing all properties <code>p</code> which can be called by <code>getP()</code> on the Object.
     *
     * @param bean
     * @return String representation of the form <code>className(p1=value,p2=value,...)</code>
     */
    public static String beanToString(final Object bean) {
        final Class<?> beanClass = bean.getClass();

        /* Output bean's hashCode and start of property list */
        final StringBuilder result = new StringBuilder(beanClass.getName())
                .append("@")
                .append(Integer.toHexString(System.identityHashCode(bean)))
                .append("(");

        /* Now show each property by calling relevant methods */
        final Method[] methods = beanClass.getMethods();
        boolean outputMade = false;
        String methodName, rawPropertyName;
        for (final Method method : methods) {
            /* See if we've got a getter method. If so, extract property name (with first char still
             * capitalised) */
            methodName = method.getName();
            if (methodName.startsWith("get") && method.getParameterTypes().length == 0
                    && !methodName.equals("getClass") && methodName.length() > "get".length()) {
                rawPropertyName = methodName.substring("get".length());
            }
            else if (methodName.startsWith("is") && method.getParameterTypes().length == 0
                    && methodName.length() > "is".length()) {
                rawPropertyName = methodName.substring("is".length());
            }
            else {
                continue;
            }
            /* See if there is an annotation controlling how the property should be displayed */
            final BeanToStringOptions beanAnnotation = method.getAnnotation(BeanToStringOptions.class);
            final PropertyOptions propertyOption = beanAnnotation != null ? beanAnnotation.value() : null;
            if (propertyOption != null && propertyOption == PropertyOptions.IGNORE_PROPERTY) {
                /* Ignore this property */
                continue;
            }
            /* It's a getP() or isP() method */
            if (outputMade) {
                result.append(",");
            }
            else {
                outputMade = true;
            }
            /* Make first character after 'get' or 'is' lower case so that it really
             * is the name of the property */
            result.append(Character.toLowerCase(rawPropertyName.charAt(0)));
            if (methodName.length() > 1) {
                result.append(rawPropertyName.substring(1));
            }
            /* Now do the equals sign */
            result.append("=");
            /* Now get the value of the property */
            Object value;
            try {
                value = method.invoke(bean);
            }
            catch (final Exception e) {
                result.append("[Caused Exception ").append(e).append("]");
                continue;
            }
            /* Now print something */
            if (propertyOption != null && propertyOption == PropertyOptions.HIDE_VALUE) {
                /* Value is to be hidden */
                result.append("[hidden]");
            }
            else {
                if (value instanceof Object[]) {
                    /* Returned an array - in this case Arrays.toString() does something nice */
                    result.append(Arrays.toString((Object[]) value));
                }
                else {
                    /* Not an array, so handle as normal */
                    result.append(value);
                }
            }
        }
        result.append(")");
        return result.toString();
    }
}
