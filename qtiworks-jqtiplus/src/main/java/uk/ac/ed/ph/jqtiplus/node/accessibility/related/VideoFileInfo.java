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

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer;
import uk.ac.ed.ph.jqtiplus.types.DataTypeBinder;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlParseUtils;

import org.joda.time.LocalTime;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * FIXME: Document this type
 *
 * @author Zack Pierce
 */
public class VideoFileInfo extends ObjectFileInfo implements AccessibilityNode {

    public static final String QTI_CLASS_NAME = "videoFileInfo";

    private static final String ELEM_START_CUE = "startCue";

    private static final String ELEM_END_CUE = "endCue";

    private static final long serialVersionUID = 5319846662148524016L;

    private LocalTime startCue;

    private LocalTime endCue;

    public VideoFileInfo(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME, true);
    }

    @Override
    protected void loadChildren(final Element element, final LoadingContext context) {
        super.loadChildren(element, context);
        final String rawStartCue = XmlParseUtils.getChildContent(element, ELEM_START_CUE);
        if (rawStartCue != null) {
            try {
                this.startCue = DataTypeBinder.parseTime(rawStartCue);
            }
            catch (final QtiParseException e) {
                context.modelBuildingError(e, element);
            }
        }
        final String rawEndCue = XmlParseUtils.getChildContent(element, ELEM_END_CUE);
        if (rawEndCue != null) {
            try {
                this.endCue = DataTypeBinder.parseTime(rawEndCue);
            }
            catch (final QtiParseException e) {
                context.modelBuildingError(e, element);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.ed.ph.jqtiplus.node.accessibility.FileInfo#fireBodySaxEvents(uk.ac.ed.ph.jqtiplus.serialization.
     * QtiSaxDocumentFirer)
     */
    @Override
    protected void fireBodySaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        super.fireBodySaxEvents(qtiSaxDocumentFirer);
        if (startCue != null) {
            qtiSaxDocumentFirer.fireSimpleElement("startCue", DataTypeBinder.toString(startCue));
        }
        if (endCue != null) {
            qtiSaxDocumentFirer.fireSimpleElement("endCue", DataTypeBinder.toString(endCue));
        }
    }

    public LocalTime getStartCue() {
        return startCue;
    }

    public void setStartCue(final LocalTime startCue) {
        this.startCue = startCue;
    }

    public LocalTime getEndCue() {
        return endCue;
    }

    public void setEndCue(final LocalTime endCue) {
        this.endCue = endCue;
    }

}
