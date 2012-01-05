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
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Document this
 * 
 * @author David McKain
 */
public final class AssessmentTestValidator implements TestValidationContext {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentTestValidator.class);

    private final AssessmentTest test;

    private final ReferenceResolver resolver;

    public AssessmentTestValidator(final AssessmentTest test, final ReferenceResolver resolver) {
        this.test = test;
        this.resolver = resolver;
    }

    public ValidationResult validate() {
        logger.info("Validating test {}", test);
        final ValidationResult result = new ValidationResult(test);
        test.validate(this, result);
        return result;
    }

    @Override
    public AssessmentObject getOwner() {
        return test;
    }

    @Override
    public AssessmentTest getTest() {
        return test;
    }

    @Override
    public AssessmentItemValidator resolveItem(AssessmentItemRef assessmentItemRef) throws ReferencingException {
        final ResolutionResult<AssessmentItem> resolved = resolver.resolve(test, assessmentItemRef.getHref(), AssessmentItem.class);
        if (resolved.getJQTIObject() == null) {
            throw new ReferencingException("Could not resolve referenced item with href " + assessmentItemRef);
        }
        final AssessmentItem item = resolved.getJQTIObject();
        return new AssessmentItemValidator(item, resolver);
    }

    @Override
    public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier)
            throws ReferencingException {
        VariableDeclaration declaration = null;
        final Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();

        /* (In tests, we allow both local and item references) */
        if (localIdentifier != null) {
            /* Referring to another test variable */
            declaration = test.getVariableDeclaration(localIdentifier);
        }
        else {
            /* It's a special ITEM.VAR reference */
            final Identifier itemRefIdentifier = variableReferenceIdentifier.getAssessmentItemRefIdentifier();
            final Identifier itemVarIdentifier = variableReferenceIdentifier.getAssessmentItemItemVariableIdentifier();
            final AssessmentItemRef itemRef = test.lookupItemRef(itemRefIdentifier);
            if (itemRef != null) {
                final AssessmentItem item = resolveItem(itemRef).getItem();
                declaration = item.getVariableDeclaration(itemRef.resolveVariableMapping(itemVarIdentifier));
            }
        }
        return declaration;
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(test=" + test
                + ",resolver=" + resolver
                + ")";
    }
}
