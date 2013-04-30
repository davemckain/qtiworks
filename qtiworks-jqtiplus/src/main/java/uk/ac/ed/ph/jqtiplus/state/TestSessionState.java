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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Encapsulates the current state of a candidate's test session.
 * <p>
 * An instance of this class is mutable, but you must let JQTI+ perform all
 * mutation operations for you via a {@link TestSessionController}.
 * <p>
 * An instance of this class is NOT safe for use by multiple threads.
 *
 * @see TestSessionController
 *
 * @author David McKain
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class TestSessionState extends ControlObjectSessionState implements Serializable {

    private static final long serialVersionUID = 9006603629987329773L;

    private final TestPlan testPlan;
    private final Map<TestPlanNodeKey, TestPartSessionState> testPartSessionStates;
    private final Map<TestPlanNodeKey, AssessmentSectionSessionState> assessmentSectionSessionStates;
    private final Map<TestPlanNodeKey, ItemSessionState> itemSessionStates;
    private final Map<Identifier, Value> outcomeValues;

    private boolean initialized;
    private TestPlanNodeKey currentTestPartKey;
    private TestPlanNodeKey currentItemKey;

    public TestSessionState(final TestPlan testPlan) {
        Assert.notNull(testPlan, "testPlan");
        this.testPlan = testPlan;
        this.testPartSessionStates = new LinkedHashMap<TestPlanNodeKey, TestPartSessionState>();
        this.assessmentSectionSessionStates = new LinkedHashMap<TestPlanNodeKey, AssessmentSectionSessionState>();
        this.itemSessionStates = new LinkedHashMap<TestPlanNodeKey, ItemSessionState>();
        this.outcomeValues = new LinkedHashMap<Identifier, Value>();
        reset();
    }

    //----------------------------------------------------------------

    public TestPlan getTestPlan() {
        return testPlan;
    }

    public Map<TestPlanNodeKey, TestPartSessionState> getTestPartSessionStates() {
        return testPartSessionStates;
    }

    public Map<TestPlanNodeKey, AssessmentSectionSessionState> getAssessmentSectionSessionStates() {
        return assessmentSectionSessionStates;
    }

    public Map<TestPlanNodeKey, ItemSessionState> getItemSessionStates() {
        return itemSessionStates;
    }

    //----------------------------------------------------------------

    @Override
    public void reset() {
        super.reset();
        this.testPartSessionStates.clear();
        this.assessmentSectionSessionStates.clear();
        this.itemSessionStates.clear();
        this.outcomeValues.clear();
        this.initialized = false;
        this.currentTestPartKey = null;
        this.currentItemKey = null;

    }

    //----------------------------------------------------------------

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }


    public TestPlanNodeKey getCurrentTestPartKey() {
        return currentTestPartKey;
    }

    public void setCurrentTestPartKey(final TestPlanNodeKey currentTestPartKey) {
        this.currentTestPartKey = currentTestPartKey;
    }


    public TestPlanNodeKey getCurrentItemKey() {
        return currentItemKey;
    }

    public void setCurrentItemKey(final TestPlanNodeKey currentItemKey) {
        this.currentItemKey = currentItemKey;
    }

    /**
     * Convenience method to obtain the {@link TestPartSessionState} for the currently-selected
     * {@link TestPart}, or null if no {@link TestPart} is selected.
     */
    @ObjectDumperOptions(DumpMode.IGNORE)
    public TestPartSessionState getCurrentTestPartSessionState() {
        return currentTestPartKey!=null ? testPartSessionStates.get(currentTestPartKey) : null;
    }

    /**
     * Convenience method to obtain the {@link ItemSessionState} for the currently-selected
     * item, or null if no item is selected.
     */
    @ObjectDumperOptions(DumpMode.IGNORE)
    public ItemSessionState getCurrentItemSessionState() {
        return currentItemKey!=null ? itemSessionStates.get(currentItemKey) : null;
    }

    //----------------------------------------------------------------
    // Duration calculation

    /**
     * Returns the accumulated duration in seconds. Note that this is the
     * accumulated duration as recorded during the last "touch" of the state.
     */
    public double computeDuration() {
        return getDurationAccumulated() / 1000.0;
    }

    /**
     * Returns the accumulated duration as a {@link FloatValue}. Note that this is the
     * accumulated duration as recorded during the last "touch" of the state.
     */
    @ObjectDumperOptions(DumpMode.IGNORE)
    public FloatValue computeDurationValue() {
        return new FloatValue(computeDuration());
    }

    //----------------------------------------------------------------
    // Outcome variables

    public Value getOutcomeValue(final Identifier identifier) {
        Assert.notNull(identifier);
        return outcomeValues.get(identifier);
    }

    public Value getOutcomeValue(final OutcomeDeclaration outcomeDeclaration) {
        Assert.notNull(outcomeDeclaration);
        return getOutcomeValue(outcomeDeclaration.getIdentifier());
    }

    public void setOutcomeValue(final Identifier identifier, final Value value) {
        Assert.notNull(identifier);
        Assert.notNull(value);
        outcomeValues.put(identifier, value);
    }

    public void setOutcomeValue(final OutcomeDeclaration outcomeDeclaration, final Value value) {
        Assert.notNull(outcomeDeclaration);
        Assert.notNull(value);
        setOutcomeValue(outcomeDeclaration.getIdentifier(), value);
    }

    public void setOutcomeValueFromLookupTable(final OutcomeDeclaration outcomeDeclaration, final NumberValue value) {
        Assert.notNull(outcomeDeclaration);
        Assert.notNull(value);
        Value targetValue = outcomeDeclaration.getLookupTable().getTargetValue(value.doubleValue());
        if (targetValue == null) {
            targetValue = NullValue.INSTANCE;
        }
        setOutcomeValue(outcomeDeclaration.getIdentifier(), targetValue);
    }

    public Map<Identifier, Value> getOutcomeValues() {
        return Collections.unmodifiableMap(outcomeValues);
    }

    //----------------------------------------------------------------

    public Value getVariableValue(final Identifier identifier) {
        Assert.notNull(identifier);
        Value result;
        if (QtiConstants.VARIABLE_DURATION_IDENTIFIER.equals(identifier)) {
            result = computeDurationValue();
        }
        else {
            result = getOutcomeValue(identifier);
        }
        return result;
    }

    //----------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof TestSessionState)) {
            return false;
        }

        final TestSessionState other = (TestSessionState) obj;
        return super.equals(obj)
                && ObjectUtilities.nullSafeEquals(currentTestPartKey, other.currentTestPartKey)
                && ObjectUtilities.nullSafeEquals(currentItemKey, other.currentItemKey)
                && testPartSessionStates.equals(other.testPartSessionStates)
                && assessmentSectionSessionStates.equals(other.assessmentSectionSessionStates)
                && itemSessionStates.equals(other.itemSessionStates)
                && outcomeValues.equals(other.outcomeValues)
                && testPlan.equals(other.testPlan)
                ;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                super.hashCode(),
                testPlan,
                testPartSessionStates,
                assessmentSectionSessionStates,
                itemSessionStates,
                currentTestPartKey,
                currentItemKey,
                outcomeValues
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(testPlan=" + testPlan
                + ",entryTime=" + getEntryTime()
                + ",endTime=" + getEndTime()
                + ",exitTime=" + getExitTime()
                + ",durationAccumulated=" + getDurationAccumulated()
                + ",durationIntervalStartTime=" + getDurationIntervalStartTime()
                + ",currentTestPartKey=" + currentTestPartKey
                + ",currentItemKey=" + currentItemKey
                + ",outcomeValues=" + outcomeValues
                + ",testPartSessionStates=" + testPartSessionStates
                + ",assessmentSectionSessionStates=" + assessmentSectionSessionStates
                + ",itemSessionStates=" + itemSessionStates
                + ")";
    }
}
