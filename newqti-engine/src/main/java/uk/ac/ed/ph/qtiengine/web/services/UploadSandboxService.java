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

import uk.ac.ed.ph.jqtiplus.internal.util.IOUtilities;

import uk.ac.ed.ph.qtiengine.EngineException;
import uk.ac.ed.ph.qtiengine.UnsupportedUploadException;
import uk.ac.ed.ph.qtiengine.UnsupportedUploadException.UploadFailureReason;

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
 * This trivial service imports incoming data into a temp sandbox area, expanding ZIP files
 *
 * @author David McKain
 */
@Service
public class UploadSandboxService {
    
    private static final Logger logger = LoggerFactory.getLogger(UploadSandboxService.class);
    
    public static final String SINGLE_FILE_NAME = "qti.xml";
    
    private File sandboxRootDirectory;
    
    @PostConstruct
    public void init() {
        sandboxRootDirectory = Files.createTempDir();
        logger.info("Created sandbox root directory at {}", sandboxRootDirectory);
    }
    
    public File importData(InputStream inputStream, String contentType) throws UnsupportedUploadException {
        File sandboxDirectory = createRequestSandbox();
        if ("application/zip".equals(contentType)) {
            logger.info("Attempting to unpack ZIP to {}", sandboxDirectory);
            extractZipFile(inputStream, sandboxDirectory);
        }
        else {
            /* (We'll call the resulting file XML, even though it might not be */
            File resultFile = new File(sandboxDirectory, SINGLE_FILE_NAME);
            try {
                IOUtilities.transfer(inputStream, new FileOutputStream(resultFile));
            }
            catch (IOException e) {
                throw EngineException.unexpectedException(e);
            }
        }
        return sandboxDirectory;
    }
    
    private File createRequestSandbox() {
        String sandboxName = Thread.currentThread().getName() + "-" + System.currentTimeMillis();
        File sandboxDirectory = new File(sandboxRootDirectory, sandboxName);
        if (!sandboxDirectory.mkdir()) {
            throw new EngineException("Could not create sandbox directory " + sandboxDirectory);
        }
        return sandboxDirectory;
    }
    
    private void extractZipFile(InputStream inputStream, File sandboxDirectory)
            throws UnsupportedUploadException {
        /* Extract ZIP contents */
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
            throw new UnsupportedUploadException(UploadFailureReason.BAD_ZIP);
            
        }
        catch (IOException e) {
            throw EngineException.unexpectedException(e);
        }
    }

}
