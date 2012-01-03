/* $Id: AssessmentItemRefController.java 2802 2011-10-05 07:59:53Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.control;

import uk.ac.ed.ph.jqtiplus.exception.QTIItemFlowException;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemRefState;
import uk.ac.ed.ph.jqtiplus.state.TimeRecord;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.AssessmentItemManager;

import java.util.Date;

/**
 * @author David McKain
 * @version $Revision: 2802 $
 */
public final class AssessmentItemRefController {

    private final AssessmentTestController testController;
    private final AssessmentItemController itemController;
    private final AssessmentItemRef itemRef;
    private final AssessmentItemRefState itemRefState;

    AssessmentItemRefController(AssessmentTestController testController,
            AssessmentItemManager itemManager, AssessmentItemRef itemRef,
            AssessmentItemRefState itemRefState) {
        ConstraintUtilities.ensureNotNull(itemManager, "assessmentTestManager");
        ConstraintUtilities.ensureNotNull(itemManager, "assessmentItemManager");
        ConstraintUtilities.ensureNotNull(itemRefState, "assessmentItemRefState");
        this.testController = testController;
        this.itemController = new AssessmentItemController(itemManager, itemRefState.getItemState());
        this.itemRef = itemRef;
        this.itemRefState = itemRefState;
    }

    public AssessmentTestController getTestController() {
        return testController;
    }

    public AssessmentItemController getItemController() {
        return itemController;
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
        return itemController.isCorrect();
    }

    public Boolean isIncorrect() {
        return itemController.isIncorrect();
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
     * @throws QTIItemFlowException if this item reference if already finished or skipping is not allowed
     * @see #isSkipped
     */
    public void skip() {
        if (isFinished()) {
            throw new QTIItemFlowException(this, "Item reference is already finished.");
        }
        if (!itemRef.getItemSessionControl().getAllowSkipping()) {
            throw new QTIItemFlowException(this, "It is not allowed to skip this item: ");
        }
        skip(testController.getTimer().getCurrentTime());
        itemRefState.setSkipped(true);
        itemRefState.setFinished(true);
    }
    
    /**
     * Times out this item reference and sets state to finished.
     * <p>
     * This method should be called when user submits answer but time was already out.
     *
     * @throws QTIItemFlowException if this item reference is already finished
     * @see #isTimedOut
     */
    public void timeOut() {
        if (isFinished()) {
            throw new QTIItemFlowException(this, "Item reference is already finished.");
        }
        setTimeOutTime(testController.getTimer().getCurrentTime());
        itemRefState.setTimedOut(true);
        itemRefState.setFinished(true);
    }
    
    // ---------------------------------------------------

    public boolean passMaximumTimeLimit() {
        return testController.passMaximumTimeLimit(itemRefState);
    }
    
    
    // ---------------------------------------------------
    // The next used to be in AssessmentItemRef. It previously added results for each "state"
    // recorded, but we're not doing this any more.
    
    public ItemResult computeItemResult(AssessmentResult parent, Integer sequenceIndex, SessionStatus sessionStatus) {
        ItemResult result = new ItemResult(parent);
        result.setIdentifier(itemRef.getIdentifier().toString());
        result.setDateStamp(new Date());
        result.setSequenceIndex(sequenceIndex);
        result.setSessionStatus(sessionStatus);
        itemController.recordItemVariables(result);
        return result;
    }

    // ---------------------------------------------------
    // (These used to be in TimeRecord)

    /**
     * Enter new start time.
     * 
     * @param time start time
     */
    @ToRefactor /* Rename this as setEnterTime() */
    public void enter(long time) {
        TimeRecord timeRecord = itemRefState.getTimeRecord();
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
    @ToRefactor /* Rename this */
    public void exit(long time) {
        TimeRecord timeRecord = itemRefState.getTimeRecord();
        assert timeRecord.getEntered().size() == (timeRecord.getExited().size() + 1) : "Cannot exit item reference prior to enter: " + itemRefState;

        timeRecord.getExited().add(time);
        long lastEntered = timeRecord.getLastEntered();
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
    @ToRefactor /* Rename this */
    public void submit(long time) {
        TimeRecord timeRecord = itemRefState.getTimeRecord();
        assert timeRecord.getSkipped() == null : "Cannot submit timeRecord.getSkipped() item reference: " + itemRefState;
        assert timeRecord.getTimedOut() == null : "Cannot submit timed out item reference: " + itemRefState;
        assert timeRecord.getEntered().size() == (timeRecord.getExited().size() + 1) : "Cannot submit item reference prior to enter: " + itemRefState;

        timeRecord.getSubmitted().add(time);
        timeRecord.addToDuration(time - timeRecord.getLastEntered());
        timeRecord.setIncreaseDuration(false);
    }

    /**
     * Enter new skip time
     * 
     * @param time skip time
     */
    @ToRefactor /* Rename this */
    public void skip(long time) {
        TimeRecord timeRecord = itemRefState.getTimeRecord();
        assert timeRecord.getSkipped() == null : "Cannot skip item reference twice: " + itemRefState;
        assert timeRecord.getTimedOut() == null : "Cannot skip timed out item reference: " + itemRefState;
        assert timeRecord.getEntered().size() == (timeRecord.getExited().size() + 1) : "Cannot skip item reference prior to enter: " + itemRefState;

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
        TimeRecord timeRecord = itemRefState.getTimeRecord();
        assert timeRecord.getSkipped() == null : "Cannot timeout timeRecord.getSkipped() item reference: " + itemRefState;
        assert timeRecord.getTimedOut() == null : "Cannot time out item reference twice: " + itemRefState;
        assert timeRecord.getEntered().size() == (timeRecord.getExited().size() + 1) : "Cannot timeout item reference prior to enter: " + itemRefState;

        timeRecord.setTimedOut(time);
        timeRecord.addToDuration(time - timeRecord.getLastEntered());
        timeRecord.setIncreaseDuration(false);
    }
    
    // ---------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(itemController=" + itemController
                + ",itemRef=" + itemRef
                + ",itemRefState=" + itemRefState + ")";
    }
}
