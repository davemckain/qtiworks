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


import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.control.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.exception.QTIProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseRuleGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;
import uk.ac.ed.ph.jqtiplus.xperimental.ReferencingException;

import java.net.URI;
import java.util.List;

/**
 * Response processing takes place each time the candidate submits the responses for an item (when in individual submission mode)
 * or A group of items (when in simultaneous submission mode). It happens after any (item level) response processing triggered
 * by the submission. The values of the test's outcome variables are always reset to their defaults prior to carrying out the
 * instructions described by the outcomeRules. Because outcome processing happens each time the candidate submits responses the
 * resulting values of the test-level outcomes may be used to activate test-level feedback during the test or to control the behaviour
 * of subsequent parts through the use of preConditions and branchRules.
 * 
 * @author Jonathon Hare
 */
public class ResponseProcessing extends AbstractNode implements RootNode {
    
    private static final long serialVersionUID = -4551768580135824154L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "responseProcessing";

    /** Name of template attribute in xml schema. */
    public static final String ATTR_TEMPLATE_NAME = "template";

    /** Name of templateLocation attribute in xml schema. */
    public static final String ATTR_TEMPLATE_LOCATION_NAME = "templateLocation";
    
    private URI systemId;

    public ResponseProcessing() {
        this(null);
    }

    /**
     * Constructs object.
     *
     * @param parent parent of this object
     */
    public ResponseProcessing(AssessmentItem parent) {
        super(parent);
        
        getAttributes().add(new UriAttribute(this, ATTR_TEMPLATE_NAME, null, null, false));
        getAttributes().add(new UriAttribute(this, ATTR_TEMPLATE_LOCATION_NAME, null, null, false));

        getNodeGroups().add(new ResponseRuleGroup(this));
    }

    @Override
    public String getClassTag()
    {
        return CLASS_TAG;
    }
    
    public URI getSystemId() {
        return systemId;
    }
    
    public void setSystemId(URI systemId) {
        this.systemId = systemId;
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

    /**
     * Gets the value of the template attribute.
     * @return Value of the template attribute
     */
    public URI getTemplate() {
        return getAttributes().getUriAttribute(ATTR_TEMPLATE_NAME).getValue();
    }

    /**
     * Gets the value of the templateLocation attribute.
     * @return Value of the templateLocation attribute
     */
    public URI getTemplateLocation() {
        return getAttributes().getUriAttribute(ATTR_TEMPLATE_LOCATION_NAME).getValue();
    }
    
    @Override
    protected void validateChildren(ValidationContext context, ValidationResult result) {
        if (getResponseRules().isEmpty()) {
            /* No responseRules */
            if (getTemplate()==null && getTemplateLocation()==null) {
                /* No external template specified */
                result.add(new ValidationWarning(this, "Node " + CLASS_TAG + " should either contain some rules, or declare a template or templateLocation"));
            }
            else {
                /* External template specified, so pull it out and validate down into it */
                ResponseProcessing resolvedResponseProcessing;
                try {
                    resolvedResponseProcessing = ((ItemValidationContext) context).getResolvedResponseProcessing();
                    resolvedResponseProcessing.validate(context, result);
                }
                catch (ReferencingException e) {
                    result.add(new ValidationError(this, "Could not resolve responseProcessing template", e));
                }
            }
        }
        else {
            /* responseRules present, so descend into them */
           super.validateChildren(context, result);
        }
    }

    /**
     * Evaluates all child outcomeRules.
     * 
     * @throws RuntimeValidationException 
     */
    public void evaluate(ProcessingContext context) throws RuntimeValidationException {
        try {
            for (ResponseRule responseRule : getResponseRules()) {
                responseRule.evaluate(context);
            }
        }
        catch (QTIProcessingInterrupt interrupt) {
            //do nothing
        }
    }
}
