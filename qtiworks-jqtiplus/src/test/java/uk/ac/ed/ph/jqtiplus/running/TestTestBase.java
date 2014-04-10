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

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.state.AssessmentSectionSessionState;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.state.marshalling.TestSessionStateXmlMarshaller;
import uk.ac.ed.ph.jqtiplus.testutils.UnitTestHelper;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.w3c.dom.Document;

/**
 * Base class for tests of running {@link AssessmentTest}s.
 *
 * NB: Subclasses should start their time deltas at 1000L and multiply by 2 each time.
 *
 * @author David McKain
 */
public abstract class TestTestBase {

    public static final Identifier CHOICE_ITEM_TP_DONE = Identifier.assumedLegal("TP_DONE");
    public static final Identifier CHOICE_ITEM_RP_DONE = Identifier.assumedLegal("RP_DONE");
    public static final Identifier CHOICE_ITEM_RESPONSE = Identifier.assumedLegal("RESPONSE");
    public static final Identifier CHOICE_ITEM_SCORE = Identifier.assumedLegal("SCORE");

    protected Date testEntryTimestamp;
    protected TestSessionController testSessionController;
    protected TestSessionState testSessionState;
    protected TestPlan testPlan;
    protected Map<String, TestPlanNode> testPlanNodesByIdentifierStringMap;

    /**
     * Subclasses should return a list of stringified identifiers of all nodes within the test using
     * the following conventions:
     * <ul>
     *   <li>testParts should start with 'p'</li>
     *   <li>assessmentSections should start with 's'</li>
     *   <li>assessmentItemRefs should start with 'i'</li>
     * </ul>
     * @return
     */
    protected abstract List<String> testNodes();

    protected abstract String getTestFilePath();

    @Before
    public void initTestSessionController() {
        testEntryTimestamp = new Date();
        testSessionController = UnitTestHelper.loadUnitTestAssessmentTestForControl(getTestFilePath(), true);
        testSessionController.initialize(testEntryTimestamp);
        testSessionState = testSessionController.getTestSessionState();
        testPlan = testSessionState.getTestPlan();

        testPlanNodesByIdentifierStringMap = new HashMap<String, TestPlanNode>();
        for (final String testNodeIdentifierString : testNodes()) {
            final TestPlanNode testPlanNode = UnitTestHelper.assertSingleTestPlanNode(testPlan, testNodeIdentifierString);
            testPlanNodesByIdentifierStringMap.put(testNodeIdentifierString, testPlanNode);
        }
    }

    @After
    public void checkMarshalling() {
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

    protected TestPlanNode getTestNode(final String identifier) {
        return testPlanNodesByIdentifierStringMap.get(identifier);
    }

    protected TestPlanNodeKey getTestNodeKey(final String identifier) {
        return getTestNode(identifier).getKey();
    }

    protected List<String> allSections() {
        final List<String> result = new ArrayList<String>();
        for (final String identifier : testNodes()) {
            if (identifier.charAt(0)=='s') {
                result.add(identifier);
            }
        }
        return result;
    }

    protected List<String> allSectionsExcept(final String... exclusions) {
        final List<String> exclusionsList = Arrays.asList(exclusions);
        final List<String> result = new ArrayList<String>();
        for (final String identifier : testNodes()) {
            if (identifier.charAt(0)=='s' && !exclusionsList.contains(identifier)) {
                result.add(identifier);
            }
        }
        return result;
    }

    protected List<String> allSectionsAfter(final String startIdentifier) {
        final List<String> result = new ArrayList<String>();
        boolean found = false;
        for (final String identifier : testNodes()) {
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
        for (final String identifier : testNodes()) {
            if (identifier.charAt(0)=='i') {
                result.add(identifier);
            }
        }
        return result;
    }

    protected List<String> allItemsExcept(final String... exclusions) {
        final List<String> exclusionsList = Arrays.asList(exclusions);
        final List<String> result = new ArrayList<String>();
        for (final String identifier : testNodes()) {
            if (identifier.charAt(0)=='i' && !exclusionsList.contains(identifier)) {
                result.add(identifier);
            }
        }
        return result;
    }

    protected List<String> allItemsAfter(final String startIdentifier) {
        final List<String> result = new ArrayList<String>();
        boolean found = false;
        for (final String identifier : testNodes()) {
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

    protected void assertTestOpen() {
        RunAssertions.assertOpen(testSessionState, testEntryTimestamp);
    }

    protected void assertTestNowEnded(final Date endTimestamp) {
        RunAssertions.assertNowEnded(testSessionState, endTimestamp);
    }

    protected void assertTestNowExited(final Date endTimestamp) {
        RunAssertions.assertNowExited(testSessionState, endTimestamp);
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

    protected void assertAssessmentSectionsNotEntered(final String... identifiers) {
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

    protected AssessmentSectionSessionState assertAssessmentSectionOpen(final String identifier, final Date entryTimestamp) {
        final AssessmentSectionSessionState result = assertAssessmentSectionState(identifier);
        RunAssertions.assertOpen(result, entryTimestamp);
        return result;
    }

    protected AssessmentSectionSessionState assertAssessmentSectionNowEnded(final String identifier, final Date endTimestamp) {
        final AssessmentSectionSessionState result = assertAssessmentSectionState(identifier);
        RunAssertions.assertNowEnded(result, endTimestamp);
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

    protected ItemSessionState assertItemOpen(final String identifier, final Date entryTimestamp) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertOpen(itemSessionState, entryTimestamp);
        return itemSessionState;
    }

    protected ItemSessionState assertItemSuspended(final String identifier, final Date suspendTimestamp) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertSuspended(itemSessionState, suspendTimestamp);
        return itemSessionState;
    }

    protected ItemSessionState assertItemNowEnded(final String identifier, final Date endTimestamp) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertNowEnded(itemSessionState, endTimestamp);
        return itemSessionState;
    }

    protected ItemSessionState assertItemNowExited(final String identifier, final Date endTimestamp) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertNowExited(itemSessionState, endTimestamp);
        return itemSessionState;
    }

    protected ItemSessionState assertItemEndedButNotEntered(final String identifier, final Date endTimestamp) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertEndedButNotEntered(itemSessionState, endTimestamp);
        return itemSessionState;
    }

    protected ItemSessionState assertItemExitedButNotEntered(final String identifier, final Date exitTimestamp) {
        final ItemSessionState itemSessionState = assertItemSessionState(identifier);
        RunAssertions.assertExitedButNotEntered(itemSessionState, exitTimestamp);
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

    protected ItemSessionState assertItemJumpedByBranchRuleAndNotExited(final String identifier) {
        final ItemSessionState result = assertItemSessionState(identifier);
        RunAssertions.assertJumpedByBranchRuleAndNotExited(result);
        return result;
    }

    protected ItemSessionState assertItemJumpedByBranchRuleAndExited(final String identifier, final Date exitTimestamp) {
        final ItemSessionState result = assertItemSessionState(identifier);
        RunAssertions.assertJumpedByBranchRuleAndExited(result, exitTimestamp);
        return result;
    }

    protected void assertItemsSelectable(final String... identifiers) {
        for (final String identifier : identifiers) {
            assertItemSelectable(identifier);
        }
    }

    protected void assertItemsSelectable(final Iterable<String> identifiers) {
        for (final String identifier : identifiers) {
            assertItemSelectable(identifier);
        }
    }

    protected void assertItemSelectable(final String identifier) {
        Assert.assertTrue(testSessionController.maySelectItemNonlinear(getTestNodeKey(identifier)));
    }

    protected void assertItemsNotSelectable(final String... identifiers) {
        for (final String identifier : identifiers) {
            assertItemNotSelectable(identifier);
        }
    }

    protected void assertItemsNotSelectable(final Iterable<String> identifiers) {
        for (final String identifier : identifiers) {
            assertItemNotSelectable(identifier);
        }
    }

    protected void assertItemNotSelectable(final String identifier) {
        Assert.assertFalse(testSessionController.maySelectItemNonlinear(getTestNodeKey(identifier)));
    }

    //-------------------------------------------------------

    protected void assertChoiceItemTemplateProcessingNotRun(final ItemSessionState itemSessionState) {
        Assert.assertEquals(BooleanValue.FALSE, itemSessionState.getTemplateValue(CHOICE_ITEM_TP_DONE));
    }

    protected void assertChoiceItemTemplateProcessingRun(final ItemSessionState itemSessionState) {
        Assert.assertEquals(BooleanValue.TRUE, itemSessionState.getTemplateValue(CHOICE_ITEM_TP_DONE));
    }

    protected void assertChoiceItemResponseProcessingNotRun(final ItemSessionState itemSessionState) {
        Assert.assertEquals(BooleanValue.FALSE, itemSessionState.getOutcomeValue(CHOICE_ITEM_RP_DONE));
    }

    protected void assertChoiceItemResponseProcessingRun(final ItemSessionState itemSessionState) {
        Assert.assertEquals(BooleanValue.TRUE, itemSessionState.getOutcomeValue(CHOICE_ITEM_RP_DONE));
    }

    protected void assertChoiceItemScore(final ItemSessionState itemSessionState, final Double expected) {
        RunAssertions.assertValueEqualsDouble(expected, itemSessionState.getOutcomeValue(CHOICE_ITEM_SCORE));
    }

    protected void handleChoiceResponse(final Date timestamp, final String choiceIdentifier) {
        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        responseMap.put(CHOICE_ITEM_RESPONSE, new StringResponseData(choiceIdentifier));
        testSessionController.handleResponsesToCurrentItem(timestamp, responseMap);
    }
}
