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
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.result.SessionStatus;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Encapsulates the current state of an item within a test
 *
 * @author David McKain
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class TestItemSessionState implements Serializable {

    private static final long serialVersionUID = -1407010070268750764L;

    private final TestPlanNodeInstanceKey instanceKey;
    private final ItemSessionState itemSessionState;

    private boolean presented;
    private boolean responded;
    private boolean finished;
    private boolean skipped;
    private boolean timedOut;
    private SessionStatus sessionStatus;
    private String candidateComment;

    public TestItemSessionState(final TestPlanNodeInstanceKey instanceKey, final ItemSessionState itemSessionState) {
        Assert.notNull(instanceKey, "instanceKey");
        Assert.notNull(itemSessionState, "itemSessionState");
        this.instanceKey = instanceKey;
        this.itemSessionState = itemSessionState;
    }

    public TestPlanNodeInstanceKey getInstanceKey() {
        return instanceKey;
    }

    public ItemSessionState getItemState() {
        return itemSessionState;
    }

    //---------------------------------------------------------------

    public boolean isPresented() {
        return presented;
    }

    public void setPresented(final boolean presented) {
        this.presented = presented;
    }


    public boolean isResponded() {
        return responded;
    }

    public void setResponded(final boolean responded) {
        this.responded = responded;
    }


    public boolean isFinished() {
        return finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }


    public boolean isSkipped() {
        return skipped;
    }

    public void setSkipped(final boolean skipped) {
        this.skipped = skipped;
    }


    public boolean isTimedOut() {
        return timedOut;
    }

    public void setTimedOut(final boolean timedOut) {
        this.timedOut = timedOut;
    }


    public SessionStatus getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(final SessionStatus sessionStatus) {
        this.sessionStatus = sessionStatus;
    }


    public String getCandidateComment() {
        return candidateComment;
    }

    public void setCandidateComment(final String candidateComment) {
        this.candidateComment = candidateComment;
    }

    //----------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof TestItemSessionState)) {
            return false;
        }

        final TestItemSessionState other = (TestItemSessionState) obj;
        return instanceKey.equals(other.instanceKey)
                && itemSessionState.equals(other.itemSessionState)
                && presented==other.presented
                && responded==other.responded
                && finished==other.finished
                && skipped==other.skipped
                && timedOut==other.timedOut
                && ObjectUtilities.nullSafeEquals(sessionStatus, other.sessionStatus)
                && candidateComment.equals(other.candidateComment);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                instanceKey,
                itemSessionState,
                Boolean.valueOf(presented),
                Boolean.valueOf(responded),
                Boolean.valueOf(finished),
                Boolean.valueOf(skipped),
                Boolean.valueOf(timedOut),
                sessionStatus,
                candidateComment
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(instanceKey=" + instanceKey
                + ",itemSessionState=" + itemSessionState
                + ",presented=" + presented
                + ",responded=" + responded
                + ",finished=" + finished
                + ",skipped=" + skipped
                + ",timedOut=" + timedOut
                + ",sessionStatus=" + sessionStatus
                + ",candidateComment=" + candidateComment
                + ")";
    }
}
