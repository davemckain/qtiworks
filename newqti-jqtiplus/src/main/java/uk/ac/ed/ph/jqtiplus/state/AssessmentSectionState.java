/* $Id: AssessmentSectionState.java 2782 2011-08-18 16:17:17Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.List;

/**
 * Encapsulates the runtime state of an {@link AssessmentSection}
 * 
 * @author  David McKain
 * @version $Revision: 2782 $
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class AssessmentSectionState extends SectionPartState {
    
    private static final long serialVersionUID = -1465101870364998266L;
    
    public AssessmentSectionState(AssessmentTestState testState, Identifier identifier, int siblingIndex, List<? extends SectionPartState> runtimeSectionPartStates) {
        super(testState, identifier, siblingIndex, runtimeSectionPartStates);
    }
    
    public List<? extends SectionPartState> getRuntimeSectionPartStates() {
        return childStates;
    }
    
    //-------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(testIdentifier=" + testIdentifier
            + ",siblingIndex=" + siblingIndex
            + ",finished=" + isFinished()
            + ",runtimeSectionPartStates=" + getRuntimeSectionPartStates()
            + ")";
    }
    
}
