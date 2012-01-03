/* $Id: QTIObjectLoader.java 2801 2011-10-05 07:57:43Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.io.reading.objects;

import uk.ac.ed.ph.jqtiplus.control.QTILogicException;
import uk.ac.ed.ph.jqtiplus.control2.JQTIExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.io.reading.xml.QTIXMLReader;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.RootNodeTypes;
import uk.ac.ed.ph.jqtiplus.xmlutils.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLResourceReader;
import uk.ac.ed.ph.jqtiplus.xperimental.ReferenceResolver;
import uk.ac.ed.ph.jqtiplus.xperimental.ResolutionResult;

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
 * @author  David McKain
 * @version $Revision: 2801 $
 */
public final class QTIObjectLoader implements ReferenceResolver {
    
    private static final Logger logger = LoggerFactory.getLogger(QTIObjectLoader.class);
    
    private final JQTIExtensionManager jqtiExtensionManager;
    private final ResourceLocator inputResourceLocator;
    private final boolean schemaValidating;
    private final QTIXMLReader qtiXMLReader;
    
    public QTIObjectLoader(JQTIExtensionManager jqtiExtensionManager, ResourceLocator parserResourceLocator, ResourceLocator inputResourceLocator, boolean schemaValidating) {
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.inputResourceLocator = inputResourceLocator;
        this.schemaValidating = schemaValidating;
        this.qtiXMLReader = new QTIXMLReader(parserResourceLocator, jqtiExtensionManager.getExtensionSchemaMap());
    }
    
    public JQTIExtensionManager getJQTIExtensionManager() {
        return jqtiExtensionManager;
    }
    
    public ResourceLocator getInputResourceLocator() {
        return inputResourceLocator;
    }
    
    public ResourceLocator getParserResourceLocator() {
        return qtiXMLReader.getParserResourceLocator();
    }

    public boolean isSchemaValidating() {
        return schemaValidating;
    }
    
    //--------------------------------------------------------------------------

    /**
     * @throws XMLResourceNotFoundException the XML resource with the given System ID could not be
     *   located by the {@link #getInputResourceLocator()}
     * @throws WrongQTIXMLRootNodeException
     */
    public <E extends RootNode> QTIReadResult<E> readQTI(URI systemId, Class<E> resultClass)
            throws XMLResourceNotFoundException, WrongQTIXMLRootNodeException {
        logger.info("Attempting to read QTI Object at system ID {}, expecting result class {}", systemId, resultClass);
        
        /* We'll create a chained resource locator using the one used to locate parser resources first, as this
         * allows us to resolve things like response processing templates and anything else that might be pre-loaded
         * this way.
         */
        ChainedResourceLocator resourceLocator = new ChainedResourceLocator(qtiXMLReader.getParserResourceLocator(), inputResourceLocator);
        
        /* Parse XML */
        XMLReadResult xmlReadResult = qtiXMLReader.read(systemId, resourceLocator, schemaValidating);
        Document document = xmlReadResult.getDocument();
        
        final List<QTIParseError> qtiParseErrors = new ArrayList<QTIParseError>();
        LoadingContext loadingContext = new LoadingContext() {
            @Override
            public JQTIExtensionManager getJQTIExtensionManager() {
                return jqtiExtensionManager;
            }
            
            @Override
            public void parseError(QTIParseException exception, Element owner) {
                QTIParseError error = new QTIParseError(exception, owner, XMLResourceReader.extractLocationInformation(owner));
                qtiParseErrors.add(error);
            }
        };
        
        /* if XML parse succeeded, instantiate JQTI Object */
        E jqtiObject = null;
        if (document!=null) {
            logger.debug("Instantiating JQTI Object hierarchy from root Element {}; expecting to create {}", document.getDocumentElement().getLocalName(), resultClass.getSimpleName());
            Element rootElement = document.getDocumentElement();
            try {
                RootNode rootNode = RootNodeTypes.load(rootElement, systemId, loadingContext);
                if (!resultClass.isInstance(rootNode)) {
                    logger.info("QTI Object read of system ID {} yielded QTI RootNode of type {} instead of {}", new Object[] { systemId, rootNode.getClassTag(), resultClass });
                    throw new WrongQTIXMLRootNodeException(resultClass, rootElement.getLocalName());
                }
                jqtiObject = resultClass.cast(rootNode);
            }
            catch (IllegalArgumentException e) {
                logger.info("QTI Object read of system ID {} yielded unsupported root Node {}", systemId, rootElement.getLocalName());
                throw new WrongQTIXMLRootNodeException(resultClass, rootElement.getLocalName());
            }
            catch (QTIParseException e) {
                throw new QTILogicException("All QTIParseExceptions should have been caught before this point!", e);
            }
        }
        QTIReadResult<E> result = new QTIReadResult<E>(jqtiObject, xmlReadResult.getXMLParseResult(), qtiParseErrors);
        logger.info("Result of QTI Object read from system ID {} is {}", systemId, result);
        return result;
    }
    
    @Override
    public <E extends RootNode> ResolutionResult<E> resolve(RootNode baseObject, URI href, Class<E> resultClass) {
        logger.info("Resolving href {} against base RootNode having System ID {}", href, baseObject.getSystemId());
        URI baseUri = baseObject.getSystemId();
        if (baseUri==null) {
            logger.error("baseObject does not have a systemId set, so cannot resolve references against it");
            return null;
        }
        URI resolved = baseUri.resolve(href.toString());
        Throwable loadException = null;
        QTIReadResult<E> readResult = null;
        try {
            readResult = readQTI(resolved, resultClass);
        }
        catch (Exception e) {
            loadException = e;
        }
        XMLResolutionResult<E> result = new XMLResolutionResult<E>(readResult, loadException);
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
