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
 * * Neither the localName of the University of Edinburgh nor the localNames of its
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
package uk.ac.ed.ph.jqtiplus.attribute;

import uk.ac.ed.ph.jqtiplus.node.QtiNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of attribute with multiple value (e.g. attr="1 2 3").
 *
 * @author Jiri Kajaba
 */
public abstract class MultipleAttribute<E> extends AbstractAttribute<List<E>> {

    private static final long serialVersionUID = -2295280039498864733L;

    public static final String SPACE_FIELD_SEPARATOR = " ";
    public static final String COMMA_FIELDS_SEPARATOR = ",";

    private final String fieldSeparator;

    public MultipleAttribute(final QtiNode parent, final String localName, final String fieldSeparator, final boolean required) {
        this(parent, localName, fieldSeparator, null, required);
    }

    public MultipleAttribute(final QtiNode parent, final String localName, final String fieldSeparator, final List<E> defaultValue, final boolean required) {
        super(parent, localName, defaultValue, required);
        this.fieldSeparator = fieldSeparator;
    }

    @Override
    public final List<E> parseDomAttributeValue(final String domAttributeValue) {
        final List<String> values = splitStringValue(domAttributeValue);
        final List<E> result = new ArrayList<E>(values.size());
        for (final String string : values) {
            result.add(parseItemValue(string));
        }
        return result;
    }

    /**
     * Splits multiple string value into single string values.
     * For example attr="1 2 3". Multiple value is "1 2 3" and result is list
     * with single values "1", "2" and "3".
     *
     * @param stringValue multiple string value
     * @return split single string values
     */
    private List<String> splitStringValue(final String stringValue) {
        final List<String> result = new ArrayList<String>();
        final String[] values = stringValue.trim().split(fieldSeparator);

        for (int i = 0; i < values.length; i++) {
            if (values[i].length() != 0) {
                result.add(values[i]);
            }
        }

        return result;
    }

    /**
     * Parses value from given string.
     *
     * @param itemValue string value
     * @return parsed value
     */
    protected abstract E parseItemValue(String itemValue);

    @Override
    public final String toDomAttributeValue(final List<E> singleValues) {
        final StringBuilder builder = new StringBuilder();
        if (singleValues!=null) {
            for (int i = 0; i < singleValues.size(); i++) {
                builder.append(itemToQtiString(singleValues.get(i)));
                if (i < singleValues.size() - 1) {
                    builder.append(fieldSeparator);
                }
            }
        }
        return builder.toString();
    }

    protected abstract String itemToQtiString(E item);
}
