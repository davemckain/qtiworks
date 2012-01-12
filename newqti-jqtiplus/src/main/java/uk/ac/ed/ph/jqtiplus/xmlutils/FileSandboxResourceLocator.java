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
package uk.ac.ed.ph.jqtiplus.xmlutils;

import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience {@link ResourceLocator} that maps a custom pseudo-URI scheme to a {@link File} under
 * a given base directory, which is useful for locating resources within (expanded) content
 * packages.
 * <p>
 * By default, the URI scheme will be called 'package', but this can be changed as required.
 * 
 * Example:
 * 
 * <code>package:/a/b/c.xml</code> -> <code>File base/a/b/c.xml</code>
 * 
 * A check is included to ensure that the resulting path does not end up outside the
 * required base directory.
 *
 * @author David McKain
 */
public class FileSandboxResourceLocator implements ResourceLocator {
    
    private static final Logger logger = LoggerFactory.getLogger(FileSandboxResourceLocator.class);
    
    private final String uriScheme;
    private final File sandboxBaseDirectory;
    
    public FileSandboxResourceLocator(String uriScheme, File sandboxBaseDirectory) {
        ConstraintUtilities.ensureNotNull(uriScheme, "uriScheme");
        ConstraintUtilities.ensureNotNull(sandboxBaseDirectory, "sandboxBaseDirectory");
        this.uriScheme = uriScheme;
        this.sandboxBaseDirectory = sandboxBaseDirectory;
    }
    
    @Override
    public InputStream findResource(URI systemIdUri) {
        if (uriScheme.equals(systemIdUri.getScheme())) {
            final String normalizedPath = systemIdUri.normalize().getSchemeSpecificPart();
            if (normalizedPath.startsWith("/..")) {
                /* This is trying to go outside the package, so we'll return null here */
                logger.warn("URI {} normalized to path {} which is 'outside' the package so returning null for safety", systemIdUri, normalizedPath);
                return null;
            }
            final String relativePath = normalizedPath.substring(1);
            final File resultingFile = new File(sandboxBaseDirectory.toURI().resolve(relativePath));
            FileInputStream result = null;
            try {
                result = new FileInputStream(resultingFile);
                logger.info("URI {} successfully mapped to file {}", systemIdUri, resultingFile);
            }
            catch (FileNotFoundException e) {
                logger.warn("URI {} successfully mapped to non-existent file {}", systemIdUri, resultingFile);
            }
            return result;
        }
        return null;
    }
    
    public String getUriScheme() {
        return uriScheme;
    }
    
    public File getExpandedBaseDirectory() {
        return sandboxBaseDirectory;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(uriScheme=" + uriScheme
                + ",sandboxBaseDirectory=" + sandboxBaseDirectory
                + ")";
    }



}
