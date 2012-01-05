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
package uk.ac.ed.ph.jqtiplus.io.reading;

import uk.ac.ed.ph.jqtiplus.control.QTILogicException;
import uk.ac.ed.ph.jqtiplus.control2.JQTIExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.exception2.QTIModelException;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.RootNodeTypes;
import uk.ac.ed.ph.jqtiplus.xmlutils.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.ResourceLocator;
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

    public QtiObjectReader(JQTIExtensionManager jqtiExtensionManager, ResourceLocator parserResourceLocator, ResourceLocator inputResourceLocator,
            boolean schemaValidating) {
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

    /**
     * @throws XMLResourceNotFoundException the XML resource with the given System ID could not be
     *             located by the {@link #getInputResourceLocator()}
     * @throws WrongQTIXMLRootNodeException
     */
    public <E extends RootNode> QtiReadResult<E> readQTI(URI systemId, Class<E> resultClass)
            throws XMLResourceNotFoundException {
        logger.info("Attempting to read QTI Object at system ID {}, expecting result class {}", systemId, resultClass);

        /* We'll create a chained resource locator using the one used to locate parser resources first, as this
         * allows us to resolve things like response processing templates and anything else that might be pre-loaded
         * this way.
         */
        final ChainedResourceLocator resourceLocator = new ChainedResourceLocator(qtiXmlReader.getParserResourceLocator(), inputResourceLocator);

        /* Parse XML */
        final XMLReadResult xmlReadResult = qtiXmlReader.read(systemId, resourceLocator, schemaValidating);
        final Document document = xmlReadResult.getDocument();

        final List<QtiModelBuildingError> qtiModelBuildingErrors = new ArrayList<QtiModelBuildingError>();
        final LoadingContext loadingContext = new LoadingContext() {

            @Override
            public JQTIExtensionManager getJQTIExtensionManager() {
                return jqtiExtensionManager;
            }

            @Override
            public void modelBuildingError(QTIModelException exception, Element owner) {
                final QtiModelBuildingError error = new QtiModelBuildingError(exception, owner.getLocalName(), owner.getNamespaceURI(), XMLResourceReader.extractLocationInformation(owner));
                qtiModelBuildingErrors.add(error);
            }
        };

        /* if XML parse succeeded, instantiate JQTI Object */
        RootNode resultingQtiObject = null;
        if (document != null) {
            logger.debug("Instantiating JQTI Object hierarchy from root Element {}; expecting to create {}", document.getDocumentElement().getLocalName(),
                    resultClass.getSimpleName());
            final Element rootElement = document.getDocumentElement();
            try {
                resultingQtiObject = RootNodeTypes.load(rootElement, systemId, loadingContext);
            }
            catch (final IllegalArgumentException e) {
                logger.warn("QTI Object read of system ID {} yielded unsupported root Node {}", systemId, rootElement.getLocalName());
            }
            catch (final QTIParseException e) {
                throw new QTILogicException("All QTIParseExceptions should have been caught before this point!", e);
            }
        }
        final QtiReadResult<E> result = new QtiReadResult<E>(resultClass, resultingQtiObject, xmlReadResult.getXMLParseResult(), qtiModelBuildingErrors);
        logger.info("Result of QTI Object read from system ID {} is {}", systemId, result);
        return result;
    }

    @Override
    public <E extends RootNode> XmlResolutionResult<E> resolve(RootNode baseObject, URI href, Class<E> resultClass) {
        logger.info("Resolving href {} against base RootNode having System ID {}", href, baseObject.getSystemId());
        final URI baseUri = baseObject.getSystemId();
        if (baseUri == null) {
            logger.error("baseObject does not have a systemId set, so cannot resolve references against it");
            return null;
        }
        final URI resolved = baseUri.resolve(href.toString());
        QtiReadResult<E> readResult = null;
        XMLResourceNotFoundException notFoundException = null;
        try {
            readResult = readQTI(resolved, resultClass);
        }
        catch (final XMLResourceNotFoundException e) {
            notFoundException = e;
        }
        final XmlResolutionResult<E> result = new XmlResolutionResult<E>(readResult, notFoundException);
        logger.info("Resolution of href {} against base RootNode with System ID {} yielded {}",
                new Object[] { href, baseObject.getSystemId(), result });
        return result;
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
