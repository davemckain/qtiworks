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
package uk.ac.ed.ph.jqtiplus.node.accessibility.link;

import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseUtils;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * FIXME: Document this type
 *
 * @author Zack Pierce
 */
public class CharacterStringLink extends AbstractNode implements StringLink, AccessibilityNode {

    private static final long serialVersionUID = 4994298956888418294L;

    public static final String QTI_CLASS_NAME = "characterStringLink";

    private Integer startCharacter;

    private Integer stopCharacter;

    public CharacterStringLink(final TextLink parent) {
        super(parent, QTI_CLASS_NAME);
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#loadChildren(org.w3c.dom.Element,
     * uk.ac.ed.ph.jqtiplus.node.LoadingContext)
     */
    @Override
    protected void loadChildren(final Element element, final LoadingContext context) {
        startCharacter = XmlParseUtils.getChildContentAsInteger(element, "startCharacter");
        stopCharacter = XmlParseUtils.getChildContentAsInteger(element, "stopCharacter");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * uk.ac.ed.ph.jqtiplus.node.AbstractNode#fireBodySaxEvents(uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer)
     */
    @Override
    protected void fireBodySaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        qtiSaxDocumentFirer.fireSimpleElement("startCharacter", this.startCharacter.toString());
        qtiSaxDocumentFirer.fireSimpleElement("stopCharacter", this.stopCharacter.toString());
    }

    /**
     * @return the starting character position in the linked text.
     */
    public Integer getStartCharacter() {
        return startCharacter;
    }

    /**
     * @param startCharacter the starting character position in the linked text.
     */
    public void setStartCharacter(final Integer startCharacter) {
        this.startCharacter = startCharacter;
    }

    /**
     * @return the last included character position in the linked text.
     */
    public Integer getStopCharacter() {
        return stopCharacter;
    }

    /**
     * @param stopCharacter the last included character position in the linked text.
     */
    public void setStopCharacter(final Integer stopCharacter) {
        this.stopCharacter = stopCharacter;
    }

}
