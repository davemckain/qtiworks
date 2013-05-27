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
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.test.ControlObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * Base state for {@link ControlObject}s.
 * <p>
 * This supports accumulating a 'duration' value in start/stop/start/stop intervals.
 *
 * @author David McKain
 */
public abstract class ControlObjectSessionState implements Serializable {

    private static final long serialVersionUID = 4027764553360833372L;

    /** Timestamp of entry into this object */
    protected Date entryTime;

    /** Timestamp for end (close) of this object */
    protected Date endTime;

    /** Timetamp for exit of this object */
    protected Date exitTime;

    /** Amount of duration accumulated so far */
    protected long durationAccumulated;

    /**
     * If not null, indicates that duration counting started this time time and is currently 'open'
     * <p>
     * (It is expected that a later 'touch in' state mutation operation will clear this and
     * update {@link #durationAccumulated} at the same time.)
     */
    protected Date durationIntervalStartTime;

    public void reset() {
        this.entryTime = null;
        this.endTime = null;
        this.exitTime = null;
        resetDuration();
    }

    public void resetDuration() {
        this.durationAccumulated = 0L;
        this.durationIntervalStartTime = null;
    }

    public final Date getEntryTime() {
        return ObjectUtilities.safeClone(entryTime);
    }

    public final void setEntryTime(final Date enteredTime) {
        this.entryTime = ObjectUtilities.safeClone(enteredTime);
    }


    public final Date getEndTime() {
        return ObjectUtilities.safeClone(endTime);
    }

    public final void setEndTime(final Date endTime) {
        this.endTime = ObjectUtilities.safeClone(endTime);
    }


    public final Date getExitTime() {
        return ObjectUtilities.safeClone(exitTime);
    }

    public final void setExitTime(final Date exitTime) {
        this.exitTime = ObjectUtilities.safeClone(exitTime);
    }


    public final Date getDurationIntervalStartTime() {
        return ObjectUtilities.safeClone(durationIntervalStartTime);
    }

    public final void setDurationIntervalStartTime(final Date outTime) {
        this.durationIntervalStartTime = ObjectUtilities.safeClone(outTime);
    }


    public final long getDurationAccumulated() {
        return durationAccumulated;
    }

    public final void setDurationAccumulated(final long durationAccumulated) {
        this.durationAccumulated = durationAccumulated;
    }

    //----------------------------------------------------------------

    @ObjectDumperOptions(DumpMode.IGNORE)
    public final boolean isEntered() {
        return entryTime!=null;
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public final boolean isOpen() {
        return isEntered() && !isEnded();
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public final boolean isEnded() {
        return endTime!=null;
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public final boolean isExited() {
        return exitTime!=null;
    }

    //----------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ControlObjectSessionState)) {
            return false;
        }

        final ControlObjectSessionState other = (ControlObjectSessionState) obj;
        return durationAccumulated==other.durationAccumulated
                && ObjectUtilities.nullSafeEquals(entryTime, other.entryTime)
                && ObjectUtilities.nullSafeEquals(endTime, other.endTime)
                && ObjectUtilities.nullSafeEquals(exitTime, other.exitTime)
                && ObjectUtilities.nullSafeEquals(durationIntervalStartTime, other.durationIntervalStartTime);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                entryTime,
                endTime,
                exitTime,
                durationAccumulated,
                durationIntervalStartTime,
        });
    }
}
