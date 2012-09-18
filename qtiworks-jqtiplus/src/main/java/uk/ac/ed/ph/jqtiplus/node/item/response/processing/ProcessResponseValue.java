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
package uk.ac.ed.ph.jqtiplus.node.item.response.processing;


import uk.ac.ed.ph.jqtiplus.attribute.value.IdentifierAttribute;
import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.node.XmlNode;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.types.Identifier;

/**
 * Abstract parent for setResponseValue and lookupResponseValue classes.
 * 
 * @author Jonathon Hare
 */
public abstract class ProcessResponseValue extends ResponseRule implements ExpressionParent {

    private static final long serialVersionUID = 4728782404420587897L;

    /** Name of identifier attribute in xml schema. */
    public static final String ATTR_IDENTIFIER_NAME = "identifier";

    public ProcessResponseValue(XmlNode parent, String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new IdentifierAttribute(this, ATTR_IDENTIFIER_NAME, true));

        getNodeGroups().add(new ExpressionGroup(this, 1, 1));
    }

    @Override
    public final String computeXPathComponent() {
        final Identifier identifier = getIdentifier();
        if (identifier != null) {
            return getQtiClassName() + "[@identifier='" + identifier + "']";
        }
        return super.computeXPathComponent();
    }

    /**
     * Gets value of identifier attribute.
     * 
     * @return value of identifier attribute
     * @see #setIdentifier
     */
    public Identifier getIdentifier() {
        return getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).getComputedValue();
    }

    /**
     * Sets new value of identifier attribute.
     * 
     * @param identifier new value of identifier attribute
     * @see #getIdentifier
     */
    public void setIdentifier(Identifier identifier) {
        getAttributes().getIdentifierAttribute(ATTR_IDENTIFIER_NAME).setValue(identifier);
    }

    /**
     * Gets expression child.
     * 
     * @return expression child
     * @see #setExpression
     */
    public Expression getExpression() {
        return getNodeGroups().getExpressionGroup().getExpression();
    }

    /**
     * Sets new expression child.
     * 
     * @param expression new expression child
     * @see #getExpression
     */
    public void setExpression(Expression expression) {
        getNodeGroups().getExpressionGroup().setExpression(expression);
    }
}
