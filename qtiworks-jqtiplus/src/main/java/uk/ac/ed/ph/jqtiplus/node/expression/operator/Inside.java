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
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ShapeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.CoordsAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractSimpleFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.PointValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.List;

/**
 * The inside operator takes a single sub-expression which must have a baseType of point. The result
 * is a single boolean with a value of true if the given point is inside the area defined by shape and
 * coords. If the sub-expression is a container the result is true if any of the points are inside the
 * area. If either sub-expression is NULL then the operator results in NULL.
 * <p>
 * This implementation doesn't support record container, default shape and percentage values.
 * <p>
 * Separator of coordinate values is not comma but space.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class Inside extends AbstractSimpleFunctionalExpression {

    private static final long serialVersionUID = 4926097648005221931L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "inside";

    /** Name of shape attribute in xml schema. */
    public static final String ATTR_SHAPE_NAME = Shape.QTI_CLASS_NAME;

    /** Name of coords attribute in xml schema. */
    public static final String ATTR_COORDINATES_NAME = "coords";

    public Inside(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new ShapeAttribute(this, ATTR_SHAPE_NAME, true));
        getAttributes().add(new CoordsAttribute(this, ATTR_COORDINATES_NAME, false));
    }

    public Shape getShape() {
        return getAttributes().getShapeAttribute(ATTR_SHAPE_NAME).getComputedValue();
    }

    public void setShape(final Shape shape) {
        getAttributes().getShapeAttribute(ATTR_SHAPE_NAME).setValue(shape);
    }


    public List<Integer> getCoordinates() {
        return getAttributes().getCoordsAttribute(ATTR_COORDINATES_NAME).getComputedValue();
    }

    public void setCoordinates(final List<Integer> value) {
        getAttributes().getCoordsAttribute(ATTR_COORDINATES_NAME).setValue(value);
    }


    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        if (getShape() != null) {
            getShape().validateCoords(context, getAttributes().getCoordsAttribute(ATTR_COORDINATES_NAME),
                   convertCoordinates(getCoordinates()));
        }
    }

    @Override
    protected Value evaluateValidSelf(final Value[] childValues) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        boolean result = false;

        final int[] coords = convertCoordinates(getCoordinates());

        if (childValues[0].getCardinality().isSingle()) {
            final PointValue point = (PointValue) childValues[0];
            result = getShape().isInside(coords, point);
        }
        else {
            final ListValue list = (ListValue) childValues[0];
            for (int i = 0; i < list.size(); i++) {
                final PointValue point = (PointValue) list.get(i);
                if (getShape().isInside(coords, point)) {
                    result = true;
                    break;
                }
            }
        }

        return BooleanValue.valueOf(result);
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
