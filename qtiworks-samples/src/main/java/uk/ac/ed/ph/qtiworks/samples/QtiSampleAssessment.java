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
public final class QtiSampleAssessment implements Serializable {
    
    private static final long serialVersionUID = -1829298855881792575L;
    
    public static enum Feature {
        /* Add things here as required */
        NOT_SCHEMA_VALID, /* Example is not schema valid, but kept for historic purposes */
        NOT_FULLY_VALID,  /* Example isn't fully valid (according to our validation process) */
    }
    
    private final AssessmentObjectType type;
    private final DeliveryStyle deliveryStyle;
    private final String assessmentHref;
    private final Set<String> otherQtiHrefs;
    private final Set<Feature> features;
    private final Set<String> fileHrefs;
    
    /** Use this constructor for sample *items* only */
    public QtiSampleAssessment(DeliveryStyle deliveryStyle, String itemHref, Feature... features) {
        this(deliveryStyle, itemHref, new String[0], features);
    }
    
    /** Use this constructor for sample *items* only */
    public QtiSampleAssessment(DeliveryStyle deliveryStyle, String itemHref,
            String[] fileRelativeHrefs, Feature... features) {
        this(AssessmentObjectType.ASSESSMENT_ITEM, deliveryStyle, itemHref, new String[0], fileRelativeHrefs, features);
    }
    
    /** Use this constructor for both items or tests */
    public QtiSampleAssessment(AssessmentObjectType type, DeliveryStyle deliveryStyle,
            String assessmentHref, String[] otherQtiRelativeHrefs, Feature... features) {
        this(type, deliveryStyle, assessmentHref, otherQtiRelativeHrefs, new String[0], features);
    }
    
    /** Use this constructor for both items or tests */
    public QtiSampleAssessment(AssessmentObjectType type, DeliveryStyle deliveryStyle,
            String assessmentHref, String[] otherQtiRelativeHrefs, String[] fileRelativeHrefs, Feature... features) {
        this.type = type;
        this.deliveryStyle = deliveryStyle;
        this.assessmentHref = assessmentHref;
        this.otherQtiHrefs = resolveHrefs(assessmentHref, otherQtiRelativeHrefs);
        this.fileHrefs = resolveHrefs(assessmentHref, fileRelativeHrefs);
        this.features = new HashSet<Feature>(Arrays.asList(features));
    }
    
    private static Set<String> resolveHrefs(final String baseHref, String... relativeHrefs) {
        Set<String> result = new HashSet<String>();
        for (String relativeHref : relativeHrefs) {
            result.add(resolveHref(baseHref, relativeHref));
        }
        return result;
    }
    
    private static String resolveHref(String baseHref, String relativeHref) {
        return URI.create(baseHref).resolve(relativeHref).toString();
    }
    
    public AssessmentObjectType getType() {
        return type;
    }
    
    public DeliveryStyle getDeliveryStyle() {
        return deliveryStyle;
    }
    
    public String getAssessmentHref() {
        return assessmentHref;
    }
    
    public Set<String> getOtherQtiHrefs() {
        return otherQtiHrefs;
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
                + ",deliveryStyle=" + deliveryStyle
                + ",assessmentHref=" + assessmentHref
                + ",otherQtiHrefs=" + otherQtiHrefs
                + ",fileHrefs=" + fileHrefs
                + ",features=" + features
                + ")";
    }
}
