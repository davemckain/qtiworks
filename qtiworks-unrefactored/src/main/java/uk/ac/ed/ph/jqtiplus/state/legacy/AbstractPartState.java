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
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.state.legacy;

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
 * @author David McKain
 */
@Deprecated
public abstract class AbstractPartState extends ControlObjectState<Identifier> {

    private static final long serialVersionUID = 3905562260354725912L;

    /** State for the owning {@link AssessmentTest} */
    protected final AssessmentTestState testState;

    /** Read-only access to parent in state hierarchy. This may differ from the hierarchy in the test due to selection/ordering/visibility */
    protected ControlObjectState<?> parentState;

    /** Read-only access to parent in state hierarchy. This may differ from the hierarchy in the test due to selection/ordering/visibility */
    protected final List<? extends SectionPartState> childStates;

    public AbstractPartState(final AssessmentTestState testState, final Identifier testIdentifier, final List<? extends SectionPartState> childStates) {
        super(testIdentifier);
        this.testState = testState;
        this.childStates = Collections.unmodifiableList(childStates);
        this.finished = false;
        for (final SectionPartState childState : childStates) {
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
    protected void setParentState(final ControlObjectState<?> parentState) {
        this.parentState = parentState;
    }

    public List<? extends SectionPartState> getChildStates() {
        return childStates;
    }

}
