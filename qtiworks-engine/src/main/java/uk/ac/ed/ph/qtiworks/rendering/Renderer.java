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

import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentTest;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.value.Value;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetCache;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetManager;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

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
public final class Renderer {
    
    private static final Logger logger = LoggerFactory.getLogger(Renderer.class);
    
    private final XsltStylesheetManager stylesheetManager;
    private final UnifiedXMLResourceResolver xmlResourceResolver;
    private final String engineBasePath;
    private final String appletCodebase;
    private final String standaloneItemStylesheetUri;
    private final String testItemStylesheetUri;
    private final String mathJaxUrl;
    private final String mathJaxConfig;
    
    public Renderer(String engineBasePath, String appletCodebase, String standaloneItemStylesheetUri, String testItemStylesheetUri,
            UnifiedXMLResourceResolver xmlResourceResolver, XsltStylesheetCache stylesheetCache,
            String mathJaxUrl, String mathJaxConfig) {
        this.engineBasePath = engineBasePath;
        this.appletCodebase = appletCodebase;
        this.xmlResourceResolver = xmlResourceResolver;
        this.standaloneItemStylesheetUri = standaloneItemStylesheetUri;
        this.testItemStylesheetUri = testItemStylesheetUri;
        this.stylesheetManager = new XsltStylesheetManager(xmlResourceResolver, stylesheetCache);
        this.mathJaxUrl = mathJaxUrl;
        this.mathJaxConfig = mathJaxConfig;
    }
    
    /**
     * This is possibly temporary. It renders an {@link AssessmentItem} in a standalone
     * fashion, and not part of an assessment.
     */
    public String renderStandaloneItem(AssessmentItem assessmentItem, 
            String resourceBasePath, String itemHref, boolean isResponded,
            Map<String, ResponseData> responseInputs,
            List<String> badResponseIdentifiers, List<String> invalidResponseIdentifiers,
            Map<String, Object> itemParameters, Map<String, Object> renderingParameters,
            SerializationMethod serializationMethod) {
        logger.info("renderStandaloneItem(item={}, resourceBasePath={}, itemHref={}, isResponded={}, "
                + "responseInputs={}, badResponseIdentifiers={}, invalidResponseIdentifiers={}, "
                + "itemParameters={}, renderingParameters={} serializationMethod={}",
                new Object[] { assessmentItem.getIdentifier(), resourceBasePath, itemHref, isResponded,
                        responseInputs, badResponseIdentifiers, invalidResponseIdentifiers,
                        itemParameters, renderingParameters, serializationMethod
                });
                
        /* Set provided item & rendering parameters */
        Map<String, Object> xsltParameters = new HashMap<String, Object>();
        if (itemParameters!=null) {
            xsltParameters.putAll(itemParameters);
            xsltParameters.putAll(renderingParameters);
        }
        
        /* (Re)set control parameters, allowing restricted safe override via RenderingParameters */
        xsltParameters.put("engineBasePath", extractPathOverride(renderingParameters, "engineBasePath", engineBasePath));
        xsltParameters.put("appletCodebase", extractPathOverride(renderingParameters, "appletCodebase", appletCodebase));
        xsltParameters.put("resourceBasePath", extractPathOverride(renderingParameters, "resourceBasePath", resourceBasePath));
        
        /* Set other control parameters */
        xsltParameters.put("serializationMethod", serializationMethod.toString());
        xsltParameters.put("itemHref", itemHref);
        xsltParameters.put("isResponded", isResponded);
        xsltParameters.put("badResponseIdentifiers", badResponseIdentifiers);
        xsltParameters.put("invalidResponseIdentifiers", invalidResponseIdentifiers);
        
        /* Convert template, response and outcome values into parameters */
        XsltParamBuilder xsltParamBuilder = new XsltParamBuilder();
        xsltParameters.put("templateValues", xsltParamBuilder.templateValuesToNodes(assessmentItem.getTemplateValues()));
        xsltParameters.put("responseValues", xsltParamBuilder.responseValuesToNodes(assessmentItem.getResponseValues()));
        xsltParameters.put("responseInputs", xsltParamBuilder.responseInputsToElements(responseInputs));
        xsltParameters.put("outcomeValues", xsltParamBuilder.outcomeValuesToNodes(assessmentItem.getOutcomeValues()));
        
        /* Pass interaction choice orders as parameters */
        List<Interaction> interactions = assessmentItem.getItemBody().search(Interaction.class);
        xsltParameters.put("shuffledChoiceOrders", xsltParamBuilder.choiceOrdersToElements(interactions));
        
        return doTransform(assessmentItem.toXmlString(), standaloneItemStylesheetUri, xsltParameters, serializationMethod);
    }
    
    /**
     * This is possibly temporary. It renders an {@link AssessmentItem} as part
     * of an {@link AssessmentTest}
     */
    public String renderTestItem(AssessmentTest assessmentTest, AssessmentItem assessmentItem,
            String resourceBasePath, String itemHref, boolean isResponded,
            Map<String, Value> responses, Map<String, Object> testParameters,
            Map<String, Object> itemParameters, Map<String, Object> renderingParameters, SerializationMethod serializationMethod) {
        logger.info("renderTestItem(test={}, item={}, resourceBasePath={}, itemHref={}, isResponded={}, "
                + "responses={}, testParameters={}, itemParameters={}, renderingParameters={}, serializationMethod={}",
                new Object[] { assessmentTest.getIdentifier(), assessmentItem.getIdentifier(),
                        resourceBasePath, itemHref, isResponded, responses,
                        testParameters, itemParameters, renderingParameters, serializationMethod
                });
        
        /* Set provided parameters */
        Map<String, Object> xsltParameters = new HashMap<String, Object>();
        if (renderingParameters!=null) {
            xsltParameters.putAll(testParameters);
            xsltParameters.putAll(itemParameters);
            xsltParameters.putAll(renderingParameters);
        }
        
        /* (Re)set control parameters, allowing restricted safe override via RenderingParameters */
        xsltParameters.put("engineBasePath", extractPathOverride(renderingParameters, "engineBasePath", engineBasePath));
        xsltParameters.put("appletCodebase", extractPathOverride(renderingParameters, "appletCodebase", appletCodebase));
        xsltParameters.put("resourceBasePath", extractPathOverride(renderingParameters, "resourceBasePath", resourceBasePath));
        
        /* Set other control parameters */
        xsltParameters.put("itemHref", itemHref);
        xsltParameters.put("isResponded", isResponded);
        
        /* Convert template, response, item outcome and test outcome values into parameters */
        XsltParamBuilder xsltParamBuilder = new XsltParamBuilder();
        xsltParameters.put("templateValues", xsltParamBuilder.templateValuesToNodes(assessmentItem.getTemplateValues()));
        xsltParameters.put("responseValues", xsltParamBuilder.responseValuesToNodes(responses));
        xsltParameters.put("outcomeValues", xsltParamBuilder.outcomeValuesToNodes(assessmentItem.getOutcomeValues()));
        xsltParameters.put("testOutcomeValues", xsltParamBuilder.outcomeValuesToNodes(assessmentTest.getOutcomeValues()));
        xsltParameters.put("testOutcomeDeclarations", xsltParamBuilder.outcomeDeclarationsToNodeList(assessmentTest.getOutcomeDeclarations()));
        
        /* Pass interaction choice orders as parameters (if really rendering an item which == having an itemBody here) */
        ItemBody itemBody = assessmentItem.getItemBody();
        if (itemBody!=null) {
            List<Interaction> interactions = assessmentItem.getItemBody().search(Interaction.class);
            xsltParameters.put("shuffledChoiceOrders", xsltParamBuilder.choiceOrdersToElements(interactions));
        }

        return doTransform(assessmentItem.toXmlString(), testItemStylesheetUri, xsltParameters, serializationMethod);
    }
    
    /**
     * Looks in the given {@link Map} of renderingParameters for that having the give name. If found, and
     * its valued is deemed legal, then its value is returned. Otherwise, the provided default is returned.
     */
    private String extractPathOverride(Map<String, Object> renderingParameters, String parameterName, String defaultValue) {
        String value = null;
        if (renderingParameters!=null) {
            value = (String) renderingParameters.get(parameterName);
            if (value!=null && value.matches(".*[^\\w/\\.-].*")) {
                logger.warn("Value for rendering parameter {} must contain only alphanumeric characters, '_', '.', '/' or '-'", parameterName, value);
                value = null;
            }
        }
        if (value==null) {
            value = defaultValue;
        }
        return value;
    }
    
    private String doTransform(String xmlSourceString, String stylesheetUri, 
            Map<String, Object> parameters, SerializationMethod serializationMethod) {
        /* Compile stylesheet (or reuse compiled stylesheet from cache) */
        Transformer transformer = stylesheetManager.getStylesheet(stylesheetUri);
        transformer.clearParameters();
        for (String name : parameters.keySet()) {
            transformer.setParameter(name, parameters.get(name));
        }
        
        /* Configure requested serialization */
        transformer.setParameter("serializationMethod", serializationMethod.toString());
        transformer.setParameter("outputMethod", serializationMethod.getMethod());
        transformer.setParameter("contentType", serializationMethod.getContentType());
        transformer.setParameter("mathJaxUrl", mathJaxUrl);
        transformer.setParameter("mathJaxConfig", mathJaxConfig);
        
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, serializationMethod.getContentType());
        transformer.setOutputProperty(OutputKeys.METHOD, serializationMethod.getMethod());
        transformer.setOutputProperty("include-content-type", "no");

        /* If we're building HTML5, add in its custom pseudo-DOCTYPE as we can't generate this in XSLT */
        StringWriter resultWriter = new StringWriter();
        if (serializationMethod==SerializationMethod.HTML5_MATHJAX) {
            resultWriter.append("<!DOCTYPE html>\n");
        }
        
        /* Then set up source/result and apply stylesheet */
        XMLReader xmlReader;
        try {
            xmlReader = XMLReaderFactory.createXMLReader();
        }
        catch (SAXException e) {
            throw new QtiRenderingException("Could not create XMLReaderFactory");
        }
        xmlReader.setEntityResolver(xmlResourceResolver);
        
        Source source = new SAXSource(xmlReader, new InputSource(new StringReader(xmlSourceString)));
        StreamResult result = new StreamResult(resultWriter);
        try {
            transformer.transform(source, result);
        }
        catch (TransformerException e) {
            throw new QtiRenderingException("XSLT Transformation using stylesheet " + stylesheetUri + " failed", e);
        }
        return resultWriter.toString();
    }
}
