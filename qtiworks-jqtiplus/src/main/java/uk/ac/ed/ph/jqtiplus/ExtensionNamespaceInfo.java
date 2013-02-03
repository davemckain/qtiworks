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
package uk.ac.ed.ph.jqtiplus;

import java.io.Serializable;

/**
 * Encapsulates details about a particular XML namespace used by a QTI extension
 *
 * @author David McKain
 */
public final class ExtensionNamespaceInfo implements Serializable {

    private static final long serialVersionUID = -8191755193016965572L;

    /** XML namespace */
    private final String namespaceUri;

    /** Schema location URI for this namespace */
    private final String schemaLocationUri;

    /** Default prefix to be used for XML serialization */
    private final String defaultPrefix;

    public ExtensionNamespaceInfo(final String namespaceUri, final String schemaLocationUri, final String defaultPrefix) {
        this.namespaceUri = namespaceUri;
        this.schemaLocationUri = schemaLocationUri;
        this.defaultPrefix = defaultPrefix;
    }

    public String getNamespaceUri() {
        return namespaceUri;
    }

    public String getSchemaLocationUri() {
        return schemaLocationUri;
    }

    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(namespaceUri=" + namespaceUri
                + ",schemaUri=" + schemaLocationUri
                + ",defaultPrefix=" + defaultPrefix
                + ")";
    }
}
