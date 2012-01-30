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
package uk.ac.ed.ph.jqtiplus.node;

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.result.AssessmentResult;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

/**
 * This enumeration instantiates all supported {@link RootObject}, which correspond to QTI
 * root element names.
 *
 * @author David McKain
 * @author Jiri Kajaba
 */
public enum RootObjectTypes {
    
    /**
     * Creates assessmentTest root node.
     * 
     * @see AssessmentTest
     */
    ASSESSMENT_TEST(AssessmentTest.QTI_CLASS_NAME, AssessmentTest.class),

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

    private static Map<String, RootObjectTypes> rootObjectTypesMap;

    static {
        rootObjectTypesMap = new HashMap<String, RootObjectTypes>();

        for (final RootObjectTypes rootObjectType : RootObjectTypes.values()) {
            rootObjectTypesMap.put(rootObjectType.rootObjectName, rootObjectType);
        }
    }

    private final String rootObjectName;
    private final Class<? extends RootObject> rootObjectClass;

    private RootObjectTypes(String rootObjectType, Class<? extends RootObject> rootObjectClass) {
        this.rootObjectName = rootObjectType;
        this.rootObjectClass = rootObjectClass;
    }

    public String getRootName() {
        return rootObjectName;
    }

    public Class<? extends RootObject> getRootObjectClass() {
        return rootObjectClass;
    }

    @Override
    public String toString() {
        return rootObjectName;
    }

    /**
     * Creates root node with given classTag
     * 
     * @param classTag QTI_CLASS_NAME of created root node
     * @return created root node
     * @throws IllegalArgumentException if the given classTag does not correspond to a root Node
     * @throws QtiLogicException if the resulting {@link RootObject} could not be instantiated
     */
    public static RootObject getInstance(String classTag, URI systemId, ModelRichness modelRichness) {
        final RootObjectTypes rootObjectType = rootObjectTypesMap.get(classTag);
        RootObject result = null;
        if (rootObjectType == null) {
            throw new IllegalArgumentException("Class Tag " + classTag + " does not correspond to a QTI Root Node");
        }
        try {
            result = rootObjectType.getRootObjectClass().newInstance();
        }
        catch (final Exception e) {
            throw new QtiLogicException("Could not instantiate root node Class " + classTag, e);
        }
        result.setSystemId(systemId);
        result.setModelRichness(modelRichness);
        return result;
    }

    /**
     * Loads root node from given source node.
     * 
     * @param sourceElement source node
     * @return loaded root node
     * @throws IllegalArgumentException if the given classTag does not correspond to a root Node
     * @throws QtiLogicException if the resulting {@link RootObject} could not be instantiated
     */
    public static RootObject load(Element sourceElement, URI systemId, ModelRichness modelRichness, LoadingContext context) {
        final RootObject root = getInstance(sourceElement.getLocalName(), systemId, modelRichness);
        root.load(sourceElement, context);
        return root;
    }
}
