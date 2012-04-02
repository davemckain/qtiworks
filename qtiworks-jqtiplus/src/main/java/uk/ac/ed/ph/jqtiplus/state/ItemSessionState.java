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
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Shuffleable;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the current state of a candidate's item session.
 * <p>
 * An instance of this class is mutable, but you must let JQTI+ perform all
 * mutation operations for you.
 * <p>
 * An instance of this class is NOT safe for use by multiple threads.
 * 
 * @author David McKain
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class ItemSessionState implements Serializable {

    private static final long serialVersionUID = -7586529679289092485L;
    
    private boolean isInitialized;
    
    /**
     * Map of interaction choice orders, for all {@link Shuffleable} interactions.
     * This is keyed on the interaction's responseIdentifier.
     */
    private final Map<Identifier, List<Identifier>> shuffledInteractionChoiceOrders;

    private final Map<Identifier, Value> overriddenTemplateDefaultValues;
    private final Map<Identifier, Value> overriddenResponseDefaultValues;
    private final Map<Identifier, Value> overriddenOutcomeDefaultValues;
    private final Map<Identifier, Value> overriddenCorrectResponseValues;

    private final Map<Identifier, Value> templateValues;
    private final Map<Identifier, Value> responseValues;
    private final Map<Identifier, Value> outcomeValues;

    public ItemSessionState() {
        this.shuffledInteractionChoiceOrders = new HashMap<Identifier, List<Identifier>>();
        this.isInitialized = false;
        this.overriddenTemplateDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenResponseDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenOutcomeDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenCorrectResponseValues = new HashMap<Identifier, Value>();
        this.templateValues = new HashMap<Identifier, Value>();
        this.responseValues = new HashMap<Identifier, Value>();
        this.outcomeValues = new HashMap<Identifier, Value>();
        
        /* Set built-in variables */
        setDuration(0);
        setNumAttempts(0);
        setCompletionStatus(AssessmentItem.VALUE_ITEM_IS_NOT_ATTEMPTED);
    }
    
    //----------------------------------------------------------------
    // Initialisation-related mutators

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    public Map<Identifier, List<Identifier>> getShuffledInteractionChoiceOrders() {
        return Collections.unmodifiableMap(shuffledInteractionChoiceOrders);
    }

    public List<Identifier> getShuffledInteractionChoiceOrder(Identifier responseIdentifier) {
        ConstraintUtilities.ensureNotNull(responseIdentifier);
        return shuffledInteractionChoiceOrders.get(responseIdentifier);
    }
    
    public List<Identifier> getShuffledInteractionChoiceOrder(String responseIdentifierString) {
        ConstraintUtilities.ensureNotNull(responseIdentifierString);
        return getShuffledInteractionChoiceOrder(new Identifier(responseIdentifierString));
    }

    public List<Identifier> getShuffledInteractionChoiceOrder(Interaction interaction) {
        ConstraintUtilities.ensureNotNull(interaction);
        return getShuffledInteractionChoiceOrder(interaction.getResponseIdentifier());
    }

    public void setShuffledInteractionChoiceOrder(Identifier responseIdentifier, List<Identifier> shuffleOrders) {
        ConstraintUtilities.ensureNotNull(responseIdentifier);
        if (shuffleOrders == null || shuffleOrders.isEmpty()) {
            shuffledInteractionChoiceOrders.remove(responseIdentifier);
        }
        else {
            shuffledInteractionChoiceOrders.put(responseIdentifier, shuffleOrders);
        }
    }

    public void setShuffledInteractionChoiceOrder(String responseIdentifierString, List<Identifier> shuffleOrders) {
        ConstraintUtilities.ensureNotNull(responseIdentifierString);
        setShuffledInteractionChoiceOrder(new Identifier(responseIdentifierString), shuffleOrders);
    }

    public void setShuffledInteractionChoiceOrder(Interaction interaction, List<Identifier> shuffleOrders) {
        ConstraintUtilities.ensureNotNull(interaction);
        setShuffledInteractionChoiceOrder(interaction.getResponseIdentifier(), shuffleOrders);
    }

    //----------------------------------------------------------------
    // Built-in variable manipulation
    
    @ObjectDumperOptions(DumpMode.IGNORE)
    public FloatValue getDurationValue() {
        return (FloatValue) responseValues.get(AssessmentItem.VARIABLE_DURATION_NAME_IDENTIFIER);
    }
    
    public void setDurationValue(FloatValue value) {
        ConstraintUtilities.ensureNotNull(value);
        responseValues.put(AssessmentItem.VARIABLE_DURATION_NAME_IDENTIFIER, value);
    }
    
    public double getDuration() {
        return getDurationValue().doubleValue();
    }
    
    public void setDuration(double duration) {
        setDurationValue(new FloatValue(duration));
    }
    
    
    @ObjectDumperOptions(DumpMode.IGNORE)
    public IntegerValue getNumAttemptsValue() {
        return (IntegerValue) responseValues.get(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER);
    }
    
    public void setNumAttemptsValue(IntegerValue value) {
        ConstraintUtilities.ensureNotNull(value);
        responseValues.put(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER, value);
    }
    
    public int getNumAttempts() {
        return getNumAttemptsValue().intValue();
    }
    
    public void setNumAttempts(int numAttempts) {
        setNumAttemptsValue(new IntegerValue(numAttempts));
    }
    
    
    @ObjectDumperOptions(DumpMode.IGNORE)
    public IdentifierValue getCompletionStatusValue() {
        return (IdentifierValue) outcomeValues.get(AssessmentItem.VARIABLE_COMPLETION_STATUS_IDENTIFIER);
    }
    
    public void setCompletionStatusValue(IdentifierValue value) {
        ConstraintUtilities.ensureNotNull(value);
        String status = value.toQtiString();
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
    
    public void setCompletionStatus(String completionStatus) {
        ConstraintUtilities.ensureNotNull(completionStatus);
        setCompletionStatusValue(new IdentifierValue(completionStatus));
    }
    
    //----------------------------------------------------------------

    public Value getOverriddenDefaultValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        Value result = getOverriddenTemplateDefaultValue(identifier);
        if (result == null) {
            result = getOverriddenResponseDefaultValue(identifier);
            if (result == null) {
                result = getOverriddenOutcomeDefaultValue(identifier);
            }
        }
        return result;
    }
    
    public Value getOverriddenDefaultValue(String identifierString) {
        ConstraintUtilities.ensureNotNull(identifierString);
        return getOverriddenDefaultValue(new Identifier(identifierString));
    }

    public Value getOverriddenDefaultValue(VariableDeclaration declaration) {
        return getOverriddenDefaultValue(declaration.getIdentifier());
    }
    
    //----------------------------------------------------------------

    public Value getOverriddenTemplateDefaultValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return overriddenTemplateDefaultValues.get(identifier);
    }

    public Value getOverriddenTemplateDefaultValue(String identifierString) {
        ConstraintUtilities.ensureNotNull(identifierString);
        return getOverriddenTemplateDefaultValue(new Identifier(identifierString));
    }
    
    public Value getOverriddenTemplateDefaultValue(TemplateDeclaration templateDeclaration) {
        ConstraintUtilities.ensureNotNull(templateDeclaration);
        return getOverriddenTemplateDefaultValue(templateDeclaration.getIdentifier());
    }

    public void setOverriddenTemplateDefaultValue(Identifier identifier, Value value) {
        ConstraintUtilities.ensureNotNull(identifier);
        ConstraintUtilities.ensureNotNull(value);
        overriddenTemplateDefaultValues.put(identifier, value);
    }

    public void setOverriddenTemplateDefaultValue(String identifierString, Value value) {
        ConstraintUtilities.ensureNotNull(identifierString);
        setOverriddenTemplateDefaultValue(new Identifier(identifierString), value);
    }
    
    public void setOverriddenTemplateDefaultValue(TemplateDeclaration templateDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(templateDeclaration);
        setOverriddenTemplateDefaultValue(templateDeclaration.getIdentifier(), value);
    }
    
    public Map<Identifier, Value> getOverriddenTemplateDefaultValues() {
        return Collections.unmodifiableMap(overriddenTemplateDefaultValues);
    }

    //----------------------------------------------------------------

    public Value getOverriddenResponseDefaultValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return overriddenResponseDefaultValues.get(identifier);
    }

    public Value getOverriddenResponseDefaultValue(String identifierString) {
        ConstraintUtilities.ensureNotNull(identifierString);
        return overriddenResponseDefaultValues.get(new Identifier(identifierString));
    }

    public Value getOverriddenResponseDefaultValue(ResponseDeclaration responseDeclaration) {
        ConstraintUtilities.ensureNotNull(responseDeclaration);
        return getOverriddenResponseDefaultValue(responseDeclaration.getIdentifier());
    }

    public void setOverriddenResponseDefaultValue(Identifier identifier, Value value) {
        ConstraintUtilities.ensureNotNull(identifier);
        ConstraintUtilities.ensureNotNull(value);
        overriddenResponseDefaultValues.put(identifier, value);
    }
    
    public void setOverriddenResponseDefaultValue(String identifierString, Value value) {
        ConstraintUtilities.ensureNotNull(identifierString);
        setOverriddenResponseDefaultValue(new Identifier(identifierString), value);
    }

    public void setOverriddenResponseDefaultValue(ResponseDeclaration responseDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(responseDeclaration);
        ConstraintUtilities.ensureNotNull(value);
        setOverriddenResponseDefaultValue(responseDeclaration.getIdentifier(), value);
    }
    
    public Map<Identifier, Value> getOverriddenResponseDefaultValues() {
        return Collections.unmodifiableMap(overriddenResponseDefaultValues);
    }

    //----------------------------------------------------------------
    
    public Value getOverriddenOutcomeDefaultValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return overriddenOutcomeDefaultValues.get(identifier);
    }
    
    public Value getOverriddenOutcomeDefaultValue(String identifierString) {
        ConstraintUtilities.ensureNotNull(identifierString);
        return getOverriddenOutcomeDefaultValue(new Identifier(identifierString));
    }

    public Value getOverriddenOutcomeDefaultValue(OutcomeDeclaration outcomeDeclaration) {
        ConstraintUtilities.ensureNotNull(outcomeDeclaration);
        return getOverriddenOutcomeDefaultValue(outcomeDeclaration.getIdentifier());
    }

    public void setOverriddenOutcomeDefaultValue(Identifier identifier, Value value) {
        ConstraintUtilities.ensureNotNull(identifier);
        ConstraintUtilities.ensureNotNull(value);
        overriddenOutcomeDefaultValues.put(identifier, value);
    }

    public void setOverriddenOutcomeDefaultValue(OutcomeDeclaration outcomeDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(outcomeDeclaration);
        setOverriddenResponseDefaultValue(outcomeDeclaration.getIdentifier(), value);
    }
    
    public Map<Identifier, Value> getOverriddenOutcomeDefaultValues() {
        return Collections.unmodifiableMap(overriddenOutcomeDefaultValues);
    }

    //----------------------------------------------------------------

    public Value getOverriddenCorrectResponseValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return overriddenCorrectResponseValues.get(identifier);
    }
    
    public Value getOverriddenCorrectResponseValue(String identifierString) {
        ConstraintUtilities.ensureNotNull(identifierString);
        return getOverriddenCorrectResponseValue(new Identifier(identifierString));
    }

    public Value getOverriddenCorrectResponseValue(ResponseDeclaration responseDeclaration) {
        ConstraintUtilities.ensureNotNull(responseDeclaration);
        return getOverriddenCorrectResponseValue(responseDeclaration.getIdentifier());
    }

    public void setOverriddenCorrectResponseValue(Identifier identifier, Value value) {
        ConstraintUtilities.ensureNotNull(identifier);
        ConstraintUtilities.ensureNotNull(value);
        overriddenCorrectResponseValues.put(identifier, value);
    }

    public void setOverriddenCorrectResponseValue(String identifierString, Value value) {
        ConstraintUtilities.ensureNotNull(identifierString);
        setOverriddenOutcomeDefaultValue(new Identifier(identifierString), value);
    }

    public void setOverriddenCorrectResponseValue(ResponseDeclaration responseDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(responseDeclaration);
        setOverriddenCorrectResponseValue(responseDeclaration.getIdentifier(), value);
    }
    
    public Map<Identifier, Value> getOverriddenCorrectResponseValues() {
        return Collections.unmodifiableMap(overriddenCorrectResponseValues);
    }
    
    //----------------------------------------------------------------

    public Value getTemplateValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return templateValues.get(identifier);
    }

    public Value getTemplateValue(String identifierString) {
        ConstraintUtilities.ensureNotNull(identifierString);
        return getTemplateValue(new Identifier(identifierString));
    }

    public Value getTemplateValue(TemplateDeclaration templateDeclaration) {
        ConstraintUtilities.ensureNotNull(templateDeclaration);
        return getTemplateValue(templateDeclaration.getIdentifier());
    }

    public void setTemplateValue(Identifier identifier, Value value) {
        ConstraintUtilities.ensureNotNull(identifier);
        ConstraintUtilities.ensureNotNull(value);
        templateValues.put(identifier, value);
    }

    public void setTemplateValue(String identifierString, Value value) {
        ConstraintUtilities.ensureNotNull(identifierString);
        setTemplateValue(new Identifier(identifierString), value);
    }

    public void setTemplateValue(TemplateDeclaration templateDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(templateDeclaration);
        setTemplateValue(templateDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getTemplateValues() {
        return Collections.unmodifiableMap(templateValues);
    }

    //----------------------------------------------------------------

    public Value getResponseValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return responseValues.get(identifier);
    }

    public Value getResponseValue(String identifierString) {
        ConstraintUtilities.ensureNotNull(identifierString);
        return getResponseValue(new Identifier(identifierString));
    }

    public Value getResponseValue(ResponseDeclaration responseDeclaration) {
        ConstraintUtilities.ensureNotNull(responseDeclaration);
        return getResponseValue(responseDeclaration.getIdentifier());
    }

    public Value getResponseValue(Interaction interaction) {
        ConstraintUtilities.ensureNotNull(interaction);
        return getResponseValue(interaction.getResponseIdentifier());
    }

    public void setResponseValue(Identifier identifier, Value value) {
        ConstraintUtilities.ensureNotNull(identifier);
        ConstraintUtilities.ensureNotNull(value);
        if (identifier.equals(AssessmentItem.VARIABLE_DURATION_NAME_IDENTIFIER)) {
            throw new IllegalArgumentException("duration variable should not be set via setResponseValue()");
        }
        if (identifier.equals(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER)) {
            throw new IllegalArgumentException("numAttempts variable should not be set via setResponseValue()");
        }
        responseValues.put(identifier, value);
    }

    public void setResponseValue(String identifierString, Value value) {
        ConstraintUtilities.ensureNotNull(identifierString);
        setResponseValue(new Identifier(identifierString), value);
    }

    public void setResponseValue(ResponseDeclaration responseDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(responseDeclaration);
        ConstraintUtilities.ensureNotNull(value);
        setResponseValue(responseDeclaration.getIdentifier(), value);
    }

    public void setResponseValue(Interaction interaction, Value value) {
        ConstraintUtilities.ensureNotNull(interaction);
        ConstraintUtilities.ensureNotNull(value);
        setResponseValue(interaction.getResponseIdentifier(), value);
    }

    public Map<Identifier, Value> getResponseValues() {
        return Collections.unmodifiableMap(responseValues);
    }

    //----------------------------------------------------------------

    public Value getOutcomeValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return outcomeValues.get(identifier);
    }

    public Value getOutcomeValue(String identifierString) {
        ConstraintUtilities.ensureNotNull(identifierString);
        return getOutcomeValue(new Identifier(identifierString));
    }
    
    public Value getOutcomeValue(OutcomeDeclaration outcomeDeclaration) {
        ConstraintUtilities.ensureNotNull(outcomeDeclaration);
        return getOutcomeValue(outcomeDeclaration.getIdentifier());
    }

    /* NB: It's OK to set completionStatus this way, unlike the reserved response values */
    public void setOutcomeValue(Identifier identifier, Value value) {
        ConstraintUtilities.ensureNotNull(identifier);
        ConstraintUtilities.ensureNotNull(value);
        outcomeValues.put(identifier, value);
    }

    /* NB: It's OK to set completionStatus this way, unlike the reserved response values */
    public void setOutcomeValue(String identifierString, Value value) {
        ConstraintUtilities.ensureNotNull(identifierString);
        setOutcomeValue(new Identifier(identifierString), value);
    }

    /* NB: It's OK to set completionStatus this way, unlike the reserved response values */
    public void setOutcomeValue(OutcomeDeclaration outcomeDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(outcomeDeclaration);
        setOutcomeValue(outcomeDeclaration.getIdentifier(), value);
    }

    public void setOutcomeValueFromLookupTable(OutcomeDeclaration outcomeDeclaration, NumberValue value) {
        ConstraintUtilities.ensureNotNull(outcomeDeclaration);
        ConstraintUtilities.ensureNotNull(value);
        Value targetValue = outcomeDeclaration.getLookupTable().getTargetValue(value);
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
     * Gets A template or outcome variable with given identifier or null.
     * DM: This used to be called getValue() in JQTI, but I've renamed it to be
     * clearer
     * 
     * @param identifier
     *            given identifier
     * @return value of templateDeclaration or outcomeDeclaration with given
     *         identifier or null
     */
    public Value getTemplateOrOutcomeValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        Value result = getTemplateValue(identifier);
        if (result == null) {
            result = getOutcomeValue(identifier);
        }
        return result;
    }
    
    public Value getTemplateOrOutcomeValue(String identifierString) {
        ConstraintUtilities.ensureNotNull(identifierString);
        return getTemplateOrOutcomeValue(new Identifier(identifierString));
    }

    public Value getVariableValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        Value result = getResponseValue(identifier);
        if (result == null) {
            result = getTemplateOrOutcomeValue(identifier);
        }
        return result;
    }

    public Value getVariableValue(String identifierString) {
        ConstraintUtilities.ensureNotNull(identifierString);
        return getVariableValue(new Identifier(identifierString));
    }

    public Value getVariableValue(VariableDeclaration variableDeclaration) {
        ConstraintUtilities.ensureNotNull(variableDeclaration);
        return getVariableValue(variableDeclaration.getIdentifier());
    }

    public void setVariableValue(VariableDeclaration variableDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(variableDeclaration);
        ConstraintUtilities.ensureNotNull(value);
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
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(initialized=" + isInitialized
                + ",shuffledInteractionChoiceOrders=" + shuffledInteractionChoiceOrders
                + ",overriddenTemplateDefaultValues=" + overriddenTemplateDefaultValues
                + ",overriddenResponseDefaultValues=" + overriddenResponseDefaultValues
                + ",overriddenOutcomeDefaultValues=" + overriddenOutcomeDefaultValues
                + ",overriddenCorrectResponseValues=" + overriddenCorrectResponseValues
                + ",templateValues=" + templateValues
                + ",responseValues=" + responseValues
                + ",outcomesValues=" + outcomeValues
                + ")";
    }
}
