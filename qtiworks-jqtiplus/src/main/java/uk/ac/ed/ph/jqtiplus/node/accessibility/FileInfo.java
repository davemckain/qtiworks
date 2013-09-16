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

import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseUtils;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Base class for data objects containing a fileHref and a mimeType
 *
 * @author Zack Pierce
 */
public abstract class FileInfo extends AbstractNode implements AccessibilityNode {

    private static final long serialVersionUID = -1524033363125950739L;

    private String fileHref;

    public FileInfo(final QtiNode parent, final String qtiClassName, final boolean mimeTypeRequired) {
        super(parent, qtiClassName);
        getAttributes().add(new StringAttribute(this, "mimeType", mimeTypeRequired));
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.node.AbstractNode#loadChildren(org.w3c.dom.Element,
     * uk.ac.ed.ph.jqtiplus.node.LoadingContext)
     */
    @Override
    protected void loadChildren(final Element element, final LoadingContext context) {
        fileHref = XmlParseUtils.getChildContent(element, "fileHref");
    }

    @Override
    protected void fireBodySaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        qtiSaxDocumentFirer.fireSimpleElement("fileHref", fileHref);
    }

    public String getFileHref() {
        return fileHref;
    }

    public void setFileHref(final String fileHref) {
        this.fileHref = fileHref;
    }

    public String getMimeType() {
        return ((StringAttribute) getAttributes().get("mimeType")).getValue();
    }
}
