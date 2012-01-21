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
package dave;

import uk.ac.ed.ph.jqtiplus.control.AssessmentTestAttemptController;
import uk.ac.ed.ph.jqtiplus.control.JQTIController;
import uk.ac.ed.ph.jqtiplus.control.Timer;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.state.AssessmentTestState;
import uk.ac.ed.ph.jqtiplus.validation.ValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathHTTPResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLParseResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.AssessmentTestManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.QTIObjectManager;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.QTIReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.SimpleQTIObjectCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.legacy.SupportedXMLReader;

import java.net.URI;

/**
 * @author David McKain
 */
public class TestTest {

    public static void main(String[] args) {
        final URI inputUri = StandaloneXMLResourceLocator.getMAFileUri("web-app/WEB-INF/content/tests/test_package_minfilesG/example/rtest.xml");

        final JQTIController jqtiController = new JQTIController();

        System.out.println("Reading and validating");
        final SupportedXMLReader xmlReader = new SupportedXMLReader(new ClassPathHTTPResourceLocator(), true);
        final QTIObjectManager objectManager = new QTIObjectManager(jqtiController, xmlReader, new StandaloneXMLResourceLocator(), new SimpleQTIObjectCache());

        final QTIReadResult<AssessmentTest> qtiReadResult = objectManager.getQTIObject(inputUri, AssessmentTest.class);
        final XMLParseResult xmlParseResult = qtiReadResult.getXMLParseResult();
        if (!xmlParseResult.isSchemaValid()) {
            System.out.println("Schema validation failed: " + xmlParseResult);
            return;
        }

        final AssessmentTest test = qtiReadResult.getJQTIObject();
        final AssessmentTestManager testManager = new AssessmentTestManager(objectManager, test);
        final ValidationResult validationResult = testManager.validateTest();
        if (!validationResult.getAllItems().isEmpty()) {
            System.out.println("JQTI validation failed: " + validationResult);
            return;
        }

        System.out.println("Initializing test");
        final AssessmentTestState testState = new AssessmentTestState(test);
        final AssessmentTestAttemptController testController = new AssessmentTestAttemptController(testManager, testState, new Timer());
        testController.initialize();

        System.out.println("Test state: " + ObjectDumper.dumpObject(testState, DumpMode.DEEP));
        System.out.println("Test structure: " + testState.debugStructure());
    }
}
