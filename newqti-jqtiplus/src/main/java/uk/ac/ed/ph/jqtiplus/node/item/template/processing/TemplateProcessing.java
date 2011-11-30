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

package uk.ac.ed.ph.jqtiplus.node.item.template.processing;

import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.exception.QTIProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.group.item.template.processing.TemplateRuleGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractObject;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;

import java.util.List;


/**
 * Template processing takes place each time the candidate submits the responses for an item (when in individual submission mode)
 * or A group of items (when in simultaneous submission mode). It happens after any (item level) response processing triggered
 * by the submission. The values of the test's outcome variables are always reset to their defaults prior to carrying out the
 * instructions described by the outcomeRules. Because outcome processing happens each time the candidate submits responses the
 * resulting values of the test-level outcomes may be used to activate test-level feedback during the test or to control the behaviour
 * of subsequent parts through the use of preConditions and branchRules.
 * 
 * @author Jonathon Hare
 */
public class TemplateProcessing extends AbstractObject
{
    private static final long serialVersionUID = 1L;
    
    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "templateProcessing";

    /**
     * Constructs object.
     *
     * @param parent parent of this object
     */
    public TemplateProcessing(AssessmentItem parent)
    {
        super(parent);

        getNodeGroups().add(new TemplateRuleGroup(this));
    }

    @Override
    public String getClassTag()
    {
        return CLASS_TAG;
    }

    /**
     * Gets templateRule children.
     *
     * @return templateRule children
     */
    public List<TemplateRule> getTemplateRules()
    {
        return getNodeGroups().getTemplateRuleGroup().getTemplateRules();
    }

    @Override
    protected ValidationResult validateChildren(ValidationContext context)
    {
        ValidationResult result = super.validateChildren(context);

        if (getTemplateRules().size() == 0)
            result.add(new ValidationWarning(this, "Node " + CLASS_TAG + " should contain some rules."));

        return result;
    }

    /**
     * Evaluates all child templateRules.
     * @param context TODO
     */
    public void evaluate(ProcessingContext context)
    {
        try {
        for (TemplateRule templateRule : getTemplateRules())
            templateRule.evaluate(context);
        } catch (QTIProcessingInterrupt interrupt) {
            //do nothing
        }
    }
}
