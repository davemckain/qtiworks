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

import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackageImportType;
import uk.ac.ed.ph.qtiworks.services.base.ServiceUtilities;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException;
import uk.ac.ed.ph.qtiworks.services.domain.AssessmentPackageFileImportException.APFIFailureReason;
import uk.ac.ed.ph.qtiworks.testutils.ClassPathMultipartFile;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

/**
 * Tests the {@link AssessmentPackageImporter} helper/service
 *
 * @author David McKain
 */
public class AssessmentPackageImporterTest {

    private AssessmentPackageImporter importer;
    private File importSandboxDirectory;

    @Before
    public void setup() {
        importer = new AssessmentPackageImporter();
        importSandboxDirectory = Files.createTempDir();
    }

    @After
    public void tearDown() {
        if (importSandboxDirectory!=null) {
            ServiceUtilities.recursivelyDelete(importSandboxDirectory);
        }
    }

    //----------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void nullDirectory() throws Exception {
        importer.importAssessmentPackageData(null, getThisUnitTestMultipartFile("uk/ac/ed/ph/qtiworks/samples/ims/choice.xml", "text/xml"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullResource() throws Exception {
        importer.importAssessmentPackageData(importSandboxDirectory, null);
    }

    //----------------------------------------------------------

    @Test
    public void importStandaloneItem() throws Exception {
        final MultipartFile multipartFile = getThisUnitTestMultipartFile("uk/ac/ed/ph/qtiworks/samples/ims/choice.xml", "text/xml");

        final AssessmentPackage result = importer.importAssessmentPackageData(importSandboxDirectory, multipartFile);
        Assert.assertEquals(importSandboxDirectory.getPath(), result.getSandboxPath());
        Assert.assertEquals(AssessmentObjectType.ASSESSMENT_ITEM, result.getAssessmentType());
        Assert.assertEquals(AssessmentPackageImportType.STANDALONE_ITEM_XML, result.getImportType());
    }

    @Test
    public void importPackagedItem() throws Exception {
        final MultipartFile multipartFile = getThisUnitTestMultipartFile("uk/ac/ed/ph/qtiworks/services/Aardvark-cannon.zip", "application/zip");

        final AssessmentPackage result = importer.importAssessmentPackageData(importSandboxDirectory, multipartFile);
        Assert.assertEquals(importSandboxDirectory.getPath(), result.getSandboxPath());
        Assert.assertEquals(AssessmentObjectType.ASSESSMENT_ITEM, result.getAssessmentType());
        Assert.assertEquals(AssessmentPackageImportType.CONTENT_PACKAGE, result.getImportType());
    }

    @Test
    public void importPackagedTest() throws Exception {
        final MultipartFile multipartFile = getThisUnitTestMultipartFile("uk/ac/ed/ph/qtiworks/services/WebDeveloperTest1.zip", "application/zip");

        final AssessmentPackage result = importer.importAssessmentPackageData(importSandboxDirectory, multipartFile);
        Assert.assertEquals(importSandboxDirectory.getPath(), result.getSandboxPath());
        Assert.assertEquals(AssessmentObjectType.ASSESSMENT_TEST, result.getAssessmentType());
        Assert.assertEquals(AssessmentPackageImportType.CONTENT_PACKAGE, result.getImportType());
    }

    //----------------------------------------------------------

    @Test
    public void notXmlOrZip() throws Exception {
        final MultipartFile multipartFile = getThisUnitTestMultipartFile("uk/ac/ed/ph/qtiworks/QtiWorksRuntimeException.class", "application/x-java-class");
        try {
            importer.importAssessmentPackageData(importSandboxDirectory, multipartFile);
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageFileImportException e) {
            Assert.assertEquals(APFIFailureReason.NOT_XML_OR_ZIP, e.getFailure().getReason());
        }
    }

    @Test
    public void badZipIncomplete() throws Exception {
        final MultipartFile multipartFile = getThisUnitTestMultipartFile("uk/ac/ed/ph/qtiworks/services/WebDeveloperTest1-incomplete.zip", "application/zip");
        try {
            importer.importAssessmentPackageData(importSandboxDirectory, multipartFile);
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageFileImportException e) {
            Assert.assertEquals(APFIFailureReason.NOT_XML_OR_ZIP, e.getFailure().getReason());
        }
    }

    @Test
    public void notContentPackage() throws Exception {
        final MultipartFile multipartFile = getThisUnitTestMultipartFile("uk/ac/ed/ph/qtiworks/services/NotContentPackage.zip", "application/zip");
        try {
            importer.importAssessmentPackageData(importSandboxDirectory, multipartFile);
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageFileImportException e) {
            Assert.assertEquals(APFIFailureReason.NOT_CONTENT_PACKAGE, e.getFailure().getReason());
        }
    }

    @Test
    public void badManifest() throws Exception {
        final MultipartFile multipartFile = getThisUnitTestMultipartFile("uk/ac/ed/ph/qtiworks/services/BadManifest.zip", "application/zip");
        try {
            importer.importAssessmentPackageData(importSandboxDirectory, multipartFile);
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageFileImportException e) {
            Assert.assertEquals(APFIFailureReason.BAD_IMS_MANIFEST, e.getFailure().getReason());
        }
    }

    @Test
    public void badMix() throws Exception {
        final MultipartFile multipartFile = getThisUnitTestMultipartFile("uk/ac/ed/ph/qtiworks/services/BadMix.zip", "application/zip");
        try {
            importer.importAssessmentPackageData(importSandboxDirectory, multipartFile);
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageFileImportException e) {
            Assert.assertEquals(APFIFailureReason.UNSUPPORTED_PACKAGE_CONTENTS, e.getFailure().getReason());
            final List<?> failureArguments = e.getFailure().getArguments();
            Assert.assertEquals(2, failureArguments.size());
            Assert.assertEquals(Integer.valueOf(1), failureArguments.get(0)); /* 1 item */
            Assert.assertEquals(Integer.valueOf(2), failureArguments.get(1)); /* 2 tests */
        }
    }

    @Test
    public void externalHref() throws Exception {
        final MultipartFile multipartFile = getThisUnitTestMultipartFile("uk/ac/ed/ph/qtiworks/services/ExternalHref.zip", "application/zip");
        try {
            importer.importAssessmentPackageData(importSandboxDirectory, multipartFile);
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageFileImportException e) {
            Assert.assertEquals(APFIFailureReason.HREF_OUTSIDE_PACKAGE, e.getFailure().getReason());
            final List<?> failureArguments = e.getFailure().getArguments();
            Assert.assertEquals(1, failureArguments.size());
            Assert.assertEquals("../outside.xml", failureArguments.get(0));
        }
    }

    @Test
    public void missingFile() throws Exception {
        final MultipartFile multipartFile = getThisUnitTestMultipartFile("uk/ac/ed/ph/qtiworks/services/MissingFile.zip", "application/zip");
        try {
            importer.importAssessmentPackageData(importSandboxDirectory, multipartFile);
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageFileImportException e) {
            Assert.assertEquals(APFIFailureReason.FILE_MISSING, e.getFailure().getReason());
            final List<?> failureArguments = e.getFailure().getArguments();
            Assert.assertEquals(1, failureArguments.size());
            Assert.assertEquals("missing.xml", failureArguments.get(0));
        }
    }

    //----------------------------------------------------------

    private MultipartFile getThisUnitTestMultipartFile(final String path, final String contentType) {
        return new ClassPathMultipartFile(path, contentType);
    }
}
