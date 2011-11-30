/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.control.QTILogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
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
 * An instance of this is not safe for use by multiple threads.
 * 
 * TODO: Document this!
 * 
 * TODO: Shuffle orders need integrated into the XSLT since we're no longer
 * explicitly reordering the elements.
 * 
 * @author David McKain
 * @author Jonathon Hare
 * @author Jiri Kajaba
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class AssessmentItemState implements Serializable {

    private static final long serialVersionUID = -7586529679289092485L;

    private final Map<Identifier, Value> overriddenTemplateDefaultValues;
    private final Map<Identifier, Value> overriddenResponseDefaultValues;
    private final Map<Identifier, Value> overriddenOutcomeDefaultValues;
    private final Map<Identifier, Value> overriddenCorrectResponseValues;

    private final Map<Identifier, Value> templateValues;
    private final Map<Identifier, Value> responseValues;
    private final Map<Identifier, Value> outcomeValues;

    private final Map<Identifier, List<Identifier>> shuffledInteractionChoiceOrders;
    private boolean isInitialized;

    private ItemTimeRecord timeRecord;

    /**
     * Constructs assessmentItem.
     */
    public AssessmentItemState() {
        this.timeRecord = null;
        this.isInitialized = false;
        this.overriddenTemplateDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenResponseDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenOutcomeDefaultValues = new HashMap<Identifier, Value>();
        this.overriddenCorrectResponseValues = new HashMap<Identifier, Value>();
        this.templateValues = new HashMap<Identifier, Value>();
        this.responseValues = new HashMap<Identifier, Value>();
        this.outcomeValues = new HashMap<Identifier, Value>();
        this.shuffledInteractionChoiceOrders = new HashMap<Identifier,List<Identifier>>();
    }
    
    public ItemTimeRecord getTimeRecord() {
        return timeRecord;
    }

    public void setTimeRecord(ItemTimeRecord timeRecord) {
        this.timeRecord = timeRecord;
    }
    
    //----------------------------------------------------------------
    
    public void reset() {
        isInitialized = false;
        overriddenTemplateDefaultValues.clear();
        overriddenResponseDefaultValues.clear();
        overriddenOutcomeDefaultValues.clear();
        overriddenCorrectResponseValues.clear();
        templateValues.clear();
        responseValues.clear();
        outcomeValues.clear();
        shuffledInteractionChoiceOrders.clear();
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }
    
    //----------------------------------------------------------------
    
    public Value getOverriddenDefaultValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        Value result = getOverriddenTemplateDefaultValue(identifier);
        if (result==null) {
            result = getOverriddenResponseDefaultValue(identifier);
            if (result==null) {
                result = getOverriddenOutcomeDefaultValue(identifier);
            }
        }
        return result;
    }
    
    public Value getOverriddenDefaultValue(VariableDeclaration declaration) {
        return getOverriddenDefaultValue(declaration.getIdentifier());
    }
    
    //----------------------------------------------------------------
    
    public Value getOverriddenTemplateDefaultValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return overriddenTemplateDefaultValues.get(identifier);
    }

    public Value getOverriddenTemplateDefaultValue(TemplateDeclaration templateDeclaration) {
        return getOverriddenTemplateDefaultValue(templateDeclaration.getIdentifier());
    }

    public void setOverriddenTemplateDefaultValue(Identifier identifier, Value value) {
        ConstraintUtilities.ensureNotNull(identifier);
        overriddenTemplateDefaultValues.put(identifier, value);
    }
    
    public void setOverriddenTemplateDefaultValue(TemplateDeclaration templateDeclaration, Value value) {
        setOverriddenTemplateDefaultValue(templateDeclaration.getIdentifier(), value);
    }
    
    //----------------------------------------------------------------
    
    public Value getOverriddenResponseDefaultValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return overriddenResponseDefaultValues.get(identifier);
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
    
    public void setOverriddenResponseDefaultValue(ResponseDeclaration responseDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(responseDeclaration);
        ConstraintUtilities.ensureNotNull(value);
        setOverriddenResponseDefaultValue(responseDeclaration.getIdentifier(), value);
    }
    
    //----------------------------------------------------------------
    
    public Value getOverriddenCorrectResponseValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return overriddenCorrectResponseValues.get(identifier);
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
    
    public void setOverriddenCorrectResponseValue(ResponseDeclaration responseDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(responseDeclaration);
        ConstraintUtilities.ensureNotNull(value);
        setOverriddenCorrectResponseValue(responseDeclaration.getIdentifier(), value);
    }
    
    //----------------------------------------------------------------

    public Value getOverriddenOutcomeDefaultValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return overriddenOutcomeDefaultValues.get(identifier);
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
        ConstraintUtilities.ensureNotNull(value);
        setOverriddenResponseDefaultValue(outcomeDeclaration.getIdentifier(), value);
    }
    
    //----------------------------------------------------------------
    
    public Value getDurationValue() {
        if (timeRecord==null) {
            return NullValue.INSTANCE;
        }
        /* Return duration as supplied by timeRecord */
        return new FloatValue(timeRecord.getDuration() / 1000.0);
    }
    
    /**
     * Gets A response variable with given identifier or null
     * 
     * @param identifier
     *            given identifier
     * @return value of responseDeclaration with given identifier or null
     */
    public Value getResponseValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        if (identifier.equals(AssessmentItem.VARIABLE_DURATION_NAME_IDENTIFIER)) {
            /* Return duration as supplied by timeRecord */
            return getDurationValue();
        }
        return responseValues.get(identifier);
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
        if (identifier.equals(AssessmentItem.VARIABLE_DURATION_NAME_IDENTIFIER)) {
            throw new QTILogicException("duration variable should not be explicitly set");
        }
        responseValues.put(identifier, value);
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
    	Map<Identifier, Value> result = new HashMap<Identifier, Value>(responseValues);
    	result.put(AssessmentItem.VARIABLE_DURATION_NAME_IDENTIFIER, getDurationValue());
        return Collections.unmodifiableMap(result);
    }
    
    //----------------------------------------------------------------
    
    /**
     * Gets value of templateDeclaration with given identifier or null.
     * 
     * @param identifier
     *            given identifier
     * @return value of templateDeclaration with given identifier or null
     */
    public Value getTemplateValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return templateValues.get(identifier);
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

    public void setTemplateValue(TemplateDeclaration templateDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(templateDeclaration);
        ConstraintUtilities.ensureNotNull(value);
        setTemplateValue(templateDeclaration.getIdentifier(), value);
    }

    public Map<Identifier, Value> getTemplateValues() {
        return Collections.unmodifiableMap(templateValues);
    }
    
    //---------------------------------------------------------------

    public Value getOutcomeValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return outcomeValues.get(identifier);
    }

    public Value getOutcomeValue(OutcomeDeclaration outcomeDeclaration) {
        ConstraintUtilities.ensureNotNull(outcomeDeclaration);
        return getOutcomeValue(outcomeDeclaration.getIdentifier());
    }

    public void setOutcomeValue(Identifier identifier, Value value) {
        ConstraintUtilities.ensureNotNull(identifier);
        ConstraintUtilities.ensureNotNull(value);
        outcomeValues.put(identifier, value);
    }

    public void setOutcomeValue(OutcomeDeclaration outcomeDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(outcomeDeclaration);
        ConstraintUtilities.ensureNotNull(value);
        setOutcomeValue(outcomeDeclaration.getIdentifier(), value);
    }

    public void setOutcomeValueFromLookupTable(OutcomeDeclaration outcomeDeclaration, NumberValue value) {
        ConstraintUtilities.ensureNotNull(outcomeDeclaration);
        ConstraintUtilities.ensureNotNull(value);
        Value targetValue = outcomeDeclaration.getLookupTable().getTargetValue(value);
        if (targetValue==null) {
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
     * 
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
        if (result==null) {
            result = getOutcomeValue(identifier);
        }
        return result;
    }

    /* (NEW!) */
    public Value getValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        Value result = getResponseValue(identifier);
        if (result==null) {
            result = getTemplateOrOutcomeValue(identifier);
        }
        return result;
    }
    
    public Value getValue(VariableDeclaration variableDeclaration) {
        ConstraintUtilities.ensureNotNull(variableDeclaration);
        return getValue(variableDeclaration.getIdentifier());
    }

    public void setValue(VariableDeclaration variableDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(variableDeclaration);
        ConstraintUtilities.ensureNotNull(value);
        Identifier identifier = variableDeclaration.getIdentifier();
        if (variableDeclaration instanceof ResponseDeclaration) {
            responseValues.put(identifier, value);
        }
        else if (variableDeclaration instanceof OutcomeDeclaration) {
            outcomeValues.put(identifier, value);
        }
        else if (variableDeclaration instanceof TemplateDeclaration) {
            templateValues.put(identifier, value);
        }
        else {
            throw new QTILogicException("Unexpected logic branch");
        }
    }
    
    //----------------------------------------------------------------
    
    public Map<Identifier, List<Identifier>> getShuffledInteractionChoiceOrders() {
        return Collections.unmodifiableMap(shuffledInteractionChoiceOrders);
    }
    
    public List<Identifier> getShuffledInteractionChoiceOrder(Identifier responseIdentifier) {
        ConstraintUtilities.ensureNotNull(responseIdentifier);
        return shuffledInteractionChoiceOrders.get(responseIdentifier);
    }
    
    public List<Identifier> getShuffledInteractionChoiceOrder(Interaction interaction) {
        ConstraintUtilities.ensureNotNull(interaction);
        return getShuffledInteractionChoiceOrder(interaction.getResponseIdentifier());
    }

    public void setShuffledInteractionChoiceOrder(Identifier responseIdentifier, List<Identifier> shuffleOrders) {
        ConstraintUtilities.ensureNotNull(responseIdentifier);
        if (shuffleOrders==null || shuffleOrders.isEmpty()) {
        	shuffledInteractionChoiceOrders.remove(responseIdentifier);
        }
        else {
        	shuffledInteractionChoiceOrders.put(responseIdentifier, shuffleOrders);
        }
    }

    public void setShuffledInteractionChoiceOrder(Interaction interaction, List<Identifier> shuffleOrders) {
        ConstraintUtilities.ensureNotNull(interaction);
        setShuffledInteractionChoiceOrder(interaction.getResponseIdentifier(), shuffleOrders);
    }
    
    
    //----------------------------------------------------------------
    /* (New convenience methods) */
    
    public int getNumAttempts() {
        Value value = getResponseValue(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER);
        int result = -1;
        if (value!=null && value.getBaseType()==BaseType.INTEGER && value.getCardinality()==Cardinality.SINGLE) {
            result = ((IntegerValue) value).intValue();
        }
        return result;
    }
    
    //----------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(overriddenTemplateDefaultValues=" + overriddenTemplateDefaultValues
            + ",overriddenResponseDefaultValues=" + overriddenResponseDefaultValues
            + ",overriddenOutcomeDefaultValues=" + overriddenOutcomeDefaultValues
            + ",overriddenCorrectResponseValues=" + overriddenCorrectResponseValues
            + ",templateValues=" + templateValues
            + ",responseValues=" + responseValues
            + ",outcomesValues=" + outcomeValues
            + ",shuffledInteractionChoiceOrders=" + shuffledInteractionChoiceOrders 
            + ",initialized=" + isInitialized
            + ",timeRecord=" + timeRecord
            + ")";
    }
}
