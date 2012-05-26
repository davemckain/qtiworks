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
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.utils.XmlUtilities;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.result.ItemResult;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
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
import java.util.HashMap;
import java.util.HashSet;
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
        candidateSession.setState(attemptAllowed ? CandidateSessionState.INTERACTING : CandidateSessionState.REVIEWING);
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

    //----------------------------------------------------
    // Session reset

    /**
     * Resets the {@link CandidateItemSession} having the given ID (xid), returning the
     * updated {@link CandidateItemSession}
     *
     * @param xid
     * @return
     * @throws RuntimeValidationException
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     */
    public CandidateItemSession resetCandidateSession(final long xid)
            throws RuntimeValidationException, PrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateSession = lookupCandidateSession(xid);
        return resetCandidateSession(candidateSession);
    }

    public CandidateItemSession resetCandidateSession(final CandidateItemSession candidateSession)
            throws RuntimeValidationException, PrivilegeException {
        Assert.ensureNotNull(candidateSession, "candidateSession");
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();

        /* Make sure we can reset the session */
        if (candidateSession.getState()!=CandidateSessionState.INTERACTING) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_RESET_SESSION_AFTER_INTERACTING, candidateSession);
        }
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        if (!itemDelivery.isAllowReset()) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_RESET_SESSION_WHEN_INTERACTING, itemDelivery);
        }

        /* Create fresh JQTI+ state */
        final ItemSessionState itemSessionState = new ItemSessionState();

        /* Get the resolved JQTI+ Object for the underlying package */
        final ItemSessionController itemSessionController = createItemSessionController(itemDelivery, itemSessionState);

        /* Initialise state */
        itemSessionController.initialize();

        /* Record event */
        recordEvent(candidateSession, CandidateItemEventType.RESET, itemSessionState);

        /* Update state */
        final boolean attemptAllowed = itemSessionController.isAttemptAllowed(itemDelivery.getMaxAttempts());
        candidateSession.setState(attemptAllowed ? CandidateSessionState.INTERACTING : CandidateSessionState.REVIEWING);
        candidateItemSessionDao.update(candidateSession);

        auditor.recordEvent("Candidate reset session #" + candidateSession.getId());
        return candidateSession;
    }

    //----------------------------------------------------
    // Session end (by candidate)

    /**
     * Ends the {@link CandidateItemSession} having the given ID (xid).
     *
     * @param xid
     * @return
     * @throws PrivilegeException
     * @throws DomainEntityNotFoundException
     */
    public CandidateItemSession endCandidateSession(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateItemSession candidateSession = lookupCandidateSession(xid);
        return endCandidateSession(candidateSession);
    }

    public CandidateItemSession endCandidateSession(final CandidateItemSession candidateSession)
            throws PrivilegeException {
        Assert.ensureNotNull(candidateSession, "candidateSession");
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();

        /* Check we're in the right state */
        if (candidateSession.getState()!=CandidateSessionState.INTERACTING) {
            throw new PrivilegeException(caller, Privilege.CANDIDATE_END_SESSION, candidateSession);
        }

        /* Record event */
        final ItemSessionState itemSessionState = getCurrentItemSessionState(candidateSession);
        recordEvent(candidateSession, CandidateItemEventType.END, itemSessionState);

        /* Update state */
        candidateSession.setState(CandidateSessionState.REVIEWING);
        candidateItemSessionDao.update(candidateSession);

        auditor.recordEvent("Candidate ended session #" + candidateSession.getId());
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
        return renderEvent(latestEvent, renderingOptions);
    }

    private String renderEvent(final CandidateItemEvent candidateEvent,
            final RenderingOptions renderingOptions) {
        final CandidateItemEventType eventType = candidateEvent.getEventType();
        switch (eventType) {
            case INIT:
            case RESET:
                return renderAfterInit(candidateEvent, renderingOptions);

            case ATTEMPT_VALID:
            case ATTEMPT_INVALID:
            case ATTEMPT_BAD:
                return renderAfterAttempt(candidateEvent, renderingOptions);

            default:
                throw new QtiWorksLogicException("Rendering of event " + eventType + " has not been implemented yet");
        }
    }

    private String renderAfterInit(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequest(candidateEvent, renderingOptions);
        return assessmentRenderer.renderItem(renderingRequest);
    }

    private ItemRenderingRequest createItemRenderingRequest(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final ItemSessionController itemSessionController = createItemSessionController(candidateEvent);
        final CandidateItemSession candidateSession = candidateEvent.getCandidateItemSession();
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        final AssessmentPackage assessmentPackage = itemDelivery.getAssessmentPackage();

        final ItemRenderingRequest renderingRequest = new ItemRenderingRequest();
        renderingRequest.setAssessmentResourceLocator(ServiceUtilities.createAssessmentResourceLocator(assessmentPackage));
        renderingRequest.setAssessmentResourceUri(ServiceUtilities.createAssessmentObjectUri(assessmentPackage));
        renderingRequest.setAttemptAllowed(itemSessionController.isAttemptAllowed(itemDelivery.getMaxAttempts()));
        renderingRequest.setCandidateSessionState(candidateSession.getState());
        renderingRequest.setItemSessionState(itemSessionController.getItemSessionState());
        renderingRequest.setRenderingOptions(renderingOptions);
        renderingRequest.setResetAllowed(itemDelivery.isAllowReset());
        renderingRequest.setResultAllowed(itemDelivery.isAllowResult());
        renderingRequest.setSourceAllowed(itemDelivery.isAllowSource());
        renderingRequest.setBadResponseIdentifiers(null);
        renderingRequest.setInvalidResponseIdentifiers(null);
        renderingRequest.setResponseInputs(null);
        return renderingRequest;
    }

    private String renderAfterAttempt(final CandidateItemEvent candidateEvent, final RenderingOptions renderingOptions) {
        final ItemRenderingRequest renderingRequest = createItemRenderingRequest(candidateEvent, renderingOptions);

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
        candidateSession.setState(attemptAllowed ? CandidateSessionState.INTERACTING : CandidateSessionState.REVIEWING);
        candidateItemSessionDao.update(candidateSession);

        auditor.recordEvent("Recorded candidate attempt #" + candidateItemAttempt.getId()
                + " on session #" + candidateSession.getId()
                + " on delivery #" + candidateSession.getItemDelivery().getId());
        return candidateItemAttempt;
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
        final ItemDelivery itemDelivery = candidateSession.getItemDelivery();
        ensureCallerMayViewResult(itemDelivery);

        /* Get current state */
        final CandidateItemEvent mostRecentEvent = getMostRecentEvent(candidateSession);

        /* Generate result Object from state */
        final ItemSessionController itemSessionController = createItemSessionController(mostRecentEvent);
        final ItemResult itemResult = itemSessionController.computeItemResult();

        /* Send result */
        assessmentRenderer.serializeJqtiObject(itemResult, outputStream);
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
        final CandidateItemEvent event = new CandidateItemEvent();
        event.setCandidateItemSession(candidateSession);
        event.setEventType(eventType);

        event.setCompletionStatus(itemSessionState.getCompletionStatus());
        event.setDuration(itemSessionState.getDuration());
        event.setNumAttempts(itemSessionState.getNumAttempts());
        event.setTimestamp(requestTimestampContext.getCurrentRequestTimestamp());

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
