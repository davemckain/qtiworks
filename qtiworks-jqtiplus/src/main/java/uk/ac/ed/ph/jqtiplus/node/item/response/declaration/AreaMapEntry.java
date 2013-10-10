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

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ShapeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.CoordsAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Shape;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.util.List;

/**
 * @see AreaMapping
 * @author Jonathon Hare
 */
public final class AreaMapEntry extends AbstractNode {

    private static final long serialVersionUID = -8158849868275249200L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "areaMapEntry";

    /** Name of shape attribute in xml schema. */
    public static final String ATTR_SHAPE_NAME = "shape";

    /** Name of coords attribute in xml schema. */
    public static final String ATTR_COORDS_NAME = "coords";

    /** Name of mappedValue attribute in xml schema. */
    public static final String ATTR_MAPPED_VALUE_NAME = "mappedValue";

    public AreaMapEntry(final AreaMapping parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new ShapeAttribute(this, ATTR_SHAPE_NAME, true));
        getAttributes().add(new CoordsAttribute(this, ATTR_COORDS_NAME, true));
        getAttributes().add(new FloatAttribute(this, ATTR_MAPPED_VALUE_NAME, true));
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        if (getShape() != null) {
            getShape().validateCoords(context, getAttributes().getCoordsAttribute(ATTR_COORDS_NAME),
                    convertCoordinates(getCoordinates()));
        }
    }



    public Shape getShape() {
        return getAttributes().getShapeAttribute(ATTR_SHAPE_NAME).getComputedValue();
    }

    public void setShape(final Shape shape) {
        getAttributes().getShapeAttribute(ATTR_SHAPE_NAME).setValue(shape);
    }


    public List<Integer> getCoordinates() {
        return getAttributes().getCoordsAttribute(ATTR_COORDS_NAME).getComputedValue();
    }

    public void setCoordinates(final List<Integer> value) {
        getAttributes().getCoordsAttribute(ATTR_COORDS_NAME).setValue(value);
    }


    public double getMappedValue() {
        return getAttributes().getFloatAttribute(ATTR_MAPPED_VALUE_NAME).getComputedNonNullValue();
    }

    public void setMappedValue(final Double mappedValue) {
        getAttributes().getFloatAttribute(ATTR_MAPPED_VALUE_NAME).setValue(mappedValue);
    }


    /**
     * Converts list of coordinates to array of coordinates.
     *
     * @param coords list of coordinates
     * @return array of coordinates
     */
    private int[] convertCoordinates(final List<Integer> coords) {
        final int[] result = new int[coords.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = coords.get(i).intValue();
        }

        return result;
    }
}
