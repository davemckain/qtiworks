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

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.ContentPackageResource;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Helper service for importing assessment package data into the filesystem
 *
 * @see AssessmentManagementService
 * @see FilespaceManager
 *
 * @author David McKain
 */
@Service
public class AssessmentPackageFileImporter {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentPackageFileImporter.class);

    /** File name that will be used when uploading standalone XML */
    private static final String STANDALONE_XML_IMPORT_FILE_NAME = "qti.xml";

    /** Allowed MIME types for ZIP files */
    private static String[] ZIP_MIME_TYPES = new String[] {
            "application/zip",
            "application/x-zip",
            "application/x-zip-compressed",
            "application/x-compress",
            "application/x-compressed",
            "multipart/x-zip"
    };
    static {
        Arrays.sort(ZIP_MIME_TYPES);
    }

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
    public AssessmentPackage importAssessmentPackageData(final File importSandboxDirectory,
            final InputStream inputStream,
            final String contentType)
            throws AssessmentPackageFileImportException {
        Assert.ensureNotNull(importSandboxDirectory, "importSandboxDirectory");
        Assert.ensureNotNull(inputStream, "inputStream");
        Assert.ensureNotNull(contentType, "contentType");
        AssessmentPackage result = null;
        if (Arrays.binarySearch(ZIP_MIME_TYPES, contentType) >= 0) {
            logger.debug("Import is ZIP. Attempting to unpack into {}", importSandboxDirectory);
            result = unpackZipFile(importSandboxDirectory, inputStream);
        }
        else if ("application/xml".equals(contentType) || "text/xml".equals(contentType) || contentType.endsWith("+xml")) {
            logger.debug("Import uses a known XML MIME type {} so saving to {} and treating as XML", contentType, importSandboxDirectory);
            result = importXml(importSandboxDirectory, inputStream);
        }
        else {
            logger.warn("User uploaded content with unsupported MIME type {}", contentType);
            throw new AssessmentPackageFileImportException(new EnumerableClientFailure<APFIFailureReason>(APFIFailureReason.NOT_XML_OR_ZIP));
        }
        logger.debug("Successfully imported files for new {}", result);
        return result;
    }

    private AssessmentPackage importXml(final File importSandboxDirectory, final InputStream inputStream) {
        /* Save XML */
        final File resultFile = new File(importSandboxDirectory, STANDALONE_XML_IMPORT_FILE_NAME);
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
        assessmentPackage.setAssessmentHref(STANDALONE_XML_IMPORT_FILE_NAME);
        assessmentPackage.setSandboxPath(importSandboxDirectory.getAbsolutePath());
        assessmentPackage.setQtiFileHrefs(new HashSet<String>(Arrays.asList(STANDALONE_XML_IMPORT_FILE_NAME)));
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

        /* Build appropriate result based on number of item & test resources found */
        final int testCount = contentPackageSummary.getTestResources().size();
        final int itemCount = contentPackageSummary.getItemResources().size();
        final AssessmentPackage assessmentPackage = new AssessmentPackage();
        assessmentPackage.setImportType(AssessmentPackageImportType.CONTENT_PACKAGE);
        assessmentPackage.setSandboxPath(importSandboxDirectory.getAbsolutePath());
        if (testCount==1) {
            /* Treat as a test */
            logger.debug("Package contains 1 test resource, so treating this as an AssessmentTest");
            assessmentPackage.setAssessmentType(AssessmentObjectType.ASSESSMENT_TEST);
            assessmentPackage.setAssessmentHref(checkPackageFile(importSandboxDirectory,
                    contentPackageSummary.getTestResources().get(0).getHref()));
        }
        else if (testCount==0 && itemCount==1) {
            /* Treat as an item */
            logger.debug("Package contains 1 item resource and no test resources, so treating this as an AssessmentItem");
            assessmentPackage.setAssessmentType(AssessmentObjectType.ASSESSMENT_ITEM);
            assessmentPackage.setAssessmentHref(checkPackageFile(importSandboxDirectory,
                    contentPackageSummary.getItemResources().get(0).getHref()));
        }
        else {
            /* Barf */
            logger.debug("Package contains {} items and {} tests. Don't know how to deal with this", itemCount, testCount);
            throw new AssessmentPackageFileImportException(APFIFailureReason.UNSUPPORTED_PACKAGE_CONTENTS, itemCount, testCount);
        }

        /* Build up Set of all files in the package. We need to be a bit careful to flag up the
         * ones that correspond to QTI files. We'll assume that QTI files are the *first* ones
         * listed in each item/test resource in the CP, though this is not clear from the CP spec
         */
        final Set<String> packageQtiFileBuilder = new HashSet<String>();
        final Set<String> packageSafeFileBuilder = new HashSet<String>();
        buildPackageFileMap(importSandboxDirectory, packageQtiFileBuilder, packageSafeFileBuilder, contentPackageSummary.getItemResources());
        buildPackageFileMap(importSandboxDirectory, packageQtiFileBuilder, packageSafeFileBuilder, contentPackageSummary.getTestResources());
        assessmentPackage.setQtiFileHrefs(packageQtiFileBuilder);
        assessmentPackage.setSafeFileHrefs(packageSafeFileBuilder);

        return assessmentPackage;
    }

    /**
     * Builds up the provided Map of all files within this package, flagging those which correspond
     * to QTI XML files, which are assumed to be the *first* files declared within item or test
     * resource elements in the manifest.
     */
    private void buildPackageFileMap(final File importSandboxDirectory,
            final Set<String> packageQtiFileBuilder, final Set<String> packageSafeFileBuilder,
            final List<ContentPackageResource> qtiResources)
            throws AssessmentPackageFileImportException {
        for (final ContentPackageResource qtiResource : qtiResources) {
            final List<URI> fileHrefs = qtiResource.getFileHrefs();
            boolean isFirst = true;
            for (final URI fileHref : fileHrefs) {
                final String fileHrefString = checkPackageFile(importSandboxDirectory, fileHref);
                if (isFirst) {
                    packageQtiFileBuilder.add(fileHrefString);
                }
                else {
                    packageSafeFileBuilder.add(fileHrefString);
                }
                isFirst = false;
            }
        }
    }

    /**
     * Checks the given file URI (href) and makes sure it exists within the sandbox.
     * Returns the original href as a String if successful, otherwise throws
     * {@link AssessmentPackageFileImportException}.
     */
    private String checkPackageFile(final File importSandboxDirectory, final URI href)
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
