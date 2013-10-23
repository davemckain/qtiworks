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
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;

import java.util.List;

/**
 * @author Zack Pierce
 */
public class TactileFile extends AbstractNode implements AccessibilityNode {

    private static final long serialVersionUID = 5540725777117688624L;

    public static final String QTI_CLASS_NAME = "tactileFile";

    public static final String ELEM_TACTILE_AUDIO_FILE = "tactileAudioFile";

    public static final String ELEM_TACTILE_AUDIO_TEXT = "tactileAudioText";

    public static final String ELEM_TACTILE_BRAILLE_TEXT = "tactileBrailleText";

    public TactileFile(final RelatedElementInfo parent) {
        super(parent, QTI_CLASS_NAME);
        getNodeGroups().add(new AudioFileInfoGroup(this, ELEM_TACTILE_AUDIO_FILE, 1));
        getNodeGroups().add(new LabelledStringGroup(this, ELEM_TACTILE_AUDIO_TEXT, true));
        getNodeGroups().add(new LabelledStringGroup(this, ELEM_TACTILE_BRAILLE_TEXT, false));
    }

    public AudioFileInfo getTactileAudioFile() {
        final List<AudioFileInfo> audioFileInfos = getNodeGroups().getAudioFileInfoGroup(ELEM_TACTILE_AUDIO_FILE)
                .getAudioFileInfos();
        if (audioFileInfos.isEmpty()) {
            return null;
        }
        return audioFileInfos.get(0);
    }

    public void setTactileAudioFile(final AudioFileInfo audioFileInfo) {
        final List<AudioFileInfo> audioFileInfos = getNodeGroups().getAudioFileInfoGroup(ELEM_TACTILE_AUDIO_FILE)
                .getAudioFileInfos();
        audioFileInfos.clear();
        if (audioFileInfo != null) {
            audioFileInfos.add(audioFileInfo);
        }
    }

    public LabelledString getTactileAudioText() {
        return getNodeGroups().getTactileAudioTextGroup().getLabelledString();
    }

    public void setTactileAudioText(final LabelledString tactileAudioText) {
        getNodeGroups().getTactileAudioTextGroup().setLabelledString(tactileAudioText);
    }

    public LabelledString getTactileBrailleText() {
        return getNodeGroups().getTactileBrailleTextGroup().getLabelledString();
    }

    public void setTactileBrailleText(final LabelledString tactileBrailleText) {
        getNodeGroups().getTactileBrailleTextGroup().setLabelledString(tactileBrailleText);
    }

}
