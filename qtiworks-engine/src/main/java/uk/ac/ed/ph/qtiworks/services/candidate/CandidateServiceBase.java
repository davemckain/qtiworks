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
package uk.ac.ed.ph.qtiworks.services.candidate;

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.services.CandidateAuditLogger;
import uk.ac.ed.ph.qtiworks.services.CandidateDataService;
import uk.ac.ed.ph.qtiworks.services.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for {@link CandidateRenderingService}, {@link CandidateItemDeliveryService}
 * and {@link CandidateTestDeliveryService}
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public abstract class CandidateServiceBase {

    private static final Logger logger = LoggerFactory.getLogger(CandidateServiceBase.class);

    @Resource
    protected CandidateAuditLogger candidateAuditLogger;

    @Resource
    protected CandidateDataService candidateDataService;

    @Resource
    protected CandidateSessionDao candidateSessionDao;

    @Resource
    protected RequestTimestampContext requestTimestampContext;

    //----------------------------------------------------
    // Access controls

    protected void assertSessionType(final CandidateSession candidateSession, final AssessmentObjectType assessmentObjectType)
            throws CandidateException {
        if (assessmentObjectType != candidateSession.getDelivery().getAssessment().getAssessmentType()) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.SESSION_WRONG_TYPE);
        }
    }

    protected void assertSessionNotTerminated(final CandidateSession candidateSession)
            throws CandidateException {
        if (candidateSession.isTerminated()) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.SESSION_IS_TERMINATED);
        }
    }

    protected CandidateEvent assertSessionEntered(final CandidateSession candidateSession)
            throws CandidateException {
        final CandidateEvent mostRecentEvent = candidateDataService.getMostRecentEvent(candidateSession);
        if (mostRecentEvent==null) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.SESSION_NOT_ENTERED);
        }
        return mostRecentEvent;
    }

    protected void assertCallerMayAccessAuthorInfo(final CandidateSession candidateSession)
            throws CandidateException {
        if (!candidateSession.isAuthorMode()) {
            candidateAuditLogger.logAndThrowCandidateException(candidateSession, CandidateExceptionReason.AUTHOR_INFO_FORBIDDEN);
        }
    }

    //----------------------------------------------------

    protected CandidateSession handleExplosion(final RuntimeException e, final CandidateSession candidateSession) {
        if (e!=null) {
            logger.error("Intercepted RuntimeException so marking CandidateSession session as exploded", e);
        }
        final Date currentTimestamp = requestTimestampContext.getCurrentRequestTimestamp();
        candidateSession.setExploded(true);
        candidateSession.setTerminationTime(currentTimestamp);
        candidateAuditLogger.logExplosion(candidateSession);
        candidateSessionDao.update(candidateSession);
        return candidateSession;
    }
}
