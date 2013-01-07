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
package uk.ac.ed.ph.qtiworks.mathassess.glue.maxima;

import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.MaximaDataBinder;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.BooleanMultipleValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.BooleanOrderedValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.BooleanValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.FloatMultipleValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.FloatOrderedValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.FloatValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.IntegerMultipleValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.IntegerOrderedValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.IntegerValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.StringMultipleValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.StringOrderedValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.StringValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.WrapperUtilities;

import java.util.Arrays;
import java.util.Collection;

/**
 * Samples to use for various tests of {@link MaximaDataBinder}.
 *
 * @author David McKain
 */
final class MaximaDataBindingSamples {
    
    public static Collection<Object[]> CIRCULAR_EXAMPLES = Arrays.asList(new Object[][] { { "false", new BooleanValueWrapper(false) }, { "true", new BooleanValueWrapper(true) }, { "0", new IntegerValueWrapper(0) }, { "1", new IntegerValueWrapper(1) }, { "-1", new IntegerValueWrapper(-1) }, { "0.0", new FloatValueWrapper(0.0) }, { "1.0", new FloatValueWrapper(1.0) }, { "1.23", new FloatValueWrapper(1.23) }, { "-3.14", new FloatValueWrapper(-3.14) }, { "\"\"", new StringValueWrapper("") }, { "\"a\"", new StringValueWrapper("a") }, { "\"a\\\"b\"", new StringValueWrapper("a\"b") },
//            { "[0, 1]", new PointValueWrapper(new Integer[] { Integer.valueOf(0), Integer.valueOf(1) }) },
 { "{}", WrapperUtilities.createCompoundValue(BooleanMultipleValueWrapper.class) }, { "{}", WrapperUtilities.createCompoundValue(IntegerMultipleValueWrapper.class) }, { "{}", WrapperUtilities.createCompoundValue(FloatMultipleValueWrapper.class) },
//            { "{}", WrapperUtilities.createCompoundValue(PointMultipleValueWrapper.class) },
// NB: The next one doesn't work the way it looks at first hand!
//            { "{}", WrapperUtilities.createCompoundValue(StringMultipleValueWrapper.class) },
 { "[]", WrapperUtilities.createCompoundValue(BooleanOrderedValueWrapper.class) }, { "[]", WrapperUtilities.createCompoundValue(IntegerOrderedValueWrapper.class) }, { "[]", WrapperUtilities.createCompoundValue(FloatOrderedValueWrapper.class) },
//            { "[]", WrapperUtilities.createCompoundValue(PointOrderedValueWrapper.class) },
// NB: The next one doesn't work the way it looks at first hand!
//            { "[]", WrapperUtilities.createCompoundValue(StringOrderedValueWrapper.class) },
 { "{false}", WrapperUtilities.createCompoundValue(BooleanMultipleValueWrapper.class, BooleanValueWrapper.class, Boolean.FALSE) }, { "{true}", WrapperUtilities.createCompoundValue(BooleanMultipleValueWrapper.class, BooleanValueWrapper.class, Boolean.TRUE) }, { "[false]", WrapperUtilities.createCompoundValue(BooleanOrderedValueWrapper.class, BooleanValueWrapper.class, Boolean.FALSE) }, { "[true]", WrapperUtilities.createCompoundValue(BooleanOrderedValueWrapper.class, BooleanValueWrapper.class, Boolean.TRUE) }, { "[false, false]", WrapperUtilities.createCompoundValue(BooleanOrderedValueWrapper.class, BooleanValueWrapper.class, Boolean.FALSE, Boolean.FALSE) }, { "[false, false, true]", WrapperUtilities.createCompoundValue(BooleanOrderedValueWrapper.class, BooleanValueWrapper.class, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE) }, { "{0}", WrapperUtilities.createCompoundValue(IntegerMultipleValueWrapper.class, IntegerValueWrapper.class, Integer.valueOf(0)) }, { "{1}", WrapperUtilities.createCompoundValue(IntegerMultipleValueWrapper.class, IntegerValueWrapper.class, Integer.valueOf(1)) }, { "[0]", WrapperUtilities.createCompoundValue(IntegerOrderedValueWrapper.class, IntegerValueWrapper.class, Integer.valueOf(0)) }, { "[0, 1]", WrapperUtilities.createCompoundValue(IntegerOrderedValueWrapper.class, IntegerValueWrapper.class, Integer.valueOf(0), Integer.valueOf(1)) },
//            { "[[0, 1], [1, 2]]", WrapperUtilities.createCompoundValue(PointOrderedValueWrapper.class, PointValueWrapper.class,
//                    new Integer[] { Integer.valueOf(0), Integer.valueOf(1) },
//                    new Integer[] { Integer.valueOf(1), Integer.valueOf(2) }) },
 { "{0}", WrapperUtilities.createCompoundValue(IntegerMultipleValueWrapper.class, IntegerValueWrapper.class, Integer.valueOf(0)) }, { "{1}", WrapperUtilities.createCompoundValue(IntegerMultipleValueWrapper.class, IntegerValueWrapper.class, Integer.valueOf(1)) }, { "[0]", WrapperUtilities.createCompoundValue(IntegerOrderedValueWrapper.class, IntegerValueWrapper.class, Integer.valueOf(0)) }, { "{0.0}", WrapperUtilities.createCompoundValue(FloatMultipleValueWrapper.class, FloatValueWrapper.class, Double.valueOf(0.0)) }, { "{1.0}", WrapperUtilities.createCompoundValue(FloatMultipleValueWrapper.class, FloatValueWrapper.class, Double.valueOf(1.0)) },
//            { "{[0, 1]}", WrapperUtilities.createCompoundValue(PointMultipleValueWrapper.class, PointValueWrapper.class,
//                    new Integer[][] { { Integer.valueOf(0), Integer.valueOf(1) } }) },
 { "[0.0]", WrapperUtilities.createCompoundValue(FloatOrderedValueWrapper.class, FloatValueWrapper.class, Double.valueOf(0.0)) }, { "[0.0, 1.0]", WrapperUtilities.createCompoundValue(FloatOrderedValueWrapper.class, FloatValueWrapper.class, Double.valueOf(0.0), Double.valueOf(1.0)) }, { "{\"\"}", WrapperUtilities.createCompoundValue(StringMultipleValueWrapper.class, StringValueWrapper.class, "") }, { "{\"a\"}", WrapperUtilities.createCompoundValue(StringMultipleValueWrapper.class, StringValueWrapper.class, "a") }, { "{\"\\\"\"}", WrapperUtilities.createCompoundValue(StringMultipleValueWrapper.class, StringValueWrapper.class, "\"") }, { "[\"\"]", WrapperUtilities.createCompoundValue(StringOrderedValueWrapper.class, StringValueWrapper.class, "") }, { "[\"a\"]", WrapperUtilities.createCompoundValue(StringOrderedValueWrapper.class, StringValueWrapper.class, "a") }, { "[\"\\\"\"]", WrapperUtilities.createCompoundValue(StringOrderedValueWrapper.class, StringValueWrapper.class, "\"") }, { "[\"a\", \"b\"]", WrapperUtilities.createCompoundValue(StringOrderedValueWrapper.class, StringValueWrapper.class, "a", "b") }, { "[\"\\\"a,b\\\"\", \"a\", \"b\"]", WrapperUtilities.createCompoundValue(StringOrderedValueWrapper.class, StringValueWrapper.class, "\"a,b\"", "a", "b") }
    });
    
    public static Collection<Object[]> BAD_PARSING_EXAMPLES = Arrays.asList(new Object[][] { { "", BooleanValueWrapper.class }, { "falsey", BooleanValueWrapper.class }, { "truey", BooleanValueWrapper.class }, { "0", BooleanValueWrapper.class }, { "1.23", BooleanValueWrapper.class }, { "\"a\"", BooleanValueWrapper.class }, { "{1}", BooleanValueWrapper.class }, { "{1.23}", BooleanValueWrapper.class }, { "{\"a\"}", BooleanValueWrapper.class }, { "{false}", BooleanValueWrapper.class }, { "[1]", BooleanValueWrapper.class }, { "[1.23]", BooleanValueWrapper.class }, { "[\"a\"]", BooleanValueWrapper.class }, { "[false]", BooleanValueWrapper.class },
             { "", IntegerValueWrapper.class }, { "1x", IntegerValueWrapper.class }, { "false", IntegerValueWrapper.class }, { "1.23", IntegerValueWrapper.class }, { "\"a\"", IntegerValueWrapper.class }, { "{1}", IntegerValueWrapper.class }, { "{1.23}", IntegerValueWrapper.class }, { "{\"a\"}", IntegerValueWrapper.class }, { "{false}", IntegerValueWrapper.class }, { "[1]", IntegerValueWrapper.class }, { "[1.23]", IntegerValueWrapper.class }, { "[\"a\"]", IntegerValueWrapper.class }, { "[false]", IntegerValueWrapper.class },
             { "", FloatValueWrapper.class }, { "false", FloatValueWrapper.class }, { "1.x", FloatValueWrapper.class }, { "\"a\"", FloatValueWrapper.class }, { "{1}", FloatValueWrapper.class }, { "{1.23}", FloatValueWrapper.class }, { "{\"a\"}", FloatValueWrapper.class }, { "{false}", FloatValueWrapper.class }, { "[1]", FloatValueWrapper.class }, { "[1.23]", FloatValueWrapper.class }, { "[\"a\"]", FloatValueWrapper.class }, { "[false]", FloatValueWrapper.class },
             { "\"a", StringValueWrapper.class }, { "false", StringValueWrapper.class }, { "1", StringValueWrapper.class }, { "1.23", StringValueWrapper.class }, { "{1}", StringValueWrapper.class }, { "{1.23}", StringValueWrapper.class }, { "{\"a\"}", StringValueWrapper.class }, { "{false}", StringValueWrapper.class }, { "[1]", StringValueWrapper.class }, { "[1.23]", StringValueWrapper.class }, { "[\"a\"]", StringValueWrapper.class }, { "[false]", StringValueWrapper.class },
             { "", BooleanMultipleValueWrapper.class }, { "false", BooleanMultipleValueWrapper.class }, { "1", BooleanMultipleValueWrapper.class }, { "1.23", BooleanMultipleValueWrapper.class }, { "{1}", BooleanMultipleValueWrapper.class }, { "{1.23}", BooleanMultipleValueWrapper.class }, { "{\"a\"}", BooleanMultipleValueWrapper.class }, { "[1]", BooleanMultipleValueWrapper.class }, { "[1.23]", BooleanMultipleValueWrapper.class }, { "[\"a\"]", BooleanMultipleValueWrapper.class }, { "[false]", BooleanMultipleValueWrapper.class }, { "", BooleanOrderedValueWrapper.class }, { "false", BooleanOrderedValueWrapper.class }, { "1", BooleanOrderedValueWrapper.class }, { "1.23", BooleanOrderedValueWrapper.class }, { "{1}", BooleanOrderedValueWrapper.class }, { "{1.23}", BooleanOrderedValueWrapper.class }, { "{\"a\"}", BooleanOrderedValueWrapper.class }, { "{false}", BooleanOrderedValueWrapper.class }, { "[1]", BooleanOrderedValueWrapper.class }, { "[1.23]", BooleanOrderedValueWrapper.class }, { "[\"a\"]", BooleanOrderedValueWrapper.class },
             { "", IntegerMultipleValueWrapper.class }, { "false", IntegerMultipleValueWrapper.class }, { "1", IntegerMultipleValueWrapper.class }, { "1.23", IntegerMultipleValueWrapper.class }, { "{float}", IntegerMultipleValueWrapper.class }, { "{1.23}", IntegerMultipleValueWrapper.class }, { "{\"a\"}", IntegerMultipleValueWrapper.class }, { "[1]", IntegerMultipleValueWrapper.class }, { "[1.23]", IntegerMultipleValueWrapper.class }, { "[\"a\"]", IntegerMultipleValueWrapper.class }, { "[false]", IntegerMultipleValueWrapper.class }, { "", IntegerOrderedValueWrapper.class }, { "false", IntegerOrderedValueWrapper.class }, { "1", IntegerOrderedValueWrapper.class }, { "1.23", IntegerOrderedValueWrapper.class }, { "{false}", IntegerOrderedValueWrapper.class }, { "{1.23}", IntegerOrderedValueWrapper.class }, { "{\"a\"}", IntegerOrderedValueWrapper.class }, { "[false]", IntegerOrderedValueWrapper.class }, { "[1.23]", IntegerOrderedValueWrapper.class }, { "[\"a\"]", IntegerOrderedValueWrapper.class },
             { "", FloatMultipleValueWrapper.class }, { "false", FloatMultipleValueWrapper.class }, { "1", FloatMultipleValueWrapper.class }, { "1.23", FloatMultipleValueWrapper.class }, { "{false}", FloatMultipleValueWrapper.class }, { "{\"a\"}", FloatMultipleValueWrapper.class }, { "[1]", FloatMultipleValueWrapper.class }, { "[1.23]", FloatMultipleValueWrapper.class }, { "[\"a\"]", FloatMultipleValueWrapper.class }, { "[false]", FloatMultipleValueWrapper.class }, { "", FloatOrderedValueWrapper.class }, { "false", FloatOrderedValueWrapper.class }, { "1", FloatOrderedValueWrapper.class }, { "1.23", FloatOrderedValueWrapper.class }, { "{false}", FloatOrderedValueWrapper.class }, { "{1}", FloatOrderedValueWrapper.class }, { "{1.23}", FloatOrderedValueWrapper.class }, { "{\"a\"}", FloatOrderedValueWrapper.class }, { "{false}", FloatOrderedValueWrapper.class }, { "[\"a\"]", FloatOrderedValueWrapper.class },
             { "", StringMultipleValueWrapper.class }, { "false", StringMultipleValueWrapper.class }, { "1", StringMultipleValueWrapper.class }, { "1.23", StringMultipleValueWrapper.class }, { "{false}", StringMultipleValueWrapper.class }, { "{1}", StringMultipleValueWrapper.class }, { "{1.23}", StringMultipleValueWrapper.class }, { "[1]", StringMultipleValueWrapper.class }, { "[1.23]", StringMultipleValueWrapper.class }, { "[\"a\"]", StringMultipleValueWrapper.class }, { "[false]", StringMultipleValueWrapper.class }, { "", StringOrderedValueWrapper.class }, { "false", StringOrderedValueWrapper.class }, { "1", StringOrderedValueWrapper.class }, { "1.23", StringOrderedValueWrapper.class }, { "{1}", StringOrderedValueWrapper.class }, { "{1.23}", StringOrderedValueWrapper.class }, { "{\"a\"}", StringOrderedValueWrapper.class }, { "{false}", StringOrderedValueWrapper.class }, { "[false]", StringOrderedValueWrapper.class }, { "[1]", StringOrderedValueWrapper.class }, { "[1.23]", StringOrderedValueWrapper.class },
    });
}
