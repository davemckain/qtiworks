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
package org.qtitools.mathassess;

import static org.qtitools.mathassess.MathAssessConstants.ATTR_SIMPLIFY_NAME;
import static org.qtitools.mathassess.MathAssessConstants.MATHASSESS_NAMESPACE_URI;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionPackage;
import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.qtitools.mathassess.tools.qticasbridge.MathsContentTooComplexException;
import org.qtitools.mathassess.tools.qticasbridge.maxima.QTIMaximaSession;
import org.qtitools.mathassess.tools.qticasbridge.types.ValueWrapper;

import uk.ac.ed.ph.jacomax.MaximaTimeoutException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the <tt>org.qtitools.mathassess.ScriptRule</tt> customOperator
 * 
 * @author Jonathon Hare
 */
public final class ScriptRule extends MathAssessOperator {

    private static final long serialVersionUID = -3954677712564518901L;

    private static final Logger logger = LoggerFactory.getLogger(ScriptRule.class);

    public ScriptRule(JqtiExtensionPackage jqtiExtensionPackage, ExpressionParent parent) {
        super(jqtiExtensionPackage, parent);

        getAttributes().add(new BooleanAttribute(this, ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI, Boolean.FALSE, Boolean.FALSE, false));

        // Allow 1 child only
        getNodeGroups().clear();
        getNodeGroups().add(new ExpressionGroup(this, Integer.valueOf(1), Integer.valueOf(1)));
    }

    /**
     * Gets value of simplify attribute.
     * 
     * @return value of simplify attribute
     * @see #setSimplify
     */
    public Boolean getSimplify() {
        return ((BooleanAttribute) getAttributes().get(ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI))
                .getValue();
    }

    /**
     * Sets new value of simplify attribute.
     * 
     * @param simplify new value of simplify attribute
     * @see #getSimplify
     */
    public void setSimplify(Boolean simplify) {
        ((BooleanAttribute) getAttributes().get(ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI)).setValue(simplify);
    }

    @Override
    protected void doAdditionalValidation(ValidationContext context) {
        /* Nothing to do here */
    }

    @Override
    protected Value maximaEvaluate(ItemProcessingContext context)
            throws MaximaTimeoutException, MathsContentTooComplexException {
        final MathAssessExtensionPackage mathAssessExtensionPackage = (MathAssessExtensionPackage) getJQTIExtensionPackage();
        final QTIMaximaSession qtiMaximaSession = mathAssessExtensionPackage.obtainMaximaSessionForThread();
        final String code = context.getExpressionValue(getFirstChild()).toString().trim();
        final List<VariableDeclaration> inputDeclarations = getAllCASReadableVariableDeclarations();
        final List<VariableDeclaration> outputDeclarations = getAllCASWriteableVariableDeclarations();
        final boolean simplify = getSimplify().booleanValue();

        logger.info("Performing scriptRule: code={}, simplify={}", code, simplify);

        /* Pass variables to Maxima */
        logger.debug("Passing variables to maxima");
        for (final VariableDeclaration declaration : inputDeclarations) {
            final Value value = context.lookupVariable(declaration);
            final Class<? extends ValueWrapper> resultClass = CasTypeGlue.getCasClass(declaration.getBaseType(), declaration.getCardinality());
            if (value != null && !value.isNull() && resultClass != null) {
                qtiMaximaSession.passQTIVariableToMaxima(declaration.getIdentifier().toString(), CasTypeGlue.convertFromJQTI(value));
            }
        }

        /* Run code */
        logger.debug("Executing scriptRule code");
        qtiMaximaSession.executeScriptRule(code, simplify);

        /* Read variables back */
        logger.debug("Reading variables back from Maxima");
        for (final VariableDeclaration var : outputDeclarations) {
            final Class<? extends ValueWrapper> resultClass = CasTypeGlue.getCasClass(var.getBaseType(), var.getCardinality());
            if (resultClass != null) {
                final ValueWrapper wrapper = qtiMaximaSession.queryMaximaVariable(var.getIdentifier().toString(), resultClass);
                if (wrapper != null) {
                    context.setVariableValue(var, CasTypeGlue.convertToJQTI(wrapper));
                }
            }
        }

        logger.debug("scriptRule finished - returning TRUE");
        return BooleanValue.TRUE;
    }

    @Override
    public BaseType[] getProducedBaseTypes(ValidationContext context) {
        return new BaseType[] { BaseType.BOOLEAN };
    }

    @Override
    public Cardinality[] getProducedCardinalities(ValidationContext context) {
        return new Cardinality[] { Cardinality.SINGLE };
    }

    @Override
    public BaseType[] getRequiredBaseTypes(ValidationContext context, int index) {
        return new BaseType[] { BaseType.STRING };
    }

    @Override
    public Cardinality[] getRequiredCardinalities(ValidationContext context, int index) {
        return new Cardinality[] { Cardinality.SINGLE };
    }
}
