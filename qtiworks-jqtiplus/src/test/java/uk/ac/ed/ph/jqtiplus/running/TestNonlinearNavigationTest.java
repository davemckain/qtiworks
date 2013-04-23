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
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPartSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests navigation within a test containing 2 {@link TestPart}s
 * having non-linear navigation mode.
 *
 * @author David McKain
 */
public final class TestNonlinearNavigationTest extends TestTestBase {

    private static final String TEST_FILE_PATH = "running/test-nonlinear.xml";

    private static final List<String> TEST_NODES = Arrays.asList(new String[] {
        "p1",
            "s11",
                "s111",
                    "i1111",
                "i112",
        "p2",
            "s21",
                "i211"
    });

    private TestPartSessionState testPart1SessionState;
    private TestPartSessionState testPart2SessionState;

    private long testPart1EntryDelta;
    private Date testPart1EntryTimestamp;
    private long item1EntryDelta;
    private Date item1EntryTimestamp;

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
        testPart1SessionState = testSessionState.getTestPartSessionStates().get(getTestNodeKey("p1"));
        testPart2SessionState = testSessionState.getTestPartSessionStates().get(getTestNodeKey("p2"));

        /* Create times & deltas. We use powers to 2 in deltas to help diagnosis and avoid addition collisions */
        testPart1EntryDelta = 1000L;
        testPart1EntryTimestamp = ObjectUtilities.addToTime(testEntryTimestamp, testPart1EntryDelta);

        item1EntryDelta = 2000L;
        item1EntryTimestamp = ObjectUtilities.addToTime(testPart1EntryTimestamp, item1EntryDelta);
    }

    //-------------------------------------------------------

    @Test
    public void testBefore() {
        RunAssertions.assertNotYetEntered(testSessionState);
        Assert.assertEquals(0.0, testSessionState.computeDuration(), 0);
        Assert.assertNull(testSessionState.getDurationIntervalStartTime());
        Assert.assertNull(testSessionState.getCurrentTestPartKey());
        Assert.assertNull(testSessionState.getCurrentItemKey());
        Assert.assertNotNull(testPart1SessionState);

        /* We won't have entered the first testPart yet */
        RunAssertions.assertNotYetEntered(testPart1SessionState);

        /* We won't have entered any sections yet */
        assertAssessmentSectionsNotEntered(allSections());

        /* We won't have entered any items yet */
        assertItemsNotEntered(allItems());
    }

    @Test
    public void testEntryIntoTest() {
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
        RunAssertions.assertNotYetEntered(testPart1SessionState);
        RunAssertions.assertNotYetEntered(testPart2SessionState);

        /* We won't have entered any sections yet */
        assertAssessmentSectionsNotEntered(allSections());

        /* We won't have entered any items yet */
        assertItemsNotEntered(allItems());
    }

    @Test
    public void testEntryIntoTestPart1() {
        testSessionController.enterTest(testEntryTimestamp);

        /* Enter first test part */
        final TestPlanNode testPart1Node = testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
        Assert.assertEquals(getTestNode("p1"), testPart1Node);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EntryTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(null, testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(0, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EntryTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* We won't have entered any items or sections */
        assertAssessmentSectionsNotEntered(allSections());
        assertItemsNotEntered(allItems());

        /* All items in this part should be selectable */
        assertItemsSelectable("i1111", "i112");
    }

    //-------------------------------------------------------

    private void enterTestPart1() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testMaySelectItemNullKey() {
        enterTestPart1();
        testSessionController.maySelectItemNonlinear(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testMaySelectItemUnknownKey() {
        enterTestPart1();
        testSessionController.maySelectItemNonlinear(new TestPlanNodeKey(Identifier.assumedLegal("unknown"), 1, 1));
    }

    @Test
    public void testMaySelectItemInOtherPart() {
        enterTestPart1();
        Assert.assertFalse(testSessionController.maySelectItemNonlinear(getTestNodeKey("i211")));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSelectItemNullTimestamp() {
        enterTestPart1();
        testSessionController.selectItemNonlinear(null, getTestNodeKey("i1111"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSelectItemUnknownKey() {
        enterTestPart1();
        testSessionController.selectItemNonlinear(item1EntryTimestamp, new TestPlanNodeKey(Identifier.assumedLegal("unknown"), 1, 1));
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testSelectItemInOtherPart() {
        enterTestPart1();
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i211"));
    }

    //-------------------------------------------------------

    @Test
    public void testEntryIntoTestPart1ThenEndImmediately() {
        final long testPart1EndDelta = 4000L;
        final Date testPart1EndTimestamp = ObjectUtilities.addToTime(testPart1EntryTimestamp, testPart1EndDelta);

        enterTestPart1();
        testSessionController.endCurrentTestPart(testPart1EndTimestamp);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + testPart1EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(null, testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Ended(testPart1EndTimestamp);
        Assert.assertEquals(testPart1EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(null, testPart1SessionState.getDurationIntervalStartTime());

        /* We should have ended but not entered all items & sections within part */
        assertAssessmentSectionEndedButNotEntered("s11", testPart1EndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s111", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i1111", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i112", testPart1EndTimestamp);

        /* Nothing should have happened in remaining testPart */
        assertNothingInTestPart2Entered();
    }

    @Test
    public void testEntryIntoTestPart1AndSelectI1111() {
        /* Enter test, enter first test part then select item */
        enterTestPart1();
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i1111"));

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EntryDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item1EntryTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i1111"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(item1EntryDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(item1EntryTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* We should have entered s11 and s111 */
        assertAssessmentSectionOpen("s11", item1EntryTimestamp);
        assertAssessmentSectionOpen("s111", item1EntryTimestamp);

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i1111", item1EntryTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(item1EntryTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Nothing should have happened in remaining testPart */
        assertNothingInTestPart2Entered();
    }

    @Test
    public void testEntryIntoTestPart1ThenSelectI1111ThenDeselect() {
        final long item1DeselectDelta = 4000L;
        final Date item1DeselectTimestamp = ObjectUtilities.addToTime(item1EntryTimestamp, item1DeselectDelta);

        enterTestPart1();
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i1111"));
        testSessionController.selectItemNonlinear(item1DeselectTimestamp, null);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EntryDelta + item1DeselectDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item1DeselectTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(null, testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(item1EntryDelta + item1DeselectDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(item1DeselectTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* We should have entered s11 and s111 */
        assertAssessmentSectionOpen("s11", item1EntryTimestamp);
        assertAssessmentSectionOpen("s111", item1EntryTimestamp);

        /* Check state of item we deselected */
        assertItemSuspended("i1111", item1DeselectTimestamp);

        /* Nothing should have happened in remaining testPart */
        assertNothingInTestPart2Entered();
    }

    @Test
    public void testEntryIntoTestPart1AndSelectI112() {
        /* Enter test, enter first test part then select item */
        enterTestPart1();
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i112"));

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EntryDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item1EntryTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i112"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(item1EntryDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(item1EntryTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* We should have entered s11  */
        assertAssessmentSectionOpen("s11", item1EntryTimestamp);

        /* Check state of item we entered */
        final ItemSessionState itemSessionState = assertItemOpen("i112", item1EntryTimestamp);
        Assert.assertEquals(0, itemSessionState.getDurationAccumulated());
        Assert.assertEquals(item1EntryTimestamp, itemSessionState.getDurationIntervalStartTime());

        /* Nothing should have happened in remaining testPart */
        assertNothingInTestPart2Entered();
    }

    @Test
    public void testEntryIntoTestPart1AndSelectI1111ThenI112() {
        final long item2EntryDelta = 4000L;
        final Date item2EntryTimestamp = ObjectUtilities.addToTime(item1EntryTimestamp, item2EntryDelta);

        /* Perform selections */
        enterTestPart1();
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i1111"));
        testSessionController.selectItemNonlinear(item2EntryTimestamp, getTestNodeKey("i112"));

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EntryDelta + item2EntryDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item2EntryTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(getTestNodeKey("i112"), testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Open();
        Assert.assertEquals(item1EntryDelta + item2EntryDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(item2EntryTimestamp, testPart1SessionState.getDurationIntervalStartTime());

        /* We should have entered s11  */
        assertAssessmentSectionOpen("s11", item1EntryTimestamp);

        /* Check state of items we entered */
        final ItemSessionState item1SessionState = assertItemSuspended("i1111", item2EntryTimestamp);
        Assert.assertEquals(item2EntryDelta, item1SessionState.getDurationAccumulated());
        Assert.assertEquals(null, item1SessionState.getDurationIntervalStartTime());

        final ItemSessionState item2SessionState = assertItemOpen("i112", item2EntryTimestamp);
        Assert.assertEquals(0, item2SessionState.getDurationAccumulated());
        Assert.assertEquals(item2EntryTimestamp, item2SessionState.getDurationIntervalStartTime());

        /* Nothing should have happened in remaining testPart */
        assertNothingInTestPart2Entered();
    }

    @Test
    public void testEntryIntoTestPart1ThenSelectI1111ThenEndTestPart() {
        final long testPart1EndDelta = 4000L;
        final Date testPart1EndTimestamp = ObjectUtilities.addToTime(item1EntryTimestamp, testPart1EndDelta);

        enterTestPart1();
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i1111"));
        testSessionController.endCurrentTestPart(testPart1EndTimestamp);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EntryDelta + testPart1EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(null, testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Ended(testPart1EndTimestamp);
        Assert.assertEquals(item1EntryDelta + testPart1EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(null, testPart1SessionState.getDurationIntervalStartTime());

        /* We should have ended all sections in the first part */
        assertAssessmentSectionNowEnded("s11", testPart1EndTimestamp);
        assertAssessmentSectionNowEnded("s111", testPart1EndTimestamp);

        /* We should have ended all items in the first part */
        assertItemNowEnded("i1111", testPart1EndTimestamp);
        assertItemEndedButNotEntered("i112", testPart1EndTimestamp);

        /* Nothing should have happened in remaining testPart */
        assertNothingInTestPart2Entered();
    }

    @Test
    public void testEntryIntoTestPart1ThenSelectI112ThenEndTestPart() {
        final long testPart1EndDelta = 4000L;
        final Date testPart1EndTimestamp = ObjectUtilities.addToTime(item1EntryTimestamp, testPart1EndDelta);

        enterTestPart1();
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i112"));
        testSessionController.endCurrentTestPart(testPart1EndTimestamp);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + item1EntryDelta + testPart1EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart1EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(null, testSessionState.getCurrentItemKey());

        /* Check state on testPart */
        assertTestPart1Ended(testPart1EndTimestamp);
        Assert.assertEquals(item1EntryDelta + testPart1EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(null, testPart1SessionState.getDurationIntervalStartTime());

        /* We should have ended all sections in the first part */
        assertAssessmentSectionNowEnded("s11", testPart1EndTimestamp);
        assertAssessmentSectionEndedButNotEntered("s111", testPart1EndTimestamp);

        /* We should have ended all items in the first part */
        assertItemEndedButNotEntered("i1111", testPart1EndTimestamp);
        assertItemNowEnded("i112", testPart1EndTimestamp);

        /* Nothing should have happened in remaining testPart */
        assertNothingInTestPart2Entered();
    }

    //-------------------------------------------------------

    @Test(expected=QtiCandidateStateException.class)
    public void testEntryIntoTestPart2ButPart1NotEnded() {
        enterTestPart1();
        testSessionController.enterNextAvailableTestPart(testPart1EntryTimestamp);
    }


    @Test
    public void testEntryIntoTestPart1ThenTestPart2() {
        final long testPart1EndDelta = 4000L;
        final Date testPart1EndTimestamp = ObjectUtilities.addToTime(testPart1EntryTimestamp, testPart1EndDelta);
        final long testPart2EntryDelta = 8000L;
        final Date testPart2EntryTimestamp = ObjectUtilities.addToTime(testPart1EndTimestamp, testPart2EntryDelta);

        enterTestPart1();
        testSessionController.endCurrentTestPart(testPart1EndTimestamp);
        testSessionController.enterNextAvailableTestPart(testPart2EntryTimestamp);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPart1EntryDelta + testPart1EndDelta + testPart2EntryDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPart2EntryTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p2"), testSessionState.getCurrentTestPartKey());
        Assert.assertEquals(null, testSessionState.getCurrentItemKey());

        /* Check state on testPart 1 */
        assertTestPart1Exited(testPart2EntryTimestamp);
        Assert.assertEquals(testPart1EndDelta, testPart1SessionState.getDurationAccumulated());
        Assert.assertEquals(null, testPart1SessionState.getDurationIntervalStartTime());

        /* Check state on testPart 2 */
        assertTestPart2Open(testPart2EntryTimestamp);
        Assert.assertEquals(0L, testPart2SessionState.getDurationAccumulated());
        Assert.assertEquals(testPart2EntryTimestamp, testPart2SessionState.getDurationIntervalStartTime());

        /* We won't have entered any items or sections in this part */
        assertAssessmentSectionsNotEntered(allSectionsAfter("p2"));
        assertItemsNotEntered(allItemsAfter("p2"));

        /* All items in this part should be selectable */
        assertItemsSelectable(allItemsAfter("p2"));
    }

    private void assertNothingInTestPart2Entered() {
        assertTestPart2NotEntered();
        assertAssessmentSectionsNotEntered("s21");
        assertItemsNotEntered("i211");
    }

    //-------------------------------------------------------

    protected void assertTestPart1Open() {
        RunAssertions.assertOpen(testPart1SessionState, testPart1EntryTimestamp);
    }

    protected void assertTestPart1Ended(final Date endTimestamp) {
        RunAssertions.assertNowEnded(testPart1SessionState, endTimestamp);
    }

    protected void assertTestPart1Exited(final Date exitTimestamp) {
        RunAssertions.assertNowExited(testPart1SessionState, exitTimestamp);
    }

    protected void assertTestPart2NotEntered() {
        RunAssertions.assertNotYetEntered(testPart2SessionState);
    }

    protected void assertTestPart2Open(final Date timestamp) {
        RunAssertions.assertOpen(testPart2SessionState, timestamp);
    }
}
