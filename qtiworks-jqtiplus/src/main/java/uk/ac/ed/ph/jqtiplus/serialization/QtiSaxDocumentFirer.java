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
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Fires a QTI {@link XmlNode} as a standalone SAX Document
 * 
 * TODO-LATER: Would be nice to be able to specify how to output MathML elements, i.e. using a
 * prefix instead of changing the default namespace.
 * 
 * TODO: Do we really need to keep support for printing default values? It gets stupidly complicated in
 * classes like {@link ItemSessionControl} and I'm not sure I see the value in doing this.
 *
 * @author David McKain
 */
public final class QtiSaxDocumentFirer {
    
    private final ContentHandler targetHandler;
    private final SaxFiringOptions saxFiringOptions;
    
    public QtiSaxDocumentFirer(ContentHandler targetHandler, SaxFiringOptions saxFiringOptions) {
        this.targetHandler = targetHandler;
        this.saxFiringOptions = saxFiringOptions;
    }
    
    public void fireSaxDocument(XmlNode node) throws SAXException {
        /* First, we'll reserve 'xsi' for schema instances */
        NamespacePrefixMappings attrNamespacePrefixMappings = new NamespacePrefixMappings();
        attrNamespacePrefixMappings.registerSchemaInstanceMapping();
        
        /* Next let each extension package that has been used have a shot */
        Set<JqtiExtensionPackage> usedExtensionPackages = QueryUtils.findExtensionsWithin(node);
        attrNamespacePrefixMappings.registerExtensionPrefixMappings(usedExtensionPackages);
        
        /* Register prefixes for each foreign attribute in non-default namespace */
        attrNamespacePrefixMappings.registerForeignAttributeNamespaces(node);
        
        SaxEventFirer saxEventFirer = new SaxEventFirer(attrNamespacePrefixMappings,
                createSchemaLocationMap(usedExtensionPackages), targetHandler, saxFiringOptions);
        
        /* Fire off the start of the document */
        targetHandler.startDocument();
        
        /* Put namespace prefixes in scope */
        saxEventFirer.fireStartDocumentAndPrefixMappings();

        /* Create callback for nodes */
        SaxFiringContext saxFiringContext = new SaxFiringContext(saxEventFirer, attrNamespacePrefixMappings);
        
        /* Get document Node to fire itself off */
        node.fireSaxEvents(saxFiringContext);
        
        /* Remove namespace prefixes from scope */
        saxEventFirer.fireEndDocumentAndPrefixMappings();
        
        /* Finish off the document */
        targetHandler.endDocument();
    }
    
    public static Map<String, String> createSchemaLocationMap(Set<JqtiExtensionPackage> usedExtensionPackages) {
        Map<String, String> result = new HashMap<String, String>();
        
        /* First do QTI 2.1 namespace */
        /* TODO: If we add support for APIP, we'll need to change namespace here */
        result.put(QtiConstants.QTI_21_NAMESPACE_URI, QtiConstants.QTI_21_SCHEMA_LOCATION);
        
        /* Then do each extension */
        for (JqtiExtensionPackage jqtiExtensionPackage : usedExtensionPackages) {
            for (ExtensionNamespaceInfo extensionNamespaceInfo : jqtiExtensionPackage.getNamespaceInfoMap().values()) {
                result.put(extensionNamespaceInfo.getNamespaceUri(), extensionNamespaceInfo.getSchemaUri());
            }
        }
        return result;
    }
}
