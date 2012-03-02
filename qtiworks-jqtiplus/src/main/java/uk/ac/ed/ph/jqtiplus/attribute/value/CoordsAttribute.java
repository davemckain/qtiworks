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

import uk.ac.ed.ph.jqtiplus.attribute.MultipleAttribute;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;

import java.util.List;

/**
 * Attribute with integer values.
 * 
 * @author Jiri Kajaba
 */
public class CoordsAttribute extends MultipleAttribute<Integer> {

    private static final long serialVersionUID = -3499455085135083349L;

    /** Values separator. */
    public static String FIELDS_SEPARATOR = ",";

    /**
     * Constructs attribute.
     * 
     * @param parent attribute's parent
     * @param localName attribute's localName
     */
    public CoordsAttribute(XmlNode parent, String localName) {
        super(parent, localName);
        super.FIELDS_SEPARATOR = FIELDS_SEPARATOR;
    }

    /**
     * Constructs attribute.
     * 
     * @param parent attribute's parent
     * @param localName attribute's localName
     * @param defaultValue attribute's default value
     */
    public CoordsAttribute(XmlNode parent, String localName, List<Integer> defaultValue) {
        super(parent, localName, defaultValue);
        super.FIELDS_SEPARATOR = FIELDS_SEPARATOR;
    }

    /**
     * Constructs attribute.
     * 
     * @param parent attribute's parent
     * @param localName attribute's localName
     * @param value attribute's value
     * @param defaultValue attribute's default value
     * @param required is this attribute required
     */
    public CoordsAttribute(XmlNode parent, String localName, List<Integer> value, List<Integer> defaultValue, boolean required) {
        super(parent, localName, value, defaultValue, required);
        super.FIELDS_SEPARATOR = FIELDS_SEPARATOR;
    }

    @Override
    public List<Integer> getValueAsList() {
        return super.getValueAsList();
    }

    @Override
    public List<Integer> getDefaultValueAsList() {
        return super.getDefaultValueAsList();
    }

    @Override
    protected Integer parseValue(String value) {
        return IntegerValue.parseInteger(value);
    }
}
