
/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.state;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Record of an amount of time in an assessmentItemRef
 * 
 * FIXME: Work out what the hell this does!
 * 
 * @author Jiri Kajaba
 */
public class TimeRecord implements Serializable, ItemTimeRecord {

    private static final long serialVersionUID = 5242908459013903094L;

    private final AssessmentItemRefState owner;

    private List<Long> entered;

    private List<Long> exited;

    private List<Long> submitted;

    private Long skipped;

    private Long timedOut;

    private long total;

    private long duration;

    private boolean increaseDuration;

    public TimeRecord(AssessmentItemRefState owner) {
        this.owner = owner;
        this.entered = new ArrayList<Long>();
        this.exited = new ArrayList<Long>();
        this.submitted = new ArrayList<Long>();
    }

    public AssessmentItemRefState getOwner() {
        return owner;
    }
    
    //---------------------------------------------------------
    
    public void reset() {
        this.entered.clear();
        this.exited.clear();
        this.submitted.clear();
        this.skipped = 0L;
        this.timedOut = 0L;
        this.total = 0L;
        this.duration = 0L;
        this.increaseDuration = false;
    }
    
    //---------------------------------------------------------

    /**
     * @return record of start times
     */
    public List<Long> getEntered() {
        return entered;
    }

    /**
     * @return last start time
     */
    public Long getLastEntered() {
        return (entered.size() > 0) ? entered.get(entered.size() - 1) : null;
    }

    /**
     * @return stopped time
     */
    public List<Long> getExited() {
        return exited;
    }

    /**
     * @return submitted times
     */
    public List<Long> getSubmitted() {
        return submitted;
    }

    /**
     * @return skipped time
     */
    public Long getSkipped() {
        return skipped;
    }

    /**
     * @return timedOut time
     */
    public Long getTimedOut() {
        return timedOut;
    }

    /**
     * FIXME: This used to be getTotal() but was renamed so that I could provide
     * access to the raw 'total' field. I'm not exactly sure what this does!!
     * 
     * @return total time
     */
    public long getActualTotal() {
        long last = 0;
        if (entered.size() == (exited.size() + 1)) {
            last = owner.getTestState().getTimer().getCurrentTime() - getLastEntered();
        }

        return total + last;
    }

    /**
     * FIXME: This used to be getDuration() but was renamed so that I could
     * provide access to the raw 'duration' field. I'm not exactly sure what
     * this does!!
     * 
     * @return total time
     */
    public long getActualDuration() {
        long last = 0;
        if (increaseDuration) {
            last = owner.getTestState().getTimer().getCurrentTime() - getLastEntered();
        }

        return duration + last;
    }

    // -------------------------------------------------------------
    // (Mutators for logic classes only)

    public boolean isIncreaseDuration() {
        return increaseDuration;
    }

    public void setIncreaseDuration(boolean increaseDuration) {
        this.increaseDuration = increaseDuration;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
    
    public void addToTotal(long addition) {
        this.total += addition;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
    
    public void addToDuration(long addition) {
        this.duration += addition;
    }

    public void setSkipped(Long skipped) {
        this.skipped = skipped;
    }

    public void setTimedOut(Long timedOut) {
        this.timedOut = timedOut;
    }

//    // -------------------------------------------------------------
//    // (Followed moved from TimeRecord to AssessmentItemRefController)
//
//     /**
//     * Enter new start time.
//     * @param time start time
//     */
//     public void enter(long time)
//     {
//     assert entered.size() == exited.size() :
//     "Cannot enter item reference twice: " + owner.getIdentifier();
//    
//     entered.add(time);
//     if (!owner.isFinished() && owner.passMaximumTimeLimit())
//     increaseDuration = true;
//     }
//    
//     /**
//     * Enter new exit time.
//     * @param time exit time
//     */
//     public void exit(long time)
//     {
//     assert entered.size() == (exited.size() + 1) :
//     "Cannot exit item reference prior to enter: " + owner.getIdentifier();
//    
//     exited.add(time);
//     long lastEntered = getLastEntered();
//     total += time - lastEntered;
//     if (increaseDuration)
//     {
//     duration += time - lastEntered;
//     increaseDuration = false;
//     }
//     }
//    
//     /**
//     * Enter new submit time.
//     * @param time submit time
//     */
//     public void submit(long time)
//     {
//     assert skipped == null : "Cannot submit skipped item reference: " +
//     owner.getIdentifier();
//    
//     assert timedOut == null : "Cannot submit timed out item reference: " +
//     owner.getIdentifier();
//    
//     assert entered.size() == (exited.size() + 1) :
//     "Cannot submit item reference prior to enter: " + owner.getIdentifier();
//    
//     submitted.add(time);
//     duration += time - getLastEntered();
//     increaseDuration = false;
//     }
//    
//     /**
//     * Enter new skip time
//     * @param time skip time
//     */
//     public void skip(long time)
//     {
//     assert skipped == null : "Cannot skip item reference twice: " +
//     owner.getIdentifier();
//    
//     assert timedOut == null : "Cannot skip timed out item reference: " +
//     owner.getIdentifier();
//    
//     assert entered.size() == (exited.size() + 1) :
//     "Cannot skip item reference prior to enter: " + owner.getIdentifier();
//    
//     skipped = time;
//     duration += time - getLastEntered();
//     increaseDuration = false;
//     }
//    
//     /**
//     * Enter new timeout time
//     * @param time timeout time
//     */
//     public void timeOut(long time)
//     {
//     assert skipped == null : "Cannot timeout skipped item reference: " +
//     owner.getIdentifier();
//    
//     assert timedOut == null : "Cannot time out item reference twice: " +
//     owner.getIdentifier();
//    
//     assert entered.size() == (exited.size() + 1) :
//     "Cannot timeout item reference prior to enter: " + owner.getIdentifier();
//    
//     timedOut = time;
//     duration += time - getLastEntered();
//     increaseDuration = false;
//     }
     
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(entered=" + entered
            + ",exited=" + exited
            + ",submitted=" + submitted
            + ",skipped=" + skipped
            + ",timedOut=" + timedOut
            + ",total=" + total
            + ",duration=" + duration
            + ",increaseDuration=" + increaseDuration
            + ")";
    }
}
