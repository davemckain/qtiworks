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
package uk.ac.ed.ph.qtiworks.web.candidate;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.DeliveryType;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiLaunchType;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiUser;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.domain.entities.UserRole;
import uk.ac.ed.ph.qtiworks.services.AuditLogger;
import uk.ac.ed.ph.qtiworks.services.CandidateAuditLogger;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;
import uk.ac.ed.ph.qtiworks.services.IdentityService;
import uk.ac.ed.ph.qtiworks.services.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateException;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateExceptionReason;
import uk.ac.ed.ph.qtiworks.services.dao.AssessmentDao;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.web.view.ViewUtilities;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper service for launching and authenticating users to {@link CandidateSession}s in various
 * ways.
 *
 * @see CandidateSessionStarter
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class CandidateSessionLaunchService {

    @Resource
    private AuditLogger auditLogger;

    @Resource
    private IdentityService identityService;

    @Resource
    private CandidateAuditLogger candidateAuditLogger;

    @Resource
    private AssessmentDao assessmentDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private CandidateSessionStarter candidateSessionStarter;

    //-------------------------------------------------
    // Anonymous session launching

    public CandidateSessionTicket launchAnonymousCandidateSession(final HttpSession httpSession,
            final Delivery delivery, final String sessionExitReturnUrl)
            throws CandidateException {
        Assert.notNull(httpSession, "httpSession");
        Assert.notNull(delivery, "delivery");

        /* In this case the "candidate" should be the user who owns the Delivery, so let's check this here */
        final User candidate = identityService.assertCurrentThreadUser();
        final Assessment assessment = delivery.getAssessment();
        if (!assessment.getOwnerUser().equals(candidate)) {
            logAndThrowLaunchException(candidate, delivery, CandidateExceptionReason.LAUNCH_ASSESSMENT_NO_ACCESS);
        }
        return launchCandidateSession(httpSession, candidate, delivery, true, sessionExitReturnUrl, null, null);
    }

    //-------------------------------------------------
    // Session launching for web services
    // (This uses a slightly different 2-step launch process that avoids cookies.)

    public CandidateSessionTicket launchWebServiceCandidateSession(final HttpSession httpSession,
            final long did, final String deliveryToken, final String sessionExitReturnUrl)
            throws CandidateException, DomainEntityNotFoundException {
        Assert.notNull(httpSession, "httpSession");

        /* Look up Delivery and compare with token */
        final Delivery delivery = deliveryDao.requireFindById(did);
        final Assessment assessment = delivery.getAssessment();
        final User candidate = identityService.assertCurrentThreadUser();
        final String deliveryTokenVerify = generateWebServiceDeliveryToken(delivery);
        if (!deliveryTokenVerify.equals(deliveryToken)) {
            logAndThrowLaunchException(candidate, delivery, CandidateExceptionReason.LAUNCH_ASSESSMENT_NO_ACCESS);
        }

        /* We don't check ownership of the Assessment here, since the user launching the
         * assessment (i.e. the end user) will be different from the one that initially created it
         * (i.e. the web service).
         * However, we will at least make sure that the Assessment is owned by an anonymous user.
         */
        if (!assessment.getOwnerUser().isAnonymous()) {
            logAndThrowLaunchException(candidate, delivery, CandidateExceptionReason.LAUNCH_ASSESSMENT_NO_ACCESS);
        }

        /* OK then, we can finally launch a new CandidateSession */
        return launchCandidateSession(httpSession, candidate, delivery, true, sessionExitReturnUrl, null, null);
    }

    /**
     * Generates a "delivery token" for this launch. This is appended the WS launch URL to make it
     * harder (but obviously not impossible) for a 3rd party to guess the launch URL.
     */
    public String generateWebServiceDeliveryToken(final Delivery delivery) {
        final String tokenData = "QTIWorksWS/"
                + delivery.getAssessment().getId()
                + "/" + delivery.getAssessment().getOwnerUser().getId()
                + "/" + ViewUtilities.getDateAndTimeFormat().format(delivery.getCreationTime());
        return ServiceUtilities.computeSha1Digest(tokenData);
    }

    //-------------------------------------------------
    // System sample launching

    public CandidateSessionTicket launchSystemSampleSession(final HttpSession httpSession,
            final long aid, final String sessionExitReturnUrl)
            throws DomainEntityNotFoundException, CandidateException {
        Assert.notNull(httpSession, "httpSession");

        final User candidate = identityService.assertCurrentThreadUser();
        final Delivery sampleDelivery = lookupSystemSampleDelivery(aid);
        return launchCandidateSession(httpSession, candidate, sampleDelivery, true, sessionExitReturnUrl, null, null);
    }

    private Delivery lookupSystemSampleDelivery(final long aid)
            throws DomainEntityNotFoundException, CandidateException {
        final Assessment assessment = lookupSampleAssessment(aid);
        final List<Delivery> systemDemoDeliveries = deliveryDao.getForAssessmentAndType(assessment, DeliveryType.SYSTEM_DEMO);
        if (systemDemoDeliveries.size()!=1) {
            throw new QtiWorksLogicException("Expected system sample Assessment with ID " + aid
                    + " to have exactly 1 system demo deliverable associated with it");
        }
        return systemDemoDeliveries.get(0);
    }

    private Assessment lookupSampleAssessment(final long aid)
            throws DomainEntityNotFoundException, CandidateException {
        final User caller = identityService.assertCurrentThreadUser();
        final Assessment assessment = assessmentDao.requireFindById(aid);
        if (assessment.getSampleCategory()==null) {
            logAndThrowLaunchException(caller, assessment, CandidateExceptionReason.LAUNCH_ASSESSMENT_AS_SAMPLE);
        }
        return assessment;
    }

    //----------------------------------------------------
    // Instructor mode

    public CandidateSessionTicket launchInstructorTrialSession(final HttpSession httpSession,
            final User candidate, final Delivery delivery, final boolean authorMode,
            final String sessionExitReturnUrl)
            throws CandidateException {
        Assert.notNull(httpSession, "httpSession");
        return launchCandidateSession(httpSession, candidate, delivery, authorMode, sessionExitReturnUrl, null, null);
    }

    //----------------------------------------------------
    // LTI launches

    /**
     * Starts a new {@link CandidateSession} for the (LTI) candidate {@link User} accessing a
     * link-level launch on the {@link Delivery} having the given ID (did).
     * <p>
     * Access controls are checked on the {@link Delivery}.
     */
    public CandidateSessionTicket launchLinkLevelLtiCandidateSession(final HttpSession httpSession,
            final LtiUser candidate, final String sessionExitReturnUrl,
            final String lisOutcomeServiceUrl, final String lisResultSourcedid)
            throws CandidateException {
        Assert.notNull(httpSession, "httpSession");
        Assert.notNull(candidate, "candidate");

        /* Make sure this is the correct type of user */
        if (candidate.getLtiLaunchType()!=LtiLaunchType.LINK) {
            throw new IllegalArgumentException("Candidate LtiUser must be of type " + LtiLaunchType.LINK);
        }

        /* Extract Delivery to be launched */
        final Delivery delivery = candidate.getDelivery();

        /* Make sure delivery is open to candidates */
        if (!delivery.isOpen()) {
            logAndThrowLaunchException(candidate, delivery, CandidateExceptionReason.LAUNCH_CLOSED_DELIVERY);
        }

        /* Now launch session */
        return launchCandidateSession(httpSession, candidate, delivery,
                false /* Never use author mode here */,
                sanitiseReturnUrl(sessionExitReturnUrl) /* Return URL might not be trustworthy */,
                lisOutcomeServiceUrl, lisResultSourcedid);
    }

    public CandidateSessionTicket launchDomainLevelLtiCandidateSession(final HttpSession httpSession,
            final LtiUser candidate, final LtiResource ltiResource,
            final String sessionExitReturnUrl, final String lisOutcomeServiceUrl, final String lisResultSourcedid)
            throws CandidateException {
        Assert.notNull(httpSession, "httpSession");
        Assert.notNull(candidate, "candidate");
        Assert.notNull(ltiResource, "ltiResource");
        if (candidate.getLtiLaunchType()!=LtiLaunchType.DOMAIN) {
            throw new IllegalArgumentException("Candidate LtiUser must be of type " + LtiLaunchType.DOMAIN);
        }

        /* Extract Delivery to be launched from LtiResource */
        final Delivery delivery = ltiResource.getDelivery();

        /* Make sure delivery is open to candidates */
        if (!delivery.isOpen()) {
            logAndThrowLaunchException(candidate, delivery, CandidateExceptionReason.LAUNCH_CLOSED_DELIVERY);
        }

        /* Will use author mode if candidate is an instructor */
        final boolean authorMode = candidate.getUserRole()==UserRole.INSTRUCTOR;

        /* Now launch session */
        return launchCandidateSession(httpSession, candidate, delivery, authorMode,
                sanitiseReturnUrl(sessionExitReturnUrl) /* Return URL might not be trustworthy */,
                lisOutcomeServiceUrl, lisResultSourcedid);
    }

    //----------------------------------------------------
    // Low-level launches.
    // NB: Caller should have checked that candidate is allowed to launch session before here,
    // and the sessionExitReturnUrl should have been sanitised (if appropriate) beforehand too.

    private CandidateSessionTicket launchCandidateSession(final HttpSession httpSession,
            final User candidate, final Delivery delivery, final boolean authorMode,
            final String sessionExitReturnUrl,
            final String lisOutcomeServiceUrl, final String lisResultSourcedid)
            throws CandidateException {
        /* Create/reuse session */
        final CandidateSession candidateSession = candidateSessionStarter.launchCandidateSession(candidate, delivery,
                authorMode, lisOutcomeServiceUrl, lisResultSourcedid);

        /* Create XSRF token */
        final String xsrfToken = ServiceUtilities.createRandomAlphanumericToken(DomainConstants.XSRF_TOKEN_LENGTH);

        /* Authenticate this user to access this CandidateSession */
        final CandidateSessionTicket candidateSessionTicket = new CandidateSessionTicket(xsrfToken,
                candidate.getId(),
                candidateSession.getId(),
                delivery.getAssessment().getAssessmentType(),
                sessionExitReturnUrl);
        CandidateSessionAuthenticationFilter.authenticateUserForHttpSession(httpSession, candidateSessionTicket);

        /* Caller should now issue appropriate redirect to session... */
        return candidateSessionTicket;
    }

    private void logAndThrowLaunchException(final User candidate, final Delivery delivery,
            final CandidateExceptionReason reason)
            throws CandidateException {
        candidateAuditLogger.logAndThrowCandidateException(candidate, delivery, reason);
    }

    private void logAndThrowLaunchException(final User candidate, final Assessment assessment,
            final CandidateExceptionReason reason)
            throws CandidateException {
        candidateAuditLogger.logAndThrowCandidateException(candidate, assessment, reason);
    }

    private String sanitiseReturnUrl(final String sessionExitReturnUrl) {
        if (sessionExitReturnUrl==null) {
            return null;
        }
        /* Allow valid http:// or https:// URIs only */
        final URI exitUrlUri;
        try {
            exitUrlUri = new URI(sessionExitReturnUrl);
        }
        catch (final URISyntaxException e) {
            auditLogger.recordEvent("Rejecting return URL " + sessionExitReturnUrl + " - not a URI");
            return null;
        }
        final String scheme = exitUrlUri.getScheme();
        if (!scheme.equals("http") && !scheme.equals("https")) {
            auditLogger.recordEvent("Rejecting return URL " + sessionExitReturnUrl + " - only accepting http and https schemes");
        }
        /* If still here, then OK */
        return sessionExitReturnUrl;
    }
}
