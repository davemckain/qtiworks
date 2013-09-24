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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.xmlutils;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.types.DataTypeBinder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides some utility methods to assist in parsing Xml Elements.
 *
 * @author Zack Pierce
 */
public final class XmlParseUtils {

	/**
	 * Finds the first child element with the specified local name within the
	 * supplied parent node. If a matching child node exists, returns the
	 * concatenated direct textual contents of that child element.
	 *
	 * @param parent
	 * @param childLocalName
	 * @return null if no such child found
	 */
	public static String getChildContent(final Element parent,
			final String childLocalName) {
		final NodeList childNodes = parent.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			final Node childNode = childNodes.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (childLocalName.equals(childNode.getLocalName())) {
				return getDirectTextualContent((Element) childNode);
			}
		}
		return null;
	}

	/**
	 * Produces the concatenated result of all direct TEXT_NODE
	 * children of the element.
	 * Text found within child/descendant elements or attributes is not included!
	 * @param element
	 * @return
	 */
	public static String getDirectTextualContent(final Element element) {
		Assert.notNull(element);
		final StringBuilder stringBuilder = new StringBuilder();
		final NodeList children = element.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			final Node kid = children.item(j);
			if (kid.getNodeType() == Node.TEXT_NODE) {
				stringBuilder.append(kid.getNodeValue());
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Finds the first child element with the specified local name within the
	 * supplied parent node. If a matching child node exists, returns an Integer
	 * parsed from the concatenated textual contents of that child element.
	 *
	 * @param parent
	 * @param childLocalName
	 * @return
	 * @throws QtiParseException
	 */
	public static Integer getChildContentAsInteger(final Element parent,
			final String childLocalName) throws QtiParseException {
		final String childContent = getChildContent(parent, childLocalName);
		if (childContent == null) {
			return null;
		}
		return DataTypeBinder.parseInteger(childContent);
	}

}
