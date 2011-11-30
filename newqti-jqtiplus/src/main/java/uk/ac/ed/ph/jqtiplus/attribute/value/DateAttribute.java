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

package uk.ac.ed.ph.jqtiplus.attribute.value;

import uk.ac.ed.ph.jqtiplus.attribute.SingleAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Attribute with Date value.
 * 
 * @author Jiri Kajaba
 */
public class DateAttribute extends SingleAttribute
{
    private static final long serialVersionUID = 1L;
    
    /** Date formatting pattern. */
    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Constructs attribute.
     *
     * @param parent attribute's parent
     * @param name attribute's name
     */
    public DateAttribute(XmlNode parent, String name)
    {
        super(parent, name);
    }

    /**
     * Constructs attribute.
     *
     * @param parent attribute's parent
     * @param name attribute's name
     * @param defaultValue attribute's default value
     */
    public DateAttribute(XmlNode parent, String name, Date defaultValue)
    {
        super(parent, name, defaultValue);
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
    public DateAttribute(XmlNode parent, String name, Date value, Date defaultValue, boolean required)
    {
        super(parent, name, value, defaultValue, required);
    }

    @Override
    public Date getValue()
    {
        return (Date) super.getValue();
    }

    /**
     * Sets new value of attribute.
     *
     * @param value new value of attribute
     * @see #getValue
     */
    public void setValue(Date value)
    {
        super.setValue(value);
    }

    @Override
    public Date getDefaultValue()
    {
        return (Date) super.getDefaultValue();
    }

    /**
     * Sets new default value of attribute.
     *
     * @param defaultValue new default value of attribute
     * @see #getDefaultValue
     */
    public void setDefaultValue(Date defaultValue)
    {
        super.setDefaultValue(defaultValue);
    }

    @Override
    protected Date parseValue(String value)
    {
        if (value == null || value.length() == 0)
            throw new QTIParseException("Invalid datetime '" + value + "'. Length is not valid.");

        try
        {
            return format.parse(value);
        }
        catch (ParseException ex)
        {
            throw new QTIParseException("Invalid datetime '" + value + "'.", ex);
        }
    }

    @Override
    public String valueToString()
    {
        return (getValue() != null) ? format.format(getValue()) : "";
    }

    @Override
    public String defaultValueToString()
    {
        return (getDefaultValue() != null) ? format.format(getDefaultValue()) : "";
    }
}
