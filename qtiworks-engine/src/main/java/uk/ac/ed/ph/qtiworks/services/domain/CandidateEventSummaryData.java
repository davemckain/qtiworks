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
package uk.ac.ed.ph.qtiworks.services.domain;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateTestEventType;
import uk.ac.ed.ph.qtiworks.web.view.ElFunctions;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;

import java.io.Serializable;
import java.util.Date;

/**
 * Information about a {@link CandidateEvent}, oriented for reporting and display.
 *
 * @author David McKain
 */
public final class CandidateEventSummaryData implements Serializable {

    private static final long serialVersionUID = 3118078698680765414L;

    private final Date timestamp;
    private final CandidateTestEventType testEventType;
    private final CandidateItemEventType itemEventType;
    private final TestPlanNodeKey testItemTestPlanNodeKey;

    public CandidateEventSummaryData(final CandidateEvent candidateEvent) {
        this.timestamp = ObjectUtilities.safeClone(candidateEvent.getTimestamp());
        this.testEventType = candidateEvent.getTestEventType();
        this.itemEventType = candidateEvent.getItemEventType();

        final String testItemKey = candidateEvent.getTestItemKey();
        this.testItemTestPlanNodeKey = testItemKey!=null ? TestPlanNodeKey.fromString(testItemKey) : null;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public CandidateTestEventType getTestEventType() {
        return testEventType;
    }

    public CandidateItemEventType getItemEventType() {
        return itemEventType;
    }

    public String getFormattedDescription() {
        if (testEventType!=null) {
            switch (testEventType) {
                case ENTER_TEST: return "Entered test";
                case ADVANCE_TEST_PART: return "Advanced to next testPart";
                case SELECT_MENU: return "Returned to Test Question Menu";
                case FINISH_ITEM: return "Finished current item (in linear navigation mode)";
                case FINISH_FINAL_ITEM: return "Finished current item (in linear navigation mode), ending the testPart as no further items are available to enter";
                case REVIEW_TEST_PART: return "Reviewed current testPart";
                case END_TEST_PART: return "Ended current testPart";
                case EXIT_TEST: return "Exited test";
                case REVIEW_ITEM: return "Reviewed item " + formatTestItemKey();
                case SELECT_ITEM: return "Selected item " + formatTestItemKey();
                case SOLUTION_ITEM: return "Requested solution for item " + formatTestItemKey();
                case ITEM_EVENT: switch (itemEventType) {
                    /* NB: These are slightly different to standalone items */
                    case RESPONSE_BAD: return "Submitted response(s) to current item, some of which were bad";
                    case RESPONSE_INVALID: return "Submitted response(s) to current item, some of which were not valid";
                    case RESPONSE_VALID: return "Submitted valid response(s) to current item";
                    case ATTEMPT_VALID: return "Submitted a valid attempt to current item";

                    /* The following shouldn't happen here */
                    case RESET:
                    case REINIT:
                    case ENTER:
                    case SOLUTION:
                    case END:
                    case EXIT:
                        return "[Should not occur for items within tests]";

                    default:
                        throw new QtiWorksLogicException("Unexpected switch case " + itemEventType);
                }
                default:
                    throw new QtiWorksLogicException("Unexpected switch case " + testEventType);
            }
        }
        else if (itemEventType!=null) {
            switch (itemEventType) {
                case ENTER: return "Entered item";
                case END: return "Ended item";
                case RESPONSE_BAD: return "Submitted response(s), some of which were bad";
                case RESPONSE_INVALID: return "Submitted response(s), some of which were not valid";
                case ATTEMPT_VALID: return "Submitted a valid attempt";
                case REINIT: return "Reinitialised item";
                case RESET: return "Reset item";
                case SOLUTION: return "Requested solution";
                case EXIT: return "Exited item";

                /* The remaining events don't happen in standalone items */
                case RESPONSE_VALID: return "[This should not happend for standalone items]";

                default:
                    throw new QtiWorksLogicException("Unexpected switch case " + itemEventType);
            }
        }
        else {
            throw new QtiWorksLogicException("CandidateEvent is missing item/test type information");
        }
    }

    private String formatTestItemKey() {
        return testItemTestPlanNodeKey!=null ? ElFunctions.formatTestPlanNodeKey(testItemTestPlanNodeKey) : "NULL";
    }

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
