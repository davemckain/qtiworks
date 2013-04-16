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

import uk.ac.ed.ph.jqtiplus.exception.QtiCandidateStateException;
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

    private static final String TEST_FILE_PATH = "running/test-branch-rule.xml";

    private static final List<String> TEST_NODES = Arrays.asList(new String[] {
        "p1",
            "s11",
                "s111",
                    "i1111", /* = 1st entered item */
                    "i1112", /* Skipped */
                    "i1113", /* = 2nd entered item */
                    "i1114", /* Skipped */
                "s112",      /* Failed P/C */
                    "i1121", /* = 3rd entered item */
                    "i1122", /* Skipped */
                "s113",
                    "i1131", /* = 4th entered item, ends testPart afterwards */
                    "i1132", /* Skipped */
        "p2",
            "s21",
                "i211",      /* = 5th entered item */
        "p3",
            "s31",
                "i311",      /* = 6th entered item */
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
    private long item6EndDelta;
    private Date item6EndTimestamp;

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
        testPart1ExitTimestamp = ObjectUtilities.addToTime(item4EndTimestamp, testPart1EntryDelta);

        item5EndDelta = 128000L;
        item5EndTimestamp = ObjectUtilities.addToTime(testPart1ExitTimestamp, item5EndDelta);

        testPart2ExitDelta = 256000L;
        testPart2ExitTimestamp = ObjectUtilities.addToTime(item5EndTimestamp, testPart1EntryDelta);

        item5EndDelta = 512000L;
        item5EndTimestamp = ObjectUtilities.addToTime(testPart2ExitTimestamp, item5EndDelta);
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

        /* Check state of sections we should have entered */
        assertAssessmentSectionOpen("s11", testPart1EntryTimestamp);
        assertAssessmentSectionOpen("s111", testPart1EntryTimestamp);

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i1111", testPart1EntryTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EntryTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of things we haven't entered yet */
        assertItemsNotEntered(allItemsAfter("i1111"));
        assertAssessmentSectionsNotEntered(allSectionsAfter("s111"));
        assertTestPart2NotYetEntered();
        assertTestPart3NotYetEntered();
    }

    @Test
    public void testEntryIntoItem2() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        final TestPlanNode itemNode = testSessionController.endItemLinear(item1EndTimestamp);
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

        /* Check state of sections we should have entered */
        assertAssessmentSectionOpen("s11", testPart1EntryTimestamp);
        assertAssessmentSectionOpen("s111", item1EndTimestamp);

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i1113", item1EndTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(item1EndTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of future things */
        assertAssessmentSectionsNotEntered(allSectionsAfter("s111"));
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemEndedButNotEntered("i1112", item1EndTimestamp);
        assertItemsNotEntered(allItemsAfter("i1113"));
        assertTestPart2NotYetEntered();
        assertTestPart3NotYetEntered();
    }

    @Test
    public void testEntryIntoItem3() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.endItemLinear(item1EndTimestamp);
        final TestPlanNode itemNode = testSessionController.endItemLinear(item2EndTimestamp);
        Assert.assertEquals(getTestNode("i1133"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + item2EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item2EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart1NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i1133"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(item1EndDelta + item2EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(item2EndTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of sections we should and should not have entered */
        assertAssessmentSectionOpen("s11", testPart1EntryTimestamp);
        assertAssessmentSectionNowEnded("s111", item1EndTimestamp);
        assertAssessmentSectionFailedPreconditionAndNotExited("s112");
        assertAssessmentSectionOpen("s113", item1EndTimestamp);
        assertAssessmentSectionNotEntered("s12");
        assertAssessmentSectionNotEntered("s121");

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i1133", item2EndTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(item2EndTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of previous & future items */
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemNowEnded("i1132", item2EndTimestamp);
        assertItemFailedPreconditionAndNotExited("i1112");
        assertItemFailedPreconditionAndNotExited("i1131");
        assertItemsNotEntered(allItemsAfter("i1133"));
    }

    @Test
    public void testEntryIntoItem4() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.endItemLinear(item1EndTimestamp);
        testSessionController.endItemLinear(item2EndTimestamp);
        final TestPlanNode itemNode = testSessionController.endItemLinear(item3EndTimestamp);
        Assert.assertEquals(getTestNode("i1211"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + item2EndDelta + item3EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item3EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart1NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i1211"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(item1EndDelta + item2EndDelta + item3EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(item3EndTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of sections we should and should not have entered */
        assertAssessmentSectionNowEnded("s11", item3EndTimestamp);
        assertAssessmentSectionNowEnded("s111", item1EndTimestamp);
        assertAssessmentSectionFailedPreconditionAndNotExited("s112");
        assertAssessmentSectionNowEnded("s113", item3EndTimestamp);
        assertAssessmentSectionOpen("s12", item3EndTimestamp);
        assertAssessmentSectionOpen("s121", item3EndTimestamp);

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i1211", item3EndTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(item3EndTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of previous & future items */
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemNowEnded("i1132", item2EndTimestamp);
        assertItemNowEnded("i1133", item3EndTimestamp);
        assertItemFailedPreconditionAndNotExited("i1112");
        assertItemFailedPreconditionAndNotExited("i1131");
        assertItemsNotEntered(allItemsAfter("i1211"));
    }

    @Test
    public void testEntryIntoItem5() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.endItemLinear(item1EndTimestamp);
        testSessionController.endItemLinear(item2EndTimestamp);
        testSessionController.endItemLinear(item3EndTimestamp);
        final TestPlanNode itemNode = testSessionController.endItemLinear(item4EndTimestamp);
        Assert.assertEquals(getTestNode("i123"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item4EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart1NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i123"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(item4EndTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of sections we should and should not have entered */
        assertAssessmentSectionNowEnded("s11", item3EndTimestamp);
        assertAssessmentSectionNowEnded("s111", item1EndTimestamp);
        assertAssessmentSectionFailedPreconditionAndNotExited("s112");
        assertAssessmentSectionNowEnded("s113", item3EndTimestamp);
        assertAssessmentSectionNowEnded("s121", item4EndTimestamp);
        assertAssessmentSectionOpen("s12", item3EndTimestamp);
        assertAssessmentSectionNowEnded("s121", item4EndTimestamp);

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i123", item4EndTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(item4EndTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of previous & future items */
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemNowEnded("i1132", item2EndTimestamp);
        assertItemNowEnded("i1133", item3EndTimestamp);
        assertItemNowEnded("i1211", item4EndTimestamp);
        assertItemFailedPreconditionAndNotExited("i1112");
        assertItemFailedPreconditionAndNotExited("i1131");
        assertItemFailedPreconditionAndNotExited("i122");
        assertItemsNotEntered(allItemsAfter("i123"));
    }

    @Test
    public void testEndTestPartNaturallyAfterItem5() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.endItemLinear(item1EndTimestamp);
        testSessionController.endItemLinear(item2EndTimestamp);
        testSessionController.endItemLinear(item3EndTimestamp);
        testSessionController.endItemLinear(item4EndTimestamp);
        final TestPlanNode itemNode = testSessionController.endItemLinear(item5EndTimestamp);

        /* This should have ended the testPart */
        Assert.assertNull(itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta + item5EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item5EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart1NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Ended(item5EndTimestamp);
        Assert.assertEquals(item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta + item5EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertNull(testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of sections either entered or failed at */
        assertAssessmentSectionNowEnded("s11", item3EndTimestamp);
        assertAssessmentSectionNowEnded("s111", item1EndTimestamp);
        assertAssessmentSectionFailedPreconditionAndNotExited("s112");
        assertAssessmentSectionNowEnded("s113", item3EndTimestamp);
        assertAssessmentSectionNowEnded("s121", item4EndTimestamp);
        assertAssessmentSectionNowEnded("s12", item5EndTimestamp);
        assertAssessmentSectionNowEnded("s121", item4EndTimestamp);

        /* Check state of items */
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemFailedPreconditionAndNotExited("i1112");
        assertItemFailedPreconditionAndNotExited("i1131");
        assertItemNowEnded("i1132", item2EndTimestamp);
        assertItemNowEnded("i1133", item3EndTimestamp);
        assertItemNowEnded("i1211", item4EndTimestamp);
        assertItemFailedPreconditionAndNotExited("i122");
        assertItemNowEnded("i123", item5EndTimestamp);
        assertItemFailedPreconditionAndNotExited("i124");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEndTestPartExplicitlyNullTimestamp() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.endCurrentTestPart(null);
    }

    @Test
    public void testEndTestPartExplicitlyAfterEntry() {
        final long testPart1EndDelta = 64000L;
        final Date testPart1EndTimestamp = ObjectUtilities.addToTime(testPart1EntryTimestamp, testPart1EndDelta);
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.endCurrentTestPart(testPart1EndTimestamp);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + testPart1EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart1NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());
        Assert.assertNull(testSessionController.findNextEnterableTestPart());

        /* Check state on testPart */
        assertTestPart1Ended(testPart1EndTimestamp);
        Assert.assertEquals(testPart1EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertNull(testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of sections either entered or failed at */
        assertAssessmentSectionNowEnded("s11", testPart1EndTimestamp);
        assertAssessmentSectionNowEnded("s111", testPart1EndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s112", testPart1EndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s113", testPart1EndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s121", testPart1EndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s12", testPart1EndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s121", testPart1EndTimestamp);

        /* Check state of items */
        assertItemNowEnded("i1111", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i1112", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i1131", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i1132", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i1133", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i1211", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i122", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i123", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i124", testPart1EndTimestamp);
    }

    @Test
    public void testExitTestPartExplicitlyAfterEntry() {
        final long testPart1EndDelta = 64000L;
        final Date testPart1EndTimestamp = ObjectUtilities.addToTime(testPart1EntryTimestamp, testPart1EndDelta);
        final long testPart1ExitDelta = 128000L;
        final Date testPart1ExitTimestamp = ObjectUtilities.addToTime(testPart1EndTimestamp, testPart1ExitDelta);
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.endCurrentTestPart(testPart1EndTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1ExitTimestamp);

        /* Check state on test */
        assertTestNowEnded(testPart1ExitTimestamp);
        Assert.assertEquals(testPart1EntryDelta + testPart1EndDelta + testPart1ExitDelta, testSessionState.getDurationAccumulated());
        Assert.assertNull(testPart1SessionState.getDurationIntervalStartTime());
        Assert.assertNull(testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1NowExited(testPart1ExitTimestamp);
        Assert.assertEquals(testPart1EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertNull(testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of sections either entered or failed at */
        assertAssessmentSectionNowExited("s11", testPart1ExitTimestamp);
        assertAssessmentSectionNowExited("s111", testPart1ExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s112", testPart1ExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s113", testPart1ExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s121", testPart1ExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s12", testPart1ExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s121", testPart1ExitTimestamp);

        /* Check state of items */
        assertItemNowExited("i1111", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i1112", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i1131", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i1132", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i1133", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i1211", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i122", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i123", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i124", testPart1ExitTimestamp);
    }

    @Test
    public void testEndTestPartExplicitlyAfterItem1() {
        final long testPart1EndDelta = 64000L;
        final Date testPart1EndTimestamp = ObjectUtilities.addToTime(item1EndTimestamp, testPart1EndDelta);
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.endItemLinear(item1EndTimestamp);
        testSessionController.endCurrentTestPart(testPart1EndTimestamp);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + testPart1EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPart1NodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());
        Assert.assertNull(testSessionController.findNextEnterableTestPart());

        /* Check state on testPart */
        assertTestPart1Ended(testPart1EndTimestamp);
        Assert.assertEquals(item1EndDelta + testPart1EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertNull(testPart1SessionState.getDurationIntervalStartTime());

        /* Check state of sections either entered or failed at */
        assertAssessmentSectionNowEnded("s11", testPart1EndTimestamp);
        assertAssessmentSectionNowEnded("s111", item1EndTimestamp);
        assertAssessmentSectionFailedPreconditionAndNotExited("s112");
        assertAssessmentSectionNowEnded("s113", testPart1EndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s121", testPart1EndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s12", testPart1EndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s121", testPart1EndTimestamp);

        /* Check state of items */
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemFailedPreconditionAndNotExited("i1112");
        assertItemFailedPreconditionAndNotExited("i1131");
        assertItemNowEnded("i1132", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i1133", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i1211", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i122", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i123", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i124", testPart1EndTimestamp);
    }

    @Test
    public void testExitTestPartExplicitlyAfterItem1() {
        final long testPart1EndDelta = 64000L;
        final Date testPart1EndTimestamp = ObjectUtilities.addToTime(item1EndTimestamp, testPart1EndDelta);
        final long testPart1ExitDelta = 128000L;
        final Date testPart1ExitTimestamp = ObjectUtilities.addToTime(testPart1EndTimestamp, testPart1ExitDelta);
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.endItemLinear(item1EndTimestamp);
        testSessionController.endCurrentTestPart(testPart1EndTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1ExitTimestamp);

        /* Check state on test */
        assertTestNowEnded(testPart1ExitTimestamp);
        Assert.assertEquals(testPart1EntryDelta + item1EndDelta + testPart1EndDelta + testPart1ExitDelta, testSessionState.getDurationAccumulated());
        Assert.assertNull(testPart1SessionState.getDurationIntervalStartTime());
        Assert.assertNull(testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1NowExited(testPart1ExitTimestamp);
        Assert.assertEquals(item1EndDelta + testPart1EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertNull(testPart1SessionState.getDurationIntervalStartTime());

        /* Check final state of sections */
        assertAssessmentSectionNowExited("s11", testPart1ExitTimestamp);
        assertAssessmentSectionNowExited("s111", testPart1ExitTimestamp);
        assertAssessmentSectionFailedPreconditionAndExited("s112", testPart1ExitTimestamp);
        assertAssessmentSectionNowExited("s113", testPart1ExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s121", testPart1ExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s12", testPart1ExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s121", testPart1ExitTimestamp);

        /* Check state of items */
        assertItemNowExited("i1111", testPart1ExitTimestamp);
        assertItemFailedPreconditionAndExited("i1112", testPart1ExitTimestamp);
        assertItemFailedPreconditionAndExited("i1131", testPart1ExitTimestamp);
        assertItemNowExited("i1132", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i1133", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i1211", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i122", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i123", testPart1ExitTimestamp);
        assertItemExitedButNotEntered("i124", testPart1ExitTimestamp);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testExitTestPartBeforeEnded() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        testSessionController.exitTest(testPart1EntryTimestamp);
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

    protected void assertTestPart1Ended(final Date timestamp) {
        RunAssertions.assertNowEnded(testPart1SessionState, timestamp);
    }

    protected void assertTestPart1NowExited(final Date timestamp) {
        RunAssertions.assertNowExited(testPart1SessionState, timestamp);
    }

    protected void assertTestPart2NotYetEntered() {
        RunAssertions.assertNotYetEntered(testPart2SessionState);
    }

    protected void assertTestPart3NotYetEntered() {
        RunAssertions.assertNotYetEntered(testPart3SessionState);
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
