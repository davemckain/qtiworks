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

import java.util.Iterator;

/**
 * Some random {@link String} utilities and helpers.
 *
 * (Ported from SnuggleTeX for use in serialization. Some of this may have applications
 * elsewhere in JQTI. Another option is pulling in a 3rd party, but this stuff is
 * so trivial it's not worth it.)
 *
 * @author David McKain
 */
public final class StringUtilities {

    /** Shared instance of an empty array of Strings, which is sometimes useful! */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * Joins the given collection of Objects using the given
     * separator and each Object's normal toString() method.
     * <p>
     * For example, joining the collection "a", "b", "c" with "/"
     * gives "a/b/c".
     *
     * @param objects collection of Objects to join
     * @param separator separator to use
     * @return objects joined using the given separator.
     */
    public static String join(final Iterable<? extends Object> objects, final CharSequence separator) {
        final StringBuilder result = new StringBuilder();
        for (final Iterator<? extends Object> iter = objects.iterator(); iter.hasNext(); ) {
            result.append(iter.next().toString());
            if (iter.hasNext()) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    /**
     * Same as {@link #join(Iterable, CharSequence)} but simply takes an array
     * of Objects.
     *
     * @param objects array of Objects to join
     * @param separator separator to use
     */
    public static String join(final Object[] objects, final CharSequence separator) {
        return join(objects, separator, 0, objects.length);
    }

    /**
     * Version of {@link #join(Object[], CharSequence)} that allows you to pass in
     * a {@link StringBuilder} that the result will be built up in. This is useful if you need
     * to do add in other stuff later on.
     *
     * @param resultBuilder StringBuilder to append results to
     * @param objects array of Objects to join
     * @param separator separator to use
     */
    public static void join(final StringBuilder resultBuilder, final Object[] objects,
            final CharSequence separator) {
        join(resultBuilder, objects, separator, 0, objects.length);
    }

    /**
     * Version of {@link #join(Object[], CharSequence)} that allows you to specify a range of
     * indices in the array to join. This can be useful in some cases.
     *
     * @param objects array of Objects to join
     * @param separator separator to use
     * @param startIndex first index to join
     * @param endIndex index after last one to join
     */
    public static String join(final Object[] objects, final CharSequence separator,
            final int startIndex, final int endIndex) {
        final StringBuilder result = new StringBuilder();
        join(result, objects, separator, startIndex, endIndex);
        return result.toString();
    }

    /**
     * Version of {@link #join(Object[], CharSequence, int, int)} that allows you to pass in
     * a {@link StringBuilder} that the result will be built up in. This is useful if you need
     * to do add in other stuff later on.
     *
     * @param resultBuilder StringBuilder to append results to
     * @param objects array of Objects to join
     * @param separator separator to use
     * @param startIndex first index to join
     * @param endIndex index after last one to join
     */
    public static void join(final StringBuilder resultBuilder, final Object[] objects,
            final CharSequence separator, final int startIndex, final int endIndex) {
        boolean hasDoneFirst = false;
        for (int i=startIndex; i<endIndex; i++) {
            if (hasDoneFirst) {
                resultBuilder.append(separator);
            }
            resultBuilder.append(objects[i].toString());
            hasDoneFirst = true;
        }
    }

    //------------------------------------------------------------------------

    /**
     * Tests whether the given String is null or empty ("").
     */
    public static boolean isNullOrEmpty(final String string) {
        return string==null || string.length()==0;
    }

    /**
     * Tests whether the given String is null or blank (whitespace only).
     */
    public static boolean isNullOrBlank(final String string) {
        return string==null || string.trim().length()==0;
    }

    /**
     * Convenience method that turns a String to null if it is empty (i.e. "") or null.
     *
     * @param string
     * @return same string if it is non-null and non-empty, otherwise null.
     */
    public static String nullIfEmpty(final String string) {
        return isNullOrEmpty(string) ? null : string;
    }

    /**
     * Convenience method that replaces a String with null if it is blank or null.
     *
     * @param string
     * @return same string if it is non-null and non-empty, otherwise null.
     */
    public static String nullIfBlank(final String string) {
        return isNullOrBlank(string) ? null : string;
    }

    /**
     * Convenience method that replaces a String with an empty String ("") if it is null.
     *
     * @param string
     * @return same string if it is non-empty, otherwise null.
     */
    public static String emptyIfNull(final String string) {
        return string!=null ? string : "";
    }

    public static String safeToStringEmptyIfNull(final Object object) {
        return object!=null ? object.toString() : "";
    }

    /**
     * Convenience method that returns a default value if the provided String is null. This
     * doesn't do anything that can't be trivially expressed by the ?: operator, but can be
     * more readable in some cases.
     *
     * @param string
     * @return same string if it is non-empty, otherwise null.
     */
    public static String defaultIfNull(final String string, final String defaultValue) {
        return string!=null ? string : defaultValue;
    }

    //------------------------------------------------------------------------

    /**
     * Trivial helper method to convert a boolean into either
     * "yes" or "no" depending on its state.
     *
     * @param state boolean to convert
     * @return "yes" if true, "no" if false.
     */
    public static String toYesNo(final boolean state) {
        return state ? "yes" : "no";
    }

    /**
     * Trivial helper method to convert a boolean into either
     * "true" or "false" depending on its state.
     *
     * @param state boolean to convert
     * @return "true" if true, "false" if false.
     */
    public static String toTrueFalse(final boolean state) {
        return state ? "true" : "false";
    }

    /**
     * Converts the given String argument to a boolean using
     * the scheme "yes"=>true, "no"=>false. Any other value
     * results in an IllegalArgumentException.
     *
     * @param value
     * @return true if "yes", false if "no"
     *
     * @throws IllegalArgumentException if value if null or
     *   neither "yes" nor "no"
     */
    public static boolean fromYesNo(final String value) {
        return fromBinaryValues(value, "yes", "no");
    }

    /**
     * Converts the given String argument to a boolean using
     * the scheme "true"=>true, "false"=>false. Any other value
     * results in an IllegalArgumentException.
     *
     * @param value
     * @return true if "yes", false if "no"
     *
     * @throws IllegalArgumentException if value if null or
     *   neither "true" nor "false"
     */
    public static boolean fromTrueFalse(final String value) {
        return fromBinaryValues(value, "true", "false");
    }

    /**
     * Converts the given String argument to a boolean using
     * the scheme trueValue => true, falseValue => false.
     * Any other value results in an IllegalArgumentException.
     *
     * @param value
     * @param trueValue value returning true
     * @param falseValue value returning false
     *
     * @throws IllegalArgumentException if value if null or
     *   neither trueValue nor falseValue
     */
    public static boolean fromBinaryValues(final String value,
            final String trueValue, final String falseValue) {
        if (value!=null) {
            if (value.equals(trueValue)) {
                return true;
            }
            else if (value.equals(falseValue)) {
                return false;
            }
        }
        throw new IllegalArgumentException("Argument '" + value
                + "' must be "
                + trueValue + " or " + falseValue);
    }

    //------------------------------------------------------------------------

    /**
     * This is used to generate pseudo XPaths only.
     */
    public static String escapeForXmlString(final String text, final boolean asAttribute) {
        final StringBuilder builder = new StringBuilder();
        for (final char c : text.toCharArray()) {
            switch (c) {
                case '<':
                    builder.append("&lt;");
                    break;

                case '>':
                    builder.append("&gt;");
                    break;

                case '&':
                    builder.append("&amp;");
                    break;

                case '"':
                    if (asAttribute) {
                        /* (We're always be writing attributes within double-quotes so need to escape in this case) */
                        builder.append("&quot;");
                    }
                    else {
                        builder.append('"');
                    }
                    break;

                default:
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }
}
