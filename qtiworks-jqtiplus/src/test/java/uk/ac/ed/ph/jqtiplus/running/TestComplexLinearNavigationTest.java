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
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.PreCondition;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests navigation through a linear {@link TestPart}
 * containing {@link PreCondition} and nested {@link AssessmentSection}s.
 *
 * @author David McKain
 */
public final class TestComplexLinearNavigationTest extends SinglePartTestBase {

    private static final String TEST_FILE_PATH = "running/test-linear-preCondition.xml";

    private static final List<String> TEST_NODES = Arrays.asList(new String[] {
        "p",
            "s11",
                "s111",
                    "i1111", /* = 1st entered item */
                    "i1112", /* Failed preCondition */
                "s112",      /* Failed P/C */
                    "i1121",
                "s113",
                    "i1131", /* Failed P/C */
                    "i1132", /* = 2nd entered item */
                    "i1133", /* = 3rd entered item */
            "s12",
                "s121",
                    "i1211", /* = 4th entered item */
                "i122",      /* Failed P/C */
                "i123",      /* = 5th entered item */
                "i124"       /* Failed P/C */
    });

    private long item1EndDelta;
    private Date item1EndTimestamp;
    private long item2EndDelta;
    private Date item2EndTimestamp;
    private long item3EndDelta;
    private Date item3EndTimestamp;
    private long item4EndDelta;
    private Date item4EndTimestamp;
    private long item5EndDelta;
    private Date item5EndTimestamp;

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
        item1EndDelta = 2000L;
        item1EndTimestamp = ObjectUtilities.addToTime(testPartEntryTimestamp, item1EndDelta);

        item2EndDelta = 4000L;
        item2EndTimestamp = ObjectUtilities.addToTime(item1EndTimestamp, item2EndDelta);

        item3EndDelta = 8000L;
        item3EndTimestamp = ObjectUtilities.addToTime(item2EndTimestamp, item3EndDelta);

        item4EndDelta = 16000L;
        item4EndTimestamp = ObjectUtilities.addToTime(item3EndTimestamp, item4EndDelta);

        item5EndDelta = 32000L;
        item5EndTimestamp = ObjectUtilities.addToTime(item4EndTimestamp, item5EndDelta);
    }

    //-------------------------------------------------------

    @Test
    public void testEntryIntoTestPartAndItem1() {
        testSessionController.enterTest(testEntryTimestamp);

        /* Enter test part (which should also select first item) */
        final TestPlanNode testPartNode = testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        Assert.assertEquals(getTestPartNode(), testPartNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPartEntryTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPartNodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i1111"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPartOpen();
        Assert.assertEquals(0, testPartSessionState.getDurationAccumulated());
        Assert.assertEquals(testPartEntryTimestamp, testPartSessionState.getDurationIntervalStartTime());

        /* Check state of sections we should and should not have entered */
        assertAssessmentSectionOpen("s11", testPartEntryTimestamp);
        assertAssessmentSectionOpen("s111", testPartEntryTimestamp);
        assertAssessmentSectionsNotEntered(allSectionsAfter("s111"));

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i1111", testPartEntryTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(testPartEntryTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* We won't have entered any other items yet */
        assertItemsNotEntered(allItemsAfter("i1111"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEndItem1NullTimestamp() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.advanceItemLinear(null);
    }

    @Test
    public void testEntryIntoItem2() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        final TestPlanNode itemNode = testSessionController.advanceItemLinear(item1EndTimestamp);
        Assert.assertEquals(getTestNode("i1132"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item1EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPartNodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i1132"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPartOpen();
        Assert.assertEquals(item1EndDelta, testPartSessionState.getDurationAccumulated());
        Assert.assertEquals(item1EndTimestamp, testPartSessionState.getDurationIntervalStartTime());

        /* Check state of sections we should and should not have entered */
        assertAssessmentSectionOpen("s11", testPartEntryTimestamp);
        assertAssessmentSectionNowEnded("s111", item1EndTimestamp);
        assertAssessmentSectionFailedPreconditionAndNotExited("s112");
        assertAssessmentSectionOpen("s113", item1EndTimestamp);
        assertAssessmentSectionNotEntered("s12");
        assertAssessmentSectionNotEntered("s121");

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i1132", item1EndTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(item1EndTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Check state of previous & future items */
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemFailedPreconditionAndNotExited("i1112");
        assertItemFailedPreconditionAndNotExited("i1131");
        assertItemsNotEntered(allItemsAfter("i1132"));
    }

    @Test
    public void testEntryIntoItem3() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        final TestPlanNode itemNode = testSessionController.advanceItemLinear(item2EndTimestamp);
        Assert.assertEquals(getTestNode("i1133"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta + item2EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item2EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPartNodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i1133"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPartOpen();
        Assert.assertEquals(item1EndDelta + item2EndDelta, testPartSessionState.getDurationAccumulated());
        Assert.assertEquals(item2EndTimestamp, testPartSessionState.getDurationIntervalStartTime());

        /* Check state of sections we should and should not have entered */
        assertAssessmentSectionOpen("s11", testPartEntryTimestamp);
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
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.advanceItemLinear(item2EndTimestamp);
        final TestPlanNode itemNode = testSessionController.advanceItemLinear(item3EndTimestamp);
        Assert.assertEquals(getTestNode("i1211"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta + item2EndDelta + item3EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item3EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPartNodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i1211"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPartOpen();
        Assert.assertEquals(item1EndDelta + item2EndDelta + item3EndDelta, testPartSessionState.getDurationAccumulated());
        Assert.assertEquals(item3EndTimestamp, testPartSessionState.getDurationIntervalStartTime());

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
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.advanceItemLinear(item2EndTimestamp);
        testSessionController.advanceItemLinear(item3EndTimestamp);
        final TestPlanNode itemNode = testSessionController.advanceItemLinear(item4EndTimestamp);
        Assert.assertEquals(getTestNode("i123"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item4EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPartNodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i123"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPartOpen();
        Assert.assertEquals(item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta, testPartSessionState.getDurationAccumulated());
        Assert.assertEquals(item4EndTimestamp, testPartSessionState.getDurationIntervalStartTime());

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
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.advanceItemLinear(item2EndTimestamp);
        testSessionController.advanceItemLinear(item3EndTimestamp);
        testSessionController.advanceItemLinear(item4EndTimestamp);
        final TestPlanNode itemNode = testSessionController.advanceItemLinear(item5EndTimestamp);

        /* This should have ended the testPart */
        Assert.assertNull(itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta + item5EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item5EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPartNodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPartEnded(item5EndTimestamp);
        Assert.assertEquals(item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta + item5EndDelta, testPartSessionState.getDurationAccumulated());
        Assert.assertNull(testPartSessionState.getDurationIntervalStartTime());

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
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.endCurrentTestPart(null);
    }

    @Test
    public void testEndTestPartExplicitlyAfterEntry() {
        final long testPartEndDelta = 64000L;
        final Date testPartEndTimestamp = ObjectUtilities.addToTime(testPartEntryTimestamp, testPartEndDelta);
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.endCurrentTestPart(testPartEndTimestamp);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + testPartEndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPartEndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPartNodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());
        Assert.assertNull(testSessionController.findNextEnterableTestPart());

        /* Check state on testPart */
        assertTestPartEnded(testPartEndTimestamp);
        Assert.assertEquals(testPartEndDelta, testPartSessionState.getDurationAccumulated());
        Assert.assertNull(testPartSessionState.getDurationIntervalStartTime());

        /* Check state of sections either entered or failed at */
        assertAssessmentSectionNowEnded("s11", testPartEndTimestamp);
        assertAssessmentSectionNowEnded("s111", testPartEndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s112", testPartEndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s113", testPartEndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s121", testPartEndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s12", testPartEndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s121", testPartEndTimestamp);

        /* Check state of items */
        assertItemNowEnded("i1111", testPartEndTimestamp);
        assertItemEndedButNotEntered("i1112", testPartEndTimestamp);
        assertItemEndedButNotEntered("i1131", testPartEndTimestamp);
        assertItemEndedButNotEntered("i1132", testPartEndTimestamp);
        assertItemEndedButNotEntered("i1133", testPartEndTimestamp);
        assertItemEndedButNotEntered("i1211", testPartEndTimestamp);
        assertItemEndedButNotEntered("i122", testPartEndTimestamp);
        assertItemEndedButNotEntered("i123", testPartEndTimestamp);
        assertItemEndedButNotEntered("i124", testPartEndTimestamp);
    }

    @Test
    public void testExitTestPartExplicitlyAfterEntry() {
        final long testPartEndDelta = 64000L;
        final Date testPartEndTimestamp = ObjectUtilities.addToTime(testPartEntryTimestamp, testPartEndDelta);
        final long testPartExitDelta = 128000L;
        final Date testPartExitTimestamp = ObjectUtilities.addToTime(testPartEndTimestamp, testPartExitDelta);
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.endCurrentTestPart(testPartEndTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartExitTimestamp);

        /* Check state on test */
        assertTestNowEnded(testPartExitTimestamp);
        Assert.assertEquals(testPartEntryDelta + testPartEndDelta + testPartExitDelta, testSessionState.getDurationAccumulated());
        Assert.assertNull(testPartSessionState.getDurationIntervalStartTime());
        Assert.assertNull(testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPartNowExited(testPartExitTimestamp);
        Assert.assertEquals(testPartEndDelta, testPartSessionState.getDurationAccumulated());
        Assert.assertNull(testPartSessionState.getDurationIntervalStartTime());

        /* Check state of sections either entered or failed at */
        assertAssessmentSectionNowExited("s11", testPartExitTimestamp);
        assertAssessmentSectionNowExited("s111", testPartExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s112", testPartExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s113", testPartExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s121", testPartExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s12", testPartExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s121", testPartExitTimestamp);

        /* Check state of items */
        assertItemNowExited("i1111", testPartExitTimestamp);
        assertItemExitedButNotEntered("i1112", testPartExitTimestamp);
        assertItemExitedButNotEntered("i1131", testPartExitTimestamp);
        assertItemExitedButNotEntered("i1132", testPartExitTimestamp);
        assertItemExitedButNotEntered("i1133", testPartExitTimestamp);
        assertItemExitedButNotEntered("i1211", testPartExitTimestamp);
        assertItemExitedButNotEntered("i122", testPartExitTimestamp);
        assertItemExitedButNotEntered("i123", testPartExitTimestamp);
        assertItemExitedButNotEntered("i124", testPartExitTimestamp);
    }

    @Test
    public void testEndTestPartExplicitlyAfterItem1() {
        final long testPartEndDelta = 64000L;
        final Date testPartEndTimestamp = ObjectUtilities.addToTime(item1EndTimestamp, testPartEndDelta);
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.endCurrentTestPart(testPartEndTimestamp);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta + testPartEndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPartEndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestPartNodeKey(), testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());
        Assert.assertNull(testSessionController.findNextEnterableTestPart());

        /* Check state on testPart */
        assertTestPartEnded(testPartEndTimestamp);
        Assert.assertEquals(item1EndDelta + testPartEndDelta, testPartSessionState.getDurationAccumulated());
        Assert.assertNull(testPartSessionState.getDurationIntervalStartTime());

        /* Check state of sections either entered or failed at */
        assertAssessmentSectionNowEnded("s11", testPartEndTimestamp);
        assertAssessmentSectionNowEnded("s111", item1EndTimestamp);
        assertAssessmentSectionFailedPreconditionAndNotExited("s112");
        assertAssessmentSectionNowEnded("s113", testPartEndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s121", testPartEndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s12", testPartEndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s121", testPartEndTimestamp);

        /* Check state of items */
        assertItemNowEnded("i1111", item1EndTimestamp);
        assertItemFailedPreconditionAndNotExited("i1112");
        assertItemFailedPreconditionAndNotExited("i1131");
        assertItemNowEnded("i1132", testPartEndTimestamp);
        assertItemEndedButNotEntered("i1133", testPartEndTimestamp);
        assertItemEndedButNotEntered("i1211", testPartEndTimestamp);
        assertItemEndedButNotEntered("i122", testPartEndTimestamp);
        assertItemEndedButNotEntered("i123", testPartEndTimestamp);
        assertItemEndedButNotEntered("i124", testPartEndTimestamp);
    }

    @Test
    public void testExitTestPartExplicitlyAfterItem1() {
        final long testPartEndDelta = 64000L;
        final Date testPartEndTimestamp = ObjectUtilities.addToTime(item1EndTimestamp, testPartEndDelta);
        final long testPartExitDelta = 128000L;
        final Date testPartExitTimestamp = ObjectUtilities.addToTime(testPartEndTimestamp, testPartExitDelta);
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.advanceItemLinear(item1EndTimestamp);
        testSessionController.endCurrentTestPart(testPartEndTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartExitTimestamp);

        /* Check state on test */
        assertTestNowEnded(testPartExitTimestamp);
        Assert.assertEquals(testPartEntryDelta + item1EndDelta + testPartEndDelta + testPartExitDelta, testSessionState.getDurationAccumulated());
        Assert.assertNull(testPartSessionState.getDurationIntervalStartTime());
        Assert.assertNull(testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPartNowExited(testPartExitTimestamp);
        Assert.assertEquals(item1EndDelta + testPartEndDelta, testPartSessionState.getDurationAccumulated());
        Assert.assertNull(testPartSessionState.getDurationIntervalStartTime());

        /* Check final state of sections */
        assertAssessmentSectionNowExited("s11", testPartExitTimestamp);
        assertAssessmentSectionNowExited("s111", testPartExitTimestamp);
        assertAssessmentSectionFailedPreconditionAndExited("s112", testPartExitTimestamp);
        assertAssessmentSectionNowExited("s113", testPartExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s121", testPartExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s12", testPartExitTimestamp);
        assertAssessmentSectionExitedButNotEntered("s121", testPartExitTimestamp);

        /* Check state of items */
        assertItemNowExited("i1111", testPartExitTimestamp);
        assertItemFailedPreconditionAndExited("i1112", testPartExitTimestamp);
        assertItemFailedPreconditionAndExited("i1131", testPartExitTimestamp);
        assertItemNowExited("i1132", testPartExitTimestamp);
        assertItemExitedButNotEntered("i1133", testPartExitTimestamp);
        assertItemExitedButNotEntered("i1211", testPartExitTimestamp);
        assertItemExitedButNotEntered("i122", testPartExitTimestamp);
        assertItemExitedButNotEntered("i123", testPartExitTimestamp);
        assertItemExitedButNotEntered("i124", testPartExitTimestamp);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testExitTestPartBeforeEnded() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.exitTest(testPartEntryTimestamp);
    }
}
