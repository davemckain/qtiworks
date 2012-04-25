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
import uk.ac.ed.ph.qtiworks.UploadException;
import uk.ac.ed.ph.qtiworks.UploadException.UploadFailureReason;
import uk.ac.ed.ph.qtiworks.web.domain.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.web.domain.AssessmentPackage.AssessmentType;
import uk.ac.ed.ph.qtiworks.web.domain.AssessmentUpload;
import uk.ac.ed.ph.qtiworks.web.domain.AssessmentUpload.UploadType;

import uk.ac.ed.ph.jqtiplus.internal.util.IOUtilities;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.ImsManifestException;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageSummary;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.FileSandboxResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NetworkHttpResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

/**
 * @author David McKain
 */
@Service
public class UploadService {

    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    public static final String SINGLE_FILE_NAME = "qti.xml";

    private File sandboxRootDirectory;

    @Resource
    private QtiXmlReader qtiXmlReader;

    @PostConstruct
    public void init() {
        sandboxRootDirectory = Files.createTempDir();
        logger.info("Created sandbox root directory at {}", sandboxRootDirectory);
    }

    public AssessmentUpload importData(final InputStream inputStream, final String contentType) throws UploadException {
        final File sandboxDirectory = createRequestSandbox();
        AssessmentUpload result = null;
        try {
            if ("application/zip".equals(contentType)) {
                logger.info("Attempting to unpack ZIP to {}", sandboxDirectory);
                result = extractZipFile(inputStream, sandboxDirectory);
            }
            else if ("application/xml".equals(contentType) || "text/xml".equals(contentType) || contentType.endsWith("+xml")) {
                logger.info("Upload uses a known XML MIME type {} so saving to {} and treating as XML", contentType, sandboxDirectory);
                result = importXml(inputStream, sandboxDirectory);
            }
            else {
                logger.info("Don't know how to handle MIME type {}", contentType);
                throw new UploadException(UploadFailureReason.NOT_XML_OR_ZIP);
            }
        }
        catch (final UploadException e) {
            logger.info("Upload resulted in an Exception, so deleting sandbox", e);
            deleteSandbox(sandboxDirectory);
            throw e;
        }
        catch (final RuntimeException e) {
            logger.info("Upload resulted in a RuntimeException, so deleting sandbox", e);
            deleteSandbox(sandboxDirectory);
            throw e;
        }
        return result;
    }

    public void deleteUpload(final AssessmentUpload assessmentUpload) {
        logger.info("Deleting sandbox for upload {}", assessmentUpload);
        try {
            IOUtilities.recursivelyDelete(new File(assessmentUpload.getAssessmentPackage().getSandboxPath()));
        }
        catch (final IOException e) {
            logger.error("Could not delete upload {}", assessmentUpload);
        }
    }

    public void deleteSandbox(final File sandboxDirectory) {
        try {
            IOUtilities.recursivelyDelete(sandboxDirectory);
        }
        catch (final IOException e) {
            logger.error("Could not delete sandbox {}", sandboxDirectory.getAbsolutePath());
        }
    }

    private AssessmentUpload importXml(final InputStream inputStream, final File importSandboxDirectory) {
        final File resultFile = new File(importSandboxDirectory, SINGLE_FILE_NAME);
        try {
            IOUtilities.transfer(inputStream, new FileOutputStream(resultFile));
        }
        catch (final IOException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }

        final AssessmentPackage assessmentPackage = new AssessmentPackage();
        assessmentPackage.setAssessmentType(AssessmentType.ITEM);
        assessmentPackage.setAssessmentObjectHref(SINGLE_FILE_NAME);
        assessmentPackage.setSandboxPath(importSandboxDirectory.getAbsolutePath());

        /* Attempt to validate as an item */
        final ItemValidationResult validationResult = validate(importSandboxDirectory, SINGLE_FILE_NAME, ItemValidationResult.class);

        return new AssessmentUpload(assessmentPackage, UploadType.STANDALONE, validationResult);
    }

    private AssessmentUpload extractZipFile(final InputStream inputStream, final File importSandboxDirectory)
            throws UploadException {
        /* Extract ZIP contents */
        logger.info("Expanding ZIP file from stream {} to sandbox {}", inputStream, importSandboxDirectory);
        ZipEntry zipEntry;
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        try {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                final File destFile = new File(importSandboxDirectory, zipEntry.getName());
                if (!zipEntry.isDirectory()) {
                    IOUtilities.ensureFileCreated(destFile);
                    IOUtilities.transfer(zipInputStream, new FileOutputStream(destFile), false, true);
                    zipInputStream.closeEntry();
                }
            }
            zipInputStream.close();
        }
        catch (final ZipException e) {
            throw new UploadException(UploadFailureReason.BAD_ZIP, e);

        }
        catch (final IOException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }

        /* Expand content package */
        final QtiContentPackageExtractor contentPackageExtractor = new QtiContentPackageExtractor(importSandboxDirectory);
        QtiContentPackageSummary contentPackageSummary;
        try {
            contentPackageSummary = contentPackageExtractor.parse();
        }
        catch (final XmlResourceNotFoundException e) {
            throw new UploadException(UploadFailureReason.NOT_CONTENT_PACKAGE, e);
        }
        catch (final ImsManifestException e) {
            throw new UploadException(UploadFailureReason.BAD_IMS_MANIFEST, e);
        }
        final int testCount = contentPackageSummary.getTestResourceHrefs().size();
        final int itemCount = contentPackageSummary.getItemResourceHrefs().size();

        final AssessmentPackage assessmentPackage = new AssessmentPackage();
        assessmentPackage.setSandboxPath(importSandboxDirectory.getAbsolutePath());
        AssessmentObjectValidationResult<?> validationResult;
        if (testCount==1) {
            /* Treat as a test */
            logger.info("Package contains 1 test resource, so treating this as an AssessmentTest");
            assessmentPackage.setAssessmentType(AssessmentType.TEST);
            assessmentPackage.setAssessmentObjectHref(contentPackageSummary.getTestResourceHrefs().iterator().next());
            assessmentPackage.setFileHrefs(contentPackageSummary.getFileHrefs());
            validationResult = validate(assessmentPackage, TestValidationResult.class);
        }
        else if (testCount==0 && itemCount==1) {
            /* Treat as an item */
            logger.info("Package contains 1 item resource and no test resources, so treating this as an AssessmentItem");
            assessmentPackage.setAssessmentType(AssessmentType.ITEM);
            assessmentPackage.setAssessmentObjectHref(contentPackageSummary.getItemResourceHrefs().iterator().next());
            assessmentPackage.setFileHrefs(contentPackageSummary.getFileHrefs());
            validationResult = validate(assessmentPackage, ItemValidationResult.class);
        }
        else {
            /* Barf */
            logger.warn("Package contains {} items and {} tests. Don't know how to deal with this", itemCount, testCount);
            throw new UploadException(UploadFailureReason.UNSUPPORTED_PACKAGE_CONTENTS);
        }

        /* Validate and wrap up */
        return new AssessmentUpload(assessmentPackage, UploadType.CONTENT_PACKAGE, validationResult);
    }

    private <E extends AssessmentObjectValidationResult<?>> E validate(final AssessmentPackage assessmentPackage, final Class<E> resultClass) {
        return validate(new File(assessmentPackage.getSandboxPath()), assessmentPackage.getAssessmentObjectHref(), resultClass);
    }

    @SuppressWarnings("unchecked")
    private <E extends AssessmentObjectValidationResult<?>> E validate(final File importSandboxDirectory, final String assessmentObjectHref, final Class<E> resultClass) {
        final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        final ResourceLocator inputResourceLocator = createInputResourceLocator(importSandboxDirectory);
        final QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(inputResourceLocator);
        final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
        final URI objectSystemId = packageUriScheme.pathToUri(assessmentObjectHref);
        E result;
        if (resultClass.equals(ItemValidationResult.class)) {
            result = (E) objectManager.resolveAndValidateItem(objectSystemId);
        }
        else if (resultClass.equals(TestValidationResult.class)) {
            result = (E) objectManager.resolveAndValidateTest(objectSystemId);
        }
        else {
            throw new QtiWorksRuntimeException("Unexpected switch case " + resultClass);
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
