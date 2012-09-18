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

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.provision.RootNodeProvider;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentObject;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup;
import uk.ac.ed.ph.jqtiplus.resolution.VariableResolutionException;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;

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

    private final RootNodeProvider rootNodeProvider;

    public AssessmentObjectValidator(final RootNodeProvider resourceProvider) {
        this.rootNodeProvider = resourceProvider;
    }

    public ItemValidationResult validateItem(final ResolvedAssessmentItem resolvedAssessmentItem) {
        Assert.ensureNotNull(resolvedAssessmentItem);
        if (resolvedAssessmentItem.getModelRichness()!=ModelRichness.FOR_VALIDATION) {
            throw new IllegalArgumentException("ReeolvedAssessmentItem must have modelRichness " + ModelRichness.FOR_VALIDATION);
        }
        logger.debug("Validating {}", resolvedAssessmentItem);
        final ItemValidationResult result = new ItemValidationResult(resolvedAssessmentItem);
        final AssessmentItem item = resolvedAssessmentItem.getItemLookup().extractIfSuccessful();
        if (item!=null) {
            final RootNodeLookup<ResponseProcessing> resolvedResponseProcessingTemplate = resolvedAssessmentItem.getResolvedResponseProcessingTemplateLookup();
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
        Assert.ensureNotNull(resolvedAssessmentTest);
        if (resolvedAssessmentTest.getModelRichness()!=ModelRichness.FOR_VALIDATION) {
            throw new IllegalArgumentException("ReeolvedAssessmentTest must have modelRichness " + ModelRichness.FOR_VALIDATION);
        }
        logger.debug("Validating {}", resolvedAssessmentTest);
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
                                + " has errors. Please see the attached validation result for this item for further debugrmation."));
                    }
                    if (itemValidationResult.hasWarnings()) {
                        result.add(new ValidationError(test, messageBuilder.toString()
                                + " has warnings. Please see the attached validation result for this item for further debugrmation."));
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
            this.subject = resolvedAssessmentObject.getRootNodeLookup().extractAssumingSuccessful();
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
        public final VariableDeclaration checkVariableReference(final QtiNode owner, final Identifier variableDeclarationIdentifier) {
            try {
                return resolvedAssessmentObject.resolveVariableReference(variableDeclarationIdentifier);
            }
            catch (final VariableResolutionException e) {
                validationResult.add(new ValidationError(owner, e.getMessage()));
                return null;
            }
        }

        @Override
        public final VariableDeclaration checkVariableReference(final QtiNode owner, final VariableReferenceIdentifier variableReferenceIdentifier) {
            try {
                return resolvedAssessmentObject.resolveVariableReference(variableReferenceIdentifier);
            }
            catch (final VariableResolutionException e) {
                validationResult.add(new ValidationError(owner, e.getMessage()));
                return null;
            }
        }

        @Override
        public boolean checkVariableType(final QtiNode owner, final VariableDeclaration variableDeclaration, final VariableType... requiredTypes) {
            Assert.ensureNotNull(variableDeclaration);
            boolean result;
            if (variableDeclaration.isType(requiredTypes)) {
                result = true;
            }
            else {
                final StringBuilder messageBuilder = new StringBuilder("Variable ")
                    .append(variableDeclaration)
                    .append(" must be a ");
                for (int i=0; i<requiredTypes.length; i++) {
                    messageBuilder.append(requiredTypes[i].getName())
                        .append(i < requiredTypes.length-1 ? ", " : " or ");
                }
                messageBuilder.append(" variable but is a ")
                    .append(variableDeclaration.getVariableType().getName())
                    .append(" variable");
                validationResult.add(new ValidationError(owner, messageBuilder.toString()));
                result = false;
            }
            return result;
        }

        @Override
        public boolean checkBaseType(final QtiNode owner, final VariableDeclaration variableDeclaration, final BaseType... requiredTypes) {
            Assert.ensureNotNull(variableDeclaration);
            boolean result;
            final BaseType baseType = variableDeclaration.getBaseType();
            if (baseType!=null && baseType.isOneOf(requiredTypes)) {
                result = true;
            }
            else {
                final StringBuilder messageBuilder = new StringBuilder("Variable ")
                    .append(variableDeclaration)
                    .append(" must have baseType ");
                for (int i=0; i<requiredTypes.length; i++) {
                    messageBuilder.append(requiredTypes[i].toQtiString())
                        .append(i < requiredTypes.length-1 ? ", " : " or ");
                }
                if (baseType!=null) {
                    messageBuilder.append(" but has baseType ")
                        .append(variableDeclaration.getBaseType().toQtiString());
                }
                validationResult.add(new ValidationError(owner, messageBuilder.toString()));
                result = false;
            }
            return result;
        }

        @Override
        public boolean checkCardinality(final QtiNode owner, final VariableDeclaration variableDeclaration, final Cardinality... requiredTypes) {
            Assert.ensureNotNull(variableDeclaration);
            boolean result;
            if (variableDeclaration.getCardinality().isOneOf(requiredTypes)) {
                result = true;
            }
            else {
                final StringBuilder messageBuilder = new StringBuilder("Variable ")
                    .append(variableDeclaration)
                    .append(" must have cardinality ");
                for (int i=0; i<requiredTypes.length; i++) {
                    messageBuilder.append(requiredTypes[i].toQtiString())
                        .append(i < requiredTypes.length-1 ? ", " : " or ");
                }
                messageBuilder.append(" but has cardinality ")
                    .append(variableDeclaration.getCardinality().toQtiString());
                validationResult.add(new ValidationError(owner, messageBuilder.toString()));
                result = false;
            }
            return result;
        }
    }

    final class ItemValidationContextImpl extends AbstractValidationContextImpl<AssessmentItem> {

        ItemValidationContextImpl(final ItemValidationResult validationResult, final ResolvedAssessmentItem resolvedAssessmentItem) {
            super(validationResult, resolvedAssessmentItem);
        }

        @Override
        public JqtiExtensionManager getJqtiExtensionManager() {
            return rootNodeProvider.getJqtiExtensionManager();
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
        public JqtiExtensionManager getJqtiExtensionManager() {
            return rootNodeProvider.getJqtiExtensionManager();
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
                + "(resourceProvider=" + rootNodeProvider
                + ")";
    }
}
