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
package uk.ac.ed.ph.jqtiplus.node.item.template.processing;

import uk.ac.ed.ph.jqtiplus.exception.TemplateProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateElseGroup;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateElseIfGroup;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateIfGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of if-elseif-else template rule (behaviour is same like in other programming languages).
 * <p>
 * If the expression given in the templateIf or templateElseIf evaluates to true then the sub-rules contained within it are followed and any following
 * templateElseIf or templateElse parts are ignored for this template condition.
 * <p>
 * If the expression given in the templateIf or templateElseIf does not evaluate to true then consideration passes to the next templateElseIf or, if there are
 * no more templateElseIf parts then the sub-rules of the templateElse are followed (if specified).
 *
 * @author Jonathon Hare
 */
public final class TemplateCondition extends TemplateRule {

    private static final long serialVersionUID = 5066193969135526272L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "templateCondition";

    public TemplateCondition(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getNodeGroups().add(new TemplateIfGroup(this));
        getNodeGroups().add(new TemplateElseIfGroup(this));
        getNodeGroups().add(new TemplateElseGroup(this));
    }

    /**
     * Gets IF child.
     *
     * @return IF child
     * @see #setTemplateIf
     */
    public TemplateIf getTemplateIf() {
        return getNodeGroups().getTemplateIfGroup().getTemplateIf();
    }

    /**
     * Sets new IF child.
     *
     * @param templateIf new IF child
     * @see #getTemplateIf
     */
    public void setTemplateIf(final TemplateIf templateIf) {
        getNodeGroups().getTemplateIfGroup().setTemplateIf(templateIf);
    }

    /**
     * Gets ELSE-IF children.
     *
     * @return ELSE-IF children
     */
    public List<TemplateElseIf> getTemplateElseIfs() {
        return getNodeGroups().getTemplateElseIfGroup().getTemplateElseIfs();
    }

    /**
     * Gets ELSE child.
     *
     * @return ELSE child
     * @see #setTemplateElse
     */
    public TemplateElse getTemplateElse() {
        return getNodeGroups().getTemplateElseGroup().getTemplateElse();
    }

    /**
     * Sets new ELSE child.
     *
     * @param templateElse new ELSE child
     * @see #getTemplateElse
     */
    public void setTemplateElse(final TemplateElse templateElse) {
        getNodeGroups().getTemplateElseGroup().setTemplateElse(templateElse);
    }

    /**
     * Gets all children (IF, ELSE-IF, ELSE) in one ordered list.
     *
     * @return all children (IF, ELSE-IF, ELSE) in one ordered list
     */
    private List<TemplateConditionChild> getConditionChildren() {
        final List<TemplateConditionChild> children = new ArrayList<TemplateConditionChild>();

        children.add(getTemplateIf());
        children.addAll(getTemplateElseIfs());
        if (getTemplateElse() != null) {
            children.add(getTemplateElse());
        }

        return children;
    }

    @Override
    public void evaluate(final ItemProcessingContext context) throws TemplateProcessingInterrupt {
        for (final TemplateConditionChild child : getConditionChildren()) {
            if (child.evaluate(context)) {
                return;
            }
        }
    }
}
