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

import uk.ac.ed.ph.qtiworks.domain.IdentityContext;
import uk.ac.ed.ph.qtiworks.domain.PrivilegeException;
import uk.ac.ed.ph.qtiworks.domain.dao.AssessmentPackageDao;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.User;

import java.io.File;
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * Services for managing {@link AssessmentPackage}s
 *
 * @author David McKain
 */
@Service
public class AssessmentPackageServices {

    @Resource
    IdentityContext identityContext;

    @Resource
    FilespaceManager filespaceManager;

    @Resource
    AssessmentPackageFileImporter assessmentPackageFileImporter;

    @Resource
    AssessmentPackageDao assessmentPackageDao;

    public void createAssessmentPackage(@Nonnull final InputStream inputStream, @Nonnull final String contentType) {
        /* Do something! Throw something at us! */
    }

    /**
     * NOTE: Not allowed to go item->test or test->item.
     *
     * @param inputStream
     * @param contentType
     */
    public void replaceAssessmentPackage(@Nonnull final Long aid, @Nonnull final InputStream inputStream, @Nonnull final String contentType) {
        /* Do something */
    }

    private AssessmentPackage importPackageData(final InputStream inputStream, final String contentType)
            throws PrivilegeException, AssessmentPackageFileImportException {
        final User owner = ensureCallerCanUploadPackages();
        final File packageSandbox = filespaceManager.createAssessmentPackageSandbox(owner);
        try {
            return assessmentPackageFileImporter.importAssessmentPackageData(packageSandbox, inputStream, contentType);
        }
        catch (final AssessmentPackageFileImportException e) {
            filespaceManager.deleteSandbox(packageSandbox);
            throw e;
        }
    }

    /**
     * FIXME: Currently we are only allowing instructors to upload packages
     *
     * @throws PrivilegeException
     */
    private User ensureCallerCanUploadPackages() throws PrivilegeException {
        return identityContext.ensureEffectiveIdentityIsInstructor();
    }

}