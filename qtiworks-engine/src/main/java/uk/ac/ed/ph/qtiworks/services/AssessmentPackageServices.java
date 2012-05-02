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
import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.Privilege;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentDeliveryDao;
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentDelivery;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.User;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageStateException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageStateException.APSFailureReason;

import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.utils.contentpackaging.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.FileSandboxResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NetworkHttpResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

/**
 * Services for managing {@link AssessmentPackage}s
 *
 * @author David McKain
 */
@Service
public class AssessmentPackageServices {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentPackageServices.class);

    public static final String DEFAULT_IMPORT_TITLE = "My Assessment";

    @Resource
    IdentityContext identityContext;

    @Resource
    FilespaceManager filespaceManager;

    @Resource
    AssessmentPackageFileImporter assessmentPackageFileImporter;

    @Resource
    AssessmentPackageDao assessmentPackageDao;

    @Resource
    AssessmentDeliveryDao assessmentDeliveryDao;

    @Resource
    QtiXmlReader qtiXmlReader;

    public List<AssessmentPackage> getCallerAssessmentPackages() {
        return assessmentPackageDao.getForOwner(identityContext.getCurrentThreadEffectiveIdentity());
    }

    /**
     * Creates and persists a new {@link AssessmentPackage} from the data provided by the given
     * {@link InputStream} and having the given content type.
     * <p>
     * Success post-conditions:
     * - the {@link InputStream} is left open
     * - a new {@link AssessmentPackage} is persisted, and its data is safely stored in a sandbox
     *
     * @param inputStream
     * @param contentType
     *
     * @throws PrivilegeException
     * @throws AssessmentPackageFileImportException
     * @throws QtiWorksRuntimeException
     */
    public AssessmentPackage importAssessmentPackage(@Nonnull final InputStream inputStream, @Nonnull final String contentType)
            throws PrivilegeException, AssessmentPackageFileImportException {
        ConstraintUtilities.ensureNotNull(inputStream, "inputStream");
        ConstraintUtilities.ensureNotNull(contentType, "contentType");
        ensureCallerCanUploadPackages();

        /* First, upload the data into a sandbox */
        final AssessmentPackage assessmentPackage = importPackageFiles(inputStream, contentType);

        /* FIXME: Make this more clever */
        assessmentPackage.setName("Assessment");

        /* Guess a title */
        final String guessedTitle = guessAssessmentTitle(assessmentPackage);
        assessmentPackage.setTitle(!guessedTitle.isEmpty() ? guessedTitle : DEFAULT_IMPORT_TITLE);

        System.out.println("PERSISTING " + assessmentPackage);

        /* Persist date */
        try {
            assessmentPackageDao.persist(assessmentPackage);
        }
        catch (final Exception e) {
            logger.warn("Persistence of AssessmentPackage failed - deleting its sandbox", assessmentPackage);
            deleteAssessmentPackageSandbox(assessmentPackage);
            throw new QtiWorksRuntimeException("Failed to persist AssessmentPackage " + assessmentPackage, e);
        }
        logger.info("Created new AssessmentPackage {}", assessmentPackage);
        return assessmentPackage;
    }

    /**
     * NOTE: Not allowed to go item->test or test->item.
     *
     * @param inputStream
     * @param contentType
     * @throws AssessmentPackageStateException
     * @throws PrivilegeException
     * @throws AssessmentPackageFileImportException
     */
    public AssessmentPackage replaceAssessmentPackageFiles(@Nonnull final AssessmentPackage assessmentPackage,
            @Nonnull final InputStream inputStream, @Nonnull final String contentType)
            throws AssessmentPackageStateException, PrivilegeException,
            AssessmentPackageFileImportException {
        ConstraintUtilities.ensureNotNull(assessmentPackage, "assessmentPackage");
        ConstraintUtilities.ensureNotNull(inputStream, "inputStream");
        ConstraintUtilities.ensureNotNull(contentType, "contentType");
        ensureCallerMayChange(assessmentPackage);
        ensureNoDeliveries(assessmentPackage);

        /* Upload data into a new sandbox */
        final AssessmentPackage replacedAssessmentPackage = importPackageFiles(inputStream, contentType);

        /* Make sure we haven't gone item->test or test->item */
        if (replacedAssessmentPackage.getAssessmentType()!=assessmentPackage.getAssessmentType()) {
            throw new AssessmentPackageStateException(APSFailureReason.CANNOT_CHANGE_ASSESSMENT_TYPE);
        }

        /* Merge data in */
        assessmentPackage.setAssessmentHref(replacedAssessmentPackage.getAssessmentHref());
        assessmentPackage.setFileHrefs(replacedAssessmentPackage.getFileHrefs());
        assessmentPackage.setImportType(replacedAssessmentPackage.getImportType());
        assessmentPackage.setSandboxPath(replacedAssessmentPackage.getSandboxPath());
        assessmentPackage.setValidated(false);
        assessmentPackage.setValid(false);

        /* Finally update DB */
        try {
            assessmentPackageDao.update(assessmentPackage);
        }
        catch (final Exception e) {
            logger.warn("Failed to update state of AssessmentPackage {} after file replacement - deleting new sandbox", e);
            deleteAssessmentPackageSandbox(replacedAssessmentPackage);
            throw new QtiWorksRuntimeException("Failed to update AssessmentPackage entity " + assessmentPackage, e);
        }
        return assessmentPackage;
    }

    public void deleteAssessmentPackage(@Nonnull final AssessmentPackage assessmentPackage)
            throws AssessmentPackageStateException, PrivilegeException {
        ConstraintUtilities.ensureNotNull(assessmentPackage, "assessmentPackage");
        ensureNoDeliveries(assessmentPackage); /* FIXME: This is too strict, but will require a lot more logic to relax */
        ensureCallerMayChange(assessmentPackage);

        /* Delete entity from DB */
        try {
            assessmentPackageDao.remove(assessmentPackage);
        }
        catch (final Exception e) {
            throw new QtiWorksRuntimeException("Failed to delete AssessmentPackage entity " + assessmentPackage, e);
        }
        finally {
            deleteAssessmentPackage(assessmentPackage);
        }
    }

    //-------------------------------------------------

    public ResourceLocator createAssessmentResourceLocator(final AssessmentPackage assessmentPackage) {
        final File sandboxDirectory = new File(assessmentPackage.getSandboxPath());
        final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        final ChainedResourceLocator result = new ChainedResourceLocator(
                new FileSandboxResourceLocator(packageUriScheme, sandboxDirectory), /* (to resolve things in this package) */
                QtiXmlReader.JQTIPLUS_PARSER_RESOURCE_LOCATOR, /* (to resolve internal HTTP resources, e.g. RP templates) */
                new NetworkHttpResourceLocator() /* (to resolve external HTTP resources, e.g. RP templates, external items) */
        );
        return result;
    }

    public URI createAssessmentObjectUri(final AssessmentPackage assessmentPackage) {
        final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        return packageUriScheme.pathToUri(assessmentPackage.getAssessmentHref().toString());
    }

    //-------------------------------------------------

    /**
     * @throws QtiWorksLogicException if sandboxPath is already null
     */
    private void deleteAssessmentPackageSandbox(final AssessmentPackage assessmentPackage) {
        final String sandboxPath = assessmentPackage.getSandboxPath();
        if (sandboxPath==null) {
            throw new QtiWorksLogicException("AssessmentPackage sandbox is null");
        }
        filespaceManager.deleteSandbox(new File(sandboxPath));
        assessmentPackage.setSandboxPath(null);
    }

    /**
     * @throws PrivilegeException
     * @throws AssessmentPackageFileImportException
     * @throws QtiWorksRuntimeException
     */
    private AssessmentPackage importPackageFiles(final InputStream inputStream, final String contentType)
            throws PrivilegeException, AssessmentPackageFileImportException {
        final User owner = identityContext.getCurrentThreadEffectiveIdentity();
        final File packageSandbox = filespaceManager.createAssessmentPackageSandbox(owner);
        try {
            final AssessmentPackage assessmentPackage = assessmentPackageFileImporter.importAssessmentPackageData(packageSandbox, inputStream, contentType);
            assessmentPackage.setOwner(owner);
            return assessmentPackage;
        }
        catch (final AssessmentPackageFileImportException e) {
            filespaceManager.deleteSandbox(packageSandbox);
            throw e;
        }
    }

    /**
     * Attempts to extract the title from an {@link AssessmentItem} or {@link AssessmentTest} for
     * bootstrapping the initial state of the resulting {@link AssessmentPackage}.
     * <p>
     * This performs a low level XML parse to save time; proper read/validation using JQTI+
     * is expected to happen later on.
     *
     * @param assessmentPackage
     * @return
     */
    private String guessAssessmentTitle(final AssessmentPackage assessmentPackage) {
        final File importSandboxDirectory = new File(assessmentPackage.getSandboxPath());
        final CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        final URI assessmentSystemId = packageUriScheme.pathToUri(assessmentPackage.getAssessmentHref());
        final ResourceLocator inputResourceLocator = filespaceManager.createSandboxInputResourceLocator(importSandboxDirectory);
        XmlReadResult xmlReadResult;
        try {
            xmlReadResult = qtiXmlReader.read(assessmentSystemId, inputResourceLocator, false);
        }
        catch (final XmlResourceNotFoundException e) {
            throw new QtiWorksLogicException("Assessment resource not found, which import process should have guarded against", e);
        }
        /* Let's simply extract the title attribute from the document element, and not worry about
         * anything else at this point.
         */
        final Document document = xmlReadResult.getDocument();
        return document.getDocumentElement().getAttribute("title");
    }

    //-------------------------------------------------

    private void ensureNoDeliveries(final AssessmentPackage assessmentPackage)
            throws AssessmentPackageStateException {
        final List<AssessmentDelivery> deliveries = assessmentDeliveryDao.getForAssessmentPackage(assessmentPackage);
        if (!deliveries.isEmpty()) {
            throw new AssessmentPackageStateException(APSFailureReason.DELIVERIES_EXIST);
        }
    }

    private User ensureCallerMayChange(final AssessmentPackage assessmentPackage)
            throws PrivilegeException {
        final User caller = identityContext.getCurrentThreadEffectiveIdentity();
        if (!assessmentPackage.getOwner().equals(caller)) {
            throw new PrivilegeException(caller, Privilege.OWNER);
        }
        return caller;
    }

    /**
     * TODO: Currently we are only allowing instructors to upload packages. We may choose
     * to let anonymous users do the same in future.
     *
     * @throws PrivilegeException
     */
    private User ensureCallerCanUploadPackages() throws PrivilegeException {
        return identityContext.ensureEffectiveIdentityIsInstructor();
    }

}
