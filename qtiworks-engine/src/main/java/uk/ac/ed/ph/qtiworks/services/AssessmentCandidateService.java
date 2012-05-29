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
package uk.ac.ed.ph.qtiworks.services;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;
import uk.ac.ed.ph.qtiworks.base.services.Auditor;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.Privilege;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.binding.ItemSesssionStateXmlMarshaller;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemAttemptDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemEventDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemSessionDao;
import uk.ac.ed.ph.qtiworks.domain.dao.ItemDeliveryDao;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateFileSubmission;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemAttempt;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemResponse;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSessionState;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.ResponseLegality;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.ItemRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.RenderingMode;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.utils.XmlUtilities;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.FileResponseData;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.ResponseData.ResponseDataType;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;

import uk.ac.ed.ph.snuggletex.XMLStringOutputOptions;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Service the manages the real-time delivery of an {@link Assessment}
 * to a particular candidate {@link User}.
 * <p>
 * DEV NOTE: MVC Controllers should use the public methods taking IDs to ensure
 * that work is done in a single transaction and avoid detached entities.
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class AssessmentCandidateService {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentCandidateService.class);

    @Resource
    private Auditor auditor;

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private IdentityContext identityContext;

    @Resource
    private QtiSerializer qtiSerializer;

    @Resource
    private AssessmentObjectManagementService assessmentObjectManagementService;

    @Resource
    private AssessmentRenderer assessmentRenderer;

    @Resource
    private CandidateUploadService candidateUploadService;

    @Resource
    private JqtiExtensionManager jqtiExtensionManager;

    @Resource
    private ItemDeliveryDao itemDeliveryDao;

    @Resource
    private CandidateItemSessionDao candidateItemSessionDao;

    @Resource
    private CandidateItemEventDao candidateItemEventDao;

    @Resource
    private CandidateItemAttemptDao candidateItemAttemptDao;

    //----------------------------------------------------
    // Candidate delivery access

    public ItemDelivery lookupItemDelivery(final long did)
            throws DomainEntityNotFoundException, PrivilegeException {
        final ItemDelivery itemDelivery = itemDeliveryDao.requireFindById(did);
        ensureCandidateMayAccess(itemDelivery);
        return itemDelivery;
    }

    /**
     * FIXME: Currently we're only allowing access to public or owned deliveries! This will need
     * to be relaxed in order to allow "real" deliveries to be done.
     */
    private User ensureCandidateMayAccess(final ItemDelivery itemDelivery)
            throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!itemDelivery.isOpen()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_ACCESS_ITEM_DELIVERY, itemDelivery);
        }
        final Assessment assessment = itemDelivery.getAssessmentPackage().getAssessment();
        if (!assessment.isPublic() && !caller.equals(assessment.getOwner())) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_ACCESS_ITEM_DELIVERY, itemDelivery);
        }
        return caller;
    }

    //----------------------------------------------------
    // Session creation and initialisation

    /**
     * Starts a new {@link CandidateItemSession} for the {@link ItemDelivery}
     * having the given ID (did).
     *
     * @param did
     * @return
     * @throws RuntimeValidationException
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     */
    public CandidateItemSession createCandidateSession(final long did)
            throws RuntimeValidationException, PrivilegeException, DomainEntityNotFoundException {
        final ItemDelivery itemDelivery = lookupItemDelivery(did);
        return createCandidateSession(itemDelivery);
    }

    /**
     * Starts new {@link CandidateItemSession} for the given {@link ItemDelivery}
     * @param itemDelivery
     * @return
     * @throws RuntimeValidationException
     */
    private CandidateItemSession createCandidateSession(final ItemDelivery itemDelivery)
            throws RuntimeValidationException {
        Assert.ensureNotNull(itemDelivery, "itemDelivery");

        /* Create fresh JQTI+ state Object */
        final ItemSessionState itemSessionState = new ItemSessionState();

        /* Initialise state */
        final ItemSessionController itemSessionController = createItemSessionController(itemDelivery, itemSessionState);
        itemSessionController.initialize();

        /* Check whether an attempt is allowed. This is a bit pathological here,
         * but it makes sense to be consistent.
         */
        final boolean attemptAllowed = itemSessionController.isAttemptAllowed(itemDelivery.getMaxAttempts());

        /* Create new session and put into appropriate state */
        final CandidateItemSession candidateSession = new CandidateItemSession();
        candidateSession.setCandidate(identityContext.getCurrentThreadEffectiveIdentity());
        candidateSession.setItemDelivery(itemDelivery);
        candidateSession.setState(attemptAllowed ? CandidateSessionState.INTERACTING : CandidateSessionState.CLOSED);
        candidateItemSessionDao.persist(candidateSession);

        /* Record initialisation event */
        recordEvent(candidateSession, CandidateItemEventType.INIT, itemSessionState);

        auditor.recordEvent("Created and initialised new CandidateItemSession #" + candidateSession.getId()
                + " on ItemDelivery #" + itemDelivery.getId());
        return candidateSession;
    }

    private ItemSessionController createItemSessionController(final ItemDelivery itemDelivery, final ItemSessionState itemSessionState) {
        /* Get the resolved JQTI+ Object for the underlying package */
        final AssessmentPackage assessmentPackage = itemDelivery.getAssessmentPackage();
        final ResolvedAssessmentItem resolvedAssessmentItem = assessmentObjectManagementService.getResolvedAssessmentItem(assessmentPackage);

        return new ItemSessionController(jqtiExtensionManager, resolvedAssessmentItem, itemSessionState);
    }

    //----------------------------------------------------
    // Session access after creation

    /**
     * Looks up the {@link CandidateItemSession} having the given ID (xid)
     * and ensures the caller has access to it.
     *
     * @param xid
     * @return
     * @throws DomainEntityNotFoundException
     * @throws PrivilegeException
     */
    public CandidateItemSession lookupCandidateSession(final long xid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final CandidateItemSession session = candidateItemSessionDao.requireFindById(xid);
        ensureCallerMayAccess(session);
        return session;
    }

    /**
     * (Currently we're restricting access to sessions to their owners.)
     */
    private User ensureCallerMayAccess(final CandidateItemSession candidateSession) throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!caller.equals(candidateSession.getCandidate())) {
            /* TOOD: Only allow access to session owner */
            throw new PrivilegeException(caller, Privilege.ACCESS_CANDIDATE_SESSION, candidateSession);
        }
        if (!candidateSession.getItemDelivery().isOpen()) {
            /* No access when delivery is closed */
            throw new PrivilegeException(caller, Privilege.ACCESS_CANDIDATE_SESSION, candidateSession);
        }
        return caller;
    }

    private User ensureSessionNotTerminated(final CandidateItemSession candidateSession) throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (candidateSession.getState()==CandidateSessionState.TERMINATED) {
            /* No access when session has been is closed */
            throw new PrivilegeException(caller, Privilege.CANDIDATE_ACCESS_TERMINATED_SESSION, candidateSession);
        }
        return caller;
    }

    //----------------------------------------------------
    // Attempt

    public CandidateItemAttempt handleAttempt(final long xid,
            final Map<Identifier, StringResponseData> stringResponseMap,
            final Map<Identifier, MultipartFile> fileResponseMap)
            throws RuntimeValidationException, PrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateSession = lookupCandidateSession(xid);
        return handleAttempt(candidateSession, stringResponseMap, fileResponseMap);
    }

    public CandidateItemAttempt handleAttempt(final CandidateItemSession candidateSession,
            final Map<Identifier, StringResponseData> stringResponseMap,
            final Map<Identifier, MultipartFile> fileResponseMap)
            throws RuntimeValidationException, PrivilegeException {
        Assert.ensureNotNull(candidateSession, "candidateSession");
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();

        /* Make sure an attempt is allowed */
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (candidateSession.getState()!=CandidateSessionState.INTERACTING) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_MAKE_ATTEMPT, candidateSession);
        }

        /* Build response map in required format for JQTI+.
         * NB: The following doesn't test for duplicate keys in the two maps. I'm not sure
         * it's worth the effort.
         */
        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        if (stringResponseMap!=null) {
            for (final Entry<Identifier, StringResponseData> stringResponseEntry : stringResponseMap.entrySet()) {
                final Identifier identifier = stringResponseEntry.getKey();
                final StringResponseData stringResponseData = stringResponseEntry.getValue();
                responseMap.put(identifier, stringResponseData);
            }
        }
        final Map<Identifier, CandidateFileSubmission> fileSubmissionMap = new HashMap<Identifier, CandidateFileSubmission>();
        if (fileResponseMap!=null) {
            for (final Entry<Identifier, MultipartFile> fileResponseEntry : fileResponseMap.entrySet()) {
                final Identifier identifier = fileResponseEntry.getKey();
                final MultipartFile multipartFile = fileResponseEntry.getValue();
                final CandidateFileSubmission fileSubmission = candidateUploadService.importFileSubmission(candidateSession, multipartFile);
                final FileResponseData fileResponseData = new FileResponseData(new File(fileSubmission.getStoredFilePath()), fileSubmission.getContentType());
                responseMap.put(identifier, fileResponseData);
                fileSubmissionMap.put(identifier, fileSubmission);
            }
        }

        /* Build Map of responses in appropriate entity form */
        final CandidateItemAttempt candidateItemAttempt = new CandidateItemAttempt();
        final Map<Identifier, CandidateItemResponse> responseEntityMap = new HashMap<Identifier, CandidateItemResponse>();
        final Set<CandidateItemResponse> candidateItemResponses = new HashSet<CandidateItemResponse>();
        for (final Entry<Identifier, ResponseData> responseEntry : responseMap.entrySet()) {
            final Identifier responseIdentifier = responseEntry.getKey();
            final ResponseData responseData = responseEntry.getValue();

            final CandidateItemResponse candidateItemResponse = new CandidateItemResponse();
            candidateItemResponse.setResponseIdentifier(responseIdentifier.toString());
            candidateItemResponse.setAttempt(candidateItemAttempt);
            candidateItemResponse.setResponseType(responseData.getType());
            candidateItemResponse.setResponseLegality(ResponseLegality.VALID); /* (May change this below) */
            switch (responseData.getType()) {
                case STRING:
                    candidateItemResponse.setStringResponseData(((StringResponseData) responseData).getResponseData());
                    break;

                case FILE:
                    candidateItemResponse.setFileSubmission(fileSubmissionMap.get(responseIdentifier));
                    break;

                default:
                    throw new QtiWorksLogicException("Unexpected switch case: " + responseData.getType());
            }
            responseEntityMap.put(responseIdentifier, candidateItemResponse);
            candidateItemResponses.add(candidateItemResponse);
        }
        candidateItemAttempt.setResponses(candidateItemResponses);

        /* Get current JQTI state and create JQTI controller */
        final CandidateItemEvent mostRecentEvent = getMostRecentEvent(candidateSession);
        final ItemSessionController itemSessionController = createItemSessionController(mostRecentEvent);

        /* Attempt to bind responses */
        final Set<Identifier> badResponseIdentifiers = itemSessionController.bindResponses(responseMap);

        /* Record any responses that failed to bind */
        final boolean allResponsesBound = badResponseIdentifiers.isEmpty();
        for (final Identifier badResponseIdentifier : badResponseIdentifiers) {
            responseEntityMap.get(badResponseIdentifier).setResponseLegality(ResponseLegality.BAD);
        }

        /* Now validate the responses according to any constraints specified by the interactions */
        boolean allResponsesValid = false;
        if (allResponsesBound) {
            final Set<Identifier> invalidResponseIdentifiers = itemSessionController.validateResponses();
            allResponsesValid = invalidResponseIdentifiers.isEmpty();
            if (!allResponsesValid) {
                /* Some responses not valid, so note these down */
                for (final Identifier invalidResponseIdentifier : invalidResponseIdentifiers) {
                    responseEntityMap.get(invalidResponseIdentifier).setResponseLegality(ResponseLegality.INVALID);
                }
            }

            /* Invoke response processing (only if responses are valid) */
            if (allResponsesValid) {
                itemSessionController.processResponses();
            }
        }

        /* Record resulting attempt and event */
        final CandidateItemEventType eventType = allResponsesBound ?
            (allResponsesValid ? CandidateItemEventType.ATTEMPT_VALID : CandidateItemEventType.ATTEMPT_INVALID)
            : CandidateItemEventType.ATTEMPT_BAD;
        final CandidateItemEvent candidateItemEvent = recordEvent(candidateSession, eventType, itemSessionController.getItemSessionState());

        candidateItemAttempt.setEvent(candidateItemEvent);
        candidateItemAttemptDao.persist(candidateItemAttempt);

        /* Update session state */
        final boolean attemptAllowed = itemSessionController.isAttemptAllowed(itemDelivery.getMaxAttempts());
        candidateSession.setState(attemptAllowed ? CandidateSessionState.INTERACTING : CandidateSessionState.CLOSED);
        candidateItemSessionDao.update(candidateSession);

        auditor.recordEvent("Recorded candidate attempt #" + candidateItemAttempt.getId()
                + " on session #" + candidateSession.getId()
                + " on delivery #" + candidateSession.getItemDelivery().getId());
        return candidateItemAttempt;
    }

    //----------------------------------------------------
    // Session close(by candidate)

    /**
     * Closes the {@link CandidateItemSession} having the given ID (xid), moving it
     * into {@link CandidateSessionState#CLOSED} state.
     *
     * @param xid
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     */
    public CandidateItemSession closeCandidateSession(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateSession = lookupCandidateSession(xid);
        return closeCandidateSession(candidateSession);
    }

    public CandidateItemSession closeCandidateSession(final CandidateItemSession candidateSession)
            throws PrivilegeException {
        Assert.ensureNotNull(candidateSession, "candidateSession");

        /* Check this is allowed in current state */
        final User caller = ensureSessionNotTerminated(candidateSession);
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        if (candidateSession.getState()==CandidateSessionState.CLOSED) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_CLOSE_SESSION_WHEN_CLOSED, candidateSession);
        }
        else if (!itemDelivery.isAllowClose()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_CLOSE_SESSION_WHEN_INTERACTING, candidateSession);
        }

        /* Record event */
        final ItemSessionState itemSessionState = getCurrentItemSessionState(candidateSession);
        recordEvent(candidateSession, CandidateItemEventType.CLOSED, itemSessionState);

        /* Update state */
        candidateSession.setState(CandidateSessionState.CLOSED);
        candidateItemSessionDao.update(candidateSession);

        auditor.recordEvent("Candidate ended session #" + candidateSession.getId());
        return candidateSession;
    }

    //----------------------------------------------------
    // Session reinit

    /**
     * Re-initialises the {@link CandidateItemSession} having the given ID (xid), returning the
     * updated {@link CandidateItemSession}. At QTI level, this reruns template processing, so
     * randomised values will change as a result of this process.
     *
     * @param xid
     * @return
     * @throws RuntimeValidationException
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     */
    public CandidateItemSession reinitCandidateSession(final long xid)
            throws RuntimeValidationException, PrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateSession = lookupCandidateSession(xid);
        return reinitCandidateSession(candidateSession);
    }

    public CandidateItemSession reinitCandidateSession(final CandidateItemSession candidateSession)
            throws RuntimeValidationException, PrivilegeException {
        Assert.ensureNotNull(candidateSession, "candidateSession");

        /* Make sure caller may reinit the session */
        final User caller = ensureSessionNotTerminated(candidateSession);
        final CandidateSessionState candidateSessionState = candidateSession.getState();
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        if (candidateSessionState==CandidateSessionState.INTERACTING && !itemDelivery.isAllowReinitWhenInteracting()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_REINIT_SESSION_WHEN_INTERACTING, candidateSession);
        }
        else if (candidateSessionState==CandidateSessionState.CLOSED && !itemDelivery.isAllowReinitWhenClosed()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_REINIT_SESSION_WHEN_CLOSED, candidateSession);
        }

        /* Create fresh JQTI+ state */
        final ItemSessionState itemSessionState = new ItemSessionState();

        /* Get the resolved JQTI+ Object for the underlying package */
        final ItemSessionController itemSessionController = createItemSessionController(itemDelivery, itemSessionState);

        /* Initialise state */
        itemSessionController.initialize();

        /* Record event */
        recordEvent(candidateSession, CandidateItemEventType.REINIT, itemSessionState);

        /* Update state */
        final boolean attemptAllowed = itemSessionController.isAttemptAllowed(itemDelivery.getMaxAttempts());
        candidateSession.setState(attemptAllowed ? CandidateSessionState.INTERACTING : CandidateSessionState.CLOSED);
        candidateItemSessionDao.update(candidateSession);

        auditor.recordEvent("Candidate re-intialized session #" + candidateSession.getId());
        return candidateSession;
    }

    //----------------------------------------------------
    // Session reset

    /**
     * Resets the {@link CandidateItemSession} having the given ID (xid), returning the
     * updated {@link CandidateItemSession}. This takes the session back to the state it
     * was in immediately after the last {@link CandidateItemEvent#REINIT_WHEN_INTERACTING} (if applicable),
     * or after the original {@link CandidateItemEvent#INIT}.
     *
     * @param xid
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     */
    public CandidateItemSession resetCandidateSession(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateSession = lookupCandidateSession(xid);
        return resetCandidateSession(candidateSession);
    }

    public CandidateItemSession resetCandidateSession(final CandidateItemSession candidateSession)
            throws PrivilegeException {
        Assert.ensureNotNull(candidateSession, "candidateSession");

        /* Make sure caller may reset the session */
        final User caller = ensureSessionNotTerminated(candidateSession);
        final CandidateSessionState candidateSessionState = candidateSession.getState();
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        if (candidateSessionState==CandidateSessionState.INTERACTING && !itemDelivery.isAllowResetWhenInteracting()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_RESET_SESSION_WHEN_INTERACTING, candidateSession);
        }
        else if (candidateSessionState==CandidateSessionState.CLOSED && !itemDelivery.isAllowResetWhenClosed()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_RESET_SESSION_WHEN_CLOSED, candidateSession);
        }

        /* Find the last REINIT, falling back to original INIT if none present */
        final List<CandidateItemEvent> events = candidateItemEventDao.getForSessionReversed(candidateSession);
        CandidateItemEvent lastInitEvent = null;
        for (final CandidateItemEvent event : events) {
            if (event.getEventType()==CandidateItemEventType.REINIT) {
                lastInitEvent = event;
                break;
            }
        }
        if (lastInitEvent==null) {
            lastInitEvent = events.get(events.size()-1);
        }

        /* Pull the QTI state from this event */
        final ItemSessionState itemSessionState = unmarshalItemSessionState(lastInitEvent);

        /* Record event */
        recordEvent(candidateSession, CandidateItemEventType.RESET, itemSessionState);

        /* Update state */
        final ItemSessionController itemSessionController = createItemSessionController(itemDelivery, itemSessionState);
        final boolean attemptAllowed = itemSessionController.isAttemptAllowed(itemDelivery.getMaxAttempts());
        candidateSession.setState(attemptAllowed ? CandidateSessionState.INTERACTING : CandidateSessionState.CLOSED);
        candidateItemSessionDao.update(candidateSession);

        auditor.recordEvent("Candidate reset session #" + candidateSession.getId());
        return candidateSession;
    }

    //----------------------------------------------------
    // Solution request

    /**
     * Transitions the {@link CandidateItemSession} having the given ID (xid) into solution state.
     *
     * @param xid
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     */
    public CandidateItemSession transitionCandidateSessionToSolutionState(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateSession = lookupCandidateSession(xid);
        return transitionCandidateSessionToSolutionState(candidateSession);
    }

    public CandidateItemSession transitionCandidateSessionToSolutionState(final CandidateItemSession candidateSession)
            throws PrivilegeException {
        Assert.ensureNotNull(candidateSession, "candidateSession");

        /* Make sure caller may do this */
        final User caller = ensureSessionNotTerminated(candidateSession);
        final CandidateSessionState candidateSessionState = candidateSession.getState();
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        if (candidateSessionState==CandidateSessionState.INTERACTING && !itemDelivery.isAllowSolutionWhenInteracting()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_SOLUTION_WHEN_INTERACTING, candidateSession);
        }
        else if (candidateSessionState==CandidateSessionState.CLOSED && !itemDelivery.isAllowResetWhenClosed()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_SOLUTION_WHEN_CLOSED, candidateSession);
        }

        /* Record event */
        final ItemSessionState itemSessionState = getCurrentItemSessionState(candidateSession);
        recordEvent(candidateSession, CandidateItemEventType.SOLUTION, itemSessionState);

        /* Change session state to CLOSED if it's not already there */
        if (candidateSessionState==CandidateSessionState.INTERACTING) {
            candidateSession.setState(CandidateSessionState.CLOSED);
            candidateItemSessionDao.update(candidateSession);
        }

        auditor.recordEvent("Candidate moved session #" + candidateSession.getId() + " to solution state");
        return candidateSession;
    }

    //----------------------------------------------------
    // Playback request

    /**
     * Updates the state of the {@link CandidateItemSession} having the given ID (xid)
     * so that it will play back the {@link CandidateItemEvent} having the given ID (xeid).
     *
     * @param xid
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     */
    public CandidateItemSession setPlaybackState(final long xid, final long xeid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateSession = lookupCandidateSession(xid);
        return setPlaybackState(candidateSession, xeid);
    }

    public CandidateItemSession setPlaybackState(final CandidateItemSession candidateSession, final long xeid)
            throws PrivilegeException, DomainEntityNotFoundException {
        Assert.ensureNotNull(candidateSession, "candidateSession");

        /* Make sure caller may do this */
        final User caller = ensureSessionNotTerminated(candidateSession);
        final CandidateSessionState candidateSessionState = candidateSession.getState();
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        if (candidateSessionState==CandidateSessionState.INTERACTING) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_PLAYBACK_WHEN_INTERACTING, candidateSession);
        }
        else if (candidateSessionState==CandidateSessionState.CLOSED && !itemDelivery.isAllowPlayback()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_PLAYBACK, candidateSession);
        }

        /* Look up target event, make sure it belongs to this session and make sure it can be played back */
        final CandidateItemEvent targetEvent = candidateItemEventDao.requireFindById(xeid);
        if (targetEvent.getCandidateItemSession().getId()!=candidateSession.getId()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_PLAYBACK_OTHER_SESSION, candidateSession);
        }
        final CandidateItemEventType targetEventType = targetEvent.getEventType();
        if (targetEventType==CandidateItemEventType.PLAYBACK
                || targetEventType==CandidateItemEventType.CLOSED
                || targetEventType==CandidateItemEventType.TERMINATED) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_PLAYBACK_EVENT, candidateSession);
        }

        /* Record event */
        final ItemSessionState itemSessionState = getCurrentItemSessionState(candidateSession);
        recordEvent(candidateSession, CandidateItemEventType.PLAYBACK, itemSessionState, targetEvent);

        auditor.recordEvent("Candidate played back event #" + targetEvent.getId()
                + " in session #" + candidateSession.getId());
        return candidateSession;
    }

    //----------------------------------------------------
    // Session termination (by candidate)

    /**
     * Terminates the {@link CandidateItemSession} having the given ID (xid), moving it into
     * {@link CandidateSessionState#TERMINATED} state.
     * <p>
     * Currently we're always allowing this action to be made when in
     * {@link CandidateSessionState#INTERACTING} or {@link CandidateSessionState#CLOSED}
     * states.
     *
     * @param xid
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     */
    public CandidateItemSession terminateCandidateSession(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateSession = lookupCandidateSession(xid);
        return terminateCandidateSession(candidateSession);
    }

    public CandidateItemSession terminateCandidateSession(final CandidateItemSession candidateSession)
            throws PrivilegeException {
        Assert.ensureNotNull(candidateSession, "candidateSession");

        /* Check session has not already been terminated */
        ensureSessionNotTerminated(candidateSession);

        /* Record event */
        final ItemSessionState itemSessionState = getCurrentItemSessionState(candidateSession);
        recordEvent(candidateSession, CandidateItemEventType.TERMINATED, itemSessionState);

        /* Update state */
        candidateSession.setState(CandidateSessionState.TERMINATED);
        candidateItemSessionDao.update(candidateSession);

        auditor.recordEvent("Candidate terminated session #" + candidateSession.getId());
        return candidateSession;
    }

    //----------------------------------------------------
    // Rendering

    /**
     * Renders the current state of the {@link CandidateItemSession} having
     * the given ID (xid).
     *
     * FIXME: This should render to a temporary {@link File} or something.
     *
     * @param candidateSession
     * @return
     * @throws DomainEntityNotFoundException
     * @throws PrivilegeException
     */
    public String renderCurrentState(final long xid,
            final RenderingOptions renderingOptions) throws PrivilegeException, DomainEntityNotFoundException {
        Assert.ensureNotNull(renderingOptions, "renderingOptions");

        final CandidateItemSession candidateSession = lookupCandidateSession(xid);
        return renderCurrentState(candidateSession, renderingOptions);
    }

    public String renderCurrentState(final CandidateItemSession candidateSession,
            final RenderingOptions renderingOptions) {
        Assert.ensureNotNull(candidateSession, "candidateSession");
        Assert.ensureNotNull(renderingOptions, "renderingOptions");
        final CandidateItemEvent latestEvent = getMostRecentEvent(candidateSession);
        return renderEvent(candidateSession, latestEvent, renderingOptions);
    }

    private String renderEvent(final CandidateItemSession candidateSession,
            final CandidateItemEvent candidateEvent,
            final RenderingOptions renderingOptions) {
        final CandidateSessionState candidateSessionState = candidateSession.getState();
        switch (candidateSessionState) {
            case INTERACTING:
                return renderEventWhenInteracting(candidateEvent, renderingOptions);

            case CLOSED:
                return renderEventWhenClosed(candidateEvent, renderingOptions);

            case TERMINATED:
                return renderTerminated(candidateEvent, renderingOptions);

            default:
                throw new QtiWorksLogicException("Unexpected state " + candidateSessionState);
        }
    }

    private String renderEventWhenInteracting(final CandidateItemEvent candidateEvent,
            final RenderingOptions renderingOptions) {
        final CandidateItemEventType eventType = candidateEvent.getEventType();
        switch (eventType) {
            case INIT:
            case REINIT:
            case RESET:
                return renderInteractingPresentation(candidateEvent, renderingOptions);

            case ATTEMPT_VALID:
            case ATTEMPT_INVALID:
            case ATTEMPT_BAD:
                return renderInteractingAfterAttempt(candidateEvent, renderingOptions);

            default:
                throw new QtiWorksLogicException("Unexpected logic branch. Event " + eventType
                        + " should have moved session state out of " + CandidateSessionState.INTERACTING
                        + " mode");
        }
    }

    private String renderInteractingPresentation(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequestWhenInteracting(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.INTERACTING_PRESENTATION);

        return assessmentRenderer.renderItem(renderingRequest);
    }

    private String renderInteractingAfterAttempt(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequestWhenInteracting(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.INTERACTING_AFTER_ATTEMPT);

        final CandidateItemAttempt attempt = candidateItemAttemptDao.getForEvent(candidateEvent);
        if (attempt==null) {
            throw new QtiWorksLogicException("Expected to find a CandidateItemAttempt corresponding to event #" + candidateEvent.getId());
        }
        final Map<Identifier, ResponseData> responseDataBuilder = new HashMap<Identifier, ResponseData>();
        final Set<Identifier> badResponseIdentifiersBuilder = new HashSet<Identifier>();
        final Set<Identifier> invalidResponseIdentifiersBuilder = new HashSet<Identifier>();
        extractResponseMap(attempt, responseDataBuilder, badResponseIdentifiersBuilder, invalidResponseIdentifiersBuilder);

        renderingRequest.setResponseInputs(responseDataBuilder);
        renderingRequest.setBadResponseIdentifiers(badResponseIdentifiersBuilder);
        renderingRequest.setInvalidResponseIdentifiers(invalidResponseIdentifiersBuilder);

        return assessmentRenderer.renderItem(renderingRequest);
    }

    private ItemRenderingRequest createItemRenderingRequestWhenInteracting(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final CandidateItemSession candidateSession = candidateEvent.getCandidateItemSession();
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();

        final ItemRenderingRequest renderingRequest = createPartialItemRenderingRequest(candidateEvent, renderingOptions);
        renderingRequest.setCloseAllowed(itemDelivery.isAllowClose());
        renderingRequest.setReinitAllowed(itemDelivery.isAllowReinitWhenInteracting());
        renderingRequest.setResetAllowed(itemDelivery.isAllowResetWhenInteracting());
        renderingRequest.setSolutionAllowed(itemDelivery.isAllowSolutionWhenInteracting());
        renderingRequest.setResultAllowed(false);
        renderingRequest.setSourceAllowed(itemDelivery.isAllowSource());
        renderingRequest.setBadResponseIdentifiers(null);
        renderingRequest.setInvalidResponseIdentifiers(null);
        renderingRequest.setResponseInputs(null);
        return renderingRequest;
    }

    private String renderEventWhenClosed(final CandidateItemEvent candidateEvent,
            final RenderingOptions renderingOptions) {
        final CandidateItemEventType eventType = candidateEvent.getEventType();
        switch (eventType) {
            case ATTEMPT_VALID:
            case ATTEMPT_INVALID:
            case ATTEMPT_BAD:
                return renderClosedAfterAttempt(candidateEvent, renderingOptions);

            case CLOSED:
                return renderClosed(candidateEvent, renderingOptions);

            case SOLUTION:
                return renderSolution(candidateEvent, renderingOptions);

            case PLAYBACK:
                return renderPlayback(candidateEvent, renderingOptions);

            default:
                throw new QtiWorksLogicException("Unexpected logic branch. Event " + eventType
                        + " either hasn't been implemented here, or should have earlier moved session state out of "
                        + CandidateSessionState.INTERACTING
                        + " mode");
        }
    }

    private ItemRenderingRequest createPartialItemRenderingRequest(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final CandidateItemSession candidateSession = candidateEvent.getCandidateItemSession();
        final CandidateSessionState candidateSessionState = candidateSession.getState();
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        final AssessmentPackage assessmentPackage = itemDelivery.getAssessmentPackage();

        final ItemRenderingRequest renderingRequest = new ItemRenderingRequest();
        renderingRequest.setAssessmentResourceLocator(ServiceUtilities.createAssessmentResourceLocator(assessmentPackage));
        renderingRequest.setAssessmentResourceUri(ServiceUtilities.createAssessmentObjectUri(assessmentPackage));
        renderingRequest.setCandidateSessionState(candidateSessionState);
        renderingRequest.setItemSessionState(unmarshalItemSessionState(candidateEvent));
        renderingRequest.setRenderingOptions(renderingOptions);
        return renderingRequest;
    }

    private String renderClosedAfterAttempt(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequestWhenClosed(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.CLOSED_AFTER_ATTEMPT);

        /* FIXME: Cut & paste below! Refactor this! */
        final CandidateItemAttempt attempt = candidateItemAttemptDao.getForEvent(candidateEvent);
        if (attempt==null) {
            throw new QtiWorksLogicException("Expected to find a CandidateItemAttempt corresponding to event #" + candidateEvent.getId());
        }
        final Map<Identifier, ResponseData> responseDataBuilder = new HashMap<Identifier, ResponseData>();
        final Set<Identifier> badResponseIdentifiersBuilder = new HashSet<Identifier>();
        final Set<Identifier> invalidResponseIdentifiersBuilder = new HashSet<Identifier>();
        extractResponseMap(attempt, responseDataBuilder, badResponseIdentifiersBuilder, invalidResponseIdentifiersBuilder);

        renderingRequest.setResponseInputs(responseDataBuilder);
        renderingRequest.setBadResponseIdentifiers(badResponseIdentifiersBuilder);
        renderingRequest.setInvalidResponseIdentifiers(invalidResponseIdentifiersBuilder);

        return assessmentRenderer.renderItem(renderingRequest);
    }

    private String renderClosed(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequestWhenClosed(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.CLOSED);
        return assessmentRenderer.renderItem(renderingRequest);
    }

    private String renderSolution(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequestWhenClosed(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.SOLUTION);
        return assessmentRenderer.renderItem(renderingRequest);
    }

    private String renderPlayback(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final CandidateItemEvent playbackEvent = candidateEvent.getPlaybackEvent();

        final CandidateItemSession candidateSession = candidateEvent.getCandidateItemSession();
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        final AssessmentPackage assessmentPackage = itemDelivery.getAssessmentPackage();

        final ItemRenderingRequest renderingRequest = new ItemRenderingRequest();
        renderingRequest.setCandidateSessionState(CandidateSessionState.CLOSED);
        renderingRequest.setRenderingMode(RenderingMode.PLAYBACK);
        renderingRequest.setAssessmentResourceLocator(ServiceUtilities.createAssessmentResourceLocator(assessmentPackage));
        renderingRequest.setAssessmentResourceUri(ServiceUtilities.createAssessmentObjectUri(assessmentPackage));
        renderingRequest.setItemSessionState(unmarshalItemSessionState(playbackEvent));
        renderingRequest.setRenderingOptions(renderingOptions);

        renderingRequest.setCloseAllowed(false);
        renderingRequest.setSolutionAllowed(itemDelivery.isAllowSolutionWhenClosed());
        renderingRequest.setReinitAllowed(itemDelivery.isAllowReinitWhenClosed());
        renderingRequest.setResetAllowed(itemDelivery.isAllowResetWhenClosed());
        renderingRequest.setResultAllowed(itemDelivery.isAllowResult());
        renderingRequest.setSourceAllowed(itemDelivery.isAllowSource());

        renderingRequest.setPlaybackAllowed(itemDelivery.isAllowPlayback());
        if (itemDelivery.isAllowPlayback()) {
            renderingRequest.setPlaybackEventIds(getPlaybackEventIds(candidateSession));
        }

        return assessmentRenderer.renderItem(renderingRequest);
    }

    private ItemRenderingRequest createItemRenderingRequestWhenClosed(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final CandidateItemSession candidateSession = candidateEvent.getCandidateItemSession();
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        final AssessmentPackage assessmentPackage = itemDelivery.getAssessmentPackage();

        final ItemRenderingRequest renderingRequest = new ItemRenderingRequest();
        renderingRequest.setCandidateSessionState(CandidateSessionState.CLOSED);
        renderingRequest.setRenderingMode(RenderingMode.CLOSED);
        renderingRequest.setAssessmentResourceLocator(ServiceUtilities.createAssessmentResourceLocator(assessmentPackage));
        renderingRequest.setAssessmentResourceUri(ServiceUtilities.createAssessmentObjectUri(assessmentPackage));
        renderingRequest.setItemSessionState(unmarshalItemSessionState(candidateEvent));
        renderingRequest.setRenderingOptions(renderingOptions);
        renderingRequest.setCloseAllowed(false);
        renderingRequest.setSolutionAllowed(itemDelivery.isAllowSolutionWhenClosed());
        renderingRequest.setReinitAllowed(itemDelivery.isAllowReinitWhenClosed());
        renderingRequest.setResetAllowed(itemDelivery.isAllowResetWhenClosed());
        renderingRequest.setResultAllowed(itemDelivery.isAllowResult());
        renderingRequest.setSourceAllowed(itemDelivery.isAllowSource());

        renderingRequest.setPlaybackAllowed(itemDelivery.isAllowPlayback());
        if (itemDelivery.isAllowPlayback()) {
            renderingRequest.setPlaybackEventIds(getPlaybackEventIds(candidateSession));
        }
        return renderingRequest;
    }

    private String renderTerminated(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final ItemRenderingRequest renderingRequest = createPartialItemRenderingRequest(candidateEvent, renderingOptions);
        renderingRequest.setRenderingMode(RenderingMode.TERMINATED);
        return assessmentRenderer.renderItem(renderingRequest);
    }

    private void extractResponseMap(final CandidateItemAttempt attempt, final Map<Identifier, ResponseData> responseDataBuilder,
            final Set<Identifier> badResponseIdentifiersBuilder, final Set<Identifier> invalidResponseIdentifiersBuilder) {
        for (final CandidateItemResponse response : attempt.getResponses()) {
            final Identifier responseIdentifier = new Identifier(response.getResponseIdentifier());
            final ResponseLegality responseLegality = response.getResponseLegality();
            final ResponseDataType responseType = response.getResponseType();
            ResponseData responseData = null;
            switch (responseType) {
                case STRING:
                    responseData = new StringResponseData(response.getStringResponseData());
                    break;

                case FILE:
                    final CandidateFileSubmission fileSubmission = response.getFileSubmission();
                    responseData = new FileResponseData(new File(fileSubmission.getStoredFilePath()),
                            fileSubmission.getContentType());
                    break;

                default:
                    throw new QtiWorksLogicException("Unexpected ResponseDataType " + responseType);
            }
            responseDataBuilder.put(responseIdentifier, responseData);
            if (responseLegality==ResponseLegality.BAD) {
                badResponseIdentifiersBuilder.add(responseIdentifier);
            }
            else if (responseLegality==ResponseLegality.INVALID) {
                invalidResponseIdentifiersBuilder.add(responseIdentifier);
            }
        }
    }

    //----------------------------------------------------
    // Access to additional package resources (e.g. images/CSS)

    public void streamAssessmentResource(final long did, final String fileSystemIdString,
            final OutputStream outputStream)
            throws PrivilegeException, IOException, DomainEntityNotFoundException {
        Assert.ensureNotNull(fileSystemIdString, "fileSystemIdString");
        Assert.ensureNotNull(outputStream, "outputStream");
        final ItemDelivery itemDelivery = lookupItemDelivery(did);
        streamAssessmentResource(itemDelivery, fileSystemIdString, outputStream);
    }

    /** FIXME: Add caching support */
    public void streamAssessmentResource(final ItemDelivery itemDelivery, final String fileSystemIdString,
            final OutputStream outputStream)
            throws PrivilegeException, IOException {
        Assert.ensureNotNull(itemDelivery, "itemDelivery");
        Assert.ensureNotNull(fileSystemIdString, "fileSystemIdString");
        Assert.ensureNotNull(outputStream, "outputStream");

        /* Make sure requested file is whitelisted for access */
        final AssessmentPackage assessmentPackage = itemDelivery.getAssessmentPackage();
        String resultingFileHref = null;
        for (final String fileHref : assessmentPackage.getFileHrefs()) {
            final URI fileUri = ServiceUtilities.createAssessmentFileUri(assessmentPackage, fileHref);
            if (fileUri.toString().equals(fileSystemIdString)) {
                resultingFileHref = fileHref;
                break;
            }
        }
        if (resultingFileHref==null) {
            final User caller = identityContext.getCurrentThreadEffectiveIdentity();
            throw new PrivilegeException(caller, Privilege.CANDIDATE_ACCESS_ASSESSMENT_FILE, assessmentPackage);
        }

        /* Finally stream the required resource */
        ServiceUtilities.streamAssessmentFile(assessmentPackage, resultingFileHref, outputStream);
    }

    //----------------------------------------------------
    // Candidate Source access

    public void streamAssessmentSource(final long did, final OutputStream outputStream)
            throws PrivilegeException, IOException, DomainEntityNotFoundException {
        Assert.ensureNotNull(outputStream, "outputStream");
        final ItemDelivery itemDelivery = lookupItemDelivery(did);
        streamAssessmentSource(itemDelivery, outputStream);
    }

    /** FIXME: Add caching support */
    public void streamAssessmentSource(final ItemDelivery itemDelivery, final OutputStream outputStream)
            throws PrivilegeException, IOException {
        Assert.ensureNotNull(itemDelivery, "itemDelivery");
        Assert.ensureNotNull(outputStream, "outputStream");
        ensureCallerMayViewSource(itemDelivery);

        ServiceUtilities.streamAssessmentPackageSource(itemDelivery.getAssessmentPackage(), outputStream);
        auditor.recordEvent("Candidate streamed source for delivery #" + itemDelivery.getId());
    }

    private User ensureCallerMayViewSource(final ItemDelivery itemDelivery)
            throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!itemDelivery.isAllowSource()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_VIEW_ASSESSMENT_SOURCE, itemDelivery);
        }
        return caller;
    }

    //----------------------------------------------------
    // Candidate Result access

    public void streamItemResult(final long xid, final OutputStream outputStream)
            throws PrivilegeException, DomainEntityNotFoundException {
        Assert.ensureNotNull(outputStream, "outputStream");
        final CandidateItemSession candidateSession = lookupCandidateSession(xid);
        streamItemResult(candidateSession, outputStream);
    }

    public void streamItemResult(final CandidateItemSession candidateSession, final OutputStream outputStream)
            throws PrivilegeException {
        Assert.ensureNotNull(candidateSession, "candidateSession");
        Assert.ensureNotNull(outputStream, "outputStream");

        /* Forbid results if the candidate session is closed */
        ensureSessionNotTerminated(candidateSession);

        /* Make sure candidate is actually allowed to get results for this delivery */
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        ensureCallerMayViewResult(itemDelivery);

        /* Get current state */
        final CandidateItemEvent mostRecentEvent = getMostRecentEvent(candidateSession);

        /* Generate result Object from state */
        final ItemSessionController itemSessionController = createItemSessionController(mostRecentEvent);
        final ItemResult itemResult = itemSessionController.computeItemResult();

        /* Send result */
        qtiSerializer.serializeJqtiObject(itemResult, outputStream);
        auditor.recordEvent("Candidate streamed result for session #" + candidateSession.getId()
                + " on delivery #" + candidateSession.getItemDelivery().getId());
    }

    private User ensureCallerMayViewResult(final ItemDelivery itemDelivery)
            throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!itemDelivery.isAllowResult()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_VIEW_ASSESSMENT_RESULT, itemDelivery);
        }
        return caller;
    }

    //----------------------------------------------------
    // Utilities

    /**
     * Returns a List of IDs (xeid) of all {@link CandidateItemEvent}s in the given
     * {@link CandidateItemSession} that a candidate may play back.
     *
     * @param candidateSession
     * @return
     */
    private List<Long> getPlaybackEventIds(final CandidateItemSession candidateSession) {
        final List<CandidateItemEvent> events = candidateItemEventDao.getForSession(candidateSession);
        final List<Long> result = new ArrayList<Long>(events.size());
        for (final CandidateItemEvent event : events) {
            if (isCandidatePlaybackCapable(event)) {
                result.add(event.getId());
            }
        }
        return result;
    }

    private boolean isCandidatePlaybackCapable(final CandidateItemEvent event) {
        final CandidateItemEventType eventType = event.getEventType();
        return eventType==CandidateItemEventType.ATTEMPT_VALID
                || eventType==CandidateItemEventType.ATTEMPT_INVALID
                || eventType==CandidateItemEventType.ATTEMPT_BAD
                || eventType==CandidateItemEventType.INIT
                || eventType==CandidateItemEventType.REINIT
                || eventType==CandidateItemEventType.RESET;

    }

    private CandidateItemEvent getMostRecentEvent(final CandidateItemSession candidateSession)  {
        final CandidateItemEvent mostRecentEvent = candidateItemEventDao.getNewestEventInSession(candidateSession);
        if (mostRecentEvent==null) {
            throw new QtiWorksLogicException("Session has no events registered. Current logic should not have allowed this!");
        }
        return mostRecentEvent;
    }

    private ItemSessionState getCurrentItemSessionState(final CandidateItemSession candidateSession)  {
        final CandidateItemEvent mostRecentEvent = getMostRecentEvent(candidateSession);
        return unmarshalItemSessionState(mostRecentEvent);
    }

    private ItemSessionController createItemSessionController(final CandidateItemEvent candidateEvent) {
        final AssessmentPackage assessmentPackage = candidateEvent.getCandidateItemSession().getItemDelivery().getAssessmentPackage();
        final ResolvedAssessmentItem resolvedAssessmentItem = assessmentObjectManagementService.getResolvedAssessmentItem(assessmentPackage);
        final ItemSessionState itemSessionState = unmarshalItemSessionState(candidateEvent);
        return new ItemSessionController(jqtiExtensionManager, resolvedAssessmentItem, itemSessionState);
    }

    private ItemSessionState unmarshalItemSessionState(final CandidateItemEvent event) {
        final String itemSessionStateXml = event.getItemSessionStateXml();
        final DocumentBuilder documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
        Document doc;
        try {
            doc = documentBuilder.parse(new InputSource(new StringReader(itemSessionStateXml)));
        }
        catch (final Exception e) {
            throw new QtiWorksLogicException("Could not parse ItemSessionState XML. This is an internal error as we currently don't expose this data to clients", e);
        }
        return ItemSesssionStateXmlMarshaller.unmarshal(doc);
    }

    private CandidateItemEvent recordEvent(final CandidateItemSession candidateSession,
            final CandidateItemEventType eventType, final ItemSessionState itemSessionState) {
        return recordEvent(candidateSession, eventType, itemSessionState, null);
    }

    private CandidateItemEvent recordEvent(final CandidateItemSession candidateSession,
            final CandidateItemEventType eventType, final ItemSessionState itemSessionState,
            final CandidateItemEvent playbackEvent) {
        final CandidateItemEvent event = new CandidateItemEvent();
        event.setCandidateItemSession(candidateSession);
        event.setEventType(eventType);
        event.setSessionState(candidateSession.getState());
        event.setCompletionStatus(itemSessionState.getCompletionStatus());
        event.setDuration(itemSessionState.getDuration());
        event.setNumAttempts(itemSessionState.getNumAttempts());
        event.setTimestamp(requestTimestampContext.getCurrentRequestTimestamp());
        event.setPlaybackEvent(playbackEvent);

        /* Record serialized ItemSessionState */
        event.setItemSessionStateXml(marshalItemSessionState(itemSessionState));

        /* Store */
        candidateItemEventDao.persist(event);
        logger.debug("Recorded {}", event);
        return event;
    }

    private String marshalItemSessionState(final ItemSessionState itemSessionState) {
        final Document marshalledState = ItemSesssionStateXmlMarshaller.marshal(itemSessionState);
        final XMLStringOutputOptions xmlOptions = new XMLStringOutputOptions();
        xmlOptions.setIndenting(true);
        xmlOptions.setIncludingXMLDeclaration(false);
        return XMLUtilities.serializeNode(marshalledState, xmlOptions);
    }
}
