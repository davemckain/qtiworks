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
package uk.ac.ed.ph.jqtiplus.attribute.value;

import uk.ac.ed.ph.jqtiplus.attribute.SingleAttribute;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;

/**
 * Attribute with integer value.
 * 
 * @author Jiri Kajaba
 */
public class IntegerAttribute extends SingleAttribute {

    private static final long serialVersionUID = 6169314176032331265L;

    /**
     * Constructs attribute.
     * 
     * @param parent attribute's parent
     * @param name attribute's name
     */
    public IntegerAttribute(XmlNode parent, String name) {
        super(parent, name);
    }

    /**
     * Constructs attribute.
     * 
     * @param parent attribute's parent
     * @param name attribute's name
     * @param defaultValue attribute's default value
     */
    public IntegerAttribute(XmlNode parent, String name, Integer defaultValue) {
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
    public IntegerAttribute(XmlNode parent, String name, Integer value, Integer defaultValue, boolean required) {
        super(parent, name, value, defaultValue, required);
    }

    @Override
    public Integer getValue() {
        return (Integer) super.getValue();
    }

    /**
     * Sets new value of attribute.
     * 
     * @param value new value of attribute
     * @see #getValue
     */
    public void setValue(Integer value) {
        super.setValue(value);
    }

    @Override
    public Integer getDefaultValue() {
        return (Integer) super.getDefaultValue();
    }

    /**
     * Sets new default value of attribute.
     * 
     * @param defaultValue new default value of attribute
     * @see #getDefaultValue
     */
    public void setDefaultValue(Integer defaultValue) {
        super.setDefaultValue(defaultValue);
    }

    @Override
    protected Integer parseValue(String value) {
        return IntegerValue.parseInteger(value);
    }
}
