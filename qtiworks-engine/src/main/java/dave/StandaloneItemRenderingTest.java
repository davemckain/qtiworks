/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEventCategory;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.RenderingMode;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;
import uk.ac.ed.ph.qtiworks.rendering.StandaloneItemRenderingRequest;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLogListener;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.SimpleXsltStylesheetCache;

import java.net.URI;
import java.util.Arrays;

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
        final URI itemUri = URI.create("classpath:/choice.xml");

        System.out.println("Reading");
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final NotificationLogListener notificationLogListener = new NotificationLogListener();
        jqtiExtensionManager.init();
        try {
            final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
            final QtiObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());

            final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
            final ResolvedAssessmentItem resolvedAssessmentItem = objectManager.resolveAssessmentItem(itemUri, ModelRichness.FULL_ASSUMED_VALID);
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
            renderingRequest.setRenderingMode(RenderingMode.PLAYBACK);
            renderingRequest.setAssessmentResourceLocator(objectReader.getInputResourceLocator());
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
            renderingRequest.setPlaybackAllowed(true);

            final CandidateEvent playback1 = new CandidateEvent();
            playback1.setId(1L);
            playback1.setCandidateEventCategory(CandidateEventCategory.ITEM);
            playback1.setItemEventType(CandidateItemEventType.INIT);

            final CandidateEvent playback2 = new CandidateEvent();
            playback2.setId(2L);
            playback2.setCandidateEventCategory(CandidateEventCategory.ITEM);
            playback2.setItemEventType(CandidateItemEventType.ATTEMPT_VALID);

            renderingRequest.setPlaybackEvents(Arrays.asList(playback1, playback2));
            renderingRequest.setCurrentPlaybackEvent(playback1);

            final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.afterPropertiesSet();

            final AssessmentRenderer renderer = new AssessmentRenderer();
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
        renderingOptions.setPlaybackUrlBase("/playback");
        renderingOptions.setTerminateUrl("/terminate");
        renderingOptions.setSelectItemUrl("/select");
        renderingOptions.setEndTestPartUrl("/endtestpart");
        renderingOptions.setExitTestPartUrl("/exittestpart");
        renderingOptions.setTestPartNavigationUrl("/navigation");
        renderingOptions.setSerializationMethod(SerializationMethod.HTML5_MATHJAX);
        return renderingOptions;
    }
}
