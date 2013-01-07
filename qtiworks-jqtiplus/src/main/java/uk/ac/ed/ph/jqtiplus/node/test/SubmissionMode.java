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
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.test;

import uk.ac.ed.ph.jqtiplus.exception.QtiParseException;
import uk.ac.ed.ph.jqtiplus.types.Stringifiable;

import java.util.HashMap;
import java.util.Map;

/**
 * The submission mode determines when the candidate's responses are submitted for response processing.
 * <p>
 * individual - requires the candidate to submit their responses on an item-by-item basis.
 * <p>
 * simultaneous - the candidate's responses are all submitted together at the end of the testPart.
 * 
 * @author Jiri Kajaba
 */
public enum SubmissionMode implements Stringifiable {
    /**
     * Requires the candidate to submit their responses on an item-by-item basis.
     */
    INDIVIDUAL("individual"),

    /**
     * The candidate's responses are all submitted together at the end of the testPart.
     */
    SIMULTANEOUS("simultaneous");

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "submissionMode";

    private static Map<String, SubmissionMode> submissionModes;

    static {
        submissionModes = new HashMap<String, SubmissionMode>();

        for (final SubmissionMode submissionMode : SubmissionMode.values()) {
            submissionModes.put(submissionMode.submissionMode, submissionMode);
        }
    }

    private String submissionMode;

    private SubmissionMode(String submissionMode) {
        this.submissionMode = submissionMode;
    }

    @Override
    public String toQtiString() {
        return submissionMode;
    }

    /**
     * Returns parsed <code>SubmissionMode</code> from given <code>String</code>.
     * 
     * @param submissionMode <code>String</code> representation of <code>SubmissionMode</code>
     * @return parsed <code>SubmissionMode</code> from given <code>String</code>
     * @throws QtiParseException if given <code>String</code> is not valid <code>SubmissionMode</code>
     */
    public static SubmissionMode parseSubmissionMode(String submissionMode) {
        final SubmissionMode result = submissionModes.get(submissionMode);

        if (result == null) {
            throw new QtiParseException("Invalid " + QTI_CLASS_NAME + " '" + submissionMode + "'.");
        }

        return result;
    }
}
