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
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.test.AbstractPart;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.BranchRule;
import uk.ac.ed.ph.jqtiplus.node.test.PreCondition;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Base for recording the session state of an {@link AbstractPart}
 *
 * @author David McKain
 */
@ObjectDumperOptions(DumpMode.DEEP)
public abstract class AbstractPartSessionState extends ControlObjectSessionState implements Serializable {

    private static final long serialVersionUID = -134308115257966761L;

    /**
     * Indicates whether a {@link PreCondition} on the corresponding {@link AbstractPart}
     * has failed.
     */
    protected boolean preConditionFailed;

    /**
     * Indicates whether this {@link AbstractPart} was skipped because a {@link BranchRule}
     * caused a jump to a later node.
     * <p>
     * NB: This will be set as appropriate for descendants of {@link AssessmentSection} nodes,
     * but not for descendants of {@link TestPart} nodes.
     */
    protected boolean jumpedByBranchRule;

    /**
     * If not null, then a {@link BranchRule} on the corresponding {@link AbstractPart} evaluated to
     * true and branched as determined by the value of this property. The value of this will be either
     * {@link BranchRule#EXIT_SECTION}, {@link BranchRule#EXIT_TESTPART}, {@link BranchRule#EXIT_TEST}
     * or the key of the {@link TestPlanNode} corresponding to an explicit branch target.
     */
    protected String branchRuleTarget;

    @Override
    public void reset() {
        super.reset();
        this.preConditionFailed = false;
        this.branchRuleTarget = null;
    }

    //----------------------------------------------------------------

    public boolean isPreConditionFailed() {
        return preConditionFailed;
    }

    public void setPreConditionFailed(final boolean preConditionFailed) {
        this.preConditionFailed = preConditionFailed;
    }


    public boolean isJumpedByBranchRule() {
        return jumpedByBranchRule;
    }

    public void setJumpedByBranchRule(final boolean jumpedByBranchRule) {
        this.jumpedByBranchRule = jumpedByBranchRule;
    }


    public String getBranchRuleTarget() {
        return branchRuleTarget;
    }

    public void setBranchRuleTarget(final String branchRuleTarget) {
        this.branchRuleTarget = branchRuleTarget;
    }

    //----------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof AbstractPartSessionState)) {
            return false;
        }
        final AbstractPartSessionState other = (AbstractPartSessionState) obj;
        return super.equals(other)
                && preConditionFailed==other.preConditionFailed
                && jumpedByBranchRule==other.jumpedByBranchRule
                && ObjectUtilities.nullSafeEquals(branchRuleTarget, other.branchRuleTarget);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                super.hashCode(),
                preConditionFailed,
                jumpedByBranchRule,
                branchRuleTarget
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(preConditionFailed=" + preConditionFailed
                + ",skippedByBranchRule=" + jumpedByBranchRule
                + ",branchRuleTarget=" + branchRuleTarget
                + ",entryTime=" + getEntryTime()
                + ",endTime=" + getEndTime()
                + ",exitTime=" + getExitTime()
                + ",durationAccumulated=" + getDurationAccumulated()
                + ",durationIntervalStartTime=" + getDurationIntervalStartTime()
                + ")";
    }
}
