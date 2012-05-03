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
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException.APFIFailureReason;
import uk.ac.ed.ph.qtiworks.services.domain.EnumerableClientFailure;
import uk.ac.ed.ph.qtiworks.utils.IoUtilities;

import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.ImsManifestException;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageSummary;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Helper service for importing assessment package data into the filesystem
 *
 * @see AssessmentServices
 * @see FilespaceManager
 *
 * @author David McKain
 */
@Service
public class AssessmentPackageFileImporter {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentPackageFileImporter.class);

    public static final String SINGLE_FILE_NAME = "qti.xml";

    /**
     * Imports the assessment data from the given {@link InputStream} into the given
     * sandbox directory.
     * <p>
     * It is up to the caller to close the {@link InputStream}
     *
     * @throws AssessmentPackageFileImportException
     * @throws IllegalArgumentException if any of the provided arguments are null
     * @throws QtiWorksRuntimeException if something unexpected happens, such as experiencing
     *   an {@link IOException}
     */
    public AssessmentPackage importAssessmentPackageData(@Nonnull final File importSandboxDirectory,
            @Nonnull final InputStream inputStream,
            @Nonnull final String contentType)
            throws AssessmentPackageFileImportException {
        ConstraintUtilities.ensureNotNull(importSandboxDirectory, "importSandboxDirectory");
        ConstraintUtilities.ensureNotNull(inputStream, "inputStream");
        ConstraintUtilities.ensureNotNull(contentType, "contentType");
        AssessmentPackage result = null;
        if ("application/zip".equals(contentType)) {
            logger.debug("Import is ZIP. Attempting to unpack into {}", importSandboxDirectory);
            result = unpackZipFile(importSandboxDirectory, inputStream);
        }
        else if ("application/xml".equals(contentType) || "text/xml".equals(contentType) || contentType.endsWith("+xml")) {
            logger.debug("Import uses a known XML MIME type {} so saving to {} and treating as XML", contentType, importSandboxDirectory);
            result = importXml(importSandboxDirectory, inputStream);
        }
        else {
            logger.debug("Don't know how to handle MIME type {}", contentType);
            throw new AssessmentPackageFileImportException(new EnumerableClientFailure<APFIFailureReason>(APFIFailureReason.NOT_XML_OR_ZIP));
        }
        logger.debug("Successfully imported files for new {}", result);
        return result;
    }

    private AssessmentPackage importXml(final File importSandboxDirectory, final InputStream inputStream) {
        /* Save XML */
        final File resultFile = new File(importSandboxDirectory, SINGLE_FILE_NAME);
        try {
            IoUtilities.transfer(inputStream, new FileOutputStream(resultFile), false, true);
        }
        catch (final IOException e) {
            throw QtiWorksRuntimeException.unexpectedException(e);
        }

        /* Create AssessmentPackage representing this */
        final AssessmentPackage assessmentPackage = new AssessmentPackage();
        assessmentPackage.setAssessmentType(AssessmentObjectType.ASSESSMENT_ITEM);
        assessmentPackage.setImportType(AssessmentPackageImportType.STANDALONE_ITEM_XML);
        assessmentPackage.setAssessmentHref(SINGLE_FILE_NAME);
        assessmentPackage.setSandboxPath(importSandboxDirectory.getAbsolutePath());
        return assessmentPackage;
    }

    private AssessmentPackage unpackZipFile(final File importSandboxDirectory, final InputStream inputStream)
            throws AssessmentPackageFileImportException {
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
        }
        catch (final EOFException e) {
            /* (Might get this if the ZIP file is truncated for some reason) */
            throw new AssessmentPackageFileImportException(APFIFailureReason.BAD_ZIP, e);
        }
        catch (final ZipException e) {
            throw new AssessmentPackageFileImportException(APFIFailureReason.BAD_ZIP, e);
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
            throw new AssessmentPackageFileImportException(APFIFailureReason.NOT_CONTENT_PACKAGE, e);
        }
        catch (final ImsManifestException e) {
            throw new AssessmentPackageFileImportException(APFIFailureReason.BAD_IMS_MANIFEST, e);
        }
        logger.trace("Submitted content package was successfully expanded as {}", contentPackageSummary);

        /* Check each file and convert URI -> String */
        final Set<String> fileHrefs = new HashSet<String>();
        for (final URI fileHref : contentPackageSummary.getFileHrefs()) {
            fileHrefs.add(checkPackageFileHref(importSandboxDirectory, fileHref));
        }

        /* Now build appropriate result based on number of item & test resources found */
        final int testCount = contentPackageSummary.getTestResources().size();
        final int itemCount = contentPackageSummary.getItemResources().size();
        final AssessmentPackage assessmentPackage = new AssessmentPackage();
        assessmentPackage.setImportType(AssessmentPackageImportType.CONTENT_PACKAGE);
        assessmentPackage.setSandboxPath(importSandboxDirectory.getAbsolutePath());
        assessmentPackage.setFileHrefs(fileHrefs);
        if (testCount==1) {
            /* Treat as a test */
            logger.debug("Package contains 1 test resource, so treating this as an AssessmentTest");
            final String testHref = checkPackageFileHref(importSandboxDirectory, contentPackageSummary.getTestResources().get(0).getHref());
            assessmentPackage.setAssessmentType(AssessmentObjectType.ASSESSMENT_TEST);
            assessmentPackage.setAssessmentHref(testHref);
        }
        else if (testCount==0 && itemCount==1) {
            /* Treat as an item */
            logger.debug("Package contains 1 item resource and no test resources, so treating this as an AssessmentItem");
            final String itemHref = checkPackageFileHref(importSandboxDirectory, contentPackageSummary.getItemResources().get(0).getHref());
            assessmentPackage.setAssessmentType(AssessmentObjectType.ASSESSMENT_ITEM);
            assessmentPackage.setAssessmentHref(itemHref);
        }
        else {
            /* Barf */
            logger.debug("Package contains {} items and {} tests. Don't know how to deal with this", itemCount, testCount);
            throw new AssessmentPackageFileImportException(APFIFailureReason.UNSUPPORTED_PACKAGE_CONTENTS, itemCount, testCount);
        }

        /* Validate and wrap up */
        return assessmentPackage;
    }

    private String checkPackageFileHref(final File importSandboxDirectory, final URI href)
            throws AssessmentPackageFileImportException {
        final String hrefString = href.toString();
        final URI sandboxUri = importSandboxDirectory.toURI();
        final URI resolvedFileUri = sandboxUri.resolve(href);

        /* Make sure href points to something within the sandbox */
        if (!resolvedFileUri.toString().startsWith(sandboxUri.toString())) {
            throw new AssessmentPackageFileImportException(APFIFailureReason.HREF_OUTSIDE_PACKAGE, hrefString);
        }

        /* Make sure file exists */
        final File resolvedFile = new File(resolvedFileUri);
        if (!resolvedFile.exists()) {
            throw new AssessmentPackageFileImportException(APFIFailureReason.FILE_MISSING, hrefString);
        }

        return hrefString;
    }
}
