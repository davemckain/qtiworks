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

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates details of the contents of a Content Package that are of interest to QTI.
 *
 * @see QtiContentPackageExtractor
 *
 * @author David McKain
 */
public final class QtiContentPackageSummary implements Serializable {

    private static final long serialVersionUID = 6791550769947268491L;

    /** Parsed IMS manifest details */
    private final ImsManifestReadResult packageManifestDetails;

    /** Details of all QTI assessmentTest resources declared */
    private final List<ContentPackageResource> testResources;

    /** Details of all QTI assessmentItem resources declared */
    private final List<ContentPackageResource> itemResources;

    /** Convenience set of ALL file hrefs found within the package, with duplicates removed */
    private final Set<URI> fileHrefs;

    public QtiContentPackageSummary(final ImsManifestReadResult packageManifestDetails,
            final List<ContentPackageResource> testResources,
            final List<ContentPackageResource> itemResources, final Set<URI> fileHrefs) {
        this.packageManifestDetails = packageManifestDetails;
        this.testResources = ObjectUtilities.unmodifiableList(testResources);
        this.itemResources = ObjectUtilities.unmodifiableList(itemResources);
        this.fileHrefs = ObjectUtilities.unmodifiableSet(fileHrefs);
    }

    @ObjectDumperOptions(DumpMode.DEEP)
    public ImsManifestReadResult getPackageManifestDetails() {
        return packageManifestDetails;
    }

    public List<ContentPackageResource> getTestResources() {
        return testResources;
    }

    public List<ContentPackageResource> getItemResources() {
        return itemResources;
    }

    public Set<URI> getFileHrefs() {
        return fileHrefs;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(packageManifestDetails=" + packageManifestDetails
                + ",testResources=" + testResources
                + ",itemResources=" + itemResources
                + ",fileHrefs=" + fileHrefs
                + ")";
    }
}
