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
package uk.ac.ed.ph.qtiworks.test.integration;

import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.MaximaLaunchHelper;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment;
import uk.ac.ed.ph.qtiworks.samples.QtiSampleAssessment.Feature;
import uk.ac.ed.ph.qtiworks.test.utils.TestUtils;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.reading.AssessmentObjectXmlLoader;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidationResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import uk.ac.ed.ph.snuggletex.internal.util.IOUtilities;

import java.io.IOException;
import java.net.URI;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for integration tests that run on a {@link QtiSampleAssessment}
 *
 * @author David McKain
 */
@RunWith(Parameterized.class)
public abstract class AbstractIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);

    protected final QtiSampleAssessment qtiSampleAssessment;
    protected final URI sampleResourceUri;
    protected final ResourceLocator sampleResourceLocator;
    protected final QtiXmlReader sampleXmlReader;
    protected final JqtiExtensionManager jqtiExtensionManager;

    protected AbstractIntegrationTest(final QtiSampleAssessment qtiSampleAssessment) {
        this.jqtiExtensionManager = TestUtils.buildJqtiExtensionManager(qtiSampleAssessment);
        this.qtiSampleAssessment = qtiSampleAssessment;
        this.sampleResourceUri = qtiSampleAssessment.assessmentClassPathUri();
        this.sampleResourceLocator = new ClassPathResourceLocator();
        this.sampleXmlReader = new QtiXmlReader(jqtiExtensionManager);
    }

    @Before
    public void before() {
        /* If we required MathAssess extensions, check whether Maxima is available and
         * skip the test if not. (This allows people to build the project successfully
         * if they don't have Maxima or don't want the Maxima extensions.)
         */
        if (qtiSampleAssessment.hasFeature(Feature.REQUIRES_MATHASSES)) {
            if (!MaximaLaunchHelper.isMaximaWorking()) {
                /* Configuration failed, so use JUnit's Assume class to skip */
                logger.warn("No working Maxima detected, so skipping this test as it requires the MathAssess extensions");
                Assume.assumeTrue(false);
                return;
            }
        }
        jqtiExtensionManager.init();
    }

    @After
    public void after() {
        jqtiExtensionManager.destroy();
    }

    /* NB: This assumes all of our samples are UTF-8! */
    protected String readSampleXmlSource() throws IOException {
        return IOUtilities.readUnicodeStream(sampleResourceLocator.findResource(sampleResourceUri));
    }

    protected XmlReadResult readSampleXml() throws Exception {
        return sampleXmlReader.read(sampleResourceUri, sampleResourceLocator, true);
    }

    protected QtiObjectReader createSampleQtiObjectReader(final boolean schemaValidating) {
        return sampleXmlReader.createQtiObjectReader(sampleResourceLocator, schemaValidating);
    }

    protected AssessmentObjectXmlLoader createSampleAssessmentXmlLoader() {
        return new AssessmentObjectXmlLoader(sampleXmlReader, sampleResourceLocator);
    }

    protected AssessmentObjectValidationResult<?> validateSampleObject() {
        AssessmentObjectValidationResult<?> result = null;
        switch (qtiSampleAssessment.getType()) {
            case ASSESSMENT_ITEM:
                result = createSampleAssessmentXmlLoader().loadResolveAndValidateItem(sampleResourceUri);
                break;

            case ASSESSMENT_TEST:
                result = createSampleAssessmentXmlLoader().loadResolveAndValidateTest(sampleResourceUri);
                break;
        }
        return result;
    }

    protected ItemSessionController createItemSessionController(final boolean isValid) {
        final ResolvedAssessmentItem resolvedAssessmentItem = createSampleAssessmentXmlLoader().loadAndResolveAssessmentItem(sampleResourceUri);
        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(resolvedAssessmentItem, isValid).initialize();
        final ItemSessionState itemSessionState = new ItemSessionState();
        return new ItemSessionController(jqtiExtensionManager, itemSessionControllerSettings, itemProcessingMap, itemSessionState);
    }
}
