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
package uk.ac.ed.ph.jqtiplus.node.item.response.processing;

import uk.ac.ed.ph.jqtiplus.exception.QtiProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseRuleGroup;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.util.List;

/**
 * An responseProcessingFragment is a simple group of responseRules which are grouped together in order to allow them
 * to be managed as a separate resource. It should not be used for any other purpose.
 *
 * @author Jonathon Hare
 */
public final class ResponseProcessingFragment extends ResponseRule {

    private static final long serialVersionUID = 3713907544750395437L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "responseProcessingFragment";

    public ResponseProcessingFragment(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);

        getNodeGroups().add(new ResponseRuleGroup(this));
    }

    public List<ResponseRule> getResponseRules() {
        return getNodeGroups().getResponseRuleGroup().getResponseRules();
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        if (getResponseRules().size() == 0) {
            context.fireValidationWarning(this, "Node " + QTI_CLASS_NAME + " should contain some rules.");
        }
    }

    @Override
    public void evaluate(final ItemProcessingContext context) throws QtiProcessingInterrupt {
        for (final ResponseRule responseRule : getResponseRules()) {
            responseRule.evaluate(context);
        }
    }
}
