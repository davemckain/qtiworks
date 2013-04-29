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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import uk.ac.ed.ph.jqtiplus.exception.QtiCandidateStateException;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.marshalling.ItemSessionStateXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.types.FileResponseData;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests the {@link ItemSessionController} running the standard
 * <code>choice.xml</code> item
 *
 * @author David McKain
 */
public final class ChoiceItemRunningTest {

    public static final String TEST_FILE_PATH = "running/choice.xml";

    public static final Identifier TP_DONE = Identifier.assumedLegal("TP_DONE");
    public static final Identifier RESPONSE = Identifier.assumedLegal("RESPONSE");
    public static final Identifier RP_DONE = Identifier.assumedLegal("RP_DONE");
    public static final Identifier SCORE = Identifier.assumedLegal("SCORE");

    private ItemSessionController itemSessionController;
    private ItemSessionState itemSessionState;

    private Date initTimestamp;
    private long templateProcessingDelta;
    private Date templateProcessingTimestamp;
    private long entryDelta;
    private Date entryTimestamp;
    private long bindDelta;
    private Date bindTimestamp;
    private long commitDelta;
    private Date commitTimestamp;
    private long rpDelta;
    private Date rpTimestamp;

    @Before
    public void before() {
        initTimestamp = new Date();
        templateProcessingDelta = 1000L;
        templateProcessingTimestamp = ObjectUtilities.addToTime(initTimestamp, templateProcessingDelta);
        entryDelta = 2000L;
        entryTimestamp = ObjectUtilities.addToTime(templateProcessingTimestamp, entryDelta);
        bindDelta = 4000L;
        bindTimestamp = ObjectUtilities.addToTime(entryTimestamp, bindDelta);
        commitDelta = 8000L;
        commitTimestamp = ObjectUtilities.addToTime(bindTimestamp, commitDelta);
        rpDelta = 16000L;
        rpTimestamp = ObjectUtilities.addToTime(commitTimestamp, rpDelta);

        itemSessionController = UnitTestHelper.loadUnitTestAssessmentItemForControl(TEST_FILE_PATH, true);
        itemSessionController.initialize(initTimestamp);
        itemSessionState = itemSessionController.getItemSessionState();
    }

    @After
    public void after() {
        /* This is strictly outside what we're testing here, but let's just check that the
         * state -> XML -> state process is idempotent in this instance
         */
        final Document itemSessionStateXmlDocument = ItemSessionStateXmlMarshaller.marshal(itemSessionState);
        final ItemSessionState refried = ItemSessionStateXmlMarshaller.unmarshal(itemSessionStateXmlDocument.getDocumentElement());
        if (!refried.equals(itemSessionState)) {
            System.err.println("State before marshalling: " + ObjectDumper.dumpObject(itemSessionState));
            System.err.println("State after marshalling: " + ObjectDumper.dumpObject(refried));
            Assert.assertEquals(itemSessionState, refried);
        }
    }

    @Test
    public void testBefore() {
        assertItemNotEntered();
        assertEquals(0.0, itemSessionState.computeDuration(), 0);
        assertNull(itemSessionState.getDurationIntervalStartTime());
        assertEquals(SessionStatus.INITIAL, itemSessionState.getSessionStatus());
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingNotDone();
        assertChoiceResponseValue(null);
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    //-------------------------------------------------------

    @Test
    public void testTemplateProcessingGood() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);

        assertItemNotEntered();
        assertEquals(0.0, itemSessionState.computeDuration(), 0);
        assertNull(itemSessionState.getDurationIntervalStartTime());
        assertEquals(SessionStatus.INITIAL, itemSessionState.getSessionStatus());
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone(); /* (Set during TP) */
        assertNoUncommittedResponseValue();
        assertChoiceResponseValue(null);
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testTemplateProcessingAfterEntry() {
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.performTemplateProcessing(entryTimestamp);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTemplateProcessingNull() {
        itemSessionController.performTemplateProcessing(null);
    }

    //-------------------------------------------------------

    @Test
    public void testEntryIntoItem() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);

        assertItemOpen();
        assertEquals(0L, itemSessionState.getDurationAccumulated());
        assertEquals(entryTimestamp, itemSessionState.getDurationIntervalStartTime());
        assertEquals(SessionStatus.PENDING_SUBMISSION, itemSessionState.getSessionStatus());
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertNoUncommittedResponseValue();
        assertChoiceResponseBound();
        assertChoiceResponseValue(null);
        assertChoiceResponseValid();
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEntryNull() {
        itemSessionController.enterItem(null);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testEntryAfterEntry() {
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.enterItem(entryTimestamp);
    }

    //-------------------------------------------------------

    @Test
    public void testTouchDurationGood() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);

        final long touchDelta = 2000L;
        final Date touchTimestamp = ObjectUtilities.addToTime(entryTimestamp, touchDelta);
        itemSessionController.touchDuration(touchTimestamp);

        assertItemOpen();
        assertEquals(touchDelta, itemSessionState.getDurationAccumulated());
        assertEquals(touchTimestamp, itemSessionState.getDurationIntervalStartTime());
        assertEquals(SessionStatus.PENDING_SUBMISSION, itemSessionState.getSessionStatus());
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertNoUncommittedResponseValue();
        assertChoiceResponseBound();
        assertChoiceResponseValue(null);
        assertChoiceResponseValid();
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTouchDurationNull() {
        itemSessionController.touchDuration(null);
    }

    public void testTouchDurationAfterEnd() {
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.endItem(entryTimestamp);

        /* Touching duration after end should not change anything */
        final long touchDelta = 2000L;
        final Date touchTimestamp = ObjectUtilities.addToTime(entryTimestamp, touchDelta);
        itemSessionController.touchDuration(touchTimestamp);

        assertEquals(entryTimestamp, itemSessionState.getDurationAccumulated());
        assertEquals(null, itemSessionState.getDurationIntervalStartTime());
    }

    //-------------------------------------------------------

    @Test
    public void testResetHardResetDuration() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);

        /* Save some responses and do RP to check that things get reset */
        bindChoiceResponse("ChoiceA");
        itemSessionController.commitResponses(entryTimestamp);
        itemSessionController.performResponseProcessing(entryTimestamp);

        /* Now do reset */
        final long resetDelta = 2000L;
        final Date resetTimestamp = ObjectUtilities.addToTime(entryTimestamp, resetDelta);
        itemSessionController.resetItemSessionHard(resetTimestamp, true);

        /* Check final state */
        assertItemOpen();
        assertEquals(0L, itemSessionState.getDurationAccumulated());
        assertEquals(resetTimestamp, itemSessionState.getDurationIntervalStartTime());
        assertEquals(SessionStatus.INITIAL, itemSessionState.getSessionStatus());
        Assert.assertTrue(itemSessionState.isInitialized());
        assertScore(0.0);
        assertNoUncommittedResponseValue();
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertChoiceResponseValue(null);
        assertResponseProcessingNotDone();
    }

    @Test
    public void testResetHardKeepDuration() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);

        /* Save some responses and do RP to check that things get reset */
        bindChoiceResponse("ChoiceA");
        itemSessionController.commitResponses(entryTimestamp);
        itemSessionController.performResponseProcessing(entryTimestamp);

        /* Now do reset */
        final long resetDelta = 2000L;
        final Date resetTimestamp = ObjectUtilities.addToTime(entryTimestamp, resetDelta);
        itemSessionController.resetItemSessionHard(resetTimestamp, false);

        /* Check final state */
        assertItemOpen();
        assertEquals(resetDelta, itemSessionState.getDurationAccumulated());
        assertEquals(resetTimestamp, itemSessionState.getDurationIntervalStartTime());
        assertEquals(SessionStatus.INITIAL, itemSessionState.getSessionStatus());
        Assert.assertTrue(itemSessionState.isInitialized());
        assertNoUncommittedResponseValue();
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertChoiceResponseValue(null);
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testResetHardNull() {
        itemSessionController.resetItemSessionHard(null, true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testResetSoftNull() {
        itemSessionController.resetItemSessionSoft(null, true);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testResetNotEntered1() {
        itemSessionController.resetItemSessionHard(entryTimestamp, true);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testResetNotEntered2() {
        itemSessionController.resetItemSessionHard(entryTimestamp, false);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testResetNotEntered3() {
        itemSessionController.resetItemSessionSoft(entryTimestamp, true);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testResetNotEntered4() {
        itemSessionController.resetItemSessionSoft(entryTimestamp, false);
    }

    //-------------------------------------------------------

    @Test
    public void testSuspendItemSessionGood() {
        final long suspendDelta = 2000L;
        final Date suspendTimestamp = ObjectUtilities.addToTime(entryTimestamp, suspendDelta);
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.suspendItemSession(suspendTimestamp);

        assertItemOpen();
        assertNull(itemSessionState.getDurationIntervalStartTime());
        assertEquals(suspendDelta, itemSessionState.getDurationAccumulated());
        assertEquals(SessionStatus.PENDING_SUBMISSION, itemSessionState.getSessionStatus());
        assertTrue(itemSessionState.isInitialized());
        assertTrue(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertNoUncommittedResponseValue();
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertChoiceResponseValue(null);
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSuspendNull() {
        itemSessionController.suspendItemSession(null);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testSuspendNotEntered() {
        itemSessionController.suspendItemSession(initTimestamp);
    }

    //-------------------------------------------------------

    @Test
    public void testUnsuspendItemSessionGood() {
        final long suspendDelta = 2000L;
        final Date suspendTimestamp = ObjectUtilities.addToTime(entryTimestamp, suspendDelta);
        final long unsuspendDelta = 4000L;
        final Date unsuspendTimestamp = ObjectUtilities.addToTime(suspendTimestamp, unsuspendDelta);
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.suspendItemSession(suspendTimestamp);
        itemSessionController.unsuspendItemSession(unsuspendTimestamp);

        assertItemOpen();
        assertEquals(unsuspendTimestamp, itemSessionState.getDurationIntervalStartTime());
        assertEquals(suspendDelta, itemSessionState.getDurationAccumulated()); /* (This won't have increased) */
        assertEquals(SessionStatus.PENDING_SUBMISSION, itemSessionState.getSessionStatus());
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertNoUncommittedResponseValue();
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertChoiceResponseValue(null);
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUnsuspendNull() {
        itemSessionController.unsuspendItemSession(null);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testUnsuspendNotEntered() {
        itemSessionController.suspendItemSession(initTimestamp);
    }

    //-------------------------------------------------------

    @Test
    public void testEndItemSessionGood() {
        final long endDelta = 2000L;
        final Date endTimestamp = ObjectUtilities.addToTime(entryTimestamp, endDelta);
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.endItem(endTimestamp);

        assertItemEnded(endTimestamp);
        assertNull(itemSessionState.getDurationIntervalStartTime());
        assertEquals(endDelta, itemSessionState.getDurationAccumulated());
        assertEquals(SessionStatus.PENDING_SUBMISSION, itemSessionState.getSessionStatus()); /* (This isn't changed) */
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertNoUncommittedResponseValue();
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertChoiceResponseValue(null);
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEndNull() {
        itemSessionController.endItem(null);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testEndAfterEntered() {
        itemSessionController.endItem(initTimestamp);
        itemSessionController.endItem(initTimestamp);
    }

    //-------------------------------------------------------

    @Test
    public void testExitItemSessionGood() {
        final long endDelta = 2000L;
        final Date endTimestamp = ObjectUtilities.addToTime(entryTimestamp, endDelta);
        final long exitDelta = 4000L;
        final Date exitTimestamp = ObjectUtilities.addToTime(endTimestamp, exitDelta);
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.endItem(endTimestamp);
        itemSessionController.exitItem(exitTimestamp);

        assertItemExited(exitTimestamp);
        assertNull(itemSessionState.getDurationIntervalStartTime());
        assertEquals(endDelta, itemSessionState.getDurationAccumulated());
        assertEquals(SessionStatus.PENDING_SUBMISSION, itemSessionState.getSessionStatus()); /* (This isn't changed) */
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertNoUncommittedResponseValue();
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertChoiceResponseValue(null);
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExitNull() {
        itemSessionController.exitItem(null);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testExitNotEntered() {
        itemSessionController.exitItem(initTimestamp);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testExitAfterExited() {
        itemSessionController.exitItem(initTimestamp);
        itemSessionController.exitItem(initTimestamp);
    }

    //-------------------------------------------------------

    @Test
    public void testBindResponsesGood() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);

        final boolean bindSuccess = bindChoiceResponse("ChoiceA");
        assertTrue(bindSuccess);

        assertItemOpen();
        assertEquals(bindTimestamp, itemSessionState.getDurationIntervalStartTime());
        assertEquals(bindDelta, itemSessionState.getDurationAccumulated());
        assertEquals(SessionStatus.PENDING_SUBMISSION, itemSessionState.getSessionStatus()); /* (This does not change yet) */
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertUncommittedResponseValue("ChoiceA");
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertChoiceResponseValue(null);
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBindResponseNull1() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.bindResponses(bindTimestamp, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBindResponseNull2() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.bindResponses(null, new HashMap<Identifier, ResponseData>());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBindResponseUnknownResponseIdentifier() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);

        final HashMap<Identifier, ResponseData> responseDataMap = new HashMap<Identifier, ResponseData>();
        responseDataMap.put(Identifier.assumedLegal("BAD"), new StringResponseData("x"));
        itemSessionController.bindResponses(bindTimestamp, responseDataMap);
    }

    public void testBindResponseBad() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);

        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        responseMap.put(RESPONSE, new FileResponseData(new File("/"), "text/plain", "file"));
        final boolean bindSuccess = itemSessionController.bindResponses(bindTimestamp, responseMap);

        assertFalse(bindSuccess);
        assertItemOpen();
        assertEquals(bindTimestamp, itemSessionState.getDurationIntervalStartTime());
        assertEquals(bindDelta, itemSessionState.getDurationAccumulated());
        assertEquals(SessionStatus.PENDING_SUBMISSION, itemSessionState.getSessionStatus()); /* (This does not change yet) */
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertUncommittedResponseValue("INVALID");
        assertChoiceResponseUnbound();
        assertChoiceResponseInvalid();
        assertChoiceResponseValue(null);
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    public void testBindResponseInvalid() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        final boolean bindSuccess = bindChoiceResponse("INVALID");

        assertFalse(bindSuccess);
        assertItemOpen();
        assertEquals(bindTimestamp, itemSessionState.getDurationIntervalStartTime());
        assertEquals(bindDelta, itemSessionState.getDurationAccumulated());
        assertEquals(SessionStatus.PENDING_SUBMISSION, itemSessionState.getSessionStatus()); /* (This does not change yet) */
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertUncommittedResponseValue("INVALID");
        assertChoiceResponseBound();
        assertChoiceResponseInvalid();
        assertChoiceResponseValue(null);
        assertResponseProcessingNotDone();
        assertScore(0.0);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testBindResponseNotEntered() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        bindChoiceResponse("ChoiceA");
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testBindResponseSuspended() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.suspendItemSession(entryTimestamp);
        bindChoiceResponse("ChoiceA");
    }

    //-------------------------------------------------------

    @Test
    public void testCommitResponsesGood() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        assertTrue(bindChoiceResponse("ChoiceA"));
        itemSessionController.commitResponses(commitTimestamp);

        assertItemOpen();
        assertEquals(commitTimestamp, itemSessionState.getDurationIntervalStartTime());
        assertEquals(bindDelta + commitDelta, itemSessionState.getDurationAccumulated());
        assertEquals(SessionStatus.PENDING_RESPONSE_PROCESSING, itemSessionState.getSessionStatus()); /* Changes now*/
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertNoUncommittedResponseValue();
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertChoiceResponseValue("ChoiceA");
        assertResponseProcessingNotDone();
        assertScore(0.0); /* No RP yet */
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCommitResponseNull() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.bindResponses(bindTimestamp, new HashMap<Identifier, ResponseData>());
        itemSessionController.commitResponses(null);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testCommitResponseSuspended() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        bindChoiceResponse("ChoiceA");
        itemSessionController.suspendItemSession(commitTimestamp);
        itemSessionController.commitResponses(commitTimestamp);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testCommitResponseButNoneBound() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.commitResponses(commitTimestamp);
    }

    //-------------------------------------------------------

    @Test
    public void testResponseProcessingGood() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        assertTrue(bindChoiceResponse("ChoiceA"));
        itemSessionController.commitResponses(commitTimestamp);
        itemSessionController.performResponseProcessing(rpTimestamp);

        assertItemOpen();
        assertEquals(rpTimestamp, itemSessionState.getDurationIntervalStartTime());
        assertEquals(bindDelta + commitDelta + rpDelta, itemSessionState.getDurationAccumulated());
        assertEquals(SessionStatus.FINAL, itemSessionState.getSessionStatus()); /* Changes now*/
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertNoUncommittedResponseValue();
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertChoiceResponseValue("ChoiceA");
        assertResponseProcessingDone();
        assertScore(1.0); /* No RP yet */
    }

    @Test(expected=IllegalArgumentException.class)
    public void testResponseProcessingNull() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.bindResponses(bindTimestamp, new HashMap<Identifier, ResponseData>());
        itemSessionController.commitResponses(commitTimestamp);
        itemSessionController.performResponseProcessing(null);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testResponseProcessingNotEntered() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.performResponseProcessing(rpTimestamp);
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testResponseProcessingSuspended() {
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        bindChoiceResponse("ChoiceA");
        itemSessionController.commitResponses(commitTimestamp);
        itemSessionController.suspendItemSession(commitTimestamp);
        itemSessionController.performResponseProcessing(rpTimestamp);
    }

    @Test
    public void testResponseProcessingNoBind() {
        /* This is currently allowed */
        itemSessionController.performTemplateProcessing(templateProcessingTimestamp);
        itemSessionController.enterItem(entryTimestamp);
        itemSessionController.performResponseProcessing(rpTimestamp);

        assertItemOpen();
        assertEquals(rpTimestamp, itemSessionState.getDurationIntervalStartTime());
        assertEquals(bindDelta + commitDelta + rpDelta, itemSessionState.getDurationAccumulated());
        assertEquals(SessionStatus.FINAL, itemSessionState.getSessionStatus()); /* Changes now*/
        assertTrue(itemSessionState.isInitialized());
        assertFalse(itemSessionState.isSuspended());
        assertTemplateProcessingDone();
        assertNoUncommittedResponseValue();
        assertChoiceResponseBound();
        assertChoiceResponseValid();
        assertChoiceResponseValue(null);
        assertResponseProcessingDone();
        assertScore(0.0);
    }

    //-------------------------------------------------------

    protected boolean bindChoiceResponse(final String choiceIdentifier) {
        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        responseMap.put(RESPONSE, new StringResponseData(choiceIdentifier));
        return itemSessionController.bindResponses(bindTimestamp, responseMap);
    }

    protected void assertItemNotEntered() {
        RunAssertions.assertNotYetEntered(itemSessionState);
    }

    protected void assertItemEnded(final Date timestamp) {
        RunAssertions.assertNowEnded(itemSessionState, timestamp);
    }

    protected void assertItemExited(final Date timestamp) {
        RunAssertions.assertNowExited(itemSessionState, timestamp);
    }

    protected void assertItemOpen() {
        RunAssertions.assertOpen(itemSessionState, entryTimestamp);
    }

    protected void assertTemplateProcessingNotDone() {
        Assert.assertEquals(BooleanValue.FALSE, itemSessionState.getTemplateValue(TP_DONE));
    }

    protected void assertTemplateProcessingDone() {
        Assert.assertEquals(BooleanValue.TRUE, itemSessionState.getTemplateValue(TP_DONE));
    }

    protected void assertNoUncommittedResponseValue() {
        Assert.assertNull(itemSessionState.getUncommittedResponseValue(RESPONSE));
    }

    protected void assertUncommittedResponseValue(final String choiceIdentifier) {
        /* NB: choiceIdentifier should not be null here */
        final Value value = itemSessionState.getUncommittedResponseValue(RESPONSE);
        RunAssertions.assertValueEqualsIdentifier(choiceIdentifier, value);
    }

    protected void assertChoiceResponseValue(final String choiceIdentifier) {
        RunAssertions.assertValueEqualsIdentifier(choiceIdentifier, itemSessionState.getResponseValue(RESPONSE));
    }

    protected void assertResponseProcessingNotDone() {
        assertEquals(BooleanValue.FALSE, itemSessionState.getOutcomeValue(RP_DONE));
    }

    protected void assertResponseProcessingDone() {
        assertEquals(BooleanValue.TRUE, itemSessionState.getOutcomeValue(RP_DONE));
    }

    protected void assertScore(final Double expected) {
        RunAssertions.assertValueEqualsDouble(expected, itemSessionState.getOutcomeValue(SCORE));
    }

    protected void assertChoiceResponseValid() {
        final Set<Identifier> invalidResponseIdentifiers = itemSessionState.getInvalidResponseIdentifiers();
        assertFalse(invalidResponseIdentifiers.contains(RESPONSE));
    }

    protected void assertChoiceResponseInvalid() {
        final Set<Identifier> invalidResponseIdentifiers = itemSessionState.getInvalidResponseIdentifiers();
        assertTrue(invalidResponseIdentifiers.contains(RESPONSE));
    }

    protected void assertChoiceResponseBound() {
        final Set<Identifier> unboundResponseIdentifiers = itemSessionState.getUnboundResponseIdentifiers();
        assertFalse(unboundResponseIdentifiers.contains(RESPONSE));
    }

    protected void assertChoiceResponseUnbound() {
        final Set<Identifier> unboundResponseIdentifiers = itemSessionState.getUnboundResponseIdentifiers();
        assertTrue(unboundResponseIdentifiers.contains(RESPONSE));
    }
}
