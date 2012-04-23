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
package uk.ac.ed.ph.qtiworks.mathassess.glue.types;

import uk.ac.ed.ph.qtiworks.mathassess.glue.AsciiMathHelper;
import uk.ac.ed.ph.snuggletex.upconversion.UpConversionFailure;

import java.util.List;

/**
 * Extension of {@link MathsContentValueWrapper} that includes some other bits and pieces
 * of "internal" information that you are free to ignore if you want!
 * <p>
 * You will get one of these coming out of the QTI/CAS layer as a response variable bound to
 * a <tt>mathEntryInteraction</tt>.
 * 
 * @see AsciiMathHelper#createMathsContentFromASCIIMath(String)
 * @see WrapperUtilities
 *
 * @author David McKain
 */
public final class MathsContentInputValueWrapper extends MathsContentValueWrapper {
    
    /** Bracketed Presentation MathML, used to echo the input back to the user */
    private String pMathMLBracketed;
    
    /** Details of any up-conversion failures, null or empty if none occurred. */
    private List<UpConversionFailure> upConversionFailures;


    public String getPMathMLBracketed() {
        return pMathMLBracketed;
    }
    
    public void setPMathMLBracketed(String pMathMLBracketed) {
        this.pMathMLBracketed = pMathMLBracketed;
    }

    public List<UpConversionFailure> getUpConversionFailures() {
        return upConversionFailures;
    }
    
    public void setUpConversionFailures(List<UpConversionFailure> upConversionFailures) {
        this.upConversionFailures = upConversionFailures;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
            + "(asciiMathInput=" + asciiMathInput
            + ",pMathML=" + pMathML
            + ",pMathMLBracketed=" + pMathMLBracketed
            + ",cMathML=" + cMathML
            + ",maximaInput=" + maximaInput
            + ",upConversionFailures=" + upConversionFailures
            + ")";
    }
}
