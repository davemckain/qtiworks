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
package uk.ac.ed.ph.jqtiplus.utils.contentpackaging;

import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the results of attempting to read and parse a Content Packaging imsmanifest.xml
 * file, containing only details that are relevant to QTI.
 *
 * @author David McKain
 */
public final class ImsManifestReadResult implements Serializable {

    private static final long serialVersionUID = -4113064947715847310L;

    private final String manifestHref;
    private final XmlParseResult xmlParseResult;
    private final String namespaceUri;
    private final List<ContentPackageResource> resourceList;
    private final Map<String, List<ContentPackageResource>> resourcesByTypeMap;

    public ImsManifestReadResult(final String manifestHref, final XmlParseResult xmlParseResult,
            final String namespaceUri, final List<ContentPackageResource> resources) {
        this.manifestHref = manifestHref;
        this.xmlParseResult = xmlParseResult;
        this.namespaceUri = namespaceUri;
        this.resourceList = ObjectUtilities.unmodifiableList(resources);

        final Map<String, List<ContentPackageResource>> builder = new HashMap<String, List<ContentPackageResource>>();
        for (final ContentPackageResource resource : resources) {
            List<ContentPackageResource> resourcesByType = builder.get(resource.getType());
            if (resourcesByType==null) {
                resourcesByType = new ArrayList<ContentPackageResource>();
                builder.put(resource.getType(), resourcesByType);
            }
            resourcesByType.add(resource);
        }
        this.resourcesByTypeMap = Collections.unmodifiableMap(builder);
    }

    public ImsManifestReadResult(final String manifestHref, final XmlParseResult xmlParseResult) {
        this.manifestHref = manifestHref;
        this.xmlParseResult = xmlParseResult;
        this.namespaceUri = null;
        this.resourceList = Collections.emptyList();
        this.resourcesByTypeMap = Collections.emptyMap();
    }

    public boolean isUnderstood() {
        return namespaceUri!=null;
    }

    public String getManifestHref() {
        return manifestHref;
    }

    public XmlParseResult getXmlParseResult() {
        return xmlParseResult;
    }

    public String getNamespaceUri() {
        return namespaceUri;
    }

    public List<ContentPackageResource> getResourceList() {
        return resourceList;
    }

    public Map<String, List<ContentPackageResource>> getResourcesByTypeMap() {
        return resourcesByTypeMap;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(manifestHref=" + manifestHref
                + ",xmlParseResult=" + xmlParseResult
                + ",understood=" + isUnderstood()
                + ",namespaceUri=" + namespaceUri
                + ",resourceList=" + resourceList
                + ",resourcesByTypeMap=" + resourcesByTypeMap
                + ")";
    }
}
