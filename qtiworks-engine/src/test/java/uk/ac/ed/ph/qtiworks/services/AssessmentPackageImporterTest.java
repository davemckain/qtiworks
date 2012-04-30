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

import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackage;
import uk.ac.ed.ph.qtiworks.domain.entities.AssessmentPackageImportType;
import uk.ac.ed.ph.qtiworks.services.AssessmentPackageImportException.FailureReason;
import uk.ac.ed.ph.qtiworks.utils.IoUtilities;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Closeables;
import com.google.common.io.Files;

/**
 * Tests the {@link AssessmentPackageImporter} helper/service
 *
 * @author David McKain
 */
public class AssessmentPackageImporterTest {

    private AssessmentPackageImporter importer;
    private File importSandboxDirectory;
    private InputStream importStream;

    @Before
    public void setup() {
        importer = new AssessmentPackageImporter();
        importSandboxDirectory = Files.createTempDir();
    }

    @After
    public void tearDown() throws IOException {
        if (importSandboxDirectory!=null) {
            IoUtilities.recursivelyDelete(importSandboxDirectory);
        }
        if (importStream!=null) {
            Closeables.closeQuietly(importStream);
        }
    }

    //----------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void nullDirectory() throws Exception {
        importer.importData(null, getThisUnitTestResource("uk/ac/ed/ph/qtiworks/samples/ims/choice.xml"), "text/xml");
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullResource() throws Exception {
        importer.importData(importSandboxDirectory, null, "text/xml");
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullType() throws Exception {
        importer.importData(importSandboxDirectory, getThisUnitTestResource("uk/ac/ed/ph/qtiworks/samples/ims/choice.xml"), null);
    }

    //----------------------------------------------------------

    @Test
    public void importStandaloneItem() throws Exception {
        final InputStream importResource = getThisUnitTestResource("uk/ac/ed/ph/qtiworks/samples/ims/choice.xml");

        final AssessmentPackage result = importer.importData(importSandboxDirectory, importResource, "text/xml");
        Assert.assertEquals(importSandboxDirectory.getPath(), result.getBasePath());
        Assert.assertEquals(AssessmentObjectType.ASSESSMENT_ITEM, result.getAssessmentType());
        Assert.assertEquals(AssessmentPackageImportType.STANDALONE_ITEM_XML, result.getImportType());
    }

    @Test
    public void importPackagedItem() throws Exception {
        final InputStream importResource = getThisUnitTestResource("uk/ac/ed/ph/qtiworks/services/Aardvark-cannon.zip");

        final AssessmentPackage result = importer.importData(importSandboxDirectory, importResource, "application/zip");
        Assert.assertEquals(importSandboxDirectory.getPath(), result.getBasePath());
        Assert.assertEquals(AssessmentObjectType.ASSESSMENT_ITEM, result.getAssessmentType());
        Assert.assertEquals(AssessmentPackageImportType.CONTENT_PACKAGE, result.getImportType());
    }

    @Test
    public void importPackagedTest() throws Exception {
        final InputStream importResource = getThisUnitTestResource("uk/ac/ed/ph/qtiworks/services/WebDeveloperTest1.zip");

        final AssessmentPackage result = importer.importData(importSandboxDirectory, importResource, "application/zip");
        Assert.assertEquals(importSandboxDirectory.getPath(), result.getBasePath());
        Assert.assertEquals(AssessmentObjectType.ASSESSMENT_TEST, result.getAssessmentType());
        Assert.assertEquals(AssessmentPackageImportType.CONTENT_PACKAGE, result.getImportType());
    }

    //----------------------------------------------------------

    @Test
    public void notXmlOrZip() throws Exception {
        final InputStream importResource = getThisUnitTestResource("uk/ac/ed/ph/qtiworks/QtiWorksRuntimeException.class");
        try {
            importer.importData(importSandboxDirectory, importResource, "application/x-java-class");
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageImportException e) {
            Assert.assertEquals(FailureReason.NOT_XML_OR_ZIP, e.getFailure().getReason());
        }
    }

    @Test
    public void badZipIncomplete() throws Exception {
        final InputStream importResource = getThisUnitTestResource("uk/ac/ed/ph/qtiworks/services/WebDeveloperTest1-incomplete.zip");
        try {
            importer.importData(importSandboxDirectory, importResource, "application/zip");
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageImportException e) {
            Assert.assertEquals(FailureReason.BAD_ZIP, e.getFailure().getReason());
        }
    }

    @Test
    public void notContentPackage() throws Exception {
        final InputStream importResource = getThisUnitTestResource("uk/ac/ed/ph/qtiworks/services/NotContentPackage.zip");
        try {
            importer.importData(importSandboxDirectory, importResource, "application/zip");
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageImportException e) {
            Assert.assertEquals(FailureReason.NOT_CONTENT_PACKAGE, e.getFailure().getReason());
        }
    }

    @Test
    public void badManifest() throws Exception {
        final InputStream importResource = getThisUnitTestResource("uk/ac/ed/ph/qtiworks/services/BadManifest.zip");
        try {
            importer.importData(importSandboxDirectory, importResource, "application/zip");
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageImportException e) {
            Assert.assertEquals(FailureReason.BAD_IMS_MANIFEST, e.getFailure().getReason());
        }
    }

    @Test
    public void badMix() throws Exception {
        final InputStream importResource = getThisUnitTestResource("uk/ac/ed/ph/qtiworks/services/BadMix.zip");
        try {
            importer.importData(importSandboxDirectory, importResource, "application/zip");
            Assert.fail("Should have failed");
        }
        catch (final AssessmentPackageImportException e) {
            Assert.assertEquals(FailureReason.UNSUPPORTED_PACKAGE_CONTENTS, e.getFailure().getReason());
            final List<?> failureArguments = e.getFailure().getArguments();
            Assert.assertEquals(Integer.valueOf(1), failureArguments.get(0)); /* 1 item */
            Assert.assertEquals(Integer.valueOf(2), failureArguments.get(1)); /* 2 tests */
        }
    }

    //----------------------------------------------------------

    private InputStream getThisUnitTestResource(final String path) {
        importStream = getClass().getClassLoader().getResourceAsStream(path);
        Assert.assertNotNull(importStream);
        return importStream;
    }

}
