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
package uk.ac.ed.ph.jqtiplus.serialization;

import uk.ac.ed.ph.jqtiplus.ExtensionNamespaceInfo;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.block.ForeignBlock;

import java.util.Set;

import javax.xml.XMLConstants;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public class SaxFiringContext {
    
    private final Set<JqtiExtensionPackage> usedExtensionPackages;
    private final ContentHandler targetHandler;
    private final SerializationOptions serializationOptions;
    private final NamespacePrefixMappings namespacePrefixMappings;
    private boolean doneStartDocumentNode;
    
    SaxFiringContext(ContentHandler targetHandler, SerializationOptions serializationOptions, 
            Set<JqtiExtensionPackage> usedExtensionPackages, NamespacePrefixMappings namespacePrefixMappings) {
        this.targetHandler = targetHandler;
        this.serializationOptions = serializationOptions;
        this.usedExtensionPackages = usedExtensionPackages;
        this.namespacePrefixMappings = namespacePrefixMappings;
        this.doneStartDocumentNode = false;
    }
    
    //-----------------------------------------------
    // Callbacks
    
    public void startSupportedElement(AbstractNode node) throws SAXException {
        /* Build up attributes */
        AttributesImpl xmlAttributes = new AttributesImpl();
        if (!doneStartDocumentNode) {
            if (!serializationOptions.isOmitSchemaLocations()) {
                xmlAttributes.addAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation", 
                        namespacePrefixMappings.getQName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation"),
                        "CDATA", createSchemaLocationAttributeValue());
            }
        }
        for (Attribute<?> attribute : node.getAttributes()) {
            if (attribute.isRequired() || attribute.getValue()!=null) {
                String localName = attribute.getLocalName();
                String namespaceUri = attribute.getNamespaceUri();
                String qName = namespacePrefixMappings.getQName(namespaceUri, localName);
                xmlAttributes.addAttribute(namespaceUri, localName, qName,
                        "CDATA", attribute.valueToString());
            }
        }

        /* Start element */
        targetHandler.startElement(getNodeNamespaceUri(node), node.getLocalName(), node.getLocalName(), xmlAttributes);
        doneStartDocumentNode = true;
    }
    
    public void endSupportedElement(AbstractNode node) throws SAXException {
        targetHandler.endElement(getNodeNamespaceUri(node), node.getLocalName(), node.getLocalName());
    }
    
    private String getNodeNamespaceUri(AbstractNode node) {
        String namespaceUri = "";
        if (node instanceof ForeignBlock) {
            namespaceUri = ((ForeignBlock) node).getNamespaceUri();
        }
        else if (node instanceof uk.ac.ed.ph.jqtiplus.node.content.mathml.Math) {
            namespaceUri = QtiConstants.MATHML_NAMESPACE_URI;
        }
        else {
            /* TODO: If we choose to support APIP or later versions of QTI, we'll
             * need to change output namespace, so this will have to change.
             */
            namespaceUri = QtiConstants.QTI_21_NAMESPACE_URI;
        }
        return namespaceUri;
    }
    
    private String createSchemaLocationAttributeValue() {
        StringBuilder resultBuilder = new StringBuilder();
        
        /* First do QTI 2.1 namespace */
        /* TODO: If we add support for APIP, we'll need to change namespace here */
        resultBuilder.append(QtiConstants.QTI_21_NAMESPACE_URI)
            .append(' ')
            .append(QtiConstants.QTI_21_SCHEMA_LOCATION);
        
        /* Then do each extension */
        for (JqtiExtensionPackage jqtiExtensionPackage : usedExtensionPackages) {
            for (ExtensionNamespaceInfo extensionNamespaceInfo : jqtiExtensionPackage.getNamespaceInfoMap().values()) {
                resultBuilder.append(' ')
                    .append(extensionNamespaceInfo.getNamespaceUri())
                    .append(' ')
                    .append(extensionNamespaceInfo.getSchemaUri());
            }
        }
        
        return resultBuilder.toString();
    }

    public void fireText(String string) throws SAXException {
        targetHandler.characters(string.toCharArray(), 0, string.length());
    }
    
}
