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

import uk.ac.ed.ph.jqtiplus.attribute.value.SingleValueAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;

import java.util.List;

/**
 * An abstract class associated with an outcomeDeclaration used to create A lookup table from A numeric source value
 * to A single outcome value in the declared value set. A lookup table works in the reverse sense to the similar mapping
 * as it defines how A source numeric value is transformed into the outcome value, whereas A (response) mapping defines
 * how the response value is mapped onto A target numeric value.
 * <p>
 * The transformation takes place using the lookupOutcomeValue rule within responseProcessing or outcomeProcessing.
 *
 * @author Jiri Kajaba
 */
public abstract class LookupTable extends AbstractNode {

    private static final long serialVersionUID = -6380035531180684801L;

    /** Display name of this class. */
    public static final String DISPLAY_NAME = "lookupTable";

    /** Name of defaultValue attribute in xml schema. */
    public static final String ATTR_DEFAULT_VALUE_NAME = "defaultValue";

    public LookupTable(final OutcomeDeclaration parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new SingleValueAttribute(this, ATTR_DEFAULT_VALUE_NAME, getTargetValueBaseType(), false));
    }

    @Override
    public OutcomeDeclaration getParent() {
        return (OutcomeDeclaration) super.getParent();
    }

    public BaseType getTargetValueBaseType() {
        return getParent().getBaseType();
    }


    public SingleValue getDefaultValue() {
        return getAttributes().getSingleValueAttribute(ATTR_DEFAULT_VALUE_NAME).getComputedValue();
    }

    public void setDefaultValue(final SingleValue defaultValue) {
        getAttributes().getSingleValueAttribute(ATTR_DEFAULT_VALUE_NAME).setValue(defaultValue);
    }

    /**
     * Gets lookupTableEntry children.
     *
     * @return lookupTableEntry children
     */
    public abstract List<? extends LookupTableEntry> getLookupEntries();

    /**
     * Gets target value for given source value.
     *
     * @param sourceValue given source value
     * @return target value for given source value
     */
    public SingleValue getTargetValue(final NumberValue sourceValue) {
        SingleValue targetValue = getDefaultValue();
        if (targetValue == null) {
            if (getParent().getCardinality().isSingle() && getParent().getBaseType().isInteger()) {
                targetValue = IntegerValue.ZERO;
            }
            else if (getParent().getCardinality().isSingle() && getParent().getBaseType().isFloat()) {
                targetValue = new FloatValue(0);
            }
        }

        return targetValue;
    }

    @Override
    protected void validateAttributesComplex(final ValidationContext context) {
        final Cardinality cardinality = getParent().getCardinality();
        if (cardinality != null) {
            if (!cardinality.isSingle()) {
                context.fireValidationError(this, "This node is not supported for " + Cardinality.QTI_CLASS_NAME + ": " + cardinality);
            }
        }

        if (getParent().getBaseType() != null) {
            getAttributes().getSingleValueAttribute(ATTR_DEFAULT_VALUE_NAME).setBaseType(getParent().getBaseType());
        }
    }

    @Override
    protected void validateChildren(final ValidationContext context) {
        super.validateChildren(context);

        for (int i = 0; i < getLookupEntries().size(); i++) {
            final LookupTableEntry firstEntry = getLookupEntries().get(i);
            if (firstEntry.getSourceValue() != null) {
                for (int j = i + 1; j < getLookupEntries().size(); j++) {
                    final LookupTableEntry secondEntry = getLookupEntries().get(j);
                    if (secondEntry.getSourceValue() != null && firstEntry.getSourceValue().doubleValue() == secondEntry.getSourceValue().doubleValue()) {
                        context.fireValidationWarning(this, "Duplicate source value: " + firstEntry.getSourceValue());
                    }
                }
            }
        }
    }
}
