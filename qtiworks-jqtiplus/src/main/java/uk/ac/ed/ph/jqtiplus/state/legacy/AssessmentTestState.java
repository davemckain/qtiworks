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
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.state.legacy;

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.running.legacy.Timer;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
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
     * READ-ONLY Map giving access to the state of each {@link AbstractPart} Keyed on the underlying {@link Identifier}.
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

    public AssessmentTestState(final AssessmentTest test) {
        super(test.getIdentifier());
        this.outcomeValues = new HashMap<Identifier, Value>();
        this.testPartStates = Collections.<TestPartState> emptyList();
        this.abstractPartStateMap = Collections.<Identifier, List<AbstractPartState>> emptyMap();
        this.sectionPartStateMap = Collections.<SectionPartStateKey, SectionPartState> emptyMap();
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

    public <E extends SectionPartState> E getSectionPartState(final SectionPartStateKey key, final Class<E> resultClass) {
        final SectionPartState result = sectionPartStateMap.get(key);
        if (result == null) {
            throw new QtiLogicException("Expected to find SectionPart with key " + key);
        }
        if (!resultClass.isAssignableFrom(result.getClass())) {
            throw new QtiLogicException("Expected state for key " + key + " to be a " + resultClass.getSimpleName() + ", but got " + result.getClass());
        }
        return resultClass.cast(result);
    }

    //---------------------------------------------------------------

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(final Timer timer) {
        this.timer = timer;
    }

    //---------------------------------------------------------------

    public void initialize(final List<TestPartState> testPartStates, final Map<Identifier, List<AbstractPartState>> abstractPartStateMap,
            final Map<SectionPartStateKey, SectionPartState> sectionPartStateMap) {
        this.testPartStates = Collections.unmodifiableList(testPartStates);
        for (final TestPartState testPartState : testPartStates) {
            testPartState.setParentState(this);
        }
        this.abstractPartStateMap = Collections.unmodifiableMap(abstractPartStateMap);
        this.sectionPartStateMap = Collections.unmodifiableMap(sectionPartStateMap);
        outcomeValues.clear();
        finished = false;
    }

    //---------------------------------------------------------------



    //-------------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(identifier=" + testIdentifier
                + ",outcomeValues=" + outcomeValues
                + ",testPartStates=" + testPartStates
                + ",controlObjectStateMap=" + abstractPartStateMap
                + ",finished=" + finished
                + ")";
    }

    public String debugStructure() {
        final StringBuilder result = new StringBuilder();
        result.append("assessmentTest[").append(testIdentifier).append("]\n");
        buildStructure(result, testPartStates, 1);
        return result.toString();
    }

    private void buildStructure(final StringBuilder result, final List<? extends AbstractPartState> states, final int indent) {
        for (final AbstractPartState state : states) {
            for (int i = 0; i < indent; i++) {
                result.append("  ");
            }
            result.append(state.getClass().getSimpleName())
                    .append('[')
                    .append(state instanceof SectionPartState ? ((SectionPartState) state).getSectionPartStateKey() : state.getTestIdentifier())
                    .append("]\n");
            buildStructure(result, state.getChildStates(), indent + 1);
        }
    }

}
