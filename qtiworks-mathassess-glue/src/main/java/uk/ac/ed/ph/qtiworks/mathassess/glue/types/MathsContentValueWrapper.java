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
package uk.ac.ed.ph.qtiworks.mathassess.glue.types;

import uk.ac.ed.ph.qtiworks.mathassess.glue.AsciiMathHelper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess;

/**
 * Wrapper representing a <tt>MathsContent</tt> record value.
 * <p>
 * You will VERY RARELY have to construct one of these directly. The only situation where you
 * should find yourself doing this is if the record variable has been authored within the QTI
 * XML itself, which is considered bad practice any probably ought to be deprecated!
 * <p>
 * In all other cases, you will be given an instance of a subclass of this (that contains extra bits
 * of information you can safely ignore) by appropriate methods in the glue layer.
 *
 * @see QtiMaximaProcess#executeMathOutput(String, boolean)
 * @see QtiMaximaProcess#queryMaximaVariable(String, Class)
 * @see AsciiMathHelper#createMathsContentFromAsciiMath(String)
 *
 * @see MathsContentInputValueWrapper
 * @see MathsContentOutputValueWrapper
 *
 * @author David McKain
 */
public class MathsContentValueWrapper implements ValueWrapper {

    /**
     * Raw ASCIIMath input (called <tt>CandidateInput</tt> in our spec document).
     * <p>
     * This should be non-null if this MathsContent value was produced by an ASCIIMathML
     * input.
     *
     * @see AsciiMathHelper
     */
    protected String asciiMathInput;

    /**
     * Tidied up Presentation MathML (called <tt>PMathML</tt> in our spec document).
     * <p>
     * This {@link MathsContentValueWrapper} is considered to hold a null value if and only if
     * this field is null.
     */
    protected String pMathML;

    /**
     * Content MathML form obtained by up-conversion process (called <tt>CMath</tt> in our spec
     * document).
     * <p>
     * This is not strictly required anywhere.
     */
    protected String cMathML;

    /**
     * Up-converted form suitable for Maxima input.
     * <p>
     * You MUST fill this in when creating a new wrapper
     * for the {@link MathsContentSource#STUDENT_MATH_ENTRY_INTERACTION} case.
     * <p>
     * This will ALWAYS be filled in when returning MathsContent from {@link QtiMaximaProcess}
     * in the {@link MathsContentSource#CAS_OUTPUT} case UNLESS the up-conversion process
     * fails, in which case it will be null.
     */
    protected String maximaInput;


    public final String getAsciiMathInput() {
        return asciiMathInput;
    }

    public final void setAsciiMathInput(final String asciiMathInput) {
        this.asciiMathInput = asciiMathInput;
    }


    public final String getPMathML() {
        return pMathML;
    }

    public final void setPMathML(final String pmathML) {
        this.pMathML = pmathML;
    }


    public String getCMathML() {
        return cMathML;
    }

    public void setCMathML(final String cmathML) {
        this.cMathML = cmathML;
    }


    public final String getMaximaInput() {
        return maximaInput;
    }

    public final void setMaximaInput(final String maximaInput) {
        this.maximaInput = maximaInput;
    }


    @Override
    public final ValueCardinality getCardinality() {
        return ValueCardinality.MATHS_CONTENT;
    }

    @Override
    public final boolean isNull() {
        return pMathML==null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
            + "(asciiMathInput=" + asciiMathInput
            + ",pMathML=" + pMathML
            + ",cMathML=" + cMathML
            + ",maximaInput=" + maximaInput
            + ")";
    }
}
