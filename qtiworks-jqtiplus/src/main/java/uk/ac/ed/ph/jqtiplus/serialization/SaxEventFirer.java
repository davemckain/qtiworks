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
import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;
import uk.ac.ed.ph.jqtiplus.utils.ForeignNamespaceSummary;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;

import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * TODO: Need to be able to specify how to output MathML elements, i.e. using a prefix, or by changing
 * the default namespace.
 * 
 * TODO: Do we really need to keep support for printing default values? It gets stupidly complicated in
 * classes like {@link ItemSessionControl} and I'm not sure I see the value in doing this.
 * 
 * FIXME: Document this type
 *
 * @author David McKain
 */
public final class SaxEventFirer {
    
    private static final String SCHEMA_INSTANCE_NS_PREFIX = "xsi";
    
    private final JqtiExtensionManager jqtiExtensionManager;
    
    public SaxEventFirer(JqtiExtensionManager jqtiExtensionManager) {
        this.jqtiExtensionManager = jqtiExtensionManager;
    }
    
    public void fireSaxDocument(XmlNode node, ContentHandler targetHandler, SerializationOptions serializationOptions) throws SAXException {
        /* Choose global NS prefix mappings */
        NamespacePrefixMappings namespacePrefixMappings = new NamespacePrefixMappings();
        
        /* First, we'll reserve 'xsi' for schema instances */
        namespacePrefixMappings.register(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, SCHEMA_INSTANCE_NS_PREFIX);
        
        /* Next let each extension package that has been used have a shot */
        Set<JqtiExtensionPackage> usedExtensionPackages = QueryUtils.findExtensionsWithin(node);
        for (JqtiExtensionPackage jqtiExtensionPackage : jqtiExtensionManager.getExtensionPackages()) {
            if (usedExtensionPackages.contains(jqtiExtensionPackage)) {
                for (Entry<String, ExtensionNamespaceInfo> entry : jqtiExtensionPackage.getNamespaceInfoMap().entrySet()) {
                    String namespaceUri = entry.getKey();
                    ExtensionNamespaceInfo extensionNamespaceInfo = entry.getValue();
                    String defaultPrefix = extensionNamespaceInfo.getDefaultPrefix();
                    String actualPrefix = namespacePrefixMappings.makeUniquePrefix(defaultPrefix);
                    namespacePrefixMappings.register(namespaceUri, actualPrefix);
                }
            }
        }
        
        /* Register prefixes for each foreign attribute in non-default namespace */
        ForeignNamespaceSummary foreignNamespaces = QueryUtils.findForeignNamespaces(node);
        for (String attributeNamespaceUri : foreignNamespaces.getAttributeNamespaceUris()) {
            /* TODO: Maybe allow some more control over this choice of prefix? */
            String resultingPrefix = namespacePrefixMappings.makeUniquePrefix("ns");
            namespacePrefixMappings.register(attributeNamespaceUri, resultingPrefix);
        }
        
        /* Fire off the start of the document */
        targetHandler.startDocument();
        
        /* Put namespace prefixes in scope */
        for (Entry<String, String> entry : namespacePrefixMappings.entrySet()) {
            String prefix = entry.getKey();
            String namespaceUri = entry.getValue();
            if (!prefix.equals(SCHEMA_INSTANCE_NS_PREFIX) || !serializationOptions.isOmitSchemaLocations()) {
                targetHandler.startPrefixMapping(prefix, namespaceUri);
            }
        }

        /* Create callback for nodes */
        SaxFiringContext saxFiringContext = new SaxFiringContext(targetHandler, serializationOptions,
                usedExtensionPackages, namespacePrefixMappings);
        
        /* Get document Node to fire itself off */
        node.fireSaxEvents(saxFiringContext);
        
        /* Remove namespace prefixes from scope */
        for (Entry<String, String> entry : namespacePrefixMappings.entrySet()) {
            String prefix = entry.getKey();
            if (!prefix.equals(SCHEMA_INSTANCE_NS_PREFIX) || !serializationOptions.isOmitSchemaLocations()) {
                targetHandler.endPrefixMapping(prefix);
            }
        }
        
        /* Finish off the document */
        targetHandler.endDocument();
    }
}
