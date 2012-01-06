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
package uk.ac.ed.ph.jqtiplus.xmlutils.legacy;

import uk.ac.ed.ph.jqtiplus.control.QTILogicException;
import uk.ac.ed.ph.jqtiplus.control2.JQTIExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;
import uk.ac.ed.ph.jqtiplus.exception2.QTIModelException;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.RootNode;
import uk.ac.ed.ph.jqtiplus.node.RootNodeTypes;
import uk.ac.ed.ph.jqtiplus.reading.QtiModelBuildingError;
import uk.ac.ed.ph.jqtiplus.xmlutils.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLReaderException;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

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
@ToRefactor
public final class QTIObjectManager {

    private static final Logger logger = LoggerFactory.getLogger(QTIObjectManager.class);

    private final JQTIExtensionManager jqtiExtensionManager;

    private final SupportedXMLReader supportedXMLReader;

    private final ResourceLocator inputResourceLocator;

    private final QTIObjectCache qtiObjectCache;

    public QTIObjectManager(JQTIExtensionManager jqtiExtensionManager, SupportedXMLReader supportedXMLReader, ResourceLocator inputResourceLocator,
            QTIObjectCache qtiObjectCache) {
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.supportedXMLReader = supportedXMLReader;
        this.inputResourceLocator = inputResourceLocator;
        this.qtiObjectCache = qtiObjectCache;
    }

    public JQTIExtensionManager getJQTIExtensionManager() {
        return jqtiExtensionManager;
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
     *             loaded by the {@link #inputResourceLocator}
     */
    @SuppressWarnings("unchecked")
    public <E extends RootNode> QTIReadResult<E> getQTIObject(URI systemId, Class<E> resultClass) {
        /* Try cache first */
        QTIReadResult<E> result = null;
        synchronized (qtiObjectCache) {
            result = (QTIReadResult<E>) qtiObjectCache.getObject(systemId);
            if (result == null) {
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
     *             loaded by the {@link #inputResourceLocator}
     */
    private <E extends RootNode> QTIReadResult<E> readQTI(URI systemId, Class<E> resultClass) {
        /* We'll create a chained resource locator using the one used to locate parser resources first, as this
         * allows us to resolve things like response processing templates and anything else that might be pre-loaded
         * this way.
         */
        final ChainedResourceLocator resourceLocator = new ChainedResourceLocator(supportedXMLReader.getParserResourceLocator(), inputResourceLocator);

        /* Parse XML */
        final XMLReadResult xmlReadResult = supportedXMLReader.read(systemId, resourceLocator);
        final Document document = xmlReadResult.getDocument();

        final List<QtiModelBuildingError> qtiParseErrors = new ArrayList<QtiModelBuildingError>();
        final LoadingContext loadingContext = new LoadingContext() {

            @Override
            public JQTIExtensionManager getJQTIExtensionManager() {
                return jqtiExtensionManager;
            }

            @Override
            public void modelBuildingError(QTIModelException exception, Element owner) {
                final QtiModelBuildingError error = new QtiModelBuildingError(exception,
                        owner.getLocalName(), owner.getNamespaceURI(), SupportedXMLReader.extractLocationInformation(owner));
                qtiParseErrors.add(error);
            }
        };

        /* if XML parse succeeded, instantiate JQTI Object */
        E jqtiObject = null;
        if (document != null) {
            logger.debug("Instantiating JQTI Object hierarchy from root Element {}; expecting to create {}", document.getDocumentElement().getLocalName(),
                    resultClass.getSimpleName());
            try {
                final RootNode xmlObject = RootNodeTypes.load(document.getDocumentElement(), systemId, loadingContext);
                if (!resultClass.isInstance(xmlObject)) {
                    throw new XMLReaderException("QTI XML was instantiated into an instance of "
                            + xmlObject.getClass().getSimpleName()
                            + ", not the requested "
                            + resultClass.getSimpleName());
                }
                jqtiObject = resultClass.cast(xmlObject);
            }
            catch (final QTIParseException e) {
                throw new QTILogicException("All QTIParseExceptions should now be caught before this point!", e);
            }
        }
        return new QTIReadResult<E>(jqtiObject, xmlReadResult.getXMLParseResult(), qtiParseErrors);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(jqtiController=" + jqtiExtensionManager
                + ",supportedXMLReader=" + supportedXMLReader
                + ",inputResourceLocator=" + inputResourceLocator
                + ",qtiObjectCache=" + qtiObjectCache
                + ")";
    }
}
