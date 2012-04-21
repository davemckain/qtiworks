/* Copyright (c) 2012, University of Edinburgh.
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
package org.qtitools.mathassess.tools.qticasbridge;

/**
 * Represents a failure caused by bad CAS code in the QTI, which should ultimately
 * be fixed by the QTI author.
 *
 * @author David McKain
 */
public final class BadQTICASCodeException extends QTICASAuthoringException {
    
    private static final long serialVersionUID = 5940754810506417594L;
    
    /** Short reason */
    private final String reason;
    
    /** The CAS input deemed bad */
    private final String maximaInput;
    
    /** The raw response from the CAS */
    private final String maximaOutput;

    public BadQTICASCodeException(final String reason, final String maximaInput, final String maximaOutput) {
        super(reason
                + "\nMaxima Input was: '" + maximaInput
                + "'\nMaxima Output was: '" + maximaOutput
                + "'");
        this.reason = reason;
        this.maximaInput = maximaInput;
        this.maximaOutput = maximaOutput;
    }
    
    public String getReason() {
        return reason;
    }

    public String getMaximaInput() {
        return maximaInput;
    }

    public String getMaximaOutput() {
        return maximaOutput;
    }
}
