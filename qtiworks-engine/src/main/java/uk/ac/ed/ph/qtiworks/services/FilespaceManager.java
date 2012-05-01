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
import uk.ac.ed.ph.qtiworks.base.services.QtiWorksSettings;
import uk.ac.ed.ph.qtiworks.domain.RequestTimestampContext;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.utils.IoUtilities;

import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.FileSandboxResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NetworkHttpResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service to manage the creation and deletion of filespaces/sandboxes
 * for storing things like uploaded {@link AssessmentPackage}s and submitted files
 *
 * @author David McKain
 */
@Service
public final class FilespaceManager {

    private static final Logger logger = LoggerFactory.getLogger(FilespaceManager.class);

    @Resource
    private QtiWorksSettings qtiWorksSettings;

    @Resource
    private RequestTimestampContext requestTimestampContext;

    private File filesystemBaseDirectory;

    @PostConstruct
    public void init() {
        final String filesystemBaseString = qtiWorksSettings.getFilesystemBase();
        logger.info("Filesystem base will be {}", filesystemBaseString);
        this.filesystemBaseDirectory = new File(filesystemBaseString);
        if (!filesystemBaseDirectory.isDirectory()) {
            throw new QtiWorksRuntimeException("Filesystem base path " + filesystemBaseString + " is not a directory");
        }
    }

    public void deleteSandbox(final File sandboxDirectory) {
        ConstraintUtilities.ensureNotNull(sandboxDirectory, "sandboxDirectory");
        try {
            IoUtilities.recursivelyDelete(sandboxDirectory);
        }
        catch (final IOException e) {
            /* We won't fail here, but this is probably a bad sign so let's log it! */
            logger.warn("Could not delete sandbox directory {}", sandboxDirectory, e);
        }
    }

    public File createAssessmentPackageSandbox(final User owner) {
        ConstraintUtilities.ensureNotNull(owner, "owner");
        final String filespaceUri = filesystemBaseDirectory.toURI().toString()
                + "/assessments/"
                + owner.getBusinessKey()
                + "/" + createUniqueRequestComponent();
        return createDirectoryPath(filespaceUri);
    }

    public ResourceLocator createSandboxInputResourceLocator(final File sandboxDirectory) {
        ConstraintUtilities.ensureNotNull(sandboxDirectory, "sandboxDirectory");
        final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        final ChainedResourceLocator result = new ChainedResourceLocator(
                new FileSandboxResourceLocator(packageUriScheme, sandboxDirectory), /* (to resolve things in this package) */
                QtiXmlReader.JQTIPLUS_PARSER_RESOURCE_LOCATOR, /* (to resolve internal HTTP resources, e.g. RP templates) */
                new NetworkHttpResourceLocator() /* (to resolve external HTTP resources, e.g. RP templates, external items) */
        );
        return result;
    }

    private File createDirectoryPath(final String uri) {
        final File directory;
        try {
            directory = new File(URI.create(uri));
            return IoUtilities.ensureDirectoryCreated(directory);
        }
        catch (final RuntimeException e) {
            throw new QtiWorksLogicException("Unexpected failure parsing File URI " + uri);
        }
        catch (final IOException e) {
            throw new QtiWorksLogicException("Unexpected IO failure creating File URI " + uri);
        }
    }

    private String createUniqueRequestComponent() {
        final Date timestamp = requestTimestampContext.getCurrentRequestTimestamp();
        final long threadId = Thread.currentThread().getId();
        return new SimpleDateFormat("yyyyMMdd-HHmmss-SSS").format(timestamp) + "-" + String.valueOf(threadId);
    }

}
