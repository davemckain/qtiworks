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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.state;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Encapsulates the state of a {@link TestPart}.
 *
 * @author David McKain
 */
@ObjectDumperOptions(DumpMode.DEEP)
public class TestPartSessionState implements Serializable {

    private static final long serialVersionUID = -1041244926292225923L;

    private boolean preConditionFailed;
    private boolean entered;
    private boolean ended;
    private boolean exited;

    public TestPartSessionState() {
        reset();
    }

    public void reset() {
    	this.preConditionFailed = false;
        this.entered = false;
        this.ended = false;
        this.exited = false;
    }

    //----------------------------------------------------------------

    public boolean isPreConditionFailed() {
		return preConditionFailed;
	}

	public void setPreConditionFailed(final boolean preConditionFailed) {
		this.preConditionFailed = preConditionFailed;
	}


	public boolean isEntered() {
        return entered;
    }

	public void setEntered(final boolean entered) {
        this.entered = entered;
    }


    public boolean isEnded() {
        return ended;
    }

    public void setEnded(final boolean ended) {
        this.ended = ended;
    }


    public boolean isExited() {
        return exited;
    }

    public void setExited(final boolean exited) {
        this.exited = exited;
    }

    //----------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof TestPartSessionState)) {
            return false;
        }
        final TestPartSessionState other = (TestPartSessionState) obj;
        return preConditionFailed==other.preConditionFailed
        		&& entered==other.entered
                && ended==other.ended
                && exited==other.exited;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
        		preConditionFailed,
                entered,
                ended,
                exited
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(preConditionFailed=" + preConditionFailed
                + ",entered=" + entered
                + ",ended=" + ended
                + ",exited=" + exited
                + ")";
    }
}
