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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.running;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.LifecycleEventType;
import uk.ac.ed.ph.jqtiplus.exception2.QtiInvalidLookupException;
import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.result.Context;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.node.result.OutcomeVariable;
import uk.ac.ed.ph.jqtiplus.node.result.SessionIdentifier;
import uk.ac.ed.ph.jqtiplus.node.result.TestResult;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.NavigationMode;
import uk.ac.ed.ph.jqtiplus.node.test.PreCondition;
import uk.ac.ed.ph.jqtiplus.node.test.SubmissionMode;
import uk.ac.ed.ph.jqtiplus.node.test.TemplateDefault;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.notification.ListenerNotificationForwarder;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedTestVariableReference;
import uk.ac.ed.ph.jqtiplus.state.EffectiveItemSessionControl;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.types.ComplexReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.utils.TreeWalkNodeHandler;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationController;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: Document this type
 *
 * Usage: one-shot, not thread safe.
 *
 * @author David McKain
 */
public final class TestSessionController extends TestValidationController implements TestProcessingContext {

    private static final Logger logger = LoggerFactory.getLogger(TestSessionController.class);

    private final TestSessionControllerSettings testSessionControllerSettings;
    private final TestProcessingMap testProcessingMap;
    private final TestSessionState testSessionState;

    /** NB: These are created lazily */
    private final Map<TestPlanNodeKey, ItemSessionController> itemSessionControllerMap;

    private final ListenerNotificationForwarder listenerNotificationForwarder;

    private Long randomSeed;
    private Random randomGenerator;

    public TestSessionController(final JqtiExtensionManager jqtiExtensionManager,
            final TestSessionControllerSettings testSessionControllerSettings,
            final TestProcessingMap testProcessingMap,
            final TestSessionState testSessionState) {
        super(jqtiExtensionManager, testProcessingMap!=null ? testProcessingMap.getResolvedAssessmentTest() : null);
        Assert.notNull(testSessionControllerSettings, "testSessionControllerSettings");
        Assert.notNull(testSessionState, "testSessionState");
        this.testSessionControllerSettings = new TestSessionControllerSettings(testSessionControllerSettings);
        this.listenerNotificationForwarder = new ListenerNotificationForwarder(this);
        this.testProcessingMap = testProcessingMap;
        this.testSessionState = testSessionState;
        this.randomSeed = null;
        this.randomGenerator = null;
        this.itemSessionControllerMap = new HashMap<TestPlanNodeKey, ItemSessionController>();
    }

    public TestSessionControllerSettings getTestSessionControllerSettings() {
        return testSessionControllerSettings;
    }

    @Override
    public TestProcessingMap getTestProcessingMap() {
        return testProcessingMap;
    }

    @Override
    public TestSessionState getTestSessionState() {
        return testSessionState;
    }

    @Override
    public boolean isSubjectValid() {
        return testProcessingMap.isValid();
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

    @Override
    public ItemProcessingContext getItemSessionContext(final TestPlanNode itemRefNode) {
        return getItemSessionController(itemRefNode);
    }

    /**
     * Gets the {@link ItemSessionController} for the {@link TestPlanNode} corresponding to
     * an {@link AssessmentItemRef}, lazily creating one if required.
     *
     * @param itemRefNode
     * @return
     */
    private ItemSessionController getItemSessionController(final TestPlanNode itemRefNode) {
        Assert.notNull(itemRefNode);
        if (itemRefNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF) {
            throw new IllegalArgumentException("TestPlanNode must have type " + TestNodeType.ASSESSMENT_ITEM_REF
                    + " rather than " + itemRefNode.getTestNodeType());
        }
        final TestPlanNodeKey key = itemRefNode.getKey();
        ItemSessionController result = itemSessionControllerMap.get(key);
        if (result==null) {
            /* Create controller lazily */
            final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(key);
            result = createItemSessionController(itemRefNode, itemSessionState);
            itemSessionControllerMap.put(key, result);
        }
        return result;
    }

    private ItemSessionController createItemSessionController(final TestPlanNode itemRefNode, final ItemSessionState itemSessionState) {
        final ItemProcessingMap itemProcessingMap = testProcessingMap.resolveItemProcessingMap(itemRefNode);
        final EffectiveItemSessionControl effectiveItemSessionControl = testProcessingMap.resolveEffectiveItemSessionControl(itemRefNode);

        /* Copy relevant bits of ItemSessionControl into ItemSessionControllerSettings */
        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        itemSessionControllerSettings.setTemplateProcessingLimit(testSessionControllerSettings.getTemplateProcessingLimit());
        itemSessionControllerSettings.setMaxAttempts(effectiveItemSessionControl.getMaxAttempts());

        /* Create controller and forward any notifications it generates */
        final ItemSessionController itemSessionController = new ItemSessionController(jqtiExtensionManager,
                itemSessionControllerSettings, itemProcessingMap, itemSessionState);
        itemSessionController.addNotificationListener(listenerNotificationForwarder);

        return itemSessionController;
    }

    //-------------------------------------------------------------------
    // Initialization

    /**
     * Sets all explicitly-defined (valid) variables to NULL, and the
     * <code>duration</code> variable to 0, and calls {@link #initialize()} on each item in the
     * test plan.
     */
    public void initialize() {
        /* Clear ItemSessionController map as we'll have to create new ones for new item states */
        itemSessionControllerMap.clear();

        /* Reset test variables */
        testSessionState.reset();
        for (final Identifier identifier : testProcessingMap.getValidOutcomeDeclarationMap().keySet()) {
            testSessionState.setOutcomeValue(identifier, NullValue.INSTANCE);
        }
        testSessionState.resetBuiltinVariables();

        /* Initialise state & controller for each item instance */
        for (final TestPlanNode testPlanNode : testSessionState.getTestPlan().getTestPlanNodeMap().values()) {
            if (testPlanNode.getTestNodeType()==TestNodeType.ASSESSMENT_ITEM_REF) {
                final TestPlanNodeKey key = testPlanNode.getKey();
                final ItemSessionState itemSessionState = new ItemSessionState();
                testSessionState.getItemSessionStates().put(key, itemSessionState);

                final ItemSessionController itemSessionController = createItemSessionController(testPlanNode, itemSessionState);
                itemSessionController.initialize();
                itemSessionControllerMap.put(key, itemSessionController);
            }
        }
    }

    //-------------------------------------------------------------------
    // WORK IN PROGRESS - TEST CONTROL - NONLINEAR/INDIVIDUAL ONLY WITH SINGLE TEST PART

    /**
     * Temporary start method!
     *
     * Only works for tests with 1 part in N/I mode.
     *
     * Runs template processing on each item, selects the single part, then test is ready.
     */
    public void startTestNI() {
        testSessionState.setCurrentTestPartKey(null);
        testSessionState.setCurrentItemKey(null);
        final TestPlan testPlan = testSessionState.getTestPlan();
        final List<TestPlanNode> testPartNodes = testPlan.getTestPartNodes();
        if (testPartNodes.size()!=1) {
            fireRuntimeWarning(getSubjectTest(), "Support for multiple part tests is coming soon. We'll just run the first part for now.");
        }

        /* Select first (assumed only) part */
        final TestPlanNode testPlanNode = testPartNodes.get(0);
        testSessionState.setCurrentTestPartKey(testPlanNode.getKey());

        /* Check submission/navigation mode */
        final TestPart testPart = (TestPart) testProcessingMap.resolveAbstractPart(testPlanNode);
        if (!(testPart.getNavigationMode()==NavigationMode.NONLINEAR && testPart.getSubmissionMode()==SubmissionMode.INDIVIDUAL)) {
            fireRuntimeWarning(testPart, "This work in progress only supports NONLINEAR/INDIVIDUAL testParts. We're going to ignore your choices today and run the test in N/I mode.");
        }

        /* Perform template processing on each item */
        logger.debug("Performing template processing on each item in this testPart");
        final List<TestPlanNode> itemRefNodes = testPlanNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
        for (final TestPlanNode itemRefNode : itemRefNodes) {
            performTemplateProcessing(itemRefNode);
        }
    }

    private void performTemplateProcessing(final TestPlanNode itemRefNode) {
        Assert.notNull(itemRefNode);
        ensureItemRef(itemRefNode);

        final AssessmentItemRef assessmentItemRef = (AssessmentItemRef) testProcessingMap.resolveAbstractPart(itemRefNode);
        final List<TemplateDefault> templateDefaults = assessmentItemRef.getTemplateDefaults();

        final ItemSessionController itemSessionController = getItemSessionController(itemRefNode);
        itemSessionController.performTemplateProcessing(templateDefaults);
    }

    public boolean maySelectItem(final TestPlanNodeKey itemKey) {
        final TestPlanNode currentTestPart = getCurrentTestPart();
        if (currentTestPart==null) {
            return false;
        }
        if (itemKey!=null) {
            final TestPlanNode itemRefNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(itemKey);
            if (itemRefNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF || !itemRefNode.hasAncestor(currentTestPart)) {
                return false;
            }
            return true;
        }
        else {
            /* Allow deselection FIXME: Review when we support linear? */
            return true;
        }
    }

    public boolean mayReviewItem(final TestPlanNodeKey itemKey) {
        Assert.notNull(itemKey);
        final TestPlanNode currentTestPartNode = ensureTestPartSelected();
        final TestPlanNode itemRefNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(itemKey);
        if (itemRefNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF || !itemRefNode.hasAncestor(currentTestPartNode)) {
            return false;
        }
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(itemRefNode.getKey());
        final EffectiveItemSessionControl effectiveItemSessionControl = testProcessingMap.resolveEffectiveItemSessionControl(itemRefNode);

        return itemSessionState.isClosed() && effectiveItemSessionControl.isAllowReview();
    }

    /**
     * Selects the given item within the part.
     *
     * FIXME: Decide whether to leave this free, or whether to enforce constraints made
     * by navigation mode here.
     *
     * @param itemKey item to select, or null to select no item
     *
     * @throws IllegalStateException if no testPart is selected, or item is not in the current part
     */
    public TestPlanNode selectItem(final TestPlanNodeKey itemKey) {
        final TestPlanNode testPartNode = ensureTestPartSelected();
        if (itemKey!=null) {
            final TestPlanNode itemRefNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(itemKey);
            ensureItemRef(itemRefNode);
            if (!itemRefNode.hasAncestor(testPartNode)) {
                throw new IllegalStateException(itemRefNode + " is not a descendant of " + testPartNode);
            }
            testSessionState.setCurrentItemKey(itemRefNode.getKey());

            /* Mark item as being presented
             * FIXME: Is this the right place, or should it go in engine service layer?
             */
            final ItemSessionController itemSessionController = getItemSessionController(itemRefNode);
            itemSessionController.markPresented();

            return itemRefNode;
        }
        else {
            /* Allow deselection FIXME: Review when we support linear? */
            testSessionState.setCurrentItemKey(null);
            return null;
        }
    }

    /**
     * Returns whether responses may be submitted for the currently selected item.
     *
     * @throws IllegalStateException if no item is selected
     */
    public boolean maySubmitResponsesToCurrentItem() {
        final TestPlanNode currentItemRefNode = ensureItemSelected();
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(currentItemRefNode.getKey());

        return !itemSessionState.isClosed();
    }

    /**
     * Handles response submission to the currently selected item.
     * <p>
     * RP and OP is always run in {@link SubmissionMode#INDIVIDUAL} mode.
     */
    public void handleResponses(final Map<Identifier, ResponseData> responseMap) {
        Assert.notNull(responseMap, "responseMap");
        final TestPlanNode currentItemRefNode = ensureItemSelected();

        /* Bind responses and run response processing */
        final ItemSessionController itemSessionController = getItemSessionController(currentItemRefNode);
        if (itemSessionController.bindResponses(responseMap)) {
            itemSessionController.performResponseProcessing();
        }

        /* Run outcome processing */
        performOutcomeProcessing();
    }

    /**
     * Can we end the current test part?
     *
     * @throws IllegalStateException if no test part is selected
     */
    public boolean mayEndTestPart() {
        final TestPlanNode currentTestPartNode = ensureTestPartSelected();
        final List<TestPlanNode> itemRefNodes = currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
        for (final TestPlanNode itemRefNode : itemRefNodes) {
            final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(itemRefNode.getKey());
            final EffectiveItemSessionControl effectiveItemSessionControl = testProcessingMap.resolveEffectiveItemSessionControl(itemRefNode);
            if (!effectiveItemSessionControl.isAllowSkipping() && !itemSessionState.isResponded()) {
                logger.debug("Item " + itemRefNode.getKey() + " has not been responded and allowSkipping=false, so test part exit will be forbidden");
                return false;
            }
            if (effectiveItemSessionControl.isValidateResponses() && !itemSessionState.isRespondedValidly()) {
                logger.debug("Item " + itemRefNode.getKey() + " has been responded with bad/invalid responses and validateResponses=true, so test part exit will be forbidden");
                return false;
            }
        }
        return true;
    }

//    /**
//     * FIXME: We need to find a way to determine when the testPart has ended but
//     * not been exited. E.g. a new TestPartState or something like that.
//     *
//     * @return
//     */
//    public boolean canExitTest() {
//        ensureTestPartSelected();
//        return testSessionState.getCurrentItemKey()==null;
//    }

    /**
     * FIXME: Fill in the implementation for this. It would depend on the navigation mode, whether
     * the test was still open, and probably whether allowReview is enabled.... BUT allowReview
     * can be done on an item-by-item basis too, so it gets even more complicated there. So might need
     * to make a list of what questions can actually be selected for review. Gah!
     *
     * @return
     */
    public boolean maySelectQuestions() {
        return true;
    }

    /**
     * Ends the test part.
     *
     * (This would end the test in this case)
     *
     * FIXME: Need to check that this is allowed - e.g. is anything getting skipped?
     *
     * FIXME: When we add support for {@link NavigationMode#LINEAR}, we'd trigger response
     * processing at this time.
     *
     * FIXME: This currently doesn't let the candidate review any feedback a {@link TestPart} level, as
     * it clears the selected test part.
     */
    public void endTestPart() {
        final TestPlanNode currentTestPartNode = ensureTestPartSelected();
        if (!mayEndTestPart()) {
            throw new IllegalStateException("Current test part cannot be exited");
        }

        /* Close all items */
        final List<TestPlanNode> itemRefNodes = currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
        for (final TestPlanNode itemRefNode : itemRefNodes) {
            final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(itemRefNode.getKey());
            itemSessionState.setClosed(true);
        }

        /* Deselect item */
        testSessionState.setCurrentItemKey(null);

        /* Mark test as finished.
         *
         * FIXME: This would need generalised to handle multiple testParts
         */
        testSessionState.setFinished(true);
    }

    /**
     * Exits the test.
     *
     * FIXME: This is work in progress! It is only legal to be called at the end of the test
     */
    public void exitTest() {
        testSessionState.setCurrentTestPartKey(null);
        testSessionState.setFinished(true);
    }

    private TestPlanNode ensureItemSelected() {
        final TestPlanNodeKey currentItemKey = testSessionState.getCurrentItemKey();
        if (currentItemKey==null) {
            throw new IllegalStateException("No current item");
        }
        final TestPlanNode itemRefNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(currentItemKey);
        if (itemRefNode==null) {
            throw new QtiLogicException("Unexpected map lookup failure");
        }
        return itemRefNode;
    }

    private TestPlanNode ensureTestPartSelected() {
        final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        if (currentTestPartKey==null) {
            throw new IllegalStateException("No current test part");
        }
        final TestPlanNode testPlanNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(currentTestPartKey);
        if (testPlanNode==null) {
            throw new QtiLogicException("Unexpected map lookup failure");
        }
        return testPlanNode;
    }

    private TestPlanNode getCurrentTestPart() {
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

    private void ensureItemRef(final TestPlanNode itemRefNode) {
        if (itemRefNode==null || itemRefNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF) {
            throw new IllegalArgumentException("Expected " + itemRefNode + " to be an " + TestNodeType.ASSESSMENT_ITEM_REF);
        }
    }

    //-------------------------------------------------------------------
    // WORK IN PROGRESS - TEST CONTROL - LINEAR/INDIVIDUAL ONLY

    public boolean hasMoreTestParts() {
        ensureNotFinished();

        final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        final TestPlan testPlan = testSessionState.getTestPlan();
        final List<TestPlanNode> testPartNodes = testPlan.getTestPartNodes();
        boolean result;
        if (currentTestPartKey==null) {
            /* Haven't started yet */
            result = !testPartNodes.isEmpty();
        }
        else {
            final TestPlanNode currentTestPart = testPlan.getTestPlanNodeMap().get(currentTestPartKey);
            final int currentTestPartIndex = currentTestPart.getSiblingIndex();
            result = currentTestPartIndex < testPartNodes.size()-1;
        }
        return result;
    }

    /**
     * Advances to the next {@link TestPart} in the {@link TestPlan}
     *
     * FIXME: This needs to support {@link PreCondition}!
     */
    public TestPlanNode enterNextTestPart() {
        ensureNotFinished();
        if (!hasMoreTestParts()) {
            return null;
        }
        final TestPlan testPlan = testSessionState.getTestPlan();
        final List<TestPlanNode> testPartNodes = testPlan.getTestPartNodes();
        final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        final TestPlanNode result;
        if (currentTestPartKey==null) {
            result = testPartNodes.get(0);
        }
        else {
            final TestPlanNode currentTestPart = testPlan.getTestPlanNodeMap().get(currentTestPartKey);
            final int currentSiblingIndex = currentTestPart.getSiblingIndex();
            result = testPartNodes.get(currentSiblingIndex + 1);
        }
        testSessionState.setCurrentTestPartKey(result.getKey());
        testSessionState.setCurrentItemKey(null);
        return result;
    }

    public boolean hasMoreItemsInPart() {
        ensureNotFinished();

        final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        if (currentTestPartKey==null) {
            throw new IllegalStateException("Not currently in a testPart");
        }
        final TestPlan testPlan = testSessionState.getTestPlan();
        final TestPlanNode currentTestPart = testPlan.getTestPlanNodeMap().get(currentTestPartKey);
        final List<TestPlanNode> itemsInTestPart = currentTestPart.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
        final TestPlanNodeKey itemKey = testSessionState.getCurrentItemKey();
        boolean result;
        if (itemKey==null) {
            /* Haven't entered any items yet */
            result = !itemsInTestPart.isEmpty();
        }
        else {
            final TestPlanNode currentItem = testPlan.getTestPlanNodeMap().get(itemKey);
            final int itemIndex = itemsInTestPart.indexOf(currentItem);
            result = itemIndex==itemsInTestPart.size()-1;
        }
        return result;
    }

    public TestPlanNode enterNextItem() {
        ensureNotFinished();
        if (!hasMoreItemsInPart()) {
            return null;
        }
        final TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        final TestPlan testPlan = testSessionState.getTestPlan();
        final TestPlanNode currentTestPart = testPlan.getTestPlanNodeMap().get(currentTestPartKey);
        final List<TestPlanNode> itemsInTestPart = currentTestPart.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
        final TestPlanNodeKey itemKey = testSessionState.getCurrentItemKey();
        TestPlanNode result;
        if (itemKey==null) {
            /* Haven't entered any items yet */
            result = itemsInTestPart.get(0);
        }
        else {
            final TestPlanNode currentItem = testPlan.getTestPlanNodeMap().get(itemKey);
            final int currentItemIndex = itemsInTestPart.indexOf(currentItem);
            result = itemsInTestPart.get(currentItemIndex+1);
        }
        testSessionState.setCurrentItemKey(result.getKey());
        return result;
    }

    private void ensureNotFinished() {
        if (testSessionState.isFinished()) {
            throw new IllegalStateException("Test is finished");
        }
    }

    //-------------------------------------------------------------------
    // Outcome processing

    public void performOutcomeProcessing() {
        logger.debug("Outcome processing starting on test {}", getSubject().getSystemId());
        fireLifecycleEvent(LifecycleEventType.TEST_OUTCOME_PROCESSING_STARTING);
        try {
            resetOutcomeVariables();

            final OutcomeProcessing outcomeProcessing = getSubjectTest().getOutcomeProcessing();
            if (outcomeProcessing != null) {
                outcomeProcessing.evaluate(this);
            }
        }
        finally {
            fireLifecycleEvent(LifecycleEventType.TEST_OUTCOME_PROCESSING_FINISHED);
            logger.debug("Outcome processing finished on test {}", getSubject().getSystemId());
        }
    }

    private void resetOutcomeVariables() {
        for (final OutcomeDeclaration outcomeDeclaration : testProcessingMap.getValidOutcomeDeclarationMap().values()) {
            testSessionState.setOutcomeValue(outcomeDeclaration, NullValue.INSTANCE);
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
            if (identifier.equals(AssessmentTest.VARIABLE_DURATION_IDENTIFIER)) {
                result = testProcessingMap.getDurationResponseDeclaration();
            }
            else {
                result = testProcessingMap.getValidOutcomeDeclarationMap().get(identifier);
            }
        }
        else {
            /* Only allows specified types of variables */
            CHECK_LOOP: for (final VariableType type : permittedTypes) {
                switch (type) {
                    case OUTCOME:
                        result = testProcessingMap.getValidOutcomeDeclarationMap().get(identifier);
                        break;

                    case RESPONSE:
                        /* Only response variable is duration */
                        if (AssessmentTest.VARIABLE_DURATION_IDENTIFIER.equals(identifier)) {
                            result = testProcessingMap.getDurationResponseDeclaration();
                        }
                        break;

                    case TEMPLATE:
                        /* No template variables in tests */
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
        if (!testProcessingMap.isValidVariableIdentifier(identifier)) {
            throw new QtiInvalidLookupException(identifier);
        }
        final Value value = getVariableValue(identifier, permittedTypes);
        if (value==null) {
            throw new IllegalStateException("TestSessionState lookup of variable " + identifier + " returned NULL, indicating state is not in sync");
        }
        return value;
    }

    private Value getVariableValue(final Identifier identifier, final VariableType... permittedTypes) {
        Value value = null;
        if (permittedTypes.length==0) {
            /* No types specified, so allow any variable */
            value = testSessionState.getVariableValue(identifier);
        }
        else {
            /* Only allows specified types of variables */
            CHECK_LOOP: for (final VariableType type : permittedTypes) {
                switch (type) {
                    case OUTCOME:
                        value = testSessionState.getOutcomeValue(identifier);
                        break;

                    case RESPONSE:
                        if (AssessmentTest.VARIABLE_DURATION_IDENTIFIER.equals(identifier)) {
                            value = testSessionState.getDurationValue();
                        }
                        break;

                    case TEMPLATE:
                        /* Nothing to do */
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

    public Value evaluateVariableReference(final QtiNode caller, final Identifier referenceIdentifier) {
        return dereferenceVariable(caller, referenceIdentifier, variableEvaluator);
    }

    public Value evaluateVariableReference(final QtiNode caller, final ComplexReferenceIdentifier referenceIdentifier) {
        return dereferenceVariable(caller, referenceIdentifier, variableEvaluator);
    }

    public Value evaluateVariableReference(final QtiNode caller, final ResolvedTestVariableReference resolvedTestVariableReference) {
        return dereferenceVariable(caller, resolvedTestVariableReference, variableEvaluator);
    }

    /**
     * Shareable instance of a {@link DereferencedTestVariableHandler} that simply evaluates
     * the resulting variables.
     */
    public static final DereferencedTestVariableHandler variableEvaluator = new DereferencedTestVariableHandler() {

        @Override
        public Value evaluateInThisTest(final TestProcessingContext testProcessingContext, final Identifier testVariableIdentifier) {
            return testProcessingContext.evaluateVariableValue(testVariableIdentifier);
        }

        @Override
        public Value evaluateInReferencedItem(final ItemProcessingContext itemProcessingContext,
                final AssessmentItemRef assessmentItemRef, final TestPlanNode testPlanNode,
                final Identifier itemVariableIdentifier) {
            return itemProcessingContext.evaluateVariableValue(itemVariableIdentifier);
        }
    };

    @Override
    public Value dereferenceVariable(final QtiNode caller, final Identifier referenceIdentifier,
            final DereferencedTestVariableHandler dereferencedTestVariableHandler) {
        Assert.notNull(referenceIdentifier);
        Assert.notNull(dereferencedTestVariableHandler);

        final List<ResolvedTestVariableReference> resolvedVariableReferences = resolvedAssessmentTest.resolveVariableReference(referenceIdentifier);
        if (resolvedVariableReferences==null) {
            throw new QtiLogicException("Did not expect null result here");
        }
        if (resolvedVariableReferences.size()==0) {
            throw new QtiInvalidLookupException(referenceIdentifier);
        }
        final ResolvedTestVariableReference resultingVariableReference = resolvedVariableReferences.get(0);
        if (resolvedVariableReferences.size()>1) {
            fireRuntimeWarning(caller, "Complex variable reference " + referenceIdentifier
                    + " within test could be derefenced in " + resolvedVariableReferences + " ways. Using the first of these");
        }
        return dereferenceVariable(caller, resultingVariableReference, dereferencedTestVariableHandler);
    }

    @Override
    public Value dereferenceVariable(final QtiNode caller, final ComplexReferenceIdentifier referenceIdentifier,
            final DereferencedTestVariableHandler dereferencedTestVariableHandler) {
        Assert.notNull(referenceIdentifier);
        Assert.notNull(dereferencedTestVariableHandler);

        final List<ResolvedTestVariableReference> resolvedVariableReferences = resolvedAssessmentTest.resolveVariableReference(referenceIdentifier);
        if (resolvedVariableReferences==null) {
            throw new QtiLogicException("Did not expect null result here");
        }
        if (resolvedVariableReferences.size()==0) {
            throw new QtiInvalidLookupException(referenceIdentifier);
        }
        final ResolvedTestVariableReference resultingVariableReference = resolvedVariableReferences.get(0);
        if (resolvedVariableReferences.size()>1) {
            fireRuntimeWarning(caller, "Complex variable reference " + referenceIdentifier
                    + " within test could be derefenced in " + resolvedVariableReferences + " ways. Using the first of these");
        }
        return dereferenceVariable(caller, resultingVariableReference, dereferencedTestVariableHandler);
    }

    public Value dereferenceVariable(final QtiNode caller, final ResolvedTestVariableReference resolvedTestVariableReference,
            final DereferencedTestVariableHandler deferencedTestVariableHandler) {
        Assert.notNull(resolvedTestVariableReference);
        Assert.notNull(deferencedTestVariableHandler);

        final VariableDeclaration targetVariableDeclaration = resolvedTestVariableReference.getVariableDeclaration();
        final Identifier targetVariableIdentifier = targetVariableDeclaration.getIdentifier();
        if (resolvedTestVariableReference.isTestVariableReference()) {
            /* Refers to a variable within this test */
            return deferencedTestVariableHandler.evaluateInThisTest(this, targetVariableIdentifier);
        }
        else {
            /* Refers to a variable within a referenced item */
            final TestPlan testPlan = testSessionState.getTestPlan();
            final AssessmentItemRef assessmentItemRef = resolvedTestVariableReference.getAssessmentItemRef();
            final Integer instanceNumber = resolvedTestVariableReference.getInstanceNumber();
            TestPlanNode testPlanNode = null;
            if (instanceNumber!=null) {
                /* Referring to a particular instance */
                testPlanNode = testPlan.getNodeInstance(assessmentItemRef.getIdentifier(), instanceNumber.intValue());
                if (testPlanNode==null) {
                    fireRuntimeWarning(caller,
                            "Reference to variable " + targetVariableIdentifier
                            + " in instance " + instanceNumber
                            + " of assessmentItemRef " + assessmentItemRef
                            + " yielded no result. Returning NULL");
                    return NullValue.INSTANCE;
                }
                if (testPlanNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF) {
                    fireRuntimeWarning(caller,
                            "Reference to instance " + instanceNumber
                            + " of assessmentItemRef " + assessmentItemRef
                            + " yielded something other than an assessmentItemRef. Returning NULL");
                    return NullValue.INSTANCE;
                }
            }
            else {
                /* No instance number specified, so assume we mean 1 */
                final List<TestPlanNode> testPlanNodes = testPlan.getNodes(assessmentItemRef.getIdentifier());
                if (testPlanNodes==null || testPlanNodes.isEmpty()) {
                    fireRuntimeWarning(caller,
                            "Reference to variable " + targetVariableIdentifier
                            + " in assessmentItemRef " + assessmentItemRef
                            + " yielded no result. Returning NULL");
                    return NullValue.INSTANCE;
                }
                testPlanNode = testPlanNodes.get(0);
                if (testPlanNodes.size()>1) {
                    fireRuntimeWarning(caller,
                            "Reference to assessmentItemRef " + assessmentItemRef
                            + " yielded " + testPlanNodes.size() + " possible instances. Returning the value of the first of these");
                }
                if (testPlanNode.getTestNodeType()!=TestNodeType.ASSESSMENT_ITEM_REF) {
                    fireRuntimeWarning(caller,
                            "Reference to assessmentItemRef " + assessmentItemRef
                            + " yielded something other than an assessmentItemRef. Returning NULL");
                    return NullValue.INSTANCE;
                }
            }
            final ItemSessionController itemSessionController = getItemSessionController(testPlanNode);
            return deferencedTestVariableHandler.evaluateInReferencedItem(itemSessionController,
                    assessmentItemRef, testPlanNode, targetVariableIdentifier);
        }
    }

    //-------------------------------------------------------------------

    @Override
    public List<TestPlanNode> computeItemSubset(final Identifier sectionIdentifier, final List<String> includeCategories, final List<String> excludeCategories) {
        final TestPlan testPlan = testSessionState.getTestPlan();

        final List<TestPlanNode> itemRefNodes = new ArrayList<TestPlanNode>();
        if (sectionIdentifier!=null) {
            /* Search all AssessmentItemRef instances in the TestPlan that are descendants
             * of the AssessmentSection(s) having the given identifier in the ORIGINAL test
             * structure.
             */
            final List<AssessmentItemRef> assessmentItemRefs = findAssessmentItemRefsInSections(sectionIdentifier);
            for (final AssessmentItemRef assessmentItemRef : assessmentItemRefs) {
                itemRefNodes.addAll(testPlan.getNodes(assessmentItemRef.getIdentifier()));
            }
        }
        else {
            /* Take all AssessmentItemRef instances */
            itemRefNodes.addAll(testPlan.searchNodes(TestNodeType.ASSESSMENT_ITEM_REF));
        }

        /* Now apply includes/excludes */
        for (int i=itemRefNodes.size()-1; i>=0; i--) { /* Easiest to move backwards, removing elements as required */
            final TestPlanNode itemRefNode = itemRefNodes.get(i);
            final AssessmentItemRef assessmentItemRef = (AssessmentItemRef) testProcessingMap.resolveAbstractPart(itemRefNode);
            final List<String> categories = assessmentItemRef.getCategories();
            boolean keep;
            if (includeCategories!=null) {
                keep = false;
                for (final String includeCategory : includeCategories) {
                    if (categories.contains(includeCategory)) {
                        keep = true;
                        break;
                    }
                }
            }
            else {
                keep = true;
            }

            if (keep && excludeCategories!=null) {
                for (final String excludeCategory : excludeCategories) {
                    if (categories.contains(excludeCategory)) {
                        keep = false;
                        break;
                    }
                }
            }
            if (!keep) {
                itemRefNodes.remove(i);
            }
        }
        return itemRefNodes;
    }

    /**
     * Helper method to find all {@link AssessmentItemRef}s living below the {@link AssessmentSection}(s)
     * with given identifier.
     * <p>
     * IMPORTANT: This must search the *original* test structure. We need to do this as invisible
     * sections may have been removed by the time the {@link TestPlan} gets computed.
     *
     * @param sectionIdentifier
     * @return
     */
    private final List<AssessmentItemRef> findAssessmentItemRefsInSections(final Identifier sectionIdentifier) {
        Assert.notNull(sectionIdentifier);
        final List<AssessmentItemRef> result = new ArrayList<AssessmentItemRef>();
        QueryUtils.walkTree(new TreeWalkNodeHandler() {
            @Override
            public boolean handleNode(final QtiNode node) {
                if (node instanceof AssessmentSection) {
                    final AssessmentSection assessmentSection = (AssessmentSection) node;
                    if (assessmentSection.getIdentifier().equals(sectionIdentifier)) {
                        /* Found matching section, so search for all assessmentItemRefs below this */
                        result.addAll(QueryUtils.search(AssessmentItemRef.class, assessmentSection));
                    }
                }
                /* Keep searching */
                return true;
            }
        }, getSubjectTest());
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
}
