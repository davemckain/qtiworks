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
package uk.ac.ed.ph.jqtiplus.node.accessibility.related;

import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.LabelledStringGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.SingleIntegerElementGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.accessibility.inclusion.InclusionOrder;

/**
 * Container for the definition of the order in which the guidance is to be presented and the text string used as part of the guidance.
 *
 * @author Zack Pierce
 */
public class GuidanceSupport extends AbstractNode implements AccessibilityNode {

    private static final String ELEM_TEXT_STRING = "textString";
    private static final String ELEM_SUPPORT_ORDER = "supportOrder";

    private static final long serialVersionUID = 3407187991826466361L;

    public GuidanceSupport(final QtiNode parent, final String qtiClassName) {
        super(parent, qtiClassName);
        getNodeGroups().add(new SingleIntegerElementGroup(this, ELEM_SUPPORT_ORDER, true));
        getNodeGroups().add(new LabelledStringGroup(this, ELEM_TEXT_STRING, true));
    }

    public LabelledString getTextString() {
        return getNodeGroups().getLabelledStringGroup(ELEM_TEXT_STRING).getLabelledString();
    }

    public void setTextString(final LabelledString textString) {
        getNodeGroups().getLabelledStringGroup(ELEM_TEXT_STRING).setLabelledString(textString);
    }

    /**
     * The order in which the guidance is to be presented, since guidance accessibility metacontent
     * do not have direct representation in a distinct ElementOrderList within {@link InclusionOrder}
     */
    public Integer getSupportOrder() {
        return getNodeGroups().getSingleIntegerElementGroup(ELEM_SUPPORT_ORDER).getInteger();
    }

    public void setSupportOrder(final Integer supportOrder) {
        getNodeGroups().getSingleIntegerElementGroup(ELEM_SUPPORT_ORDER).setInteger(supportOrder);
    }

}
