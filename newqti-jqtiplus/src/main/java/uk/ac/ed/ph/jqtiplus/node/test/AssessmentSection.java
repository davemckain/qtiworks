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
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.test.OrderingGroup;
import uk.ac.ed.ph.jqtiplus.group.test.RubricBlockGroup;
import uk.ac.ed.ph.jqtiplus.group.test.SectionPartGroup;
import uk.ac.ed.ph.jqtiplus.group.test.SelectionGroup;
import uk.ac.ed.ph.jqtiplus.node.content.variable.RubricBlock;


import java.util.List;

/**
 * Sections group together individual item references and/or sub-sections.
 * 
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public class AssessmentSection extends SectionPart {
    
    private static final long serialVersionUID = 371468215845203409L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "assessmentSection";

    /** Name of title attribute in xml schema. */
    public static final String ATTR_TITLE_NAME = "title";

    /** Name of visible attribute in xml schema. */
    public static final String ATTR_VISIBLE_NAME = "visible";

    /** Name of keepTogether attribute in xml schema. */
    public static final String ATTR_KEEP_TOGETHER_NAME = "keepTogether";
    /** Default value of keepTogether attribute. */
    public static final boolean ATTR_KEEP_TOGETHER_DEFAULT_VALUE = true;

    /**
     * Constructs section.
     *
     * @param parent parent of constructed section
     */
    public AssessmentSection(AbstractPart parent)
    {
        super(parent);

        getAttributes().add(new StringAttribute(this, ATTR_TITLE_NAME));
        getAttributes().add(new BooleanAttribute(this, ATTR_VISIBLE_NAME));
        getAttributes().add(new BooleanAttribute(this, ATTR_KEEP_TOGETHER_NAME, ATTR_KEEP_TOGETHER_DEFAULT_VALUE));

        getNodeGroups().add(new SelectionGroup(this));
        getNodeGroups().add(new OrderingGroup(this));
        getNodeGroups().add(new RubricBlockGroup(this));
        getNodeGroups().add(new SectionPartGroup(this));
    }

    @Override
    public String getClassTag()
    {
        return CLASS_TAG;
    }

    @Override
    public List<SectionPart> getChildren()
    {
        return getSectionParts();
    }

    /**
     * Gets value of title attribute.
     *
     * @return value of title attribute
     * @see #setTitle
     */
    public String getTitle()
    {
        return getAttributes().getStringAttribute(ATTR_TITLE_NAME).getValue();
    }

    /**
     * Sets new value of title attribute.
     *
     * @param title new value of title attribute
     * @see #getTitle
     */
    public void setTitle(String title)
    {
        getAttributes().getStringAttribute(ATTR_TITLE_NAME).setValue(title);
    }

    /**
     * Gets value of visible attribute.
     *
     * @return value of visible attribute
     * @see #setVisible
     */
    public Boolean getVisible()
    {
        return getAttributes().getBooleanAttribute(ATTR_VISIBLE_NAME).getValue();
    }

    /**
     * Sets new value of visible attribute.
     *
     * @param visible new value of visible attribute
     * @see #getVisible
     */
    public void setVisible(Boolean visible)
    {
        getAttributes().getBooleanAttribute(ATTR_VISIBLE_NAME).setValue(visible);
    }

    /**
     * Gets value of keepTogether attribute.
     *
     * @return value of keepTogether attribute
     * @see #setKeepTogether
     */
    public Boolean getKeepTogether()
    {
        return getAttributes().getBooleanAttribute(ATTR_KEEP_TOGETHER_NAME).getValue();
    }

    /**
     * Sets new value of keepTogether attribute.
     *
     * @param keepTogether new value of keepTogether attribute
     * @see #getKeepTogether
     */
    public void setKeepTogether(Boolean keepTogether)
    {
        getAttributes().getBooleanAttribute(ATTR_KEEP_TOGETHER_NAME).setValue(keepTogether);
    }

    /**
     * Gets selection child.
     *
     * @return selection child
     * @see #setSelection
     */
    public Selection getSelection()
    {
        return getNodeGroups().getSelectionGroup().getSelection();
    }

    /**
     * Sets new selection child.
     *
     * @param selection new selection child
     * @see #getSelection
     */
    public void setSelection(Selection selection)
    {
        getNodeGroups().getSelectionGroup().setSelection(selection);
    }

    /**
     * Gets ordering child.
     *
     * @return ordering child
     * @see #setOrdering
     */
    public Ordering getOrdering()
    {
        return getNodeGroups().getOrderingGroup().getOrdering();
    }

    /**
     * Sets new ordering child.
     *
     * @param ordering new ordering child
     * @see #getOrdering
     */
    public void setOrdering(Ordering ordering)
    {
        getNodeGroups().getOrderingGroup().setOrdering(ordering);
    }

    /**
     * Gets rubricBlock children.
     *
     * @return rubricBlock children
     */
    public List<RubricBlock> getRubricBlocks()
    {
        return getNodeGroups().getRubricBlockGroup().getRubricBlocks();
    }

    /**
     * Gets sectionPart children.
     *
     * @return sectionPart children
     */
    public List<SectionPart> getSectionParts()
    {
        return getNodeGroups().getSectionPartGroup().getSectionParts();
    }
}
