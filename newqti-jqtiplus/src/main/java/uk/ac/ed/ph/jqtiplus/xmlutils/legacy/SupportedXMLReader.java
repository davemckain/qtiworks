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

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathHttpResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.SimpleDomBuilderHandler;
import uk.ac.ed.ph.jqtiplus.xmlutils.UnifiedXmlResourceResolver;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceReaderException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlSourceLocationInformation;
import uk.ac.ed.ph.jqtiplus.xperimental.ToRefactor;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Provides support for locating, reading, parsing and optionally validating the XML
 * against a configurable set of supported schemas.
 * FIXME: I need to add legacy support for QTI 2.0, which would silently convert the whole thing to QTI 2.1
 * and validate against the 2.1 schema instead.
 * 
 * @author David McKain
 */
@ToRefactor
public final class SupportedXMLReader implements Serializable {

    private static final long serialVersionUID = 3647116039217223320L;

    private static final Logger logger = LoggerFactory.getLogger(SupportedXMLReader.class);

    /** Name of the DOM "user object" where SAX {@link Locator} information will be stowed while parsing */
    public static final String LOCATION_INFORMATION_NAME = "locationInformation";

    /**
     * Base path within ClassPath to search in when using the default {@link ClassPathHttpResourceLocator} instance of {@link #parserResourceLocator}.
     */
    public static final String DEFAULT_PARSER_RESOURCE_CLASSPATH_BASE_PATH = "uk/ac/ed/ph/jqtiplus";

    public static final ResourceLocator DEFAULT_PARSER_RESOURCE_LOCATOR = new ClassPathHttpResourceLocator(DEFAULT_PARSER_RESOURCE_CLASSPATH_BASE_PATH);

    public static final Map<String, String> DEFAULT_SCHEMA_MAP;
    static {
        DEFAULT_SCHEMA_MAP = new HashMap<String, String>();
        DEFAULT_SCHEMA_MAP.put(QtiConstants.QTI_21_NAMESPACE_URI, QtiConstants.QTI_21_SCHEMA_LOCATION);
    }

    /**
     * {@link ResourceLocator} used to locate schema files (and DTD-related entities if used).
     * Default is {@link #DEFAULT_PARSER_RESOURCE_LOCATOR}
     */
    private ResourceLocator parserResourceLocator;

    /**
     * Whether to perform schema validation.
     * Default is true
     */
    private boolean schemaValidating;

    /**
     * Map containing details of each schema registered with this reader. Keys are namespace URI, value is schema URI.
     * Default is {@link #DEFAULT_SCHEMA_MAP}
     */
    private Map<String, String> registeredSchemaMap;

    public SupportedXMLReader() {
        this(true);
    }

    public SupportedXMLReader(final boolean schemaValidating) {
        this.schemaValidating = schemaValidating;
        this.parserResourceLocator = DEFAULT_PARSER_RESOURCE_LOCATOR;
        this.registeredSchemaMap = DEFAULT_SCHEMA_MAP;
    }

    public ResourceLocator getParserResourceLocator() {
        return parserResourceLocator;
    }

    public void setParserResourceLocator(ResourceLocator parserResourceLocator) {
        this.parserResourceLocator = parserResourceLocator;
    }


    public boolean isSchemaValidating() {
        return schemaValidating;
    }

    public void setSchemaValidating(boolean schemaValidating) {
        this.schemaValidating = schemaValidating;
    }


    public Map<String, String> getRegisteredSchemaMap() {
        return registeredSchemaMap;
    }

    public void setRegisteredSchemaMap(Map<String, String> registeredSchemaMap) {
        this.registeredSchemaMap = registeredSchemaMap;
    }

    //--------------------------------------------------

    /**
     * @throws QTIXMLResourceNotFoundException if the XML resource with the given System ID cannot be
     *             located using the given {@link ResourceLocator}
     * @throws XmlResourceReaderException if an unexpected Exception occurred parsing and/or validating the XML
     */
    public XMLReadResult read(String systemId, ResourceLocator inputResourceLocator) {
        ConstraintUtilities.ensureNotNull(systemId, "systemId");
        ConstraintUtilities.ensureNotNull(inputResourceLocator, "inputResourceLocator");
        try {
            return read(new URI(systemId), inputResourceLocator);
        }
        catch (final URISyntaxException e) {
            throw new IllegalArgumentException("System ID " + systemId + " is not a valid URI");
        }
    }

    /**
     * @throws QTIXMLResourceNotFoundException if the XML resource with the given System ID cannot be
     *             located using the given {@link ResourceLocator}
     * @throws XmlResourceReaderException if an unexpected Exception occurred parsing and/or validating the XML
     */
    public XMLReadResult read(URI systemIdUri, ResourceLocator inputResourceLocator) {
        ConstraintUtilities.ensureNotNull(systemIdUri, "systemIdUri");
        ConstraintUtilities.ensureNotNull(inputResourceLocator, "inputResourceLocator");

        /* Ensure bean properties are set */
        ConstraintUtilities.ensureNotNull(parserResourceLocator, "parserResourceLocator");
        ConstraintUtilities.ensureNotNull(registeredSchemaMap, "registeredSchemaMap");

        final String systemId = systemIdUri.toString();
        final XMLParseResult xmlParseResult = new XMLParseResult(systemId);
        Document document = null;
        logger.info("Reading QTI XML Resource with System ID {}", systemId);
        try {
            logger.debug("XML parse of {} starting", systemId);
            final UnifiedXmlResourceResolver resourceResolver = new UnifiedXmlResourceResolver();
            resourceResolver.setResourceLocator(parserResourceLocator);
            resourceResolver.setFailOnMissedEntityResolution(false);
            resourceResolver.setFailOnMissedLRResourceResolution(true);

            /* Create DOM Document. (We'll wire this up for parsing, even though we're now doing a SAX parse) */
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            dbFactory.setXIncludeAware(true);
            dbFactory.setExpandEntityReferences(true);
            final DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(resourceResolver);
            documentBuilder.setErrorHandler(xmlParseResult);
            document = documentBuilder.newDocument();

            /* Configure SAX parser */
            final SAXParserFactory spFactory = SAXParserFactory.newInstance();
            spFactory.setNamespaceAware(true);
            spFactory.setValidating(false);
            spFactory.setXIncludeAware(true);
            spFactory.setFeature("http://xml.org/sax/features/validation", false);
            spFactory.setFeature("http://xml.org/sax/features/external-general-entities", true);
            spFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
            spFactory.setFeature("http://xml.org/sax/features/lexical-handler/parameter-entities", false);
            final SAXParser saxParser = spFactory.newSAXParser();
            final XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setEntityResolver(resourceResolver);
            xmlReader.setErrorHandler(xmlParseResult);

            /* Parse input and convert to a DOM containing SAX Locator information */
            final InputSource inputSource = new InputSource();
            inputSource.setByteStream(ensureLocateInput(systemIdUri, inputResourceLocator));
            inputSource.setSystemId(systemId);

            final SimpleDomBuilderHandler handler = new SimpleDomBuilderHandler(document);
            xmlReader.setContentHandler(handler);
            xmlReader.parse(inputSource); /* Fatal errors will cause SAXParseException */
            if (!xmlParseResult.getErrors().isEmpty()) {
                /* If we had any non-fatal errors, now throw SAXParseException */
                throw xmlParseResult.getErrors().get(0);
            }
            xmlParseResult.setParsed(true);
            logger.debug("XML parse of {} completed successfully", systemId);

            if (schemaValidating) {
                /* Work out which schema(s) to use */
                logger.debug("XML parse of {} completed successfully - deciding which schemas to use", systemId);
                final Element rootElement = document.getDocumentElement();
                final String schemaLocation = rootElement.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");
                final List<String> schemaUris = new ArrayList<String>();
                if (schemaLocation.length() != 0) {
                    /* Document declares schema(s) to use. Make sure we support each one */
                    final String[] schemaData = schemaLocation.trim().split("\\s+");
                    for (int i = 0; i < schemaData.length; i += 2) { /* (ns1 uri1 ns2 uri2 ...) */
                        final String schemaNamespace = schemaData[i];
                        final String schemaUri = registeredSchemaMap.get(schemaNamespace);
                        if (schemaUri != null) {
                            xmlParseResult.getSupportedSchemaNamespaces().add(schemaNamespace);
                            schemaUris.add(schemaUri);
                        }
                        else {
                            logger.error("Schema with namespace " + schemaNamespace + " declared in schemaLocation is not registered with this reader");
                            xmlParseResult.getUnsupportedSchemaNamespaces().add(schemaNamespace);
                        }
                    }
                }
                else {
                    /* No schema declared in the document, so use namespace of root element */
                    final String schemaNamespace = rootElement.getNamespaceURI();
                    final String schemaUri = registeredSchemaMap.get(rootElement.getNamespaceURI());
                    if (schemaUri != null) {
                        xmlParseResult.getSupportedSchemaNamespaces().add(schemaNamespace);
                        schemaUris.add(schemaUri);
                    }
                    else {
                        logger.error("Schema with namespace " + schemaNamespace + " inferred from that of document element is not registered with this reader");
                        xmlParseResult.getUnsupportedSchemaNamespaces().add(schemaNamespace);
                    }
                }

                logger.info("Will validate {} against schemas {}", systemId, schemaUris);
                final Source[] schemaSources = new Source[schemaUris.size()];
                for (int i = 0; i < schemaSources.length; i++) {
                    schemaSources[i] = resourceResolver.loadResourceAsSource(schemaUris.get(i));
                }
                final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                sf.setResourceResolver(resourceResolver);
                sf.setErrorHandler(xmlParseResult);
                final Schema schema = sf.newSchema(schemaSources);

                /* Now validate. Note that we read in the input again, as this will let the parser provide source
                 * information to the schema validator. (I couldn't work out a way of passing source information
                 * when validating from a DOM.)
                 */
                logger.debug("Schema validaton of {} starting", systemId);
                final StreamSource input = new StreamSource(ensureLocateInput(systemIdUri, inputResourceLocator), systemId);
                final Validator validator = schema.newValidator();
                validator.setResourceResolver(resourceResolver);
                validator.setErrorHandler(xmlParseResult);
                validator.validate(input);
                xmlParseResult.setValidated(true);
                logger.debug("Schema validation of {} finished", systemId);
            }
        }
        catch (final SAXParseException e) {
            throw new QTIXMLReadException(xmlParseResult);
        }
        catch (final Exception e) {
            throw new XmlResourceReaderException("Unexpected Exception during parsing", e);
        }
        logger.info("Result of read is {}", xmlParseResult);
        return new XMLReadResult(document, xmlParseResult);
    }

    private static InputStream ensureLocateInput(URI systemIdUri, ResourceLocator inputResourceLocator) {
        logger.debug("Locating resource at {} using locator {}", systemIdUri, inputResourceLocator);
        final InputStream inputStream = inputResourceLocator.findResource(systemIdUri);
        if (inputStream == null) {
            throw new QTIXMLResourceNotFoundException(inputResourceLocator, systemIdUri.toString());
        }
        return inputStream;
    }

    public static XmlSourceLocationInformation extractLocationInformation(Element element) {
        XmlSourceLocationInformation result = null;
        final Object locationData = element.getUserData(LOCATION_INFORMATION_NAME);
        if (locationData != null && locationData instanceof XmlSourceLocationInformation) {
            result = (XmlSourceLocationInformation) locationData;
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
                + "(parserResourceLocator=" + parserResourceLocator
                + ",schemaValidating=" + schemaValidating
                + ",registeredSchemaMap=" + registeredSchemaMap
                + ")";
    }
}
