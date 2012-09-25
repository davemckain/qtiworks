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

import static uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess.MAXIMA_EQUAL_CODE;
import static uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess.MAXIMA_SYNTEQUAL_CODE;


import uk.ac.ed.ph.qtiworks.mathassess.glue.AsciiMathHelper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.MathAssessBadCasCodeException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.MathsContentTooComplexException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.BooleanValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.IntegerValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MathsContentValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.NullValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.StringValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueWrapper;
import uk.ac.ed.ph.snuggletex.utilities.MathMLUtilities;
import uk.ac.ed.ph.snuggletex.utilities.SerializationOptions;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Basic tests for the {@link QtiMaximaProcess} class.
 *
 * @author David McKain
 */
public class QtiMaximaSessionTest extends QtiMaximaSessionTestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(QtiMaximaSessionTest.class);

    //-----------------------------------------------------------
    // Basic Tests for executeStringOutput()
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecuteStringOutputNull() throws Exception {
        process.executeStringOutput(null, false);
    }
    
    @Test
    public void testExecuteStringOutputBlank() throws Exception {
        String result = process.executeStringOutput("", false);
        Assert.assertEquals("", result);
    }
    
    @Test
    public void testExecuteStringOutputMultiline() throws Exception {
        String result = process.executeStringOutput("\"a\nb\"", false);
        Assert.assertEquals("\"a\nb\"", result); /* (We raw output from Maxima is tidied up for us here) */
    }
    
    @Test(expected=MathAssessBadCasCodeException.class)
    public void testExecuteStringOutputBad() throws Exception {
        process.executeStringOutput("+", false);
    }
    
    @Test
    public void testExecuteStringNoSimplification() throws Exception {
        String result = process.executeStringOutput("1+1", false);
        Assert.assertEquals("1+1", result);
    }
    
    @Test
    public void testExecuteStringWithSimplification() throws Exception {
        String result = process.executeStringOutput("1+1", true);
        Assert.assertEquals("2", result);
    }
    
    //-----------------------------------------------------------
    // Basic Tests for executeMathOutput()
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecuteMathOutputNull() throws Exception {
        process.executeMathOutput(null, false);
    }
    
    public void testExecuteMathOutputSimple() throws Exception {
        MathsContentValueWrapper result = process.executeMathOutput("1", false);

        /* This is all I'll do here... the MathML stuff is tested in detail elsewhere! */
        Assert.assertEquals("1", result.getMaximaInput());
    }
    
    public void testExecuteMathOutputWithSimplfication() throws Exception {
        MathsContentValueWrapper result = process.executeMathOutput("1+1", true);

        /* This is all I'll do here... the MathML stuff is tested in detail elsewhere! */
        Assert.assertEquals("1+1", result.getMaximaInput());
    }
    
    @Test(expected=MathsContentTooComplexException.class)
    public void testExecuteMathOutputTooComplex() throws Exception {
        process.executeMathOutput("gamma(x)", false);
    }
    
    //-----------------------------------------------------------
    // Basic Tests for passQTIVariableToMaxima()
    
    @Test(expected=IllegalArgumentException.class)
    public void testPassVariableNull1() throws Exception {
        process.passQTIVariableToMaxima(null, new BooleanValueWrapper(false));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testPassVariableBlank() throws Exception {
        process.passQTIVariableToMaxima("", new BooleanValueWrapper(false));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testPassVariableUnicode() throws Exception {
        process.passQTIVariableToMaxima("\u00eb", new BooleanValueWrapper(false));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testPassVariableMathNoMaxima() throws Exception {
        process.passQTIVariableToMaxima("\u00eb", new MathsContentValueWrapper());
    }
    
    @Test
    public void testPassVariableBooleanGood() throws Exception {
        /* Set 'x' to the boolean value true */
        process.passQTIVariableToMaxima("x", new BooleanValueWrapper(true));
        
        /* Extract value using string() then we'll just check directly ourselves */
        String output = process.executeStringOutput("x", false);
        Assert.assertEquals("true", output);
    }
    
    @Test
    public void testPassVariableUnsetGood1() throws Exception {
        /* Temporarily set 'x' to the boolean value true */
        process.passQTIVariableToMaxima("x", new BooleanValueWrapper(true));
        
        /* Then we'll clear it */
        process.passQTIVariableToMaxima("x", NullValueWrapper.INSTANCE);
        
        /* Make sure it is really unset */
        String output = process.executeStringOutput("x", false);
        Assert.assertEquals("x", output);
    }
    
    @Test
    public void testPassVariableUnsetGood2() throws Exception {
        /* Temporarily set 'x' to the boolean value true */
        process.passQTIVariableToMaxima("x", new BooleanValueWrapper(true));
        
        /* Then we'll clear it (but differently to the first test) */
        process.passQTIVariableToMaxima("x", new BooleanValueWrapper(null));
        
        /* Make sure it is really unset */
        String output = process.executeStringOutput("x", false);
        Assert.assertEquals("x", output);
    }
    
    //-----------------------------------------------------------
    // Basic Tests for queryMaximaVariable()
    
    @Test(expected=IllegalArgumentException.class)
    public void testQueryMaximaVariableNull1() throws Exception {
        process.queryMaximaVariable(null, BooleanValueWrapper.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testQueryMaximaVariableBlank() throws Exception {
        process.queryMaximaVariable("", BooleanValueWrapper.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testQueryMaximaVariableUnicode() throws Exception {
        process.queryMaximaVariable("\u00eb", BooleanValueWrapper.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testQueryMaximaVariableNull2() throws Exception {
        process.queryMaximaVariable("x", null);
    }
    
    @Test()
    public void testQueryMaximaVariableUndefined() throws Exception {
        BooleanValueWrapper result = process.queryMaximaVariable("x", BooleanValueWrapper.class);
        
        Assert.assertNull(result);
    }
    
    @Test()
    public void testQueryMaximaVariableGood() throws Exception {
        ValueWrapper input = new BooleanValueWrapper(true);
        process.passQTIVariableToMaxima("x", input);
        BooleanValueWrapper result = process.queryMaximaVariable("x", BooleanValueWrapper.class);
        
        Assert.assertEquals(input, result);
    }
    
    @Test()
    public void testQueryMaximaVariableGoodCastToMath() throws Exception {
        ValueWrapper input = new BooleanValueWrapper(true);
        process.passQTIVariableToMaxima("x", input);
        
        /* We'll extract the result in a different form */
        MathsContentValueWrapper result = process.queryMaximaVariable("x", MathsContentValueWrapper.class);
        Assert.assertEquals("true", result.getMaximaInput());
    }
    
    @Test(expected=QtiMaximaTypeConversionException.class)
    public void testQueryMaximaVariableBadCast() throws Exception {
        ValueWrapper input = new BooleanValueWrapper(true);
        process.passQTIVariableToMaxima("x", input);
        
        /* We'll extract the result in a different form */
        process.queryMaximaVariable("x", IntegerValueWrapper.class);
    }
    
    //-----------------------------------------------------------
    // Tests for executeScriptRule()
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecuteScriptRuleNull() throws Exception {
        process.executeScriptRule(null, false);
    }
    
    @Test()
    public void testExecuteScriptRuleGood() throws Exception {
        /* Trivial script that sets the var 'x' to 1 */
        process.executeScriptRule("x:1$", false);
        
        /* Check that this did what we expected */
        Assert.assertEquals(1, process.queryMaximaVariable("x", IntegerValueWrapper.class).getValue().intValue());
    }
    
    @Test()
    public void testExecuteScriptRuleGoodSimplify() throws Exception {
        /* Trivial script that sets the var 'x' to 1+1 simplified */
        process.executeScriptRule("x:1+1$", true);
        
        /* Check that this did what we expected */
        Assert.assertEquals(2, process.queryMaximaVariable("x", IntegerValueWrapper.class).getValue().intValue());
    }
    
    //-----------------------------------------------------------
    // Tests for executeCasProcess()
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecuteCasProcessNull1() throws Exception {
        process.executeCasProcess(null, false, IntegerValueWrapper.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecuteCasProcessNull2() throws Exception {
        process.executeCasProcess("1", false, null);
    }
    
    @Test()
    public void testExecuteCasProcessGood() throws Exception {
        /* Do 1+1 = 2 explicitly ! */
        IntegerValueWrapper result = process.executeCasProcess("ev(1+1, simp)", false, IntegerValueWrapper.class);
        Assert.assertEquals(2, result.getValue().intValue());
    }
    
    @Test()
    public void testExecuteCasProcessGoodWithSimplify() throws Exception {
        /* Do 1+1 = 2 the other way */
        IntegerValueWrapper result = process.executeCasProcess("1+1", true, IntegerValueWrapper.class);
        Assert.assertEquals(2, result.getValue().intValue());
    }
    
    @Test(expected=MathAssessBadCasCodeException.class)
    public void testExecuteCasProcessBadCode() throws Exception {
        process.executeCasProcess("=", false, IntegerValueWrapper.class);
    }
    
    @Test(expected=QtiMaximaTypeConversionException.class)
    public void testExecuteCasProcessWrongReturnType() throws Exception {
        process.executeCasProcess("3.14", false, IntegerValueWrapper.class);
    }
    
    //-----------------------------------------------------------
    // Tests for executeCasCompare()
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecuteCasCompareNull1() throws Exception {
        process.executeCasCompare(null, false, new BooleanValueWrapper(true), new BooleanValueWrapper(true));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecuteCasCompareNull2() throws Exception {
        process.executeCasCompare(MAXIMA_SYNTEQUAL_CODE, false, null, new BooleanValueWrapper(true));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecuteCasCompareNull3() throws Exception {
        process.executeCasCompare(MAXIMA_SYNTEQUAL_CODE, false, new BooleanValueWrapper(true), null);
    }
    
    @Test
    public void testExecuteCasCompareGood1() throws Exception {
        Assert.assertEquals(true, process.executeCasCompare(MAXIMA_SYNTEQUAL_CODE, false,
                new BooleanValueWrapper(true), new BooleanValueWrapper(true)));
    }
    
    @Test
    public void testExecuteCasCompareGood1A() throws Exception {
        Assert.assertEquals(false, process.executeCasCompare(MAXIMA_SYNTEQUAL_CODE, false,
                new BooleanValueWrapper(true), new IntegerValueWrapper(23)));
    }
    
    @Test
    public void testExecuteCasCompareGood2() throws Exception {
        /* Automatic simplification of 1+1 */
        Assert.assertEquals(true, process.executeCasCompare(MAXIMA_SYNTEQUAL_CODE, true,
                process.executeMathOutput("1+1", false), new IntegerValueWrapper(2)));
    }
    
    @Test
    public void testExecuteCasCompareGood2A() throws Exception {
        Assert.assertEquals(true, process.executeCasCompare(MAXIMA_SYNTEQUAL_CODE, true,
                process.executeMathOutput("1+1", false), process.executeMathOutput("3-1", false)));
    }
    
    @Test
    public void testExecuteCasCompareGood2B() throws Exception {
        Assert.assertEquals(false, process.executeCasCompare(MAXIMA_SYNTEQUAL_CODE, true,
                process.executeMathOutput("1+1", false), process.executeMathOutput("1+2", false)));
    }
    
    @Test
    public void testExecuteCasCompareUnknown() throws Exception {
        /* Test equality of 5 and x, which Maxima returns as 'unknown' but gets treated as false here by the boilerplate code. */
        Assert.assertEquals(false, process.executeCasCompare(MAXIMA_EQUAL_CODE, false,
                process.executeMathOutput("x", false), process.executeMathOutput("5", false)));
    }
    
    //-----------------------------------------------------------
    // Tests for executeCasCondition()
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecuteCasConditionNull() throws Exception {
        process.executeCasCondition(null, false);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testExecuteCasConditionNull2() throws Exception {
        process.executeCasCondition(null, false, (ValueWrapper) null);
    }
    
    @Test()
    public void testExecuteCasConditionNullTrivial() throws Exception {
        /* This condition has no parameters! */
        Assert.assertEquals(true, process.executeCasCondition("true", false));
    }
    
    @Test()
    public void testExecuteCasConditionGood1() throws Exception {
        /* (Ends up doing is(x+1=x+1), which is true without simplification) */
        Assert.assertEquals(true, process.executeCasCondition("is($1+$2=$3)", false,
                process.executeMathOutput("x", false), new IntegerValueWrapper(1),
                process.executeMathOutput("x+1", false)));
    }
    
    @Test()
    public void testExecuteCasConditionGood1A() throws Exception {
        /* (Ends up doing is(x+1=1+x), which is false without simplification.
         * Note there are hidden depths here! Maxima gets the x+1 passed to it before
         * simplification is turned off, and rearranges it into its own form which also
         * happens to be x+1.
         */
        Assert.assertEquals(false, process.executeCasCondition("is($1+$2=$3)", false,
                new IntegerValueWrapper(1), process.executeMathOutput("x", false),
                process.executeMathOutput("x+1", false)));
    }
    
    
    @Test()
    public void testExecuteCasConditionGood1C() throws Exception {
        /* (Ends up doing is(x+1=1+x), which is true with simplification) */
        Assert.assertEquals(true, process.executeCasCondition("is($1+$2=$3)", true,
                new IntegerValueWrapper(1), process.executeMathOutput("x", false),
                process.executeMathOutput("x+1", false)));
    }
    
    @Test()
    public void testExecuteCasConditionGood2() throws Exception {
        /* This does x*x = x^2 */
        Assert.assertEquals(true, process.executeCasCondition("is(equal($1 * $2, $3))", false,
                process.executeMathOutput("x", false), process.executeMathOutput("x", false),
                process.executeMathOutput("x^2", false)));
    }
    
    @Test
    public void testExecuteCasConditionWeird1() throws Exception {
        /* String comparison (unlikely!).
         * 
         * This ends up doing is("$"=("$")), which is true.
         */
        Assert.assertEquals(true, process.executeCasCondition("is(\"$$\"=$1)", false,
                new StringValueWrapper("$")));
    }
    
    @Test
    public void testExecuteCasConditionWeird2() throws Exception {
        /* This does is("$1"=("$1")), which is true */
        Assert.assertEquals(true, process.executeCasCondition("is(\"$$1\"=$1)", false,
                new StringValueWrapper("$1")));
    }
    
    @Test
    public void testExecuteCasConditionWeird3() throws Exception {
        /* This does is("$2"=("$2")), which is true.
         * 
         * This exploits the fact that there's no second argument so $2 remains as $2.
         */
        Assert.assertEquals(true, process.executeCasCondition("is(\"$2\"=$1)", false,
                new StringValueWrapper("$2")));
    }
    
    @Test(expected=MathAssessBadCasCodeException.class)
    public void testExecuteCasConditionBadCode() throws Exception {
        process.executeCasCondition("=", false);
    }
    
    //-----------------------------------------------------------
    // Tests substitution
    
    @Test(expected=IllegalArgumentException.class)
    public void testSubstituteNull() throws Exception {
        process.substituteVariables(null);
    }
    
    @Test()
    public void testSubstituteSimple() throws Exception {
        /* This substitutes the value of 'n' in the given expression */
        process.passQTIVariableToMaxima("n", new IntegerValueWrapper(23));

        Element mathmlElement = makeAuthoredMathML("<mi>n</mi><mi>x</mi>");
        process.substituteVariables(mathmlElement);
        
        assertMathMLContent("<mn>23</mn><mi>x</mi>", mathmlElement);
    }
    
    @Test()
    public void testSubstituteSimpleRepeated() throws Exception {
        process.passQTIVariableToMaxima("n", new IntegerValueWrapper(23));
        
        Element mathmlElement = makeAuthoredMathML("<mi>n</mi><mi>n</mi>");
        process.substituteVariables(mathmlElement);
        
        assertMathMLContent("<mn>23</mn><mn>23</mn>", mathmlElement);
    }
    
    @Test()
    public void testSubstituteComplex() throws Exception {
        MathsContentValueWrapper nValue = new AsciiMathHelper().createMathsContentFromASCIIMath("sin x");
        process.passQTIVariableToMaxima("n", nValue);
        
        Element mathmlElement = makeAuthoredMathML("<mi>n</mi><mi>x</mi>");
        process.substituteVariables(mathmlElement);
        assertMathMLContent("<mrow><mi>sin</mi><mo>\u2061</mo><mi>x</mi></mrow><mi>x</mi>", mathmlElement);
    }
    
// This one needs a bit more thought about exactly how best to proceed, but there's
// definitely a solution somewhere.
//    @Test()
//    public void testSubstituteOperator() throws Exception {
//        /* This substitutes the value of 'n' in the given expression */
//        Element mathmlElement = makeAuthoredMathML("<mi>x</mi><mi>op</mi><mi>y</mi>");
//        process.passQTIVariableToMaxima("op", new StringValueWrapper("+"));
//        process.substituteVariables(mathmlElement);
//        
//        assertMathMLContent("<mi>x</mi><mo>+</mo><mi>y</mi>", mathmlElement);
//    }
    
    private Element makeAuthoredMathML(String content) throws IOException, SAXException {
        String mathmlDocument = "<math xmlns='http://www.w3.org/1998/Math/MathML'>" + content + "</math>";
        return MathMLUtilities.parseMathMLDocumentString(mathmlDocument).getDocumentElement();
    }
    
    private void assertMathMLContent(String expectedContent, Element mathmlElement) {
        SerializationOptions serializationOptions = new SerializationOptions();
        String resultMathML = MathMLUtilities.serializeElement(mathmlElement, serializationOptions);
        String resultContent = resultMathML
            .replaceAll("^.*<math.+?>", "")
            .replaceAll("</math>.*$", "")
            .replaceAll(">\\s<", "><");
        if (!expectedContent.equals(resultContent)) {
            logger.info("Expected: " + expectedContent);
            logger.info("Actual:   " + resultContent);
            Assert.assertEquals(expectedContent, resultContent);
        }
    }
}
