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
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.BranchRule;
import uk.ac.ed.ph.jqtiplus.node.test.ControlObject;
import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;
import uk.ac.ed.ph.jqtiplus.node.test.NavigationMode;
import uk.ac.ed.ph.jqtiplus.node.test.PreCondition;
import uk.ac.ed.ph.jqtiplus.node.test.SubmissionMode;
import uk.ac.ed.ph.jqtiplus.node.test.TemplateDefault;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.state.AssessmentSectionSessionState;
import uk.ac.ed.ph.jqtiplus.state.ControlObjectSessionState;
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
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;
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
 * High level controller for running an {@link AssessmentTest}.
 * <p>
 * Rendering and delivery engines will probably want to use this to perform the core
 * QTI processing.
 * <p>
 * Typical lifecycle:
 * <ul>
 *   <li>{@link #initialize(Date)}</li>
 *   <li>{@link #enterTest(Date)}</li>
 *   <li>{@link #enterNextAvailableTestPart(Date)}</li>
 *   <li>(Then navigate through the testPart as below)</li>
 *   <li>{@link #endCurrentTestPart(Date)}</li>
 *   <li>(Repeat enter/end until there are no more testParts)</li>
 *   <li>(Call {@link #touchDurations(Date)} to update various timers when accessing but not
 *     changing the state.)</li>
 *   <li>{@link #exitTest(Date)}}</li>
 * </ul>
 * Navigation within a {@link TestPart} depends on its {@link NavigationMode}:
 * <ul>
 *   <li>Linear mode:
 *     <ul>
 *       <li>{@link #enterNextAvailableTestPart(Date)} will select the first available item</li>
 *       <li>Use {@link #advanceItemLinear(Date)} to navigate through items</li>
 *     </ul>
 *   </li>
 *   <li>Nonlinear mode:
 *     <ul>
 *       <li>Use {@link #selectItemNonlinear(Date, TestPlanNodeKey)} to select items</li>
 *   </li>
 * </ul>
 * Responses can be submitted while an item is selected via {@link #handleResponsesToCurrentItem(Date, Map)}
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
     * Sets all explicitly-defined (valid) variables to their default value, and the
     * <code>duration</code> variable to 0, and calls {@link #initialize(Date)} on each
     * item in the test plan.
     * <p>
     * Preconditions: None, this can be called at any time.
     * <p>
     * Postconditions: (Valid) outcome variables will be set to their default values,
     * states for each testPart and assessmentSection will be reset,
     * {@link ItemSessionController#initialize(Date)} will be called for each item,
     * <code>duration</code> will be set to 0. Duration timers will not yet start.
     *
     * @param timestamp initialisation timestamp, which must not be null
     *
     * @throws IllegalArgumentException if timestamp is null
     */
    public void initialize(final Date timestamp) {
        Assert.notNull(timestamp, "timestamp");

        /* Clear existing ItemSessionController map */
        itemSessionControllerMap.clear();

        /* Reset test variables */
        testSessionState.reset();
        resetOutcomeVariables();

        /* Initialise each testPart, assessmentSection and item instance */
        for (final TestPlanNode testPlanNode : testSessionState.getTestPlan().getTestPlanNodeList()) {
            final TestPlanNodeKey key = testPlanNode.getKey();
            switch (testPlanNode.getTestNodeType()) {
                case TEST_PART:
                    final TestPartSessionState testPartSessionState = new TestPartSessionState();
                    testSessionState.getTestPartSessionStates().put(key, testPartSessionState);
                    break;

                case ASSESSMENT_SECTION:
                    final AssessmentSectionSessionState assessmentSectionSessionState = new AssessmentSectionSessionState();
                    testSessionState.getAssessmentSectionSessionStates().put(key, assessmentSectionSessionState);
                    break;

                case ASSESSMENT_ITEM_REF:
                    final ItemSessionState itemSessionState = new ItemSessionState();
                    testSessionState.getItemSessionStates().put(key, itemSessionState);

                    final ItemSessionController itemSessionController = getItemSessionController(testPlanNode);
                    itemSessionController.initialize(timestamp);
                    break;

                case ROOT:
                    /* Ignore this */
                    break;

                default:
                    throw new QtiLogicException("Unexpected switch case " + testPlanNode.getTestNodeType());
            }
        }

        /* Mark test session as initialized */
        testSessionState.setInitialized(true);
    }

    //-------------------------------------------------------------------
    // High level test navigation - test entry, flow through testParts then exit

    /**
     * Updates the {@link TestSessionState} to indicate that the test has
     * been entered. Returns the number {@link TestPart}s in the test.
     * The caller would be expected to call {@link #enterNextAvailableTestPart(Date)}
     * next.
     * <p>
     * Precondition: the test must have been initialized and not have already been entered.
     * <p>
     * Postcondition: the test will be marked as having been entered. No {@link TestPart}
     * will been entered, no item will have been selected. The duration timer will start on the test.
     *
     * @param timestamp test entry timestamp, which must not be null
     *
     * @return number of {@link TestPart}s in the test.
     *
     * @throws IllegalArgumentException if timestamp is null
     * @throws QtiCandidateStateException if the test has already been entered.
     *
     * @see #findNextEnterableTestPart()
     * @see #enterNextAvailableTestPart(Date)
     */
    public int enterTest(final Date timestamp) {
        Assert.notNull(timestamp, "timestamp");
        assertTestInitialized();
        assertTestNotEntered();
        logger.debug("Entering test {}", getSubject().getSystemId());

        testSessionState.setEntryTime(timestamp);
        testSessionState.setCurrentTestPartKey(null);
        testSessionState.setCurrentItemKey(null);
        startControlObjectTimer(testSessionState, timestamp);

        return testSessionState.getTestPlan().getTestPartNodes().size();
    }

    /**
     * Touches (updates) the <code>duration</code> variables for all of the {@link ControlObject}s
     * within the test that are currently open.
     * Call this method before rendering and other operations that use rather than
     * change the session state.
     * <p>
     * Precondition: Test Session must have been initialized.
     * <p>
     * Postcondition: Duration variables will be updated as appropriate.
     *
     * @param timestamp timestamp for this event, which must not be null
     */
    public void touchDurations(final Date timestamp) {
        Assert.notNull(timestamp);
        assertTestInitialized();

        logger.debug("Touching durations on test");

        /* Touch durations on item and ancestor sections (if applicable) */
        final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
        if (currentItemKey!=null) {
            final TestPlanNode currentItemRefNode = expectItemRefNode(currentItemKey);
            final ItemSessionController itemSessionController = getItemSessionController(currentItemRefNode);
            itemSessionController.touchDuration(timestamp);
            for (final TestPlanNode sectionNode : currentItemRefNode.searchAncestors(TestNodeType.ASSESSMENT_SECTION)) {
                final AssessmentSectionSessionState assessmentSectionSessionState = expectAssessmentSectionSessionState(sectionNode);
                touchControlObjectTimerIfOpen(assessmentSectionSessionState, timestamp);
            }
        }

        /* Touch duration on testPart (if applicable) */
        final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        if (currentTestPartKey!=null) {
            final TestPlanNode testPartNode = expectTestPartNode(currentTestPartKey);
            final TestPartSessionState testPartSessionState = expectTestPartSessionState(testPartNode);
            touchControlObjectTimerIfOpen(testPartSessionState, timestamp);
        }

        /* Finally touch duration on test */
        touchControlObjectTimerIfOpen(testSessionState, timestamp);
    }

    /**
     * Returns the currently visited {@link TestPart}, or null if we are not currently
     * inside a {@link TestPart}.
     * <p>
     * Precondition: None.
     * <p>
     * Postcondition: None.
     */
    public TestPart getCurrentTestPart() {
        final TestPlanNode currentTestPartNode = getCurrentTestPartNode();
        if (currentTestPartNode==null) {
            return null;
        }
        return expectTestPart(currentTestPartNode);
    }

    /**
     * Finds the {@link TestPlanNode} corresponding to the next enterable {@link TestPart},
     * starting from the one after the current one (if a {@link TestPart} is already selected)
     * or the first {@link TestPart} (if there is no current {@link TestPart}), applying
     * {@link PreCondition}s along the way. Returns null if there are no enterable {@link TestPart}s.
     * <p>
     * Precondition: The test must have been entered and not ended.
     * <p>
     * Postcondition: None.
     *
     * @throws QtiCandidateStateException if the test has not been entered or if the test has ended
     */
    public TestPlanNode findNextEnterableTestPart() {
        assertTestOpen();
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
            /* We're currently inside a TestPart. Check any BranchRules declared on it */
            final TestPart currentTestPart = expectTestPart(currentTestPartNode);
            final Identifier branchTargetIdentifier = evaluateBranchRules(currentTestPart);
            if (branchTargetIdentifier!=null) {
                if (BranchRule.EXIT_TEST.equals(branchTargetIdentifier)) {
                    /* This will end the test */
                    return null;
                }
                else if (BranchRule.EXIT_TESTPART.equals(branchTargetIdentifier)) {
                    fireRuntimeWarning(currentTestPart, "Ignoring EXIT_TESTPART branchRule on a testPart");
                    nextTestPartIndex = currentTestPartNode.getSiblingIndex();
                }
                else {
                    final TestPlanNode branchTargetNode = findBranchRuleTestPartTarget(currentTestPartNode, currentTestPart, branchTargetIdentifier);
                    if (branchTargetNode!=null) {
                        nextTestPartIndex = branchTargetNode.getSiblingIndex();
                    }
                    else {
                        nextTestPartIndex = currentTestPartNode.getSiblingIndex();
                    }
                }
            }
            else {
                /* No branches, so start from next testPart */
                nextTestPartIndex = currentTestPartNode.getSiblingIndex() + 1;
            }
        }

        /* Now locate the first of these for which any preConditions are satisfied */
        TestPlanNode nextEnterableTestPartNode = null;
        int searchIndex=nextTestPartIndex;
        for (; searchIndex<testPartNodes.size(); searchIndex++) {
            final TestPlanNode testPlanNode = testPartNodes.get(searchIndex);
            final TestPart testPart = expectTestPart(testPlanNode);
            if (testPart.arePreConditionsMet(this)) {
                nextEnterableTestPartNode = testPlanNode;
                break;
            }
        }
        return nextEnterableTestPartNode;
    }

    private TestPlanNode findBranchRuleTestPartTarget(final TestPlanNode currentTestPartNode, final TestPart currentTestPart, final Identifier branchTargetIdentifier) {
        final TestPlan testPlan = testSessionState.getTestPlan();
        final TestPlanNode testPartTarget = testPlan.getTestPartNode(branchTargetIdentifier);
        if (testPartTarget!=null) {
            if (testPartTarget.getSiblingIndex() > currentTestPartNode.getSiblingIndex()) {
                return testPartTarget;
            }
            fireRuntimeWarning(currentTestPart, "Cannot branch to earlier testPart target " + branchTargetIdentifier + " from " + currentTestPart.getIdentifier());
        }
        else {
            fireRuntimeWarning(currentTestPart, "Could not find branchRule testPart target " + branchTargetIdentifier);
        }
        return null;
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
     * item instances therein. The next available {@link TestPart} will be entered, if one is available,
     * taking into account {@link BranchRule}s and {@link PreCondition}s. If there are no more
     * available {@link TestPart}s, or if there was a successful {@link BranchRule#EXIT_TEST} on the
     * current {@link TestPart} then the test itself will be ended.
     *
     * @param timestamp timestamp for this operation, which must not be null
     *
     * @return {@link TestPlanNode} corresponding to the newly-selected {@link TestPart}, or null if there
     * were not more available {@link TestPart}s and the test was exited.
     *
     * @throws QtiCandidateStateException if the test has not been entered, or has ended
     * @throws IllegalArgumentException if timestamp is null
     */
    public TestPlanNode enterNextAvailableTestPart(final Date timestamp) {
        Assert.notNull(timestamp, "timestamp");
        assertTestOpen();
        endControlObjectTimer(testSessionState, timestamp);

        final TestPlan testPlan = testSessionState.getTestPlan();
        final List<TestPlanNode> testPartNodes = testPlan.getTestPartNodes();
        final TestPlanNode currentTestPartNode = getCurrentTestPartNode();

        /* Exit current testPart (if appropriate) and locate next testPart */
        int nextTestPartIndex = 0;
        if (currentTestPartNode!=null) {
            final TestPart currentTestPart = expectTestPart(currentTestPartNode);
            final TestPartSessionState currentTestPartSessionState = expectTestPartSessionState(currentTestPartNode);
            assertTestPartEnded(currentTestPartSessionState);

            /* Exit current testPart */
            exitCurrentTestPart(currentTestPartNode, timestamp);

            /* Check any BranchRules declared on this testPart */
            final Identifier branchTargetIdentifier = evaluateBranchRules(currentTestPart);
            if (branchTargetIdentifier!=null) {
                if (BranchRule.EXIT_TEST.equals(branchTargetIdentifier)) {
                    logger.debug("branchRule has requested EXIT_TEST");
                    currentTestPartSessionState.setBranchRuleTarget(BranchRule.EXIT_TEST.toString());
                    markRemainingTestPartNodesAsJumped(currentTestPartNode);
                    testSessionState.setEndTime(timestamp);
                    return null;
                }
                else if (BranchRule.EXIT_TESTPART.equals(branchTargetIdentifier)) {
                    logger.debug("branchRule has requested EXIT_TESTPART - ignoring as we are already on a testPart");
                    fireRuntimeWarning(currentTestPart, "Ignoring invalid EXIT_TESTPART branchRule on a testPart");
                    nextTestPartIndex = currentTestPartNode.getSiblingIndex();
                }
                else {
                    /* Must be a branch to an explicit testPart */
                    final TestPlanNode branchTargetNode = findBranchRuleTestPartTarget(currentTestPartNode, currentTestPart, branchTargetIdentifier);
                    logger.debug("branchRule has resolved to target {}", branchTargetNode);
                    if (branchTargetNode!=null) {
                        nextTestPartIndex = branchTargetNode.getSiblingIndex();
                        currentTestPartSessionState.setBranchRuleTarget(branchTargetNode.getKey().toString());
                        markIntermediateTestPartNodesAsJumped(currentTestPartNode, branchTargetNode);
                    }
                    else {
                        nextTestPartIndex = currentTestPartNode.getSiblingIndex();
                    }
                }
            }

            /* Choose next testPart index */
            nextTestPartIndex = currentTestPartNode.getSiblingIndex() + 1;
        }

        /* Work from next testPart onwards, applying preConditions until successful (or we run out of testParts) */
        TestPlanNode nextAvailableTestPartNode = null;
        int searchIndex=nextTestPartIndex;
        for (; searchIndex<testPartNodes.size(); searchIndex++) {
            final TestPlanNode testPlanNode = testPartNodes.get(searchIndex);
            final TestPart testPart = expectTestPart(testPlanNode);
            if (testPart.arePreConditionsMet(this)) {
                nextAvailableTestPartNode = testPlanNode;
                break;
            }
            else {
                /* Record failed preCondition */
                expectTestPartSessionState(testPlanNode).setPreConditionFailed(true);
            }
        }

        /* Exit test if no more testParts are available */
        if (nextAvailableTestPartNode==null) {
            logger.debug("No more testParts available, so ending test");
            testSessionState.setEndTime(timestamp);
            return null;
        }

        /* Enter next testPart */
        logger.debug("Entering testPart {} and running template processing on each item", nextAvailableTestPartNode.getIdentifier());
        final TestPart nextTestPart = expectTestPart(nextAvailableTestPartNode);
        final TestPartSessionState nextTestPartSessionState = expectTestPartSessionState(nextAvailableTestPartNode);
        testSessionState.setCurrentTestPartKey(nextAvailableTestPartNode.getKey());
        nextTestPartSessionState.setEntryTime(timestamp);
        startControlObjectTimer(nextTestPartSessionState, timestamp);
        startControlObjectTimer(testSessionState, timestamp);

        /* Perform template processing on each item */
        final List<TestPlanNode> itemRefNodes = nextAvailableTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
        for (final TestPlanNode itemRefNode : itemRefNodes) {
            performTemplateProcessing(itemRefNode, timestamp);
        }

        /* If linear navigation, select the first item (if possible) */
        if (nextTestPart.getNavigationMode()==NavigationMode.LINEAR) {
            logger.debug("Auto-selecting first item in testPart as we are in LINEAR mode");
            enterNextEnterableItemOrEndTestPart(nextAvailableTestPartNode, timestamp);
        }

        return nextAvailableTestPartNode;
    }

    private void exitCurrentTestPart(final TestPlanNode currentTestPartNode, final Date timestamp) {
        /* Check pre-condition on testPart */
        final TestPartSessionState currentTestPartSessionState = expectTestPartSessionState(currentTestPartNode);
        assertTestPartEnded(currentTestPartSessionState);

        /* Exit all items */
        for (final TestPlanNode itemRefNode : currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF)) {
            getItemSessionController(itemRefNode).exitItem(timestamp);
        }

        /* Exit all assessmentSections */
        for (final TestPlanNode testPlanNode : currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_SECTION)) {
            final AssessmentSectionSessionState assessmentSectionSessionState = testSessionState.getAssessmentSectionSessionStates().get(testPlanNode.getKey());
            assessmentSectionSessionState.setExitTime(timestamp);
        }

        /* Exit the testPart itself */
        currentTestPartSessionState.setExitTime(timestamp);

        /* Update state */
        testSessionState.setCurrentTestPartKey(null);
    }

    /**
     * {@link BranchRule} helper to mark all {@link TestPart} nodes between the given start node
     * and the given target {@link TestPart} as having been jumped.
     */
    private void markIntermediateTestPartNodesAsJumped(final TestPlanNode startTestPlanNode, final TestPlanNode branchTargetNode) {
        final TestPlan testPlan = testSessionState.getTestPlan();
        final List<TestPlanNode> testPartNodes = testPlan.getTestPartNodes();
        for (int testPartIndex = startTestPlanNode.getSiblingIndex(); testPartIndex < branchTargetNode.getSiblingIndex(); testPartIndex++) {
            final TestPlanNode testPartNode = testPartNodes.get(testPartIndex);
            markTestPartNodeAsJumped(testPartNode);
        }
    }

    /**
     * {@link BranchRule} helper to mark all {@link TestPart} nodes after the given start Node
     * as having been jumped.
     */
    private void markRemainingTestPartNodesAsJumped(final TestPlanNode startTestPlanNode) {
        final TestPlan testPlan = testSessionState.getTestPlan();
        final List<TestPlanNode> testPartNodes = testPlan.getTestPartNodes();
        for (int testPartIndex=startTestPlanNode.getSiblingIndex(); testPartIndex<testPartNodes.size(); testPartIndex++) {
            final TestPlanNode testPartNode = testPartNodes.get(testPartIndex);
            markTestPartNodeAsJumped(testPartNode);
        }
    }

    private void markTestPartNodeAsJumped(final TestPlanNode testPartNode) {
        final TestPartSessionState testPartSessionState = expectTestPartSessionState(testPartNode);
        testPartSessionState.setJumpedByBranchRule(true);
    }

    /**
     * Performs template processing on the given {@link TestPlanNode} corresponding to an
     * {@link AssessmentItemRef}
     *
     * @param timestamp timestamp for this operation, which must not be null
     *
     * @throws IllegalArgumentException if timestamp is null
     */
    private void performTemplateProcessing(final TestPlanNode itemRefNode, final Date timestamp) {
        Assert.notNull(timestamp, "timestamp");
        Assert.notNull(itemRefNode);

        final AssessmentItemRef assessmentItemRef = expectItemRef(itemRefNode);
        final List<TemplateDefault> templateDefaults = assessmentItemRef.getTemplateDefaults();

        final ItemSessionController itemSessionController = getItemSessionController(itemRefNode);
        itemSessionController.performTemplateProcessing(timestamp, templateDefaults);
    }

    /**
     * Returns whether we the current {@link TestPart} may be exited. This is normally the case, but
     * {@link EffectiveItemSessionControl#isAllowSkipping()} and {@link EffectiveItemSessionControl#isValidateResponses()}
     * may prevent that happening.
     * <p>
     * Precondition: there must be a current {@link TestPart}.
     * <p>
     * Postcondition: None
     *
     * @throws QtiCandidateStateException if no test part is selected
     */
    public boolean mayEndCurrentTestPart() {
        final TestPlanNode currentTestPartNode = assertCurrentTestPartNode();
        final TestPart currentTestPart = expectTestPart(currentTestPartNode);
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
     * Precondition: {@link #mayEndCurrentTestPart()} must return true
     * <p>
     * Postcondition: If the {@link TestPart} has {@link SubmissionMode#SIMULTANEOUS} then
     * any uncommitted responses will be committed, then Response Processing and Outcome Processing will be run.
     * All item and assessment section states within the testPart will be ended if that hasn't
     * happened already; current test part state will be marked as ended; current item will be cleared.
     *
     * @param timestamp timestamp for this operation, which must not be null
     *
     * @throws QtiCandidateStateException if there is no current {@link TestPart}, or if
     *   {@link #mayEndCurrentTestPart()} returns false.
     * @throws IllegalArgumentException if timestamp is null
     */
    public void endCurrentTestPart(final Date timestamp) {
        Assert.notNull(timestamp, "timestamp");
        if (!mayEndCurrentTestPart()) {
            throw new QtiCandidateStateException("Current test part cannot be ended");
        }
        final TestPlanNode currentTestPartNode = assertCurrentTestPartNode();

        /* Perform logic for ending testPart */
        endCurrentTestPart(currentTestPartNode, timestamp);

        logger.debug("Ended testPart {}", currentTestPartNode.getIdentifier());
    }

    private void endCurrentTestPart(final TestPlanNode currentTestPartNode, final Date timestamp) {
        final TestPart currentTestPart = expectTestPart(currentTestPartNode);
        final TestPartSessionState currentTestPartSessionState = expectTestPartSessionState(currentTestPartNode);
        final List<TestPlanNode> itemRefNodes = currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);

        /* If in SIMULTANEOUS mode, then commit responses on each item that has been visited and invoke run RP */
        if (currentTestPart.getSubmissionMode()==SubmissionMode.SIMULTANEOUS) {
            for (final TestPlanNode itemRefNode : itemRefNodes) {
                final ItemSessionController itemSessionController = getItemSessionController(itemRefNode);
                final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();
                if (itemSessionState.isEntered()) {
                    if (itemSessionState.isSuspended()) {
                        itemSessionController.unsuspendItemSession(timestamp);
                    }
                    if (itemSessionState.hasUncommittedResponseValues()) {
                        itemSessionController.commitResponses(timestamp);
                    }
                    itemSessionController.performResponseProcessing(timestamp);
                }
            }
        }

        /* End all items (if not done so already due to RP ending the item or during LINEAR navigation) */
        for (final TestPlanNode itemRefNode : itemRefNodes) {
            final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(itemRefNode.getKey());
            if (!itemSessionState.isEnded() && !(itemSessionState.isPreConditionFailed() || itemSessionState.isJumpedByBranchRule())) {
                getItemSessionController(itemRefNode).endItem(timestamp);
            }
        }

        /* End all assessmentSections (if not done so already during LINEAR navigation) */
        for (final TestPlanNode testPlanNode : currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_SECTION)) {
            final AssessmentSectionSessionState assessmentSectionSessionState = testSessionState.getAssessmentSectionSessionStates().get(testPlanNode.getKey());
            if (!assessmentSectionSessionState.isEnded() && !(assessmentSectionSessionState.isPreConditionFailed() || assessmentSectionSessionState.isJumpedByBranchRule())) {
                assessmentSectionSessionState.setEndTime(timestamp);
                if (assessmentSectionSessionState.getDurationIntervalStartTime()!=null) {
                    endControlObjectTimer(assessmentSectionSessionState, timestamp);
                }
            }
        }

        /* End testPart */
        currentTestPartSessionState.setEndTime(timestamp);
        endControlObjectTimer(currentTestPartSessionState, timestamp);

        /* Update test duration */
        touchControlObjectTimer(testSessionState, timestamp);

        /* Deselect item */
        testSessionState.setCurrentItemKey(null);

        /* Finally, if in SIMULTANEOUS mode then invoke outcome processing.
         * (We do this last to ensure that the various duration values have been updated
         * so that they are accurate if accessed during OP.)
         */
        if (currentTestPart.getSubmissionMode()==SubmissionMode.SIMULTANEOUS) {
            performOutcomeProcessing();
        }
    }

    /**
     * Exits the test after it has been ended.
     * <p>
     * Precondition: The test must have been ended and not already exited.
     * <p>
     * Postcondition: The test will be marked as being exited.
     *
     * @param timestamp timestamp for this operation, which must not be null
     *
     * @throws QtiCandidateStateException if the test has not been ended, or has already been
     *   exited.
     * @throws IllegalArgumentException if timestamp is null
     */
    public void exitTest(final Date timestamp) {
        Assert.notNull(timestamp, "timestamp");
        assertTestEnded();
        assertTestNotExited();

        testSessionState.setExitTime(timestamp);
        logger.debug("Exited test");
    }

    //-------------------------------------------------------------------

    /**
     * Exits an incomplete test before it has been ended, using the current
     * (not necessarily committed) responses to the items in the current {@link TestPart},
     * then invoking Outcome Processing, closing the {@link TestPart}, then immediately
     * ending and exiting the test itself.
     * <p>
     * This is possibly more of use to proctors than candidates.
     * <p>
     * NOTE: This feature was added just before the release of 1.0.0. More investigation is
     * probably required to make sure it has sensible semantics.
     * <p>
     * Precondition: The test must not have been exited.
     * <p>
     * Postcondition: The current {@link TestPart} (if available) will have been ended as in
     * {@link #endCurrentTestPart(Date)}. The test itself will be ended (if not done already)
     * and exited.
     *
     * @throws QtiCandidateStateException if the test has already been exited.
     * @throws IllegalArgumentException if timestamp is null
     */
    public void exitTestIncomplete(final Date timestamp) {
        Assert.notNull(timestamp, "timestamp");
        assertTestNotExited();

        /* End current testPart, ignoring any validation issues. This will perform OP */
        final TestPlanNode currentTestPartNode = getCurrentTestPartNode();
        if (currentTestPartNode!=null) {
           endCurrentTestPart(currentTestPartNode, timestamp);
           exitCurrentTestPart(currentTestPartNode, timestamp);
        }

        /* Mark test as ended */
        if (testSessionState.isOpen()) {
            testSessionState.setEndTime(timestamp);
        }

        /* Then exit test */
        testSessionState.setExitTime(timestamp);
        endControlObjectTimer(testSessionState, timestamp);
        logger.debug("Exited incomplete test");
    }

    //-------------------------------------------------------------------
    // Nonlinear navigation within a testPart

    /**
     * Returns whether the current {@link NavigationMode#NONLINEAR} {@link TestPart} contains
     * the item having the given {@link TestPlanNodeKey}.
     * <p>
     * Precondition: We must be inside a {@link TestPart} having {@link NavigationMode#NONLINEAR}
     * navigation mode. The {@link TestPart} must be open.
     * <p>
     * Postcondition: None.
     *
     * @param itemKey key for the requested item, which must not be null.
     *
     * @throws IllegalArgumentException if the given itemKey is null, or if it does not correspond to an item in the test.
     * @throws QtiCandidateStateException if no testPart is selected, if the current testPart is not
     *   open or does not have {@link NavigationMode#NONLINEAR}
     *
     * @see #selectItemNonlinear(Date, TestPlanNodeKey)
     */
    public boolean maySelectItemNonlinear(final TestPlanNodeKey itemKey) {
        Assert.notNull(itemKey, "itemKey");
        final TestPlanNode currentTestPartNode = assertCurrentTestPartNode();
        assertNonlinearTestPart(currentTestPartNode);

        final TestPlanNode itemRefNode = assertItemRefNode(itemKey);
        return itemRefNode.hasAncestor(currentTestPartNode);
    }

    /**
     * Select an item within the current {@link NavigationMode#NONLINEAR} {@link TestPart}, or
     * deselects the current item is the given key is null.
     * <p>
     * Precondition: We must be inside a {@link TestPart} having {@link NavigationMode#NONLINEAR}
     * navigation mode. The {@link TestPart} must be open.
     * <p>
     * Postcondition: The current item session (if applicable) will be suspended. The requested
     * item (if applicable) will be entered (if not already entered) or unsuspended (if not yet ended).
     * Duration timers on parent {@link AssessmentSection}s will be updated if they have not been
     * ended.
     *
     * @param timestamp timestamp for this operation, which must not be null
     * @param itemKey key for the requested item, or null to indicate that we want to deselect the
     *   current item.
     *
     * @return true if the current testPart contains an item having the given itemKey, false otherwise.
     *
     * @throws IllegalArgumentException if the timestamp is null, or if itemKey is not null and does
     *   not correspond to an item within the test
     * @throws QtiCandidateStateException if no testPart is selected, if the current testPart is not
     *   open or does not have {@link NavigationMode#NONLINEAR}, or if the requested itemKey is not within the
     *   current testPart.
     *
     * @see #maySelectItemNonlinear(TestPlanNodeKey)
     */
    public TestPlanNode selectItemNonlinear(final Date timestamp, final TestPlanNodeKey itemKey) {
        Assert.notNull(timestamp, "timestamp");
        final TestPlanNode currentTestPartNode = assertCurrentTestPartNode();
        final TestPartSessionState currentTestPartSessionState = expectTestPartSessionState(currentTestPartNode);
        assertNonlinearTestPart(currentTestPartNode);
        assertTestPartOpen(currentTestPartSessionState);

        /* If an item is currently selected then suspend the session (if still open) and update timer on parent sections */
        final TestPlanNode currentItemRefNode = getCurrentItemRefNode();
        if (currentItemRefNode!=null) {
            final ItemSessionState currentItemSessionState = expectItemRefState(currentItemRefNode);
            if (!currentItemSessionState.isEnded()) {
                getItemSessionController(currentItemRefNode).suspendItemSession(timestamp);
            }
            for (final TestPlanNode sectionNode : currentItemRefNode.searchAncestors(TestNodeType.ASSESSMENT_SECTION)) {
                endControlObjectTimer(expectAssessmentSectionSessionState(sectionNode), timestamp);
            }
        }

        /* Touch duration on test & testPart */
        touchControlObjectTimer(testSessionState, timestamp);
        touchControlObjectTimer(currentTestPartSessionState, timestamp);

        if (itemKey!=null) {
            final TestPlanNode newItemRefNode = assertItemRefNode(itemKey);
            final ItemSessionState newItemSessionState = expectItemRefState(newItemRefNode);
            if (!newItemRefNode.hasAncestor(currentTestPartNode)) {
                throw new QtiCandidateStateException(newItemRefNode + " is not a descendant of " + currentTestPartNode);
            }
            testSessionState.setCurrentItemKey(newItemRefNode.getKey());

            /* Enter/unsuspend item as appropriate */
            final ItemSessionController newItemSessionController = getItemSessionController(newItemRefNode);
            if (!newItemSessionState.isEntered()) {
                newItemSessionController.enterItem(timestamp);
            }
            else if (!newItemSessionState.isEnded()) {
                newItemSessionController.unsuspendItemSession(timestamp);
            }

            /* enter and/or start timer on parent sections */
            for (final TestPlanNode sectionNode : newItemRefNode.searchAncestors(TestNodeType.ASSESSMENT_SECTION)) {
                final AssessmentSectionSessionState assessmentSectionSessionState = expectAssessmentSectionSessionState(sectionNode);
                if (!assessmentSectionSessionState.isEntered()) {
                    assessmentSectionSessionState.setEntryTime(timestamp);
                }
                if (!assessmentSectionSessionState.isEnded()) {
                    startControlObjectTimer(expectAssessmentSectionSessionState(sectionNode), timestamp);
                }
            }

            return newItemRefNode;
        }
        else {
            /* Allow deselection */
            testSessionState.setCurrentItemKey(null);
            return null;
        }
    }

    private TestPart assertNonlinearTestPart(final TestPlanNode currentTestPartNode) {
        final TestPart currentTestPart = expectTestPart(currentTestPartNode);
        if (currentTestPart.getNavigationMode()!=NavigationMode.NONLINEAR) {
            throw new QtiCandidateStateException("Expected this testPart to have NONLINEAR navigationMode");
        }
        return currentTestPart;
    }

    //-------------------------------------------------------------------
    // Linear navigation within a testPart

    /**
     * Returns whether the currently-selected item within a {@link TestPart} with
     * {@link NavigationMode#LINEAR} may be advanced. (If the {@link TestPart} has
     * {@link SubmissionMode#INDIVIDUAL} then advancing ends te item session, which may
     * be prevented by {@link ItemSessionControl} conditions.)
     * <p>
     * Precondition: We must be inside a {@link TestPart} having {@link NavigationMode#LINEAR}
     * navigation mode. An item must be selected.
     * <p>
     * Postcondition: None
     *
     * @return true if the item may be ended, false otherwise.
     *
     * @throws QtiCandidateStateException if we are not currently in a {@link TestPart},
     *   if the current {@link TestPart} does not have {@link NavigationMode#NONLINEAR},
     *   or if there is no currently selected item.
     */
    public boolean mayAdvanceItemLinear() {
        final TestPlanNode currentTestPartNode = assertCurrentTestPartNode();
        final TestPart currentTestPart = assertLinearTestPart(currentTestPartNode);
        final TestPlanNodeKey currentItemKey = assertItemSelected();

        /* The only thing preventing submission is allowSkipping and validateResponses, which
         * only apply in INDIVIDUAL submission mode.
         */
        if (currentTestPart.getSubmissionMode()==SubmissionMode.INDIVIDUAL) {
            final ItemSessionState itemSessionState = expectItemRefState(currentItemKey);
            final EffectiveItemSessionControl effectiveItemSessionControl = testProcessingMap.resolveEffectiveItemSessionControl(expectItemRefNode(currentItemKey));
            if (!itemSessionState.isResponded() && !effectiveItemSessionControl.isAllowSkipping()) {
                /* Not responded, and allowSkipping=false */
                logger.debug("Item {} has not been responded and allowSkipping=false, so ending item is forbidden", currentItemKey);
                return false;
            }
            if (itemSessionState.isRespondedInvalidly() && effectiveItemSessionControl.isValidateResponses()) {
                /* Invalid response, and validateResponses=true */
                logger.debug("Item {} has been responded with bad/invalid responses and validateResponses=true, so ending item will be forbidden", currentItemKey);
                return false;
            }
        }
        return true;
    }

    /**
     * Advances the currently-selected item within a {@link TestPart} with
     * {@link NavigationMode#LINEAR}, if possible.
     * <p>
     * If the {@link TestPart} has {@link SubmissionMode#INDIVIDUAL} then the current item session
     * will be ended first. Otherwise, the current item session will be suspended.
     * <p>
     * The test will then advance to the next available item, or will end the {@link TestPart},
     * either because there are no more available items or because we successfully evaluated a
     * {@link BranchRule#EXIT_TESTPART}.
     * <p>
     * A {@link BranchRule#EXIT_TEST} is treated the same as {@link BranchRule#EXIT_TESTPART} on
     * tests with a single {@link TestPart}, as this will make more feedback available. In a
     * multi-part test, a successful {@link BranchRule#EXIT_TEST} will end the current
     * {@link TestPart}, then jump remaining {@link TestPart}s, then end the test itself.
     * <p>
     * Precondition: We must be inside a {@link TestPart} having {@link NavigationMode#LINEAR}
     * navigation mode. An item must be selected. If we are in {@link SubmissionMode#INDIVIDUAL} then
     * the effective {@link ItemSessionControl} for the selected item must allow it to be ended.
     * <p>
     * Postcondition: Current item session will be ended (if in {@link SubmissionMode#INDIVIDUAL} or
     * suspended (if in {@link SubmissionMode#SIMULTANEOUS}). The next available item will be
     * selected, if such a thing exists, taking into account {@link BranchRule}s and {@link PreCondition}s.
     * If there are no more items available then the {@link TestPart} will be ended.
     * If there was a successful {@link BranchRule#EXIT_TESTPART} on the current
     * item and the test contains multiple {@link TestPart}s, then the {@link TestPart} will then
     * be exited, future {@link TestPart}s will be skipped and the test itself will be ended.
     *
     * @param timestamp timestamp for this operation, which must not be null
     *
     * @return the {@link TestPlanNode} corresponding to the next selected item,
     *   or null if there are no more available items and the {@link TestPart} has
     *   ended.
     *
     * @throws QtiCandidateStateException if we are not currently in a {@link TestPart},
     *   if the current {@link TestPart} does not have {@link NavigationMode#NONLINEAR},
     *   if there is no currently selected item, or if the effective {@link ItemSessionControl}
     *   for the item does not allow it to be ended (when in {@link SubmissionMode#SIMULTANEOUS} mode).
     * @throws IllegalArgumentException if timestamp is null
     */
    public TestPlanNode advanceItemLinear(final Date timestamp) {
        Assert.notNull(timestamp, "timestamp");
        final TestPlanNode currentTestPartNode = assertCurrentTestPartNode();
        final TestPart currentTestPart = assertLinearTestPart(currentTestPartNode);
        final TestPartSessionState currentTestPartSessionState = expectTestPartSessionState(currentTestPartNode);
        final TestPlanNodeKey currentItemKey = assertItemSelected();

        /* Make sure item can be ended (see mayEndLinearItem() for logic summary) */
        final ItemSessionState itemSessionState = expectItemRefState(currentItemKey);
        final TestPlanNode currentItemRefNode = expectItemRefNode(currentItemKey);
        final SubmissionMode submissionMode = currentTestPart.getSubmissionMode();
        if (submissionMode==SubmissionMode.INDIVIDUAL) {
            final EffectiveItemSessionControl effectiveItemSessionControl = testProcessingMap.resolveEffectiveItemSessionControl(currentItemRefNode);
            if (!itemSessionState.isResponded() && !effectiveItemSessionControl.isAllowSkipping()) {
                throw new QtiCandidateStateException("Item " + currentItemKey + " has not been responded and allowSkipping=false, so ending item is forbidden");
            }
            if (itemSessionState.isRespondedInvalidly() && effectiveItemSessionControl.isValidateResponses()) {
                throw new QtiCandidateStateException("Item " + currentItemKey + " has been responded with bad/invalid responses and validateResponses=true, so ending item is forbidden");
            }
        }

        final ItemSessionController currentItemSessionController = getItemSessionController(currentItemRefNode);
        if (submissionMode==SubmissionMode.INDIVIDUAL) {
            /* We're in INDIVIDUAL mode, so end item (if that hasn't already happened during RP) */
            final ItemSessionState currentItemSessionState = currentItemSessionController.getItemSessionState();
            if (!currentItemSessionState.isEnded()) {
                currentItemSessionController.endItem(timestamp);
            }
        }
        else {
            /* We're in SIMULTANEOUS mode, so suspend the item session */
            currentItemSessionController.suspendItemSession(timestamp);
        }

        /* Update duration on test and testPart */
        touchControlObjectTimer(currentTestPartSessionState, timestamp);
        touchControlObjectTimer(testSessionState, timestamp);

        /* Log what's happened */
        logger.debug("Finished item {}", currentItemKey);

        /* Select next item (if one is available) or end the testPart */
        return enterNextEnterableItemOrEndTestPart(currentTestPartNode, timestamp);
    }

    /**
     * Enters the next item in the given ({@link NavigationMode#LINEAR}) {@link TestPart}, applying
     * any {@link PreCondition}s on the way.
     * <p>
     * Returns the newly selected {@link TestPlanNode}, or null if there are no more items
     * that can be entered.
     */
    private TestPlanNode enterNextEnterableItemOrEndTestPart(final TestPlanNode currentTestPartNode, final Date timestamp) {
        /* First, we work out where to start looking from, taking into account any branchRules */
        TestPlanNode startSearchNode = null;
        final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
        if (currentItemKey!=null) {
            final TestPlanNode currentItemNode = getCurrentItemRefNode();
            final AssessmentItemRef currentItemRef = expectItemRef(currentItemNode);

            /* Evaluate any branchRules on current item */
            boolean branchSucceeded = false;
            final Identifier branchTargetIdentifier = evaluateBranchRules(currentItemRef);
            if (branchTargetIdentifier!=null) {
                final ItemSessionState currentItemState = expectItemRefState(currentItemNode);
                if (BranchRule.EXIT_TEST.equals(branchTargetIdentifier) || BranchRule.EXIT_TESTPART.equals(branchTargetIdentifier)) {
                    logger.debug("branchRule requested {}", branchTargetIdentifier);

                    /* First branch to end of testPart */
                    currentItemState.setBranchRuleTarget(branchTargetIdentifier.toString());

                    /* Mark all nodes until end of testPart as having been skipped */
                    markRemainingNodesInTestPartAsJumped(currentItemNode);

                    /* End current testPart */
                    testSessionState.setCurrentItemKey(null);
                    endCurrentTestPart(timestamp);

                    /* If we're actually doing EXIT_TEST on a multi-part test, then also exit current testPart, jump remaining testParts and end the test itself */
                    if (BranchRule.EXIT_TEST.equals(branchTargetIdentifier) && testSessionState.getTestPlan().getTestPartNodes().size() > 1) {
                        exitCurrentTestPart(currentTestPartNode, timestamp);
                        markRemainingTestPartNodesAsJumped(currentTestPartNode);
                        testSessionState.setEndTime(timestamp);
                        if (testSessionState.getDurationIntervalStartTime()!=null) {
                            endControlObjectTimer(testSessionState, timestamp);
                        }
                    }

                    /* End of testPart, so no more nodes */
                    branchSucceeded = true;
                    return null;
                }
                else if (BranchRule.EXIT_SECTION.equals(branchTargetIdentifier)) {
                    /* Branch to end of section */
                    logger.debug("branchRule requested EXIT_SECTION");
                    currentItemState.setBranchRuleTarget(BranchRule.EXIT_SECTION.toString());

                    /* End section */
                    final TestPlanNode parentSectionNode = currentItemNode.getParent();
                    final AssessmentSectionSessionState assessmentSectionSessionState = expectAssessmentSectionSessionState(parentSectionNode);
                    assessmentSectionSessionState.setEndTime(timestamp);
                    endControlObjectTimer(assessmentSectionSessionState, timestamp);

                    /* Then walk to next node */
                    startSearchNode = walkToNextSiblingOrAncestorNode(parentSectionNode, timestamp);

                    /* Mark intermediate nodes as having been skipped */
                    markIntermediateNodesAsJumped(currentItemNode, startSearchNode);
                    branchSucceeded = true;
                }
                else {
                    /* Branch to requested item/section */
                    logger.debug("branchRule requested target {}", branchTargetIdentifier);
                    startSearchNode = walkToBranchTarget(currentItemNode, branchTargetIdentifier, timestamp);
                    if (startSearchNode!=null) {
                        currentItemState.setBranchRuleTarget(startSearchNode.getKey().toString());
                        branchSucceeded = true;
                    }
                    else {
                        fireRuntimeWarning(testProcessingMap.resolveAbstractPart(currentItemNode),
                                "branchRule failed to move forward to target " + branchTargetIdentifier
                                + " so is being ignored. Check the validity of this test!");
                    }
                }
            }

            /* If no branchRule, or the branch attempt failed, then walk to next sibling/ancestor of the current item
             * so that we can search for next enterable item */
            if (!branchSucceeded) {
                startSearchNode = walkToNextSiblingOrAncestorNode(currentItemNode, timestamp);
            }
        }
        else {
            /* Haven't entered any items yet, so search from first child (if available) */
            if (currentTestPartNode.getChildCount()==0) {
                return null;
            }
            startSearchNode = currentTestPartNode.getChildAt(0);
        }

        /* Walk to next enterable item */
        final TestPlanNode nextItemRefNode = walkToNextEnterableItemDepthFirst(currentTestPartNode, startSearchNode, timestamp);
        if (nextItemRefNode!=null) {
            /* Enterable item */
            testSessionState.setCurrentItemKey(nextItemRefNode.getKey());
            getItemSessionController(nextItemRefNode).enterItem(timestamp);
            logger.debug("Entered item {}", nextItemRefNode.getKey());

        }
        else {
            /* No (more) items available, so end testPart */
            testSessionState.setCurrentItemKey(null);
            endCurrentTestPart(timestamp);
            logger.debug("Linear navigation has reached end of testPart");
        }
        return nextItemRefNode;
    }

    private TestPlanNode walkToNextSiblingOrAncestorNode(final TestPlanNode startNode, final Date timestamp) {
        final TestPlanNode currentNode = startNode;
        if (currentNode.hasFollowingSibling()) {
            /* Walk to next sibling */
            return currentNode.getFollowingSibling();
        }
        else {
            /* No more siblings, so go up to parent then onto its next sibling */
            final TestPlanNode parentNode = currentNode.getParent();
            switch (parentNode.getTestNodeType()) {
                case TEST_PART:
                    /* We've reached the end of the TestPart, so stop searching altogether */
                    return null;

                case ASSESSMENT_SECTION:
                    /* Reached end of section. So exit then move on */
                    final AssessmentSectionSessionState assessmentSectionSessionState = expectAssessmentSectionSessionState(parentNode);
                    assessmentSectionSessionState.setEndTime(timestamp);
                    endControlObjectTimer(assessmentSectionSessionState, timestamp);
                    return walkToNextSiblingOrAncestorNode(parentNode, timestamp);

                default:
                    throw new QtiLogicException("Did not expect to meet a Node of type " + currentNode.getTestNodeType());
            }
        }
    }

    /**
     * Walks from the given starting Node to the section or item node matching the given branchRule
     * target identifier.
     * <p>
     * If there are multiple branch targets, then we walk to the first one after the current node within
     * the {@link TestPlan} for the current testPart.
     *
     * @return target Node, or null if suitable target was not found.
     */
    private TestPlanNode walkToBranchTarget(final TestPlanNode startNode, final Identifier branchTargetIdentifier, final Date timestamp) {
        /* Find and check the target */
        final AbstractPart startPart = testProcessingMap.resolveAbstractPart(startNode);
        final int currentGlobalIndex = startNode.getKey().getAbstractPartGlobalIndex();
        final TestPlan testPlan = testSessionState.getTestPlan();
        final List<TestPlanNode> branchTargetNodes = testPlan.getNodes(branchTargetIdentifier);
        if (branchTargetNodes==null) {
            fireRuntimeError(startPart, "Failed to find branchRule target with identifier " + branchTargetIdentifier
                    + ", so ignoring this branchRule. Check test validity.");
            return null;
        }
        TestPlanNode branchTargetNode = null;
        for (final TestPlanNode branchTargetCandidateNode : branchTargetNodes) {
            if (branchTargetCandidateNode.getKey().getAbstractPartGlobalIndex() > currentGlobalIndex) {
                /* Found suitable target */
                branchTargetNode = branchTargetCandidateNode;
                break;
            }
        }
        if (branchTargetNode==null) {
            fireRuntimeError(startPart, "No branchRule target found with identifier " + branchTargetIdentifier
                    + " after node with key " + startNode.getKey() + ". Ignoring branchRule. Check test validity.");
            return null;
        }
        /* Make sure branch target is in this testPart */
        if (!startNode.searchEnclosingTestPartNode().equals(branchTargetNode.searchEnclosingTestPartNode())) {
            fireRuntimeError(startPart, "branchRule target found with identifier " + branchTargetIdentifier
                    + " is in a different testPart to the current node with key " + startNode.getKey()
                    + ". Ignoring branchRule. Check test validity");
            return null;
        }

        /* The walk to the branchTarget is quite complicated. Probably best explained by
         * an example:
         *
         * Suppose we're at P/A/B/C/D
         *    branch to     P/A/X/Y
         *
         * then we need to move up to the common ancestor (A), ending sections C and B on the way.
         * We then descend into X and Y, opening them as required.
         */
        TestPlanNode goingUpNode = startNode;
        while (!(branchTargetNode.hasAncestor(goingUpNode))) {
            if (goingUpNode.getTestNodeType()==TestNodeType.ASSESSMENT_SECTION) {
                final AssessmentSectionSessionState assessmentSectionSessionState = expectAssessmentSectionSessionState(goingUpNode);
                assessmentSectionSessionState.setEndTime(timestamp);
                endControlObjectTimer(assessmentSectionSessionState, timestamp);
            }
            goingUpNode = goingUpNode.getParent();
        }
        /* Now we traverse down to the startNode, entering sections as required.
         * (The easiest way to code this is to enter from the targetNode upwards.)
         */
        TestPlanNode goingDownNode = branchTargetNode;
        while (goingDownNode.hasAncestor(goingUpNode)) {
            if (goingDownNode.getTestNodeType()==TestNodeType.ASSESSMENT_SECTION) {
                final AssessmentSectionSessionState assessmentSectionSessionState = expectAssessmentSectionSessionState(goingDownNode);
                assessmentSectionSessionState.setEntryTime(timestamp);
                startControlObjectTimer(assessmentSectionSessionState, timestamp);
            }
            goingDownNode = goingDownNode.getParent();
        }

        /* Mark all intermediate nodes (in document order) as having been skipped if they haven't yet been entered */
        markIntermediateNodesAsJumped(startNode, branchTargetNode);

        /* That's us done now */
        return branchTargetNode;
    }

    /**
     * This {@link BranchRule} helper marks all {@link AssessmentSection} and
     * {@link AssessmentItemRef} between the given start and target nodes as having been jumped over
     * by the effect of the {@link BranchRule}.
     */
    private void markIntermediateNodesAsJumped(final TestPlanNode branchStartNode, final TestPlanNode branchTargetNode) {
        final TestPlan testPlan = testSessionState.getTestPlan();
        final int startIndex = testPlan.getGlobalIndex(branchStartNode);
        final int targetIndex = testPlan.getGlobalIndex(branchTargetNode);
        for (int index = startIndex+1; index < targetIndex; index++) {
            final TestPlanNode testPlanNode = testPlan.getNodeAtGlobalIndex(index);
            markSectionPartNodeAsJumped(testPlanNode);
        }
    }

    /**
     * This {@link BranchRule} helper marks all {@link AssessmentSection} and
     * {@link AssessmentItemRef} from the given starting Node until the end of the current
     * {@link TestPart} as having been jumped.
     */
    private void markRemainingNodesInTestPartAsJumped(final TestPlanNode branchStartNode) {
        final TestPlan testPlan = testSessionState.getTestPlan();
        final int startIndex = testPlan.getGlobalIndex(branchStartNode);
        final int endIndex = testPlan.getTestPlanNodeList().size();
        for (int index = startIndex+1; index < endIndex; index++) {
            final TestPlanNode testPlanNode = testPlan.getNodeAtGlobalIndex(index);
            if (testPlanNode.getTestNodeType()==TestNodeType.TEST_PART) {
                break;
            }
            markSectionPartNodeAsJumped(testPlanNode);
        }
    }

    private void markSectionPartNodeAsJumped(final TestPlanNode testPlanNode) {
        switch (testPlanNode.getTestNodeType()) {
            case ASSESSMENT_SECTION:
                final AssessmentSectionSessionState assessmentSectionSessionState = expectAssessmentSectionSessionState(testPlanNode);
                if (!assessmentSectionSessionState.isEntered()) {
                    assessmentSectionSessionState.setJumpedByBranchRule(true);
                }
                break;

            case ASSESSMENT_ITEM_REF:
                final ItemSessionState itemSessionState = expectItemRefState(testPlanNode);
                itemSessionState.setJumpedByBranchRule(true);
                break;

            default:
                throw new QtiLogicException("Unexpected switch case " + testPlanNode.getTestNodeType());
        }
    }

    private TestPlanNode walkToNextEnterableItemDepthFirst(final TestPlanNode currentTestPartNode, final TestPlanNode startNode, final Date timestamp) {
        final TestPart currentTestPart = expectTestPart(currentTestPartNode);
        TestPlanNode currentNode = startNode;
        SEARCH: while (currentNode!=null) {
            switch (currentNode.getTestNodeType()) {
                case ASSESSMENT_SECTION:
                    /* We're at a section. Check preconditions and enter (if met) or move on (otherwise) */
                    final AssessmentSection assessmentSection = expectAssessmentSection(currentNode);
                    final AssessmentSectionSessionState assessmentSectionSessionState = expectAssessmentSectionSessionState(currentNode);
                    if (currentTestPart.areJumpsEnabled() && !assessmentSection.arePreConditionsMet(this)) {
                        /* preCondition on section failed, so note this. */
                        assessmentSectionSessionState.setPreConditionFailed(true);
                    }
                    else {
                        /* Enter section and search its child nodes */
                        assessmentSectionSessionState.setEntryTime(timestamp);
                        startControlObjectTimer(assessmentSectionSessionState, timestamp);
                        if (currentNode.getChildCount()>0) {
                            currentNode = currentNode.getChildAt(0);
                            continue SEARCH;
                        }
                    }
                    break;

                case ASSESSMENT_ITEM_REF:
                    /* We're at an item. Check if it can be entered */
                    final AssessmentItemRef assessmentItemRef = expectItemRef(currentNode);
                    final ItemSessionState itemSessionState = expectItemRefState(currentNode);
                    if (currentTestPart.areJumpsEnabled() && !assessmentItemRef.arePreConditionsMet(this)) {
                        /* preCondition on assessmentItemRef, so note this. */
                       itemSessionState.setPreConditionFailed(true);
                    }
                    else {
                        /* Found enterable item */
                        return currentNode;
                    }
                    break;

                default:
                    throw new QtiLogicException("Did not expect to meet a Node of type " + currentNode.getTestNodeType());
            }
            /* If we reach here, then the a depth-first search on the currentNode yielded nothing.
             * So we move onto the next sibling. If there are no more siblings, we go up and move on.
             * Continue until we reach the end of the testPart
             */
            currentNode = walkToNextSiblingOrAncestorNode(currentNode, timestamp);
        }
        return null;
    }

    /**
     * Evaluates each {@link BranchRule} declared on the given {@link AbstractPart} in order,
     * until a {@link BranchRule} evaluates to true. If this happens, we return the target of
     * the {@link BranchRule}. If all rules evaluate to false (or there are no rules) then we
     * return null.
     */
    private Identifier evaluateBranchRules(final AbstractPart abstractPart) {
        for (final BranchRule branchRule : abstractPart.getBranchRules()) {
            if (branchRule.evaluatesTrue(this)) {
                return branchRule.getTarget();
            }
        }
        return null;
    }

    private TestPart assertLinearTestPart(final TestPlanNode currentTestPartNode) {
        final TestPart currentTestPart = expectTestPart(currentTestPartNode);
        if (currentTestPart.getNavigationMode()!=NavigationMode.LINEAR) {
            throw new QtiCandidateStateException("Expected this testPart to have LINEAR navigationMode");
        }
        return currentTestPart;
    }

    //-------------------------------------------------------------------
    // Response submission

    /**
     * Returns whether responses may be submitted for the currently selected item.
     * <p>
     * Precondition: an item must be selected.
     * <p>
     * Postcondition: none
     *
     * @throws QtiCandidateStateException if no item is selected
     */
    public boolean maySubmitResponsesToCurrentItem() {
        final TestPlanNodeKey currentItemKey = assertItemSelected();

        final ItemSessionState itemSessionState = expectItemRefState(currentItemKey);
        return !itemSessionState.isEnded();
    }

    /**
     * Handle responses for the currently selected item. First, the responses will be bound to
     * the current item as in {@link ItemSessionController#bindResponses(Date, Map)}.
     * <p>
     * In {@link SubmissionMode#INDIVIDUAL} mode, the responses are then committed as in
     * {@link ItemSessionController#commitResponses(Date)}, then response processing is run,
     * followed by outcome processing.
     * <p>
     * Precondition: an item must be selected, and its session must be open.
     * <p>
     * Postcondition: responses will be bound, validated and processed as described above.
     *
     * @param timestamp timestamp for this operation, which must not be null
     * @param responseMap Map of response data, which must not be null
     *
     * @throws QtiCandidateStateException if no item is selected or if its session is not open
     */
    public void handleResponsesToCurrentItem(final Date timestamp, final Map<Identifier, ResponseData> responseMap) {
        Assert.notNull(timestamp, "timestamp");
        Assert.notNull(responseMap, "responseMap");
        final TestPlanNodeKey currentItemKey = assertItemSelected();

        /* Touch durations on item, ancestor sections, test part and test */
        final TestPlanNode currentItemRefNode = expectItemRefNode(currentItemKey);
        touchDurations(timestamp);

        /* Bind responses */
        final ItemSessionController itemSessionController = getItemSessionController(currentItemRefNode);
        final boolean boundSuccessfully = itemSessionController.bindResponses(timestamp, responseMap);

        /* If we're in INDIVIDUAL mode, then commit responses then do RP & OP */
        final TestPart testPart = expectCurrentTestPart();
        if (testPart.getSubmissionMode()==SubmissionMode.INDIVIDUAL) {
            /* Commit responses */
            itemSessionController.commitResponses(timestamp);

            /* Run response processing if everything was bound successfully */
            if (boundSuccessfully) {
                itemSessionController.performResponseProcessing(timestamp);
            }

            /* Run outcome processing */
            performOutcomeProcessing();
        }
    }

    /**
     * Sets the candidate comment for the current item, replacing any comment that has already been
     * set.
     * <p>
     * Precondition: an item must be selected, and its session must be open. The effective
     * {@link ItemSessionControl} for the item must allow comments
     * <p>
     * Postcondition: Candidate comment will be changed.
     *
     * @param timestamp timestamp for this event, which must not be null
     * @param candidateComment comment to record, which may be null. An empty or blank comment will be
     *   treated in the same way as a null comment.
     */
    public void setCandidateCommentForCurrentItem(final Date timestamp, final String candidateComment) {
        Assert.notNull(timestamp);
        final TestPlanNodeKey currentItemKey = assertItemSelected();
        final TestPlanNode currentItemRefNode = expectItemRefNode(currentItemKey);
        final EffectiveItemSessionControl effectiveItemSessionControl = currentItemRefNode.getEffectiveItemSessionControl();
        if (!effectiveItemSessionControl.isAllowComment()) {
            throw new QtiCandidateStateException("The item has allowComment=false, so setting a candidate comment is not permitted");
        }
        logger.debug("Setting candidate comment to {}", candidateComment);

        final ItemSessionController itemSessionController = getItemSessionController(currentItemRefNode);
        itemSessionController.setCandidateComment(timestamp, candidateComment);
    }

    //-------------------------------------------------------------------

    /**
     * Returns whether the candidate may review the item having the given {@link TestPlanNodeKey}.
     * <p>
     * Returns true if the item state is ended and its effective session control allows review,
     * or showing feedback
     * <p>
     * Precondition: A TestPart must be selected
     * <p>
     * Postcondition: None
     *
     * @param itemKey {@link TestPlanNodeKey} of the item to check, which must not be null
     *
     * @throws QtiCandidateStateException if there is no selected {@link TestPart},
     *   if the requested item does not live within the current
     *   {@link TestPart}, or if its session has not been ended.
     * @throws IllegalArgumentException if itemKey is null or does not correspond to an item
     *   within the current {@link TestPart}.
     */
    public boolean mayReviewItem(final TestPlanNodeKey itemKey) {
        Assert.notNull(itemKey);
        final TestPlanNode itemRefNode = assertItemRefNode(itemKey);
        final TestPlanNode currentTestPartNode = assertCurrentTestPartNode();

        if (!itemRefNode.hasAncestor(currentTestPartNode)) {
            throw new IllegalArgumentException("Item with key " + itemKey + " does not live within the current TestPart " + currentTestPartNode.getKey());
        }
        final ItemSessionState itemSessionState = expectItemRefState(itemRefNode);
        final EffectiveItemSessionControl effectiveItemSessionControl = itemRefNode.getEffectiveItemSessionControl();

        return itemSessionState.isEnded()
                && (effectiveItemSessionControl.isAllowReview()
                || effectiveItemSessionControl.isShowFeedback());
    }

    /**
     * Returns whether the candidate may access the solution to the item having the given {@link TestPlanNodeKey}.
     * <p>
     * Returns true if the item state is ended and its effective session control allows the
     * solution to be shown
     * <p>
     * Precondition: A TestPart must be selected
     * <p>
     * Postcondition: None
     *
     * @param itemKey {@link TestPlanNodeKey} of the item to check, which must not be null
     *
     * @throws QtiCandidateStateException if there is no selected {@link TestPart},
     *   if the requested item does not live within the current
     *   {@link TestPart}, or if its session has not been ended.
     * @throws IllegalArgumentException if itemKey is null or does not correspond to an item
     *   within the current {@link TestPart}.
     */
    public boolean mayAccessItemSolution(final TestPlanNodeKey itemKey) {
        Assert.notNull(itemKey);
        final TestPlanNode itemRefNode = assertItemRefNode(itemKey);
        final TestPlanNode currentTestPartNode = assertCurrentTestPartNode();

        if (!itemRefNode.hasAncestor(currentTestPartNode)) {
            throw new IllegalArgumentException("Item with key " + itemKey + " does not live within the current TestPart " + currentTestPartNode.getKey());
        }
        final ItemSessionState itemSessionState = expectItemRefState(itemRefNode);
        final EffectiveItemSessionControl effectiveItemSessionControl = itemRefNode.getEffectiveItemSessionControl();

        return itemSessionState.isEnded()
                && effectiveItemSessionControl.isShowSolution();
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
            testSessionState.setOutcomeValue(outcomeDeclaration, computeInitialValue(outcomeDeclaration));
        }
    }

    private Value computeInitialValue(final OutcomeDeclaration declaration) {
        Assert.notNull(declaration);
        Value result;
        final DefaultValue defaultValue = declaration.getDefaultValue();
        if (defaultValue != null) {
            result = defaultValue.evaluate();
        }
        else if (declaration.hasSignature(Signature.SINGLE_INTEGER)) {
            /* (5.2 says that the default for a [presumed single] integer outcome variable should be 0) */
            result = IntegerValue.ZERO;
        }
        else if (declaration.hasSignature(Signature.SINGLE_FLOAT)) {
            /* (5.2 says that the default for a [presumed single] float outcome variable should be 0) */
            result = FloatValue.ZERO;
        }
        else {
            result = NullValue.INSTANCE;
        }
        return result;
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
        for (final TestPlanNode testPlanNode : testSessionState.getTestPlan().getTestPlanNodeList()) {
            if (testPlanNode.getTestNodeType()==TestNodeType.ASSESSMENT_ITEM_REF) {
                final ItemSessionController itemSessionController = getItemSessionController(testPlanNode);
                final ItemResult itemResult = itemSessionController.computeItemResult(result,
                        testPlanNode.getIdentifier().toString(), timestamp);
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

    private void assertTestInitialized() {
        if (!testSessionState.isInitialized()) {
            throw new QtiCandidateStateException("Test has not been initialized");
        }
    }

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
            throw new QtiCandidateStateException("Test has already ended");
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

    private void assertTestOpen() {
        assertTestEntered();
        assertTestNotEnded();
    }

    private TestPlanNodeKey assertTestPartSelected() {
        final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        if (currentTestPartKey==null) {
            throw new QtiCandidateStateException("Expected to be inside a testPart");
        }
        return currentTestPartKey;
    }

    private void assertTestPartOpen(final TestPartSessionState testPartSessionState) {
        if (!testPartSessionState.isEntered()) {
            throw new QtiCandidateStateException("Current testPart has not been entered");
        }
        if (testPartSessionState.isEnded()) {
            throw new QtiCandidateStateException("Current testPart has been ended");
        }
    }

    private void assertTestPartEnded(final TestPartSessionState testPartSessionState) {
        if (!testPartSessionState.isEnded()) {
            throw new QtiCandidateStateException("Current testPart has not been ended");
        }
    }

    private TestPlanNodeKey assertItemSelected() {
        final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
        if (currentItemKey==null) {
            throw new QtiCandidateStateException("Expected to be inside an item");
        }
        return currentItemKey;
    }

    //-------------------------------------------------------------------
    // Lookup helpers & sanity checkers

    private TestPlanNode assertCurrentTestPartNode() {
        final TestPlanNodeKey currentTestPartKey = assertTestPartSelected();
        return expectTestPartNode(currentTestPartKey);
    }

    private TestPlanNode getCurrentTestPartNode() {
        final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        if (currentTestPartKey==null) {
            return null;
        }
        return expectTestPartNode(currentTestPartKey);
    }

    private TestPart expectCurrentTestPart() {
        final TestPart result = getCurrentTestPart();
        if (result==null) {
            throw new QtiLogicException("No current test part");
        }
        return result;
    }

    private TestPlanNode expectTestPartNode(final TestPlanNodeKey key) {
        final TestPlanNode testPlanNode = testSessionState.getTestPlan().getNode(key);
        if (testPlanNode.getTestNodeType()!=TestNodeType.TEST_PART) {
            throw new QtiLogicException("Expected TestPlanNode with key " + key
                    + " to be of type " + TestNodeType.TEST_PART
                    + " but got " + testPlanNode.getTestNodeType());
        }
        return testPlanNode;
    }

    private TestPart expectTestPart(final TestPlanNode testPlanNode) {
        final AbstractPart result = testProcessingMap.resolveAbstractPart(testPlanNode);
        if (result==null || !(result instanceof TestPart)) {
            throw new QtiLogicException("Expected " + testPlanNode + " to resolve to a TestPart");
        }
        return (TestPart) result;
    }

    private TestPartSessionState expectTestPartSessionState(final TestPlanNode testPlanNode) {
        final TestPartSessionState testPartSessionState = testSessionState.getTestPartSessionStates().get(testPlanNode.getKey());
        if (testPartSessionState==null) {
            throw new QtiLogicException("No TestPartSessionState corresponding to " + testPlanNode);
        }
        return testPartSessionState;
    }

    private AssessmentSectionSessionState expectAssessmentSectionSessionState(final TestPlanNode testPlanNode) {
        final AssessmentSectionSessionState assessmentSectionSessionState = testSessionState.getAssessmentSectionSessionStates().get(testPlanNode.getKey());
        if (assessmentSectionSessionState==null) {
            throw new QtiLogicException("No AssessmentSectionSessionState corresponding to " + testPlanNode);
        }
        return assessmentSectionSessionState;
    }

    private AssessmentSection expectAssessmentSection(final TestPlanNode testPlanNode) {
        final AbstractPart result = testProcessingMap.resolveAbstractPart(testPlanNode);
        if (result==null || !(result instanceof AssessmentSection)) {
            throw new QtiLogicException("Expected " + testPlanNode + " to resolve to an AssessmentSection");
        }
        return (AssessmentSection) result;
    }

    private TestPlanNode getCurrentItemRefNode() {
        final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
        if (currentItemKey==null) {
            return null;
        }
        return testSessionState.getTestPlan().getNode(currentItemKey);
    }

    private TestPlanNode assertItemRefNode(final TestPlanNodeKey key) {
        final TestPlanNode itemRefNode = testSessionState.getTestPlan().getNode(key);
        if (itemRefNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF) {
            throw new IllegalArgumentException("TestPlanNode with key " + key
                    + " is of type " + itemRefNode.getTestNodeType()
                    + " rather than " + TestNodeType.ASSESSMENT_ITEM_REF);
        }
        return itemRefNode;
    }

    private TestPlanNode expectItemRefNode(final TestPlanNodeKey key) {
        final TestPlanNode testPlanNode = testSessionState.getTestPlan().getNode(key);
        if (testPlanNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF) {
            throw new QtiLogicException("Expected TestPlanNode with key " + key
                    + " to be of type " + TestNodeType.ASSESSMENT_ITEM_REF
                    + " but got " + testPlanNode.getTestNodeType());
        }
        return testPlanNode;
    }

    private ItemSessionState expectItemRefState(final TestPlanNodeKey key) {
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(key);
        if (itemSessionState==null) {
            throw new QtiLogicException("Failed to extract ItemSessionState corresponding to key " + key);
        }
        return itemSessionState;
    }

    private ItemSessionState expectItemRefState(final TestPlanNode itemRefNode) {
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(itemRefNode.getKey());
        if (itemSessionState==null) {
            throw new QtiLogicException("No ItemSessionState corresponding to " + itemRefNode);
        }
        return itemSessionState;
    }

    private AssessmentItemRef expectItemRef(final TestPlanNode itemRefNode) {
        final AbstractPart result = testProcessingMap.resolveAbstractPart(itemRefNode);
        if (result==null || !(result instanceof AssessmentItemRef)) {
            throw new QtiLogicException("Expected " + itemRefNode + " to resolve to an AssessmentItemRef");
        }
        return (AssessmentItemRef) result;
    }

    //-------------------------------------------------------------------
    // Duration management for Control Objects

    private void startControlObjectTimer(final ControlObjectSessionState controlObjectState, final Date timestamp) {
        controlObjectState.setDurationIntervalStartTime(timestamp);
    }

    private void endControlObjectTimer(final ControlObjectSessionState controlObjectState, final Date timestamp) {
        final Date startTime = controlObjectState.getDurationIntervalStartTime();
        if (startTime==null) {
            throw new QtiLogicException("Start time on control Object " + controlObjectState + " was not expected to be null");
        }
        final long durationDelta = timestamp.getTime() - startTime.getTime();
        controlObjectState.setDurationAccumulated(controlObjectState.getDurationAccumulated() + durationDelta);
        controlObjectState.setDurationIntervalStartTime(null);
    }

    private void touchControlObjectTimer(final ControlObjectSessionState controlObjectState, final Date timestamp) {
        endControlObjectTimer(controlObjectState, timestamp);
        startControlObjectTimer(controlObjectState, timestamp);
    }

    private void touchControlObjectTimerIfOpen(final ControlObjectSessionState controlObjectState, final Date timestamp) {
        if (controlObjectState.isOpen()) {
            touchControlObjectTimer(controlObjectState, timestamp);
        }
    }
}
