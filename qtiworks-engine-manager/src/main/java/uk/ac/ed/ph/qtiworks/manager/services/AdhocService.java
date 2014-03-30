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
package uk.ac.ed.ph.qtiworks.manager.services;

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.LtiResource;
import uk.ac.ed.ph.qtiworks.services.AssessmentReportingService;
import uk.ac.ed.ph.qtiworks.services.CandidateDataService;
import uk.ac.ed.ph.qtiworks.services.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateTestDeliveryService;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiResourceDao;
import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;
import uk.ac.ed.ph.qtiworks.web.candidate.CandidateSessionContext;

import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationRecorder;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.state.TestPartSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dev utility class for running arbitrary JPA code
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRES_NEW)
public class AdhocService {

    @Resource
    private AssessmentReportingService assessmentReportingService;

    @Resource
    private LtiResourceDao ltiResourceDao;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    @Resource
    private CandidateDataService candidateDataService;

    @Resource
    private CandidateTestDeliveryService candidateTestDeliveryService;

    @Resource
    private RequestTimestampContext requestTimestampContext;

    public void doWork(final List<String> parameters) throws Exception {
        closeBemaTestSessions(parameters);
    }

    /**
     * TEMPORARY! This is being used to close off unfinished test sessions for the BEMA pilot
     * being done at UoE. This idea might want to become part of core functionality in future...
     */
    public void closeBemaTestSessions(final List<String> parameters) throws Exception {
        if (parameters.size()!=1) {
            System.err.println("Required parameter: lrid");
            return;
        }
        final Long ltiResourceId = Long.parseLong(parameters.get(0));
        final LtiResource ltiResource = ltiResourceDao.requireFindById(ltiResourceId);
        final Delivery bemaDelivery = ltiResource.getDelivery();

        final Date timestamp = new Date();
        requestTimestampContext.setCurrentRequestTimestamp(timestamp);
        final List<CandidateSession> candidateSessions = candidateSessionDao.getForDelivery(bemaDelivery);
        final NotificationRecorder notificationRecorder = new NotificationRecorder(NotificationLevel.INFO);
        for (final CandidateSession candidateSession : candidateSessions) {
        	final CandidateSessionContext candidateSessionContext = new CandidateSessionContext(candidateSession, "/unused");
            if (!candidateSession.isClosed() && !candidateSession.isTerminated()) {
                final CandidateEvent mostRecentEvent = candidateDataService.getMostRecentEvent(candidateSession);
                if (mostRecentEvent!=null) {
                    final TestSessionController testSessionController = candidateDataService.createTestSessionController(mostRecentEvent, notificationRecorder);
                    final TestSessionState testSessionState = testSessionController.getTestSessionState();
                    final TestPartSessionState testPartSessionState = testSessionState.getTestPartSessionStates().values().iterator().next();
                    if (!testPartSessionState.isEnded()) {
                        System.out.println("Ending and exiting test for session " + candidateSession.getId());
                        candidateTestDeliveryService.endCurrentTestPart(candidateSessionContext);
                        candidateTestDeliveryService.advanceTestPart(candidateSessionContext);
                    }
                    else if (!testSessionState.isExited()) {
                        System.out.println("Exiting test for session " + candidateSession.getId());
                        candidateTestDeliveryService.advanceTestPart(candidateSessionContext);
                    }
                }
            }
        }
    }

    public static class Utf8Streamer implements OutputStreamer {

        private String result = null;

        @Override
        public void stream(final String contentType, final long contentLength, final Date lastModifiedTime, final InputStream resultStream) throws IOException {
            this.result = IOUtils.toString(resultStream, "UTF-8");
        }

        public String getResult() {
            return result;
        }

    }
}
