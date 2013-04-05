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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * FIXME: Document this type
 *
 * FIXME: Rename 'end' as 'finish'?? We're using the word 'close' for items and 'end' of test objects at the moment.
 * Might be nice to merge this together somehow to be easier to understand.
 * FIXME: Using 'presented' instead of 'entry' for items. Maybe try to unify this as well?
 *
 * @author David McKain
 */
public abstract class ControlObjectSessionState implements Serializable {

    private static final long serialVersionUID = 4027764553360833372L;

    protected Date entryTime;
    protected Date endTime;
    protected Date exitTime;

    protected Date durationIntervalStartTime;
    protected long durationAccumulated;

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

    @ObjectDumperOptions(DumpMode.IGNORE)
    public final boolean isEntered() {
        return entryTime!=null;
    }


    public final Date getEndTime() {
        return ObjectUtilities.safeClone(endTime);
    }

    public final void setEndTime(final Date endTime) {
        this.endTime = ObjectUtilities.safeClone(endTime);
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public final boolean isEnded() {
        return endTime!=null;
    }


    public final Date getExitTime() {
        return ObjectUtilities.safeClone(exitTime);
    }

    public final void setExitTime(final Date exitTime) {
        this.exitTime = ObjectUtilities.safeClone(exitTime);
    }

    @ObjectDumperOptions(DumpMode.IGNORE)
    public final boolean isExited() {
        return exitTime!=null;
    }


    public final Date getDurationIntervalStartTime() {
        return durationIntervalStartTime;
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
