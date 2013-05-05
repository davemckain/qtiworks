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
package uk.ac.ed.ph.jqtiplus.xmlutils.locators;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.xmlutils.CustomUriScheme;

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
 * By name of the URI scheme is defined by the {@link CustomUriScheme} used to create this
 * locator.
 *
 * Example with a {@link CustomUriScheme} using the 'package' scheme:
 *
 * <code>package:/a/b/c.xml</code> -> <code>File base/a/b/c.xml</code>
 *
 * A check is included to ensure that the resulting path does not escape the "sandbox" of the
 * required base directory.
 *
 * @author David McKain
 */
public class FileSandboxResourceLocator implements ResourceLocator {

    private static final Logger logger = LoggerFactory.getLogger(FileSandboxResourceLocator.class);

    private final CustomUriScheme uriScheme;
    private final File sandboxBaseDirectory;

    public FileSandboxResourceLocator(final CustomUriScheme uriScheme, final File sandboxBaseDirectory) {
        Assert.notNull(uriScheme, "uriScheme");
        Assert.notNull(sandboxBaseDirectory, "sandboxBaseDirectory");
        this.uriScheme = uriScheme;
        this.sandboxBaseDirectory = sandboxBaseDirectory;
    }

    @Override
    public InputStream findResource(final URI systemIdUri) {
        final File sandboxFile = findSandboxFile(systemIdUri);
        if (sandboxFile!=null) {
            try {
                return new FileInputStream(sandboxFile);
            }
            catch (final FileNotFoundException e) {
                logger.trace("URI {} successfully mapped to non-existent file {}", systemIdUri, sandboxFile);
                return null;
            }
        }
        return null;
    }

    public File findSandboxFile(final URI systemIdUri) {
        final URI normalizedUri = systemIdUri.normalize();
        if (uriScheme.isInScheme(normalizedUri)) {
            final String normalizedRawPath = uriScheme.uriToRawPath(normalizedUri);
            if (normalizedRawPath!=null) {
                if (normalizedRawPath.startsWith("..")) {
                    /* This is trying to go outside the package, so we'll return null here */
                    logger.trace("URI {} normalized to path {} which is 'outside' the package so returning null for safety", systemIdUri, normalizedRawPath);
                    return null;
                }
                final File resultingFile = new File(sandboxBaseDirectory.toURI().resolve(normalizedRawPath));
                if (!resultingFile.exists()) {
                    logger.trace("URI {} successfully mapped to non-existent file {}", systemIdUri, resultingFile);
                    return null;
                }
                else if (!resultingFile.isFile()) {
                    logger.trace("URI {} successfully mapped to non-file file {}", systemIdUri, resultingFile);
                    return null;
                }
                logger.trace("URI {} successfully mapped to good file {}", systemIdUri, resultingFile);
                return resultingFile;
            }
        }
        return null;
    }

    public CustomUriScheme getUriScheme() {
        return uriScheme;
    }

    public File getExpandedBaseDirectory() {
        return sandboxBaseDirectory;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(uriScheme=" + uriScheme
                + ",sandboxBaseDirectory=" + sandboxBaseDirectory
                + ")";
    }



}
