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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Trivial but slightly useful helper for creating deep dumps of Objects and their
 * its properties, which can be useful for displaying status messages and debugging.
 *
 * @author David McKain
 */
public final class ObjectDumper {

    public static final int DEFAULT_INDENT_WIDTH = 4;

    public static final int DEFAULT_MAX_DEPTH = 10;

    private static final String EMPTY = "(empty)";

    /** {@link StringBuilder} that the dump will append to */
    private final StringBuilder result;

    /** Number of spaces to indent at each level of Object depth */
    private int indentWidth;

    /** Maximum child Object depth to traverse to before issuing circularity error */
    private int maxDepth;

    public ObjectDumper(final StringBuilder result) {
        this.result = result;
        this.indentWidth = DEFAULT_INDENT_WIDTH;
        this.maxDepth = DEFAULT_MAX_DEPTH;
    }

    //-------------------------------------------------------

    public static String dumpObject(final Object object, final DumpMode dumpMode) {
        final StringBuilder builder = new StringBuilder();
        new ObjectDumper(builder).appendObject(object, dumpMode);
        return builder.toString();
    }

    public static String dumpObject(final Object object) {
        return dumpObject(object, DumpMode.DEEP);
    }

    public static void dumpObjectToStdout(final Object object) {
        System.out.println(dumpObject(object));
    }


    public static void dumpObjectToStderr(final Object object) {
        System.err.println(dumpObject(object));
    }

    //-------------------------------------------------------

    public int getIndentWidth() {
        return this.indentWidth;
    }

    public void setIndentWidth(final int indentWidth) {
        this.indentWidth = indentWidth;
    }


    public int getMaxDepth() {
        return this.maxDepth;
    }

    public void setMaxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
    }

    //-------------------------------------------------------

    /**
     * Does a dump of the given Object, using whatever {@link DumpMode} is providied for its
     * class. If the Object is null, then {@link DumpMode#TO_STRING} is used.
     */
    public void appendObject(final Object object) {
        DumpMode dumpMode = DumpMode.TO_STRING;
        if (object != null) {
            dumpMode = getElementDumpMode(object, dumpMode);
        }
        appendObject(object, dumpMode);
    }

    /**
     * Does a dump of the given Object using the given {@link DumpMode}.
     *
     * @param object
     * @param dumpMode if {@link DumpMode#DEEP} then the Object is dumped deeply, if {@link DumpMode#TO_STRING} then the Object's {@link #toString()} is called,
     *            if {@link DumpMode#IGNORE} then nothing happens.
     */
    public void appendObject(final Object object, final DumpMode dumpMode) {
        Assert.notNull(dumpMode, "dumpMode");
        appendObject(object, dumpMode, 0);
        result.append("\n");
    }

    //-------------------------------------------------------

    private void appendObject(final Object object, final DumpMode dumpMode, final int depth) {
        /* First check depth */
        if (object != null && depth > maxDepth) {
            result.append("[Maximum graph depth exceeded by child Object of type " + object.getClass() + "]");
            return;
        }
        /* Now decide what to dump */
        if (dumpMode == DumpMode.IGNORE) {
            /* Do nothing */
            return;
        }
        else if (object == null) {
            result.append("null");
        }
        else {
            if (object instanceof Object[]) {
                appendArray((Object[]) object, dumpMode, depth);
            }
            else if (object instanceof List<?>) {
                appendList((List<?>) object, dumpMode, depth);
            }
            else if (object instanceof Set<?>) {
                appendSet((Set<?>) object, dumpMode, depth);
            }
            else if (object instanceof Map<?, ?>) {
                appendMap((Map<?, ?>) object, dumpMode, depth);
            }
            else if (object instanceof Collection<?>) {
                appendCollection((Collection<?>) object, dumpMode, depth);
            }
            else {
                /* Now decide what to do */
                switch (dumpMode) {
                    case DEEP:
                        appendObjectDeep(object, depth);
                        break;

                    case TO_STRING:
                        result.append(object.toString());
                        break;

                    case IGNORE:
                        break;

                    default:
                        throw new IllegalStateException("Unexpected DumpMode " + dumpMode);
                }
            }
        }
    }

    private void appendArray(final Object[] array, final DumpMode dumpMode, final int depth) {
        final Class<?> componentType = array.getClass().getComponentType();
        result.append(componentType.getName())
                .append("[]@")
                .append(Integer.toHexString(System.identityHashCode(array)))
                .append("[");
        if (array.length == 0) {
            result.append(EMPTY);
        }
        else {
            result.append("\n");
            for (int i = 0; i < array.length; i++) {
                makeIndent(depth + 1);
                result.append(i).append(" => ");
                appendObject(array[i], getElementDumpMode(array[i], dumpMode), depth + 1);
                result.append("\n");
            }
            makeIndent(depth);
        }
        result.append("]");
    }

    private <E> void appendSet(final Set<E> set, final DumpMode dumpMode, final int depth) {
        final Class<?> collectionClass = set.getClass();
        result.append(collectionClass.getName()).append("{");
        if (set.isEmpty()) {
            result.append(EMPTY);
        }
        else {
            result.append("\n");
            for (final E element : set) {
                makeIndent(depth + 1);
                appendObject(element, getElementDumpMode(element, dumpMode), depth + 1);
                result.append("\n");
            }
            makeIndent(depth);
        }
        result.append("}");
    }

    private <E> void appendList(final List<E> list, final DumpMode dumpMode, final int depth) {
        final Class<?> collectionClass = list.getClass();
        result.append(collectionClass.getName()).append("[");
        if (list.isEmpty()) {
            result.append(EMPTY);
        }
        else {
            result.append("\n");
            E element;
            for (int i = 0; i < list.size(); i++) {
                element = list.get(i);
                makeIndent(depth + 1);
                result.append(i).append(" => ");
                appendObject(element, getElementDumpMode(element, dumpMode), depth + 1);
                result.append("\n");
            }
            makeIndent(depth);
        }
        result.append("]");
    }

    private <E> void appendCollection(final Collection<E> collection, final DumpMode dumpMode, final int depth) {
        final Class<?> collectionClass = collection.getClass();
        result.append(collectionClass.getName()).append("[");
        if (collection.isEmpty()) {
            result.append(EMPTY);
        }
        else {
            result.append("\n");
            for (final E element : collection) {
                makeIndent(depth + 1);
                appendObject(element, getElementDumpMode(element, dumpMode), depth + 1);
                result.append("\n");
            }
            makeIndent(depth);
        }

        result.append("]");
    }

    private <K, V> void appendMap(final Map<K, V> map, final DumpMode dumpMode, final int depth) {
        final Class<?> collectionClass = map.getClass();
        result.append(collectionClass.getName()).append("(");
        if (map.isEmpty()) {
            result.append(EMPTY);
        }
        else {
            result.append("\n");
            K key;
            V value;
            for (final Entry<K, V> entry : map.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();
                makeIndent(depth + 1);
                result.append(key).append(" => ");
                appendObject(value, getElementDumpMode(value, dumpMode), depth + 1);
                result.append("\n");
            }
            makeIndent(depth);
        }
        result.append(")");
    }

    private void appendObjectDeep(final Object object, final int depth) {
        final Class<?> objectClass = object.getClass();
        result.append(objectClass.getName())
                .append("@")
                .append(Integer.toHexString(System.identityHashCode(object)))
                .append("(");

        /* Traverse all properties */
        boolean hasOutputProperty = false;
        final Method[] methods = objectClass.getMethods();
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
            /* See if a DumpMode has been explicitly provided for this property. */
            final ObjectDumperOptions annotation = method.getAnnotation(ObjectDumperOptions.class);
            DumpMode targetDumpMode = annotation != null ? annotation.value() : null;
            if (targetDumpMode == DumpMode.IGNORE) {
                /* We can ignore this property so let's bail out now */
                continue;
            }

            /* If still here then it's a getP() or isP() method that we've chosen to dump */
            if (!hasOutputProperty) {
                /* Need to do an initial newline before first property */
                result.append("\n");
                hasOutputProperty = true;
            }
            makeIndent(depth + 1);

            /* Make first character after 'get' or 'is' lower case so that it really
             * is the name of the property */
            result.append(Character.toLowerCase(rawPropertyName.charAt(0)));
            if (methodName.length() > 1) {
                result.append(rawPropertyName.substring(1));
            }
            result.append(" => ");

            /* Now get the value of the property */
            Object value = null;
            try {
                value = method.invoke(object);
            }
            catch (final Exception e) {
                result.append("[Caused Exception ").append(e).append("]");
                continue;
            }

            /* If no DumpMode was specified for this property, then take the DumpMode of the
             * target's class. If nothing is specified then we'll use TO_STRING.
             */
            if (targetDumpMode == null) {
                targetDumpMode = getElementDumpMode(value, DumpMode.TO_STRING);
            }

            /* Dump property value in appropriate way */
            appendObject(value, targetDumpMode, depth + 1);
            result.append("\n");
        }
        if (hasOutputProperty) {
            makeIndent(depth);
        }
        result.append(")");
    }

    /**
     * Works out the most appropriate {@link DumpMode} for the given Object by inspecting
     * its class for the relevant annotation.
     *
     * @param object Object being dumped
     * @param currentDumpMode DumpMode currently in operation, used if nothing is specified
     *            for the Object's class or if the Object is null.
     */
    private DumpMode getElementDumpMode(final Object object, final DumpMode currentDumpMode) {
        DumpMode resultingDumpMode = currentDumpMode;
        if (object != null) {
            final ObjectDumperOptions annotation = object.getClass().getAnnotation(ObjectDumperOptions.class);
            if (annotation != null) {
                resultingDumpMode = annotation.value();
            }
        }
        return resultingDumpMode;
    }

    private void makeIndent(final int depth) {
        for (int i = 0; i < depth * DEFAULT_INDENT_WIDTH; i++) {
            result.append(' ');
        }
    }
}
