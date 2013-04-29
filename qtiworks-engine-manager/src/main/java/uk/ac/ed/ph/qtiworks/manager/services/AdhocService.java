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

import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.services.AssessmentReportingService;
import uk.ac.ed.ph.qtiworks.services.dao.DeliveryDao;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport.DcsrRow;
import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;
import uk.ac.ed.ph.qtiworks.utils.IoUtilities;

import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.csvreader.CsvWriter;

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
    private DeliveryDao deliveryDao;

    public void doWork() throws Exception {
        doReport();
    }

    public void doReport() throws Exception {
        final Delivery delivery = deliveryDao.requireFindById(98L);
        final DeliveryCandidateSummaryReport report = assessmentReportingService.buildDeliveryCandidateSummaryReport(delivery);

        System.out.println(report);

        final StringWriter stringWriter = new StringWriter();
        final CsvWriter csvWriter = new CsvWriter(stringWriter, ',');

        /* Write header */
        final StringBuilder headerBuilder = new StringBuilder("Session ID,First Name,Last Name,Email Address");
        for (final String outcomeName : report.getNumericalOutcomeNames()) {
            headerBuilder.append(outcomeName);
        }

        for (final DcsrRow row : report.getRows()) {
            csvWriter.write(Long.toString(row.getSessionId()));
            csvWriter.write(row.getFirstName());
            csvWriter.write(row.getLastName());
            csvWriter.write(StringUtilities.emptyIfNull(row.getEmailAddress()));
            csvWriter.write(row.isSessionClosed() ? "Finished" : "In Progress");
            final List<String> outcomeValues = row.getNumericalOutcomeValues();
            if (outcomeValues!=null) {
                for (final String outcomeValue : row.getNumericalOutcomeValues()) {
                    csvWriter.write(outcomeValue, true);
                }
            }
            csvWriter.endRecord();
        }
        csvWriter.close();
        System.out.println(stringWriter);
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
