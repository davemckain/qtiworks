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
package uk.ac.ed.ph.jqtiplus.state.marshalling;

import uk.ac.ed.ph.jqtiplus.internal.util.StringUtilities;
import uk.ac.ed.ph.jqtiplus.state.TestPartSessionState;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Marshals an {@link TestPartSessionState} to/from XML
 *
 * @author David McKain
 */
public final class TestPartSessionStateXmlMarshaller {

    public static Document marshal(final TestPartSessionState testPartSessionState) {
        final DocumentBuilder documentBuilder = XmlMarshallerCore.createNsAwareDocumentBuilder();
        final Document document = documentBuilder.newDocument();
        appendTestSessionState(document, testPartSessionState);
        return document;
    }

    static void appendTestSessionState(final Node documentOrElement, final TestPartSessionState testPartSessionState) {
        final Element element = XmlMarshallerCore.appendElement(documentOrElement, "testPartSessionState");
        element.setAttribute("preConditionFailed", StringUtilities.toTrueFalse(testPartSessionState.isPreConditionFailed()));
        element.setAttribute("entered", StringUtilities.toTrueFalse(testPartSessionState.isEntered()));
        element.setAttribute("ended", StringUtilities.toTrueFalse(testPartSessionState.isEnded()));
        element.setAttribute("exited", StringUtilities.toTrueFalse(testPartSessionState.isExited()));
    }

    //----------------------------------------------

    public static TestPartSessionState unmarshal(final String xmlString) {
        final DocumentBuilder documentBuilder = XmlMarshallerCore.createNsAwareDocumentBuilder();
        Document document;
        try {
            document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
        }
        catch (final Exception e) {
            throw new XmlUnmarshallingException("XML parsing failed", e);
        }
        return unmarshal(document.getDocumentElement());
    }

    public static TestPartSessionState unmarshal(final Element element) {
        XmlMarshallerCore.expectThisElement(element, "testPartSessionState");

        final TestPartSessionState result = new TestPartSessionState();

        /* Extract state attributes */
        result.setPreConditionFailed(XmlMarshallerCore.parseOptionalBooleanAttribute(element, "preConditionFailed", false));
        result.setEntered(XmlMarshallerCore.parseOptionalBooleanAttribute(element, "entered", false));
        result.setEnded(XmlMarshallerCore.parseOptionalBooleanAttribute(element, "ended", false));
        result.setExited(XmlMarshallerCore.parseOptionalBooleanAttribute(element, "exited", false));

        return result;
    }
}
