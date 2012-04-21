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

import org.qtitools.mathassess.tools.maxima.upconversion.UpConversionConstants;
import org.qtitools.mathassess.tools.qticasbridge.types.MathsContentInputValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.MathsContentOutputValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.WrapperUtilities;

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
 * process on it, creating a {@link MathsContentOutputValueWrapper} that can be picked apart
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
public final class ASCIIMathMLHelper {
    
    private final MathMLUpConverter mathMLUpConverter;
    private final AsciiMathParser asciiMathParser;
    private final AsciiMathParserOptions asciiMathParserOptions;
    
    /** Location of our slightly modified version of ASCIIMathParser.js */
    private final String ASCIIMATH_PARSER_JS_LOCATION = "org/qtitools/mathassess/tools/AsciiMathParser.js";
    
    public ASCIIMathMLHelper() {
        this(new SimpleStylesheetCache());
    }
    
    public ASCIIMathMLHelper(StylesheetCache stylesheetCache) {
        this.mathMLUpConverter = new MathMLUpConverter(stylesheetCache);
        this.asciiMathParser = new AsciiMathParser(ASCIIMATH_PARSER_JS_LOCATION);
        this.asciiMathParserOptions = new AsciiMathParserOptions();
        asciiMathParserOptions.setAddSourceAnnotation(true);
    }
    
    /**
     * Takes the raw input from ASCIIMath and up-converts it, creating a
     * {@link MathsContentOutputValueWrapper} filled in with all of the relevant details,
     * which may include failure information if the input cannot be fully up-converted.
     * <p>
     * Use this when handling the input from the <tt>mathInputInteraction</tt>
     * 
     * @param asciiMathInput raw input entered into the ASCIIMathML box (e.g. '4sin x')
     * 
     * @return filled in {@link MathsContentOutputValueWrapper}
     * 
     * @throws IllegalArgumentException if the ASCIIMath input is null
     * @throws QTICASBridgeException if an unexpected Exception happened processing the input
     */
    public MathsContentInputValueWrapper createMathsContentFromASCIIMath(final String asciiMathInput) {
        ConstraintUtilities.ensureNotNull(asciiMathInput, "asciiMathInput");
        
        /* Run the ASCIIMathParser.js on the input */
        Document upConvertedDocument;
        try {
            Document asciiMathMLDocument = asciiMathParser.parseAsciiMath(asciiMathInput, asciiMathParserOptions);
            upConvertedDocument = mathMLUpConverter.upConvertASCIIMathML(asciiMathMLDocument, UpConversionConstants.UP_CONVERSION_OPTIONS);
        }
        catch (RuntimeException e) {
            throw new QTICASBridgeException("Unexpected Exception parsing or up-converting ASCIIMath input", e);
        }
        
        /* Create appropriate wrapper */
        MathsContentInputValueWrapper result = WrapperUtilities.createFromUpconvertedASCIIMathInput(asciiMathInput, upConvertedDocument);
        return result;
    }
    
    //-----------------------------------------------------------------
    
    /**
     * Validates some ASCIIMath input, returning a {@link Map} containing various bits of MathML
     * information in source format.
     * <p>
     * The keys are defined within this method. Read the source!
     * <p>
     * Use this when providing real-time AJAX feedback for the input widget.
     * 
     * @param asciiMathInput raw input entered into the ASCIIMathML box (e.g. '4sin x')
     * 
     * @return text-based Map, ready for sending via JSON or something similar
     * 
     * @throws IllegalArgumentException if the ASCIIMath input is null
     */
    public Map<String, String> upConvertASCIIMathInput(final String asciiMathInput) {
        ConstraintUtilities.ensureNotNull(asciiMathInput, "asciiMathInput");
        
        /* Run the ASCIIMathParser.js on the input */
        Document asciiMathMLDocument = asciiMathParser.parseAsciiMath(asciiMathInput, asciiMathParserOptions);
        Document upConvertedDocument = mathMLUpConverter.upConvertASCIIMathML(asciiMathMLDocument, UpConversionConstants.UP_CONVERSION_OPTIONS);
        Element mathElement = upConvertedDocument.getDocumentElement(); /* NB: Document is <math/> here */

        return unwrapMathMLElement(mathElement);
    }
    
    protected LinkedHashMap<String, String> unwrapMathMLElement(Element mathElement) {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        
        /* These options are used to serialize MathML that might get used, i.e. no entities */
        SerializationSpecifier serializationOptions = new SerializationOptions();
        serializationOptions.setIndenting(true);
        
        /* These options are used to serialize MathML that will only be displayed as source */
        SerializationSpecifier sourceOptions = new SerializationOptions();
        sourceOptions.setIndenting(true);
        sourceOptions.setUsingNamedEntities(true);
        
        /* Isolate various annotations from the result */
        Document pmathSemanticDocument = MathMLUtilities.isolateFirstSemanticsBranch(mathElement);
        Document pmathBracketedDocument = MathMLUtilities.isolateAnnotationXML(mathElement, MathMLUpConverter.BRACKETED_PRESENTATION_MATHML_ANNOTATION_NAME);
        Document cmathDocument = MathMLUtilities.isolateAnnotationXML(mathElement, MathMLUpConverter.CONTENT_MATHML_ANNOTATION_NAME);
        String maximaAnnotation = MathMLUtilities.extractAnnotationString(mathElement, MathMLUpConverter.MAXIMA_ANNOTATION_NAME);
        Document contentFailuresAnnotation = MathMLUtilities.isolateAnnotationXML(mathElement, MathMLUpConverter.CONTENT_FAILURES_ANNOTATION_NAME);
        Document maximaFailuresAnnotation = MathMLUtilities.isolateAnnotationXML(mathElement, MathMLUpConverter.MAXIMA_FAILURES_ANNOTATION_NAME);
        
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
    
    private void maybeAddResult(Map<String, String> resultBuilder, String key, Document value, SerializationSpecifier serializationSpecifier) {
        if (value!=null) {
            resultBuilder.put(key, MathMLUtilities.serializeDocument(value, serializationSpecifier));
        }
    }
    
    private void maybeAddResult(Map<String, String> resultBuilder, String key, Element value, SerializationSpecifier serializationSpecifier) {
        if (value!=null) {
            resultBuilder.put(key, MathMLUtilities.serializeElement(value, serializationSpecifier));
        }
    }
    
    private void maybeAddResult(Map<String, String> resultBuilder, String key, String value) {
        if (value!=null) {
            resultBuilder.put(key, value);
        }
    }

}
