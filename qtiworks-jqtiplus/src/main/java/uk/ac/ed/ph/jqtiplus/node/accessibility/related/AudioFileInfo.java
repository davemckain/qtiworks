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
 * Container for the accessibility content contained in an external audio file.
 *
 * The start and durations are supplied so that one audio file can be used
 * to support more than one accessibility element.
 *
 * @author Zack Pierce
 */
public class AudioFileInfo extends ObjectFileInfo implements ContentLinkIdentifierBearer, AccessibilityNode {

    private static final long serialVersionUID = -6758112561134526415L;

    public static final String QTI_CLASS_NAME = "audioFileInfo";

    private static final String ELEM_START_TIME = "startTime";
    private static final String ELEM_DURATION = "duration";

    private VoiceType voiceType;

    private VoiceSpeed voiceSpeed;

    private LocalTime startTime;

    /**
     * A duration, represented as a time-of-day to match the (odd) XML representation
     */
    private LocalTime duration;

    public AudioFileInfo(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME, true);
    }

    public AudioFileInfo(final QtiNode parent, final String localName) {
        super(parent, localName, true);
    }

    @Override
    protected void loadChildren(final Element element, final LoadingContext context) {
        super.loadChildren(element, context);
        final String rawVoiceType = XmlParseUtils.getChildContent(element, VoiceType.QTI_CLASS_NAME);
        try {
            this.voiceType = rawVoiceType != null ? VoiceType.parseVoiceType(rawVoiceType) : VoiceType.SYNTHETIC;
        }
        catch (final QtiParseException e) {
            context.modelBuildingError(e, element);
        }
        final String rawVoiceSpeed = XmlParseUtils.getChildContent(element, VoiceSpeed.QTI_CLASS_NAME);
        try {
            this.voiceSpeed = rawVoiceSpeed != null ? VoiceSpeed.parseVoiceSpeed(rawVoiceSpeed) : VoiceSpeed.STANDARD;
        }
        catch (final QtiParseException e) {
            context.modelBuildingError(e, element);
        }

        final String rawStartTime = XmlParseUtils.getChildContent(element, ELEM_START_TIME);
        if (rawStartTime != null) {
            try {
                this.startTime = DataTypeBinder.parseTime(rawStartTime);
            }
            catch (final QtiParseException e) {
                context.modelBuildingError(e, element);
            }
        }
        final String rawDuration = XmlParseUtils.getChildContent(element, ELEM_DURATION);
        if (rawDuration != null) {
            try {
                this.duration = DataTypeBinder.parseTime(rawDuration);
            }
            catch (final QtiParseException e) {
                context.modelBuildingError(e, element);
            }
        }
    }

    /* (non-Javadoc)
     * @see uk.ac.ed.ph.jqtiplus.node.accessibility.FileInfo#fireBodySaxEvents(uk.ac.ed.ph.jqtiplus.serialization.QtiSaxDocumentFirer)
     */
    @Override
    protected void fireBodySaxEvents(final QtiSaxDocumentFirer qtiSaxDocumentFirer) throws SAXException {
        super.fireBodySaxEvents(qtiSaxDocumentFirer);
        qtiSaxDocumentFirer.fireSimpleElement(VoiceType.QTI_CLASS_NAME, this.voiceType.toQtiString());
        qtiSaxDocumentFirer.fireSimpleElement(VoiceSpeed.QTI_CLASS_NAME, this.voiceSpeed.toQtiString());
        if (startTime != null) {
            qtiSaxDocumentFirer.fireSimpleElement("startTime", DataTypeBinder.toString(startTime));
        }
        if (duration != null) {
            qtiSaxDocumentFirer.fireSimpleElement("duration", DataTypeBinder.toString(duration));
        }
    }

    public VoiceType getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(final VoiceType voiceType) {
        this.voiceType = voiceType;
    }

    public VoiceSpeed getVoiceSpeed() {
        return voiceSpeed;
    }

    public void setVoiceSpeed(final VoiceSpeed voiceSpeed) {
        this.voiceSpeed = voiceSpeed;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(final LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(final LocalTime duration) {
        this.duration = duration;
    }

}
