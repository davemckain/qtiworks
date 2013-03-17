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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.running;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.JqtiLifecycleEventType;
import uk.ac.ed.ph.jqtiplus.exception.QtiCandidateStateException;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.result.Context;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.node.result.OutcomeVariable;
import uk.ac.ed.ph.jqtiplus.node.result.SessionIdentifier;
import uk.ac.ed.ph.jqtiplus.node.result.TestResult;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.NavigationMode;
import uk.ac.ed.ph.jqtiplus.node.test.PreCondition;
import uk.ac.ed.ph.jqtiplus.node.test.SubmissionMode;
import uk.ac.ed.ph.jqtiplus.node.test.TemplateDefault;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.state.AssessmentSectionSessionState;
import uk.ac.ed.ph.jqtiplus.state.ControlObjectState;
import uk.ac.ed.ph.jqtiplus.state.EffectiveItemSessionControl;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPartSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Document this type
 *
 * TO FINISH: Update linear mode to touch durations and enter sections
 *
 * Usage: one-shot, not thread safe.
 *
 * @author David McKain
 */
public final class TestSessionController extends TestProcessingController {

    private static final Logger logger = LoggerFactory.getLogger(TestSessionController.class);

    private final TestProcessingMap testProcessingMap;
    private final TestSessionState testSessionState;

    /** NB: These are created lazily */
    private final Map<TestPlanNodeKey, ItemSessionController> itemSessionControllerMap;

    public TestSessionController(final JqtiExtensionManager jqtiExtensionManager,
            final TestSessionControllerSettings testSessionControllerSettings,
            final TestProcessingMap testProcessingMap,
            final TestSessionState testSessionState) {
        super(jqtiExtensionManager, testSessionControllerSettings, testProcessingMap, testSessionState);
        this.testProcessingMap = testProcessingMap;
        this.testSessionState = testSessionState;
        this.itemSessionControllerMap = new HashMap<TestPlanNodeKey, ItemSessionController>();
    }

    //-------------------------------------------------------------------

    private void fireLifecycleEvent(final JqtiLifecycleEventType eventType) {
        for (final JqtiExtensionPackage<?> extensionPackage : jqtiExtensionManager.getExtensionPackages()) {
            extensionPackage.lifecycleEvent(this, eventType);
        }
    }

    //-------------------------------------------------------------------
    // Initialization

    /**
     * Sets all explicitly-defined (valid) variables to NULL, and the
     * <code>duration</code> variable to 0, and calls {@link #initialize()} on each item in the
     * test plan.
     */
    public void initialize(final Date timestamp) {
        Assert.notNull(timestamp);

        /* Clear existing ItemSessionController map */
        itemSessionControllerMap.clear();

        /* Reset test variables */
        testSessionState.reset();
        resetOutcomeVariables();
        for (final Identifier identifier : testProcessingMap.getValidOutcomeDeclarationMap().keySet()) {
            testSessionState.setOutcomeValue(identifier, NullValue.INSTANCE);
        }

        /* Initialise state for each testPart */
        for (final TestPlanNode testPartNode : testSessionState.getTestPlan().getTestPartNodes()) {
            final TestPlanNodeKey key = testPartNode.getKey();
            final TestPartSessionState testPartSessionState = new TestPartSessionState();
            testSessionState.getTestPartSessionStates().put(key, testPartSessionState);
        }

        /* Initialise state and controller for each item instance */
        for (final TestPlanNode testPlanNode : testSessionState.getTestPlan().getTestPlanNodeMap().values()) {
            if (testPlanNode.getTestNodeType()==TestNodeType.ASSESSMENT_ITEM_REF) {
                final TestPlanNodeKey key = testPlanNode.getKey();
                final ItemSessionState itemSessionState = new ItemSessionState();
                testSessionState.getItemSessionStates().put(key, itemSessionState);

                final ItemSessionController itemSessionController = getItemSessionController(testPlanNode);
                itemSessionController.initialize(timestamp);
            }
        }
    }

    //-------------------------------------------------------------------
    // High level test navigation - test entry, flow through testParts then exit

    /**
     * Updates the {@link TestSessionState} to indicate that the test has
     * been entered. Returns the number {@link TestPart}s in the test.
     * The caller would be expected to call {@link #enterNextAvailableTestPart()}
     * next.
     * <p>
     * Precondition: the test must not have already been entered.
     * <p>
     * Postcondition: the test will be marked as having been entered. No {@link TestPart}
     * will been entered, no item will have been selected. Outcomes variables will
     * be set to their default values. The duration timer will start on the test.
     *
     * @return number of {@link TestPart}s in the test.
     *
     * @see #findNextAvailableTestPart()
     * @see #enterNextAvailableTestPart()
     */
    public int enterTest(final Date timestamp) {
        Assert.notNull(timestamp);
    	assertTestNotEntered();
    	logger.debug("Entering test {}", getSubject().getSystemId());

    	testSessionState.setEntryTime(timestamp);
        testSessionState.setCurrentTestPartKey(null);
        testSessionState.setCurrentItemKey(null);
        startControlObjectTimer(testSessionState, timestamp);
        resetOutcomeVariables();

        return testSessionState.getTestPlan().getTestPartNodes().size();
    }


    /**
     * Finds the {@link TestPlanNode} corresponding to the next available {@link TestPart},
     * starting from the one after the current one (if a {@link TestPart} is already selected)
     * or the first {@link TestPart} (if there is no current {@link TestPart}), applying
     * {@link PreCondition}s along the way.
     * <p>
     * Precondition: The test must have been entered.
     *
     * @throws QtiCandidateStateException if the test has not been entered or if the test has ended
     */
    public TestPlanNode findNextAvailableTestPart() {
    	assertInsideTest();
        final TestPlan testPlan = testSessionState.getTestPlan();
        final List<TestPlanNode> testPartNodes = testPlan.getTestPartNodes();

        /* Find next unvisited testPart index */
        final TestPlanNode currentTestPartNode = getCurrentTestPartNode();
        int nextTestPartIndex;
        if (currentTestPartNode==null) {
            /* Haven't entered any testPart yet */
            nextTestPartIndex = 0;
        }
        else {
        	nextTestPartIndex = currentTestPartNode.getSiblingIndex() + 1;
        }

        /* Now locate the first of these for which any preConditions are satisfied */
        TestPlanNode nextAvailableTestPartNode = null;
        int searchIndex=nextTestPartIndex;
        for (; searchIndex<testPartNodes.size(); searchIndex++) {
        	final TestPlanNode testPlanNode = testPartNodes.get(searchIndex);
        	final TestPart testPart = ensureTestPart(testPlanNode);
        	if (testPart.arePreConditionsMet(this)) {
        		nextAvailableTestPartNode = testPlanNode;
        		break;
        	}
        }
        return nextAvailableTestPartNode;
    }

    /**
     * Exits the current {@link TestPart} (if selected), then advances to the next
     * available {@link TestPart} in the {@link TestPlan}, taking into account any {@link PreCondition}s.
     * If there are no further available {@link TestPart}s, then the test will be exited.
     * <p>
     * If the newly-presented {@link TestPart} has {@link NavigationMode#LINEAR} then we will
     * attempt to select the first item.
     * <p>
     * Precondition: The test must have been entered but not yet ended. The current testPart
     * (if appropriate) must have been ended.
     * <p>
     * Postcondition: The current testPart will be exited, as will all assessmentSections and
     * item instances therein. The next available {@link TestPart} will be entered, if one is available.
     * Otherwise the test itself will be ended.
     */
    public TestPlanNode enterNextAvailableTestPart(final Date timestamp) {
        Assert.notNull(timestamp);
    	assertInsideTest();
        endControlObjectTimer(testSessionState, timestamp);

        final TestPlan testPlan = testSessionState.getTestPlan();
        final List<TestPlanNode> testPartNodes = testPlan.getTestPartNodes();

        /* Exit current testPart (if appropriate) and locate next testPart */
        final TestPlanNode currentTestPartNode = getCurrentTestPartNode();
        final int nextTestPartIndex;
        if (currentTestPartNode!=null) {
    	    /* Exit all items */
    	    for (final TestPlanNode itemRefNode : currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF)) {
                getItemSessionController(itemRefNode).exitItem(timestamp);
    	    }

    	    /* Exit all assessmentSections */
    	    for (final TestPlanNode testPlanNode : currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_SECTION)) {
                final AssessmentSectionSessionState assessmentSectionSessionState = testSessionState.getAssessmentSectionSessionStates().get(testPlanNode.getKey());
                endControlObjectTimer(assessmentSectionSessionState, timestamp);
                assessmentSectionSessionState.setExitTime(timestamp);
    	    }

    	    /* Exit the testPart itself */
            final TestPartSessionState currentTestPartSessionState = ensureTestPartSessionState(currentTestPartNode);
            assertTestPartEnded(currentTestPartSessionState);
            currentTestPartSessionState.setExitTime(timestamp);

            /* Choose next testPart index */
            nextTestPartIndex = currentTestPartNode.getSiblingIndex() + 1;
        }
        else {
        	nextTestPartIndex = 0;
        }

        /* Work from next testPart onwards, applying preConditions until successful (or we run out of testParts) */
        TestPlanNode nextAvailableTestPartNode = null;
        int searchIndex=nextTestPartIndex;
        for (; searchIndex<testPartNodes.size(); searchIndex++) {
        	final TestPlanNode testPlanNode = testPartNodes.get(searchIndex);
        	final TestPart testPart = ensureTestPart(testPlanNode);
        	if (testPart.arePreConditionsMet(this)) {
        		nextAvailableTestPartNode = testPlanNode;
        		break;
        	}
        	else {
        		/* Record failed preCondition */
        		ensureTestPartSessionState(testPlanNode).setPreConditionFailed(true);
        	}
        }

        /* Clear current part/item */
        testSessionState.setCurrentTestPartKey(null);
        testSessionState.setCurrentItemKey(null);

        /* Exit test if no more testParts are available */
        if (nextAvailableTestPartNode==null) {
            logger.debug("No more testParts available, so ending test");
            testSessionState.setEndTime(timestamp);
            return null;
        }

        /* Enter next testPart */
        logger.debug("Entering testPart {} and running template processing on each item", nextAvailableTestPartNode.getIdentifier());
        final TestPartSessionState nextTestPartSessionState = ensureTestPartSessionState(nextAvailableTestPartNode);
        testSessionState.setCurrentTestPartKey(nextAvailableTestPartNode.getKey());
        nextTestPartSessionState.setEntryTime(timestamp);
        startControlObjectTimer(nextTestPartSessionState, timestamp);
        startControlObjectTimer(testSessionState, timestamp);

        /* Perform template processing on each item */
        final List<TestPlanNode> itemRefNodes = nextAvailableTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
        for (final TestPlanNode itemRefNode : itemRefNodes) {
            performTemplateProcessing(timestamp, itemRefNode);
        }

        /* If linear navigation, select the first item (if possible) */
        final TestPart nextTestPart = ensureTestPart(nextAvailableTestPartNode);
        if (nextTestPart.getNavigationMode()==NavigationMode.LINEAR) {
        	logger.debug("Auto-selecting first item in testPart as we are in LINEAR mode");
            selectNextItemOrEndTestPart(timestamp);
        }

        return nextAvailableTestPartNode;
    }

    /**
	 * Performs template processing on the given {@link TestPlanNode} corresponding to an
	 * {@link AssessmentItemRef}
	 */
	private void performTemplateProcessing(final Date timestamp, final TestPlanNode itemRefNode) {
        Assert.notNull(timestamp);
	    Assert.notNull(itemRefNode);
	    ensureItemRefNode(itemRefNode);

	    final AssessmentItemRef assessmentItemRef = (AssessmentItemRef) testProcessingMap.resolveAbstractPart(itemRefNode);
	    final List<TemplateDefault> templateDefaults = assessmentItemRef.getTemplateDefaults();

	    final ItemSessionController itemSessionController = getItemSessionController(itemRefNode);
	    itemSessionController.performTemplateProcessing(timestamp, templateDefaults);
	}

	/**
	 * Can we end the current test part?
	 *
	 * @throws QtiCandidateStateException if no test part is selected
	 */
	public boolean mayEndTestPart() {
	    final TestPlanNode currentTestPartNode = ensureCurrentTestPartNode();
	    final TestPart currentTestPart = ensureTestPart(currentTestPartNode);
	    if (currentTestPart.getSubmissionMode()==SubmissionMode.INDIVIDUAL) {
	        /* (allowSkipping & validateResponses only apply in INDIVIDUAL submission mode) */
	        final List<TestPlanNode> itemRefNodes = currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
	        for (final TestPlanNode itemRefNode : itemRefNodes) {
	            final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(itemRefNode.getKey());
	            final EffectiveItemSessionControl effectiveItemSessionControl = testProcessingMap.resolveEffectiveItemSessionControl(itemRefNode);
	            if (!itemSessionState.isResponded() && !effectiveItemSessionControl.isAllowSkipping()) {
	                logger.debug("Item " + itemRefNode.getKey() + " has not been responded and allowSkipping=false, so ending test part will be forbidden");
	                return false;
	            }
	            if (itemSessionState.isRespondedInvalidly() && effectiveItemSessionControl.isValidateResponses()) {
	                logger.debug("Item " + itemRefNode.getKey() + " has been responded with bad/invalid responses and validateResponses=true, so ending test part will be forbidden");
	                return false;
	            }
	        }
	    }
	    return true;
	}

	/**
	 * Marks the current test part as ended, if allowed, performing any processing
	 * required at this time and updating state as appropriate.
	 * <p>
	 * Precondition: {@link #mayEndTestPart()} must return true
	 * <p>
	 * Postcondition: Response Processing and Outcome Processing will be run if the testPart
	 * has {@link SubmissionMode#SIMULTANEOUS}. All item and assessment section states in testPart
	 * will be ended if they haven't done so already, current test state will be marked as ended,
	 * current item will be cleared.
	 */
	public void endTestPart(final Date timestamp) {
        Assert.notNull(timestamp);
	    if (!mayEndTestPart()) {
	        throw new QtiCandidateStateException("Current test part cannot be ended");
	    }

	    final TestPlanNode currentTestPartNode = ensureCurrentTestPartNode();
	    final TestPartSessionState currentTestPartSessionState = ensureTestPartSessionState(currentTestPartNode);
	    final TestPart currentTestPart = ensureTestPart(currentTestPartNode);
	    final List<TestPlanNode> itemRefNodes = currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
	    if (currentTestPart.getSubmissionMode()==SubmissionMode.SIMULTANEOUS) {
	        /* If we're in SIMULTANEOUS mode, we run response processing on all items now */
	        for (final TestPlanNode itemRefNode : itemRefNodes) {
	            final ItemSessionController itemSessionController = getItemSessionController(itemRefNode);
	            itemSessionController.performResponseProcessing(timestamp);
	        }
	        /* Then we'll run outcome processing for the test */
	        performOutcomeProcessing();
	    }

	    /* End all items (if not done so already due to RP ending the item) */
	    for (final TestPlanNode itemRefNode : itemRefNodes) {
            final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(itemRefNode.getKey());
            if (!itemSessionState.isEnded()) {
                getItemSessionController(itemRefNode).endItem(timestamp);
            }
	    }

	    /* End all assessmentSections */
	    for (final TestPlanNode testPlanNode : currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_SECTION)) {
            final AssessmentSectionSessionState assessmentSectionSessionState = testSessionState.getAssessmentSectionSessionStates().get(testPlanNode.getKey());
            endControlObjectTimer(assessmentSectionSessionState, timestamp);
            assessmentSectionSessionState.setEndTime(timestamp);
	    }

	    /* End testPart */
        currentTestPartSessionState.setEndTime(timestamp);
        endControlObjectTimer(currentTestPartSessionState, timestamp);

        /* Update test duration */
        touchControlObjectTimer(testSessionState, timestamp);

	    /* Deselect item */
	    testSessionState.setCurrentItemKey(null);

	    logger.debug("Ended testPart {}", currentTestPartNode.getIdentifier());
	}

    /**
     * Exits the test after it has been ended.
     * <p>
     * Precondition: The test must have been ended
     * <p>
     * Postcondition: The test will be marked as being exited.
     */
    public void exitTest(final Date timestamp) {
        Assert.notNull(timestamp);
        assertTestEnded();
        assertTestNotExited();

        testSessionState.setExitTime(timestamp);
    }

    //-------------------------------------------------------------------
    // Nonlinear navigation within a testPart

	/**
     * Returns whether the candidate may select the item in the current {@link NavigationMode#NONLINEAR}
     * {@link TestPart} having the given {@link TestPlanNodeKey}.
     *
     * @param itemKey key for the requested item, or null to indicate that we want to deselect the
     *   current item.
     *
     * @see #selectItemNonlinear(TestPlanNodeKey)
     */
    public boolean maySelectItemNonlinear(final TestPlanNodeKey itemKey) {
        final TestPlanNode currentTestPartNode = ensureCurrentTestPartNode();
        final TestPart currentTestPart = ensureTestPart(currentTestPartNode);

        /* Make sure we're in nonlinear navigation mode */
        if (currentTestPart.getNavigationMode()!=NavigationMode.NONLINEAR) {
            throw new QtiCandidateStateException("Explicit item selection is not allowed in NONLINEAR navigationMode");
        }

        if (itemKey!=null) {
            final TestPlanNode itemRefNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(itemKey);
            if (itemRefNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF || !itemRefNode.hasAncestor(currentTestPartNode)) {
                return false;
            }
            return true;
        }
        else {
            /* Allow deselection */
            return true;
        }
    }

    /**
     * Select an item within the current {@link NavigationMode#NONLINEAR} {@link TestPart}, or
     * deselects the current item is the given key is null.
     *
     * @param itemKey key for the requested item, or null to indicate that we want to deselect the
     *   current item.
     *
     * @param itemKey item to select, or null to select no item
     *
     * @throws QtiCandidateStateException if no testPart is selected, if the current testPart
     *   does not have {@link NavigationMode#NONLINEAR}, or if the requested item is not in the current part.
     *
     * @see #maySelectItemNonlinear(TestPlanNodeKey)
     */
    public TestPlanNode selectItemNonlinear(final Date timestamp, final TestPlanNodeKey itemKey) {
        Assert.notNull(timestamp);
        final TestPlanNode currentTestPartNode = ensureCurrentTestPartNode();
        final TestPart currentTestPart = ensureTestPart(currentTestPartNode);
        final TestPartSessionState currentTestPartSessionState = ensureTestPartSessionState(currentTestPartNode);

        /* Make sure we're in nonlinear navigation mode */
        if (currentTestPart.getNavigationMode()!=NavigationMode.NONLINEAR) {
            throw new QtiCandidateStateException("Explicit selection is not allowed in NONLINEAR navigationMode");
        }

        /* If an item is currently selected, then suspend the session and update timer on parent sections */
        final TestPlanNode currentItemRefNode = getCurrentItemRefNode();
        if (currentItemRefNode!=null) {
            getItemSessionController(currentItemRefNode).suspendItemSession(timestamp);
            for (final TestPlanNode sectionNode : currentItemRefNode.searchAncestors(TestNodeType.ASSESSMENT_SECTION)) {
                endControlObjectTimer(ensureAssessmentSectionSessionState(sectionNode), timestamp);
            }
        }

        /* Touch duration on test & testPart */
        touchControlObjectTimer(testSessionState, timestamp);
        touchControlObjectTimer(currentTestPartSessionState, timestamp);

        if (itemKey!=null) {
            final TestPlanNode newItemRefNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(itemKey);
            ensureItemRefNode(newItemRefNode);
            if (!newItemRefNode.hasAncestor(currentTestPartNode)) {
                throw new QtiCandidateStateException(newItemRefNode + " is not a descendant of " + currentTestPartNode);
            }
            testSessionState.setCurrentItemKey(newItemRefNode.getKey());

            /* Select or unsuspend item */
            final ItemSessionController newItemSessionController = getItemSessionController(newItemRefNode);
            final ItemSessionState newItemSessionState = newItemSessionController.getItemSessionState();
            if (!newItemSessionState.isEntered()) {
                newItemSessionController.enterItem(timestamp);
            }
            else {
                newItemSessionController.unsuspendItemSession(timestamp);
            }

            /* start timer on parent sections */
            for (final TestPlanNode sectionNode : newItemRefNode.searchAncestors(TestNodeType.ASSESSMENT_SECTION)) {
                startControlObjectTimer(ensureAssessmentSectionSessionState(sectionNode), timestamp);
            }

            return newItemRefNode;
        }
        else {
            /* Allow deselection */
            testSessionState.setCurrentItemKey(null);
            return null;
        }
    }

    //-------------------------------------------------------------------
    // Linear navigation within a testPart

    public boolean mayFinishItemLinear() {
        final TestPlanNode currentTestPartNode = ensureCurrentTestPartNode();
        final TestPart currentTestPart = ensureTestPart(currentTestPartNode);

        /* Make sure we're in linear navigation mode */
        if (currentTestPart.getNavigationMode()!=NavigationMode.LINEAR) {
            throw new QtiCandidateStateException("Finishing an item is only supported in LINEAR navigationMode");
        }

        /* Get current item */
        final TestPlanNode currentItemRefNode = getCurrentItemRefNode();
        if (currentItemRefNode==null) {
            return false;
        }

        /* The only thing preventing submission is allowSkipping and validateResponses, which
         * only apply in INDIVIDUAL submission mode.
         */
        if (currentTestPart.getSubmissionMode()==SubmissionMode.INDIVIDUAL) {
            final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(currentItemRefNode.getKey());
            final EffectiveItemSessionControl effectiveItemSessionControl = testProcessingMap.resolveEffectiveItemSessionControl(currentItemRefNode);
            if (!itemSessionState.isResponded() && !effectiveItemSessionControl.isAllowSkipping()) {
                /* Not responded, and allowSkipping=false */
                logger.debug("Item {} has not been responded and allowSkipping=false, so finishing item is forbidden", currentItemRefNode.getKey());
                return false;
            }
            if (itemSessionState.isRespondedInvalidly() && effectiveItemSessionControl.isValidateResponses()) {
                /* Invalid response, and validateResponses=true */
                logger.debug("Item {} has been responded with bad/invalid responses and validateResponses=true, so ending item will be forbidden", currentItemRefNode.getKey());
                return false;
            }
        }

        return true;
    }

    public TestPlanNode finishItemLinear(final Date timestamp) {
        Assert.notNull(timestamp);
        final TestPlanNode currentTestPartNode = ensureCurrentTestPartNode();
        final TestPart currentTestPart = ensureTestPart(currentTestPartNode);

        /* Make sure we're in linear navigation mode */
        if (currentTestPart.getNavigationMode()!=NavigationMode.LINEAR) {
            throw new QtiCandidateStateException("Finishing an item is only supported in LINEAR navigationMode");
        }

        /* Make sure an item is selected and it can be finished */
        final TestPlanNode currentItemRefNode = ensureCurrentItemRefNode();

        /* Make sure item can be finished (see mayFinishLinearItem() for logic summary) */
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(currentItemRefNode.getKey());
        if (currentTestPart.getSubmissionMode()==SubmissionMode.INDIVIDUAL) {
            final EffectiveItemSessionControl effectiveItemSessionControl = testProcessingMap.resolveEffectiveItemSessionControl(currentItemRefNode);
            if (!itemSessionState.isResponded() && !effectiveItemSessionControl.isAllowSkipping()) {
                throw new QtiCandidateStateException("Item " + currentItemRefNode.getKey() + " has not been responded and allowSkipping=false, so finishing item is forbidden");
            }
            if (itemSessionState.isRespondedInvalidly() && effectiveItemSessionControl.isValidateResponses()) {
                throw new QtiCandidateStateException("Item " + currentItemRefNode.getKey() + " has been responded with bad/invalid responses and validateResponses=true, so finishing is forbidden");
            }
        }

        /* Log what's happened */
        logger.debug("Finished item {}", currentItemRefNode.getKey());

        /* Update duration */
        touchControlObjectTimer(testSessionState, timestamp);

        /* Select next item (if one is available) or end the testPart */
        return selectNextItemOrEndTestPart(timestamp);
    }

    /**
     * Select the next item in a {@link NavigationMode#LINEAR} {@link TestPart}
     * <p>
     * Returns the newly selected {@link TestPlanNode}, or null if there are no more items
     * to select.
     *
     * @throws QtiCandidateStateException if no testPart is selected, if the current testPart
     *   does not have {@link NavigationMode#LINEAR}.
     */
    private TestPlanNode selectNextItemOrEndTestPart(final Date timestamp) {
        Assert.notNull(timestamp);
        final TestPlanNode currentTestPartNode = ensureCurrentTestPartNode();
        final TestPart currentTestPart = ensureTestPart(currentTestPartNode);

        /* Make sure we're in linear navigation mode */
        if (currentTestPart.getNavigationMode()!=NavigationMode.LINEAR) {
            throw new QtiCandidateStateException("Selection of next item is only supported in LINEAR navigationMode");
        }

        /* Find next item */
        final TestPlan testPlan = testSessionState.getTestPlan();
        final List<TestPlanNode> itemsInTestPart = currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
        final TestPlanNodeKey itemKey = testSessionState.getCurrentItemKey();
        TestPlanNode nextItemRefNode;
        if (itemKey==null) {
            /* Haven't entered any items yet, so select first if available */
            nextItemRefNode = !itemsInTestPart.isEmpty() ? itemsInTestPart.get(0) : null;
        }
        else {
            final TestPlanNode currentItem = testPlan.getTestPlanNodeMap().get(itemKey);
            final int currentItemIndex = itemsInTestPart.indexOf(currentItem);
            nextItemRefNode = currentItemIndex+1 < itemsInTestPart.size() ? itemsInTestPart.get(currentItemIndex+1) : null;
        }
        testSessionState.setCurrentItemKey(nextItemRefNode!=null ? nextItemRefNode.getKey() : null);

        if (nextItemRefNode!=null) {
            /* Mark item as being presented */
            logger.debug("Selected and presenting item {}", nextItemRefNode.getKey());
            getItemSessionController(nextItemRefNode).enterItem(timestamp);
        }
        else {
            /* No more items available, so end testPart */
            endTestPart(timestamp);
        }
        return nextItemRefNode;
    }

    /**
     * Returns whether responses may be submitted for the currently selected item.
     *
     * @throws QtiCandidateStateException if no item is selected
     */
    public boolean maySubmitResponsesToCurrentItem() {
        final TestPlanNode currentItemRefNode = ensureCurrentItemRefNode();
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(currentItemRefNode.getKey());

        return !itemSessionState.isClosed();
    }

    /**
     * Handles binding of responses for the currently selected item.
     * <p>
     * In {@link SubmissionMode#INDIVIDUAL} mode, response processing is then
     * run, following by outcome processing.
     * <p>
     * In {@link SubmissionMode#SIMULTANEOUS} mode, no processing happens.
     */
    public void handleResponsesToCurrentItem(final Date timestamp, final Map<Identifier, ResponseData> responseMap) {
        Assert.notNull(timestamp);
        Assert.notNull(responseMap, "responseMap");
        final TestPlanNode currentItemRefNode = ensureCurrentItemRefNode();

        /* Bind responses */
        final ItemSessionController itemSessionController = getItemSessionController(currentItemRefNode);
        final boolean boundSuccessfully = itemSessionController.bindResponses(timestamp, responseMap);

        /* Touch durations on item, ancestor sections, test part and test */
        itemSessionController.touchDuration(timestamp);
        for (final TestPlanNode sectionNode : currentItemRefNode.searchAncestors(TestNodeType.ASSESSMENT_SECTION)) {
            touchControlObjectTimer(ensureAssessmentSectionSessionState(sectionNode), timestamp);
        }
        final TestPlanNode currentTestPartNode = ensureCurrentTestPartNode();
        touchControlObjectTimer(ensureTestPartSessionState(currentTestPartNode), timestamp);
        touchControlObjectTimer(testSessionState, timestamp);

        /* Do processing if we're in INDIVIDUAL mode */
        final TestPart testPart = ensureCurrentTestPart();
        if (testPart.getSubmissionMode()==SubmissionMode.INDIVIDUAL) {
            /* Run response processing if everything was bound successfully */
            if (boundSuccessfully) {
                itemSessionController.performResponseProcessing(timestamp);
            }
            /* Run outcome processing */
            performOutcomeProcessing();
        }
    }

    /**
     * Tests whether the candidate may review the item having the given {@link TestPlanNodeKey}.
     * <p>
     * Returns true if the item state is closed and its effective session control allows review,
     * or showing feedback
     *
     * @param key of the item to test, which must not be null
     */
    public boolean mayReviewItem(final TestPlanNodeKey itemKey) {
        Assert.notNull(itemKey);
        final TestPlanNode currentTestPartNode = ensureCurrentTestPartNode();
        final TestPlanNode itemRefNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(itemKey);
        if (itemRefNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF || !itemRefNode.hasAncestor(currentTestPartNode)) {
            return false;
        }
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(itemRefNode.getKey());
        final EffectiveItemSessionControl effectiveItemSessionControl = itemRefNode.getEffectiveItemSessionControl();

        return itemSessionState.isClosed()
                && (effectiveItemSessionControl.isAllowReview()
                || effectiveItemSessionControl.isShowFeedback());
    }

    public boolean mayAccessItemSolution(final TestPlanNodeKey itemKey) {
        Assert.notNull(itemKey);
        final TestPlanNode currentTestPartNode = ensureCurrentTestPartNode();
        final TestPlanNode itemRefNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(itemKey);
        if (itemRefNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF || !itemRefNode.hasAncestor(currentTestPartNode)) {
            return false;
        }
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(itemRefNode.getKey());
        final EffectiveItemSessionControl effectiveItemSessionControl = itemRefNode.getEffectiveItemSessionControl();

        return itemSessionState.isClosed()
                && (effectiveItemSessionControl.isAllowReview() || effectiveItemSessionControl.isShowFeedback())
                && effectiveItemSessionControl.isShowSolution();
    }

    //-------------------------------------------------------------------

    private TestPlanNode getCurrentItemRefNode() {
        final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
        if (currentItemKey==null) {
            return null;
        }
        final TestPlanNode itemRefNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(currentItemKey);
        if (itemRefNode==null) {
            throw new QtiLogicException("Unexpected map lookup failure");
        }
        return itemRefNode;
    }

    private TestPlanNode ensureCurrentItemRefNode() {
        final TestPlanNode result = getCurrentItemRefNode();
        if (result==null) {
            throw new QtiCandidateStateException("Expected current item to be set");
        }
        return result;
    }

    private TestPlanNode getCurrentTestPartNode() {
        final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        if (currentTestPartKey==null) {
            return null;
        }
        final TestPlanNode testPlanNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(currentTestPartKey);
        if (testPlanNode==null) {
            throw new QtiLogicException("Unexpected map lookup failure");
        }
        return testPlanNode;
    }

    private TestPlanNode ensureCurrentTestPartNode() {
        final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        if (currentTestPartKey==null) {
            throw new QtiCandidateStateException("No current test part");
        }
        final TestPlanNode testPlanNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(currentTestPartKey);
        if (testPlanNode==null) {
            throw new QtiLogicException("Unexpected map lookup failure");
        }
        return testPlanNode;
    }

    public TestPart getCurrentTestPart() {
        final TestPlanNode currentTestPartNode = getCurrentTestPartNode();
        if (currentTestPartNode==null) {
            return null;
        }
        return ensureTestPart(currentTestPartNode);
    }

    private TestPart ensureCurrentTestPart() {
        final TestPart result = getCurrentTestPart();
        if (result==null) {
            throw new QtiCandidateStateException("No current test part");
        }
        return result;
    }

    private TestPart ensureTestPart(final TestPlanNode testPlanNode) {
        final AbstractPart result = testProcessingMap.resolveAbstractPart(testPlanNode);
        if (result==null || !(result instanceof TestPart)) {
            throw new QtiLogicException("Expected " + testPlanNode + " to resolve to a TestPart");
        }
        return (TestPart) result;
    }

    private TestPartSessionState ensureTestPartSessionState(final TestPlanNode testPlanNode) {
        final TestPartSessionState testPartSessionState = testSessionState.getTestPartSessionStates().get(testPlanNode.getKey());
        if (testPartSessionState==null) {
            throw new QtiLogicException("No TestPartSessionState corresponding to " + testPlanNode);
        }
        return testPartSessionState;
    }

    private void assertTestPartEnded(final TestPartSessionState testPartSessionState) {
        if (!testPartSessionState.isEnded()) {
            throw new QtiCandidateStateException("Expected testPartSessionState.isEnded() => true");
        }
    }

    private AssessmentSectionSessionState ensureAssessmentSectionSessionState(final TestPlanNode testPlanNode) {
        final AssessmentSectionSessionState assessmentSectionSessionState = testSessionState.getAssessmentSectionSessionStates().get(testPlanNode.getKey());
        if (assessmentSectionSessionState==null) {
            throw new QtiLogicException("No AssessmentSectionSessionState corresponding to " + testPlanNode);
        }
        return assessmentSectionSessionState;
    }

    private void ensureItemRefNode(final TestPlanNode itemRefNode) {
        if (itemRefNode==null || itemRefNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF) {
            throw new IllegalArgumentException("Expected " + itemRefNode + " to be an " + TestNodeType.ASSESSMENT_ITEM_REF);
        }
    }

    //-------------------------------------------------------------------
    // Outcome processing

    private void performOutcomeProcessing() {
        logger.debug("Outcome processing starting on test {}", getSubject().getSystemId());
        fireLifecycleEvent(JqtiLifecycleEventType.TEST_OUTCOME_PROCESSING_STARTING);
        try {
            resetOutcomeVariables();

            final OutcomeProcessing outcomeProcessing = getSubjectTest().getOutcomeProcessing();
            if (outcomeProcessing != null) {
                outcomeProcessing.evaluate(this);
            }
        }
        finally {
            fireLifecycleEvent(JqtiLifecycleEventType.TEST_OUTCOME_PROCESSING_FINISHED);
            logger.debug("Outcome processing finished on test {}", getSubject().getSystemId());
        }
    }

    private void resetOutcomeVariables() {
        for (final OutcomeDeclaration outcomeDeclaration : testProcessingMap.getValidOutcomeDeclarationMap().values()) {
        	testSessionState.setOutcomeValue(outcomeDeclaration, computeDefaultValue(outcomeDeclaration));
        }
    }

    public Value computeDefaultValue(final OutcomeDeclaration declaration) {
        Assert.notNull(declaration);
        Value result;
        final DefaultValue defaultValue = declaration.getDefaultValue();
        if (defaultValue != null) {
            result = defaultValue.evaluate();
        }
        else {
            result = NullValue.INSTANCE;
        }
        return result;
    }

    //-------------------------------------------------------------------
    // Duration management for Control Objects

	private void startControlObjectTimer(final ControlObjectState controlObjectState, final Date timestamp) {
		controlObjectState.setDurationIntervalStartTime(timestamp);
	}

	private void endControlObjectTimer(final ControlObjectState controlObjectState, final Date timestamp) {
		final long durationDelta = timestamp.getTime() - controlObjectState.getDurationIntervalStartTime().getTime();
		controlObjectState.setDurationAccumulated(controlObjectState.getDurationAccumulated() + durationDelta);
		controlObjectState.setDurationIntervalStartTime(null);
	}

    private void touchControlObjectTimer(final ControlObjectState controlObjectState, final Date timestamp) {
        endControlObjectTimer(controlObjectState, timestamp);
        startControlObjectTimer(controlObjectState, timestamp);
    }

    //-------------------------------------------------------------------
    // Result generation

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

        /* Record test result */
        result.setTestResult(computeTestResult(result, timestamp));

        /* Record item results */
        final List<ItemResult> itemResults = result.getItemResults();
        for (final TestPlanNode testPlanNode : testSessionState.getTestPlan().getTestPlanNodeMap().values()) {
            if (testPlanNode.getTestNodeType()==TestNodeType.ASSESSMENT_ITEM_REF) {
                final ItemSessionController itemSessionController = getItemSessionController(testPlanNode);
                final ItemResult itemResult = itemSessionController.computeItemResult(result, timestamp);
                itemResult.setSequenceIndex(testPlanNode.getInstanceNumber());
                itemResults.add(itemResult);
            }
        }
        return result;
    }

    private TestResult computeTestResult(final AssessmentResult owner, final Date timestamp) {
        final TestResult result = new TestResult(owner);
        result.setIdentifier(getSubject().getIdentifier());
        result.setDateStamp(timestamp);

        /* Record outcome variables */
        for (final Entry<Identifier, Value> mapEntry : testSessionState.getOutcomeValues().entrySet()) {
            final OutcomeDeclaration declaration = testProcessingMap.getValidOutcomeDeclarationMap().get(mapEntry.getKey());
            final Value value = mapEntry.getValue();
            final OutcomeVariable variable = new OutcomeVariable(result, declaration, value);
            result.getItemVariables().add(variable);
        }
        return result;
    }

    //-------------------------------------------------------------------
    // Precondition helpers

    private void assertTestEntered() {
        if (!testSessionState.isEntered()) {
            throw new QtiCandidateStateException("Test has not been entered");
        }
    }

    private void assertTestNotEntered() {
        if (testSessionState.isEntered()) {
            throw new QtiCandidateStateException("Test has already been entered");
        }
    }

    private void assertTestNotEnded() {
        if (testSessionState.isEnded()) {
            throw new QtiCandidateStateException("Test has alread ended");
        }
    }

    private void assertTestEnded() {
        if (!testSessionState.isEnded()) {
            throw new QtiCandidateStateException("Test has not been ended");
        }
    }

    private void assertTestNotExited() {
        if (testSessionState.isExited()) {
            throw new QtiCandidateStateException("Test has already been exited");
        }
    }

    private void assertInsideTest() {
        assertTestEntered();
        assertTestNotEnded();
    }
}
