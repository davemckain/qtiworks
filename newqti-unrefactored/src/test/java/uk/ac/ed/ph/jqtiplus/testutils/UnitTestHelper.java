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

import uk.ac.ed.ph.jqtiplus.control.AssessmentItemController;
import uk.ac.ed.ph.jqtiplus.control.AssessmentTestController;
import uk.ac.ed.ph.jqtiplus.control.JQTIController;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.state.AssessmentItemState;
import uk.ac.ed.ph.jqtiplus.state.AssessmentTestState;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathHTTPResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.AssessmentItemManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.AssessmentTestManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.QTIObjectManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.QTIReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.SimpleQTIObjectCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.SupportedXMLReader;

import java.net.URI;

/**
 * @author David McKain
 */
public final class UnitTestHelper {

    public static AssessmentItemController loadItemForControl(String fileName, Class<?> baseClass) {
        final QTIObjectManager qtiObjectManager = createUnitTestObjectManager(baseClass);
        final AssessmentItem item = loadUnitTestFile(fileName, qtiObjectManager, AssessmentItem.class);
        final AssessmentItemState itemState = new AssessmentItemState();
        final AssessmentItemManager itemManager = new AssessmentItemManager(qtiObjectManager, item);
        return new AssessmentItemController(itemManager, itemState);
    }

    public static AssessmentTestController loadTestForControl(String fileName, Class<?> baseClass) {
        final QTIObjectManager qtiObjectManager = createUnitTestObjectManager(baseClass);
        final AssessmentTest test = loadUnitTestFile(fileName, qtiObjectManager, AssessmentTest.class);
        final AssessmentTestState testState = new AssessmentTestState();
        final AssessmentTestManager testManager = new AssessmentTestManager(qtiObjectManager, test);
        return new AssessmentTestController(testManager, testState);
    }

    public static QTIObjectManager createUnitTestObjectManager(Class<?> baseClass) {
        final JQTIController jqtiController = new JQTIController();
        final SupportedXMLReader xmlReader = new SupportedXMLReader(new ClassPathHTTPResourceLocator(), true);
        return new QTIObjectManager(jqtiController, xmlReader, new ClassResourceLocator(baseClass), new SimpleQTIObjectCache());
    }

    public static <E extends RootNode> E loadUnitTestFile(String fileName, Class<?> baseClass, Class<E> resultClass) {
        final QTIObjectManager objectManager = createUnitTestObjectManager(baseClass);
        return loadUnitTestFile(fileName, objectManager, resultClass);
    }

    public static <E extends RootNode> E loadUnitTestFile(String fileName, QTIObjectManager qtiObjectManager, Class<E> resultClass) {
        final URI fileUri = URI.create("class:" + fileName);
        final QTIReadResult<E> result = qtiObjectManager.getQTIObject(fileUri, resultClass);
        return result.getJQTIObject();
    }
}
