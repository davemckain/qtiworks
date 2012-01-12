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
package uk.ac.ed.ph.qtiengine.web.services;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.IOUtilities;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.utils.ImsManifestException;
import uk.ac.ed.ph.jqtiplus.utils.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.utils.QtiContentPackageSummary;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.FileSandboxResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * FIXME: Document this type
 * 
 * General idea:
 * 
 * ZIP:
 *   * Treated as CP
 *   * Must contain 1T+nI or 0T+1I (where T=test, I=item, n >=0)
 *   
 * XML:
 *   * Must be item (no point in validating tests!)
 *   
 * Things that can go wrong:
 * 
 * - Unexpected/low level Exceptions handling submitted file & areas where they're stored (internal, so log well and fail)
 * - Can't handle given file (not ZIP or XML)
 * ZIP:
 * - Not a ZIP file despite content type
 * - Can't find manifest in content package
 * - ImsManifestException when parsing CP
 * - Can't handle the combination of items/tests sent.
 * XML:
 * - Not an assessmentItem
 * 
 * Then we've got the validation result to account for.
 * 
 * For ZIPs, we'll need to unpack into a temporary directory and ensure it's deleted afterwards.
 *
 * @author David McKain
 */
@Service
public class ValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);
    
    public String validate(String contentType, File submittedData) throws XmlResourceNotFoundException, ImsManifestException, IOException {
        if (contentType.equals("application/zip")) {
            logger.info("It's a ZIP");
            return validateContentPackage(submittedData);
        }
        else {
            logger.info("Something else");
            return null;
        }
    }
    
    private String validateContentPackage(File zipFile) throws IOException, XmlResourceNotFoundException, ImsManifestException {
        /* 1. Expand ZIP file
         * 2. Parse CP contents
         * 3. See what results we get.
         * 4. Check for sensible
         */
        /* Expand ZIP file */
        /* FIXME: This is a crap way of creating directories! */
        File expandedZipDir = File.createTempFile("qtiengine", "dir");
        expandedZipDir.delete();
        expandedZipDir.mkdir();
        extractZipFile(zipFile, expandedZipDir);
        
        /* Parse Content Package content */
        QtiContentPackageExtractor contentPackageExtractor = new QtiContentPackageExtractor(expandedZipDir);
        QtiContentPackageSummary contentPackageSummary = contentPackageExtractor.parse();
        
        int testCount = contentPackageSummary.getTestResourceUris().size();
        int itemCount = contentPackageSummary.getItemResourceUris().size();

        String result = null;
        if (testCount==1) {
            /* Treat as a test */
            logger.info("It's a test!");
        }
        else if (itemCount==1) {
            /* Treat as an item */
            logger.info("It's an item!");
            result = validateItem(expandedZipDir, contentPackageSummary.getItemResourceUris().get(0));
        }
        else {
            /* Barf */
            logger.info("Barf... TODO!");
        }
        return result;
    }
    
    private String validateItem(File sandboxBaseDirectory, URI itemSystemId) {
        JqtiExtensionManager extensionManager = new JqtiExtensionManager(); /* TODO: Add registered extensions... this should become a bean! */
        FileSandboxResourceLocator inputResourceLocator = new FileSandboxResourceLocator(QtiContentPackageExtractor.PACKAGE_URI_SCHEME, sandboxBaseDirectory);
        QtiXmlObjectReader objectReader = new QtiXmlObjectReader(extensionManager, inputResourceLocator);
        AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
        ItemValidationResult itemResult = objectManager.validateItem(itemSystemId);
        
        /* TEMP! */
        return ObjectDumper.dumpObject(itemResult, DumpMode.DEEP);

    }
    
    private void extractZipFile(File zipFile, File destinationDirectory) throws IOException {
        /* Extract ZIP contents */
        ZipEntry zipEntry;
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            File destFile = new File(destinationDirectory, zipEntry.getName());
            if (!zipEntry.isDirectory()) {
                IOUtilities.ensureFileCreated(destFile);
                IOUtilities.transfer(zipInputStream, new FileOutputStream(destFile), false, true);
                zipInputStream.closeEntry();
            }
        }
        zipInputStream.close();
    }
}
