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

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.NumberValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
public final class TestSessionState implements Serializable {

    private static final long serialVersionUID = 9006603629987329773L;

    private final TestPlan testPlan;
    private final Map<Identifier, Value> outcomeValues;
    private FloatValue durationValue;
    private final Map<TestPlanNodeKey, TestPartSessionState> testPartSessionStates;
    private final Map<TestPlanNodeKey, ItemSessionState> itemSessionStates;

    private boolean entered;
    private boolean ended;
    private boolean exited;
    private TestPlanNodeKey currentTestPartKey;
    private TestPlanNodeKey currentItemKey;

    public TestSessionState(final TestPlan testPlan) {
        Assert.notNull(testPlan, "testPlan");
        this.testPlan = testPlan;
        this.outcomeValues = new HashMap<Identifier, Value>();
        this.testPartSessionStates = new HashMap<TestPlanNodeKey, TestPartSessionState>();
        this.itemSessionStates = new HashMap<TestPlanNodeKey, ItemSessionState>();
        reset();
    }

    //----------------------------------------------------------------

    public TestPlan getTestPlan() {
        return testPlan;
    }

    public Map<TestPlanNodeKey, TestPartSessionState> getTestPartSessionStates() {
        return testPartSessionStates;
    }

    public Map<TestPlanNodeKey, ItemSessionState> getItemSessionStates() {
        return itemSessionStates;
    }

    //----------------------------------------------------------------

    public void reset() {
        this.entered = false;
        this.ended = false;
        this.exited = false;
        this.currentTestPartKey = null;
        this.currentItemKey = null;
        this.outcomeValues.clear();
        this.testPartSessionStates.clear();
        this.itemSessionStates.clear();
        resetBuiltinVariables();
    }

    public void resetBuiltinVariables() {
        setDuration(0);
    }

    //----------------------------------------------------------------

    public boolean isEntered() {
		return entered;
	}

	public void setEntered(final boolean entered) {
		this.entered = entered;
	}


    public boolean isEnded() {
		return ended;
	}

	public void setEnded(final boolean ended) {
		this.ended = ended;
	}


	public boolean isExited() {
        return exited;
    }

	public void setExited(final boolean exited) {
        this.exited = exited;
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

    @ObjectDumperOptions(DumpMode.IGNORE)
    public ItemSessionState getCurrentItemSessionState() {
        return currentItemKey!=null ? itemSessionStates.get(currentItemKey) : null;
    }

    //----------------------------------------------------------------
    // Built-in variable manipulation

    @ObjectDumperOptions(DumpMode.IGNORE)
    public FloatValue getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(final FloatValue value) {
        Assert.notNull(value);
        this.durationValue = value;
    }

    public double getDuration() {
        return getDurationValue().doubleValue();
    }

    public void setDuration(final double duration) {
        setDurationValue(new FloatValue(duration));
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
        if (AssessmentTest.VARIABLE_DURATION_IDENTIFIER.equals(identifier)) {
            result = durationValue;
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
        return testPlan.equals(other.testPlan)
        		&& entered==other.entered
        		&& ended==other.ended
                && exited==other.exited
                && currentTestPartKey.equals(other.currentTestPartKey)
                && currentItemKey.equals(other.currentItemKey)
                && durationValue.equals(other.durationValue)
                && outcomeValues.equals(other.outcomeValues)
                && testPartSessionStates.equals(other.testPartSessionStates)
                && itemSessionStates.equals(other.itemSessionStates);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                testPlan,
                entered,
                ended,
                exited,
                currentTestPartKey,
                currentItemKey,
                durationValue,
                outcomeValues,
                testPartSessionStates,
                itemSessionStates
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(testPlan=" + testPlan
                + ",entered=" + entered
                + ",ended=" + ended
                + ",exited=" + exited
                + ",currentTestPartKey=" + currentTestPartKey
                + ",currentItemKey=" + currentItemKey
                + ",durationValue=" + durationValue
                + ",outcomeValues=" + outcomeValues
                + ",testPartSessionStates=" + testPartSessionStates
                + ",itemSessionStates=" + itemSessionStates
                + ")";
    }
}
