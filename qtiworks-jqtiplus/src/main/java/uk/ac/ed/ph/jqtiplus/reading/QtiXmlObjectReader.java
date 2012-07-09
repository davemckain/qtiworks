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

import static uk.ac.ed.ph.jqtiplus.reading.QtiXmlInterpretationException.InterpretationFailureReason.JQTI_MODEL_BUILD_FAILED;
import static uk.ac.ed.ph.jqtiplus.reading.QtiXmlInterpretationException.InterpretationFailureReason.UNSUPPORTED_ROOT_NODE;
import static uk.ac.ed.ph.jqtiplus.reading.QtiXmlInterpretationException.InterpretationFailureReason.WRONG_RESULT_TYPE;
import static uk.ac.ed.ph.jqtiplus.reading.QtiXmlInterpretationException.InterpretationFailureReason.XML_PARSE_FAILED;
import static uk.ac.ed.ph.jqtiplus.reading.QtiXmlInterpretationException.InterpretationFailureReason.XML_SCHEMA_VALIDATION_FAILED;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.exception2.QtiModelException;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.ModelRichness;
import uk.ac.ed.ph.jqtiplus.node.RootObject;
import uk.ac.ed.ph.jqtiplus.node.RootObjectTypes;
import uk.ac.ed.ph.jqtiplus.provision.RootObjectProvider;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceReader;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceReaderException;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ChainedResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Default implementation of {@link RootObjectProvider}, which uses a {@link QtiXmlReader} to
 * instantiate QTI {@link RootObject}s by parsing (and optionally schema-validating) XML.
 *
 * @author David McKain
 */
public final class QtiXmlObjectReader implements RootObjectProvider {

    private static final Logger logger = LoggerFactory.getLogger(QtiXmlObjectReader.class);

    private final ResourceLocator inputResourceLocator;
    private final QtiXmlReader qtiXmlReader;

    QtiXmlObjectReader(final QtiXmlReader qtiXmlReader, final ResourceLocator inputResourceLocator) {
        this.qtiXmlReader = qtiXmlReader;
        this.inputResourceLocator = inputResourceLocator;
    }

    public QtiXmlReader getQtiXmlReader() {
        return qtiXmlReader;
    }

    public ResourceLocator getInputResourceLocator() {
        return inputResourceLocator;
    }

    //--------------------------------------------------------------------------

    @Override
    public JqtiExtensionManager getJqtiExtensionManager() {
        return qtiXmlReader.getJqtiExtensionManager();
    }

    /**
     * @throws XmlResourceNotFoundException if the XML resource with the given System ID cannot be
     *             located using the current {@link #inputResourceLocator}
     * @throws QtiXmlInterpretationException if the required QTI Object model could not be instantiated from
     *             the XML
     * @throws XmlResourceReaderException if an unexpected Exception occurred parsing and/or validating the XML, or
     *             if any of the required schemas could not be located.
     */
    @Override
    public <E extends RootObject> QtiXmlObjectReadResult<E> lookupRootObject(final URI systemId, final ModelRichness requiredModelRichness, final Class<E> requiredResultClass)
            throws XmlResourceNotFoundException, QtiXmlInterpretationException {
        logger.debug("Attempting to read QTI Object at system ID {} for use {}, requiring result class {}",
                new Object[] { systemId, requiredModelRichness, requiredResultClass });

        /* We'll create a chained resource locator using the one used to locate parser resources first, as this
         * allows us to resolve things like response processing templates and anything else that might be pre-loaded
         * this way.
         */
        final ChainedResourceLocator resourceLocator = new ChainedResourceLocator(QtiXmlReader.JQTIPLUS_PARSER_RESOURCE_LOCATOR, inputResourceLocator);

        /* We will only schema validate the XML if we are going to be validating this resource */
        final boolean schemaValidating = requiredModelRichness==ModelRichness.FOR_VALIDATION;

        /* Parse XML */
        final XmlReadResult xmlReadResult = qtiXmlReader.read(systemId, resourceLocator, schemaValidating);
        final XmlParseResult xmlParseResult = xmlReadResult.getXmlParseResult();
        final Document document = xmlReadResult.getDocument();
        if (document==null) {
            /* Parsing failed */
            throw new QtiXmlInterpretationException(XML_PARSE_FAILED, "XML parsing failed",
                    requiredModelRichness, requiredResultClass, xmlParseResult);
        }

        /* Bail out if we're validating and the resulting XML was not valid */
        if (schemaValidating && !xmlParseResult.isSchemaValid()) {
            throw new QtiXmlInterpretationException(XML_SCHEMA_VALIDATION_FAILED, "XML schema validation was requested and the resulting XML was not valid",
                   requiredModelRichness, requiredResultClass, xmlParseResult);
        }

        /* Build QTI Object Model */
        final List<QtiModelBuildingError> qtiModelBuildingErrors = new ArrayList<QtiModelBuildingError>();
        final LoadingContext loadingContext = new LoadingContextImpl(qtiModelBuildingErrors);
        logger.trace("Instantiating JQTI Object hierarchy from root Element {}");
        final RootObject rootObject;
        final Element rootElement = document.getDocumentElement();
        final String rootNamespaceUri = rootElement.getNamespaceURI();
        try {
            rootObject = RootObjectTypes.load(rootElement, systemId, requiredModelRichness, loadingContext);
        }
        catch (final IllegalArgumentException e) {
            /* Unsupported root Node type */
            logger.debug("QTI Object read of system ID {} yielded unsupported root Node {}", systemId, rootElement.getLocalName());
            throw new QtiXmlInterpretationException(UNSUPPORTED_ROOT_NODE, "XML parse succeeded but had an unsupported root Node {"
                    + rootNamespaceUri + "}:" + rootElement.getLocalName(),
                    requiredModelRichness, requiredResultClass, xmlParseResult, null, qtiModelBuildingErrors);
        }
        catch (final QtiParseException e) {
            throw new QtiLogicException("All QtiParseExceptions should have been caught before this point!", e);
        }

        /* Make sure we got the right type of Object */
        if (!requiredResultClass.isInstance(rootObject)) {
            logger.debug("QTI Object {} is not of the required type {}", rootObject, requiredResultClass);
            throw new QtiXmlInterpretationException(WRONG_RESULT_TYPE, "QTI Object Model was not of the required type " + requiredResultClass,
                    requiredModelRichness, requiredResultClass, xmlParseResult, rootObject, qtiModelBuildingErrors);
        }

        /* Make sure there were no model building errors */
        if (!qtiModelBuildingErrors.isEmpty()) {
            logger.debug("QTI Object read of system ID {} resulting in {} model building error(s): {}",
                    new Object[] { systemId, qtiModelBuildingErrors.size(), qtiModelBuildingErrors });
            throw new QtiXmlInterpretationException(JQTI_MODEL_BUILD_FAILED, "XML parse succeeded but generated " + qtiModelBuildingErrors.size() + " model building error(s)",
                    requiredModelRichness, requiredResultClass, xmlParseResult, null, qtiModelBuildingErrors);
        }

        /* Success! */
        final QtiXmlObjectReadResult<E> result = new QtiXmlObjectReadResult<E>(requiredResultClass,
                xmlParseResult, rootNamespaceUri,
                requiredResultClass.cast(rootObject));
        logger.debug("Result of QTI Object read from system ID {} is {}", systemId, result);
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
        public JqtiExtensionManager getJqtiExtensionManager() {
            return qtiXmlReader.getJqtiExtensionManager();
        }

        @Override
        public void modelBuildingError(final QtiModelException exception, final Node errorNode) {
            qtiModelBuildingErrors.add(new QtiModelBuildingError(exception, errorNode.getLocalName(),
                    errorNode.getNamespaceURI(), XmlResourceReader.extractLocationInformation(errorNode)));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(qtiXmlReader=" + qtiXmlReader
                + ",inputResourceLocator=" + inputResourceLocator
                + ")";
    }
}
