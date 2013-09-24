/* Copyright (c) 2012-2013, University of Edinburgh.
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

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.serialization.SaxFiringOptions;
import uk.ac.ed.ph.jqtiplus.xmlutils.SimpleDomBuilderHandler;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Helper to build DOM Documents containing a mixture of JQTI elements and
 * custom stuff.
 *
 * @author David McKain
 */
public final class XsltParamDocumentBuilder {

    public static interface SaxFirerCallback {

        List<? extends QtiNode> getQtiNodes();

        void fireSaxEvents(QtiSaxDocumentFirer qtiSaxDocumentFirer)
                throws SAXException;
    }

    private final JqtiExtensionManager jqtiExtensionManager;
    private final SaxFirerCallback saxFirerCallback;

    public XsltParamDocumentBuilder(final JqtiExtensionManager jqtiExtensionManager, final SaxFirerCallback saxFirerCallback) {
        this.jqtiExtensionManager = jqtiExtensionManager;
        this.saxFirerCallback = saxFirerCallback;
    }

    public Document buildDocument() {
        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            final DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            final Document document = documentBuilder.newDocument();
            final SimpleDomBuilderHandler domBuilderHandler = new SimpleDomBuilderHandler(document);

            /* Create QTI SAX firer with suitable options */
            final SaxFiringOptions saxFiringOptions = new SaxFiringOptions(true);
            final QtiSaxDocumentFirer qtiSaxDocumentFirer = new QtiSaxDocumentFirer(jqtiExtensionManager, domBuilderHandler, saxFiringOptions);

            /* Register namespace for parameter XML */
            qtiSaxDocumentFirer.requirePrefixMapping(XsltParamBuilder.QTIWORKS_NAMESPACE, XsltParamBuilder.QTIWORKS_NAMESPACE_PREFIX);

            /* Next let each extension package that has been used have a shot */
            final List<? extends QtiNode> qtiNodes = saxFirerCallback.getQtiNodes();
            for (final QtiNode qtiNode : qtiNodes) {
                qtiSaxDocumentFirer.prepareFor(qtiNode);
            }

            /* Start document and put prefixes in scope */
            qtiSaxDocumentFirer.fireStartDocumentAndPrefixMappings();

            /* Get callback to do its stuff */
            saxFirerCallback.fireSaxEvents(qtiSaxDocumentFirer);

            /* Remove namespace prefixes from scope and end document */
            qtiSaxDocumentFirer.fireEndDocumentAndPrefixMappings();

            return document;
        }
        catch (final Exception e) {
            throw new QtiWorksRenderingException("Unexpected Exception generating DOM parameter", e);
        }
    }
}
