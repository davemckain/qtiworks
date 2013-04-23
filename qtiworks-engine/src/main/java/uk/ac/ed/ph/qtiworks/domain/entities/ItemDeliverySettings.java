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
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.qtiworks.domain.entities;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import org.hibernate.annotations.Type;

/**
 * Specifies settings controlling the delivery of an {@link AssessmentItem} to a group of candidates.
 *
 * @see DeliverySettings
 *
 * @author David McKain
 */
@Entity
@Table(name="item_delivery_settings")
public class ItemDeliverySettings extends DeliverySettings implements BaseEntity {

    private static final long serialVersionUID = 6573748787230595395L;

    /** Optional prompt to show to candidates */
    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Basic(optional=true)
    @Column(name="prompt")
    private String prompt;

    /** Maximum number of attempts, as defined by {@link ItemSessionControl} */
    @Min(value=0)
    @Basic(optional=false)
    @Column(name="max_attempts")
    private Integer maxAttempts;

    /** Allow candidate to close session */
    @Basic(optional=false)
    @Column(name="allow_close")
    private boolean allowClose;

    /** Allow candidate to reset attempt while in interacting state */
    @Basic(optional=false)
    @Column(name="allow_reset_when_interacting")
    private boolean allowResetWhenInteracting;

    /** Allow candidate to reset attempt while in closed state */
    @Basic(optional=false)
    @Column(name="allow_reset_when_closed")
    private boolean allowResetWhenClosed;

    /** Allow candidate to re-initialize attempt while in interacting state */
    @Basic(optional=false)
    @Column(name="allow_reinit_when_interacting")
    private boolean allowReinitWhenInteracting;

    /** Allow candidate to re-initialize attempt while in closed state */
    @Basic(optional=false)
    @Column(name="allow_reinit_when_closed")
    private boolean allowReinitWhenClosed;

    /** Allow candidate to show solution when in interacting state */
    @Basic(optional=false)
    @Column(name="allow_solution_when_interacting")
    private boolean allowSolutionWhenInteracting;

    /** Allow candidate to show solution when in closed state */
    @Basic(optional=false)
    @Column(name="allow_solution_when_closed")
    private boolean allowSolutionWhenClosed;


    /** Allow candidate to submit comments */
    @Basic(optional=false)
    @Column(name="allow_candidate_comment")
    private boolean allowCandidateComment;

    //------------------------------------------------------------

    public ItemDeliverySettings() {
        super(AssessmentObjectType.ASSESSMENT_ITEM);
    }

    //------------------------------------------------------------

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(final String prompt) {
        this.prompt = prompt;
    }


    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(final Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }


    public boolean isAllowClose() {
        return allowClose;
    }

    public void setAllowClose(final boolean allowClose) {
        this.allowClose = allowClose;
    }


    public boolean isAllowResetWhenInteracting() {
        return allowResetWhenInteracting;
    }

    public void setAllowResetWhenInteracting(final boolean allowReset) {
        this.allowResetWhenInteracting = allowReset;
    }


    public boolean isAllowResetWhenClosed() {
        return allowResetWhenClosed;
    }

    public void setAllowResetWhenClosed(final boolean allowReset) {
        this.allowResetWhenClosed = allowReset;
    }


    public boolean isAllowReinitWhenInteracting() {
        return allowReinitWhenInteracting;
    }

    public void setAllowReinitWhenInteracting(final boolean allowReinit) {
        this.allowReinitWhenInteracting = allowReinit;
    }


    public boolean isAllowReinitWhenClosed() {
        return allowReinitWhenClosed;
    }

    public void setAllowReinitWhenClosed(final boolean allowReinitWhenClosed) {
        this.allowReinitWhenClosed = allowReinitWhenClosed;
    }


    public boolean isAllowSolutionWhenInteracting() {
        return allowSolutionWhenInteracting;
    }

    public void setAllowSolutionWhenInteracting(final boolean allowSolution) {
        this.allowSolutionWhenInteracting = allowSolution;
    }


    public boolean isAllowSolutionWhenClosed() {
        return allowSolutionWhenClosed;
    }

    public void setAllowSolutionWhenClosed(final boolean allowSolutionWhenClosed) {
        this.allowSolutionWhenClosed = allowSolutionWhenClosed;
    }


    public boolean isAllowCandidateComment() {
        return allowCandidateComment;
    }

    public void setAllowCandidateComment(final boolean allowCandidateComment) {
        this.allowCandidateComment = allowCandidateComment;
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return ObjectUtilities.beanToString(this);
    }
}
