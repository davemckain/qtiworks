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

import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.UpConversionConstants;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MathsContentInputValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.WrapperUtilities;

import uk.ac.ed.ph.asciimath.parser.AsciiMathParser;
import uk.ac.ed.ph.asciimath.parser.AsciiMathParserOptions;
import uk.ac.ed.ph.snuggletex.SerializationSpecifier;
import uk.ac.ed.ph.snuggletex.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.snuggletex.upconversion.MathMLUpConverter;
import uk.ac.ed.ph.snuggletex.utilities.MathMLUtilities;
import uk.ac.ed.ph.snuggletex.utilities.SerializationOptions;
import uk.ac.ed.ph.snuggletex.utilities.SimpleStylesheetCache;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetCache;

import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper class that takes the raw input and output from ASCIIMathML and runs the up-conversion
 * process on it, creating a {@link MathsContentInputValueWrapper} that can be picked apart
 * as required.
 * <p>
 * An instance of this class is NOT thread-safe
 *
 * <h2>Usage Notes</h2>
 *
 * Consider passing a shared instance of a {@link StylesheetCache} so that the underlying XSLT
 * stylesheets can be reused.
 *
 * @author David McKain
 */
public final class AsciiMathHelper {

    private final MathMLUpConverter mathmlUpConverter;
    private final AsciiMathParser asciiMathParser;
    private final AsciiMathParserOptions asciiMathParserOptions;

    /** Location of our slightly modified version of ASCIIMathParser.js */
    private static final String ASCIIMATH_PARSER_JS_LOCATION = "uk/ac/ed/ph/qtiworks/mathassess/glue/AsciiMathParser.js";

    public AsciiMathHelper() {
        this(new SimpleStylesheetCache());
    }

    public AsciiMathHelper(final StylesheetCache stylesheetCache) {
        this.mathmlUpConverter = new MathMLUpConverter(stylesheetCache);
        this.asciiMathParser = new AsciiMathParser(ASCIIMATH_PARSER_JS_LOCATION);
        this.asciiMathParserOptions = new AsciiMathParserOptions();
        asciiMathParserOptions.setAddSourceAnnotation(true);
    }

    /**
     * Takes the raw input from ASCIIMath and up-converts it, creating a
     * {@link MathsContentInputValueWrapper} filled in with all of the relevant details,
     * which may include failure information if the input cannot be fully up-converted.
     * <p>
     * Use this when handling the input from the <tt>mathInputInteraction</tt>
     *
     * @param asciiMathInput raw input entered into the ASCIIMathML box (e.g. '4sin x')
     *
     * @return filled in {@link MathsContentInputValueWrapper}
     *
     * @throws IllegalArgumentException if the ASCIIMath input is null
     * @throws AsciiMathInputException if the up-conversion process fails unexpectedly
     *   on the ASCIIMath input
     */
    public MathsContentInputValueWrapper createMathsContentFromAsciiMath(final String asciiMathInput) {
        ConstraintUtilities.ensureNotNull(asciiMathInput, "asciiMathInput");

        final Document upConvertedDocument = doAsciiMathUpConversion(asciiMathInput);
        return WrapperUtilities.createFromUpconvertedAsciiMathInput(asciiMathInput, upConvertedDocument);
    }

    /**
     * Takes the raw input from ASCIIMath and up-converts it,
     * returning a {@link Map} containing various bits of MathML
     * information in source format.
     * <p>
     * The keys are defined within {@link #unwrapMathmlElement(Element)}.
     * <p>
     * Use this when providing real-time AJAX feedback for the input widget.
     *
     * @param asciiMathInput raw input entered into the ASCIIMathML box (e.g. '4sin x')
     *
     * @return text-based Map, ready for sending via JSON or something similar
     *
     * @throws IllegalArgumentException if the ASCIIMath input is null
     * @throws AsciiMathInputException if the up-conversion process fails unexpectedly
     *   on the ASCIIMath input
     */
    public Map<String, String> upConvertAsciiMathInput(final String asciiMathInput) {
        ConstraintUtilities.ensureNotNull(asciiMathInput, "asciiMathInput");

        final Document upconvertedDocument = doAsciiMathUpConversion(asciiMathInput);
        final Element mathElement = upconvertedDocument.getDocumentElement();
        return unwrapMathmlElement(mathElement);
    }

    //-----------------------------------------------------------------

    private Document doAsciiMathUpConversion(final String asciiMathInput) {
        ConstraintUtilities.ensureNotNull(asciiMathInput, "asciiMathInput");

        /* Run the ASCIIMathParser.js on the input */
        final Document mathmlDocument = asciiMathParser.parseAsciiMath(asciiMathInput, asciiMathParserOptions);

        /* Then attempt to up-convert the resulting PMathML */
        try {
            return mathmlUpConverter.upConvertASCIIMathML(mathmlDocument, UpConversionConstants.UP_CONVERSION_OPTIONS);
        }
        catch (final RuntimeException e) {
            /* This can happen if SnuggleTeX up-conversion barfs on the input */
            throw new AsciiMathInputException(asciiMathInput);
        }
    }

    protected LinkedHashMap<String, String> unwrapMathmlElement(final Element mathElement) {
        final LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();

        /* These options are used to serialize MathML that might get used, i.e. no entities */
        final SerializationSpecifier serializationOptions = new SerializationOptions();
        serializationOptions.setIndenting(true);

        /* These options are used to serialize MathML that will only be displayed as source */
        final SerializationSpecifier sourceOptions = new SerializationOptions();
        sourceOptions.setIndenting(true);
        sourceOptions.setUsingNamedEntities(true);

        /* Isolate various annotations from the result */
        final Document pmathSemanticDocument = MathMLUtilities.isolateFirstSemanticsBranch(mathElement);
        final Document pmathBracketedDocument = MathMLUtilities.isolateAnnotationXML(mathElement, MathMLUpConverter.BRACKETED_PRESENTATION_MATHML_ANNOTATION_NAME);
        final Document cmathDocument = MathMLUtilities.isolateAnnotationXML(mathElement, MathMLUpConverter.CONTENT_MATHML_ANNOTATION_NAME);
        final String maximaAnnotation = MathMLUtilities.extractAnnotationString(mathElement, MathMLUpConverter.MAXIMA_ANNOTATION_NAME);
        final Document contentFailuresAnnotation = MathMLUtilities.isolateAnnotationXML(mathElement, MathMLUpConverter.CONTENT_FAILURES_ANNOTATION_NAME);
        final Document maximaFailuresAnnotation = MathMLUtilities.isolateAnnotationXML(mathElement, MathMLUpConverter.MAXIMA_FAILURES_ANNOTATION_NAME);

        /* Build up result */
        maybeAddResult(result, "pmathParallel", mathElement, sourceOptions);
        maybeAddResult(result, "pmathSemantic", pmathSemanticDocument, sourceOptions);
        maybeAddResult(result, "pmathBracketed", pmathBracketedDocument, serializationOptions);
        maybeAddResult(result, "cmath", cmathDocument, sourceOptions);
        maybeAddResult(result, "maxima", maximaAnnotation);
        maybeAddResult(result, "cmathFailures", contentFailuresAnnotation, sourceOptions);
        maybeAddResult(result, "maximaFailures", maximaFailuresAnnotation, sourceOptions);
        return result;
    }

    private void maybeAddResult(final Map<String, String> resultBuilder, final String key, final Document value, final SerializationSpecifier serializationSpecifier) {
        if (value!=null) {
            resultBuilder.put(key, MathMLUtilities.serializeDocument(value, serializationSpecifier));
        }
    }

    private void maybeAddResult(final Map<String, String> resultBuilder, final String key, final Element value, final SerializationSpecifier serializationSpecifier) {
        if (value!=null) {
            resultBuilder.put(key, MathMLUtilities.serializeElement(value, serializationSpecifier));
        }
    }

    private void maybeAddResult(final Map<String, String> resultBuilder, final String key, final String value) {
        if (value!=null) {
            resultBuilder.put(key, value);
        }
    }

}
