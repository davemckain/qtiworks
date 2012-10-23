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
import uk.ac.ed.ph.jqtiplus.exception2.QtiInvalidLookupException;
import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.exception2.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.exception2.TemplateProcessingInterrupt;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
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
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationController;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Usage: one-shot, not thread safe.
 * FIXME: Document this!
 *
 * @author David McKain
 */
public final class ItemSessionController extends ItemValidationController implements ItemProcessingContext {

    private static final Logger logger = LoggerFactory.getLogger(ItemSessionController.class);

    /** TODO: Make this settable! */
    public static final int MAX_TEMPLATE_PROCESSING_TRIES = 100;

    private final ItemProcessingMap itemProcessingMap;
    private final ItemSessionState itemSessionState;

    private Long randomSeed;
    private Random randomGenerator;

    public ItemSessionController(final JqtiExtensionManager jqtiExtensionManager,
            final ItemProcessingMap itemProcessingMap, final ItemSessionState itemSessionState) {
        super(jqtiExtensionManager, itemProcessingMap!=null ? itemProcessingMap.getResolvedAssessmentItem() : null);
        Assert.notNull(itemSessionState, "itemSessionState");
        this.itemProcessingMap = itemProcessingMap;
        this.itemSessionState = itemSessionState;
        this.randomSeed = null;
        this.randomGenerator = null;
    }

    @Override
    public ResolvedAssessmentItem getResolvedAssessmentItem() {
        return resolvedAssessmentItem;
    }

    @Override
    public AssessmentItem getSubjectItem() {
        return item;
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

    private void fireLifecycleEvent(final LifecycleEventType eventType) {
        if (jqtiExtensionManager!=null) {
            for (final JqtiExtensionPackage<?> extensionPackage : jqtiExtensionManager.getExtensionPackages()) {
                extensionPackage.lifecycleEvent(this, eventType);
            }
        }
    }

    //-------------------------------------------------------------------
    // Initialization & template processing

    /**
     * Sets all explicitly-defined (valid) variables to NULL, and the
     * built-in variables to their initial values.
     */
    public void initialize() {
        itemSessionState.reset();
        for (final Identifier identifier : itemProcessingMap.getValidTemplateDeclarationMap().keySet()) {
            itemSessionState.setTemplateValue(identifier, NullValue.INSTANCE);
        }
        for (final Identifier identifier : itemProcessingMap.getValidResponseDeclarationMap().keySet()) {
            itemSessionState.setResponseValue(identifier, NullValue.INSTANCE);
        }
        for (final Identifier identifier : itemProcessingMap.getValidOutcomeDeclarationMap().keySet()) {
            itemSessionState.setOutcomeValue(identifier, NullValue.INSTANCE);
        }
        itemSessionState.resetBuiltinVariables();
    }

    /**
     * Performs template processing, with no <code>templateDefaults</code>.
     */
    public void performTemplateProcessing() {
        performTemplateProcessing(null);
    }

    /**
     * Performs template processing using the given <code>templateDefaults</code>.
     *
     * @param templateDefaults List of {@link TemplateDefault}s, which may be null or empty.
     */
    public void performTemplateProcessing(final List<TemplateDefault> templateDefaults) {
        fireLifecycleEvent(LifecycleEventType.ITEM_TEMPLATE_PROCESSING_STARTING);
        try {
            /* Initialise template defaults with any externally provided defaults */
            if (templateDefaults != null) {
                logger.trace("Setting template default values");
                for (final TemplateDefault templateDefault : templateDefaults) {
                    final TemplateDeclaration templateDeclaration = itemProcessingMap.getValidTemplateDeclarationMap().get(templateDefault.getTemplateIdentifier());
                    if (templateDeclaration!=null) {
                        final Value defaultValue = templateDefault.evaluate(this);
                        itemSessionState.setOverriddenTemplateDefaultValue(templateDeclaration.getIdentifier(), defaultValue);
                    }
                    else {
                        fireRuntimeWarning(templateDefault, "Ignoring templateDefault '" + templateDefault.getTemplateIdentifier()
                                + "' as variable identifier is not unique");
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

            /* Initialise all outcome and response variables to their default values */
            resetOutcomeVariables();
            resetResponseVariables();

            /* Set special built-in response and outcome variables */
            itemSessionState.setNumAttempts(0);
            itemSessionState.setCompletionStatus(AssessmentItem.VALUE_ITEM_IS_UNKNOWN);

            /* Initialize all interactions */
            for (final Interaction interaction : itemProcessingMap.getInteractions()) {
                interaction.initialize(this);
            }
        }
        finally {
            fireLifecycleEvent(LifecycleEventType.ITEM_TEMPLATE_PROCESSING_FINISHED);
        }
    }



    private boolean doTemplateProcessingRun(final int attemptNumber) {
        logger.debug("Template Processing attempt #{} starting", attemptNumber);

        /* Reset template variables */
        resetTemplateVariables();

        if (attemptNumber > MAX_TEMPLATE_PROCESSING_TRIES) {
            fireRuntimeWarning(item, "Exceeded maximum number " + MAX_TEMPLATE_PROCESSING_TRIES + " of template processing retries - leaving variables at default values");
            return true;
        }

        /* Perform templateProcessing. */
        final TemplateProcessing templateProcessing = item.getTemplateProcessing();
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
     * Binds response variables for this assessmentItem, returning a Set of response
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

        /* First set all responses bound to <endAttemptInteractions> to false initially.
         * These may be overridden for responses to the presented interactions below.
         *
         * (The spec seems to indicate that ALL responses bound to these interactions
         * should be set, which is why we have this special code here.)
         */
        for (final Interaction interaction : itemProcessingMap.getInteractions()) {
            if (interaction instanceof EndAttemptInteraction) {
                itemSessionState.setResponseValue(interaction, BooleanValue.FALSE);
            }
        }

        final Map<Identifier, Interaction> interactionByResponseIdentifierMap = itemProcessingMap.getInteractionByResponseIdentifierMap();
        final Set<Identifier> badResponses = new HashSet<Identifier>();
        for (final Entry<Identifier, ResponseData> responseEntry : responseMap.entrySet()) {
            final Identifier responseIdentifier = responseEntry.getKey();
            final ResponseData responseData = responseEntry.getValue();
            Assert.notNull(responseData, "responseMap entry for key " + responseIdentifier);
            try {
                final Interaction interaction = interactionByResponseIdentifierMap.get(responseIdentifier);
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
        return Collections.unmodifiableSet(badResponses);
    }

    /**
     * Validates the currently-bound responses for each of the interactions
     *
     * @return a Set of identifiers corresponding to invalid responses. The Set will be
     *         empty if all responses were valid.
     */
    public Set<Identifier> validateResponses() {
        logger.debug("Validating responses");
        final Set<Identifier> invalidResponseIdentifiers = new HashSet<Identifier>();
        for (final Interaction interaction : itemProcessingMap.getInteractions()) {
            final Value responseValue = itemSessionState.getResponseValue(interaction);
            if (!interaction.validateResponse(this, responseValue)) {
                invalidResponseIdentifiers.add(interaction.getResponseIdentifier());
            }
        }
        return Collections.unmodifiableSet(invalidResponseIdentifiers);
    }

    /**
     * Runs response processing on the currently bound responses, changing {@link #itemSessionState}
     * as appropriate.
     */
    public void performResponseProcessing() {
        logger.debug("Response processing starting");
        fireLifecycleEvent(LifecycleEventType.ITEM_RESPONSE_PROCESSING_STARTING);
        try {
            /* We always count the attempt, unless the response was to an endAttemptInteraction
             * with countAttempt set to false.
             */
            boolean countAttempt = true;
            for (final Interaction interaction : itemProcessingMap.getInteractions()) {
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

            /* For non-adaptive items, reset outcome variables to default values */
            if (!item.getAdaptive()) {
                resetOutcomeVariables();
            }

            ResponseProcessing responseProcessing = null;
            final RootNodeLookup<ResponseProcessing> resolvedResponseProcessingTemplateLookup = resolvedAssessmentItem.getResolvedResponseProcessingTemplateLookup();
            if (resolvedResponseProcessingTemplateLookup!=null) {
                responseProcessing = resolvedResponseProcessingTemplateLookup.extractAssumingSuccessful();
            }
            else {
                responseProcessing = item.getResponseProcessing();
            }

            /* Invoke response processing */
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
        else {
            throw new IllegalStateException("Interaction '" + interaction.getQtiClassName()
                    + "' attempted shuffling but does not implement Shuffleable interface");
        }
    }

    //-------------------------------------------------------------------

    @Override
    public VariableDeclaration ensureVariableDeclaration(final Identifier identifier, final VariableType... permittedTypes) {
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
    public Value evaluateVariableValue(final Identifier identifier, final VariableType... permittedTypes) {
        Assert.notNull(identifier);
        if (!itemProcessingMap.isValidVariableIdentifier(identifier)) {
            throw new QtiInvalidLookupException(identifier);
        }
        final Value value = getVariableValue(identifier, permittedTypes);
        if (value==null) {
            throw new IllegalStateException("ItemSessionState lookup of variable " + identifier + " returned NULL, indicating state is not in sync");
        }
        return value;
    }

    private Value getVariableValue(final Identifier identifier, final VariableType... permittedTypes) {
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
                        throw new QtiLogicException("Unexpected switch case: " + type);
                }
                if (value!=null) {
                    break CHECK_LOOP;
                }
            }
        }
        return value;
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
    public Value computeDefaultValue(final Identifier identifier) {
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
    public Value computeDefaultValue(final VariableDeclaration declaration) {
        Assert.notNull(declaration);
        Value result = itemSessionState.getOverriddenDefaultValue(declaration);
        if (result==null) {
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

    /**
     * Computes the current correct response for the {@link ResponseDeclaration} having the
     * given {@link Identifier}. The result will be not null (though may be a {@link NullValue}).
     *
     * @param identifier identifier of the required variable, which must not be null
     * @return computed correct response value, which will not be null.
     *
     * @throws QtiInvalidLookupException
     */
    @Override
    public Value computeCorrectResponse(final Identifier identifier) {
        Assert.notNull(identifier);
        return computeCorrectResponse((ResponseDeclaration) ensureVariableDeclaration(identifier, VariableType.RESPONSE));
    }

    /**
     * Computes the current correct response for the {@link ResponseDeclaration} having the
     * given {@link Identifier}. The result will be not null (though may be a {@link NullValue}).
     *
     * @param identifier identifier of the required variable, which must not be null
     * @return computed correct response value, which will not be null.
     */
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

    //-------------------------------------------------------------------

    private void resetTemplateVariables() {
        for (final TemplateDeclaration templateDeclaration : itemProcessingMap.getValidTemplateDeclarationMap().values()) {
            initValue(templateDeclaration);
        }
    }

    private void resetResponseVariables() {
        for (final ResponseDeclaration responseDeclaration : itemProcessingMap.getValidResponseDeclarationMap().values()) {
            if (!responseDeclaration.getIdentifier().equals(AssessmentItem.VARIABLE_DURATION_IDENTIFIER) &&
                    !responseDeclaration.getIdentifier().equals(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS_IDENTIFIER)) {
                initValue(responseDeclaration);
            }
        }
    }

    private void resetOutcomeVariables() {
        for (final OutcomeDeclaration outcomeDeclaration : itemProcessingMap.getValidOutcomeDeclarationMap().values()) {
            if (!outcomeDeclaration.getIdentifier().equals(AssessmentItem.VARIABLE_COMPLETION_STATUS_IDENTIFIER)) {
                initValue(outcomeDeclaration);
            }
        }
    }

    private void initValue(final VariableDeclaration declaration) {
        Assert.notNull(declaration);
        itemSessionState.setVariableValue(declaration, computeInitialValue(declaration));
    }

    private Value computeInitialValue(final VariableDeclaration declaration) {
        Assert.notNull(declaration);
        return computeInitialValue(declaration.getIdentifier());
    }

    private Value computeInitialValue(final Identifier identifier) {
        return computeDefaultValue(identifier);
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

    public void recordItemVariables(final ItemResult result) {
        final List<ItemVariable> itemVariables = result.getItemVariables();
        itemVariables.clear();
        for (final Entry<Identifier, Value> mapEntry : itemSessionState.getOutcomeValues().entrySet()) {
            final OutcomeDeclaration declaration = itemProcessingMap.getValidOutcomeDeclarationMap().get(mapEntry.getKey());
            if (declaration!=null) {
                final Value value = mapEntry.getValue();
                final OutcomeVariable variable = new OutcomeVariable(result, declaration, value);
                itemVariables.add(variable);
            }
        }
        final Map<Identifier, Interaction> interactionMap = itemProcessingMap.getInteractionByResponseIdentifierMap();
        for (final Entry<Identifier, Value> mapEntry : itemSessionState.getResponseValues().entrySet()) {
            final ResponseDeclaration declaration = itemProcessingMap.getValidResponseDeclarationMap().get(mapEntry.getKey());
            if (declaration!=null) {
                final Value value = mapEntry.getValue();
                List<Identifier> interactionChoiceOrder = null;
                final Interaction interaction = interactionMap.get(declaration.getIdentifier());
                if (interaction != null && interaction instanceof Shuffleable) {
                    interactionChoiceOrder = itemSessionState.getShuffledInteractionChoiceOrder(interaction);
                }
                final ResponseVariable variable = new ResponseVariable(result, declaration, value, interactionChoiceOrder);
                itemVariables.add(variable);
            }
        }
        for (final Entry<Identifier, Value> mapEntry : itemSessionState.getTemplateValues().entrySet()) {
            final TemplateDeclaration declaration = itemProcessingMap.getValidTemplateDeclarationMap().get(mapEntry.getKey());
            if (declaration!=null) {
                final Value value = mapEntry.getValue();
                final TemplateVariable variable = new TemplateVariable(result, declaration, value);
                itemVariables.add(variable);
            }
        }
    }

    //-------------------------------------------------------------------

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
