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
import uk.ac.ed.ph.jqtiplus.node.test.BranchRule;
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
 * Tests navigation through a test containing lots of {@link BranchRule}s.
 *
 * @author David McKain
 */
public final class TestBranchRuleNavigationTest extends TestTestBase {

    private static final String TEST_FILE_PATH = "running/test-linear-branchRule.xml";

    private static final List<String> TEST_NODES = Arrays.asList(new String[] {
        "p1",
            "s11",
                "s111",
                    "i1111", /* = 1st entered item, should then branch to i1113 */
                    "i1112", /* Skipped by branchRule */
                    "i1113", /* = 2nd entered item, should then branch to s112 */
                    "i1114", /* Skipped by branchRule */
                "s112",
                    "i1121", /* Skipped due to failed preCondition */
                    "i1122", /* = 3rd entered item, should then branch to exit section */
                    "i1123", /* Skipped by branchRule */
                "s113",
                    "i1131", /* = 4th entered item, should then branch to end of testPart */
                    "i1132", /* Skipped by branchRule */
        "p2",
            "s21",
                "i211",      /* = 5th entered item, which ends the test afterwards */
        "p3",
            "s31",
                "i311",
    });


    private TestPartSessionState testPart1SessionState;
    private TestPartSessionState testPart2SessionState;
    private TestPartSessionState testPart3SessionState;

    private long testPart1EntryDelta;
    private Date testPart1EntryTimestamp;
    private long item1EndDelta;
    private Date item1EndTimestamp;
    private long item2EndDelta;
    private Date item2EndTimestamp;
    private long item3EndDelta;
    private Date item3EndTimestamp;
    private long item4EndDelta;
    private Date item4EndTimestamp;
    private long testPart1ExitDelta;
    private Date testPart1ExitTimestamp;
    private long item5EndDelta;
    private Date item5EndTimestamp;
    private long testPart2ExitDelta;
    private Date testPart2ExitTimestamp;
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
        testPart3SessionState = testSessionState.getTestPartSessionStates().get(getTestPart3NodeKey());

        testPart1EntryDelta = 2000L;
        testPart1EntryTimestamp = ObjectUtilities.addToTime(testEntryTimestamp, testPart1EntryDelta);

        item1EndDelta = 4000L;
        item1EndTimestamp = ObjectUtilities.addToTime(testPart1EntryTimestamp, item1EndDelta);

        item2EndDelta = 8000L;
        item2EndTimestamp = ObjectUtilities.addToTime(item1EndTimestamp, item2EndDelta);

        item3EndDelta = 16000L;
        item3EndTimestamp = ObjectUtilities.addToTime(item2EndTimestamp, item3EndDelta);

        item4EndDelta = 32000L;
        item4EndTimestamp = ObjectUtilities.addToTime(item3EndTimestamp, item4EndDelta);

        testPart1ExitDelta = 64000L;
        testPart1ExitTimestamp = ObjectUtilities.addToTime(item4EndTimestamp, testPart1ExitDelta);

        item5EndDelta = 128000L;
        item5EndTimestamp = ObjectUtilities.addToTime(testPart1ExitTimestamp, item5EndDelta);

        testPart2ExitDelta = 256000L;
        testPart2ExitTimestamp = ObjectUtilities.addToTime(item5EndTimestamp, testPart2ExitDelta);

        testExitDelta = 512000L;
        testExitTimestamp = ObjectUtilities.addToTime(testPart2ExitTimestamp, testExitDelta);
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
        Assert.assertEquals(getTestNodeKey("i1111"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(0, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EntryTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of item we just entered */
        final ItemSessionState itemSessionState = assertItemOpen("i1111", testPart1EntryTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EntryTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of everything else entered */
        assertItemsNotEntered(allItemsAfter("i1111"));
        assertAssessmentSectionOpen("s11", testPart1EntryTimestamp);
        assertAssessmentSectionOpen("s111", testPart1EntryTimestamp);
        assertAssessmentSectionsNotEntered(allSectionsAfter("s111"));
        assertTestPart2NotYetEntered();
        assertTestPart3NotYetEntered();
    }

    @Test
    public void testEntryIntoItem2() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        final TestPlanNode itemNode = testSessionController.advanceItemLinear(item1EndTimestamp);
        Assert.assertEquals(getTestNode("i1113"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item1EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart1NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i1113"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(item1EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(item1EndTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of item we should have entered */
        final ItemSessionState itemSessionState = assertItemOpen("i1113", item1EndTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(item1EndTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of everything else */
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemJumpedByBranchRuleAndNotExited("i1112");
        assertItemsNotEntered(allItemsAfter("i1113"));
        assertAssessmentSectionOpen("s11", testPart1EntryTimestamp);
        assertAssessmentSectionOpen("s111", testPart1EntryTimestamp);
        assertAssessmentSectionsNotEntered(allSectionsAfter("s111"));
        assertTestPart2NotYetEntered();
        assertTestPart3NotYetEntered();
    }

    @Test
    public void testEntryIntoItem3() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        final TestPlanNode itemNode = testSessionController.advanceItemLinear(item2EndTimestamp);
        Assert.assertEquals(getTestNode("i1122"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + item2EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item2EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart1NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i1122"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(item1EndDelta + item2EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(item2EndTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i1122", item2EndTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(item2EndTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of everything else */
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemJumpedByBranchRuleAndNotExited("i1112");
        assertItemNowEnded("i1113", item2EndTimestamp);
        assertItemNotEntered("i1114");
        assertItemFailedPreconditionAndNotExited("i1121");
        assertItemsNotEntered(allItemsAfter("i1122"));
        assertAssessmentSectionOpen("s11", testPart1EntryTimestamp);
        assertAssessmentSectionNowEnded("s111", item2EndTimestamp);
        assertAssessmentSectionOpen("s112", item2EndTimestamp);
        assertAssessmentSectionsNotEntered(allSectionsAfter("s112"));
        assertTestPart2NotYetEntered();
        assertTestPart3NotYetEntered();
    }

    @Test
    public void testEntryIntoItem4() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.advanceItemLinear(item2EndTimestamp);
        final TestPlanNode itemNode = testSessionController.advanceItemLinear(item3EndTimestamp);
        Assert.assertEquals(getTestNode("i1131"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + item2EndDelta + item3EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item3EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart1NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i1131"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(item1EndDelta + item2EndDelta + item3EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(item3EndTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i1131", item3EndTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(item3EndTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of everything else */
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemJumpedByBranchRuleAndNotExited("i1112");
        assertItemNowEnded("i1113", item2EndTimestamp);
        assertItemNotEntered("i1114");
        assertItemFailedPreconditionAndNotExited("i1121");
        assertItemNowEnded("i1122", item3EndTimestamp);
        assertItemNotEntered("i1123");
        assertItemsNotEntered(allItemsAfter("i1131"));
        assertAssessmentSectionOpen("s11", testPart1EntryTimestamp);
        assertAssessmentSectionNowEnded("s111", item2EndTimestamp);
        assertAssessmentSectionNowEnded("s112", item3EndTimestamp);
        assertAssessmentSectionOpen("s113", item3EndTimestamp);
        assertAssessmentSectionsNotEntered(allSectionsAfter("s113"));
        assertTestPart2NotYetEntered();
        assertTestPart3NotYetEntered();
    }

    @Test
    public void testEndTestPart1AfterItem4() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.advanceItemLinear(item2EndTimestamp);
        testSessionController.advanceItemLinear(item3EndTimestamp);
        Assert.assertNull(testSessionController.advanceItemLinear(item4EndTimestamp));

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item4EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart1NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Ended();
        Assert.assertEquals(item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertNull(testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of everything else */
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemJumpedByBranchRuleAndNotExited("i1112");
        assertItemNowEnded("i1113", item2EndTimestamp);
        assertItemJumpedByBranchRuleAndNotExited("i1114");
        assertItemFailedPreconditionAndNotExited("i1121");
        assertItemNowEnded("i1122", item3EndTimestamp);
        assertItemJumpedByBranchRuleAndNotExited("i1123");
        assertItemNowEnded("i1131", item4EndTimestamp);
        assertItemJumpedByBranchRuleAndNotExited("i1132");
        assertItemsNotEntered("i211", "i311");
        assertAssessmentSectionNowEnded("s11", item4EndTimestamp);
        assertAssessmentSectionNowEnded("s111", item2EndTimestamp);
        assertAssessmentSectionNowEnded("s112", item3EndTimestamp);
        assertAssessmentSectionNowEnded("s113", item4EndTimestamp);
        assertAssessmentSectionsNotEntered("s21", "s31");
        assertTestPart2NotYetEntered();
        assertTestPart3NotYetEntered();
    }

    @Test
    public void testEntryIntoTestPart2AndItem5() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.advanceItemLinear(item2EndTimestamp);
        testSessionController.advanceItemLinear(item3EndTimestamp);
        Assert.assertNull(testSessionController.advanceItemLinear(item4EndTimestamp));
        final TestPlanNode testPart2 = testSessionController.enterNextAvailableTestPart(testPart1ExitTimestamp);
        Assert.assertEquals(getTestPart2Node(), testPart2);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta + testPart1ExitDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1ExitTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart2NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i211"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart2Open();
        Assert.assertEquals(0, testPart2SessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1ExitTimestamp, testPart2SessionState.getDurationIntervalStartTime());

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i211", testPart1ExitTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1ExitTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of everything else */
        assertTestPart1AllExited();
        assertItemNotEntered("i311");
        assertAssessmentSectionOpen("s21", testPart1ExitTimestamp);
        assertAssessmentSectionsNotEntered("s31");
        assertTestPart3NotYetEntered();
    }

    @Test
    public void testEndTestPart2AfterItem5() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.advanceItemLinear(item2EndTimestamp);
        testSessionController.advanceItemLinear(item3EndTimestamp);
        testSessionController.advanceItemLinear(item4EndTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1ExitTimestamp);
        Assert.assertNull(testSessionController.advanceItemLinear(item5EndTimestamp));

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta + testPart1ExitDelta + item5EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item5EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart2NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart2Ended();
        Assert.assertEquals(item5EndDelta, testPart2SessionState.getDurationAccumulated());
        Assert.assertNull(null, testPart2SessionState.getDurationIntervalStartTime());

        /* Check state of everything else */
        assertTestPart1AllExited();
        assertItemNowEnded("i211", item5EndTimestamp);
        assertItemNotEntered("i311");
        assertAssessmentSectionNowEnded("s21", item5EndTimestamp);
        assertAssessmentSectionsNotEntered("s31");
        assertTestPart3NotYetEntered();
    }

    @Test
    public void testEndTestAfterTestPart2() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.advanceItemLinear(item2EndTimestamp);
        testSessionController.advanceItemLinear(item3EndTimestamp);
        testSessionController.advanceItemLinear(item4EndTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1ExitTimestamp);
        testSessionController.advanceItemLinear(item5EndTimestamp);
        Assert.assertNull(testSessionController.enterNextAvailableTestPart(testPart2ExitTimestamp));

        /* Check state on test */
        assertTestNowEnded(testPart2ExitTimestamp);
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta + testPart1ExitDelta + item5EndDelta + testPart2ExitDelta, testSessionState.getDurationAccumulated());
        Assert.assertNull(testSessionState.getDurationIntervalStartTime());
        Assert.assertNull(testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart2AllExited();
        Assert.assertEquals(item5EndDelta, testPart2SessionState.getDurationAccumulated());
        Assert.assertNull(null, testPart2SessionState.getDurationIntervalStartTime());

        /* Check state of everything else */
        assertTestPart1AllExited();
        assertAssessmentSectionsNotEntered("s31");
        assertTestPart3NotYetEntered();
    }

    @Test
    public void testExitTestAfterTestPart2() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.advanceItemLinear(item2EndTimestamp);
        testSessionController.advanceItemLinear(item3EndTimestamp);
        testSessionController.advanceItemLinear(item4EndTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1ExitTimestamp);
        testSessionController.advanceItemLinear(item5EndTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart2ExitTimestamp);
        testSessionController.exitTest(testExitTimestamp);

        /* Check state on test */
        assertTestNowExited(testExitTimestamp);
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta + testPart1ExitDelta + item5EndDelta + testPart2ExitDelta, testSessionState.getDurationAccumulated());
        Assert.assertNull(testSessionState.getDurationIntervalStartTime());
        Assert.assertNull(testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state of everything else */
        assertTestPart1AllExited();
        assertTestPart2AllExited();
        assertTestPart3Missed();
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

    protected void assertTestPart2Open() {
        RunAssertions.assertOpen(testPart2SessionState, testPart1ExitTimestamp);
    }

    protected void assertTestPart3Open() {
        RunAssertions.assertOpen(testPart3SessionState, testPart2ExitTimestamp);
    }

    protected void assertTestPart1Ended() {
        RunAssertions.assertNowEnded(testPart1SessionState, item4EndTimestamp);
    }

    protected void assertTestPart2Ended() {
        RunAssertions.assertNowEnded(testPart2SessionState, item5EndTimestamp);
    }

    protected void assertTestPart1AllExited() {
        assertItemNowExited("i1111", testPart1ExitTimestamp);
        assertItemJumpedByBranchRuleAndExited("i1112", testPart1ExitTimestamp);
        assertItemNowExited("i1113", testPart1ExitTimestamp);
        assertItemJumpedByBranchRuleAndExited("i1114", testPart1ExitTimestamp);
        assertItemFailedPreconditionAndExited("i1121", testPart1ExitTimestamp);
        assertItemNowExited("i1122", testPart1ExitTimestamp);
        assertItemJumpedByBranchRuleAndExited("i1123", testPart1ExitTimestamp);
        assertItemNowExited("i1131", testPart1ExitTimestamp);
        assertItemJumpedByBranchRuleAndExited("i1132", testPart1ExitTimestamp);
        assertAssessmentSectionNowExited("s11", testPart1ExitTimestamp);
        assertAssessmentSectionNowExited("s111", testPart1ExitTimestamp);
        assertAssessmentSectionNowExited("s112", testPart1ExitTimestamp);
        assertAssessmentSectionNowExited("s113", testPart1ExitTimestamp);
        RunAssertions.assertNowExited(testPart1SessionState, testPart1ExitTimestamp);
    }

    protected void assertTestPart2AllExited() {
        assertItemNowExited("i211", testPart2ExitTimestamp);
        assertAssessmentSectionNowExited("s21", testPart2ExitTimestamp);
        RunAssertions.assertNowExited(testPart2SessionState, testPart2ExitTimestamp);
    }

    protected void assertTestPart3Missed() {
        assertItemNotEntered("i311");
        assertAssessmentSectionNotEntered("s31");
        assertTestPart3Jumped();
    }

    protected void assertTestPart2NotYetEntered() {
        RunAssertions.assertNotYetEntered(testPart2SessionState);
    }

    protected void assertTestPart3NotYetEntered() {
        RunAssertions.assertNotYetEntered(testPart3SessionState);
    }

    protected void assertTestPart3Jumped() {
        RunAssertions.assertJumpedByBranchRuleAndNotExited(testPart3SessionState);
    }

    protected TestPlanNode getTestPart2Node() {
        return getTestNode("p2");
    }

    protected TestPlanNodeKey getTestPart2NodeKey() {
        return getTestNodeKey("p2");
    }

    protected TestPlanNode getTestPart3Node() {
        return getTestNode("p3");
    }

    protected TestPlanNodeKey getTestPart3NodeKey() {
        return getTestNodeKey("p3");
    }
}
