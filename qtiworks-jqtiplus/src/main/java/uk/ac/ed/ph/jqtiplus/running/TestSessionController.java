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
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedTestVariableReference;
import uk.ac.ed.ph.jqtiplus.state.EffectiveItemSessionControl;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeInstanceKey;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private final TestProcessingMap testProcessingMap;
    private final TestSessionState testSessionState;
    private final Map<TestPlanNode, ItemSessionController> itemSessionControllerMap;

    private Long randomSeed;
    private Random randomGenerator;

    public TestSessionController(final JqtiExtensionManager jqtiExtensionManager,
            final TestProcessingMap testProcessingMap,
            final TestSessionState testSessionState) {
        super(jqtiExtensionManager, testProcessingMap!=null ? testProcessingMap.getResolvedAssessmentTest() : null);
        this.testProcessingMap = testProcessingMap;
        this.testSessionState = testSessionState;
        this.randomSeed = null;
        this.randomGenerator = null;
        this.itemSessionControllerMap = new HashMap<TestPlanNode, ItemSessionController>();
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
        final ItemSessionController result = itemSessionControllerMap.get(itemRefNode);
        if (result==null) {
            throw new IllegalStateException("Expected ItemSessionController to be not null");
        }
        return result;
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

        /* Initialise state in each item instance */
        for (final TestPlanNode testPlanNode : testSessionState.getTestPlan().getTestPlanNodeMap().values()) {
            if (testPlanNode.getTestNodeType()==TestNodeType.ASSESSMENT_ITEM_REF) {
                final TestPlanNodeInstanceKey instanceKey = testPlanNode.getTestPlanNodeInstanceKey();
                final ItemSessionState itemSessionState = new ItemSessionState();
                testSessionState.getItemSessionStates().put(instanceKey, itemSessionState);

                final ItemProcessingMap itemProcessingMap = testProcessingMap.resolveItemProcessingMap(testPlanNode);
                final ItemSessionController itemSessionController = new ItemSessionController(jqtiExtensionManager, itemProcessingMap, itemSessionState);
                itemSessionControllerMap.put(testPlanNode, itemSessionController);
                itemSessionController.initialize();
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
            fireRuntimeWarning(getSubjectTest(), "Support for single part tests is coming soon");
            testSessionState.setFinished(true);
            return;
        }
        /* Check submission/navigation mode */
        final TestPlanNode testPlanNode = testPartNodes.get(0);
        final TestPart testPart = (TestPart) testProcessingMap.resolveAbstractPart(testPlanNode);
        if (!(testPart.getNavigationMode()==NavigationMode.NONLINEAR && testPart.getSubmissionMode()==SubmissionMode.INDIVIDUAL)) {
            fireRuntimeWarning(testPart, "This work in progress only supports NONLINEAR/INDIVIDUAL testParts");
            testSessionState.setFinished(true);
            return;
        }
        /* Select part */
        testSessionState.setCurrentTestPartKey(testPlanNode.getTestPlanNodeInstanceKey());

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

    /**
     * Selects the given item within the part.
     */
    public TestPlanNode selectItem(final TestPlanNodeInstanceKey itemKey) {
        final TestPlanNode testPartNode = ensureTestPartSelected();
        final TestPlanNode itemRefNode = testSessionState.getTestPlan().getTestPlanNodeMap().get(itemKey);
        ensureItemRef(itemRefNode);
        if (!itemRefNode.hasAncestor(testPartNode)) {
            throw new IllegalStateException(itemRefNode + " is not a descendant of " + testPartNode);
        }
        testSessionState.setCurrentItemKey(itemRefNode.getTestPlanNodeInstanceKey());
        return itemRefNode;
    }

    /**
     * Handles response submission to the currently selected item
     */
    public void handleResponses(final Map<Identifier, ResponseData> responseMap) {
        Assert.notNull(responseMap, "responseMap");
        final TestPlanNode itemRefNode = ensureItemSelected();
        final AssessmentItemRef itemRef = (AssessmentItemRef) testProcessingMap.resolveAbstractPart(itemRefNode);
        final EffectiveItemSessionControl effectiveItemSessionControl = testProcessingMap.getEffectiveItemSessionControlMap().get(itemRef);

        /* Bind responses and run response processing */
        final ItemSessionController itemSessionController = getItemSessionController(itemRefNode);
        if (itemSessionController.bindResponses(responseMap)) {
            itemSessionController.performResponseProcessing();
        }
        itemSessionController.checkAttemptAllowed(effectiveItemSessionControl.getMaxAttempts());
    }

    /**
     * Can we exit the test part?
     */
    public boolean canExitTestPart() {
        final TestPlanNode currentTestPartNode = ensureTestPartSelected();
        final List<TestPlanNode> itemRefNodes = currentTestPartNode.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
        for (final TestPlanNode itemRefNode : itemRefNodes) {
            final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(itemRefNode.getTestPlanNodeInstanceKey());
            final AssessmentItemRef itemRef = (AssessmentItemRef) testProcessingMap.resolveAbstractPart(itemRefNode);
            final EffectiveItemSessionControl effectiveItemSessionControl = testProcessingMap.getEffectiveItemSessionControlMap().get(itemRef);
            if (!effectiveItemSessionControl.isAllowSkipping() && !itemSessionState.isResponded()) {
                logger.debug("Item " + itemRefNode.getTestPlanNodeInstanceKey() + " has not been responded and allowSkipping=false, so test part exit will be forbidden");
                return false;
            }
            if (effectiveItemSessionControl.isValidateResponses() && !itemSessionState.isRespondedValidly()) {
                logger.debug("Item " + itemRefNode.getTestPlanNodeInstanceKey() + " has been responded with bad/invalid responses and validateResponses=true, so test part exit will be forbidden");
                return false;
            }
        }
        return true;
    }

    /**
     * Exits the test part.
     *
     * (This would end the test in this case)
     */
    public void exitTestPart() {
        ensureTestPartSelected();
        if (!canExitTestPart()) {
            throw new IllegalStateException("Current test part cannot be exited");
        }
        testSessionState.setCurrentTestPartKey(null);
        testSessionState.setCurrentItemKey(null);
        testSessionState.setFinished(true);
    }

    private TestPlanNode ensureItemSelected() {
        final TestPlanNodeInstanceKey currentItemKey = testSessionState.getCurrentItemKey();
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
        final TestPlanNodeInstanceKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        if (currentTestPartKey==null) {
            throw new IllegalStateException("No current test part");
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

        final TestPlanNodeInstanceKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
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
            result = currentTestPartIndex==testPartNodes.size()-1;
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
        final TestPlanNodeInstanceKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        final TestPlanNode result;
        if (currentTestPartKey==null) {
            result = testPartNodes.get(0);
        }
        else {
            final TestPlanNode currentTestPart = testPlan.getTestPlanNodeMap().get(currentTestPartKey);
            final int currentSiblingIndex = currentTestPart.getSiblingIndex();
            result = testPartNodes.get(currentSiblingIndex + 1);
        }
        testSessionState.setCurrentTestPartKey(result.getTestPlanNodeInstanceKey());
        testSessionState.setCurrentItemKey(null);
        return result;
    }

    public boolean hasMoreItemsInPart() {
        ensureNotFinished();

        final TestPlanNodeInstanceKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        if (currentTestPartKey==null) {
            throw new IllegalStateException("Not currently in a testPart");
        }
        final TestPlan testPlan = testSessionState.getTestPlan();
        final TestPlanNode currentTestPart = testPlan.getTestPlanNodeMap().get(currentTestPartKey);
        final List<TestPlanNode> itemsInTestPart = currentTestPart.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
        final TestPlanNodeInstanceKey itemKey = testSessionState.getCurrentItemKey();
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
        final TestPlanNodeInstanceKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
        final TestPlan testPlan = testSessionState.getTestPlan();
        final TestPlanNode currentTestPart = testPlan.getTestPlanNodeMap().get(currentTestPartKey);
        final List<TestPlanNode> itemsInTestPart = currentTestPart.searchDescendants(TestNodeType.ASSESSMENT_ITEM_REF);
        final TestPlanNodeInstanceKey itemKey = testSessionState.getCurrentItemKey();
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
        testSessionState.setCurrentItemKey(result.getTestPlanNodeInstanceKey());
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
        logger.info("Test outcome processing starting on {}", getSubjectTest().getSystemId());
        fireLifecycleEvent(LifecycleEventType.TEST_OUTCOME_PROCESSING_STARTING);
        try {
            resetOutcomeVariables();

            final OutcomeProcessing outcomeProcessing = getSubjectTest().getOutcomeProcessing();
            if (outcomeProcessing != null) {
                outcomeProcessing.evaluate(this);
            }
            logger.info("Test outcome processing completed on {}", getSubjectTest().getSystemId());
        }
        finally {
            fireLifecycleEvent(LifecycleEventType.TEST_OUTCOME_PROCESSING_FINISHED);
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
}
