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
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * Basic test of linear/simultaneous
 *
 * @author David McKain
 */
public final class TestLinearSimultaneousTest extends SimpleProcessingTestBase {

    @Override
    protected String getTestFilePath() {
        return "running/simple-linear-simultaneous.xml";
    }

    //-------------------------------------------------------

    @Test
    public void testSelectAndAnswerItem1Correctly() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);

        /* Submit item 1 correctly */
        handleChoiceResponse("ChoiceA");

        /* RP & OP should not have happened */
        assertItemResponseProcessingNotRun();
        assertOutcomeProcessingNotRun();
        assertChoiceItemScore(item1SessionState, 0.0);
        assertChoiceItemScore(item2SessionState, 0.0);
        assertTestScore(0.0);
    }

    @Test
    public void testSelectAndRespondItem1ThenEndTestPart() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);

        /* Submit item 1 correctly */
        handleChoiceResponse("ChoiceA");

        /* End testPart now */
        testSessionController.endCurrentTestPart(operationTimestamp);

        /* Check what happened afterwards */
        assertChoiceItemResponseProcessingRun(item1SessionState);
        assertChoiceItemResponseProcessingNotRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertChoiceItemScore(item1SessionState, 1.0);
        assertChoiceItemScore(item2SessionState, 0.0);
        assertTestScore(1.0);
    }

    @Test
    public void testSkipItem1ThenAnswerItem2Correctly() {
        final long item1SkipDelta = 2000L;
        final Date item1SkipTimestamp = ObjectUtilities.addToTime(testPartEntryTimestamp, item1SkipDelta);
        final long item2AnswerDelta = 4000L;
        final Date item2AnswerTimestamp = ObjectUtilities.addToTime(item1SkipTimestamp, item2AnswerDelta);
        final long endTestPartDelta = 8000L;
        final Date endTestPartTimestamp = ObjectUtilities.addToTime(item2AnswerTimestamp, endTestPartDelta);

        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);

        /* We're not going to answer item 1. So advance to item 2 */
        final TestPlanNode item2Node = testSessionController.advanceItemLinear(item1SkipTimestamp);
        Assert.assertEquals(getTestNode("i2"), item2Node);

        /* Now answer item 2 */
        handleChoiceResponse(item2AnswerTimestamp, "ChoiceA");

        /* Now move on, which should end the testPart */
        Assert.assertNull(testSessionController.advanceItemLinear(endTestPartTimestamp));

        /* Check states at this point */
        assertItemNowEnded("i1", endTestPartTimestamp);
        assertItemNowEnded("i2", endTestPartTimestamp);
        assertTestPartEnded(endTestPartTimestamp);

        /* RP & OP should now have happened */
        assertChoiceItemResponseProcessingRun(item1SessionState);
        assertChoiceItemResponseProcessingRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertChoiceItemScore(item1SessionState, 0.0);
        assertChoiceItemScore(item2SessionState, 1.0);
        assertTestScore(1.0);
    }

    @Test
    public void testSkipItem1ThenEndTestPart() {
        final long item1SkipDelta = 2000L;
        final Date item1SkipTimestamp = ObjectUtilities.addToTime(testPartEntryTimestamp, item1SkipDelta);
        final long endTestPartDelta = 4000L;
        final Date endTestPartTimestamp = ObjectUtilities.addToTime(item1SkipTimestamp, endTestPartDelta);

        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);

        /* We're not going to answer item 1. So advance to item 2 */
        final TestPlanNode item2Node = testSessionController.advanceItemLinear(item1SkipTimestamp);
        Assert.assertEquals(getTestNode("i2"), item2Node);

        /* Session for item 1 should be been suspended */
        assertItemSuspended("i1", item1SkipTimestamp);
        assertItemOpen("i2", item1SkipTimestamp);

        /* End testPart */
        testSessionController.endCurrentTestPart(endTestPartTimestamp);
        assertItemNowEnded("i1", endTestPartTimestamp);
        assertItemNowEnded("i2", endTestPartTimestamp);
        assertTestPartEnded(endTestPartTimestamp);

        /* RP & OP should now have happened */
        assertChoiceItemResponseProcessingRun(item1SessionState);
        assertChoiceItemResponseProcessingRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertChoiceItemScore(item1SessionState, 0.0);
        assertChoiceItemScore(item2SessionState, 0.0);
        assertTestScore(0.0);
    }
}
