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

import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.group.item.response.processing.ResponseRuleGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;

import java.net.URI;
import java.util.List;

/**
 * Response processing takes place each time the candidate submits the responses for an item (when in individual submission mode)
 * or a group of items (when in simultaneous submission mode). It happens after any (item level) response processing triggered
 * by the submission. The values of the test's outcome variables are always reset to their defaults prior to carrying out the
 * instructions described by the outcomeRules. Because outcome processing happens each time the candidate submits responses the
 * resulting values of the test-level outcomes may be used to activate test-level feedback during the test or to control the behaviour
 * of subsequent parts through the use of preConditions and branchRules.
 *
 * @author Jonathon Hare
 */
public final class ResponseProcessing extends AbstractNode implements RootNode {

    private static final long serialVersionUID = -4551768580135824154L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "responseProcessing";

    /** Name of template attribute in xml schema. */
    public static final String ATTR_TEMPLATE_NAME = "template";

    /** Name of templateLocation attribute in xml schema. */
    public static final String ATTR_TEMPLATE_LOCATION_NAME = "templateLocation";

    private URI systemId;

    public ResponseProcessing() {
        this(null);
    }

    public ResponseProcessing(final AssessmentItem parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new UriAttribute(this, ATTR_TEMPLATE_NAME, false));
        getAttributes().add(new UriAttribute(this, ATTR_TEMPLATE_LOCATION_NAME, false));

        getNodeGroups().add(new ResponseRuleGroup(this));
    }

    @Override
    public URI getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(final URI systemId) {
        this.systemId = systemId;
    }


    public List<ResponseRule> getResponseRules() {
        return getNodeGroups().getResponseRuleGroup().getResponseRules();
    }

    public URI getTemplate() {
        return getAttributes().getUriAttribute(ATTR_TEMPLATE_NAME).getComputedValue();
    }

    public void setTemplate(final URI value) {
        getAttributes().getUriAttribute(ATTR_TEMPLATE_NAME).setValue(value);
    }


    public URI getTemplateLocation() {
        return getAttributes().getUriAttribute(ATTR_TEMPLATE_LOCATION_NAME).getComputedValue();
    }

    public void setTemplateLocation(final URI value) {
        getAttributes().getUriAttribute(ATTR_TEMPLATE_LOCATION_NAME).setValue(value);
    }


    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final ItemValidationContext itemValidationContext = (ItemValidationContext) context;
        final List<ResponseRule> responseRules = getResponseRules();
        if (!responseRules.isEmpty()) {
            /* ResponseRules exist, so we'll validate these */
            super.validateChildren(context);
        }
        else {
            /* No ResponseRules, so we'll use any template that will have been resolved for us by caller */
            final ResolvedAssessmentItem resolvedAssessmentItem = itemValidationContext.getResolvedAssessmentItem();
            final RootNodeLookup<ResponseProcessing> resolvedResponseProcessingTemplateLookup = resolvedAssessmentItem.getResolvedResponseProcessingTemplateLookup();
            if (resolvedResponseProcessingTemplateLookup!=null && resolvedResponseProcessingTemplateLookup.wasSuccessful()) {
                resolvedResponseProcessingTemplateLookup.extractIfSuccessful().validate(context);
            }
        }
    }

    public void evaluate(final ItemProcessingContext context) {
        try {
            for (final ResponseRule responseRule : getResponseRules()) {
                responseRule.evaluate(context);
            }
        }
        catch (final QtiProcessingInterrupt interrupt) {
            /* Do nothing */
        }
    }

    @Override
    public String toString() {
        return super.toString()
                + "(systemId=" + systemId
                + ")";
    }
}
