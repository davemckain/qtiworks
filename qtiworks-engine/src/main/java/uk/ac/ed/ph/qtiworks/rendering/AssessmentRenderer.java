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
package uk.ac.ed.ph.qtiworks.rendering;

import uk.ac.ed.ph.qtiworks.domain.binding.ItemSesssionStateXmlMarshaller;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentObject;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.serialization.SaxFiringOptions;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltSerializationOptions;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetManager;

import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * TODO: Need to add support for coping with Content MathML, and possibly annotated MathML
 * containing a mixture of C & P. The idea would be that we use the PMathML, if available,
 * or convert the CMathML to PMathML once all substitutions have been made. Potential
 * complexity exists if we add support for substituting both CMathML and PMathML in a
 * MathML expression containing both PMathML and CMathML annotations. What guarantee is there
 * that we get the same result? I think the spec needs more thought wrt MathML.
 *
 * An instance of this class is safe to use concurrently by multiple threads.
 *
 * @author David McKain
 */
public final class AssessmentRenderer {

    private static final Logger logger = LoggerFactory.getLogger(AssessmentRenderer.class);

    private static final URI standaloneItemXsltUri = URI.create("classpath:/rendering-xslt/standalone-item.xsl");
    private static final URI testItemXsltUri = URI.create("classpath:/rendering-xslt/test-item.xsl");

    private final String webappContextPath;
    private final JqtiExtensionManager jqtiExtensionManager;
    private final XsltStylesheetManager stylesheetManager;

    public AssessmentRenderer(final JqtiExtensionManager jqtiExtensionManager, final String webappContextPath, final XsltStylesheetCache stylesheetCache) {
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.webappContextPath = webappContextPath;
        this.stylesheetManager = new XsltStylesheetManager(new ClassPathResourceLocator(), stylesheetCache);
    }

    /**
     * FIXME: This is probably in the wrong place, as it's not technically a rendering thing!
     * @param object
     * @return
     */
    public String serializeJqtiObject(final XmlNode jqtiObject) {
        final StringWriter resultWriter = new StringWriter();
        serializeJqtiObject(jqtiObject, new StreamResult(resultWriter));
        return resultWriter.toString();
    }

    /**
     * FIXME: This is probably in the wrong place, as it's not technically a rendering thing!
     * @param object
     * @return
     */
    public void serializeJqtiObject(final XmlNode jqtiObject, final OutputStream outputStream) {
        serializeJqtiObject(jqtiObject, new StreamResult(outputStream));
    }

    private void serializeJqtiObject(final XmlNode jqtiObject, final StreamResult result) {
        final XsltSerializationOptions serializationOptions = new XsltSerializationOptions();
        serializationOptions.setIndenting(true);

        final TransformerHandler serializerHandler = stylesheetManager.getSerializerHandler(serializationOptions);
        serializerHandler.setResult(result);
        final QtiSaxDocumentFirer saxEventFirer = new QtiSaxDocumentFirer(jqtiExtensionManager, serializerHandler, new SaxFiringOptions());
        try {
            saxEventFirer.fireSaxDocument(jqtiObject);
        }
        catch (final SAXException e) {
            throw new QtiRenderingException("Unexpected Exception firing QTI Object SAX events at serializer stylesheet");
        }
    }

    /**
     * This is possibly temporary. It renders an {@link AssessmentItem} in a standalone
     * fashion, and not part of an assessment.
     */
    public String renderFreshStandaloneItem(final ItemSessionController itemSessionController,
            final SerializationMethod serializationMethod) {
        logger.debug("renderFreshStandaloneItem(itemSessionController={}, serializationMethod={}",
                new Object[] {
                        itemSessionController, serializationMethod
                });

        return doRenderStandaloneItem(itemSessionController,
                null, null, null, serializationMethod);
    }

    /**
     * This is possibly temporary. It renders an {@link AssessmentItem} in a standalone
     * fashion, and not part of an assessment.
     */
    public String renderRespondedStandaloneItem(final ItemSessionController itemSessionController,
            final Map<Identifier, ResponseData> responseInputs, final Set<Identifier> badResponseIdentifiers,
            final Set<Identifier> invalidResponseIdentifiers, final SerializationMethod serializationMethod) {
        logger.debug("renderStandaloneItem(itemSessionController={}, "
                + "responseInputs={}, unboundResponseIdentifiers={}, "
                + "invalidResponseIdentifiers={}, serializationMethod={}",
                new Object[] {
                        itemSessionController, responseInputs, badResponseIdentifiers,
                        invalidResponseIdentifiers, serializationMethod
                });
        return doRenderStandaloneItem(itemSessionController,
                responseInputs, badResponseIdentifiers, invalidResponseIdentifiers,
                serializationMethod);
    }

    /**
     * This is possibly temporary. It renders an {@link AssessmentItem} in a standalone
     * fashion, and not part of an assessment.
     */
    private String doRenderStandaloneItem(final ItemSessionController itemSessionController,
            final Map<Identifier, ResponseData> responseInputs,
            final Set<Identifier> badResponseIdentifiers, final Set<Identifier> invalidResponseIdentifiers,
            final SerializationMethod serializationMethod) {
        final ResolvedAssessmentItem resolvedAssessmentItem = itemSessionController.getResolvedAssessmentItem();
        final ItemSessionState itemSessionState = itemSessionController.getItemSessionState();

        /* Set various control parameters */
        final Map<String, Object> xsltParameters = new HashMap<String, Object>();
        xsltParameters.put("webappContextPath", webappContextPath);
        xsltParameters.put("serializationMethod", serializationMethod.toString());
        xsltParameters.put("itemSystemId", resolvedAssessmentItem.getItemLookup().getSystemId());
        xsltParameters.put("isResponded", responseInputs!=null);
        xsltParameters.put("badResponseIdentifiers", ObjectUtilities.safeToString(badResponseIdentifiers));
        xsltParameters.put("invalidResponseIdentifiers", ObjectUtilities.safeToString(invalidResponseIdentifiers));

        /* Pass ItemSessionState as XML */
        xsltParameters.put("itemSessionState", ItemSesssionStateXmlMarshaller.marshal(itemSessionState).getDocumentElement());

        /* Pass raw response inputs */
        final XsltParamBuilder xsltParamBuilder = new XsltParamBuilder(jqtiExtensionManager);
        xsltParameters.put("responseInputs", xsltParamBuilder.responseInputsToElements(responseInputs));

        /* FIXME: I've hard-coded maxAttempts=1 (for non-adaptive) items here. In future, this
         * should be settable by the instructor.
         */
        xsltParameters.put("furtherAttemptsAllowed", Boolean.valueOf(itemSessionController.isAttemptAllowed(1)));

        return doTransform(resolvedAssessmentItem, standaloneItemXsltUri, xsltParameters, serializationMethod);
    }

    /**
     * FIXME: This has not been completely refactored yet!
     *
     * This is possibly temporary. It renders an {@link AssessmentItem} as part
     * of an {@link AssessmentTest}
     */
    public String renderTestItem(final ResolvedAssessmentTest resolvedAssessmentTest, final ResolvedAssessmentItem resolvedAssessmentItem,
            final ItemSessionState itemSessionState, final String itemHref, final boolean isResponded,
            final Map<String, Value> responses, final Map<String, Object> testParameters,
            final Map<String, Object> itemParameters, final Map<String, Object> renderingParameters, final SerializationMethod serializationMethod) {
        logger.debug("renderTestItem(resolvedAssessmentTest={}, resolvedAssessmentItem={}, itemSessionState={}, itemHref={}, isResponded={}, "
                + "responses={}, testParameters={}, itemParameters={}, renderingParameters={}, serializationMethod={}",
                new Object[] { resolvedAssessmentTest, resolvedAssessmentItem, itemSessionState,
                        itemHref, isResponded, responses,
                        testParameters, itemParameters, renderingParameters, serializationMethod
                });

        /* Set provided parameters */
        final Map<String, Object> xsltParameters = new HashMap<String, Object>();
        if (renderingParameters!=null) {
            xsltParameters.putAll(testParameters);
            xsltParameters.putAll(itemParameters);
            xsltParameters.putAll(renderingParameters);
        }

        /* (Re)set control parameters, allowing restricted safe override via RenderingParameters */
        xsltParameters.put("webappContextPath", webappContextPath);

        /* Set other control parameters */
        xsltParameters.put("itemHref", itemHref);
        xsltParameters.put("isResponded", isResponded);

        /* Convert template, response, item outcome and test outcome values into parameters */
//        XsltParamBuilder xsltParamBuilder = new XsltParamBuilder();
//        xsltParameters.put("templateValues", xsltParamBuilder.templateValuesToElements(itemSessionState.getTemplateValues()));
//        xsltParameters.put("responseValues", xsltParamBuilder.responseValuesToElements(responses));
//        xsltParameters.put("outcomeValues", xsltParamBuilder.outcomeValuesToElements(itemSessionState.getOutcomeValues()));
//        xsltParameters.put("testOutcomeValues", xsltParamBuilder.outcomeValuesToElements(testSessionState.getOutcomeValues()));
//        xsltParameters.put("testOutcomeDeclarations", xsltParamBuilder.outcomeDeclarationsToElements(testSessionState.getOutcomeDeclarations()));
//
//        /* Pass interaction choice orders as parameters (if really rendering an item which == having an itemBody here) */
//        ItemBody itemBody = item.getItemBody();
//        if (itemBody!=null) {
//            xsltParameters.put("shuffledChoiceOrders", xsltParamBuilder.choiceOrdersToElements(itemSessionState));
//        }

        return doTransform(resolvedAssessmentItem, testItemXsltUri, xsltParameters, serializationMethod);
    }

    private String doTransform(final ResolvedAssessmentObject<?> resolvedAssessmentObject, final URI stylesheetUri,
            final Map<String, Object> parameters, final SerializationMethod serializationMethod) {
        /* Compile stylesheet (or reuse compiled stylesheet from cache) */
        final TransformerHandler transformerHandler = stylesheetManager.getCompiledStylesheetHandler(stylesheetUri);
        final Transformer transformer = transformerHandler.getTransformer();
        transformer.clearParameters();
        for (final Entry<String, Object> paramEntry : parameters.entrySet()) {
            transformer.setParameter(paramEntry.getKey(), paramEntry.getValue());
        }

        /* Configure requested serialization */
        transformer.setParameter("serializationMethod", serializationMethod.toString());
        transformer.setParameter("outputMethod", serializationMethod.getMethod());
        transformer.setParameter("contentType", serializationMethod.getContentType());

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, serializationMethod.getContentType());
        transformer.setOutputProperty(OutputKeys.METHOD, serializationMethod.getMethod());
        transformer.setOutputProperty("include-content-type", "no");

        /* If we're building HTML5, add in its custom pseudo-DOCTYPE as we can't generate this in XSLT */
        final StringWriter resultWriter = new StringWriter();
        if (serializationMethod==SerializationMethod.HTML5_MATHJAX) {
            resultWriter.append("<!DOCTYPE html>\n");
        }

        /* Set up Result */
        final StreamResult result = new StreamResult(resultWriter);
        transformerHandler.setResult(result);

        /* Finally fire the QTI Object at the XSLT handler */
        final XsltSerializationOptions serializationOptions = new XsltSerializationOptions();
        serializationOptions.setIndenting(true);
        final QtiSaxDocumentFirer saxEventFirer = new QtiSaxDocumentFirer(jqtiExtensionManager, transformerHandler, new SaxFiringOptions());
        try {
            saxEventFirer.fireSaxDocument(resolvedAssessmentObject.getRootObjectLookup().extractAssumingSuccessful());
        }
        catch (final SAXException e) {
            throw new QtiRenderingException("Unexpected Exception firing QTI Object SAX events at rendering stylesheet");
        }
        return resultWriter.toString();
    }
}
