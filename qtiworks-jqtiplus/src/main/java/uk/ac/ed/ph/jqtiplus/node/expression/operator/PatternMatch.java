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
package uk.ac.ed.ph.jqtiplus.node.expression.operator;

import uk.ac.ed.ph.jqtiplus.attribute.value.StringOrVariableRefAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.StringOrVariableRef;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The patternMatch operator takes a sub-expression which must have single cardinality and a base-type
 * of string. The result is a single boolean with a value of true if the sub-expression matches the regular
 * expression given by pattern and false if it doesn't.
 * If the sub-expression is NULL then the operator results in NULL.
 * <p>
 * The syntax for the regular expression language is defined in Appendix F of <A href="http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#regexs">XML</A>.
 * <p>
 * Current implementation supports only java regular expression language definition!
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @author Jiri Kajaba
 */
public final class PatternMatch extends AbstractFunctionalExpression {

    private static final long serialVersionUID = 2422777906725885061L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "patternMatch";

    /** Name of pattern attribute in xml schema. */
    public static final String ATTR_PATTERN_NAME = "pattern";

    public PatternMatch(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new StringOrVariableRefAttribute(this, ATTR_PATTERN_NAME, true));
    }

    public StringOrVariableRef getPattern() {
        return getAttributes().getStringOrVariableRefAttribute(ATTR_PATTERN_NAME).getValue();
    }

    public void setPattern(final StringOrVariableRef pattern) {
        getAttributes().getStringOrVariableRefAttribute(ATTR_PATTERN_NAME).setValue(pattern);
    }


    @Override
    protected Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        final Value computedPattern = getPattern().evaluate(this, context);
        if (computedPattern.isNull()) {
            context.fireRuntimeWarning(this, "Computed value of pattern was NULL. Returning NULL");
            return NullValue.INSTANCE;
        }

        final String pattern = ((StringValue) computedPattern).stringValue();
        final String childString =  ((StringValue) childValues[0]).toQtiString();

        final boolean result = childString.matches(pattern);
        return BooleanValue.valueOf(result);
    }
}
