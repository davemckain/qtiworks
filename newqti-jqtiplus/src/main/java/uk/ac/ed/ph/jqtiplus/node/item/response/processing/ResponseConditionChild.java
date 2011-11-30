/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.node.item.response.processing;

import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.exception.QTIProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseRuleGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractObject;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;

import java.util.List;


/**
 * Abstract parent for all responseCondition children (IF, ELSE-IF, ELSE).
 * 
 * @author Jonathon Hare
 */
public abstract class ResponseConditionChild extends AbstractObject
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs condition rule child.
     *
     * @param parent parent of this condition rule child
     */
    public ResponseConditionChild(ResponseCondition parent)
    {
        super(parent);

        getNodeGroups().add(new ResponseRuleGroup(this));
    }

    /**
     * Gets responseRule children.
     *
     * @return responseRule children
     */
    public List<ResponseRule> getResponseRules()
    {
        return getNodeGroups().getResponseRuleGroup().getResponseRules();
    }

    @Override
    protected ValidationResult validateChildren(ValidationContext context)
    {
        ValidationResult result = super.validateChildren(context);

        if (getResponseRules().size() == 0)
            result.add(new ValidationWarning(this, "Node " + getClassTag() + " should contain some rules."));

        return result;
    }

    /**
     * Evaluates all child responseRules and returns true.
     * @param context TODO
     *
     * @return true
     * @throws QTIProcessingInterrupt 
     */
    public boolean evaluate(ProcessingContext context) throws QTIProcessingInterrupt
    {
        for (ResponseRule responseRule : getResponseRules())
            responseRule.evaluate(context);

        return true;
    }
}
