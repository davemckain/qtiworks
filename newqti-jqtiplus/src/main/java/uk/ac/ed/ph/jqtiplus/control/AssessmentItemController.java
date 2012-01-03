/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.exception.QTIEvaluationException;
import uk.ac.ed.ph.jqtiplus.exception2.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.exception2.TemplateProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.CorrectResponse;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.EndAttemptInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Shuffleable;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.Choice;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateProcessing;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateProcessingRule;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.node.result.OutcomeVariable;
import uk.ac.ed.ph.jqtiplus.node.result.ResponseVariable;
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;
import uk.ac.ed.ph.jqtiplus.node.result.TemplateVariable;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.node.test.TemplateDefault;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.AssessmentItemManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Usage: one-shot, not thread safe.
 * 
 * FIXME: Document this!
 * FIXME: This now includes runtime context information, so is no longer stateless. Need to fix that!
 */
public final class AssessmentItemController {
    
    protected static Logger logger = LoggerFactory.getLogger(AssessmentItemController.class);
    
    /** FIXME: Make this settable! */
    public static int MAX_TEMPLATE_PROCESSING_TRIES = 100;
    
    private final AssessmentItemManager itemManager;
    private final AssessmentItem item;
    private final AssessmentItemState itemState;
    
    public AssessmentItemController(AssessmentItemManager itemManager, AssessmentItemState assessmentItemState) {
        ConstraintUtilities.ensureNotNull(itemManager, "assessmentItemManager");
        ConstraintUtilities.ensureNotNull(assessmentItemState, "assessmentItemState");
        this.itemManager = itemManager;
        this.item = itemManager.getItem();
        this.itemState = assessmentItemState;
    }
    
    public AssessmentItemManager getItemManager() {
        return itemManager;
    }

    public AssessmentItem getItem() {
        return item;
    }
    
    public AssessmentItemState getItemState() {
        return itemState;
    }

    //-------------------------------------------------------------------
    
    public ValidationResult validate() {
        return itemManager.validateItem();
    }
    
    public ResponseProcessing getResolvedResponseProcessing() {
        return itemManager.getResolvedResponseProcessing();
    }
    
    //-------------------------------------------------------------------
    // Shuffle helpers
    
    public <C extends Choice> void shuffleInteractionChoiceOrder(final Interaction interaction, final List<C> choiceList) {
        List<List<C>> choiceLists = new ArrayList<List<C>>();
        choiceLists.add(choiceList);
        shuffleInteractionChoiceOrders(interaction, choiceLists);
    }
    
    public <C extends Choice> void shuffleInteractionChoiceOrders(final Interaction interaction, final List<List<C>> choiceLists) {
        if (interaction instanceof Shuffleable) {
            if (((Shuffleable) interaction).getShuffle().booleanValue()) {
                List<Identifier> choiceIdentifiers = new ArrayList<Identifier>();
                for (List<C> choiceList : choiceLists) {
                    List<Identifier> shuffleableChoiceIdentifiers = new ArrayList<Identifier>();

                    /* Build up sortable identifiers */
                    for (int i=0; i<choiceList.size(); i++) {
                        C choice = choiceList.get(i);
                        if (!choice.getFixed()) {
                            shuffleableChoiceIdentifiers.add(choice.getIdentifier());
                        }
                    }
                    
                    /* Perform shuffle */
                    Collections.shuffle(shuffleableChoiceIdentifiers);
                    
                    /* Then merge fixed identifiers back in */
                    for (int i=0, sortedIndex=0; i<choiceList.size(); i++) {
                        C choice = choiceList.get(i);
                        if (choice.getFixed()) {
                            choiceIdentifiers.add(choice.getIdentifier());
                        }
                        else {
                            choiceIdentifiers.add(shuffleableChoiceIdentifiers.get(sortedIndex++));
                        }
                    }
                }
                itemState.setShuffledInteractionChoiceOrder(interaction, choiceIdentifiers);
            }
            else {
                itemState.setShuffledInteractionChoiceOrder(interaction, null);
            }
        }
    }
    
    //-------------------------------------------------------------------
    
    public Value lookupVariable(VariableDeclaration variableDeclaration) {
        ConstraintUtilities.ensureNotNull(variableDeclaration);
        return lookupVariable(variableDeclaration.getIdentifier());
    }
    
    public Value lookupVariable(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return itemState.getValue(identifier);
    }
    
    public Value lookupVariable(Identifier identifier, VariableType... permittedTypes) {
        ConstraintUtilities.ensureNotNull(identifier);
        Value value = null;
        for (VariableType type : permittedTypes) {
            switch (type) {
                case TEMPLATE:
                    value = itemState.getTemplateValue(identifier);
                    break;
                    
                case RESPONSE:
                    value = itemState.getResponseValue(identifier);
                    break;
                    
                case OUTCOME:
                    value = itemState.getOutcomeValue(identifier);
                    break;
                    
                default:
                    throw new QTILogicException("Unexpected switch case");
            }
        }
        return value;
    }

    /**
     * Set the completionStatus to the given value.
     * @param completionStatus value to set
     */
    public void setCompletionStatus(String completionStatus) {
        itemState.setOutcomeValue(AssessmentItem.VARIABLE_COMPLETION_STATUS_IDENTIFIER, new IdentifierValue(completionStatus));
    }
    
    private void initValue(VariableDeclaration declaration) {
        ConstraintUtilities.ensureNotNull(declaration);
        itemState.setValue(declaration, computeInitialValue(declaration));
    }
    
    private Value computeInitialValue(Identifier identifier) {
        return computeDefaultValue(identifier);
    }
    
    private Value computeInitialValue(VariableDeclaration declaration) {
        ConstraintUtilities.ensureNotNull(declaration);
        return computeInitialValue(declaration.getIdentifier());
    }
    
    /* DM: This copes with defaults and overridden values */
    public Value computeDefaultValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return computeDefaultValue(ensureVariableDeclaration(identifier));
    }
    
    public Value computeDefaultValue(VariableDeclaration declaration) {
        ConstraintUtilities.ensureNotNull(declaration);
        Value result = itemState.getOverriddenDefaultValue(declaration);
        if (result==null) {
            DefaultValue defaultValue = declaration.getDefaultValue();
            if (defaultValue!=null) {
                result = defaultValue.evaluate();
            }
            else {
                result = NullValue.INSTANCE;
            }
        }
        return result;
    }
    
    private VariableDeclaration ensureVariableDeclaration(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        VariableDeclaration result = item.getVariableDeclaration(identifier);
        if (result==null) {
            throw new QTIEvaluationException("Item variable with identifier " + identifier + " is not defined");
        }
        return result;
    }
    
    private ResponseDeclaration ensureResponseDeclaration(Identifier responseIdentifier) {
        ConstraintUtilities.ensureNotNull(responseIdentifier);
        ResponseDeclaration result = item.getResponseDeclaration(responseIdentifier);
        if (result==null) {
            throw new QTIEvaluationException("Response variable with identifier " + responseIdentifier + " is not defined");
        }
        return result;
    }
    
    public Value computeCorrectResponse(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return computeCorrectResponse(ensureResponseDeclaration(identifier));
    }
    
    public Value computeCorrectResponse(ResponseDeclaration declaration) {
        ConstraintUtilities.ensureNotNull(declaration);
        Value result = itemState.getOverriddenCorrectResponseValue(declaration);
        if (result==null) {
            CorrectResponse correctResponse = declaration.getCorrectResponse();
            if (correctResponse!=null) {
                result = correctResponse.evaluate();
            }
            else {
                result = NullValue.INSTANCE;
            }
        }
        return result;
    }

    /**
     * Returns true if this declarations value matches its correctValue.
     * Returns null if there is no correct value
     * 
     * NOTE: This only tests for "the" "correct" response, not "a" correct response.
     * 
     * @return true if the associated correctResponse matches the value; false or null otherwise.
     */
    public Boolean isCorrectResponse(ResponseDeclaration responseDeclaration) {
        Value correctResponseValue = computeCorrectResponse(responseDeclaration);
        if (correctResponseValue.isNull()) {
            return null;
        }
        return correctResponseValue.equals(itemState.getValue(responseDeclaration));
    }

    //-------------------------------------------------------------------
    // Workflow methods
    
    private void fireLifecycleEvent(LifecycleEventType eventType) {
        for (JQTIExtensionPackage extensionPackage : itemManager.getQTIObjectManager().getJQTIController().getExtensionPackages()) {
            extensionPackage.lifecycleEvent(this, eventType);
        }
    }
    
    /**
     * Initialise the item by setting the template defaults, resetting variables,
     * and performing templateProcessing.
     * 
     * An item should only be initialised if it is going to be rendered/presented
     * 
     * @param templateDefaults given templateDefaults values
     *  
     * @throws RuntimeValidationException if a runtime validation error is detected during template
     *   processing. 
     */
    public void initialize(List<TemplateDefault> templateDefaults) throws RuntimeValidationException {
        // DM: Changed behaviour here in order to suit MathAssess project better. We now only
        // initialise an item once. The previous JQTI behaviour allowed items to be reinitialised,
        // which had the effect of rebuilding template variables, which caused randomised questions
        // in tests to re-randomise during navigation, which was deemed to be very confusing to students.
        // I have left the existing logic intact below.
        //
        // FIXME-DM: Maybe we should dump this flag completely and only initialise items when it actually makes sense?
        if (itemState.isInitialized()) {
            return;
        }
        
        ItemProcessingContext context = new ItemProcessingContextImpl();
        
        fireLifecycleEvent(LifecycleEventType.ITEM_INITIALISATION_STARTING);
        try {
            /* Reset state */
            itemState.reset();
            
            /* Set up built-in variables */
            setCompletionStatus(AssessmentItem.VALUE_ITEM_IS_NOT_ATTEMPTED);
            itemState.setResponseValue(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER, new IntegerValue(0));
            
            /* Perform template processing as many times as required. */
            int templateProcessingAttemptNumber = 0;
            boolean templateProcessingCompleted = false;
            while (!templateProcessingCompleted) {
                templateProcessingCompleted = doTemplateProcessing(context, templateDefaults, ++templateProcessingAttemptNumber);
            }
            
            /* Initialises outcomeDeclaration's values. */
            for (OutcomeDeclaration outcomeDeclaration : item.getOutcomeDeclarations()) {
                initValue(outcomeDeclaration);
            }
        
            /* Initialises responseDeclaration's values. */
            for (ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
                initValue(responseDeclaration);
            }
        
            /* Set the completion status to unknown */
            setCompletionStatus(AssessmentItem.VALUE_ITEM_IS_UNKNOWN);
            
            /* Initialize all interactions in the itemBody */
            for (Interaction interaction : item.getItemBody().getInteractions()) {
                interaction.initialize(this);
            }
            
            itemState.setInitialized(true);
        }
        finally {
            fireLifecycleEvent(LifecycleEventType.ITEM_INITIALISATION_FINISHED);
        }
    }
    
    
    private boolean doTemplateProcessing(ItemProcessingContext context, List<TemplateDefault> templateDefaults, int attemptNumber)
            throws RuntimeValidationException {
        logger.info("Template Processing attempt #{} starting", attemptNumber);
        
        /* Initialise template defaults with any externally provided defaults */
        if (templateDefaults!=null) {
            logger.debug("Setting template default values");
            for (TemplateDefault templateDefault : templateDefaults) {
                TemplateDeclaration declaration = item.getTemplateDeclaration(templateDefault.getTemplateIdentifier());
                if (declaration != null) {
                    Value defaultValue = templateDefault.evaluate(context);
                    itemState.setOverriddenTemplateDefaultValue(declaration.getIdentifier(), defaultValue);
                }
            }
        }

        /* Initialise template values. */
        for (TemplateDeclaration templateDeclaration : item.getTemplateDeclarations()) {
            initValue(templateDeclaration);
        }

        if (attemptNumber > MAX_TEMPLATE_PROCESSING_TRIES) {
            logger.warn("Exceeded maxmimum number of template processing retries - leaving variables at default values");
            return true;
        }

        /* Perform templateProcessing. */
        TemplateProcessing templateProcessing = item.getTemplateProcessing();
        if (templateProcessing != null) {
            logger.debug("Evaluating template processing rules");
            try {
                for (TemplateProcessingRule templateProcessingRule : templateProcessing.getTemplateProcessingRules()) {
                    templateProcessingRule.evaluate(context);
                }
            }
            catch (TemplateProcessingInterrupt e) {
                switch (e.getInterruptType()) {
                    case EXIT_TEMPLATE:
                        /* Exit template processing */
                        logger.info("Template processing interrupted by exitTemplate");
                        return true;

                    case TEMPLATE_CONSTRAINT_FAILURE:
                        /* Failed templateCondition, so try again. */
                        logger.info("Template processing interrupted by failed templateConstraint");
                        return false;

                    default:
                        break;
                }
            }
        }
        return true;
    }
    
    /**
     * Set the responses for this assessmentItem.
     * The provided responses must be in the form responseIdentifier -> List of responses.
     * If A given response is A singleValue, then the list will only have one element.
     * 
     * Record cardinality responses are not supported!
     * 
     * @return a List of identifiers corresponding to responses which could not be successfully
     *   parsed. (E.g. expected a float but got a String)
     * 
     * @param responses Responses to set.
     */
    public List<String> setResponses(Map<String, List<String>> responses) {
        /* First set all responses bound to <endAttemptInteractions> to false initially.
         * These may be overridden for responses to the presented interactions below.
         * 
         * (The spec seems to indicate that ALL responses bound to these interactions
         * should be set, which is why we have this special code here.)
         */
        ItemBody itemBody = item.getItemBody();
        for (EndAttemptInteraction endAttemptInteraction : itemBody.search(EndAttemptInteraction.class)) {
            itemState.setResponseValue(endAttemptInteraction, BooleanValue.FALSE);
        }
        
        /* Now bind response values for each incoming response. (Note that this may be a subset
         * of all responses, since adaptive items will only present certain interactions at certain
         * times.) */
        List<String> badResponses = new ArrayList<String>();
        for (String responseIdentifier : responses.keySet()) {
            try {
                Interaction interaction = itemBody.getInteraction(new Identifier(responseIdentifier));
                if (interaction != null) {
                    interaction.bindResponse(this, responses.get(responseIdentifier));
                }
                else {
                    logger.warn("setResponses couldn't find interaction for identifier " + responseIdentifier);
                }
            }
            catch (ResponseBindingException e) {
                badResponses.add(responseIdentifier);
            }
        }
        return badResponses;
    }
    
    /**
     * Process the responses
     * @throws RuntimeValidationException 
     */
    public void processResponses() throws RuntimeValidationException {
        ItemProcessingContext processingContext = new ItemProcessingContextImpl();
        fireLifecycleEvent(LifecycleEventType.ITEM_RESPONSE_PROCESSING_STARTING);
        try {
            /* We always count the attempt, unless the response was to an endAttemptInteraction
             * with countAttempt set to false.
             */
            boolean countAttempt = true;
            for (Interaction interaction : item.getItemBody().getInteractions()) {
                if (interaction instanceof EndAttemptInteraction) {
                    EndAttemptInteraction endAttemptInteraction = (EndAttemptInteraction) interaction;
                    BooleanValue value = (BooleanValue) itemState.getResponseValue(interaction);
                    if (value!=null && value.booleanValue()==true) {
                        countAttempt = !Boolean.FALSE.equals(endAttemptInteraction.getCountAttempt());
                        break;
                    }
                }
            }
            if (countAttempt) {
                int oldAttempts = ((IntegerValue) itemState.getResponseValue(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER)).intValue();
                itemState.setResponseValue(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER, new IntegerValue(oldAttempts + 1));
            }
            
            if (!item.getAdaptive()) {
                for (OutcomeDeclaration outcomeDeclaration : item.getOutcomeDeclarations()) {
                    initValue(outcomeDeclaration);            
                }
            }
            
            ResponseProcessing responseProcessing = getResolvedResponseProcessing();
            if (responseProcessing != null) {
                responseProcessing.evaluate(processingContext);
            }
        }
        finally {
            fireLifecycleEvent(LifecycleEventType.ITEM_RESPONSE_PROCESSING_FINISHED);
        }
    }

    /**
     * Validate the responses set for each of the interactions
     * 
     * @return a List of identifiers corresponding to invalid responses. The List will be
     *   empty if all responses were valid.
     */
    public List<Identifier> validateResponses() {
        List<Identifier> invalidResponseIdentifiers = new ArrayList<Identifier>();
        for (Interaction interaction : item.getItemBody().getInteractions()) {
            Value responseValue = itemState.getResponseValue(interaction);
            if (!interaction.validateResponse(this, responseValue)) {
                invalidResponseIdentifiers.add(interaction.getResponseIdentifier());
            }
        }
        return invalidResponseIdentifiers;
    }
    
    //-------------------------------------------------------------------
    // Computes standalone ItemResult for this item. This wasn't available in the original JQTI
    
    public ItemResult computeItemResult() {
        ItemResult result = new ItemResult(null);
        result.setIdentifier(item.getIdentifier());
        result.setDateStamp(new Date());
        result.setSessionStatus(itemState.getNumAttempts()>0 ? SessionStatus.FINAL : SessionStatus.INITIAL); // TODO: Not really sure what's best here, but probably not important!
        recordItemVariables(result);
        return result;
    }
    
    void recordItemVariables(ItemResult result) {
        result.getItemVariables().clear();
        for (Entry<Identifier, Value> mapEntry : itemState.getOutcomeValues().entrySet()) {
            OutcomeDeclaration declaration = item.getOutcomeDeclaration(mapEntry.getKey());
            Value value = mapEntry.getValue();
            OutcomeVariable variable = new OutcomeVariable(result, declaration, value);
            result.getItemVariables().add(variable);
        }
        for (Entry<Identifier, Value> mapEntry : itemState.getResponseValues().entrySet()) {
            ResponseDeclaration declaration = item.getResponseDeclaration(mapEntry.getKey());
            Value value = mapEntry.getValue();
            List<Identifier> interactionChoiceOrder = null;
            Interaction interaction = item.getItemBody().getInteraction(declaration.getIdentifier());
            if (interaction != null && interaction instanceof Shuffleable) {
                interactionChoiceOrder = itemState.getShuffledInteractionChoiceOrder(interaction);
            }
            ResponseVariable variable = new ResponseVariable(result, declaration, value, interactionChoiceOrder);
            result.getItemVariables().add(variable);
        }
        for (Entry<Identifier, Value> mapEntry : itemState.getTemplateValues().entrySet()) {
            TemplateDeclaration declaration = item.getTemplateDeclaration(mapEntry.getKey());
            Value value = mapEntry.getValue();
            TemplateVariable variable = new TemplateVariable(result, declaration, value);
            result.getItemVariables().add(variable);
        }
    }
    
    //-------------------------------------------------------------------
    
    /**
     * FIXME-DM: THIS LOGIC IS PROBABLY WRONG!!! Judging whether a response is correct
     * is in general not simply a case of comparing with <correctResponse/>
     * 
     * Returns true if this item reference was correctly responded; 
     * Correctly responded means ALL defined responseVars match their associated correctResponse.
     * Returns null if any of the responseDeclarations don't have  correctResponses.
     *
     * @return true if this item reference was correctly responded; null if not all 
     * responseDeclarations contain correctResponses; false otherwise
     * @see #isIncorrect
     */
    public Boolean isCorrect() {
        for (ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (responseDeclaration.getCorrectResponse()==null) {
                return null;
            }
        }
        for (ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (!Boolean.TRUE.equals(responseDeclaration.isCorrectResponse(this))) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * FIXME-DM: THIS LOGIC IS PROBABLY WRONG!!! Judging whether a response is correct
     * is in general not simply a case of comparing with <correctResponse/>
     * 
     * Returns the number of correct responses 
     *
     * @return the number of correct responses 
     * @see #countIncorrect
     */
    public int countCorrect() {
        int count = 0;
        for (ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (Boolean.TRUE.equals(responseDeclaration.isCorrectResponse(this))) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * FIXME-DM: THIS LOGIC IS PROBABLY WRONG!!! Judging whether a response is correct
     * is in general not simply a case of comparing with <correctResponse/>
     * 
     * Returns true if this item reference was incorrectly responded; 
     * Incorrectly responded means ANY defined responseVars didn't match their 
     * associated correctResponse.
     * 
     * Returns null if any of the responseDeclarations don't have correctResponses.
     *
     * @return true if this item reference was incorrectly responded; null if not all 
     * responseDeclarations contain correctResponses; false otherwise
     * @see #isCorrect
     */
    public Boolean isIncorrect() {
        for (ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (responseDeclaration.getCorrectResponse()==null) {
                return null;
            }
        }
        for (ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (!Boolean.TRUE.equals(responseDeclaration.isCorrectResponse(this))) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * FIXME-DM: THIS LOGIC IS PROBABLY WRONG!!! Judging whether a response is correct
     * is in general not simply a case of comparing with <correctResponse/>
     * 
     * Returns the number of incorrect responses 
     *
     * @return the number of incorrect responses 
     * @see #countIncorrect
     */
    public int countIncorrect()    {
        int count = 0;
        for (ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (!Boolean.TRUE.equals(responseDeclaration.isCorrectResponse(this))) {
                count++;
            }
        }
        return count;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(itemManager=" + itemManager
            + ",itemState=" + itemState
            + ")";
    }
    
    //---------------------------------------------------

    /**
     * Callback implementation of {@link ItemProcessingContext}
     */
    protected class ItemProcessingContextImpl implements ItemProcessingContext {

        private static final long serialVersionUID = -6778262823346257573L;
        
        private final Map<String, Value> expressionValues;
        
        public ItemProcessingContextImpl() {
            this.expressionValues = new TreeMap<String, Value>();
        }
        
        public AssessmentItem getItem() {
            return item;
        }
        
        public AssessmentObject getOwner() {
            return item;
        }
        
        public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
            return itemManager.resolveVariableReference(variableReferenceIdentifier);
        }

        public ResponseProcessing getResolvedResponseProcessing() {
            return itemManager.getResolvedResponseProcessing();
        }
        
        public Value getExpressionValue(Expression expression) {
            return expressionValues.get(expression.computeXPath());
        }
        
        public Map<String, Value> exportExpressionValues() {
            return Collections.unmodifiableMap(expressionValues);
        }

        public void setExpressionValue(Expression expression, Value value) {
            expressionValues.put(expression.computeXPath(), value);
        }
        
        public void setTemplateValue(TemplateDeclaration variableDeclaration, Value value) {
            ConstraintUtilities.ensureNotNull(variableDeclaration);
            ConstraintUtilities.ensureNotNull(value);
            itemState.setTemplateValue(variableDeclaration, value);
        }
        
        public void setOutcomeValue(OutcomeDeclaration variableDeclaration, Value value) {
            ConstraintUtilities.ensureNotNull(variableDeclaration);
            ConstraintUtilities.ensureNotNull(value);
            itemState.setOutcomeValue(variableDeclaration, value);
        }
        
        public void setOutcomeValueFromLookupTable(OutcomeDeclaration outcomeDeclaration, NumberValue value) {
            ConstraintUtilities.ensureNotNull(outcomeDeclaration);
            ConstraintUtilities.ensureNotNull(value);
            itemState.setOutcomeValueFromLookupTable(outcomeDeclaration, value);
        }
        
        public void setVariableValue(VariableDeclaration variableDeclaration, Value value) {
            ConstraintUtilities.ensureNotNull(variableDeclaration);
            ConstraintUtilities.ensureNotNull(value);
            itemState.setValue(variableDeclaration, value);
        }
        
        public void setOverriddenResponseDefaultValue(ResponseDeclaration responseDeclaration, Value value) {
            ConstraintUtilities.ensureNotNull(responseDeclaration);
            ConstraintUtilities.ensureNotNull(value);
            itemState.setOverriddenResponseDefaultValue(responseDeclaration, value);
        }
        
        public void setOverriddenCorrectResponseValue(ResponseDeclaration responseDeclaration, Value value) {
            ConstraintUtilities.ensureNotNull(responseDeclaration);
            ConstraintUtilities.ensureNotNull(value);
            itemState.setOverriddenCorrectResponseValue(responseDeclaration, value);
        }
        
        public void setOverriddenOutcomeDefaultValue(OutcomeDeclaration outcomeDeclaration, Value value) {
            ConstraintUtilities.ensureNotNull(outcomeDeclaration);
            ConstraintUtilities.ensureNotNull(value);
            itemState.setOverriddenOutcomeDefaultValue(outcomeDeclaration, value);
        }

        public Value lookupVariable(VariableDeclaration variableDeclaration) {
            return AssessmentItemController.this.lookupVariable(variableDeclaration);
        }
        
        public Value lookupVariable(Identifier identifier) {
            return AssessmentItemController.this.lookupVariable(identifier);
        }
        
        public Value lookupVariable(Identifier identifier, VariableType... permittedTypes) {
            return AssessmentItemController.this.lookupVariable(identifier, permittedTypes);
        }
        
        /* DM: This copes with defaults and overridden values */
        public Value computeDefaultValue(Identifier identifier) {
            return AssessmentItemController.this.computeDefaultValue(identifier);
        }
        
        public Value computeDefaultValue(VariableDeclaration declaration) {
            return AssessmentItemController.this.computeDefaultValue(declaration);
        }

        public Value computeCorrectReponse(Identifier responseIdentifier) {
            ConstraintUtilities.ensureNotNull(responseIdentifier);
            return AssessmentItemController.this.computeCorrectResponse(ensureResponseDeclaration(responseIdentifier));
        }
        
        @Override
        public String toString() {
            return getClass().getSimpleName() + "@" + hashCode()
                + "(controller=" + AssessmentItemController.this
                + ",expressionValues=" + expressionValues
                + ")";
        }
    }
}
