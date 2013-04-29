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
package uk.ac.ed.ph.jqtiplus.serialization;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.xmlutils.SimpleDomBuilderHandler;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.QtiSerializationException;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltSerializationOptions;
import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetManager;

import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Simple entry point into the JQTI serialization logic, serializing JQTI
 * Objects as XML in a number of simple ways.
 *
 * @author David McKain
 */
public final class QtiSerializer {

    private final JqtiExtensionManager jqtiExtensionManager;

    public QtiSerializer(final JqtiExtensionManager jqtiExtensionManager) {
        this.jqtiExtensionManager = jqtiExtensionManager;
    }

    //----------------------------------------------------

    public String serializeJqtiObject(final QtiNode jqtiObject) {
        final StringWriter resultWriter = new StringWriter();
        serializeJqtiObject(jqtiObject, new StreamResult(resultWriter));
        return resultWriter.toString();
    }

    public String serializeJqtiObject(final QtiNode jqtiObject, final SaxFiringOptions saxFiringOptions,
            final XsltSerializationOptions xsltSerializationOptions) {
        final StringWriter resultWriter = new StringWriter();
        serializeJqtiObject(jqtiObject, new StreamResult(resultWriter), saxFiringOptions, xsltSerializationOptions);
        return resultWriter.toString();
    }

    public void serializeJqtiObject(final QtiNode jqtiObject, final OutputStream outputStream) {
        serializeJqtiObject(jqtiObject, new StreamResult(outputStream));
    }

    public void serializeJqtiObject(final QtiNode jqtiObject, final StreamResult result) {
        final XsltSerializationOptions xsltSerializationOptions = new XsltSerializationOptions();
        xsltSerializationOptions.setIndenting(true);

        serializeJqtiObject(jqtiObject, result, new SaxFiringOptions(), xsltSerializationOptions);
    }

    public void serializeJqtiObject(final QtiNode jqtiObject, final StreamResult result,
            final SaxFiringOptions saxFiringOptions, final XsltSerializationOptions xsltSerializationOptions) {
        final TransformerHandler serializerHandler = XsltStylesheetManager.createSerializerHandler(xsltSerializationOptions);
        serializerHandler.setResult(result);
        final QtiSaxDocumentFirer qtiSaxDocumentFirer = new QtiSaxDocumentFirer(jqtiExtensionManager, serializerHandler, saxFiringOptions);
        try {
            qtiSaxDocumentFirer.fireSaxDocument(jqtiObject);
        }
        catch (final SAXException e) {
            throw new QtiSerializationException("Unexpected Exception firing QTI Object SAX events at serializer stylesheet", e);
        }
    }

    public Document serializeJqtiObjectAsDocument(final QtiNode jqtiObject, final SaxFiringOptions saxFiringOptions) {
        /* Create DOM Document */
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = dbFactory.newDocumentBuilder();
        }
        catch (final ParserConfigurationException e) {
            throw new QtiSerializationException("Could not create namespace aware DocumentBuilder");
        }
        final Document document = documentBuilder.newDocument();
        final SimpleDomBuilderHandler domBuilderHandler = new SimpleDomBuilderHandler(document);

        final QtiSaxDocumentFirer qtiSaxDocumentFirer = new QtiSaxDocumentFirer(jqtiExtensionManager, domBuilderHandler, saxFiringOptions);
        try {
            qtiSaxDocumentFirer.fireSaxDocument(jqtiObject);
        }
        catch (final SAXException e) {
            throw new QtiSerializationException("Unexpected Exception firing QTI Object SAX events at serializer stylesheet", e);
        }
        return document;
    }
}
