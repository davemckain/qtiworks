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

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.Privilege;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiLaunchType;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateItemDeliveryService;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateTestDeliveryService;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.net.URI;
import java.net.URISyntaxException;
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
    private CandidateSessionCloser candidateSessionCloser;

    @Resource
    private CandidateDataService candidateDataService;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    //-------------------------------------------------
    // System samples

    public CandidateSession launchSystemSampleSession(final long aid, final String exitUrl)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery sampleDelivery = lookupSystemSampleDelivery(aid);
        return launchCandidateSession(sampleDelivery, true, exitUrl, null, null);
    }

    private Delivery lookupSystemSampleDelivery(final long aid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final Assessment assessment = lookupSampleAssessment(aid);
        final List<Delivery> systemDemoDeliveries = deliveryDao.getForAssessmentAndType(assessment, DeliveryType.SYSTEM_DEMO);
        if (systemDemoDeliveries.size()!=1) {
            throw new QtiWorksLogicException("Expected system sample Assessment with ID " + aid
                    + " to have exactly 1 system demo deliverable associated with it");
        }
        return systemDemoDeliveries.get(0);
    }

    private Assessment lookupSampleAssessment(final long aid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final Assessment assessment = assessmentDao.requireFindById(aid);
        final User caller = identityService.getCurrentThreadUser();
        if (!assessment.isPublic() || assessment.getSampleCategory()==null) {
            throw new PrivilegeException(caller, Privilege.LAUNCH_ASSESSMENT_AS_SAMPLE, assessment);
        }
        return assessment;
    }

    //----------------------------------------------------
    // Single delivery launches (currently LTI only)

    /**
     * Starts a new {@link CandidateSession} for the (LTI) candidate {@link User} accessing a
     * link-level launch on the {@link Delivery} having the given ID (did).
     * <p>
     * Access controls are checked on the {@link Delivery}.
     */
    public CandidateSession launchLinkLevelLtiCandidateSession(final LtiUser candidate,
            final String exitUrl, final String lisOutcomeServiceUrl, final String lisResultSourcedid)
            throws PrivilegeException {
        /* Make sure this is the correct type of user */
        Assert.notNull(candidate, "candidate");
        if (candidate.getLtiLaunchType()!=LtiLaunchType.LINK) {
            throw new IllegalArgumentException("Candidate LtiUser must be of type " + LtiLaunchType.LINK);
        }

        /* Extract Delivery to be launched */
        final Delivery delivery = candidate.getDelivery();

        /* Make sure delivery is open */
        if (!delivery.isOpen()) {
            throw new PrivilegeException(candidate, Privilege.LAUNCH_CLOSED_DELIVERY, delivery);
        }

        /* Launch the session */
        return launchCandidateSession(candidate, delivery,
                false, /* Never author mode here */
                sanitiseExitUrl(exitUrl), /* Don't necessarily trust exitUrl passed from TC */
                lisOutcomeServiceUrl, lisResultSourcedid);
    }

    public CandidateSession launchDomainLevelLtiCandidateSession(final LtiUser candidate, final LtiResource ltiResource,
            final String exitUrl, final String lisOutcomeServiceUrl, final String lisResultSourcedid)
            throws PrivilegeException {
        Assert.notNull(candidate, "candidate");
        Assert.notNull(ltiResource, "ltiResource");
        if (candidate.getLtiLaunchType()!=LtiLaunchType.DOMAIN) {
            throw new IllegalArgumentException("Candidate LtiUser must be of type " + LtiLaunchType.DOMAIN);
        }

        /* Extract Delivery to be launched from LtiResource */
        final Delivery delivery = ltiResource.getDelivery();

        /* Make sure delivery is open */
        if (!delivery.isOpen()) {
            throw new PrivilegeException(candidate, Privilege.LAUNCH_CLOSED_DELIVERY, delivery);
        }

        /* Will use author mode if candidate is an instructor */
        final boolean authorMode = candidate.getUserRole()==UserRole.INSTRUCTOR;

        /* Launch the session */
        return launchCandidateSession(candidate, delivery, authorMode,
                sanitiseExitUrl(exitUrl), /* Don't necessarily trust exitUrl passed from TC */
                lisOutcomeServiceUrl, lisResultSourcedid);
    }

    //----------------------------------------------------
    // Low-level session creation

    /**
     * Starts new {@link CandidateSession} for the given {@link Delivery}
     * <p>
     * NO ACCESS controls are checked on the {@link Delivery}
     */
    public CandidateSession launchCandidateSession(final Delivery delivery, final boolean authorMode,
            final String exitUrl, final String lisOutcomeServiceUrl, final String lisResultSourcedid)
            throws PrivilegeException {
        Assert.notNull(delivery, "delivery");
        final User candidate = identityService.getCurrentThreadUser();
        return launchCandidateSession(candidate, delivery, authorMode, exitUrl, lisOutcomeServiceUrl, lisResultSourcedid);
    }

    /**
     * Starts new {@link CandidateSession} for the given {@link User} on the given {@link Delivery}
     * <p>
     * NO ACCESS controls are checked on the {@link User} and {@link Delivery}
     */
    public CandidateSession launchCandidateSession(final User candidate, final Delivery delivery,
            final boolean authorMode, final String exitUrl, final String lisOutcomeServiceUrl,
            final String lisResultSourcedid)
            throws PrivilegeException {
        Assert.notNull(candidate, "candidate");
        Assert.notNull(delivery, "delivery");

        /* Make sure Candidate's account is not disabled */
        if (candidate.isLoginDisabled()) {
            throw new PrivilegeException(candidate, Privilege.USER_ACCOUNT_ENABLED, delivery);
        }

        /* Make sure Delivery is runnable */
        if (delivery.getAssessment()==null) {
            throw new PrivilegeException(candidate, Privilege.LAUNCH_INCOMPLETE_DELIVERY, delivery);
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
        candidateSession.setSessionToken(ServiceUtilities.createRandomAlphanumericToken(DomainConstants.CANDIDATE_SESSION_TOKEN_LENGTH));
        candidateSession.setExitUrl(exitUrl);
        candidateSession.setLisOutcomeServiceUrl(lisOutcomeServiceUrl);
        candidateSession.setLisResultSourcedid(lisResultSourcedid);
        candidateSession.setCandidate(candidate);
        candidateSession.setDelivery(delivery);
        candidateSession.setAuthorMode(authorMode);
        candidateSession.setClosed(false);
        candidateSession.setTerminated(false);
        candidateSession.setExploded(false);
        candidateSessionDao.persist(candidateSession);
        auditLogger.recordEvent("Created and initialised new CandidateSession #" + candidateSession.getId()
                + " on Delivery #" + delivery.getId());
        return candidateSession;
    }

    private String sanitiseExitUrl(final String exitUrl) {
        if (exitUrl==null) {
            return null;
        }
        /* Allow valid http:// or https:// URIs only */
        final URI exitUrlUri;
        try {
            exitUrlUri = new URI(exitUrl);
        }
        catch (final URISyntaxException e) {
            auditLogger.recordEvent("Rejecting exit URL " + exitUrl + " - not a URI");
            return null;
        }
        final String scheme = exitUrlUri.getScheme();
        if (!scheme.equals("http") && !scheme.equals("https")) {
            auditLogger.recordEvent("Rejecting exit URL " + exitUrl + " - only accepting http and https schemes");
        }
        /* If still here, then OK */
        return exitUrl;
    }
}
