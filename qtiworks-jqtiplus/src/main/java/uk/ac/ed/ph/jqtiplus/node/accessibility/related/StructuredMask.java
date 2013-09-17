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
package uk.ac.ed.ph.jqtiplus.node.accessibility.related;

import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.types.DataTypeBinder;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseUtils;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * FIXME: Document this type
 *
 * <p>TODO : Mark as Experimental.
 * This is a very incomplete and not fully thought out structure
 * in the XML schema.
 *
 * @author Zack Pierce
 */
public class StructuredMask extends AbstractNode implements AccessibilityNode {

    private static final long serialVersionUID = -3598197907209423743L;

    public static final String QTI_CLASS_NAME = "structuredMask";

    private Integer revealOrder;

    private Boolean answerOption;

    public StructuredMask(final RelatedElementInfo parent) {
        super(parent, QTI_CLASS_NAME);
    }

    @Override
    protected void loadChildren(final Element element, final LoadingContext context) {
        this.revealOrder = XmlParseUtils.getChildContentAsInteger(element, "revealOrder");
        final String rawAnswerOption = XmlParseUtils.getChildContent(element, "answerOption");
        this.answerOption = rawAnswerOption != null ? DataTypeBinder.parseBoolean(rawAnswerOption) : true;
    }

    @Override
    protected void fireBodySaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        if (revealOrder != null) {
            qtiSaxDocumentFirer.fireSimpleElement("revealOrder", revealOrder.toString());
        }
        if (answerOption != null) {
            qtiSaxDocumentFirer.fireSimpleElement("answerOption", answerOption.toString());
        }

    }

    public Integer getRevealOrder() {
        return revealOrder;
    }

    public void setRevealOrder(final Integer revealOrder) {
        this.revealOrder = revealOrder;
    }

    /**
     * Defaults to "true" when not specified in the loaded XML.
     * @return
     */
    public Boolean getAnswerOption() {
        return answerOption;
    }

    public void setAnswerOption(final Boolean answerOption) {
        this.answerOption = answerOption;
    }

}
