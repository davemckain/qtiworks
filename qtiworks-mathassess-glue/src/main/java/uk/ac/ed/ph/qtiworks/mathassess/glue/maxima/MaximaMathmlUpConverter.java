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
package uk.ac.ed.ph.qtiworks.mathassess.glue.maxima;

import uk.ac.ed.ph.snuggletex.upconversion.MathMLUpConverter;
import uk.ac.ed.ph.snuggletex.utilities.SimpleStylesheetCache;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetCache;

import org.w3c.dom.Document;

/**
 * Simple helper class that up-converts the raw MathML obtained from Maxima,
 * creating a much nicer and richer annotated MathML element.
 * <p>
 * (This just bootstraps over the raw {@link MaximaMathmlFixer} and the SnuggleTeX
 * up-conversion logic to do everything in one convenient step.)
 *
 * @author David McKain
 */
public final class MaximaMathmlUpConverter {
    
    /** Used to fix up the MathML coming out of Maxima */
    private final MaximaMathmlFixer mathmlFixer;
    
    /** SnuggleTeX MathML up-converter */
    private final MathMLUpConverter mathMLUpConverter;
    
    public MaximaMathmlUpConverter() {
        this(new SimpleStylesheetCache());
    }
    
    public MaximaMathmlUpConverter(StylesheetCache stylesheetCache) {
        this.mathmlFixer = new MaximaMathmlFixer(stylesheetCache);
        this.mathMLUpConverter = new MathMLUpConverter(stylesheetCache);
    }
    
    /**
     * Takes raw MathML returned from Maxima via the <tt>mathml.lisp</tt> module and attempts to
     * up-convert it into annotated MathML, returning a DOM {@link Document} Object.
     * <p>
     * <strong>NOTE:</strong> If you are calling this directly, then it is your responsibility to
     * check the annotations to make sure there were no failures. (The corresponding higher-level
     * code in {@link QtiMaximaProcess} does this for you.)
     * 
     * @param rawMaximaMathmlOutput MathML output from Maxima, which doesn't need to have been
     *   trimmed or tidied up.
     */
    public Document upconvertRawMaximaMathML(final String rawMaximaMathmlOutput) {
        /* First of all, trim off labels and other extraneous stuff */
        String mathMLElementString = rawMaximaMathmlOutput.replaceFirst("(?s)^.+(<math)", "$1")
            .replaceFirst("(?s)(</math>).+$", "$1");

        /* Then fix up the MathML... */
        Document fixedDocument = mathmlFixer.fixMaximaMathmlOutput(mathMLElementString);
        
        /* ...then up-convert */
        return mathMLUpConverter.upConvertSnuggleTeXMathML(fixedDocument, UpConversionConstants.UP_CONVERSION_OPTIONS);
    }
}
