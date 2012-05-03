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

import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.entities.AnonymousUser;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentUploadAndValidationResultV1;

import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Temporary service for uploading and validating assessments, as used in first few
 * snapshots of QTIWorks
 *
 * @author David McKain
 */
@Service
@ToRefactor
public class AnonymousUploadAndValidationServiceV1 {

    private static final Logger logger = LoggerFactory.getLogger(AnonymousUploadAndValidationServiceV1.class);

    public static final String SINGLE_FILE_NAME = "qti.xml";

    @Resource
    private IdentityContext identityContext;

    @Resource
    private AssessmentPackageFileImporter assessmentPackageImporter;

    @Resource
    private FilespaceManager filespaceManager;

    @Resource
    private AssessmentManagementServices assessmentManagementServices;

    public AssessmentUploadAndValidationResultV1 importAndValidate(final InputStream inputStream, final String contentType) throws AssessmentPackageFileImportException {
        final File sandboxDirectory = createRequestSandbox();
        final AssessmentPackage importedPackage;
        try {
            importedPackage = assessmentPackageImporter.importAssessmentPackageData(sandboxDirectory, inputStream, contentType);
        }
        catch (final AssessmentPackageFileImportException e) {
            logger.info("Upload resulted in an Exception, so deleting sandbox", e);
            deleteSandbox(sandboxDirectory);
            throw e;
        }
        catch (final RuntimeException e) {
            logger.info("Upload resulted in a RuntimeException, so deleting sandbox", e);
            deleteSandbox(sandboxDirectory);
            throw e;
        }
        final AssessmentObjectValidationResult<?> validationResult = assessmentManagementServices.validateAssessment(importedPackage);
        return new AssessmentUploadAndValidationResultV1(importedPackage, validationResult);
    }

    public void deleteUpload(final AssessmentUploadAndValidationResultV1 assessmentUpload) {
        logger.info("Deleting sandbox for upload {}", assessmentUpload);
        deleteSandbox(new File(assessmentUpload.getAssessmentPackage().getSandboxPath()));
    }

    public void deleteSandbox(final File sandboxDirectory) {
        filespaceManager.deleteSandbox(sandboxDirectory);
    }

    private File createRequestSandbox() {
        final AnonymousUser caller = (AnonymousUser) identityContext.getCurrentThreadEffectiveIdentity();
        return filespaceManager.createAssessmentPackageSandbox(caller);
    }
}
