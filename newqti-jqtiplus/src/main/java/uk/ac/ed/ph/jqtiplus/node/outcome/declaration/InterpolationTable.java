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

import uk.ac.ed.ph.jqtiplus.group.outcome.declaration.InterpolationTableEntryGroup;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;

import java.util.List;

/**
 * An interpolationTable transforms A source float (or integer) by finding the first interpolationTableEntry
 * with A sourceValue that is less than or equal to (subject to includeBoundary) the source value.
 * <p>
 * For example, an interpolation table can be used to map A raw numeric score onto an identifier representing A grade. It may also be used to implement numeric
 * transformations such as those from A simple raw score to A value on A calibrated scale.
 * 
 * @author Jiri Kajaba
 */
public class InterpolationTable extends LookupTable {

    private static final long serialVersionUID = -7056243816798489068L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "interpolationTable";

    /**
     * Creates object.
     * 
     * @param parent parent of this object
     */
    public InterpolationTable(OutcomeDeclaration parent) {
        super(parent);

        getNodeGroups().add(new InterpolationTableEntryGroup(this));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public List<? extends LookupTableEntry> getLookupEntries() {
        return getInterpolationEntries();
    }

    /**
     * Gets interpolationTableEntry children.
     * 
     * @return interpolationTableEntry children
     */
    public List<InterpolationTableEntry> getInterpolationEntries() {
        return getNodeGroups().getInterpolationTableEntryGroup().getEntries();
    }

    @Override
    public SingleValue getTargetValue(NumberValue sourceValue) {
        if (sourceValue != null) {
            for (final InterpolationTableEntry entry : getInterpolationEntries()) {
                if (entry.getIncludeBoundary()) {
                    if (entry.getSourceValue().doubleValue() <= sourceValue.doubleValue()) {
                        return entry.getTargetValue();
                    }
                }
                else {
                    if (entry.getSourceValue().doubleValue() < sourceValue.doubleValue()) {
                        return entry.getTargetValue();
                    }
                }
            }
        }

        return super.getTargetValue(sourceValue);
    }

    @Override
    protected void validateChildren(ValidationContext context, ValidationResult result) {
        super.validateChildren(context, result);

        Double lastValue = null;
        for (final InterpolationTableEntry entry : getInterpolationEntries()) {
            if (entry.getSourceValue() != null) {
                final double currentValue = entry.getSourceValue().doubleValue();

                if (lastValue == null || lastValue >= currentValue) {
                    lastValue = currentValue;
                }
                else {
                    result.add(new ValidationWarning(this, "Invalid order of entries. Entries should be ordered from highest to lowest."));
                    break;
                }
            }
        }
    }
}
