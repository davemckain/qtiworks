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
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPartSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Companion to {@link TestBranchRuleNavigationTest} that tests a deep EXIT_TEST branch.
 *
 * @author David McKain
 */
public final class TestBranchRuleNavigationExitTest extends TestTestBase {

    private static final String TEST_FILE_PATH = "running/test-linear-branchRule-exit.xml";

    private static final List<String> TEST_NODES = Arrays.asList(new String[] {
        "p1",
            "s11",
                "i111", /* = 1st entered item, then does EXIT_TEST*/
        "p2",           /* Jumped entered */
            "s21",
                "i211", /* = 5th entered item, which ends the test afterwards */
    });


    private TestPartSessionState testPart1SessionState;
    private TestPartSessionState testPart2SessionState;

    private long testPart1EntryDelta;
    private Date testPart1EntryTimestamp;
    private long item1EndDelta;
    private Date item1EndTimestamp;
    private long testExitDelta;
    private Date testExitTimestamp;

    @Override
    protected List<String> testNodes() {
        return TEST_NODES;
    }

    @Override
    protected String getTestFilePath() {
        return TEST_FILE_PATH;
    }

    @Before
    public void before() {
        testPart1SessionState = testSessionState.getTestPartSessionStates().get(getTestPart1NodeKey());
        testPart2SessionState = testSessionState.getTestPartSessionStates().get(getTestPart2NodeKey());

        testPart1EntryDelta = 2000L;
        testPart1EntryTimestamp = ObjectUtilities.addToTime(testEntryTimestamp, testPart1EntryDelta);

        item1EndDelta = 4000L;
        item1EndTimestamp = ObjectUtilities.addToTime(testPart1EntryTimestamp, item1EndDelta);

        testExitDelta = 8000L;
        testExitTimestamp = ObjectUtilities.addToTime(item1EndTimestamp, testExitDelta);
    }

    //-------------------------------------------------------

    @Test
    public void testEntryIntoTestPart1AndItem1() {
        testSessionController.enterTest(testEntryTimestamp);

        /* Enter test part 1 (which should also select first item) */
        final TestPlanNode testPartNode = testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        Assert.assertEquals(getTestPart1Node(), testPartNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EntryTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart1NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i111"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(0, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EntryTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of item we just entered */
        final ItemSessionState itemSessionState = assertItemOpen("i111", testPart1EntryTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EntryTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of everything else entered */
        assertItemsNotEntered(allItemsAfter("i111"));
        assertAssessmentSectionOpen("s11", testPart1EntryTimestamp);
        assertTestPart2NotYetEntered();
    }

    @Test
    public void testEndItem1() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        Assert.assertNull(testSessionController.advanceItemLinear(item1EndTimestamp));

        /* Check state on test */
        assertTestNowEnded(item1EndTimestamp);
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertNull(testSessionState.getDurationIntervalStartTime());
        Assert.assertNull(testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1AllExited();
        assertTestPart2Jumped();
    }

    @Test
    public void testExitTestAfterItem1() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.exitTest(testExitTimestamp);

        /* Check state on test */
        assertTestNowExited(testExitTimestamp);
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertNull(testSessionState.getDurationIntervalStartTime());
        Assert.assertNull(testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state of everything else */
        assertTestPart1AllExited();
        assertTestPart2Jumped();
    }

    //-------------------------------------------------------

    protected TestPlanNode getTestPart1Node() {
        return getTestNode("p1");
    }

    protected TestPlanNodeKey getTestPart1NodeKey() {
        return getTestNodeKey("p1");
    }

    protected void assertTestPart1Open() {
        RunAssertions.assertOpen(testPart1SessionState, testPart1EntryTimestamp);
    }

    protected void assertTestPart1Ended() {
        RunAssertions.assertNowEnded(testPart1SessionState, item1EndTimestamp);
    }

    protected void assertTestPart1AllExited() {
        assertItemNowExited("i111", item1EndTimestamp);
        assertAssessmentSectionNowExited("s11", item1EndTimestamp);
        RunAssertions.assertNowExited(testPart1SessionState, item1EndTimestamp);
    }

    protected void assertTestPart2NotYetEntered() {
        RunAssertions.assertNotYetEntered(testPart2SessionState);
    }

    protected void assertTestPart2Jumped() {
        RunAssertions.assertJumpedByBranchRuleAndNotExited(testPart2SessionState);
    }

    protected TestPlanNode getTestPart2Node() {
        return getTestNode("p2");
    }

    protected TestPlanNodeKey getTestPart2NodeKey() {
        return getTestNodeKey("p2");
    }
}
