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
package uk.ac.ed.ph.jqtiplus.node.item.response.declaration;


import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QTINotImplementedException;
import uk.ac.ed.ph.jqtiplus.group.item.response.declaration.AreaMapEntryGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.expression.general.MapResponsePoint;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.PointValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * A special class used to create a mapping from a source set of point
 * values to a target set of float values. When mapping containers, the
 * result is the sum of the mapped values from the target set. See
 * mapResponsePoint for details. The attributes have the same meaning
 * as the similarly named attributes on mapping.
 * 
 * @see MapResponsePoint for details.
 * @author Jonathon Hare
 */
public class AreaMapping extends AbstractNode {

    private static final long serialVersionUID = 6134649478484970261L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "areaMapping";

    /** Name of lowerBound attribute in xml schema. */
    public static final String ATTR_LOWER_BOUND_NAME = "lowerBound";

    /** Name of upperBound attribute in xml schema. */
    public static final String ATTR_UPPER_BOUND_NAME = "upperBound";

    /** Name of defaultValue attribute in xml schema. */
    public static final String ATTR_DEFAULT_VALUE_NAME = "defaultValue";

    /** Default value of the defaultValue attribute */
    public static final double ATTR_DEFAULT_VALUE_DEFAULT_VALUE = 0.0;

    /**
     * Creates object.
     * 
     * @param parent parent of this object
     */
    public AreaMapping(ResponseDeclaration parent) {
        super(parent);

        getAttributes().add(new FloatAttribute(this, ATTR_LOWER_BOUND_NAME, null, null, false));
        getAttributes().add(new FloatAttribute(this, ATTR_UPPER_BOUND_NAME, null, null, false));
        getAttributes().add(new FloatAttribute(this, ATTR_DEFAULT_VALUE_NAME, ATTR_DEFAULT_VALUE_DEFAULT_VALUE));

        getNodeGroups().add(new AreaMapEntryGroup(this));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public ResponseDeclaration getParent() {
        return (ResponseDeclaration) super.getParent();
    }

    /**
     * Gets value of defaultValue attribute.
     * 
     * @return value of defaultValue attribute
     * @see #setDefaultValue
     */
    public Double getDefaultValue() {
        return getAttributes().getFloatAttribute(ATTR_DEFAULT_VALUE_NAME).getValue();
    }

    /**
     * Sets new value of defaultValue attribute.
     * 
     * @param defaultValue new value of defaultValue attribute
     * @see #getDefaultValue
     */
    public void setDefaultValue(Double defaultValue) {
        getAttributes().getFloatAttribute(ATTR_DEFAULT_VALUE_NAME).setValue(defaultValue);
    }

    /**
     * Gets value of lowerBound attribute.
     * 
     * @return value of lowerBound attribute
     * @see #setLowerBound
     */
    public Double getLowerBound() {
        return getAttributes().getFloatAttribute(ATTR_LOWER_BOUND_NAME).getValue();
    }

    /**
     * Sets new value of lowerBound attribute.
     * 
     * @param lowerBound new value of lowerBound attribute
     * @see #getLowerBound
     */
    public void setLowerBound(Double lowerBound) {
        getAttributes().getFloatAttribute(ATTR_LOWER_BOUND_NAME).setValue(lowerBound);
    }

    /**
     * Gets value of upperBound attribute.
     * 
     * @return value of upperBound attribute
     * @see #setUpperBound
     */
    public Double getUpperBound() {
        return getAttributes().getFloatAttribute(ATTR_UPPER_BOUND_NAME).getValue();
    }

    /**
     * Sets new value of upperBound attribute.
     * 
     * @param upperBound new value of upperBound attribute
     * @see #getUpperBound
     */
    public void setUpperBound(Double upperBound) {
        getAttributes().getFloatAttribute(ATTR_UPPER_BOUND_NAME).setValue(upperBound);
    }

    /**
     * Gets areaMapEntry children.
     * 
     * @return areaMapEntry children
     */
    public List<AreaMapEntry> getAreaMapEntries() {
        return getNodeGroups().getAreaMapEntryGroup().getAreaMapEntries();
    }

    @Override
    public void validate(ValidationContext context, ValidationResult result) {
        super.validate(context, result);

        if (getLowerBound() != null && getUpperBound() != null && getLowerBound() > getUpperBound()) {
            result.add(new ValidationError(this, "Upper bound cannot be less than lower bound."));
        }

        final ResponseDeclaration declaration = getParent();
        if (declaration != null) {
            if (declaration.getBaseType() != null && !declaration.getBaseType().isPoint()) {
                result.add(new ValidationError(this, "Base type must be point."));
            }
        }
    }

    /**
     * Gets target value for given source value.
     * 
     * @param sourceValue given source value
     * @return target value for given source value
     */
    public FloatValue getTargetValue(Value sourceValue) {
        if (sourceValue != null) {
            /*
             * The transformation is similar to mapResponse except that 
             * the points are tested against each area in turn. When 
             * mapping containers each area can be mapped once only. For 
             * example, if the candidate identified two points that both 
             * fall in the same area then the mappedValue is still added 
             * to the calculated total just once.
             */
            if (getParent().getCardinality() == Cardinality.SINGLE) {
                for (final AreaMapEntry entry : getAreaMapEntries()) {
                    if (entry.getShape().isInside(convertCoordinates(entry.getCoordinates()), (PointValue) sourceValue)) {
                        return new FloatValue(applyConstraints(entry.getMappedValue()));
                    }
                }
            }
            else {
                if (sourceValue instanceof ListValue) {
                    double sum = 0.0;
                    final List<SingleValue> values = new ArrayList<SingleValue>(((ListValue) sourceValue).getAll());

                    for (final AreaMapEntry entry : getAreaMapEntries()) {
                        boolean allow = true;
                        for (int i = 0; i < ((ListValue) sourceValue).size(); i++) {
                            if (entry.getShape().isInside(convertCoordinates(entry.getCoordinates()), (PointValue) ((ListValue) sourceValue).get(i))) {
                                if (allow) {
                                    sum += entry.getMappedValue();
                                    allow = false;
                                }
                                values.remove(((ListValue) sourceValue).get(i));
                            }
                        }
                    }
                    sum += getDefaultValue() * values.size();

                    return new FloatValue(applyConstraints(sum));
                }
                else {
                    throw new QTINotImplementedException();
                }
            }
        }

        return new FloatValue(applyConstraints(getDefaultValue()));
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

    private double applyConstraints(double value) {
        if (getLowerBound() != null) {
            if (value < getLowerBound()) {
                value = getLowerBound();
            }
        }

        if (getUpperBound() != null) {
            if (value > getUpperBound()) {
                value = getUpperBound();
            }
        }

        return value;
    }
}
