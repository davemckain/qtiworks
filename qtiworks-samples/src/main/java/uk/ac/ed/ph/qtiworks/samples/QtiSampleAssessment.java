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
package uk.ac.ed.ph.qtiworks.samples;

import uk.ac.ed.ph.jqtiplus.node.AssessmentObjectType;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Encapsulates a sample QTI Assessment
 *
 * @author David McKain
 */
public class QtiSampleAssessment implements Serializable {
    
    private static final long serialVersionUID = -1829298855881792575L;
    
    public static enum Feature {
        /* Add things here as required */
        NOT_SCHEMA_VALID, /* Example is not schema valid, but kept for historic purposes */
        NOT_FULLY_VALID,  /* Example isn't fully valid (according to our validation process) */
    }
    
    private final AssessmentObjectType type;
    private final String assessmentHref;
    private final Set<Feature> features;
    private final Set<String> fileHrefs;
    
    public QtiSampleAssessment(AssessmentObjectType type, String assessmentHref, Feature... features) {
        this(type, assessmentHref, new String[0], features);
    }
    
    public QtiSampleAssessment(AssessmentObjectType type, String assessmentHref, String[] fileHrefs, Feature... features) {
        this.type = type;
        this.assessmentHref = assessmentHref;
        this.features = new HashSet<Feature>(Arrays.asList(features));
        this.fileHrefs = new HashSet<String>(Arrays.asList(fileHrefs));
    }
    
    public AssessmentObjectType getType() {
        return type;
    }
    
    public String getAssessmentHref() {
        return assessmentHref;
    }
    
    public Set<String> getFileHrefs() {
        return fileHrefs;
    }
    
    public Set<Feature> getFeatures() {
        return features;
    }
    
    public boolean hasFeature(Feature feature) {
        return features.contains(feature);
    }
    
    
    public URI assessmentClassPathUri() {
        return toClassPathUri(assessmentHref);
    }
    
    public URI fileClassPathUri(final String href) {
        return toClassPathUri(href);
    }
    
    /**
     * Converts the href (relative file path) of a sample resource to a ClassPath URI
     * that can be used to load the resource.
     */
    public static URI toClassPathUri(final String sampleResourceHref) {
        return URI.create("classpath:/uk/ac/ed/ph/qtiworks/samples/" + sampleResourceHref);
    }
    
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(type=" + type
                + ",assessmentPath=" + assessmentHref
                + ",fileHrefs=" + fileHrefs
                + ",features=" + features
                + ")";
    }
}
