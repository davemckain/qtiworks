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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateException;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateExceptionReason;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateItemDeliveryService;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateTestDeliveryService;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper service for creating new (or reconnecting to existing) {@link CandidateSession}s
 * on a {@link Delivery} or {@link Assessment}.
 *
 * @see CandidateItemDeliveryService
 * @see CandidateTestDeliveryService
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class CandidateSessionStarter {

    @Resource
    private AuditLogger auditLogger;

    @Resource
    private IdentityService identityService;

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private CandidateAuditLogger candidateAuditLogger;

    @Resource
    private CandidateSessionFinisher candidateSessionCloser;

    @Resource
    private CandidateDataService candidateDataService;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    //----------------------------------------------------
    // Low-level session creation

    /**
     * Starts new {@link CandidateSession} for the current thread's {@link User}
     * on the given {@link Delivery}
     * <p>
     * NB: No checks are made on whether the {@link User} should be allowed to start a session
     * on this {@link Delivery}.
     */
    public CandidateSession launchCandidateSession(final Delivery delivery, final boolean authorMode,
            final String lisOutcomeServiceUrl, final String lisResultSourcedid)
            throws CandidateException {
        Assert.notNull(delivery, "delivery");
        final User candidate = identityService.assertCurrentThreadUser();
        return launchCandidateSession(candidate, delivery, authorMode, lisOutcomeServiceUrl, lisResultSourcedid);
    }

    /**
     * Starts new {@link CandidateSession} for the given {@link User} on the given {@link Delivery}
     * <p>
     * NB: No checks are made on whether the {@link User} should be allowed to start a session
     * on this {@link Delivery}.
     */
    public CandidateSession launchCandidateSession(final User candidate, final Delivery delivery,
            final boolean authorMode, final String lisOutcomeServiceUrl,
            final String lisResultSourcedid)
            throws CandidateException {
        Assert.notNull(candidate, "candidate");
        Assert.notNull(delivery, "delivery");

        /* Make sure Candidate's account is not disabled */
        if (candidate.isLoginDisabled()) {
            logAndThrowLaunchException(candidate, delivery, CandidateExceptionReason.USER_ACCOUNT_DISABLED);
        }

        /* Make sure Delivery is runnable */
        if (delivery.getAssessment()==null) {
            logAndThrowLaunchException(candidate, delivery, CandidateExceptionReason.LAUNCH_INCOMPLETE_DELIVERY);
        }

        /* If the candidate already has any non-terminated sessions open for this Delivery,
         * then we shall reconnect to the (most recent) session instead of creating a new one.
         */
        final List<CandidateSession> existingSessions = candidateSessionDao.getNonTerminatedForDeliveryAndCandidate(delivery, candidate);
        if (!existingSessions.isEmpty()) {
            final CandidateSession mostRecent = existingSessions.get(existingSessions.size()-1);
            auditLogger.recordEvent("Reconnected to existing CandidateSession #" + mostRecent.getId()
                    + " on Delivery #" + delivery.getId());
            return mostRecent;
        }

        /* No existing session to reconnect to, so create a new session.
         *
         * (NB: The session will later need to be explicitly entered before anything can be done
         * with it.)
         */
        final CandidateSession candidateSession = new CandidateSession();
        candidateSession.setLisOutcomeServiceUrl(lisOutcomeServiceUrl);
        candidateSession.setLisResultSourcedid(lisResultSourcedid);
        candidateSession.setCandidate(candidate);
        candidateSession.setDelivery(delivery);
        candidateSession.setAuthorMode(authorMode);
        candidateSession.setFinishTime(null);
        candidateSession.setTerminationTime(null);
        candidateSession.setExploded(false);
        candidateSessionDao.persist(candidateSession);
        auditLogger.recordEvent("Created and initialised new CandidateSession #" + candidateSession.getId()
                + " on Delivery #" + delivery.getId());
        return candidateSession;
    }

    private void logAndThrowLaunchException(final User candidate, final Delivery delivery,
            final CandidateExceptionReason reason)
            throws CandidateException {
        candidateAuditLogger.logAndThrowCandidateException(candidate, delivery, reason);
    }
}
