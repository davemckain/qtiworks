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
package uk.ac.ed.ph.jqtiplus.node.item.interaction.choice;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.content.FlowStaticGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;

import java.util.List;

/**
 * simpleChoice is a choice that contains flowStatic objects.
 * A simpleChoice must not contain any nested interactions.
 *
 * @author Jonathon Hare
 */
public final class SimpleChoice extends Choice {

    private static final long serialVersionUID = 5742864616479376297L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "simpleChoice";

    /** Name of ariaControls attribute in xml schema. */
    static final String ATTR_ARIA_CONTROLS_NAME = "aria-controls";

    /** Name of ariaDescribedBy attribute in xml schema. */
    static final String ATTR_ARIA_DESCRIBED_BY_NAME = "aria-describedby";

    /** Name of ariaFlowsTo attribute in xml schema. */
    static final String ATTR_ARIA_FLOWS_TO_NAME = "aria-flowsto";

    /** Name of ariaLabel attribute in xml schema. */
    static final String ATTR_ARIA_LABEL_NAME = "aria-label";

    /** Name of ariaLabelledBy attribute in xml schema. */
    static final String ATTR_ARIA_LABELLED_BY_NAME = "aria-labelledby";

    /** Name of ariaLevel attribute in xml schema. */
    static final String ATTR_ARIA_LEVEL_NAME = "aria-level";

    /** Name of ariaLive attribute in xml schema. */
    static final String ATTR_ARIA_LIVE_NAME = "aria-live";

    /** Name of ariaOrientation attribute in xml schema. */
    static final String ATTR_ARIA_ORIENTATION_NAME = "aria-orientation";

    /** Name of ariaOwns attribute in xml schema. */
    static final String ATTR_ARIA_OWNS_NAME = "aria-owns";

    public SimpleChoice(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getNodeGroups().add(new FlowStaticGroup(this));
        getAttributes().add(new StringAttribute(this, ATTR_ARIA_CONTROLS_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_ARIA_DESCRIBED_BY_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_ARIA_FLOWS_TO_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_ARIA_LABEL_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_ARIA_LABELLED_BY_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_ARIA_LEVEL_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_ARIA_LIVE_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_ARIA_ORIENTATION_NAME, false));
        getAttributes().add(new StringAttribute(this, ATTR_ARIA_OWNS_NAME, false));    }

    public List<FlowStatic> getFlowStatics() {
        return getNodeGroups().getFlowStaticGroup().getFlowStatics();
    }

    public String getAriaControls() {
        return getAttributes().getStringAttribute(ATTR_ARIA_CONTROLS_NAME).getComputedValue();
    }

    public void setAriaControls(final String ariaControls) {
        getAttributes().getStringAttribute(ATTR_ARIA_CONTROLS_NAME).setValue(ariaControls);
    }

    public String getAriaDescribedBy() {
        return getAttributes().getStringAttribute(ATTR_ARIA_DESCRIBED_BY_NAME).getComputedValue();
    }

    public void setAriaDescribedBy(final String ariaDescribedBy) {
        getAttributes().getStringAttribute(ATTR_ARIA_DESCRIBED_BY_NAME).setValue(ariaDescribedBy);
    }

    public String getAriaFlowsTo() {
        return getAttributes().getStringAttribute(ATTR_ARIA_FLOWS_TO_NAME).getComputedValue();
    }

    public void setAriaFlowsTo(final String ariaFlowsTo) {
        getAttributes().getStringAttribute(ATTR_ARIA_FLOWS_TO_NAME).setValue(ariaFlowsTo);
    }

    public String getAriaLabel() {
        return getAttributes().getStringAttribute(ATTR_ARIA_LABEL_NAME).getComputedValue();
    }

    public void setAriaLabel(final String ariaLabel) {
        getAttributes().getStringAttribute(ATTR_ARIA_LABEL_NAME).setValue(ariaLabel);
    }

    public String getAriaLabelledBy() {
        return getAttributes().getStringAttribute(ATTR_ARIA_LABELLED_BY_NAME).getComputedValue();
    }

    public void setAriaLabelledBy(final String ariaLabelledBy) {
        getAttributes().getStringAttribute(ATTR_ARIA_LABELLED_BY_NAME).setValue(ariaLabelledBy);
    }

    public String getAriaLevel() {
        return getAttributes().getStringAttribute(ATTR_ARIA_LEVEL_NAME).getComputedValue();
    }

    public void setAriaLevel(final String ariaLevel) {
        getAttributes().getStringAttribute(ATTR_ARIA_LEVEL_NAME).setValue(ariaLevel);
    }

    public String getAriaLive() {
        return getAttributes().getStringAttribute(ATTR_ARIA_LIVE_NAME).getComputedValue();
    }

    public void setAriaLive(final String ariaLive) {
        getAttributes().getStringAttribute(ATTR_ARIA_LIVE_NAME).setValue(ariaLive);
    }

    public String getAriaOrientation() {
        return getAttributes().getStringAttribute(ATTR_ARIA_ORIENTATION_NAME).getComputedValue();
    }

    public void setAriaOrientation(final String ariaOrientation) {
        getAttributes().getStringAttribute(ATTR_ARIA_ORIENTATION_NAME).setValue(ariaOrientation);
    }

    public String getAriaOwns() {
        return getAttributes().getStringAttribute(ATTR_ARIA_OWNS_NAME).getComputedValue();
    }

    public void setAriaOwns(final String ariaOwns) {
        getAttributes().getStringAttribute(ATTR_ARIA_OWNS_NAME).setValue(ariaOwns);
    }

    /**
     * @deprecated Please now use {@link #getFlowStatics()}
     */
    @Deprecated
    public List<FlowStatic> getChildren() {
        return getFlowStatics();
    }
}
