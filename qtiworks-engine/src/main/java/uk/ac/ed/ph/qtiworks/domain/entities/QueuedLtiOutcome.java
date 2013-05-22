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

import uk.ac.ed.ph.jqtiplus.internal.util.BeanToStringOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.PropertyOptions;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Corresponds to an assessment outcome queued up for sending back to the LTI Tool Consumer
 * that invokes QTIWorks. Such an entity will be stored until the results are successfully
 * recorded, or until a pre-defined number of failures occur.
 * <p>
 * We are making these entities to get simple durability in the event of transient issues such as
 * system downtime, network problems, or issues sending outcomes back to the LTI TC.
 *
 * @author David McKain
 */
@Entity
@Table(name="queued_lti_outcomes")
@SequenceGenerator(name="queuedLtiOutcomeSequence", sequenceName="queued_lti_outcome_sequence", initialValue=1, allocationSize=1)
@NamedQueries({
    /* Retrieves all queued outcomes that should be sent next (according to failure logic) */
    @NamedQuery(name="QueuedLtiOutcome.getNextQueuedOutcomes",
            query="SELECT q"
                + "  FROM QueuedLtiOutcome q"
                + "  WHERE q.retryTime IS NULL"
                + "    OR q.retryTime <= CURRENT_TIMESTAMP"
                + "  ORDER BY q.id"),
    /* Retrieves all queued outcomes */
    @NamedQuery(name="QueuedLtiOutcome.getAllQueuedOutcomes",
            query="SELECT q"
                + "  FROM QueuedLtiOutcome q"
                + "  ORDER BY q.id"),

})
public class QueuedLtiOutcome implements BaseEntity, TimestampedOnCreation {

    private static final long serialVersionUID = 7723390303071648772L;

    @Id
    @GeneratedValue(generator="queuedLtiOutcomeSequence")
    @Column(name="qoid")
    private Long qoid;

    /** Timestamp when this outcome was queued up */
    @Basic(optional=false)
    @Column(name="creation_time", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    /** {@link CandidateSession} whose outcomes are being reported */
    @ManyToOne(optional=false, fetch=FetchType.EAGER)
    @JoinColumn(name="xid", updatable=false)
    private CandidateSession candidateSession;

    /** LIS score being recorded, which must be between 0.0 and 1.0 */
    @Basic(optional=false)
    @Column(name="score", updatable=false)
    private double score;

    /** Records the number of failed attempts to send this outcome back to the TC */
    @Basic(optional=false)
    @Column(name="failure_count")
    private int failureCount;

    /**
     * In the event of earlier failure to send this outcome back to the TC,
     * this field will indicate the time to next try to resend this data.
     */
    @Basic(optional=true)
    @Column(name="retry_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date retryTime;

    //------------------------------------------------------------

    @Override
    public Long getId() {
        return qoid;
    }

    @Override
    public void setId(final Long id) {
        this.qoid = id;
    }


    @Override
    public Date getCreationTime() {
        return ObjectUtilities.safeClone(creationTime);
    }

    @Override
    public void setCreationTime(final Date creationTime) {
        this.creationTime = ObjectUtilities.safeClone(creationTime);
    }


    @BeanToStringOptions(PropertyOptions.IGNORE_PROPERTY)
    public CandidateSession getCandidateSession() {
        return candidateSession;
    }

    public void setCandidateSession(final CandidateSession candidateSession) {
        this.candidateSession = candidateSession;
    }


    public double getScore() {
        return score;
    }

    public void setScore(final double score) {
        this.score = score;
    }


    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(final int failureCount) {
        this.failureCount = failureCount;
    }


    public Date getRetryTime() {
        return ObjectUtilities.safeClone(retryTime);
    }

    public void setRetryTime(final Date retryTime) {
        this.retryTime = ObjectUtilities.safeClone(retryTime);
    }

    //------------------------------------------------------------

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(qoid=" + qoid
                + ",score= + score"
                + ",failureCount=" + failureCount
                + ",retryTime=" + retryTime
                + ")";
    }
}
