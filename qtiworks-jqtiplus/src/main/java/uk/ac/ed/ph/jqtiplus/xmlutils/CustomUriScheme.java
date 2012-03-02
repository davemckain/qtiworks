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

import java.net.URI;

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
 * @author David McKain
 */
public final class CustomUriScheme {
    
    private final String schemeName;
    
    public CustomUriScheme(String schemeName) {
        this.schemeName = schemeName;
    }
    
    public URI pathToUri(String path) {
        return URI.create(schemeName + ":/" + path);
    }
    
    public boolean isInScheme(URI uri) {
        String path = uri.getPath();
        return schemeName.equals(uri.getScheme())
                && uri.getAuthority()==null
                && path!=null
                && path.length()>0
                && path.charAt(0)=='/';
    }
    
    public String uriToPath(URI uri) {
        if (isInScheme(uri)) {
            return uri.getPath().substring(1);
        }
        return uri.toString();
    }
    
    public String getSchemeName() {
        return schemeName;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(schemeName=" + schemeName
                + ")";
    }
}
