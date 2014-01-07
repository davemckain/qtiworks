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
import uk.ac.ed.ph.qtiworks.domain.DomainEntityNotFoundException;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSession;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSessionOutcome;
import uk.ac.ed.ph.qtiworks.domain.entities.Delivery;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionDao;
import uk.ac.ed.ph.qtiworks.services.dao.CandidateSessionOutcomeDao;
import uk.ac.ed.ph.qtiworks.services.domain.CandidateSessionSummaryData;
import uk.ac.ed.ph.qtiworks.services.domain.CandidateSessionSummaryMetadata;
import uk.ac.ed.ph.qtiworks.services.domain.CandidateSessionSummaryReport;
import uk.ac.ed.ph.qtiworks.services.domain.DeliveryCandidateSummaryReport;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.csvreader.CsvWriter;

/**
 * Service for reporting on assessment deliveries and candidate sessions
 *
 * @author David McKain
 */
@Service
@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
public class AssessmentReportingService {

    @Resource
    private AuditLogger auditLogger;

    @Resource
    private AssessmentManagementService assessmentManagementService;

    @Resource
    private CandidateDataService candidateDataService;

    @Resource
    private CandidateSessionDao candidateSessionDao;

    @Resource
    private CandidateSessionOutcomeDao candidateSessionOutcomeDao;

    //-------------------------------------------------

    public CandidateSession lookupCandidateSession(final long xid)
            throws DomainEntityNotFoundException, PrivilegeException {
        final CandidateSession candidateSession = candidateSessionDao.requireFindById(xid);
        assessmentManagementService.ensureCallerMayManage(candidateSession.getDelivery().getAssessment());
        return candidateSession;
    }

    /**
     * Generates a {@link CandidateSessionSummaryReport} containing summary statistics
     * about the candidate session having given ID (xid).
     */
    public CandidateSessionSummaryReport buildCandidateSessionSummaryReport(final long xid)
            throws PrivilegeException, DomainEntityNotFoundException {
        final CandidateSession candidateSession = lookupCandidateSession(xid);
        return buildCandidateSessionSummaryReport(candidateSession);
    }

    /**
     * Builds a {@link CandidateSessionSummaryReport} for the given {@link CandidateSession}
     *
     * @param candidateSession {@link CandidateSession} to report on, which must not be null
     */
    public CandidateSessionSummaryReport buildCandidateSessionSummaryReport(final CandidateSession candidateSession) {
        Assert.notNull(candidateSession, "candidateSession");

        /* Look up stored outcomes for this session */
        final List<CandidateSessionOutcome> candidateSessionOutcomes = candidateSessionOutcomeDao.getForSession(candidateSession);

        /* Convert outcomes into an easy form for manipulating */
        final List<String> numericOutcomeIdentifiers = new ArrayList<String>();
        final List<String> otherOutcomeIdentifiers = new ArrayList<String>();
        final List<String> numericOutcomeValues = new ArrayList<String>();
        final List<String> otherOutcomeValues = new ArrayList<String>();
        for (final CandidateSessionOutcome candidateSessionOutcome : candidateSessionOutcomes) {
            final String outcomeIdentifier = candidateSessionOutcome.getOutcomeIdentifier();
            final String outcomeValue = candidateSessionOutcome.getStringValue();
            final BaseType baseType = candidateSessionOutcome.getBaseType();
            if (baseType!=null && baseType.isNumeric() && candidateSessionOutcome.getCardinality()==Cardinality.SINGLE) {
                numericOutcomeIdentifiers.add(outcomeIdentifier);
                numericOutcomeValues.add(outcomeValue);
            }
            else {
                otherOutcomeIdentifiers.add(outcomeIdentifier);
                otherOutcomeValues.add(outcomeValue);
            }
        }

        /* Extract LTI result (if specified, before normalisation) */
        final String ltiResultOutcomeIdentifier = candidateSession.getDelivery().getAssessment().getLtiResultOutcomeIdentifier();
        String ltiResultOutcomeValue = null;
        if (ltiResultOutcomeIdentifier!=null) {
            for (final CandidateSessionOutcome candidateSessionOutcome : candidateSessionOutcomes) {
                if (ltiResultOutcomeIdentifier.equals(candidateSessionOutcome.getOutcomeIdentifier())) {
                    ltiResultOutcomeValue = candidateSessionOutcome.getStringValue();
                    break;
                }
            }
        }

        final CandidateSessionSummaryMetadata summaryMetadata = new CandidateSessionSummaryMetadata(ltiResultOutcomeIdentifier, numericOutcomeIdentifiers, otherOutcomeIdentifiers);
        final User candidate = candidateSession.getCandidate();
        final CandidateSessionSummaryData data = new CandidateSessionSummaryData(candidateSession.getId().longValue(),
                candidateSession.getCreationTime(),
                candidate.getFirstName(),
                candidate.getLastName(),
                candidate.getEmailAddress(),
                candidateSession.isClosed(),
                candidateSession.isTerminated(),
                candidateSession.isExploded(),
                candidateSession.getLisOutcomeReportingStatus(),
                ltiResultOutcomeValue,
                candidateSession.getLisScore(),
                numericOutcomeValues,
                otherOutcomeValues);

        /* read assessmentResult XML */
        final String assessmentResultXml = candidateDataService.readResultFile(candidateSession);

        auditLogger.recordEvent("Generated summary report for CandidateSession #" + candidateSession.getId());
        return new CandidateSessionSummaryReport(summaryMetadata, data, assessmentResultXml);
    }

    //-------------------------------------------------

    public void streamCandidateAssessmentResult(final long xid, final OutputStream outputStream)
            throws DomainEntityNotFoundException, PrivilegeException {
        Assert.notNull(outputStream, "outputStream");
        final CandidateSession candidateSession = lookupCandidateSession(xid);
        streamCandidateAssessmentResult(candidateSession, outputStream);
    }

    public void streamCandidateAssessmentResult(final CandidateSession candidateSession, final OutputStream outputStream) {
        Assert.notNull(candidateSession, "candidateSession");
        Assert.notNull(outputStream, "outputStream");

        candidateDataService.streamAssessmentResult(candidateSession, outputStream);
        auditLogger.recordEvent("Streamed assessmentResult for session #" + candidateSession.getId());
    }

    //-------------------------------------------------

    /**
     * Generates a {@link DeliveryCandidateSummaryReport} containing summary statistics
     * about each candidate session launched on the {@link Delivery} having the given ID (did).
     */
    public DeliveryCandidateSummaryReport buildDeliveryCandidateSummaryReport(final long did)
            throws PrivilegeException, DomainEntityNotFoundException {
        final Delivery delivery = assessmentManagementService.lookupDelivery(did);
        return buildDeliveryCandidateSummaryReport(delivery);
    }

    public DeliveryCandidateSummaryReport buildDeliveryCandidateSummaryReport(final Delivery delivery) {
        Assert.notNull(delivery, "delivery");

        /* Look up all outcomes for all sessions associated with this delivery */
        final List<CandidateSessionOutcome> candidateSessionOutcomes = candidateSessionOutcomeDao.getForDelivery(delivery);

        /* Group results by each individual CandidateSession, also building up lists (ordered sets) of unique outcome identifiers */
        final TreeMap<Long, CandidateSession> candidateSessionByIdMap = new TreeMap<Long, CandidateSession>();
        final Map<Long, Map<String, String>> numericOutcomesBySessionIdMap = new HashMap<Long, Map<String,String>>();
        final Map<Long, Map<String, String>> otherOutcomesBySessionIdMap = new HashMap<Long, Map<String,String>>();
        final LinkedHashSet<String> numericOutcomeIdentifiers = new LinkedHashSet<String>(); /* (Ordered avoiding duplicates) */
        final LinkedHashSet<String> otherOutcomeIdentifiers = new LinkedHashSet<String>(); /* (Ordered avoiding duplicates) */
        for (final CandidateSessionOutcome candidateSessionOutcome : candidateSessionOutcomes) {
            /* Do grouping first */
            final Map<String, String> numericOutcomesForSession, otherOutcomesForSession;
            final CandidateSession candidateSession = candidateSessionOutcome.getCandidateSession();
            final Long xid = candidateSession.getId();
            if (!candidateSessionByIdMap.containsKey(xid)) {
                /* First outcome for this session, so set up appropriate map values */
                candidateSessionByIdMap.put(xid, candidateSession);
                numericOutcomesForSession = new HashMap<String, String>();
                otherOutcomesForSession = new HashMap<String, String>();
                numericOutcomesBySessionIdMap.put(xid, numericOutcomesForSession);
                otherOutcomesBySessionIdMap.put(xid, otherOutcomesForSession);
            }
            else {
                numericOutcomesForSession = numericOutcomesBySessionIdMap.get(xid);
                otherOutcomesForSession = otherOutcomesBySessionIdMap.get(xid);
            }

            /* Record outcomes */
            final String outcomeIdentifier = candidateSessionOutcome.getOutcomeIdentifier();
            final String outcomeValue = candidateSessionOutcome.getStringValue();
            final BaseType baseType = candidateSessionOutcome.getBaseType();
            if (baseType!=null && baseType.isNumeric() && candidateSessionOutcome.getCardinality()==Cardinality.SINGLE) {
                numericOutcomeIdentifiers.add(outcomeIdentifier);
                numericOutcomesForSession.put(outcomeIdentifier, outcomeValue);
            }
            else {
                otherOutcomeIdentifiers.add(outcomeIdentifier);
                otherOutcomesForSession.put(outcomeIdentifier, outcomeValue);
            }
        }

        /* Record metadata */
        final String ltiResultOutcomeIdentifier = delivery.getAssessment().getLtiResultOutcomeIdentifier();
        final CandidateSessionSummaryMetadata summaryMetadata = new CandidateSessionSummaryMetadata(ltiResultOutcomeIdentifier, numericOutcomeIdentifiers, otherOutcomeIdentifiers);

        /* Now build report for each session */
        final List<CandidateSessionSummaryData> rows = new ArrayList<CandidateSessionSummaryData>();
        for (final Entry<Long, CandidateSession> entry : candidateSessionByIdMap.entrySet()) {
            final long xid = entry.getKey();
            final CandidateSession candidateSession = entry.getValue();
            final List<String> numericOutcomeValues = new ArrayList<String>();
            final List<String> otherOutcomeValues = new ArrayList<String>();

            final Map<String, String> numericOutcomesForSession = numericOutcomesBySessionIdMap.get(xid);
            for (final String outcomeIdentifier : numericOutcomeIdentifiers) {
                numericOutcomeValues.add(numericOutcomesForSession.get(outcomeIdentifier));
            }
            final Map<String, String> otherOutcomesForSession = otherOutcomesBySessionIdMap.get(xid);
            for (final String outcomeIdentifier : otherOutcomeIdentifiers) {
                otherOutcomeValues.add(otherOutcomesForSession.get(outcomeIdentifier));
            }
            String ltiResultOutcomeValue = null;
            if (ltiResultOutcomeIdentifier!=null && numericOutcomesForSession!=null) {
                ltiResultOutcomeValue = numericOutcomesForSession.get(ltiResultOutcomeIdentifier);
            }
            final User candidate = candidateSession.getCandidate();
            final CandidateSessionSummaryData row = new CandidateSessionSummaryData(candidateSession.getId().longValue(),
                    candidateSession.getCreationTime(),
                    candidate.getFirstName(),
                    candidate.getLastName(),
                    candidate.getEmailAddress(),
                    candidateSession.isClosed(),
                    candidateSession.isTerminated(),
                    candidateSession.isExploded(),
                    candidateSession.getLisOutcomeReportingStatus(),
                    ltiResultOutcomeValue,
                    candidateSession.getLisScore(),
                    numericOutcomeValues,
                    otherOutcomeValues);
            rows.add(row);
        }

        auditLogger.recordEvent("Generated candidate summary report for Delivery #" + delivery.getId());
        return new DeliveryCandidateSummaryReport(summaryMetadata, rows);
    }

    //-------------------------------------------------

    /**
     * Generates a UTF-8 CSV summary of all {@link CandidateSession}s for the given {@link Delivery},
     * streaming the result to the given {@link OutputStream}
     * <p>
     * The stream will be flushed at the end of this; the caller is responsible for closing it.
     *
     * @param did ID (did) of the required {@link Delivery}
     * @param outputStream {@link OutputStream} to send the results to, which must not be null
     */
    public void streamDeliveryCandidateSummaryReportCsv(final long did, final OutputStream outputStream)
            throws PrivilegeException, DomainEntityNotFoundException, IOException {
        Assert.notNull(outputStream, "outputStream");
        final DeliveryCandidateSummaryReport report = buildDeliveryCandidateSummaryReport(did);
        final CsvWriter csvWriter = new CsvWriter(outputStream, ',', Charsets.UTF_8);
        try {
            /* Write header */
            final StringBuilder headerBuilder = new StringBuilder("Session ID,Email Address,First Name,Last Name,Launch Time,Session Status");
            final CandidateSessionSummaryMetadata metadata = report.getCandidateSessionSummaryMetadata();
            final String lisResultOutcomeIdentifier = metadata.getLisResultOutcomeIdentifier();
            if (lisResultOutcomeIdentifier!=null) {
                /* LTI results set up, so add in details about that */
                headerBuilder.append(',')
                    .append("LTI Result Variable (")
                    .append(lisResultOutcomeIdentifier)
                    .append("),LTI Normalized Score,LTI Result Reporting Status");
            }
            /* Add details about outcome variables */
            for (final String outcomeName : metadata.getNumericOutcomeIdentifiers()) {
                headerBuilder.append(',').append(outcomeName);
            }
            for (final String outcomeName : metadata.getOtherOutcomeIdentifiers()) {
                headerBuilder.append(',').append(outcomeName);
            }
            csvWriter.writeComment(headerBuilder.toString());

            /* Write each row */
            for (final CandidateSessionSummaryData row : report.getRows()) {
                csvWriter.write(Long.toString(row.getSessionId()));
                csvWriter.write(StringUtilities.emptyIfNull(row.getEmailAddress()));
                csvWriter.write(row.getFirstName());
                csvWriter.write(row.getLastName());
                csvWriter.write(row.getLaunchTime().toString());
                csvWriter.write(row.getSessionStatusMessage());
                if (lisResultOutcomeIdentifier!=null) {
                    csvWriter.write(StringUtilities.emptyIfNull(row.getLisResultOutcomeValue()));
                    csvWriter.write(StringUtilities.safeToStringEmptyIfNull(row.getLisScore()));
                    csvWriter.write(StringUtilities.safeToStringEmptyIfNull(row.getLisOutcomeReportingStatus()));
                }
                writeOutcomes(csvWriter, metadata.getNumericOutcomeIdentifiers(), row.getNumericOutcomeValues());
                writeOutcomes(csvWriter, metadata.getOtherOutcomeIdentifiers(), row.getOtherOutcomeValues());
                csvWriter.endRecord();
            }
        }
        finally {
            csvWriter.flush();
        }
    }

    private void writeOutcomes(final CsvWriter csvWriter, final List<String> outcomeNames, final List<String> outcomeValues)
            throws IOException {
        if (outcomeValues!=null) {
            /* Outcomes have been recorded, so output them */
            for (final String outcomeValue : outcomeValues) {
                csvWriter.write(outcomeValue, true);
            }
        }
        else {
            /* No outcomes recorded for this candidate */
            for (int i=0; i<outcomeNames.size(); i++) {
                csvWriter.write("");
            }
        }
    }

    //-------------------------------------------------
    // Report ZIP building

    /**
     * Generates a ZIP file containing the <code>assessmentReport</code>s for all closed or terminated
     * candidate sessions for the given {@link Delivery}, streaming the result to the given stream.
     * <p>
     * The stream will be flushed at the end of this; the caller is responsible for closing it.
     *
     * @param did ID (did) of the required {@link Delivery}
     * @param outputStream {@link OutputStream} to send the results to, which must not be null
     */
    public void streamAssessmentReports(final long did, final OutputStream outputStream)
            throws DomainEntityNotFoundException, PrivilegeException, IOException {
        Assert.notNull(outputStream, "outputStream");

        /* Look up sessions */
        final Delivery delivery = assessmentManagementService.lookupDelivery(did);
        final List<CandidateSession> candidateSessions = candidateSessionDao.getForDelivery(delivery);

        /* Create ZIP builder */
        final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        boolean hasIncludedSomething = false;
        for (final CandidateSession candidateSession : candidateSessions) {
            if (!candidateSession.isExploded() && (candidateSession.isClosed() || candidateSession.isTerminated())) {
                addAssessmentReport(zipOutputStream, candidateSession);
                hasIncludedSomething = true;
            }
        }
        safelyFinishZipStream(zipOutputStream, hasIncludedSomething);
        auditLogger.recordEvent("Generated assessmentResult ZIP file for delviery #" + did);
    }

    private void addAssessmentReport(final ZipOutputStream zipOutputStream, final CandidateSession candidateSession)
            throws IOException {
        final File assessmentResultFile = candidateDataService.getResultFile(candidateSession);
        if (!assessmentResultFile.exists()) {
            throw new QtiWorksLogicException("Expected result file " + assessmentResultFile + " to exist after session is closed");
        }

        /* Work out what to call the ZIP entry */
        final String zipEntryName = makeReportFileName(candidateSession);

        /* Add result to ZIP */
        zipOutputStream.putNextEntry(new ZipEntry(zipEntryName));
        FileUtils.copyFile(assessmentResultFile, zipOutputStream);
        zipOutputStream.closeEntry();
    }

    /**
     * Generates a suitably readable and unique name for the assessmentResult XML file for the
     * given {@link CandidateSession}
     */
    private String makeReportFileName(final CandidateSession candidateSession) {
        final User candidate = candidateSession.getCandidate();
        final StringBuilder entryNameBuilder = new StringBuilder("assessmentResult-")
            .append(candidateSession.getId())
            .append('-');
        if (candidate.getEmailAddress()!=null) {
            entryNameBuilder.append(candidate.getEmailAddress());
        }
        else {
            entryNameBuilder.append(candidate.getFirstName())
                .append('-')
                .append(candidate.getLastName());
        }
        entryNameBuilder.append(".xml");
        return entryNameBuilder.toString();
    }

    private void safelyFinishZipStream(final ZipOutputStream zipOutputStream, final boolean hasIncludedSomething)
            throws IOException {
        if (!hasIncludedSomething) {
            zipOutputStream.putNextEntry(new ZipEntry("NoResults.txt"));
            final OutputStreamWriter commentWriter = new OutputStreamWriter(zipOutputStream, "UTF-8");
            commentWriter.write("There are no results for this delivery yet");
            commentWriter.flush();
            zipOutputStream.closeEntry();
        }
        zipOutputStream.finish();
        zipOutputStream.flush();
    }

}
