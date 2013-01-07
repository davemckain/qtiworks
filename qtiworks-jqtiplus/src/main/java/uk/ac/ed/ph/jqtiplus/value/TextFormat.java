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

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * plain -
 * Indicates that the text to be entered by the candidate is plain text.
 * This format is suitable for short unstructured responses. Delivery
 * engines should preserve white-space characters in candidate input except
 * where a response consists only of white-space characters, in which case
 * it should be treated as an empty string (NULL).
 * <p>
 * preFormatted - Indicates that the text to be entered by the candidate is pre-formatted and should be rendered in a way consistent with the definition of pre
 * in [XHTML]. Delivery engines must preserve white-space characters except where a response consists only of white-space characters, in which case it should be
 * treated as an empty string (NULL).
 * <p>
 * xhtml - Indicates that the text to be entered by the candidate is structured text. The value of the response variable is text marked up in XHTML. The
 * delivery engine should present an interface suitable for capturing structured text, this might be plain typed text interpreted with a set of simple text
 * markup conventions such as those used in wiki page editors or a complete WYSIWYG editor.
 * 
 * @author Jiri Kajaba
 */
public enum TextFormat implements Stringifiable {
    /**
     * plain type.
     */
    PLAIN("plain"),

    /**
     * preFormatted type.
     */
    PRE_FORMATTED("preFormatted"),

    /**
     * xhtml type.
     */
    XHTML("xhtml");

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "textFormat";

    private static Map<String, TextFormat> formats;

    static {
        formats = new HashMap<String, TextFormat>();

        for (final TextFormat type : TextFormat.values()) {
            formats.put(type.textFormat, type);
        }
    }

    private String textFormat;

    private TextFormat(String textFormat) {
        this.textFormat = textFormat;
    }

    /**
     * Returns true if this textFormat is plain; false otherwise.
     * 
     * @return true if this textFormat is plain; false otherwise
     */
    public boolean isPlain() {
        return this == PLAIN;
    }

    /**
     * Returns true if this textFormat is preFormatted; false otherwise.
     * 
     * @return true if this textFormat is preFormatted; false otherwise
     */
    public boolean isPreFormatted() {
        return this == PRE_FORMATTED;
    }

    /**
     * Returns true if this textFormat is xhtml; false otherwise.
     * 
     * @return true if this textFormat is xhtml; false otherwise
     */
    public boolean isXhtml() {
        return this == XHTML;
    }

    @Override
    public String toQtiString() {
        return textFormat;
    }

    /**
     * Returns parsed <code>TextFormat</code> from given <code>String</code>.
     * 
     * @param textFormat <code>String</code> representation of <code>TextFormat</code>
     * @return parsed <code>TextFormat</code> from given <code>String</code>
     * @throws QtiParseException if given <code>String</code> is not valid <code>TextFormat</code>
     */
    public static TextFormat parseTextFormat(String textFormat) {
        final TextFormat result = formats.get(textFormat);

        if (result == null) {
            throw new QtiParseException("Invalid " + QTI_CLASS_NAME + " '" + textFormat + "'.");
        }

        return result;
    }

    /**
     * Returns intersection of two given types sets (order is not important).
     * 
     * @param firstSet first set of types
     * @param secondSet second set of types
     * @return intersection of two given types sets
     */
    public static TextFormat[] intersection(TextFormat[] firstSet, TextFormat[] secondSet) {
        final List<TextFormat> paramTypes = new ArrayList<TextFormat>();

        for (final TextFormat type : firstSet) {
            if (Arrays.binarySearch(secondSet, type) >= 0) {
                paramTypes.add(type);
            }
        }

        return paramTypes.toArray(new TextFormat[] {});
    }
}
