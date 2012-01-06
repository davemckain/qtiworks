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
package uk.ac.ed.ph.jqtiplus.resolution;

import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Document this
 * 
 * @author David McKain
 */
public final class AssessmentObjectResolver {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentObjectResolver.class);

    private final ReferenceResolver referenceResolver;

    public AssessmentObjectResolver(final ReferenceResolver referenceResolver) {
        this.referenceResolver = referenceResolver;
    }
    
    public ResolutionResult<ResponseProcessing> resolveResponseProcessingTemplate(AssessmentItem item)
            throws ResourceNotFoundException, BadResultException {
        logger.info("Resolving responseProcessing template for item {}", item);
        final ResponseProcessing responseProcessing = item.getResponseProcessing();
        if (responseProcessing==null) {
            /* No response processing */
            logger.warn("No responseProcessing present");
            return null;
        }
        
        /* Sanity check (but OK) */
        if (!responseProcessing.getResponseRules().isEmpty()) {
            logger.warn("responseProcessing contains responseRules, so it is probably a waste of time to try to resolve templates!");
        }
        
        /* We will resolve @template first, then @templateLocation */
        ResolutionResult<ResponseProcessing> result = null;
        if (responseProcessing.getTemplate() != null) {
            result = referenceResolver.resolve(item, responseProcessing.getTemplate(), ResponseProcessing.class);
            logger.info("Resolved responseProcessing template for item {} using template attribute to {}", item, result);
            return result;
        }
        if (responseProcessing.getTemplateLocation() != null) {
            result = referenceResolver.resolve(item, responseProcessing.getTemplateLocation(), ResponseProcessing.class);
            logger.info("Resolved responseProcessing template for item {} using templateLocation attribute to {}", item, result);
            return result;
        }
        logger.warn("No template or templateLocation attribute on responseProcessing for item {} so nothing to resolve", item);
        return null;
    }
    
    public ResolutionResult<AssessmentItem> resolveItem(AssessmentItemRef itemRef)
            throws ResourceNotFoundException, BadResultException {
        logger.info("Resolving AssessmentItem for ref {}", itemRef);
        final AssessmentTest test = itemRef.getRootNode(AssessmentTest.class);
        
        ResolutionResult<AssessmentItem> result = referenceResolver.resolve(test, itemRef.getHref(), AssessmentItem.class);
        logger.info("Resolved AssessmentItem for ref {} to {}", itemRef, result);
        return result;
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(referenceResolver=" + referenceResolver
                + ")";
    }
}
