/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2012, The University of Edinburgh.
 * All Rights Reserved
 */
package dave;

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateSessionState;
import uk.ac.ed.ph.qtiworks.rendering.AssessmentRenderer;
import uk.ac.ed.ph.qtiworks.rendering.ItemRenderingRequest;
import uk.ac.ed.ph.qtiworks.rendering.RenderingOptions;
import uk.ac.ed.ph.qtiworks.rendering.SerializationMethod;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.RuntimeValidationException;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectManager;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.SimpleXsltStylesheetCache;

import java.net.URI;

import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class RenderingTest {

    public static void main(final String[] args) throws RuntimeValidationException {
        final URI inputUri = URI.create("classpath:/templateConstraint-1.xml");

        System.out.println("Reading");
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        jqtiExtensionManager.init();
        try {
            final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
            final QtiXmlObjectReader objectReader = qtiXmlReader.createQtiXmlObjectReader(new ClassPathResourceLocator());

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
            renderingOptions.setEndUrl("/end");
            renderingOptions.setResetUrl("/reset");
            renderingOptions.setReinitUrl("/reinit");
            renderingOptions.setResultUrl("/result");
            renderingOptions.setSourceUrl("/source");
            renderingOptions.setServeFileUrl("/serveFile");
            renderingOptions.setCloseUrl("/close");
            renderingOptions.setSerializationMethod(SerializationMethod.HTML5_MATHJAX);

            final ItemRenderingRequest renderingRequest = new ItemRenderingRequest();
            renderingRequest.setAssessmentResourceLocator(objectReader.getInputResourceLocator());
            renderingRequest.setAssessmentResourceUri(inputUri);
            renderingRequest.setCandidateSessionState(CandidateSessionState.INTERACTING);
            renderingRequest.setItemSessionState(itemSessionState);
            renderingRequest.setRenderingOptions(renderingOptions);
            renderingRequest.setAttemptAllowed(true);
            renderingRequest.setResetAllowed(true);
            renderingRequest.setReinitAllowed(true);
            renderingRequest.setResultAllowed(true);
            renderingRequest.setSourceAllowed(true);
            renderingRequest.setBadResponseIdentifiers(null);
            renderingRequest.setInvalidResponseIdentifiers(null);

            final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.afterPropertiesSet();

            final AssessmentRenderer renderer = new AssessmentRenderer();
            renderer.setJsr303Validator(validator);
            renderer.setJqtiExtensionManager(jqtiExtensionManager);
            renderer.setXsltStylesheetCache(new SimpleXsltStylesheetCache());
            renderer.init();
            final String rendered = renderer.renderItem(renderingRequest);
            System.out.println("Rendered page: " + rendered);
        }
        finally {
            jqtiExtensionManager.destroy();
        }
    }
}
