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
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.test.BranchRule;
import uk.ac.ed.ph.jqtiplus.node.test.PreCondition;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.state.AssessmentSectionSessionState;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPartSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.state.marshalling.TestSessionStateXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests navigation through a linear {@link TestPart}
 * containing {@link PreCondition} and {@link BranchRule}s
 *
 * @author David McKain
 */
public final class TestComplexLinearNavigationTest {

    private static final String TEST_FILE_PATH = "running/test-complex-linear.xml";

    private static final String[] TEST_NODES = new String[] {
        "p1",
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
    };

    private Date testEntryTimestamp;
    private TestSessionController testSessionController;
    private TestSessionState testSessionState;
    private TestPlan testPlan;
    private TestPartSessionState testPartSessionState;

    private Map<String, TestPlanNode> testPlanNodesByIdentifierStringMap;

    private long testPartEntryDelta;
    private Date testPartEntryTimestamp;
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

    @Before
    public void before() {
        testEntryTimestamp = new Date();
        testSessionController = UnitTestHelper.loadUnitTestAssessmentTestForControl(TEST_FILE_PATH, true);
        testSessionController.initialize(testEntryTimestamp);
        testSessionState = testSessionController.getTestSessionState();
        testPlan = testSessionState.getTestPlan();

        testPlanNodesByIdentifierStringMap = new HashMap<String, TestPlanNode>();
        for (final String testNodeIdentifierString : TEST_NODES) {
            final TestPlanNode testPlanNode = UnitTestHelper.assertSingleTestPlanNode(testPlan, testNodeIdentifierString);
            testPlanNodesByIdentifierStringMap.put(testNodeIdentifierString, testPlanNode);
        }

        testPartSessionState = testSessionState.getTestPartSessionStates().get(getTestNodeKey("p1"));

        /* Create times & deltas. We use powers to 2 in deltas to help diagnosis and avoid addition collisions */
        testPartEntryDelta = 1000L;
        testPartEntryTimestamp = ObjectUtilities.addToTime(testEntryTimestamp, testPartEntryDelta);

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

    @After
    public void after() {
        /* This is strictly outside what we're testing here, but let's just check that the
         * state -> XML -> state process is idempotent in this instance
         */
        final Document testSessionStateXmlDocument = TestSessionStateXmlMarshaller.marshal(testSessionState);
        final TestSessionState refried = TestSessionStateXmlMarshaller.unmarshal(testSessionStateXmlDocument.getDocumentElement());
        if (!refried.equals(testSessionState)) {
            System.err.println("State before marshalling: " + ObjectDumper.dumpObject(testSessionState));
            System.err.println("State after marshalling: " + ObjectDumper.dumpObject(refried));
            Assert.assertEquals(testSessionState, refried);
        }
    }

    //-------------------------------------------------------

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

    @Test(expected=IllegalArgumentException.class)
    public void testEntryIntoTestPartNullTimestamp() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(null);
    }

    @Test
    public void testEntryIntoTestPartAndItem1() {
        testSessionController.enterTest(testEntryTimestamp);

        /* Enter test part (which should also select first item) */
        final TestPlanNode testPartNode = testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        Assert.assertEquals(getTestNode("p1"), testPartNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPartEntryTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
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
        testSessionController.endItemLinear(null);
    }

    @Test
    public void testEntryIntoItem2() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        final TestPlanNode itemNode = testSessionController.endItemLinear(item1EndTimestamp);
        Assert.assertEquals(getTestNode("i1132"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item1EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
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
        testSessionController.endItemLinear(item1EndTimestamp);
        final TestPlanNode itemNode = testSessionController.endItemLinear(item2EndTimestamp);
        Assert.assertEquals(getTestNode("i1133"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta + item2EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item2EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
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
        testSessionController.endItemLinear(item1EndTimestamp);
        testSessionController.endItemLinear(item2EndTimestamp);
        final TestPlanNode itemNode = testSessionController.endItemLinear(item3EndTimestamp);
        Assert.assertEquals(getTestNode("i1211"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta + item2EndDelta + item3EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item3EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
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
        testSessionController.endItemLinear(item1EndTimestamp);
        testSessionController.endItemLinear(item2EndTimestamp);
        testSessionController.endItemLinear(item3EndTimestamp);
        final TestPlanNode itemNode = testSessionController.endItemLinear(item4EndTimestamp);
        Assert.assertEquals(getTestNode("i123"), itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item4EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
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
        testSessionController.endItemLinear(item1EndTimestamp);
        testSessionController.endItemLinear(item2EndTimestamp);
        testSessionController.endItemLinear(item3EndTimestamp);
        testSessionController.endItemLinear(item4EndTimestamp);
        final TestPlanNode itemNode = testSessionController.endItemLinear(item5EndTimestamp);

        /* This should have ended the testPart */
        Assert.assertNull(itemNode);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta + item2EndDelta + item3EndDelta + item4EndDelta + item5EndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(item5EndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
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
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
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
        testSessionController.endItemLinear(item1EndTimestamp);
        testSessionController.endCurrentTestPart(testPartEndTimestamp);

        /* Check state on test */
        assertTestOpen();
        Assert.assertEquals(testPartEntryDelta + item1EndDelta + testPartEndDelta, testSessionState.getDurationAccumulated());
        Assert.assertEquals(testPartEndTimestamp, testSessionState.getDurationIntervalStartTime());
        Assert.assertEquals(getTestNodeKey("p1"), testSessionState.getCurrentTestPartKey());
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
        testSessionController.endItemLinear(item1EndTimestamp);
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

    //-------------------------------------------------------

    protected List<String> allSections() {
        final List<String> result = new ArrayList<String>();
        for (final String identifier : TEST_NODES) {
            if (identifier.charAt(0)=='s') {
                result.add(identifier);
            }
        }
        return result;
    }

    protected List<String> allSectionsExcept(final String... exclusions) {
        final List<String> exclusionsList = Arrays.asList(exclusions);
        final List<String> result = new ArrayList<String>();
        for (final String identifier : TEST_NODES) {
            if (identifier.charAt(0)=='s' && !exclusionsList.contains(identifier)) {
                result.add(identifier);
            }
        }
        return result;
    }

    protected List<String> allSectionsAfter(final String startIdentifier) {
        final List<String> result = new ArrayList<String>();
        boolean found = false;
        for (final String identifier : TEST_NODES) {
            if (identifier.charAt(0)=='s') {
                if (found) {
                    result.add(identifier);
                }
                else if (startIdentifier.equals(identifier)) {
                    found = true;
                }

            }
        }
        return result;
    }

    protected List<String> allItems() {
        final List<String> result = new ArrayList<String>();
        for (final String identifier : TEST_NODES) {
            if (identifier.charAt(0)=='i') {
                result.add(identifier);
            }
        }
        return result;
    }

    protected List<String> allItemsExcept(final String... exclusions) {
        final List<String> exclusionsList = Arrays.asList(exclusions);
        final List<String> result = new ArrayList<String>();
        for (final String identifier : TEST_NODES) {
            if (identifier.charAt(0)=='i' && !exclusionsList.contains(identifier)) {
                result.add(identifier);
            }
        }
        return result;
    }

    protected List<String> allItemsAfter(final String startIdentifier) {
        final List<String> result = new ArrayList<String>();
        boolean found = false;
        for (final String identifier : TEST_NODES) {
            if (identifier.charAt(0)=='i') {
                if (found) {
                    result.add(identifier);
                }
                else if (startIdentifier.equals(identifier)) {
                    found = true;
                }

            }
        }
        return result;
    }

    protected AssessmentSectionSessionState assertAssessmentSectionState(final String identifier) {
        final AssessmentSectionSessionState result = testSessionState.getAssessmentSectionSessionStates().get(getTestNodeKey(identifier));
        Assert.assertNotNull(result);
        return result;
    }

    protected void assertAssessmentSectionsNotEntered(final Iterable<String> identifiers) {
        for (final String identifier : identifiers) {
            assertAssessmentSectionNotEntered(identifier);
        }
    }

    protected AssessmentSectionSessionState assertAssessmentSectionNotEntered(final String identifier) {
        final AssessmentSectionSessionState result = assertAssessmentSectionState(identifier);
        RunAssertions.assertNotYetEntered(result);
        return result;
    }

    protected AssessmentSectionSessionState assertAssessmentSectionFailedPreconditionAndNotExited(final String identifier) {
        final AssessmentSectionSessionState result = assertAssessmentSectionState(identifier);
        RunAssertions.assertFailedPreconditionAndNotExited(result);
        return result;
    }

    protected AssessmentSectionSessionState assertAssessmentSectionFailedPreconditionAndExited(final String identifier, final Date exitTimestamp) {
        final AssessmentSectionSessionState result = assertAssessmentSectionState(identifier);
        RunAssertions.assertFailedPreconditionAndExited(result, exitTimestamp);
        return result;
    }

    protected AssessmentSectionSessionState assertAssessmentSectionOpen(final String identifier, final Date timestamp) {
        final AssessmentSectionSessionState result = assertAssessmentSectionState(identifier);
        RunAssertions.assertOpen(result, timestamp);
        return result;
    }

    protected AssessmentSectionSessionState assertAssessmentSectionNowEnded(final String identifier, final Date timestamp) {
        final AssessmentSectionSessionState result = assertAssessmentSectionState(identifier);
        RunAssertions.assertNowEnded(result, timestamp);
        return result;
    }

    protected AssessmentSectionSessionState assertAssessmentSectionEndedButNotEntered(final String identifier, final Date endTimestamp) {
        final AssessmentSectionSessionState result = assertAssessmentSectionState(identifier);
        RunAssertions.assertEndedButNotEntered(result, endTimestamp);
        return result;
    }

    protected AssessmentSectionSessionState assertAssessmentSectionNowExited(final String identifier, final Date exitTimestamp) {
        final AssessmentSectionSessionState result = assertAssessmentSectionState(identifier);
        RunAssertions.assertNowExited(result, exitTimestamp);
        return result;
    }

    protected AssessmentSectionSessionState assertAssessmentSectionExitedButNotEntered(final String identifier, final Date exitTimestamp) {
        final AssessmentSectionSessionState result = assertAssessmentSectionState(identifier);
        RunAssertions.assertExitedButNotEntered(result, exitTimestamp);
        return result;
    }

    protected ItemSessionState assertItemSessionState(final String identifier) {
        final ItemSessionState itemSessionState = testSessionState.getItemSessionStates().get(getTestNodeKey(identifier));
        Assert.assertNotNull(itemSessionState);
        return itemSessionState;
    }

    protected void assertItemsNotEntered(final Iterable<String> identifiers) {
        for (final String identifier : identifiers) {
            assertItemNotEntered(identifier);
        }
    }

    protected void assertItemsNotEntered(final String... identifiers) {
        for (final String identifier : identifiers) {
            assertItemNotEntered(identifier);
        }
    }

    protected ItemSessionState assertItemNotEntered(final String identifier) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertNotYetEntered(itemSessionState);
        return itemSessionState;
    }

    protected ItemSessionState assertItemOpen(final String identifier, final Date timestamp) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertOpen(itemSessionState, timestamp);
        return itemSessionState;
    }

    protected ItemSessionState assertItemNowEnded(final String identifier, final Date timestamp) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertNowEnded(itemSessionState, timestamp);
        return itemSessionState;
    }

    protected ItemSessionState assertItemNowExited(final String identifier, final Date timestamp) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertNowExited(itemSessionState, timestamp);
        return itemSessionState;
    }

    protected ItemSessionState assertItemEndedButNotEntered(final String identifier, final Date timestamp) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertEndedButNotEntered(itemSessionState, timestamp);
        return itemSessionState;
    }

    protected ItemSessionState assertItemExitedButNotEntered(final String identifier, final Date timestamp) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertExitedButNotEntered(itemSessionState, timestamp);
        return itemSessionState;
    }

    protected ItemSessionState assertItemFailedPreconditionAndNotExited(final String identifier) {
        final ItemSessionState result = assertItemSessionState(identifier);
        RunAssertions.assertFailedPreconditionAndNotExited(result);
        return result;
    }

    protected ItemSessionState assertItemFailedPreconditionAndExited(final String identifier, final Date exitTimestamp) {
        final ItemSessionState result = assertItemSessionState(identifier);
        RunAssertions.assertFailedPreconditionAndExited(result, exitTimestamp);
        return result;
    }

    protected void assertTestOpen() {
        RunAssertions.assertOpen(testSessionState, testEntryTimestamp);
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

    protected void assertTestNowEnded(final Date timestamp) {
        RunAssertions.assertNowEnded(testSessionState, timestamp);
    }

    protected void assertTestNowExited(final Date timestamp) {
        RunAssertions.assertNowExited(testSessionState, timestamp);
    }

    protected TestPlanNode getTestNode(final String identifier) {
        return testPlanNodesByIdentifierStringMap.get(identifier);
    }

    protected TestPlanNodeKey getTestNodeKey(final String identifier) {
        return getTestNode(identifier).getKey();
    }
}
