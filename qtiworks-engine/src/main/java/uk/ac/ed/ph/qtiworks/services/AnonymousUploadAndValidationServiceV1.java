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
import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.utils.IoUtilities;
import uk.ac.ed.ph.qtiworks.web.domain.AssessmentUpload;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.FileSandboxResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NetworkHttpResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

/**
 * Temporary service for uploading assessments, as used in first few snapshots of QTIWorks
 *
 * @author David McKain
 */
@Service
@ToRefactor
public class UploadService {

    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    public static final String SINGLE_FILE_NAME = "qti.xml";

    private File sandboxRootDirectory;

    @Resource
    private QtiXmlReader qtiXmlReader;

    @Resource
    private AssessmentPackageImporter assessmentPackageImporter;

    @PostConstruct
    public void init() {
        sandboxRootDirectory = Files.createTempDir();
        logger.info("Created sandbox root directory at {}", sandboxRootDirectory);
    }

    public AssessmentUpload importData(final InputStream inputStream, final String contentType) throws AssessmentPackageImportException {
        final File sandboxDirectory = createRequestSandbox();
        final AssessmentPackage importedPackage;
        try {
            importedPackage = assessmentPackageImporter.importData(sandboxDirectory, inputStream, contentType);
        }
        catch (final AssessmentPackageImportException e) {
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
        try {
            IoUtilities.recursivelyDelete(new File(assessmentUpload.getAssessmentPackage().getBasePath()));
        }
        catch (final IOException e) {
            logger.error("Could not delete upload {}", assessmentUpload);
        }
    }

    public void deleteSandbox(final File sandboxDirectory) {
        try {
            IoUtilities.recursivelyDelete(sandboxDirectory);
        }
        catch (final IOException e) {
            logger.error("Could not delete sandbox {}", sandboxDirectory.getAbsolutePath());
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends AssessmentObjectValidationResult<?>> E validate(final AssessmentPackage assessmentPackage) {
        final File importSandboxDirectory = new File(assessmentPackage.getBasePath());
        final String assessmentObjectHref = assessmentPackage.getAssessmentHref();
        final AssessmentObjectType assessmentObjectType = assessmentPackage.getAssessmentType();
        final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        final ResourceLocator inputResourceLocator = createInputResourceLocator(importSandboxDirectory);
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

    private ResourceLocator createInputResourceLocator(final File importSandboxDirectory) {
        final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        final ChainedResourceLocator result = new ChainedResourceLocator(
                new FileSandboxResourceLocator(packageUriScheme, importSandboxDirectory), /* (to resolve things in this package) */
                QtiXmlReader.JQTIPLUS_PARSER_RESOURCE_LOCATOR, /* (to resolve internal HTTP resources, e.g. RP templates) */
                new NetworkHttpResourceLocator() /* (to resolve external HTTP resources, e.g. RP templates, external items) */
        );
        return result;
    }

    private File createRequestSandbox() {
        final String sandboxName = Thread.currentThread().getName() + "-" + System.currentTimeMillis();
        final File sandboxDirectory = new File(sandboxRootDirectory, sandboxName);
        if (!sandboxDirectory.mkdir()) {
            throw new QtiWorksRuntimeException("Could not create sandbox directory " + sandboxDirectory);
        }
        return sandboxDirectory;
    }
}
