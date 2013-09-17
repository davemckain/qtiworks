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
package uk.ac.ed.ph.jqtiplus.node.content.variable;

import uk.ac.ed.ph.jqtiplus.attribute.enumerate.ViewMultipleAttribute;
import uk.ac.ed.ph.jqtiplus.group.accessibility.ApipAccessibilityGroup;
import uk.ac.ed.ph.jqtiplus.group.content.BlockGroup;
import uk.ac.ed.ph.jqtiplus.group.item.StylesheetGroup;
import uk.ac.ed.ph.jqtiplus.node.ContentContainer;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.accessibility.ApipAccessibility;
import uk.ac.ed.ph.jqtiplus.node.accessibility.ApipAccessibilityBearer;
import uk.ac.ed.ph.jqtiplus.node.content.basic.AbstractFlowBodyElement;
import uk.ac.ed.ph.jqtiplus.node.content.basic.Block;
import uk.ac.ed.ph.jqtiplus.node.content.basic.SimpleBlock;
import uk.ac.ed.ph.jqtiplus.node.item.Stylesheet;
import uk.ac.ed.ph.jqtiplus.node.test.View;

import java.util.List;

/**
 * A rubric block contains instructions to one or more of the actors.
 *
 * @author Jonathon Hare
 * @author Jiri Kajaba
 * @author Zack Pierce
 */
public final class RubricBlock extends AbstractFlowBodyElement implements SimpleBlock, ContentContainer, ApipAccessibilityBearer {

    private static final long serialVersionUID = -531414949035924206L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "rubricBlock";

    /** Name of view attribute in xml schema. */
    public static final String ATTR_VIEWS_NAME = View.QTI_CLASS_NAME;

    public RubricBlock(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new ViewMultipleAttribute(this, ATTR_VIEWS_NAME, true));

        getNodeGroups().add(new BlockGroup(this));

        getNodeGroups().add(new StylesheetGroup(this));
        getNodeGroups().add(new ApipAccessibilityGroup(this, false));
    }

    public List<View> getViews() {
        return getAttributes().getViewMultipleAttribute(ATTR_VIEWS_NAME).getComputedValue();
    }

    public void setViews(final List<View> value) {
        getAttributes().getViewMultipleAttribute(ATTR_VIEWS_NAME).setValue(value);
    }

    @Override
    public List<Block> getBlocks() {
        return getNodeGroups().getBlockGroup().getBlocks();
    }

    /**
     * Gets mutable list of stylesheet children.
     *
     * @return stylesheet children
     */
    public List<Stylesheet> getStylesheets() {
        return getNodeGroups().getStylesheetGroup().getStylesheets();
    }

    /**
     * Gets apipAccessibility child
     *
     * @return apipAccessibility child
     * @see #setApipAccessibility
     */
    public ApipAccessibility getApipAccessibility() {
        return getNodeGroups().getApipAccessibilityGroup().getApipAccessibility();
    }

    /**
     * Sets apipAccessibility child
     * @param apipAccessibility
     */
    public void setApipAccessibility(final ApipAccessibility apipAccessibility) {
        getNodeGroups().getApipAccessibilityGroup().setApipAccessibility(apipAccessibility);
    }

}
