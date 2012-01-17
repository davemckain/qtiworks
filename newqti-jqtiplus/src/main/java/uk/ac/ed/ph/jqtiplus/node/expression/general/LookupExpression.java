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
import uk.ac.ed.ph.jqtiplus.internal.util.Pair;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.resolution.VariableResolutionException;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemRefState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;
import uk.ac.ed.ph.jqtiplus.xperimental.control.AssessmentItemRefController;
import uk.ac.ed.ph.jqtiplus.xperimental.control.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.xperimental.control.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.xperimental.control.TestProcessingContext;

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
    protected final void validateAttributes(ValidationContext context) {
        super.validateAttributes(context);
        
        /* Check reference */
        final VariableReferenceIdentifier variableReferenceIdentifier = getIdentifier();
        VariableDeclaration resolvedDeclaration = context.checkVariableReference(this, variableReferenceIdentifier);
        
        /* If reference was OK, let subclasses do any further validation as required */
        if (resolvedDeclaration!=null) {
            validateResolvedVariableReference(context, variableReferenceIdentifier, resolvedDeclaration);
            
        }
    }
    
    protected abstract void validateResolvedVariableReference(ValidationContext context, VariableReferenceIdentifier variableReferenceIdentifier, VariableDeclaration resolvedDeclaration);

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
        catch (VariableResolutionException e) {
            logger.warn("Refactor this:", e);
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
        catch (VariableResolutionException e) {
            logger.warn("Refactor this:", e);
        }
        return super.getProducedCardinalities(context);
    }

    @ToRefactor
    public VariableDeclaration lookupTargetVariableDeclaration(ValidationContext context)
            throws VariableResolutionException {
        return context.getResolvedAssessmentObject().resolveVariableReference(getIdentifier());
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
