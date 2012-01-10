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
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ShapeAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.CoordsAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.PointValue;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xperimental.control.ProcessingContext;

import java.util.List;

/**
 * The inside operator takes A single sub-expression which must have A baseType of point. The result
 * is A single boolean with A value of true if the given point is inside the area defined by shape and
 * coords. If the sub-expression is A container the result is true if any of the points are inside the
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
public class Inside extends AbstractExpression {

    private static final long serialVersionUID = 4926097648005221931L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "inside";

    /** Name of shape attribute in xml schema. */
    public static final String ATTR_SHAPE_NAME = Shape.CLASS_TAG;

    /** Name of coords attribute in xml schema. */
    public static final String ATTR_COORDINATES_NAME = "coords";

    /** Default value of coords attribute. */
    public static final List<Integer> ATTR_COORDINATES_DEFAULT_VALUE = null;

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public Inside(ExpressionParent parent) {
        super(parent);

        getAttributes().add(new ShapeAttribute(this, ATTR_SHAPE_NAME));
        getAttributes().add(new CoordsAttribute(this, ATTR_COORDINATES_NAME, ATTR_COORDINATES_DEFAULT_VALUE));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    /**
     * Gets value of shape attribute.
     * 
     * @return value of shape attribute
     * @see #setShape
     */
    public Shape getShape() {
        return getAttributes().getShapeAttribute(ATTR_SHAPE_NAME).getValue();
    }

    /**
     * Sets new value of shape attribute.
     * 
     * @param shape new value of shape attribute
     * @see #getShape
     */
    public void setShape(Shape shape) {
        getAttributes().getShapeAttribute(ATTR_SHAPE_NAME).setValue(shape);
    }

    /**
     * Gets value of coords attribute.
     * 
     * @return value of coords attribute
     */
    public List<Integer> getCoordinates() {
        return getAttributes().getCoordsAttribute(ATTR_COORDINATES_NAME).getValues();
    }

    @Override
    protected void validateAttributes(ValidationContext context, AbstractValidationResult result) {
        super.validateAttributes(context, result);

        if (getShape() != null) {
            getShape().validateCoords(getAttributes().get(ATTR_COORDINATES_NAME), result, convertCoordinates(getCoordinates()));
        }
    }

    @Override
    protected Value evaluateSelf(ProcessingContext context, int depth) {
        if (isAnyChildNull(context)) {
            return NullValue.INSTANCE;
        }

        boolean result = false;

        final int[] coords = convertCoordinates(getCoordinates());

        if (getFirstChild().getCardinality(context).isSingle()) {
            final PointValue point = (PointValue) getFirstChild().getValue(context);
            result = getShape().isInside(coords, point);
        }
        else {
            final ListValue list = (ListValue) getFirstChild().getValue(context);
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
    private int[] convertCoordinates(List<Integer> coords) {
        final int[] result = new int[coords.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = coords.get(i);
        }

        return result;
    }
}
