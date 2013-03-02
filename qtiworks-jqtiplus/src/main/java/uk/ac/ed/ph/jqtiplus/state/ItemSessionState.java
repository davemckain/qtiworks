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
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Shuffleable;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates the current state of a candidate's item session.
 * <p>
 * An instance of this class is mutable, but you must let JQTI+ perform all
 * mutation operations for you via the {@link ItemSessionController}.
 * <p>
 * An instance of this class is NOT safe for use by multiple threads.
 *
 * @see ItemSessionController
 *
 * @author David McKain
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class ItemSessionState implements Serializable {

    private static final long serialVersionUID = -7586529679289092485L;

    /**
     * Map of interaction choice orders, for all {@link Shuffleable} interactions.
     * This is keyed on the interaction's responseIdentifier.
     */
    private final Map<Identifier, List<Identifier>> shuffledInteractionChoiceOrders;

    private final Map<Identifier, ResponseData> rawResponseDataMap;
    private final Set<Identifier> unboundResponseIdentifiers;
    private final Set<Identifier> invalidResponseIdentifiers;

    private final Map<Identifier, Value> overriddenTemplateDefaultValues;
    private final Map<Identifier, Value> overriddenResponseDefaultValues;
    private final Map<Identifier, Value> overriddenOutcomeDefaultValues;
    private final Map<Identifier, Value> overriddenCorrectResponseValues;

    private final Map<Identifier, Value> templateValues;
    private final Map<Identifier, Value> responseValues;
    private final Map<Identifier, Value> outcomeValues;

    private SessionStatus sessionStatus;

    private boolean initialized;

    /** Has item been presented? */
    private boolean presented;

    private boolean responded;
    private boolean closed;
    private String candidateComment;

    public ItemSessionState() {
        this.initialized = false;
        this.shuffledInteractionChoiceOrders = new HashMap<Identifier, List<Identifier>>();
        this.rawResponseDataMap = new HashMap<Identifier, ResponseData>();
        this.unboundResponseIdentifiers = new HashSet<Identifier>();
        this.invalidResponseIdentifiers = new HashSet<Identifier>();
        this.overriddenTemplateDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenResponseDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenOutcomeDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenCorrectResponseValues = new HashMap<Identifier, Value>();
        this.templateValues = new HashMap<Identifier, Value>();
        this.responseValues = new HashMap<Identifier, Value>();
        this.outcomeValues = new HashMap<Identifier, Value>();
        this.sessionStatus = null;

        /* Set built-in variables */
        resetBuiltinVariables();
    }

    //----------------------------------------------------------------

    public void reset() {
        this.initialized = false;
        this.presented = false;
        this.responded = false;
        this.closed = false;
        this.candidateComment = null;
        this.rawResponseDataMap.clear();
        this.unboundResponseIdentifiers.clear();
        this.invalidResponseIdentifiers.clear();
        this.shuffledInteractionChoiceOrders.clear();
        this.overriddenTemplateDefaultValues.clear();
        this.overriddenResponseDefaultValues.clear();
        this.overriddenOutcomeDefaultValues.clear();
        this.overriddenCorrectResponseValues.clear();
        this.templateValues.clear();
        this.responseValues.clear();
        this.outcomeValues.clear();
        this.sessionStatus = SessionStatus.INITIAL;
        resetBuiltinVariables();
    }

    public void resetBuiltinVariables() {
        setDuration(0);
        setNumAttempts(0);
        setCompletionStatus(AssessmentItem.VALUE_ITEM_IS_NOT_ATTEMPTED);
    }

    //----------------------------------------------------------------
    // Interaction init helpers

    public Map<Identifier, List<Identifier>> getShuffledInteractionChoiceOrders() {
        return Collections.unmodifiableMap(shuffledInteractionChoiceOrders);
    }

    public List<Identifier> getShuffledInteractionChoiceOrder(final Identifier responseIdentifier) {
        Assert.notNull(responseIdentifier);
        return shuffledInteractionChoiceOrders.get(responseIdentifier);
    }

    public List<Identifier> getShuffledInteractionChoiceOrder(final Interaction interaction) {
        Assert.notNull(interaction);
        return getShuffledInteractionChoiceOrder(interaction.getResponseIdentifier());
    }

    public void setShuffledInteractionChoiceOrder(final Identifier responseIdentifier, final List<Identifier> shuffleOrders) {
        Assert.notNull(responseIdentifier);
        if (shuffleOrders == null || shuffleOrders.isEmpty()) {
            shuffledInteractionChoiceOrders.remove(responseIdentifier);
        }
        else {
            shuffledInteractionChoiceOrders.put(responseIdentifier, shuffleOrders);
        }
    }

    public void setShuffledInteractionChoiceOrder(final Interaction interaction, final List<Identifier> shuffleOrders) {
        Assert.notNull(interaction);
        setShuffledInteractionChoiceOrder(interaction.getResponseIdentifier(), shuffleOrders);
    }

    //----------------------------------------------------------------

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }


    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(final SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }


    public boolean isPresented() {
        return presented;
    }

    public void setPresented(final boolean presented) {
        this.presented = presented;
    }


    public boolean isResponded() {
        return responded;
    }

    public void setResponded(final boolean responded) {
        this.responded = responded;
    }


    public boolean isRespondedValidly() {
        return isResponded() && unboundResponseIdentifiers.isEmpty() && invalidResponseIdentifiers.isEmpty();
    }

    public boolean isRespondedInvalidly() {
        return isResponded() && !(unboundResponseIdentifiers.isEmpty() && invalidResponseIdentifiers.isEmpty());
    }


    public boolean isClosed() {
        return closed;
    }

    public void setClosed(final boolean closed) {
        this.closed = closed;
    }


    public String getCandidateComment() {
        return candidateComment;
    }

    public void setCandidateComment(final String candidateComment) {
        this.candidateComment = candidateComment;
    }

    //----------------------------------------------------------------
    // Built-in variable manipulation

    @ObjectDumperOptions(DumpMode.IGNORE)
    public FloatValue getDurationValue() {
        return (FloatValue) responseValues.get(AssessmentItem.VARIABLE_DURATION_IDENTIFIER);
    }

    public void setDurationValue(final FloatValue value) {
        Assert.notNull(value);
        responseValues.put(AssessmentItem.VARIABLE_DURATION_IDENTIFIER, value);
    }

    public double getDuration() {
        return getDurationValue().doubleValue();
    }

    public void setDuration(final double duration) {
        setDurationValue(new FloatValue(duration));
    }


    @ObjectDumperOptions(DumpMode.IGNORE)
    public IntegerValue getNumAttemptsValue() {
        return (IntegerValue) responseValues.get(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER);
    }

    public void setNumAttemptsValue(final IntegerValue value) {
        Assert.notNull(value);
        responseValues.put(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER, value);
    }

    public int getNumAttempts() {
        return getNumAttemptsValue().intValue();
    }

    public void setNumAttempts(final int numAttempts) {
        setNumAttemptsValue(new IntegerValue(numAttempts));
    }


    @ObjectDumperOptions(DumpMode.IGNORE)
    public IdentifierValue getCompletionStatusValue() {
        return (IdentifierValue) outcomeValues.get(AssessmentItem.VARIABLE_COMPLETION_STATUS_IDENTIFIER);
    }

    public void setCompletionStatusValue(final IdentifierValue value) {
        Assert.notNull(value);
        final String status = value.toQtiString();
        if (!AssessmentItem.VALUE_ITEM_IS_UNKNOWN.equals(status)
                && !AssessmentItem.VALUE_ITEM_IS_NOT_ATTEMPTED.equals(status)
                && !AssessmentItem.VALUE_ITEM_IS_COMPLETED.equals(status)
                && !AssessmentItem.VALUE_ITEM_IS_INCOMPLETE.equals(status)) {
            throw new IllegalArgumentException("Value " + status + " is not an acceptable completionStatus");
        }
        outcomeValues.put(AssessmentItem.VARIABLE_COMPLETION_STATUS_IDENTIFIER, value);
    }

    public String getCompletionStatus() {
        return getCompletionStatusValue().toQtiString();
    }

    public void setCompletionStatus(final String completionStatus) {
        Assert.notNull(completionStatus);
        setCompletionStatusValue(new IdentifierValue(Identifier.parseString(completionStatus)));
    }

    //----------------------------------------------------------------

    public Value getOverriddenDefaultValue(final Identifier identifier) {
        Assert.notNull(identifier);
        Value result = getOverriddenTemplateDefaultValue(identifier);
        if (result == null) {
            result = getOverriddenResponseDefaultValue(identifier);
            if (result == null) {
                result = getOverriddenOutcomeDefaultValue(identifier);
            }
        }
        return result;
    }

    public Value getOverriddenDefaultValue(final VariableDeclaration declaration) {
        return getOverriddenDefaultValue(declaration.getIdentifier());
    }

    //----------------------------------------------------------------

    public Value getOverriddenTemplateDefaultValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return overriddenTemplateDefaultValues.get(identifier);
    }

    public Value getOverriddenTemplateDefaultValue(final TemplateDeclaration templateDeclaration) {
        Assert.notNull(templateDeclaration);
        return getOverriddenTemplateDefaultValue(templateDeclaration.getIdentifier());
    }

    public void setOverriddenTemplateDefaultValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        overriddenTemplateDefaultValues.put(identifier, value);
    }

    public void setOverriddenTemplateDefaultValue(final TemplateDeclaration templateDeclaration, final Value value) {
        Assert.notNull(templateDeclaration);
        setOverriddenTemplateDefaultValue(templateDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getOverriddenTemplateDefaultValues() {
        return Collections.unmodifiableMap(overriddenTemplateDefaultValues);
    }

    //----------------------------------------------------------------

    public Value getOverriddenResponseDefaultValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return overriddenResponseDefaultValues.get(identifier);
    }

    public Value getOverriddenResponseDefaultValue(final ResponseDeclaration responseDeclaration) {
        Assert.notNull(responseDeclaration);
        return getOverriddenResponseDefaultValue(responseDeclaration.getIdentifier());
    }

    public void setOverriddenResponseDefaultValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        overriddenResponseDefaultValues.put(identifier, value);
    }

    public void setOverriddenResponseDefaultValue(final ResponseDeclaration responseDeclaration, final Value value) {
        Assert.notNull(responseDeclaration);
        Assert.notNull(value);
        setOverriddenResponseDefaultValue(responseDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getOverriddenResponseDefaultValues() {
        return Collections.unmodifiableMap(overriddenResponseDefaultValues);
    }

    //----------------------------------------------------------------

    public Value getOverriddenOutcomeDefaultValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return overriddenOutcomeDefaultValues.get(identifier);
    }

    public Value getOverriddenOutcomeDefaultValue(final OutcomeDeclaration outcomeDeclaration) {
        Assert.notNull(outcomeDeclaration);
        return getOverriddenOutcomeDefaultValue(outcomeDeclaration.getIdentifier());
    }

    public void setOverriddenOutcomeDefaultValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        overriddenOutcomeDefaultValues.put(identifier, value);
    }

    public void setOverriddenOutcomeDefaultValue(final OutcomeDeclaration outcomeDeclaration, final Value value) {
        Assert.notNull(outcomeDeclaration);
        setOverriddenResponseDefaultValue(outcomeDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getOverriddenOutcomeDefaultValues() {
        return Collections.unmodifiableMap(overriddenOutcomeDefaultValues);
    }

    //----------------------------------------------------------------

    public Value getOverriddenCorrectResponseValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return overriddenCorrectResponseValues.get(identifier);
    }

    public Value getOverriddenCorrectResponseValue(final ResponseDeclaration responseDeclaration) {
        Assert.notNull(responseDeclaration);
        return getOverriddenCorrectResponseValue(responseDeclaration.getIdentifier());
    }

    public void setOverriddenCorrectResponseValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        overriddenCorrectResponseValues.put(identifier, value);
    }

    public void setOverriddenCorrectResponseValue(final ResponseDeclaration responseDeclaration, final Value value) {
        Assert.notNull(responseDeclaration);
        setOverriddenCorrectResponseValue(responseDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getOverriddenCorrectResponseValues() {
        return Collections.unmodifiableMap(overriddenCorrectResponseValues);
    }

    //----------------------------------------------------------------

    public Value getTemplateValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return templateValues.get(identifier);
    }

    public Value getTemplateValue(final String identifierString) {
        Assert.notNull(identifierString);
        return getTemplateValue(Identifier.parseString(identifierString));
    }

    public Value getTemplateValue(final TemplateDeclaration templateDeclaration) {
        Assert.notNull(templateDeclaration);
        return getTemplateValue(templateDeclaration.getIdentifier());
    }

    public void setTemplateValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        templateValues.put(identifier, value);
    }

    public void setTemplateValue(final String identifierString, final Value value) {
        Assert.notNull(identifierString);
        setTemplateValue(Identifier.parseString(identifierString), value);
    }

    public void setTemplateValue(final TemplateDeclaration templateDeclaration, final Value value) {
        Assert.notNull(templateDeclaration);
        setTemplateValue(templateDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getTemplateValues() {
        return Collections.unmodifiableMap(templateValues);
    }

    //----------------------------------------------------------------
    // Response mutation

    public ResponseData getRawResponseData(final Identifier identifier) {
        Assert.notNull(identifier, "identifier");
        return rawResponseDataMap.get(identifier);
    }

    public void setRawResponseData(final Identifier identifier, final ResponseData responseData) {
        Assert.notNull(identifier, "identifier");
        Assert.notNull(responseData, "responseData");
        rawResponseDataMap.put(identifier, responseData);
    }

    public Map<Identifier, ResponseData> getRawResponseDataMap() {
        return Collections.unmodifiableMap(rawResponseDataMap);
    }

    public void clearRawResponseDataMap() {
        this.rawResponseDataMap.clear();
    }

    public void setRawResponseDataMap(final Map<Identifier, ResponseData> rawResponseDataMap) {
        this.rawResponseDataMap.clear();
        this.rawResponseDataMap.putAll(rawResponseDataMap);
    }


    public Set<Identifier> getUnboundResponseIdentifiers() {
        return Collections.unmodifiableSet(unboundResponseIdentifiers);
    }

    public void setUnboundResponseIdentifiers(final Collection<Identifier> unboundResponseIdentifiers) {
        this.unboundResponseIdentifiers.clear();
        this.unboundResponseIdentifiers.addAll(unboundResponseIdentifiers);
    }

    public void clearUnboundResponseIdentifiers() {
        this.unboundResponseIdentifiers.clear();
    }

    public Set<Identifier> getInvalidResponseIdentifiers() {
        return Collections.unmodifiableSet(invalidResponseIdentifiers);
    }


    public void setInvalidResponseIdentifiers(final Collection<Identifier> invalidResponseIdentifiers) {
        this.invalidResponseIdentifiers.clear();
        this.invalidResponseIdentifiers.addAll(invalidResponseIdentifiers);
    }

    public void clearInvalidResponseIdentifiers() {
        this.invalidResponseIdentifiers.clear();
    }


    public Value getResponseValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return responseValues.get(identifier);
    }

    public Value getResponseValue(final ResponseDeclaration responseDeclaration) {
        Assert.notNull(responseDeclaration);
        return getResponseValue(responseDeclaration.getIdentifier());
    }

    public Value getResponseValue(final Interaction interaction) {
        Assert.notNull(interaction);
        return getResponseValue(interaction.getResponseIdentifier());
    }

    public void setResponseValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        responseValues.put(identifier, value);
    }

    public void setResponseValue(final ResponseDeclaration responseDeclaration, final Value value) {
        Assert.notNull(responseDeclaration);
        Assert.notNull(value);
        setResponseValue(responseDeclaration.getIdentifier(), value);
    }

    public void setResponseValue(final Interaction interaction, final Value value) {
        Assert.notNull(interaction);
        Assert.notNull(value);
        setResponseValue(interaction.getResponseIdentifier(), value);
    }

    public Map<Identifier, Value> getResponseValues() {
        return Collections.unmodifiableMap(responseValues);
    }

    //----------------------------------------------------------------

    public Value getOutcomeValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return outcomeValues.get(identifier);
    }

    public Value getOutcomeValue(final OutcomeDeclaration outcomeDeclaration) {
        Assert.notNull(outcomeDeclaration);
        return getOutcomeValue(outcomeDeclaration.getIdentifier());
    }

    /* NB: It's OK to set completionStatus this way, unlike the reserved response values */
    public void setOutcomeValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        outcomeValues.put(identifier, value);
    }

    /* NB: It's OK to set completionStatus this way, unlike the reserved response values */
    public void setOutcomeValue(final OutcomeDeclaration outcomeDeclaration, final Value value) {
        Assert.notNull(outcomeDeclaration);
        setOutcomeValue(outcomeDeclaration.getIdentifier(), value);
    }

    public void setOutcomeValueFromLookupTable(final OutcomeDeclaration outcomeDeclaration, final NumberValue value) {
        Assert.notNull(outcomeDeclaration);
        Assert.notNull(value);
        Value targetValue = outcomeDeclaration.getLookupTable().getTargetValue(value.doubleValue());
        if (targetValue == null) {
            targetValue = NullValue.INSTANCE;
        }
        setOutcomeValue(outcomeDeclaration.getIdentifier(), targetValue);
    }

    public Map<Identifier, Value> getOutcomeValues() {
        return Collections.unmodifiableMap(outcomeValues);
    }

    //---------------------------------------------------------------

    /**
     * FIXME: Is this still necessary?
     *
     * Gets A template or outcome variable with given identifier or null.
     * DM: This used to be called getValue() in JQTI, but I've renamed it to be
     * clearer
     *
     * @param identifier
     *            given identifier
     * @return value of templateDeclaration or outcomeDeclaration with given
     *         identifier or null
     */
    public Value getTemplateOrOutcomeValue(final Identifier identifier) {
        Assert.notNull(identifier);
        Value result = getTemplateValue(identifier);
        if (result == null) {
            result = getOutcomeValue(identifier);
        }
        return result;
    }

    public Value getVariableValue(final Identifier identifier) {
        Assert.notNull(identifier);
        Value result = getResponseValue(identifier);
        if (result == null) {
            result = getTemplateOrOutcomeValue(identifier);
        }
        return result;
    }

    public Value getVariableValue(final VariableDeclaration variableDeclaration) {
        Assert.notNull(variableDeclaration);
        return getVariableValue(variableDeclaration.getIdentifier());
    }

    public void setVariableValue(final VariableDeclaration variableDeclaration, final Value value) {
        Assert.notNull(variableDeclaration);
        Assert.notNull(value);
        final Identifier identifier = variableDeclaration.getIdentifier();
        if (variableDeclaration instanceof TemplateDeclaration) {
            templateValues.put(identifier, value);
        }
        else if (variableDeclaration instanceof ResponseDeclaration) {
            responseValues.put(identifier, value);
        }
        else if (variableDeclaration instanceof OutcomeDeclaration) {
            outcomeValues.put(identifier, value);
        }
        else {
            throw new QtiLogicException("Unexpected logic branch");
        }
    }

    //----------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ItemSessionState)) {
            return false;
        }
        final ItemSessionState other = (ItemSessionState) obj;
        return initialized==other.initialized
                && sessionStatus==other.sessionStatus
                && presented==other.presented
                && responded==other.responded
                && closed==other.closed
                && shuffledInteractionChoiceOrders.equals(other.shuffledInteractionChoiceOrders)
                && overriddenTemplateDefaultValues.equals(other.overriddenCorrectResponseValues)
                && overriddenResponseDefaultValues.equals(other.overriddenResponseDefaultValues)
                && overriddenOutcomeDefaultValues.equals(other.overriddenOutcomeDefaultValues)
                && overriddenCorrectResponseValues.equals(other.overriddenCorrectResponseValues)
                && rawResponseDataMap.equals(other.rawResponseDataMap)
                && unboundResponseIdentifiers.equals(other.unboundResponseIdentifiers)
                && invalidResponseIdentifiers.equals(other.invalidResponseIdentifiers)
                && templateValues.equals(other.templateValues)
                && responseValues.equals(other.responseValues)
                && outcomeValues.equals(other.outcomeValues);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                initialized,
                sessionStatus,
                presented,
                responded,
                closed,
                shuffledInteractionChoiceOrders,
                overriddenTemplateDefaultValues,
                overriddenResponseDefaultValues,
                overriddenOutcomeDefaultValues,
                overriddenCorrectResponseValues,
                rawResponseDataMap,
                unboundResponseIdentifiers,
                invalidResponseIdentifiers,
                templateValues,
                responseValues,
                outcomeValues
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(initialized=" + initialized
                + ",sessionStatus=" + sessionStatus
                + ",presented=" + presented
                + ",responded=" + responded
                + ",closed=" + closed
                + ",shuffledInteractionChoiceOrders=" + shuffledInteractionChoiceOrders
                + ",overriddenTemplateDefaultValues=" + overriddenTemplateDefaultValues
                + ",overriddenResponseDefaultValues=" + overriddenResponseDefaultValues
                + ",overriddenOutcomeDefaultValues=" + overriddenOutcomeDefaultValues
                + ",overriddenCorrectResponseValues=" + overriddenCorrectResponseValues
                + ",rawResponseDataMap=" + rawResponseDataMap
                + ",unboundResponseIdentifiers=" + unboundResponseIdentifiers
                + ",invalidResponseIdentifiers=" + invalidResponseIdentifiers
                + ",templateValues=" + templateValues
                + ",responseValues=" + responseValues
                + ",outcomesValues=" + outcomeValues
                + ")";
    }
}
