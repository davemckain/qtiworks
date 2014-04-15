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

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ItemProcessingController} when applied to the standard
 * <code>choice.xml</code> item.
 *
 * FIXME: Coverage is currently poor!
 *
 * @author David McKain
 */
public final class ItemProcessorControllerTest {

    public static final String TEST_FILE_PATH = "running/choice.xml";

    public static final Identifier TP_DONE = Identifier.assumedLegal("TP_DONE");
    public static final Identifier RESPONSE = Identifier.assumedLegal("RESPONSE");
    public static final Identifier RP_DONE = Identifier.assumedLegal("RP_DONE");
    public static final Identifier SCORE = Identifier.assumedLegal("SCORE");

    private ItemProcessingController itemProcessingController;
    private ItemSessionState itemSessionState;

    @Before
    public void before() {
        itemProcessingController = UnitTestHelper.loadUnitTestAssessmentItemForControl(TEST_FILE_PATH, true);
        itemSessionState = itemProcessingController.getItemSessionState();
    }

    @Test
    public void testEvaluateNumAttempts() {
        /* Set numAttempts to some silly number in the state Object */
        final int numAttempts = 100;
        itemSessionState.setNumAttempts(numAttempts);

        /* Now make sure that the corresponding variable lookup agrees with what is expected */
        final Value lookup = itemProcessingController.evaluateVariableValue(Identifier.assumedLegal("numAttempts"));
        Assert.assertEquals(itemSessionState.getNumAttemptsValue(), lookup);
    }

    @Test
    public void testEvaluateDuration() {
        /* Set to some silly number */
        final long duration = 1234L;
        itemSessionState.setDurationAccumulated(duration);

        /* Check variable lookup gives expected value */
        final Identifier durationIdentifier = Identifier.assumedLegal("duration");
        Assert.assertNotNull(itemProcessingController.isValidLocalVariableReference(durationIdentifier));

        /* Check variable lookup gives expected value */
        final Value lookup = itemProcessingController.evaluateVariableValue(durationIdentifier);
        Assert.assertEquals(itemSessionState.computeDurationValue(), lookup);
    }

    @Test
    public void testEvaluateCompletionStatus() {
        final String completionStatus = QtiConstants.COMPLETION_STATUS_COMPLETED;
        itemSessionState.setCompletionStatus(completionStatus);

        /* Check variable lookup gives expected value */
        final Value lookup = itemProcessingController.evaluateVariableValue(Identifier.assumedLegal("completionStatus"));
        Assert.assertEquals(itemSessionState.getCompletionStatusValue(), lookup);
    }
}
