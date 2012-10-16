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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates the current state of a candidate's test session.
 * <p>
 * An instance of this class is mutable, but you must let JQTI+ perform all
 * mutation operations for you.
 * <p>
 * An instance of this class is NOT safe for use by multiple threads.
 *
 * @author David McKain
 */
public final class TestSessionState implements Serializable {

    private static final long serialVersionUID = 9006603629987329773L;

    private final TestPlan testPlan;
    private final Map<Identifier, Value> outcomeValues;
    private final Map<TestPlanNodeInstanceKey, TestItemState> testItemStates;

    public TestSessionState(final TestPlan testPlan) {
        Assert.notNull(testPlan, "testPlan");
        this.testPlan = testPlan;
        this.outcomeValues = new HashMap<Identifier, Value>();
        this.testItemStates = new HashMap<TestPlanNodeInstanceKey, TestItemState>();
    }

    public TestPlan getTestPlan() {
        return testPlan;
    }

    public Map<Identifier, Value> getOutcomeValues() {
        return outcomeValues;
    }

    public Map<TestPlanNodeInstanceKey, TestItemState> getTestItemStates() {
        return testItemStates;
    }

    //----------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof TestSessionState)) {
            return false;
        }

        final TestSessionState other = (TestSessionState) obj;
        return testPlan.equals(other.testPlan)
                && outcomeValues.equals(other.outcomeValues)
                && testItemStates.equals(other.testItemStates);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                testPlan,
                outcomeValues,
                testItemStates
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(testPlan=" + testPlan
                + ",outcomeValues=" + outcomeValues
                + ",testItemStates=" + testItemStates
                + ")";
    }
}
