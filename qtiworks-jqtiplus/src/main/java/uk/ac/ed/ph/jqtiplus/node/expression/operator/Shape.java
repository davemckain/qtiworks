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

import uk.ac.ed.ph.jqtiplus.attribute.value.CoordsAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.PointValue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for inside expression.
 *
 * @see Inside
 * @author Jiri Kajaba
 */
public enum Shape implements Stringifiable {

    /**
     * The default shape refers to the entire area of the associated image.
     */
    DEFAULT("default") {

        private static final int COORDS_LENGTH = 0;

        @Override
        public void validateCoords(final ValidationContext context, final CoordsAttribute attribute, final int[] coords) {
            validateCoordsLength(context, attribute, coords, COORDS_LENGTH);
        }

        @Override
        public boolean isInside(final int[] coords, final PointValue point) {
            return true; //always true
        }
    },

    /**
     * A rectangular region.
     */
    RECT("rect") {

        private static final int COORDS_LENGTH = 4;

        private static final int LEFT_X = 0;

        private static final int TOP_Y = 1;

        private static final int RIGHT_X = 2;

        private static final int BOTTOM_Y = 3;

        @Override
        public void validateCoords(final ValidationContext context, final CoordsAttribute attribute, final int[] coords) {
            validateCoordsLength(context, attribute, coords, COORDS_LENGTH);

            if (coords.length == COORDS_LENGTH) {
                validatePositiveCoords(context, attribute, coords);

                if (coords[LEFT_X] >= coords[RIGHT_X]) {
                    context.fireAttributeValidationError(attribute, "Left-x (" + coords[LEFT_X] +
                            ") cannot be larger or equal than right-x (" + coords[RIGHT_X] + ").");
                }

                if (coords[TOP_Y] >= coords[BOTTOM_Y]) {
                    context.fireAttributeValidationError(attribute, "Top-y (" + coords[TOP_Y] +
                            ") cannot be larger or equal than bottom-y (" + coords[BOTTOM_Y] + ").");
                }
            }
        }

        @Override
        public boolean isInside(final int[] coords, final PointValue point) {
            final boolean result =
                    point.horizontalValue() >= coords[LEFT_X] &&
                            point.horizontalValue() <= coords[RIGHT_X] &&
                            point.verticalValue() >= coords[TOP_Y] &&
                            point.verticalValue() <= coords[BOTTOM_Y];

            return result;
        }
    },

    /**
     * A circular region.
     */
    CIRCLE("circle") {

        private static final int COORDS_LENGTH = 3;

        private static final int CENTER_X = 0;

        private static final int CENTER_Y = 1;

        private static final int RADIUS = 2;

        @Override
        public void validateCoords(final ValidationContext context, final CoordsAttribute attribute, final int[] coords) {
            validateCoordsLength(context, attribute, coords, COORDS_LENGTH);

            if (coords.length == COORDS_LENGTH) {
                if (coords[RADIUS] < 1) {
                    context.fireAttributeValidationError(attribute, "Radius (" + coords[RADIUS] + ") must be positive.");
                }

                validatePositiveCoords(context, attribute, coords);
            }
        }

        @Override
        public boolean isInside(final int[] coords, final PointValue point) {
            final double x = Math.pow(point.horizontalValue() - coords[CENTER_X], 2);
            final double y = Math.pow(point.verticalValue() - coords[CENTER_Y], 2);

            return x + y <= Math.pow(coords[RADIUS], 2);
        }
    },

    /**
     * An arbitrary polygonal region.
     */
    POLY("poly") {

        private static final int MINIMUM_COORDS_LENGTH = 6;

        @Override
        public void validateCoords(final ValidationContext context, final CoordsAttribute attribute, final int[] coords) {
            boolean sameLastPoint = false;
            if (coords.length > 1 && coords[0] == coords[coords.length - 2] && coords[1] == coords[coords.length - 1]) {
                sameLastPoint = true;
            }

            int minimumCoordsLength = MINIMUM_COORDS_LENGTH;
            if (sameLastPoint) {
                minimumCoordsLength += 2;
            }

            if (coords.length < minimumCoordsLength) {
                context.fireAttributeValidationError(attribute, "Invalid number of coordinates for " + toString() + " shape. Expected at least: " + minimumCoordsLength + ", but found: " + coords.length);
            }

            if (coords.length >= minimumCoordsLength) {
                if (coords.length % 2 != 0) {
                    context.fireAttributeValidationError(attribute, "Invalid number of coordinates for " + toString() + " shape. Expected even number of coordinates, but found: " + coords.length);
                }

                validatePositiveCoords(context, attribute, coords);
            }
        }

        @Override
        public boolean isInside(final int[] coords, final PointValue point) {
            // If the last point of poly is not the same like first one append it.
            int[] co = coords;
            if (co[0] != co[co.length - 2] || co[1] != co[co.length - 1]) {
                final int[] newCoords = new int[co.length + 2];
                System.arraycopy(co, 0, newCoords, 0, co.length);

                newCoords[newCoords.length - 2] = co[0];
                newCoords[newCoords.length - 1] = co[1];

                co = newCoords;
            }

            // Sum the signed angles formed at the point (B) by each edge's endpoints (A, C).
            // If the sum is near zero, the point is outside; if not, it's inside.

            // Sum of all signed angles (ABC).
            double sum = 0;

            // Tested point. Second vertex (B).
            final int bx = point.horizontalValue();
            final int by = point.verticalValue();

            for (int i = 0; i < co.length - 3; i += 2) {
                // First vertex (A).
                final int ax = co[i];
                final int ay = co[i + 1];
                // Third vertex (B).
                final int cx = co[i + 2];
                final int cy = co[i + 3];

                // Distance between B and C.
                final double a = Math.sqrt(Math.pow(bx - cx, 2) + Math.pow(by - cy, 2));
                // Distance between C and A.
                final double b = Math.sqrt(Math.pow(cx - ax, 2) + Math.pow(cy - ay, 2));
                // Distance between A and B.
                final double c = Math.sqrt(Math.pow(ax - bx, 2) + Math.pow(ay - by, 2));

                // Computes angle ABC.
                final double angle = Math.acos((Math.pow(a, 2) - Math.pow(b, 2) + Math.pow(c, 2)) /
                        (2 * a * c)) * 180 / Math.PI;
                // Orientation of angle. Positive: counter clockwise. Negative: clockwise.
                final double sign = (cx - bx) * (by - ay) - (cy - by) * (bx - ax);

                // If tested point (B) is same like first (A) or third (B) vertex, computed angle is NaN.
                if (Double.isNaN(angle)) {
                    return true;
                }

                // Adds/removes computed angle to/from sum.
                if (sign >= 0) {
                    sum += angle;
                }
                else {
                    sum -= angle;
                }
            }

            BigDecimal bigDecimal = new BigDecimal(sum);
            // Rounds sum because of inaccuracy in computation.
            bigDecimal = bigDecimal.setScale(6, BigDecimal.ROUND_HALF_UP);

            // If and only if sum is zero, point is outside of polygon.
            return bigDecimal.doubleValue() != 0;
        }
    },

    /**
     * This value is deprecated, but is included for compatibility with version of 1 of the QTI specification.
     * Systems should use circle or poly shapes instead.
     */
    ELLIPSE("ellipse") {

        private static final int COORDS_LENGTH = 4;

        private static final int CENTER_X = 0;

        private static final int CENTER_Y = 1;

        private static final int H_RADIUS = 2;

        private static final int V_RADIUS = 3;

        @Override
        public void validateCoords(final ValidationContext context, final CoordsAttribute attribute, final int[] coords) {
            validateCoordsLength(context, attribute, coords, COORDS_LENGTH);

            if (coords.length == COORDS_LENGTH) {
                if (coords[H_RADIUS] < 1) {
                    context.fireAttributeValidationError(attribute, "H-radius (" + coords[H_RADIUS] + ") must be positive.");
                }

                if (coords[V_RADIUS] < 1) {
                    context.fireAttributeValidationError(attribute, "V-radius (" + coords[V_RADIUS] + ") must be positive.");
                }

                validatePositiveCoords(context, attribute, coords);
            }
        }

        @Override
        public boolean isInside(final int[] coords, final PointValue point) {
            final double x = Math.pow(point.horizontalValue() - coords[CENTER_X], 2) / Math.pow(coords[H_RADIUS], 2);
            final double y = Math.pow(point.verticalValue() - coords[CENTER_Y], 2) / Math.pow(coords[V_RADIUS], 2);

            return x + y <= 1;
        }
    };

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "shape";

    private static Map<String, Shape> shapes;

    static {
        shapes = new HashMap<String, Shape>();

        for (final Shape shape : Shape.values()) {
            shapes.put(shape.shape, shape);
        }
    }

    private String shape;

    private Shape(final String shape) {
        this.shape = shape;
    }

    /**
     * Validates coords attribute.
     *
     * @param attribute attribute to be validated
     * @param result TODO
     * @param coords attribute's value to be validated
     */
    public abstract void validateCoords(final ValidationContext context, CoordsAttribute attribute, int[] coords);

    /**
     * Validates length of coords attribute (number of coordinates).
     *
     * @param attribute attribute to be validated
     * @param result TODO
     * @param coords attribute's value to be validated
     * @param expectedLength expected length of coords attribute (number of coordinates)
     */
    protected void validateCoordsLength(final ValidationContext context, final CoordsAttribute attribute, final int[] coords, final int expectedLength) {
        if (coords.length != expectedLength) {
            context.fireAttributeValidationError(attribute, "Invalid number of coordinates for " +
                    toString() + " shape. Expected: " + expectedLength + ", but found: " + coords.length);
        }
    }

    /**
     * Validates if all coordinates are greater or equal than zero.
     *
     * @param attribute attribute to be validated
     * @param result TODO
     * @param coords attribute's value to be validated
     */
    protected void validatePositiveCoords(final ValidationContext context, final CoordsAttribute attribute, final int[] coords) {
        for (int i = 0; i < coords.length; i++) {
            if (coords[i] < 0) {
                context.fireAttributeValidationError(attribute, "Coordinate (" + coords[i] + ") at (" + (i + 1) +
                        ") cannot be negative.");
            }
        }
    }

    /**
     * Returns true if given <code>PointValue</code> is inside this <code>Shape</code>; false otherwise.
     *
     * @param coords coordinates of this shape
     * @param point given <code>PointValue</code>
     * @return true if given <code>PointValue</code> is inside this <code>Shape</code>; false otherwise
     */
    public abstract boolean isInside(int[] coords, PointValue point);

    @Override
    public String toQtiString() {
        return shape;
    }

    /**
     * Parses string representation of <code>Shape</code>.
     *
     * @param shape string representation of <code>Shape</code>
     * @return parsed <code>Shape</code>
     */
    public static Shape parseShape(final String shape) {
        final Shape result = shapes.get(shape);

        if (result == null) {
            throw new QtiParseException("Invalid " + QTI_CLASS_NAME + " '" + shape + "'.");
        }

        return result;
    }
}
