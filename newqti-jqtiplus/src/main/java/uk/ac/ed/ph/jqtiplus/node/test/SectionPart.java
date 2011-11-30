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

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;

/**
 * Abstract super class for assessmentSection and assessmentItemRef.
 * 
 * @author Jiri Kajaba
 */
public abstract class SectionPart extends AbstractPart
{
    private static final long serialVersionUID = 1L;

    /** Display name of this part. */
    public static final String DISPLAY_NAME = "sectionPart";

    /** Name of required attribute in xml schema. */
    public static final String ATTR_REQUIRED_NAME = "required";
    /** Default value of required attribute. */
    public static final boolean ATTR_REQUIRED_DEFAULT_VALUE = false;

    /** Name of fixed attribute in xml schema. */
    public static final String ATTR_FIXED_NAME = "fixed";
    /** Default value of fixed attribute. */
    public static final boolean ATTR_FIXED_DEFAULT_VALUE = false;

    /**
     * Constructs part.
     *
     * @param parent parent of constructed part.
     */
    public SectionPart(AbstractPart parent)
    {
        super(parent);

        getAttributes().add(new BooleanAttribute(this, ATTR_REQUIRED_NAME, ATTR_REQUIRED_DEFAULT_VALUE));
        getAttributes().add(new BooleanAttribute(this, ATTR_FIXED_NAME, ATTR_FIXED_DEFAULT_VALUE));
    }

    @Override
    public AbstractPart getParent()
    {
        return (AbstractPart) super.getParent();
    }

    /**
     * Gets parent assessment section of this part or null if parent is test part.
     *
     * @return parent assessment section of this part or null if parent is test part
     */
    public AssessmentSection getParentSection()
    {
        return (getParent() instanceof AssessmentSection) ? (AssessmentSection) getParent() : null;
    }

    /**
     * Gets value of required attribute.
     *
     * @return value of required attribute
     * @see #setRequired
     */
    public Boolean getRequired()
    {
        return getAttributes().getBooleanAttribute(ATTR_REQUIRED_NAME).getValue();
    }

    /**
     * Sets new value of required attribute.
     *
     * @param required new value of required attribute
     * @see #getRequired
     */
    public void setRequired(Boolean required)
    {
        getAttributes().getBooleanAttribute(ATTR_REQUIRED_NAME).setValue(required);
    }

    /**
     * Gets value of fixed attribute.
     *
     * @return value of fixed attribute
     * @see #setFixed
     */
    public Boolean getFixed()
    {
        return getAttributes().getBooleanAttribute(ATTR_FIXED_NAME).getValue();
    }

    /**
     * Sets new value of fixed attribute.
     *
     * @param fixed new value of fixed attribute
     * @see #getFixed
     */
    public void setFixed(Boolean fixed)
    {
        getAttributes().getBooleanAttribute(ATTR_FIXED_NAME).setValue(fixed);
    }

    @Override
    public ItemSessionControl getItemSessionControl()
    {
        ItemSessionControl itemSessionControl = getItemSessionControlNode();
        if (itemSessionControl != null)
            return itemSessionControl;

        SectionPart parentSection = getParentSection();
        if (parentSection != null)
            return parentSection.getItemSessionControl();

        return getParentTestPart().getItemSessionControl();
    }

    @Override
    public boolean isJumpSafeSource()
    {
        AssessmentSection parent = getParentSection();
        if (parent != null)
        {
            if (parent.getOrdering() != null &&
                parent.getOrdering().getShuffle() != null &&
                parent.getOrdering().getShuffle())
            {
                if (!getFixed())
                    return false;
            }

            return parent.isJumpSafeSource();
        }

        return true;
    }

    @Override
    public boolean isJumpSafeTarget()
    {
        AssessmentSection parent = getParentSection();
        if (parent != null)
        {
            if (parent.getSelection() != null)
            {
                if (!getRequired())
                    return false;

                if (parent.getSelection().getWithReplacement() != null && parent.getSelection().getWithReplacement())
                    return false;
            }

            if (parent.getOrdering() != null &&
                parent.getOrdering().getShuffle() != null &&
                parent.getOrdering().getShuffle())
            {
                if (!getFixed())
                    return false;
            }

            return parent.isJumpSafeTarget();
        }

        return true;
    }
}
