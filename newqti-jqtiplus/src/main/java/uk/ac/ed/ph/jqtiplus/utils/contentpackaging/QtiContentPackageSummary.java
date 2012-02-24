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
package uk.ac.ed.ph.jqtiplus.utils.contentpackaging;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import java.io.Serializable;
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
    
    private final ImsManifestReadResult packageManifestDetails;
    private final Set<String> testResourceHrefs;
    private final Set<String> itemResourceHrefs;
    private final Set<String> fileHrefs;
    
    public QtiContentPackageSummary(ImsManifestReadResult packageManifestDetails, Set<String> testResourceHrefs, Set<String> itemResourceHrefs, Set <String> fileHrefs) {
        this.packageManifestDetails = packageManifestDetails;
        this.testResourceHrefs = ObjectUtilities.unmodifiableSet(testResourceHrefs);
        this.itemResourceHrefs = ObjectUtilities.unmodifiableSet(itemResourceHrefs);
        this.fileHrefs = ObjectUtilities.unmodifiableSet(fileHrefs);
    }

    @ObjectDumperOptions(DumpMode.DEEP)
    public ImsManifestReadResult getPackageManifestDetails() {
        return packageManifestDetails;
    }
    
    public Set<String> getTestResourceHrefs() {
        return testResourceHrefs;
    }
    
    public Set<String> getItemResourceHrefs() {
        return itemResourceHrefs;
    }
    
    public Set<String> getFileHrefs() {
        return fileHrefs;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(packageManifestDetails=" + packageManifestDetails
                + ",testResourceHrefs=" + testResourceHrefs
                + ",itemResourceHrefs=" + itemResourceHrefs
                + ",fileHrefs=" + fileHrefs
                + ")";
    }
}
