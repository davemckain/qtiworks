/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Helper class that makes it easy to parse XML and optionally schema validate it against
 * a set of prescribed schemas. 
 * <p>
 * Schemas are *always* loaded via the prescribed {@link #getParserResourceLocator()}, so
 * the use of this class must ensure they are made available for loading in this way. Suggested
 * usage is to make a local cache of the schemas and use a {@link ClassPathHTTPResourceLocator}
 * or similar to load them. A default implementation called {@link #DEFAULT_PARSER_RESOURCE_LOCATOR}
 * is provided that uses {@link ClassPathHTTPResourceLocator} to search under the path
 * <code>uk/ac/ed/ph/jqtiplus</code>.
 * <p>
 * The XML parsing process performs a SAX parse followed by a DOM tree build, filling the resulting tree with
 * SAX {@link Locator} information, which makes later error reporting richer.
 * 
 * @see XMLReadResult
 * 
 * @author  David McKain
 * @version $Revision: 2824 $
 */
public final class XMLResourceReader {
    
    private static final Logger logger = LoggerFactory.getLogger(XMLResourceReader.class);

    /** Name of the DOM "user object" where SAX {@link Locator} information will be stowed while parsing */
    public static final String LOCATION_INFORMATION_NAME = "locationInformation";
    
    /** 
     * Base path within ClassPath to search in when using the default {@link ClassPathHTTPResourceLocator}
     * instance of {@link #parserResourceLocator}.
     */
    public static final String DEFAULT_PARSER_RESOURCE_CLASSPATH_BASE_PATH = "uk/ac/ed/ph/jqtiplus";
    
    /** 
     * Default {@link ResourceLocator} that will be used to locate schemas (and DTDs). This searches within
     * the ClassPath under {@link #DEFAULT_PARSER_RESOURCE_CLASSPATH_BASE_PATH}. 
     */
    public static final ResourceLocator DEFAULT_PARSER_RESOURCE_LOCATOR = new ClassPathHTTPResourceLocator(DEFAULT_PARSER_RESOURCE_CLASSPATH_BASE_PATH);
    
    //--------------------------------------------------    
    
    /** 
     * {@link ResourceLocator} used to locate schema files (and DTD-related entities if used).
     * Default is {@link #DEFAULT_PARSER_RESOURCE_LOCATOR}
     */
    private final ResourceLocator parserResourceLocator;
    
    /** 
     * Map containing details of each schema registered with this reader. Keys are namespace URI, value is schema URI.
     * <p>
     * This may be null or empty, to indicate that there are no registered schemas (and therefore schema validation won't work!).
     * <p>
     * The default is null,
     */
    private final Map<String, String> registeredSchemaMap;
    
    public XMLResourceReader() {
        this(null, null);
    }
    
    public XMLResourceReader(ResourceLocator parserResourceLocator) {
        this(parserResourceLocator, null);
    }
    
    public XMLResourceReader(Map<String, String> registeredSchemaMap) {
        this(null, registeredSchemaMap);
    }
    
    public XMLResourceReader(ResourceLocator parserResourceLocator, Map<String, String> registeredSchemaMapTemplate) {
        this.parserResourceLocator = parserResourceLocator!=null ? parserResourceLocator : DEFAULT_PARSER_RESOURCE_LOCATOR;
        this.registeredSchemaMap = registeredSchemaMapTemplate!=null ? Collections.unmodifiableMap(registeredSchemaMapTemplate) : null;
    }

    public ResourceLocator getParserResourceLocator() {
        return parserResourceLocator;
    }
    
    public Map<String, String> getRegisteredSchemaMap() {
        return registeredSchemaMap;
    }
    
    //--------------------------------------------------

    /**
     * @throws XMLResourceNotFoundException if the XML resource with the given System ID cannot be
     *   located using the given {@link ResourceLocator}
     * @throws XMLReaderException if an unexpected Exception occurred parsing and/or validating the XML, or
     *   if any of the required schemas could not be located.
     */
    public XMLReadResult read(String systemId, ResourceLocator inputResourceLocator, boolean schemaValidating)
            throws XMLResourceNotFoundException {
        ConstraintUtilities.ensureNotNull(systemId, "systemId");
        ConstraintUtilities.ensureNotNull(inputResourceLocator, "inputResourceLocator");
        try {
            return read(new URI(systemId), inputResourceLocator, schemaValidating);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("System ID " + systemId + " is not a valid URI");
        }
    }
    
    /**
     * @throws XMLResourceNotFoundException if the XML resource with the given System ID cannot be
     *   located using the given {@link ResourceLocator}
     * @throws XMLReaderException if an unexpected Exception occurred parsing and/or validating the XML, or
     *   if any of the required schemas could not be located.
     */
    public XMLReadResult read(URI systemIdUri, ResourceLocator inputResourceLocator, boolean schemaValidating)
            throws XMLResourceNotFoundException {
        ConstraintUtilities.ensureNotNull(systemIdUri, "systemIdUri");
        ConstraintUtilities.ensureNotNull(inputResourceLocator, "inputResourceLocator");
        
        try {
            logger.info("read({}, {}, {}) starting", new Object[] { systemIdUri, inputResourceLocator, schemaValidating });
            XMLReadResult result = doRead(systemIdUri, inputResourceLocator, schemaValidating);
            logger.info("read({}, {}, {}) => {}", new Object[] { systemIdUri, inputResourceLocator, schemaValidating, result });
            return result;
        }
        catch (XMLResourceNotFoundException e) {
            logger.info("read({}, {}, {}) => {}", new Object[] { systemIdUri, inputResourceLocator, schemaValidating, e });
            throw e;
        }
        catch (Exception e) {
            logger.info("read({}, {}, {}) => UNEXPECTED EXCEPTION {}", new Object[] { systemIdUri, inputResourceLocator, schemaValidating, e });
            throw new XMLReaderException("Unexpected Exception parsing or validating XML at system ID " + systemIdUri, e);
        }
    }
    
    private XMLReadResult doRead(URI systemIdUri, ResourceLocator inputResourceLocator, boolean schemaValidating)
            throws XMLResourceNotFoundException, ParserConfigurationException, SAXException, IOException {
        String systemId = systemIdUri.toString();
        XMLParseResult xmlParseResult = new XMLParseResult(systemIdUri);
        
        logger.debug("XML parse of {} starting", systemId);
        UnifiedXMLResourceResolver resourceResolver = new UnifiedXMLResourceResolver();
        resourceResolver.setResourceLocator(parserResourceLocator);
        resourceResolver.setFailOnMissedEntityResolution(false);
        resourceResolver.setFailOnMissedLRResourceResolution(true);
        
        /* Create DOM Document. (We'll wire this up for parsing, even though we're now doing a SAX parse) */
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        dbFactory.setXIncludeAware(true);
        dbFactory.setExpandEntityReferences(true);
        DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
        documentBuilder.setEntityResolver(resourceResolver);
        documentBuilder.setErrorHandler(xmlParseResult);
        Document document = documentBuilder.newDocument();
        
        /* Configure SAX parser */
        SAXParserFactory spFactory = SAXParserFactory.newInstance();
        spFactory.setNamespaceAware(true);
        spFactory.setValidating(false);
        spFactory.setXIncludeAware(true);
        spFactory.setFeature("http://xml.org/sax/features/validation", false);
        spFactory.setFeature("http://xml.org/sax/features/external-general-entities", true);
        spFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
        spFactory.setFeature("http://xml.org/sax/features/lexical-handler/parameter-entities", false);
        XMLReader xmlReader = spFactory.newSAXParser().getXMLReader();
        xmlReader.setEntityResolver(resourceResolver);
        xmlReader.setErrorHandler(xmlParseResult);

        /* Parse input and convert to a DOM containing SAX Locator information */
        InputSource inputSource = new InputSource();
        inputSource.setByteStream(ensureLocateInput(systemIdUri, inputResourceLocator));
        inputSource.setSystemId(systemId);
        
        SimpleDOMBuilderHandler handler = new SimpleDOMBuilderHandler(document);
        xmlReader.setContentHandler(handler);
        try {
            xmlReader.parse(inputSource); /* Fatal errors will cause SAXParseException */
        }
        catch (SAXParseException e) {
            /* Fatal parsing error */
        }
        /* We'll consider successful parsing to be no errors or fatal errors */
        boolean parsed = xmlParseResult.getFatalErrors().isEmpty() && xmlParseResult.getErrors().isEmpty();
        xmlParseResult.setParsed(parsed);
        logger.debug("XML parse of {} success? ", parsed);
        
        if (parsed && schemaValidating) {
            /* Work out which schema(s) to use */
            logger.debug("XML parse of {} completed successfully - deciding which schemas to use", systemId);
            Element rootElement =  document.getDocumentElement();
            String schemaLocation = rootElement.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");
            List<String> schemaUris = new ArrayList<String>();
            if (schemaLocation.length()!=0) {
                /* Document declares schema(s) to use. Make sure we support each one */
                String[] schemaData = schemaLocation.trim().split("\\s+");
                for (int i=0; i<schemaData.length; i+=2) { /* (ns1 uri1 ns2 uri2 ...) */
                    String schemaNamespaceUri = schemaData[i];
                    String schemaUri = getRegisteredSchemaLocation(schemaNamespaceUri);
                    if (schemaUri!=null) {
                        xmlParseResult.getSupportedSchemaNamespaces().add(schemaNamespaceUri);
                        schemaUris.add(schemaUri);
                    }
                    else {
                        logger.error("Schema with namespace " + schemaNamespaceUri + " declared in schemaLocation is not registered with this reader");
                        xmlParseResult.getUnsupportedSchemaNamespaces().add(schemaNamespaceUri);
                    }
                }
            }
            else {
                /* No schema declared in the document, so use namespace of root element */
                String schemaNamespaceUri = rootElement.getNamespaceURI();
                String schemaUri = getRegisteredSchemaLocation(rootElement.getNamespaceURI());
                if (schemaUri!=null) {
                    xmlParseResult.getSupportedSchemaNamespaces().add(schemaNamespaceUri);
                    schemaUris.add(schemaUri);
                }
                else {
                    logger.error("Schema with namespace " + schemaNamespaceUri + " inferred from that of document element is not registered with this reader");
                    xmlParseResult.getUnsupportedSchemaNamespaces().add(schemaNamespaceUri);
                }
            }
            
            /* Validate (if some supported schemas were declared) */
            if (!schemaUris.isEmpty()) {
                logger.info("Will validate {} against schemas {}", systemId, schemaUris);
                Source[] schemaSources = new Source[schemaUris.size()];
                for (int i=0; i<schemaSources.length; i++) {
                    Source schemaSource = resourceResolver.loadResourceAsSource(schemaUris.get(i));
                    if (schemaSource==null) {
                        String message = "parserResourceLocator failed to locate schema with URI " + schemaUris.get(i);
                        logger.error(message);
                        throw new XMLReaderException(message);
                    }
                    schemaSources[i] = schemaSource;
                }
                SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                sf.setResourceResolver(resourceResolver);
                sf.setErrorHandler(xmlParseResult);
                Schema schema = sf.newSchema(schemaSources);

                /* Now validate. Note that we read in the input again, as this will let the parser provide source
                 * information to the schema validator. (I couldn't work out a way of passing source information
                 * when validating from a DOM.)
                 */
                logger.debug("Schema validaton of {} starting", systemId);
                StreamSource input = new StreamSource(ensureLocateInput(systemIdUri, inputResourceLocator), systemId);
                Validator validator = schema.newValidator();
                validator.setResourceResolver(resourceResolver);
                validator.setErrorHandler(xmlParseResult);
                validator.validate(input);
                xmlParseResult.setValidated(true);
                logger.debug("Schema validation of {} finished", systemId);
            }
            else {
                logger.warn("No supported schemas declared, so no validation will be performed");
            }
        }
        
        return new XMLReadResult(parsed ? document : null, xmlParseResult);
    }
    
    private static InputStream ensureLocateInput(URI systemIdUri, ResourceLocator inputResourceLocator)
            throws XMLResourceNotFoundException {
        logger.debug("Attempting to locate XML resource at {} using locator {}", systemIdUri, inputResourceLocator);
        InputStream inputStream = inputResourceLocator.findResource(systemIdUri);
        if (inputStream==null) {
            logger.warn("Could not locate and open XML at system ID {} using locator {}", systemIdUri, inputResourceLocator);
            throw new XMLResourceNotFoundException(inputResourceLocator, systemIdUri.toString());
        }
        return inputStream;
    }
    
    public static XMLSourceLocationInformation extractLocationInformation(Element element) {
        XMLSourceLocationInformation result = null;
        Object locationData = element.getUserData(LOCATION_INFORMATION_NAME);
        if (locationData!=null && locationData instanceof XMLSourceLocationInformation) {
            result = (XMLSourceLocationInformation) locationData;
        }
        return result;
    }
    
    private String getRegisteredSchemaLocation(String namespaceUri) {
        return registeredSchemaMap!=null ? registeredSchemaMap.get(namespaceUri) : null;
    }
 
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(parserResourceLocator=" + parserResourceLocator
            + ",registeredSchemaMap=" + registeredSchemaMap
            + ")";
    }
}
