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

import java.io.InputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ResourceLocator} that looks for resources within
 * a specified base directory in the ClassPath, using a pseudo-URL scheme <tt>classpath:/path/to/resource.txt</tt>
 * <p>
 * For example, <tt>classpath:/hello.txt</tt> looks in the ClassPath for <tt>[basePath]/hello.txt</tt>
 * <p>
 * Note that the '/' after the ':' is important here. I have enforced this to make sure the URI resolution works nicely.
 * 
 * @author David McKain
 */
public final class ClassPathResourceLocator implements ResourceLocator {

    private static final Logger logger = LoggerFactory.getLogger(ClassPathResourceLocator.class);

    public static final String CLASSPATH_SCHEME_NAME = "classpath";

    /** basePath to search in. null is treated as blank */
    private String basePath;

    // -------------------------------------------

    public ClassPathResourceLocator() {
        this(null);
    }

    public ClassPathResourceLocator(String basePath) {
        this.basePath = basePath;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    // -------------------------------------------

    @Override
    public InputStream findResource(final URI systemId) {
        final String scheme = systemId.getScheme();
        InputStream result = null;
        if (CLASSPATH_SCHEME_NAME.equals(scheme)) {
            final String systemIdAfterScheme = systemId.toString().substring(CLASSPATH_SCHEME_NAME.length());
            if (systemIdAfterScheme.startsWith(":/")) {
                final String resultingPath = basePath != null ? basePath + systemIdAfterScheme.substring(1) : systemIdAfterScheme.substring(2);
                result = loadResource(systemId, resultingPath);
            }
            else {
                logger.trace("ClassPath URI must be of the form {}:/path", CLASSPATH_SCHEME_NAME);
            }
        }
        return result;
    }

    private InputStream loadResource(final URI systemIdUri, final String resourcePath) {
        final InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (resourceStream != null) {
            logger.trace("Successful locate of ClassPath resource with URI {}  in ClassPath at {}", systemIdUri, resourcePath);
        }
        else {
            logger.trace("Failed to locate ClassPath resource with URI {} in ClassPath at {}", systemIdUri, resourcePath);
        }
        return resourceStream;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(basePath=" + basePath
                + ")";
    }
}
