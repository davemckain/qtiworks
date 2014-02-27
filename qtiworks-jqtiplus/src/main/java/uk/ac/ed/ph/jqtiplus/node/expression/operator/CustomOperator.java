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

import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.UriAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.AbstractFunctionalExpression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ProcessingContext;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.net.URI;

/**
 * The custom operator provides an extension mechanism for defining operations not currently supported
 * by this specification.
 * <p>
 * use the class attr to point to a java class (fully qualified) that implements Expression (and possibly extends AbstractExpression). If you do extend
 * AbstractExpression, set the QTI_CLASS_NAME to "customOperator", or override relevant methods required for validation that might call getType() to stop
 * unsupportedExpression exceptions at runtime.
 *
 * @param <E> {@link JqtiExtensionPackage} providing the implementation of this operator
 *
 * @author Jonathon Hare
 */
public abstract class CustomOperator<E extends JqtiExtensionPackage<E>> extends AbstractFunctionalExpression {

    private static final long serialVersionUID = -3800871694273961417L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "customOperator";

    /** Name of the class attribute in xml schema. */
    public static final String ATTR_CLASS_NAME = "class";

    /** Name of the definition attribute in xml schema. */
    public static final String ATTR_DEFINITION_NAME = "definition";

    protected CustomOperator(final ExpressionParent parent) {
        super(parent, QTI_CLASS_NAME);
        getAttributes().add(new StringAttribute(this, ATTR_CLASS_NAME, false)); //allow .'s, so use String
        getAttributes().add(new UriAttribute(this, ATTR_DEFINITION_NAME, false));
    }


    public String getClassAttr() {
        return getAttributes().getStringAttribute(ATTR_CLASS_NAME).getComputedValue();
    }

    public void setClassAttr(final String name) {
        getAttributes().getStringAttribute(ATTR_CLASS_NAME).setValue(name);
    }


    public URI getDefinition() {
        return getAttributes().getUriAttribute(ATTR_DEFINITION_NAME).getComputedValue();
    }

    public void setDefinition(final URI name) {
        getAttributes().getUriAttribute(ATTR_DEFINITION_NAME).setValue(name);
    }

    @Override
    protected final Value evaluateValidSelf(final ProcessingContext context, final Value[] childValues, final int depth) {
        final E jqtiExtensionPackage = getOwningExtensionPackage(context);
        if (jqtiExtensionPackage!=null) {
            return evaluateSelf(jqtiExtensionPackage, context, childValues, depth);
        }
        else {
            context.fireRuntimeWarning(this, "Extension package for this operator is not found - returning NULL");
            return NullValue.INSTANCE;
        }
    }

    protected E getOwningExtensionPackage(final ProcessingContext context) {
        return context.getJqtiExtensionManager().getJqtiExtensionPackageImplementingOperator(this);
    }

    /**
     * customOperators should implement this to evaluate themselves. This is the same as
     * {@link #evaluateValidSelf(ProcessingContext, Value[], int)}, but is also passed the
     * owning {@link JqtiExtensionPackage}
     */
    protected abstract Value evaluateSelf(E jqtiExtensionPackage, ProcessingContext context, Value[] childValues, int depth);
}
