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
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Document this
 * 
 * TODO: This should keep track of resolution results and make them available. Validation should
 * now run AFTER resolution, as it's cleaner and simpler that way!
 * 
 * @author David McKain
 */
public final class AssessmentItemHolder implements ItemValidationContext {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentItemHolder.class);

    private final AssessmentItem item;
    private final ReferenceResolver referenceResolver;

    private ResponseProcessing resolvedResponseProcessing;

    public AssessmentItemHolder(final AssessmentItem item, final ReferenceResolver resolver) {
        this.item = item;
        this.referenceResolver = resolver;
        this.resolvedResponseProcessing = null;
    }

    public ValidationResult validate() {
        logger.info("Validating item {}", item);
        final ValidationResult result = new ValidationResult(item);
        item.validate(this, result);
        return result;
    }

    @Override
    public AssessmentItem getItem() {
        return item;
    }

    @Override
    public AssessmentObject getOwner() {
        return item;
    }

    private ResponseProcessing resolveResponseProcessing() throws ReferencingException {
        final ResponseProcessing responseProcessing = item.getResponseProcessing();
        if (responseProcessing==null) {
            /* No responseProcessing */
            return null;
        }
        if (!responseProcessing.getResponseRules().isEmpty()) {
            /* ResponseProcessing contains rules */
            return responseProcessing;
        }
        ResolutionResult<ResponseProcessing> resolutionResult = null;
        final List<URI> attemptedUris = new ArrayList<URI>();
        URI templateUri = responseProcessing.getTemplate();
        if (templateUri != null) {
            attemptedUris.add(templateUri);
            resolutionResult = referenceResolver.resolve(item, templateUri, ResponseProcessing.class);
        }
        if (resolutionResult == null || resolutionResult.getQtiObject() == null) {
            templateUri = responseProcessing.getTemplateLocation();
            if (templateUri != null) {
                attemptedUris.add(templateUri);
                resolutionResult = referenceResolver.resolve(item, templateUri, ResponseProcessing.class);
            }
        }
        if (resolutionResult == null || resolutionResult.getQtiObject() == null) {
            throw new ReferencingException("Could not obtain responseProcessing template from URI(s) " + attemptedUris);
        }
        logger.info("Resolved responseProcessing template using href {} to {}", templateUri, resolutionResult);
        return resolutionResult.getQtiObject();
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
                + ",referenceResolver=" + referenceResolver
                + ")";
    }
}
