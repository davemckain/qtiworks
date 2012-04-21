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
package org.qtitools.mathassess.tools.qticasbridge.types;

import org.qtitools.mathassess.tools.qticasbridge.QTICASBridgeException;

import uk.ac.ed.ph.snuggletex.upconversion.MathMLUpConverter;
import uk.ac.ed.ph.snuggletex.upconversion.UpConversionUtilities;
import uk.ac.ed.ph.snuggletex.utilities.MathMLUtilities;
import uk.ac.ed.ph.snuggletex.utilities.UnwrappedParallelMathMLDOM;

import org.w3c.dom.Document;

/**
 * Some useful (but scary looking) methods for building up {@link CompoundValueWrapper}s
 * of various types.
 * <p>
 * This is useful for wrapping up existing values and also handy for testing.
 *
 * @author David McKain
 */
public final class WrapperUtilities {
    
    /**
     * Unravels an up-converted MathML document (as produced previously from either from raw
     * SnuggleTeX, ASCIIMathML or Maxima MathML output) and converts the result to a
     * {@link MathsContentValueWrapper}.
     */
    public static MathsContentInputValueWrapper createFromUpconvertedASCIIMathInput(final String asciiMathInput, final Document upConvertedMathMLDocument) {
        UnwrappedParallelMathMLDOM unwrappedDocument = MathMLUtilities.unwrapParallelMathMLDOM(upConvertedMathMLDocument.getDocumentElement());
        MathsContentInputValueWrapper result = new MathsContentInputValueWrapper();
        result.setAsciiMathInput(asciiMathInput);
        
        /* Extract semantic PMathML */
        Document pMathMLDocument = MathMLUtilities.isolateFirstSemanticsBranch(unwrappedDocument);
        result.setPMathML(MathMLUtilities.serializeDocument(pMathMLDocument));
        
        /* Extract bracketed PMathML */
        Document pMathMLBracketedDocument = MathMLUtilities.isolateAnnotationXML(unwrappedDocument, MathMLUpConverter.BRACKETED_PRESENTATION_MATHML_ANNOTATION_NAME);
        result.setPMathMLBracketed(MathMLUtilities.serializeDocument(pMathMLBracketedDocument));

        /* Extract up-converted information */
        Document cMathMLDocument = MathMLUtilities.isolateAnnotationXML(unwrappedDocument, MathMLUpConverter.CONTENT_MATHML_ANNOTATION_NAME);
        if (cMathMLDocument!=null) {
            result.setCMathML(MathMLUtilities.serializeDocument(cMathMLDocument));
        }
        result.setMaximaInput(unwrappedDocument.getTextAnnotations().get(MathMLUpConverter.MAXIMA_ANNOTATION_NAME));
        
        /* Extract any up-conversion failures */
        result.setUpConversionFailures(UpConversionUtilities.extractUpConversionFailures(unwrappedDocument));
        
        /* That's it! */
        return result;
    }
    
    /**
     * Unravels an up-converted MathML document (as produced previously from either from raw
     * SnuggleTeX, ASCIIMathML or Maxima MathML output) and converts the result to a
     * {@link MathsContentValueWrapper}.
     */
    public static MathsContentOutputValueWrapper createFromUpconvertedMaximaOutput(final Document upConvertedMathMLDocument) {
        UnwrappedParallelMathMLDOM unwrappedDocument = MathMLUtilities.unwrapParallelMathMLDOM(upConvertedMathMLDocument.getDocumentElement());
        MathsContentOutputValueWrapper result = new MathsContentOutputValueWrapper();
        
        /* Extract semantic PMathML */
        Document pMathMLDocument = MathMLUtilities.isolateFirstSemanticsBranch(unwrappedDocument);
        result.setPMathML(MathMLUtilities.serializeDocument(pMathMLDocument));
        result.setPMathMLElement(pMathMLDocument.getDocumentElement());

        /* Extract up-converted information */
        Document cMathMLDocument = MathMLUtilities.isolateAnnotationXML(unwrappedDocument, MathMLUpConverter.CONTENT_MATHML_ANNOTATION_NAME);
        if (cMathMLDocument!=null) {
            result.setCMathML(MathMLUtilities.serializeDocument(cMathMLDocument));
        }
        result.setMaximaInput(unwrappedDocument.getTextAnnotations().get(MathMLUpConverter.MAXIMA_ANNOTATION_NAME));
        
        /* Extract any up-conversion failures */
        result.setUpConversionFailures(UpConversionUtilities.extractUpConversionFailures(unwrappedDocument));
        
        /* That's it! */
        return result;
    }
    
    //------------------------------------------------------------------------
    
    /**
     * Creates a {@link SingleValueWrapper} instance of the given type, representing a null
     * value.
     * 
     * @param <B> underlying baseType
     * @param <S> required {@link SingleValueWrapper} type
     * @param resultClass Class specifying the required SingleValueWrapper type
     */
    public static <B, S extends SingleValueWrapper<B>>
    S createSingleValue(final Class<S> resultClass) {
        return instantiateValueWrapper(resultClass);
    }
    
    /**
     * Creates a {@link CompoundValueWrapper} instance of the given type, representing an empty
     * collection.
     * 
     * @param <B> underlying baseType
     * @param <S> required {@link SingleValueWrapper} type for the elements in the collection
     * @param <C> required {@link CompoundValueWrapper} type.
     * @param resultClass Class specifying the required SingleValueWrapper type
     */
    public static <B, S extends SingleValueWrapper<B>, C extends CompoundValueWrapper<B,S>>
    C createCompoundValue(final Class<C> resultClass) {
        return instantiateValueWrapper(resultClass);
    }
    
    /**
     * Creates a {@link CompoundValueWrapper} instance of the given type, containing
     * the given (wrapped) items.
     * 
     * @param <B> underlying baseType
     * @param <S> required {@link SingleValueWrapper} type for the elements in the collection
     * @param <C> required {@link CompoundValueWrapper} type.
     * @param resultClass Class specifying the required SingleValueWrapper type
     * @param itemValueWrappers items to add to the resulting collection.
     */
    public static <B, S extends SingleValueWrapper<B>, C extends CompoundValueWrapper<B,S>> 
    C createCompoundValue(final Class<C> resultClass, final Iterable<S> itemValueWrappers) {
        C result = createCompoundValue(resultClass);
        for (S valueWrapper : itemValueWrappers) {
            result.add(valueWrapper);
        }
        return result;
    }
    
    /**
     * Creates a {@link CompoundValueWrapper} instance of the given type, containing
     * the given (wrapped) items.
     * 
     * @param <B> underlying baseType
     * @param <S> required {@link SingleValueWrapper} type for the elements in the collection
     * @param <C> required {@link CompoundValueWrapper} type.
     * @param resultClass Class specifying the required SingleValueWrapper type
     * @param itemValueWrappers items to add to the resulting collection.
     */
    public static <B, S extends SingleValueWrapper<B>, C extends CompoundValueWrapper<B,S>> 
    C createCompoundValue(final Class<C> resultClass, final S... itemValueWrappers) {
        C result = createCompoundValue(resultClass);
        for (S valueWrapper : itemValueWrappers) {
            result.add(valueWrapper);
        }
        return result;
    }
    
    
    /**
     * Creates a {@link CompoundValueWrapper} instance of the given type, containing
     * wrapped versions of the given raw items.
     * 
     * @param <B> underlying baseType
     * @param <S> required {@link SingleValueWrapper} type for the elements in the collection
     * @param <C> required {@link CompoundValueWrapper} type.
     * @param resultWrapperClass Class specifying the required SingleValueWrapper type
     * @param itemValues items to add to the resulting collection.
     */
    public static <B, S extends SingleValueWrapper<B>, C extends CompoundValueWrapper<B,S>> 
    C createCompoundValue(final Class<C> resultWrapperClass, final Class<S> itemWrapperClass,
            final Iterable<B> itemValues) {
        C result = createCompoundValue(resultWrapperClass);
        for (B value : itemValues) {
            S item = instantiateValueWrapper(itemWrapperClass);
            item.setValue(value);
            result.add(item);
        }
        return result;
    }
    
    /**
     * Creates a {@link CompoundValueWrapper} instance of the given type, containing
     * wrapped versions of the given raw items.
     * 
     * @param <B> underlying baseType
     * @param <S> required {@link SingleValueWrapper} type for the elements in the collection
     * @param <C> required {@link CompoundValueWrapper} type.
     * @param resultClass Class specifying the required SingleValueWrapper type
     * @param itemValues items to add to the resulting collection.
     */
    public static <B, S extends SingleValueWrapper<B>, C extends CompoundValueWrapper<B,S>> 
    C createCompoundValue(final Class<C> resultClass, final Class<S> itemClass, B... itemValues) {
        C result = createCompoundValue(resultClass);
        for (B value : itemValues) {
            S item = instantiateValueWrapper(itemClass);
            item.setValue(value);
            result.add(item);
        }
        return result;
    }
    
    private static <V extends ValueWrapper> V instantiateValueWrapper(final Class<V> valueWrapperClass) {
        try {
            return valueWrapperClass.newInstance();
        }
        catch (Exception e) {
            throw new QTICASBridgeException("Unexpected Exception instantiating ValueWrapper of type "
                    + valueWrapperClass);
        }
    }
}
