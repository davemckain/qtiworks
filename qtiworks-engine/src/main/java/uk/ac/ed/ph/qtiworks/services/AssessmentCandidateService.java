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

import uk.ac.ed.ph.qtiworks.base.services.Auditor;
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.Privilege;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.binding.ItemSesssionStateXmlMarshaller;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemEventDao;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateItemSessionDao;
import uk.ac.ed.ph.qtiworks.domain.entities.Assessment;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.ItemDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.User;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;

import uk.ac.ed.ph.snuggletex.XMLStringOutputOptions;
import uk.ac.ed.ph.snuggletex.internal.util.XMLUtilities;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

/**
 * Service the manages the real-time delivery of an {@link Assessment}
 * to a particular candidate {@link User}
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class AssessmentCandidateService {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentCandidateService.class);

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private Auditor auditor;

    @Resource
    private IdentityContext identityContext;

    @Resource
    private CandidateItemSessionDao candidateItemSessionDao;

    @Resource
    private CandidateItemEventDao candidateItemEventDao;

    //----------------------------------------------------
    // Session management

    public CandidateItemSession getCandidateSession(final Long sessionId)
            throws DomainEntityNotFoundException, PrivilegeException {
        Assert.ensureNotNull(sessionId, "sessionId");

        final CandidateItemSession session = candidateItemSessionDao.requireFindById(sessionId);
        ensureCallerMayAccess(session);
        return session;
    }

    public CandidateItemSession createCandidateSession(final ItemDelivery itemDelivery) {
        Assert.ensureNotNull(itemDelivery, "itemDelivery");

        final CandidateItemSession result = new CandidateItemSession();
        result.setCandidate(identityContext.getCurrentThreadEffectiveIdentity());
        result.setItemDelivery(itemDelivery);
        candidateItemSessionDao.persist(result);

        auditor.recordEvent("Created item session #" + result.getId());
        return result;
    }

    /**
     * (Currently we're restricting access to sessions to their owners.)
     */
    private User ensureCallerMayAccess(final CandidateItemSession candidateSession) throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!caller.equals(candidateSession.getCandidate())) {
            throw new PrivilegeException(caller, Privilege.ACCESS_CANDIDATE_SESSION, candidateSession);
        }
        return caller;
    }


    //----------------------------------------------------

    public ItemSessionState initialiseSession(final CandidateItemSession candidateSession) {
        final CandidateItemEvent mostRecentEvent = candidateItemEventDao.getNewestEventInSession(candidateSession);
        if (mostRecentEvent!=null) {
            /* FIXME: Need to add logic to decide whether we are allowed to re-initialize
             * the session or not
             */
        }

        /* Create new state */
        final ItemSessionState itemSessionState = new ItemSessionState();

        /* Record event */
        recordEvent(candidateSession, CandidateItemEventType.INIT, itemSessionState);
        auditor.recordEvent("Initialized session #" + candidateSession);

        return itemSessionState;
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
        final Document marshalledState = ItemSesssionStateXmlMarshaller.marshal(itemSessionState);
        final XMLStringOutputOptions xmlOptions = new XMLStringOutputOptions();
        xmlOptions.setIndenting(true);
        xmlOptions.setIncludingXMLDeclaration(false);
        event.setItemSessionStateXml(XMLUtilities.serializeNode(marshalledState, xmlOptions));

        /* Store */
        candidateSession.getEvents().add(event);
        candidateItemSessionDao.update(candidateSession);
        candidateItemEventDao.persist(event);
        logger.debug("Recorded {}", event);
        return event;
    }
}
