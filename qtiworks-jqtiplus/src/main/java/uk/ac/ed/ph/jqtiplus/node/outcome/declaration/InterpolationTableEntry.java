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
package uk.ac.ed.ph.jqtiplus.node.outcome.declaration;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;

/**
 * Entry for interpolationTable.
 * 
 * @author Jiri Kajaba
 */
public class InterpolationTableEntry extends LookupTableEntry {

    private static final long serialVersionUID = -7963297659090182595L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "interpolationTableEntry";

    /** Name of sourceValue attribute in xml schema. */
    public static final String ATTR_SOURCE_VALUE_NAME = "sourceValue";

    /** Name of includeBoundary attribute in xml schema. */
    public static final String ATTR_INCLUDE_BOUNDARY_NAME = "includeBoundary";

    /** Default value of includeBoundary attribute. */
    public static final Boolean ATTR_INCLUDE_BOUNDARY_DEFAULT_VALUE = Boolean.TRUE;

    public InterpolationTableEntry(InterpolationTable parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(0, new FloatAttribute(this, ATTR_SOURCE_VALUE_NAME, true));
        getAttributes().add(1, new BooleanAttribute(this, ATTR_INCLUDE_BOUNDARY_NAME, ATTR_INCLUDE_BOUNDARY_DEFAULT_VALUE, false));
    }

    /**
     * Gets value of sourceValue attribute.
     * 
     * @return value of sourceValue attribute
     * @see #setSourceValue
     */
    @Override
    public Double getSourceValue() {
        return getAttributes().getFloatAttribute(ATTR_SOURCE_VALUE_NAME).getComputedValue();
    }

    /**
     * Sets new value of sourceValue attribute.
     * 
     * @param sourceValue new value of sourceValue attribute
     * @see #getSourceValue
     */
    public void setSourceValue(Double sourceValue) {
        getAttributes().getFloatAttribute(ATTR_SOURCE_VALUE_NAME).setValue(sourceValue);
    }

    /**
     * Gets value of includeBoundary attribute.
     * 
     * @return value of includeBoundary attribute
     * @see #setIncludeBoundary
     */
    public boolean getIncludeBoundary() {
        return getAttributes().getBooleanAttribute(ATTR_INCLUDE_BOUNDARY_NAME).getComputedNonNullValue();
    }

    /**
     * Sets new value of includeBoundary attribute.
     * 
     * @param includeBoundary new value of includeBoundary attribute
     * @see #getIncludeBoundary
     */
    public void setIncludeBoundary(Boolean includeBoundary) {
        getAttributes().getBooleanAttribute(ATTR_INCLUDE_BOUNDARY_NAME).setValue(includeBoundary);
    }
}
