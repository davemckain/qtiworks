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
 * @author David McKain
 */
public final class QtiXmlReader implements Serializable {

    private static final long serialVersionUID = 1318545084577401859L;

    /** Delegating {@link XMLResourceReader} */
    private final XMLResourceReader xmlResourceReader;

    /** Registered extension schemas, which may be null or empty */
    private final Map<String, String> extensionSchemaMap;

    public QtiXmlReader() {
        this(null, null);
    }

    public QtiXmlReader(ResourceLocator parserResourceLocator) {
        this(parserResourceLocator, null);
    }

    public QtiXmlReader(Map<String, String> extensionSchemaMapTemplate) {
        this(null, extensionSchemaMapTemplate);
    }

    public QtiXmlReader(ResourceLocator parserResourceLocator, Map<String, String> extensionSchemaMapTemplate) {
        /* Merge extension schema with QTI 2.1 schema */
        final Map<String, String> resultingSchemaMapTemplate = new HashMap<String, String>();
        if (extensionSchemaMapTemplate != null) {
            resultingSchemaMapTemplate.putAll(extensionSchemaMapTemplate);
        }
        resultingSchemaMapTemplate.put(QTIConstants.QTI_21_NAMESPACE, QTIConstants.QTI_21_SCHEMA_LOCATION);

        this.extensionSchemaMap = extensionSchemaMapTemplate != null ? Collections.unmodifiableMap(extensionSchemaMapTemplate) : null;
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
     *             located using the given {@link ResourceLocator}
     * @throws XMLReaderException if an unexpected Exception occurred parsing and/or validating the XML, or
     *             if any of the required schemas could not be located.
     */
    public XMLReadResult read(String systemId, ResourceLocator inputResourceLocator, boolean schemaValidating)
            throws XMLResourceNotFoundException {
        return xmlResourceReader.read(systemId, inputResourceLocator, schemaValidating);
    }

    /**
     * @throws XMLResourceNotFoundException if the XML resource with the given System ID cannot be
     *             located using the given {@link ResourceLocator}
     * @throws XMLReaderException if an unexpected Exception occurred parsing and/or validating the XML, or
     *             if any of the required schemas could not be located.
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
