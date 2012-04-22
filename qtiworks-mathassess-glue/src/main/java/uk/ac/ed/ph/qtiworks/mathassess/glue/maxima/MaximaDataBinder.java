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


import uk.ac.ed.ph.qtiworks.mathassess.glue.MathAssessCasException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.MathAssessSpecUnimplementedException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.CompoundValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MathsContentValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MultipleValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.OrderedValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.SingleValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.StringValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueOrVariableWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.VariableWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.WrapperUtilities;
import uk.ac.ed.ph.snuggletex.internal.util.ConstraintUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class performing "data binding" tasks that establish mappings
 * between {@link ValueOrVariableWrapper}s and Maxima input/output.
 * 
 * @author David McKain
 */
public final class MaximaDataBinder {

    //-------------------------------------------------------------------------------
    // Methods for converting QTI values to Maxima input forms
    
    /**
     * Converts the given {@link ValueOrVariableWrapper} to a corresponding Maxima
     * form that can be used a part of an input expression.
     * 
     * @throws IllegalArgumentException if the given wrapper is a
     *  {@link MathsContentValueWrapper} with a missing Maxima annotation, which indicates
     *  that the value passed hasn't been filled in correctly (or was maybe too complex, but
     *  this should have been checked in advance of calling this.)
     */
    public String toMaximaExpression(final ValueOrVariableWrapper argument) {
        ConstraintUtilities.ensureNotNull(argument, "variable or value");
        String result;
        if (argument instanceof VariableWrapper) {
            VariableWrapper variable = (VariableWrapper) argument;
            result = variable.getIdentifier();
        }
        else if (argument instanceof ValueWrapper) {
            ValueWrapper valueWrapper = (ValueWrapper) argument;
            result = toMaximaExpression(valueWrapper);
        }
        else {
            throw new MathAssessCasException("Unexpected logic branch");
        }
        return result;
    }
    
    private String toMaximaExpression(final ValueWrapper valueWrapper) {
        ConstraintUtilities.ensureNotNull(valueWrapper, "value");
        switch (valueWrapper.getCardinality()) {
            case MATHS_CONTENT:
                MathsContentValueWrapper mathsContent = (MathsContentValueWrapper) valueWrapper;
                String maximaInput = mathsContent.getMaximaInput();
                if (maximaInput==null) {
                    throw new IllegalArgumentException("The maximaInput field must not be empty");
                }
                return maximaInput;
                
            case SINGLE:
                return toMaximaExpression((SingleValueWrapper<?>) valueWrapper);
                
            case MULTIPLE:
                return toMaximaSet((MultipleValueWrapper<?,?>) valueWrapper);

            case ORDERED:
                return toMaximaList((OrderedValueWrapper<?,?>) valueWrapper);
                
            default:
                throw new MathAssessSpecUnimplementedException("No current support for mapping values with cardinality "
                    + valueWrapper.getCardinality()
                    + " to Maxima input");
        }
    }
    
    private <B> String toMaximaExpression(final SingleValueWrapper<B> valueWrapper) {
        ConstraintUtilities.ensureNotNull(valueWrapper, "value");
        return toMaximaItem(valueWrapper);
    }
    
    private <B, S extends SingleValueWrapper<B>>
    String toMaximaSet(MultipleValueWrapper<B,S> wrapperSet) {
        return makeComposite(wrapperSet, "{", "}");
    }
    
    private <B, S extends SingleValueWrapper<B>>
    String toMaximaList(OrderedValueWrapper<B,S> wrapperSet) {
        return makeComposite(wrapperSet, "[", "]");
    }
    
    private <B, S extends SingleValueWrapper<B>>
    String makeComposite(Collection<S> collection, String opener, String closer) {
        StringBuilder resultBuilder = new StringBuilder(opener);
        String prefix = "";
        for (S entry : collection) {
            resultBuilder.append(prefix).append(toMaximaItem(entry));
            prefix = ", ";
        }
        resultBuilder.append(closer);
        return resultBuilder.toString();
    }
    
    private <B, S extends SingleValueWrapper<B>>
    String toMaximaItem(S item) {
        switch (item.getBaseType()) {
            case BOOLEAN:
            case FLOAT:
            case INTEGER:
                return item.getValue().toString();
                
//            case POINT:
//                PointValueWrapper pointValue = (PointValueWrapper) item;
//                return toMaximaList(pointValue.toIntegerOrderedValueWrapper());
               
            case STRING:
                String itemString = ((StringValueWrapper) item).getValue();
                return "\"" + itemString.replace("\"", "\\\"") + "\"";
                
            default:
                throw new MathAssessSpecUnimplementedException("No support for mapping single variables of baseType "
                        + item.getBaseType() + " to Maxima input");
        }
    }
    
    //-------------------------------------------------------------------------------
    // General methods for getting results out of Maxima
    
    /**
     * Parses the linear output from Maxima's <tt>string();</tt> or <tt>grind()$</tt> commands,
     * attempting to construct a {@link ValueWrapper} Object of the type specified in resultClass.
     * If the Maxima output is compatible with this type then an appropriately populated Object
     * is returned representing the Maxima output, otherwise null is returned.
     * 
     * @param maximaLinearOutput linear output from <tt>string(expr);</tt> or <tt>grind(expr)$</tt>,
     *   which must not be null. (It is assumed you have pulled this out of the raw Maxima output
     *   and tidied up in advance.)
     * @param resultClass Class of the desired result type
     * @param <V> the desired result type
     * 
     * @return instance of the required result type representing the linear output, or null if
     *   the output could not be converted into the given type. (E.g. if called requesting to
     *   return an integer when the linear output is an expression).
     * 
     * @throws IllegalArgumentException if called with a {@link MathsContentValueWrapper} resultClass.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <V extends ValueWrapper>
    V parseMaximaLinearOutput(final String maximaLinearOutput, final Class<V> resultClass) {
        ConstraintUtilities.ensureNotNull(maximaLinearOutput, "Maxima linear output");
        ConstraintUtilities.ensureNotNull(resultClass, "result Class");
        
        /* Now parse */
        if (SingleValueWrapper.class.isAssignableFrom(resultClass)) {
            return (V) parseSingleLinearOutput(maximaLinearOutput, (Class<? extends SingleValueWrapper>) resultClass);
        }
        else if (MultipleValueWrapper.class.isAssignableFrom(resultClass)) {
            return (V) parseMultipleLinearOutput(maximaLinearOutput, (Class<? extends MultipleValueWrapper>) resultClass);
        }
        else if (OrderedValueWrapper.class.isAssignableFrom(resultClass)) {
            return (V) parseOrderedLinearOutput(maximaLinearOutput, (Class<? extends OrderedValueWrapper>) resultClass);
        }
        else if (MathsContentValueWrapper.class.isInstance(resultClass)) {
            throw new IllegalArgumentException("This method should not be used to extract MathsContent values");
        }
        else {
            throw new MathAssessSpecUnimplementedException("Support for parsing raw Maxima output into values of class " + resultClass
                    + " has not yet been implemented");
        }
    }
    
    @SuppressWarnings("unchecked")
    private <B, S extends SingleValueWrapper<B>>
    S parseSingleLinearOutput(final String linearOutput, final Class<S> resultClass) {
        S resultWrapper = WrapperUtilities.createSingleValue(resultClass);
        B resultContent = null;
        switch (resultWrapper.getBaseType()) {
            case STRING:
                String resultString = null;
                if (linearOutput.length()==0) {
                    /* grind("") and string("") yield an empty result, which is out of step with what follows */
                    resultString = "";
                }
                else if (linearOutput.startsWith("\"") && linearOutput.endsWith("\"")) {
                    resultString = linearOutput
                        .substring(1, linearOutput.length()-1) /* Trim off opening and closing double-quotes */
                        .replace("\\\n", "\n")  /* Maxima outputs \ followed by a newline for a newline */
                        .replace("\\\"", "\""); /* Unescape escaped double-quotes */
                }
                resultContent = (B) resultString;
                break;
                
            case BOOLEAN:
                if (linearOutput.equals("true")) {
                    resultContent = (B) Boolean.TRUE;
                }
                else if (linearOutput.equals("false")) {
                    resultContent = (B) Boolean.FALSE;
                }
                break;
                
            case INTEGER:
                try {
                    resultContent = (B) Integer.valueOf(linearOutput); 
                }
                catch (NumberFormatException e) {
                    /* Continue to catch-all below */
                }
                break;
                
            case FLOAT:
                try {
                    resultContent = (B) Double.valueOf(linearOutput); 
                }
                catch (NumberFormatException e) {
                    /* Continue to catch-all below */
                }
                break;
                
            default:
                throw new MathAssessSpecUnimplementedException("Support for parsing raw Maxima output fragments into "
                        + "single QTI values of baseType "
                        + resultWrapper.getBaseType()
                        + " has not yet been implemented");
        }
        /* If result is still null then parsing failed, so return null */
        if (resultContent==null) {
            return null;
        }
        resultWrapper.setValue(resultContent);
        return resultWrapper;
    }
    
    private <B, S extends SingleValueWrapper<B>, C extends MultipleValueWrapper<B,S>>
    C parseMultipleLinearOutput(final String linearOutput, final Class<C> resultClass) {
        return parseCompoundLinearOutput(linearOutput, "{", "}", resultClass);
    }
    
    private <B, S extends SingleValueWrapper<B>, C extends OrderedValueWrapper<B,S>>
    C parseOrderedLinearOutput(final String linearOutput, final Class<C> resultClass) {
        return parseCompoundLinearOutput(linearOutput, "[", "]", resultClass);
    }
    
    private <B, S extends SingleValueWrapper<B>, C extends CompoundValueWrapper<B,S>>
    C parseCompoundLinearOutput(final String linearOutput, final String opener, final String closer,
            final Class<C> resultClass) {
        C result = WrapperUtilities.createCompoundValue(resultClass);
        
        /* Bail out if not delimited appropriately */
        if (!(linearOutput.startsWith(opener) && linearOutput.endsWith(closer))) {
            return null;
        }
        String collectionContent = linearOutput.substring(opener.length(),
                linearOutput.length() + 1 - opener.length() - closer.length());
        
        /* Maxima's string outputs are a bit odd in that [] is output instead of [""],
         * [,] represents ["",""] etc. so we need to fix these up if that's the case
         */
        if (StringValueWrapper.class.isAssignableFrom(result.getItemClass())) {
            if (collectionContent.length()==0) {
                collectionContent = "\"\"";
            }
            else {
                collectionContent = collectionContent.replaceAll("(^|,),", "$1\"\",");
                collectionContent = collectionContent.replaceAll(",(,|$)", ",\"\"$1");
            }
        }
        
        /* Do a safe CSV split on it */
        List<String> itemStrings = splitCSVSafely(collectionContent);
        for (String itemString : itemStrings) {
            /* Parse each bit */
            Class<S> itemClass = result.getItemClass();
            S item = parseSingleLinearOutput(itemString, itemClass);
            if (item==null) {
                /* Parsing failed, so reject the whole Collection */
                return null;
            }
            result.add(item);
        }
        return result;
    }
    
    /**
     * Helper method to split up CSV that might delimit some fields inside double quotes, handling
     * commas correctly that might appear within these quotes.
     * <p>
     * (Note that any double quotes delimiting fields are kept in the results.)
     * @param collectionContent
     * @return
     */
    private static List<String> splitCSVSafely(final String collectionContent) {
        Pattern p = Pattern.compile("([^,]*)(,\\s*)?");
        Matcher m = p.matcher(collectionContent);
        
        List<String> result = new ArrayList<String>();
        String contentGroup = null;
        String commaGroup = null;
        boolean quoted = false;
        StringBuilder itemBuilder = new StringBuilder();
        while (m.find()) {
            contentGroup = m.group(1);
            commaGroup = m.group(2);
            if (contentGroup.length()==0 && commaGroup==null) {
                /* Found nothing */
                break;
            }
            if (quoted) {
                itemBuilder.append(contentGroup);
                if (contentGroup.endsWith("\"")) {
                    /* End of quote mode */
                    result.add(itemBuilder.toString());
                    itemBuilder.setLength(0);
                    quoted = false;
                }
                else {
                    /* Keep content and comma */
                    itemBuilder.append(commaGroup);
                }
            }
            else {
                if (contentGroup.startsWith("\"") && !(contentGroup.length() > 1 && contentGroup.endsWith("\""))) {
                    itemBuilder.append(contentGroup);
                    itemBuilder.append(commaGroup);
                    quoted = true;
                }
                else {
                    result.add(contentGroup);
                }
            }
        }
        if (quoted) {
            throw new MathAssessCasException("Last opened double quotes were not closed");
        }
        return result;
    }
}
