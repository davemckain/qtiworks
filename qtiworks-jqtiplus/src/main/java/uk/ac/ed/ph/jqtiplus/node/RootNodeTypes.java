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
package uk.ac.ed.ph.jqtiplus.node;

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.QtiProfile;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * This enumeration instantiates all supported {@link RootNode}, which correspond to QTI
 * root element names.
 *
 * @author David McKain
 * @author Jiri Kajaba
 */
public enum RootNodeTypes {

    /**
     * Creates assessmentTest root node.
     *
     * @see AssessmentTest
     */
    ASSESSMENT_TEST(AssessmentTest.QTI_CLASS_NAME, AssessmentTest.class),

    /**
     * Creates assessmentSection root node.
     *
     * @see AssessmentSection
     */
    ASSESSMENT_SECTION(AssessmentSection.QTI_CLASS_NAME, AssessmentSection.class),

    /**
     * Creates assessmentItem root node.
     *
     * @see AssessmentItem
     */
    ASSESSMENT_ITEM(AssessmentItem.QTI_CLASS_NAME, AssessmentItem.class),

    /**
     * Creates responseProcessing root node.
     *
     * @see AssessmentItem
     */
    RESPONSE_PROCESSING(ResponseProcessing.QTI_CLASS_NAME, ResponseProcessing.class),

    /**
     * Creates assessmentResult root node.
     *
     * @see AssessmentResult
     */
    ASSESSMENT_RESULT(AssessmentResult.QTI_CLASS_NAME, AssessmentResult.class),

    ;

    private static Map<String, RootNodeTypes> rootNodeTypesMap;

    static {
        rootNodeTypesMap = new HashMap<String, RootNodeTypes>();

        for (final RootNodeTypes rootNodeType : RootNodeTypes.values()) {
            rootNodeTypesMap.put(rootNodeType.rootNodeName, rootNodeType);
        }
    }

    private final String rootNodeName;
    private final Class<? extends RootNode> rootNodeClass;

    private RootNodeTypes(final String rootNodeType, final Class<? extends RootNode> rootNodeClass) {
        this.rootNodeName = rootNodeType;
        this.rootNodeClass = rootNodeClass;
    }

    public String getRootName() {
        return rootNodeName;
    }

    public Class<? extends RootNode> getRootNodeClass() {
        return rootNodeClass;
    }

    @Override
    public String toString() {
        return rootNodeName;
    }

    /**
     * Creates a QTI root node with given class name.
     *
     * @param qtiClassName QTI_CLASS_NAME of created root node
     * @return created root node
     * @throws IllegalArgumentException if the given qtiClassName does not correspond to a QTI root Node
     * @throws QtiLogicException if the resulting {@link RootNode} could not be instantiated
     */
    public static RootNode getInstance(final String qtiClassName, final URI systemId) {
        final RootNodeTypes rootNodeType = rootNodeTypesMap.get(qtiClassName);
        RootNode result = null;
        if (rootNodeType == null) {
            throw new IllegalArgumentException("Class Tag " + qtiClassName + " does not correspond to a QTI Root Node");
        }
        try {
            result = rootNodeType.getRootNodeClass().newInstance();
        }
        catch (final Exception e) {
            throw new QtiLogicException("Could not instantiate root node Class " + qtiClassName, e);
        }
        result.setSystemId(systemId);
        return result;
    }

    /**
     * Loads root node from given source node, checking namespaces
     *
     * @param sourceElement source node
     * @return loaded root node
     * @throws IllegalArgumentException if the given qtiClassName does not correspond to a root Node
     * @throws QtiLogicException if the resulting {@link RootNode} could not be instantiated
     */
    public static RootNode load(final Element sourceElement, final URI systemId, final LoadingContext context) {
        final RootNode root = getInstance(sourceElement.getLocalName(), systemId);

        /* Check namespaces */
        final String namespaceUri = sourceElement.getNamespaceURI();
        if (root instanceof AssessmentResult) {
            if (!(QtiProfile.QTI_21_CORE.getResultsNamespace().equals(namespaceUri) || QtiProfile.APIP_CORE.getResultsNamespace().equals(namespaceUri)) ) {
                throw new IllegalArgumentException("Element {" + namespaceUri
                        + "}" + sourceElement.getLocalName()
                        + " is not in a correct results namespace, typically " + QtiConstants.QTI_RESULT_21_NAMESPACE_URI);
            }
        }
        else {
            if (!QtiProfile.getAllNamespaceUrisFromAllProfiles().contains(namespaceUri)) {
                throw new IllegalArgumentException("Element {" + namespaceUri
                        + "}" + sourceElement.getLocalName()
                        + " is not in either the QTI 2.1, QTI 2.0, or APIP 1.0 Core namespaces");
            }
        }

        root.load(sourceElement, context);
        return root;
    }
}
