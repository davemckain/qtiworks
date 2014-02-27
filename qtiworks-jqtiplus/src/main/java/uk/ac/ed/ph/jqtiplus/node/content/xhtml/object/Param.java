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
package uk.ac.ed.ph.jqtiplus.node.content.xhtml.object;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ParamTypeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.ObjectFlow;
import uk.ac.ed.ph.jqtiplus.value.ParamType;

/**
 * Attribute : name [1]: string
 * The name of the parameter, as interpreted by the object.
 * Attribute : value [1]: string
 * The value to pass to the object for the named parameter. This value
 * is subject to template variable expansion. If the value is the name
 * of a template variable that was declared with the paramVariable set
 * to true then the template variable's value is passed to the object
 * as the value for the given parameter.
 * When expanding a template variable as a parameter value, types other
 * than identifiers, strings and uris must be converted to strings.
 * Numeric types are converted to strings using the "%i" or "%G" formats
 * as appropriate (see printedVariable for a discussion of numeric formatting).
 * Values of base-type boolean are expanded to one of the strings "true" or
 * "false". Values of base-type point are expanded to two space-separated integers
 * in the order horizontal coordinate, vertical coordinate, using "%i" format.
 * Values of base-type pair and directedPair are converted to a string
 * consisting of the two identifiers, space separated. Values of base-type
 * duration are converted using "%G" format. Values of base-type file cannot
 * be used in parameter expansion.
 * If the valuetype is REF the template variable must be of base-type uri.
 * Attribute : valuetype [1]: paramType = DATA
 * This specification supports the use of DATA and REF but not OBJECT.
 * Attribute : type [0..1]: mimeType
 * Used to provide a type for values valuetype REF.
 *
 * @author Jonathon Hare
 */
public final class Param extends AbstractNode implements ObjectFlow {

    private static final long serialVersionUID = 2765069194418566232L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "param";

    /** Name of name attribute in xml schema. */
    public static final String ATTR_NAME_NAME = "name";

    /** Name of value attribute in xml schema. */
    public static final String ATTR_VALUE_NAME = "value";

    /** Name of valuetype attribute in xml schema. */
    public static final String ATTR_VALUETYPE_NAME = "valuetype";

    /** Name of type attribute in xml schema. */
    public static final String ATTR_TYPE_NAME = "type";

    public Param(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new StringAttribute(this, ATTR_NAME_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_VALUE_NAME, true));
        getAttributes().add(new ParamTypeAttribute(this, ATTR_VALUETYPE_NAME, true));
        getAttributes().add(new StringAttribute(this, ATTR_TYPE_NAME, false));
    }

    public String getName() {
        return getAttributes().getStringAttribute(ATTR_NAME_NAME).getComputedValue();
    }

    public void setName(final String name) {
        getAttributes().getStringAttribute(ATTR_NAME_NAME).setValue(name);
    }


    public String getValue() {
        return getAttributes().getStringAttribute(ATTR_VALUE_NAME).getComputedValue();
    }

    public void setValue(final String value) {
        getAttributes().getStringAttribute(ATTR_VALUE_NAME).setValue(value);
    }


    public ParamType getValuetype() {
        return getAttributes().getParamTypeAttribute(ATTR_VALUETYPE_NAME).getComputedValue();
    }

    public void setValuetype(final ParamType valuetype) {
        getAttributes().getParamTypeAttribute(ATTR_VALUETYPE_NAME).setValue(valuetype);
    }


    public String getType() {
        return getAttributes().getStringAttribute(ATTR_TYPE_NAME).getComputedValue();
    }

    public void setType(final String type) {
        getAttributes().getStringAttribute(ATTR_TYPE_NAME).setValue(type);
    }
}
