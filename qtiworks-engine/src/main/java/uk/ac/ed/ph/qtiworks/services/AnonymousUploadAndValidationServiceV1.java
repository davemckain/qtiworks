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
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.entities.AnonymousUser;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException;
import uk.ac.ed.ph.qtiworks.web.domain.AssessmentUpload;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

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
    private QtiXmlReader qtiXmlReader;

    @Resource
    private AssessmentPackageFileImporter assessmentPackageImporter;

    @Resource
    private FilespaceManager filespaceManager;

    public AssessmentUpload importData(final InputStream inputStream, final String contentType) throws AssessmentPackageFileImportException {
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
        final AssessmentObjectValidationResult<?> validationResult = validate(importedPackage);
        return new AssessmentUpload(importedPackage, validationResult);
    }

    public void deleteUpload(final AssessmentUpload assessmentUpload) {
        logger.info("Deleting sandbox for upload {}", assessmentUpload);
        deleteSandbox(new File(assessmentUpload.getAssessmentPackage().getSandboxPath()));
    }

    public void deleteSandbox(final File sandboxDirectory) {
        filespaceManager.deleteSandbox(sandboxDirectory);
    }

    @SuppressWarnings("unchecked")
    private <E extends AssessmentObjectValidationResult<?>> E validate(final AssessmentPackage assessmentPackage) {
        final File importSandboxDirectory = new File(assessmentPackage.getSandboxPath());
        final String assessmentObjectHref = assessmentPackage.getAssessmentHref();
        final AssessmentObjectType assessmentObjectType = assessmentPackage.getAssessmentType();
        final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        final ResourceLocator inputResourceLocator = filespaceManager.createSandboxInputResourceLocator(importSandboxDirectory);
        final QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(inputResourceLocator);
        final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
        final URI objectSystemId = packageUriScheme.pathToUri(assessmentObjectHref);
        E result;
        if (assessmentObjectType==AssessmentObjectType.ASSESSMENT_ITEM) {
            result = (E) objectManager.resolveAndValidateItem(objectSystemId);
        }
        else if (assessmentObjectType==AssessmentObjectType.ASSESSMENT_TEST) {
            result = (E) objectManager.resolveAndValidateTest(objectSystemId);
        }
        else {
            throw new QtiWorksLogicException("Unexpected branch " + assessmentObjectType);
        }
        return result;
    }

    private File createRequestSandbox() {
        final AnonymousUser caller = (AnonymousUser) identityContext.getCurrentThreadEffectiveIdentity();
        return filespaceManager.createAssessmentPackageSandbox(caller);
    }
}
