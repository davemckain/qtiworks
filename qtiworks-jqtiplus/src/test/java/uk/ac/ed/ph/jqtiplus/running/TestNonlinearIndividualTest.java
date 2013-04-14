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
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Basic test of nonlinear/individual, focusing on processing rather than navigation and timing
 *
 * @author David McKain
 */
public final class TestNonlinearIndividualTest extends SinglePartTestBase {

    public static final Identifier ITEM_TP_DONE = Identifier.assumedLegal("TP_DONE");
    public static final Identifier ITEM_RP_DONE = Identifier.assumedLegal("RP_DONE");
    public static final Identifier ITEM_RESPONSE = Identifier.assumedLegal("RESPONSE");
    public static final Identifier ITEM_SCORE = Identifier.assumedLegal("SCORE");
    public static final Identifier TEST_OP_DONE = Identifier.assumedLegal("OP_DONE");
    public static final Identifier TEST_SCORE = Identifier.assumedLegal("TEST_SCORE");

    private static final String TEST_FILE_PATH = "running/simple-nonlinear-individual.xml";

    private static final List<String> TEST_NODES = Arrays.asList(new String[] {
        "p",
            "s",
                "i1",
                "i2",
    });

    private long item1EntryDelta;
    private Date item1EntryTimestamp;

    private ItemSessionState item1SessionState;
    private ItemSessionState item2SessionState;

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
        item1EntryDelta = 2000L;
        item1EntryTimestamp = ObjectUtilities.addToTime(testPartEntryTimestamp, item1EntryDelta);

        item1SessionState = testSessionState.getItemSessionStates().get(getTestNodeKey("i1"));
        item2SessionState = testSessionState.getItemSessionStates().get(getTestNodeKey("i2"));
    }

    //-------------------------------------------------------

    @Test
    public void testBeforeExtra() {
        assertItemTemplateProcessingNotRun();
        assertItemResponseProcessingNotRun();
        assertOutcomeProcessingNotRun();
    }

    @Test
    public void testItemStateOnTestEntry() {
        testSessionController.enterTest(testEntryTimestamp);

        assertItemTemplateProcessingNotRun();
        assertItemResponseProcessingNotRun();
        assertOutcomeProcessingNotRun();
    }

    @Test
    public void testEntryIntoTestPart() {
        testSessionController.enterTest(testEntryTimestamp);

        /* Enter first test part */
        final TestPlanNode testPartNode = testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        Assert.assertEquals(getTestPartNode(), testPartNode);

        /* Template processing should have run in both items */
        assertItemTemplateProcessingRun();

        /* RP & OP shouldn't have happened */
        assertItemResponseProcessingNotRun();
        assertOutcomeProcessingNotRun();
    }

    @Test
    public void testSelectAndRespondItem1Correctly() {
        final long responseDelta = 4000L;
        final Date responseTimestamp = ObjectUtilities.addToTime(item1EntryTimestamp, responseDelta);

        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i1"));

        /* Answer item 1 correctly */
        handleChoiceResponse(responseTimestamp, "ChoiceA");

        /* RP should have happened on item 1 but not 2. OP should have happened */
        assertItemResponseProcessingRun(item1SessionState);
        assertItemResponseProcessingNotRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertItemScore(item1SessionState, 1.0);
        assertItemScore(item2SessionState, 0.0);
        assertTestScore(1.0);
    }

    @Test
    public void testSelectAndRespondItem1Invalid() {
        final long responseDelta = 4000L;
        final Date responseTimestamp = ObjectUtilities.addToTime(item1EntryTimestamp, responseDelta);

        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i1"));

        /* Answer item 1 invalidly */
        handleChoiceResponse(responseTimestamp, "Invalid");

        /* RP would not have happened in this case */
        assertItemResponseProcessingNotRun(item1SessionState);
        assertItemResponseProcessingNotRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertItemScore(item1SessionState, 0.0);
        assertItemScore(item2SessionState, 0.0);
        assertTestScore(0.0);
    }

    @Test
    public void testSelectAndRespondItem1Wrongly() {
        final long responseDelta = 4000L;
        final Date responseTimestamp = ObjectUtilities.addToTime(item1EntryTimestamp, responseDelta);

        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i1"));

        /* Answer item 1 wrongly */
        handleChoiceResponse(responseTimestamp, "ChoiceB");

        /* RP should have happened on item 1 but not 2. OP should have happened */
        assertItemResponseProcessingRun(item1SessionState);
        assertItemResponseProcessingNotRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertItemScore(item1SessionState, 0.0);
        assertItemScore(item2SessionState, 0.0);
        assertTestScore(0.0);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testSelectAndRespondItem1ThenAgain() {
        final long responseDelta = 4000L;
        final Date responseTimestamp = ObjectUtilities.addToTime(item1EntryTimestamp, responseDelta);

        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i1"));

        /* Answer item 1 correctly */
        handleChoiceResponse(responseTimestamp, "ChoiceA");

        /* Item session should have ended */
        Assert.assertFalse(testSessionController.maySubmitResponsesToCurrentItem());
        handleChoiceResponse(responseTimestamp, "ChoiceA");
    }

    @Test
    public void testSelectAndRespondItem1ThenItem2Correctly() {
        final long response1Delta = 4000L;
        final Date response1Timestamp = ObjectUtilities.addToTime(item1EntryTimestamp, response1Delta);
        final long item2EntryDelta = 8000L;
        final Date item2EntryTimestamp = ObjectUtilities.addToTime(response1Timestamp, item2EntryDelta);
        final long response2Delta = 16000L;
        final Date response2Timestamp = ObjectUtilities.addToTime(item2EntryTimestamp, response2Delta);

        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i1"));
        handleChoiceResponse(response1Timestamp, "ChoiceA");
        testSessionController.selectItemNonlinear(item2EntryTimestamp, getTestNodeKey("i2"));
        handleChoiceResponse(response2Timestamp, "ChoiceA");

        /* RP should have happened on both items; OP should have run */
        assertItemResponseProcessingRun(item1SessionState);
        assertItemResponseProcessingRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertItemScore(item1SessionState, 1.0);
        assertItemScore(item2SessionState, 1.0);
        assertTestScore(2.0);
    }

    @Test
    public void testSelectAndRespondItem1ThenItem2Mixed() {
        final long response1Delta = 4000L;
        final Date response1Timestamp = ObjectUtilities.addToTime(item1EntryTimestamp, response1Delta);
        final long item2EntryDelta = 8000L;
        final Date item2EntryTimestamp = ObjectUtilities.addToTime(response1Timestamp, item2EntryDelta);
        final long response2Delta = 16000L;
        final Date response2Timestamp = ObjectUtilities.addToTime(item2EntryTimestamp, response2Delta);

        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i1"));
        handleChoiceResponse(response1Timestamp, "ChoiceA");
        testSessionController.selectItemNonlinear(item2EntryTimestamp, getTestNodeKey("i2"));
        handleChoiceResponse(response2Timestamp, "ChoiceB");

        /* RP should have happened on both items; OP should have run */
        assertItemResponseProcessingRun(item1SessionState);
        assertItemResponseProcessingRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertItemScore(item1SessionState, 1.0);
        assertItemScore(item2SessionState, 0.0);
        assertTestScore(1.0);
    }

    @Test
    public void testSelectItem1ThenEnd() {
        final long response1Delta = 4000L;
        final Date response1Timestamp = ObjectUtilities.addToTime(item1EntryTimestamp, response1Delta);
        final long item2EntryDelta = 8000L;
        final Date item2EntryTimestamp = ObjectUtilities.addToTime(response1Timestamp, item2EntryDelta);
        final long endTestPartDelta = 16000L;
        final Date endTestPartTimestamp = ObjectUtilities.addToTime(item2EntryTimestamp, endTestPartDelta);

        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(item1EntryTimestamp, getTestNodeKey("i1"));
        testSessionController.endCurrentTestPart(endTestPartTimestamp);

        /* RP should not have happened on either item, OP should not have run */
        assertItemResponseProcessingNotRun();
        assertOutcomeProcessingNotRun();
    }

    //-------------------------------------------------------

    protected void assertOutcomeProcessingRun() {
        Assert.assertEquals(BooleanValue.TRUE, testSessionState.getOutcomeValue(TEST_OP_DONE));
    }

    protected void assertOutcomeProcessingNotRun() {
        Assert.assertEquals(BooleanValue.FALSE, testSessionState.getOutcomeValue(TEST_OP_DONE));
    }


    protected void assertItemTemplateProcessingRun() {
        assertItemTemplateProcessingRun(item1SessionState);
        assertItemTemplateProcessingRun(item2SessionState);
    }

    protected void assertItemTemplateProcessingNotRun() {
        assertItemTemplateProcessingNotRun(item1SessionState);
        assertItemTemplateProcessingNotRun(item2SessionState);
    }

    protected void assertItemTemplateProcessingNotRun(final ItemSessionState itemSessionState) {
        Assert.assertEquals(BooleanValue.FALSE, itemSessionState.getTemplateValue(ITEM_TP_DONE));
    }

    protected void assertItemTemplateProcessingRun(final ItemSessionState itemSessionState) {
        Assert.assertEquals(BooleanValue.TRUE, itemSessionState.getTemplateValue(ITEM_TP_DONE));
    }

    protected void assertItemResponseProcessingRun() {
        assertItemResponseProcessingRun(item1SessionState);
        assertItemResponseProcessingRun(item2SessionState);
    }

    protected void assertItemResponseProcessingNotRun() {
        assertItemResponseProcessingNotRun(item1SessionState);
        assertItemResponseProcessingNotRun(item2SessionState);
    }

    protected void assertItemResponseProcessingNotRun(final ItemSessionState itemSessionState) {
        Assert.assertEquals(BooleanValue.FALSE, itemSessionState.getOutcomeValue(ITEM_RP_DONE));
    }

    protected void assertItemResponseProcessingRun(final ItemSessionState itemSessionState) {
        Assert.assertEquals(BooleanValue.TRUE, itemSessionState.getOutcomeValue(ITEM_RP_DONE));
    }

    protected void assertItemScore(final ItemSessionState itemSessionState, final Double expected) {
        RunAssertions.assertValueEqualsDouble(expected, itemSessionState.getOutcomeValue(ITEM_SCORE));
    }

    protected void assertTestScore(final Double expected) {
        RunAssertions.assertValueEqualsDouble(expected, testSessionState.getOutcomeValue(TEST_SCORE));
    }

    protected void handleChoiceResponse(final Date timestamp, final String choiceIdentifier) {
        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        responseMap.put(ITEM_RESPONSE, new StringResponseData(choiceIdentifier));
        testSessionController.handleResponsesToCurrentItem(timestamp, responseMap);
    }

}
