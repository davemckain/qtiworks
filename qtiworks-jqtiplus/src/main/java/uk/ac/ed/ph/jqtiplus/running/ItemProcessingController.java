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
package uk.ac.ed.ph.jqtiplus.running;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.exception.QtiCandidateStateException;
import uk.ac.ed.ph.jqtiplus.exception.QtiInvalidLookupException;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.item.CorrectResponse;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.SetCorrectResponse;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationController;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Random;

/**
 * Implementation of {@link ItemProcessingContext}, filling in the low level
 * logic for running items. You probably want to use {@link ItemSessionController}
 * for higher level stuff.
 * <p>
 * Usage: an instance of this class may only be used by a single Thread.
 *
 * @see ItemSessionController
 * @see TestProcessingController
 *
 * @author David McKain
 */
public class ItemProcessingController extends ItemValidationController implements ItemProcessingContext, InteractionBindingContext {

    protected final ItemProcessingMap itemProcessingMap;
    protected final ItemSessionState itemSessionState;

    private Long randomSeed;
    private Random randomGenerator;

    public ItemProcessingController(final JqtiExtensionManager jqtiExtensionManager,
            final ItemProcessingMap itemProcessingMap, final ItemSessionState itemSessionState) {
        super(jqtiExtensionManager, itemProcessingMap!=null ? itemProcessingMap.getResolvedAssessmentItem() : null);
        Assert.notNull(itemProcessingMap, "itemProcessingMap");
        Assert.notNull(itemSessionState, "itemSessionState");
        this.itemProcessingMap = itemProcessingMap;
        this.itemSessionState = itemSessionState;
        this.randomSeed = null;
        this.randomGenerator = null;
    }

    @Override
    public ItemSessionState getItemSessionState() {
        return itemSessionState;
    }

    @Override
    public boolean isSubjectValid() {
        return itemProcessingMap.isValid();
    }

    //-------------------------------------------------------------------

    public Long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(final Long randomSeed) {
        this.randomSeed = randomSeed;
        this.randomGenerator = null;
    }

    @Override
    public Random getRandomGenerator() {
        if (randomGenerator==null) {
            randomGenerator = randomSeed!=null ? new Random(randomSeed) : new Random();
        }
        return randomGenerator;
    }

    //-------------------------------------------------------------------
    // Interaction binding callbacks

    @Override
    public final void bindResponseVariable(final Identifier responseIdentifier, final Value value) {
        itemSessionState.setUncommittedResponseValue(responseIdentifier, value);
    }

    //-------------------------------------------------------------------

    @Override
    public final VariableDeclaration ensureVariableDeclaration(final Identifier identifier, final VariableType... permittedTypes) {
        Assert.notNull(identifier);
        final VariableDeclaration result = getVariableDeclaration(identifier, permittedTypes);
        if (result==null) {
            throw new QtiInvalidLookupException(identifier);
        }
        return result;
    }

    private VariableDeclaration getVariableDeclaration(final Identifier identifier, final VariableType... permittedTypes) {
        Assert.notNull(identifier);
        VariableDeclaration result = null;
        if (permittedTypes.length==0) {
            /* No types specified, so allow any variable */
            result = itemProcessingMap.getValidTemplateDeclarationMap().get(identifier);
            if (result==null) {
                result = itemProcessingMap.getValidResponseDeclarationMap().get(identifier);
            }
            if (result==null) {
                result = itemProcessingMap.getValidOutcomeDeclarationMap().get(identifier);
            }
        }
        else {
            /* Only allows specified types of variables */
            CHECK_LOOP: for (final VariableType type : permittedTypes) {
                switch (type) {
                    case TEMPLATE:
                        result = itemProcessingMap.getValidTemplateDeclarationMap().get(identifier);
                        break;

                    case RESPONSE:
                        result = itemProcessingMap.getValidResponseDeclarationMap().get(identifier);
                        break;

                    case OUTCOME:
                        result = itemProcessingMap.getValidOutcomeDeclarationMap().get(identifier);
                        break;

                    default:
                        throw new QtiLogicException("Unexpected switch case: " + type);
                }
                if (result!=null) {
                    break CHECK_LOOP;
                }
            }
        }
        return result;
    }

    //-------------------------------------------------------------------

    @Override
    public final Value evaluateVariableValue(final VariableDeclaration variableDeclaration) {
        Assert.notNull(variableDeclaration);
        return evaluateVariableValue(variableDeclaration.getIdentifier());
    }

    @Override
    public final Value evaluateVariableValue(final Identifier identifier, final VariableType... permittedTypes) {
        Assert.notNull(identifier);
        if (!itemProcessingMap.isValidVariableIdentifier(identifier)) {
            throw new QtiInvalidLookupException(identifier);
        }
        Value result = null;
        if (permittedTypes.length==0) {
            /* No types specified, so allow any variable */
            result = evaluateTemplateValue(identifier);
            if (result==null) {
                result = evaluateOutcomeValue(identifier);
                if (result==null) {
                    result = evaluateResponseValue(identifier);
                }
            }
        }
        else {
            /* Only allows specified types of variables */
            CHECK_LOOP: for (final VariableType type : permittedTypes) {
                switch (type) {
                    case TEMPLATE:
                        result = evaluateTemplateValue(identifier);
                        break;

                    case RESPONSE:
                        result = evaluateResponseValue(identifier);
                        break;

                    case OUTCOME:
                        result = evaluateOutcomeValue(identifier);
                        break;

                    default:
                        throw new QtiLogicException("Unexpected switch case: " + type);
                }
                if (result!=null) {
                    break CHECK_LOOP;
                }
            }
        }
        if (result==null) {
            throw new QtiCandidateStateException("ItemSessionState lookup of variable " + identifier + " returned NULL, indicating state is not in sync");
        }
        return result;
    }

    private Value evaluateTemplateValue(final Identifier identifier) {
        return itemSessionState.getTemplateValue(identifier);
    }

    private Value evaluateResponseValue(final Identifier identifier) {
        if (identifier.equals(QtiConstants.VARIABLE_DURATION_IDENTIFIER)) {
            return itemSessionState.computeDurationValue();
        }
        else if (identifier.equals(QtiConstants.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER)) {
            return itemSessionState.getNumAttemptsValue();
        }
        return itemSessionState.getResponseValue(identifier);
    }

    private Value evaluateOutcomeValue(final Identifier identifier) {
        if (identifier.equals(QtiConstants.VARIABLE_COMPLETION_STATUS_IDENTIFIER)) {
            return itemSessionState.getCompletionStatusValue();
        }
        return itemSessionState.getOutcomeValue(identifier);
    }

    //-------------------------------------------------------------------

    @Override
    public final void setVariableValue(final VariableDeclaration variableDeclaration, final Value value) {
        Assert.notNull(variableDeclaration);
        Assert.notNull(value);
        final Identifier identifier = variableDeclaration.getIdentifier();
        if (VariableDeclaration.isReservedIdentifier(identifier)) {
            if (QtiConstants.VARIABLE_COMPLETION_STATUS_IDENTIFIER.equals(identifier)) {
                /* It is legal to set completionStatus */
                if (value.hasBaseType(BaseType.IDENTIFIER)) {
                    itemSessionState.setCompletionStatus(value.toQtiString());
                }
                else {
                    throw new IllegalArgumentException("Variable " + QtiConstants.VARIABLE_COMPLETION_STATUS_NAME
                            + " must be set to an identifier value");
                }
            }
            else {
                /* Other reserved variable may not be set */
                throw new IllegalArgumentException("The reserved variable with " + identifier + " may not be explicitly set");
            }
        }
        else {
            if (variableDeclaration instanceof TemplateDeclaration) {
                itemSessionState.setTemplateValue(identifier, value);
            }
            else if (variableDeclaration instanceof ResponseDeclaration) {
                itemSessionState.setResponseValue(identifier, value);
            }
            else if (variableDeclaration instanceof OutcomeDeclaration) {
                itemSessionState.setOutcomeValue(identifier, value);
            }
            else {
                throw new QtiLogicException("Unexpected logic branch");
            }
        }
    }

    //-------------------------------------------------------------------

    /**
     * Computes the current default value of the variable having the
     * given {@link Identifier}. The result will be not null (though may be a {@link NullValue}).
     *
     * @param identifier identifier of the required variable, which must not be null
     * @return computed default value, which will not be null.
     *
     * @throws QtiInvalidLookupException
     */
    @Override
    public final Value computeDefaultValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return computeDefaultValue(ensureVariableDeclaration(identifier));
    }

    /**
     * Computes the current default value of the given {@link VariableDeclaration}.
     * The result will be not null (though may be a {@link NullValue}).
     *
     * @param declaration declaration of the required variable, which must not be null.
     * @return computed default value, which will not be null.
     */
    public final Value computeDefaultValue(final VariableDeclaration declaration) {
        Assert.notNull(declaration);
        Value result = itemSessionState.getOverriddenDefaultValue(declaration);
        if (result==null) {
            final DefaultValue defaultValue = declaration.getDefaultValue();
            if (defaultValue != null) {
                result = defaultValue.evaluate();
            }
            else if (declaration.isType(VariableType.OUTCOME) && declaration.hasSignature(Signature.SINGLE_INTEGER)) {
                result = IntegerValue.ZERO;
            }
            else if (declaration.isType(VariableType.OUTCOME) && declaration.hasSignature(Signature.SINGLE_FLOAT)) {
                result = FloatValue.ZERO;
            }
            else {
                result = NullValue.INSTANCE;
            }
        }
        return result;
    }

    /**
     * Computes the current correct response for the {@link ResponseDeclaration} having the
     * given {@link Identifier}. The result will be null if there is no {@link CorrectResponse}
     * for this {@link ResponseDeclaration} or no overridden response has been set, otherwise a non-null {@link Value}.
     *
     * @param identifier identifier of the required variable, which must not be null
     * @return computed correct response value or null
     *
     * @throws QtiInvalidLookupException
     */
    @Override
    public final Value computeCorrectResponse(final Identifier identifier) {
        Assert.notNull(identifier);
        return computeCorrectResponse((ResponseDeclaration) ensureVariableDeclaration(identifier, VariableType.RESPONSE));
    }

    /**
     * Computes the current correct response for the given {@link ResponseDeclaration}.
     * The result will be null if there is no {@link CorrectResponse}
     * for this {@link ResponseDeclaration}, otherwise a non-null {@link Value}.
     *
     * @param declaration {@link ResponseDeclaration} to test, which must not be null
     * @return computed correct response value or null
     */
    public final Value computeCorrectResponse(final ResponseDeclaration declaration) {
        Assert.notNull(declaration);
        Value result = itemSessionState.getOverriddenCorrectResponseValue(declaration);
        if (result==null) {
            final CorrectResponse correctResponse = declaration.getCorrectResponse();
            if (correctResponse != null) {
                result = correctResponse.evaluate();
            }
        }
        return result;
    }

    /**
     * Returns whether a correct response has been set for the given {@link ResponseDeclaration},
     * either having been set via {@link SetCorrectResponse} or via an explicit
     * {@link CorrectResponse}.
     *
     * @param declaration {@link ResponseDeclaration} to test, which must not be null
     * @return whether a correct response has been set
     */
    public final boolean hasCorrectResponse(final ResponseDeclaration declaration) {
        Assert.notNull(declaration);
        return declaration.getCorrectResponse()!=null
                || itemSessionState.getOverriddenCorrectResponseValue(declaration)!=null;
    }

    /**
     * Returns whether the current response value for the given {@link ResponseDeclaration}
     * matches the currently correct response set for it. Returns false if there is no
     * correct response set.
     * <p>
     * NOTE: This only tests for "the" "correct" response, not "a" correct response.
     *
     * @return true if the associated correctResponse matches the value; false otherwise.
     */
    private boolean isCorrectResponse(final ResponseDeclaration responseDeclaration) {
        final Value correctResponseValue = computeCorrectResponse(responseDeclaration);
        if (correctResponseValue==null) {
            return false;
        }
        final Value currentResponseValue = evaluateVariableValue(responseDeclaration);
        return currentResponseValue.equals(correctResponseValue);
    }

    /**
     * Returns whether ALL response variables have their current value equal to their current
     * correct response value.
     * <p>
     * NOTE: Remember that this only makes sense if the item uses {@link CorrectResponse}
     * or {@link SetCorrectResponse}.
     *
     * @see #isIncorrect
     */
    @Override
    public final boolean isCorrect() {
        for (final ResponseDeclaration responseDeclaration : itemProcessingMap.getValidResponseDeclarationMap().values()) {
            if (!hasCorrectResponse(responseDeclaration)) {
                return false;
            }
            if (!isCorrectResponse(responseDeclaration)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether SOME response variables does not have their current value equal to their current
     * correct response value (or has no correct response set).
     * <p>
     * NOTE: Remember that this only makes sense if the item uses {@link CorrectResponse}
     * or {@link SetCorrectResponse}.
     *
     * @see #isIncorrect
     */
    @Override
    public final boolean isIncorrect() {
        for (final ResponseDeclaration responseDeclaration : itemProcessingMap.getValidResponseDeclarationMap().values()) {
            if (!hasCorrectResponse(responseDeclaration)) {
                return true;
            }
            if (!isCorrectResponse(responseDeclaration)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the number of correct responses, as judged by
     * {@link #isCorrectResponse(ResponseDeclaration)}.
     *
     * @see #isCorrectResponse(ResponseDeclaration)
     */
    public final int countCorrect() {
        int count = 0;
        for (final ResponseDeclaration responseDeclaration : itemProcessingMap.getValidResponseDeclarationMap().values()) {
            if (isCorrectResponse(responseDeclaration)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of correct responses, as judged by
     * {@link #isCorrectResponse(ResponseDeclaration)}.
     *
     * @see #isCorrectResponse(ResponseDeclaration)
     */
    public final int countIncorrect() {
        int count = 0;
        for (final ResponseDeclaration responseDeclaration : itemProcessingMap.getValidResponseDeclarationMap().values()) {
            if (!isCorrectResponse(responseDeclaration)) {
                count++;
            }
        }
        return count;
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(resolvedAssessmentItem=" + resolvedAssessmentItem
                + ",itemSessionState=" + itemSessionState
                + ")";
    }
}
