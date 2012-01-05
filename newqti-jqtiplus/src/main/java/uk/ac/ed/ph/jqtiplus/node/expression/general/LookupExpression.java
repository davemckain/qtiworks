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
package uk.ac.ed.ph.jqtiplus.node.expression.general;

import uk.ac.ed.ph.jqtiplus.attribute.value.VariableReferenceIdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.control.AssessmentItemRefController;
import uk.ac.ed.ph.jqtiplus.control.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.ItemValidationContext;
import uk.ac.ed.ph.jqtiplus.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.control.TestValidationContext;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.internal.util.Pair;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.ControlObject;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemRefState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.AttributeValidationError;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xperimental.AssessmentItemValidator;
import uk.ac.ed.ph.jqtiplus.xperimental.ReferencingException;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent of correct, default and variable expression.
 * 
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public abstract class LookupExpression extends AbstractExpression {

    private static final long serialVersionUID = 1803604885120254713L;

    private static final Logger logger = LoggerFactory.getLogger(LookupExpression.class);

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    /**
     * Constructs expression.
     * 
     * @param parent parent of this expression
     */
    public LookupExpression(ExpressionParent parent) {
        super(parent);
        getAttributes().add(new VariableReferenceIdentifierAttribute(this, ATTR_IDENTIFIER_NAME));
    }

    /**
     * Gets value of identifier attribute.
     * 
     * @return value of identifier attribute
     * @see #setIdentifier
     */
    public VariableReferenceIdentifier getIdentifier() {
        return getAttributes().getVariableReferenceIdentifierAttribute(ATTR_IDENTIFIER_NAME).getValue();
    }

    /**
     * Sets new value of identifier attribute.
     * 
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    public void setIdentifier(VariableReferenceIdentifier identifier) {
        getAttributes().getVariableReferenceIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    //----------------------------------------------------------------------

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    protected final void validateAttributes(ValidationContext context, ValidationResult result) {
        super.validateAttributes(context, result);
        final VariableReferenceIdentifier variableReferenceIdentifier = getIdentifier();
        final Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();
        if (context instanceof ItemValidationContext) {
            if (localIdentifier != null) {
                final VariableDeclaration declaration = context.getOwner().getVariableDeclaration(localIdentifier);
                if (declaration == null) {
                    result.add(new AttributeValidationError(getAttributes().get(ATTR_IDENTIFIER_NAME),
                            "Cannot find variable declaration " + getIdentifier()));
                }
                validateTargetVariableDeclaration(result, declaration);
            }
            else {
                result.add(new AttributeValidationError(getAttributes().get(ATTR_IDENTIFIER_NAME),
                        "Variable reference '" + getIdentifier() + "' of the form itemRefIdentifier.itemVariableIdentifier may only be used in tests "));
            }
            validateAdditionalAttributes(result, null);
        }
        else {
            if (localIdentifier != null) {
                /* Referring to another test variable */
                final VariableDeclaration declaration = context.getOwner().getVariableDeclaration(localIdentifier);
                if (declaration == null) {
                    result.add(new AttributeValidationError(getAttributes().get(ATTR_IDENTIFIER_NAME),
                            "Cannot find variable declaration " + getIdentifier()));
                }
                validateTargetVariableDeclaration(result, declaration);
                validateAdditionalAttributes(result, null);
            }
            else {
                /* It's a special ITEM.VAR reference */
                /* First resolve the assessmentItemRef */
                final TestValidationContext testContext = (TestValidationContext) context;
                final Identifier itemRefIdentifier = variableReferenceIdentifier.getAssessmentItemRefIdentifier();
                final Identifier itemVarIdentifier = variableReferenceIdentifier.getAssessmentItemItemVariableIdentifier();
                final ControlObject<?> controlObject = testContext.getTest().lookupDescendentOrSelf(itemRefIdentifier);
                if (controlObject == null) {
                    result.add(new AttributeValidationError(getAttributes().get(ATTR_IDENTIFIER_NAME),
                            "Cannot find referenced item with identifier " + itemRefIdentifier));
                }
                else if (!(controlObject instanceof AssessmentItemRef)) {
                    result.add(new AttributeValidationError(getAttributes().get(ATTR_IDENTIFIER_NAME),
                            "Prefix " + itemRefIdentifier + " does not refer to an assessmentItemRef"));
                }
                else {
                    final AssessmentItemRef itemRef = (AssessmentItemRef) controlObject;
                    try {
                        final AssessmentItemValidator assessmentItemValidator = testContext.resolveItem(itemRef);
                        final AssessmentItem item = assessmentItemValidator.getItem();
                        final VariableDeclaration declaration = item.getVariableDeclaration(itemRef.resolveVariableMapping(itemVarIdentifier));
                        if (declaration == null) {
                            result.add(new AttributeValidationError(getAttributes().get(ATTR_IDENTIFIER_NAME),
                                    "Cannot find variable declaration " + itemVarIdentifier + " in item " + itemRefIdentifier));
                        }
                        validateTargetVariableDeclaration(result, declaration);
                        validateAdditionalAttributes(result, itemRef);
                    }
                    catch (final ReferencingException e) {
                        result.add(new AttributeValidationError(getAttributes().get(ATTR_IDENTIFIER_NAME),
                                "Could not resolve referenced item with identifier " + itemRefIdentifier + " and href " + itemRef.getHref()));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    protected void validateTargetVariableDeclaration(ValidationResult result, VariableDeclaration targetVariableDeclaration) {
        /* (Subclasses should override as required to validate the "target" of the variable reference) */
    }

    @SuppressWarnings("unused")
    protected void validateAdditionalAttributes(ValidationResult result, AssessmentItemRef resolvedItemReference) {
        /* (Subclasses should override as required to validate any attributes other than "identifier") */
    }

    //----------------------------------------------------------------------

    @Override
    public BaseType[] getProducedBaseTypes(ValidationContext context) {
        VariableDeclaration declaration;
        try {
            declaration = lookupTargetVariableDeclaration(context);
            if (declaration != null && declaration.getBaseType() != null) {
                return new BaseType[] { declaration.getBaseType() };
            }
        }
        catch (final ReferencingException e) {
        }
        return super.getProducedBaseTypes(context);
    }

    @Override
    public Cardinality[] getProducedCardinalities(ValidationContext context) {
        VariableDeclaration declaration;
        try {
            declaration = lookupTargetVariableDeclaration(context);
            if (declaration != null && declaration.getCardinality() != null) {
                return new Cardinality[] { declaration.getCardinality() };
            }
        }
        catch (final ReferencingException e) {
        }
        return super.getProducedCardinalities(context);
    }

    public VariableDeclaration lookupTargetVariableDeclaration(ValidationContext context)
            throws ReferencingException {
        return context.resolveVariableReference(getIdentifier());
    }

    //----------------------------------------------------------------------

    @Override
    protected final Value evaluateSelf(ProcessingContext context, int depth) {
        logger.debug("{}Evaluation of expression {} on variable {} started.", new Object[] { formatIndent(depth), getClassTag(), getIdentifier() });

        final VariableReferenceIdentifier variableReferenceIdentifier = getIdentifier();
        final Identifier localIdentifier = variableReferenceIdentifier.getLocalIdentifier();
        Value result = null;
        if (context instanceof ItemProcessingContext) {
            /* Refers to a variable within this item */
            final ItemProcessingContext itemContext = (ItemProcessingContext) context;
            result = evaluateInThisItem(itemContext, localIdentifier);
        }
        else {
            final TestProcessingContext testContext = (TestProcessingContext) context;
            if (localIdentifier != null) {
                /* Refers to a variable within this test */
                result = evaluateInThisTest(testContext, localIdentifier);
            }
            else {
                /* It's a special ITEM.VAR reference */
                final Identifier itemRefIdentifier = variableReferenceIdentifier.getAssessmentItemRefIdentifier();
                final Identifier itemVarIdentifier = variableReferenceIdentifier.getAssessmentItemItemVariableIdentifier();
                final Pair<VariableDeclaration, Map<AssessmentItemRefState, AssessmentItemRefController>> resolved = testContext
                        .resolveDottedVariableReference(variableReferenceIdentifier);
                if (resolved == null) {
                    logger.error("{}Cannot find assessmentItemRef with identifier {}. Returning NULL value.", getIndent(depth), itemRefIdentifier);
                }
                else {
                    final Map<AssessmentItemRefState, AssessmentItemRefController> itemRefControllerMap = resolved.getSecond();
                    if (itemRefControllerMap.size() != 1) {
                        logger.error("{}Lookup of variable {} with identifier in assessmentItemRef with identifier {} resulted in {} matches. "
                                + "The '.' notation in QTI only supports assessmentItemRefs that are selected exactly one. Returning NULL value.",
                                new Object[] { getIndent(depth), itemVarIdentifier, itemRefIdentifier, itemRefControllerMap.size() });
                    }
                    else {
                        final AssessmentItemRefController itemRefController = itemRefControllerMap.values().iterator().next();
                        result = evaluateInReferencedItem(depth, itemRefController, itemVarIdentifier);
                    }
                }


            }
        }
        return result != null ? result : NullValue.INSTANCE;
    }

    protected abstract Value evaluateInThisItem(ItemProcessingContext itemContext, Identifier itemVariableIdentifier);

    protected abstract Value evaluateInThisTest(TestProcessingContext testContext, Identifier testVariableIdentifier);

    protected abstract Value evaluateInReferencedItem(int depth, AssessmentItemRefController itemRefController, Identifier itemVariableIdentifier);

    //----------------------------------------------------------------------


    @Override
    public String toString() {
        return getIdentifier() != null ? getIdentifier().toString() : "<NONE>";
    }
}
