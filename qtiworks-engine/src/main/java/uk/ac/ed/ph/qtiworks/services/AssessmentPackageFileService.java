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

import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackageImportType;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;
import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.FileSandboxResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NetworkHttpResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.activation.FileTypeMap;
import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Provides read-only access (and related services) to {@link AssessmentPackage} files
 *
 * @author David McKain
 */
@Service
public class AssessmentPackageFileService {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentPackageFileService.class);

    /** Fallback content type used when streaming an unknown content type */
    private static final String FALLBACK_CONTENT_TYPE = "application/octet-stream";

    /** Content type used when streaming QTI sources */
    private static final String QTI_CONTENT_TYPE = "application/xml";

    @Resource
    private FilespaceManager filespaceManager;

    @Resource
    private FileTypeMap fileTypeMap;

    /**
     * {@link ResourceLocator} for reading in sample assessment resources. These are bundled
     * within the ClassPath in a fixed way.
     */
    private final ClassPathResourceLocator classPathResourceLocator;

    /**
     * {@link ResourceLocator} for resolving sample assessment resources. These are bundled
     * within the ClassPath in a fixed way.
     */
    private final ResourceLocator sampleResolvingResourceLocator;

    public AssessmentPackageFileService() {
        this.classPathResourceLocator = new ClassPathResourceLocator();
        this.sampleResolvingResourceLocator = new ChainedResourceLocator(
                classPathResourceLocator, /* (to resolve things in the sample set) */
                QtiXmlReader.JQTIPLUS_PARSER_RESOURCE_LOCATOR /* (to resolve internal HTTP resources, e.g. RP templates) */
                /* (No resolution of external resources, since the samples are all self-contained) */
        );
    }

    /**
     * Creates a {@link ResourceLocator} for the given {@link AssessmentPackage} that is capable
     * only of reading in files within the package.
     * <p>
     * Do NOT use this for parsing (e.g. via {@link QtiObjectReader}), as it won't be able
     * to resolve references.
     */
    public ResourceLocator createPackageFileResourceLocator(final AssessmentPackage assessmentPackage) {
        final ResourceLocator result;
        if (assessmentPackage.getImportType()==AssessmentPackageImportType.BUNDLED_SAMPLE) {
            /* This is a bundled sample, which lives in the ClassPath */
            result = classPathResourceLocator;
        }
        else {
            /* Uploaded by user, so resource lives in a sandbox within the filesystem */
            final File sandboxDirectory = new File(assessmentPackage.getSandboxPath());
            final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
            result = new FileSandboxResourceLocator(packageUriScheme, sandboxDirectory);
        }
        return result;
    }

    /**
     * Creates a {@link ResourceLocator} for reading in and resolving the resources associated with
     * the given {@link AssessmentPackage} using a {@link QtiObjectReader}.
     * <p>
     * For an {@link AssessmentPackage} uploaded by a user, the resulting {@link ResourceLocator} will
     * be restricted to the package's sandbox, plus bundled parser resources and external HTTP locations.
     * <p>
     * For the bundled samples, this will look within the ClassPath at the approptiate locations only.
     */
    public ResourceLocator createResolvingResourceLocator(final AssessmentPackage assessmentPackage) {
        final ResourceLocator result;
        if (assessmentPackage.getImportType()==AssessmentPackageImportType.BUNDLED_SAMPLE) {
            /* This is a bundled sample, which lives in the ClassPath */
            result = sampleResolvingResourceLocator;
        }
        else {
            /* Uploaded by user, so resource lives in a sandbox within the filesystem */
            result = new ChainedResourceLocator(
                    createPackageFileResourceLocator(assessmentPackage), /* (to resolve things in this package) */
                    QtiXmlReader.JQTIPLUS_PARSER_RESOURCE_LOCATOR, /* (to resolve internal HTTP resources, e.g. RP templates) */
                    new NetworkHttpResourceLocator() /* (to resolve external HTTP resources, e.g. RP templates, external items) */
            );
        }
        return result;
    }

    //-------------------------------------------------

    /**
     * Generates a URI for the {@link AssessmentObject} within the given {@link AssessmentPackage}
     * that can be passed to a {@link ResourceLocator} created by corresponding methods in this
     * service.
     * <p>
     * For an {@link AssessmentPackage} uploaded by a user, this will be a "package" URI that can
     * access the package's sandbox directory.
     * <p>
     * For the bundled samples, this will be a ClassPath URI
     *
     * @param assessmentPackage
     */
    public URI createAssessmentObjectUri(final AssessmentPackage assessmentPackage) {
        return createAssessmentFileUri(assessmentPackage, assessmentPackage.getAssessmentHref());
    }

    /**
     * Generates a URI for the given file resource within the given {@link AssessmentPackage}
     * that can be passed to a {@link ResourceLocator} created by corresponding methods in this
     * service.
     * <p>
     * For an {@link AssessmentPackage} uploaded by a user, this will be a "package" URI that can
     * access the package's sandbox directory.
     * <p>
     * For the bundled samples, this will be a ClassPath URI
     *
     * (NOTE: This does not check the existence of the resulting resource)
     *
     * @param assessmentPackage
     */
    public URI createAssessmentFileUri(final AssessmentPackage assessmentPackage, final String fileHref) {
        URI result;
        if (assessmentPackage.getImportType()==AssessmentPackageImportType.BUNDLED_SAMPLE) {
            result = QtiSampleAssessment.toClassPathUri(fileHref);
        }
        else {
            final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
            result = packageUriScheme.decodedPathToUri(fileHref);
        }
        return result;
    }

    //-------------------------------------------------

    /**
     * Streams the source of the given {@link AssessmentPackage} to the required {@link OutputStreamer}
     *
     * @param assessmentPackage
     */
    public void streamAssessmentPackageSource(final AssessmentPackage assessmentPackage,
            final OutputStreamer outputStreamer)
            throws IOException {
        streamAssessmentFile(assessmentPackage, assessmentPackage.getAssessmentHref(),
                QTI_CONTENT_TYPE, outputStreamer);
    }

    /**
     * Streams the source of the given {@link AssessmentPackage} to the required {@link OutputStreamer}
     * <p>
     * (NB: this service does not whether this file is white-listed. The caller should ensure this
     * in advance.)
     *
     * @param assessmentPackage
     */
    public void streamAssessmentPackageFile(final AssessmentPackage assessmentPackage,
            final String fileHref, final OutputStreamer outputStreamer)
            throws IOException {
        final String contentType = getResourceContentType(fileHref);
        streamAssessmentFile(assessmentPackage, fileHref, contentType, outputStreamer);
    }

    private void streamAssessmentFile(final AssessmentPackage assessmentPackage, final String fileHref,
            final String contentType, final OutputStreamer outputStreamer)
            throws IOException {
        if (assessmentPackage.getImportType()==AssessmentPackageImportType.BUNDLED_SAMPLE) {
            /* Bundled sample lives in the ClassPath. We'll copy it to a temp File
             * for serving
             */
            final File tempFile = filespaceManager.createTempFile();
            try {
                final URI fileClassPathSystemId = QtiSampleAssessment.toClassPathUri(fileHref);
                final InputStream sampleFileStream = classPathResourceLocator.findResource(fileClassPathSystemId);
                if (sampleFileStream==null) {
                    throw new QtiWorksRuntimeException("Sample AssessmentPackage file with href " + fileHref
                            + " yielded null lookup. The sample bootstrap process may need to be redone.");
                }
                try {
                    FileUtils.copyInputStreamToFile(sampleFileStream, tempFile);
                    sampleFileStream.close();
                }
                catch (final IOException e) {
                    throw new QtiWorksRuntimeException("Sample AssessmentPackage file with href " + fileHref
                            + " yielded null lookup. The sample bootstrap process may need to be redone.");
                }
                finally {
                    IOUtils.closeQuietly(sampleFileStream);
                }
                streamPackageFile(assessmentPackage, tempFile, contentType, outputStreamer);
            }
            finally {
                if (!tempFile.delete()) {
                    logger.warn("Could not delete temp file {}", tempFile);
                }
            }
        }
        else {
            /* Uploaded file, which exists in the sandbox */
            final File sandboxDirectory = new File(assessmentPackage.getSandboxPath());
            final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
            final FileSandboxResourceLocator fileSandboxResourceLocator = new FileSandboxResourceLocator(packageUriScheme, sandboxDirectory);
            final File sandboxFile = fileSandboxResourceLocator.findSandboxFile(packageUriScheme.decodedPathToUri(fileHref));
            if (sandboxFile==null) {
                throw new QtiWorksRuntimeException("Uploaded AssessmentPackage file with href " + fileHref
                        + " in package " + assessmentPackage + " yielded null lookup");
            }
            streamPackageFile(assessmentPackage, sandboxFile, contentType, outputStreamer);
        }
    }

    private void streamPackageFile(final AssessmentPackage assessmentPackage, final File file,
            final String contentType, final OutputStreamer outputStreamer)
            throws IOException {
        final long contentLength = file.length();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            outputStreamer.stream(contentType, contentLength, assessmentPackage.getCreationTime(), fileInputStream);
        }
        finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    private String getResourceContentType(final String href) {
        final String result;
        synchronized (fileTypeMap) {
            result = fileTypeMap.getContentType(href);
        }
        if (result.equals(FALLBACK_CONTENT_TYPE)) {
            logger.warn("MIME type lookup for href {} yielded fallback {}", href, FALLBACK_CONTENT_TYPE);
        }
        return result;
    }
}
