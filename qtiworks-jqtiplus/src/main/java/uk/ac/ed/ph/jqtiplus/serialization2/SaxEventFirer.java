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
package uk.ac.ed.ph.jqtiplus.serialization2;

import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * FIXME: Document this type
 *
 * TODO: Need to be able to specify how to output MathML elements, i.e. using a prefix, or by changing
 * the default namespace.
 *
 * TODO: Do we really need to keep support for printing default values? It gets stupidly complicated in
 * classes like {@link ItemSessionControl} and I'm not sure I see the value in doing this.
 *
 * @author David McKain
 */
/*
 * Thinking for new version:
 *
 * - takes a NSPrefixMappings with caller's preferences
 * - caller decides whether to set schema locations
 * - caller explicitly fires start and end of document
 * - caller can fire off explicit SAX events if required
 * - caller can request to fire off entire QTI Object when required
 *
 */
public final class SaxEventFirer {

    private static final String SCHEMA_INSTANCE_NS_PREFIX = "xsi";

    private final ContentHandler targetHandler;
    private final SaxFiringOptions saxFiringOptions;

    private final NamespacePrefixMappings namespacePrefixMappings;
    private final Map<String, String> schemaLocations;

    /**
     * Tracks changes in the default namespace.
     *
     * Key is {@link Object} (usually a {@link XmlNode}) where the default namespace change occurs
     * Value is the *previous* namespace URI (which is null before the first element is fired)
     */
    private final Map<Object, String> defaultNamespaceChangeMap;

    /** Current default namespace URI */
    private String currentDefaultNamespaceUri;

    private boolean doneStartDocumentElement;

    public SaxEventFirer(final NamespacePrefixMappings namespacePrefixMappings, final Map<String, String> schemaLocations,
            final ContentHandler targetHandler, final SaxFiringOptions saxFiringOptions) {
        this.targetHandler = targetHandler;
        this.saxFiringOptions = saxFiringOptions;
        this.namespacePrefixMappings = namespacePrefixMappings;
        this.schemaLocations = schemaLocations;

        this.defaultNamespaceChangeMap = new HashMap<Object, String>();
        this.currentDefaultNamespaceUri = null;
        this.doneStartDocumentElement = false;
    }

    //-------------------------------------------------------------------------------

    public void fireStartDocumentAndPrefixMappings() throws SAXException {
        targetHandler.startDocument();

        /* Put namespace prefixes in scope */
        for (final Entry<String, String> entry : namespacePrefixMappings.entrySet()) {
            final String prefix = entry.getKey();
            final String namespaceUri = entry.getValue();
            if (!prefix.equals(SCHEMA_INSTANCE_NS_PREFIX) || !saxFiringOptions.isOmitSchemaLocations()) {
                targetHandler.startPrefixMapping(prefix, namespaceUri);
            }
        }
    }

    public void fireEndDocumentAndPrefixMappings() throws SAXException {
        /* Remove namespace prefixes from scope */
        for (final Entry<String, String> entry : namespacePrefixMappings.entrySet()) {
            final String prefix = entry.getKey();
            if (!prefix.equals(SCHEMA_INSTANCE_NS_PREFIX) || !saxFiringOptions.isOmitSchemaLocations()) {
                targetHandler.endPrefixMapping(prefix);
            }
        }

        targetHandler.endDocument();
    }

    public void fireStartElement(final Object object, final String localName, final String namespaceUri, final AttributesImpl attributes) throws SAXException {
        if (currentDefaultNamespaceUri==null || !currentDefaultNamespaceUri.equals(namespaceUri)) {
            /* We're changing the default namespace, so register a prefix mapping and keep track
             * of this Object so we know to end the mapping later */
            targetHandler.startPrefixMapping("", namespaceUri);
            defaultNamespaceChangeMap.put(object, currentDefaultNamespaceUri);
            currentDefaultNamespaceUri = namespaceUri;
        }

        if (!doneStartDocumentElement && !saxFiringOptions.isOmitSchemaLocations()) {
            if (!namespacePrefixMappings.isNamespaceUriRegistered(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
                throw new IllegalStateException("XML Schema instance NS has not been registered in your mappings");
            }
            /* Add xsi:schemaLocation attribute */
            attributes.addAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation",
                    namespacePrefixMappings.getQName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation"),
                    "CDATA", createSchemaLocationAttributeValue());
        }

        /* Start element */
        targetHandler.startElement(namespaceUri, localName, localName, attributes);
        doneStartDocumentElement = true;
    }

    private String createSchemaLocationAttributeValue() {
        final StringBuilder attrBuilder = new StringBuilder();
        boolean doneFirst = false;
        for (final Entry<String, String> schemaEntry : schemaLocations.entrySet()) {
            if (doneFirst) {
                attrBuilder.append(' ');
            }
            attrBuilder.append(schemaEntry.getKey())
                .append(' ')
                .append(schemaEntry.getValue());
            doneFirst = true;
        }
        return attrBuilder.toString();
    }

    public void fireEndElement(final Object object, final String localName, final String namespaceUri) throws SAXException {
        targetHandler.endElement(namespaceUri, localName, localName);

        /* See if we're ending a default namespace change */
        if (defaultNamespaceChangeMap.containsKey(object)) {
            targetHandler.endPrefixMapping("");
            currentDefaultNamespaceUri = defaultNamespaceChangeMap.get(object);
            defaultNamespaceChangeMap.remove(object);
        }
    }

    public void fireText(final String string) throws SAXException {
        targetHandler.characters(string.toCharArray(), 0, string.length());
    }
}
