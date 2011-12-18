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

package uk.ac.ed.ph.jqtiplus.attribute;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
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
public abstract class MultipleAttribute<E> extends AbstractAttribute {
    
    private static final long serialVersionUID = -2295280039498864733L;

    /** Values separator. */
    public String FIELDS_SEPARATOR = " ";

    /** Value of this attribute. */
    private List<E> value;

    /** Default value of this attribute. */
    private List<E> defaultValue;

    /**
     * Constructs attribute.
     *
     * @param parent attribute's parent
     * @param name attribute's name
     */
    public MultipleAttribute(XmlNode parent, String name)
    {
        this(parent, name, null, null, true);
    }

    /**
     * Constructs attribute.
     *
     * @param parent attribute's parent
     * @param name attribute's name
     * @param defaultValue attribute's default value
     */
    public MultipleAttribute(XmlNode parent, String name, List<E> defaultValue)
    {
        this(parent, name, defaultValue, defaultValue, false);
    }

    /**
     * Constructs attribute.
     *
     * @param parent attribute's parent
     * @param name attribute's name
     * @param value attribute's value
     * @param defaultValue attribute's default value
     * @param required is this attribute required
     */
    public MultipleAttribute(XmlNode parent, String name, List<E> value, List<E> defaultValue, boolean required)
    {
        super(parent, name, required, true);

        if (value != null) 
            this.value = value;
        else if (defaultValue != null)
            this.value = new ArrayList<E>(defaultValue);
        else
            this.value = new ArrayList<E>();

        if (defaultValue != null)
            this.defaultValue = defaultValue;
        else
            this.defaultValue = new ArrayList<E>();
    }

    /**
     * Gets value of attribute.
     *
     * @return value of attribute
     */
    public List<E> getValues()
    {
        return value;
    }

    /**
     * Gets default value of attribute.
     *
     * @return default value of attribute
     */
    public List<E> getDefaultValues()
    {
        return defaultValue;
    }

    public void load(Element owner, Node node, LoadingContext context) {
        load(owner, node.getNodeValue(), context);
    }

    public void load(Element owner, String value, LoadingContext context) {
        if (value != null && value.length() != 0) {
            try {
                this.value.clear();
                List<String> values = splitValue(value);
                for (String string : values) {
                    this.value.add(parseValue(string));
                }
            }
            catch (QTIParseException ex) {
                this.value.clear();
                context.parseError(ex, owner);
            }
        }
        else
            this.value = new ArrayList<E>(defaultValue);
    }

    /**
     * Splits multiple string value into single string values.
     * For example attr="1 2 3". Multiple value is "1 2 3" and result is list with single values "1", "2" and "3".
     *
     * @param value multiple string value
     * @return split single string values
     */
    private List<String> splitValue(String value)
    {
        List<String> result = new ArrayList<String>();
        String[] values = value.split(FIELDS_SEPARATOR);

        for (int i = 0; i < values.length; i++)
            if (values[i].length() != 0)
                result.add(values[i]);

        return result;
    }

    /**
     * Parses value from given string.
     *
     * @param value string value
     * @return parsed value
     */
    protected abstract E parseValue(String value) 
           ;

    public String valueToString()
    {
        return valueToXmlString(value);
    }

    public String defaultValueToString()
    {
        return valueToXmlString(defaultValue);
    }

    /**
     * Gets multiple string value from given single values.
     *
     * @param value single values
     * @return multiple string value
     */
    private String valueToXmlString(List<E> value)
    {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < value.size(); i++)
        {
            builder.append(value.get(i));

            if (i < value.size() - 1)
                builder.append(FIELDS_SEPARATOR);
        }

        return builder.toString();
    }
}
