/* Copyright (c) 2012, University of Edinburgh.
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
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.running;

import uk.ac.ed.ph.jqtiplus.exception.QtiItemFlowException;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemRefState;
import uk.ac.ed.ph.jqtiplus.state.TimeRecord;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.util.Date;

/**
 * @author David McKain
 */
public final class AssessmentItemRefAttemptController {

    private final AssessmentTestAttemptController testAttemptController;
    private final ItemSessionController itemAttemptController;
    private final AssessmentItemRef itemRef;
    private final AssessmentItemRefState itemRefState;

    AssessmentItemRefAttemptController(AssessmentTestAttemptController testAttemptController,
            ResolvedAssessmentItem resolvedAssessmentItem, AssessmentItemRef itemRef,
            AssessmentItemRefState itemRefState) {
        ConstraintUtilities.ensureNotNull(testAttemptController, "testAttemptController");
        ConstraintUtilities.ensureNotNull(resolvedAssessmentItem, "resolvedAssessmentItem");
        ConstraintUtilities.ensureNotNull(itemRef, "itemRef");
        ConstraintUtilities.ensureNotNull(itemRefState, "assessmentItemRefState");
        this.testAttemptController = testAttemptController;
        this.itemAttemptController = new ItemSessionController(testAttemptController.getJqtiExtensionManager(),
                resolvedAssessmentItem, itemRefState.getItemState());
        this.itemRef = itemRef;
        this.itemRefState = itemRefState;
    }

    public AssessmentTestAttemptController getTestController() {
        return testAttemptController;
    }

    public ItemSessionController getItemController() {
        return itemAttemptController;
    }

    public AssessmentItemRef getItemRef() {
        return itemRef;
    }

    public AssessmentItemRefState getItemRefState() {
        return itemRefState;
    }

    // ---------------------------------------------------------
    // (These methods were in the original AssessmentItemRef) */

    public long getTotalTime() {
        return itemRefState.getTimeRecord().getTotal();
    }

    public long getResponseTime() {
        return itemRefState.getTimeRecord().getDuration();
    }

    public Boolean isCorrect() {
        return itemAttemptController.isCorrect();
    }

    public Boolean isIncorrect() {
        return itemAttemptController.isIncorrect();
    }

    public boolean isResponded() {
        return itemRefState.isResponded();
    }

    public boolean isSkipped() {
        return itemRefState.isSkipped();
    }

    public boolean isPresented() {
        return itemRefState.isPresented();
    }

    public boolean isFinished() {
        return itemRefState.isFinished();
    }

    public void setFinished() {
        itemRefState.setFinished(true);
    }

    //---------------------------------------------------
    // (These logic methods used to be in AssessmentItemRef)

    /**
     * Skips this item reference and sets state to finished.
     * 
     * @throws QtiItemFlowException if this item reference if already finished or skipping is not allowed
     * @see #isSkipped
     */
    @ToRefactor
    public void skip() {
        if (isFinished()) {
            throw new QtiItemFlowException(this, "Item reference is already finished.");
        }
        if (!itemRef.getItemSessionControl().getAllowSkipping()) {
            throw new QtiItemFlowException(this, "It is not allowed to skip this item: ");
        }
        skip(testAttemptController.getTimer().getCurrentTime());
        itemRefState.setSkipped(true);
        itemRefState.setFinished(true);
    }

    /**
     * Times out this item reference and sets state to finished.
     * <p>
     * This method should be called when user submits answer but time was already out.
     * 
     * @throws QtiItemFlowException if this item reference is already finished
     * @see AssessmentItemRefState#isTimedOut()
     */
    @ToRefactor
    public void timeOut() {
        if (isFinished()) {
            throw new QtiItemFlowException(this, "Item reference is already finished.");
        }
        setTimeOutTime(testAttemptController.getTimer().getCurrentTime());
        itemRefState.setTimedOut(true);
        itemRefState.setFinished(true);
    }

    // ---------------------------------------------------

    public boolean passMaximumTimeLimit() {
        return testAttemptController.passMaximumTimeLimit(itemRefState);
    }


    // ---------------------------------------------------
    // The next used to be in AssessmentItemRef. It previously added results for each "state"
    // recorded, but we're not doing this any more.

    public ItemResult computeItemResult(AssessmentResult parent, Integer sequenceIndex, SessionStatus sessionStatus) {
        final ItemResult result = new ItemResult(parent);
        result.setIdentifier(itemRef.getIdentifier().toString());
        result.setDateStamp(new Date());
        result.setSequenceIndex(sequenceIndex);
        result.setSessionStatus(sessionStatus);
        itemAttemptController.recordItemVariables(result);
        return result;
    }

    // ---------------------------------------------------
    // (These used to be in TimeRecord)

    /**
     * Enter new start time.
     * 
     * @param time start time
     */
    @ToRefactor
    /* Rename this as setEnterTime() */
    public void enter(long time) {
        final TimeRecord timeRecord = itemRefState.getTimeRecord();
        assert timeRecord.getEntered().size() == timeRecord.getExited().size() : "Cannot enter item reference twice: "
                + itemRefState;

        timeRecord.getEntered().add(time);
        if (!itemRefState.isFinished() && passMaximumTimeLimit()) {
            timeRecord.setIncreaseDuration(true);
        }
    }

    /**
     * Enter new exit time.
     * 
     * @param time exit time
     */
    @ToRefactor
    /* Rename this */
    public void exit(long time) {
        final TimeRecord timeRecord = itemRefState.getTimeRecord();
        assert timeRecord.getEntered().size() == timeRecord.getExited().size() + 1 : "Cannot exit item reference prior to enter: " + itemRefState;

        timeRecord.getExited().add(time);
        final long lastEntered = timeRecord.getLastEntered();
        timeRecord.addToTotal(time - lastEntered);
        if (timeRecord.isIncreaseDuration()) {
            timeRecord.addToDuration(time - lastEntered);
            timeRecord.setIncreaseDuration(false);
        }
    }

    /**
     * Enter new submit time.
     * 
     * @param time submit time
     */
    @ToRefactor
    /* Rename this */
    public void submit(long time) {
        final TimeRecord timeRecord = itemRefState.getTimeRecord();
        assert timeRecord.getSkipped() == null : "Cannot submit timeRecord.getSkipped() item reference: " + itemRefState;
        assert timeRecord.getTimedOut() == null : "Cannot submit timed out item reference: " + itemRefState;
        assert timeRecord.getEntered().size() == timeRecord.getExited().size() + 1 : "Cannot submit item reference prior to enter: " + itemRefState;

        timeRecord.getSubmitted().add(time);
        timeRecord.addToDuration(time - timeRecord.getLastEntered());
        timeRecord.setIncreaseDuration(false);
    }

    /**
     * Enter new skip time
     * 
     * @param time skip time
     */
    @ToRefactor
    /* Rename this */
    public void skip(long time) {
        final TimeRecord timeRecord = itemRefState.getTimeRecord();
        assert timeRecord.getSkipped() == null : "Cannot skip item reference twice: " + itemRefState;
        assert timeRecord.getTimedOut() == null : "Cannot skip timed out item reference: " + itemRefState;
        assert timeRecord.getEntered().size() == timeRecord.getExited().size() + 1 : "Cannot skip item reference prior to enter: " + itemRefState;

        timeRecord.setSkipped(time);
        timeRecord.addToDuration(time - timeRecord.getLastEntered());
        timeRecord.setIncreaseDuration(false);
    }

    /**
     * Enter new timeout time
     * 
     * @param time timeout time
     */
    public void setTimeOutTime(long time) {
        final TimeRecord timeRecord = itemRefState.getTimeRecord();
        assert timeRecord.getSkipped() == null : "Cannot timeout timeRecord.getSkipped() item reference: " + itemRefState;
        assert timeRecord.getTimedOut() == null : "Cannot time out item reference twice: " + itemRefState;
        assert timeRecord.getEntered().size() == timeRecord.getExited().size() + 1 : "Cannot timeout item reference prior to enter: " + itemRefState;

        timeRecord.setTimedOut(time);
        timeRecord.addToDuration(time - timeRecord.getLastEntered());
        timeRecord.setIncreaseDuration(false);
    }

    // ---------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(itemController=" + itemAttemptController
                + ",itemRef=" + itemRef
                + ",itemRefState=" + itemRefState + ")";
    }
}
