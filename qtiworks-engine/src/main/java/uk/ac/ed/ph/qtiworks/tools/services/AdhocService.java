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
package uk.ac.ed.ph.qtiworks.tools.services;

import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.domain.dao.InstructorUserDao;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.InstructorUser;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.services.AssessmentReportingService;
import uk.ac.ed.ph.qtiworks.services.CandidateSessionStarter;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateItemDeliveryService;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateTestDeliveryService;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport.DcsrRow;
import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;
import uk.ac.ed.ph.qtiworks.utils.IoUtilities;
import uk.ac.ed.ph.qtiworks.utils.NullMultipartFile;

import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.csvreader.CsvWriter;

import dave.StandaloneItemRenderingTest;

/**
 * Dev utility class for running arbitrary JPA code
 *
 * @author David McKain
 */
@Service
@Transactional(propagation=Propagation.REQUIRES_NEW)
public class AdhocService {

    @Resource
    private IdentityContext identityContext;

    @Resource
    private RequestTimestampContext requestTimestampContext;

    @Resource
    private CandidateItemDeliveryService candidateItemDeliveryService;

    @Resource
    private CandidateTestDeliveryService candidateTestDeliveryService;

    @Resource
    private CandidateSessionStarter candidateSessionStarter;

    @Resource
    private InstructorUserDao instructorUserDao;

    @Resource
    private AssessmentReportingService assessmentReportingService;

    @Resource
    private DeliveryDao deliveryDao;

    public void doWork() throws Exception {
        doReport();
//        doWorkItem();
//        doWorkTest();
    }

    public void doReport() throws Exception {
        final Delivery delivery = deliveryDao.requireFindById(98L);
        final DeliveryCandidateSummaryReport report = assessmentReportingService.buildDeliveryCandidateSummaryReport(delivery);

        System.out.println(report);

        final StringWriter stringWriter = new StringWriter();
        final CsvWriter csvWriter = new CsvWriter(stringWriter, ',');

        /* Write header */
        final StringBuilder headerBuilder = new StringBuilder("Session ID,First Name,Last Name,Email Address");
        for (final String outcomeName : report.getOutcomeNames()) {
            headerBuilder.append(outcomeName);
        }

        for (final DcsrRow row : report.getRows()) {
            csvWriter.write(Long.toString(row.getSessionId()));
            csvWriter.write(row.getFirstName());
            csvWriter.write(row.getLastName());
            csvWriter.write(StringUtilities.emptyIfNull(row.getEmailAddress()));
            csvWriter.write(row.isSessionClosed() ? "Finished" : "In Progress");
            final List<String> outcomeValues = row.getOutcomeValues();
            if (outcomeValues!=null) {
                for (final String outcomeValue : row.getOutcomeValues()) {
                    csvWriter.write(outcomeValue, true);
                }
            }
            csvWriter.endRecord();
        }
        csvWriter.close();
        System.out.println(stringWriter);
    }

    public void doWorkTest() throws Exception {
        requestTimestampContext.setCurrentRequestTimestamp(new Date());

        final InstructorUser dave = instructorUserDao.requireFindByLoginName("dmckain");
        identityContext.setCurrentThreadEffectiveIdentity(dave);
        identityContext.setCurrentThreadUnderlyingIdentity(dave);

        final long did = 93L; /* ID of test delivery */
        final String exitUrl = "/exit";

        final CandidateSession candidateSession = candidateSessionStarter.createCandidateSession(did, exitUrl);

        /* Render initial state */
        final RenderingOptions renderingOptions = StandaloneItemRenderingTest.createRenderingOptions();

        final Utf8Streamer utf8Streamer = new Utf8Streamer();
        candidateTestDeliveryService.renderCurrentState(candidateSession, renderingOptions, utf8Streamer);
        System.out.println("Rendering after init:\n" + utf8Streamer.getResult());
    }

    public void doWorkItem() throws Exception {
        requestTimestampContext.setCurrentRequestTimestamp(new Date());

        final InstructorUser dave = instructorUserDao.requireFindByLoginName("dmckain");
        identityContext.setCurrentThreadEffectiveIdentity(dave);
        identityContext.setCurrentThreadUnderlyingIdentity(dave);

        final long did = 4L; /* ID of delivery to try out (choice.xml) */
        final String exitUrl = "/exit";

        final CandidateSession candidateSession = candidateSessionStarter.createCandidateSession(did, exitUrl);

        /* Render initial state */
        final RenderingOptions renderingOptions = StandaloneItemRenderingTest.createRenderingOptions();

        final Utf8Streamer utf8Streamer = new Utf8Streamer();
        candidateItemDeliveryService.renderCurrentState(candidateSession, renderingOptions, utf8Streamer);
        System.out.println("Rendering after init:\n" + utf8Streamer.getResult());

        /* Do bad attempt == file submission */
        final Map<Identifier, MultipartFile> fileResponseMap = new HashMap<Identifier, MultipartFile>();
        fileResponseMap.put(Identifier.parseString("RESPONSE"), new NullMultipartFile());
        candidateItemDeliveryService.handleAttempt(candidateSession, null, fileResponseMap);

        /* Do invalid attempt */
        final Map<Identifier, StringResponseData> stringResponseMap = new HashMap<Identifier, StringResponseData>();
        stringResponseMap.put(Identifier.parseString("RESPONSE"), new StringResponseData("x"));
        candidateItemDeliveryService.handleAttempt(candidateSession, stringResponseMap, null);

        /* Then valid attempt */
        stringResponseMap.clear();
        stringResponseMap.put(Identifier.parseString("RESPONSE"), new StringResponseData("ChoiceA"));
        candidateItemDeliveryService.handleAttempt(candidateSession, stringResponseMap, null);

        /* Render new state */
        candidateItemDeliveryService.renderCurrentState(candidateSession, renderingOptions, utf8Streamer);
        System.out.println("Rendering after first proper attempt:\n" + utf8Streamer.getResult());

        /* Then reinit state */
        candidateItemDeliveryService.reinitCandidateSession(candidateSession);

        /* Then reset state */
        candidateItemDeliveryService.resetCandidateSession(candidateSession);

        /* Then end session */
        candidateItemDeliveryService.closeCandidateSession(candidateSession);

        /* Then close session */
        candidateItemDeliveryService.terminateCandidateSession(candidateSession);
    }

    public static class Utf8Streamer implements OutputStreamer {

        private String result = null;

        @Override
        public void stream(final String contentType, final long contentLength, final Date lastModifiedTime, final InputStream resultStream) throws IOException {
            this.result = IoUtilities.readUnicodeStream(resultStream);
        }

        public String getResult() {
            return result;
        }

    }
}
