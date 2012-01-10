/* Copyright (c) 2012, University of Edinburgh.
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

import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.exception2.TemplateProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateRuleGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;

import java.util.List;

/**
 * Abstract parent for all templateCondition children (IF, ELSE-IF, ELSE).
 * 
 * @author Jonathon Hare
 */
public abstract class TemplateConditionChild extends AbstractNode {

    private static final long serialVersionUID = 1073349682487961960L;

    /**
     * Constructs condition rule child.
     * 
     * @param parent parent of this condition rule child
     */
    public TemplateConditionChild(TemplateCondition parent) {
        super(parent);

        getNodeGroups().add(new TemplateRuleGroup(this));
    }

    /**
     * Gets templateRule children.
     * 
     * @return templateRule children
     */
    public List<TemplateRule> getTemplateRules() {
        return getNodeGroups().getTemplateRuleGroup().getTemplateRules();
    }

    @Override
    protected void validateChildren(ValidationContext context, AbstractValidationResult result) {
        super.validateChildren(context, result);

        if (getTemplateRules().size() == 0) {
            result.add(new ValidationWarning(this, "Node " + getClassTag() + " should contain some rules."));
        }
    }

    /**
     * Evaluates all child templateRules and returns true.
     * 
     * @return true
     * @throws RuntimeValidationException
     */
    public boolean evaluate(ProcessingContext context) throws TemplateProcessingInterrupt, RuntimeValidationException {
        for (final TemplateRule templateRule : getTemplateRules()) {
            templateRule.evaluate(context);
        }
        return true;
    }
}
