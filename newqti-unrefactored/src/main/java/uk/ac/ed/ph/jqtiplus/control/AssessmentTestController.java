/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.exception.QTIItemFlowException;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.Pair;
import uk.ac.ed.ph.jqtiplus.node.AssessmentItemOrTest;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.general.LookupExpression;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.node.result.OutcomeVariable;
import uk.ac.ed.ph.jqtiplus.node.result.TestResult;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.ControlObject;
import uk.ac.ed.ph.jqtiplus.node.test.Selection;
import uk.ac.ed.ph.jqtiplus.node.test.SubmissionMode;
import uk.ac.ed.ph.jqtiplus.node.test.TestFeedback;
import uk.ac.ed.ph.jqtiplus.node.test.TestFeedbackAccess;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.node.test.TimeLimit;
import uk.ac.ed.ph.jqtiplus.state.AbstractPartState;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemRefState;
import uk.ac.ed.ph.jqtiplus.state.AssessmentTestState;
import uk.ac.ed.ph.jqtiplus.state.ControlObjectState;
import uk.ac.ed.ph.jqtiplus.state.SectionPartStateKey;
import uk.ac.ed.ph.jqtiplus.state.TestPartState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.VariableReferenceIdentifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.value.DurationValue;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xmlutils.AssessmentItemManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.AssessmentTestManager;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
 * FIXME: Need to somewhere keep track of SessionStatus, which doesn't ever seem to have happened in the original JQTI
 * FIXME: This now includes runtime context information, so is no longer stateless. Need to fix that!
 * 
 * @author  David McKain
 * @version $Revision: 2802 $
 */
public final class AssessmentTestController {

    protected static Logger logger = LoggerFactory.getLogger(AssessmentTestController.class);

    private final AssessmentTestManager testManager;
    private final AssessmentTest test;
    private final AssessmentTestState testState;
    private final Timer timer;
    
    /** 
     * Map of controllers for each {@link AssessmentItem}. This is keyed on the underlying {@link AssessmentItemRefState}
     * rather than the corresponding {@link Identifier}, since the {@link AssessmentSection} selection/ordering logic means
     * that it's possible for a referenced {@link AssessmentItemRef} to be used zero or more times in the delivered test. 
     */
    private final Map<AssessmentItemRefState, AssessmentItemRefController> itemRefControllerMap;
    
    public AssessmentTestController(AssessmentTestManager testManager, AssessmentTestState assessmentTestState, Timer timer) {
        ConstraintUtilities.ensureNotNull(testManager, "assessmentTestManager");
        ConstraintUtilities.ensureNotNull(assessmentTestState, "assessmentTestState");
        ConstraintUtilities.ensureNotNull(timer, "timer");
        this.testManager = testManager;
        this.test = testManager.getTest();
        this.testState = assessmentTestState;
        this.itemRefControllerMap = new HashMap<AssessmentItemRefState, AssessmentItemRefController>();
        this.timer = timer;
    }
    
    public AssessmentTestState getTestState() {
        return testState;
    }

    public AssessmentTestManager getTestManager() {
        return testManager;
    }

    public AssessmentTest getTest() {
        return test;
    }
    
    public Timer getTimer() {
        return timer;
    }
    
    //-------------------------------------------------------------------

    public ValidationResult validate() {
        return testManager.validateTest();
    }
    
    //-------------------------------------------------------------------
    // Test variable manipulation

    public Value getEffectiveDefaultValue(Identifier identifier) {
        /* (No default overrides, unlike items) */
        VariableDeclaration declaration = test.getVariableDeclaration(identifier);
        DefaultValue defaultValue = declaration.getDefaultValue();
        Value result = null;
        if (defaultValue!=null) {
            result = defaultValue.evaluate();
        }
        return result;
    }

    public Value computeInitialValue(Identifier identifier) {
        Value defaultValue = getEffectiveDefaultValue(identifier);
        return defaultValue!=null ? defaultValue : NullValue.INSTANCE;
    }

    public Value computeInitialValue(OutcomeDeclaration declaration) {
        return computeInitialValue(declaration.getIdentifier());
    }
    
    private void initValue(OutcomeDeclaration declaration) {
        ConstraintUtilities.ensureNotNull(declaration);
        testState.setOutcomeValue(declaration, computeInitialValue(declaration));
    }

    //-------------------------------------------------------------------
    // Resolving item references after selection/ordering
    
    /** Returns data for all of the items selected for the given {@link AssessmentItemRef}. */
    public Map<AssessmentItemRefState, AssessmentItemRefController> getItemRefControllers(AssessmentItemRef itemRef) {
        Map<AssessmentItemRefState, AssessmentItemRefController> result = new HashMap<AssessmentItemRefState, AssessmentItemRefController>();
        List<AbstractPartState> correspondingStates = testState.getAbstractPartStateMap().get(itemRef.getIdentifier());
        for (AbstractPartState state : correspondingStates) {
            AssessmentItemRefState itemRefState = (AssessmentItemRefState) state;
            AssessmentItemRefController itemRefController = itemRefControllerMap.get(itemRefState);
            result.put(itemRefState, itemRefController);
        }
        return result;
    }
    
    /**
     * Resolves a dotted variable reference of the form ITEM_REF_IDENTIFIER.ITEM_VARIABLE_REF_IDENTIFIER
     * <p>
     * Note that {@link Selection} may result in zero or more matches. This is more general than what
     * is supported by {@link LookupExpression}. 
     */
    public Pair<VariableDeclaration, Map<AssessmentItemRefState, AssessmentItemRefController>> resolveDottedVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
        ConstraintUtilities.ensureNotNull(variableReferenceIdentifier);
        Identifier itemRefIdentifier = variableReferenceIdentifier.getAssessmentItemRefIdentifier();
        Identifier itemVarIdentifier = variableReferenceIdentifier.getAssessmentItemItemVariableIdentifier();
        if (itemRefIdentifier==null || itemVarIdentifier==null) {
            throw new IllegalArgumentException("Reference " + variableReferenceIdentifier + " is not of the form ITEM_REF_IDENTIFIER.ITEM_VAR_IDENTIFIER");
        }
        
        Pair<VariableDeclaration, Map<AssessmentItemRefState, AssessmentItemRefController>> result = null;
        AssessmentItemRef itemRef = getTest().lookupItemRef(itemRefIdentifier);
        if (itemRef!=null) {
            /* Get the resulting VariableDeclaration, applying any VariableMappings as required */
            AssessmentItem item = testManager.resolveItem(itemRef).getItem();
            VariableDeclaration itemVariableDeclation = item.getVariableDeclaration(itemRef.resolveVariableMapping(itemVarIdentifier));
            if (itemVariableDeclation!=null) {
                Map<AssessmentItemRefState, AssessmentItemRefController> itemRefControllers = getItemRefControllers(itemRef);
                result = new Pair<VariableDeclaration, Map<AssessmentItemRefState,AssessmentItemRefController>>(itemVariableDeclation, itemRefControllers);
            }
        }
        return result;
    }
    
    //-------------------------------------------------------------------
    // Workflow methods
    
    private void fireLifecycleEvent(LifecycleEventType eventType) {
        for (JQTIExtensionPackage extensionPackage : testManager.getQTIObjectManager().getJQTIController().getExtensionPackages()) {
            extensionPackage.lifecycleEvent(this, eventType);
        }
    }
    
    public void initialize() {
        logger.info("Initializing assessmentTest {}", test.getIdentifier());
        fireLifecycleEvent(LifecycleEventType.TEST_INITIALISATION_STARTING);
        try {
            /* Clear all existing state */
            itemRefControllerMap.clear();
            
            /* Compute the hierarchy of descendent Objects, taking into account selection/ordering/etc... */
            AssessmentTestInitializer testInitializer = new AssessmentTestInitializer(test, testState);
            testInitializer.run();
            
            /* Set up controllers for child Objects */
            initializeItemRefControllers();
            
            /* Initialize outcomeDeclaration's values */
            for (OutcomeDeclaration outcomeDeclaration : test.getOutcomeDeclarations()) {
                initValue(outcomeDeclaration);
            }
        }
        finally {
            fireLifecycleEvent(LifecycleEventType.TEST_INITIALISATION_FINISHED);
        }
    }
    
    private void initializeItemRefControllers() {
        itemRefControllerMap.clear();
        Map<Identifier, List<AbstractPartState>> controlObjectStateMap = testState.getAbstractPartStateMap();
        for (AssessmentItemRef itemRef : test.searchItemRefs()) {
            List<AbstractPartState> statesForItemRef = controlObjectStateMap.get(itemRef.getIdentifier());
            if (statesForItemRef!=null) {
                for (ControlObjectState<Identifier> state : statesForItemRef) {
                    AssessmentItemRefState itemRefState = (AssessmentItemRefState) state;
                    initializeItemRefController(itemRef, itemRefState);
                }
            }
        }
    }
    
    private void initializeItemRefController(AssessmentItemRef itemRef, AssessmentItemRefState itemRefState) {
        /* Resolve item href */
        AssessmentItemManager itemManager = getTestManager().resolveItem(itemRef);
        
        /* Create controller for this ref */
        AssessmentItemRefController itemRefController = new AssessmentItemRefController(this, itemManager, itemRef, itemRefState);
        itemRefControllerMap.put(itemRefState, itemRefController);
    }

    //-------------------------------------------------------------------
    
    /**
     * FIXME: Re-document this.
     * 
     * This used to be in {@link AssessmentItemRef} and was called setOutcomes() and it
     * stored a copy of a supplied Map of outcome values in the ref itself. But those were
     * never used anywhere, and I can't work out why this was happening!
     *
     * @param outcomes new outcomes to be submitted
     * @throws QTIItemFlowException if this item reference is already finished
     * @see #getOutcomes
     */
    @ToRefactor /* NEEDS A BETTER NAME, AND CLARITY WHEN IT'S ACTUALLY CALLED */
    public void setOutcomes(SectionPartStateKey itemRefKey, Map<Identifier, Value> outcomes) {
        AssessmentItemRefState itemRefState = testState.getSectionPartState(itemRefKey, AssessmentItemRefState.class);
        AssessmentItemRefController itemRefController = itemRefControllerMap.get(itemRefState);
        
        if (itemRefState.isFinished()) {
            throw new QTIItemFlowException(this, "Item reference is already finished.");
        }
        
        itemRefController.submit(timer.getCurrentTime());

        if (outcomes == null) {
            outcomes = new TreeMap<Identifier, Value>();
        }
        
        // WHAT EXACTLY IS HAPPENING NEXT? WHAT OUTCOMES ARE THESE, AND WHERE ARE THEY BEING SET????
        itemRefController.setOutcomes(outcomes);

        this.currentState.outcomes = outcomes;

        currentState.responded = true;

        if (getItem().getAdaptive()) {
            IdentifierValue status = (IdentifierValue) lookupValue(AssessmentItem.VARIABLE_COMPLETION_STATUS);
            if (status != null
                    && status.stringValue().equals(AssessmentItem.VALUE_ITEM_IS_COMPLETED))
                setFinished();
        }
        else {
            int attempts = ((IntegerValue) item
                    .getResponseValue(AssessmentItem.VARIABLE_NUMBER_OF_ATTEMPTS)).intValue();
            boolean isResponseCorrect = (isCorrect() == Boolean.TRUE) ? true : false;
            // TODO: figure out a way to remove R2Q2_IS_RESPONSE_CORRECT using
            // the following?
            // boolean isResponseCorrect = (getItem().countCorrect() > 0 &&
            // getItem().countIncorrect() == 0);

            int maxAttempts = getItemSessionControl().getMaxAttempts();

            if (getParentTestPart().getSubmissionMode() == SubmissionMode.SIMULTANEOUS
                    || isResponseCorrect || (maxAttempts != 0 && attempts >= maxAttempts))
                setFinished();
        }
    }
    
    //-------------------------------------------------------------------
    
    public void processOutcomes() {
        logger.info("Test outcome processing starting on {}", test.getIdentifier());
        TestProcessingContext processingContext = new TestProcessingContextImpl();
        fireLifecycleEvent(LifecycleEventType.TEST_OUTCOME_PROCESSING_STARTING);
        try {
            for (OutcomeDeclaration outcomeDeclaration : test.getOutcomeDeclarations()) {
                initValue(outcomeDeclaration);
            }

            OutcomeProcessing outcomeProcessing = test.getOutcomeProcessing();
            if (outcomeProcessing != null) {
                outcomeProcessing.evaluate(processingContext);
            }
            logger.info("Test outcome processing completed on {}", test.getIdentifier());
        }
        finally {
            fireLifecycleEvent(LifecycleEventType.TEST_OUTCOME_PROCESSING_FINISHED);
        }
    }
    
    /**
     * Gets all viewable testFeedbacks with given access.
     * Tests if feedbacks can be displayed.
     *
     * @param requestedAccess given access
     * @return all testFeedbacks with given access
     */
    /* (This was moved from AssessmentTest itself) */
    public List<TestFeedback> getTestFeedbacks(TestFeedbackAccess requestedAccess) {
        List<TestFeedback> result = new ArrayList<TestFeedback>();

        for (TestFeedback feedback : test.getTestFeedbacks()) {
            if (feedback.isVisible(testState, requestedAccess)) {
                result.add(feedback);
            }
        }
        return result;
    }

    /**
     * Gets all viewable testFeedbacks with given access.
     * Tests if feedbacks can be displayed.
     *
     * @param requestedAccess given access
     * @return all testFeedbacks with given access
     */
    /* (This was moved and refactored from TestPart itself) */
    public List<TestFeedback> getTestFeedbacks(TestPart testPart, TestFeedbackAccess requestedAccess) {
        List<TestFeedback> result = new ArrayList<TestFeedback>();

        for (TestFeedback feedback : testPart.getTestFeedbacks()) {
            if (feedback.isVisible(testState, requestedAccess)) {
                result.add(feedback);
            }
        }
        return result;
    }
    
    //-------------------------------------------------------------------
    // Result generation. These were moved from the AssessmentTest class
    
    /**
     * Returns current result of whole assessment (test and all its items).
     *
     * @return current result of whole assessment (test and all its items)
     */
    public AssessmentResult computeAssessmentResult() {
        AssessmentResult result = new AssessmentResult();

        result.setTestResult(computeTestResult(result));
        
        int sequenceIndex = 1;
        List<AssessmentItemRefState> itemRefStates = testState.lookupItemRefStates();
        for (AssessmentItemRefState itemRefState : itemRefStates) {
            AssessmentItemRefController itemRefController = itemRefControllerMap.get(itemRefState);
            ItemResult itemResult = itemRefController.computeItemResult(result, sequenceIndex++, null); // FIXME: Needs proper SessionStatus instead of null
            result.getItemResults().add(itemResult);
        }
        
        return result;
    }

    /**
     * Returns current result of this test (only test itself, no items).
     *
     * @param parent parent of created result
     * @return current result of this test (only test itself, no items)
     */
    public TestResult computeTestResult(AssessmentResult parent) {
        TestResult result = new TestResult(parent);

        result.setIdentifier(test.getIdentifier());
        result.setDateStamp(new Date());
        
        for (Entry<Identifier, Value> mapEntry : testState.getOutcomeValues().entrySet()) {
            OutcomeDeclaration declaration = test.getOutcomeDeclaration(mapEntry.getKey());
            Value value = mapEntry.getValue();
            OutcomeVariable variable = new OutcomeVariable(result, declaration, value);
            result.getItemVariables().add(variable);
        }
        result.getItemVariables().add(new OutcomeVariable(result, AssessmentTest.VARIABLE_DURATION_IDENTIFIER.toVariableReferenceIdentifier(), new DurationValue(getDuration(testState) / 1000.0)));

        for (TestPartState testPartState : testState.getTestPartStates()) {
            processDuration(result, testPartState);
        }
        return result;
    }

    private void processDuration(TestResult result, AbstractPartState parentState) {
        if (!(parentState instanceof AssessmentItemRefState)) {
            VariableReferenceIdentifier identifier = new VariableReferenceIdentifier(parentState.getTestIdentifier() + "." + AssessmentTest.VARIABLE_DURATION_NAME);
            DurationValue duration = new DurationValue(getDuration(parentState) / 1000.0);

            result.getItemVariables().add(new OutcomeVariable(result, identifier, duration));
            
            for (AbstractPartState childState : parentState.getChildStates()) {
                processDuration(result, childState);
            }
        }
    }
    
    //-------------------------------------------------------------------
    
    public List<AbstractPartState> getControlObjectStates(ControlObject<Identifier> controlObject) {
        return testState.getAbstractPartStateMap().get(controlObject.getIdentifier());
    }
    
    public List<AssessmentItemRefController> findAssessmentItemRefControllers(ControlObject<?> start) {
        List<AssessmentItemRefController> result = new ArrayList<AssessmentItemRefController>();
        for (AssessmentItemRef itemRef : start.searchItemRefs()) {
            List<AbstractPartState> itemRefStates = testState.getAbstractPartStateMap().get(itemRef.getIdentifier());
            for (AbstractPartState itemRefState : itemRefStates) {
                result.add(itemRefControllerMap.get(itemRefState));
            }
        }
        return result;
    }
    
    public List<AssessmentItemRefController> findAssessmentItemRefControllers(ControlObjectState<?> start) {
        List<AssessmentItemRefController> result = new ArrayList<AssessmentItemRefController>();
        for (AssessmentItemRefState itemRefState : start.lookupItemRefStates()) {
            result.add(itemRefControllerMap.get(itemRefState));
        }
        return result;
    }
    
    public ControlObject<?> getControlObject(ControlObjectState<?> state) {
        ControlObject<?> result;
        if (state instanceof AssessmentTestState) {
            result = test;
        }
        else {
            Identifier identifier = (Identifier) state.getTestIdentifier();
            result = test.lookupDescendentOrSelf(identifier);
        }
        return result;
    }
    
    //-------------------------------------------------------------------
    
    /**
     * Gets total number of item references of this control object.
     *
     * @return total number of item references of this control object
     */
    public int calculateTotalCount(ControlObjectState<?> start) {
        return findAssessmentItemRefControllers(start).size();
    }
    
    /**
     * (This is based on the original logic)
     * @param start
     * @return
     */
    public boolean isPresented(ControlObjectState<?> start) {
        for (AssessmentItemRefController itemRefController : findAssessmentItemRefControllers(start)) {
            if (!itemRefController.getItemRefState().isPresented()) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Gets total number of presented item references of this control object.
     *
     * @return total number of presented item references of this control object
     */
    public int calculatePresentedCount(ControlObjectState<?> start) {
        int result = 0;
        for (AssessmentItemRefController itemRefController : findAssessmentItemRefControllers(start)) {
            if (itemRefController.isPresented()) {
                result++;
            }
        }
        return result;
    }

    /**
     * Gets total number of finished item references of this control object.
     *
     * @return total number of finished item references of this control object
     */
    public int calculateFinishedCount(ControlObjectState<?> start) {
        int result = 0;
        for (AssessmentItemRefController itemRefController : findAssessmentItemRefControllers(start)) {
            if (itemRefController.isFinished()) {
                result++;
            }
        }
        return result;
    }
    
    /**
     * Gets total time spent <em>inside</em> this object including navigation time.
     *
     * @return total time spent <em>inside</em> this object including navigation time
     * @see #getResponseTime
     */
    public long calculateTotalTime(ControlObjectState<?> start) {
        long total = 0;
        for (AssessmentItemRefController itemRefController : findAssessmentItemRefControllers(start)) {
            total += itemRefController.getTotalTime();
        }
        return total;
    }

    /* (This was in ControlObject and overridden in AssessmentItemRef. Below is the original logic) */
    public long getDuration(ControlObjectState<?> start) {
        long result;
        if (start instanceof AssessmentItemRefState) {
            AssessmentItemRefController itemRefController = itemRefControllerMap.get(start);
            result = itemRefController.getResponseTime();
        }
        else {
            result = calculateTotalTime(start);
        }
        return result;
    }
    
    /**
     * Returns true if time used by this control object is higher or equal than minimum time limit; false otherwise.
     * <p>
     * Time used by this control object is calculated as:
     * <ul>
     * <li>If this control object is {@code AssessmentItemRef} method {@code getDuration} is used.</li>
     * <li>If this control object if not {@code AssessmentItemRef} method {@code getTotalTime} is used.</li>
     * </ul>
     * <p>
     * This method is not implemented and returns always true.
     *
     * @return true if time used by this control object is higher or equal than minimum time limit; false otherwise
     */
    /* Was in ControlObject before */
    @SuppressWarnings("unused")
    public boolean passMinimumTimeLimit(ControlObjectState<?> start) {
        return true;
    }

    /**
     * Returns true if time used by this control object is lower or equal than maximum time limit; false otherwise.
     * This method checks first this control object and then recursively all its parents
     * and returns true only if every tested object passed check.
     * <p>
     * Time used by this control object is calculated as:
     * <ul>
     * <li>If this control object is {@code AssessmentItemRef} method {@code getDuration} is used.</li>
     * <li>If this control object if not {@code AssessmentItemRef} method {@code getTotalTime} is used.</li>
     * </ul>
     * <p>
     * This method is used for check if item can be shown to the user.
     *
     * @return true if time used by this control object is lower or equal than maximum time limit; false otherwise
     */
    /* Was in ControlObject before */
    public boolean passMaximumTimeLimit(ControlObjectState<?> start) {
        ControlObject<?> controlObject = getControlObject(start);
        TimeLimit timeLimit = controlObject.getTimeLimit();
        long duration = getDuration(start);
        if (timeLimit != null && timeLimit.getMaximumMillis() != null) {
            if (duration >= timeLimit.getMaximumMillis()) {
                return false;
            }
        }
        return (start instanceof AbstractPartState) ? passMaximumTimeLimit(((AbstractPartState) start).getParentState()) : true;
    }

    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(testManager=" + testManager
            + ",testState=" + testState
            + ",itemRefControllerMap=" + itemRefControllerMap
            + ",timer=" + timer
            + ")";
    }

    //---------------------------------------------------

    /**
     * Callback implementation of {@link TestProcessingContext}
     */
    protected class TestProcessingContextImpl implements TestProcessingContext {

        private static final long serialVersionUID = 5390209865608918154L;

        private final Map<String, Value> expressionValues;

        public TestProcessingContextImpl() {
            this.expressionValues = new TreeMap<String, Value>();
        }

        public AssessmentTest getTest() {
            return test;
        }

        public AssessmentItemOrTest getOwner() {
            return test;
        }
        
        public void terminate() {
            testState.setFinished(true);
        }
        
        public AssessmentItemManager resolveItem(AssessmentItemRef assessmentItemRef) {
            return testManager.resolveItem(assessmentItemRef);
        }
        
        public VariableDeclaration resolveVariableReference(VariableReferenceIdentifier variableReferenceIdentifier) {
            return testManager.resolveVariableReference(variableReferenceIdentifier);
        }
        
        public Map<AssessmentItemRefState, AssessmentItemRefController> getItemRefControllers(AssessmentItemRef itemRef) {
            return AssessmentTestController.this.getItemRefControllers(itemRef);
        }
        
        public AssessmentItemRefController getItemRefController(AssessmentItemRefState itemRefState) {
            return AssessmentTestController.this.itemRefControllerMap.get(itemRefState);
        }
        
        public Pair<VariableDeclaration, Map<AssessmentItemRefState, AssessmentItemRefController>> resolveDottedVariableReference(
                VariableReferenceIdentifier variableReferenceIdentifier) {
            return AssessmentTestController.this.resolveDottedVariableReference(variableReferenceIdentifier);
        }
        
        public List<AssessmentItemRefState> lookupItemRefStates() {
            return testState.lookupItemRefStates();
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

        public void setOutcomeValue(OutcomeDeclaration variableDeclaration, Value value) {
            ConstraintUtilities.ensureNotNull(variableDeclaration);
            ConstraintUtilities.ensureNotNull(value);
            testState.setOutcomeValue(variableDeclaration, value);
        }
        
        public void setOutcomeValueFromLookupTable(OutcomeDeclaration outcomeDeclaration, NumberValue value) {
            ConstraintUtilities.ensureNotNull(outcomeDeclaration);
            ConstraintUtilities.ensureNotNull(value);
            testState.setOutcomeValueFromLookupTable(outcomeDeclaration, value);
        }

        public Value lookupVariable(VariableDeclaration variableDeclaration) {
            ConstraintUtilities.ensureNotNull(variableDeclaration);
            return lookupVariable(variableDeclaration.getIdentifier());
        }

        public Value lookupVariable(Identifier identifier) {
            ConstraintUtilities.ensureNotNull(identifier);
            return testState.getOutcomeValue(identifier);
        }

        public Value lookupVariable(Identifier identifier, VariableType... permittedTypes) {
            ConstraintUtilities.ensureNotNull(identifier);
            Value value = null;
            for (VariableType type : permittedTypes) {
                switch (type) {
                    case OUTCOME:
                        value = testState.getOutcomeValue(identifier);
                        break;

                    case TEMPLATE:
                    case RESPONSE:
                        /* (Tests only contain outcome variables!) */
                        break;

                    default:
                        throw new QTILogicException("Unexpected switch case");
                }
            }
            return value;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "@" + hashCode()
                + "(controller=" + AssessmentTestController.this
                + ",expressionValues=" + expressionValues
                + ")";
        }
    }
}
