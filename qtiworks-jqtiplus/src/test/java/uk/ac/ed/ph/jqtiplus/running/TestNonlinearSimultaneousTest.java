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

import org.junit.Test;

/**
 * Basic test of nonlinear/simultaneous, focusing on processing rather than navigation and timing
 *
 * @author David McKain
 */
public final class TestNonlinearSimultaneousTest extends SimpleProcessingTestBase {

    @Override
    protected String getTestFilePath() {
        return "running/simple-nonlinear-simultaneous.xml";
    }

    //-------------------------------------------------------

    @Test
    public void testSelectAndRespondItem1Correctly() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(operationTimestamp, getTestNodeKey("i1"));

        /* Submit item 1 correctly */
        handleChoiceResponse("ChoiceA");

        /* RP & OP should not have happened */
        assertItemResponseProcessingNotRun();
        assertOutcomeProcessingNotRun();

        /* End testPart */
        testSessionController.endCurrentTestPart(operationTimestamp);

        /* RP & OP should now have happened */
        assertChoiceItemResponseProcessingRun(item1SessionState);
        assertChoiceItemResponseProcessingNotRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertChoiceItemScore(item1SessionState, 1.0);
        assertChoiceItemScore(item2SessionState, 0.0);
        assertTestScore(1.0);
    }

    @Test
    public void testSelectAndRespondItem1Invalid() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(operationTimestamp, getTestNodeKey("i1"));

        /* Submit item 1 invalidly */
        handleChoiceResponse("Invalid");

        /* RP & OP should not have happened */
        assertItemResponseProcessingNotRun();
        assertOutcomeProcessingNotRun();

        /* End testPart */
        testSessionController.endCurrentTestPart(operationTimestamp);

        /* RP & OP should now have happened */
        assertChoiceItemResponseProcessingRun(item1SessionState);
        assertChoiceItemResponseProcessingNotRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertChoiceItemScore(item1SessionState, 0.0);
        assertChoiceItemScore(item2SessionState, 0.0);
        assertTestScore(0.0);
    }

    @Test
    public void testSelectAndRespondItem1Wrongly() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(operationTimestamp, getTestNodeKey("i1"));

        /* Submit item 1 wrongly */
        handleChoiceResponse("ChoiceB");

        /* RP & OP should not have happened */
        assertItemResponseProcessingNotRun();
        assertOutcomeProcessingNotRun();

        /* End testPart */
        testSessionController.endCurrentTestPart(operationTimestamp);

        /* RP should have happened on item 1 but not 2. OP should have happened */
        assertChoiceItemResponseProcessingRun(item1SessionState);
        assertChoiceItemResponseProcessingNotRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertChoiceItemScore(item1SessionState, 0.0);
        assertChoiceItemScore(item2SessionState, 0.0);
        assertTestScore(0.0);
    }

    @Test
    public void testSelectAndRespondItem1ThenCorrect() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(operationTimestamp, getTestNodeKey("i1"));

        /* Answer item 1 wrongly then correct immediately */
        handleChoiceResponse("ChoiceB");
        handleChoiceResponse("ChoiceA");

        /* End testPart */
        testSessionController.endCurrentTestPart(operationTimestamp);

        /* RP should have happened on item 1 but not 2. OP should have happened */
        assertChoiceItemResponseProcessingRun(item1SessionState);
        assertChoiceItemResponseProcessingNotRun(item2SessionState);
        assertOutcomeProcessingRun();
        assertChoiceItemScore(item1SessionState, 1.0);
        assertChoiceItemScore(item2SessionState, 0.0);
        assertTestScore(1.0);
    }

    @Test
    public void testSelectAndRespondItem1ThenMoveAwayThenComeBackAndCorrect() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(operationTimestamp, getTestNodeKey("i1"));

        /* Answer item 1 wrongly then correct immediately */
        handleChoiceResponse("ChoiceB");
        testSessionController.selectItemNonlinear(operationTimestamp, getTestNodeKey("i2"));
        handleChoiceResponse("ChoiceA");
        testSessionController.selectItemNonlinear(operationTimestamp, getTestNodeKey("i1"));
        handleChoiceResponse("ChoiceA");

        /* End testPart */
        testSessionController.endCurrentTestPart(operationTimestamp);

        /* RP should have happened on item 1 but not 2. OP should have happened */
        assertItemResponseProcessingRun();
        assertOutcomeProcessingRun();
        assertChoiceItemScore(item1SessionState, 1.0);
        assertChoiceItemScore(item2SessionState, 1.0);
        assertTestScore(2.0);
    }

    @Test
    public void testSelectItem1NoResponseThenEnd() {
        testSessionController.enterTest(testEntryTimestamp);
        testSessionController.enterNextAvailableTestPart(testPartEntryTimestamp);
        testSessionController.selectItemNonlinear(operationTimestamp, getTestNodeKey("i1"));
        testSessionController.endCurrentTestPart(operationTimestamp);

        /* RP should have happened on item 1, OP will be run */
        assertChoiceItemResponseProcessingRun(item1SessionState);
        assertChoiceItemResponseProcessingNotRun(item2SessionState);
        assertOutcomeProcessingRun();
    }

}
