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

import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the basic {@link TestProcessingController} class, using one of the sample tests.
 *
 * FIXME: Coverage is currently poor!
 *
 * @author David McKain
 */
public final class TestProcessorControllerTest {

    private static final String TEST_FILE_PATH = "running/simple-nonlinear-individual.xml";

    protected TestProcessingController testProcessingController;
    protected TestSessionState testSessionState;

    @Before
    public void before() {
        testProcessingController = UnitTestHelper.loadUnitTestAssessmentTestForControl(TEST_FILE_PATH, true);
        testSessionState = testProcessingController.getTestSessionState();
    }

    @Test
    public void testEvaluateDuration() {
        /* Set to some silly number */
        final long duration = 1234L;
        testSessionState.setDurationAccumulated(duration);

        /* Check variable lookup gives expected value */
        final Identifier durationIdentifier = Identifier.assumedLegal("duration");
        Assert.assertNotNull(testProcessingController.isValidLocalVariableReference(durationIdentifier));

        final Value lookup = testProcessingController.evaluateVariableValue(durationIdentifier);
        Assert.assertEquals(testSessionState.computeDurationValue(), lookup);
    }
}
