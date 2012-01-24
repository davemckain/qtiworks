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
package uk.ac.ed.ph.qtiengine.services;

import uk.ac.ed.ph.jqtiplus.internal.util.IOUtilities;
import uk.ac.ed.ph.jqtiplus.node.AssessmentObject;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.utils.ImsManifestException;
import uk.ac.ed.ph.jqtiplus.utils.QtiContentPackageExtractor;
import uk.ac.ed.ph.jqtiplus.utils.QtiContentPackageSummary;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;
import uk.ac.ed.ph.jqtiplus.xmlutils.FileSandboxResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;

import uk.ac.ed.ph.qtiengine.EngineException;
import uk.ac.ed.ph.qtiengine.UploadException;
import uk.ac.ed.ph.qtiengine.UploadException.UploadFailureReason;
import uk.ac.ed.ph.qtiengine.web.domain.AssessmentPackage;
import uk.ac.ed.ph.qtiengine.web.domain.AssessmentPackage.AssessmentType;
import uk.ac.ed.ph.qtiengine.web.domain.AssessmentPackage.Packaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

/**
 * This trivial service uploads {@link AssessmentObject}s into the system
 * that have been sent either as raw XML or bundled within a Content Package.
 * 
 * ZIP:
 *   * Treated as CP
 *   * Must contain 1T+nI or 0T+1I (where T=test, I=item, n >=0)
 *   
 * Anything else:
 *   * Assumed to be XML:
 *   * Must be item (no point in accepting standalone tests!)
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
 * @author David McKain
 */
@Service
public class UploadService {
    
    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);
    
    public static final String SINGLE_FILE_NAME = "qti.xml";
    
    private File sandboxRootDirectory;
    
    @PostConstruct
    public void init() {
        sandboxRootDirectory = Files.createTempDir();
        logger.info("Created sandbox root directory at {}", sandboxRootDirectory);
    }
    
    public AssessmentPackage importData(InputStream inputStream, String contentType) throws UploadException {
        File sandboxDirectory = createRequestSandbox();
        AssessmentPackage result;
        if ("application/zip".equals(contentType)) {
            logger.info("Attempting to unpack ZIP to {}", sandboxDirectory);
            result = extractZipFile(inputStream, sandboxDirectory);
        }
        else {
            logger.info("Treating upload as a single file");
            result = trySaveSingleFile(inputStream, sandboxDirectory);
        }
        return result;
    }
    
    public void deletePackage(AssessmentPackage assessmentPackage) {
        logger.info("Deleting sandbox for package {}", assessmentPackage);
        try {
            IOUtilities.recursivelyDelete(new File(assessmentPackage.getSandboxPath()));
        }
        catch (IOException e) {
            logger.error("Could not delete package at {}", assessmentPackage.getSandboxPath());
        }
    }
    
    private AssessmentPackage trySaveSingleFile(InputStream inputStream, File sandboxDirectory) throws UploadException {
        /* (We'll call the resulting file XML, even though it might not be */
        CustomUriScheme packageUriScheme = QtiContentPackageExtractor.PACKAGE_URI_SCHEME;
        File resultFile = new File(sandboxDirectory, SINGLE_FILE_NAME);
        try {
            IOUtilities.transfer(inputStream, new FileOutputStream(resultFile));
        }
        catch (IOException e) {
            throw EngineException.unexpectedException(e);
        }
        
        /* Let's make sure it's really XML by parsing it (and throwing away the result) */
        FileSandboxResourceLocator inputResourceLocator = new FileSandboxResourceLocator(packageUriScheme, sandboxDirectory);
        QtiXmlReader xmlReader = new QtiXmlReader();
        try {
            XmlReadResult xmlReadResult = xmlReader.read(packageUriScheme.pathToUri(SINGLE_FILE_NAME), inputResourceLocator, false);
            if (!xmlReadResult.getXmlParseResult().isParsed()) {
                throw new UploadException(UploadFailureReason.NOT_XML_OR_ZIP);
            }
        }
        catch (XmlResourceNotFoundException e) {
            throw EngineException.unexpectedException(e);
        }
        
        AssessmentPackage result = new AssessmentPackage();
        result.setPackaging(Packaging.STANDALONE);
        result.setAssessmentType(AssessmentType.ITEM);
        result.setAssessmentObjectHref(SINGLE_FILE_NAME);
        result.setSandboxPath(sandboxDirectory.getAbsolutePath());
        return result;
    }
    
    private AssessmentPackage extractZipFile(InputStream inputStream, File sandboxDirectory)
            throws UploadException {
        /* Extract ZIP contents */
        logger.info("Expanding ZIP file from stream {} to sandbox {}", inputStream, sandboxDirectory);
        ZipEntry zipEntry;
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        try {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File destFile = new File(sandboxDirectory, zipEntry.getName());
                if (!zipEntry.isDirectory()) {
                    IOUtilities.ensureFileCreated(destFile);
                    IOUtilities.transfer(zipInputStream, new FileOutputStream(destFile), false, true);
                    zipInputStream.closeEntry();
                }
            }
            zipInputStream.close();
        }
        catch (ZipException e) {
            throw new UploadException(UploadFailureReason.BAD_ZIP, e);
            
        }
        catch (IOException e) {
            throw EngineException.unexpectedException(e);
        }
        
        /* Expand content package */
        QtiContentPackageExtractor contentPackageExtractor = new QtiContentPackageExtractor(sandboxDirectory);
        QtiContentPackageSummary contentPackageSummary;
        try {
            contentPackageSummary = contentPackageExtractor.parse();
        }
        catch (XmlResourceNotFoundException e) {
            throw new UploadException(UploadFailureReason.NOT_CONTENT_PACKAGE, e);
        }
        catch (ImsManifestException e) {
            throw new UploadException(UploadFailureReason.BAD_IMS_MANIFEST, e);
        }
        int testCount = contentPackageSummary.getTestResourceHrefs().size();
        int itemCount = contentPackageSummary.getItemResourceHrefs().size();
        
        AssessmentPackage result = new AssessmentPackage();
        result.setSandboxPath(sandboxDirectory.getAbsolutePath());
        result.setPackaging(Packaging.CONTENT_PACKAGING);
        if (testCount==1) {
            /* Treat as a test */
            logger.info("Package contains 1 test resource, so treating this as an AssessmentTest");
            result.setAssessmentType(AssessmentType.TEST);
            result.setAssessmentObjectHref(contentPackageSummary.getTestResourceHrefs().iterator().next());
            result.setFileHrefs(contentPackageSummary.getFileHrefs());
        }
        else if (testCount==0 && itemCount==1) {
            /* Treat as an item */
            logger.info("Package contains 1 item resource and no test resources, so treating this as an AssessmentItem");
            result.setAssessmentType(AssessmentType.ITEM);
            result.setAssessmentObjectHref(contentPackageSummary.getItemResourceHrefs().iterator().next());
            result.setFileHrefs(contentPackageSummary.getFileHrefs());
        }
        else {
            /* Barf */
            logger.warn("Package contains {} items and {} tests. Don't know how to deal with this", itemCount, testCount);
            throw new UploadException(UploadFailureReason.UNSUPPORTED_PACKAGE_CONTENTS);
        }
        return result;
    }
    
    private File createRequestSandbox() {
        String sandboxName = Thread.currentThread().getName() + "-" + System.currentTimeMillis();
        File sandboxDirectory = new File(sandboxRootDirectory, sandboxName);
        if (!sandboxDirectory.mkdir()) {
            throw new EngineException("Could not create sandbox directory " + sandboxDirectory);
        }
        return sandboxDirectory;
    }



}
