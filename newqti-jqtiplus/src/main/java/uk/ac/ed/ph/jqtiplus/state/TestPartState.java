/* $Id: TestPartState.java 2782 2011-08-18 16:17:17Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the runtime state of an {@link TestPart}
 * 
 * @author  David McKain
 * @version $Revision: 2782 $
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class TestPartState extends AbstractPartState {

    private static final long serialVersionUID = 5713456627632942141L;
    
    /** READ-ONLY list of runtime {@link SectionPartState}s, taking into account selections and ordering */
    private final List<SectionPartState> runtimeSectionPartStates;
    
    public TestPartState(AssessmentTestState testState, Identifier identifier, List<SectionPartState> runtimeSectionPartStates) {
        super(testState, identifier, runtimeSectionPartStates);
        this.runtimeSectionPartStates = Collections.unmodifiableList(runtimeSectionPartStates);
    }
    
    public List<SectionPartState> getRuntimeSectionPartStates() {
        return runtimeSectionPartStates;
    }
    
    //-------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(identifier=" + testIdentifier
            + ",finished=" + isFinished()
            + ",runtimeSectionPartStates=" + runtimeSectionPartStates
            + ")";
    }
}
