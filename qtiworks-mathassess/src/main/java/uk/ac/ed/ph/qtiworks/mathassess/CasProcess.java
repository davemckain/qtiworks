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
package uk.ac.ed.ph.qtiworks.mathassess;

import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_RETURN_TYPE_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_SIMPLIFY_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.MATHASSESS_NAMESPACE_URI;

import uk.ac.ed.ph.qtiworks.mathassess.attribute.ReturnTypeAttribute;
import uk.ac.ed.ph.qtiworks.mathassess.glue.MathsContentTooComplexException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.value.ReturnTypeType;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.exception.QtiEvaluationException;
import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;

import uk.ac.ed.ph.jacomax.MaximaTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the <tt>org.qtitools.mathassess.CasProcess</tt> customOperator
 *
 * @author Jonathon Hare
 */
public final class CasProcess extends MathAssessOperator {

    private static final long serialVersionUID = -2916041095499411867L;

    private static final Logger logger = LoggerFactory.getLogger(CasProcess.class);

    public CasProcess(final ExpressionParent parent) {
        super(parent);

        getAttributes().add(new ReturnTypeAttribute(this, ATTR_RETURN_TYPE_NAME, MATHASSESS_NAMESPACE_URI));
        getAttributes().add(new BooleanAttribute(this, ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI, false, false));

        // Allow 1 child only
        getNodeGroups().clear();
        getNodeGroups().add(new ExpressionGroup(this, 1, 1));
    }

    /**
     * Gets value of returnType attribute.
     *
     * @return value of returnType attribute
     * @see #setReturnType
     */
    public ReturnTypeType getReturnType() {
        return ((ReturnTypeAttribute) getAttributes().get(ATTR_RETURN_TYPE_NAME, MATHASSESS_NAMESPACE_URI))
                .getComputedValue();
    }

    /**
     * Sets new value of returnType attribute.
     *
     * @param returnTypeType new value of returnType attribute
     * @see #getReturnType
     */
    public void setReturnType(final ReturnTypeType returnTypeType) {
        ((ReturnTypeAttribute) getAttributes().get(ATTR_RETURN_TYPE_NAME, MATHASSESS_NAMESPACE_URI))
                .setValue(returnTypeType);
    }

    /**
     * Gets value of simplify attribute.
     *
     * @return value of simplify attribute
     * @see #setSimplify
     */
    public boolean getSimplify() {
        return ((BooleanAttribute) getAttributes().get(ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI))
                .getComputedNonNullValue();
    }

    /**
     * Sets new value of simplify attribute.
     *
     * @param simplify new value of simplify attribute
     * @see #getSimplify
     */
    public void setSimplify(final Boolean simplify) {
        ((BooleanAttribute) getAttributes().get(ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI))
            .setValue(simplify);
    }

    @Override
    protected void doAdditionalValidation(final ValidationContext context) {
        /* Nothing to do here */
    }

    @Override
    protected Value maximaEvaluate(final MathAssessExtensionPackage mathAssessExtensionPackage, final ItemProcessingContext context, final Value[] childValues) throws MaximaTimeoutException, MathsContentTooComplexException {
        final boolean simplify = getSimplify();
        final String code = childValues[0].toQtiString().trim();

        logger.debug("Performing casProcess: code={}, simplify={}", code, simplify);

        final QtiMaximaProcess qtiMaximaProcess = mathAssessExtensionPackage.obtainMaximaSessionForThread();

        /* Pass variables to Maxima */
        logger.trace("Passing variables to maxima");
        for (final VariableDeclaration declaration : getAllCASReadableVariableDeclarations()) {
            final Class<? extends ValueWrapper> resultClass = CasTypeGlue.getCasClass(declaration.getBaseType(), declaration.getCardinality());
            final Value value = context.getItemSessionState().getVariableValue(declaration);
            if (value!=null && !value.isNull() && resultClass != null) {
                qtiMaximaProcess.passQTIVariableToMaxima(declaration.getIdentifier().toString(), CasTypeGlue.convertFromJQTI(value));
            }
        }

        /* Run Maxima code and return result */
        logger.trace("Running code to determine result of casProcess");
        final Class<? extends ValueWrapper> resultClass = CasTypeGlue.getCasClass(getBaseType(), getCardinality());
        final ValueWrapper result = qtiMaximaProcess.executeCasProcess(code, simplify, resultClass);
        return CasTypeGlue.convertToJQTI(result);
    }

    private BaseType getBaseType() {
        if (getReturnType() == null) {
            return null;
        }
        switch (getReturnType()) {
            case INTEGER:
            case INTEGER_MULTIPLE:
            case INTEGER_ORDERED:
                return BaseType.INTEGER;

            case FLOAT:
            case FLOAT_MULTIPLE:
            case FLOAT_ORDERED:
                return BaseType.FLOAT;

            case BOOLEAN:
            case BOOLEAN_MULTIPLE:
            case BOOLEAN_ORDERED:
                return BaseType.BOOLEAN;

            default:
                return null;
        }
    }

    @Override
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        if (getReturnType() != null) {
            final BaseType type = getBaseType();
            return type != null ? new BaseType[] { type } : BaseType.values();
        }
        return super.getProducedBaseTypes(context);
    }

    private Cardinality getCardinality() {
        if (getReturnType() == null) {
            return null;
        }
        switch (getReturnType()) {
            case MATHS_CONTENT:
                return Cardinality.RECORD;

            case INTEGER:
            case FLOAT:
            case BOOLEAN:
                return Cardinality.SINGLE;

            case INTEGER_MULTIPLE:
            case FLOAT_MULTIPLE:
            case BOOLEAN_MULTIPLE:
                return Cardinality.MULTIPLE;

            case INTEGER_ORDERED:
            case FLOAT_ORDERED:
            case BOOLEAN_ORDERED:
                return Cardinality.ORDERED;

            default:
                throw new QtiEvaluationException("Error: Unsupported return type " + getReturnType()
                        + " (unable to determine cardinality)");
        }
    }

    @Override
    public Cardinality[] getProducedCardinalities(final ValidationContext context) {
        if (getReturnType() != null) {
            return new Cardinality[] { getCardinality() };
        }
        return super.getProducedCardinalities(context);
    }

    @Override
    public BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        return new BaseType[] { BaseType.STRING };
    }

    @Override
    public Cardinality[] getRequiredCardinalities(final ValidationContext context, final int index) {
        return new Cardinality[] { Cardinality.SINGLE };
    }

}
