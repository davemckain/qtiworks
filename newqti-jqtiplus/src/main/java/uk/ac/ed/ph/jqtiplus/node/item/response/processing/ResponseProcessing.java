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
package uk.ac.ed.ph.jqtiplus.node.item.response.processing;


import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QTIProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseRuleGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.RootObject;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.validation.AbstractValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.xperimental.control.ProcessingContext;

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
public class ResponseProcessing extends AbstractNode implements RootObject {

    private static final long serialVersionUID = -4551768580135824154L;

    /** Name of this class in xml schema. */
    public static final String CLASS_TAG = "responseProcessing";

    /** Name of template attribute in xml schema. */
    public static final String ATTR_TEMPLATE_NAME = "template";

    /** Name of templateLocation attribute in xml schema. */
    public static final String ATTR_TEMPLATE_LOCATION_NAME = "templateLocation";

    private URI systemId;
    private ModelRichness modelRichness;

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
    public String getClassTag() {
        return CLASS_TAG;
    }

    @Override
    public URI getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(URI systemId) {
        this.systemId = systemId;
    }
    

    @Override
    public ModelRichness getModelRichness() {
        return modelRichness;
    }
    
    @Override
    public void setModelRichness(ModelRichness modelRichness) {
        this.modelRichness = modelRichness;
    }

    /**
     * Gets responseRule children.
     * 
     * @return responseRule children
     */
    public List<ResponseRule> getResponseRules() {
        return getNodeGroups().getResponseRuleGroup().getResponseRules();
    }

    /**
     * Gets the value of the template attribute.
     * 
     * @return Value of the template attribute
     */
    public URI getTemplate() {
        return getAttributes().getUriAttribute(ATTR_TEMPLATE_NAME).getValue();
    }

    /**
     * Gets the value of the templateLocation attribute.
     * 
     * @return Value of the templateLocation attribute
     */
    public URI getTemplateLocation() {
        return getAttributes().getUriAttribute(ATTR_TEMPLATE_LOCATION_NAME).getValue();
    }

    @Override
    protected void validateChildren(ValidationContext context, AbstractValidationResult result) {
        List<ResponseRule> responseRules = getResponseRules();
        if (!responseRules.isEmpty()) {
            /* ResponseRules exist, so we'll validate these */
            super.validateChildren(context, result);
        }
        else {
            /* No ResponseRules, so we'll use any template that will have been resolved for us by caller */
            ItemValidationContext itemContext = (ItemValidationContext) context;
            ResponseProcessing resolvedResponseProcessingTemplate = itemContext.getResolvedResponseProcessingTemplate();
            if (resolvedResponseProcessingTemplate!=null) {
                resolvedResponseProcessingTemplate.validate(context, result);
            }
        }
    }

    /**
     * Evaluates all child outcomeRules.
     * 
     * @throws RuntimeValidationException
     */
    public void evaluate(ProcessingContext context) throws RuntimeValidationException {
        try {
            for (final ResponseRule responseRule : getResponseRules()) {
                responseRule.evaluate(context);
            }
        }
        catch (final QTIProcessingInterrupt interrupt) {
            //do nothing
        }
    }

    @Override
    public String toString() {
        return super.toString()
                + "(systemId=" + systemId
                + ",modelRichness=" + modelRichness
                + ")";
    }
}
