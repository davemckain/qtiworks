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

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.test.ControlObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class describing the state of a {@link ControlObject}
 *
 * @param <E> type of the identifier of the underlying {@link ControlObject}
 * @author David McKain
 */
@Deprecated
public abstract class ControlObjectState<E> implements Serializable {

    private static final long serialVersionUID = -1407010070268750764L;

    /** Identifier of the corresponding {@link ControlObject} as referenced in the test */
    protected final E testIdentifier;

    protected boolean finished;

    protected ControlObjectState(final E testIdentifier) {
        this.testIdentifier = testIdentifier;
        this.finished = false;
    }

    public E getTestIdentifier() {
        return testIdentifier;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    //---------------------------------------------------------------

    public List<AssessmentItemRefState> lookupItemRefStates() {
        final List<AssessmentItemRefState> resultBuilder = new ArrayList<AssessmentItemRefState>();
        lookupItemRefStates(this, resultBuilder);
        return resultBuilder;
    }

    private void lookupItemRefStates(final ControlObjectState<?> start, final List<AssessmentItemRefState> resultBuilder) {
        if (start instanceof AssessmentTestState) {
            final AssessmentTestState testState = (AssessmentTestState) start;
            for (final TestPartState testPartState : testState.getTestPartStates()) {
                lookupItemRefStates(testPartState, resultBuilder);
            }
        }
        else if (start instanceof TestPartState) {
            final TestPartState testPartState = (TestPartState) start;
            for (final SectionPartState sectionPartState : testPartState.getRuntimeSectionPartStates()) {
                lookupItemRefStates(sectionPartState, resultBuilder);
            }
        }
        else if (start instanceof AssessmentSectionState) {
            final AssessmentSectionState sectionState = (AssessmentSectionState) start;
            for (final SectionPartState sectionPartState : sectionState.getRuntimeSectionPartStates()) {
                lookupItemRefStates(sectionPartState, resultBuilder);
            }
        }
        else if (start instanceof AssessmentItemRefState) {
            resultBuilder.add((AssessmentItemRefState) start);
        }
        else {
            throw new QtiLogicException("Unexpected logic branch: start=" + start);
        }
    }
}
