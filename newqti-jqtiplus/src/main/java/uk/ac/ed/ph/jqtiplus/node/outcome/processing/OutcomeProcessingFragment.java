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
package uk.ac.ed.ph.jqtiplus.node.outcome.processing;

import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.exception.QTIProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.group.outcome.processing.OutcomeRuleGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;

import java.util.List;

/**
 * An outcomeProcessingFragment is A simple group of outcomeRules which are grouped together in order to allow them
 * to be managed as A separate resource. It should not be used for any other purpose.
 * 
 * @author Jiri Kajaba
 */
public class OutcomeProcessingFragment extends OutcomeRule {

    private static final long serialVersionUID = 4189180798268332071L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "outcomeProcessingFragment";

    /**
     * Constructs rule.
     * 
     * @param parent parent of this rule
     */
    public OutcomeProcessingFragment(XmlNode parent) {
        super(parent);

        getNodeGroups().add(new OutcomeRuleGroup(this));
    }

    @Override
    public String getClassTag() {
        return CLASS_TAG;
    }

    /**
     * Gets outcomeRule children.
     * 
     * @return outcomeRule children
     */
    public List<OutcomeRule> getOutcomeRules() {
        return getNodeGroups().getOutcomeRuleGroup().getOutcomeRules();
    }

    @Override
    protected void validateChildren(ValidationContext context, ValidationResult result) {
        super.validateChildren(context, result);

        if (getOutcomeRules().size() == 0) {
            result.add(new ValidationWarning(this, "Node " + CLASS_TAG + " should contain some rules."));
        }
    }

    @Override
    public void evaluate(ProcessingContext context) throws QTIProcessingInterrupt, RuntimeValidationException {
        for (final OutcomeRule outcomeRule : getOutcomeRules()) {
            outcomeRule.evaluate(context);
        }
    }
}
