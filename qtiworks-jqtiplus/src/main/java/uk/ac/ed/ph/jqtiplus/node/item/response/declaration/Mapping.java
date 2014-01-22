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

import uk.ac.ed.ph.jqtiplus.attribute.value.FloatAttribute;
import uk.ac.ed.ph.jqtiplus.group.item.response.declaration.MapEntryGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.expression.general.MapResponse;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A special class used to create a mapping from a source set of any baseType
 * (except file and duration) to a single float. Note that mappings from values
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
    public void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Double lowerBound = getLowerBound();
        final Double upperBound = getUpperBound();

        if (lowerBound != null && upperBound != null && lowerBound.doubleValue() > upperBound.doubleValue()) {
            context.fireValidationError(this, "Upper bound cannot be less than lower bound");
        }

        final ResponseDeclaration declaration = getParent();
        if (declaration != null) {
            if (declaration.hasBaseType(BaseType.FILE, BaseType.DURATION)) {
                context.fireValidationError(this, "File or duration base types are not supported with a mapping");
            }

            /* (The new caseSensitive="false" entries are only permitted for string baseTypes) */
            boolean hasCaseInsensitiveEntry = false;
            for (final MapEntry mapEntry : getMapEntries()) {
                if (!mapEntry.getCaseSensitive()) {
                    hasCaseInsensitiveEntry = true;
                    break;
                }
            }
            if (hasCaseInsensitiveEntry && !declaration.hasBaseType(BaseType.STRING)) {
                context.fireValidationError(this, "Only String base types may be used with case insensitive mapEntries");
            }
        }
    }

    /**
     * Gets target value for given source value.
     *
     * @param sourceValue given source value
     * @return target value for given source value
     */
    public FloatValue computeTargetValue(final Value sourceValue) {
        if (!sourceValue.isNull()) {
            final ResponseDeclaration responseDeclaration = getParent();
            if (responseDeclaration.hasCardinality(Cardinality.SINGLE)) {
                /* Single cardinality => take mapped value, using default if nothing specified */
                return new FloatValue(applyConstraints(mapSingleValue((SingleValue) sourceValue)));
            }
            else if (responseDeclaration.getCardinality().isList()) {
                /* Multiple cardinality => sum mapped values of unique items in container */
                double sum = 0.0;
                final ListValue sourceListValue = (ListValue) sourceValue;
                final Set<SingleValue> uniqueValues = new HashSet<SingleValue>(sourceListValue.getAll());
                for (final SingleValue value : uniqueValues) {
                    sum += mapSingleValue(value);
                }
                return new FloatValue(applyConstraints(sum));
            }
        }
        return new FloatValue(applyConstraints(getDefaultValue()));
    }

    private double mapSingleValue(final SingleValue value) {
        double result = getDefaultValue();
        for (final MapEntry entry : getMapEntries()) {
            if (entryCompare(entry, value)) {
                result = entry.getMappedValue();
                break;
            }
        }
        return result;
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
