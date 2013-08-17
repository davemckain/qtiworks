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
package uk.ac.ed.ph.jqtiplus.node.accessibility;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.types.DataTypeBinder;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Convenience base class elements that only contain a single integer
 * in their textual children.
 *
 * @author Zack Pierce
 */
public class SingleIntegerElement extends AbstractNode {

    private static final long serialVersionUID = -2288768722969592203L;

    private Integer value;

    public SingleIntegerElement(final QtiNode parent, final String qtiClassName) {
        super(parent, qtiClassName);
    }

    @Override
    protected void loadChildren(final Element element, final LoadingContext context) {
        try {
            this.setValue(DataTypeBinder.parseInteger(element.getTextContent()));
        }
        catch (final QtiParseException ex) {
            this.setValue(null);
            context.modelBuildingError(ex, element);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#loadAttributes(org.w3c.dom.Element,
     * uk.ac.ed.ph.jqtiplus.node.LoadingContext)
     */
    @Override
    protected void loadAttributes(final Element element, final LoadingContext context) {
        /* No attributes to load */
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#hasChildNodes()
     */
    @Override
    public boolean hasChildNodes() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * uk.ac.ed.ph.jqtiplus.node.AbstractNode#fireBodySaxEvents(uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer)
     */
    @Override
    protected void fireBodySaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        qtiSaxDocumentFirer.fireText(Integer.toString(getValue()));
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(final Integer value) {
        this.value = value;
    }

}
