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

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

    public MultipleAttribute(XmlNode parent, String localName, String fieldSeparator, boolean required) {
        this(parent, localName, fieldSeparator, null, required);
    }

    public MultipleAttribute(XmlNode parent, String localName, String fieldSeparator, List<E> defaultValue, boolean required) {
        super(parent, localName, defaultValue, required);
        this.fieldSeparator = fieldSeparator;
    }
    
    @Override
    public final void load(Element owner, Node node, LoadingContext context) {
        load(owner, node.getNodeValue(), context);
    }

    @Override
    public final void load(Element owner, String stringValue, LoadingContext context) {
        if (stringValue != null) {
            try {
                value = parseQtiString(stringValue);
            }
            catch (final QtiParseException ex) {
                value = null;
                context.modelBuildingError(ex, owner);
            }
        }
        else {
            value = null;
        }
    }

    @Override
    protected final List<E> parseQtiString(String stringValue) {
        final List<String> values = splitStringValue(stringValue);
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
    private List<String> splitStringValue(String stringValue) {
        final List<String> result = new ArrayList<String>();
        final String[] values = stringValue.split(fieldSeparator);

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
    protected final String toQtiString(List<E> value) {
        return itemsToString(value);
    }

    @Override
    public final String valueToQtiString() {
        return itemsToString(value);
    }

    @Override
    public final String defaultValueToQtiString() {
        return itemsToString(defaultValue);
    }

    /**
     * Gets multiple string value from given single values.
     * 
     * @param value single values
     * @return multiple string value
     */
    private String itemsToString(List<E> value) {
        final StringBuilder builder = new StringBuilder();
        if (value!=null) {
            for (int i = 0; i < value.size(); i++) {
                builder.append(itemToQtiString(value.get(i)));
                if (i < value.size() - 1) {
                    builder.append(fieldSeparator);
                }
            }
        }
        return builder.toString();
    }
    
    protected abstract String itemToQtiString(E item);
}
