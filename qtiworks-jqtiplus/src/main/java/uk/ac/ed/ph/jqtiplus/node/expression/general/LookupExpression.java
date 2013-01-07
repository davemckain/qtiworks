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
package uk.ac.ed.ph.jqtiplus.node.expression.general;

import uk.ac.ed.ph.jqtiplus.attribute.value.ComplexReferenceIdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedTestVariableReference;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.ComplexReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent of <code>correct</code>, <code>default</code> and <code>variable</code> expression.
 * <p>
 * These ones are fairly complex in that they allow deep references in tests.
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 * @author David McKain (refactored)
 */
public abstract class LookupExpression extends AbstractFunctionalExpression implements TestProcessingContext.DereferencedTestVariableHandler {

    private static final long serialVersionUID = 1803604885120254713L;

    private static final Logger logger = LoggerFactory.getLogger(LookupExpression.class);

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    public LookupExpression(final ExpressionParent parent, final String qtiClassName) {
        super(parent, qtiClassName);
        getAttributes().add(new ComplexReferenceIdentifierAttribute(this, ATTR_IDENTIFIER_NAME, true));
    }

    public ComplexReferenceIdentifier getIdentifier() {
        return getAttributes().getComplexReferenceIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    public void setIdentifier(final ComplexReferenceIdentifier identifier) {
        getAttributes().getComplexReferenceIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    //----------------------------------------------------------------------
    // Validation

    @Override
    protected final void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final ComplexReferenceIdentifier variableReferenceIdentifier = getIdentifier();

        if (context.isSubjectItem()) {
            /* Check reference within this item */
            final VariableDeclaration resolvedDeclaration = context.checkLocalVariableReference(this, variableReferenceIdentifier);

            /* If reference was OK, let subclasses do any further validation as required */
            if (resolvedDeclaration!=null) {
                validateResolvedItemVariableReference(context, resolvedDeclaration);
            }
        }
        else {
            /* Check reference within this test OR within referenced item */
            final TestValidationContext testValidationContext = (TestValidationContext) context;
            final ResolvedTestVariableReference resolvedReference = testValidationContext.checkComplexVariableReference(this, variableReferenceIdentifier);

            /* If reference was OK, let subclasses do any further validation as required */
            if (resolvedReference!=null) {
                validateResolvedTestVariableReference(context, resolvedReference);
            }
        }
    }

    /**
     * Subclasses should implement this to perform additional validation on the resolved
     * {@link VariableDeclaration} within an {@link AssessmentItem}
     */
    protected abstract void validateResolvedItemVariableReference(ValidationContext context,
            VariableDeclaration resolvedDeclaration);

    /**
     * Subclasses should implement this to perform additional validation on the given
     * {@link ResolvedTestVariableReference} within an {@link AssessmentTest}
     */
    protected abstract void validateResolvedTestVariableReference(ValidationContext context,
            ResolvedTestVariableReference resolvedReference);

    //----------------------------------------------------------------------

    @Override
    public final BaseType[] getProducedBaseTypes(final ValidationContext context) {
        final VariableDeclaration declaration = lookupTargetVariableDeclaration(context);
        if (declaration != null && declaration.getBaseType() != null) {
            return new BaseType[] { declaration.getBaseType() };
        }
        return super.getProducedBaseTypes(context);
    }

    @Override
    public final Cardinality[] getProducedCardinalities(final ValidationContext context) {
        final VariableDeclaration declaration = lookupTargetVariableDeclaration(context);
        if (declaration != null) {
            return new Cardinality[] { declaration.getCardinality() };
        }
        return super.getProducedCardinalities(context);
    }

    public final VariableDeclaration lookupTargetVariableDeclaration(final ValidationContext context) {
        final ComplexReferenceIdentifier referenceIdentifier = getIdentifier();

        if (context.isSubjectItem()) {
            return context.isValidLocalVariableReference(referenceIdentifier);
        }
        else {
            /* Check reference within this test OR within referenced item */
            final TestValidationContext testValidationContext = (TestValidationContext) context;
            final ResolvedTestVariableReference resolvedTestVariableReference = testValidationContext.isValidComplexVariableReference(referenceIdentifier);
            return (resolvedTestVariableReference!=null) ? resolvedTestVariableReference.getVariableDeclaration() : null;
        }
    }

    //----------------------------------------------------------------------

    @Override
    protected final Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        logger.debug("{}Evaluation of expression {} on variable {} started.", new Object[] { formatIndent(depth), getQtiClassName(), getIdentifier() });

        final ComplexReferenceIdentifier referenceIdentifier = getIdentifier();
        Value result;
        if (context.isSubjectItem()) {
            /* Variable reference within item */
            final ItemProcessingContext itemProcessingContext = (ItemProcessingContext) context;
            result = evaluateInThisItem(itemProcessingContext, Identifier.assumedLegal(referenceIdentifier.toString()));
        }
        else {
            /* Variable reference in test */
            final TestProcessingContext testProcessingContext = (TestProcessingContext) context;
            result = testProcessingContext.dereferenceVariable(this, referenceIdentifier, this);
        }
        return result;
    }

    protected abstract Value evaluateInThisItem(ItemProcessingContext itemProcessingContext, Identifier itemVariableIdentifier);
}
