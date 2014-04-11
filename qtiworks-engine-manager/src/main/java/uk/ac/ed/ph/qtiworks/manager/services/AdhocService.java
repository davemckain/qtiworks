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

import uk.ac.ed.ph.qtiworks.services.AssessmentReportingService;
import uk.ac.ed.ph.qtiworks.services.CandidateDataService;
import uk.ac.ed.ph.qtiworks.services.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.services.candidate.CandidateTestDeliveryService;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.LtiResourceDao;
import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;

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
    	/* Put something here when required */
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
