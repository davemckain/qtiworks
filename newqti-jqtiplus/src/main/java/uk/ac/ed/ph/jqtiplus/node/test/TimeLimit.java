/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.node.test;


import uk.ac.ed.ph.jqtiplus.attribute.value.DurationAttribute;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.node.AbstractObject;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;

/**
 * In the context of A specific assessmentTest an item, or group of items, may be subject to A time constraint.
 * This specification supports both minimum and maximum time constraints. The controlled time for A single item
 * is simply the duration of the item session as defined by the built-in response variable duration.
 * For assessmentSections, testParts and whole assessmentTests the time limits relate to the durations of all the
 * item sessions plus any other time spent navigating that part of the test. In other words, the time includes time
 * spent in states where no item is being interacted with, such as dedicated navigation screens.
 * <p>
 * Minimum times are applicable to assessmentSections and assessmentItems only when linear navigation mode is in effect.
 * 
 * @author Jiri Kajaba
 */
public class TimeLimit extends AbstractObject
{
    private static final long serialVersionUID = 1L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "timeLimits";

    /** Name of minTime attribute in xml schema. */
    public static final String ATTR_MINIMUM_NAME = "minTime";
    /** Default value of minTime attribute. */
    public static final Double ATTR_MINIMUM_DEFAULT_VALUE = null;

    /** Name of maxTime attribute in xml schema. */
    public static final String ATTR_MAXIMUM_NAME = "maxTime";
    /** Default value of maxTime attribute. */
    public static final Double ATTR_MAXIMUM_DEFAULT_VALUE = null;

    /**
     * Creates object.
     *
     * @param parent parent of this object
     */
    public TimeLimit(ControlObject<?> parent)
    {
        super(parent);

        getAttributes().add(new DurationAttribute(this, ATTR_MINIMUM_NAME, ATTR_MINIMUM_DEFAULT_VALUE));
        getAttributes().add(new DurationAttribute(this, ATTR_MAXIMUM_NAME, ATTR_MAXIMUM_DEFAULT_VALUE));
    }

    @Override
    public String getClassTag()
    {
        return CLASS_TAG;
    }

    /**
     * Gets value of minTime attribute.
     *
     * @return value of minTime attribute
     * @see #setMinimum
     */
    public Double getMinimum()
    {
        return getAttributes().getDurationAttribute(ATTR_MINIMUM_NAME).getValue();
    }

    /**
     * Gets value of minTime attribute in millis or null.
     *
     * @return value of minTime attribute in millis or null
     */
    public Long getMinimumMillis()
    {
        if (getMinimum() == null)
            return null;

        return (long) (getMinimum() * 1000);
    }

    /**
     * Sets new value of minTime attribute.
     *
     * @param minimum new value of minTime attribute
     * @see #getMinimum
     */
    public void setMinimum(Double minimum)
    {
        getAttributes().getDurationAttribute(ATTR_MINIMUM_NAME).setValue(minimum);
    }

    /**
     * Gets value of maxTime attribute.
     *
     * @return value of maxTime attribute
     * @see #setMaximum
     */
    public Double getMaximum()
    {
        return getAttributes().getDurationAttribute(ATTR_MAXIMUM_NAME).getValue();
    }

    /**
     * Gets value of maxTime attribute in millis.
     *
     * @return value of maxTime attribute in millis
     */
    public Long getMaximumMillis()
    {
        if (getMaximum() == null)
            return null;

        return (long) (getMaximum() * 1000);
    }

    /**
     * Sets new value of maxTime attribute.
     *
     * @param maximum new value of maxTime attribute
     * @see #getMaximum
     */
    public void setMaximum(Double maximum)
    {
        getAttributes().getDurationAttribute(ATTR_MAXIMUM_NAME).setValue(maximum);
    }

    @Override
    protected ValidationResult validateAttributes(ValidationContext context)
    {
        ValidationResult result = super.validateAttributes(context);

        if (getMinimum() != null && getMinimum() < 0)
            result.add(new ValidationError(this, "Minimum time cannot be negative."));

        if (getMaximum() != null && getMaximum() < 0)
            result.add(new ValidationError(this, "Maximum time cannot be negative."));

        return result;
    }
}
