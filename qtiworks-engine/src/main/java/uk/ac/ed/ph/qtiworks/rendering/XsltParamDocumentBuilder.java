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
package uk.ac.ed.ph.qtiworks.rendering;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.serialization.NamespacePrefixMappings;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxFiringContext;
import uk.ac.ed.ph.jqtiplus.serialization.SaxEventFirer;
import uk.ac.ed.ph.jqtiplus.serialization.SaxFiringOptions;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;
import uk.ac.ed.ph.jqtiplus.xmlutils.SimpleDomBuilderHandler;

import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public class XsltParamDocumentBuilder {
    
    public interface SaxFirerCallback {
        
        XmlNode[] getQtiNodes();
        
        void fireSaxEvents(SaxEventFirer saxEventFirer, QtiSaxFiringContext saxFiringContext)
                throws SAXException;
    }

    private final SaxFirerCallback saxFirerCallback;
    
    public XsltParamDocumentBuilder(SaxFirerCallback saxFirerCallback) {
        this.saxFirerCallback = saxFirerCallback;
    }
    
    public Document buildDocument() {
        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            final DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            SimpleDomBuilderHandler domBuilderHandler = new SimpleDomBuilderHandler(document);
            
            SaxFiringOptions saxFiringOptions = new SaxFiringOptions();
            saxFiringOptions.setOmitSchemaLocations(true);
            
            /* First, we'll reserve 'xsi' for schema instances */
            NamespacePrefixMappings attrNamespacePrefixMappings = new NamespacePrefixMappings();
            attrNamespacePrefixMappings.registerSchemaInstanceMapping();

            /* Register namespace for parameter XML */
            attrNamespacePrefixMappings.register(XsltParamBuilder.QTIWORKS_NAMESPACE, "qw");
            
            /* Next let each extension package that has been used have a shot */
            XmlNode[] qtiNodes = saxFirerCallback.getQtiNodes();
            Set<JqtiExtensionPackage> usedExtensionPackages = QueryUtils.findExtensionsWithin(qtiNodes);
            attrNamespacePrefixMappings.registerExtensionPrefixMappings(usedExtensionPackages);
            
            /* Register prefixes for each foreign attribute in non-default namespace */
            attrNamespacePrefixMappings.registerForeignAttributeNamespaces(qtiNodes);
            
            SaxEventFirer saxEventFirer = new SaxEventFirer(attrNamespacePrefixMappings,
                    QtiSaxDocumentFirer.createSchemaLocationMap(usedExtensionPackages),
                    domBuilderHandler, saxFiringOptions);
            
            /* Put namespace prefixes in scope */
            saxEventFirer.fireStartDocumentAndPrefixMappings();

            /* Create callback for nodes */
            QtiSaxFiringContext saxFiringContext = new QtiSaxFiringContext(saxEventFirer, attrNamespacePrefixMappings);
            
            /* Now build stuff */
            saxFirerCallback.fireSaxEvents(saxEventFirer, saxFiringContext);
            
            /* Remove namespace prefixes from scope */
            saxEventFirer.fireEndDocumentAndPrefixMappings();
            
            return document;
        }
        catch (Exception e) {
            throw new QtiRenderingException("Unexpected Exception generating DOM parameter", e);
        }
    }
}
