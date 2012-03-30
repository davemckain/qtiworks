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
import java.util.Collections;
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

    /** Values separator. */
    public String FIELDS_SEPARATOR = " ";

    public MultipleAttribute(XmlNode parent, String localName) {
        this(parent, localName, null, null, true);
    }

    public MultipleAttribute(XmlNode parent, String localName, List<E> defaultValue) {
        this(parent, localName, null, defaultValue, false);
    }

    public MultipleAttribute(XmlNode parent, String localName, List<E> value, List<E> defaultValue, boolean required) {
        super(parent, localName, defaultValue, value, required);
    }

    /**
     * Returns a List representing the value of this attribute, returning an empty List
     * for a null value.
     * <p>
     * Important Note: you cannot subsequently differentiate between a null and an empty value from
     * the result of this method!
     * 
     * @return value of attribute
     */
    @SuppressWarnings("unchecked")
    public List<E> getValueAsList() {
        return value!=null ? value : (List<E>) Collections.emptyList();
    }

    /**
     * Returns a List representing the default value of this attribute, returning an empty List
     * for a null default value.
     * <p>
     * Important Note: you cannot subsequently differentiate between a null and an empty value from
     * the result of this method!
     * 
     * @return value of attribute
     */
    @SuppressWarnings("unchecked")
    public List<E> getDefaultValueAsList() {
        return defaultValue!=null ? defaultValue : (List<E>) Collections.emptyList();
    }

    @Override
    public final void load(Element owner, Node node, LoadingContext context) {
        load(owner, node.getNodeValue(), context);
    }

    @Override
    public final void load(Element owner, String stringValue, LoadingContext context) {
        if (stringValue != null) {
            try {
                value = parseStringValue(stringValue);
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
        final String[] values = stringValue.split(FIELDS_SEPARATOR);

        for (int i = 0; i < values.length; i++) {
            if (values[i].length() != 0) {
                result.add(values[i]);
            }
        }

        return result;
    }
    
    private List<E> parseStringValue(String stringValue) {
        final List<String> values = splitStringValue(stringValue);
        final List<E> result = new ArrayList<E>(values.size());
        for (final String string : values) {
            result.add(parseSingleValue(string));
        }
        return result;
    }

    /**
     * Parses value from given string.
     * 
     * @param value string value
     * @return parsed value
     */
    protected abstract E parseSingleValue(String value);

    @Override
    public final String valueToString() {
        return itemsToString(value);
    }

    @Override
    public final String defaultValueToString() {
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
                builder.append(itemToString(value.get(i)));
                if (i < value.size() - 1) {
                    builder.append(FIELDS_SEPARATOR);
                }
            }
        }
        return builder.toString();
    }
    
    protected abstract String itemToString(E item);
}
