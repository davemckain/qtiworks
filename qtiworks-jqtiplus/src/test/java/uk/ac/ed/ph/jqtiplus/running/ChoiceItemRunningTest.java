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

import uk.ac.ed.ph.jqtiplus.exception.QtiCandidateStateException;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.marshalling.ItemSessionStateXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

import java.util.Date;

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

    public static final Identifier SCORE = Identifier.assumedLegal("SCORE");
    public static final Identifier RESPONSE = Identifier.assumedLegal("RESPONSE");

    private ItemSessionController itemSessionController;
    private ItemSessionState itemSessionState;

    private Date itemEntryTimestamp;

    @Before
    public void before() {
        itemEntryTimestamp = new Date();
        itemSessionController = UnitTestHelper.loadUnitTestAssessmentItemForControl(TEST_FILE_PATH, true);
        itemSessionController.initialize(itemEntryTimestamp);
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
        assertFalse(itemSessionState.isEntered());
        assertFalse(itemSessionState.isEnded());
        assertFalse(itemSessionState.isExited());
        assertEquals(0.0, itemSessionState.computeDuration(), 0);
        assertNull(itemSessionState.getDurationIntervalStartTime());
        assertEquals(SessionStatus.INITIAL, itemSessionState.getSessionStatus());
        Assert.assertTrue(itemSessionState.isInitialized());
        assertScore(0.0);
    }

    @Test
    public void testTemplateProcessingGood() {
        /* (This doesn't actually do anything here) */
        itemSessionController.performTemplateProcessing(itemEntryTimestamp);
        testBefore();
    }

    @Test(expected=QtiCandidateStateException.class)
    public void testTemplateProcessingAfterEntry() {
        itemSessionController.enterItem(itemEntryTimestamp);
        itemSessionController.performTemplateProcessing(itemEntryTimestamp);
    }

    @Test
    public void testEntryIntoItem() {
        /* (No TP done here as it's not actually required) */
        itemSessionController.enterItem(itemEntryTimestamp);

        assertItemOpen();
        assertEquals(0L, itemSessionState.getDurationAccumulated());
        assertEquals(itemEntryTimestamp, itemSessionState.getDurationIntervalStartTime());
    }

    //-------------------------------------------------------

    protected void assertItemOpen() {
        RunAssertions.assertOpen(itemSessionState, itemEntryTimestamp);
    }

    protected void assertScore(final double expected) {
        RunAssertions.assertValueEquals(expected, itemSessionState.getOutcomeValue(SCORE));
    }
}
