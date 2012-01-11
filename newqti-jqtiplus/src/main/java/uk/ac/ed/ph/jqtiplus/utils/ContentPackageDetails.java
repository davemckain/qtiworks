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
package uk.ac.ed.ph.jqtiplus.utils;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumperOptions;

import java.io.Serializable;
import java.util.List;

/**
 * Encapsulates details of the contents of a Content Package that are of interest to QTI.
 * 
 * @author David McKain
 */
public final class ContentPackageDetails implements Serializable {
    
    private static final long serialVersionUID = 6791550769947268491L;
    
    private final ImsManifestReadResult packageManifestDetails;
    private final List<String> testResourceHrefs;
    private final List<String> itemResourceHrefs;
    
    public ContentPackageDetails(ImsManifestReadResult packageManifestDetails, List<String> testResourceHrefs, List<String> itemResourceHrefs) {
        this.packageManifestDetails = packageManifestDetails;
        this.testResourceHrefs = testResourceHrefs;
        this.itemResourceHrefs = itemResourceHrefs;
    }

    @ObjectDumperOptions(DumpMode.DEEP)
    public ImsManifestReadResult getPackageManifestDetails() {
        return packageManifestDetails;
    }
    
    public List<String> getTestResourceHrefs() {
        return testResourceHrefs;
    }
    
    public List<String> getItemResourceHrefs() {
        return itemResourceHrefs;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(packageManifestDetails=" + packageManifestDetails
                + ",testResourceHrefs=" + testResourceHrefs
                + ",itemResourceHrefs=" + itemResourceHrefs
                + ")";
    }
}
