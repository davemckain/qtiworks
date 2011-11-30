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
import uk.ac.ed.ph.jqtiplus.node.XmlNode;

import org.w3c.dom.Node;

/**
 * Implementation of attribute with single value (normal attribute).
 * 
 * @author Jiri Kajaba
 */
public abstract class SingleAttribute extends AbstractAttribute
{
    private static final long serialVersionUID = 1L;

    /** Value of this attribute. */
    private Object value;

    /** Default value of this attribute. */
    private Object defaultValue;

    /**
     * Constructs attribute.
     *
     * @param parent attribute's parent
     * @param name attribute's name
     */
    public SingleAttribute(XmlNode parent, String name)
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
    public SingleAttribute(XmlNode parent, String name, Object defaultValue)
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
    public SingleAttribute(XmlNode parent, String name, Object value, Object defaultValue, boolean required)
    {
        super(parent, name, required, true);

        this.value = value;
        this.defaultValue = defaultValue;
    }

    /**
     * Gets value of attribute.
     *
     * @return value of attribute
     * @see #setValue
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Sets new value of attribute.
     *
     * @param value new value of attribute
     * @see #getValue
     */
    protected void setValue(Object value)
    {
        this.value = value;
    }

    /**
     * Gets default value of attribute.
     *
     * @return default value of attribute
     * @see #setDefaultValue
     */
    public Object getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Sets new default value of attribute.
     *
     * @param defaultValue new default value of attribute
     * @see #getDefaultValue
     */
    protected void setDefaultValue(Object defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public void load(Node node)
    {
        load(node.getNodeValue());
    }

    public void load(String value)
    {
        setLoadedValue(value);
        setLoadingProblem(null);

        if (value != null && value.length() != 0)
        {
            try
            {
                this.value = parseValue(value);
            }
            catch (QTIParseException ex)
            {
                this.value = null;
                logger.error(ex.getMessage());
                setLoadingProblem(ex);
            }
        }
        else
            this.value = defaultValue;
    }

    /**
     * Parses value from given string.
     *
     * @param value string value
     * @return parsed value
     */
    protected abstract Object parseValue(String value);

    public String valueToString()
    {
        return (value != null) ? value.toString() : "";
    }

    public String defaultValueToString()
    {
        return (defaultValue != null) ? defaultValue.toString() : "";
    }
}
