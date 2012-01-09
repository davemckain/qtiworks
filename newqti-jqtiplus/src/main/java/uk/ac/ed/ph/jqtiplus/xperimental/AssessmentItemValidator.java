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
package uk.ac.ed.ph.jqtiplus.xperimental;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectResolver;
import uk.ac.ed.ph.jqtiplus.resolution.BadResourceException;
import uk.ac.ed.ph.jqtiplus.resolution.ReferenceResolver;
import uk.ac.ed.ph.jqtiplus.resolution.ResolutionResult;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ValidationWarning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates an {@link AssessmentItem}, pulling in the appropriate {@link ResponseProcessing} template
 * if required.
 * 
 * @author David McKain
 */
public final class AssessmentItemValidator implements ItemValidationContext {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentItemValidator.class);

    private final AssessmentItem item;
    private final AssessmentObjectResolver objectResolver;
    private ResponseProcessing resolvedResponseProcessingTemplate;

    public AssessmentItemValidator(final AssessmentItem item, final ReferenceResolver referenceResolver) {
        this.item = item;
        this.objectResolver = new AssessmentObjectResolver(referenceResolver);
    }

    public ValidationResult validate() {
        logger.info("Validating item {}", item);
        final ValidationResult result = new ValidationResult(item);
        
        /* First resolve response processing template if no rules have been specified */
        ResponseProcessing responseProcessing = item.getResponseProcessing();
        if (responseProcessing.getResponseRules().isEmpty()) {
            /* No ResponseRules, so we assume that there must be a RP template, which we'll resolve */
            resolveResponseProcessingTemplate(result);
        }
        
        /* Now validate item */
        item.validate(this, result);
        return result;
    }
    
    private void resolveResponseProcessingTemplate(ValidationResult result) {
        ResolutionResult<ResponseProcessing> resolutionResult;
        try {
            resolutionResult = objectResolver.resolveResponseProcessingTemplate(item);
            if (resolutionResult!=null) {
                /* Successful resolution - first record it */
                result.addResolutionResult(resolutionResult);
                resolvedResponseProcessingTemplate = resolutionResult.getResolvedQtiObject();
            }
            else {
                /* No template supplied */
                result.add(new ValidationWarning(item, "responseProcessing should either contain some rules, or declare a template or templateLocation"));
            }
        }
        catch (ResourceNotFoundException e) {
            result.add(new ValidationError(item, "Could not find responseProcessing template", e));
        }
        catch (BadResourceException e) {
            result.add(new ValidationError(item, "Target of responseProcessing template was not a responseProcessing class", e));
        }
    }

    @Override
    public AssessmentItem getItem() {
        return item;
    }

    @Override
    public AssessmentObject getOwner() {
        return item;
    }
    
    @Override
    public ResponseProcessing getResolvedResponseProcessingTemplate() {
        return resolvedResponseProcessingTemplate;
    }
    
    @Override
    public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
        VariableDeclaration declaration = null;
        final Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();

        /* (In Items, we only allow local references) */
        if (localIdentifier != null) {
            declaration = item.getVariableDeclaration(localIdentifier);
        }
        return declaration;
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(item=" + item
                + ",objectResolver=" + objectResolver
                + ")";
    }
}
