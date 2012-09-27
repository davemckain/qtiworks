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

import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_SIMPLIFY_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.MATHASSESS_NAMESPACE_URI;

import uk.ac.ed.ph.qtiworks.mathassess.glue.MathsContentTooComplexException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaTypeConversionException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueWrapper;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.group.expression.ExpressionGroup;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

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

    public ScriptRule(final ExpressionParent parent) {
        super(parent);

        getAttributes().add(new BooleanAttribute(this, ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI, false, false));

        /* Allow 1 child only */
        getNodeGroups().clear();
        getNodeGroups().add(new ExpressionGroup(this, 1,1));
    }

    public boolean getSimplify() {
        return ((BooleanAttribute) getAttributes().get(ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI))
                .getComputedNonNullValue();
    }

    public void setSimplify(final Boolean simplify) {
        ((BooleanAttribute) getAttributes().get(ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI)).setValue(simplify);
    }

    @Override
    protected void doAdditionalValidation(final ValidationContext context) {
        /* Nothing to do here */
    }

    @Override
    protected Value maximaEvaluate(final MathAssessExtensionPackage mathAssessExtensionPackage, final ItemProcessingContext context, final Value[] childValues) {
        final String code = childValues[0].toQtiString().trim();
        final boolean simplify = getSimplify();

        logger.debug("Performing scriptRule: code={}, simplify={}", code, simplify);

        /* Pass variables to Maxima */
        final QtiMaximaProcess qtiMaximaProcess = mathAssessExtensionPackage.obtainMaximaSessionForThread();
        passVariablesToMaxima(qtiMaximaProcess, context);

        /* Run code */
        logger.debug("Executing scriptRule code");
        try {
            qtiMaximaProcess.executeScriptRule(code, simplify);
        }
        catch (final MaximaTimeoutException e) {
            context.fireRuntimeError(this, "A timeout occurred executing the ScriptRule logic. Not setting QTI variables and returing FALSE");
            return BooleanValue.FALSE;
        }

        /* Read variables back */
        logger.debug("Reading variables back from Maxima");
        final ItemSessionState itemSessionState = context.getItemSessionState();
        final List<VariableDeclaration> outputDeclarations = getAllCASWriteableVariableDeclarations();
        for (final VariableDeclaration var : outputDeclarations) {
            final Class<? extends ValueWrapper> resultClass = GlueValueBinder.getCasReturnClass(var.getBaseType(), var.getCardinality());
            Value resultValue = NullValue.INSTANCE;
            if (resultClass!=null) {
                /* Variable is supported */
                ValueWrapper wrapper;
                try {
                    wrapper = qtiMaximaProcess.queryMaximaVariable(var.getIdentifier().toString(), resultClass);
                    if (wrapper!=null) {
                        resultValue = GlueValueBinder.casToJqti(wrapper);
                    }
                    else {
                        /* Variable isn't defined in Maxima, so leave as NULL */
                        context.fireRuntimeInfo(this, "Variable " + var.getIdentifier() + " remained undefined in Maxima at end of ScriptRule logic so is being set to NULL");
                    }
                }
                catch (final QtiMaximaTypeConversionException e) {
                    logger.warn("Unexpected conversion failure", e);
                    context.fireRuntimeError(this, "An unexpected problem occurred querying the value of " + var.getIdentifier() + ", so this variable was set to NULL");
                }
                catch (final MathsContentTooComplexException e) {
                    context.fireRuntimeError(this, "The value of the variable " + var.getIdentifier() + " was too complex to extract from Maxima, so it was set to NULL");
                }
            }
            else {
                context.fireRuntimeInfo(this, "Variable " + var.getIdentifier() + " is not of a supported baseType and/or cardinality for passing to the CAS - setting to NULL");
            }
            itemSessionState.setVariableValue(var, resultValue);
        }

        logger.debug("scriptRule finished successfully - returning TRUE");
        return BooleanValue.TRUE;
    }

    @Override
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        return new BaseType[] { BaseType.BOOLEAN };
    }

    @Override
    public Cardinality[] getProducedCardinalities(final ValidationContext context) {
        return new Cardinality[] { Cardinality.SINGLE };
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
