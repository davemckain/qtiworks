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

import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.notification.NotificationListener;
import uk.ac.ed.ph.jqtiplus.reading.AssessmentObjectXmlLoader;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReadResult;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlInterpretationException;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.running.TestPlanner;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.running.TestSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlan;
import uk.ac.ed.ph.jqtiplus.state.TestProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.validation.TestValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceReaderException;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;

/**
 * Very simple facade API for JQTI+ providing an entry point for basic read/write functionality
 * and running of items and tests.
 * <p>
 * While this provides reasonable coverage, not every scenario is covered here so you may need to
 * work with the lower-level APIs..
 *
 * @author David McKain
 */
public final class SimpleJqtiFacade {

    /**
     * Default {@link JqtiExtensionManager} to use if client doesn't specify one.
     * <p>
     * (Since no extension packages have been registered, it is safe to have a singleton here.)
     */
    private static final JqtiExtensionManager DEFAULT_JQTI_EXTENSION_MANAGER = new JqtiExtensionManager();

    private final JqtiExtensionManager jqtiExtensionManager;
    private final QtiXmlReader qtiXmlReader;

    public SimpleJqtiFacade() {
        this(DEFAULT_JQTI_EXTENSION_MANAGER);
    }

    public SimpleJqtiFacade(final JqtiExtensionManager jqtiExtensionManager) {
        Assert.notNull(jqtiExtensionManager, "jqtiExtensionManager");
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
    }

    public JqtiExtensionManager getJqtiExtensionManager() {
        return jqtiExtensionManager;
    }

    //--------------------------------------------------------------
    // Basic QTI reading

    /**
     * Reads the XML resource having the given System ID using the specified {@link ResourceLocator}
     * to locate the XML, optionally performing schema validation.
     *
     * @param inputResourceLocator {@link ResourceLocator} used to read in the QTI XML
     * @param systemId System ID (URI) of the QTI XML resource to be read
     * @param performSchemaValidation whethe to perform schema validation
     *
     * @throws XmlResourceNotFoundException if the XML resource with the given System ID cannot be
     *             located using the given {@link ResourceLocator}
     * @throws XmlResourceReaderException if an unexpected Exception occurred parsing and/or validating the XML, or
     *             if any of the required schemas could not be located.
     *
     * @see QtiXmlReader
     */
    public XmlReadResult readQtiXml(final ResourceLocator inputResourceLocator, final URI systemId,
            final boolean performSchemaValidation)
            throws XmlResourceNotFoundException {
        return qtiXmlReader.read(inputResourceLocator, systemId, performSchemaValidation);
    }

    /**
     * Reads in the QTI XML from the given System ID, then attempts to build a JQTI+ Object
     * model from it.
     *
     * @param inputResourceLocator {@link ResourceLocator} used to read in the QTI XML
     * @param systemId System ID (URI) of the QTI XML, which must correspond to an allowed QTI
     *   {@link RootNode}.
     *
     * @throws XmlResourceNotFoundException if the XML resource with the given System ID cannot be
     *             located using the give {@link ResourceLocator}
     * @throws QtiXmlInterpretationException if the required QTI Object model could not be instantiated from
     *             the XML
     * @throws XmlResourceReaderException if an unexpected Exception occurred parsing and/or validating the XML, or
     *             if any of the required schemas could not be located.
     *
     * @see QtiObjectReader
     */
    public QtiObjectReadResult<RootNode> readQtiRootNode(final ResourceLocator inputResourceLocator,
            final URI systemId, final boolean performSchemaValidation)
            throws XmlResourceNotFoundException, QtiXmlInterpretationException {
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(inputResourceLocator, performSchemaValidation);
        return qtiObjectReader.lookupRootNode(systemId);
    }

    /**
     * Reads in the QTI XML from the given System ID, then attempts to build a JQTI+ Object
     * model from it, having a {@link RootNode} of the specified class.
     *
     * @param inputResourceLocator {@link ResourceLocator} used to read in the QTI XML
     * @param systemId System ID (URI) of the QTI XML, which must correspond to an allowed QTI
     *   {@link RootNode}.
     * @param requiredRootNodeClass Class of the expected {@link RootNode} in the resulting JQTI+
     *   Object model
     *
     * @throws XmlResourceNotFoundException if the XML resource with the given System ID cannot be
     *             located using the given {@link ResourceLocator}
     * @throws QtiXmlInterpretationException if the required QTI Object model could not be instantiated from
     *             the XML
     * @throws XmlResourceReaderException if an unexpected Exception occurred parsing and/or validating the XML, or
     *             if any of the required schemas could not be located.
     *
     * @see QtiObjectReader
     */
    public <E extends RootNode> QtiObjectReadResult<E> readQtiRootNode(final ResourceLocator inputResourceLocator,
            final URI systemId, final boolean performSchemaValidation, final Class<E> requiredRootNodeClass)
            throws XmlResourceNotFoundException, QtiXmlInterpretationException {
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(inputResourceLocator, performSchemaValidation);
        return qtiObjectReader.lookupRootNode(systemId, requiredRootNodeClass);
    }

    //--------------------------------------------------------------
    // QTI AssessmentItem & AssessmentTest loading and resolution

    public ResolvedAssessmentItem loadAndResolveAssessmentItem(final ResourceLocator inputResourceLocator, final URI systemId) {
        final AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader, inputResourceLocator);
        return assessmentObjectXmlLoader.loadAndResolveAssessmentItem(systemId);
    }

    public ResolvedAssessmentTest loadAndResolveAssessmentTest(final ResourceLocator inputResourceLocator, final URI systemId) {
        final AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader, inputResourceLocator);
        return assessmentObjectXmlLoader.loadAndResolveAssessmentTest(systemId);
    }

    //--------------------------------------------------------------
    // QTI AssessmentItem & AssessmentTest loading, resolution and full validation

    public ItemValidationResult loadResolveAndValidateItem(final ResourceLocator inputResourceLocator, final URI systemId) {
        final AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader, inputResourceLocator);
        return assessmentObjectXmlLoader.loadResolveAndValidateItem(systemId);
    }

    public TestValidationResult loadResolveAndValidateTest(final ResourceLocator inputResourceLocator, final URI systemId) {
        final AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader, inputResourceLocator);
        return assessmentObjectXmlLoader.loadResolveAndValidateTest(systemId);
    }

    //--------------------------------------------------------------
    // QTI AssessmentItem & AssessmentTest running (basic)

    /**
     * Builds an {@link ItemProcessingMap} from an {@link ItemValidationResult}. This can be
     * used to create an {@link ItemSessionController} for running the item.
     *
     * @param itemValidationResult {@link ItemValidationResult}, calculated previously.
     *
     * @return Resulting {@link ItemProcessingMap}, which will be null if the underlying
     *   {@link AssessmentItem} could not be resolved.
     *
     * @see #loadResolveAndValidateItem(ResourceLocator, URI)
     * @see #createItemSessionController(ItemSessionControllerSettings, ItemProcessingMap, ItemSessionState)
     */
    public ItemProcessingMap buildItemProcessingMap(final ItemValidationResult itemValidationResult) {
        return new ItemProcessingInitializer(itemValidationResult).initialize();
    }

    /**
     * Creates an {@link ItemSessionController} for running the item, which involves making
     * changes to the provided {@link ItemSessionState} state Object. The provided
     * {@link ItemSessionControllerSettings} will be used to provide additional configuration.
     * <p>
     * You will almost certainly want to register a {@link NotificationListener} on the resulting
     * controller to be kept informed of any runtime notifications when running the item.
     *
     * @param itemSessionControllerSettings {@link ItemSessionControllerSettings} to be used,
     *   which must not be null
     * @param itemProcessingMap already-built {@link ItemProcessingMap},
     *   which must not be null
     * @param itemSessionState {@link ItemSessionState} for the item being run,
     *   which must not be null
     *
     * @return Suitably initialised {@link ItemSessionController}
     *
     * @see #buildItemProcessingMap(ItemValidationResult)
     * @see ItemSessionController#addNotificationListener(NotificationListener)
     */
    public ItemSessionController createItemSessionController(final ItemSessionControllerSettings itemSessionControllerSettings,
            final ItemProcessingMap itemProcessingMap,
            final ItemSessionState itemSessionState) {
        return new ItemSessionController(jqtiExtensionManager, itemSessionControllerSettings,
                itemProcessingMap, itemSessionState);
    }

    /**
     * Builds an {@link TestProcessingMap} from an {@link TestValidationResult}. This can be
     * used to create an {@link TestSessionController} for running the item.
     *
     * @param testValidationResult {@link TestValidationResult}, calculated previously.
     *
     * @return Resulting {@link TestProcessingMap}, which will be null if the underlying
     *   {@link AssessmentTest} could not be resolved.
     *
     * @see #loadResolveAndValidateTest(ResourceLocator, URI)
     * @see #createTestSessionController(TestSessionControllerSettings, TestProcessingMap, TestSessionState)
     */
    public TestProcessingMap buildTestProcessingMap(final TestValidationResult testValidationResult) {
        return new TestProcessingInitializer(testValidationResult).initialize();
    }

    /**
     * Creates a {@link TestPlanner} for initialising a run of an {@link AssessmentTest},
     * using the {@link TestProcessingMap} computed previously.
     * <p>
     * You will almost certainly want to register a {@link NotificationListener} on the result
     * to be kept informed of any runtime notifications when generating the {@link TestPlan}.
     *
     * @see #buildTestProcessingMap(TestValidationResult)
     * @see TestPlanner#generateTestPlan()
     *
     * @param testProcessingMap {@link TestProcessingMap} for the test being run
     *
     * @return A {@link TestPlanner}, which can be used to compute the {@link TestPlan}.
     */
    public TestPlanner createTestPlanner(final TestProcessingMap testProcessingMap) {
        return new TestPlanner(testProcessingMap);
    }

    /**
     * Creates an {@link TestSessionController} for running the test, which involves making
     * changes to the provided {@link TestSessionState} state Object. The provided
     * {@link TestSessionControllerSettings} will be used to provide additional configuration.
     * <p>
     * You will almost certainly want to register a {@link NotificationListener} on the resulting
     * controller to be kept informed of any runtime notifications when running the test.
     *
     * @param testSessionControllerSettings {@link TestSessionControllerSettings} to be used,
     *   which must not be null
     * @param testProcessingMap already-built {@link TestProcessingMap}, which must not be null
     * @param testSessionState {@link TestSessionState} for the test being run,
     *   which must not be null
     *
     * @return Suitably initialised {@link TestSessionController}
     *
     * @see #buildTestProcessingMap(TestValidationResult)
     * @see TestSessionController#addNotificationListener(NotificationListener)
     */
    public TestSessionController createTestSessionController(final TestSessionControllerSettings testSessionControllerSettings,
            final TestProcessingMap testProcessingMap,
            final TestSessionState testSessionState) {
        return new TestSessionController(jqtiExtensionManager, testSessionControllerSettings,
                testProcessingMap, testSessionState);
    }

    //--------------------------------------------------------------
    // Serialization

    /**
     * Creates and returns {@link QtiSerializer} for serializing JQTI+ Objects back to XML.
     * <p>
     * (This will support whichever {@link JqtiExtensionPackage}s were registered when this
     * {@link SimpleJqtiFacade} was created.)
     */
    public QtiSerializer createQtiSerializer() {
        return new QtiSerializer(jqtiExtensionManager);
    }

}
