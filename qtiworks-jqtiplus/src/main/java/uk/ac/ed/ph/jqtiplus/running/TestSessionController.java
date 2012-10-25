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
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedTestVariableReference;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode.TestNodeType;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeInstanceKey;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
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
        ItemSessionController result = itemSessionControllerMap.get(itemRefNode);
        if (result==null) {
            final ItemProcessingMap itemProcessingMap = testProcessingMap.resolveItemProcessingMap(itemRefNode);
            final ItemSessionState itemSessionState = getItemSessionState(itemRefNode);
            result = new ItemSessionController(jqtiExtensionManager, itemProcessingMap, itemSessionState);
            itemSessionControllerMap.put(itemRefNode, result);
        }
        return result;
    }

    private ItemSessionState getItemSessionState(final TestPlanNode itemRefNode) {
        return testSessionState.getItemSessionStates().get(itemRefNode.getTestPlanNodeInstanceKey());
    }

    //-------------------------------------------------------------------
    // Initialization

    /**
     * Sets all explicitly-defined (valid) variables to NULL, and the
     * <code>duration</code> variable to 0, and calls {@link #initialize()} on each item in the
     * test plan.
     */
    public void initialize() {
        /* Reset test variables */
        testSessionState.reset();
        for (final Identifier identifier : testProcessingMap.getValidOutcomeDeclarationMap().keySet()) {
            testSessionState.setOutcomeValue(identifier, NullValue.INSTANCE);
        }
        testSessionState.resetBuiltinVariables();

        /* Reset variables in each item instance */
        for (final TestPlanNode testPlanNode : testSessionState.getTestPlan().getTestPlanNodeMap().values()) {
            if (testPlanNode.getTestNodeType()==TestNodeType.ASSESSMENT_ITEM_REF) {
                final TestPlanNodeInstanceKey instanceKey = testPlanNode.getTestPlanNodeInstanceKey();
                ItemSessionState itemSessionState = getItemSessionState(testPlanNode);
                if (itemSessionState==null) {
                    itemSessionState = new ItemSessionState();
                    testSessionState.getItemSessionStates().put(instanceKey, itemSessionState);
                }
                final ItemSessionController itemSessionController = getItemSessionController(testPlanNode);
                itemSessionController.initialize();
            }
        }
    }

    public void performOutcomeProcessing() {
        logger.info("Test outcome processing starting on {}", getResolvedAssessmentTest().getRootNodeLookup().getSystemId());
        fireLifecycleEvent(LifecycleEventType.TEST_OUTCOME_PROCESSING_STARTING);
        try {
            resetOutcomeVariables();

            final OutcomeProcessing outcomeProcessing = getSubjectTest().getOutcomeProcessing();
            if (outcomeProcessing != null) {
                outcomeProcessing.evaluate(this);
            }
            logger.info("Test outcome processing completed on {}", getResolvedAssessmentTest().getRootNodeLookup().getSystemId());
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
