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
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.test.OrderingGroup;
import uk.ac.ed.ph.jqtiplus.group.test.RubricBlockGroup;
import uk.ac.ed.ph.jqtiplus.group.test.SectionPartGroup;
import uk.ac.ed.ph.jqtiplus.group.test.SelectionGroup;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.content.variable.RubricBlock;

import java.net.URI;
import java.util.List;

/**
 * Represents the <code>assessmentSection</code> QTI class
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public final class AssessmentSection extends SectionPart implements RootNode {

    private static final long serialVersionUID = 371468215845203409L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "assessmentSection";

    /** Name of title attribute in xml schema. */
    public static final String ATTR_TITLE_NAME = "title";

    /** Name of visible attribute in xml schema. */
    public static final String ATTR_VISIBLE_NAME = "visible";

    /** Name of keepTogether attribute in xml schema. */
    public static final String ATTR_KEEP_TOGETHER_NAME = "keepTogether";

    /** Default value of keepTogether attribute. */
    public static final boolean ATTR_KEEP_TOGETHER_DEFAULT_VALUE = true;

    /** System ID of this RootNode (optional) */
    private URI systemId;

    public AssessmentSection(final AbstractPart parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new StringAttribute(this, ATTR_TITLE_NAME, true));
        getAttributes().add(new BooleanAttribute(this, ATTR_VISIBLE_NAME, true));
        getAttributes().add(new BooleanAttribute(this, ATTR_KEEP_TOGETHER_NAME, ATTR_KEEP_TOGETHER_DEFAULT_VALUE, false));

        getNodeGroups().add(new SelectionGroup(this));
        getNodeGroups().add(new OrderingGroup(this));
        getNodeGroups().add(new RubricBlockGroup(this));
        getNodeGroups().add(new SectionPartGroup(this));
    }

    @Override
    public List<SectionPart> getChildAbstractParts() {
        return getSectionParts();
    }

    public String getTitle() {
        return getAttributes().getStringAttribute(ATTR_TITLE_NAME).getComputedValue();
    }

    public void setTitle(final String title) {
        getAttributes().getStringAttribute(ATTR_TITLE_NAME).setValue(title);
    }


    public boolean getVisible() {
        return getAttributes().getBooleanAttribute(ATTR_VISIBLE_NAME).getComputedNonNullValue();
    }

    public void setVisible(final Boolean visible) {
        getAttributes().getBooleanAttribute(ATTR_VISIBLE_NAME).setValue(visible);
    }


    public boolean getKeepTogether() {
        return getAttributes().getBooleanAttribute(ATTR_KEEP_TOGETHER_NAME).getComputedNonNullValue();
    }

    public void setKeepTogether(final Boolean keepTogether) {
        getAttributes().getBooleanAttribute(ATTR_KEEP_TOGETHER_NAME).setValue(keepTogether);
    }


    public Selection getSelection() {
        return getNodeGroups().getSelectionGroup().getSelection();
    }

    public void setSelection(final Selection selection) {
        getNodeGroups().getSelectionGroup().setSelection(selection);
    }


    public Ordering getOrdering() {
        return getNodeGroups().getOrderingGroup().getOrdering();
    }

    public void setOrdering(final Ordering ordering) {
        getNodeGroups().getOrderingGroup().setOrdering(ordering);
    }


    public List<RubricBlock> getRubricBlocks() {
        return getNodeGroups().getRubricBlockGroup().getRubricBlocks();
    }

    public List<SectionPart> getSectionParts() {
        return getNodeGroups().getSectionPartGroup().getSectionParts();
    }

    @Override
    public URI getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(final URI systemId) {
        this.systemId = systemId;
    }

    @Override
    public String toString() {
        return super.toString()
                + "(systemId=" + systemId
                + ")";
    }
}
