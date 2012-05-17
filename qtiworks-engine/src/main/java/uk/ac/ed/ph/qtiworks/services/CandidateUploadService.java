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

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.base.services.Auditor;
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.dao.CandidateFileSubmissionDao;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateFileSubmission;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemSession;
import uk.ac.ed.ph.qtiworks.domain.entities.User;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.types.FileResponseData;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRemove;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

/**
 * Service for handling uploaded candidate data (i.e. responses to
 * <code>uploadInteraction</code> and similar).
 *
 * @author David McKain
 */
@ToRefactor
@Service
public final class CandidateUploadService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateUploadService.class);

    @Resource
    private Auditor auditor;

    @Resource
    private IdentityContext identityContext;

    @Resource
    private FilespaceManager filespaceManager;

    @Resource
    private CandidateFileSubmissionDao candidateFileSubmissionDao;

    @ToRemove
    private File sandboxRootDirectory;

    @PostConstruct
    public void init() {
        sandboxRootDirectory = Files.createTempDir();
        logger.info("Created candidate upload directory at {}", sandboxRootDirectory);
    }

    public CandidateFileSubmission importFileSubmission(final CandidateItemSession candidateItemSession,
            final MultipartFile multipartFile) {
        Assert.ensureNotNull(candidateItemSession, "candidateItemSession");
        Assert.ensureNotNull(multipartFile, "multipartFile");

        /* Save file into filesystem */
        final User candidate = identityContext.getCurrentThreadEffectiveIdentity();
        final File uploadFile = filespaceManager.createCandidateUploadFile(candidateItemSession, candidate);
        try {
            multipartFile.transferTo(uploadFile);
        }
        catch (final Exception e) {
            throw new QtiWorksRuntimeException("Unexpected Exception uploading file submission", e);
        }

        /* Create and persist submission */
        final CandidateFileSubmission result = new CandidateFileSubmission();
        result.setCandidateItemSession(candidateItemSession);
        result.setContentType(multipartFile.getContentType());
        result.setFileName(multipartFile.getOriginalFilename());
        result.setStoredFilePath(uploadFile.getAbsolutePath());
        candidateFileSubmissionDao.persist(result);

        auditor.recordEvent("Imported candidate file submission #" + result.getId());
        return result;
    }

    @ToRefactor
    public FileResponseData importDataV1(final MultipartFile multipartFile) {
        logger.debug("Importing candidate file upload {}", multipartFile);
        final String uploadName = "fileupload-" + Thread.currentThread().getName() + "-" + System.currentTimeMillis();
        final File uploadFile = new File(sandboxRootDirectory, uploadName);
        try {
            multipartFile.transferTo(uploadFile);
        }
        catch (final Exception e) {
            throw new QtiWorksRuntimeException("Unexpected Exception uploading file submission", e);
        }
        return new FileResponseData(uploadFile, multipartFile.getContentType(), multipartFile.getOriginalFilename());
    }
}
