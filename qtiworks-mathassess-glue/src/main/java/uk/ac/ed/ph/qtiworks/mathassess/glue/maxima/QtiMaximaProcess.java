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

import uk.ac.ed.ph.jacomax.MaximaInteractiveProcess;
import uk.ac.ed.ph.jacomax.MaximaProcessTerminatedException;
import uk.ac.ed.ph.jacomax.MaximaTimeoutException;
import uk.ac.ed.ph.jacomax.utilities.MaximaOutputUtilities;
import uk.ac.ed.ph.qtiworks.mathassess.glue.MathAssessBadCasCodeException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.MathsContentTooComplexException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.MathAssessCasException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.BooleanValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MathsContentOutputValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MathsContentValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueOrVariableWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.WrapperUtilities;
import uk.ac.ed.ph.snuggletex.internal.util.ConstraintUtilities;
import uk.ac.ed.ph.snuggletex.upconversion.UpConversionFailure;
import uk.ac.ed.ph.snuggletex.utilities.MathMLUtilities;
import uk.ac.ed.ph.snuggletex.utilities.StylesheetCache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is the main interface for performing MathAssess QTI-related interactions with Maxima.
 * 
 * <h2>Usage Notes</h2>
 * <ul>
 *   <li>
 *     Use an instance of {@link QtiMaximaProcessManager} to create and manage instances of
 *     this class. The simplest option is {@link SimpleQtiMaximaProcessManager}; a more
 *     performant option that uses a pool of processes can be found in the Extras module.
 *   </li>
 *   <li>
 *     An instance of this class should only be used by one Thread at a time but is serially
 *     reusable.
 *   </li>
 *   <li>
 *     Call {@link QtiMaximaProcessManager#obtainProcess()} to obtain an instance of this class,
 *     which can be assumed to be a sufficiently empty state. This state will change when other
 *     calls are made.
 *   </li>
 *   <li>
 *     Call the various methods here as required.
 *   </li>
 *   <li>
 *     Call {@link QtiMaximaProcessManager#returnProcess(QtiMaximaProcess)} when you have
 *     finished using this {@link QtiMaximaProcess}. 
 *   </li>
 *   <li>
 *     For examples, see the examples package or the test suite.
 *   </li>
 * </ul>
 * 
 * FIXME: Add constraints from the spec on which types of values can be passed to CasCompare/CasCondition
 * 
 * @author David McKain
 */
public final class QtiMaximaProcess {
    
    private final static Logger logger = LoggerFactory.getLogger(QtiMaximaProcess.class);

    /** Maxima code for the <tt>equal</tt> action in <tt>CasCompare</tt> */
    public static final String MAXIMA_EQUAL_CODE = "is(is(equal($1,$2))=true)";
    
    /** Maxima code for the <tt>syntequal<tt> action in <tt>CasCompare</tt> */
    public static final String MAXIMA_SYNTEQUAL_CODE = "is(is($1=$2)=true)";
    
    /** Underlying Raw Maxima Session */
    private final MaximaInteractiveProcess maximaInteractiveProcess;
    
    /** Helper to up-convert Maxima MathML output */
    private final MaximaMathmlUpConverter maximaMathMLUpconverter;
    
    /** Maxima Data Binding helper */
    private final MaximaDataBinder maximaDataBinder;
    
    public QtiMaximaProcess(MaximaInteractiveProcess maximaInteractiveProcess, StylesheetCache stylesheetCache) {
        this.maximaInteractiveProcess = maximaInteractiveProcess;
        this.maximaMathMLUpconverter = new MaximaMathmlUpConverter(stylesheetCache);
        this.maximaDataBinder = new MaximaDataBinder();
    }
    
    public MaximaInteractiveProcess getMaximaInteractiveProcess() {
        return maximaInteractiveProcess;
    }
    
    //------------------------------------------------
    // Session lifecycle methods - do not call these directly
    
    public void init() {
        logger.info("Initialising new QtiMaximaProcess");
        try {
            /* Load the MathML module */
            maximaInteractiveProcess.executeCallDiscardOutput("load(mathml)$");
        }
        catch (Exception e) {
            throw new MathAssessCasException("Failed to start and fully initialise Maxima process for use with MathAssess QTI", e);
        }
    }
    
    public boolean isTerminated() {
        return maximaInteractiveProcess.isTerminated();
    }
    
    public void terminate() {
        maximaInteractiveProcess.terminate();
    }
    
    public void reset() throws MaximaTimeoutException {
        maximaInteractiveProcess.softReset();
    }
    
    //------------------------------------------------
    // Passing variables from the QTI code to Maxima
    
    /**
     * Assigns the given {@link ValueWrapper} to the given QTI variable within Maxima.
     * <p>
     * If the {@link ValueWrapper} is null or encapsulates a null value, then the corresponding
     * Maxima variable is cleared.
     * 
     * @param variableIdentifier QTI variable identifier, which must not be null
     * @param valueWrapper representing the QTI value of the variable.
     * 
     * @throws IllegalArgumentException if the given value is null, represents a null value or
     *   is a {@link MathsContentValueWrapper} with a missing maximaInput field.
     * @throws MaximaProcessTerminatedException
     */
    public void passQTIVariableToMaxima(final String variableIdentifier, final ValueWrapper valueWrapper) {
        logger.info("passQTIVariableToMaxima: var={}, value={}", variableIdentifier, valueWrapper);
        checkVariableIdentifier(variableIdentifier);
        if (valueWrapper==null || valueWrapper.isNull()) {
            /* Nullify variable using kill() */
            try {
                maximaInteractiveProcess.executeCallDiscardOutput("kill(" + variableIdentifier  + ")$");
            }
            catch (MaximaTimeoutException e) {
                /* This shouldn't happen here! */
                throw new MathAssessCasException("Unexpected timeout while killing Maxima variable "
                        + variableIdentifier, e);
            }            
        }
        else {
            /* Convert the QTI value to an appropriate Maxima expression */
            String maximaValue = maximaDataBinder.toMaximaExpression(valueWrapper);
            
            /* Then do the appropriate Maxima call, with no simplification */
            try {
                maximaInteractiveProcess.executeCallDiscardOutput("simp:false$ " + variableIdentifier + ": " + maximaValue + "$");
            }
            catch (MaximaTimeoutException e) {
                /* This shouldn't happen here! */
                throw new MathAssessCasException("Unexpected timeout when setting Maxima variable "
                        + variableIdentifier + " to value " + maximaValue, e);
            }
        }
    }
    
    //------------------------------------------------
    // Extracting variables between the QTI layer and Maxima
    
    /**
     * Asks Maxima to return the value of the variable with the given identifier in a form
     * matching the given resultClass.
     * <p>
     * Note that it's legal to ask Maxima to return the value of any variable as a
     * {@link MathsContentOutputValueWrapper}, even if it wasn't defined that way at first.
     * The converse isn't necessarily trough, though, so think carefully whether any
     * attempted type conversions are legal.
     * 
     * @param variableIdentifier code to be executed returning a single value of the given type
     * @param resultClass Class specifying the required return type
     * @param <V> required return type
     * 
     * @return wrapper encapsulating the resulting QTI value of the given variable, or null
     *   if it isn't defined.
     *   
     * @throws QtiMaximaTypeConversionException if the given variable could not be represented using
     *   the specified resultClass.
     * @throws MathsContentTooComplexException if the output from Maxima could not be
     *   up-converted, presumably because it is too complex.
     * @throws MaximaProcessTerminatedException 
     * @throws MathAssessCasException if a timeout occurs or if the Maxima output cannot
     *   be parsed correctly.
     */
    @SuppressWarnings("unchecked")
    public <V extends ValueWrapper> V queryMaximaVariable(final String variableIdentifier,
            final Class<V> resultClass)
            throws QtiMaximaTypeConversionException, MathsContentTooComplexException {
        checkVariableIdentifier(variableIdentifier);
        ConstraintUtilities.ensureNotNull(resultClass, "resultClass");
        V result;
        try {
            /* Check whether the variable is actually defined by asking Maxima
             * to return it. We will use the "string(var);" form as me may end up picking this
             * apart to parse the actual value later.
             * 
             * Note that simplification is turned off here.
             */
            String maximaStringOutput;
            try {
                maximaStringOutput = executeStringOutput(variableIdentifier, false);
            }
            catch (MathAssessBadCasCodeException e) {
                throw new MathAssessCasException("Unexpected failure to parse string() output", e);
            }
            
            if (variableIdentifier.equals(maximaStringOutput)) {
                /* If we got the variable name back, then it wasn't defined */
                result = null;
            }
            else {
                if (MathsContentValueWrapper.class.isAssignableFrom(resultClass)) {
                    /* We ask Maxima again, this time returning MathML */
                    result = (V) executeMathOutput(variableIdentifier, false);
                }
                else {
                    /* We just parse the string() output */
                    result = ensureParseStringOutput(variableIdentifier, maximaStringOutput, resultClass);
                }
            }
        }
        catch (MaximaTimeoutException e) {
            throw new MathAssessCasException("Unexpected timeout occurred while extracting the value of variable "
                    + variableIdentifier, e);
        }
        logger.info("queryMaximaVariable: {} => {}", variableIdentifier, result);
        return result;
    }
    
    /**
     * Convenience method to evaluate the given Maxima expression, which is assumed to be
     * a single expression of the form "expr" or "expr;", using <tt>string()</tt>
     * to format the output and parsing the output to fit the given resultClass.
     * 
     * @param maximaExpression Maxima expression to evaluate, which can be of the form
     *   "expr" or "expr;".
     * @param resultClass Class corresponding to the desired type of {@link ValueWrapper} to return
     * @param <V> desired type of {@link ValueWrapper} to return.
     * 
     * @return Maxima output, parsed as a value of the given type, or null if the value
     *   is not of the given type.
     *   
     * @throws MathAssessBadCasCodeException if the output from string() could not be parsed,
     *   most likely indicating bad input.
     * @throws QtiMaximaTypeConversionException if the output from string() could not be converted
     *   into the desired return type.
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException 
     * 
     * @see #executeStringOutput(String, boolean)
     */
    public <V extends ValueWrapper> V executeStringOutput(final String maximaExpression,
            final boolean simplify, final Class<V> resultClass)
            throws MaximaTimeoutException {
        String stringOutput = executeStringOutput(maximaExpression, simplify);
        return ensureParseStringOutput(maximaExpression, stringOutput, resultClass);
    }
    
    /**
     * Convenience method to evaluate the given Maxima expression, which is assumed to be
     * a single expression of the form "expr" or "expr;", using <tt>string()</tt>
     * to format the output, returning a tidied (but unparsed) version of the resulting output.
     * 
     * @param maximaExpression Maxima expression to evaluate, which can be of the form
     *   "expr" or "expr;".
     * @return string() output, trimmed of leading and trailing whitespace
     * 
     * @throws MathAssessBadCasCodeException if the output from string() did not have the expected
     *   format, most likely indicating bad input.
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException 
     * 
     * @see #executeStringOutput(String, boolean, Class)
     */
    public String executeStringOutput(final String maximaExpression, final boolean simplify)
            throws MaximaTimeoutException {
        ConstraintUtilities.ensureNotNull(maximaExpression, "Maxima expression");
        logger.debug("executeStringOutput: expr={}, simp={}", maximaExpression, simplify);
        
        /* Strip off any terminator, if provided */
        String withoutTerminator = stripTrailingTerminator(maximaExpression);
        if (withoutTerminator.length()==0) {
            /* Exit now as string() doesn't behave as expected here! */
            return "";
        }
        
        /* Now do call, with simplification set as required */
        String maximaInput = "simp:" + simplify + "$ string(" + withoutTerminator + ");";
        String maximaOutput = maximaInteractiveProcess.executeCall(maximaInput);
        
        /* Decompose the output */
        String result = MaximaOutputUtilities.parseSingleLinearOutputResult(maximaOutput);
        if (result==null) {
            throw new MathAssessBadCasCodeException("Maxima call did not return a parseable result", maximaInput, maximaOutput);
        }
        return result;
    }
    
    private <V extends ValueWrapper> V ensureParseStringOutput(final String maximaExpression,
            final String stringOutput, final Class<V> resultClass) {
        V result = maximaDataBinder.parseMaximaLinearOutput(stringOutput, resultClass);
        if (result==null) {
            logger.warn("Could not bind raw string(" + maximaExpression
                    + ") output '" + stringOutput
                    + "' into result type " + resultClass.getSimpleName());
            throw new QtiMaximaTypeConversionException(maximaExpression, stringOutput, resultClass);
        }
        return result;        
    }
    
    private String stripTrailingTerminator(final String maximaInput) {
        if (maximaInput.length() > 0) {
            char lastChar = maximaInput.charAt(maximaInput.length() - 1);
            if (lastChar==';' || lastChar=='$') {
                return maximaInput.substring(0, maximaInput.length() - 1);
            }
        }
        return maximaInput;
    }
    
    /**
     * Convenience method to evaluate the given Maxima expression, which is assumed to be
     * a single expression of the form "expr" or "expr;", outputting the result as MathML
     * which is then up-converted and represented as a {@link MathsContentOutputValueWrapper}.
     * <p>
     * The implementation converts this to "mathml(expr);" and returns the result as a
     * {@link MathsContentOutputValueWrapper}.
     * 
     * @return MathsContentOutputValueWrapper representing the given Maxima expression and
     *   the results of up-conversion on it.
     * 
     * @throws MathsContentTooComplexException if the output from Maxima could not be
     *   up-converted, presumably because it is too complex.
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException 
     */
    public MathsContentOutputValueWrapper executeMathOutput(final String maximaExpression, final boolean simplify)
            throws MaximaTimeoutException, MathsContentTooComplexException {
        ConstraintUtilities.ensureNotNull(maximaExpression, "Maxima expression");
        logger.debug("executeMathOutput: expr={}, simp={}", maximaExpression, simplify);
        
        /* Do MathML output and up-convert */
        MathsContentOutputValueWrapper result = doExecuteMathOutput(maximaExpression, simplify);
        
        /* Fail fast if any up-conversion failures were found */
        List<UpConversionFailure> upConversionFailures = result.getUpConversionFailures();
        if (upConversionFailures!=null && !upConversionFailures.isEmpty()) {
            logger.warn("MathsContent output from Maxima expression " + maximaExpression
                    + " is too complex: failures are " + upConversionFailures);
            logger.warn("Up-converted PMathML was: " + result.getPMathML());
            logger.warn("Content MathML was: " + result.getCMathML());
            throw new MathsContentTooComplexException(result);
        }
        return result;
    }
    
    private MathsContentOutputValueWrapper doExecuteMathOutput(final String maximaExpression, final boolean simplify)
            throws MaximaTimeoutException {
        /* Chop off any trailing terminator */
        String resultingExpression = stripTrailingTerminator(maximaExpression);
        
        /* Convert to "simp:...$ mathml(expr)$" format */
        resultingExpression = "simp:" + simplify + "$ mathml(" + resultingExpression + ")$";
        
        /* Then execute and up-convert the results.
         * (Note that mathml() outputs its result to STDOUT) 
         */
        String rawMaximaMathMLOutput = maximaInteractiveProcess.executeCall(resultingExpression).trim();
        
        /* Up-convert the raw MathML and create appropriate wrapper */
        Document upconvertedDocument = maximaMathMLUpconverter.upconvertRawMaximaMathML(rawMaximaMathMLOutput);
        
        /* Create appropriate wrapper */
        return WrapperUtilities.createFromUpconvertedMaximaOutput(upconvertedDocument);
    }

    //------------------------------------------------
    // MathAssess extension methods
    
    /**
     * Performs the CAS work for <tt>ScriptRule</tt>.
     * 
     * @param maximaCode maxima code to be executed
     * @param simplify whether to turn Maxima simplification on or off when executing the
     *   given code
     * 
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException 
     */
    public void executeScriptRule(final String maximaCode, final boolean simplify)
            throws MaximaTimeoutException {
        logger.info("executeScriptRule: code={}, simp={}", maximaCode, simplify);
        ConstraintUtilities.ensureNotNull(maximaCode, "maximaCode");
        maximaInteractiveProcess.executeCallDiscardOutput("simp:" + simplify + "$ " + maximaCode);
    }
    
    /**
     * Performs the CAS work for <tt>CasProcess</tt>
     * 
     * @param maximaCode code to be executed returning a single value of the given type
     * @param simplify whether to turn Maxima simplification on or off when executing the
     *   given code
     * @param resultClass Class specifying required return type
     * @param <V> required return type
     * 
     * @return wrapper encapsulating the resulting QTI value
     * 
     * @throws MathAssessBadCasCodeException if the given Maxima code did not produce a result which
     *   could be parsed.
     * @throws QtiMaximaTypeConversionException if the output from Maxima could not be represented using
     *   the specified resultClass.
     * @throws MathsContentTooComplexException if the output from Maxima could not be
     *   up-converted, presumably because it is too complex.
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException 
     */
    @SuppressWarnings("unchecked")
    public <V extends ValueWrapper> V executeCasProcess(final String maximaCode,
            final boolean simplify, final Class<V> resultClass)
            throws MaximaTimeoutException, MathsContentTooComplexException {
        if (logger.isInfoEnabled()) {
            logger.info("executeCasProcess: code={}, simp={}, resultClass={}",
                    new Object[] { maximaCode, simplify, resultClass });
        }
        ConstraintUtilities.ensureNotNull(maximaCode, "maximaCode");
        ConstraintUtilities.ensureNotNull(resultClass, "resultClass");
        V result;
        
        /* What we do here depends on whether we are returning a MathsContent variable or not. */
        if (MathsContentValueWrapper.class.isAssignableFrom(resultClass)) {
            /* Get result as MathML */
            result = (V) executeMathOutput(maximaCode, simplify);
        }
        else {
            /* Get result using string() */
            result = executeStringOutput(maximaCode, simplify, resultClass);
        }
        return result;
    }

    /**
     * Performs the CAS work for <tt>CasCompare</tt> when authored with an explicit Maxima
     * code expression.
     * <p>
     * NOTE: The code passed here should <strong>NOT</strong> have a leading "casresult:"
     * string in it!
     * 
     * @param comparisonCode Maxima code that does the comparison work, expected to return
     *   a boolean value.
     * @param simplify whether to turn Maxima simplification on or off when executing the
     *   given code
     * @param arg1 encapsulates the first value/variable to be compared
     * @param arg2 encapsulates the second value/variable to be compared
     * 
     * @return true or false
     * 
     * @throws IllegalArgumentException if the given code is null, or if any of the arguments
     *   is null or represents a null value or is a {@link MathsContentValueWrapper}
     *   with a missing maximaInput field.
     * @throws MathAssessBadCasCodeException if the resulting Maxima call does not
     *   return either true or false
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException 
     */
    public boolean executeCasCompare(final String comparisonCode, final boolean simplify,
            final ValueOrVariableWrapper arg1, final ValueOrVariableWrapper arg2)
            throws MaximaTimeoutException {
        return executeCasCondition(comparisonCode, simplify, arg1, arg2);
    }
    
    /**
     * Performs the CAS work for <tt>CasCondition</tt>
     * 
     * @param comparisonCode Maxima code that does the comparison work, expected to return
     *   a boolean value.
     * @param simplify whether to turn Maxima simplification on or off when executing the
     *   given code
     * @param arguments array of arguments to be substituted into the Maxima code
     * 
     * @return true or false as reported by Maxima evaluating the given condition
     * 
     * @throws IllegalArgumentException if the given code is null, or if any of the arguments
     *   is null or represents a null value or is a {@link MathsContentValueWrapper}
     *   with a missing maximaInput field.
     * @throws MathAssessBadCasCodeException if the resulting Maxima call does not
     *   return either true or false
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException 
     */
    public boolean executeCasCondition(final String comparisonCode, final boolean simplify,
            final ValueOrVariableWrapper... arguments)
            throws MaximaTimeoutException {
        ConstraintUtilities.ensureNotNull(comparisonCode, "comparisonCode");
        
        /* Get maxima forms of the input values and perform substitutions */
        String maximaInput = comparisonCode;
        String searchRegexp, replacement;
        for (int i=0; i<arguments.length; i++) {
            ConstraintUtilities.ensureNotNull(arguments[i], "argument #" + (i+1));
            /* Using negative look-behind to allow things like $$ */
            searchRegexp = "(?<!\\$)\\$" + (i+1);
            
            /* Create replacement, adding brackets for safety and remembering to
             * escape any '$' in them */
            replacement = "(" + maximaDataBinder.toMaximaExpression(arguments[i]) + ")";
            replacement = replacement.replaceAll("[\\$\\\\]", "\\\\\\$");
            
            /* Perform search and replace */
            maximaInput = maximaInput.replaceAll(searchRegexp, replacement);
        }
        
        /* Replace any '$$' with literal '$' signs */
        maximaInput = maximaInput.replace("$$", "$");
        
        /* Now execute the appropriate call as a string(), book-ended by code to temporarily
         * turn on simplification, if required.
         */
        BooleanValueWrapper compareResult;
        try {
            compareResult = executeStringOutput(maximaInput, simplify, BooleanValueWrapper.class);
        }
        catch (QtiMaximaTypeConversionException e) {
            throw new MathAssessBadCasCodeException("Maxima call '" + maximaInput + "' did not return a boolean", maximaInput,
                    e.getMaximaOutput());
        }
        
        /* Log result on exit */
        boolean result = compareResult.getValue().booleanValue();
        if (logger.isInfoEnabled()) {
            logger.info("executeCasCondition: code={}, simp={}, args={} => {}", 
                    new Object[] { comparisonCode, simplify, Arrays.toString(arguments), result });
        }
        return result;
    }
    
    //------------------------------------------------
    // Helpers
    
    /**
     * Helper method to validate a QTI variable identifier to ensure it matches the restrictions
     * outlined in the spec. (This is designed to allow it to be used as a Maxima variable as-is.)
     * 
     * @param variableIdentifier QTI variable identifier
     * 
     * @throws IllegalArgumentException if the given variableIdentifier is not compatible with
     *   the allowed production rules
     */
    private void checkVariableIdentifier(final String variableIdentifier) {
        ConstraintUtilities.ensureNotNull(variableIdentifier, "variableIdentifier");
      
        /* Ensure that the name matches NCName intersected with alphanumeric */
        if (!Pattern.matches("[a-zA-Z][a-zA-Z0-9]*", variableIdentifier)) {
            throw new IllegalArgumentException("variableIdentifier is not a suitably restricted NCName as defined in the MathAssess spec");
        }
    }
    
    //------------------------------------------------
    // Substitution of sub-expressions in MathML

    /**
     * Traverses through the DOM tree of the given MathML <tt>math</tt> element, substituting
     * any <tt>mi</tt> elements corresponding to Maxima values of the same name with their
     * values (as MathML fragments).
     * 
     * @throws MathAssessCasException if a problem occurred converting one of the variables to MathML
     * @throws MaximaTimeoutException
     * @throws MaximaProcessTerminatedException
     */
    public void substituteVariables(Element rawMathMLElement)
            throws MaximaTimeoutException {
        ConstraintUtilities.ensureNotNull(rawMathMLElement, "MathML Element");
        /* We'll create a little hash to store variable lookups as we traverse the <math/>
         * element just in case we have the same variable more than once. This will save
         * having to make extra calls.
         */
        Map<String,MathsContentOutputValueWrapper> valuesCache = new HashMap<String, MathsContentOutputValueWrapper>();
        
        /* Now walk the <math/> XML tree making substitutions as required, grafting into the tree */
        try {
            doSubstituteVariables(rawMathMLElement, valuesCache);
        }
        catch (QtiMaximaTypeConversionException e) {
            /* This shouldn't happen as conversion to MathsContent should always succeed! */
            throw new MathAssessCasException("Did not expect to get a QtiMaximaTypeConversionException during variable substitutions!");
        }
    }
    
    private void doSubstituteVariables(Element mathMLElement, Map<String,MathsContentOutputValueWrapper> valuesCache)
            throws MaximaTimeoutException, MathAssessBadCasCodeException, QtiMaximaTypeConversionException, MaximaProcessTerminatedException {
        /* Search child elements */
        NodeList childNodes = mathMLElement.getChildNodes();
        Node childNode;
        Element childElement;
        for (int i=0, size=childNodes.getLength(); i<size; i++) {
            childNode = childNodes.item(i);
            if (childNode.getNodeType()==Node.ELEMENT_NODE) {
                childElement = (Element) childNode;
                if (MathMLUtilities.isMathMLElement(childElement, "mi")) {
                    doHandleMiElement(childElement, valuesCache);
                }
                else {
                    /* Continue recursively.
                     * TODO: Should we avoid a possible stack overflow by flattening this out?
                     */
                    doSubstituteVariables(childElement, valuesCache);
                }
            }
        }
    }
    
    private void doHandleMiElement(Element miElement, Map<String,MathsContentOutputValueWrapper> valuesCache)
            throws QtiMaximaTypeConversionException, MaximaProcessTerminatedException {
        /* Extract text content and see if it's a variable */
        StringBuilder contentBuilder = new StringBuilder();
        NodeList contentList = miElement.getChildNodes();
        for (int i=0, size=contentList.getLength(); i<size; i++) {
            contentBuilder.append(contentList.item(i).getNodeValue());
        }
        String varIdentifier = contentBuilder.toString();
        if (varIdentifier.length()>0) {
            MathsContentOutputValueWrapper mathValue = doExtractValue(varIdentifier, valuesCache);
            if (mathValue!=null) {
                /* It's defined in Maxima, so make the subs */
                doSubstituteVariableValue(miElement, mathValue);
            }
        }
    }
    
    private MathsContentOutputValueWrapper doExtractValue(final String varIdentifier,
            Map<String,MathsContentOutputValueWrapper> valuesCache)
            throws QtiMaximaTypeConversionException, MaximaProcessTerminatedException {
        /* Note that we're caching 'null' lookups here */
        MathsContentOutputValueWrapper result = null;
        if (valuesCache.containsKey(varIdentifier)) {
            result = valuesCache.get(varIdentifier);
        }
        else {
            /* Ask Maxima for the value of the variable, but don't fail if up-conversion fails
             * as all we're going to do is interpolate the resulting PMathML.
             */
            try {
                result = doExecuteMathOutput(varIdentifier, false);
            }
            catch (MaximaTimeoutException e) {
                throw new MathAssessCasException("Unexpected timeout extracting MathML value of variable " + varIdentifier);
            }
            valuesCache.put(varIdentifier, result);
        }
        return result;
    }
    
    private void doSubstituteVariableValue(Element miElement, final MathsContentOutputValueWrapper value) {
        Node parentNode = miElement.getParentNode();
        
        /* Need to adopt a clone of the (single) child of the enclosing <math> element in the isolated PMathML Element */
        Node pmathContent = value.getPMathMLElement().getChildNodes().item(0);
        Node pmathContentCloned = pmathContent.cloneNode(true);
        Node importedNode = parentNode.getOwnerDocument().adoptNode(pmathContentCloned);
        
        /* Replace <mi/> element with this value */
        parentNode.replaceChild(importedNode, miElement);
    }
}
