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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.accessibility;

import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityInfoGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.group.accessibility.companion.CompanionMaterialsInfoGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.inclusion.InclusionOrderGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.ContentContainer;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.accessibility.companion.CompanionMaterialsInfo;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.InclusionOrder;
import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.content.variable.RubricBlock;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.ModalFeedback;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.TestFeedback;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;

import java.util.List;

/**
 * Holds all of the accessibility metadata related to a given top-level {@link ContentContainer}
 *
 * <p>A given ApipAccessibility may be related to an {@link AssessmentItem}'s {@link ItemBody},
 * {@link ModalFeedback}, one of an {@link AssessmentSection}'s {@link RubricBlock}s, or a
 * {@link TestFeedback} element found within an {@link AssessmentTest} or its {@link TestPart}s.</p>
 *
 * @author Zack Pierce
 */
public class ApipAccessibility extends AbstractNode implements AccessibilityNode {
    private static final long serialVersionUID = 624091309484859804L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "apipAccessibility";

    public ApipAccessibility(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);
        getNodeGroups().add(new CompanionMaterialsInfoGroup(this));
        getNodeGroups().add(new InclusionOrderGroup(this));
        getNodeGroups().add(new AccessibilityInfoGroup(this));
    }

    public CompanionMaterialsInfo getCompanionMaterialsInfo() {
        return getNodeGroups().getCompanionMaterialsInfoGroup().getCompanionMaterialsInfo();
    }

    public InclusionOrder getInclusionOrder() {
        return getNodeGroups().getInclusionOrderGroup().getInclusionOrder();
    }

    public AccessibilityInfo getAccessibilityInfo() {
        return getNodeGroups().getAccessibilityInfoGroup().getAccessibilityInfo();
    }

    public void setAccessibilityInfo(final AccessibilityInfo accessibilityInfo) {
        getNodeGroups().getAccessibilityInfoGroup().setAccessibilityInfo(accessibilityInfo);
    }

    /**
     * A convenience for retrieving the access elements nested within the {@link AccessibilityInfo} child.
     *
     * @return A list of Access Elements, or null if no AccessibilityInfo child exists
     */
    public List<AccessElement> getAccessElements() {
        final AccessibilityInfo accessibilityInfo = getAccessibilityInfo();
        return accessibilityInfo != null ? accessibilityInfo.getAccessElements() : null;
    }

    /**
     * Searches through its ancestors and the root document to find the content container element
     * which this accessibility data is intended to assist.
     *
     * @see ContentContainer
     * @return
     */
    public ContentContainer getRelatedContentContainer() {
        return QueryUtils.findRelatedTopLevelContentContainer(this);
    }
}
