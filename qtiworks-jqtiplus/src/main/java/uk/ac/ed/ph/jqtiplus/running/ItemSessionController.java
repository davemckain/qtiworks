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
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.JqtiLifecycleEventType;
import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.exception.QtiCandidateStateException;
import uk.ac.ed.ph.jqtiplus.exception.QtiInvalidLookupException;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.exception.ResponseBindingException;
import uk.ac.ed.ph.jqtiplus.exception.TemplateProcessingInterrupt;
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
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.SetCorrectResponse;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateProcessing;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateProcessingRule;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.result.Context;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.node.result.ItemVariable;
import uk.ac.ed.ph.jqtiplus.node.result.OutcomeVariable;
import uk.ac.ed.ph.jqtiplus.node.result.ResponseVariable;
import uk.ac.ed.ph.jqtiplus.node.result.SessionIdentifier;
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;
import uk.ac.ed.ph.jqtiplus.node.result.TemplateVariable;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.node.test.TemplateDefault;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.RootNodeLookup;
import uk.ac.ed.ph.jqtiplus.state.ControlObjectState;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationController;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.net.URI;
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
 * FIXME: Document this!
 *
 * FIXME: This is currently being refactored. Need to finish off replacing presented/closed with
 * equivalents in {@link ControlObjectState}, also need to change the way durations are managed.
 *
 * FIXME: There's too much in here. Let's split off the ProcessingContext callbacks from the actual
 * run logic.
 *
 * Usage: one-shot, not thread safe.
 *
 * Current lifecycle is: (FIXME - this needs updated)
 *
 * {@link #initialize()}
 * {@link #performTemplateProcessing()}
 * {@link #enterItem(Date)}
 * {@link #markPendingSubmission()}
 * {@link #markPendingResponseProcessing()}
 * {@link #bindResponses(Map)}
 * {@link #performResponseProcessing()}
 * {@link #markClosed()}
 *
 * @author David McKain
 */
public final class ItemSessionController extends ItemValidationController implements ItemProcessingContext {

    private static final Logger logger = LoggerFactory.getLogger(ItemSessionController.class);

    private final ItemSessionControllerSettings itemSessionControllerSettings;
    private final ItemProcessingMap itemProcessingMap;
    private final ItemSessionState itemSessionState;

    private Long randomSeed;
    private Random randomGenerator;

    public ItemSessionController(final JqtiExtensionManager jqtiExtensionManager,
            final ItemSessionControllerSettings itemSessionControllerSettings,
            final ItemProcessingMap itemProcessingMap, final ItemSessionState itemSessionState) {
        super(jqtiExtensionManager, itemProcessingMap!=null ? itemProcessingMap.getResolvedAssessmentItem() : null);
        Assert.notNull(itemSessionControllerSettings, "itemSessionControllerSettings");
        Assert.notNull(itemSessionState, "itemSessionState");
        this.itemSessionControllerSettings = new ItemSessionControllerSettings(itemSessionControllerSettings);
        this.itemProcessingMap = itemProcessingMap;
        this.itemSessionState = itemSessionState;
        this.randomSeed = null;
        this.randomGenerator = null;
    }

    public ItemSessionControllerSettings getItemSessionControllerSettings() {
        return itemSessionControllerSettings;
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

    private void fireJqtiLifecycleEvent(final JqtiLifecycleEventType eventType) {
        for (final JqtiExtensionPackage<?> jqtiExtensionPackage : jqtiExtensionManager.getExtensionPackages()) {
            jqtiExtensionPackage.lifecycleEvent(this, eventType);
        }
    }

    //-------------------------------------------------------------------
    // Initialization

    /**
     * Resets the current {@link ItemSessionState} then sets all explicitly-defined
     * (valid) variables to their default values, and the built-in variables to their
     * initial values. Interactions supporting shuffling will have their random orders
     * selected.
     * <p>
     * Preconditions: None. This can be called at any time; any existing state will
     *   be wiped.
     * <p>
     * Postconditions: All explicitly-defined variables are reset to their default values.
     *   Built-in variables are set to initial values. Interaction shuffle orders will
     *   have been chosen. {@link ItemSessionState#isInitialized()} will return true.
     *
     * @param timestamp for this operation.
     */
    public void initialize(final Date timestamp) {
        Assert.notNull(timestamp);
        logger.debug("Initializing item {}", getSubject().getSystemId());

        /* Reset all state */
        itemSessionState.reset();
        resetTemplateVariables();
        resetResponseState();
        resetOutcomeVariables();
        itemSessionState.setSessionStatus(SessionStatus.INITIAL);
        itemSessionState.setInitialized(true);

        /* Initialize all interactions */
        for (final Interaction interaction : itemProcessingMap.getInteractions()) {
            interaction.initialize(this);
        }

        /* Update closed status. (This normally won't do anything, but a pathological
         * question might have completionStatus having a default value of completed!
         */
        updateClosedStatus(timestamp);
    }

    private void ensureInitialized() {
        if (!itemSessionState.isInitialized()) {
            throw new QtiCandidateStateException("ItemSessionState has not been initialized");
        }
    }

    private void ensureEntered() {
        ensureInitialized();
        if (!itemSessionState.isEntered()) {
            throw new QtiCandidateStateException("Item has not been entered");
        }
    }

    private void ensureOpen() {
        ensureEntered();
        if (itemSessionState.isEnded()) {
            throw new QtiCandidateStateException("ItemSession is ended");
        }
    }

    private void ensureEnded() {
        ensureInitialized();
        if (!itemSessionState.isEnded()) {
            throw new QtiCandidateStateException("ItemSession has not been ended");
        }
    }

    /**
     * Checks whether a further attempt is allowed on this item, updating
     * {@link ItemSessionState#isClosed()} as appropriate.
     * <p>
     * This must be called after every attempt and every processing run
     * to update the state.
     *
     * (New in JQTI+)
     */
    private void updateClosedStatus(final Date timestamp) {
        boolean shouldClose;
        if (item.getAdaptive()) {
            /* For adaptive items, attempts are limited by the value of the completion status variable */
            final String completionStatus = itemSessionState.getCompletionStatus();
            shouldClose = QtiConstants.COMPLETION_STATUS_COMPLETED.equals(completionStatus);
        }
        else {
            /* Non-adaptive items use maxAttempts, with 0 treated as unlimited */
            final int maxAttempts = itemSessionControllerSettings.getMaxAttempts();
            final int numAttempts = itemSessionState.getNumAttempts();
            shouldClose = (maxAttempts>0 && numAttempts>=maxAttempts);
        }
        itemSessionState.setEndTime(shouldClose ? timestamp : null);
    }

    //-------------------------------------------------------------------
    // Template processing

    /**
     * Performs Template Processing, with no <code>templateDefaults</code>, then
     * resets Response and Outcome variables to their default values.
     * <p>
     * Pre-condition: {@link ItemSessionState} must have been initialized. Candidate
     *   must not have entered the item.
     * <p>
     * Post-condition: Template variables will be initialized. Response and Outcome
     *   variables will be reset to their default values.
     *
     * @see #performTemplateProcessing(Date, List)
     */
    public void performTemplateProcessing(final Date timestamp) {
        performTemplateProcessing(timestamp, null);
    }

    /**
     * Performs Template Processing using the given <code>templateDefaults</code>,
     * then resets Response and Outcome variables to their default values.
     * <p>
     * Pre-condition: {@link ItemSessionState} must have been initialized. Candidate
     *   must not have entered the item.
     * <p>
     * Post-condition: Template variables will be initialized. Response and Outcome
     *   variables will be reset to their default values.
     *
     * @see #performTemplateProcessing(Date)
     *
     * @param templateDefaults List of {@link TemplateDefault}s, which may be null or empty.
     */
    public void performTemplateProcessing(final Date timestamp, final List<TemplateDefault> templateDefaults) {
        Assert.notNull(timestamp);
        ensureNotEntered();
        logger.debug("Template processing starting on item {}", getSubject().getSystemId());

        fireJqtiLifecycleEvent(JqtiLifecycleEventType.ITEM_TEMPLATE_PROCESSING_STARTING);
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

            /* Initialize all interactions */
            for (final Interaction interaction : itemProcessingMap.getInteractions()) {
                interaction.initialize(this);
            }

            /* Reset OVs and RVs session */
            resetOutcomeAndResponseVariables();
            updateClosedStatus(timestamp);
        }
        finally {
            fireJqtiLifecycleEvent(JqtiLifecycleEventType.ITEM_TEMPLATE_PROCESSING_FINISHED);
            logger.debug("Template processing finished on item {}", getSubject().getSystemId());
        }
    }

    private void resetOutcomeAndResponseVariables() {
        resetOutcomeVariables();
        resetResponseState();
        itemSessionState.setSessionStatus(SessionStatus.INITIAL);
    }

    private boolean doTemplateProcessingRun(final int attemptNumber) {
        logger.debug("Template Processing attempt #{} starting", attemptNumber);

        /* Reset template variables */
        resetTemplateVariables();

        final int maxTemplateProcessingTries = itemSessionControllerSettings.getTemplateProcessingLimit();
        if (attemptNumber > maxTemplateProcessingTries) {
            fireRuntimeWarning(item, "Exceeded maximum number " + maxTemplateProcessingTries + " of template processing retries - leaving variables at default values");
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

    private void ensureNotEntered() {
        ensureInitialized();
        if (itemSessionState.isEntered()) {
            throw new QtiCandidateStateException("Expected itemSessionState.isEntered() => false");
        }
    }

    //-------------------------------------------------------------------
    // Entry, Reset and Exit

    /**
     * "Enters" the item, marking it as having been presented.
     * <p>
     * Pre-condition: Item Session must have been initialized and not already entered.
     * <p>
     * Post-condition: Item Session will have its entry time set and the duration timer
     * will start. The {@link SessionStatus} will be changed to {@link SessionStatus#PENDING_SUBMISSION}
     *
     * @param timestamp
     */
    public void enterItem(final Date timestamp) {
        Assert.notNull(timestamp);
        ensureNotEntered();
        logger.debug("Entering item {}", getSubject().getSystemId());

        /* Record entry */
        itemSessionState.setEntryTime(timestamp);

        /* Check closed status */
        updateClosedStatus(timestamp); /* (This can't change anything here but I'll keep for completeness) */

        if (!itemSessionState.isClosed()) {
            /* Update SessionStatus */
            itemSessionState.setSessionStatus(SessionStatus.PENDING_SUBMISSION);

            /* Start the duration timer */
            startItemSessionTimer(itemSessionState, timestamp);
        }
    }

    /**
     * Touches (updates) the <code>duration</code> variable in the item session if the session is
     * currently open. Call this
     * method before rendering and other operations that use rather than change the session state.
     * <p>
     * Pre-condition: Item Session must have been initialised
     * <p>
     * Post-condition: Duration variable will be updated (if the item session is currently open)
     * @param timestamp
     */
    public void touchDuration(final Date timestamp) {
        Assert.notNull(timestamp);
        ensureInitialized();
        logger.debug("Touching duration for item {}", getSubject().getSystemId());

        if (!itemSessionState.isEnded()) {
            endItemSessionTimer(itemSessionState, timestamp);
            startItemSessionTimer(itemSessionState, timestamp);
        }
    }

    /**
     * Resets the item session back to the state it was immediately after template processing.
     * <p>
     * Pre-condition: Item Session must have been entered.
     * <p>
     * Post-condition: Item Session will be reset back to the state it was on entry. I.e.
     *   Template Variables will have the values they had after init and template processing;
     *   Outcome and Response variables will be reset. The duration timer is restarted but not
     *   reset.
     */
    public void resetItemSession(final Date timestamp) {
        Assert.notNull(timestamp);
        ensureEntered();
        logger.debug("Resetting item session {}", getSubject().getSystemId());

        /* Stop duration timer */
        endItemSessionTimer(itemSessionState, timestamp);

        /* Reset OVs and RVs */
        resetOutcomeAndResponseVariables();

        /* Check closed status */
        updateClosedStatus(timestamp);

        if (!itemSessionState.isClosed()) {
            /* Update SessionStatus */
            itemSessionState.setSessionStatus(SessionStatus.PENDING_SUBMISSION);

            /* ASK: We are not reseting the duration here. Is this the most useful behaviour? */
            startItemSessionTimer(itemSessionState, timestamp);
        }
    }

    /**
     * Ends (closes) the item session.
     * <p>
     * Pre-condition: Item must have been entered.
     * <p>
     * Post-conditions: Item session state will be marked as ended (closed). Duration
     *   timer will be stopped.
     *
     * @param timestamp
     */
    public void endItem(final Date timestamp) {
        Assert.notNull(timestamp);
        ensureEntered();
        logger.debug("Ending item {}", getSubject().getSystemId());

        itemSessionState.setEndTime(timestamp);
        endItemSessionTimer(itemSessionState, timestamp);
    }

    /**
     * Exits the item session. (This isn't strictly needed when running single items.)
     * <p>
     * Pre-condition: Item session must have been ended (closed)
     * <p>
     * Post-condiiton: Exit time will be recorded.
     *
     * @param timestamp
     */
    public void exitItem(final Date timestamp) {
        Assert.notNull(timestamp);
        ensureEnded();
        logger.debug("Exiting item {}", getSubject().getSystemId());

        itemSessionState.setExitTime(timestamp);
    }

    //-------------------------------------------------------------------
    // Response processing

    /**
     * Binds response variables for this assessmentItem, leaving them in uncommitted state.
     * If all responses are successfully bound to variables of the required types then they
     * are additionally validated.
     * <p>
     * The set of uncommitted response variables (except those bound to {@link EndAttemptInteraction})
     * will be cleared before this runs.
     * <p>
     * Pre-condition: Item session must be open
     * <p>
     * Post-conditions: Response variables will be bound and validated, but not committed.
     *
     * @see #commitResponses(Date)
     *
     * @param timestamp timestamp for this event
     * @param responseMap Map of responses to set, keyed on response variable identifier
     * @return true if all responses were successfully bound and validated, false otherwise.
     *   Further details can be found within {@link ItemSessionState}
     *
     * @throws IllegalArgumentException if responseMap is null, contains a null value, or if
     *   any key fails to map to an interaction
     */
    public boolean bindResponses(final Date timestamp, final Map<Identifier, ResponseData> responseMap) {
        Assert.notNull(timestamp);
        Assert.notNull(responseMap, "responseMap");
        ensureOpen();
        logger.debug("Binding responses {} on item {}", responseMap, getSubject().getSystemId());

        /* Stop duration timer */
        endItemSessionTimer(itemSessionState, timestamp);

        /* Copy existing committed response values over to uncommitted ones, for possible
         * over-writing below.
         */
        for (final Entry<Identifier, Value> responseEntry : itemSessionState.getResponseValues().entrySet()) {
            final Identifier identifier = responseEntry.getKey();
            final Value value = responseEntry.getValue();
            itemSessionState.setUncommittedResponseValue(identifier, value);
        }

        /* Set all uncommitted responses bound to <endAttemptInteractions> to false initially.
         * These may be overridden for responses to the presented interactions below.
         */
        for (final Interaction interaction : itemProcessingMap.getInteractions()) {
            if (interaction instanceof EndAttemptInteraction) {
                itemSessionState.setUncommittedResponseValue(interaction, BooleanValue.FALSE);
            }
        }

        /* Save raw responses */
        itemSessionState.setRawResponseDataMap(responseMap);

        /* Now bind responses */
        final Map<Identifier, Interaction> interactionByResponseIdentifierMap = itemProcessingMap.getInteractionByResponseIdentifierMap();
        final Set<Identifier> unboundResponseIdentifiers = new HashSet<Identifier>();
        for (final Entry<Identifier, ResponseData> responseEntry : responseMap.entrySet()) {
            final Identifier responseIdentifier = responseEntry.getKey();
            final ResponseData responseData = responseEntry.getValue();
            Assert.notNull(responseData, "responseMap entry for key " + responseIdentifier);
            final Interaction interaction = interactionByResponseIdentifierMap.get(responseIdentifier);
            if (interaction != null) {
                try {
                    interaction.bindResponse(this, responseData);
                }
                catch (final ResponseBindingException e) {
                    unboundResponseIdentifiers.add(responseIdentifier);
                }
            }
            else {
                throw new IllegalArgumentException("No interaction found for response identifier " + responseIdentifier);
            }
        }

        /* Validate if all responses were successfully bound */
        final Set<Identifier> invalidResponseIdentifiers = new HashSet<Identifier>();
        if (unboundResponseIdentifiers.isEmpty()) {
            logger.debug("Validating responses");
            for (final Interaction interaction : itemProcessingMap.getInteractions()) {
                final Value responseValue = itemSessionState.getResponseValue(interaction);
                if (!interaction.validateResponse(this, responseValue)) {
                    invalidResponseIdentifiers.add(interaction.getResponseIdentifier());
                }
            }
        }

        /* Save results */
        itemSessionState.setUnboundResponseIdentifiers(unboundResponseIdentifiers);
        itemSessionState.setInvalidResponseIdentifiers(invalidResponseIdentifiers);

        /* Touch the duration timer */
        if (!itemSessionState.isClosed()) { /* (NB: This should never fail but we'll keep it for consistency) */
            startItemSessionTimer(itemSessionState, timestamp);
        }

        return unboundResponseIdentifiers.isEmpty() && invalidResponseIdentifiers.isEmpty();
    }

    /**
     * Commits any responses previously saved via {@link #bindResponses(Date, Map)}.
     * <p>
     * NB: This is counted as an attempt until {@link #performResponseProcessing(Date)} is invoked.
     * <p>
     * Pre-condition: Item session must be open
     * <p>
     * Post-conditions: Response variables will be replaced by the uncommitted variables. The uncommitted
     * variables will be cleared. The item session state will
     * be marked as having been responded, and Session Status will be set to
     * {@link SessionStatus#PENDING_RESPONSE_PROCESSING}
     *
     * @see #bindResponses(Date, Map)
     *
     * @param timestamp timestamp for this event
     */
    public void commitResponses(final Date timestamp) {
        Assert.notNull(timestamp);
        ensureOpen();
        logger.debug("Committing currently saved responses to item {}", getSubject().getSystemId());

        /* Stop duration timer */
        endItemSessionTimer(itemSessionState, timestamp);

        /* Commit responses */
        for (final Entry<Identifier, Value> uncommittedResponseEntry : itemSessionState.getUncommittedResponseValues().entrySet()) {
            final Identifier identifier = uncommittedResponseEntry.getKey();
            final Value value = uncommittedResponseEntry.getValue();
            itemSessionState.setResponseValue(identifier, value);
        }

        /* Clear uncommitted responses */
        itemSessionState.clearUncommittedResponseValues();

        /* Update session status */
        itemSessionState.setResponded(true);
        itemSessionState.setSessionStatus(SessionStatus.PENDING_RESPONSE_PROCESSING);

        /* Touch the duration timer */
        if (!itemSessionState.isClosed()) { /* (NB: This should never fail but we'll keep it for consistency) */
            startItemSessionTimer(itemSessionState, timestamp);
        }
    }

    /**
     * Resets all responses
     * <p>
     * Pre-condition: Item session must be open.
     * <p>
     * Post-condition: Responses are reset to their default values. SessionStatus is
     * reset to {@link SessionStatus#INITIAL}.
     */
    public void resetResponses(final Date timestamp) {
        Assert.notNull(timestamp);
        ensureOpen();
        logger.debug("Resetting responses on item {}", getSubject().getSystemId());

        endItemSessionTimer(itemSessionState, timestamp);
        resetResponseState();
        itemSessionState.setSessionStatus(SessionStatus.INITIAL);
        updateClosedStatus(timestamp);
        if (!itemSessionState.isClosed()) {
            startItemSessionTimer(itemSessionState, timestamp);
        }
    }

    /**
     * Performs response processing on the currently committed responses, changing {@link #itemSessionState}
     * as appropriate.
     * <p>
     * Pre-condition: item session must be open.
     * <p>
     * Post-conditions: Outcome Variables will be updated. SessionStatus will be changed to
     * {@link SessionStatus#FINAL}. The <code>numAttempts</code> variables will be incremented (unless
     * directed otherwise).
     */
    public void performResponseProcessing(final Date timestamp) {
        Assert.notNull(timestamp);
        ensureOpen();
        logger.debug("Response processing starting on item {}", getSubject().getSystemId());

        fireJqtiLifecycleEvent(JqtiLifecycleEventType.ITEM_RESPONSE_PROCESSING_STARTING);
        try {
            /* Stop timer to recalculate current duration */
            endItemSessionTimer(itemSessionState, timestamp);

            /* If no responses have been committed, then set responses for all endAttemptInteractions to false now */
            if (!itemSessionState.isResponded()) {
                for (final Interaction interaction : itemProcessingMap.getInteractions()) {
                    if (interaction instanceof EndAttemptInteraction) {
                        itemSessionState.setResponseValue(interaction, BooleanValue.FALSE);
                    }
                }
            }

            /* We will always count the attempt, unless the response was to an endAttemptInteraction
             * with countAttempt set to false.
             */
            boolean countAttempt = true;
            for (final Interaction interaction : itemProcessingMap.getInteractions()) {
                if (interaction instanceof EndAttemptInteraction) {
                    final EndAttemptInteraction endAttemptInteraction = (EndAttemptInteraction) interaction;
                    final Value responseValue = itemSessionState.getResponseValue(interaction);
                    if (responseValue==null) {
                        throw new QtiCandidateStateException("Expected to find a response value for identifier " + interaction.getResponseDeclaration());
                    }
                    if (!responseValue.hasSignature(Signature.SINGLE_BOOLEAN)) {
                        fireRuntimeWarning(getSubjectItem().getResponseProcessing(),
                                "Expected value " + responseValue + " bound to endAttemptInteraction "
                                + endAttemptInteraction.getResponseIdentifier() + " to be a "
                                + Signature.SINGLE_BOOLEAN
                                + " but got " + responseValue.getSignature());
                    }
                    else if (((BooleanValue) responseValue).booleanValue()) {
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

            /* Work out which RP logic to perform */
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

            /* Update final state */
            itemSessionState.setSessionStatus(SessionStatus.FINAL);
            updateClosedStatus(timestamp);

            /* Start timer again to keep calculating duration */
            if (!itemSessionState.isClosed()) {
                startItemSessionTimer(itemSessionState, timestamp);
            }
        }
        finally {
            fireJqtiLifecycleEvent(JqtiLifecycleEventType.ITEM_RESPONSE_PROCESSING_FINISHED);
            logger.debug("Response processing finished on item {}", getSubject().getSystemId());
        }
    }

    //-------------------------------------------------------------------
    // Duration management

    private void startItemSessionTimer(final ItemSessionState itemSessionState, final Date timestamp) {
        itemSessionState.setDurationIntervalStartTime(timestamp);
    }

    private void endItemSessionTimer(final ItemSessionState itemSessionState, final Date timestamp) {
        final long durationDelta = timestamp.getTime() - itemSessionState.getDurationIntervalStartTime().getTime();
        itemSessionState.setDurationAccumulated(itemSessionState.getDurationAccumulated() + durationDelta);
        itemSessionState.setDurationIntervalStartTime(null);
    }

    //-------------------------------------------------------------------
    // Shuffle callbacks (from interactions)

    public <C extends Choice> void shuffleInteractionChoiceOrder(final Interaction interaction, final List<C> choiceList) {
        final List<List<C>> choiceLists = new ArrayList<List<C>>();
        choiceLists.add(choiceList);
        shuffleInteractionChoiceOrders(interaction, choiceLists);
    }

    public <C extends Choice> void shuffleInteractionChoiceOrders(final Interaction interaction, final List<List<C>> choiceLists) {
        ensureInitialized();
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
            throw new QtiCandidateStateException("Interaction '" + interaction.getQtiClassName()
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
    public Value evaluateVariableValue(final VariableDeclaration variableDeclaration) {
        Assert.notNull(variableDeclaration);
        return evaluateVariableValue(variableDeclaration.getIdentifier());
    }

    @Override
    public Value evaluateVariableValue(final Identifier identifier, final VariableType... permittedTypes) {
        Assert.notNull(identifier);
        if (!itemProcessingMap.isValidVariableIdentifier(identifier)) {
            throw new QtiInvalidLookupException(identifier);
        }
        Value result = null;
        if (permittedTypes.length==0) {
            /* No types specified, so allow any variable */
            result = evaluateVariableValue(identifier);
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

    public Value evaluateVariableValue(final Identifier identifier) {
        Assert.notNull(identifier);
        Value result = evaluateTemplateValue(identifier);
        if (result==null) {
            result = evaluateOutcomeValue(identifier);
            if (result==null) {
                result = evaluateResponseValue(identifier);
            }
        }
        return result;
    }

    //-------------------------------------------------------------------

    @Override
    public void setVariableValue(final VariableDeclaration variableDeclaration, final Value value) {
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
                    throw new IllegalArgumentException("Variable " + QtiConstants.VARIABLE_COMPLETION_STATUS
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
     * given {@link Identifier}. The result will be null if there is no {@link CorrectResponse}
     * for this {@link ResponseDeclaration} or no overridden response has been set, otherwise a non-null {@link Value}.
     *
     * @param identifier identifier of the required variable, which must not be null
     * @return computed correct response value or null
     *
     * @throws QtiInvalidLookupException
     */
    @Override
    public Value computeCorrectResponse(final Identifier identifier) {
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
    public Value computeCorrectResponse(final ResponseDeclaration declaration) {
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
    public boolean hasCorrectResponse(final ResponseDeclaration declaration) {
        Assert.notNull(declaration);
        return declaration.getCorrectResponse()!=null
                || itemSessionState.getOverriddenCorrectResponseValue(declaration)!=null;
    }

    //-------------------------------------------------------------------

    private void resetTemplateVariables() {
        for (final TemplateDeclaration templateDeclaration : itemProcessingMap.getValidTemplateDeclarationMap().values()) {
            initValue(templateDeclaration);
        }
    }

    private void resetResponseState() {
        for (final ResponseDeclaration responseDeclaration : itemProcessingMap.getValidResponseDeclarationMap().values()) {
            if (!VariableDeclaration.isReservedIdentifier(responseDeclaration.getIdentifier())) {
                initValue(responseDeclaration);
            }
        }
        itemSessionState.clearUncommittedResponseValues();
        itemSessionState.clearRawResponseDataMap();
        itemSessionState.clearUnboundResponseIdentifiers();
        itemSessionState.clearInvalidResponseIdentifiers();
        itemSessionState.setNumAttempts(0);
        itemSessionState.setResponded(false);
    }

    private void resetOutcomeVariables() {
        for (final OutcomeDeclaration outcomeDeclaration : itemProcessingMap.getValidOutcomeDeclarationMap().values()) {
            if (!VariableDeclaration.isReservedIdentifier(outcomeDeclaration.getIdentifier())) {
                initValue(outcomeDeclaration);
            }
        }
        itemSessionState.setCompletionStatus(QtiConstants.COMPLETION_STATUS_UNKNOWN);
    }

    private void initValue(final VariableDeclaration declaration) {
        Assert.notNull(declaration);
        setVariableValue(declaration, computeInitialValue(declaration));
    }

    private Value computeInitialValue(final VariableDeclaration declaration) {
        Assert.notNull(declaration);
        return computeInitialValue(declaration.getIdentifier());
    }

    private Value computeInitialValue(final Identifier identifier) {
        return computeDefaultValue(identifier);
    }
    //-------------------------------------------------------------------

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
    public boolean isCorrect() {
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
    public boolean isIncorrect() {
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
    public int countCorrect() {
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
    public int countIncorrect() {
        int count = 0;
        for (final ResponseDeclaration responseDeclaration : itemProcessingMap.getValidResponseDeclarationMap().values()) {
            if (!isCorrectResponse(responseDeclaration)) {
                count++;
            }
        }
        return count;
    }

    //-------------------------------------------------------------------
    // Computes standalone assessmentResult for this item. This wasn't available in the original JQTI

    public AssessmentResult computeAssessmentResult() {
        return computeAssessmentResult(new Date(), null, null);
    }

    public AssessmentResult computeAssessmentResult(final Date timestamp, final String sessionIdentifier,
            final URI sessionIdentifierSourceId) {
        final AssessmentResult result = new AssessmentResult();
        final Context context = new Context(result);
        result.setContext(context);
        if (sessionIdentifier!=null && sessionIdentifierSourceId!=null) {
            final SessionIdentifier sessionIdentifierNode = new SessionIdentifier(context);
            sessionIdentifierNode.setIdentifier(sessionIdentifier);
            sessionIdentifierNode.setSourceId(sessionIdentifierSourceId);
            context.getSessionIdentifiers().add(sessionIdentifierNode);
        }

        result.getItemResults().add(computeItemResult(result, timestamp));
        return result;
    }

    ItemResult computeItemResult(final AssessmentResult owner, final Date timestamp) {
        final ItemResult itemResult = new ItemResult(owner);
        itemResult.setIdentifier(item.getIdentifier());
        itemResult.setDateStamp(timestamp);
        itemResult.setSessionStatus(itemSessionState.getSessionStatus());
        recordItemVariables(itemResult);
        return itemResult;
    }

    private void recordItemVariables(final ItemResult result) {
        final List<ItemVariable> itemVariables = result.getItemVariables();
        itemVariables.clear();

        /* Record completionStatus */
        itemVariables.add(new OutcomeVariable(result, item.getCompletionStatusOutcomeDeclaration(), itemSessionState.getCompletionStatusValue()));

        /* Then do other OVs */
        for (final Entry<Identifier, Value> mapEntry : itemSessionState.getOutcomeValues().entrySet()) {
            final OutcomeDeclaration declaration = itemProcessingMap.getValidOutcomeDeclarationMap().get(mapEntry.getKey());
            if (declaration!=null) {
                final Value value = mapEntry.getValue();
                final OutcomeVariable variable = new OutcomeVariable(result, declaration, value);
                itemVariables.add(variable);
            }
        }

        /* Record duration & numAttempts */
        itemVariables.add(new ResponseVariable(result, item.getDurationResponseDeclaration(), itemSessionState.computeDurationValue(), null));
        itemVariables.add(new ResponseVariable(result, item.getNumAttemptsResponseDeclaration(), itemSessionState.getNumAttemptsValue(), null));

        /* Then do rest of RVs */
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

        /* Then do TVs */
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(resolvedAssessmentItem=" + resolvedAssessmentItem
                + ",itemSessionState=" + itemSessionState
                + ")";
    }
}
