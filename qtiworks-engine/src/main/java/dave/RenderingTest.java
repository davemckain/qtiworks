/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEvent;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventNotification;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateItemEventType;
import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSessionStatus;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.ItemRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.RenderingMode;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.notification.NotificationLevel;
import uk.ac.ed.ph.jqtiplus.notification.NotificationType;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.SimpleXsltStylesheetCache;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class RenderingTest {

    public static void main(final String[] args) {
        final URI inputUri = URI.create("classpath:/choice.xml");

        System.out.println("Reading");
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        jqtiExtensionManager.init();
        try {
            final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
            final QtiObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());

            final AssessmentObjectManager objectManager = new AssessmentObjectManager(objectReader);
            final ResolvedAssessmentItem resolvedAssessmentItem = objectManager.resolveAssessmentItem(inputUri, ModelRichness.FULL_ASSUMED_VALID);
            final ItemSessionState itemSessionState = new ItemSessionState();
            final ItemSessionController itemSessionController = new ItemSessionController(jqtiExtensionManager, resolvedAssessmentItem, itemSessionState);

            System.out.println("\nInitialising");
            itemSessionController.initialize();
            System.out.println("Item session state after init: " + ObjectDumper.dumpObject(itemSessionState, DumpMode.DEEP));

            System.out.println("\nRendering");

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
            renderingOptions.setSerializationMethod(SerializationMethod.HTML5_MATHJAX);

            final ItemRenderingRequest renderingRequest = new ItemRenderingRequest();
            renderingRequest.setCandidateSessionState(CandidateSessionStatus.CLOSED);
            renderingRequest.setRenderingMode(RenderingMode.PLAYBACK);
            renderingRequest.setAssessmentResourceLocator(objectReader.getInputResourceLocator());
            renderingRequest.setAssessmentResourceUri(inputUri);
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
            renderingRequest.setBadResponseIdentifiers(null);
            renderingRequest.setInvalidResponseIdentifiers(null);

            final CandidateItemEvent playback1 = new CandidateItemEvent();
            playback1.setId(1L);
            playback1.setEventType(CandidateItemEventType.INIT);

            final CandidateItemEvent playback2 = new CandidateItemEvent();
            playback2.setId(2L);
            playback2.setEventType(CandidateItemEventType.ATTEMPT_VALID);

            renderingRequest.setPlaybackEvents(Arrays.asList(playback1, playback2));
            renderingRequest.setCurrentPlaybackEvent(playback1);

            final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.afterPropertiesSet();

            final AssessmentRenderer renderer = new AssessmentRenderer();
            renderer.setJsr303Validator(validator);
            renderer.setJqtiExtensionManager(jqtiExtensionManager);
            renderer.setXsltStylesheetCache(new SimpleXsltStylesheetCache());
            renderer.init();

            /* Create a fake notification for debugging */
            final CandidateItemEventNotification notification = new CandidateItemEventNotification();
            notification.setNotificationLevel(NotificationLevel.INFO);
            notification.setNotificationType(NotificationType.RUNTIME);
            notification.setMessage("This is a notification");
            final List<CandidateItemEventNotification> notifications = new ArrayList<CandidateItemEventNotification>();
            notifications.add(notification);

            final String rendered = renderer.renderItemToString(renderingRequest, notifications);
            System.out.println("Rendered page: " + rendered);
        }
        finally {
            jqtiExtensionManager.destroy();
        }
    }
}
