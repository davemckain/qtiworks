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

import uk.ac.ed.ph.jqtiplus.QtiConstants;
import uk.ac.ed.ph.jqtiplus.attribute.Attribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.block.ForeignElement;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Callback interface used by {@link AbstractNode}s to fire off their SAX events.
 *
 * @author David McKain
 */
public final class QtiSaxFiringContext {
    
    private final SaxEventFirer saxEventFirer;
    private final NamespacePrefixMappings attributeNamespacePrefixMappings;
    
    public QtiSaxFiringContext(SaxEventFirer saxEventFirer, NamespacePrefixMappings attributeNamespacePrefixMappings) {
        this.saxEventFirer = saxEventFirer;
        this.attributeNamespacePrefixMappings = attributeNamespacePrefixMappings;
    }
    
    //-----------------------------------------------
    // Callbacks
    
    public void fireStartQtiElement(AbstractNode node) throws SAXException {
        /* Build up attributes */
        AttributesImpl xmlAttributes = new AttributesImpl();
        for (Attribute<?> attribute : node.getAttributes()) {
            if (attribute.isRequired() || attribute.getValue()!=null) {
                String localName = attribute.getLocalName();
                String namespaceUri = attribute.getNamespaceUri();
                String qName = attributeNamespacePrefixMappings.getQName(namespaceUri, localName);
                xmlAttributes.addAttribute(namespaceUri, localName, qName,
                        "CDATA", attribute.valueToString());
            }
        }

        /* Decide on element Name */
        String elementNamespaceUri = getNodeNamespaceUri(node);

        /* Fire element */
        saxEventFirer.fireStartElement(node, node.getLocalName(), elementNamespaceUri, xmlAttributes);
    }

    public void fireText(String string) throws SAXException {
        saxEventFirer.fireText(string);
    }
    
    public void fireEndQtiElement(AbstractNode node) throws SAXException {
        saxEventFirer.fireEndElement(node, node.getLocalName(), getNodeNamespaceUri(node));
    }
    
    private String getNodeNamespaceUri(AbstractNode node) {
        String namespaceUri = "";
        if (node instanceof ForeignElement) {
            namespaceUri = ((ForeignElement) node).getNamespaceUri();
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
}
