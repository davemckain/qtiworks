/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils.legacy;

import uk.ac.ed.ph.jqtiplus.QTIConstants;
import uk.ac.ed.ph.jqtiplus.control.ToRefactor;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.jqtiplus.xmlutils.ClassPathHTTPResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.SimpleDOMBuilderHandler;
import uk.ac.ed.ph.jqtiplus.xmlutils.UnifiedXMLResourceResolver;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLReaderException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLSourceLocationInformation;

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
 * 
 * FIXME: I need to add legacy support for QTI 2.0, which would silently convert the whole thing to QTI 2.1
 * and validate against the 2.1 schema instead.
 * 
 * @author  David McKain
 * @version $Revision: 2824 $
 */
@ToRefactor
public final class SupportedXMLReader implements Serializable {
    
    private static final long serialVersionUID = 3647116039217223320L;

    private static final Logger logger = LoggerFactory.getLogger(SupportedXMLReader.class);

    /** Name of the DOM "user object" where SAX {@link Locator} information will be stowed while parsing */
    public static final String LOCATION_INFORMATION_NAME = "locationInformation";
    
    /** 
     * Base path within ClassPath to search in when using the default {@link ClassPathHTTPResourceLocator}
     * instance of {@link #parserResourceLocator}.
     */
    public static final String DEFAULT_PARSER_RESOURCE_CLASSPATH_BASE_PATH = "uk/ac/ed/ph/jqtiplus";
    
    public static final ResourceLocator DEFAULT_PARSER_RESOURCE_LOCATOR = new ClassPathHTTPResourceLocator(DEFAULT_PARSER_RESOURCE_CLASSPATH_BASE_PATH);
    
    public static final Map<String, String> DEFAULT_SCHEMA_MAP;
    static {
        DEFAULT_SCHEMA_MAP = new HashMap<String, String>();
        DEFAULT_SCHEMA_MAP.put(QTIConstants.QTI_21_NAMESPACE, QTIConstants.QTI_21_SCHEMA_LOCATION);
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
     *   located using the given {@link ResourceLocator}
     * @throws XMLReaderException if an unexpected Exception occurred parsing and/or validating the XML
     */
    public XMLReadResult read(String systemId, ResourceLocator inputResourceLocator) {
        ConstraintUtilities.ensureNotNull(systemId, "systemId");
        ConstraintUtilities.ensureNotNull(inputResourceLocator, "inputResourceLocator");
        try {
            return read(new URI(systemId), inputResourceLocator);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("System ID " + systemId + " is not a valid URI");
        }
    }
    
    /**
     * @throws QTIXMLResourceNotFoundException if the XML resource with the given System ID cannot be
     *   located using the given {@link ResourceLocator}
     * @throws XMLReaderException if an unexpected Exception occurred parsing and/or validating the XML
     */
    public XMLReadResult read(URI systemIdUri, ResourceLocator inputResourceLocator) {
        ConstraintUtilities.ensureNotNull(systemIdUri, "systemIdUri");
        ConstraintUtilities.ensureNotNull(inputResourceLocator, "inputResourceLocator");
        
        /* Ensure bean properties are set */
        ConstraintUtilities.ensureNotNull(parserResourceLocator, "parserResourceLocator");
        ConstraintUtilities.ensureNotNull(registeredSchemaMap, "registeredSchemaMap");
        
        String systemId = systemIdUri.toString();
        XMLParseResult xmlParseResult = new XMLParseResult(systemId);
        Document document = null;
        logger.info("Reading QTI XML Resource with System ID {}", systemId);
        try {
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
            document = documentBuilder.newDocument();
            
            /* Configure SAX parser */
            SAXParserFactory spFactory = SAXParserFactory.newInstance();
            spFactory.setNamespaceAware(true);
            spFactory.setValidating(false);
            spFactory.setXIncludeAware(true);
            spFactory.setFeature("http://xml.org/sax/features/validation", false);
            spFactory.setFeature("http://xml.org/sax/features/external-general-entities", true);
            spFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
            spFactory.setFeature("http://xml.org/sax/features/lexical-handler/parameter-entities", false);
            SAXParser saxParser = spFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setEntityResolver(resourceResolver);
            xmlReader.setErrorHandler(xmlParseResult);

            /* Parse input and convert to a DOM containing SAX Locator information */
            InputSource inputSource = new InputSource();
            inputSource.setByteStream(ensureLocateInput(systemIdUri, inputResourceLocator));
            inputSource.setSystemId(systemId);
            
            SimpleDOMBuilderHandler handler = new SimpleDOMBuilderHandler(document);
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
                Element rootElement =  document.getDocumentElement();
                String schemaLocation = rootElement.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");
                List<String> schemaUris = new ArrayList<String>();
                if (schemaLocation.length()!=0) {
                    /* Document declares schema(s) to use. Make sure we support each one */
                    String[] schemaData = schemaLocation.trim().split("\\s+");
                    for (int i=0; i<schemaData.length; i+=2) { /* (ns1 uri1 ns2 uri2 ...) */
                        String schemaNamespace = schemaData[i];
                        String schemaUri = registeredSchemaMap.get(schemaNamespace);
                        if (schemaUri!=null) {
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
                    String schemaNamespace = rootElement.getNamespaceURI();
                    String schemaUri = registeredSchemaMap.get(rootElement.getNamespaceURI());
                    if (schemaUri!=null) {
                        xmlParseResult.getSupportedSchemaNamespaces().add(schemaNamespace);
                        schemaUris.add(schemaUri);
                    }
                    else {
                        logger.error("Schema with namespace " + schemaNamespace + " inferred from that of document element is not registered with this reader");
                        xmlParseResult.getUnsupportedSchemaNamespaces().add(schemaNamespace);
                    }
                }

                logger.info("Will validate {} against schemas {}", systemId, schemaUris);
                Source[] schemaSources = new Source[schemaUris.size()];
                for (int i=0; i<schemaSources.length; i++) {
                    schemaSources[i] = resourceResolver.loadResourceAsSource(schemaUris.get(i));
                }
                SchemaFactory sf =  SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
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
        }
        catch (SAXParseException e) {
            throw new QTIXMLReadException(xmlParseResult);
        }
        catch (Exception e) {
            throw new XMLReaderException("Unexpected Exception during parsing", e);
        }
        logger.info("Result of read is {}", xmlParseResult);
        return new XMLReadResult(document, xmlParseResult);
    }
    
    private static InputStream ensureLocateInput(URI systemIdUri, ResourceLocator inputResourceLocator) {
        logger.debug("Locating resource at {} using locator {}", systemIdUri, inputResourceLocator);
        InputStream inputStream = inputResourceLocator.findResource(systemIdUri);
        if (inputStream==null) {
            throw new QTIXMLResourceNotFoundException(inputResourceLocator, systemIdUri.toString());
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
 
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(parserResourceLocator=" + parserResourceLocator
            + ",schemaValidating=" + schemaValidating
            + ",registeredSchemaMap=" + registeredSchemaMap
            + ")";
    }
}
