/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.control.QTILogicException;
import uk.ac.ed.ph.jqtiplus.node.test.ControlObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class describing the state of a {@link ControlObject}
 * 
 * @param <E> type of the identifier of the underlying {@link ControlObject}
 * 
 * @author David McKain
 */
public abstract class ControlObjectState<E> implements Serializable {

	private static final long serialVersionUID = -1407010070268750764L;
	
    /** Identifier of the corresponding {@link ControlObject} as referenced in the test */
    protected final E testIdentifier;
	
	protected boolean finished;
	
	protected ControlObjectState(E testIdentifier) {
		this.testIdentifier = testIdentifier;
		this.finished = false;
	}
	
    public E getTestIdentifier() {
		return testIdentifier;
	}
	
	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	   
    //---------------------------------------------------------------
	
    public List<AssessmentItemRefState> lookupItemRefStates() {
        List<AssessmentItemRefState> resultBuilder = new ArrayList<AssessmentItemRefState>();
        lookupItemRefStates(this, resultBuilder);
        return resultBuilder;
    }
    
    private void lookupItemRefStates(ControlObjectState<?> start, List<AssessmentItemRefState> resultBuilder) {
        if (start instanceof AssessmentTestState) {
            AssessmentTestState testState = (AssessmentTestState) start;
            for (TestPartState testPartState : testState.getTestPartStates()) {
                lookupItemRefStates(testPartState, resultBuilder);
            }
        }
        else if (start instanceof TestPartState) {
            TestPartState testPartState = (TestPartState) start;
            for (SectionPartState sectionPartState : testPartState.getRuntimeSectionPartStates()) {
                lookupItemRefStates(sectionPartState, resultBuilder);
            }
        }
        else if (start instanceof AssessmentSectionState) {
            AssessmentSectionState sectionState = (AssessmentSectionState) start;
            for (SectionPartState sectionPartState : sectionState.getRuntimeSectionPartStates()) {
                lookupItemRefStates(sectionPartState, resultBuilder);
            }
        }
        else if (start instanceof AssessmentItemRefState) {
            resultBuilder.add((AssessmentItemRefState) start);
        }
        else {
            throw new QTILogicException("Unexpected logic branch: start=" + start);
        }
    }
}
