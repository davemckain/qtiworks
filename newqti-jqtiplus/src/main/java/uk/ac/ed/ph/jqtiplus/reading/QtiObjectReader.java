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
import uk.ac.ed.ph.jqtiplus.xmlutils.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLParseResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLResourceReader;
import uk.ac.ed.ph.jqtiplus.xperimental.ReferenceResolver;

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
public final class QtiObjectReader implements ReferenceResolver {

    private static final Logger logger = LoggerFactory.getLogger(QtiObjectReader.class);

    private final JQTIExtensionManager jqtiExtensionManager;
    private final ResourceLocator inputResourceLocator;
    private final boolean schemaValidating;
    private final QtiXmlReader qtiXmlReader;
    
    public QtiObjectReader(JQTIExtensionManager jqtiExtensionManager, ResourceLocator inputResourceLocator, 
            boolean schemaValidating) {
        this(jqtiExtensionManager, null, inputResourceLocator, schemaValidating);
    }

    public QtiObjectReader(JQTIExtensionManager jqtiExtensionManager, ResourceLocator parserResourceLocator, 
            ResourceLocator inputResourceLocator, boolean schemaValidating) {
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.inputResourceLocator = inputResourceLocator;
        this.schemaValidating = schemaValidating;
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

    public boolean isSchemaValidating() {
        return schemaValidating;
    }

    //--------------------------------------------------------------------------
    
    public QtiReadResult readQti(URI systemId)
            throws XMLResourceNotFoundException, BadQtiResourceException {
        logger.info("Attempting to read QTI RootNode from system ID {}", systemId);

        /* We'll create a chained resource locator using the one used to locate parser resources first, as this
         * allows us to resolve things like response processing templates and anything else that might be pre-loaded
         * this way.
         */
        final ChainedResourceLocator resourceLocator = new ChainedResourceLocator(qtiXmlReader.getParserResourceLocator(), inputResourceLocator);

        /* Parse XML */
        final XMLReadResult xmlReadResult = qtiXmlReader.read(systemId, resourceLocator, schemaValidating);
        final XMLParseResult xmlParseResult = xmlReadResult.getXMLParseResult();
        final Document document = xmlReadResult.getDocument();
        if (document==null) {
            /* Parsing failed */
            throw new BadQtiResourceException("XML parsing failed", new QtiReadResult(systemId, xmlParseResult));
        }
        
        /* Bail out if we're validating and the resulting XML was not valid */
        if (schemaValidating && !xmlParseResult.isValidated()) {
            throw new BadQtiResourceException("XML schema validation was requested and the resulting XML was not valid", new QtiReadResult(systemId, xmlParseResult));
        }

        /* Build QTI Object Model */
        final List<QtiModelBuildingError> qtiModelBuildingErrors = new ArrayList<QtiModelBuildingError>();
        final LoadingContext loadingContext = new LoadingContextImpl(qtiModelBuildingErrors);
        logger.debug("Instantiating JQTI Object hierarchy from root Element {}");
        final Element rootElement = document.getDocumentElement();
        try {
            final RootNode resultingQtiObject = RootNodeTypes.load(rootElement, systemId, loadingContext);
            
            /* QTI Object Model built (possibly with errors) */
            final QtiReadResult result = new QtiReadResult(systemId, xmlParseResult, resultingQtiObject, qtiModelBuildingErrors);
            logger.info("Result of QTI Object read from system ID {} is {}", systemId, result);
            return result;
        }
        catch (final IllegalArgumentException e) {
            /* Unsupported root Node type */
            logger.warn("QTI Object read of system ID {} yielded unsupported root Node {}", systemId, rootElement.getLocalName());
            final QtiReadResult failureResult = new QtiReadResult(systemId, xmlParseResult, null, qtiModelBuildingErrors);
            throw new BadQtiResourceException("XML parse succeeded but had an unsupported root Node {"
                    + rootElement.getNamespaceURI()
                    + "}:" + rootElement.getLocalName(), failureResult);
        }
        catch (final QTIParseException e) {
            throw new QTILogicException("All QTIParseExceptions should have been caught before this point!", e);
        }
    }

    public <E extends RootNode> QtiRequireResult<E> readQti(URI systemId, Class<E> requiredResultClass)
            throws XMLResourceNotFoundException, BadQtiResourceException {
        logger.info("Attempting to read QTI Object at system ID {}, requiring result class {}", systemId, requiredResultClass);
        
        QtiReadResult qtiReadResult = readQti(systemId);
        QtiRequireResult<E> result = null;
        RootNode rootNode = qtiReadResult.getResolvedQtiObject();
        if (requiredResultClass.isInstance(rootNode)) {
            result = new QtiRequireResult<E>(requiredResultClass, qtiReadResult);
        }
        else {
            logger.warn("QTI Object {} is not of the required type {}", qtiReadResult, requiredResultClass);
            throw new BadQtiResourceException("QTI Object Model was not of the required type " + requiredResultClass, qtiReadResult);
        }
        
        return result;
    }

    @Override
    public <E extends RootNode> QtiResolutionResult<E> resolve(RootNode baseObject, URI href, Class<E> requiredResultClass)
            throws XMLResourceNotFoundException, BadQtiResourceException {
        logger.info("Resolving href {} against base RootNode having System ID {}", href, baseObject.getSystemId());
        final URI baseUri = baseObject.getSystemId();
        if (baseUri == null) {
            logger.error("baseObject does not have a systemId set, so cannot resolve references against it");
            return null;
        }
        final URI resolved = baseUri.resolve(href.toString());
        QtiRequireResult<E> readResult = readQti(resolved, requiredResultClass);
        QtiResolutionResult<E> result = new QtiResolutionResult<E>(baseUri, href, readResult);
        
        logger.info("Resolution of href {} against base RootNode with System ID {} yielded {}",
                new Object[] { href, baseObject.getSystemId(), result });
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
                + ",schemaValidating=" + schemaValidating
                + ")";
    }
}
