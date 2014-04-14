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
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Base for some simple processing tests of N/I, N/S, L/I and L/S
 *
 * @author David McKain
 */
public abstract class SimpleProcessingTestBase extends SinglePartTestBase {

    public static final Identifier TEST_OP_DONE = Identifier.assumedLegal("OP_DONE");
    public static final Identifier TEST_SCORE = Identifier.assumedLegal("TEST_SCORE");

    protected static final List<String> TEST_NODES = Arrays.asList(new String[] {
        "p",
            "s",
                "i1",
                "i2",
    });

    protected Date operationTimestamp;

    protected ItemSessionState item1SessionState;
    protected ItemSessionState item2SessionState;

    @Override
    protected List<String> testNodes() {
        return TEST_NODES;
    }

    @Before
    public void before() {
        operationTimestamp = ObjectUtilities.addToTime(testPartEntryTimestamp, 2000L);

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

    //-------------------------------------------------------

    protected void assertOutcomeProcessingRun() {
        Assert.assertEquals(BooleanValue.TRUE, testSessionState.getOutcomeValue(TEST_OP_DONE));
    }

    protected void assertOutcomeProcessingNotRun() {
        Assert.assertEquals(BooleanValue.FALSE, testSessionState.getOutcomeValue(TEST_OP_DONE));
    }


    protected void assertItemTemplateProcessingRun() {
        assertChoiceItemTemplateProcessingRun(item1SessionState);
        assertChoiceItemTemplateProcessingRun(item2SessionState);
    }

    protected void assertItemTemplateProcessingNotRun() {
        assertChoiceItemTemplateProcessingNotRun(item1SessionState);
        assertChoiceItemTemplateProcessingNotRun(item2SessionState);
    }

    protected void assertItemResponseProcessingRun() {
        assertChoiceItemResponseProcessingRun(item1SessionState);
        assertChoiceItemResponseProcessingRun(item2SessionState);
    }

    protected void assertItemResponseProcessingNotRun() {
        assertChoiceItemResponseProcessingNotRun(item1SessionState);
        assertChoiceItemResponseProcessingNotRun(item2SessionState);
    }

    protected void assertTestScore(final Double expected) {
        RunAssertions.assertValueEqualsDouble(expected, testSessionState.getOutcomeValue(TEST_SCORE));
    }

    protected void handleChoiceResponse(final String choiceIdentifier) {
        handleChoiceResponse(operationTimestamp, choiceIdentifier);
    }
}