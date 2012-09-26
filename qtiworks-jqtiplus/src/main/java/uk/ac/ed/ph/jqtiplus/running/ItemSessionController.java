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
package uk.ac.ed.ph.jqtiplus.running;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.LifecycleEventType;
import uk.ac.ed.ph.jqtiplus.exception.QtiEvaluationException;
import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.exception2.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.exception2.TemplateProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
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
import uk.ac.ed.ph.jqtiplus.node.result.ItemVariable;
import uk.ac.ed.ph.jqtiplus.node.result.OutcomeVariable;
import uk.ac.ed.ph.jqtiplus.node.result.ResponseVariable;
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;
import uk.ac.ed.ph.jqtiplus.node.result.TemplateVariable;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;
import uk.ac.ed.ph.jqtiplus.node.test.TemplateDefault;
import uk.ac.ed.ph.jqtiplus.notification.AbstractNotificationFirer;
import uk.ac.ed.ph.jqtiplus.notification.ModelNotification;
import uk.ac.ed.ph.jqtiplus.notification.NotificationListener;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Usage: one-shot, not thread safe.
 * FIXME: Document this!
 *
 * @author David McKain
 */
public final class ItemSessionController extends AbstractNotificationFirer implements ItemProcessingContext {

    private static final Logger logger = LoggerFactory.getLogger(ItemSessionController.class);

    /** TODO: Make this settable! */
    public static final int MAX_TEMPLATE_PROCESSING_TRIES = 100;

    private final List<NotificationListener> notificationListeners;

    private final JqtiExtensionManager jqtiExtensionManager;
    private final ResolvedAssessmentItem resolvedAssessmentItem;
    private final AssessmentItem item;
    private final ItemSessionState itemSessionState;

    public ItemSessionController(final JqtiExtensionManager jqtiExtensionManager, final ResolvedAssessmentItem resolvedAssessmentItem, final ItemSessionState itemSessionState) {
        Assert.notNull(resolvedAssessmentItem, "resolvedAssessmentItem");
        Assert.notNull(itemSessionState, "itemSessionState");
        this.notificationListeners = new ArrayList<NotificationListener>();
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.resolvedAssessmentItem = resolvedAssessmentItem;
        this.item = resolvedAssessmentItem.getItemLookup().extractAssumingSuccessful();
        this.itemSessionState = itemSessionState;
    }

    @Override
    public JqtiExtensionManager getJqtiExtensionManager() {
        return jqtiExtensionManager;
    }

    public ResolvedAssessmentItem getResolvedAssessmentItem() {
        return resolvedAssessmentItem;
    }

    @Override
    public AssessmentObject getSubject() {
        return item;
    }

    public AssessmentItem getItem() {
        return item;
    }

    @Override
    public AssessmentItem getSubjectItem() {
        return item;
    }

    @Override
    public ItemSessionState getItemSessionState() {
        return itemSessionState;
    }

    //-------------------------------------------------------------------

    public void addNotificationListener(final NotificationListener listener) {
        notificationListeners.add(listener);
    }

    public void removeNotificationListener(final NotificationListener listener) {
        notificationListeners.remove(listener);
    }

    @Override
    public void fireNotification(final ModelNotification notification) {
        for (final NotificationListener listener : notificationListeners) {
            listener.onNotification(notification);
        }
    }

    //-------------------------------------------------------------------
    // Initialization & template processing

    public void initialize() {
        initialize(null);
    }

    /**
     * Initialise the item by setting the template defaults, resetting variables,
     * and performing templateProcessing.
     * An item should only be initialised if it is going to be rendered/presented
     *
     * @param templateDefaults given templateDefaults values
     */
    public void initialize(final List<TemplateDefault> templateDefaults) {
        /* (We only allow initialization once. This contrasts with the original JQTI.) */
        if (itemSessionState.isInitialized()) {
            throw new IllegalStateException("Item state has already been initialized");
        }

        fireLifecycleEvent(LifecycleEventType.ITEM_INITIALISATION_STARTING);
        try {
            /* Initialise template defaults with any externally provided defaults */
            if (templateDefaults != null) {
                logger.trace("Setting template default values");
                for (final TemplateDefault templateDefault : templateDefaults) {
                    final TemplateDeclaration declaration = item.getTemplateDeclaration(templateDefault.getTemplateIdentifier());
                    if (declaration != null) {
                        final Value defaultValue = templateDefault.evaluate(this);
                        itemSessionState.setOverriddenTemplateDefaultValue(declaration.getIdentifier(), defaultValue);
                    }
                }
            }

            /* Perform template processing as many times as required. */
            int templateProcessingAttemptNumber = 0;
            boolean templateProcessingCompleted = false;
            while (!templateProcessingCompleted) {
                templateProcessingCompleted = doTemplateProcessingRun(++templateProcessingAttemptNumber);
            }
            if (templateProcessingAttemptNumber>1) {
                fireRuntimeInfo(item, "Template Processing was run " + templateProcessingAttemptNumber + " times");
            }

            /* Initialise all outcome variables */
            for (final OutcomeDeclaration outcomeDeclaration : item.getOutcomeDeclarations()) {
                initValue(outcomeDeclaration);
            }

            /* Initialise all response variables */
            for (final ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
                initValue(responseDeclaration);
            }

            /* Set special built-in variables */
            itemSessionState.setCompletionStatus(AssessmentItem.VALUE_ITEM_IS_NOT_ATTEMPTED);
            itemSessionState.setNumAttempts(0);
            itemSessionState.setCompletionStatus(AssessmentItem.VALUE_ITEM_IS_UNKNOWN);

            /* Initialize all interactions in the itemBody */
            for (final Interaction interaction : item.getItemBody().getInteractions()) {
                interaction.initialize(this);
            }

            itemSessionState.setInitialized(true);
        }
        finally {
            fireLifecycleEvent(LifecycleEventType.ITEM_INITIALISATION_FINISHED);
        }
    }

    private boolean doTemplateProcessingRun(final int attemptNumber) {
        logger.debug("Template Processing attempt #{} starting", attemptNumber);


        /* Initialise template values. */
        final TemplateProcessing templateProcessing = item.getTemplateProcessing();
        for (final TemplateDeclaration templateDeclaration : item.getTemplateDeclarations()) {
            initValue(templateDeclaration);
        }

        if (attemptNumber > MAX_TEMPLATE_PROCESSING_TRIES) {
            fireRuntimeWarning(item, "Exceeded maximum number " + MAX_TEMPLATE_PROCESSING_TRIES + " of template processing retries - leaving variables at default values");
            return true;
        }

        /* Perform templateProcessing. */
        if (templateProcessing != null) {
            logger.trace("Evaluating template processing rules");
            try {
                for (final TemplateProcessingRule templateProcessingRule : templateProcessing.getTemplateProcessingRules()) {
                    templateProcessingRule.evaluate(this);
                }
            }
            catch (final TemplateProcessingInterrupt e) {
                switch (e.getInterruptType()) {
                    case EXIT_TEMPLATE:
                        /* Exit template processing */
                        logger.trace("Template processing interrupted by exitTemplate");
                        return true;

                    case TEMPLATE_CONSTRAINT_FAILURE:
                        /* Failed templateCondition, so try again. */
                        logger.trace("Template processing interrupted by failed templateConstraint");
                        return false;

                    default:
                        break;
                }
            }
        }
        return true;
    }

    //-------------------------------------------------------------------
    // Response processing


    /**
     * Binds response variables for this assessmentItem, returning a List of response
     * variable identifiers for whom the given data could not be successfully bound.
     * <p>
     * This will modify {@link #itemSessionState}
     *
     * @return a {@link Set} of identifiers corresponding to response variables which could not be
     *         successfully bound from the provided data. (E.g. expected a float but got a String)
     * @param responseMap Map of responses to set, keyed on response variable identifier
     *
     * @throws IllegalArgumentException if responseMap is null, contains a null value, or if
     *   any key fails to map to an interaction
     */
    public Set<Identifier> bindResponses(final Map<Identifier, ResponseData> responseMap) {
        Assert.notNull(responseMap, "responseMap");
        logger.debug("Binding responses {}", responseMap);
        ensureInitialized();

        /* First set all responses bound to <endAttemptInteractions> to false initially.
         * These may be overridden for responses to the presented interactions below.
         *
         * (The spec seems to indicate that ALL responses bound to these interactions
         * should be set, which is why we have this special code here.)
         */
        final ItemBody itemBody = item.getItemBody();
        for (final EndAttemptInteraction endAttemptInteraction : QueryUtils.search(EndAttemptInteraction.class, itemBody)) {
            itemSessionState.setResponseValue(endAttemptInteraction, BooleanValue.FALSE);
        }

        /* Now bind response values for each incoming response. (Note that this may be a subset
         * of all responses, since adaptive items will only present certain interactions at certain
         * times.) */
        final Map<Identifier, Interaction> interactionMap = itemBody.getInteractionMap();
        final Set<Identifier> badResponses = new HashSet<Identifier>();
        for (final Entry<Identifier, ResponseData> responseEntry : responseMap.entrySet()) {
            final Identifier responseIdentifier = responseEntry.getKey();
            final ResponseData responseData = responseEntry.getValue();
            Assert.notNull(responseData, "responseMap entry for key " + responseIdentifier);
            try {
                final Interaction interaction = interactionMap.get(responseIdentifier);
                if (interaction != null) {
                    interaction.bindResponse(this, responseData);
                }
                else {
                    throw new IllegalArgumentException("No interaction found for response identifier " + responseIdentifier);
                }
            }
            catch (final ResponseBindingException e) {
                badResponses.add(responseIdentifier);
            }
        }
        return badResponses;
    }

    /**
     * Validates the currently-bound responses for each of the interactions
     *
     * @return a Set of identifiers corresponding to invalid responses. The Set will be
     *         empty if all responses were valid.
     */
    public Set<Identifier> validateResponses() {
        logger.debug("Validating responses");
        ensureInitialized();
        final Set<Identifier> invalidResponseIdentifiers = new HashSet<Identifier>();
        for (final Interaction interaction : item.getItemBody().getInteractions()) {
            final Value responseValue = itemSessionState.getResponseValue(interaction);
            if (!interaction.validateResponse(this, responseValue)) {
                invalidResponseIdentifiers.add(interaction.getResponseIdentifier());
            }
        }
        return invalidResponseIdentifiers;
    }

    /**
     * Runs response processing on the currently bound responses, changing {@link #itemSessionState}
     * as appropriate.
     */
    public void processResponses() {
        logger.debug("Response processing starting");
        ensureInitialized();
        fireLifecycleEvent(LifecycleEventType.ITEM_RESPONSE_PROCESSING_STARTING);
        try {
            /* We always count the attempt, unless the response was to an endAttemptInteraction
             * with countAttempt set to false.
             */
            boolean countAttempt = true;
            for (final Interaction interaction : item.getItemBody().getInteractions()) {
                if (interaction instanceof EndAttemptInteraction) {
                    final EndAttemptInteraction endAttemptInteraction = (EndAttemptInteraction) interaction;
                    final BooleanValue value = (BooleanValue) itemSessionState.getResponseValue(interaction);
                    if (value != null && value.booleanValue() == true) {
                        countAttempt = !endAttemptInteraction.getCountAttempt();
                        break;
                    }
                }
            }
            if (countAttempt) {
                final int oldAttempts = itemSessionState.getNumAttempts();
                itemSessionState.setNumAttempts(oldAttempts + 1);
            }

            if (!item.getAdaptive()) {
                for (final OutcomeDeclaration outcomeDeclaration : item.getOutcomeDeclarations()) {
                    initValue(outcomeDeclaration);
                }
            }

            ResponseProcessing responseProcessing = null;
            final RootNodeLookup<ResponseProcessing> resolvedResponseProcessingTemplateLookup = resolvedAssessmentItem.getResolvedResponseProcessingTemplateLookup();
            if (resolvedResponseProcessingTemplateLookup!=null) {
                responseProcessing = resolvedResponseProcessingTemplateLookup.extractAssumingSuccessful();
            }
            else {
                responseProcessing = item.getResponseProcessing();
            }

            if (responseProcessing != null) {
                responseProcessing.evaluate(this);
            }
            else {
                logger.debug("No responseProcessing rules or responseProcessing template exists, so no response processing will be performed");
            }
        }
        finally {
            logger.debug("Response processing finished");
            fireLifecycleEvent(LifecycleEventType.ITEM_RESPONSE_PROCESSING_FINISHED);
        }
    }


    //-------------------------------------------------------------------
    // Shuffle callbacks (from interactions)

    public <C extends Choice> void shuffleInteractionChoiceOrder(final Interaction interaction, final List<C> choiceList) {
        final List<List<C>> choiceLists = new ArrayList<List<C>>();
        choiceLists.add(choiceList);
        shuffleInteractionChoiceOrders(interaction, choiceLists);
    }

    public <C extends Choice> void shuffleInteractionChoiceOrders(final Interaction interaction, final List<List<C>> choiceLists) {
        if (interaction instanceof Shuffleable) {
            if (((Shuffleable) interaction).getShuffle()) {
                final List<Identifier> choiceIdentifiers = new ArrayList<Identifier>();
                for (final List<C> choiceList : choiceLists) {
                    final List<Identifier> shuffleableChoiceIdentifiers = new ArrayList<Identifier>();

                    /* Build up sortable identifiers */
                    for (int i = 0; i < choiceList.size(); i++) {
                        final C choice = choiceList.get(i);
                        if (!choice.getFixed()) {
                            shuffleableChoiceIdentifiers.add(choice.getIdentifier());
                        }
                    }

                    /* Perform shuffle */
                    Collections.shuffle(shuffleableChoiceIdentifiers);

                    /* Then merge fixed identifiers back in */
                    for (int i = 0, sortedIndex = 0; i < choiceList.size(); i++) {
                        final C choice = choiceList.get(i);
                        if (choice.getFixed()) {
                            choiceIdentifiers.add(choice.getIdentifier());
                        }
                        else {
                            choiceIdentifiers.add(shuffleableChoiceIdentifiers.get(sortedIndex++));
                        }
                    }
                }
                itemSessionState.setShuffledInteractionChoiceOrder(interaction, choiceIdentifiers);
            }
            else {
                itemSessionState.setShuffledInteractionChoiceOrder(interaction, null);
            }
        }
    }

    //-------------------------------------------------------------------

    @Override
    public Value lookupVariableValue(final Identifier identifier, final VariableType... permittedTypes) {
        Assert.notNull(identifier);
        Value value = null;
        if (permittedTypes.length==0) {
            /* No types specified, so allow any variable */
            value = itemSessionState.getVariableValue(identifier);
        }
        else {
            /* Only allows specified types of variables */
            CHECK_LOOP: for (final VariableType type : permittedTypes) {
                switch (type) {
                    case TEMPLATE:
                        value = itemSessionState.getTemplateValue(identifier);
                        break;

                    case RESPONSE:
                        value = itemSessionState.getResponseValue(identifier);
                        break;

                    case OUTCOME:
                        value = itemSessionState.getOutcomeValue(identifier);
                        break;

                    default:
                        throw new QtiLogicException("Unexpected switch case");
                }
                if (value!=null) {
                    break CHECK_LOOP;
                }
            }
        }
        if (value==null) {
            throw new QtiEvaluationException("No variable with identifier " + identifier
                    + " and permitted type(s) " + Arrays.toString(permittedTypes));
        }
        return value;
    }

    @Override
    public Value lookupVariableValue(final VariableReferenceIdentifier variableReferenceIdentifier, final VariableType... permittedTypes) {
        if (variableReferenceIdentifier.isDotted()) {
            throw new QtiEvaluationException("Dotted variables cannot be used in items");
        }
        final Identifier itemVariableIdentifier = variableReferenceIdentifier.getLocalIdentifier();
        return lookupVariableValue(itemVariableIdentifier, permittedTypes);
    }

    public Value lookupVariableValue(final String identifierString, final VariableType... permittedTypes) {
        Assert.notNull(identifierString);
        return lookupVariableValue(new Identifier(identifierString), permittedTypes);
    }

    @Override
    public Value computeDefaultValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return computeDefaultValue(ensureVariableDeclaration(identifier));
    }

    public Value computeDefaultValue(final String identifierString) {
        Assert.notNull(identifierString);
        return computeDefaultValue(ensureVariableDeclaration(new Identifier(identifierString)));
    }

    public Value computeDefaultValue(final VariableDeclaration declaration) {
        Assert.notNull(declaration);
        Value result = itemSessionState.getOverriddenDefaultValue(declaration);
        if (result == null) {
            final DefaultValue defaultValue = declaration.getDefaultValue();
            if (defaultValue != null) {
                result = defaultValue.evaluate();
            }
            else {
                result = NullValue.INSTANCE;
            }
        }
        return result;
    }

    private VariableDeclaration ensureVariableDeclaration(final Identifier identifier) {
        Assert.notNull(identifier);
        final VariableDeclaration result = item.getVariableDeclaration(identifier);
        if (result == null) {
            throw new QtiEvaluationException("Item variable with identifier " + identifier + " is not defined");
        }
        return result;
    }

    private ResponseDeclaration ensureResponseDeclaration(final Identifier responseIdentifier) {
        Assert.notNull(responseIdentifier);
        final ResponseDeclaration result = item.getResponseDeclaration(responseIdentifier);
        if (result == null) {
            throw new QtiEvaluationException("Response variable with identifier " + responseIdentifier + " is not defined");
        }
        return result;
    }

    @Override
    public Value computeCorrectResponse(final Identifier identifier) {
        Assert.notNull(identifier);
        return computeCorrectResponse(ensureResponseDeclaration(identifier));
    }

    public Value computeCorrectResponse(final String identifierString) {
        Assert.notNull(identifierString);
        return computeCorrectResponse(new Identifier(identifierString));
    }

    public Value computeCorrectResponse(final ResponseDeclaration declaration) {
        Assert.notNull(declaration);
        Value result = itemSessionState.getOverriddenCorrectResponseValue(declaration);
        if (result == null) {
            final CorrectResponse correctResponse = declaration.getCorrectResponse();
            if (correctResponse != null) {
                result = correctResponse.evaluate();
            }
            else {
                result = NullValue.INSTANCE;
            }
        }
        return result;
    }

    /**
     * FIXME: Returning Boolean is dodgy. Fix this API!
     *
     * Returns true if this declarations value matches its correctValue.
     * Returns null if there is no correct value
     * NOTE: This only tests for "the" "correct" response, not "a" correct response.
     *
     * @return true if the associated correctResponse matches the value; false or null otherwise.
     */
    public Boolean isCorrectResponse(final ResponseDeclaration responseDeclaration) {
        final Value correctResponseValue = computeCorrectResponse(responseDeclaration);
        if (correctResponseValue.isNull()) {
            return null;
        }
        return Boolean.valueOf(correctResponseValue.equals(itemSessionState.getVariableValue(responseDeclaration)));
    }

    //-------------------------------------------------------------------

    private void initValue(final VariableDeclaration declaration) {
        Assert.notNull(declaration);
        itemSessionState.setVariableValue(declaration, computeInitialValue(declaration));
    }

    private Value computeInitialValue(final Identifier identifier) {
        return computeDefaultValue(identifier);
    }

    private Value computeInitialValue(final VariableDeclaration declaration) {
        Assert.notNull(declaration);
        return computeInitialValue(declaration.getIdentifier());
    }


    //-------------------------------------------------------------------

    private void ensureInitialized() {
        if (!itemSessionState.isInitialized()) {
            throw new IllegalStateException("Item session has not been initialized");
        }
    }

    private void fireLifecycleEvent(final LifecycleEventType eventType) {
        if (jqtiExtensionManager!=null) {
            for (final JqtiExtensionPackage<?> extensionPackage : jqtiExtensionManager.getExtensionPackages()) {
                extensionPackage.lifecycleEvent(this, eventType);
            }
        }
    }

    //-------------------------------------------------------------------
    // Computes standalone ItemResult for this item. This wasn't available in the original JQTI

    public ItemResult computeItemResult() {
        final ItemResult result = new ItemResult(null);
        result.setIdentifier(item.getIdentifier());
        result.setDateStamp(new Date());
        result.setSessionStatus(itemSessionState.getNumAttempts() > 0 ? SessionStatus.FINAL : SessionStatus.INITIAL); // TODO: Not really sure what's best here, but probably not important!
        recordItemVariables(result);
        return result;
    }

    void recordItemVariables(final ItemResult result) {
        final List<ItemVariable> itemVariables = result.getItemVariables();
        itemVariables.clear();
        for (final Entry<Identifier, Value> mapEntry : itemSessionState.getOutcomeValues().entrySet()) {
            final OutcomeDeclaration declaration = item.getOutcomeDeclaration(mapEntry.getKey());
            final Value value = mapEntry.getValue();
            final OutcomeVariable variable = new OutcomeVariable(result, declaration, value);
            itemVariables.add(variable);
        }
        final Map<Identifier, Interaction> interactionMap = item.getItemBody().getInteractionMap();
        for (final Entry<Identifier, Value> mapEntry : itemSessionState.getResponseValues().entrySet()) {
            final ResponseDeclaration responseDeclaration = item.getResponseDeclaration(mapEntry.getKey());
            final Value value = mapEntry.getValue();
            List<Identifier> interactionChoiceOrder = null;
            final Interaction interaction = interactionMap.get(responseDeclaration.getIdentifier());
            if (interaction != null && interaction instanceof Shuffleable) {
                interactionChoiceOrder = itemSessionState.getShuffledInteractionChoiceOrder(interaction);
            }
            final ResponseVariable variable = new ResponseVariable(result, responseDeclaration, value, interactionChoiceOrder);
            itemVariables.add(variable);
        }
        for (final Entry<Identifier, Value> mapEntry : itemSessionState.getTemplateValues().entrySet()) {
            final TemplateDeclaration declaration = item.getTemplateDeclaration(mapEntry.getKey());
            final Value value = mapEntry.getValue();
            final TemplateVariable variable = new TemplateVariable(result, declaration, value);
            itemVariables.add(variable);
        }
    }

    //-------------------------------------------------------------------

    /**
     * Determines whether a further attempt is allowed on this item.
     *
     * FIXME: Ideally, this should be called with something resembling {@link ItemSessionControl}
     *   that could be used in both standalone and within-test fashion.
     * FIXME: This is not integrated into tests yet
     * (New in JQTI+)
     *
     * @param maxAttempts maximum number of attempts. This is only used for non-adaptive items,
     *   and 0 is treated as "unlimited".
     * @return true if a further attempt is allowed, false otherwise.
     */
    public boolean isAttemptAllowed(final int maxAttempts) {
        boolean attemptAllowed;
        if (item.getAdaptive()) {
            /* For adaptive items, attempts are limited by the value of the completion status variable */
            final String completionStatus = itemSessionState.getCompletionStatus();
            attemptAllowed = !AssessmentItem.VALUE_ITEM_IS_COMPLETED.equals(completionStatus);
        }
        else {
            /* Non-adaptive items use maxAttempts, with 0 treated as unlimited */
            final int numAttempts = itemSessionState.getNumAttempts();
            attemptAllowed = (maxAttempts==0 || numAttempts < maxAttempts);
        }
        return attemptAllowed;
    }

    /**
     * FIXME: Returning Boolean is dodgy. Fix this API!
     *
     * FIXME-DM: THIS LOGIC IS PROBABLY WRONG!!! Judging whether a response is correct
     * is in general not simply a case of comparing with <correctResponse/>
     * Returns true if this item reference was correctly responded;
     * Correctly responded means ALL defined responseVars match their associated correctResponse.
     * Returns null if any of the responseDeclarations don't have correctResponses.
     *
     * @return true if this item reference was correctly responded; null if not all
     *         responseDeclarations contain correctResponses; false otherwise
     * @see #isIncorrect
     */
    public Boolean isCorrect() {
        for (final ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (responseDeclaration.getCorrectResponse() == null) {
                return null;
            }
        }
        for (final ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (!Boolean.TRUE.equals(isCorrectResponse(responseDeclaration))) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * FIXME-DM: THIS LOGIC IS PROBABLY WRONG!!! Judging whether a response is correct
     * is in general not simply a case of comparing with <correctResponse/>
     * Returns the number of correct responses
     *
     * @return the number of correct responses
     * @see #countIncorrect
     */
    public int countCorrect() {
        int count = 0;
        for (final ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (Boolean.TRUE.equals(isCorrectResponse(responseDeclaration))) {
                count++;
            }
        }
        return count;
    }

    /**
     * FIXME: Returning Boolean is dodgy. Fix this API!
     *
     * FIXME-DM: THIS LOGIC IS PROBABLY WRONG!!! Judging whether a response is correct
     * is in general not simply a case of comparing with <correctResponse/>
     * Returns true if this item reference was incorrectly responded;
     * Incorrectly responded means ANY defined responseVars didn't match their
     * associated correctResponse.
     * Returns null if any of the responseDeclarations don't have correctResponses.
     *
     * @return true if this item reference was incorrectly responded; null if not all
     *         responseDeclarations contain correctResponses; false otherwise
     * @see #isCorrect
     */
    public Boolean isIncorrect() {
        for (final ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (responseDeclaration.getCorrectResponse() == null) {
                return null;
            }
        }
        for (final ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (!Boolean.TRUE.equals(isCorrectResponse(responseDeclaration))) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * FIXME-DM: THIS LOGIC IS PROBABLY WRONG!!! Judging whether a response is correct
     * is in general not simply a case of comparing with <correctResponse/>
     * Returns the number of incorrect responses
     *
     * @return the number of incorrect responses
     * @see #countIncorrect
     */
    public int countIncorrect() {
        int count = 0;
        for (final ResponseDeclaration responseDeclaration : item.getResponseDeclarations()) {
            if (!Boolean.TRUE.equals(isCorrectResponse(responseDeclaration))) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(resolvedAssessmentItem=" + resolvedAssessmentItem
                + ",itemSessionState=" + itemSessionState
                + ")";
    }
}
