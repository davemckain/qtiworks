/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012-2013, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.qtiworks.config.beans.QtiWorksProperties;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.RenderingMode;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.rendering.StandaloneItemRenderingRequest;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
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

import org.apache.commons.io.Charsets;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.io.output.WriterOutputStream;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Developer class for debugging standalone item rendering
 *
 * @author David McKain
 */
public class StandaloneItemRenderingTest {

    public static void main(final String[] args) {
        final URI itemUri = URI.create("classpath:/uk/ac/ed/ph/qtiworks/samples/ims/choice.xml");

        System.out.println("Reading");
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final NotificationLogListener notificationLogListener = new NotificationLogListener();
        jqtiExtensionManager.init();
        try {
            final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
            final AssessmentObjectXmlLoader assessmentObjectXmlLoader = new AssessmentObjectXmlLoader(qtiXmlReader, new ClassPathResourceLocator());

            final ResolvedAssessmentItem resolvedAssessmentItem = assessmentObjectXmlLoader.loadAndResolveAssessmentItem(itemUri);
            final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
            final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(resolvedAssessmentItem, true).initialize();
            final ItemSessionState itemSessionState = new ItemSessionState();
            final ItemSessionController itemSessionController = new ItemSessionController(jqtiExtensionManager,
                    itemSessionControllerSettings, itemProcessingMap, itemSessionState);
            itemSessionController.addNotificationListener(notificationLogListener);

            System.out.println("\nInitialising");
            itemSessionController.initialize();
            itemSessionController.performTemplateProcessing();
            System.out.println("Item session state after init: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

            System.out.println("\nRendering");

            final RenderingOptions renderingOptions = createRenderingOptions();
            final StandaloneItemRenderingRequest renderingRequest = new StandaloneItemRenderingRequest();
            renderingRequest.setRenderingMode(RenderingMode.INTERACTING);
            renderingRequest.setAssessmentResourceLocator(assessmentObjectXmlLoader.getInputResourceLocator());
            renderingRequest.setAssessmentResourceUri(itemUri);
            renderingRequest.setAssessmentItemUri(itemUri);
            renderingRequest.setItemSessionState(itemSessionState);
            renderingRequest.setRenderingOptions(renderingOptions);
            renderingRequest.setPrompt("This is an item!");
            renderingRequest.setAuthorMode(true);
            renderingRequest.setSolutionAllowed(true);
            renderingRequest.setResetAllowed(true);
            renderingRequest.setReinitAllowed(true);
            renderingRequest.setResultAllowed(true);
            renderingRequest.setSourceAllowed(true);

            final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.afterPropertiesSet();

            final QtiWorksProperties qtiWorksProperties = new QtiWorksProperties();
            qtiWorksProperties.setQtiWorksVersion("VERSION");

            final AssessmentRenderer renderer = new AssessmentRenderer();
            renderer.setQtiWorksProperties(qtiWorksProperties);
            renderer.setJsr303Validator(validator);
            renderer.setJqtiExtensionManager(jqtiExtensionManager);
            renderer.setXsltStylesheetCache(new SimpleXsltStylesheetCache());
            renderer.init();

            final StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
            renderer.renderStandaloneItem(renderingRequest, null, new WriterOutputStream(stringBuilderWriter, Charsets.UTF_8));
            final String rendered = stringBuilderWriter.toString();
            System.out.println("Rendered page: " + rendered);
        }
        finally {
            jqtiExtensionManager.destroy();
        }
    }

    public static RenderingOptions createRenderingOptions() {
        final RenderingOptions renderingOptions = new RenderingOptions();
        renderingOptions.setContextPath("/qtiworks");
        renderingOptions.setAttemptUrl("/attempt");
        renderingOptions.setCloseUrl("/close");
        renderingOptions.setResetUrl("/reset");
        renderingOptions.setReinitUrl("/reinit");
        renderingOptions.setSolutionUrl("/solution");
        renderingOptions.setResultUrl("/result");
        renderingOptions.setSourceUrl("/source");
        renderingOptions.setServeFileUrl("/serveFile");
        renderingOptions.setTerminateUrl("/terminate");
        renderingOptions.setTestPartNavigationUrl("/test-part-navigation");
        renderingOptions.setSelectTestItemUrl("/select-item");
        renderingOptions.setFinishTestItemUrl("/finish-item");
        renderingOptions.setEndTestPartUrl("/end-test-part");
        renderingOptions.setReviewTestPartUrl("/review-test-part");
        renderingOptions.setReviewTestItemUrl("/review-item");
        renderingOptions.setShowTestItemSolutionUrl("/item-solution");
        renderingOptions.setAdvanceTestPartUrl("/advance-test-part");
        renderingOptions.setExitTestUrl("/exit-test");
        renderingOptions.setSerializationMethod(SerializationMethod.HTML5_MATHJAX);
        return renderingOptions;
    }
}
