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
package uk.ac.ed.ph.jqtiplus.node.expression;

import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;

import java.util.List;

/**
 * Every object which contains expression(s) must implement this interface.
 * <p>
 * Expression contains (in general case) other expressions, so it must implement this interface too.
 *
 * @author Jiri Kajaba
 */
public interface ExpressionParent extends QtiNode {

    /**
     * Gets the {@link Expression}(s) contained by this parent.
     */
    List<Expression> getExpressions();

    /**
     * Gets list of all acceptable cardinalities which can child expression at given position produce.
     * <p>
     * For example delete expression returns single cardinality for index 0 and list of multiple and ordered cardinality for index 1.
     * <p>
     * Result of this method can change in time.
     * <p>
     * Static example is expression or. Expression or accepts only single cardinality for any index.
     * <p>
     * Dynamic example is expression match. Expression match accepts any cardinality of its children, but this cardinality must be same for all its children.
     *
     * @param index position of child expression in this parent
     * @return list of all possible cardinalities which can child expression at given position produce
     */
    Cardinality[] getRequiredCardinalities(ValidationContext context, int index);

    /**
     * Gets list of all acceptable baseTypes which can child expression at given position produce.
     * <p>
     * Result of this method can change in time.
     * <p>
     * Static example is expression or. Expression or accepts only boolean baseType for any index.
     * <p>
     * Dynamic example is expression match. Expression match accepts any baseType of its children. but this baseType must be same for all its children.
     *
     * @param index position of child expression in this parent
     * @return list of all acceptable baseTypes which can child expression at given position produce
     */
    BaseType[] getRequiredBaseTypes(ValidationContext context, int index);

}
