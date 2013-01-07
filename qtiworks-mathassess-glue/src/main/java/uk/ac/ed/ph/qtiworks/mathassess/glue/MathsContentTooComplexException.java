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
package uk.ac.ed.ph.qtiworks.mathassess.glue;

import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MathsContentOutputValueWrapper;

/**
 * This Exception is thrown if a MathsContent value encoded inside a 
 * {@link MathsContentOutputValueWrapper} does not have the required amount of semantics
 * to be handled in the appropriate way.
 * <p>
 * Normally this will be thrown if some MathML output cannot be up-converted to being
 * Maxima input again.
 * <p>
 * This one's a bit awkward as the underlying problem could be:
 * <ul>
 *   <li>The up-conversion process isn't clever enough.</li>
 *   <li>The content was authored directly and is genuinely outside the scope of what we support.</li>
 *   <li>The content arose as part of a Maxima call and has now gone outside the scope of what we support.</li>
 * <ul>
 *
 * @author David McKain
 */
public final class MathsContentTooComplexException extends Exception {

    private static final long serialVersionUID = -3121152232236067772L;
    
    private final MathsContentOutputValueWrapper valueWrapper;
    
    public MathsContentTooComplexException(final MathsContentOutputValueWrapper valueWrapper) {
        super("MathsContent based on PMathML "
                + valueWrapper.getPMathML()
                + " cannot be up-converted into Maxima input format... probably too complex");
        this.valueWrapper = valueWrapper;
    }

    public MathsContentOutputValueWrapper getMathsContentValueWrapper() {
        return valueWrapper;
    }
}
