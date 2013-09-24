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
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

import java.util.HashMap;
import java.util.Map;

/**
 * FIXME: Document this type
 *
 * @author Zack Pierce
 */
public enum VoiceSpeed implements Stringifiable {

    SLOW("Slow"),

    STANDARD("Standard"),

    FAST("Fast");

    private String voiceSpeed;

    public static final String QTI_CLASS_NAME = "voiceSpeed";

    private static final Map<String, VoiceSpeed> nameToEnum;

    static {
        final HashMap<String, VoiceSpeed> names = new HashMap<String, VoiceSpeed>();

        for (final VoiceSpeed voiceSpeed : VoiceSpeed.values()) {
            names.put(voiceSpeed.voiceSpeed, voiceSpeed);
        }

        nameToEnum = ObjectUtilities.unmodifiableMap(names);
    }

    private VoiceSpeed(final String voiceSpeed) {
        this.voiceSpeed = voiceSpeed;
    }

    @Override
    public String toQtiString() {
        return voiceSpeed;
    }

    public static VoiceSpeed parseVoiceSpeed(final String voiceSpeed) throws QtiParseException {
        final VoiceSpeed result = nameToEnum.get(voiceSpeed);
        if (result == null) {
            throw new QtiParseException("Invalid " + QTI_CLASS_NAME + " '" + voiceSpeed + "'.");
        }
        return result;
    }
}
