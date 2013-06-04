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
package uk.ac.ed.ph.jqtiplus.running;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import uk.ac.ed.ph.jqtiplus.state.AbstractPartSessionState;
import uk.ac.ed.ph.jqtiplus.state.ControlObjectSessionState;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.value.Signature;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.util.Date;

import org.junit.Assert;

/**
 * Various common assertions for tests in this package.
 *
 * @author David McKain
 */
public final class RunAssertions {

    public static void assertOpen(final AbstractPartSessionState state, final Date entryTimestamp) {
        assertOpen((ControlObjectSessionState) state, entryTimestamp);
        assertFalse(state.isPreConditionFailed());
        assertFalse(state.isJumpedByBranchRule());
    }

    public static void assertNotYetEntered(final ControlObjectSessionState state) {
        assertFalse(state.isEntered());
        assertFalse(state.isEnded());
        assertFalse(state.isExited());
    }

    public static void assertOpen(final ControlObjectSessionState state, final Date entryTimestamp) {
        assertTrue(state.isEntered());
        assertEquals(entryTimestamp, state.getEntryTime());
        assertFalse(state.isEnded());
        assertFalse(state.isExited());
    }

    public static void assertSuspended(final ItemSessionState state, final Date suspendTimestamp) {
        assertTrue(state.isEntered());
        assertFalse(state.isEnded());
        assertFalse(state.isExited());
        assertTrue(state.isSuspended());
        assertEquals(suspendTimestamp, state.getSuspendTime());
    }

    public static void assertNowEnded(final ControlObjectSessionState state, final Date endTimestamp) {
        assertTrue(state.isEntered());
        assertTrue(state.isEnded());
        assertEquals(endTimestamp, state.getEndTime());
        assertFalse(state.isExited());
    }

    public static void assertEndedButNotEntered(final ControlObjectSessionState state, final Date endTimestamp) {
        assertFalse(state.isEntered());
        assertTrue(state.isEnded());
        assertEquals(endTimestamp, state.getEndTime());
        assertFalse(state.isExited());
    }

    public static void assertNowExited(final ControlObjectSessionState state, final Date exitTimestamp) {
        assertTrue(state.isEntered());
        assertTrue(state.isEnded());
        assertTrue(state.isExited());
        assertEquals(exitTimestamp, state.getExitTime());
    }

    public static void assertExitedButNotEntered(final ControlObjectSessionState state, final Date exitTimestamp) {
        assertFalse(state.isEntered());
        assertTrue(state.isEnded());
        assertTrue(state.isExited());
        assertEquals(exitTimestamp, state.getExitTime());
    }

    public static void assertFailedPreconditionAndNotExited(final AbstractPartSessionState state) {
        assertFalse(state.isEntered());
        assertFalse(state.isEnded());
        assertFalse(state.isExited());
        assertTrue(state.isPreConditionFailed());
    }

    public static void assertFailedPreconditionAndExited(final AbstractPartSessionState state, final Date exitTimestamp) {
        assertFalse(state.isEntered());
        assertFalse(state.isEnded());
        assertTrue(state.isPreConditionFailed());
        assertTrue(state.isExited());
        assertEquals(exitTimestamp, state.getExitTime());
    }

    public static void assertJumpedByBranchRuleAndNotExited(final AbstractPartSessionState state) {
        assertFalse(state.isEntered());
        assertFalse(state.isEnded());
        assertFalse(state.isExited());
        assertTrue(state.isJumpedByBranchRule());
    }

    public static void assertJumpedByBranchRuleAndExited(final AbstractPartSessionState state, final Date exitTimestamp) {
        assertFalse(state.isEntered());
        assertFalse(state.isEnded());
        assertTrue(state.isJumpedByBranchRule());
        assertTrue(state.isExited());
        assertEquals(exitTimestamp, state.getExitTime());
    }

    //----------------------------------------
    // These are fairly generic Value assertions, so could possible move elsewhere?

    public static void assertValueNotNull(final Value value) {
        Assert.assertFalse(value.isNull());
    }

    public static void assertValueEqualsDouble(final Double expected, final Value value) {
        if (expected!=null) {
            Assert.assertTrue(value.hasSignature(Signature.SINGLE_FLOAT));
            Assert.assertEquals(new FloatValue(expected.doubleValue()), value);
        }
        else {
            Assert.assertTrue(value.isNull());
        }
    }

    public static void assertValueEqualsString(final String expected, final Value value) {
        if (expected!=null) {
            Assert.assertTrue(value.hasSignature(Signature.SINGLE_STRING));
            Assert.assertEquals(new StringValue(expected), value);
        }
        else {
            Assert.assertTrue(value.isNull());
        }
    }

    public static void assertValueEqualsIdentifier(final String expected, final Value value) {
        if (expected!=null) {
            Assert.assertTrue(value.hasSignature(Signature.SINGLE_IDENTIFIER));
            Assert.assertEquals(new IdentifierValue(expected), value);
        }
        else {
            Assert.assertTrue(value.isNull());
        }
    }
}
