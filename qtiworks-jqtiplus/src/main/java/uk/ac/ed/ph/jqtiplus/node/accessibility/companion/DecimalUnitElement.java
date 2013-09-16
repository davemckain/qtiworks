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
package uk.ac.ed.ph.jqtiplus.node.accessibility.companion;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.types.DataTypeBinder;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

import java.math.BigDecimal;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Abstract base class for an element containing a decimal value in its textual
 * child contents, and a "unit" attribute
 *
 * @author Zack Pierce
 */
public abstract class DecimalUnitElement<V extends Enum<V> & Stringifiable> extends AbstractNode implements
        AccessibilityNode {

    private static final long serialVersionUID = -90607178471563924L;

    private static final String UNIT_QTI_CLASS_NAME = "unit";

    private V unit;
    private BigDecimal decimal;

    public DecimalUnitElement(final QtiNode parent, final String qtiClassName) {
        super(parent, qtiClassName);
    }

    public abstract V parseUnit(String unitAttributeValue);

    public V getUnit() {
        return unit;
    }

    public void setUnit(final V unit) {
        this.unit = unit;
    }

    public BigDecimal getDecimal() {
        return decimal;
    }

    public void setDecimal(final BigDecimal decimal) {
        this.decimal = decimal;
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#loadAttributes(org.w3c.dom.Element ,
     * uk.ac.ed.ph.jqtiplus.node.LoadingContext)
     */
    @Override
    protected void loadAttributes(final Element element, final LoadingContext context) {
        final NamedNodeMap sourceAttributes = element.getAttributes();
        for (int i = 0; i < sourceAttributes.getLength(); i++) {
            final Node attributeNode = sourceAttributes.item(i);
            if (UNIT_QTI_CLASS_NAME.equals(attributeNode.getLocalName())) {
                try {
                    this.unit = parseUnit(attributeNode.getNodeValue().trim());
                }
                catch (final QtiParseException e) {
                    context.modelBuildingError(e, attributeNode);
                }
                break;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#loadChildren(org.w3c.dom.Element,
     * uk.ac.ed.ph.jqtiplus.node.LoadingContext)
     */
    @Override
    protected void loadChildren(final Element element, final LoadingContext context) {
        try {
            this.decimal = DataTypeBinder.parseBigDecimal(element.getTextContent());
        }
        catch (final QtiParseException e) {
            context.modelBuildingError(e, element);
        }
    }

    /* (non-Javadoc)
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#fireBodySaxEvents(uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer)
     */
    @Override
    protected void fireBodySaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        qtiSaxDocumentFirer.fireText(this.decimal.toPlainString());
    }

}
