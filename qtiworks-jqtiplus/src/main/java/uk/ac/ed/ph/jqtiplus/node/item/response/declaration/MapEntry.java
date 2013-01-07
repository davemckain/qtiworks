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
package uk.ac.ed.ph.jqtiplus.node.item.response.declaration;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.SingleValueAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;

/**
 * @see Mapping
 * @author Jonathon Hare
 */
public final class MapEntry extends AbstractNode {

    private static final long serialVersionUID = -5119382551204617489L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "mapEntry";

    /** Name of mapKey attribute in xml schema. */
    public static final String ATTR_MAP_KEY_NAME = "mapKey";

    /** Name of mappedValue attribute in xml schema. */
    public static final String ATTR_MAPPED_VALUE_NAME = "mappedValue";

    /** Name of caseSensitive attribute in xml schema (late addition to QTI 2.1) */
    public static final String ATTR_CASE_SENSITIVE_VALUE_NAME = "caseSensitive";

    /** Default value of caseSensitive attribute */
    public static final boolean ATTR_CASE_SENSITIVE_DEFAULT_VALUE = true;

    public MapEntry(final Mapping parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new SingleValueAttribute(this, ATTR_MAP_KEY_NAME, parent.getParent().getBaseType(), true));
        getAttributes().add(new FloatAttribute(this, ATTR_MAPPED_VALUE_NAME, true));
        getAttributes().add(new BooleanAttribute(this, ATTR_CASE_SENSITIVE_VALUE_NAME, ATTR_CASE_SENSITIVE_DEFAULT_VALUE, false));
    }


    public SingleValue getMapKey() {
        return getAttributes().getSingleValueAttribute(ATTR_MAP_KEY_NAME).getComputedValue();
    }

    public void setMapKey(final SingleValue mapKey) {
        getAttributes().getSingleValueAttribute(ATTR_MAP_KEY_NAME).setValue(mapKey);
    }


    public double getMappedValue() {
        return getAttributes().getFloatAttribute(ATTR_MAPPED_VALUE_NAME).getComputedNonNullValue();
    }

    public void setMappedValue(final Double mappedValue) {
        getAttributes().getFloatAttribute(ATTR_MAPPED_VALUE_NAME).setValue(mappedValue);
    }


    public boolean getCaseSensitive() {
        return getAttributes().getBooleanAttribute(ATTR_CASE_SENSITIVE_VALUE_NAME).getComputedNonNullValue();
    }

    public void setCaseSensitive(final Boolean caseSensitive) {
        getAttributes().getBooleanAttribute(ATTR_CASE_SENSITIVE_VALUE_NAME).setValue(caseSensitive);
    }
}
