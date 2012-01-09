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
package uk.ac.ed.ph.jqtiplus.reading;

import uk.ac.ed.ph.jqtiplus.control.QTILogicException;
import uk.ac.ed.ph.jqtiplus.control2.JQTIExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.exception2.QTIModelException;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.RootNodeTypes;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceProvider;
import uk.ac.ed.ph.jqtiplus.resolution.ResourceUsage;
import uk.ac.ed.ph.jqtiplus.xmlutils.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLParseResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLResourceReader;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Manages the loading of QTI resources
 * 
 * @author David McKain
 */
public final class QtiObjectReader implements ResourceProvider {

    private static final Logger logger = LoggerFactory.getLogger(QtiObjectReader.class);

    private final JQTIExtensionManager jqtiExtensionManager;
    private final ResourceLocator inputResourceLocator;
    private final QtiXmlReader qtiXmlReader;
    
    public QtiObjectReader(JQTIExtensionManager jqtiExtensionManager, ResourceLocator inputResourceLocator) {
        this(jqtiExtensionManager, null, inputResourceLocator);
    }

    public QtiObjectReader(JQTIExtensionManager jqtiExtensionManager, ResourceLocator parserResourceLocator, 
            ResourceLocator inputResourceLocator) {
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.inputResourceLocator = inputResourceLocator;
        this.qtiXmlReader = new QtiXmlReader(parserResourceLocator, jqtiExtensionManager.getExtensionSchemaMap());
    }

    public JQTIExtensionManager getJQTIExtensionManager() {
        return jqtiExtensionManager;
    }

    public ResourceLocator getInputResourceLocator() {
        return inputResourceLocator;
    }

    public ResourceLocator getParserResourceLocator() {
        return qtiXmlReader.getParserResourceLocator();
    }

    //--------------------------------------------------------------------------
    
    @Override
    public <E extends RootNode> QtiReadResult<E> provideQtiResource(URI systemId, ResourceUsage resourceUsage, Class<E> requiredResultClass)
            throws XMLResourceNotFoundException, BadQtiResourceException {
        logger.info("Attempting to read QTI Object at system ID {} for use {}, requiring result class {}", 
                new Object[] { systemId, resourceUsage, requiredResultClass });

        /* We'll create a chained resource locator using the one used to locate parser resources first, as this
         * allows us to resolve things like response processing templates and anything else that might be pre-loaded
         * this way.
         */
        final ChainedResourceLocator resourceLocator = new ChainedResourceLocator(qtiXmlReader.getParserResourceLocator(), inputResourceLocator);
        
        /* We will only schema validate the XML if we are going to be validating this resource */
        boolean schemaValidating = resourceUsage==ResourceUsage.FOR_VALIDATION;

        /* Parse XML */
        final XMLReadResult xmlReadResult = qtiXmlReader.read(systemId, resourceLocator, schemaValidating);
        final XMLParseResult xmlParseResult = xmlReadResult.getXMLParseResult();
        final Document document = xmlReadResult.getDocument();
        if (document==null) {
            /* Parsing failed */
            throw new BadQtiResourceException("XML parsing failed", new QtiReadResult<E>(systemId, resourceUsage, requiredResultClass, xmlParseResult));
        }
        
        /* Bail out if we're validating and the resulting XML was not valid */
        if (schemaValidating && !xmlParseResult.isSchemaValid()) {
            throw new BadQtiResourceException("XML schema validation was requested and the resulting XML was not valid",
                    new QtiReadResult<E>(systemId, resourceUsage, requiredResultClass, xmlParseResult));
        }

        /* Build QTI Object Model */
        final List<QtiModelBuildingError> qtiModelBuildingErrors = new ArrayList<QtiModelBuildingError>();
        final LoadingContext loadingContext = new LoadingContextImpl(qtiModelBuildingErrors);
        logger.debug("Instantiating JQTI Object hierarchy from root Element {}");
        final Element rootElement = document.getDocumentElement();
        final RootNode resultingQtiObject;
        try {
            resultingQtiObject = RootNodeTypes.load(rootElement, systemId, loadingContext);
        }
        catch (final IllegalArgumentException e) {
            /* Unsupported root Node type */
            logger.warn("QTI Object read of system ID {} yielded unsupported root Node {}", systemId, rootElement.getLocalName());
            throw new BadQtiResourceException("XML parse succeeded but had an unsupported root Node {"
                    + rootElement.getNamespaceURI() + "}:" + rootElement.getLocalName(), 
                    new QtiReadResult<E>(systemId, resourceUsage, requiredResultClass, xmlParseResult, null, qtiModelBuildingErrors));
        }
        catch (final QTIParseException e) {
            throw new QTILogicException("All QTIParseExceptions should have been caught before this point!", e);
        }
        
        /* Make sure we got the right type of Object */
        QtiReadResult<E> result = null;
        if (requiredResultClass.isInstance(resultingQtiObject)) {
            result = new QtiReadResult<E>(systemId, resourceUsage, requiredResultClass, xmlParseResult, resultingQtiObject, qtiModelBuildingErrors);
        }
        else {
            logger.warn("QTI Object {} is not of the required type {}", resultingQtiObject, requiredResultClass);
            throw new BadQtiResourceException("QTI Object Model was not of the required type " + requiredResultClass,
                    new QtiReadResult<RootNode>(systemId, resourceUsage, RootNode.class, xmlParseResult, resultingQtiObject, qtiModelBuildingErrors));
        }
        logger.info("Result of QTI Object read from system ID {} is {}", systemId, result);
        return result;
    }

    /**
     * Implementation of {@link LoadingContext} that records any {@link QtiModelBuildingError}s
     * in an {@link ArrayList}.
     *
     * @author David McKain
     */
    final class LoadingContextImpl implements LoadingContext {
        
        private final List<QtiModelBuildingError> qtiModelBuildingErrors;
        
        public LoadingContextImpl(final List<QtiModelBuildingError> qtiModelBuildingErrors) {
            this.qtiModelBuildingErrors = qtiModelBuildingErrors;
        }
        
        @Override
        public JQTIExtensionManager getJQTIExtensionManager() {
            return jqtiExtensionManager;
        }

        @Override
        public void modelBuildingError(QTIModelException exception, Element owner) {
            qtiModelBuildingErrors.add(new QtiModelBuildingError(exception, owner.getLocalName(),
                    owner.getNamespaceURI(), XMLResourceReader.extractLocationInformation(owner)));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(jqtiExtensionManager=" + jqtiExtensionManager
                + ",parserResourceLocator=" + getParserResourceLocator()
                + ",inputResourceLocator=" + inputResourceLocator
                + ")";
    }
}
