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
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.DurationAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

/**
 * In the context of a specific assessmentTest an item, or group of items, may be subject to a time constraint.
 * This specification supports both minimum and maximum time constraints. The controlled time for a single item
 * is simply the duration of the item session as defined by the built-in response variable duration.
 * For assessmentSections, testParts and whole assessmentTests the time limits relate to the durations of all the
 * item sessions plus any other time spent navigating that part of the test. In other words, the time includes time
 * spent in states where no item is being interacted with, such as dedicated navigation screens.
 * <p>
 * Minimum times are applicable to assessmentSections and assessmentItems only when linear navigation mode is in effect.
 *
 * @author Jiri Kajaba
 */
public final class TimeLimits extends AbstractNode {

    private static final long serialVersionUID = 2090259996374843635L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "timeLimits";

    /** Name of minTime attribute in xml schema. */
    public static final String ATTR_MINIMUM_NAME = "minTime";

    /** Name of maxTime attribute in xml schema. */
    public static final String ATTR_MAXIMUM_NAME = "maxTime";

    /** Name of allowLateSubmission attribute in xml schema. */
    public static final String ATTR_ALLOW_LATE_SUBMISSION = "allowLateSubmission";

    public TimeLimits(final ControlObject<?> parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new DurationAttribute(this, ATTR_MINIMUM_NAME, false));
        getAttributes().add(new DurationAttribute(this, ATTR_MAXIMUM_NAME, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_ALLOW_LATE_SUBMISSION, false, false));
    }


    public Double getMinimum() {
        return getAttributes().getDurationAttribute(ATTR_MINIMUM_NAME).getComputedValue();
    }

    public void setMinimum(final Double minimum) {
        getAttributes().getDurationAttribute(ATTR_MINIMUM_NAME).setValue(minimum);
    }

    /**
     * Gets value of minTime attribute in millis or null.
     *
     * @return value of minTime attribute in millis or null
     */
    public Long getMinimumMillis() {
        if (getMinimum() == null) {
            return null;
        }

        return Long.valueOf((long) (getMinimum().doubleValue() * 1000));
    }


    public Double getMaximum() {
        return getAttributes().getDurationAttribute(ATTR_MAXIMUM_NAME).getComputedValue();
    }

    public void setMaximum(final Double maximum) {
        getAttributes().getDurationAttribute(ATTR_MAXIMUM_NAME).setValue(maximum);
    }

    /**
     * Gets value of maxTime attribute in millis.
     *
     * @return value of maxTime attribute in millis
     */
    public Long getMaximumMillis() {
        if (getMaximum() == null) {
            return null;
        }

        return Long.valueOf((long) (getMaximum().doubleValue() * 1000));
    }


    public boolean getAllowLateSubmission() {
        return getAttributes().getBooleanAttribute(ATTR_ALLOW_LATE_SUBMISSION).getComputedNonNullValue();
    }

    public void setAllowLateSubmission(final Boolean allowLateSubmission) {
        getAttributes().getBooleanAttribute(ATTR_ALLOW_LATE_SUBMISSION).setValue(allowLateSubmission);
    }


    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        if (getMinimum() != null && getMinimum().doubleValue() < 0) {
            context.fireValidationError(this, "Minimum time cannot be negative");
        }

        if (getMaximum() != null && getMaximum().doubleValue() < 0) {
            context.fireValidationError(this, "Maximum time cannot be negative");
        }
    }
}
