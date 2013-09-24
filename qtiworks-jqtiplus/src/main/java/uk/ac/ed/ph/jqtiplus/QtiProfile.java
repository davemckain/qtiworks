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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus;

import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.ForeignElement;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.result.ResultNode;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates the namespace URI and schema location settings for a given QTI/APIP profile.
 * TODO : NLQTI, APIP Entry, etc
 *
 * @author Zack Pierce
 */
public enum QtiProfile {
    QTI_20(QtiConstants.QTI_20_NAMESPACE_URI, QtiConstants.QTI_20_SCHEMA_LOCATION,
            QtiConstants.QTI_20_NAMESPACE_URI, QtiConstants.QTI_20_SCHEMA_LOCATION,
            QtiConstants.QTI_20_NAMESPACE_URI, QtiConstants.QTI_20_SCHEMA_LOCATION,
            QtiConstants.QTI_20_NAMESPACE_URI, QtiConstants.QTI_20_SCHEMA_LOCATION,
            QtiConstants.QTI_20_NAMESPACE_URI, QtiConstants.QTI_20_SCHEMA_LOCATION,
            QtiConstants.QTI_20_NAMESPACE_URI, QtiConstants.QTI_20_SCHEMA_LOCATION,
            QtiConstants.QTI_20_NAMESPACE_URI, QtiConstants.QTI_20_SCHEMA_LOCATION,
            QtiConstants.QTI_20_NAMESPACE_URI, QtiConstants.QTI_20_SCHEMA_LOCATION),

    QTI_21_CORE(QtiConstants.QTI_21_NAMESPACE_URI, QtiConstants.QTI_21_SCHEMA_LOCATION,
            QtiConstants.QTI_21_NAMESPACE_URI, QtiConstants.QTI_21_SCHEMA_LOCATION,
            QtiConstants.QTI_21_NAMESPACE_URI, QtiConstants.QTI_21_SCHEMA_LOCATION,
            QtiConstants.QTI_21_NAMESPACE_URI, QtiConstants.QTI_21_SCHEMA_LOCATION,
            QtiConstants.QTI_21_NAMESPACE_URI, QtiConstants.QTI_21_SCHEMA_LOCATION,
            QtiConstants.QTI_METADATA_21_NAMESPACE_URI, QtiConstants.QTI_METADATA_21_SCHEMA_LOCATION,
            QtiConstants.APIP_CORE_ACCESSIBILITY_URI, QtiConstants.APIP_CORE_ACCESSIBILITY_SCHEMA_LOCATION,
            QtiConstants.QTI_RESULT_21_NAMESPACE_URI, QtiConstants.QTI_RESULT_21_SCHEMA_LOCATION),

    APIP_CORE(QtiConstants.APIP_CORE_ITEM_URI, QtiConstants.APIP_CORE_ITEM_SCHEMA_LOCATION,
            QtiConstants.APIP_CORE_SECTION_URI, QtiConstants.APIP_CORE_SECTION_SCHEMA_LOCATION,
            QtiConstants.APIP_CORE_TEST_URI, QtiConstants.APIP_CORE_TEST_SCHEMA_LOCATION,
            QtiConstants.APIP_CORE_RESPONSE_PROCESSING_URI, QtiConstants.APIP_CORE_RESPONSE_PROCESSING_SCHEMA_LOCATION,
            QtiConstants.APIP_CORE_OUTCOMES_URI, QtiConstants.APIP_CORE_OUTCOMES_SCHEMA_LOCATION,
            QtiConstants.APIP_CORE_QTI_METADATA_URI, QtiConstants.APIP_CORE_QTI_METADATA_SCHEMA_LOCATION,
            QtiConstants.APIP_CORE_ACCESSIBILITY_URI, QtiConstants.APIP_CORE_ACCESSIBILITY_SCHEMA_LOCATION,
            QtiConstants.QTI_RESULT_21_NAMESPACE_URI, QtiConstants.QTI_RESULT_21_SCHEMA_LOCATION);

    private final String itemNamespace;
    private final String itemSchemaLocation;
    private final String sectionNamespace;
    private final String sectionSchemaLocation;
    private final String testNamespace;
    private final String testSchemaLocation;
    private final String responseProcessingNamespace;
    private final String responseProcessingSchemaLocation;
    private final String outcomesNamespace;
    private final String outcomesSchemaLocation;
    private final String qtiMetadataNamespace;
    private final String qtiMetadataSchemaLocation;
    private final String accessibilityNamespace;
    private final String accessibilitySchemaLocation;
    private final String resultsNamespace;
    private final String resultsSchemaLocation;

    private static final Set<String> allNamespacesFromAllProfiles;
    private static final Set<String> allSchemaLocationsFromAllProfiles;
    private static final List<SimpleEntry<String, String>> allNamespaceUriToSchemaLocationPairs;

    static {
        final Set<String> uris = new HashSet<String>();
        final Set<String> locations = new HashSet<String>();
        final List<SimpleEntry<String, String>> pairs = new ArrayList<SimpleEntry<String, String>>();
        for (final QtiProfile profile : QtiProfile.values()) {
            for (final String uri : profile.getNamespaceUris()) {
                uris.add(uri);
            }
            for (final String location : profile.getSchemaLocations()) {
                locations.add(location);
            }
            pairs.addAll(profile.getNamespaceUriToSchemaLocationPairs());
        }
        allNamespacesFromAllProfiles = ObjectUtilities.unmodifiableSet(uris);
        allSchemaLocationsFromAllProfiles = ObjectUtilities.unmodifiableSet(locations);
        allNamespaceUriToSchemaLocationPairs = ObjectUtilities.unmodifiableList(pairs);

    }

    private QtiProfile(final String itemNamespace, final String itemSchemaLocation, final String sectionNamespace, final String sectionSchemaLocation, final String testNamespace, final String testSchemaLocation, final String responseProcessingNamespace, final String responseProcessingSchemaLocation, final String outcomesNamespace, final String outcomesSchemaLocation, final String qtiMetadataNamespace, final String qtiMetadataSchemaLocation, final String accessibilityNamespace, final String accessibilitySchemaLocation, final String resultsNamespace, final String resultsSchemaLocation) {
        this.itemNamespace = itemNamespace;
        this.itemSchemaLocation = itemSchemaLocation;
        this.sectionNamespace = sectionNamespace;
        this.sectionSchemaLocation = sectionSchemaLocation;
        this.testNamespace = testNamespace;
        this.testSchemaLocation = testSchemaLocation;
        this.responseProcessingNamespace = responseProcessingNamespace;
        this.responseProcessingSchemaLocation = responseProcessingSchemaLocation;
        this.outcomesNamespace = responseProcessingNamespace;
        this.outcomesSchemaLocation = outcomesSchemaLocation;
        this.accessibilityNamespace = accessibilityNamespace;
        this.accessibilitySchemaLocation = accessibilitySchemaLocation;
        this.qtiMetadataNamespace = qtiMetadataNamespace;
        this.qtiMetadataSchemaLocation = qtiMetadataSchemaLocation;
        this.resultsNamespace = resultsNamespace;
        this.resultsSchemaLocation = resultsSchemaLocation;
    }

    public String getItemNamespace() {
        return itemNamespace;
    }

    public String getItemSchemaLocation() {
        return itemSchemaLocation;
    }

    public String getSectionNamespace() {
        return sectionNamespace;
    }

    public String getSectionSchemaLocation() {
        return sectionSchemaLocation;
    }

    public String getTestNamespace() {
        return testNamespace;
    }

    public String getTestSchemaLocation() {
        return testSchemaLocation;
    }

    public String getResponseProcessingNamespace() {
        return responseProcessingNamespace;
    }

    public String getResponseProcessingSchemaLocation() {
        return responseProcessingSchemaLocation;
    }

    public String getOutcomesNamespace() {
        return outcomesNamespace;
    }

    public String getOutcomesSchemaLocation() {
        return outcomesSchemaLocation;
    }

    public String getQtiMetadataNamespace() {
        return qtiMetadataNamespace;
    }

    public String getQtiMetadataSchemaLocation() {
        return qtiMetadataSchemaLocation;
    }

    public String getAccessibilityNamespace() {
        return accessibilityNamespace;
    }

    public String getAccessibilitySchemaLocation() {
        return accessibilitySchemaLocation;
    }

    public String getResultsNamespace() {
        return resultsNamespace;
    }

    public String getResultsSchemaLocation() {
        return resultsSchemaLocation;
    }

    public List<String> getNamespaceUris() {
        return Arrays.asList(itemNamespace, sectionNamespace, testNamespace, responseProcessingNamespace,
                outcomesNamespace, qtiMetadataNamespace, accessibilityNamespace, resultsNamespace);
    }

    public List<String> getSchemaLocations() {
        return Arrays.asList(itemSchemaLocation, sectionSchemaLocation, testSchemaLocation,
                responseProcessingSchemaLocation, outcomesSchemaLocation, qtiMetadataSchemaLocation,
                accessibilitySchemaLocation, resultsSchemaLocation);
    }

    public List<SimpleEntry<String, String>> getNamespaceUriToSchemaLocationPairs() {
        final List<SimpleEntry<String, String>> pairs = new ArrayList<AbstractMap.SimpleEntry<String, String>>();
        pairs.add(new SimpleEntry<String, String>(itemNamespace, itemSchemaLocation));
        pairs.add(new SimpleEntry<String, String>(sectionNamespace, sectionSchemaLocation));
        pairs.add(new SimpleEntry<String, String>(testNamespace, testSchemaLocation));
        pairs.add(new SimpleEntry<String, String>(responseProcessingNamespace, responseProcessingSchemaLocation));
        pairs.add(new SimpleEntry<String, String>(outcomesNamespace, outcomesSchemaLocation));
        pairs.add(new SimpleEntry<String, String>(qtiMetadataNamespace, qtiMetadataSchemaLocation));
        pairs.add(new SimpleEntry<String, String>(accessibilityNamespace, accessibilitySchemaLocation));
        pairs.add(new SimpleEntry<String, String>(resultsNamespace, resultsSchemaLocation));
        return pairs;
    }

    /**
     *
     * @param namespaceUri
     * @return The first schemalocation paired with the namespace in this profile, if any. <tt>null</tt> if no matching namespace found.
     */
    public String getSchemaLocationForNamespace(final String namespaceUri) {
        final List<SimpleEntry<String, String>> pairs = this.getNamespaceUriToSchemaLocationPairs();
        for (final SimpleEntry<String, String> simpleEntry : pairs) {
            if (simpleEntry.getKey().equals(namespaceUri)) {
                return simpleEntry.getValue();
            }
        }
        return null;
    }

    /**
     * Determines the namespace URI appropriate to this node's root
     * within this profile.
     *
     * @param node
     * @return A Uri, or an empty string if no known root found
     */
    public String getNamespaceForInstanceRoot(final QtiNode node) {
        final RootNode root = node.getRootNode();
        if (root instanceof AssessmentItem) {
            return this.getItemNamespace();
        }
        else if (root instanceof AssessmentSection) {
            return this.getSectionNamespace();
        }
        else if (root instanceof AssessmentTest) {
            return this.getTestNamespace();
        }
        else if (root instanceof ResponseProcessing) {
            // TODO - consider whether to also allow ResponseProcessingFragment as a root.
            return this.getResponseProcessingNamespace();
        }
        else if (root instanceof OutcomeDeclaration) {
            return this.getOutcomesNamespace();
        }
        else if (root instanceof AssessmentResult) {
            return this.getResultsNamespace();
        }
        return "";
    }

    /**
     * @param node
     * @return The namespace appropriate for this node within the profile, falling back to the item namespace if not determinable
     */
    public String getNamespaceForInstance(final QtiNode node) {
        return getNamespaceForInstance(node, getNamespaceForInstanceRoot(node));
    }

    /**
     *
     * @param node
     * @param rootNamespace A pre-computed value for the <tt>node</tt>'s root's namespace.
     * @return The namespace appropriate for this node within the profile, falling back to the item namespace if not determinable
     */
    public String getNamespaceForInstance(final QtiNode node, final String rootNamespace) {
        if (node instanceof ResultNode) {
            return this.getResultsNamespace();
        }
        else if (node instanceof AccessibilityNode) {
            return this.getAccessibilityNamespace();
        }
        // TODO - QtiMetadata
        else if (node instanceof ForeignElement) {
            return ((ForeignElement) node).getNamespaceUri();
        }
        else if (node instanceof uk.ac.ed.ph.jqtiplus.node.content.mathml.Math) {
            return QtiConstants.MATHML_NAMESPACE_URI;
        }
        else if (rootNamespace != null && !rootNamespace.isEmpty()) {
            return rootNamespace;
        }
        return this.getItemNamespace();
    }

    public static Set<String> getAllNamespaceUrisFromAllProfiles() {
        return allNamespacesFromAllProfiles;
    }

    public static Set<String> getAllSchemaLocationsFromAllProfiles() {
        return allSchemaLocationsFromAllProfiles;
    }

    public static List<SimpleEntry<String, String>> getAllNamespaceUriToSchemaLocationPairs() {
        return allNamespaceUriToSchemaLocationPairs;
    }
}
