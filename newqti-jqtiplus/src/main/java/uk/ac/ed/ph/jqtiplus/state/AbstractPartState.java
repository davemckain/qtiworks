/* $Id: SectionPartState.java 2775 2011-08-15 07:59:08Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the runtime state of an {@link AbstractPart}
 * 
 * @author  David McKain
 * @version $Revision: 2775 $
 */
public abstract class AbstractPartState extends ControlObjectState<Identifier> {

    private static final long serialVersionUID = 3905562260354725912L;
    
    /** State for the owning {@link AssessmentTest} */
    protected final AssessmentTestState testState;
    
    /** Read-only access to parent in state hierarchy. This may differ from the hierarchy in the test due to selection/ordering/visibility */
    protected ControlObjectState<?> parentState;
    
    /** Read-only access to parent in state hierarchy. This may differ from the hierarchy in the test due to selection/ordering/visibility */
    protected final List<? extends SectionPartState> childStates;

    public AbstractPartState(AssessmentTestState testState, Identifier testIdentifier, List<? extends SectionPartState> childStates) {
        super(testIdentifier);
        this.testState = testState;
        this.childStates = Collections.unmodifiableList(childStates);
        this.finished = false;
        for (SectionPartState childState : childStates) {
            childState.setParentState(this);
        }
    }
    
    @ObjectDumperOptions(DumpMode.IGNORE)
    public AssessmentTestState getTestState() {
        return testState;
    }
    
    @ObjectDumperOptions(DumpMode.IGNORE)
    public ControlObjectState<?> getParentState() {
        return parentState;
    }
    
    /**
     * Used only to complete the parent/child hierarchy. JQTI client applications MUST NOT call this.
     * 
     * @param parentState
     */
    protected void setParentState(ControlObjectState<?> parentState) {
        this.parentState = parentState;
    }
    
    public List<? extends SectionPartState> getChildStates() {
        return childStates;
    }

}
