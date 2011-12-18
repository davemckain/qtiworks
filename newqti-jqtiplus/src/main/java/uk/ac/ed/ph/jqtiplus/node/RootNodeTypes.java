/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.node;

import uk.ac.ed.ph.jqtiplus.exception.QTINodeGroupException;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * This class creates all supported loadable root nodes.
 * <p>
 * Supported root nodes: assessmentTest, assessmentItem, assessmentResult, responseProcessing
 * 
 * @author Jiri Kajaba
 */
public enum RootNodeTypes {
    /**
     * Creates assessmentTest root node.
     *
     * @see AssessmentTest
     */
    ASSESSMENT_TEST(AssessmentTest.CLASS_TAG, AssessmentTest.class),

    /**
     * Creates assessmentItem root node.
     *
     * @see AssessmentItem
     */
    ASSESSMENT_ITEM(AssessmentItem.CLASS_TAG, AssessmentItem.class),

    /**
     * Creates responseProcessing root node.
     *
     * @see AssessmentItem
     */
    RESPONSE_PROCESSING(ResponseProcessing.CLASS_TAG, ResponseProcessing.class),

    /**
     * Creates assessmentResult root node.
     *
     * @see AssessmentResult
     */
    ASSESSMENT_RESULT(AssessmentResult.CLASS_TAG, AssessmentResult.class),
    
    ;

    private static Map<String, RootNodeTypes> rootNodeTypesMap;

    static {
        rootNodeTypesMap = new HashMap<String, RootNodeTypes>();

        for (RootNodeTypes rootNodeType : RootNodeTypes.values())
            rootNodeTypesMap.put(rootNodeType.rootNodeName, rootNodeType);
    }

    private String rootNodeName;
    private Class<? extends RootNode> rootNodeClass;

    private RootNodeTypes(String rootNodeType, Class<? extends RootNode> rootNodeClass) {
        this.rootNodeName = rootNodeType;
        this.rootNodeClass = rootNodeClass;
    }
    
    public String getRootNodeName() {
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
     * Creates root node.
     *
     * @param classTag CLASS_TAG of created root node
     * @return created root node
     */
    public static RootNode getInstance(String classTag) {
        RootNodeTypes rootNodeType = rootNodeTypesMap.get(classTag);
        if (rootNodeType == null) {
            throw new QTINodeGroupException("Unsupported root node " + classTag);
        }
        try {
            return rootNodeType.getRootNodeClass().newInstance();
        }
        catch (Exception e) {
            throw new QTINodeGroupException("Could not instantiate root node Class " + classTag);
        }
    }

    /**
     * Loads root node from given source node.
     *
     * @param sourceElement source node
     * @return loaded root node
     */
    public static RootNode load(Element sourceElement, URI systemId, LoadingContext context) {
        RootNode root = getInstance(sourceElement.getLocalName());
        root.setSystemId(systemId);
        root.load(sourceElement, context);
        return root;
    }
}
