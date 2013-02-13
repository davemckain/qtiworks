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
package uk.ac.ed.ph.jqtiplus.xmlutils;

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Trivial helper to deal with our custom hierarchic URI schemes of the form
 *
 * <code>scheme:/path/to/something.xml</code>
 *
 * which represents a resource at location
 *
 * <code>path/to/something.xml</code>
 *
 * in a filesystem subtree, sandbox or something similar.
 *
 * Note the difference between <code>decoded</code> and <code>raw</code> paths in the methods
 * below. They differentiate between ones that have had their special characters decoded and
 * those that haven't. See {@link URI#getPath()} versus {@link URI#getRawPath()} for details.
 *
 * @author David McKain
 */
public final class CustomUriScheme {

    private final String schemeName;

    public CustomUriScheme(final String schemeName) {
        Assert.notNull(schemeName);
        this.schemeName = schemeName;
    }

    /**
     * Creates a {@link URI} in this scheme from the given decoded path.
     *
     * @param decodedPath
     */
    public URI decodedPathToUri(final String decodedPath) {
        Assert.notNull(decodedPath);
        try {
            return new URI(schemeName, null, "/" + decodedPath, null);
        }
        catch (final URISyntaxException e) {
            throw new IllegalArgumentException("Could not create URI from scheme " + schemeName + " and decoded path " + decodedPath);
        }
    }

    public URI rawPathToUri(final String rawPath) {
        Assert.notNull(rawPath);
        return URI.create(schemeName + ":/" + rawPath);
    }

    public boolean isInScheme(final URI uri) {
        Assert.notNull(uri);
        final String path = uri.getPath();
        return schemeName.equals(uri.getScheme())
                && uri.getAuthority()==null
                && path!=null
                && path.length()>0
                && path.charAt(0)=='/';
    }

    public String uriToDecodedPath(final String uriString) {
        Assert.notNull(uriString);
        try {
            return uriToDecodedPath(new URI(uriString));
        }
        catch (final URISyntaxException e) {
            /* Bad URI, so leave as-is */
            return uriString;
        }
    }

    public String uriToDecodedPath(final URI uri) {
        Assert.notNull(uri);
        if (isInScheme(uri)) {
            return uri.getPath().substring(1);
        }
        return uri.toString();
    }

    public String uriToRawPath(final String uriString) {
        Assert.notNull(uriString);
        try {
            return uriToRawPath(new URI(uriString));
        }
        catch (final URISyntaxException e) {
            /* Bad URI, so leave as-is */
            return uriString;
        }
    }

    public String uriToRawPath(final URI uri) {
        Assert.notNull(uri);
        if (isInScheme(uri)) {
            return uri.getRawPath().substring(1);
        }
        return uri.toString();
    }

    public String getSchemeName() {
        return schemeName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(schemeName=" + schemeName
                + ")";
    }
}
