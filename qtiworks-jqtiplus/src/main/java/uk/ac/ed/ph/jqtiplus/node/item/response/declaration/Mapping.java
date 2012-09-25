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
import uk.ac.ed.ph.jqtiplus.exception.QtiNotImplementedException;
import uk.ac.ed.ph.jqtiplus.group.item.response.declaration.MapEntryGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.expression.general.MapResponse;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * A special class used to create A mapping from A source set of any baseType
 * (except file and duration) to A single float. Note that mappings from values
 * of base type float should be avoided due to the difficulty of matching floating
 * point values, see the match operator for more details. When mapping containers
 * the result is the sum of the mapped values from the target set.
 *
 * @see MapResponse for details.
 * @author Jonathon Hare
 */
public final class Mapping extends AbstractNode {

    private static final long serialVersionUID = 6513135215422316146L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "mapping";

    /** Name of lowerBound attribute in xml schema. */
    public static final String ATTR_LOWER_BOUND_NAME = "lowerBound";

    /** Name of upperBound attribute in xml schema. */
    public static final String ATTR_UPPER_BOUND_NAME = "upperBound";

    /** Name of defaultValue attribute in xml schema. */
    public static final String ATTR_DEFAULT_VALUE_NAME = "defaultValue";

    /** Default value of the defaultValue attribute */
    public static final double ATTR_DEFAULT_VALUE_DEFAULT_VALUE = 0.0;

    public Mapping(final ResponseDeclaration parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new FloatAttribute(this, ATTR_LOWER_BOUND_NAME, false));
        getAttributes().add(new FloatAttribute(this, ATTR_UPPER_BOUND_NAME, false));
        getAttributes().add(new FloatAttribute(this, ATTR_DEFAULT_VALUE_NAME, ATTR_DEFAULT_VALUE_DEFAULT_VALUE, false));

        getNodeGroups().add(new MapEntryGroup(this));
    }

    @Override
    public ResponseDeclaration getParent() {
        return (ResponseDeclaration) super.getParent();
    }

    public double getDefaultValue() {
        return getAttributes().getFloatAttribute(ATTR_DEFAULT_VALUE_NAME).getComputedNonNullValue();
    }

    public void setDefaultValue(final Double defaultValue) {
        getAttributes().getFloatAttribute(ATTR_DEFAULT_VALUE_NAME).setValue(defaultValue);
    }


    public Double getLowerBound() {
        return getAttributes().getFloatAttribute(ATTR_LOWER_BOUND_NAME).getComputedValue();
    }

    public void setLowerBound(final Double lowerBound) {
        getAttributes().getFloatAttribute(ATTR_LOWER_BOUND_NAME).setValue(lowerBound);
    }


    public Double getUpperBound() {
        return getAttributes().getFloatAttribute(ATTR_UPPER_BOUND_NAME).getComputedValue();
    }

    public void setUpperBound(final Double upperBound) {
        getAttributes().getFloatAttribute(ATTR_UPPER_BOUND_NAME).setValue(upperBound);
    }


    public List<MapEntry> getMapEntries() {
        return getNodeGroups().getMapEntryGroup().getMapEntries();
    }


    @Override
    public void validate(final ValidationContext context) {
        super.validate(context);
        final Double lowerBound = getLowerBound();
        final Double upperBound = getUpperBound();

        if (lowerBound != null && upperBound != null && lowerBound.doubleValue() > upperBound.doubleValue()) {
            context.fireValidationError(this, "Upper bound cannot be less than lower bound.");
        }

        final ResponseDeclaration declaration = getParent();
        if (declaration != null) {
            if (declaration.getBaseType() != null &&
                    (declaration.getBaseType().isFile() || declaration.getBaseType().isDuration())) {
                context.fireValidationError(this, "File or duration base types are not supported with a mapping.");
            }

            /* (The new caseSensitive="false" entries are only permitted for string baseTypes) */
            boolean hasCaseInsensitiveEntry = false;
            for (final MapEntry mapEntry : getMapEntries()) {
                if (!mapEntry.getCaseSensitive()) {
                    hasCaseInsensitiveEntry = true;
                    break;
                }
            }
            if (hasCaseInsensitiveEntry && !declaration.getBaseType().isString()) {
                context.fireValidationError(this, "Only String base types may be used with case insensitive mapEntries.");
            }
        }
    }

    /**
     * Gets target value for given source value.
     *
     * @param sourceValue given source value
     * @return target value for given source value
     */
    public FloatValue getTargetValue(final Value sourceValue) {
        if (!sourceValue.isNull()) {
            /*
             * If the response variable has single cardinality then the value
             * returned is simply the mapped target value from the map. If the
             * response variable has multiple or ordered cardinality then the
             * value returned is the sum of the mapped target values. This
             * expression cannot be applied to variables of record cardinality.
             *
             * For example, if a mapping associates the identifiers {A,B,C,D}
             * with the values {0,1,0.5,0} respectively then mapResponse will
             * map the single value 'C' to the numeric value 0.5 and the set of
             * values {C,B} to the value 1.5.
             *
             * If a container contains multiple instances of the same value then
             * that value is counted once only. To continue the example above
             * {B,B,C} would still map to 1.5 and not 2.5.
             */
            if (getParent().getCardinality() == Cardinality.SINGLE) {
                for (final MapEntry entry : getMapEntries()) {
                    if (entryCompare(entry, (SingleValue) sourceValue)) {
                        return new FloatValue(applyConstraints(entry.getMappedValue()));
                    }
                }
            }
            else {
                if (!(sourceValue instanceof ListValue)) {
                    throw new QtiNotImplementedException();
                }
                double sum = 0.0;
                final List<SingleValue> values = new ArrayList<SingleValue>(((ListValue) sourceValue).getAll());

                for (final MapEntry entry : getMapEntries()) {
                    boolean allow = true;

                    for (int i = 0; i < ((ListValue) sourceValue).size(); i++) {
                        if (entryCompare(entry, ((ListValue) sourceValue).get(i))) {
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
        }

        return new FloatValue(applyConstraints(getDefaultValue()));
    }

    private boolean entryCompare(final MapEntry mapEntry, final SingleValue value) {
        boolean result;
        final SingleValue mapKey = mapEntry.getMapKey();
        if (mapEntry.getCaseSensitive()) {
            result = mapKey.equals(value);
        }
        else {
            result = mapKey.toQtiString().equalsIgnoreCase(value.toQtiString());
        }
        return result;
    }

    private double applyConstraints(final double value) {
        double result = value;
        final Double lowerBound = getLowerBound();
        if (lowerBound != null) {
            result = Math.max(result, lowerBound.doubleValue());
        }

        final Double upperBound = getUpperBound();
        if (upperBound != null) {
            result = Math.min(result, upperBound.doubleValue());
        }

        return result;
    }
}
