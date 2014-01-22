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

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractSimpleFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The substring operator takes two sub-expressions which must both have an effective base-type of string
 * and single cardinality. The result is a single boolean with a value of true if the first expression
 * is a substring of the second expression and false if it isn't. If either sub-expression is NULL then
 * the result of the operator is NULL.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * @see uk.ac.ed.ph.jqtiplus.node.expression.operator.StringMatch
 * @author Jiri Kajaba
 */
public final class Substring extends AbstractSimpleFunctionalExpression {

    private static final long serialVersionUID = 389530305199141690L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "substring";

    /** Name of caseSensitive attribute in xml schema. */
    public static final String ATTR_CASE_SENSITIVE_NAME = "caseSensitive";

    public Substring(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);

        getAttributes().add(new BooleanAttribute(this, ATTR_CASE_SENSITIVE_NAME, true));
    }


    public boolean getCaseSensitive() {
        return getAttributes().getBooleanAttribute(ATTR_CASE_SENSITIVE_NAME).getComputedNonNullValue();
    }

    public void setCaseSensitive(final Boolean caseSensitive) {
        getAttributes().getBooleanAttribute(ATTR_CASE_SENSITIVE_NAME).setValue(caseSensitive);
    }


    @Override
    protected Value evaluateValidSelf(final Value[] childValues) {
        if (isAnyChildNull(childValues)) {
            return NullValue.INSTANCE;
        }

        String firstString = ((StringValue) childValues[0]).toQtiString();
        String secondString = ((StringValue) childValues[1]).toQtiString();

        if (!getCaseSensitive()) {
            firstString = firstString.toLowerCase();
            secondString = secondString.toLowerCase();
        }

        final boolean result = secondString.indexOf(firstString) != -1;
        return BooleanValue.valueOf(result);
    }
}
