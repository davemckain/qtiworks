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
package uk.ac.ed.ph.qtiworks.mathassess;

import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_ACTION_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_CODE_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.ATTR_SIMPLIFY_NAME;
import static uk.ac.ed.ph.qtiworks.mathassess.MathAssessConstants.MATHASSESS_NAMESPACE_URI;

import uk.ac.ed.ph.qtiworks.mathassess.attribute.ActionAttribute;
import uk.ac.ed.ph.qtiworks.mathassess.glue.MathAssessBadCasCodeException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaProcess;
import uk.ac.ed.ph.qtiworks.mathassess.glue.maxima.QtiMaximaTypeConversionException;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.value.ActionType;

import uk.ac.ed.ph.jqtiplus.attribute.value.BooleanAttribute;
import uk.ac.ed.ph.jqtiplus.attribute.value.StringAttribute;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionParent;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import uk.ac.ed.ph.jacomax.MaximaTimeoutException;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the <tt>org.qtitools.mathassess.CasCompare</tt> customOperator
 *
 * @author Jonathon Hare
 */
public final class CasCompare extends MathAssessOperator {

    private static final long serialVersionUID = 197792404254247716L;

    private static final Logger logger = LoggerFactory.getLogger(CasCompare.class);

    private static final Map<ActionType, String> supportedActions;
    static {
        supportedActions = new HashMap<ActionType, String>();
        supportedActions.put(ActionType.EQUAL, QtiMaximaProcess.MAXIMA_EQUAL_CODE);
        supportedActions.put(ActionType.SYNTEQUAL, QtiMaximaProcess.MAXIMA_SYNTEQUAL_CODE);
    }

    private static boolean isActionSupported(final ActionType action) {
        return supportedActions.containsKey(action);
    }

    private static String getActionCode(final ActionType action) {
        return supportedActions.get(action);
    }

    public CasCompare(final ExpressionParent parent) {
        super(parent);

        getAttributes().add(new ActionAttribute(this, ATTR_ACTION_NAME, MATHASSESS_NAMESPACE_URI, true));
        getAttributes().add(new StringAttribute(this, ATTR_CODE_NAME, MATHASSESS_NAMESPACE_URI, null, false));
        getAttributes().add(new BooleanAttribute(this, ATTR_SIMPLIFY_NAME, MATHASSESS_NAMESPACE_URI, false, false));
    }

    /**
     * Gets value of code attribute.
     *
     * @return value of code attribute
     * @see #setCode
     */
    public String getCode() {
        return ((StringAttribute) getAttributes().get(ATTR_CODE_NAME, MATHASSESS_NAMESPACE_URI))
                .getComputedValue();
    }

    /**
     * Sets new value of code attribute.
     *
     * @param code new value of code attribute
     * @see #getCode
     */
    public void setCode(final String code) {
        ((StringAttribute) getAttributes().get(ATTR_CODE_NAME, MATHASSESS_NAMESPACE_URI))
            .setValue(code);
    }

    /**
     * Gets value of action attribute.
     *
     * @return value of action attribute
     * @see #setAction
     */
    public ActionType getAction() {
        return ((ActionAttribute) getAttributes().get(ATTR_ACTION_NAME, MATHASSESS_NAMESPACE_URI))
                .getComputedValue();
    }

    /**
     * Sets new value of action attribute.
     *
     * @param action new value of action attribute
     * @see #getAction
     */
    public void setAction(final ActionType action) {
        ((ActionAttribute) getAttributes().get(ATTR_ACTION_NAME, MATHASSESS_NAMESPACE_URI))
            .setValue(action);
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
        if (getAction() == ActionType.CODE && getCode() == null) {
            context.fireValidationError(this, "The " + ATTR_CODE_NAME + " attribute must be specified when the " + ATTR_ACTION_NAME + " is " + getAction());
        }
        else if (getAction() != null && getCode() == null) {
            switch (getSyntax()) {
                case MAXIMA:
                    if (!isActionSupported(getAction())) {
                        context.fireValidationError(this, "The action '" + getAction() + "' is not supported by the '" + getSyntax() + "' engine.");
                    }
                    break;
            }
        }

        if (getChildren().size() != 2) {
            context.fireValidationError(this, "Wrong number of children - expected 2");
        }
    }

    @Override
    protected Value maximaEvaluate(final MathAssessExtensionPackage mathAssessExtensionPackage, final ItemProcessingContext context, final Value[] childValues) {
        final Value v1 = childValues[0];
        final Value v2 = childValues[1];

        final boolean simplify = getSimplify();
        String code = getCode();
        code = code != null ? code.trim() : getActionCode(getAction());

        if (logger.isDebugEnabled()) {
            logger.debug("Performing casCompare: code={}, simplify={}, value1={}, value2={}",
                    new Object[] { code, simplify, v1, v2 });
        }

        final ValueWrapper casValue1 = GlueValueBinder.jqtiToCas(v1);
        final ValueWrapper casValue2 = GlueValueBinder.jqtiToCas(v2);
        if (casValue1==null) {
            context.fireRuntimeError(this, "First child value " + v1.toQtiString() + " cannot be passed to Maima - returning NULL");
            return NullValue.INSTANCE;
        }
        if (casValue2==null) {
            context.fireRuntimeError(this, "Second child value " + v2.toQtiString() + " cannot be passed to Maxima - returning NULL");
            return NullValue.INSTANCE;
        }

        final QtiMaximaProcess qtiMaximaProcess = mathAssessExtensionPackage.obtainMaximaSessionForThread();
        try {
            return BooleanValue.valueOf(qtiMaximaProcess.executeCasCompare(code, simplify, casValue1, casValue2));
        }
        catch (final MaximaTimeoutException e) {
            context.fireRuntimeError(this, "A timeout occurred executing the CasCompare logic. Returning NULL");
            return NullValue.INSTANCE;
        }
        catch (final MathAssessBadCasCodeException e) {
            context.fireRuntimeError(this, "Your CasCompare code did not work as expected. The CAS input was '"
                    + e.getMaximaInput()
                    + "' and the CAS output was '"
                    + e.getMaximaOutput()
                    + "'. The failure reason was " + e.getReason());
            return NullValue.INSTANCE;
        }
        catch (final QtiMaximaTypeConversionException e) {
            context.fireRuntimeError(this, "Your CasCompare code did not produce a result that could be converted into a QTI boolean. The CAS input was '"
                    + e.getMaximaInput()
                    + "' and the CAS output was '"
                    + e.getMaximaOutput()
                    + "'");
            return NullValue.INSTANCE;
        }
        catch (final RuntimeException e) {
            logger.warn("Unexpected Maxima failure", e);
            context.fireRuntimeError(this, "An unexpected problem occurred while executing this CasCompare");
            return BooleanValue.FALSE;
        }
    }

    @Override
    public BaseType[] getProducedBaseTypes(final ValidationContext context) {
        return new BaseType[] { BaseType.BOOLEAN };
    }

    @Override
    public Cardinality[] getProducedCardinalities(final ValidationContext context) {
        return new Cardinality[] { Cardinality.SINGLE };
    }
}
