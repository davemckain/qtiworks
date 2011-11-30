/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.control.QTILogicException;
import uk.ac.ed.ph.jqtiplus.control.Timer;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the runtime state of an {@link AssessmentTest}
 * 
 * @author David McKain
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class AssessmentTestState extends ControlObjectState<String> {

    private static final long serialVersionUID = 5176553452095038589L;
    
    /** NB: DOES NOT INCLUDE the "duration" value! */
    private final Map<Identifier, Value> outcomeValues;
    
    /** READ-ONLY access to {@link TestPartState} for each {@link TestPart} */
    private List<TestPartState> testPartStates;
    
    /**
     * READ-ONLY Map giving access to the state of each {@link AbstractPart}
     * Keyed on the underlying {@link Identifier}.
     * <p>
     * The values will be multivalued for {@link SectionPart}s because of selection/ordering logic.
     */
    private Map<Identifier, List<AbstractPartState>> abstractPartStateMap;
    
    /**
     * READ-ONLY Map providing access to each {@link SectionPartState}, using the
     * articifically-generated keys to accommodate selection with replacement.
     */
    private Map<SectionPartStateKey, SectionPartState> sectionPartStateMap;
    
    /* FIXME: timer isn't worked out correctly yet */
    private Timer timer;
    
    public AssessmentTestState(AssessmentTest test) {
        super(test.getIdentifier());
        this.outcomeValues = new HashMap<Identifier, Value>();
        this.testPartStates = Collections.<TestPartState>emptyList();
        this.abstractPartStateMap = Collections.<Identifier, List<AbstractPartState>>emptyMap();
        this.sectionPartStateMap = Collections.<SectionPartStateKey, SectionPartState>emptyMap();
        this.timer = new Timer();
    }
    
    public List<TestPartState> getTestPartStates() {
        return testPartStates;
    }
    
    public Map<Identifier, List<AbstractPartState>> getAbstractPartStateMap() {
        return abstractPartStateMap;
    }
    
    public Map<SectionPartStateKey, SectionPartState> getSectionPartStateMap() {
        return sectionPartStateMap;
    }
    
    public <E extends SectionPartState> E getSectionPartState(SectionPartStateKey key, Class<E> resultClass) {
        SectionPartState result = sectionPartStateMap.get(key);
        if (result==null) {
            throw new QTILogicException("Expected to find SectionPart with key " + key);
        }
        if (!resultClass.isAssignableFrom(result.getClass())) {
            throw new QTILogicException("Expected state for key " + key + " to be a " + resultClass.getSimpleName() + ", but got " + result.getClass());
        }
        return resultClass.cast(result);
    }
    
    //---------------------------------------------------------------
    
    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    
	//---------------------------------------------------------------
    
    public void initialize(List<TestPartState> testPartStates, Map<Identifier, List<AbstractPartState>> abstractPartStateMap, Map<SectionPartStateKey, SectionPartState> sectionPartStateMap) {
        this.testPartStates = Collections.unmodifiableList(testPartStates);
        for (TestPartState testPartState : testPartStates) {
            testPartState.setParentState(this);
        }
        this.abstractPartStateMap = Collections.unmodifiableMap(abstractPartStateMap);
        this.sectionPartStateMap = Collections.unmodifiableMap(sectionPartStateMap);
    	outcomeValues.clear();
    	finished = false;
    }
    
	//---------------------------------------------------------------

    public Value getOutcomeValue(Identifier identifier) {
        ConstraintUtilities.ensureNotNull(identifier);
        return outcomeValues.get(identifier);
    }
    
    public Value getOutcomeValue(OutcomeDeclaration outcomeDeclaration) {
        ConstraintUtilities.ensureNotNull(outcomeDeclaration);
        return getOutcomeValue(outcomeDeclaration.getIdentifier());
    }

    public void setOutcomeValue(Identifier identifier, Value value) {
        ConstraintUtilities.ensureNotNull(identifier);
        ConstraintUtilities.ensureNotNull(value);
        outcomeValues.put(identifier, value);
    }

    public void setOutcomeValue(OutcomeDeclaration outcomeDeclaration, Value value) {
        ConstraintUtilities.ensureNotNull(outcomeDeclaration);
        ConstraintUtilities.ensureNotNull(value);
        setOutcomeValue(outcomeDeclaration.getIdentifier(), value);
    }

    public void setOutcomeValueFromLookupTable(OutcomeDeclaration outcomeDeclaration, NumberValue value) {
        ConstraintUtilities.ensureNotNull(outcomeDeclaration);
        ConstraintUtilities.ensureNotNull(value);
        Value targetValue = outcomeDeclaration.getLookupTable().getTargetValue(value);
        if (targetValue==null) {
            targetValue = NullValue.INSTANCE;
        }
        setOutcomeValue(outcomeDeclaration.getIdentifier(), targetValue);
    }

    public Map<Identifier, Value> getOutcomeValues() {
        return Collections.unmodifiableMap(outcomeValues);
    }
    
	//-------------------------------------------------------------------
    
	@Override
	public String toString() {
	    return getClass().getSimpleName() + "@" + hashCode()
	        + "(identifier=" + testIdentifier
	        + ",outcomeValues=" + outcomeValues
	        + ",testPartStates=" + testPartStates
	        + ",controlObjectStateMap=" + abstractPartStateMap
	        + ",finished=" + finished
	        + ")";
	}
	
	public String debugStructure() {
	    StringBuilder result = new StringBuilder();
	    result.append("assessmentTest[").append(testIdentifier).append("]\n");
	    buildStructure(result, testPartStates, 1);
	    return result.toString();
	}
	
	private void buildStructure(StringBuilder result, List<? extends AbstractPartState> states, int indent) {
	    for (AbstractPartState state : states) {
	        for (int i=0; i<indent; i++) {
	            result.append("  ");
	        }
	        result.append(state.getClass().getSimpleName())
	            .append('[')
	            .append(state instanceof SectionPartState ? ((SectionPartState) state).getSectionPartStateKey() : state.getTestIdentifier())
	            .append("]\n");
	        buildStructure(result, state.getChildStates(), indent+1);
	    }
	}

}
