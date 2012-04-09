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
package uk.ac.ed.ph.jqtiplus.validation;

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.provision.RootObjectProvider;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentObject;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.RootObjectLookup;
import uk.ac.ed.ph.jqtiplus.resolution.VariableResolutionException;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;

import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates a {@link ResolvedAssessmentItem} or {@link ResolvedAssessmentTest}.
 *
 * @see AssessmentObjectManager
 *
 * @author David McKain
 */
public final class AssessmentObjectValidator {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentObjectValidator.class);

    private final RootObjectProvider resourceProvider;

    public AssessmentObjectValidator(final RootObjectProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    public ItemValidationResult validateItem(final ResolvedAssessmentItem resolvedAssessmentItem) {
        ConstraintUtilities.ensureNotNull(resolvedAssessmentItem);
        if (resolvedAssessmentItem.getModelRichness()!=ModelRichness.FOR_VALIDATION) {
            throw new IllegalArgumentException("ReeolvedAssessmentItem must have modelRichness " + ModelRichness.FOR_VALIDATION);
        }
        logger.info("Validating {}", resolvedAssessmentItem);
        final ItemValidationResult result = new ItemValidationResult(resolvedAssessmentItem);
        final AssessmentItem item = resolvedAssessmentItem.getItemLookup().extractIfSuccessful();
        if (item!=null) {
            final RootObjectLookup<ResponseProcessing> resolvedResponseProcessingTemplate = resolvedAssessmentItem.getResolvedResponseProcessingTemplateLookup();
            if (resolvedResponseProcessingTemplate!=null && !resolvedResponseProcessingTemplate.wasSuccessful()) {
                result.add(new ValidationError(item.getResponseProcessing(), "Resolution of ResponseProcessing template failed. Further details are attached elsewhere."));
            }
            item.validate(new ItemValidationContextImpl(result, resolvedAssessmentItem));
        }
        else {
            result.add(new ValidationError(null, "AssessmentItem was not successfully instantiated"));
        }
        return result;
    }

    public TestValidationResult validateTest(final ResolvedAssessmentTest resolvedAssessmentTest) {
        ConstraintUtilities.ensureNotNull(resolvedAssessmentTest);
        if (resolvedAssessmentTest.getModelRichness()!=ModelRichness.FOR_VALIDATION) {
            throw new IllegalArgumentException("ReeolvedAssessmentTest must have modelRichness " + ModelRichness.FOR_VALIDATION);
        }
        logger.info("Validating {}", resolvedAssessmentTest);
        final TestValidationResult result = new TestValidationResult(resolvedAssessmentTest);
        final AssessmentTest test = resolvedAssessmentTest.getTestLookup().extractIfSuccessful();
        if (test!=null) {
            /* Validate each unique item first */
            for (final Entry<URI, ResolvedAssessmentItem> entry : resolvedAssessmentTest.getResolvedAssessmentItemMap().entrySet()) {
                final URI itemSystemId = entry.getKey();
                final ResolvedAssessmentItem itemHolder = entry.getValue();
                final StringBuilder messageBuilder = new StringBuilder("Referenced item at System ID ")
                    .append(itemSystemId)
                    .append(" referenced by identifiers ");
                final List<AssessmentItemRef> itemRefs = resolvedAssessmentTest.getItemRefsBySystemIdMap().get(itemSystemId);
                for (int i=0,size=itemRefs.size(); i<size; i++) {
                    messageBuilder.append(itemRefs.get(i).getIdentifier());
                    messageBuilder.append((i<size-1) ? ", " : " and ");
                }

                if (itemHolder.getItemLookup().wasSuccessful()) {
                    final ItemValidationResult itemValidationResult = validateItem(itemHolder);
                    result.addItemValidationResult(itemValidationResult);
                    if (itemValidationResult.hasErrors()) {
                        result.add(new ValidationError(test, messageBuilder.toString()
                                + " has errors. Please see the attached validation result for this item for further information."));
                    }
                    if (itemValidationResult.hasWarnings()) {
                        result.add(new ValidationError(test, messageBuilder.toString()
                                + " has warnings. Please see the attached validation result for this item for further information."));
                    }
                }
                else {
                    result.add(new ValidationError(test, messageBuilder.toString()
                            + " was not successfully instantiated. Further details are attached elsewhere."));
                }
            }

            /* Then validate the test itself */
            test.validate(new TestValidationContextImpl(result, resolvedAssessmentTest));
        }
        else {
            result.add(new ValidationError(null, "Provision of AssessmentTest failed"));
        }
        return result;
    }

    //-------------------------------------------------------------------

    abstract class AbstractValidationContextImpl<E extends AssessmentObject> implements ValidationContext {

        protected final AbstractValidationResult validationResult;
        protected final ResolvedAssessmentObject<E> resolvedAssessmentObject;
        protected final E subject;

        AbstractValidationContextImpl(final AbstractValidationResult validationResult, final ResolvedAssessmentObject<E> resolvedAssessmentObject) {
            this.validationResult = validationResult;
            this.resolvedAssessmentObject = resolvedAssessmentObject;
            this.subject = resolvedAssessmentObject.getRootObjectLookup().extractAssumingSuccessful();
        }

        @Override
        public void add(final ValidationItem item) {
            validationResult.add(item);
        }

        @Override
        public final AbstractValidationResult getValidationResult() {
            return validationResult;
        }

        @Override
        public final ResolvedAssessmentObject<E> getResolvedAssessmentObject() {
            return resolvedAssessmentObject;
        }

        @Override
        public final AssessmentObject getSubject() {
            return subject;
        }

        @Override
        public final VariableDeclaration checkVariableReference(final XmlNode source, final Identifier variableDeclarationIdentifier, final VariableType... allowedTypes) {
            VariableDeclaration result = null;
            try {
                final VariableDeclaration declaration = resolvedAssessmentObject.resolveVariableReference(variableDeclarationIdentifier);
                if (declaration.isType(allowedTypes)) {
                    result = declaration;
                }
                else {
                    final StringBuilder messageBuilder = new StringBuilder("Variable with identifier ")
                        .append(variableDeclarationIdentifier)
                        .append(" is a ")
                        .append(declaration.getVariableType().getName())
                        .append(" variable but must be a ");
                    for (int i=0; i<allowedTypes.length; i++) {
                        messageBuilder.append(allowedTypes[i].getName())
                            .append(i < allowedTypes.length-1 ? ", " : " or ");
                    }
                    messageBuilder.append("variable");
                    validationResult.add(new ValidationError(source, messageBuilder.toString()));
                }

            }
            catch (final VariableResolutionException e) {
                validationResult.add(new ValidationError(source, e.getMessage()));
            }
            return result;
        }

        @Override
        public final VariableDeclaration checkVariableReference(final XmlNode source, final VariableReferenceIdentifier variableReferenceIdentifier, final VariableType... allowedTypes) {
            VariableDeclaration result = null;
            try {
                final VariableDeclaration declaration = resolvedAssessmentObject.resolveVariableReference(variableReferenceIdentifier);
                if (declaration.isType(allowedTypes)) {
                    result = declaration;
                }
                else {
                    final StringBuilder messageBuilder = new StringBuilder("Variable referenced as ")
                        .append(variableReferenceIdentifier)
                        .append(" is a ")
                        .append(declaration.getVariableType().getName())
                        .append(" variable but must be a ");
                    for (int i=0; i<allowedTypes.length; i++) {
                        messageBuilder.append(allowedTypes[i].getName())
                            .append(i < allowedTypes.length-1 ? ", " : " or ");
                    }
                    messageBuilder.append("variable");
                    validationResult.add(new ValidationError(source, messageBuilder.toString()));
                }

            }
            catch (final VariableResolutionException e) {
                validationResult.add(new ValidationError(source, e.getMessage()));
            }
            return result;
        }
    }

    final class ItemValidationContextImpl extends AbstractValidationContextImpl<AssessmentItem> {

        ItemValidationContextImpl(final ItemValidationResult validationResult, final ResolvedAssessmentItem resolvedAssessmentItem) {
            super(validationResult, resolvedAssessmentItem);
        }

        @Override
        public ResolvedAssessmentItem getResolvedAssessmentItem() {
            return (ResolvedAssessmentItem) resolvedAssessmentObject;
        }

        @Override
        public ResolvedAssessmentTest getResolvedAssessmentTest() {
            throw fail();
        }

        @Override
        public boolean isValidatingItem() {
            return true;
        }

        @Override
        public boolean isValidatingTest() {
            return false;
        }

        @Override
        public AssessmentItem getSubjectItem() {
            return subject;
        }

        @Override
        public AssessmentTest getSubjectTest() {
            throw fail();
        }

        private QtiLogicException fail() {
            return new QtiLogicException("Current ValidationContext is for an item, not a test");
        }
    }



    final class TestValidationContextImpl extends AbstractValidationContextImpl<AssessmentTest> {

        TestValidationContextImpl(final TestValidationResult result, final ResolvedAssessmentTest resolvedAssessmentTest) {
            super(result, resolvedAssessmentTest);
        }

        @Override
        public ResolvedAssessmentItem getResolvedAssessmentItem() {
            throw fail();
        }

        @Override
        public ResolvedAssessmentTest getResolvedAssessmentTest() {
            return (ResolvedAssessmentTest) resolvedAssessmentObject;
        }


        @Override
        public boolean isValidatingItem() {
            return false;
        }

        @Override
        public boolean isValidatingTest() {
            return true;
        }

        @Override
        public AssessmentItem getSubjectItem() {
            throw fail();
        }

        @Override
        public AssessmentTest getSubjectTest() {
            return subject;
        }

        private QtiLogicException fail() {
            return new QtiLogicException("Current ValidationContext is for a test, not an item");
        }
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(resourceProvider=" + resourceProvider
                + ")";
    }
}
