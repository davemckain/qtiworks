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
package uk.ac.ed.ph.jqtiplus.testutils;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
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

    public static URI createTestResourceUri(final Class<?> baseClass, final String fileName) {
        try {
            return baseClass.getResource(fileName).toURI();
        }
        catch (final URISyntaxException e) {
            throw new RuntimeException("Unexpected Exception", e);
        }
    }

    public static JqtiExtensionManager createJqtiExtensionManager() {
        return new JqtiExtensionManager();
    }

    public static QtiXmlReader createUnitTestQtiXmlReader() {
        return new QtiXmlReader(createJqtiExtensionManager());
    }

    public static QtiObjectReader createUnitTestQtiObjectReader(final boolean schemaValidating) {
        final ResourceLocator testFileResourceLocator = new FileResourceLocator();
        return createUnitTestQtiXmlReader().createQtiObjectReader(testFileResourceLocator, schemaValidating);
    }

    public static AssessmentObjectManager createUnitTestAssessmentObjectManager() {
        final QtiXmlReader qtiXmlReader = createUnitTestQtiXmlReader();
        return new AssessmentObjectManager(qtiXmlReader, new FileResourceLocator());
    }

    public static ResolvedAssessmentItem resolveUnitTestAssessmentItem(final Class<?> baseClass, final String fileName) {
        final AssessmentObjectManager objectManager = createUnitTestAssessmentObjectManager();
        final URI fileUri = createTestResourceUri(baseClass, fileName);
        return objectManager.resolveAssessmentItem(fileUri);
    }

    public static ResolvedAssessmentTest resolveUnitTestAssessmentTest(final Class<?> baseClass, final String fileName) {
        final AssessmentObjectManager objectManager = createUnitTestAssessmentObjectManager();
        final URI fileUri = createTestResourceUri(baseClass, fileName);
        return objectManager.resolveAssessmentTest(fileUri);
    }

    public static ItemSessionController loadUnitTestAssessmentItemForControl(final String fileName, final Class<?> baseClass, final boolean isValid) {
        final ResolvedAssessmentItem resolvedAssessmentItem = resolveUnitTestAssessmentItem(baseClass, fileName);

        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(resolvedAssessmentItem, isValid).initialize();
        final ItemSessionState itemSessionState = new ItemSessionState();
        return new ItemSessionController(createJqtiExtensionManager(), itemSessionControllerSettings,
                itemProcessingMap, itemSessionState);
    }

}
