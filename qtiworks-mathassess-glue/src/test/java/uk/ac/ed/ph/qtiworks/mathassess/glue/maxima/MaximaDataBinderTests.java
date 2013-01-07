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
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.BooleanValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.StringOrderedValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.StringValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueOrVariableWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.WrapperUtilities;

import junit.framework.Assert;

import org.junit.Test;

/**
 * General tests for {@link MaximaDataBinder}
 *
 * @author David McKain
 */
public class MaximaDataBinderTests {

    private final MaximaDataBinder binder = new MaximaDataBinder();
    
    @Test(expected=IllegalArgumentException.class)
    public void testToMaximaExpressionNull1() {
        binder.toMaximaExpression((ValueOrVariableWrapper) null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testParseMaximaLinearOutputNull1() {
        binder.parseMaximaLinearOutput(null, BooleanValueWrapper.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testParseMaximaLinearOutputNull2() {
        binder.parseMaximaLinearOutput("", null);
    }
    
    @Test
    public void testParseEmptyStringCase() {
        /* string("") -> empty result special case test */
        StringValueWrapper result = binder.parseMaximaLinearOutput("", StringValueWrapper.class);
        Assert.assertEquals("", result.getValue());
    }
    
    @Test
    public void testParseMultiLineStringCase() {
        /* string("a\nb") -> "a\\\nb" special case test */
        StringValueWrapper result = binder.parseMaximaLinearOutput("\"a\\\nb\"", StringValueWrapper.class);
        Assert.assertNotNull(result);
        Assert.assertEquals("a\nb", result.getValue());
    }
    
    @Test
    public void testParseEmptyStringList1Case() {
        /* string([""]) -> [] special case test */
        StringOrderedValueWrapper result = binder.parseMaximaLinearOutput("[]", StringOrderedValueWrapper.class);
        Assert.assertEquals(WrapperUtilities.createCompoundValue(StringOrderedValueWrapper.class, StringValueWrapper.class, ""), result);
    }
    
    @Test
    public void testParseEmptyStringList2Case() {
        /* string(["",""]) -> [,] special case test */
        StringOrderedValueWrapper result = binder.parseMaximaLinearOutput("[,]", StringOrderedValueWrapper.class);
        Assert.assertEquals(WrapperUtilities.createCompoundValue(StringOrderedValueWrapper.class, StringValueWrapper.class, "", ""), result);
    }
    
    @Test
    public void testParseEmptyStringList3Case() {
        /* string(["","a"]) -> [,"a"] special case test */
        StringOrderedValueWrapper result = binder.parseMaximaLinearOutput("[,\"a\"]", StringOrderedValueWrapper.class);
        Assert.assertEquals(WrapperUtilities.createCompoundValue(StringOrderedValueWrapper.class, StringValueWrapper.class, "", "a"), result);
    }
}
