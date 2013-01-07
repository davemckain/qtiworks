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
package uk.ac.ed.ph.jqtiplus.serialization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;

/**
 * Keeps track of the namespace prefix mappings
 *
 * @author David McKain
 */
public final class NamespacePrefixMappings implements Serializable {

    private static final long serialVersionUID = -1652757838448828206L;

    private final Map<String, String> namespaceUriToPrefixMap;
    private final Map<String, String> prefixToNamespaceUriMap;

    public NamespacePrefixMappings() {
        this.namespaceUriToPrefixMap = new HashMap<String, String>();
        this.prefixToNamespaceUriMap = new HashMap<String, String>();
    }

    public NamespacePrefixMappings(final NamespacePrefixMappings template) {
        this();
        if (template!=null) {
            this.namespaceUriToPrefixMap.putAll(template.namespaceUriToPrefixMap);
            this.prefixToNamespaceUriMap.putAll(template.prefixToNamespaceUriMap);
        }
    }

    public Set<Entry<String, String>> entrySet() {
        return prefixToNamespaceUriMap.entrySet();
    }

    public boolean isPrefixRegistered(final String prefix) {
        return prefixToNamespaceUriMap.containsKey(prefix);
    }

    public boolean isNamespaceUriRegistered(final String namespaceUri) {
        return namespaceUriToPrefixMap.containsKey(namespaceUri);
    }

    public String getPrefix(final String namespaceUri) {
        return namespaceUriToPrefixMap.get(namespaceUri);
    }

    public String getNamespaceUri(final String prefix) {
        return namespaceUriToPrefixMap.get(prefix);
    }

    public void registerStrict(final String namespaceUri, final String prefix) {
        if (namespaceUriToPrefixMap.containsKey(namespaceUri)) {
            throw new IllegalArgumentException("Namespace URI " + namespaceUri + " has already been registered");
        }
        if (prefixToNamespaceUriMap.containsKey(prefix)) {
            throw new IllegalArgumentException("Prefix " + prefix + " has already been registered");
        }
        namespaceUriToPrefixMap.put(namespaceUri, prefix);
        prefixToNamespaceUriMap.put(prefix, namespaceUri);
    }

    public void registerLax(final String namespaceUri, final String prefix) {
        registerStrict(namespaceUri, makeUniquePrefix(prefix));
    }

    public String makeUniquePrefix(final String requestedPrefix) {
        if (!prefixToNamespaceUriMap.containsKey(requestedPrefix)) {
            return requestedPrefix;
        }
        int i=0;
        while (true) {
            final String prefix = requestedPrefix + i;
            if (!prefixToNamespaceUriMap.containsKey(prefix)) {
                return prefix;
            }
            i++;
        }
    }

    public String getQName(final String namespaceUri, final String localName) {
        if (namespaceUri.isEmpty()) {
            return localName;
        }
        String prefix;
        if (XMLConstants.XML_NS_URI.equals(namespaceUri)) {
            /* The xml: prefix mapping is implicit */
            prefix = XMLConstants.XML_NS_PREFIX;
        }
        else {
            prefix = getPrefix(namespaceUri);
            if (prefix==null) {
                throw new IllegalArgumentException("Unregistered namespace URI " + namespaceUri);
            }
        }
        return prefix + ":" + localName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(" + namespaceUriToPrefixMap.toString() + ")";
    }
}
