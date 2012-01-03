/* $Id:SAXErrorHandler.java 2824 2008-08-01 15:46:17Z davemckain $
 *
 * Copyright (c) 2011, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.io.reading.xml;

import uk.ac.ed.ph.jqtiplus.QTIConstants;
import uk.ac.ed.ph.jqtiplus.xmlutils.ResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLReaderException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.XMLResourceReader;

import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps around {@link XMLResourceReader} to provide specific support for QTI 2.1 and optional extensions.
 * 
 * @author  David McKain
 * @version $Revision: 2824 $
 */
public final class QTIXMLReader implements Serializable {
    
    private static final long serialVersionUID = 1318545084577401859L;

    /** Delegating {@link XMLResourceReader} */
    private final XMLResourceReader xmlResourceReader;
    
    /** Registered extension schemas, which may be null or empty */
    private final Map<String, String> extensionSchemaMap;
    
    public QTIXMLReader() {
        this(null, null);
    }
    
    public QTIXMLReader(ResourceLocator parserResourceLocator) {
        this(parserResourceLocator, null);
    }
    
    public QTIXMLReader(Map<String, String> extensionSchemaMapTemplate) {
        this(null, extensionSchemaMapTemplate);
    }
    
    public QTIXMLReader(ResourceLocator parserResourceLocator, Map<String, String> extensionSchemaMapTemplate) {
        /* Merge extension schema with QTI 2.1 schema */
        Map<String, String> resultingSchemaMapTemplate = new HashMap<String, String>();
        if (extensionSchemaMapTemplate!=null) {
            resultingSchemaMapTemplate.putAll(extensionSchemaMapTemplate);
        }
        resultingSchemaMapTemplate.put(QTIConstants.QTI_21_NAMESPACE, QTIConstants.QTI_21_SCHEMA_LOCATION);
        
        this.extensionSchemaMap = extensionSchemaMapTemplate!=null ? Collections.unmodifiableMap(extensionSchemaMapTemplate) : null;
        this.xmlResourceReader = new XMLResourceReader(parserResourceLocator, resultingSchemaMapTemplate);
    }

    public ResourceLocator getParserResourceLocator() {
        return xmlResourceReader.getParserResourceLocator();
    }
    
    public Map<String, String> getExtensionSchemaMap() {
        return extensionSchemaMap;
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
        return xmlResourceReader.read(systemId, inputResourceLocator, schemaValidating);
    }
    
    /**
     * @throws XMLResourceNotFoundException if the XML resource with the given System ID cannot be
     *   located using the given {@link ResourceLocator}
     * @throws XMLReaderException if an unexpected Exception occurred parsing and/or validating the XML, or
     *   if any of the required schemas could not be located.
     */
    public XMLReadResult read(URI systemIdUri, ResourceLocator inputResourceLocator, boolean schemaValidating)
            throws XMLResourceNotFoundException {
        return xmlResourceReader.read(systemIdUri, inputResourceLocator, schemaValidating);
    }
    
    //--------------------------------------------------
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + hashCode()
            + "(parserResourceLocator=" + getParserResourceLocator()
            + ",extensionSchemaMap=" + extensionSchemaMap
            + ")";
    }
}
