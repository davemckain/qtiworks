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
import uk.ac.ed.ph.qtiworks.QtiWorksRuntimeException;
import uk.ac.ed.ph.qtiworks.domain.DomainConstants;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackageImportType;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageDataImportException;
import uk.ac.ed.ph.qtiworks.services.domain.OutputStreamer;

import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.reading.AssessmentObjectXmlLoader;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentObject;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.FileSandboxResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NetworkHttpResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;

import javax.activation.FileTypeMap;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

/**
 * Provides read-only access (and related services) to {@link AssessmentPackage} files.
 * <p>
 * This is NO authorisation at this level.
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

    /** Default title for assessment if it can't be extracted from the XML */
    private static final String DEFAULT_IMPORT_TITLE = "Assessment";

    @Resource
    private AssessmentPackageFileImporter assessmentPackageFileImporter;

    @Resource
    private QtiXmlReader qtiXmlReader;

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

    //-------------------------------------------------

    /**
     * Wraps around {@link AssessmentPackageFileImporter#importAssessmentPackageData(File, MultipartFile)}
     * Imports the assessment data from the given {@link MultipartFile} into the given
     * sandbox directory, which the caller must have created.
     * <p>
     * Returns a partially-filled unpersisted {@link AssessmentPackage} object representing the
     * results of this.
     *
     * @throws AssessmentPackageDataImportException
     * @throws IllegalArgumentException if any of the provided arguments are null
     * @throws QtiWorksRuntimeException if something unexpected happens, such as experiencing
     *   an {@link IOException}
     */
    public AssessmentPackage importAssessmentPackage(final User owner,
            final MultipartFile multipartFile, final boolean validate)
            throws AssessmentPackageDataImportException {
        Assert.notNull(owner, "owner");
        Assert.notNull(multipartFile, "multipartFile");
        final File packageSandbox = filespaceManager.createAssessmentPackageSandbox(owner);
        final AssessmentPackage assessmentPackage;
        try {
            assessmentPackage = assessmentPackageFileImporter.importAssessmentPackageData(packageSandbox, multipartFile);

            /* Record importer */
            assessmentPackage.setImporter(owner);

            /* Create name for package, using original fileName if available.
             * If not supplied, use name of QTI assessment XML resource */
            String fileName = multipartFile.getOriginalFilename();
            if (fileName==null || fileName.isEmpty()) {
                fileName = assessmentPackage.getAssessmentHref().replaceFirst("^.+/", "");
            }
            assessmentPackage.setFileName(ServiceUtilities.trimSentence(fileName, DomainConstants.ASSESSMENT_NAME_MAX_LENGTH));

            /* Try to extract the title from the QTI XML */
            final String guessedTitle = extractAssessmentTitle(assessmentPackage);
            final String resultingTitle = !StringUtilities.isNullOrEmpty(guessedTitle) ? guessedTitle : DEFAULT_IMPORT_TITLE;
            assessmentPackage.setTitle(ServiceUtilities.trimSentence(resultingTitle, DomainConstants.ASSESSMENT_TITLE_MAX_LENGTH));

            /* Validate (if asked) and record summary result */
            if (validate) {
                final AssessmentObjectValidationResult<?> validationResult = loadAndValidateAssessment(assessmentPackage);
                assessmentPackage.setValidated(true);
                assessmentPackage.setLaunchable(validationResult.getResolvedAssessmentObject().getRootNodeLookup().wasSuccessful());
                assessmentPackage.setErrorCount(validationResult.getModelValidationErrors().size());
                assessmentPackage.setWarningCount(validationResult.getModelValidationWarnings().size());
                assessmentPackage.setValid(validationResult.isValid());
            }
        }
        catch (final AssessmentPackageDataImportException e) {
            filespaceManager.deleteSandbox(packageSandbox);
            throw e;
        }
        catch (final RuntimeException e) {
            filespaceManager.deleteSandbox(packageSandbox);
            throw e;
        }
        return assessmentPackage;
    }

    /**
     * Attempts to extract the title from an {@link AssessmentItem} or {@link AssessmentTest} for
     * bootstrapping the initial state of the resulting {@link AssessmentPackage}.
     * <p>
     * This performs a low level XML parse to save time; proper read/validation using JQTI+
     * is expected to happen later on.
     *
     * @param assessmentPackage
     * @return extracted title, or an empty String if nothing could be extracted.
     */
    public String extractAssessmentTitle(final AssessmentPackage assessmentPackage) {
        Assert.notNull(assessmentPackage, "assessmentPackage");
        final ResourceLocator inputResourceLocator = createResolvingResourceLocator(assessmentPackage);
        final URI assessmentSystemId = createAssessmentObjectUri(assessmentPackage);
        XmlReadResult xmlReadResult;
        try {
            xmlReadResult = qtiXmlReader.read(inputResourceLocator, assessmentSystemId, false);
        }
        catch (final XmlResourceNotFoundException e) {
            throw new QtiWorksLogicException("Assessment resource missing for package " + assessmentPackage, e);
        }
        /* Let's simply extract the title attribute from the document element, and not worry about
         * anything else at this point.
         */
        final Document document = xmlReadResult.getDocument();
        return document!=null ? document.getDocumentElement().getAttribute("title") : "";
    }

    //-------------------------------------------------

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
     * Invokes the JQTI+ load & resolution process on the given {@link AssessmentPackage}.
     *
     * @param assessmentPackage package to validate, which must not be null.
     */
    @SuppressWarnings("unchecked")
    public <E extends ResolvedAssessmentObject<?>>
    E loadAndResolveAssessmentObject(final AssessmentPackage assessmentPackage) {
        final ResourceLocator inputResourceLocator = createResolvingResourceLocator(assessmentPackage);
        final URI assessmentObjectSystemId = createAssessmentObjectUri(assessmentPackage);
        final AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader, inputResourceLocator);
        final AssessmentObjectType assessmentObjectType = assessmentPackage.getAssessmentType();
        E result;
        if (assessmentObjectType==AssessmentObjectType.ASSESSMENT_ITEM) {
            result = (E) assessmentObjectXmlLoader.loadAndResolveAssessmentItem(assessmentObjectSystemId);
        }
        else if (assessmentObjectType==AssessmentObjectType.ASSESSMENT_TEST) {
            result = (E) assessmentObjectXmlLoader.loadAndResolveAssessmentTest(assessmentObjectSystemId);
        }
        else {
            throw new QtiWorksLogicException("Unexpected branch " + assessmentObjectType);
        }
        return result;
    }

    /**
     * Invokes the JQTI+ validator on the given {@link AssessmentPackage}.
     *
     * @param assessmentPackage package to validate, which must not be null.
     */
    @SuppressWarnings("unchecked")
    public <E extends AssessmentObjectValidationResult<?>>
    E loadAndValidateAssessment(final AssessmentPackage assessmentPackage) {
        Assert.notNull(assessmentPackage, "assessmentPackage");
        final ResourceLocator inputResourceLocator = createResolvingResourceLocator(assessmentPackage);
        final URI assessmentObjectSystemId = createAssessmentObjectUri(assessmentPackage);
        final AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader, inputResourceLocator);
        final AssessmentObjectType assessmentObjectType = assessmentPackage.getAssessmentType();
        E result;
        if (assessmentObjectType==AssessmentObjectType.ASSESSMENT_ITEM) {
            result = (E) assessmentObjectXmlLoader.loadResolveAndValidateItem(assessmentObjectSystemId);
        }
        else if (assessmentObjectType==AssessmentObjectType.ASSESSMENT_TEST) {
            result = (E) assessmentObjectXmlLoader.loadResolveAndValidateTest(assessmentObjectSystemId);
        }
        else {
            throw new QtiWorksLogicException("Unexpected logic branch " + assessmentObjectType);
        }

        /* Record summary result back into AssessmentPackage */
        assessmentPackage.setValidated(true);
        assessmentPackage.setLaunchable(result.getResolvedAssessmentObject().getRootNodeLookup().wasSuccessful());
        assessmentPackage.setErrorCount(result.getModelValidationErrors().size());
        assessmentPackage.setWarningCount(result.getModelValidationWarnings().size());
        assessmentPackage.setValid(result.isValid());
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
        streamAssessmentPackageFile(assessmentPackage, assessmentPackage.getAssessmentHref(),
                QTI_CONTENT_TYPE, outputStreamer);
    }

    /**
     * Streams a file within the given {@link AssessmentPackage} to the required {@link OutputStreamer}
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
        streamAssessmentPackageFile(assessmentPackage, fileHref, contentType, outputStreamer);
    }

    private void streamAssessmentPackageFile(final AssessmentPackage assessmentPackage, final String fileHref,
            final String contentType, final OutputStreamer outputStreamer)
            throws IOException {
        /* Compute a suitable entity tag */
        final Date lastModifiedTime = assessmentPackage.getCreationTime(); /* (Safe since packages never change - they get replaced) */
        if (assessmentPackage.getImportType()==AssessmentPackageImportType.BUNDLED_SAMPLE) {
            /* Bundled sample lives in the ClassPath.
             * We'll copy it to a temp File for serving
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
                    ServiceUtilities.copyInputStreamToFile(sampleFileStream, tempFile);
                }
                catch (final IOException e) {
                    throw new QtiWorksRuntimeException("Sample AssessmentPackage file with href " + fileHref
                            + " yielded null lookup. The sample bootstrap process may need to be redone.");
                }
                finally {
                    ServiceUtilities.ensureClose(sampleFileStream);
                }
                ServiceUtilities.streamFile(tempFile, contentType, lastModifiedTime, outputStreamer);
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
                /* (This should not happen due to the way we record what's in each package) */
                throw new QtiWorksRuntimeException("Uploaded AssessmentPackage file with href " + fileHref
                        + " in package " + assessmentPackage + " yielded null lookup");
            }
            ServiceUtilities.streamFile(sandboxFile, contentType, lastModifiedTime, outputStreamer);
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
