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

import uk.ac.ed.ph.qtiworks.domain.entities.CandidateEventNotification;
import uk.ac.ed.ph.qtiworks.utils.XmlUtilities;


import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This little helper class converts various types of JQTIPlus Objects into DOM elements
 *
 * Usage: Not thread-safe
 *
 * @author  David McKain
 */
public final class XsltParamBuilder {

    /** Internal namespace used in QTIWorks Rendering XSLT that we'll use for certain custom elements/attrs */
    public static final String QTIWORKS_NAMESPACE = "http://www.ph.ed.ac.uk/qtiworks";

    /** Prefix to use for QTIWorks Rendering XSLT that we'll use for certain custom elements/attrs */
    public static final String QTIWORKS_NAMESPACE_PREFIX = "qw";

    private final DocumentBuilder documentBuilder;

    public XsltParamBuilder() {
        this.documentBuilder = XmlUtilities.createNsAwareDocumentBuilder();
    }

    public List<Node> notificationsToElements(final List<CandidateEventNotification> notifications) {
        final ArrayList<Node> result = new ArrayList<Node>();
        final Document doc = documentBuilder.newDocument();
        for (final CandidateEventNotification notification : notifications) {
            final Element element = doc.createElementNS(QTIWORKS_NAMESPACE, "notification");
            element.setAttribute("type", notification.getNotificationType().toString());
            element.setAttribute("level", notification.getNotificationLevel().toString());
            final String attrLocalName = notification.getAttributeLocalName();
            if (attrLocalName!=null) {
                element.setAttribute("attrLocalName", attrLocalName);
                element.setAttribute("attrNamespaceUri", notification.getAttributeNamespaceUri());
            }
            final String nodeQtiClassName = notification.getNodeQtiClassName();
            if (nodeQtiClassName!=null) {
                element.setAttribute("nodeQtiClassName", nodeQtiClassName);
            }
            final Integer columnNumber = notification.getColumnNumber();
            if (columnNumber!=null) {
                element.setAttribute("columnNumber", columnNumber.toString());
            }
            final Integer lineNumber = notification.getLineNumber();
            if (lineNumber!=null) {
                element.setAttribute("lineNumber", lineNumber.toString());
            }
            final String systemId = notification.getSystemId();
            if (systemId!=null) {
                element.setAttribute("systemId", systemId);
            }
            element.appendChild(doc.createTextNode(notification.getMessage()));
            result.add(element);
         }
        return result;
    }
}
