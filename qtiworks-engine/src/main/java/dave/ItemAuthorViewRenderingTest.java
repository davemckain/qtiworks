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
package dave;

import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksProperties;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.ItemAuthorViewRenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.ItemAuthorViewRenderingRequest;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLogListener;
import uk.ac.ed.ph.jqtiplus.reading.AssessmentObjectXmlLoader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.SimpleXsltStylesheetCache;

import java.net.URI;
import java.util.Date;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.output.StringBuilderWriter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Developer class for debugging standalone item rendering
 *
 * @author David McKain
 */
public class ItemAuthorViewRenderingTest {

    public static void main(final String[] args) {
        final ClassPathResourceLocator assessmentResourceLocator = new ClassPathResourceLocator();
        final URI itemUri = URI.create("classpath:/uk/ac/ed/ph/qtiworks/samples/ims/choice.xml");

        System.out.println("Reading");
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final NotificationLogListener notificationLogListener = new NotificationLogListener();
        jqtiExtensionManager.init();
        try {
            final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
            final AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader, assessmentResourceLocator);

            final ResolvedAssessmentItem resolvedAssessmentItem = assessmentObjectXmlLoader.loadAndResolveAssessmentItem(itemUri);
            final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
            final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(resolvedAssessmentItem, true).initialize();
            final ItemSessionState itemSessionState = new ItemSessionState();
            final ItemSessionController itemSessionController = new ItemSessionController(jqtiExtensionManager,
                    itemSessionControllerSettings, itemProcessingMap, itemSessionState);
            itemSessionController.addNotificationListener(notificationLogListener);

            final Date timestamp = new Date();
            itemSessionController.initialize(timestamp);
            itemSessionController.performTemplateProcessing(timestamp);
            itemSessionController.enterItem(timestamp);

            final ItemAuthorViewRenderingOptions renderingOptions = RunUtilities.createItemAuthorViewRenderingOptions();
            final ItemAuthorViewRenderingRequest renderingRequest = new ItemAuthorViewRenderingRequest();
            renderingRequest.setAssessmentResourceLocator(assessmentObjectXmlLoader.getInputResourceLocator());
            renderingRequest.setAssessmentResourceUri(itemUri);
            renderingRequest.setRenderingOptions(renderingOptions);
            renderingRequest.setItemSessionState(itemSessionState);
            renderingRequest.setSolutionMode(false);
            renderingRequest.setAuthorMode(true);

            final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.afterPropertiesSet();

            final QtiWorksProperties qtiWorksProperties = new QtiWorksProperties();
            qtiWorksProperties.setQtiWorksVersion("VERSION");

            final AssessmentRenderer renderer = new AssessmentRenderer();
            renderer.setQtiWorksProperties(qtiWorksProperties);
            renderer.setJsr303Validator(validator);
            renderer.setJqtiExtensionManager(jqtiExtensionManager);
            renderer.setXsltStylesheetCache(new SimpleXsltStylesheetCache());
            renderer.setWebappContextPath("/qtiworks");
            renderer.init();

            final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
            final StreamResult result = new StreamResult(stringBuilderWriter);

            renderer.renderItemAuthorView(renderingRequest, null, result);
            final String rendered = stringBuilderWriter.toString();
            System.out.println("Rendered page: " + rendered);
        }
        finally {
            jqtiExtensionManager.destroy();
        }
    }
}
