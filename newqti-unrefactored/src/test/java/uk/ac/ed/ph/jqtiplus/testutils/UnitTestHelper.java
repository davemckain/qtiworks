/* $Id: UnitTestHelper.java 2766 2011-07-21 17:02:08Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
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
import uk.ac.ed.ph.jqtiplus.xmlutils.AssessmentItemManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.AssessmentTestManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathHTTPResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.QTIObjectManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.QTIReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.SimpleQTIObjectCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.SupportedXMLReader;


import java.net.URI;

/**
 * @author  David McKain
 * @version $Revision: 2766 $
 */
public final class UnitTestHelper {
    
    public static AssessmentItemController loadItemForControl(String fileName, Class<?> baseClass) {
        QTIObjectManager qtiObjectManager = createUnitTestObjectManager(baseClass);
        AssessmentItem item = loadUnitTestFile(fileName, qtiObjectManager, AssessmentItem.class);
        AssessmentItemState itemState = new AssessmentItemState();
        AssessmentItemManager itemManager = new AssessmentItemManager(qtiObjectManager, item);
        return new AssessmentItemController(itemManager, itemState);
    }
    
    public static AssessmentTestController loadTestForControl(String fileName, Class<?> baseClass) {
        QTIObjectManager qtiObjectManager = createUnitTestObjectManager(baseClass);
        AssessmentTest test = loadUnitTestFile(fileName, qtiObjectManager, AssessmentTest.class);
        AssessmentTestState testState = new AssessmentTestState();
        AssessmentTestManager testManager = new AssessmentTestManager(qtiObjectManager, test);
        return new AssessmentTestController(testManager, testState);
    }
    
    public static QTIObjectManager createUnitTestObjectManager(Class<?> baseClass) {
        JQTIController jqtiController = new JQTIController();
        SupportedXMLReader xmlReader = new SupportedXMLReader(new ClassPathHTTPResourceLocator(), true);
        return new QTIObjectManager(jqtiController, xmlReader, new ClassResourceLocator(baseClass), new SimpleQTIObjectCache());
    }
    
    public static <E extends RootNode> E loadUnitTestFile(String fileName, Class<?> baseClass, Class<E> resultClass) {
        QTIObjectManager objectManager = createUnitTestObjectManager(baseClass);
        return loadUnitTestFile(fileName, objectManager, resultClass);
    }
    
    public static <E extends RootNode> E loadUnitTestFile(String fileName, QTIObjectManager qtiObjectManager, Class<E> resultClass) {
        URI fileUri = URI.create("class:" + fileName);
        QTIReadResult<E> result = qtiObjectManager.getQTIObject(fileUri, resultClass);
        return result.getJQTIObject();
    }
}
