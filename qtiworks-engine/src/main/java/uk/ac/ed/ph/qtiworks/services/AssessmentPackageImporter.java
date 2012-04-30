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
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackageImportType;
import uk.ac.ed.ph.qtiworks.services.AssessmentPackageImportException.FailureReason;
import uk.ac.ed.ph.qtiworks.utils.IoUtilities;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.ImsManifestException;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageSummary;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for importing {@link AssessmentPackage} data
 *
 * @author David McKain
 */
public final class AssessmentPackageImporter {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentPackageImporter.class);

    public static final String SINGLE_FILE_NAME = "qti.xml";

    /**
     * @throws AssessmentPackageImportException
     * @throws QtiWorksRuntimeException if something unexpected happens, such as experiencing
     *   an {@link IOException}
     */
    public AssessmentPackage importData(final File importSandboxDirectory, final InputStream inputStream, final String contentType)
            throws AssessmentPackageImportException {
        AssessmentPackage result = null;
        if ("application/zip".equals(contentType)) {
            logger.debug("Import is ZIP. Attempting to unpack into {}", importSandboxDirectory);
            result = extractZipFile(inputStream, importSandboxDirectory);
        }
        else if ("application/xml".equals(contentType) || "text/xml".equals(contentType) || contentType.endsWith("+xml")) {
            logger.debug("Import uses a known XML MIME type {} so saving to {} and treating as XML", contentType, importSandboxDirectory);
            result = importXml(inputStream, importSandboxDirectory);
        }
        else {
            logger.debug("Don't know how to handle MIME type {}", contentType);
            throw new AssessmentPackageImportException(new EnumerableClientFailure<FailureReason>(FailureReason.NOT_XML_OR_ZIP));
        }
        return result;
    }

    private AssessmentPackage importXml(final InputStream inputStream, final File importSandboxDirectory) {
        /* Save XML */
        final File resultFile = new File(importSandboxDirectory, SINGLE_FILE_NAME);
        try {
            IoUtilities.transfer(inputStream, new FileOutputStream(resultFile));
        }
        catch (final IOException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }

        final AssessmentPackage assessmentPackage = new AssessmentPackage();
        assessmentPackage.setAssessmentType(AssessmentObjectType.ASSESSMENT_ITEM);
        assessmentPackage.setAssessmentHref(SINGLE_FILE_NAME);
        assessmentPackage.setBasePath(importSandboxDirectory.getAbsolutePath());
        return assessmentPackage;
    }

    private AssessmentPackage extractZipFile(final InputStream inputStream, final File importSandboxDirectory)
            throws AssessmentPackageImportException {
        /* Extract ZIP contents */
        ZipEntry zipEntry;
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        try {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                final File destFile = new File(importSandboxDirectory, zipEntry.getName());
                if (!zipEntry.isDirectory()) {
                    IoUtilities.ensureFileCreated(destFile);
                    IoUtilities.transfer(zipInputStream, new FileOutputStream(destFile), false, true);
                    zipInputStream.closeEntry();
                }
            }
            zipInputStream.close();
        }
        catch (final ZipException e) {
            throw new AssessmentPackageImportException(new EnumerableClientFailure<FailureReason>(FailureReason.BAD_ZIP), e);

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
            throw new AssessmentPackageImportException(new EnumerableClientFailure<FailureReason>(FailureReason.NOT_CONTENT_PACKAGE), e);
        }
        catch (final ImsManifestException e) {
            throw new AssessmentPackageImportException(new EnumerableClientFailure<FailureReason>(FailureReason.BAD_IMS_MANIFEST), e);
        }
        final int testCount = contentPackageSummary.getTestResourceHrefs().size();
        final int itemCount = contentPackageSummary.getItemResourceHrefs().size();

        final AssessmentPackage assessmentPackage = new AssessmentPackage();
        assessmentPackage.setImportType(AssessmentPackageImportType.CONTENT_PACKAGE);
        assessmentPackage.setBasePath(importSandboxDirectory.getAbsolutePath());
        assessmentPackage.setFileHrefs(contentPackageSummary.getFileHrefs());
        if (testCount==1) {
            /* Treat as a test */
            logger.info("Package contains 1 test resource, so treating this as an AssessmentTest");
            assessmentPackage.setAssessmentType(AssessmentObjectType.ASSESSMENT_TEST);
            assessmentPackage.setAssessmentHref(contentPackageSummary.getTestResourceHrefs().iterator().next());
        }
        else if (testCount==0 && itemCount==1) {
            /* Treat as an item */
            logger.info("Package contains 1 item resource and no test resources, so treating this as an AssessmentItem");
            assessmentPackage.setAssessmentType(AssessmentObjectType.ASSESSMENT_ITEM);
            assessmentPackage.setAssessmentHref(contentPackageSummary.getItemResourceHrefs().iterator().next());
        }
        else {
            /* Barf */
            logger.warn("Package contains {} items and {} tests. Don't know how to deal with this", itemCount, testCount);
            throw new AssessmentPackageImportException(new EnumerableClientFailure<FailureReason>(FailureReason.UNSUPPORTED_PACKAGE_CONTENTS, itemCount, testCount));
        }

        /* Validate and wrap up */
        return assessmentPackage;
    }

    public ResourceLocator createAssessmentResourceLocator(final File importSandboxDirectory) {
        final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        final ChainedResourceLocator result = new ChainedResourceLocator(
                new FileSandboxResourceLocator(packageUriScheme, importSandboxDirectory), /* (to resolve things in this package) */
                QtiXmlReader.JQTIPLUS_PARSER_RESOURCE_LOCATOR, /* (to resolve internal HTTP resources, e.g. RP templates) */
                new NetworkHttpResourceLocator() /* (to resolve external HTTP resources, e.g. RP templates, external items) */
        );
        return result;
    }

    public URI createAssessmentResourceUri(final String assessmentHref) {
        final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        return packageUriScheme.pathToUri(assessmentHref);
    }

}
