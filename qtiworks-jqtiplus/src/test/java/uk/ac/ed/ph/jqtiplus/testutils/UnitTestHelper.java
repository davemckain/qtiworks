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
package uk.ac.ed.ph.jqtiplus.testutils;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.RootObject;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlInterpretationException;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReadResult;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.FileResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * FIXME: Refactor this more appropriately!
 * 
 * @author David McKain
 */
public final class UnitTestHelper {

    public static ItemSessionController loadTestAssessmentItemForControl(String fileName, Class<?> baseClass) 
            throws XmlResourceNotFoundException, QtiXmlInterpretationException {
        final ResolvedAssessmentItem resolvedItem = resolveAssessmentItem(baseClass, fileName, ModelRichness.EXECUTION_ONLY);
        final ItemSessionState itemSessionState = new ItemSessionState();
        return new ItemSessionController(resolvedItem, itemSessionState);
    }

    public static QtiXmlReader createUnitTestXmlReader() {
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        return new QtiXmlReader(jqtiExtensionManager);
    }
    
    public static <E extends RootObject> E loadUnitTestFile(Class<?> baseClass, String fileName, ModelRichness modelRichness, Class<E> requiredResultClass) throws XmlResourceNotFoundException, QtiXmlInterpretationException {
        final URI fileUri = createTestFileUri(baseClass, fileName);
        ResourceLocator testFileResourceLocator = new ClassPathResourceLocator();
        QtiXmlObjectReadResult<E> rootObjectLookup = createUnitTestXmlReader().createQtiXmlObjectReader(testFileResourceLocator).lookupRootObject(fileUri, modelRichness, requiredResultClass);
        return rootObjectLookup.getRootObject();
    }
    
    public static ResolvedAssessmentItem resolveAssessmentItem(Class<?> baseClass, String fileName, ModelRichness modelRichness) throws XmlResourceNotFoundException, QtiXmlInterpretationException {
        final URI fileUri = createTestFileUri(baseClass, fileName);
        ResourceLocator testFileResourceLocator = new FileResourceLocator();
        QtiXmlObjectReader qtiXmlObjectReader = createUnitTestXmlReader().createQtiXmlObjectReader(testFileResourceLocator);
        AssessmentObjectManager objectManager = new AssessmentObjectManager(qtiXmlObjectReader);
        return objectManager.resolveAssessmentItem(fileUri, modelRichness);
    }
    
    public static URI createTestFileUri(Class<?> baseClass, String fileName) {
        try {
            return baseClass.getResource(fileName).toURI();
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("Unexpected Exception", e);
        }
    }
}
