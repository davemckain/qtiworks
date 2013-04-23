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

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.state.TestPartSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Extends {@link TestTestBase} for tests of an {@link AssessmentTest}
 * having a single {@link TestPart}.
 * <p>
 * NB: The identifier of the testPart for these tests should be 'p'.
 * NB: Subclasses should start their time deltas at 2000L
 *
 * @author David McKain
 */
public abstract class SinglePartTestBase extends TestTestBase {

    protected TestPartSessionState testPartSessionState;

    protected long testPartEntryDelta;
    protected Date testPartEntryTimestamp;

    @Before
    public void initTestPart() {
        testPartSessionState = testSessionState.getTestPartSessionStates().get(getTestPartNodeKey());

        /* Create times & deltas. We use powers to 2 in deltas to help diagnosis and avoid addition collisions */
        testPartEntryDelta = 1000L;
        testPartEntryTimestamp = ObjectUtilities.addToTime(testEntryTimestamp, testPartEntryDelta);
    }

    //-------------------------------------------------------
    // Basic test-level navigation tests

    @Test
    public void testBefore() {
        RunAssertions.assertNotYetEntered(testSessionState);
        Assert.assertEquals(0.0, testSessionState.computeDuration(), 0);
        Assert.assertNull(testSessionState.getDurationIntervalStartTime());
        Assert.assertNull(testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());
        Assert.assertNotNull(testPartSessionState);

        /* We won't have entered the testPart yet */
        RunAssertions.assertNotYetEntered(testPartSessionState);

        /* We won't have entered any sections yet */
        assertAssessmentSectionsNotEntered(allSections());

        /* We won't have entered any items yet */
        assertItemsNotEntered(allItems());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEntryIntoTestNullTimestamp() {
        testSessionController.enterTest(null);
    }

    @Test
    public void testGoodEntryIntoTest() {
        testSessionController.enterTest(testEntryTimestamp);

        /* Test should be open. Times at zero. No part or item selected */
        assertTestOpen();
        Assert.assertEquals(0L, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testEntryTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertNull(testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* It should be possible to enter a testPart */
        Assert.assertNotNull(testSessionController.findNextEnterableTestPart());

        /* We won't have entered the testPart yet */
        RunAssertions.assertNotYetEntered(testPartSessionState);

        /* We won't have entered any sections yet */
        assertAssessmentSectionsNotEntered(allSections());

        /* We won't have entered any items yet */
        assertItemsNotEntered(allItems());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEntryIntoTestPartNullTimestamp() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(null);
    }


    /**
     * Tests common effects entry into first {@link TestPart}. Subclasses will need to
     * do extra to check state on items, as that depends on navigation mode.
     */
    @Test
    public void testGoodEntryIntoTestPart() {
        testSessionController.enterTest(testEntryTimestamp);

        /* Enter test part */
        final TestPlanNode testPartNode = testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        Assert.assertEquals(getTestPartNode(), testPartNode);

        /* There should be no further part available */
        Assert.assertNull(testSessionController.findNextEnterableTestPart());

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPartEntryTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPartNodeKey(), testSessionState.getCurrentTestPartKey());
        /* (Subclass will want to check item selection) */

        /* Check state on testPart */
        assertTestPartOpen();
        Assert.assertEquals(0, testPartSessionState.getDurationAccumulated());
        Assert.assertEquals(testPartEntryTimestamp, testPartSessionState.getDurationIntervalStartTime());
    }

    //-------------------------------------------------------

    protected TestPlanNode getTestPartNode() {
        return getTestNode("p");
    }

    protected TestPlanNodeKey getTestPartNodeKey() {
        return getTestNodeKey("p");
    }

    protected void assertTestPartOpen() {
        RunAssertions.assertOpen(testPartSessionState, testPartEntryTimestamp);
    }

    protected void assertTestPartEnded(final Date timestamp) {
        RunAssertions.assertNowEnded(testPartSessionState, timestamp);
    }

    protected void assertTestPartNowExited(final Date timestamp) {
        RunAssertions.assertNowExited(testPartSessionState, timestamp);
    }
}
