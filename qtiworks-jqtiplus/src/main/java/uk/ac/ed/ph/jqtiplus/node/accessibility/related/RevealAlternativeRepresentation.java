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
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.AudioFileInfoGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.LabelledStringGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.MarkupFileEmbeddedGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.ObjectFileInfoGroup;
import uk.ac.ed.ph.jqtiplus.group.accessibility.related.VideoFileInfoGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;

import java.util.List;

/**
 * FIXME: Document this type
 *
 * @author Zack Pierce
 */
public class RevealAlternativeRepresentation extends AbstractNode implements AccessibilityNode {

    private static final String ELEM_MARKUP_FILE_INFO = "markupFileInfo";
    private static final String ELEM_EXECUTABLE_FILE_INFO = "executableFileInfo";
    private static final String ELEM_TEXT_STRING = "textString";
    private static final String ELEM_TEXT_FILE_INFO = "textFileInfo";
    private static final String ELEM_GRAPHIC_FILE_INFO = "graphicFileInfo";
    private static final long serialVersionUID = -9063030312836472390L;
    public static final String QTI_CLASS_NAME = "revealAlternativeRepresentation";

    public RevealAlternativeRepresentation(final RelatedElementInfo parent) {
        super(parent, QTI_CLASS_NAME);
        getNodeGroups().add(new AudioFileInfoGroup(this, 1));
        getNodeGroups().add(new VideoFileInfoGroup(this, false));
        getNodeGroups().add(new ObjectFileInfoGroup(this, ELEM_GRAPHIC_FILE_INFO, false));
        getNodeGroups().add(new ObjectFileInfoGroup(this, ELEM_TEXT_FILE_INFO, false));
        getNodeGroups().add(new LabelledStringGroup(this, ELEM_TEXT_STRING, false));
        getNodeGroups().add(new ObjectFileInfoGroup(this, ELEM_MARKUP_FILE_INFO, false));
        getNodeGroups().add(new MarkupFileEmbeddedGroup(this));
        getNodeGroups().add(new ObjectFileInfoGroup(this, ELEM_EXECUTABLE_FILE_INFO, false));
        // TODO - add support for 3rd party namespaced elements added here
    }

    public AudioFileInfo getAudioFileInfo() {
        final List<AudioFileInfo> audioFileInfos = getNodeGroups().getAudioFileInfoGroup().getAudioFileInfos();
        if (audioFileInfos.isEmpty()) {
            return null;
        }
        return audioFileInfos.get(0);
    }

    public void setAudioFileInfo(final AudioFileInfo audioFileInfo) {
        final List<AudioFileInfo> audioFileInfos = getNodeGroups().getAudioFileInfoGroup().getAudioFileInfos();
        audioFileInfos.clear();
        if (audioFileInfo != null) {
            audioFileInfos.add(audioFileInfo);
        }
    }

    public VideoFileInfo getVideoFileInfo() {
        return getNodeGroups().getVideoFileInfoGroup().getVideoFileInfo();
    }

    public void setVideoFileInfo(final VideoFileInfo videoFileInfo) {
        getNodeGroups().getVideoFileInfoGroup().setVideoFileInfo(videoFileInfo);
    }

    public ObjectFileInfo getGraphicFileInfo() {
        return getNodeGroups().getObjectFileInfoGroup(ELEM_GRAPHIC_FILE_INFO).getObjectFileInfo();
    }

    public void setGraphicFileInfo(final ObjectFileInfo objectFileInfo) {
        getNodeGroups().getObjectFileInfoGroup(ELEM_GRAPHIC_FILE_INFO).setObjectFileInfo(objectFileInfo);
    }

    public ObjectFileInfo getTextFileInfo() {
        return getNodeGroups().getObjectFileInfoGroup(ELEM_TEXT_FILE_INFO).getObjectFileInfo();
    }

    public void setTextFileInfo(final ObjectFileInfo objectFileInfo) {
        getNodeGroups().getObjectFileInfoGroup(ELEM_TEXT_FILE_INFO).setObjectFileInfo(objectFileInfo);
    }

    public LabelledString getTextString() {
        return getNodeGroups().getLabelledStringGroup(ELEM_TEXT_STRING).getLabelledString();
    }

    public void setTextString(final LabelledString textString) {
        getNodeGroups().getLabelledStringGroup(ELEM_TEXT_STRING).setLabelledString(textString);
    }

    public ObjectFileInfo getMarkupFileInfo() {
        return getNodeGroups().getObjectFileInfoGroup(ELEM_MARKUP_FILE_INFO).getObjectFileInfo();
    }

    public void setMarkupFileInfo(final ObjectFileInfo objectFileInfo) {
        getNodeGroups().getObjectFileInfoGroup(ELEM_MARKUP_FILE_INFO).setObjectFileInfo(objectFileInfo);
    }

    public MarkupFileEmbedded getMarkupFileEmbedded() {
        return getNodeGroups().getMarkupFileEmbeddedGroup().getMarkupFileEmbedded();
    }

    public void setMarkupFileEmbedded(final MarkupFileEmbedded markupEmbeddedFile) {
        getNodeGroups().getMarkupFileEmbeddedGroup().setMarkupFileEmbedded(markupEmbeddedFile);
    }

    public ObjectFileInfo getExecutableFileInfo() {
        return getNodeGroups().getObjectFileInfoGroup(ELEM_EXECUTABLE_FILE_INFO).getObjectFileInfo();
    }

    public void setExecutableFileInfo(final ObjectFileInfo objectFileInfo) {
        getNodeGroups().getObjectFileInfoGroup(ELEM_EXECUTABLE_FILE_INFO).setObjectFileInfo(objectFileInfo);
    }

}
