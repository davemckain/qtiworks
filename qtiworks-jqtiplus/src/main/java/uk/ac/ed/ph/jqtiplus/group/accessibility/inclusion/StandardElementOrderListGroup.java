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
package uk.ac.ed.ph.jqtiplus.group.accessibility.inclusion;

import uk.ac.ed.ph.jqtiplus.group.ComplexNodeGroup;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.AslDefaultOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.AslOnDemandOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.BrailleDefaultOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.ElementOrderList;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.ElementOrderListType;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.GraphicsOnlyOnDemandOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.InclusionOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.NonVisualDefaultOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.SignedEnglishDefaultOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.SignedEnglishOnDemandOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.TextGraphicsDefaultOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.TextGraphicsOnDemandOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.TextOnlyDefaultOrder;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.TextOnlyOnDemandOrder;

import java.util.Iterator;

/**
 * Group for the set of ElementOrderList members optionally found within an InclusionOrder.
 *
 * @author Zack Pierce
 */
public class StandardElementOrderListGroup extends ComplexNodeGroup<InclusionOrder, ElementOrderList> {

    private static final long serialVersionUID = -7333407901601134922L;

    public StandardElementOrderListGroup(final InclusionOrder parent) {
        super(parent, ElementOrderListType.DISPLAY_NAME, ElementOrderListType.getElementOrderListNames());
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.ph.jqtiplus.group.NodeGroup#create(java.lang.String)
     */
    @Override
    public ElementOrderList create(final String qtiClassName) {
        return ElementOrderListType.getElementOrderListInstance(getParent(), qtiClassName);
    }

    private ElementOrderList getChildWithQtiClassName(final String qtiClassName) {
        for (final Iterator<ElementOrderList> iterator = children.iterator(); iterator.hasNext();) {
            final ElementOrderList elementOrderList = iterator.next();
            if (qtiClassName.equals(elementOrderList.getQtiClassName())) {
                return elementOrderList;
            }
        }
        return null;
    }

    public AslDefaultOrder getAslDefaultOrder() {
        return (AslDefaultOrder) getChildWithQtiClassName(AslDefaultOrder.QTI_CLASS_NAME);
    }

    public AslOnDemandOrder getAslOnDemandOrder() {
        return (AslOnDemandOrder) getChildWithQtiClassName(AslOnDemandOrder.QTI_CLASS_NAME);
    }

    public BrailleDefaultOrder getBrailleDefaultOrder() {
        return (BrailleDefaultOrder) getChildWithQtiClassName(BrailleDefaultOrder.QTI_CLASS_NAME);
    }

    public GraphicsOnlyOnDemandOrder getGraphicsOnlyOnDemandOrder() {
        return (GraphicsOnlyOnDemandOrder) getChildWithQtiClassName(GraphicsOnlyOnDemandOrder.QTI_CLASS_NAME);
    }

    public NonVisualDefaultOrder getNonVisualDefaultOrder() {
        return (NonVisualDefaultOrder) getChildWithQtiClassName(NonVisualDefaultOrder.QTI_CLASS_NAME);
    }

    public SignedEnglishDefaultOrder getSignedEnglishDefaultOrder() {
        return (SignedEnglishDefaultOrder) getChildWithQtiClassName(SignedEnglishDefaultOrder.QTI_CLASS_NAME);
    }

    public SignedEnglishOnDemandOrder getSignedEnglishOnDemandOrder() {
        return (SignedEnglishOnDemandOrder) getChildWithQtiClassName(SignedEnglishOnDemandOrder.QTI_CLASS_NAME);
    }

    public TextGraphicsDefaultOrder getTextGraphicsDefaultOrder() {
        return (TextGraphicsDefaultOrder) getChildWithQtiClassName(TextGraphicsDefaultOrder.QTI_CLASS_NAME);
    }

    public TextGraphicsOnDemandOrder getTextGraphicsOnDemandOrder() {
        return (TextGraphicsOnDemandOrder) getChildWithQtiClassName(TextGraphicsOnDemandOrder.QTI_CLASS_NAME);
    }

    public TextOnlyDefaultOrder getTextOnlyDefaultOrder() {
        return (TextOnlyDefaultOrder) getChildWithQtiClassName(TextOnlyDefaultOrder.QTI_CLASS_NAME);
    }

    public TextOnlyOnDemandOrder getTextOnlyOnDemandOrder() {
        return (TextOnlyOnDemandOrder) getChildWithQtiClassName(TextOnlyOnDemandOrder.QTI_CLASS_NAME);
    }

    // TODO - consider adding setters for standard element order lists?
}
