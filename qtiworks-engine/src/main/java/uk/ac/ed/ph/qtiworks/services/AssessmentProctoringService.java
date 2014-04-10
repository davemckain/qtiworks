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

import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.domain.Privilege;
import uk.ac.ed.ph.qtiworks.services.domain.PrivilegeException;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Top layer service providing some basic "proctoring" functionality for instructors
 * over the candidates for their deliveries.
 * <p>
 * All operations here check authorisation.
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class AssessmentProctoringService {

    @Resource
    private AuditLogger auditLogger;

    @Resource
    private IdentityService identityService;

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private DataDeletionService dataDeletionService;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    //-------------------------------------------------

    public CandidateSession lookupCandidateSession(final long xid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final CandidateSession candidateSession = candidateSessionDao.requireFindById(xid);
        assertCallerMayProctor(candidateSession);
        return candidateSession;
    }

    private User assertCallerMayProctor(final CandidateSession candidateSession)
            throws PrivilegeException {
        final User caller = identityService.assertCurrentThreadUser();
        final User assessmentOwner = candidateSession.getDelivery().getAssessment().getOwnerUser();
        if (!assessmentOwner.equals(caller)) {
            throw new PrivilegeException(caller, Privilege.PROCTOR_SESSION, candidateSession);
        }
        return caller;
    }

    //-------------------------------------------------

    public void terminateCandidateSession(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid);
        if (candidateSession.isTerminated()) {
            auditLogger.recordEvent("CandidateSession #" + xid + " already terminated");
        }
        else {
            terminateCandidateSession(candidateSession);
            auditLogger.recordEvent("Terminated CandidateSession #" + xid);
        }
    }

    private void terminateCandidateSession(final CandidateSession candidateSession) {
        /* NB: We're relying on the fact that result XMLs are stored after each candidate
         * action, so we don't have to record a final final result here.
         */
        if (!candidateSession.isTerminated()) {
            candidateSession.setTerminated(true);
            candidateSessionDao.update(candidateSession);
        }
    }

    public Delivery deleteCandidateSession(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid);
        return deleteCandidateSession(candidateSession);
    }

    public Delivery deleteCandidateSession(final CandidateSession candidateSession) {
        final Delivery delivery = candidateSession.getDelivery();
        dataDeletionService.deleteCandidateSession(candidateSession);
        auditLogger.recordEvent("Deleted CandidateSession #" + candidateSession.getId());
        return delivery;
    }

    //-------------------------------------------------

    public int terminateCandidateSessionsForDelivery(final long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery delivery = assessmentManagementService.lookupDelivery(did);
        int terminatedCount = 0;
        for (final CandidateSession candidateSession : candidateSessionDao.getForDelivery(delivery)) {
            if (!candidateSession.isTerminated()) {
                terminateCandidateSession(candidateSession);
                terminatedCount++;
            }
        }
        auditLogger.recordEvent("Terminated remaining " + terminatedCount + " CandidateSessions for Delivery #" + did);
        return terminatedCount;
    }

    public int deleteCandidateSessionsForDelivery(final long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery delivery = assessmentManagementService.lookupDelivery(did);
        final int deletedCount = dataDeletionService.deleteCandidateSessions(delivery);
        auditLogger.recordEvent("Deleted all " + deletedCount + " CandidateSessions for Delivery #" + did);
        return deletedCount;
    }

}
