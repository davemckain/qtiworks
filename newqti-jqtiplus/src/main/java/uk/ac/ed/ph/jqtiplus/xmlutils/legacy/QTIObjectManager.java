/* $Id: QTIObjectLoader.java 2801 2011-10-05 07:57:43Z davemckain $
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils.legacy;

import uk.ac.ed.ph.jqtiplus.control.JQTIController;
import uk.ac.ed.ph.jqtiplus.control.QTILogicException;
import uk.ac.ed.ph.jqtiplus.control2.JQTIExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.io.reading.xml.QTIXMLException;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.RootNodeTypes;
import uk.ac.ed.ph.jqtiplus.xmlutils.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.QTIParseError;
import uk.ac.ed.ph.jqtiplus.xmlutils.ResourceLocator;

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
@Deprecated
public final class QTIObjectManager {
    
    private static final Logger logger = LoggerFactory.getLogger(QTIObjectManager.class);
    
    private final JQTIController jqtiController;
    private final SupportedXMLReader supportedXMLReader;
    private final ResourceLocator inputResourceLocator;
    private final QTIObjectCache qtiObjectCache;
    
    public QTIObjectManager(JQTIController jqtiController, SupportedXMLReader supportedXMLReader, ResourceLocator inputResourceLocator, QTIObjectCache qtiObjectCache) {
        this.jqtiController = jqtiController;
        this.supportedXMLReader = supportedXMLReader;
        this.inputResourceLocator = inputResourceLocator;
        this.qtiObjectCache = qtiObjectCache;
    }
    
    public JQTIController getJQTIController() {
        return jqtiController;
    }
    
    public SupportedXMLReader getSupportedXMLReader() {
        return supportedXMLReader;
    }
    
    public ResourceLocator getInputResourceLocator() {
        return inputResourceLocator;
    }
    
    public QTIObjectCache getQTIObjectCache() {
        return qtiObjectCache;
    }
    
    //--------------------------------------------------------------------------

    /**
     * @throws QTIXMLResourceNotFoundException the XML resource with the given System ID could not be
     *   loaded by the {@link #inputResourceLocator}
     */
    @SuppressWarnings("unchecked")
    public <E extends RootNode> QTIReadResult<E> getQTIObject(URI systemId, Class<E> resultClass) {
        /* Try cache first */
        QTIReadResult<E> result = null;
        synchronized (qtiObjectCache) {
            result = (QTIReadResult<E>) qtiObjectCache.getObject(systemId);
            if (result==null) {
                logger.info("QTI Object for System ID {} is not in cache, so reading new one", systemId);
                result = readQTI(systemId, resultClass);
                qtiObjectCache.putObject(systemId, result);
            }
            else {
                logger.debug("Using cached copy of QTI Object for System ID {}", systemId);
            }
        }
        return result;
    }
    
    /**
     * @throws QTIXMLResourceNotFoundException the XML resource with the given System ID could not be
     *   loaded by the {@link #inputResourceLocator}
     */
    private <E extends RootNode> QTIReadResult<E> readQTI(URI systemId, Class<E> resultClass) {
        /* We'll create a chained resource locator using the one used to locate parser resources first, as this
         * allows us to resolve things like response processing templates and anything else that might be pre-loaded
         * this way.
         */
        ChainedResourceLocator resourceLocator = new ChainedResourceLocator(supportedXMLReader.getParserResourceLocator(), inputResourceLocator);
        
        /* Parse XML */
        XMLReadResult xmlReadResult = supportedXMLReader.read(systemId, resourceLocator);
        Document document = xmlReadResult.getDocument();
        
        final List<QTIParseError> qtiParseErrors = new ArrayList<QTIParseError>();
        LoadingContext loadingContext = new LoadingContext() {
            @Override
            public JQTIExtensionManager getJQTIExtensionManager() {
                return jqtiController;
            }
            
            @Override
            public void parseError(QTIParseException exception, Element owner) {
                QTIParseError error = new QTIParseError(exception, owner, SupportedXMLReader.extractLocationInformation(owner));
                qtiParseErrors.add(error);
            }
        };
        
        /* if XML parse succeeded, instantiate JQTI Object */
        E jqtiObject = null;
        if (document!=null) {
            logger.debug("Instantiating JQTI Object hierarchy from root Element {}; expecting to create {}", document.getDocumentElement().getLocalName(), resultClass.getSimpleName());
            try {
                RootNode xmlObject = RootNodeTypes.load(document.getDocumentElement(), systemId, loadingContext);
                if (!resultClass.isInstance(xmlObject)) {
                    throw new QTIXMLException("QTI XML was instantiated into an instance of "
                            + xmlObject.getClass().getSimpleName()
                            + ", not the requested "
                            + resultClass.getSimpleName());
                }
                jqtiObject = resultClass.cast(xmlObject);
            }
            catch (QTIParseException e) {
                throw new QTILogicException("All QTIParseExceptions should now be caught before this point!", e);
            }
        }
        return new QTIReadResult<E>(jqtiObject, xmlReadResult.getXMLParseResult(), qtiParseErrors);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(jqtiController=" + jqtiController
            + ",supportedXMLReader=" + supportedXMLReader
            + ",inputResourceLocator=" + inputResourceLocator
            + ",qtiObjectCache=" + qtiObjectCache
            + ")";
    }
}
