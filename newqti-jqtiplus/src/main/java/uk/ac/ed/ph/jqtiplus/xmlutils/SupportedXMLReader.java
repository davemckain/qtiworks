/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;


import uk.ac.ed.ph.jqtiplus.QTIConstants;
import uk.ac.ed.ph.jqtiplus.internal.util.ConstraintUtilities;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * Provides support for locating, reading, parsing and optionally validating the XML
 * against a configurable set of supported schemas.
 * 
 * FIXME: I need to add legacy support for QTI 2.0, which would silently convert the whole thing to QTI 2.1
 * and validate against the 2.1 schema instead.
 * 
 * @author  David McKain
 * @version $Revision: 2766 $
 */
public final class SupportedXMLReader {
    
    private static final Logger logger = LoggerFactory.getLogger(SupportedXMLReader.class);
    
    /** {@link ResourceLocator} used to locate schema files (and DTD-related entities if used) */
    private final ResourceLocator parserResourceLocator;
    
    /** Whether to perform schema validation */
    private final boolean schemaValidating;
    
    /** Map containing details of each schema registered with this reader. Keys are namespace URI, value is schema URI */
    private final Map<String, String> registeredSchemaMap;
    
    public SupportedXMLReader(final ResourceLocator parserResourceLocator, final boolean schemaValidating) {
        this.parserResourceLocator = parserResourceLocator;
        this.schemaValidating = schemaValidating;
        this.registeredSchemaMap = new HashMap<String, String>();
        this.registeredSchemaMap.put(QTIConstants.QTI_21_NAMESPACE, QTIConstants.QTI_21_SCHEMA_LOCATION);
    }

    public ResourceLocator getParserResourceLocator() {
        return parserResourceLocator;
    }
    
    public boolean isSchemaValidating() {
        return schemaValidating;
    }
    
    public Map<String, String> getRegisteredSchemaMap() {
        return registeredSchemaMap;
    }
    
    //--------------------------------------------------

    /**
     * @throws QTIXMLResourceNotFoundException if the XML resource with the given System ID cannot be
     *   located using the given {@link ResourceLocator}
     * @throws QTIXMLException if an unexpected Exception occurred parsing and/or validating the XML
     */
    public XMLReadResult read(String systemId, ResourceLocator inputResourceLocator) {
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
     * @throws QTIXMLException if an unexpected Exception occurred parsing and/or validating the XML
     */
    public XMLReadResult read(URI systemIdUri, ResourceLocator inputResourceLocator) {
        logger.debug("Locating resource at {} using locator {}", systemIdUri, inputResourceLocator);
        InputStream inputStream = inputResourceLocator.findResource(systemIdUri);
        if (inputStream==null) {
            throw new QTIXMLResourceNotFoundException(inputResourceLocator, systemIdUri.toString());
        }
        return read(inputStream, systemIdUri);
    }
    
    private XMLReadResult read(InputStream inputStream, URI systemIdUri) {
        InputSource source = new InputSource(inputStream);
        source.setSystemId(systemIdUri.toString());
        return read(source);
    }
    
    /**
     * @throws QTIXMLReadException if a parsing and/or schema validation error occurred when reading the XML
     * @throws QTIXMLException if an unexpected Exception occurred parsing and/or validating the XML
     */
    public XMLReadResult read(InputSource inputSource) {
        ConstraintUtilities.ensureNotNull(parserResourceLocator, "parserResourceLocator");
        ConstraintUtilities.ensureNotNull(inputSource, "inputSource");
        
        String systemId = inputSource.getSystemId();
        XMLParseResult xmlParseResult = new XMLParseResult(systemId);
        Document document = null;
        logger.info("Reading QTI XML Resource with System ID {}", systemId);
        try {
            logger.debug("XML parse of {} starting", systemId);
            UnifiedXMLResourceResolver resourceResolver = new UnifiedXMLResourceResolver();
            resourceResolver.setResourceLocator(parserResourceLocator);
            resourceResolver.setFailOnMissedEntityResolution(false);
            resourceResolver.setFailOnMissedLRResourceResolution(true);
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            dbFactory.setXIncludeAware(true);
            dbFactory.setExpandEntityReferences(true);
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(resourceResolver);
            documentBuilder.setErrorHandler(xmlParseResult);
            document = documentBuilder.parse(inputSource); /* Fatal errors will cause SAXParseException */
            if (!xmlParseResult.getErrors().isEmpty()) {
                /* If we had any non-fatal errors, now throw SAXParseException */ 
                throw xmlParseResult.getErrors().get(0);
            }
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

                /* Now validate */
                logger.debug("Schema validaton of {} starting", systemId);
                DOMSource input = new DOMSource(document, systemId);
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
            throw new QTIXMLException("Unexpected Exception during parsing", e);
        }
        return new XMLReadResult(document, xmlParseResult);
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
